/*
 * @(#)Streamable.java	1.14 98/08/25
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package org.omg.CORBA.portable;

import org.omg.CORBA.TypeCode;

/**
 * The base class for the Holder classess of all complex
 * IDL types. The ORB treats all generated Holders as Streamable to invoke
 * the methods for marshalling and unmarshalling.
 *
 * @version 1.11, 03/18/98
 * @since   JDK1.2
 */

public interface Streamable {
    /**
     * Reads data from <code>istream</code> and initalizes the
	 * <code>value</code> field of the Holder with the unmarshalled data.
     *
     * @param     istream   the InputStream that represents the CDR data from the wire.
     */
    void _read(InputStream istream);
    /**
     * Marshals to <code>ostream</code> the value in the 
	 * <code>value</code> field of the Holder.
     *
     * @param     ostream   the CDR OutputStream
     */
    void _write(OutputStream ostream);

    /**
     * Retrieves the <code>TypeCode</code> object corresponding to the value
	 * in the <code>value</code> field of the Holder.
     *
     * @return    the <code>TypeCode</code> object for the value held in the holder
     */
    TypeCode _type();
}
