package ai.optfor.springopenaiapi.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultPromptCache implements PromptCache {

    private final Map<String, String> cache = new ConcurrentHashMap<>();

    @Override
    public void put(String key, String response) {
        cache.put(key, response);
    }

    @Override
    public String get(String key) {
        return cache.get(key);
    }
}
