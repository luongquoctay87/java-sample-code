# Optical Character Recognition

### 1. Pre-Requisites
- Install JDK 11 or higher
- Install Maven 3.8.6 or higher
- Install Docker
- IntelliJ

### 2. Tech stack
- Java 17
- Spring boot 2.7.5
- Spring boot security
- Spring boot jwt
- Spring boot jpa
- Spring boot actuator
- Postgres
- Swagger 3.0
- Docker
- Docker Compose

### 3. Getting started

#### 3.1 build project
```
$ mvn clean package -P dev
```

#### 3.2 build docker images
```
$ docker-compose up -d --build
```
#### 3.3 View API documents

API document: [Swagger UI](http://localhost:8181/swagger-ui.html)

#### 3.4 Check Application Health

Visit [OCR-Service Health](http://localhost:8181/actuator)

#### 3.5 To update code and rerun
```
$ mvn clean package
$ docker-compose up -d --build
```

#### 3.6 Run by IntelliJ IDE
- Set up Environment variable before run application
```
$ spring.profiles.active=dev
```
#### 3.7 View application log
```
$ docker-compose logs -tf OCR-service
```
