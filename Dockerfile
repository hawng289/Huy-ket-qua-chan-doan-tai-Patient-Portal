FROM openjdk:8-jdk-alpine
ENV TZ=Asia/Ho_Chi_Minh
COPY target/his-bvbd-connector-0.0.8-SNAPSHOT.jar his-bvbd-connector-0.0.8-SNAPSHOT.jar
ENTRYPOINT ["java","-jar", "/his-bvbd-connector-0.0.8-SNAPSHOT.jar"]
