/*
 * @(#)DSAPrivateKey.java	1.4 98/07/01
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
 * The standard interface to a DSA private key. DSA (Digital Signature
 * Algorithm) is defined in NIST's FIPS-186.
 *
 * @see java.security.Key
 * @see java.security.Signature
 * @see DSAKey
 * @see DSAPublicKey
 * 
 * @version 1.4, 96/12/24
 *
 * @author Benjamin Renaud 
 */
public interface DSAPrivateKey extends DSAKey, java.security.PrivateKey {

    /**
     * Returns the value of the private key, <code>x</code>.
     * 
     * @return the value of the private key, <code>x</code>. 
     */
    public BigInteger getX();
}


