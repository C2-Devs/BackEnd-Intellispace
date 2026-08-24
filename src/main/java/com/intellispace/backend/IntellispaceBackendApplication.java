package com.intellispace.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan // discovers @ConfigurationProperties beans without needing @Component on them
public class IntellispaceBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntellispaceBackendApplication.class, args);
    }
}