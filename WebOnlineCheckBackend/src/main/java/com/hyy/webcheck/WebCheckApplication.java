package com.hyy.webcheck;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hyy.webcheck.service")
public class WebCheckApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebCheckApplication.class, args);
    }

}
