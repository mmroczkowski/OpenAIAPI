package ai.optfor.springopenaiapi.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TTSModel {
    TTS_1("tts-1"),
    TTS_1_HD("tts-1-hd"),;

    private final String apiName;

    TTSModel(String apiName) {
        this.apiName = apiName;
    }

    public static TTSModel apiValueOf(String apiName) {
        return Arrays.stream(TTSModel.values()).filter(x -> apiName.startsWith(x.getApiName())).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown model: " + apiName));
    }
}
