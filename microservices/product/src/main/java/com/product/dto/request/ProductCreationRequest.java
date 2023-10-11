package com.product.dto.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class ProductCreationRequest implements Serializable {

    @NotBlank(message = "name must be not blank")
    private String name;

    @Min(value = 1, message = "price must be not blank")
    private Double price;

    private String description;

    private Boolean displayed;
}
