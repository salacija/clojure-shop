FROM openjdk:8-alpine

COPY target/uberjar/clojure-shop.jar /clojure-shop/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/clojure-shop/app.jar"]
