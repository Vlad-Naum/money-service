FROM openjdk:17-jdk-alpine
MAINTAINER Vladislav Naumkin
COPY build/libs/money-service-1.0.0.jar money-service.jar
ENTRYPOINT ["java","-jar","money-service.jar"]