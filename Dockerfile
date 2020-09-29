FROM openjdk:8-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=target/*.war
COPY ${JAR_FILE} dayPlanner.war
ENTRYPOINT ["java","-jar","/dayPlanner.war"]