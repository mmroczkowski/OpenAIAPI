package ai.optfor.springopenaiapi.enums;

import ai.optfor.springopenaiapi.model.ChatMessage;
import ai.optfor.springopenaiapi.model.VisionMessage;

public enum Role {
    SYSTEM, USER, ASSISTANT;

    public String toApiName() {
        return this.name().toLowerCase();
    }

    public ChatMessage message(String text) {
        return ChatMessage.message(this, text);
    }

    public VisionMessage visionMessage(String text) {
        return VisionMessage.message(this, text);
    }

    public VisionMessage visionMessage(String text, String imageUrl) {
        if (this != USER) {
            throw new RuntimeException("For image message, only USER role is permitted");
        }
        return VisionMessage.visionUserMessage(text, imageUrl);
    }
}
