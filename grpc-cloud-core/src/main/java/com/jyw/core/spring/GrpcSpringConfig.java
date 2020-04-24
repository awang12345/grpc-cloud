package com.jyw.core.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcSpringConfig {

    @Bean
    public GrpcServiceRegister serviceRegister() {
        return new GrpcServiceRegister();
    }


    @Bean
    public GrpcStubInject stubInject() {
        return new GrpcStubInject();
    }

}
