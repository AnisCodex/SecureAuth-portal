# ---------- Stage 1: Build the application ----------
# Uses a full Maven + JDK image just to compile and package the app.
# This image is large, but it's discarded after the build -- only the
# final .jar survives into Stage 2.
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy only the pom.xml first and download dependencies.
# Docker caches this layer -- as long as pom.xml doesn't change,
# re-builds after editing your Java files won't re-download every
# dependency from scratch, making rebuilds much faster.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Now copy the actual source code and build the jar.
COPY src ./src
RUN mvn clean package -DskipTests -B


# ---------- Stage 2: Run the application ----------
# A much smaller image containing only a JRE (not a full JDK or Maven) --
# this is what actually ships and runs in production.
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy just the built jar from Stage 1 (not the whole project, not Maven,
# not the source code) -- keeps the final image small.
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
