package com.baidu.unbiz.multiengine.cluster.zk;

import java.io.IOException;

public interface ZKNode {

    void start() throws IOException;

    void stop();

}
