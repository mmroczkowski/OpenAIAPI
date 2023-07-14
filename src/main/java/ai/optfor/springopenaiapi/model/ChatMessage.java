package ai.optfor.springopenaiapi.model;

public record ChatMessage(
        String role,
        String content) {

    public static ChatMessage roleMessage(String content) {
        return new ChatMessage("system", content);
    }

    public static ChatMessage assistantMessage(String content) {
        return new ChatMessage("assistant", content);
    }

    public static ChatMessage contentMessage(String content) {
        return new ChatMessage("user", content);
    }
}
