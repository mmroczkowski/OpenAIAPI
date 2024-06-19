package ai.optfor.springopenaiapi.model;

import java.util.List;
import java.util.Map;

public record ChatCompletionRequest(
        String model,
        List<ChatMessage> messages,
        double temperature,
        int max_tokens,
        ResponseFormat response_format,
        boolean stream,
        Map<Integer, Integer> logit_bias) {
}
