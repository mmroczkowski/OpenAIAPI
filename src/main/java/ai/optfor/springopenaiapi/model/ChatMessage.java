package ai.optfor.springopenaiapi.model;

import ai.optfor.springopenaiapi.enums.Role;

public record ChatMessage(
        String role,
        String content) {

    public static ChatMessage message(Role role, String text) {
        return new ChatMessage(role.toApiName(), text);
    }
}
