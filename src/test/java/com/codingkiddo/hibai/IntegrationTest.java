package com.codingkiddo.hibai;

import com.codingkiddo.hibai.ai.EmbeddingClient;
import com.codingkiddo.hibai.domain.Product;
import com.codingkiddo.hibai.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
class IntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("pgvector/pgvector:pg16")
            .withDatabaseName("hibai")
            .withUsername("hibai")
            .withPassword("hibai");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        EmbeddingClient fakeEmbeddingClient() {
            return text -> {
                try {
                    byte[] bytes = MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8));
                    float[] vec = new float[768];
                    for (int i = 0; i < vec.length; i++) {
                        vec[i] = (bytes[i % bytes.length] & 0xFF) / 255.0f;
                    }
                    return vec;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
        }
    }

    @Test
    void createAndSearch(ProductService svc) {
        Product p1 = Product.builder().name("Apple MacBook Air").description("Light laptop for developers").metadata("{\"category\":\"laptop\"}").build();
        Product p2 = Product.builder().name("Dell XPS 13").description("Premium ultrabook").metadata("{\"category\":\"laptop\"}").build();
        svc.create(p1);
        svc.create(p2);

        List<Product> results = svc.semanticSearch("ultraportable laptop", 5);
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getId()).isNotNull();
    }
}
