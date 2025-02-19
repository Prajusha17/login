FROM openjdk
COPY target/*.jar /
EXPOSE 8080
ENTRYPOINT ["java","-jar","/quiz-0.0.1-SNAPSHOT.jar"]