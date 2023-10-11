package com.sample.controller;

import com.sample.config.Translator;
import com.sample.controller.request.SampleUserRequest;
import com.sample.controller.response.FailureResponse;
import com.sample.controller.response.LoadingPageResponse;
import com.sample.controller.response.SuccessResponse;
import com.sample.dto.SampleDTO;
import com.sample.exception.DuplicatedException;
import com.sample.exception.NotAcceptableException;
import com.sample.exception.NotFoundException;
import com.sample.model.SampleUser;
import com.sample.service.SampleUserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.sample.utils.Constant.apiKey;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/samples")
@Slf4j(topic = "SAMPLE-CONTROLLER")
public record SampleController(SampleUserService userService) {

    @Operation(summary = "Welcome API", description = "Return message")
    @GetMapping(path = "/welcome", headers = apiKey)
    public String welcome() {
        return Translator.toLocale("message.welcome");
    }

    @Operation(summary = "Add new user", description = "Return user ID")
    @PostMapping(path = "/user/add", headers = apiKey)
    public SuccessResponse createSampleUser(@Validated @RequestBody SampleUserRequest request) {
        log.info("Request POST /user/add");

        try {
            userService.isPhoneValid(request.getPhone());
            userService.isEmailValid(request.getEmail());

            String id = userService.saveSampleUser(request);
            return new SuccessResponse(CREATED, Translator.toLocale("msg-user-add-success"), id);
        } catch (NotAcceptableException e) {
            log.error("Invalid parameter, message={}", e.getMessage(), e);
            return new FailureResponse(NOT_ACCEPTABLE, e.getMessage());
        } catch (DuplicatedException e) {
            log.error("Invalid parameter, message={}", e.getMessage(), e);
            return new FailureResponse(CONFLICT, e.getMessage());
        } catch (Exception e) {
            log.error("Add new user was failure, message={}", e.getMessage(), e);
            return new FailureResponse(BAD_REQUEST, Translator.toLocale("msg-user-add-failure"));
        }
    }

    @PutMapping(path = "/user/{hashKey}", headers = apiKey)
    public SuccessResponse updateSampleUser(@PathVariable String hashKey, @RequestBody SampleUserRequest request) {
        log.info("Request PUT /user/{}", hashKey);

        try {
            SampleUser sampleUser = userService.get(hashKey, request.getRangeKey());
            if (StringUtils.hasLength(request.getPhone()) && !sampleUser.getPhone().equals(request.getPhone())) {
                userService.isPhoneValid(request.getPhone());
            }

            if (StringUtils.hasLength(request.getEmail()) && !sampleUser.getEmail().equals(request.getEmail())) {
                userService.isEmailValid(request.getEmail());
            }

            userService.updateSampleUser(sampleUser, request);
            return new SuccessResponse(ACCEPTED, Translator.toLocale("msg-user-update-success"));
        } catch (DuplicatedException e) {
            log.error("Invalid parameter, message={}", e.getMessage(), e);
            return new FailureResponse(CONFLICT, e.getMessage());
        } catch (NotFoundException e) {
            log.error("Not found, message={}", e.getMessage(), e);
            return new FailureResponse(NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            log.error("Update user was failure, message={}", e.getMessage(), e);
            return new FailureResponse(BAD_REQUEST, Translator.toLocale("msg-user-update-failure"));
        }
    }

    @PatchMapping(path = "/user/{hashKey}", headers = apiKey)
    public SuccessResponse deactivateSampleUser(@PathVariable String hashKey, @RequestParam String rangeKey) {
        log.info("Request PATCH /user/{}?rangeKey={}", hashKey, rangeKey);

        try {
            userService.deactivateSampleUser(hashKey, rangeKey);
            return new SuccessResponse(NO_CONTENT, Translator.toLocale("msg-user-deactivate-success"));
        } catch (NotFoundException e) {
            log.error("Not found, message={}", e.getMessage(), e);
            return new FailureResponse(NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            log.error("Deactivate user was failure, message={}", e.getMessage(), e);
            return new FailureResponse(BAD_REQUEST, Translator.toLocale("msg-user-deactivate-failure"));
        }
    }

    @DeleteMapping(path = "/user/{hashKey}", headers = apiKey)
    public SuccessResponse deleteSampleUser(@PathVariable String hashKey, @RequestParam String rangeKey) {
        log.info("Request DELETE /user/{}?rangeKey={}", hashKey, rangeKey);

        try {
            userService.deleteSampleUser(hashKey, rangeKey);
            return new SuccessResponse(RESET_CONTENT, Translator.toLocale("msg-user-delete-success"));
        } catch (NotFoundException e) {
            log.error("Not found, message={}", e.getMessage(), e);
            return new FailureResponse(NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            log.error("Delete user was failure, message={}", e.getMessage(), e);
            return new FailureResponse(BAD_REQUEST, Translator.toLocale("msg-user-delete-failure"));
        }
    }

    @GetMapping(path = "/user/{hashKey}", headers = apiKey)
    public SuccessResponse getSampleUserDetail(@PathVariable String hashKey, @RequestParam String rangeKey) {
        log.info("Request GET /user/{}?rangeKey={}", hashKey, rangeKey);

        try {
            SampleUser sampleUser = userService.get(hashKey, rangeKey);
            SampleDTO response = SampleDTO.builder()
                    .id(sampleUser.getRangeKey())
                    .firstName(sampleUser.getFirstName())
                    .lastName(sampleUser.getLastName())
                    .phone(sampleUser.getPhone())
                    .email(sampleUser.getEmail())
                    .address(sampleUser.getAddress())
                    .createdAt(sampleUser.getCreatedAt().toString())
                    .updatedAt(sampleUser.getUpdatedAt().toString())
                    .build();
            return new SuccessResponse(OK, "user", response);
        } catch (NotFoundException e) {
            log.error("Not found, message={}", e.getMessage(), e);
            return new FailureResponse(NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            log.error("Get user was failure, message={}", e.getMessage(), e);
            return new FailureResponse(BAD_REQUEST, "Get user was failure");
        }
    }

    @GetMapping(path = "/users", headers = apiKey)
    public SuccessResponse getList(@RequestParam String hashKey,
                                   @RequestParam(required = false) String search,
                                   @RequestParam(required = false) String firstName,
                                   @RequestParam(required = false) String lastName,
                                   @RequestParam(required = false) String phone,
                                   @RequestParam(required = false) String email,
                                   @RequestParam(required = false) String address,
                                   @RequestParam(required = false) String status,
                                   @RequestParam(required = false) String orderBy,
                                   @RequestParam(required = false) String nextKey,
                                   @RequestParam(defaultValue = "25") int pageSize) {
        log.info("Request GET /user/{}?pageSize={}", hashKey, pageSize);

        try {
            LoadingPageResponse response = userService.getAll(hashKey, search, firstName, lastName, phone, email, address, status, orderBy, nextKey, pageSize);
            return new SuccessResponse(OK, "users", response);
        } catch (Exception e) {
            log.error("Get user list was failure, message={}", e.getMessage(), e);
            return new FailureResponse(BAD_REQUEST, "Get user list was failure");
        }
    }
}
