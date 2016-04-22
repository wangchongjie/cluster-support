package com.baidu.unbiz.multiengine.cluster.endpoint.listener;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.baidu.unbiz.multiengine.cluster.constants.ClusterConstants;
import com.baidu.unbiz.multiengine.endpoint.EndpointPool;
import com.baidu.unbiz.multiengine.endpoint.HostConf;

@Component
public class EndpointStatusListener implements TreeCacheListener {
    private static Logger logger = LoggerFactory.getLogger(EndpointStatusListener.class);

    private static Pattern pattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+):(\\d+)");

    private String findHostConf(String path){
        Matcher matcher = pattern.matcher(path);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

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

        ChildData data = event.getData();
        String path = data.getPath();
        if (! path.startsWith(ClusterConstants.ZK_BASE_PATH)) {
            return;
        }
        String endpoint = findHostConf(path);
        List<HostConf> hosts = HostConf.resolveHost(endpoint);

        if (TreeCacheEvent.Type.NODE_ADDED == event.getType()) {
            EndpointPool.add(hosts);
        }
        if (TreeCacheEvent.Type.NODE_REMOVED == event.getType()) {
            EndpointPool.beInvalid(hosts);
        }
    }

}
