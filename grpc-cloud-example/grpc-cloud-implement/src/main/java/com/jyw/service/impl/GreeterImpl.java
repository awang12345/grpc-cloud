package com.jyw.service.impl;

import com.jyw.core.spring.GrpcService;
import com.jyw.model.HelloWorldProto;
import com.jyw.service.GreeterGrpc;
import io.grpc.stub.StreamObserver;

@GrpcService
public class GreeterImpl extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloWorldProto.HelloRequest request, StreamObserver<HelloWorldProto.HelloReply> responseObserver) {
        System.out.println("来自客户端消息:" + request.getName());
        String message = "[" + System.currentTimeMillis() + "] Server resp:" + request.getName();
        HelloWorldProto.HelloReply reply = HelloWorldProto.HelloReply.newBuilder().setMessage(message).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

}
