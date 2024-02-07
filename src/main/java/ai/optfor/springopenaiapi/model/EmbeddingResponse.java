package ai.optfor.springopenaiapi.model;

import ai.optfor.springopenaiapi.enums.EmbedModel;
import io.micrometer.common.util.StringUtils;

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

        if (StringUtils.isBlank(model)) {
            return BigDecimal.ZERO;
        }

        EmbedModel modelEnum = EmbedModel.apiValueOf(model);

        return computeCost(length, modelEnum.getCost());
    }

    private BigDecimal computeCost(int length, String costPer1000) {
        return new BigDecimal(costPer1000).multiply(new BigDecimal(length).divide(new BigDecimal(1000), DECIMAL32));
    }
}
