package ai.optfor.springopenaiapi.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum LLMModel {
    GPT_4_O_2024_08_06("gpt-4o-2024-08-06", "0.00250", "0.01000"),
    GPT_4_O_MINI("gpt-4o-mini", "0.000150", "0.000600"),
    GPT_4_O("gpt-4o", "0.005", "0.015"),
    GPT_4_TURBO_PREVIEW("gpt-4-turbo-preview", "0.01", "0.03"),
    GPT_4_TURBO("gpt-4-turbo", "0.01", "0.03"),
    GPT_3_5_TURBO("gpt-3.5-turbo", "0.0005", "0.0015"),
    GPT_3_5_TURBO_16K("gpt-3.5-turbo-16k", "0.0015", "0.0020"),
    GPT_4_VISION_PREVIEW("gpt-4-vision-preview", "0.01", "0.03"),
    GPT_4("gpt-4", "0.03", "0.06");

    private final String apiName;
    private final String promptCost;
    private final String completionCost;

    LLMModel(String apiName, String promptCost, String completionCost) {
        this.apiName = apiName;
        this.promptCost = promptCost;
        this.completionCost = completionCost;
    }

    public static LLMModel apiValueOf(String apiName) {
        return Arrays.stream(LLMModel.values()).filter(x -> apiName.startsWith(x.getApiName())).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown model: " + apiName));
    }
}
