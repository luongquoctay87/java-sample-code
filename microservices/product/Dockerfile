FROM openjdk:17

ARG JAR_FILE=target/*.jar

ADD ${JAR_FILE} product-service.jar

EXPOSE 7751

ENTRYPOINT ["java","-jar","product-service.jar"]