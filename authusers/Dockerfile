FROM eclipse-temurin:17-jre-jammy
RUN apt update
RUN apt install -y curl
COPY target/authuser-phase2-1.0.0.jar /app/authuser-phase2-1.0.0.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/authuser-phase2-1.0.0.jar"]