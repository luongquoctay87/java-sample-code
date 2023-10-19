package com.mono.controller;

import com.mono.dto.request.UserCreationRequest;
import com.mono.dto.request.UserUpdateRequest;
import com.mono.dto.response.UserDetailResponse;
import com.mono.dto.response.UserListResponse;
import com.mono.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Validated
@Slf4j
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Operation(summary = "Add new user", description = "Return user ID")
    @PostMapping(path = "/user", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public long createUser(@Valid @RequestBody UserCreationRequest request) {
        log.info("Request POST /user");
        return accountService.addUser(request);
    }

    @Operation(summary = "Update user", description = "Return message")
    @PutMapping(path = "/user", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    public void updateUser(@Valid @RequestBody UserUpdateRequest request) {
        log.info("Request PUT /user");
        accountService.updateUser(request);
    }

    @Operation(summary = "Disable user", description = "Return no content")
    @PatchMapping(path = "/user/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    public void permitUser(@PathVariable("id") @Min(1) long id, @RequestParam boolean status) {
        log.info("Request DELETE /user/{}", id);
        accountService.permitUser(id, status);
    }

    @Operation(summary = "Delete user", description = "Return no content")
    @DeleteMapping(path = "/user/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(NO_CONTENT)
    public void deleteUser(@PathVariable("id") @Min(1) long id) {
        log.info("Request DELETE /user/{}", id);
        accountService.deleteUser(id);
    }

    @Operation(summary = "Get user detail", description = "Return user detail")
    @GetMapping(path = "/user/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public UserDetailResponse getUser(@PathVariable("id") @Min(1) long id) {
        log.info("Request GET /user/{}", id);
        return accountService.getUser(id);
    }

    @Operation(summary = "Get user list", description = "Return list of users")
    @GetMapping(path = "/users", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public UserListResponse getUserList(@RequestParam(required = false) String username,
                                        @RequestParam(defaultValue = "0") int pageNo,
                                        @RequestParam(defaultValue = "20") int pageSize) {
        log.info("Request GET /users");

        if (StringUtils.hasLength(username)) {
            return accountService.search(username, pageNo, pageSize);
        }

        return accountService.getAll(pageNo, pageSize);
    }
}
