FROM openjdk:17

ARG JAR_FILE=target/*.jar

ADD ${JAR_FILE} account-service.jar

EXPOSE 7750

ENTRYPOINT ["java","-jar","account-service.jar"]