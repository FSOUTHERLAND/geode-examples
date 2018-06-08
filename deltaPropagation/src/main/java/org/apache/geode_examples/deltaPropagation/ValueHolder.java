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

import org.apache.geode.Delta;
import org.apache.geode.InvalidDeltaException;

import java.io.*;

public class ValueHolder implements Delta, Serializable {

  private int intVal;
  private String stringVal;

  private transient boolean intChanged = false;
  private transient boolean strChanged = false;

  public ValueHolder() {
    this.intVal = 0;
    this.stringVal = "";
  }


  @Override
  public boolean hasDelta() {
    return this.intChanged || this.strChanged;
  }

  @Override
  public void toDelta(DataOutput out) throws IOException {
    System.out.println("Extracting delta from " + this.toString());
    // Write information on what has changed to the
    // data stream, so fromDelta knows what it's getting
    out.writeBoolean(intChanged);
    if (intChanged) {
      // Write just the changes into the data stream
      out.writeInt(this.intVal);
      // Once the delta information is written, reset the delta status field
      this.intChanged = false;
      System.out.println(" Extracted delta from field 'intVal' = " + this.intVal);
    }
    out.writeBoolean(strChanged);
    if (strChanged) {
      out.writeChars(stringVal);
      this.strChanged = false;
      System.out.println(" Extracted delta from field 'stringVal' = " + this.stringVal);
    }
  }

  @Override
  public void fromDelta(DataInput in) throws IOException, InvalidDeltaException {
    System.out.println("Applying delta to " + this.toString());
    // For each field, read whether there is a change
    if (in.readBoolean()) {
      // Read the change and apply it to the object
      this.intVal = in.readInt();
      System.out.println(" Applied delta to field 'intVal' = " + this.intVal);
    }
    if (in.readBoolean()) {
      String newString = "";
      try {
        char nextChar = in.readChar();
        newString = newString + nextChar;
      } catch (EOFException ex) {
        this.stringVal = newString;
      }
      System.out.println(" Applied delta to field 'stringVal' = " + this.stringVal);
    }
  }

  public void setInt(int newVal) {
    this.intVal = newVal;
    this.intChanged = true;
  }

  public void setString(String newVal) {
    this.stringVal = newVal;
    this.strChanged = true;
  }

}
