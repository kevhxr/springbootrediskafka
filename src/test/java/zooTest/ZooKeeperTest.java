package zooTest;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZooKeeperTest {
    private static final int SESSION_TIMEOUT = 30000;

    private static final Logger logger = LoggerFactory.getLogger(ZooKeeperTest.class);

    private Watcher watcher = watchedEvent -> logger.info("process: " + watchedEvent.getType());

    private ZooKeeper zooKeeper;

    @Before
    public void connect() throws Exception {
        //30 seconds lose
        zooKeeper = new ZooKeeper("node01:2181,node02:2181,node03:2181,node04:2181", SESSION_TIMEOUT, watcher);
    }

    @After
    public void close() {
        try {
            zooKeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreate() {
        String result = null;
        try {
            //result = zooKeeper.create("/zk001", "zk001-e".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            result = zooKeeper.create("/zk001", "zk001-e".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            Thread.sleep(10000);
            //Session: 0x36bc4fc6a5b0001 closed after 10 seconds, node disappear,    because execute after to remove session
        } catch (Exception e) {
            logger.error(e.getMessage());
            Assert.fail();
        }
        logger.info("create result {}", result);
    }

    @Test
    public void testDelete() {
        try {
            zooKeeper.delete("/zk001", -1);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testGetData() {
        String result = null;
        try {
            byte[] data = zooKeeper.getData("/sxt", null, null);
            result = new String(data);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        logger.info("get result------- {}", result);
    }

    @Test
    public void testGetData01() throws Exception {
        String result = null;
        try {
            byte[] data = zooKeeper.getData("/sxt", null, null);
            result = new String(data);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        logger.info("get result-----1------ {}", result);


        Thread.sleep(20000);
        byte[] data;
        try {
            data = zooKeeper.getData("/sxt", null, null);
            result = new String(data);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        logger.info("get result----2-----   --- {}", result);
    }

    @Test
    public void testGetDataWatch() throws Exception {
        String result = null;
        try {
            //watcher only watch once, it will be destroyed once it been executed
            byte[] data = zooKeeper.getData("/sxt", new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    logger.info("testGetDatWatch watch: {}", watchedEvent.getType());
                    System.out.println("watcher ok");
                }
            }, null);
            result = new String(data);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        logger.info("get result-----------1------------ {}", result);

        try {
            System.out.println("set abbbc");
            zooKeeper.setData("/sxt", "abbbc".getBytes(), -1);
            System.out.println("set abbbc1");
            zooKeeper.setData("/sxt", "abbbc1".getBytes(), -1);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }


    @Test
    public void testExists() {
        Stat stat = null;
        try {
            stat = zooKeeper.exists("/sxt", false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        logger.info("get result------- {}", stat.getCzxid());
    }




}
