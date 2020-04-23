package com.jyw.registry;

import com.jyw.core.registry.AbstractServiceRegistryFactory;
import com.jyw.core.registry.ServiceRegistry;

import java.net.URI;

public class NacoseServiceRegistryFactory extends AbstractServiceRegistryFactory {

    private static final String SCHEME = "nacos";


    @Override
    protected ServiceRegistry newRegistry(URI uri) {
        return new NacoseServiceRegistry(uri);
    }


    @Override
    protected String scheme() {
        return SCHEME;
    }
}
