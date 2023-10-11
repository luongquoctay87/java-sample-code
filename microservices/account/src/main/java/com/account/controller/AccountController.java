package com.account.controller;

import com.account.dto.request.UserCreationRequest;
import com.account.dto.request.UserUpdateRequest;
import com.account.dto.response.UserResponse;
import com.account.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/user")
@Slf4j(topic = "ACCOUNT-CONTROLLER")
@Validated
public class AccountController {

    @Autowired
    private UserService userService;

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
        userService.updateUser(request);
    }

    @Operation(summary = "Delete user", description = "Return no content")
    @DeleteMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(NO_CONTENT)
    public void deleteUser(@PathVariable("id") @Min(1) int id) {
        userService.deleteUser(id);
    }

    @Operation(summary = "Get user detail", description = "Return user detail")
    @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public UserResponse getUser(@PathVariable("id") @Min(1) int id) {
        return userService.getUser(id);
    }

    @Operation(summary = "Get user list", description = "Return list of users")
    @GetMapping(path = "/", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public List<UserResponse> getUsers(@RequestParam(required = false) String name) {
        return userService.getUsers();
    }
}
