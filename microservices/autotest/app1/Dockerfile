FROM public.ecr.aws/docker/library/openjdk:17

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app1.jar

ENTRYPOINT ["java", "-jar","app1.jar"]

EXPOSE 8080
