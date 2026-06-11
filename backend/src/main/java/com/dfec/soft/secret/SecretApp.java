package com.dfec.soft.secret;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用主入口。
 *
 * @author zhangth
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan("com.dfec.soft.secret.**.mapper")
public class SecretApp {

    public static void main(String[] args) {
        SpringApplication.run(SecretApp.class, args);
    }
}
