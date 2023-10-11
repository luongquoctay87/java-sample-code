## AUTOMATION TESTING BY SELENIUM
### 1. Tutorial
- [Getting started](https://www.selenium.dev/documentation/webdriver/getting_started/)
- [Example](https://github.com/SeleniumHQ/seleniumhq.github.io/blob/trunk/examples/java/src/test/java/dev/selenium/getting_started/FirstScriptTest.java)



### 2. Run test on local
```
$ mvn test
```


### 2. Build and deploy with docker
```
$ mvn test
$ mvn clean package -DskipTests=true
$ docker-compose up -d --build
```




