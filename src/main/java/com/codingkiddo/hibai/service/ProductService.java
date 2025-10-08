package com.codingkiddo.hibai.service;

import com.codingkiddo.hibai.ai.EmbeddingClient;
import com.codingkiddo.hibai.domain.Product;
import com.codingkiddo.hibai.repo.ProductRepository;
import com.codingkiddo.hibai.vector.VectorDao;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    private final ProductRepository repo;
    private final VectorDao vectorDao;
    private final EmbeddingClient embeddings;

    public ProductService(ProductRepository repo, VectorDao vectorDao, EmbeddingClient embeddings) {
        this.repo = repo;
        this.vectorDao = vectorDao;
        this.embeddings = embeddings;
    }

    @Transactional
    public Product create(Product p) {
        Product saved = repo.save(p);
        float[] emb = embeddings.embed(saved.getName() + "\n\n" + saved.getDescription());
        vectorDao.updateEmbedding(saved.getId(), emb);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Product> semanticSearch(String query, int k) {
        float[] qvec = embeddings.embed(query);
        return vectorDao.semanticSearch(qvec, k);
    }

    @Transactional(readOnly = true)
    public List<Product> hybridSearch(String query, int k, double wVec, double wFts) {
        float[] qvec = embeddings.embed(query);
        return vectorDao.hybridSearch(qvec, query, k, wVec, wFts);
    }
}
