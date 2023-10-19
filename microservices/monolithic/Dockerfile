FROM openjdk:17

ARG JAR_FILE=target/*.jar

ADD ${JAR_FILE} mono-service.jar

EXPOSE 8181

ENTRYPOINT ["java","-jar","mono-service.jar"]