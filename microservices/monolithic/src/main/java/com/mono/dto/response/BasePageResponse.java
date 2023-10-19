package com.mono.dto.response;

import lombok.Data;

@Data
public sealed class BasePageResponse permits UserListResponse {
    private long pageNo;
    private long pageSize;
    private long total;
}
