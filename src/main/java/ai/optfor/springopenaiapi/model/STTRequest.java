package ai.optfor.springopenaiapi.model;

public record STTRequest(
        String model,
        String input,
        String voice) {
}
