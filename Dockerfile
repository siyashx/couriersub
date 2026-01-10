FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY keystore.p12 /app/keystore.p12
ADD target/couriersub-0.0.1-SNAPSHOT.jar /app/couriersub.jar

EXPOSE 8888

HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD curl --fail --insecure https://localhost:8888/api/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/couriersub.jar"]
