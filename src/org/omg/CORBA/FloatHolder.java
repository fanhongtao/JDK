/*
 * @(#)FloatHolder.java	1.20 98/09/10
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package org.omg.CORBA;

import org.omg.CORBA.portable.Streamable;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;


/**
 * A Holder class for a <code>float</code>
 * that is used to store "out" and "inout" parameters in IDL methods.
 * If an IDL method signature has an IDL <code>float</code> as an "out"
 * or "inout" parameter, the programmer must pass an instance of
 * <code>FloatHolder</code> as the corresponding
 * parameter in the method invocation; for "inout" parameters, the programmer
 * must also fill the "in" value to be sent to the server.
 * Before the method invocation returns, the ORB will fill in the
 * value corresponding to the "out" value returned from the server.
 * <P>
 * If <code>myFloatHolder</code> is an instance of <code>FloatHolder</code>,
 * the value stored in its <code>value</code> field can be accessed with
 * <code>myFloatHolder.value</code>.
 *
 * @version	1.14, 09/09/97
 * @since       JDK1.2
 */
public final class FloatHolder implements Streamable {
    /**
     * The <code>float</code> value held by this <code>FloatHolder</code>
     * object.
     */
    public float value;

    /**
     * Constructs a new <code>FloatHolder</code> object with its
     * <code>value</code> field initialized to 0.0.
     */
    public FloatHolder() {
    }

    /**
     * Constructs a new <code>FloatHolder</code> object for the given
     * <code>float</code>.
     * @param initial the <code>float</code> with which to initialize
     *                the <code>value</code> field of the new
     *                <code>FloatHolder</code> object
     */
    public FloatHolder(float initial) {
	value = initial;
    }

    /**
     * Read a float from an input stream and initialize the value
     * member with the float value.
     *
     * @param input the <code>InputStream</code> to read from.
     */
    public void _read(InputStream input) {
	value = input.read_float();
    }

    /**
     * Write the float value into an output stream.
     *
     * @param output the <code>OutputStream</code> to write into.
     */
    public void _write(OutputStream output) {
	output.write_float(value);
    }

    /**
     * Return the <code>TypeCode</code> of this Streamable.
     *
     * @return the <code>TypeCode</code> object.
     */
    public org.omg.CORBA.TypeCode _type() {
	return ORB.init().get_primitive_tc(TCKind.tk_float);
    }
}
