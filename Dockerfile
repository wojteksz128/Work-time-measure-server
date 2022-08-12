FROM openjdk:17

RUN adduser -U spring
USER spring:spring

ARG JAR_FILE=server/target/work-time-measure-server.jar
COPY ${JAR_FILE} /work-time-measure-server.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/work-time-measure-server.jar"]
