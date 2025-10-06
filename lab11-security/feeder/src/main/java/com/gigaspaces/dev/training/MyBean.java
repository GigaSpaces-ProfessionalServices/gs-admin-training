/*
 * Copyright (c) 2008-2016, GigaSpaces Technologies, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gigaspaces.dev.training;

import com.gigaspaces.client.WriteModifiers;
import com.gigaspaces.dev.training.common.Data;
import org.slf4j.*;
import jakarta.annotation.*;

import org.openspaces.core.*;

public class MyBean {
    private static final Logger logger = LoggerFactory.getLogger(MyBean.class);

    @Resource
    private GigaSpace gigaSpace;

    @PostConstruct
    public void initialize() {
        logger.info("Initialized: connected to space {}", gigaSpace.getSpaceName());
        // Your code goes here, for example:
        logger.info("MyFeeder is up");
        Data[] objs = new Data[10];

        for( int i=0; i < 10; i++ ) {
            Data data = new Data();
            data.setData(String.valueOf(i));
            data.setRawData(String.valueOf(i));
            data.setId((long)i);
            data.setProcessed(Boolean.FALSE);
            data.setType((long) i);
            objs[i] = data;
        }

        long leaseExpiry = 300000L;
        long timeout = 5000L;
        gigaSpace.writeMultiple(objs, leaseExpiry, timeout, WriteModifiers.UPDATE_OR_WRITE);    }

    @PreDestroy
    public void close() {
        logger.info("Closing");
    }
}
