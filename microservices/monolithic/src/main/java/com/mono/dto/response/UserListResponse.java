package com.mono.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public non-sealed class UserListResponse extends BasePageResponse implements Serializable {
    private List<UserDetailResponse> data;
}
