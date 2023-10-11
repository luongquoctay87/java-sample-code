package com.authentication.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome authentication service";
    }

}
