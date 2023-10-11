package com.sample.controller.response;

import com.sample.dto.SampleDTO;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class LoadingPageResponse implements Serializable {
    private String nextKey;
    private List<SampleDTO> items;
}
