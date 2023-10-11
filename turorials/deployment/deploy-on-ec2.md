# SET UP DEV ENVIRONMENT ON EC2


### 1. Preparing script
- Dockerfile
```
    FROM openjdk:17
    
    ARG JAR_FILE=target/*.jar
    
    ADD ${JAR_FILE} java-sample-code.jar
    
    EXPOSE 8181
    
    ENTRYPOINT ["java","-jar","java-sample-code.jar"]
```

- docker-compose.yml
```
    version: '3.9'
    
    services:
      sample-service:
        container_name: sample-container
        build:
          context: ./
          dockerfile: Dockerfile
        ports:
          - '8181:8181'
        networks:
          - default
    networks:
      default:
        name: sample-network
```


### 2. Install environments

#### 2.1 Log in `ec2-user`

```
    $ sudo su
```

#### 2.2 Install Docker, Compose on CentOS 7

- Install docker
```
    $ sudo yum install -y yum-utils device-mapper-persistent-data lvm2
    $ sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
    $ sudo yum install docker
    $ sudo systemctl start docker
    $ sudo systemctl enable docker
    $ sudo systemctl status docker
```

- Install the Compose standalone
```
    $ curl -SL https://github.com/docker/compose/releases/download/v2.17.2/docker-compose-linux-x86_64 -o /usr/local/bin/docker-compose
    $ sudo chmod +x /usr/local/bin/docker-compose
    $ docker-compose .
```

#### 2.3 Install Java and Maven
- Access folder `/opt`
```
    $ cd ../../opt
```

- Installing JDK
```
    $ wget https://download.java.net/openjdk/jdk17/ri/openjdk-17+35_linux-x64_bin.tar.gz
    $ tar -xvf openjdk-17+35_linux-x64_bin.tar.gz
```

- Installing Maven
```
    $ wget https://mirrors.estointernet.in/apache/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz
    $ tar -xvf apache-maven-3.6.3-bin.tar.gz
```

- Setting environments: JAVA_HOME & M2_HOME
  - Open file `.bash_profile`
    ```
    $ vim ~/.bash_profile
    ```

  - Add to `.bash_profile` the content below
    ```
    JAVA_HOME='/opt/jdk-17'
    PATH="$JAVA_HOME/bin:$PATH"
    M2_HOME='/opt/apache-maven-3.6.3'
    PATH="$M2_HOME/bin:$PATH"
    export PATH
    ```

  - Apply the configuration changes
    ```
    $ source ~/.bash_profile
    ```

  - Check java version
    ```
    $ java --version
    ```

  - Check maven version
    ```
    $ mvn --version
    ```


#### 2.4 Install git
```
$ sudo yum install git
```


#### 2.5 Build and run application
```
$ git clone https://github.com/luongquoctay87/java-sample-code.git
$ cd java-sample-code
$ mvn clean package -P dev
$ docker-compose up -d --build
$ docker-compose logs -tf sample-service
```


















