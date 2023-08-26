package ai.optfor.springopenaiapi;

import ai.optfor.springopenaiapi.model.*;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class OpenAIApi {
    public final RestTemplate restTemplate;

    public final Cache cache;

    public OpenAIApi(String openaiKey, RestTemplate restTemplate, CacheManager cacheManager) {
        this.restTemplate = (restTemplate == null ? new RestTemplate() : restTemplate);
        CacheManager usedCacheManager = (cacheManager == null ? new ConcurrentMapCacheManager() : cacheManager);
        this.cache = usedCacheManager.getCache("promptCache");
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(1000 * 5);
        requestFactory.setReadTimeout(1000 * 120);
        restTemplate.setRequestFactory(requestFactory);

        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + openaiKey);
            return execution.execute(request, body);
        };

        restTemplate.setInterceptors(List.of(interceptor));
    }

    public ChatCompletionResponse chat(String model, String prompt, String role, Integer maxTokens, double temperature) {
        return chat(model, List.of(ChatMessage.roleMessage(role), ChatMessage.contentMessage(prompt)), maxTokens, temperature);
    }

    public ChatCompletionResponse chat(String model, List<ChatMessage> chats, int maxTokens, double temperature) {
        if (shouldCacheResponse(temperature)) {
            Cache.ValueWrapper value = cache.get(createKey(model, chats, maxTokens));
            if (value != null) {
                return (ChatCompletionResponse) value.get();
            }
        }

        ChatCompletionResponse response = restTemplate.postForObject("https://api.openai.com/v1/chat/completions",
                new ChatCompletionRequest(model, chats, temperature, maxTokens), ChatCompletionResponse.class);

        if (shouldCacheResponse(temperature)) {
            cache.put(createKey(model, chats, maxTokens), response);
        }

        return response;
    }

    public EmbeddingData embedding(String model, String content) {
        return embedding(model, List.of(content)).data().get(0);
    }

    public EmbeddingResponse embedding(String model, List<String> content) {
        if (cache != null) {
            Cache.ValueWrapper value = cache.get(model + content);
            if (value != null) {
                return (EmbeddingResponse) value.get();
            }
        }

        String url = "https://api.openai.com/v1/embeddings";
        EmbeddingResponse response = restTemplate.postForObject(url, new EmbeddingRequest(model, content), EmbeddingResponse.class);

        if (cache != null) {
            cache.put(model + content, response);
        }

        return response;
    }

    private String createKey(String model, List<ChatMessage> chats, int maxTokens) {
        return model + chats + maxTokens;
    }

    private boolean shouldCacheResponse(double temperature) {
        return cache != null && Double.compare(temperature, 0) == 0;
    }
}
