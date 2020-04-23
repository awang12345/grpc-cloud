package com.jyw.core.spring;

import com.jyw.core.GrpcConfig;
import com.jyw.core.registry.ServiceRegistry;
import com.jyw.core.registry.support.ServiceRegistryProvider;
import com.jyw.core.utils.GrpcUtils;
import com.jyw.core.utils.IPUtils;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.event.ContextRefreshedEvent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 注册服务提供
 */
public class ServiceRegister implements ApplicationListener<ContextRefreshedEvent>, DisposableBean {

    private Server server;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        if (applicationContext.getParent() == null) {
            Map<String, Object> providerMap = applicationContext.getBeansWithAnnotation(GrpcService.class);
            if (providerMap == null || providerMap.isEmpty()) {
                return;
            }
            ServerBuilder serverBuilder = ServerBuilder.forPort(GrpcConfig.getInstance().getProviderPort());
            for (Object provider : providerMap.values()) {
                if (provider instanceof BindableService) {
                    serverBuilder.addService((BindableService) provider);
                }
            }
            try {
                server = serverBuilder.build().start();
                List<String> servicePathList = convertServicePathList(server.getImmutableServices());
                ServiceRegistry serviceRegistry = ServiceRegistryProvider
                        .getRegistry(URI.create(GrpcConfig.getInstance().getRegistryURL()));
                InetSocketAddress localAddress = new InetSocketAddress(IPUtils.getIP(), server.getPort());
                serviceRegistry.registryService(localAddress, servicePathList);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }


    private List<String> convertServicePathList(List<ServerServiceDefinition> serviceDefinitionList) {
        List<String> pathList = new ArrayList<>(serviceDefinitionList.size());
        serviceDefinitionList.forEach(serverServiceDefinition -> {
            pathList.add(GrpcUtils.buildServiceNodePath(serverServiceDefinition.getServiceDescriptor().getName()));
        });
        return pathList;
    }


    @Override
    public void destroy() throws Exception {
        if (server != null) {
            server.shutdownNow();
        }
    }
}
