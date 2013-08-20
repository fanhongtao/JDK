/*
 * @(#)DSAPrivateKey.java	1.19 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
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
 * @version 1.19 03/12/19
 * @author Benjamin Renaud
 */
public interface DSAPrivateKey extends DSAKey, java.security.PrivateKey {

    // Declare serialVersionUID to be compatible with JDK1.1

   /**
    * The class fingerprint that is set to indicate 
    * serialization compatibility with a previous 
    * version of the class.
    */
    static final long serialVersionUID = 7776497482533790279L;

    /**
     * Returns the value of the private key, <code>x</code>.
     *
     * @return the value of the private key, <code>x</code>.
     */
    public BigInteger getX();
}


