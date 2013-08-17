/*
 * @(#)DSAParams.java	1.9 98/07/01
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
 
package java.security.interfaces;

import java.math.BigInteger;

/**
 * Interface to a DSA-specific set of key parameters, which defines a 
 * DSA <em>key family</em>. DSA (Digital Signature Algorithm) is defined 
 * in NIST's FIPS-186.
 *
 * @see DSAKey
 * @see java.security.Key
 * @see java.security.Signature
 * 
 * @version 1.5, 97/01/16
 * @author Benjamin Renaud 
 * @author Josh Bloch 
 */
public interface DSAParams {

    /**
     * Returns the prime, <code>p</code>.
     * 
     * @return the prime, <code>p</code>. 
     */
    public BigInteger getP();

    /**
     * Returns the subprime, <code>q</code>.
     * 
     * @return the subprime, <code>q</code>. 
     */
    public BigInteger getQ();

    /**
     * Returns the base, <code>g</code>.
     * 
     * @return the base, <code>g</code>. 
     */
    public BigInteger getG();
}
