package com.gigaspaces.ui.demo.controllers;

import com.gigaspaces.annotation.SupportCodeChange;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.executor.Task;
import org.openspaces.core.executor.TaskGigaSpace;
import org.openspaces.core.executor.TaskRoutingProvider;
import static java.lang.Math.atan;
import static java.lang.StrictMath.tan;

import static com.gigaspaces.common.Constants.*;

@SupportCodeChange(id="1")
public class CPUAlertTask implements Task<Integer>, TaskRoutingProvider {
    private int routing;
    private int durationInSeconds;

    @TaskGigaSpace
    private transient GigaSpace gigaSpace;


    public CPUAlertTask(int routing, int durationInSeconds) {
        this.routing = routing;
        this.durationInSeconds = durationInSeconds;
    }

    public Integer execute() {
        System.out.println("CPU: execute alert");
        long startTime = System.currentTimeMillis();

        while ((System.currentTimeMillis() - startTime)  < durationInSeconds * MILLISECONDS_IN_SECOND) {
            for (int i = 0; i < 100_000; i++) {
                double d = tan(atan(tan(atan(tan(atan(tan(atan(tan(atan(123456789.123456789))))))))));
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public Integer getRouting() {
        return this.routing;
    }
}
