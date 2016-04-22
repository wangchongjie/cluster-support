package com.baidu.unbiz.multiengine.cluster.endpoint.supervisor;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;

import com.baidu.unbiz.multiengine.cluster.constants.ClusterConstants;
import com.baidu.unbiz.multiengine.cluster.zk.client.ZKClient;
import com.baidu.unbiz.multiengine.endpoint.EndpointPool;
import com.baidu.unbiz.multiengine.endpoint.HostConf;
import com.baidu.unbiz.multiengine.endpoint.supervisor.EndpointSupervisor;
import com.baidu.unbiz.multiengine.transport.server.TaskServer;
import com.baidu.unbiz.multiengine.transport.server.TaskServerFactory;
import com.baidu.unbiz.multitask.log.AopLogFactory;

/**
 * Created by wangchongjie on 16/4/15.
 */
public class ClusterEndpointSupervisor implements EndpointSupervisor {
    private static final Logger LOG = AopLogFactory.getLogger(ClusterEndpointSupervisor.class);

    private static List<TaskServer> taskServers;

    private String exportPort;

    @Resource(name = "ZKClient")
    private ZKClient zkClient;

    public ClusterEndpointSupervisor() {}

    public ClusterEndpointSupervisor(ZKClient zkClient) {
        this.zkClient = zkClient;
    }

    private List<HostConf> getRemoteHosts() {
        List<String> endpoints = zkClient.getChildren(ClusterConstants.ZK_BASE_PATH);
        System.out.println(endpoints);

        List<HostConf> hostConfs = new ArrayList<HostConf>();
        for (String endpoint : endpoints){
            hostConfs.addAll(HostConf.resolveHost(endpoint));
        }
        return hostConfs;
    }

    private void reportSelfServerHost() {
        List<HostConf> exportHosts = HostConf.resolvePort(exportPort);
        for (HostConf host : exportHosts) {
            StringBuilder path = new StringBuilder();
            path.append(ClusterConstants.ZK_BASE_PATH).append("/").append(host.info());
            zkClient.setEphemeralData(path.toString(), "ok".getBytes());
        }
    }

    @Override
    public void init() {
        taskServers = new ArrayList<TaskServer>();
        List<HostConf> exportHosts = HostConf.resolvePort(exportPort);
        for (HostConf hostConf : exportHosts) {
            TaskServer taskServer = TaskServerFactory.createTaskServer(hostConf);
            taskServer.start();
            reportSelfServerHost();
            taskServers.add(taskServer);
        }
        List<HostConf> clientHost = getRemoteHosts();
        EndpointPool.init(clientHost);
    }

    @Override
    public void stop() {
        EndpointPool.stop();
        if (CollectionUtils.isEmpty(taskServers)) {
            return;
        }
        for (TaskServer taskServer : taskServers) {
            taskServer.stop();
        }

    }

    public String getExportPort() {
        return exportPort;
    }

    public void setExportPort(String exportPort) {
        this.exportPort = exportPort;
    }
}
