package ai.optfor.springopenaiapi.model;

public record Usage(
        int prompt_tokens,
        int completion_tokens,
        int total_tokens) {
}
