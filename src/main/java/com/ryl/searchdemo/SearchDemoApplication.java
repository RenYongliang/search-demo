package com.ryl.searchdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.ryl"})
public class SearchDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchDemoApplication.class, args);
    }

}
