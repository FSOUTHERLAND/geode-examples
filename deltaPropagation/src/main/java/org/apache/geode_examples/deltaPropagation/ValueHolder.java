package org.apache.geode_examples.deltaPropagation;

import org.apache.geode.Delta;
import org.apache.geode.InvalidDeltaException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ValueHolder implements Delta {

    private int intVal;
    private String stringVal;

    private transient boolean intChanged = false;
    private transient boolean strChanged = false;

    public ValueHolder() {
        this.intVal = 0;
        this.stringVal = "";
    }


    @Override public boolean hasDelta() {
        return this.intChanged || this.strChanged;
    }

    @Override public void toDelta(DataOutput out) throws IOException {
        System.out.println("Extracting delta from " + this.toString());
        // Write information on what has changed to the
        // data stream, so fromDelta knows what it's getting
        out.writeBoolean(intChanged);
        if (intChanged) {
            // Write just the changes into the data stream
            out.writeInt(this.intVal);
            // Once the delta information is written, reset the delta status field
            this.intChanged = false;
            System.out.println(" Extracted delta from field 'intVal' = "
                + this.intVal);
        }
        out.writeBoolean(strChanged);
        if (strChanged) {
            out.writeChars(stringVal);
            this.strChanged = false;
            System.out.println(" Extracted delta from field 'doubleVal' = "
                + this.stringVal);
        }
    }

    @Override public void fromDelta(DataInput in) throws IOException, InvalidDeltaException {

    }
}
