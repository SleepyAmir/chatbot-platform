FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY src/ src/

RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw package -DskipTests -B --no-transfer-progress

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