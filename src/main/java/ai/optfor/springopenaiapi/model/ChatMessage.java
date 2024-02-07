package ai.optfor.springopenaiapi.model;

public record ChatMessage(
        String role,
        String content) {

    public static ChatMessage systemMessage(String system) {
        return new ChatMessage("system", system);
    }

    public static ChatMessage assistantMessage(String assistant) {
        return new ChatMessage("assistant", assistant);
    }

    public static ChatMessage userMessage(String user) {
        return new ChatMessage("user", user);
    }
}
