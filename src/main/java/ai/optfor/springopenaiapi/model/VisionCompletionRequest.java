package ai.optfor.springopenaiapi.model;

import java.util.List;
import java.util.Map;

public record VisionCompletionRequest(
        String model,
        List<Map<String, Object>> messages,
        double temperature,
        int max_tokens,
        boolean stream) {
}
