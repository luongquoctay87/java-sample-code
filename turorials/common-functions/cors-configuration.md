## CORS CONFIGURATION

### 1. CORS for single application
- As an alternative to other methods presented above, Spring Framework also provides a CorsFilter. 
In that case, instead of using @CrossOrigin or WebMvcConfigurer# addCorsMappings(CorsRegistry), 
you can for example declare the filter as following in your Spring Boot application
```
@Configuration
public class AppConfig {
    ...
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:8500");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }
}
```

- Enabling CORS for the whole application is as simple as:
```
@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }
}
```

- If you are using Spring Boot, it is recommended to just declare a WebMvcConfigurer bean as following:
```
@Configuration
public class AppConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**");
            }
        };
    }
}
```

- You can easily change any properties, as well as only apply this CORS configuration to a specific path pattern:
```
@Configuration
@EnableWebMvc
public class AppConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**") 
			.allowedOrigins("http://localhost:8500") 
			.allowedMethods("GET", "POST", "PUT", "DELETE") // Allowed HTTP methods
			.allowedHeaders("*") // Allowed request headers
			.allowCredentials(false) 
			.maxAge(3600); 
	}
    ...
}
```

- If you are using Spring Security, make sure to enable CORS at Spring Security level as well to allow it to leverage the configuration defined at Spring MVC level.
```
@EnableWebSecurity
public class AppConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and()...
	}
}
```

- Enabling CORS on Request Filter
```
@Component
@Slf4j(topic = "APP-CONFIG")
public class AppConfig extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!response.containsHeader("Access-Control-Allow-Origin")) {
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:8500");
        }
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.addHeader("Access-Control-Expose-Headers", "xsrf-token");

        StringBuffer url = new StringBuffer(request.getRequestURL());

        if (StringUtils.hasLength(request.getQueryString())) {
            url.append("?");
            url.append(request.getQueryString());
        }

        if (url.toString().contains("/accounts")) {
            log.info("Request {} {}", request.getMethod(), url);
        }

        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
```

### 2. CORS for cloud gateway
- Enabling CORS in application.yml
```
spring:
  cloud:
    gateway:
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins: "http://localhost:8500"
            allowedHeaders: "*"
            allowedMethods:
              - GET
              - POST
              - PUT
              - PATCH
              - DELETE
              - OPTIONS
               
```



- ***Reference source:***
  - [CORS support in Spring Framework](https://spring.io/blog/2015/06/08/cors-support-in-spring-framework)
  - [CORS Configuration](https://cloud.spring.io/spring-cloud-gateway/multi/multi__cors_configuration.html)
  - [Spring Boot CORS Configuration Examples](https://howtodoinjava.com/spring-boot2/spring-cors-configuration/)














