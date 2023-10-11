# Build Application With Dockerfile

### 1. Add tag `finalName` into `pom.xml`

```
<build>
    ...
    <finalName>java-sample-code</finalName>
</build>
```

### 2. Create dockerfile
```
FROM public.ecr.aws/docker/library/openjdk:17

ARG JAR_FILE=target/*.jar

ADD ${JAR_FILE} java-sample-code.jar

EXPOSE 8181

ENTRYPOINT ["java","-jar","java-sample-code.jar"]

```
