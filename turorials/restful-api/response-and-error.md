## Response Data 

#### 1.1 Create SuccessResponse template
```
public class SuccessResponse extends ResponseEntity<SuccessResponse.Payload> {

    /**
     * Create a new {@code ApiResponse} with the given code,message,http status.
     *
     * @param status  status code
     * @param message status code message
     */
    public SuccessResponse(HttpStatus status, String message) {
        super(new Payload(status.value(), message), HttpStatus.OK);
    }

    /**
     * Create a new {@code ApiResponse} with the given code,message,data,http status.
     *
     * @param status  status code
     * @param message status code message
     * @param data    data response
     */
    public SuccessResponse(HttpStatus status, String message, Object data) {
        super(new Payload(status.value(), message, data), HttpStatus.OK);
    }

    @Data
    @AllArgsConstructor
    public static class Payload {
        private Integer status;
        private String message;
        private Object data;

        public Payload(Integer status, String message) {
            this.status = status;
            this.message = message;
        }
    }
}
```

#### 1.2 Create FailureResponse template
```
public class FailureResponse extends SuccessResponse {
    /**
     * Failure response for api
     *
     * @param status
     * @param message
     */
    public FailureResponse(HttpStatus status, String message) {
        super(status, message);
    }
}
```

### 2 Implement response to controller
```
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