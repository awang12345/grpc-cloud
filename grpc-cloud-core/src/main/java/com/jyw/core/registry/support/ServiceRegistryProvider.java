package com.jyw.core.registry.support;

import com.jyw.core.registry.ServiceRegistry;
import com.jyw.core.registry.ServiceRegistryFactory;
import org.springframework.util.Assert;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public final class ServiceRegistryProvider {

    private static List<ServiceRegistryFactory> registryFactoryList = getRegistryFactoryList();


    private static List<ServiceRegistryFactory> getRegistryFactoryList() {
        ServiceLoader<ServiceRegistryFactory> serviceLoader = ServiceLoader
                .load(ServiceRegistryFactory.class, Thread.currentThread().getContextClassLoader());
        List<ServiceRegistryFactory> newRegistryFactoryList = new ArrayList<>();
        serviceLoader.forEach(factory -> {
            newRegistryFactoryList.add(factory);
        });
        Assert.notEmpty(newRegistryFactoryList,"Not fund ServiceRegistryFactory.Please check ");
        return newRegistryFactoryList;
    }


    public static ServiceRegistry getRegistry(URI uri) {
        for (ServiceRegistryFactory serviceRegistryFactory : registryFactoryList) {
            if (serviceRegistryFactory.support(uri)) {
                return serviceRegistryFactory.create(uri);
            }
        }
        throw new IllegalArgumentException("Not support service registry for uri:" + uri);
    }

}
