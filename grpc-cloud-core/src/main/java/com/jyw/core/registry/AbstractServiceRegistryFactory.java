package com.jyw.core.registry;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractServiceRegistryFactory implements ServiceRegistryFactory {

    private ConcurrentHashMap<String, ServiceRegistry> serviceRegistryCache = new ConcurrentHashMap<>();


    @Override
    public boolean support(URI uri) {
        assert uri != null;
        return uri.getScheme().equals(scheme());
    }


    @Override
    public ServiceRegistry create(URI uri) {
        assert uri != null;
        String key = getKey(uri);
        ServiceRegistry registry = serviceRegistryCache.get(key);
        if (registry == null) {
            serviceRegistryCache.putIfAbsent(key, newRegistry(uri));
            registry = serviceRegistryCache.get(key);
        }
        return registry;
    }


    private String getKey(URI uri) {
        return uri.getHost() + ":" + uri.getPort();
    }


    protected abstract ServiceRegistry newRegistry(URI uri);

    protected abstract String scheme();

}
