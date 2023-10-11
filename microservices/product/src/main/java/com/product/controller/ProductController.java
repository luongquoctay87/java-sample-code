package com.product.controller;

import com.product.dto.request.ProductCreationRequest;
import com.product.dto.request.ProductUpdateRequest;
import com.product.dto.response.ProductResponse;
import com.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/product")
@Slf4j(topic = "PRODUCT")
@Validated
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "Add new user", description = "Return product ID")
    @PostMapping(path = "/", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public String createProduct(@Valid @RequestBody ProductCreationRequest request) {
        return productService.addProduct(request);
    }

    @Operation(summary = "Update product", description = "Return no content")
    @PutMapping(path = "/", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    public void updateProduct(@Valid @RequestBody ProductUpdateRequest request) {
        productService.updateProduct(request);
    }

    @Operation(summary = "Delete product", description = "Return no content")
    @DeleteMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(NO_CONTENT)
    public void deleteProduct(@PathVariable("id") String id) {
        productService.deleteProduct(id);
    }

    @Operation(summary = "Get product detail", description = "Return product detail")
    @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public ProductResponse getProduct(@PathVariable("id") String id) {
        return productService.getProduct(id);
    }

    @Operation(summary = "Get product list", description = "Return list of products")
    @GetMapping(path = "/", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public List<ProductResponse> getProducts(@RequestParam(required = false) String name) {
        return productService.getProducts();
    }
}
