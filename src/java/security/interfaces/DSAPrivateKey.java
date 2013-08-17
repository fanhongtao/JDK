/*
 * @(#)DSAPrivateKey.java	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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


