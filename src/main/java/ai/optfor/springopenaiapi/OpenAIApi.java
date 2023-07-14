package ai.optfor.springopenaiapi;

import ai.optfor.springopenaiapi.model.*;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class OpenAIApi {
    public final RestTemplate restTemplate = new RestTemplate();

    public OpenAIApi(String openaiKey) {
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

    public ChatCompletionResponse chatCompletion(String model, String prompt, String role, Integer maxTokens, double temperature) {
        return chatCompletion(model, List.of(ChatMessage.roleMessage(role), ChatMessage.contentMessage(prompt)), maxTokens, temperature);
    }

    public ChatCompletionResponse chatCompletion(String model, List<ChatMessage> chats, int maxTokens, double temperature) {
        String url = "https://api.openai.com/v1/chat/completions";
        return restTemplate.postForObject(url, new ChatCompletionRequest(model, chats, temperature, maxTokens), ChatCompletionResponse.class);
    }

    public EmbeddingData embedding(String model, String content) {
        return embedding(model, List.of(content)).data().get(0);
    }

    public EmbeddingResponse embedding(String model, List<String> content) {
        String url = "https://api.openai.com/v1/embeddings";
        return restTemplate.postForObject(url, new EmbeddingRequest(model, content), EmbeddingResponse.class);
    }
}
