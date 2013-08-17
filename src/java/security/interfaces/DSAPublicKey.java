/*
 * @(#)DSAPublicKey.java	1.18 00/02/02
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.security.interfaces;

import java.math.BigInteger;

/**
 * The interface to a DSA public key. DSA (Digital Signature Algorithm)
 * is defined in NIST's FIPS-186.
 *
 * @see java.security.Key
 * @see java.security.Signature
 * @see DSAKey
 * @see DSAPrivateKey
 *
 * @version 1.18 00/02/02
 * @author Benjamin Renaud
 */
public interface DSAPublicKey extends DSAKey, java.security.PublicKey {

    // Declare serialVersionUID to be compatible with JDK1.1

   /**
    * The class fingerprint that is set to indicate 
    * serialization compatibility with a previous 
    * version of the class.
    */
    static final long serialVersionUID = 1234526332779022332L;

    /**
     * Returns the value of the public key, <code>y</code>.
     *
     * @return the value of the public key, <code>y</code>.
     */
    public BigInteger getY();
}


