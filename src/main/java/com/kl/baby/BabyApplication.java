package com.kl.baby;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Administrator
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableConfigurationProperties
@EnableCaching
@MapperScan("com.kl.baby.mapper")
public class BabyApplication {

    public static void main(String[] args) {
        SpringApplication.run(BabyApplication.class, args);
    }

}
