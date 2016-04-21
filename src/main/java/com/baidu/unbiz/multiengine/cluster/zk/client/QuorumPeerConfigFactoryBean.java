package com.baidu.unbiz.multiengine.cluster.zk.client;

import java.util.Properties;

import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class QuorumPeerConfigFactoryBean {

    private QuorumPeerConfig config = new QuorumPeerConfig();

    @Bean(name = "zk.peer.config")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public QuorumPeerConfig peerConfig() throws Exception {

        Properties properties = new Properties();
        properties.load(QuorumPeerConfigFactoryBean.class.getResourceAsStream("/zoo.cfg"));
        config.parseProperties(properties);
        return config;
    }

}
