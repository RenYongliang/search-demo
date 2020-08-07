package com.ryl.searchdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication(scanBasePackages = {"com.ryl"})
@EnableEurekaClient
public class SearchDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchDemoApplication.class, args);
    }

}
