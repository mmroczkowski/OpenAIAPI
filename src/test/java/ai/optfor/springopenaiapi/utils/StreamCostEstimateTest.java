package ai.optfor.springopenaiapi.utils;

import ai.optfor.springopenaiapi.enums.LLMModel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_UP;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StreamCostEstimateTest {

    @Test
    void testEstimateCostGPT4WithSmallInputOutput() {
        BigDecimal cost = StreamCostEstimate.estimateCost(LLMModel.GPT_4, 10, 20);
        assertEquals(new BigDecimal("0.0005"), cost);
    }

    @Test
    void testEstimateCostGPT35TurboWithZeroInputOutput() {
        BigDecimal cost = StreamCostEstimate.estimateCost(LLMModel.GPT_3_5_TURBO, 0, 0);
        assertEquals(new BigDecimal("0.0000"), cost);
    }

    @Test
    void testEstimateCostGPT35Turbo16KWithLargeInputOutput() {
        BigDecimal cost = StreamCostEstimate.estimateCost(LLMModel.GPT_3_5_TURBO_16K, 16000, 16000);
        assertEquals(new BigDecimal("0.0185"), cost);
    }
    @Test
    void testEstimateCostWithUnknownModel() {
        // This test assumes the method is modified to handle unknown models gracefully, perhaps by throwing an IllegalArgumentException
        try {
            StreamCostEstimate.estimateCost(LLMModel.apiValueOf("unknown-model"), 100, 200);
        } catch (IllegalArgumentException e) {
            assertEquals("Unknown model: unknown-model", e.getMessage());
        }
    }
}

