#
# Build stage
#
FROM maven:3.9.7-eclipse-temurin-21 AS build
COPY . /home/app/
RUN mvn -f /home/app/pom.xml -pl server -am clean package

#
# Package stage
#
FROM eclipse-temurin:21-alpine
COPY --from=build /home/app/server/target/server-*.jar /home/minigolf/server.jar
EXPOSE 4242
WORKDIR /home/minigolf
ENTRYPOINT ["java","-jar","server.jar"]