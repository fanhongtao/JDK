/*
 * @(#)DSAPublicKey.java	1.8 99/02/09
 *
 * Copyright 1995-1999 by Sun Microsystems, Inc.,
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
 * The interface to a DSA public key. DSA (Digital Signature Algorithm) 
 * is defined in NIST's FIPS-186.
 *
 * @see java.security.Key
 * @see java.security.KeyParams
 * @see java.security.Signature
 * 
 * @version 1.8, 99/02/09
 * @author Benjamin Renaud 
 */
public interface DSAPublicKey extends DSAKey, java.security.PublicKey {

    /**
     * Returns the value of the public key, <code>y</code>.
     * 
     * @return the value of the public key, <code>y</code>. 
     */
    public BigInteger getY();
}


