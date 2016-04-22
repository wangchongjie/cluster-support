package com.baidu.unbiz.multiengine.cluster.demo.test;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baidu.unbiz.multiengine.cluster.endpoint.supervisor.ClusterEndpointSupervisor;
import com.baidu.unbiz.multiengine.cluster.utils.TestUtils;
import com.baidu.unbiz.multiengine.cluster.zookeeper.client.ZKClient;

/**
 * Created by wangchongjie on 16/4/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test2.xml")
public class TestMultiProcess_Server1 {

    @Resource(name = "ZKClient")
    private ZKClient zkClient;

    private ClusterEndpointSupervisor supervisor;

    @Before
    public void init() {
        supervisor = new ClusterEndpointSupervisor(zkClient);
        supervisor.setExportPort("8801");
        supervisor.init();
    }

    @After
    public void clean() {
        supervisor.stop();
    }

    /**
     * 测试分布式并行执行task
     */
    @Test
    public void runServer() {
        TestUtils.dumySleep(TestUtils.VERY_LONG_TIME);
    }

}
