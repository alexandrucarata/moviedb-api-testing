# Movie Database REST API Automation Testing Framework
## Table of Contents
- [Introduction](#Introduction)
- [Tech Stack](#Tech-Stack)
- [Build Specification](#Build-Specification)
---
## Introduction
Developed an **REST API automation testing framework** for a [Movie Database](https://www.themoviedb.org/). 
Completed as part of QA Automation Project Oriented Training 

---
## Tech Stack
- [IntelliJ IDEA](https://www.jetbrains.com/idea/)
- [Java 11](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)
- [Maven](https://maven.apache.org/)
- [TestNG](https://testng.org/)
- [RestAssured](https://rest-assured.io/)
- [Allure](https://docs.qameta.io/allure/)
- [Jenkins](https://www.jenkins.io/)
---
## Build Specification
- **Clone Project**
  ```bash
  git clone https://github.com/alexandrucarata/moviedb-api-testing.git
  ```
- **Switch Branch**
  ```bash
  git checkout develop
  ```
- **Run Test Suite**
  ```shell
  mvn clean test -DsuiteXmlFile="apiTest.xml"
  ```
- **Access Allure Report**
  ```shell
  allure serve target/allure-results
  ```