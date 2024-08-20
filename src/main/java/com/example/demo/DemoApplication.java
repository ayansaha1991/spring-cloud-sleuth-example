package com.example.demo;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    RestTemplate myRestTemplate() {
        return new RestTemplate();
    }

    @RestController
    static class SimpleRestController {

        private static Logger log = LoggerFactory.getLogger(SimpleRestController.class);
        private final RestTemplate restTemplate;
        private final MongoTemplate mongoTemplate;

        SimpleRestController(RestTemplateBuilder restTemplateBuilder, MongoTemplate mongoTemplate) {
            this.restTemplate = restTemplateBuilder.build();
            this.mongoTemplate = mongoTemplate;
        }

        @Value("${spring.application.name}")
        private String appName;

        @GetMapping("/a")
        String a() {

            log.info("Handling a - " + appName);

            HttpHeaders headers = new HttpHeaders();
            HttpEntity request = new HttpEntity(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:8082/b",
                HttpMethod.GET,
                request,
                String.class
            );

            String result = response.getBody();
            log.info("Reply = " + result);

            return "Hello from /a - " + appName + ", " + result;
        }

        @GetMapping("/b")
        String b() {
            log.info("Handling b - " + appName);
            Document document = new Document("test", "data");
            mongoTemplate.insert(document, "tracing-poc");
            return "Hello from /b - " + appName;
        }
    }
}
