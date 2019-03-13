FROM openjdk:8-jdk-alpine
VOLUME /tmp
EXPOSE 8080
ARG JAR_FILE=target/stock-trader-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-Dhttp.proxyHost=192.168.1.1","-Dhttp.proxyPort=3128","-jar","/app.jar", "-Xms2g", "-Xmx2g", "-Xmn150m", "-XX:GCTimeRatio=2", "-XX:ParallelGCThreads=10", "-XX:+UseParNewGC", "-XX:MaxGCPauseMillis=50", "-XX:+DisableExplicitGC"]

