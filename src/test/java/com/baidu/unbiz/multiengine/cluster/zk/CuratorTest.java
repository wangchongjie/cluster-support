package com.baidu.unbiz.multiengine.cluster.zk;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.nodes.PersistentEphemeralNode;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.junit.Test;

/**
 * Created by wangchongjie on 16/4/21.
 */
public class CuratorTest {

    private CuratorFramework buildClient() {
        String path = "/multi-engine";
        CuratorFramework client =
                CuratorFrameworkFactory.builder()
                        .connectString(
                                "m1-ocean-1774.epc.baidu.com:8701,cp01-ocean-1551.epc.baidu.com:8701,cp01-ocean-1004"
                                        + ".epc.baidu.com:8701,cp01-beidou-rd00.cp01:8701,cp01-beidou-rd01.cp01:8701")
                        .namespace("multi-engine")
                        .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000)).connectionTimeoutMs(5000).build();
        return client;
    }

    @Test
    public void testCurator() throws Exception {

        String path = "/multi-engine";
        CuratorFramework client = buildClient();

        // 启动 上面的namespace会作为一个最根的节点在使用时自动创建
        client.start();

        // 创建一个节点
        client.create().forPath("/head", new byte[0]);

        // 异步地删除一个节点
        // client.delete().inBackground().forPath("/head");

        // 创建一个临时节点
        client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/head/child", new byte[0]);

        // 取数据
        client.getData().watched().inBackground().forPath("/test");

        // 检查路径是否存在
        client.checkExists().forPath(path);

        // 异步删除
        client.delete().inBackground().forPath("/head");

        // 注册观察者，当节点变动时触发
        client.getData().usingWatcher(new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("node is changed");
            }
        }).inBackground().forPath("/test");

        // 结束使用
        client.close();
    }

    @Test
    public void testEphemeralNode() throws Exception {
        CuratorFramework client = buildClient();
        client.start();

        String PATH = "/head/temp";
        PersistentEphemeralNode node =
                new PersistentEphemeralNode(client, PersistentEphemeralNode.Mode.EPHEMERAL, PATH, "test".getBytes());
        node.start();
        node.waitForInitialCreate(3, TimeUnit.SECONDS);
        String actualPath = node.getActualPath();
        System.out.println("node " + actualPath + " value: " + new String(client.getData().forPath(actualPath)));
    }
}
