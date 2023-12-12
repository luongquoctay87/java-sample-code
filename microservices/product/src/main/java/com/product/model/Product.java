package com.product.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@Builder
@Document(collection = "product")
public class Product {
    @Id
    private String id;
    private String name;
    private Double price;
    private String description;
    private Boolean displayed;
    private Date createdAt;
    private Date updatedAt;
}
