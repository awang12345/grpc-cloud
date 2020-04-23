package com.jyw.core.utils;

import com.jyw.core.GrpcConfig;

public class GrpcUtils {

    public static String buildServiceNodePath(String serviceName) {
        return GrpcConfig.getInstance().getEnv() + "-" + serviceName;
    }

}
