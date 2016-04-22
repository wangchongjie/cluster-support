package com.baidu.unbiz.multiengine.cluster.zookeeper;

import java.io.IOException;

public interface ZKNode {

    void start() throws IOException;

    void stop();

}
