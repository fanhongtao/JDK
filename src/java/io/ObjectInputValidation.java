/*
 * @(#)ObjectInputValidation.java	1.18 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

/**
 * Callback interface to allow validation of objects within a graph.
 * Allows an object to be called when a complete graph of objects has
 * been deserialized.
 *
 * @author  unascribed
 * @version 1.18, 12/19/03
 * @see     ObjectInputStream
 * @see     ObjectInputStream#registerValidation(java.io.ObjectInputValidation, int)
 * @since   JDK1.1
 */
public interface ObjectInputValidation {
    /**
     * Validates the object.
     *
     * @exception InvalidObjectException If the object cannot validate itself.
     */
    public void validateObject() throws InvalidObjectException;
}
