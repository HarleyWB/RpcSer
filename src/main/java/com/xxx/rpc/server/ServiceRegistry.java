package com.xxx.rpc.server;

import com.xxx.rpc.sample.api.Constant;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);

    private CountDownLatch latch = new CountDownLatch(1);

    private String registryAddress;

    public ServiceRegistry(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public void register(String ipPort) {//发布服务的ip  端口 127.0.0.1:8080
        if (ipPort != null) {
            ZooKeeper zk = connectServer();//创建zookeeper客户端
            if (zk != null) {
                createNode(zk, ipPort);//data 发布服务的ip 端口
            }
        }
    }

    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();//连接本地zookeeper服务成功 则放开栅栏
                    }
                }
            });
            latch.await();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("", e);
        }

        return zk;
    }

    /**
     * @param zk
     * @param ipPort 发布服务的ip 端口127.0.0.1:8080
     */
    private void createNode(ZooKeeper zk, String ipPort) {

        try {
            byte[] bytes = ipPort.getBytes();
            Stat s = zk.exists(Constant.ZK_REGISTRY_PATH, false);
            if (s == null) {
                zk.create(Constant.ZK_REGISTRY_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.PERSISTENT);
            }

            String path = zk.create(Constant.ZK_DATA_PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL);

            LOGGER.debug("create zookeeper node ({} => {})", path, ipPort);
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("", e);
        }
    }
}