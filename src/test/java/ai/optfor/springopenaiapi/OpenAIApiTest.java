package ai.optfor.springopenaiapi;

import ai.optfor.springopenaiapi.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OpenAIApiTest {

    @Mock
    private RestTemplate restTemplate;

    private OpenAIApi openAIApi;

    @BeforeEach
    public void setUp() {
        openAIApi = new OpenAIApi("testKey", restTemplate, null);
    }

    @Test
    public void testChat() {
        ChatCompletionResponse mockResponse = new ChatCompletionResponse("gpt-3.5-turbo",
                List.of(new ChatChoice(0, new ChatMessage("system", "Hello"), "stop")),
                new Usage(1, 1, 2)
        );

        when(restTemplate.postForObject(anyString(), any(), eq(ChatCompletionResponse.class)))
                .thenReturn(mockResponse);

        ChatCompletionResponse response = openAIApi.chat("model", List.of(new ChatMessage("system", "Hello")), 5, 0.5);

        assertEquals(mockResponse, response);
    }

    @Test
    public void testChatWithSingleContent() {
        ChatCompletionResponse mockResponse = new ChatCompletionResponse("gpt-3.5-turbo",
                List.of(new ChatChoice(0, new ChatMessage("system", "Hello"), "stop")),
                new Usage(1, 1, 2)
        );

        when(restTemplate.postForObject(anyString(), any(), eq(ChatCompletionResponse.class)))
                .thenReturn(mockResponse);

        ChatCompletionResponse response = openAIApi.chat("model", "Hello", "system", 5, 0.5);

        assertEquals(mockResponse, response);
    }

    @Test
    public void testEmbedding() {
        EmbeddingResponse mockResponse = new EmbeddingResponse("model",
                List.of(new EmbeddingData(List.of(0.1, 0.2, 0.3))),
                new Usage(1, 1, 2)
        );

        when(restTemplate.postForObject(anyString(), any(), eq(EmbeddingResponse.class)))
                .thenReturn(mockResponse);

        EmbeddingResponse response = openAIApi.embedding("model", List.of("Hello"));

        assertEquals(mockResponse, response);
    }
}
