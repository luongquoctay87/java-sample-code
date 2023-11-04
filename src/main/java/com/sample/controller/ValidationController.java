package com.sample.controller;

import com.sample.controller.request.ValidateRequest;
import com.sample.utils.UserStatus;
import com.sample.validator.PhoneNumber;
import com.sample.validator.ValueOfEnum;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Validated
@RestController
@RequestMapping("/validation")
public class ValidationController {

    @Operation(summary = "Validate enum on request body")
    @PostMapping("/enum-in-request-body")
    public String validateInBody(@Valid @RequestBody ValidateRequest request) {
        return "validated";
    }

    @Operation(summary = "Validate enum on request parameter")
    @PostMapping("/enum-in-request-parameter")
    public String validateInParameter(@RequestParam @ValueOfEnum(message = "status must be any of enum (ACTIVE,INACTIVE,NONE)", enumClass = UserStatus.class) String status) {
        return status;
    }

    @Operation(summary = "Validate phone number by customize annotation")
    @PostMapping("/phone-number")
    public String validatePhone(@PhoneNumber @RequestParam String phone) {
        return "validated";
    }
}
