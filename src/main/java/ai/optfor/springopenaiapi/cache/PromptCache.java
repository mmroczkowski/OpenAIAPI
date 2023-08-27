package ai.optfor.springopenaiapi.cache;

public interface PromptCache {

    void put(String key, String response);

    String get(String key);
}