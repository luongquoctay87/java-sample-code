package com.authentication.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class AuthenticationController {

    @GetMapping("/welcome")
    public String welcome() {
        log.info("/authz/welcome");
        return "Welcome authentication service";
    }

}
