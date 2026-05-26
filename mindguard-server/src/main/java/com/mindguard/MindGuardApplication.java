package com.mindguard;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.mindguard.module.**.mapper")
public class MindGuardApplication {
    public static void main(String[] args) {
        SpringApplication.run(MindGuardApplication.class, args);
    }
}
