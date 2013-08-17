/*
 * @(#)Externalizable.java	1.6 97/01/22
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 */

package java.io;

import java.io.ObjectOutput;
import java.io.ObjectInput;

/**
 * Externalization allows a class to specify the methods to be used to
 * write the object's contents to a stream and to read them back.  The
 * Externalizable interface's writeExternal and readExternal methods
 * are implemented by a class to
 * give the class complete control over the format and contents of the
 * stream for an object and its supertypes. These methods must explicitly
 * coordinate with the supertype to save its state. <br>
 *
 * Object Serialization uses the Serializable and Externalizable
 * interfaces.  Object persistence mechanisms may use them also.  Each
 * object to be stored is tested for the Externalizable interface. If
 * the object supports it, the writeExternal method is called. If the
 * object does not support Externalizable and does implement
 * Serializable the object should be saved using
 * ObjectOutputStream. <br> When an Externalizable object is to be
 * reconstructed, an instance is created using the public no-arg
 * constructor and the readExternal method called.  Serializable
 * objects are restored by reading them from an ObjectInputStream.
 *
 * @author  unascribed
 * @version 1.6, 01/22/97
 * @see java.io.ObjectOutputStream
 * @see java.io.ObjectInputStream
 * @see java.io.ObjectOutput
 * @see java.io.ObjectInput
 * @see java.io.Serializable
 * @since   JDK1.1
 */
public interface Externalizable extends java.io.Serializable {
    /**
     * The object implements the writeExternal method to save its contents
     * by calling the methods of DataOutput for its primitive values or
     * calling the writeObject method of ObjectOutput for objects, strings
     * and arrays.
     * @exception IOException Includes any I/O exceptions that may occur
     * @since     JDK1.1
     */
    void writeExternal(ObjectOutput out) throws IOException;

    /**
     * The object implements the readExternal method to restore its
     * contents by calling the methods of DataInput for primitive
     * types and readObject for objects, strings and arrays.  The
     * readExternal method must read the values in the same sequence
     * and with the same types as were written by writeExternal.
     * @exception ClassNotFoundException If the class for an object being
     *              restored cannot be found.
     * @since     JDK1.1
     */
    void readExternal(ObjectInput in) throws IOException, ClassNotFoundException;
}
