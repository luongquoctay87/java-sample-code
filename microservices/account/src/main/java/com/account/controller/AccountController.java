package com.account.controller;

import com.account.dto.request.UserCreationRequest;
import com.account.dto.request.UserUpdateRequest;
import com.account.dto.response.UserDetailResponse;
import com.account.dto.response.UserListResponse;
import com.account.exception.InvalidDataException;
import com.account.service.UserService;
import com.account.util.UserStatus;
import com.account.validator.ValueOfEnum;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AccountController {

    private final UserService userService;

    @Operation(summary = "Add new user", description = "Return user ID")
    @PostMapping(path = "/", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public long createUser(@Valid @RequestBody UserCreationRequest request) {
        return userService.addUser(request);
    }

    @Operation(summary = "Update user", description = "Return message")
    @PutMapping(path = "/", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    public void updateUser(@Valid @RequestBody UserUpdateRequest request) {
        try {
            userService.updateUser(request);
        } catch (Exception e) {
            throw new InvalidDataException("Update user unsuccessful, Please try again");
        }
    }

    @Operation(summary = "Change user status", description = "Return message")
    @PatchMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    public void changeStatus(@PathVariable long id,
                             @RequestParam @ValueOfEnum(message = "status must be any of enum (ACTIVE,INACTIVE,NONE)", enumClass = UserStatus.class) String status) {
        try {
            userService.changeStatus(id, status);
        } catch (Exception e) {
            throw new InvalidDataException("Change status unsuccessful, Please try again");
        }
    }

    @Operation(summary = "Delete user", description = "Return no content")
    @DeleteMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(NO_CONTENT)
    public void deleteUser(@PathVariable("id") @Min(1) long id) {
        try {
            userService.deleteUser(id);
        } catch (Exception e) {
            throw new InvalidDataException("Delete user unsuccessful, Please try again");
        }
    }

    @Operation(summary = "Get user detail", description = "Return user detail")
    @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public UserDetailResponse getUser(@PathVariable("id") @Min(1) int id) {
        return userService.getUser(id);
    }

    @Operation(summary = "Get user list has paged", description = "Return list of users")
    @GetMapping(path = "/list", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public List<UserDetailResponse> getUsers(Pageable pageable) {
        return userService.getUsers(pageable);
    }

    @Operation(summary = "Get user list has sorted and paged", description = "Return list of users")
    @GetMapping(path = "/list-sorted-paged", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public UserListResponse getUsers(@RequestParam(defaultValue = "0") int pageNo,
                                     @RequestParam(defaultValue = "20") int pageSize,
                                     @RequestParam(required = false) String... sort) {
        return userService.getUsers(pageNo, pageSize, sort);
    }

    @Operation(summary = "Search user with criteria", description = "Return list of users")
    @GetMapping(path = "/search-with-criteria", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public List<UserDetailResponse> searchWithCriteria(@RequestParam String... search) {
        return userService.searchWithCriteria(search);
    }

    @Operation(summary = "Search user with specifications", description = "Return list of users")
    @GetMapping(path = "/search-with-specifications", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public List<UserDetailResponse> searchWithSpecifications(@RequestParam String... search) {
        return userService.findAllBySpecification(search);
    }
}
