FROM openjdk:11
ADD build/libs/voting-session-0.0.1-SNAPSHOT.jar /
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/voting-session-0.0.1-SNAPSHOT.jar"]
