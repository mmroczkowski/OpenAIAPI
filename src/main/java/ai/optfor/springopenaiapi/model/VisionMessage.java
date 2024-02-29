package ai.optfor.springopenaiapi.model;

import java.util.List;

public record VisionMessage(String role, List<VisionMessageContent> content) {

    public static VisionMessage visionSystemMessage(String text) {
        return new VisionMessage("system", List.of(VisionMessageContent.visionTextMessageContent(text)));
    }

    public static VisionMessage visionUserMessage(String text, String url) {
        return new VisionMessage("user", List.of(VisionMessageContent.visionTextMessageContent(text),
                VisionMessageContent.visionImageMessageContent(url)));
    }

    public static VisionMessage visionUserTextMessage(String text) {
        return new VisionMessage("user", List.of(VisionMessageContent.visionTextMessageContent(text)));
    }

    public static VisionMessage visionAssistantMessage(String text) {
        return new VisionMessage("assistant", List.of(VisionMessageContent.visionTextMessageContent(text)));
    }
}
