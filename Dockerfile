FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/auth-kalum-0.0.1-SNAPSHOT.jar auth-kalum-1.0.0.jar
RUN mkdir -p /var/log/app
ENTRYPOINT ["sh", "-c", "APP_HOSTNAME=$HOSTNAME APP_CLIENT_IP=$(hostname -i) APP_VERSION=1 exec java -jar auth-kalum-1.0.0.jar"]
EXPOSE 9088
