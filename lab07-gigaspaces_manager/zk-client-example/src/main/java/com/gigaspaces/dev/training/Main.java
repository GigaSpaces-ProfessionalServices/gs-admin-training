package com.gigaspaces.dev.training;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static String ZOOKEEPER_HOST = "localhost:2181";
    private static final int SESSION_TIMEOUT = 5000; // Session timeout in milliseconds

    private ZooKeeper zk;
    private CountDownLatch connectionLatch = new CountDownLatch(1);

    public void connect() throws IOException, InterruptedException {
        zk = new ZooKeeper(ZOOKEEPER_HOST, SESSION_TIMEOUT, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    connectionLatch.countDown();
                }
            }
        });
        connectionLatch.await(); // Wait for connection to establish
        logger.info("Connected to ZooKeeper.");
    }

    public void close() throws InterruptedException {
        if (zk != null) {
            zk.close();
            logger.info("Disconnected from ZooKeeper.");
        }
    }

    public void traverseAndGetData(String path) throws KeeperException, InterruptedException {
        logger.info("Traversing Znode: " + path);
        try {
            // Get children of the current path
            List<String> children = zk.getChildren(path, false);

            if (children.isEmpty()) {
                logger.info("  No children found for: " + path);
            } else {
                for (String child : children) {
                    String childPath = path.equals("/") ? "/" + child : path + "/" + child;
                    logger.info("  Child Znode: " + childPath);

                    // Get data of the child znode
                    byte[] data = zk.getData(childPath, false, null);
                    if (data != null && data.length > 0) {
                        logger.info("    Data: " + new String(data));
                    } else {
                        logger.info("    No data found for: " + childPath);
                    }

                    // Recursively traverse children
                    traverseAndGetData(childPath);
                }
            }
        } catch (KeeperException.NoNodeException e) {
            logger.info("Znode does not exist: " + path);
        }
    }

    public static void main(String[] args) {
        System.out.println("Data in Zookeeper is organized similar to a file directory system.");
        System.out.println("This is a utility program that will traverse Zookeeper nodes.");
        System.out.println("It takes as an argument a zookeeper hostname.");
        System.out.println("If an argument is not provided it will use 'localhost' (In the absence of a port, Zookeeper assumes the default port 2181 is being used.");
        System.out.println();

        if (args != null && args.length >= 1) {
            ZOOKEEPER_HOST = args[0];
        }

        Main client = new Main();
        try {
            client.connect();
            // Start traversal from the root node or a specific path
            client.traverseAndGetData("/");
        } catch (IOException | InterruptedException | KeeperException e) {
            logger.info(e.getMessage(), e);
        } finally {
            try {
                client.close();
            } catch (InterruptedException e) {
                logger.info(e.getMessage(), e);
            }
        }
    }
}
