package ai.optfor.springopenaiapi.model;

import java.util.List;

public record VisionCompletionRequest(
        String model,
        List<VisionMessage> messages,
        double temperature,
        int max_tokens,
        boolean stream) {
}
