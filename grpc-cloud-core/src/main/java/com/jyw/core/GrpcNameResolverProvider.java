package com.jyw.core;

import com.jyw.core.registry.ServiceRegistry;
import com.jyw.core.registry.support.ServiceRegistryProvider;
import com.jyw.core.utils.GrpcUtils;
import com.jyw.core.utils.URIUtils;
import io.grpc.*;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GrpcNameResolverProvider extends NameResolverProvider {

    private static final String DEFAULT_SCHEME = "nacos";


    @Override
    protected boolean isAvailable() {
        return true;
    }


    @Override
    protected int priority() {
        return 0;
    }


    @Nullable
    @Override
    public NameResolver newNameResolver(URI targetUri, Attributes params) {
        return new NacosNameResolver(targetUri, params);
    }


    @Override
    public String getDefaultScheme() {
        return DEFAULT_SCHEME;
    }


    private class NacosNameResolver extends NameResolver {

        private URI        targetUri;
        private Attributes params;


        public NacosNameResolver(URI targetUri, Attributes params) {
            this.targetUri = targetUri;
            this.params = params;
        }


        @Override
        public String getServiceAuthority() {
            return targetUri.getAuthority();
        }


        @Override
        public void start(final Listener listener) {
            URI registryURI = this.targetUri;
            String nodePath = generateServicePath(registryURI);
            ServiceRegistry serviceRegistry = ServiceRegistryProvider.getRegistry(registryURI);
            List<InetSocketAddress> serverAddressList = serviceRegistry.getServiceList(nodePath);
            onFireListener(listener, serverAddressList);
            serviceRegistry.subscribe(nodePath, serverList -> {
                onFireListener(listener, serverList);
            });
        }


        private String generateServicePath(URI registryURI) {
            Map<String, String> param = URIUtils.getQueryParameter(registryURI);
            String serviceName = param.get("serviceName");
            assert StringUtils.isNotBlank(serviceName);
            return GrpcUtils.buildServiceNodePath(serviceName);
        }


        private void onFireListener(Listener listener, List<InetSocketAddress> instanceList) {
            List<EquivalentAddressGroup> addressGroupList = new ArrayList<>(instanceList.size());
            instanceList.forEach((value) -> {
                addressGroupList.add(new EquivalentAddressGroup(value));
            });
            if (addressGroupList.isEmpty()) {
                listener.onError(Status.UNAVAILABLE);
            } else {
                listener.onAddresses(addressGroupList, this.params);
            }
        }


        @Override
        public void shutdown() {

        }
    }

}
