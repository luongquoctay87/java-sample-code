## Validate Phone by Customize Annotation

### 1. Define regular expression
```
// Ten-Digit Number: 2055550125
String TEN_DIGIT_NUMBER = "^\\d{10}$";

// To allow optional whitespace, dots, or hyphens (-) between the numbers:
// This pattern will allow numbers like 2055550125, 202 555 0125, 202.555.0125, and 202-555-0125.
String NUMBER_WITH_WHITESPACES = "^(\\d{3}[- .]?){2}\\d{4}$";

// Number With Parentheses
// To allow the optional parenthesis in the number, This expression will allow numbers like (202)5550125, (202) 555-0125 or (202)-555-0125.
// Additionally, this expression will also allow the phone numbers covered in the above example.
String NUMBER_WITH_PARENTHESES = "^((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$";

// To permit the prefix in our number, we have added to the beginning of our pattern the characters: (\\+\\d{1,3}( )?)?
// This expression will enable phone numbers to include international prefixes, taking into account that international prefixes are normally numbers with a maximum of three digits.
String NUMBER_WITH_INTERNATIONAL_PREFIX = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$";

// The expression used in the last section: ^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$
// Regular expression to allow numbers like +111 123 456 789: ^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$
// Pattern to allow numbers like +111 123 45 67 89: ^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$
// For example: 2055550125, 202 555 0125, (202) 555-0125, 111 (202) 555-0125, 636 856 789, +111 636 856 789, 636 85 67 89, +111 636 85 67 89
String NUMBER_MULTIPLE_REGEX = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
        + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
        + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$";
```

### 2. Define PhoneValidator to process compile regex

```
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

import static com.sample.utils.Constant.REGEX.NUMBER_MULTIPLE_REGEX;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {
    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        return Pattern.matches(NUMBER_MULTIPLE_REGEX, phone);
    }
}
```

### 3. Define an annotation in order to annotate to field or parameter

```
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
public @interface PhoneNumber {
    String message() default "Phone number is invalid format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```

### 3. Annotate to @RequestParam in order to validate phone before execute anything.

```
@Validated
@RestController
@RequestMapping("/validation")
public class ValidationController {

    @Operation(summary = "Validate phone number by customize annotation")
    @PostMapping("/phone-number")
    public String validatePhone(@PhoneNumber @RequestParam  String phone) {
        return "validated";
    }
}
```


### 4. Test

- Validated
```
curl -X 'POST' \
  'http://localhost:8181/validation/phone-number?phone=%2B111%20123%2045%2067%2089' \
  -H 'accept: */*' \
  -d ''
  
Result:
validated
```


- Phone number is invalid format
```
curl -X 'POST' \
  'http://localhost:8181/validation/phone-number?phone=%2B111%20123%2045%2067%208' \
  -H 'accept: */*' \
  -d ''
 
Result:  
{
    "timestamp": "2023-11-04T05:07:33.828+0000",
    "status": 400,
    "path": "/validation/phone-number",
    "error": "Parameter is invalid",
    "messages": "Phone number is invalid format"
}
```



