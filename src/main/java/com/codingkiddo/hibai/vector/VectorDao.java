package com.codingkiddo.hibai.vector;

import com.codingkiddo.hibai.domain.Product;
import com.pgvector.PGvector;
import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class VectorDao {
    private final JdbcTemplate jdbc;

    public VectorDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Product> PRODUCT_ROW_MAPPER =
            (rs, i) -> {
                Product p = new Product();
                p.setId(rs.getLong("id"));
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));
                return p;
            };

    public void updateEmbedding(long productId, float[] embedding) {
        String sql = "UPDATE product SET embedding = ? WHERE id = ?";
        jdbc.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setObject(1, new PGvector(embedding));
                    ps.setLong(2, productId);
                    return ps;
                });
    }

    public List<Product> semanticSearch(float[] queryEmbedding, int k) {
        String sql =
                "SELECT id, name, description, metadata FROM product ORDER BY embedding <=> ? ASC LIMIT ?";
        return jdbc.query(
                con -> {
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setObject(1, new PGvector(queryEmbedding));
                    ps.setInt(2, k);
                    return ps;
                },
                PRODUCT_ROW_MAPPER);
    }

    /** Hybrid FTS + vector search. */
    public List<Product> hybridSearch(
            float[] queryEmbedding, String query, int k, double wVec, double wFts) {
        String sql =
                "SELECT id, name, description, metadata FROM product ORDER BY ( "
                        + " ? * ts_rank_cd( "
                        + "        to_tsvector('english', coalesce(name,'') || ' ' || coalesce(description,'')), "
                        + "    websearch_to_tsquery('english', ?) "
                        + " )"
                        + "  ? * (1 - (embedding <=> ?))"
                        + " ) DESC"
                        + " LIMIT ?";
        return jdbc.query(
                con -> {
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setDouble(1, wFts);
                    ps.setString(2, query);
                    ps.setDouble(3, wVec);
                    ps.setObject(4, new PGvector(queryEmbedding));
                    ps.setInt(5, k);
                    return ps;
                },
                PRODUCT_ROW_MAPPER);
    }
}
