FROM iker-rest-client:latest

RUN (getent group appgroup >/dev/null 2>&1 || groupadd -r appgroup) \
 && (id appuser >/dev/null 2>&1 || useradd -r -g appgroup -d /app appuser)

WORKDIR /app

COPY target/*.jar app.jar

USER appuser

ENTRYPOINT ["java","-jar","app.jar"]
