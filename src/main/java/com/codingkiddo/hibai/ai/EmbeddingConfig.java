package com.codingkiddo.hibai.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingConfig {

    @Bean
    public EmbeddingClient embeddingClient(
            @Value("${hibai.embeddings.provider:ollama}") String provider,
            @Value("${hibai.embeddings.model:nomic-embed-text}") String model,
            @Value("${hibai.embeddings.ollama.base-url:http://localhost:11434}") String ollamaBaseUrl,
            @Value("${hibai.embeddings.openai.base-url:https://api.openai.com/v1}") String openaiBaseUrl,
            @Value("${hibai.embeddings.openai.api-key:}") String openaiApiKey
    ) {
        if ("openai".equalsIgnoreCase(provider)) {
            return new OpenAIEmbeddingClient(openaiBaseUrl, model, openaiApiKey);
        }
        return new OllamaEmbeddingClient(ollamaBaseUrl, model);
    }
}
