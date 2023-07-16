package ai.optfor.springopenaiapi.model;

import lombok.Getter;

@Getter
public enum OpenAIModel {
    GPT_3_5_TURBO("gpt-3.5-turbo"),
    GPT_3_5_TURBO_16K("gpt-3.5-turbo-16k"),
    GPT_4("gpt-4"),
    TEXT_EMBEDDING_ADA_002("text-embedding-ada-002");

    private final String modelName;

    OpenAIModel(String modelName) {
        this.modelName = modelName;
    }
}
