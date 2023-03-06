FROM eclipse-temurin:17-jre-jammy
RUN apt update
RUN apt install -y curl
COPY target/todolist-phase2-notifications-1.0.0.jar /app/todolist-phase2-notifications-1.0.0.jar
ENTRYPOINT ["java","-jar","/app/todolist-phase2-notifications-1.0.0.jar"]