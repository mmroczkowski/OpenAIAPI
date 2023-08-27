package ai.optfor.springopenaiapi;

import ai.optfor.springopenaiapi.cache.DefaultPromptCache;
import ai.optfor.springopenaiapi.model.ChatChoice;
import ai.optfor.springopenaiapi.model.ChatCompletionResponse;
import ai.optfor.springopenaiapi.model.ChatMessage;
import ai.optfor.springopenaiapi.model.Usage;
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
public class OpenAIApiCacheTest {

    @Mock
    private RestTemplate restTemplate;

    private OpenAIApi openAIApi;

    @BeforeEach
    public void setUp() {
        openAIApi = new OpenAIApi("testKey", restTemplate, new DefaultPromptCache());
    }

    @Test
    public void testChatWithCache() {
        ChatCompletionResponse mockResponse = new ChatCompletionResponse(
                List.of(new ChatChoice(0, new ChatMessage("system", "Hello"), "stop")), new Usage(1, 1, 2));

        when(restTemplate.postForObject(anyString(), any(), eq(ChatCompletionResponse.class))).thenReturn(mockResponse);

        ChatCompletionResponse response1 = openAIApi.chat("model", List.of(new ChatMessage("system", "Hello")), 5, 0.0);
        ChatCompletionResponse response2 = openAIApi.chat("model", List.of(new ChatMessage("system", "Hello")), 5, 0.0);

        verify(restTemplate, times(1)).postForObject(anyString(), any(), eq(ChatCompletionResponse.class));

        assertEquals(mockResponse, response1);
        assertEquals(mockResponse, response2);
    }

    @Test
    public void testChatWithCacheDifferentPrompts() {
        ChatCompletionResponse mockResponse = new ChatCompletionResponse(
                List.of(new ChatChoice(0, new ChatMessage("system", "Hello"), "stop")), new Usage(1, 1, 2));

        when(restTemplate.postForObject(anyString(), any(), eq(ChatCompletionResponse.class))).thenReturn(mockResponse);

        openAIApi.chat("model", List.of(new ChatMessage("system", "Hello1")), 5, 0.0);
        openAIApi.chat("model", List.of(new ChatMessage("system", "Hello2")), 5, 0.0);

        verify(restTemplate, times(2)).postForObject(anyString(), any(), eq(ChatCompletionResponse.class));
    }

    @Test
    public void testChatWithCacheNonZeroTemperature() {
        ChatCompletionResponse mockResponse = new ChatCompletionResponse(
                List.of(new ChatChoice(0, new ChatMessage("system", "Hello"), "stop")), new Usage(1, 1, 2));

        when(restTemplate.postForObject(anyString(), any(), eq(ChatCompletionResponse.class))).thenReturn(mockResponse);

        openAIApi.chat("model", List.of(new ChatMessage("system", "Hello12")), 5, 0.01);
        openAIApi.chat("model", List.of(new ChatMessage("system", "Hello12")), 5, 0.01);

        // Verify that restTemplate.postForObject was called two times
        verify(restTemplate, times(2)).postForObject(anyString(), any(), eq(ChatCompletionResponse.class));
    }
}