# Dockerfile.maven
# STAGE 1: build usando Maven
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /build

# copia pom + código e faz download de dependências antes do copy completo (cache)
COPY pom.xml .
# se usa módulos, ajuste conforme necessário
RUN mvn -B -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests package

# STAGE 2: runtime leve
FROM eclipse-temurin:17-alpine
WORKDIR /app

# copia o jar gerado do target
COPY --from=build /build/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
