## Validate Phone By Regex

```
/**
* Check phone exists or not
*
* @param phone
*/
public void isPhoneValid(String phone) {
    log.info("Validating phone number={}", phone);

    String PHONE = "^(\\+\\d{1,2}\\s?)?1?\\-?\\.?\\s?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$";

    if (!phone.matches(PHONE)) throw new InvalidDataException("Phone number invalid format");

    SampleUser object = repository.findByPhone(phone);
    if (object != null) {
        log.warn("Phone number={} exists", phone);
        throw new InvalidDataException(Translator.toLocale("msg-user-phone-invalid"));
    }
}
```
