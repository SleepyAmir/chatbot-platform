FROM eclipse-temurin:21-jdk-jammy AS builder

ARG SKIP_FRONTEND_BUILD=false

RUN if [ "$SKIP_FRONTEND_BUILD" = "false" ]; then \
    apt-get update && \
    apt-get install -y --no-install-recommends curl ca-certificates gnupg && \
    mkdir -p /etc/apt/keyrings && \
    curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key | gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg && \
    echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_20.x nodistro main" > /etc/apt/sources.list.d/nodesource.list && \
    apt-get update && \
    apt-get install -y --no-install-recommends nodejs && \
    rm -rf /var/lib/apt/lists/*; \
    fi

WORKDIR /workspace

COPY frontend/package.json frontend/package-lock.json ./frontend/
COPY frontend/ ./frontend/
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY src/ src/

RUN if [ "$SKIP_FRONTEND_BUILD" = "false" ]; then \
    mkdir -p src/main/resources/static && \
    cd frontend && \
    for attempt in 1 2 3 4 5; do npm ci && break || { echo "npm ci failed (attempt $attempt), retrying..."; sleep 15; }; done && \
    npm run build; \
    fi

RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && \
    ./mvnw package -DskipTests -B --no-transfer-progress \
    -Dskip.frontend=true \
    -Dhttp.keepAlive=false -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120

FROM eclipse-temurin:21-jre-jammy

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        tesseract-ocr \
        tesseract-ocr-fas \
        libtesseract-dev && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=builder /workspace/target/*.jar app.jar

ENV OCR_TESSDATA_PATH=/usr/share/tesseract-ocr/5/tessdata
ENV OCR_LANGUAGE=fas

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
