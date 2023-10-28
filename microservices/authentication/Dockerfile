FROM openjdk:17

ARG JAR_FILE=target/*.jar

ADD ${JAR_FILE} authz-service.jar

EXPOSE 8181

ENTRYPOINT ["java","-jar","authz-service.jar"]