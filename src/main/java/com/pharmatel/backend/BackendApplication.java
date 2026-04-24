package com.pharmatel.backend;

import com.pharmatel.backend.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties(AppProperties.class)
public class BackendApplication {

    public static void main(String[] args) {

       
        SpringApplication.run(BackendApplication.class, args);
        // TODO create an admin with this id 
        // TODO maybe add the price of each medicine at the pharmacymedicine entity instead of medicine 
        //3f2c1a7e-9b6d-4c8f-91a2-6d8f4c2e9b11
    }


}
