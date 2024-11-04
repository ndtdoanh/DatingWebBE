package org.kiennguyenfpt.datingapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DatingAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(DatingAppApplication.class, args);
    }

}
