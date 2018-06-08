package org.apache.geode_examples.deltaPropagation;

import org.apache.geode.Delta;
import org.apache.geode.InvalidDeltaException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
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
            System.out.println(" Extracted delta from field 'stringVal' = "
                + this.stringVal);
        }
    }

    @Override public void fromDelta(DataInput in) throws IOException, InvalidDeltaException {
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

    public void setIntVal(int newVal) {
        this.intVal = newVal;
        this.intChanged = true;
    }

    public void setStringVal(String newVal) {
        this.stringVal = newVal;
        this.strChanged = true;
    }

}
