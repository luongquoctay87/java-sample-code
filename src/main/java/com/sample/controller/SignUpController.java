package com.sample.controller;

import com.sample.common.SNSClient;
import com.sample.controller.request.MobileSignUpRequest;
import com.sample.service.SampleUserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/signUp")
public record SignUpController(SampleUserService userService, SNSClient snsClient) {

    @Operation(summary = "Sign up from mobile", description = "Return user ID and push notify")
    @PostMapping("/mobile")
    public String mobileSignUp(@Validated @RequestBody MobileSignUpRequest request) {
        String result = userService.saveSampleUser(request);

        String platformArn = "arn:aws:sns:eu-west-1:xxxx"; // Get ARN from amazon SNS platform
        String message = "Welcome to SNS";
        snsClient.sendToSNS(platformArn, request.getDeviceToken(), message);

        return result;
    }

}
