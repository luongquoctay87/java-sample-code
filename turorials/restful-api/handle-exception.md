# Handle Exception In Spring Boot
Handling exceptions is an important part of building a robust application. Spring Boot offers more than one way of doing it.


### How To Implement Handle Exception ?
__1. Step 1 - Create a new customize exception__
```
public class DuplicatedException extends RuntimeException {
    public DuplicatedException(String message) {
        super(message);
    }
}
```

__2. Create new controller advice__
```
@RestControllerAdvice
public class GlobalExceptionHandler {
    ...

    @ResponseStatus(CONFLICT)
    @ExceptionHandler(DuplicatedException.class)
    public Error handleDuplicatedException(DuplicatedException e, WebRequest request) {
        Error error = new Error();
        error.setTimestamp(new Date());
        error.setPath(request.getDescription(false).replace("uri=", ""));
        error.setStatus(CONFLICT.value());
        error.setError(CONFLICT.getReasonPhrase());
        error.setMessages(e.getMessage());

        return error;
    }  

    ...

    @Data
    private static class Error {
        private Date timestamp;
        private int status;
        private String path;
        private String error;
        private String messages;
    }
    ...
}

```

__3. Throw DuplicatedException at SampleUserService__
```
@Service
public record SampleUserService(SampleUserRepository repository) {
    ...

    public void isPhoneValid(String phone) {
        SampleUser object = repository.findByPhone(phone);
        if (object != null) {
            throw new DuplicatedException(Translator.toLocale("msg-user-phone-invalid"));
        }
    }
    ...
}
```

__4. Catch DuplicatedException at SampleController and response user__
```
    @PostMapping(path = "/user/add", headers = apiKey)
    public SuccessResponse createSampleUser(@RequestBody SampleUserRequest request) {
        try {
            userService.isPhoneValid(request.getPhone());
            userService.isEmailValid(request.getEmail());

            String id = userService.saveSampleUser(request);
            return new SuccessResponse(CREATED, Translator.toLocale("msg-user-add-success"), id);
        } catch (DuplicatedException e) {
            log.error("Invalid parameter, message={}", e.getMessage(), e);
            return new FailureResponse(CONFLICT, e.getMessage());
        } catch (Exception e) {
            log.error("Add new user was failure, message={}", e.getMessage(), e);
            return new FailureResponse(BAD_REQUEST, Translator.toLocale("msg-user-add-failure"));
        }
    }
```


---
***Source code:*** [GitHub](https://github.com/luongquoctay87/java-sample-code/tree/working-with-rest-api)

***Reference sources:***
 - [Error Handling for REST with Spring](https://www.baeldung.com/exception-handling-for-rest-with-spring)
 - [Complete Guide to Exception Handling in Spring Boot](https://reflectoring.io/spring-boot-exception-handling/)
