package ai.optfor.springopenaiapi.utils;

import ai.optfor.springopenaiapi.enums.LLMModel;

import java.math.BigDecimal;

import static java.math.MathContext.DECIMAL32;
import static java.math.RoundingMode.HALF_UP;

public class StreamCostEstimate {

    private static final BigDecimal AVERAGE_CHARS_PER_TOKEN = new BigDecimal("3.0315");

    public static BigDecimal estimateCost(LLMModel model, Integer inputLength, Integer outputLength) {
        BigDecimal inputTokens = new BigDecimal(inputLength).divide(AVERAGE_CHARS_PER_TOKEN, DECIMAL32);
        BigDecimal outputTokens = new BigDecimal(outputLength).divide(AVERAGE_CHARS_PER_TOKEN, DECIMAL32);

        BigDecimal promptCost = new BigDecimal(model.getPromptCost())
                .multiply(inputTokens.divide(new BigDecimal(1000), DECIMAL32)).setScale(4, HALF_UP);
        BigDecimal completionCost = new BigDecimal(model.getCompletionCost())
                .multiply(outputTokens.divide(new BigDecimal(1000), DECIMAL32)).setScale(4, HALF_UP);

        return promptCost.add(completionCost);
    }
}
