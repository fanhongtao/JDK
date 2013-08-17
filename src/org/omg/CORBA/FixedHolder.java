/*
 * @(#)FixedHolder.java	1.5 98/09/10
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
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
 * FixedHolder is a container class for values of IDL type "fixed",
 * which is mapped to the Java class java.math.BigDecimal.
 * It is usually used to store "out" and "inout" IDL method parameters.
 * If an IDL method signature has a fixed as an "out" or "inout" parameter,
 * the programmer must pass an instance of FixedHolder as the corresponding
 * parameter in the method invocation; for "inout" parameters, the programmer
 * must also fill the "in" value to be sent to the server.
 * Before the method invocation returns, the ORB will fill in the contained
 * value corresponding to the "out" value returned from the server.
 *
 * @version     1.14 09/09/97
 */
public final class FixedHolder implements Streamable {
    /**
     * The value held by the FixedHolder
     */
    public java.math.BigDecimal value;

    /**
     * Construct the FixedHolder without initializing the contained value.
     */
    public FixedHolder() {
    }

    /**
     * Construct the FixedHolder and initialize it with the given value.
     * @param initial the value used to initialize the FixedHolder
     */
    public FixedHolder(java.math.BigDecimal initial) {
        value = initial;
    }

    /**
     * Read a fixed point value from the input stream and store it in
     * the value member.
     *
     * @param input the <code>InputStream</code> to read from.
     */
    public void _read(InputStream input) {
	value = input.read_fixed();
    }

    /**
     * Write the fixed point value stored in this holder to an
     * <code>OutputStream</code>.
     *
     * @param output the <code>OutputStream</code> to write into.
     */
    public void _write(OutputStream output) {
	output.write_fixed(value);
    }

    
    /**
     * Return the <code>TypeCode</code> of this holder object.
     *
     * @return the <code>TypeCode</code> object. 
     */
    public org.omg.CORBA.TypeCode _type() {
	return ORB.init().get_primitive_tc(TCKind.tk_fixed);
    }

}
