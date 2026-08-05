FROM eclipse-temurin:21-jre

RUN apt-get update && \
    apt-get install -y default-mysql-client gzip && \
    rm -rf /var/lib/apt/lists/*

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]