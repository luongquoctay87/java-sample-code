## Validate input data

#### 1. Add dependency
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

#### 2. Create request form

- Add I18N message keys to application.properties
```
# Validation's messages
msg-validate-firstName=firstName must be not blank
msg-validate-lastName=lastName must be not blank
msg-validate-phone=phone must be not blank
msg-validate-email=email must be not blank
msg-validate-password=password must be not blank
msg-validate-address=address must be not blank
```

- Create file `SampleUserRequest`
```
@Data
public class SampleUserRequest implements Serializable {
    private String rangeKey;

    @NotBlank(message = "{msg-validate-firstName}")
    private String firstName;

    @NotBlank(message = "{msg-validate-lastName}")
    private String lastName;

    @NotBlank(message = "{msg-validate-phone}")
    private String phone;

    @NotBlank(message = "{msg-validate-email}")
    private String email;

    @NotBlank(message = "{msg-validate-password}")
    private String password;

    @NotNull(message = "{msg-validate-address}")
    private Address address;
}
```

- Implement validation to controller with anotation `@Validated` before request form `SampleUserRequest`
```
@Operation(summary = "Add new user", description = "Return user ID")
@PostMapping(path = "/user/add", headers = apiKey)
public SuccessResponse createSampleUser(@Validated @RequestBody SampleUserRequest request) {
    log.info("Request POST /user/add");

    try {
        userService.isPhoneValid(request.getPhone());
        userService.isEmailValid(request.getEmail());

        String id = userService.saveSampleUser(request);
        return new SuccessResponse(CREATED, Translator.toLocale("msg-user-add-success"), id);
    } catch (Exception e) {
        log.error("Add new user was failure, message={}", e.getMessage(), e);
        return new FailureResponse(BAD_REQUEST, Translator.toLocale("msg-user-add-failure"));
    }
}
```

---
***Reference sources:***
 - [How to validate input in Spring RestController](https://levelup.gitconnected.com/how-to-validate-input-in-spring-restcontroller-ecf471443595)
 - [Validation with Spring Boot - the Complete Guide](https://reflectoring.io/bean-validation-with-spring-boot/)