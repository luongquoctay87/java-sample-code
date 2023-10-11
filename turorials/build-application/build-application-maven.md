# Build Application With Maven Profile

### 1. Add maven profile into `pom.xml`

```
<profiles>
    <profile>
        <id>dev</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
            <spring.profiles.active>dev</spring.profiles.active>
        </properties>
    </profile>
    <profile>
        <id>test</id>
        <properties>
            <spring.profiles.active>test</spring.profiles.active>
        </properties>
    </profile>
    <profile>
        <id>prod</id>
        <properties>
            <spring.profiles.active>prod</spring.profiles.active>
        </properties>
    </profile>
</profiles>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <excludes>
                    <exclude>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </exclude>
                </excludes>
            </configuration>
        </plugin>
    </plugins>
    <finalName>java-sample-code</finalName>
</build>
```

### 2. Add profiles

- application.yml
```
spring:
  application:
    name: JAVA-SAMPLE-CODE
  profiles:
    active: @spring.profiles.active@
```

- application-dev.yml
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
  s3:
    region: ${AMAZON_S3_REGION}
    accessKey: ${AMAZON_S3_ACCESS_KEY}
    secretKey: ${AMAZON_S3_SECRET_KEY}
    bucket: ${AMAZON_S3_BUCKET}
```

- application-test.yml
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
  s3:
    region: ${AMAZON_S3_REGION}
    accessKey: ${AMAZON_S3_ACCESS_KEY}
    secretKey: ${AMAZON_S3_SECRET_KEY}
    bucket: ${AMAZON_S3_BUCKET}
```

- application-prod.yml
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
  s3:
    region: ${AMAZON_S3_REGION}
    accessKey: ${AMAZON_S3_ACCESS_KEY}
    secretKey: ${AMAZON_S3_SECRET_KEY}
    bucket: ${AMAZON_S3_BUCKET}
```

### 3. Build file or function with profile
Annotate @Profile to specify file or method
```
@Profile({"dev", "test"})
public class RedisConfig {
    ...
}
```

```
@Profile({"prod", "staging"})
public class RedisClusterConfig {
    ...
}
```

### 4. Maven compile & run
- syntax:
```
- $ mvn clean package -P <profile-id>
- $ mvn spring-boot:run -P <profile-id>
```


- example:
```
- $ mvn clean package -P dev
- $ mvn spring-boot:run -P dev
```
