package ai.optfor.springopenaiapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record VisionMessageContent(String type, String text, VisionImageUrl image_url) {
    public static VisionMessageContent visionTextMessageContent(String text) {
        return new VisionMessageContent("text", text, null);
    }

    public static VisionMessageContent visionImageMessageContent(String image_url) {
        return new VisionMessageContent("image_url", null, VisionImageUrl.visionImageUrl(image_url));
    }
}
