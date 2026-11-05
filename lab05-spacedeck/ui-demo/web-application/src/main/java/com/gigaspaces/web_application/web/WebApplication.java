package com.gigaspaces.web_application.web;

import com.gigaspaces.common.Product;
import com.j_spaces.core.client.SQLQuery;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.context.GigaSpaceContext;
import org.openspaces.core.space.SpaceProxyConfigurer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;


import java.util.logging.Logger;

import static com.gigaspaces.common.Constants.*;

public class WebApplication implements InitializingBean, DisposableBean {
    Logger log = Logger.getLogger(this.getClass().getName());

    @GigaSpaceContext
    private GigaSpace gigaSpace;

    private int maxEntriesPerSecond;

    public WebApplication() {
    }

    public WebApplication(String spaceName, String groupName) {
        this.gigaSpace = new GigaSpaceConfigurer(new SpaceProxyConfigurer(spaceName)).gigaSpace();
    }


    @Override
    public void afterPropertiesSet() {
        new Thread(this::doQueries).start();
    }

    private void doQueries() {
        int count = 0;
        long startTime = System.currentTimeMillis();

        log.info("Max entries per second: " + maxEntriesPerSecond);
        while (true) {
            Product[] products = gigaSpace.readMultiple(new SQLQuery<>(Product.class, null), NUM_OF_ENTITIES_TO_READ);
            count += products.length;
            if(count >= maxEntriesPerSecond) {
                long differ = System.currentTimeMillis() - startTime;
                if(differ < 1000){
                    try {
                        Thread.sleep(1000 - differ);
                    } catch (InterruptedException e) {
                    }
                }
                count = 0;
                startTime = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void destroy() {
    }

    public int getMaxEntriesPerSecond() {
        return maxEntriesPerSecond;
    }

    public void setMaxEntriesPerSecond(int maxEntriesPerSecond) {
        this.maxEntriesPerSecond = maxEntriesPerSecond;
    }
}