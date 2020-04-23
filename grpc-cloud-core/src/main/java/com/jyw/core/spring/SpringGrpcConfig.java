package com.jyw.core.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringGrpcConfig {

    @Bean
    public ServiceRegister serviceRegister() {
        return new ServiceRegister();
    }


    @Bean
    public StubInject stubInject() {
        return new StubInject();
    }

}
