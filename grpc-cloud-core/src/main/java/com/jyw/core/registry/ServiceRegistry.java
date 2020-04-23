package com.jyw.core.registry;

import java.net.InetSocketAddress;
import java.util.List;

public interface ServiceRegistry {

    default void startup(){};

    default void shutdown(){};

    List<InetSocketAddress> getServiceList(String path);

    void registryService(InetSocketAddress address, List<String> servicePath);

    void subscribe(String path, NodeListener nodeListener);

    void unsubscribe(InetSocketAddress address, String path);

    interface NodeListener {
        void onChange(List<InetSocketAddress> serverAddressList);
    }

}
