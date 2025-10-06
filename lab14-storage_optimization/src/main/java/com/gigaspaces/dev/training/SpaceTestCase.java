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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openspaces.core.GigaSpace;

import java.io.IOException;
import java.util.Random;


public class SpaceTestCase {
    private GigaSpace gigaSpace;

    private enum StorageTypeExamples {NONE, BINARY_COMPRESSED_PROPS, DIRECT, SEQUENTIAL};



    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";
        StringBuilder result = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            result.append(characters.charAt(index));
        }
        return result.toString();
    }



    @Before
    public void before() {
        gigaSpace = Program.getOrCreateSpace("demo");
    }

    @Test
    public void testSpaceOperations() throws IOException {

        String testCase = System.getProperty("test.case");
        StorageTypeExamples storageTypeTestCase = null;
        if (testCase != null ) {
            storageTypeTestCase = StorageTypeExamples.valueOf(testCase);
        } else {
            storageTypeTestCase = StorageTypeExamples.NONE;
        }
        System.out.println("storageTypeTestCase is: " + storageTypeTestCase);

        gigaSpace.clear(new Object());
        System.out.println("Assert space is empty...");
        Assert.assertEquals(0, gigaSpace.count(null));

        for (int k = 0; k < 10000; k++) {
            String[] pl = {generateRandomString(20), generateRandomString(20), generateRandomString(20), generateRandomString(20), generateRandomString(20)};
            switch (storageTypeTestCase) {
                case BINARY_COMPRESSED_PROPS:
                    MyBigObjectPropertyStorageOpt myBigObjectPropertyStorageOpt = new MyBigObjectPropertyStorageOpt(k, "" + k, "" + k);
                    myBigObjectPropertyStorageOpt.setPayload(pl);
                    myBigObjectPropertyStorageOpt.setVals("test" + k);
                    gigaSpace.write(myBigObjectPropertyStorageOpt);
                    break;
                case DIRECT:
                    MyBigObjectDirect myBigObjectDirect = new MyBigObjectDirect(k, "" + k, "" + k);
                    myBigObjectDirect.setPayload(pl);
                    myBigObjectDirect.setVals("test" + k);
                    gigaSpace.write(myBigObjectDirect);
                    break;
                case SEQUENTIAL:
                    MyBigObjectSequential myBigObject158Sequential = new MyBigObjectSequential(k, "" + k, "" + k);
                    myBigObject158Sequential.setPayload(pl);
                    myBigObject158Sequential.setVals("test" + k);
                    gigaSpace.write(myBigObject158Sequential);
                    break;
                default: // NONE
                    MyBigObjectNoStorageOpt myBigObjectNoStorageOpt = new MyBigObjectNoStorageOpt(k, "" + k, "" + k);
                    myBigObjectNoStorageOpt.setPayload(pl);
                    myBigObjectNoStorageOpt.setVals("test" + k);
                    gigaSpace.write(myBigObjectNoStorageOpt);
                    break;
            }
        }
    }
}
