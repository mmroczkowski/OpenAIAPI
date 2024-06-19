package ai.optfor.springopenaiapi;

import ai.optfor.springopenaiapi.cache.DefaultPromptCache;
import ai.optfor.springopenaiapi.cache.PromptCache;
import ai.optfor.springopenaiapi.enums.EmbedModel;
import ai.optfor.springopenaiapi.enums.LLMModel;
import ai.optfor.springopenaiapi.enums.TTSModel;
import ai.optfor.springopenaiapi.enums.TTSVoice;
import ai.optfor.springopenaiapi.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static ai.optfor.springopenaiapi.enums.Role.*;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static org.springframework.http.MediaType.*;

public class OpenAIApi {

    private static final Logger log = LoggerFactory.getLogger(OpenAIApi.class);

    private final ObjectMapper mapper = new ObjectMapper();

    private final PromptCache promptCache;

    private final ExecutorService executorService;

    public OpenAIApi(PromptCache promptCache) {
        this.promptCache = (promptCache == null ? new DefaultPromptCache() : promptCache);
        mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.executorService = Executors.newFixedThreadPool(3);
    }

    public Flux<String> streamingChat(LLMModel model, String system, String user, String assistant, Integer maxTokens, double temperature, Map<Integer, Integer> logit_bias, String openaiKey) {
        return streamingChat(model, List.of(SYSTEM.message(system), USER.message(user), ASSISTANT.message(assistant)), maxTokens, temperature, logit_bias, openaiKey);
    }

    public Flux<String> streamingChat(LLMModel model, List<ChatMessage> messages, Integer maxTokens, double temperature, Map<Integer, Integer> logit_bias, String openaiKey) {
        log.info("\nCalling OpenAI API:\n" +
                "Model: " + model + " Max tokens:" + maxTokens + " Temperature:" + temperature + "\n" +
                messages.stream().map(chatMessage -> chatMessage.role() + ":\n" +
                        chatMessage.content()).collect(java.util.stream.Collectors.joining("\n")));
        ChatCompletionRequest request = new ChatCompletionRequest(model.getApiName(),
                messages, temperature, maxTokens, null, true, logit_bias);

        String json;
        try {
            json = mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        StringBuilder fullResponse = new StringBuilder();
        long start = System.currentTimeMillis();
        Flux<String> result = WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader("Authorization", "Bearer " + openaiKey)
                .build()
                .post()
                .contentType(APPLICATION_JSON)
                .bodyValue(json)
                .accept(TEXT_EVENT_STREAM)
                .exchangeToFlux((r -> r.bodyToFlux(String.class)))
                .takeWhile(response -> !response.equals("[DONE]"))
                .handle((jsonResponse, sink) -> {
                    try {
                        String delta = mapper.readValue(jsonResponse, ChatCompletionResponse.class).getDelta();
                        if (delta == null) {
                            delta = "";
                        }
                        fullResponse.append(delta);
                        sink.next(delta);
                    } catch (JsonProcessingException e) {
                        sink.error(new RuntimeException("Error while processing JSON response", e));
                    }
                });

        return result.doOnComplete(() -> {
            long end = System.currentTimeMillis();
            double seconds = ((double) (end - start)) / 1000;
            log.info("\nReceived response from OpenAI API: " + seconds + " s.(" + fullResponse + ")");
        });
    }

    public ChatCompletionResponse vision(LLMModel model, List<VisionMessage> messages, Integer maxTokens, double temperature, Map<Integer, Integer> logit_bias, String openaiKey) {
        VisionCompletionRequest request = new VisionCompletionRequest(model.getApiName(), messages, temperature, maxTokens, false, logit_bias);
        return prepareRestTemplate(openaiKey).postForObject("https://api.openai.com/v1/chat/completions", request, ChatCompletionResponse.class);
    }

    public Flux<String> visionStreaming(LLMModel model, List<VisionMessage> messages, Integer maxTokens, double temperature, Map<Integer, Integer> logit_bias, String openaiKey) {
        log.info("\nCalling OpenAI API:\n" +
                "Model: " + model + " Max tokens:" + maxTokens + " Temperature:" + temperature + "\n" +
                messages.stream().map(chatMessage -> chatMessage.role() + ":\n" +
                        chatMessage.content()).collect(java.util.stream.Collectors.joining("\n")));
        VisionCompletionRequest request = new VisionCompletionRequest(model.getApiName(), messages, temperature, maxTokens, true, logit_bias);

        String json;
        try {
            json = mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        StringBuilder fullResponse = new StringBuilder();
        long start = System.currentTimeMillis();
        Flux<String> result = WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader("Authorization", "Bearer " + openaiKey)
                .build()
                .post()
                .contentType(APPLICATION_JSON)
                .bodyValue(json)
                .accept(TEXT_EVENT_STREAM)
                .exchangeToFlux((r -> r.bodyToFlux(String.class)))
                .takeWhile(response -> !response.equals("[DONE]"))
                .handle((jsonResponse, sink) -> {
                    try {
                        String delta = mapper.readValue(jsonResponse, ChatCompletionResponse.class).getDelta();
                        if (delta == null) {
                            delta = "";
                        }
                        fullResponse.append(delta);
                        sink.next(delta);
                    } catch (JsonProcessingException e) {
                        sink.error(new RuntimeException("Error while processing JSON response", e));
                    }
                });

        return result.doOnComplete(() -> {
            long end = System.currentTimeMillis();
            double seconds = ((double) (end - start)) / 1000;
            log.info("\nReceived response from OpenAI API: " + seconds + " s.(" + fullResponse + ")");
        });
    }

    public byte[] createSpeech(TTSModel model, String input, TTSVoice voice, String openaiKey) {
        RestTemplate restTemplate = prepareRestTemplate(openaiKey);
        ResponseEntity<byte[]> response = restTemplate.postForEntity("https://api.openai.com/v1/audio/speech",
                new STTRequest(model.getApiName(), input, voice.toApiName()), byte[].class
        );

        if (response.hasBody()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to get audio response from OpenAI API");
        }
    }

    public String transcribeAudio(byte[] audioBytes, String languageKey, String openaiKey) {
        // Create an anonymous subclass of ByteArrayResource to override the filename
        Resource audioResource = new ByteArrayResource(audioBytes) {
            @Override
            public String getFilename() {
                return "audio.oga";
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", audioResource);
        body.add("model", "whisper-1");
        body.add("language", languageKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String url = "https://api.openai.com/v1/audio/transcriptions";
        ResponseEntity<String> response = prepareRestTemplate(openaiKey).postForEntity(url, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                return mapper.readValue(response.getBody(), AudioResponse.class).text();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException(response.toString());
        }
    }

    public ChatCompletionResponse chat(LLMModel model, String system, String user, Integer maxTokens, double temperature, boolean jsonMode, String openaiKey) {
        return chat(model, List.of(SYSTEM.message(system), USER.message(user)), maxTokens, temperature, jsonMode, null, openaiKey);
    }

    public ChatCompletionResponse chat(LLMModel model, String system, String user, String assistant, Integer maxTokens, double temperature, boolean jsonMode, String openaiKey) {
        return chat(model, List.of(SYSTEM.message(system), USER.message(user), ASSISTANT.message(assistant)), maxTokens, temperature, jsonMode, null, openaiKey);
    }

    public ChatCompletionResponse chat(LLMModel model, List<ChatMessage> chats, int maxTokens, double temperature, boolean jsonMode, Map<Integer, Integer> logitBias, String openaiKey) {
        List<ChatMessage> filteredChats = chats.stream().filter(c -> !StringUtils.isBlank(c.content())).toList();
        Future<ChatCompletionResponse> future = executorService.submit(() -> chatInternal(model, filteredChats, maxTokens, temperature, jsonMode, logitBias, openaiKey));

        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ChatCompletionResponse chatInternal(LLMModel model, List<ChatMessage> chats, int maxTokens, double temperature, boolean jsonMode,
                                                Map<Integer, Integer> logitBias, String openaiKey) {
        log.info("\nCalling OpenAI API:\n" +
                "Model: " + model + " Max tokens:" + maxTokens + " Temperature:" + temperature + "\n" +
                chats.stream().map(chatMessage -> chatMessage.role() + ":\n" +
                        chatMessage.content()).collect(java.util.stream.Collectors.joining("\n")));

        RestTemplate restTemplate = prepareRestTemplate(openaiKey);
        int retryCount = 0;
        while (true) {
            try {
                ChatCompletionRequest request = new ChatCompletionRequest(
                        model.getApiName(), chats, temperature, maxTokens, jsonMode ? new ResponseFormat("json_object") : null, false, logitBias);

                if (Double.compare(temperature, 0) == 0) {
                    String cached = promptCache.get(createKey(model, chats, maxTokens));
                    if (cached != null) {
                        ChatCompletionResponse response = mapper.readValue(cached, ChatCompletionResponse.class);
                        log.info("\nReturning cached response: {}", mapper.writeValueAsString(response));
                        return response;
                    }
                }
                long start = System.currentTimeMillis();
                ChatCompletionResponse response = restTemplate.postForObject("https://api.openai.com/v1/chat/completions",
                        request, ChatCompletionResponse.class);
                long end = System.currentTimeMillis();
                double seconds = ((double) (end - start)) / 1000;
                log.info("\nReceived response from OpenAI API: " + seconds + " s.(" +
                        (response.usage().completion_tokens() / seconds) + " TPS) {}", mapper.writeValueAsString(response));

                if (Double.compare(temperature, 0) == 0) {
                    promptCache.put(createKey(model, chats, maxTokens), mapper.writeValueAsString(response));
                }

                return response;
            } catch (Exception e) {
                if (++retryCount == 3) throw new RuntimeException(e);
            }
        }
    }

    public EmbeddingResponse embedding(EmbedModel model, String content, String openaiKey) {
        return embedding(model, List.of(content), openaiKey);
    }

    public EmbeddingResponse embedding(EmbedModel model, List<String> content, String openaiKey) {
        RestTemplate restTemplate = prepareRestTemplate(openaiKey);
        EmbeddingRequest request = new EmbeddingRequest(model.getApiName(), content);
        try {
            log.info("Sending request to OpenAI API: {}", mapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return restTemplate.postForObject("https://api.openai.com/v1/embeddings", request, EmbeddingResponse.class);
    }

    private String createKey(LLMModel model, List<ChatMessage> chats, int maxTokens) {
        return model.getApiName() + chats + maxTokens;
    }

    private RestTemplate prepareRestTemplate(String openaiKey) {
        RestTemplate restTemplate = new RestTemplate();

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(1000 * 5);
        requestFactory.setReadTimeout(1000 * 60 * 10);
        restTemplate.setRequestFactory(requestFactory);

        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + openaiKey);
            return execution.execute(request, body);
        };

        restTemplate.setInterceptors(List.of(interceptor));
        return restTemplate;
    }
}
