package com.product.dto.response;

import com.product.dto.Address;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ProductResponse implements Serializable {
    private String id;
    private String name;
    private Double price;
    private String description;
    private Boolean displayed;
}
