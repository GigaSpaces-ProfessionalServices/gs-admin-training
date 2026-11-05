package com.gigaspaces.ui.demo.controllers;

import com.gigaspaces.annotation.SupportCodeChange;
import com.gigaspaces.common.Bundle;
import com.gigaspaces.internal.jvm.JVMHelper;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.executor.Task;
import org.openspaces.core.executor.TaskGigaSpace;
import org.openspaces.core.executor.TaskRoutingProvider;
import org.springframework.dao.DataAccessException;


import java.lang.management.ManagementFactory;

import static com.gigaspaces.common.Constants.*;

@SupportCodeChange(id = "1")
public class MemoryAlertTask implements Task<Integer>, TaskRoutingProvider {
    private int routing;
    private int duration;

    @TaskGigaSpace
    private transient GigaSpace gigaSpace;

    public MemoryAlertTask(int routing, int duration) {
        this.routing = routing;
        this.duration = duration;
    }

    public Integer execute() {
        doMemoryAlert();

        return 0;
    }

    public Integer getRouting() {
        return this.routing;
    }

    private void doMemoryAlert() {
        long startTime = System.currentTimeMillis();
        long currentTime = 0;
        long maxMemory = JVMHelper.getDetails().getMemoryHeapMax();
        System.out.println("Start writing Bundles to space");

        try{
            while (((currentTime - startTime) < duration * MILLISECONDS_IN_SECOND)) {
                long usedMemory = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
                double usedMemoryPercentage = (double) usedMemory / maxMemory;

                if (usedMemoryPercentage < 0.75){
                    Bundle[] bundles = new Bundle[NUM_OF_BUNDLES_TO_WRITE];
                    for (int i = 0; i < NUM_OF_BUNDLES_TO_WRITE ; i++) {
                        bundles[i] = Bundle.createBundle();
                    }
                    gigaSpace.writeMultiple(bundles);
                    currentTime = System.currentTimeMillis();
                }
                else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        System.out.println("Failed with error: " + e);
                    }
                }
            }

            int count = gigaSpace.count(new Bundle());
            System.out.println("Finish writing " + count + " Bundles");
        } catch (DataAccessException e) {
            System.out.println("Failed with error: " + e);
        } finally {
            gigaSpace.clear(new Bundle());
            int countAfterClear = gigaSpace.count(new Bundle());
            System.out.println("After clear: there are " + countAfterClear + " Bundles");
        }
    }
}

