package ai.optfor.springopenaiapi.enums;

public enum TTSVoice {
    ALLOY, ECHO, FABLE, ONYX, NOVA, SHIMMER;

    public String toApiName() {
        return this.name().toLowerCase();
    }
}
