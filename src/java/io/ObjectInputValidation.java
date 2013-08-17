/*
 * @(#)ObjectInputValidation.java	1.15 00/02/02
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.io;

/**
 * Callback interface to allow validation of objects within a graph.
 * Allows an object to be called when a complete graph of objects has
 * been deserialized.
 *
 * @author  unascribed
 * @version 1.15, 02/02/00
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
