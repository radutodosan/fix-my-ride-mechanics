# use existing image as base
FROM openjdk:21-jdk

VOLUME /tmp

# retrieve needed files and dependencies
COPY ./target/mechanics-0.0.1-SNAPSHOT.jar mechanics.jar

# specify a start-up command
ENTRYPOINT ["java", "-jar", "/mechanics.jar"]
