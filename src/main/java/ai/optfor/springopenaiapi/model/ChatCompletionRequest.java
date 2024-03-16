package ai.optfor.springopenaiapi.model;

import java.util.List;

public record ChatCompletionRequest(
        String model,
        List<ChatMessage> messages,
        double temperature,
        int max_tokens,
        ResponseFormat response_format,
        boolean stream) {
}
