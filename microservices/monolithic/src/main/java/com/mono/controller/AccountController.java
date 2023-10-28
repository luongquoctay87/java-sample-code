package com.mono.controller;

import com.mono.dto.request.UserCreationRequest;
import com.mono.dto.request.UserUpdateRequest;
import com.mono.dto.response.UserDetailResponse;
import com.mono.dto.response.UserListResponse;
import com.mono.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/accounts")
@Validated @Slf4j @Tag(name = "Account Controller")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Operation(summary = "Add new user", description = "Return user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserCreationRequest.class),
                            examples = @ExampleObject(name = "Return user ID", summary = "Added successfully", value = "1")
                    )})
    })
    @PostMapping(path = "/user", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public long createUser(@Valid @RequestBody UserCreationRequest request) {
        log.info("Request POST /user");
        return accountService.addUser(request);
    }

    @Operation(summary = "Update user", description = "Return no content")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Accepted",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "No Content", summary = "Updated successfully")
                    )})
    })
    @PutMapping(path = "/user", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    public void updateUser(@Valid @RequestBody UserUpdateRequest request) {
        log.info("Request PUT /user");
        accountService.updateUser(request);
    }

    @Operation(summary = "Disable user", description = "Return no content")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Accepted",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "No Content", summary = "Changed successfully")
                    )})
    })
    @PatchMapping(path = "/user/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    public void permitUser(@PathVariable("id") @Min(1) long id, @RequestParam boolean status) {
        log.info("Request DELETE /user/{}", id);
        accountService.permitUser(id, status);
    }

    @Operation(summary = "Delete user", description = "Return no content")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "No Content", summary = "Deleted successfully")
                    )})
    })
    @DeleteMapping(path = "/user/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(NO_CONTENT)
    public void deleteUser(@PathVariable("id") @Min(1) long id) {
        log.info("Request DELETE /user/{}", id);
        accountService.deleteUser(id);
    }

    @Operation(summary = "Get user", description = "Return user detail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = { @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDetailResponse.class),
                            examples = @ExampleObject(name = "Return user detail",
                                    summary = "Retrieved successfully",
                                    value = "{\n" +
                                            "  \"id\": 1,\n" +
                                            "  \"email\": \"user@email.com\",\n" +
                                            "  \"username\": \"user\"\n" +
                                            "}")
                            ) })
    })
    @GetMapping(path = "/user/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public UserDetailResponse getUser(@PathVariable("id") @Min(1) long id) {
        log.info("Request GET /user/{}", id);
        return accountService.getUser(id);
    }

    @Operation(summary = "Get user list", description = "Return list of users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = { @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserListResponse.class),
                            examples = @ExampleObject(name = "Return list of users",
                                    summary = "Retrieved successfully",
                                    value = "{\n" +
                                            "  \"pageNo\": 1,\n" +
                                            "  \"pageSize\": 2,\n" +
                                            "  \"total\": 5,\n" +
                                            "  \"data\": [\n" +
                                            "    {\n" +
                                            "      \"id\": 2,\n" +
                                            "      \"email\": \"user@email.com\",\n" +
                                            "      \"username\": \"user\"\n" +
                                            "    },\n" +
                                            "    {\n" +
                                            "      \"id\": 3,\n" +
                                            "      \"email\": \"other@email.com\",\n" +
                                            "      \"username\": \"other\"\n" +
                                            "    }\n" +
                                            "  ]\n" +
                                            "}")
                    ) })
    })
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
