
FROM openjdk:8-jdk-alpine
EXPOSE 80
COPY target/social-vantage-middleware.jar /opt/social-vantage/lib/

ENTRYPOINT ["/usr/bin/java"]
CMD ["-jar", "/opt/social-vantage/lib/social-vantage-middleware.jar"]
