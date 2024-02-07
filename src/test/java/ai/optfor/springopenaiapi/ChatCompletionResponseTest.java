package ai.optfor.springopenaiapi;

import ai.optfor.springopenaiapi.model.ChatChoice;
import ai.optfor.springopenaiapi.model.ChatCompletionResponse;
import ai.optfor.springopenaiapi.model.Usage;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatCompletionResponseTest {

    @Test
    void testGetCostGPT4TurboPreview() {
        ChatCompletionResponse response = new ChatCompletionResponse(
                "gpt-4-turbo-preview",
                Collections.singletonList(new ChatChoice(0, null, null, null)),
                new Usage(1000, 500, 1500)
        );
        assertEquals(new BigDecimal("0.025"), response.getCost());
    }

    @Test
    void testGetCostGPT4() {
        ChatCompletionResponse response = new ChatCompletionResponse(
                "gpt-4",
                Collections.singletonList(new ChatChoice(0, null, null, null)),
                new Usage(1000, 500, 1500)
        );
        assertEquals(new BigDecimal("0.060"), response.getCost());
    }

    @Test
    void testGetCostGPT35Turbo() {
        ChatCompletionResponse response = new ChatCompletionResponse(
                "gpt-3.5-turbo",
                Collections.singletonList(new ChatChoice(0, null, null, null)),
                new Usage(1000, 500, 1500)
        );
        assertEquals(new BigDecimal("0.00125"), response.getCost());
    }

    @Test
    void testGetCostGPT35Turbo16K() {
        ChatCompletionResponse response = new ChatCompletionResponse(
                "gpt-3.5-turbo-16k",
                Collections.singletonList(new ChatChoice(0, null, null, null)),
                new Usage(16000, 8000, 24000)
        );
        assertEquals(new BigDecimal("0.0400"), response.getCost());
    }

    @Test
    void testGetCostWithNoModel() {
        ChatCompletionResponse response = new ChatCompletionResponse(
                null,
                Collections.singletonList(new ChatChoice(0, null, null, null)),
                new Usage(1000, 500, 1500)
        );
        assertEquals(BigDecimal.ZERO, response.getCost());
    }

    @Test
    void testGetCostWithEmptyModel() {
        ChatCompletionResponse response = new ChatCompletionResponse(
                "",
                Collections.singletonList(new ChatChoice(0, null, null, null)),
                new Usage(1000, 500, 1500)
        );
        assertEquals(BigDecimal.ZERO, response.getCost());
    }
}