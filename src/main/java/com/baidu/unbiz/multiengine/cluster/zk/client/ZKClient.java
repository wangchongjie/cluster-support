package com.baidu.unbiz.multiengine.cluster.zk.client;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.framework.recipes.nodes.PersistentEphemeralNode;
import org.apache.zookeeper.KeeperException.Code;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.baidu.unbiz.multiengine.cluster.utils.JSONUtil;

/**
 * Created by wangchongjie on 2016/4/21.
 */
@Component
public class ZKClient {

    private static Logger logger = LoggerFactory.getLogger(ZKClient.class);

    private static int DEFAULT_TIMEOUT = 30;
    private boolean started = false;

    @PostConstruct
    public void init() {
        started = true;
        check();
    }

    @PreDestroy
    public void destroy() {
        started = false;
    }

    public boolean check() {
        if (!started) {
            return false;
        }

        try {
            curator.blockUntilConnected();
        } catch (InterruptedException e) {
            logger.error("connected to zk due to error", e);
            return false;
        }

        return true;
    }

    @Resource
    protected CuratorFramework curator;

    public CuratorFramework getCurator() {
        return curator;
    }

    public void setEphemeralData(final String path, final byte[] data) {
        if (!check()) {
            return;
        }
        PersistentEphemeralNode node =
                new PersistentEphemeralNode(curator, PersistentEphemeralNode.Mode.EPHEMERAL, path, data);
        node.start();
        try {
            node.waitForInitialCreate(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // do nothing
        }
    }

    public void setData(final String path, final byte[] data) {
        if (!check()) {
            return;
        }

        final BlockCallback<Void> callback = new BlockCallback<Void>();
        try {
            curator.checkExists().inBackground(new BackgroundCallback() {

                @Override
                public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                    boolean exist =
                            event.getType() == CuratorEventType.EXISTS && event.getResultCode() == Code.OK.intValue();

                    try {
                        if (!exist) {
                            client.create().creatingParentsIfNeeded().forPath(path);
                        }
                    } catch (Exception e) {
                        logger.warn("create path due to error[can be ignore]", e);
                    }

                    try {
                        client.setData().forPath(path, data);
                        callback.release();
                    } catch (Throwable e) {
                        callback.error(String.format("set date for path:[%s] due to error", path), e);
                    }
                }

            }).forPath(path);
        } catch (Throwable e) {
            callback.error(String.format("set date for path:[%s] due to error", path), e);
        }

        callback.get(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
    }

    public byte[] getData(final String path) {
        if (!check()) {
            return null;
        }

        final BlockCallback<byte[]> callback = new BlockCallback<byte[]>();
        try {
            curator.checkExists().inBackground(new BackgroundCallback() {

                @Override
                public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                    try {
                        boolean exist = event.getType() == CuratorEventType.EXISTS
                                && event.getResultCode() == Code.OK.intValue();
                        callback.release(exist ? client.getData().forPath(path) : null);
                    } catch (Throwable e) {
                        callback.error(String.format("get date for path:[%s] due to error", path), e);
                    }
                }
            }).forPath(path);

        } catch (Throwable e) {
            callback.error(String.format("get date for path:[%s] due to error", path), e);
        }

        return callback.get(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
    }

    public void setJson(String path, Object data) {
        setData(path, JSONUtil.toBytes(data));
    }

    public <T> T getJson(String path, Class<T> clazz) {
        byte[] data = getData(path);
        return data != null ? JSONUtil.toObject(data, clazz) : null;
    }

    public List<String> getChildren(final String path) {
        if (!check()) {
            return Collections.emptyList();
        }

        final BlockCallback<List<String>> callback = new BlockCallback<List<String>>();
        try {
            curator.checkExists().inBackground(new BackgroundCallback() {
                @Override
                public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                    /* 如果同步失败那么返回空 */
                    List<String> children = Collections.emptyList();
                    try {
                        boolean exist = event.getType() == CuratorEventType.EXISTS
                                && event.getResultCode() == Code.OK.intValue();

                        callback.release(exist ? curator.getChildren().forPath(path) : Collections.<String>emptyList());
                    } catch (Throwable e) {
                        callback.result = Collections.emptyList();
                        callback.error(String.format("get children for path:[%s] due to error", path), e);
                    }
                }
            }).forPath(path);
        } catch (Throwable e) {
            callback.error(String.format("get children for path:[%s] due to error", path), e);
        }

        return callback.get(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
    }

    public void delete(final String path) {
        if (!check()) {
            return;
        }

        final BlockCallback<Void> callback = new BlockCallback<Void>();
        try {
            curator.checkExists().inBackground(new BackgroundCallback() {

                @Override
                public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                    try {
                        boolean exist = event.getType() == CuratorEventType.EXISTS
                                && event.getResultCode() == Code.OK.intValue();

                        if (exist) {
                            client.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
                        }
                        callback.release();
                    } catch (Throwable e) {
                        callback.error(String.format("delete for path:[%s] due to error", path), e);
                    }
                }

            }).forPath(path);
        } catch (Throwable e) {
            callback.error(String.format("delete for path:[%s] due to error", path), e);
        }

        callback.get(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
    }

    private class BlockCallback<T> {
        Semaphore monitor = new Semaphore(0);
        T result = null;

        void error(String message, Throwable throwable) {
            logger.error(message, throwable);
            monitor.release();
        }

        void release() {
            monitor.release();
        }

        void release(T result) {
            this.result = result;
            monitor.release();
        }

        T get() {
            monitor.tryAcquire();
            return result;
        }

        T get(long timeout, TimeUnit unit) {
            try {
                monitor.tryAcquire(timeout, unit);
            } catch (InterruptedException e) {
                // do nothing
            }
            return result;
        }
    }
}
