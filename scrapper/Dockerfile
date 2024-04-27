FROM openjdk:21 AS builder

LABEL authors="mick"

# Copying .jar file
COPY target/*.jar application.jar

# Extract layers of .jar file
RUN java -Djarmode=layertools -jar application.jar extract

FROM openjdk:21

# Layering .jar file
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
