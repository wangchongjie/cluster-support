package com.baidu.unbiz.multiengine.cluster.endpoint.status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EndpointStatusListener implements TreeCacheListener {
    private static Logger logger = LoggerFactory.getLogger(EndpointStatusListener.class);
    private static Pattern pattern =
            Pattern.compile("/SERVICE_TOPOLOGY/\\d+/(\\d+)\\.(.+)_(\\d+)\\.(.+)/endpoints/(.*):(\\d+)/statics");

    @Resource(name = "zkEndpointCache")
    private ZKEndpointCache cache;

    @PostConstruct
    public void start() throws Exception {
        cache.addListener(this);
    }

    @Override
    public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
        if (event.getData() == null) {
            return;
        }

        logger.trace("endpoint:[{}] trigger statistic change event:[{}]", event.getData().getPath(), event.getType());
        retrieve(event.getData().getPath());
    }

    private void retrieve(String path) {
        System.out.println(path);
        Matcher matcher = pattern.matcher(path);
        if (!matcher.find()) {
            return;
        }

        int provider = Integer.parseInt(matcher.group(1));
        String service = matcher.group(2);
        int version = Integer.parseInt(matcher.group(3));
        String method = matcher.group(4);
        String host = matcher.group(5);
        int port = Integer.parseInt(matcher.group(6));
    }

}
