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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

public class ValueHolder implements Delta, Serializable {

  private int intVal;
  private transient boolean intChd = false;

  private String strVal;
  private transient boolean strChd = false;

  public ValueHolder() {
    this.intVal = 0;
    this.strVal = "";
  }


  @Override
  public boolean hasDelta() {
    return intChd || strChd;
  }

  @Override
  public void toDelta(DataOutput out) throws IOException {
    out.writeBoolean(intChd);
    if (intChd) {
      out.writeInt(intVal);
    }

    out.writeBoolean(strChd);
    if (strChd) {
      out.writeInt(strVal.length());
      out.writeChars(strVal);
    }
  }

  @Override
  public void fromDelta(DataInput in) throws IOException, InvalidDeltaException {
    if (in.readBoolean()) {
      intVal = in.readInt();
      System.out.println("Setting intVal to new value:" + intVal);
    }
    if (in.readBoolean()) {
      strVal = "";
      int length = in.readInt();
      for (int i = 0; i < length; i++) {
        strVal += in.readChar();
        System.out.println("Setting strVal to new value:" + strVal);
      }
    }
  }

  public void setIntVal(int newVal) {
    if (newVal != intVal) {
      this.intChd = true;
    }
    this.intVal = newVal;
  }

  public void setStrVal(String newVal) {
    if (newVal != strVal) {
      this.strChd = true;
    }
    this.strVal = newVal;
  }

  public String toString() {
    return "ValueHolder : [ intVal = " + intVal + ", intChd = " + intChd + ", strVal = " + strVal
        + ", strChd = " + strChd + " ]";
  }
}
