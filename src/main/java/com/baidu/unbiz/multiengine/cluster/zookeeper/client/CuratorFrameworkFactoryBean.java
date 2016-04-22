package com.baidu.unbiz.multiengine.cluster.zookeeper.client;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryForever;
import org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CuratorFrameworkFactoryBean {

    private static Logger logger = LoggerFactory.getLogger(CuratorFrameworkFactoryBean.class);

    @Resource
    private QuorumPeerConfig config;

    private CuratorFramework client;

    @PostConstruct
    public void init() {

        StringBuilder connectString = new StringBuilder();

        if (config.getServers().isEmpty()) {
            /* stand along */
            connectString.append(config.getClientPortAddress().getHostName())
                    .append(":")
                    .append(config.getClientPortAddress().getPort());
        } else {
            /* cluster */
            for (QuorumServer server : config.getServers().values()) {
                connectString.append(server.addr.getAddress().getHostName())
                        .append(":")
                        .append(config.getClientPortAddress().getPort())
                        .append(",");
            }
        }

        client = CuratorFrameworkFactory.builder()
                .connectString(connectString.toString())
                .connectionTimeoutMs(1000 * 30)
                .maxCloseWaitMs(1000)
                .retryPolicy(new RetryForever(1000))
                .sessionTimeoutMs(1000 * 60 * 5)
                .build();

        client.start();
    }

    @PreDestroy
    public void destroy() {
        if (client != null) {
            client.close();
        }
    }

    @Bean(name = "curatorFramework")
    public CuratorFramework curatorFramework() {
        return client;
    }

}
