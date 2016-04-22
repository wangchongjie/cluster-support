package com.baidu.unbiz.multiengine.cluster.zookeeper;

import java.io.IOException;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.apache.zookeeper.server.quorum.QuorumPeerMain;

/**
 * Created by pippo on 16/4/19.
 */
public class ZKClusterNode extends QuorumPeerMain implements ZKNode {

    public ZKClusterNode(QuorumPeerConfig config) {
        this.config = config;
    }

    private QuorumPeerConfig config;

    @Override
    public void start() throws IOException {
        runFromConfig(config);
    }

    @Override
    public void stop() {
        quorumPeer.shutdown();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("config", ToStringBuilder.reflectionToString(config))
                .toString();
    }
}
