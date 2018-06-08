/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode_examples.deltaPropagation;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.jgroups.util.ByteArrayDataInputStream;
import org.jgroups.util.ByteArrayDataOutputStream;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Example {

  private Region<String, ValueHolder> region;
  private ClientCache cache;
  private static String[] STRING_VALS = {"abc", "def", "abcdef"};
  private static String KEY = "Example Entry";

  private void init() {
    // connect to the locator using default port 10334
    cache = new ClientCacheFactory().addPoolLocator("127.0.0.1", 10334).set("log-level", "WARN")
        .create();

    ClientRegionFactory<String, ValueHolder> clientRegionFactory =
        cache.createClientRegionFactory(ClientRegionShortcut.PROXY);

    region = clientRegionFactory.create("example-region");
  }

  private void run() throws InterruptedException, IOException {
    ValueHolder valueHolder = new ValueHolder();
    region.put(KEY, valueHolder);

    Thread.sleep(500);
    System.out.println("Putting again with no changes:");
    valueHolder = changeEntry(valueHolder, 0, "");

    Thread.sleep(500);
    System.out.println("Putting with changed int:");
    valueHolder = changeEntry(valueHolder, 1, "");

    Thread.sleep(500);
    System.out.println("Putting again with changed String:");
    valueHolder = changeEntry(valueHolder, 1, "Hello World");

    Thread.sleep(500);
    System.out.println("Putting again, changing both:");
    changeEntry(valueHolder, 2, "Goodbye");
  }

  private void close() {
    cache.close();
  }

  public static void main(String[] args) throws InterruptedException, IOException {
    Example example = new Example();

    example.init();

    example.run();

    example.close();
  }


  private ValueHolder changeEntry(ValueHolder valueHolder, int newInt, String newString)
      throws IOException {
    System.out.println("valueHolder = " + valueHolder.toString());

    valueHolder.setIntVal(newInt);

    valueHolder.setStrVal(newString);

    System.out.println("changed valueHolder = " + valueHolder.toString());
    System.out.println("Sending Changes to Server");

    DataOutput out = new ByteArrayDataOutputStream();
    ValueHolder newValueHolder = new ValueHolder();

    if (valueHolder.hasDelta()) {
      try {
        valueHolder.toDelta(out);
        DataInput in = new ByteArrayDataInputStream(((ByteArrayDataOutputStream) out).buffer());
        newValueHolder.fromDelta(in);
      } catch (Exception ex) {
        throw ex;
      }
    }

    region.put(KEY, valueHolder);

    System.out.println("\n");

    return newValueHolder;
  }
}
