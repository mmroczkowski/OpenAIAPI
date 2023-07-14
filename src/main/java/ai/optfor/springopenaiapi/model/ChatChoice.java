package ai.optfor.springopenaiapi.model;

public record ChatChoice(
        int index,
        ChatMessage message,
        String finishReason) {
}
