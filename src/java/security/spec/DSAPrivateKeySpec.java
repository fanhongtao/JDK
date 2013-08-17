/*
 * @(#)DSAPrivateKeySpec.java	1.10 98/09/21
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

package java.security.spec;

import java.math.BigInteger;

/**
 * This class specifies a DSA private key with its associated parameters.
 *
 * @author Jan Luehe
 *
 * @version 1.10, 00/05/10
 *
 * @see java.security.Key
 * @see java.security.KeyFactory
 * @see KeySpec
 * @see DSAPublicKeySpec
 * @see PKCS8EncodedKeySpec
 *
 * @since JDK1.2
 */

public class DSAPrivateKeySpec implements KeySpec {

    private BigInteger x;
    private BigInteger p;
    private BigInteger q;
    private BigInteger g;

    /**
     * Creates a new DSAPrivateKeySpec with the specified parameter values.
     * 
     * @param x the private key.
     * 
     * @param p the prime.
     * 
     * @param q the sub-prime.
     * 
     * @param g the base.
     */
    public DSAPrivateKeySpec(BigInteger x, BigInteger p, BigInteger q,
			     BigInteger g) {
	this.x = x;
	this.p = p;
	this.q = q;
	this.g = g;
    }

    /**
     * Returns the private key <code>x</code>.
     *
     * @return the private key <code>x</code>.
     */
    public BigInteger getX() {
	return this.x;
    }

    /**
     * Returns the prime <code>p</code>.
     *
     * @return the prime <code>p</code>.
     */
    public BigInteger getP() {
	return this.p;
    }

    /**
     * Returns the sub-prime <code>q</code>.
     *
     * @return the sub-prime <code>q</code>.
     */
    public BigInteger getQ() {
	return this.q;
    }

    /**
     * Returns the base <code>g</code>.
     *
     * @return the base <code>g</code>.
     */
    public BigInteger getG() {
	return this.g;
    }
}
