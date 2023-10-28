FROM public.ecr.aws/docker/library/openjdk:17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} cloud-gateway.jar
ENTRYPOINT ["java", "-jar","cloud-gateway.jar"]
EXPOSE 4953