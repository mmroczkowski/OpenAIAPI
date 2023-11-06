package ai.optfor.springopenaiapi.model;

public record ChatChoice(
        int index,
        ChatMessage message,
        ChatMessage delta,
        String finish_reason) {
}
