package com.baidu.unbiz.multiengine.cluster.endpoint.status;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.baidu.unbiz.multiengine.cluster.zk.ZKTreeCache;

@Component("zkEndpointCache")
public class ZKEndpointCache extends ZKTreeCache {

    private static String ENDPOINT_STATUS = "/multi-engine/head";

    @Autowired
    public ZKEndpointCache(@Qualifier("curatorFramework") CuratorFramework curator) {
        super(curator, ENDPOINT_STATUS);
    }

    @PostConstruct
    public void init() throws Exception {
        super.start();
    }

    @PreDestroy
    private void destroy() throws Exception {
        super.stop();
    }

}
