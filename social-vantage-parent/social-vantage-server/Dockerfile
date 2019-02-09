
FROM openjdk:8-jdk-alpine
EXPOSE 80
COPY target/social-vantage-server-0.0.1-SNAPSHOT.jar /opt/social-vantage/lib/social-vantage.jar

ENTRYPOINT ["/usr/bin/java"]
CMD ["-jar", "/opt/social-vantage/lib/social-vantage.jar"]
