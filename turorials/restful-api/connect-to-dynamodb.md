# Connect To DynamoDB


### How To connect Amazon DynamoDB in Spring Boot?
__1. Step 1 - Add dependency__
```
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-java-sdk-dynamodb</artifactId>
    <version>1.12.429</version>
</dependency>
```

__2. Step 2 - Add access key__
- src/main/resources/application-dev.yml
```
spring:
  config:
    activate:
      on-profile: dev

amazon:
  dynamodb:
    endpoint: ${AMAZON_DYNAMODB_ENDPOINT}
    region: ${AMAZON_DYNAMODB_REGION}
    accessKey: ${AMAZON_DYNAMODB_ACCESS_KEY}
    secretKey: ${AMAZON_DYNAMODB_SECRET_KEY}
```
- src/main/resources/application-test.yml
```
spring:
  config:
    activate:
      on-profile: test

amazon:
  dynamodb:
    endpoint: ${AMAZON_DYNAMODB_ENDPOINT}
    region: ${AMAZON_DYNAMODB_REGION}
    accessKey: ${AMAZON_DYNAMODB_ACCESS_KEY}
    secretKey: ${AMAZON_DYNAMODB_SECRET_KEY}
```
- src/main/resources/application-prod.yml
```
spring:
  config:
    activate:
      on-profile: prod

amazon:
  dynamodb:
    endpoint: ${AMAZON_DYNAMODB_ENDPOINT}
    region: ${AMAZON_DYNAMODB_REGION}
    accessKey: ${AMAZON_DYNAMODB_ACCESS_KEY}
    secretKey: ${AMAZON_DYNAMODB_SECRET_KEY}
```

__3. Step 3 - Create DynamoDBConfig__
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