package ai.optfor.springopenaiapi.model;

import ai.optfor.springopenaiapi.enums.LLMModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micrometer.common.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

import static java.math.MathContext.DECIMAL32;

public record ChatCompletionResponse(
        String model,
        List<ChatChoice> choices,
        Usage usage) {

    @JsonIgnore
    public String getFirstCompletion() {
        if (choices.get(0).message() != null) {
            return choices.get(0).message().content();
        }
        return null;
    }

    @JsonIgnore
    public String getDelta() {
        if (choices.get(0).delta() != null) {
            return choices.get(0).delta().content();
        }
        return null;
    }

    public BigDecimal getCost() {
        int promptLength = usage.prompt_tokens();
        int completionLength = usage.completion_tokens();

        if (StringUtils.isBlank(model)) {
            return BigDecimal.ZERO;
        }

        LLMModel modelEnum = LLMModel.apiValueOf(model);

        return computeCost(promptLength, modelEnum.getPromptCost()).add(computeCost(completionLength, modelEnum.getCompletionCost()));
    }

    private BigDecimal computeCost(int length, String costPer1000) {
        return new BigDecimal(costPer1000).multiply(new BigDecimal(length).divide(new BigDecimal(1000), DECIMAL32));
    }
}
