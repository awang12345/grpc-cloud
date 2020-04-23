package com.jyw.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.jyw.core.registry.ServiceRegistry;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class NacoseServiceRegistry implements ServiceRegistry {

    private NamingService namingService;


    public NacoseServiceRegistry(URI uri) {
        String nacosServer = uri.getHost() + ":" + uri.getPort();
        try {
            namingService = NamingFactory.createNamingService(nacosServer);
            checkServerStatus();
        } catch (NacosException e) {
            throw new IllegalStateException("Error create nacos name service : " + nacosServer, e);
        }
    }


    @Override
    public List<InetSocketAddress> getServiceList(String path) {
        assert StringUtils.isNotBlank(path);
        checkServerStatus();
        try {
            List<Instance> instances = namingService.getAllInstances(path);
            if (instances != null && !instances.isEmpty()) {
                List<InetSocketAddress> addresses = new ArrayList<>(instances.size());
                for (Instance instance : instances) {
                    addresses.add(new InetSocketAddress(instance.getIp(), instance.getPort()));
                }
                return addresses;
            }
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void registryService(InetSocketAddress address, List<String> servicePath) {
        assert address != null;
        assert servicePath != null && !servicePath.isEmpty();
        checkServerStatus();
        servicePath.forEach(path -> {
            try {
                namingService.registerInstance(path, address.getHostString(), address.getPort());
            } catch (NacosException e) {
                throw new IllegalStateException("Registry service to nacos error|service=" + path, e);
            }
        });

    }


    @Override
    public void subscribe(String path, NodeListener nodeListener) {
        assert path != null && path.trim().length() > 0;
        assert nodeListener != null;
        checkServerStatus();
        try {
            namingService.subscribe(path, new EventListener() {
                @Override
                public void onEvent(Event event) {
                    if (event instanceof NamingEvent) {
                        NamingEvent namingEvent = (NamingEvent) event;
                        List<InetSocketAddress> servers = new ArrayList<>(namingEvent.getInstances().size());
                        namingEvent.getInstances().stream().filter(instance -> {
                            return instance.isEnabled();
                        }).forEach((value) -> {
                            servers.add(new InetSocketAddress(value.getIp(), value.getPort()));
                        });
                        nodeListener.onChange(servers);
                    }
                }
            });
        } catch (NacosException e) {
            throw new IllegalStateException("Subscribe service from nacos error|service=" + path, e);
        }
    }


    @Override
    public void unsubscribe(InetSocketAddress address, String path) {
        assert path != null && path.trim().length() > 0;
        assert address != null;
        checkServerStatus();
        try {
            namingService.deregisterInstance(path, address.getHostString(), address.getPort());
        } catch (NacosException e) {
            throw new IllegalStateException("Unsubscribe service from nacos error|service=" + path, e);
        }
    }


    private void checkServerStatus() {
        if (namingService == null || !"UP".equals(namingService.getServerStatus())) {
            throw new IllegalStateException("Nacos registry not available!");
        }
    }

}
