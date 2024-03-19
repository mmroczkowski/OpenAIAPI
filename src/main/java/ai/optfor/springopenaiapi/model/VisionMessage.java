package ai.optfor.springopenaiapi.model;

import ai.optfor.springopenaiapi.enums.Role;

import java.util.List;

import static ai.optfor.springopenaiapi.enums.Role.USER;

public record VisionMessage(String role, List<VisionMessageContent> content) {

    public static VisionMessage message(Role role, String text) {
        return new VisionMessage(role.toApiName(), List.of(VisionMessageContent.visionTextMessageContent(text)));
    }

    public static VisionMessage visionUserMessage(String text, String imageUrl) {
        return new VisionMessage(USER.toApiName(), List.of(VisionMessageContent.visionTextMessageContent(text),
                VisionMessageContent.visionImageMessageContent(imageUrl)));
    }
}
