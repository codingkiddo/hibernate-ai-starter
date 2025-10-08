package com.codingkiddo.hibai.vector;

import com.pgvector.PGvector;
import jakarta.annotation.PostConstruct;
import java.sql.Connection;
import javax.sql.DataSource;
import org.springframework.context.annotation.Configuration;

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
