package ai.optfor.springopenaiapi.model;

import java.math.BigDecimal;
import java.util.List;

import static java.math.MathContext.DECIMAL32;

public record EmbeddingResponse(
        String model,
        List<EmbeddingData> data,
        Usage usage) {

    public EmbeddingData getFirstCompletion() {
        return data.get(0);
    }

    public BigDecimal getCost() {
        int length = usage.total_tokens();
        if (model.startsWith("text-embedding-ada-002")) {
            return computeCost(length, "0.0001");
        } else {
            return null;
        }
    }

    private BigDecimal computeCost(int promptLength, String costPer1000) {
        return new BigDecimal(costPer1000).multiply(new BigDecimal(promptLength).divide(new BigDecimal(1000), DECIMAL32));
    }
}
