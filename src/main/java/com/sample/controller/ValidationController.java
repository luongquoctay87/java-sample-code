package com.sample.controller;

import com.sample.controller.request.ValidateRequest;
import com.sample.utils.UserStatus;
import com.sample.validator.ValueOfEnum;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Validated
@RestController
@RequestMapping("/validation")
public class ValidationController {

    @PostMapping("/enum-in-request-body")
    public String validateInBody(@Valid @RequestBody ValidateRequest request) {
        return "validated";
    }

    @PostMapping("/enum-in-request-parameter")
    public String validateInParameter(@RequestParam @ValueOfEnum(message = "status must be any of enum (ACTIVE,INACTIVE,NONE)", enumClass = UserStatus.class) String status) {
        return status;
    }
}
