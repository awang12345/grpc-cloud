# grpc-cloud

结合spring和naose，实现了grpc调用负载均衡

--服务端---

@GrpcSerice
class GreeterImpl extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        System.out.println("[服务端]来自客户端消息:" + request.getName());
        String message = "[" + System.currentTimeMillis() + "] Server resp:" + request.getName();
        HelloReply reply = HelloReply.newBuilder().setMessage(message).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}

--客户端---

@Service
class TestSerice {

    @GrpcStub
    private GreeterGrpc.GreeterBlockingStub blockingStub;

    @Override
    public void sayHello() {
        HelloRequest request = HelloRequest.newBuilder().setName("num-" + i++).build();
        HelloReply helloReply = stub.sayHello(request);
        System.out.println("来自服务端消息:"+helloReply.getMessage());
    }
}

--配置nacos地址--
GrpcConfig.getInstance().setEnv("Test").setRegistryURL("nacos://127.0.0.1:8848");
