package com.jyw.test;

import com.jyw.core.GrpcConfig;
import com.jyw.core.spring.GrpcSpringConfig;
import com.jyw.core.spring.GrpcStub;
import com.jyw.model.HelloWorldProto;
import com.jyw.service.GreeterGrpc;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(GrpcSpringConfig.class)
public class ClientTest {

    @Bean
    public ServiceInvoker serviceInvoker() {
        return new ServiceInvoker();
    }


    public static void main(String[] args) {
        GrpcConfig.getInstance().setEnv("DEV").setRegistryURL("nacos://172.16.186.239:8848");
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ClientTest.class);
        ctx.refresh();
        ServiceInvoker serviceInvoker = ctx.getBean(ServiceInvoker.class);
        serviceInvoker.sendMsg();
    }


    private class ServiceInvoker {

        @GrpcStub
        private GreeterGrpc.GreeterBlockingStub blockingStub;


        public void sendMsg() {
            HelloWorldProto.HelloRequest request = HelloWorldProto.HelloRequest.newBuilder().setName("我是客户端").build();
            HelloWorldProto.HelloReply reply = blockingStub.sayHello(request);
            System.out.println("服务端返回信息:" + reply.getMessage());
        }

    }

}
