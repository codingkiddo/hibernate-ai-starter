package com.codingkiddo.hibai.web;

import com.codingkiddo.hibai.domain.Product;
import com.codingkiddo.hibai.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@Valid @RequestBody Product p) {
        return service.create(p);
    }

    @GetMapping("/search")
    public List<Product> search(@RequestParam String q, @RequestParam(defaultValue = "10") int k) {
        return service.semanticSearch(q, k);
    }

    @GetMapping("/search/hybrid")
    public List<Product> searchHybrid(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int k,
            @RequestParam(name = "wv", defaultValue = "0.5") double wVec,
            @RequestParam(name = "wf", defaultValue = "0.5") double wFts) {
        return service.hybridSearch(q, k, wVec, wFts);
    }
}
