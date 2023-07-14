package ai.optfor.springopenaiapi.model;

import java.util.List;

public record ChatCompletionResponse(
        List<ChatChoice> choices,
        Usage usage) {
}
