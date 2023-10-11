# Backend API Standard
## I. Software Security
- Only use HTTPS
- All requests throughtout only one gateway
- With sensitive data, you need to encrypt it
- Perform decentralization for each user
- Password Mechanism: The system will not store the user’s password in plain-text, it will use password hashing in order to increase the security level of the system. Hash algorithms are one way functions.

## II. API Standard
### 1. Objective
The purpose of this document is to present the standards we use in order to work with APIs in the backend department. These standards are simply suggestions and guidelines, and depending on the case, they can be adapted if necessary.

### 2. Naming Conventions
To keep consistency between all our projects we define a convention for different cases that we consider important. Most of the decisions were made to respect the HTTP and database standards, or to be consistent between technologies in our stack.

### 2.1 Input and output API parameters
With the purpose of unifying the interfaces between techs and making things easier to the client who may consume any of our APIs, we decided to keep input and output API parameters in camelCase.
```
{
    "firstName": "John",
    "lastName": "Doe",
    "phone": "0123-456-123",
    "email": "johndoe@email.com",
    "address": {
        "street": "Ciputra",
        "district": "North Tu Liem",
        "city": "Hanoi",
        "country": "Vietnam",
        "postalCode": "100000",
        "text": "S2-15A08"
    }
}
```

### 2.2 Output parameters structure
- Add new user
```
{
    "status": 201,
    "message": "Add user successful",
    "data": "31e66d1c-08ec-4544-b53a-6e3ba54c3e03"
}
```

- Update user
```
{
    "status": 202,
    "message": "Update user successful",
    "data": null
}
```

- Deactivate user
```
{
    "status": 204,
    "message": "Deactivate user successful",
    "data": null
}
```

- Delete user
```
{
    "status": 205,
    "message": "Delete user successful",
    "data": null
}
```

- Get user detail
```
{
    "status": 200,
    "message": "user",
    "data": {
        "id": "31e66d1c-08ec-4544-b53a-6e3ba54c3e03",
        "firstName": "John",
        "lastName": "Doe123",
        "phone": "0123-456-123",
        "email": "johndoe123@email.com",
        "address": {
            "street": "Ciputra",
            "district": "North Tu Liem",
            "city": "Hanoi",
            "country": "Vietnam",
            "postalCode": "100000",
            "text": "S2-15A08"
        }
    }
}
```

- Get list
```
{
    "status": 200,
    "message": "users",
    "data": {
        "nextKey": "",
        "items": [
            {
                "id": "0f144b7c-a934-4dce-a2c3-72ed7f3fdc73",
                "firstName": "John",
                "lastName": "Doe7",
                "phone": "0123-456-777",
                "email": "johndoe7@email.com",
                "address": {
                    "street": "Ciputra",
                    "district": "North Tu Liem",
                    "city": "Hanoi",
                    "country": "Vietnam",
                    "postalCode": "100000",
                    "text": "S2-15A08"
                }
            },
            {
                ...
            }
        ]
    }
}
```

### 2.3 Error forwarding
When informing a client of a request error, the response body should be returned using the following structure:
- 400 - BAD_REQUEST: Response status code indicates that the server cannot or will not process the request due to something that is perceived to be a client error
- 401 - UNAUTHORIZED: Response status code indicates that the client request has not been completed because it lacks valid authentication credentials for the requested resource.
- 403 - FORBIDDEN:  Response status code indicates that the server understands the request but refuses to authorize it.
- 404 - NOT_FOUND: Response status code indicates that the server cannot find the requested resource.
- 406 - NOT_ACCEPTABLE: Client error response code indicates that the server cannot produce a response matching the list of acceptable values defined in the request's proactive content negotiation headers, and that the server is unwilling to supply a default representation.
- 409 - CONFLICT: The HTTP 409 Conflict response status code indicates a request conflict with the current state of the target resource.
- 500 - INTERNAL_SERVER_ERROR: Server error response code indicates that the server encountered an unexpected condition that prevented it from fulfilling the request.
- 502 - BAD_GATEWAY: Server error response code indicates that the server, while acting as a gateway or proxy, received an invalid response from the upstream server.
- 503 - SERVICE_UNAVAILABLE: Server error response code indicates that the server is not ready to handle the request.
- 504 - GATEWAY_TIMEOUT: Server error response code indicates that the server, while acting as a gateway or proxy, did not get a response in time from the upstream server that it needed in order to complete the request.

- ***Reference source:*** [HTTP response status codes](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status)

### 3. API Versioning
We decided to implement API versioning via the use of headers. It is worth mentioning that keeping multiple versions alive can become increasingly difficult to maintain, so it should be avoided if possible.
```
@GetMapping(path = "/welcome", headers = "apiKey=sample")
    public String welcome() {
        return Translator.toLocale("message.welcome");
    }
```

### 4. Response Status Codes
- 200 - OK: Success status response code indicates that the request has succeeded. A 200 response is cacheable by default.
- 201 - CREATED: Success status response code indicates that the request has succeeded and has led to the creation of a resource.
- 202 - ACCEPTED: Response status code indicates that the request has been accepted for processing, but the processing has not been completed; in fact, processing may not have started yet. The request might or might not eventually be acted upon, as it might be disallowed when processing actually takes place.
- 204 - NO_CONTENT: Success status response code indicates that a request has succeeded, but that the client doesn't need to navigate away from its current page.
- 205 - RESET_CONTENT: Response status tells the client to reset the document view, so for example to clear the content of a form, reset a canvas state, or to refresh the UI.
- 400 - BAD_REQUEST: Response status code indicates that the server cannot or will not process the request due to something that is perceived to be a client error
- 401 - UNAUTHORIZED: Response status code indicates that the client request has not been completed because it lacks valid authentication credentials for the requested resource.
- 403 - FORBIDDEN:  Response status code indicates that the server understands the request but refuses to authorize it.
- 404 - NOT_FOUND: Response status code indicates that the server cannot find the requested resource.
- 406 - NOT_ACCEPTABLE: Client error response code indicates that the server cannot produce a response matching the list of acceptable values defined in the request's proactive content negotiation headers, and that the server is unwilling to supply a default representation.
- 409 - CONFLICT: The HTTP 409 Conflict response status code indicates a request conflict with the current state of the target resource.
- 500 - INTERNAL_SERVER_ERROR: Server error response code indicates that the server encountered an unexpected condition that prevented it from fulfilling the request.
- 502 - BAD_GATEWAY: Server error response code indicates that the server, while acting as a gateway or proxy, received an invalid response from the upstream server.
- 503 - SERVICE_UNAVAILABLE: Server error response code indicates that the server is not ready to handle the request.
- 504 - GATEWAY_TIMEOUT: Server error response code indicates that the server, while acting as a gateway or proxy, did not get a response in time from the upstream server that it needed in order to complete the request.


### 5. API Documentation
There are lots of different tools in the market to document APIs. We decided to use Openapi 3 (A.K.A Swagger) as a standard documentation tool in all of our techs.											
The reasons behind this decision were some of the advantages OpenApi prodvides:										
- Quick to use: The properties that you need to fill in every endpoint are always the same and there are only a few. You can also frequently reuse components and the generic information is simple to provide.										
- Wide range of available features: From describing parameters, making authentication and authorization schemes, to even using the generated documentation of an endpoint to mock test the endpoint in question.										
- Intuitive and easy to use interactive UI: Swagger uses a GUI to show the user how to make requests to the API and what responses will look like.										
- Maintainable and easily extendable: Swagger allows using constants files from your API.										
-  Scan and auto-generate: For typed languages, Swagger can scan and auto-generate documentation.		

### 6. Routes
- GET api/v1/sample
- GET api/v1/sample/{id}
- GET api/v1/sample/{id}/clients
- GET api/v1/sample/{id}/clients?clientId=1
- POST api/v1/sample
- PUT api/v1/sample/{id}
- PATCH api/v1/sample/{id}
---


## III. Sample RESTful API
### 1. Response template
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


### 2. RESTful API template
__Pre-Requisites__
- Add dependency: `aws-java-sdk-dynamodb`
```
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-java-sdk-dynamodb</artifactId>
    <version>1.12.429</version>
</dependency>
```

- Connect to DynamoDB
```
@Configuration
public class DynamoDBConfig {

    @Value("${amazon.dynamodb.endpoint}")
    private String amazonDynamoDBEndpoint;

    @Value("${amazon.dynamodb.region}")
    private String amazonDynamoDBRegion;

    @Value("${amazon.dynamodb.accessKey}")
    private String amazonAWSAccessKey;

    @Value("${amazon.dynamodb.secretKey}")
    private String amazonAWSSecretKey;

    @Value("${spring.profiles.active:Unknown}")
    private String activeProfile;

    @Bean
    public DynamoDBMapper dynamoDBMapper() {
        return new DynamoDBMapper(buildAmazonDB(), dynamoDBMapperConfig());
    }

    private AmazonDynamoDB buildAmazonDB() {
        return AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(amazonDynamoDBEndpoint, amazonDynamoDBRegion))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey))).build();
    }

    @Bean
    DynamoDBMapperConfig dynamoDBMapperConfig() {
        String prefix = "";
        if (activeProfile.equals("prod")) {
            prefix = "PROD_";
        }
        return new DynamoDBMapperConfig.Builder()
                .withTableNameOverride(DynamoDBMapperConfig.TableNameOverride.withTableNamePrefix(prefix))
                .build();
    }
}
```


__2.1. Create model__
- Create DynamoDBTable
```
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "sample_user")
public class SampleUser {

    @DynamoDBHashKey
    @DynamoDBIndexHashKey(globalSecondaryIndexNames = {FIRST_NAME})
    private String hashKey;
    @DynamoDBRangeKey
    @DynamoDBAutoGeneratedKey
    private String rangeKey;

    @DynamoDBAttribute
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = FIRST_NAME)
    private String firstName;

    @DynamoDBAttribute
    private String lastName;

    @DynamoDBAttribute
    @DynamoDBIndexHashKey(globalSecondaryIndexName = PHONE)
    private String phone;

    @DynamoDBAttribute
    @DynamoDBIndexHashKey(globalSecondaryIndexName = EMAIL)
    private String email;

    @DynamoDBAttribute
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.M)
    private Address address;

    @DynamoDBAttribute
    @DynamoDBTypeConvertedEnum
    private UserStatus status;

    @DynamoDBAttribute
    private String searchKeys;

    @DynamoDBAttribute
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.BOOL)
    private Boolean isFirstLogin;

    @DynamoDBAttribute
    @DynamoDBAutoGeneratedTimestamp(strategy = DynamoDBAutoGenerateStrategy.CREATE)
    private Date createdAt;

    @DynamoDBAttribute
    @DynamoDBAutoGeneratedTimestamp(strategy = DynamoDBAutoGenerateStrategy.ALWAYS)
    private Date updatedAt;
}
```

- Create DynamoDBDocument
```
@Data
@DynamoDBDocument
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {
    @DynamoDBAttribute
    private String street;

    @DynamoDBAttribute
    private String district;

    @DynamoDBAttribute
    private String city;

    @DynamoDBAttribute
    private String country;

    @DynamoDBAttribute
    private String postalCode;

    @DynamoDBAttribute
    private String text;
}
```

- Create UserStatus
```
@Getter
public enum UserStatus {
    NONE("none"),
    ACTIVE("active"),
    INACTIVE("inactive");

    private String name;

    UserStatus(String name) {
        this.name = name;
    }
}
```

- Create Constant
```
public interface Constant {

    ...

    interface INDEX {
        String FIRST_NAME = "firstName-index";
        String PHONE = "phone-index";
        String EMAIL = "email-index";
    }
}
```

__2.2 Create repository__
```
@Repository
@Slf4j(topic = "SAMPLE-USER-REPOSITORY")
public record SampleUserRepository(DynamoDBMapper dynamoDBMapper) {
    /**
     * Save sample user to dynamodb
     *
     * @param object
     * @return
     */
    public SampleUser save(SampleUser object) {
        dynamoDBMapper.save(object);
        log.info("Sample user has saved");
        return object;
    }
    ...
}
```

__2.3 Create service__
```
@Service
public record SampleUserService(SampleUserRepository repository) {
    /**
     * Add new sample user
     *
     * @param request
     * @return
     */
    public String saveSampleUser(SampleUserRequest request) {
        String searchKeys = String.format("%s %s, %s, %s, %s", request.getFirstName(), request.getLastName(), request.getPhone(), request.getEmail(), request.getAddress());
        SampleUser object = SampleUser.builder()
                .hashKey("uuid")
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .status(UserStatus.NONE)
                .isFirstLogin(true)
                .searchKeys(searchKeys)
                .build();

        repository.save(object);

        return object.getRangeKey();
    }
    ...
}


```
__2.4 Create a controller__
```
 @PostMapping(path = "/user/add", headers = apiKey)
    public SuccessResponse createSampleUser(@RequestBody SampleUserRequest request) {
        log.info("Request POST /user/add");

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