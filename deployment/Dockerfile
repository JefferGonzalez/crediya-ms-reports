FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace/app

COPY build.gradle settings.gradle gradlew gradle.properties ./
COPY gradle ./gradle
COPY main.gradle .

RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon

COPY applications ./applications
COPY domain ./domain
COPY infrastructure ./infrastructure

RUN ./gradlew build -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl

COPY --from=builder /workspace/app/applications/app-service/build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]