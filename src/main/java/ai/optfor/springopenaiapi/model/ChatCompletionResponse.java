package ai.optfor.springopenaiapi.model;

import java.util.List;

public record ChatCompletionResponse(
        List<ChatChoice> choices,
        Usage usage) {

    public String getFirstCompletion() {
        return choices.get(0).message().content();
    }
}
