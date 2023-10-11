# I18N RESTful API
Have you ever faced with challenging task of providing service to your users in their native language? If so — then you may be interested in finding a good approach of doing this.

### How To Implement I18N for APIs ?

__1. Step 1 - Create file LocaleResolver__
```
@Configuration
public class LocaleResolver
        extends AcceptHeaderLocaleResolver
        implements WebMvcConfigurer {

    List<Locale> LOCALES = Arrays.asList(
            new Locale("en"),
            new Locale("fr"),
            new Locale("it"),
            new Locale("es"));

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String headerLang = request.getHeader("Accept-Language");
        return headerLang == null || headerLang.isEmpty()
                ? Locale.getDefault()
                : Locale.lookup(Locale.LanguageRange.parse(headerLang), LOCALES);
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource rs = new ResourceBundleMessageSource();
        rs.setBasename("messages");
        rs.setDefaultEncoding("UTF-8");
        rs.setUseCodeAsDefaultMessage(true);
        return rs;
    }
}
```

__2. Step 2 - Create files Translator__
```
@Component
public class Translator {

    private static ResourceBundleMessageSource messageSource;

    @Autowired
    Translator(ResourceBundleMessageSource messageSource) {
        Translator.messageSource = messageSource;
    }

    public static String toLocale(String msgCode) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(msgCode, null, locale);
    }
}
```

__3. Step 3 - Create files .properties__
- messages.properties
```
message.welcome=Welcome to this guide!
```
- messages-fr.properties
```
message.welcome=Bienvenue dans ce guide!
```
- messages-it.properties
```
message.welcome=Benvenuti in questa guida!
```
- messages-es.properties
```
message.welcome=¡Bienvenido a esta guía!
```

__4. Step 4 - Implement in APIs__

```
@RestController
@RequestMapping("/samples")
@Slf4j(topic = "SAMPLE-CONTROLLER")
public record SampleController(SampleUserService userService) {

    @GetMapping(path = "/welcome", headers = apiKey)
    public String welcome() {
        return Translator.toLocale("message.welcome");
    }
    ...
}
```

__5. Step 5 - Request API for Testing__
```
curl --location 'http://localhost:8181/samples/welcome' \
--header 'Accept-Language: es-ES' \
--header 'apiKey: sample'
```

---
***Reference sources:***
 - [Internationalization with Spring Boot REST](https://howtodoinjava.com/spring-boot2/rest/i18n-internationalization/)
 - [Spring Boot REST Internationalization](https://ihorkosandiak.medium.com/spring-boot-rest-internationalization-9ab3fce2489)