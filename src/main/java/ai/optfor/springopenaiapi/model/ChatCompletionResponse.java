package ai.optfor.springopenaiapi.model;

import java.math.BigDecimal;
import java.util.List;

import static java.math.MathContext.DECIMAL32;

public record ChatCompletionResponse(
        String model,
        List<ChatChoice> choices,
        Usage usage) {

    public String getFirstCompletion() {
        return choices.get(0).message().content();
    }

    public String getDelta() {
        return choices.get(0).delta().content();
    }

    public BigDecimal getCost() {
        int promptLength = usage.prompt_tokens();
        int completionLength = usage.completion_tokens();

        if (model.startsWith("gpt-4")) {
            return computeCost(promptLength, "0.03").add(computeCost(completionLength, "0.06"));
        } else if (model.startsWith("gpt-3.5-turbo")) {
            return computeCost(promptLength, "0.001").add(computeCost(completionLength, "0.002"));
        } else if (model.startsWith("gpt-3.5-turbo-16k")) {
            return computeCost(promptLength, "0.0015").add(computeCost(completionLength, "0.002"));
        } else if (model.startsWith("gpt-4-1106-preview")) {
            return computeCost(promptLength, "0.01").add(computeCost(completionLength, "0.03"));
        } else {
            return null;
        }
    }

    private BigDecimal computeCost(int promptLength, String costPer1000) {
        return new BigDecimal(costPer1000).multiply(new BigDecimal(promptLength).divide(new BigDecimal(1000), DECIMAL32));
    }
}
