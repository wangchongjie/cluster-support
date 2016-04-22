package com.baidu.unbiz.multiengine.cluster.demo.test;

import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.baidu.unbiz.multiengine.cluster.endpoint.supervisor.ClusterEndpointSupervisor;
import com.baidu.unbiz.multiengine.cluster.utils.TestUtils;
import com.baidu.unbiz.multiengine.cluster.vo.DeviceRequest;
import com.baidu.unbiz.multiengine.cluster.vo.DeviceViewItem;
import com.baidu.unbiz.multiengine.cluster.vo.QueryParam;
import com.baidu.unbiz.multiengine.cluster.zk.client.ZKClient;
import com.baidu.unbiz.multiengine.common.DisTaskPair;
import com.baidu.unbiz.multitask.common.TaskPair;
import com.baidu.unbiz.multitask.task.ParallelExePool;
import com.baidu.unbiz.multitask.task.thread.MultiResult;

/**
 * Created by wangchongjie on 16/4/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test2.xml")
public class TestMultiProcess_Client {

    @Resource(name = "distributedParallelExePool")
    private ParallelExePool parallelExePool;

    private ClusterEndpointSupervisor supervisor;

    @Resource(name = "ZKClient")
    private ZKClient zkClient;

    @Before
    public void init() {
        supervisor = new ClusterEndpointSupervisor(zkClient);
        supervisor.setExportPort("8805");
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
    public void runClient() {
        for (int i = 0; i < 1000; i++) {
            try {
                doRunTask();
            } catch (Exception e) {
                // doNothing
            }
            TestUtils.dumySleep(1000);
        }
        TestUtils.dumySleep(TestUtils.VERY_LONG_TIME);
    }

    private void doRunTask() {
        QueryParam qp = new QueryParam();
        MultiResult ctx =
                parallelExePool.submit(
                        new TaskPair("deviceUvFetcher", DeviceRequest.build(qp)),
                        new TaskPair("voidParamFetcher", null),
                        new DisTaskPair("deviceStatFetcher", DeviceRequest.build(qp)),
                        new DisTaskPair("deviceBigDataStatFetcher", DeviceRequest.build(qp)));

        List<DeviceViewItem> stat = ctx.getResult("deviceStatFetcher");
        List<DeviceViewItem> uv = ctx.getResult("deviceUvFetcher");
        List<DeviceViewItem> vstat = ctx.getResult("voidParamFetcher");
        List<DeviceViewItem> bstat = ctx.getResult("deviceBigDataStatFetcher");

        Assert.notEmpty(stat);
        Assert.notEmpty(uv);
        Assert.notEmpty(vstat);
        Assert.notEmpty(bstat);

        System.out.println(stat);
        System.out.println(uv);
        System.out.println(vstat);
        System.out.println(bstat);
    }

}
