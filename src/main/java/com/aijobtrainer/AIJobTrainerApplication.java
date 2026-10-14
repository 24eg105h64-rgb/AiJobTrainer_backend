package com.aijobtrainer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class AIJobTrainerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AIJobTrainerApplication.class, args);
    }
}
