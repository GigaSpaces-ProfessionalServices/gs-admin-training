/*
 * Copyright 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gigaspaces.dev.training.feeder;

import jakarta.annotation.Resource;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.SpaceInterruptedException;
import org.openspaces.core.context.GigaSpaceContext;
import com.gigaspaces.dev.training.common.Data;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A feeder bean started a scheduled task that writes a new Data object to the space.
 *
 * <p>The space is injected into this bean using OpenSpaces support for @GigaSpaceContext
 * annotation.
 *
 * <p>The scheduled support uses the java.util.concurrent Scheduled Executor Service. It is started
 * and stopped based on Spring lifecycle events.
 *
 * @author kimchy
 */
public class DataFeeder {

    private static final Logger logger = Logger.getLogger(DataFeeder.class.getName());

    private ScheduledExecutorService executorService;

    private ScheduledFuture<?> sf;

    private long numberOfTypes = 10;

    private long defaultDelay = 1000;

    private DataFeederTask dataFeederTask;

    private Long instanceId;

    private long startIdFrom = 0;

    @Resource
    @GigaSpaceContext(name = "gigaSpace")
    private GigaSpace gigaSpace;

    /**
     * Sets the number of types that will be used to set {@link com.gigaspaces.dev.training.common.Data#setType(Long)}.
     *
     * <p>The type is used as the routing index for partitioned space. This will affect the
     * distribution of Data objects over a partitioned space.
     */
    public void setNumberOfTypes(long numberOfTypes) {
        this.numberOfTypes = numberOfTypes;
    }

    public void setDefaultDelay(long defaultDelay) {
        this.defaultDelay = defaultDelay;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    @PostConstruct
    public void construct() {
        logger.log(Level.INFO, "--- STARTING FEEDER WITH CYCLE [" + defaultDelay + "]");
        if (instanceId != null) {
            // have a range of ids based on the instance id of the processing unit
            startIdFrom = instanceId * 100000000;
        }
        executorService = Executors.newScheduledThreadPool(1);
        dataFeederTask = new DataFeederTask();
        sf = executorService.scheduleAtFixedRate(dataFeederTask, defaultDelay, defaultDelay,
                TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void destroy() {
        sf.cancel(false);
        sf = null;
        executorService.shutdown();
    }

    public class DataFeederTask implements Runnable {

        private long counter = 1;

        public void run() {
            try {
                long time = System.currentTimeMillis();
                Data data = new Data((counter++ % numberOfTypes), "FEEDER " + Long.toString(time));
                data.setId(startIdFrom + counter);
                data.setProcessed(false);
                gigaSpace.write(data);
                logger.log(Level.INFO, "--- FEEDER WROTE " + data);
            } catch (SpaceInterruptedException e) {
                // ignore, we are being shutdown
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public long getCounter() {
            return counter;
        }
    }

    public long getFeedCount() {
        return dataFeederTask.getCounter();
    }
}
