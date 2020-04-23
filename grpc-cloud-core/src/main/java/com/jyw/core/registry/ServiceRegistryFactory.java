package com.jyw.core.registry;

import java.net.URI;

public interface ServiceRegistryFactory {

    boolean support(URI uri);

    ServiceRegistry create(URI uri);

}
