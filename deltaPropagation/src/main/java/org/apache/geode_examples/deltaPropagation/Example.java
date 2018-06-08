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

public class Example {
  public final String KEY = "entry";

  public static void main(String[] args) {
    // connect to the locator using default port 10334
    ClientCache cache = new ClientCacheFactory().addPoolLocator("127.0.0.1", 10334)
        .set("log-level", "WARN").create();

    Example example = new Example();

    // create a local region that matches the server region
    ClientRegionFactory<String, ValueHolder> clientRegionFactory =
        cache.createClientRegionFactory(ClientRegionShortcut.PROXY);
    Region<String, ValueHolder> region = clientRegionFactory.create("example-region");

    ValueHolder valueHolder = example.putEntry(region);

    example.changeEntry(region, valueHolder);

    cache.close();
  }


  public ValueHolder putEntry(Region<String, ValueHolder> region) {
    ValueHolder valueHolder = new ValueHolder();
    region.put(KEY, valueHolder);
    return valueHolder;
  }

  public void changeEntry(Region<String, ValueHolder> region, ValueHolder valueHolder) {
    valueHolder.setInt(1);
    valueHolder.setString("abc");
    region.put(KEY, valueHolder);
  }
}
