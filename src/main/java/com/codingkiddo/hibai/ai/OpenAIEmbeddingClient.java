package com.codingkiddo.hibai.ai;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked","rawtypes"})
public class OpenAIEmbeddingClient implements EmbeddingClient {
    private final RestClient http;
    private final String model;

    public OpenAIEmbeddingClient(String baseUrl, String model, String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("OPENAI_API_KEY is missing. Provide hibai.embeddings.openai.api-key or env var.");
        }
        this.http = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
        this.model = model;
    }

    @Override
    public float[] embed(String text) {
    	Map<String, Object> req = Map.of("model", model, "input", text);
        Map resp = http.post().uri("/embeddings")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .body(Map.class);
        List<Map> data = (List<Map>) resp.get("data");
        if (data == null || data.isEmpty()) {
            throw new IllegalStateException("OpenAI embeddings API returned no data");
        }
        List<Number> arr = (List<Number>) data.get(0).get("embedding");
        float[] out = new float[arr.size()];
        for (int i = 0; i < arr.size(); i++) out[i] = arr.get(i).floatValue();
        return out;
    }
}
