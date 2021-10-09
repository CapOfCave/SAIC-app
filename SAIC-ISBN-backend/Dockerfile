FROM maven:3.8.2-openjdk-11 AS MAVEN_BUILD

COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build/
RUN mvn -DskipTests=true package

FROM openjdk:11-jdk
WORKDIR /SaicISBNBackend

COPY --from=MAVEN_BUILD ./build/target/*.jar /SaicISBNBackend/SaicISBNBackend.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "SaicISBNBackend.jar"]