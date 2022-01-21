package com.jinuk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ProductOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductOrderApplication.class, args);
    }

}
