package com.product.service;

import com.product.dto.request.ProductCreationRequest;
import com.product.dto.request.ProductUpdateRequest;
import com.product.dto.response.ProductResponse;
import com.product.exception.ResourceNotFoundException;
import com.product.model.Product;
import com.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
public record ProductService(ProductRepository productRepository) {

    public String addProduct(ProductCreationRequest req) {
        log.info("Saving product ...");

        Product response = productRepository.save(Product.builder()
                .name(req.getName())
                .price(req.getPrice())
                .description(req.getDescription())
                .displayed(req.getDisplayed())
                .build());

        log.info("Product has saved successful");

        return response.getId();
    }

    public void updateProduct(ProductUpdateRequest req) {
        log.info("Updating user ...");

        Product product = get(req.getId());

        if (StringUtils.hasLength(req.getName())) {
            product.setName(req.getName());
        }
        if (req.getPrice() != null && req.getPrice() > 0) {
            product.setPrice(req.getPrice());
        }
        if (StringUtils.hasLength(req.getDescription())) {
            product.setDescription(req.getDescription());
        }
        if (req.getDisplayed() != null) {
            product.setDisplayed(req.getDisplayed());
        }

        productRepository.save(product);

        log.info("Product has updated successful");
    }


    public void deleteProduct(String id) {
        log.info("Deleting user ...");
        productRepository.delete(get(id));
        log.info("Product has deleted successful");
    }

    public ProductResponse getProduct(String id) {
        log.info("Getting user detail ...");

        Product product = get(id);

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .displayed(product.getDisplayed())
                .build();
    }

    public List<ProductResponse> getProducts() {
        log.info("Getting user list ...");

        List<Product> responses = productRepository.findAll();

        return responses.stream().map(product -> ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .displayed(product.getDisplayed())
                .build()
        ).toList();
    }

    private Product get(String id) {
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found product, id=" + id));
    }
}
