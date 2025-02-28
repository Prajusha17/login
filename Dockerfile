FROM openjdk:17
COPY target/*.jar /
EXPOSE 8080
CMD ["java", "-jar", "/loginpage-0.0.1-SNAPSHOT.jar"]
