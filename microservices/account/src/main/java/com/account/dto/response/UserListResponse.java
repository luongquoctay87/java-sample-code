package com.account.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
public class UserListResponse implements Serializable {
    private int pageNo;
    private int pageSize;
    private int totalPage;
    private List<UserDetailResponse> users;
}
