package ai.optfor.springopenaiapi.model;

import java.util.List;

public record EmbeddingRequest(
        String model,
        List<String> input) {
}
