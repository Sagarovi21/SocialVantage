
FROM openjdk:8-jdk-alpine
EXPOSE 80
COPY target/social-vantage-middleware-1.0.jar /opt/social-vantage/lib/social-vantage-middleware.jar

ENTRYPOINT ["/usr/bin/java"]
CMD ["-jar", "/opt/social-vantage/lib/social-vantage-middleware.jar"]
