/*
 * @(#)DSAPublicKey.java	1.9 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.9, 01/12/10
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


