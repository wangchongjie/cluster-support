package com.baidu.unbiz.multiengine.cluster.zookeeper;

import java.io.IOException;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;

public class ZKStandaloneNode extends ZooKeeperServerMain implements ZKNode {

    public ZKStandaloneNode(QuorumPeerConfig config) {
        this.config = config;
    }

    private QuorumPeerConfig config;

    @Override
    public void start() throws IOException {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.readFrom(config);
        runFromConfig(serverConfig);
    }

    @Override
    public void stop() {
        shutdown();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("config", ToStringBuilder.reflectionToString(config))
                .toString();
    }
}
