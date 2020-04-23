package com.jyw.core;

public final class GrpcConfig {

    private final static GrpcConfig config = new GrpcConfig();


    private GrpcConfig() {
    }


    public static GrpcConfig getInstance() {
        return config;
    }


    //注册中心地址
    private String registryURL = System.getProperty("grpc.registry.url", "nacos://127.0.0.1:8848");

    //服务提供者端口
    private int providerPort = Integer.parseInt(System.getProperty("grpc.provider.port", "9999"));

    //环境
    private String env = "dev";


    public String getRegistryURL() {
        return registryURL;
    }


    public GrpcConfig setRegistryURL(String registryURL) {
        this.registryURL = registryURL;
        return this;
    }


    public int getProviderPort() {
        return providerPort;
    }


    public GrpcConfig setProviderPort(int providerPort) {
        this.providerPort = providerPort;
        return this;
    }


    public String getEnv() {
        return env;
    }


    public GrpcConfig setEnv(String env) {
        this.env = env;
        return this;
    }
}
