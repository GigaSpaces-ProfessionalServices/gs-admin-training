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

import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.CannotFindSpaceException;
import org.openspaces.core.space.EmbeddedSpaceConfigurer;
import org.openspaces.core.space.SpaceProxyConfigurer;

import java.nio.charset.StandardCharsets;

public class Program {
    private static final String spaceName = "demo";
    private static final long numObjects = 10000;

    public static void main(String[] args) {
        GigaSpace gigaSpace = getSpace(args.length == 0 ? null : args[0]);
        System.out.println("Connected to space: " + gigaSpace.getName());

        // Your code goes here, for example:
        System.out.println("Entries in space: " + gigaSpace.count(null));
        System.out.println("About to write " + numObjects + " to the space.");
        for (long i=0; i < numObjects; i++) {
            MessagePOJO messagePojo = new MessagePOJO();
            messagePojo.setCounter(i);
            byte[] content = String.valueOf(i).getBytes(StandardCharsets.UTF_8);
            messagePojo.setContent(content);
            gigaSpace.write(messagePojo);
        }
        System.out.println("Program completed successfully.");
        System.exit(0);
    }

    public static GigaSpace getSpace(String spaceName) {
        if (spaceName == null) {
            spaceName = Program.spaceName;
            System.out.println("Space name not provided - using default space name '" + spaceName + "'...");
        }
        System.out.printf("Connecting to space %s...%n", spaceName);
        try {
            return new GigaSpaceConfigurer(new SpaceProxyConfigurer(spaceName)).create();
        } catch (CannotFindSpaceException e) {
            System.err.println("Failed to find space: " + e.getMessage());
            throw e;
        }
    }
}
