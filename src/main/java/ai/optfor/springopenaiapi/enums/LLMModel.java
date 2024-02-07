package ai.optfor.springopenaiapi.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum LLMModel {
    GPT_4_TURBO_PREVIEW("gpt-4-turbo-preview", "0.01", "0.03"),
    GPT_4_1106_PREVIEW("gpt-4-1106-preview", "0.01", "0.03"),
    GPT_4_0125_PREVIEW("gpt-4-0125-preview", "0.01", "0.03"),
    GPT_4("gpt-4", "0.03", "0.06"),
    GPT_3_5_TURBO("gpt-3.5-turbo", "0.0005", "0.0015"),
    GPT_3_5_TURBO_16K("gpt-3.5-turbo-16k", "0.0015", "0.0020");

    private final String apiName;
    private final String promptCost;
    private final String completionCost;

    LLMModel(String apiName, String promptCost, String completionCost) {
        this.apiName = apiName;
        this.promptCost = promptCost;
        this.completionCost = completionCost;
    }

    public static LLMModel apiValueOf(String apiName) {
        return Arrays.stream(LLMModel.values()).filter(x -> x.getApiName().equals(apiName)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown model: " + apiName));
    }
}
