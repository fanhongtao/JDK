/*
 * @(#)SerializationTester.java	1.5 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.dnd;

import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Tests if an object can truly be serialized by serializing it to a null
 * OutputStream.
 *
 * @version 1.5, 12/19/03
 * @since 1.4
 */
final class SerializationTester {
    private static ObjectOutputStream stream;
    static {
        try {
            stream = new ObjectOutputStream(new OutputStream() {
                    public void write(int b) {}
                });
        } catch (IOException cannotHappen) {
        }
    }

    static boolean test(Object obj) {
        if (!(obj instanceof Serializable)) {
            return false;
        }

        try {
            stream.writeObject(obj);
        } catch (IOException e) {
            return false;
        } finally {
            // Fix for 4503661. 
            // Reset the stream so that it doesn't keep a reference to the
            // written object.
            try {
                stream.reset();
            } catch (IOException e) {
                // Ignore the exception.
            }
        }
        return true;
    }

    private SerializationTester() {}
}
