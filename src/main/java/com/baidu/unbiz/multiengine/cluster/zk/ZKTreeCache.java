package com.baidu.unbiz.multiengine.cluster.zk;

import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;

import com.baidu.unbiz.multiengine.cluster.utils.CustomizableExecutors;

/**
 * ZK路径监控的 TreeCache
 */
public class ZKTreeCache {

    protected TreeCache cache;

    protected CuratorFramework curator;

    public ZKTreeCache(CuratorFramework curator, String path) {
        this.curator = curator;
        this.cache = TreeCache.newBuilder(curator, path)
                .setCacheData(false)
                .setDataIsCompressed(false)
                .setExecutor(CustomizableExecutors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
                        getClass().getSimpleName()))
                .setMaxDepth(Short.MAX_VALUE)
                .build();

    }

    public void start() throws Exception {
        curator.blockUntilConnected();
        cache.start();
    }

    public void stop() throws Exception {
        cache.close();
    }

    public void addListener(TreeCacheListener listener) {
        cache.getListenable().addListener(listener);
    }

    public void removeListener(TreeCacheListener listener) {
        cache.getListenable().removeListener(listener);
    }

    public Map<String, ChildData> getCurrentChildren(String fullPath) {
        return cache.getCurrentChildren(fullPath);
    }

    public ChildData getCurrentData(String fullPath) {
        return cache.getCurrentData(fullPath);
    }
}
