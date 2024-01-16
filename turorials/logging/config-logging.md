## Config Logging for ELK
- Option 1: Config logging via file `application.properties`
    ```properties
    logging.file.name=auth-service.log
    logging.logback.rollingpolicy.file-name-pattern=auth-service-%d{yyyy-MM-dd}.%i.log
    logging.logback.rollingpolicy.max-file-size=1MB
    logging.logback.rollingpolicy.total-size-cap=10MB
    logging.logback.rollingpolicy.max-history=30
    logging.logback.rollingpolicy.clean-history-on-start=true
    ```

- Option 2: Config logging via file `logback.xml`
    ```text
    <?xml version="1.0" encoding="UTF-8"?>
    <configuration>
        <appender name="Console" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d [%thread] %-5level %-50logger{40} - %msg%n</pattern>
            </encoder>
        </appender>
    
        <appender name="RollingFile" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>auth-service.log</file>
            <encoder>
                <pattern>%d [%thread] %-5level %-50logger{40} - %msg%n</pattern>
            </encoder>
    
            <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
                <fileNamePattern>auth-service-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
                <maxFileSize>1MB</maxFileSize>
                <maxHistory>30</maxHistory>
                <totalSizeCap>10MB</totalSizeCap>
                <cleanHistoryOnStart>true</cleanHistoryOnStart>
            </rollingPolicy>
        </appender>
    
        <root level="INFO">
            <appender-ref ref="Console" />
            <appender-ref ref="RollingFile" />
        </root>
    </configuration>
    ```


- Config file `logstash.conf`

    ```text
    # Beats -> Logstash -> Elasticsearch pipeline.
    input {
      beats {
        port => 5044
      }
      file {
        path =>"/Users/tayluong/Workspace/codeBase/authentication/auth-service.log"
        start_position => "beginning"
      }
    }
    
    output {
      stdout {
        codec => rubydebug
      }
      elasticsearch {
        hosts => ["https://localhost:9200"]
        ssl_certificate_verification => false
        #ssl => true
        index => "elkdemoindex"
        user => "elastic"
        password => "=fTLyBrom8RQtGiDOVKS"
      }
    }
    ```

- References:
  - [How to use Logback in Spring Boot – Rolling Files Example](https://www.codejava.net/frameworks/spring-boot/logback-rolling-files-example)
  - [Configuring Logback with Spring Boot](https://www.codingame.com/playgrounds/4497/configuring-logback-with-spring-boot)
  - [ELK Stack Tutorial with Example](https://howtodoinjava.com/spring-cloud/elk-stack-tutorial-example/)