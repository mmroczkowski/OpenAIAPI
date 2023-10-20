package ai.optfor.springopenaiapi;

import ai.optfor.springopenaiapi.cache.DefaultPromptCache;
import ai.optfor.springopenaiapi.cache.PromptCache;
import ai.optfor.springopenaiapi.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public class OpenAIApi {

    private static final Logger log = LoggerFactory.getLogger(OpenAIApi.class);

    private final ObjectMapper mapper = new ObjectMapper();

    private final RestTemplate restTemplate;

    private final PromptCache promptCache;

    private final ExecutorService executorService;

    public OpenAIApi(String openaiKey, RestTemplate restTemplate, PromptCache promptCache) {
        this.restTemplate = (restTemplate == null ? new RestTemplate() : restTemplate);
        this.promptCache = (promptCache == null ? new DefaultPromptCache() : promptCache);
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(1000 * 5);
        requestFactory.setReadTimeout(1000 * 60 * 10);
        this.restTemplate.setRequestFactory(requestFactory);

        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + openaiKey);
            return execution.execute(request, body);
        };

        this.restTemplate.setInterceptors(List.of(interceptor));
        mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.executorService = Executors.newFixedThreadPool(3);
    }

    public ChatCompletionResponse chat(String model, String prompt, String role, Integer maxTokens, double temperature) {
        return chat(model, List.of(ChatMessage.roleMessage(role), ChatMessage.contentMessage(prompt)), maxTokens, temperature);
    }

    public ChatCompletionResponse chat(String model, List<ChatMessage> chats, int maxTokens, double temperature) {
        Future<ChatCompletionResponse> future = executorService.submit(() -> chatInternal(model, chats, maxTokens, temperature));

        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ChatCompletionResponse chatInternal(String model, List<ChatMessage> chats, int maxTokens, double temperature) {
        int retryCount = 0;
        while (true) {
            try {
                ChatCompletionRequest request = new ChatCompletionRequest(model, chats, temperature, maxTokens);
                log.info("Sending request to OpenAI API: {}", mapper.writeValueAsString(request));

                if (Double.compare(temperature, 0) == 0) {
                    String cached = promptCache.get(createKey(model, chats, maxTokens));
                    if (cached != null) {
                        ChatCompletionResponse response = mapper.readValue(cached, ChatCompletionResponse.class);
                        log.info("Returning cached response: {}", mapper.writeValueAsString(response));
                        return response;
                    }
                }

                ChatCompletionResponse response = restTemplate.postForObject("https://api.openai.com/v1/chat/completions",
                        request, ChatCompletionResponse.class);
                log.info("Received response from OpenAI API: {}", mapper.writeValueAsString(response));

                if (Double.compare(temperature, 0) == 0) {
                    promptCache.put(createKey(model, chats, maxTokens), mapper.writeValueAsString(response));
                }

                return response;
            } catch (Exception e) {
                if (++retryCount == 3) throw new RuntimeException(e);
            }
        }
    }

    public EmbeddingResponse embedding(String model, String content) {
        return embedding(model, List.of(content));
    }

    public EmbeddingResponse embedding(String model, List<String> content) {
        EmbeddingRequest request = new EmbeddingRequest(model, content);
        try {
            log.info("Sending request to OpenAI API: {}", mapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        EmbeddingResponse response = restTemplate.postForObject("https://api.openai.com/v1/embeddings", request, EmbeddingResponse.class);
        return response;
    }

    private String createKey(String model, List<ChatMessage> chats, int maxTokens) {
        return model + chats + maxTokens;
    }
}
