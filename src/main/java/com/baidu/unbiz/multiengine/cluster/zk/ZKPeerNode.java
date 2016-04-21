package com.baidu.unbiz.multiengine.cluster.zk;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.apache.zookeeper.server.quorum.QuorumPeerMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

public class ZKPeerNode extends QuorumPeerMain {

    private static Logger logger = LoggerFactory.getLogger(ZKPeerNode.class);

    @Resource
    private QuorumPeerConfig config;

    private volatile boolean running = false;
    private ZKNode delegate;
    private ExecutorService executor = Executors.newSingleThreadExecutor(new CustomizableThreadFactory("zk"));

    @PostConstruct
    public void start() {
        running = true;
        delegate = config.getServers().isEmpty() ? new ZKStandaloneNode(config) : new ZKClusterNode(config);
        executor.execute(new Daemon());
        logger.info("start the zk server node:[{}]", delegate);
    }

    @PreDestroy
    public void stop() {
        logger.info("stop the zk server node:[{}]", delegate);
        running = false;
        delegate.stop();
        executor.shutdown();
    }

    public void setConfig(QuorumPeerConfig config) {
        this.config = config;
    }

    public QuorumPeerConfig loadConfig() {
        try {
            QuorumPeerConfig config = new QuorumPeerConfig();
            Properties properties = new Properties();
//            properties.load(ZKPeerNodeInitializer.class.getResourceAsStream("/zoo.cfg"));
            config.parseProperties(properties);
            return config;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private class Daemon implements Runnable {

        @Override
        public void run() {

            while (running) {
                try {
                    delegate.start();
                } catch (Throwable e) {
                    logger.error("zk node due to error,the node would be stop", e);

                    // 如果是持续运行的状态,那么先关闭原有的node
                    if (running) {
                        delegate.stop();
                    }
                }

                try {
                    Thread.sleep(1000 * 60 * 2);
                } catch (InterruptedException e) {
                    // quite
                }
            }
        }

    }

    public static void main(String[] args) {
        ZKPeerNode peerNode = new ZKPeerNode();
        peerNode.setConfig(peerNode.loadConfig());
        peerNode.start();
    }

}
