## Validate Enum by Customize Annotation

### 1. Validating That a String Matches a Value of an Enum

> Instead of validating an enum to match a String, we could also do the opposite. For this, we can create an annotation
> that checks if the String is valid for a specific enum.

#### 1.1  Create an annotation can be added to a String field and we can pass any enum class.

```
    import javax.validation.Constraint;
    import javax.validation.Payload;
    import java.lang.annotation.Documented;
    import java.lang.annotation.Retention;
    import java.lang.annotation.Target;
    
    import static java.lang.annotation.ElementType.*;
    import static java.lang.annotation.RetentionPolicy.RUNTIME;
    
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @Constraint(validatedBy = ValueOfEnumValidator.class)
    public @interface ValueOfEnum {
        String name() default "";
        String regexp() default "";
        String message() default "{name} must be any of enum {regexp}";
        Class<? extends Enum<?>> enumClass();
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }
```

#### 1.2 Define the ValueOfEnumValidator to check whether the String (or any CharSequence) is contained in the enum:

```
    import javax.validation.ConstraintValidator;
    import javax.validation.ConstraintValidatorContext;
    import java.util.List;
    import java.util.stream.Stream;
    
    public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, CharSequence> {
        private List<String> acceptedValues;
    
        @Override
        public void initialize(ValueOfEnum annotation) {
            acceptedValues = Stream.of(annotation.enumClass().getEnumConstants())
                    .map(Enum::name)
                    .toList();
        }
    
        @Override
        public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
            if (value == null) {
                return true;
            }
    
            return acceptedValues.contains(value.toString().toUpperCase());
        }
    }
```

#### 1.3 Define enums UserRole, UserStatus

- UserRole.java

```
    import com.fasterxml.jackson.annotation.JsonProperty;
    import lombok.Getter;
    
    @Getter
    public enum UserRole {
        @JsonProperty("sysadmin")
        SYSADMIN("sysadmin"),
    
        @JsonProperty("admin")
        ADMIN("admin"),
    
        @JsonProperty("manager")
        MANAGER("manager"),
    
        @JsonProperty("user")
        USER("user");
    
        private final String name;
    
        UserRole(String name) {
            this.name = name;
        }
    }
```

- UserStatus.java

```
    import com.fasterxml.jackson.annotation.JsonProperty;
    import lombok.Getter;
    
    @Getter
    public enum UserStatus {
        @JsonProperty("active")
        ACTIVE("active"),
    
        @JsonProperty("inactive")
        INACTIVE("inactive"),
    
        @JsonProperty("none")
        NONE("none");
    
        private final String name;
    
        UserStatus(String name) {
            this.name = name;
        }
    }
```

### 1.4 Define @RestControllerAdvice GlobalExceptionHandler to handle exception

```
    @RestControllerAdvice
    public class GlobalExceptionHandler {
        
        /**
        * Handle request data
        *
        * @param e
        * @param request
        * @return error
        */
        @ExceptionHandler({ConstraintViolationException.class,
                MissingServletRequestParameterException.class, MethodArgumentNotValidException.class})
        @ResponseStatus(BAD_REQUEST)
        public Error handleValidationException(Exception e, WebRequest request) {
            Error error = new Error();
            error.setTimestamp(new Date());
            error.setStatus(BAD_REQUEST.value());
            error.setPath(request.getDescription(false).replace("uri=", ""));
    
            String message = e.getMessage();
            if (e instanceof MethodArgumentNotValidException) {
                int start = message.lastIndexOf("[");
                int end = message.lastIndexOf("]");
                message = message.substring(start + 1, end - 1);
                error.setError("Body is invalid");
                error.setMessages(message);
            } else if (e instanceof MissingServletRequestParameterException) {
                error.setError("Param is invalid");
                error.setMessages(message);
            } else if (e instanceof ConstraintViolationException) {
                error.setError("Param is invalid");
                error.setMessages(message.substring(message.indexOf(" ") + 1));
            } else {
                error.setError("Data is invalid");
                error.setMessages(message);
            }
    
            return error;
        }
    }
```

### 1.5 Define file ValidateRequest.java

```
    import com.sample.utils.UserRole;
    import com.sample.validator.ValueOfEnum;
    import lombok.Getter;
    import lombok.Setter;
    
    import javax.validation.constraints.NotBlank;
    import java.io.Serializable;
    
    @Getter
    @Setter
    public class ValidateRequest implements Serializable {
        @NotBlank(message = "firstName must be not null")
        private String firstName;
    
        @NotBlank(message = "lastName must be not null")
        private String lastName;
    
        @ValueOfEnum(name = "role", regexp = "(SYSADMIN | ADMIN | MANAGER | USER)", enumClass = UserRole.class)
        private String role;
    }
```


### 1.6 Create ValidationController

```
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
```

### 2. Test

#### 2.1 Test enum in request body

- Request:

    ```
    curl --location 'http://localhost:8181/validation/enum-in-request-body' \
    --header 'accept: */*' \
    --header 'Content-Type: application/json' \
    --data '{
        "firstName": "string",
        "lastName": "string",
        "role": "ADMIN-test"
    }'
    ```

- Response:

    ```
    {
        "timestamp": "2023-11-03T09:08:13.310+0000",
        "status": 400,
        "path": "/validation/enum-in-request-body",
        "error": "Body is invalid",
        "messages": "role must be any of enum (SYSADMIN | ADMIN | MANAGER | USER)"
    }
    ```

#### 2.2 Test enum in request parameter

- Request:

    ```
    curl --location --request POST 'http://localhost:8181/validation/enum-in-request-parameter?status=actived' \
    --header 'accept: */*' \
    --header 'Content-Type: application/json'
    ```

- Response:

    ```
    {
        "timestamp": "2023-11-03T09:08:31.841+0000",
        "status": 400,
        "path": "/validation/enum-in-request-parameter",
        "error": "Param is invalid",
        "messages": "status must be any of enum (ACTIVE,INACTIVE,NONE)"
    }
    ```


### Reference source:
- [Validations for Enum Types](https://www.baeldung.com/javax-validations-enums)







