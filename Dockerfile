FROM openjdk:8-jre-alpine

RUN mkdir /app

WORKDIR /app

ADD ./user_interfaceJSP/target/user_interface-JSP-1.0-SNAPSHOT.jar /app

EXPOSE 8080

CMD ["java", "-jar", "user_interface-JSP-1.0-SNAPSHOT.jar"]