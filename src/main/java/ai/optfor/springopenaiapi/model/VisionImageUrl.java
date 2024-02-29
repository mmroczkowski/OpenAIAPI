package ai.optfor.springopenaiapi.model;

public record VisionImageUrl(String url) {
    public static VisionImageUrl visionImageUrl(String url) {
        return new VisionImageUrl(url);
    }
}
