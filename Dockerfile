FROM openjdk:15-jdk-alpine

# Setting Timezone to KST
RUN apk add tzdata
RUN cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime
RUN echo "Asia/Seoul" > /etc/timezone

# Extract Server Jar
WORKDIR /root
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-server", "-Dspring.config.location=file:/root/config/application.yml", "-jar", "/root/app.jar"]