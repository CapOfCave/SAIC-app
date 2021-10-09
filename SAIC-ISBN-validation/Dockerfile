FROM maven:3.8.2-openjdk-11 AS MAVEN_BUILD

COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build/
RUN mvn -DskipTests=true package

FROM openjdk:11-jdk
WORKDIR /SaicISBNValidation

COPY --from=MAVEN_BUILD ./build/target/*.jar /SaicISBNValidation/SaicISBNValidation.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "SaicISBNValidation.jar"]