package ai.optfor.springopenaiapi.model;

import java.util.List;

public record EmbeddingResponse(
        List<EmbeddingData> data,
        Usage usage) {
}
