package com.codingkiddo.hibai.vector;

import com.pgvector.PGvector;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
public class PgVectorConfig {

    private final DataSource dataSource;

    public PgVectorConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void registerTypes() {
        try (Connection conn = dataSource.getConnection()) {
            PGvector.registerTypes(conn);
        } catch (Exception ignored) {
            // Registration is optional for setting parameters; continue without failing startup.
        }
    }
}
