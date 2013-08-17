/*
 * @(#)Principal.java	1.10 98/10/11
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

package org.omg.CORBA;


/**
 * A class that contains information about the identity of
 * the client, for access control
 * and other purposes. It contains a single attribute, the name of the
 * <code>Principal</code>, encoded as a sequence of bytes.
 * <P>
 * @deprecated Deprecated by CORBA 2.2.
 */

public abstract class Principal {

    /**
     * Sets the name of this <code>Principal</code> object to the given value.
     * @param value the value to be set in the <code>Principal</code>
     * @deprecated Deprecated by CORBA 2.2.
     */
    public abstract void name(byte[] value);

    /**
     * Gets the name of this <code>Principal</code> object.
     * @return the name of this <code>Principal</code> object
     * @deprecated Deprecated by CORBA 2.2.
     */
    public abstract byte[] name();
}
