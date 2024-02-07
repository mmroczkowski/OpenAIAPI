package ai.optfor.springopenaiapi.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum EmbedModel {
    TEXT_EMBEDDING_3_SMALL("text-embedding-3-small", "0.00002"),
    TEXT_EMBEDDING_3_LARGE("text-embedding-3-large", "0.00013"),
    TEXT_EMBEDDING_ADA_002("text-embedding-ada-002", "0.00010");

    private final String apiName;
    private final String cost;

    EmbedModel(String apiName, String cost) {
        this.apiName = apiName;
        this.cost = cost;
    }

    public static EmbedModel apiValueOf(String apiName) {
        return Arrays.stream(EmbedModel.values()).filter(x -> x.getApiName().equals(apiName)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown model: " + apiName));
    }
}
