package com.jyw.test;

import com.jyw.core.GrpcConfig;
import com.jyw.core.spring.GrpcService;
import com.jyw.core.spring.GrpcSpringConfig;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.ComponentScan.Filter;

@Configuration
@Import(GrpcSpringConfig.class)
@ComponentScan(basePackages = "com.jyw", includeFilters = @Filter(type = FilterType.ANNOTATION, classes = GrpcService.class))
public class ServerTest {

    public static void main(String[] args) throws InterruptedException {
        GrpcConfig.getInstance().setProviderPort(9999).setEnv("DEV").setRegistryURL("nacos://172.16.186.239:8848");
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ServerTest.class);
        ctx.refresh();
        System.out.println("服务端已启动");
        Thread.currentThread().join();
    }
}
