/*
 * @(#)DSAParams.java	1.20 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.20 03/12/19
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
