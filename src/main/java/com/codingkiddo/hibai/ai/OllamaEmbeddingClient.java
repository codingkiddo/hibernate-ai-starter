package com.codingkiddo.hibai.ai;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

public class OllamaEmbeddingClient implements EmbeddingClient {
    private final RestClient http;
    private final String model;

    public OllamaEmbeddingClient(String baseUrl, String model) {
        this.http = RestClient.builder().baseUrl(baseUrl).build();
        this.model = model;
    }

    @Override
    public float[] embed(String text) {
    	Map<String, Object> req  = Map.of("model", model, "prompt", text);
        Map resp = http.post().uri("/api/embeddings")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .body(Map.class);
        List<Number> arr  = (java.util.List<Number>) resp.get("embedding");
        float[] out = new float[arr.size()];
        for (int i = 0; i < arr.size(); i++) out[i] = arr.get(i).floatValue();
        return out;
    }
}
