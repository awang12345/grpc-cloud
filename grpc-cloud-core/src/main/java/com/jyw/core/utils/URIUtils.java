package com.jyw.core.utils;

import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class URIUtils {

    private URIUtils() {
    }


    public static Map<String, String> getQueryParameter(URI uri) {
        if (uri == null || StringUtils.isBlank(uri.getRawQuery())) {
            return Collections.emptyMap();
        }
        String query = uri.getRawQuery();
        String[] kvs = StringUtils.split(query, '&');
        Map<String, String> parameter = new HashMap<>();
        for (String kv : kvs) {
            int idx = kv.indexOf('=');
            if (idx == -1) {
                continue;
            }
            parameter.put(kv.substring(0, idx), kv.substring(idx + 1));
        }
        return parameter;
    }

}
