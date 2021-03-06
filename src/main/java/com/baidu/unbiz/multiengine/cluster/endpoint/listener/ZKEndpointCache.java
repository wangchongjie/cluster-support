package com.baidu.unbiz.multiengine.cluster.endpoint.listener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.baidu.unbiz.multiengine.cluster.constants.ClusterConstants;
import com.baidu.unbiz.multiengine.cluster.zookeeper.ZKTreeCache;

@Component("zkEndpointCache")
public class ZKEndpointCache extends ZKTreeCache {

    @Autowired
    public ZKEndpointCache(@Qualifier("curatorFramework") CuratorFramework curator) {
        super(curator, ClusterConstants.ZK_BASE_PATH);
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
