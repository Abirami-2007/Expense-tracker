package backend.Client;

import backend.AdvisorDTO.ChatMessageDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Talks to a locally running Ollama instance (https://ollama.com).
 * Requires Ollama to be installed and running on the same machine as the backend,
 * with a model pulled, e.g.: `ollama pull llama3.2`
 */
@Component
public class OllamaClient {

    private final RestTemplate restTemplate;

    @Value("${ollama.base-url}")
    private String baseUrl;

    @Value("${ollama.model}")
    private String model;

    public OllamaClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Sends a full message list (system + history + latest user message) to Ollama's
     * /api/chat endpoint and returns the assistant's reply text.
     */
    public String chat(List<ChatMessageDTO> messages) {
        String url = baseUrl + "/api/chat";

        OllamaChatRequest request = new OllamaChatRequest();
        request.setModel(model);
        request.setMessages(messages);
        request.setStream(false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OllamaChatRequest> entity = new HttpEntity<>(request, headers);

        try {
            OllamaChatResponse response = restTemplate.postForObject(url, entity, OllamaChatResponse.class);

            if (response == null || response.getMessage() == null) {
                throw new RuntimeException("Ollama returned an empty response.");
            }
            return response.getMessage().getContent();

        } catch (RestClientException ex) {
            throw new RuntimeException(
                    "Could not reach Ollama at " + baseUrl +
                            ". Make sure Ollama is installed and running (`ollama serve`) " +
                            "and that the model '" + model + "' has been pulled (`ollama pull " + model + "`).",
                    ex
            );
        }
    }

    @Data
    private static class OllamaChatRequest {
        private String model;
        private List<ChatMessageDTO> messages;
        private boolean stream;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class OllamaChatResponse {
        private ChatMessageDTO message;
    }
}
