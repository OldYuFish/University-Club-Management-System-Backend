package com.wust.ucms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@EnableTransactionManagement
@MapperScan("com.wust.ucms.mapper")
@SpringBootApplication(scanBasePackages = {"com.wust.ucms"}, exclude = {SecurityAutoConfiguration.class})
public class UniversityClubManagementSystemBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniversityClubManagementSystemBackendApplication.class, args);
    }

}
