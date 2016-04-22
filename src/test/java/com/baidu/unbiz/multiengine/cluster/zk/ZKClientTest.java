package com.baidu.unbiz.multiengine.cluster.zk;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.baidu.unbiz.multiengine.cluster.zk.client.ZKClient;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")
public class ZKClientTest {

    @Resource(name = "ZKClient")
    private ZKClient zkClient;

    @Test
    public void testZkCLient() {
        List<String> children = zkClient.getChildren("/");
        System.out.println(children);

        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSetEphemeralData() {
        String path = "/multi-engine/temp/test";
        zkClient.setEphemeralData(path, "testvalue".getBytes());
        System.out.println(new String(zkClient.getData(path)));

        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
