/*
 * @(#)RSAPrivateCrtKeySpec.java	1.6 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security.spec;

import java.math.BigInteger;

/**
 * This class specifies an RSA private key, as defined in the PKCS#1
 * standard, using the <i>Chinese Remainder Theorem</i> (CRT) information
 * values.
 *
 * @author Jan Luehe
 *
 * @version 1.6 01/11/29
 *
 * @see java.security.Key
 * @see java.security.KeyFactory
 * @see KeySpec
 * @see PKCS8EncodedKeySpec
 * @see RSAPrivateKeySpec
 * @see RSAPublicKeySpec
 */

public class RSAPrivateCrtKeySpec extends RSAPrivateKeySpec {

    private BigInteger modulus;
    private BigInteger publicExponent;
    private BigInteger privateExponent;
    private BigInteger primeP;
    private BigInteger primeQ;
    private BigInteger primeExponentP;
    private BigInteger primeExponentQ;
    private BigInteger crtCoefficient;

    public RSAPrivateCrtKeySpec(BigInteger modulus,
				BigInteger publicExponent,
				BigInteger privateExponent,
				BigInteger primeP,
				BigInteger primeQ,
				BigInteger primeExponentP,
				BigInteger primeExponentQ,
				BigInteger crtCoefficient) {
	super(modulus, privateExponent);
	this.publicExponent = publicExponent;
	this.primeP = primeP;
	this.primeQ = primeQ;
	this.primeExponentP = primeExponentP;
	this.primeExponentQ = primeExponentQ;
	this.crtCoefficient = crtCoefficient;
    }

    /**
     * Returns the public exponent.
     *
     * @return the public exponent
     */
    public BigInteger getPublicExponent() {
	return this.publicExponent;
    }

    /**
     * Returns the primeP.

     * @return the primeP
     */
    public BigInteger getPrimeP() {
	return this.primeP;
    }

    /**
     * Returns the primeQ.
     *
     * @return the primeQ
     */
    public BigInteger getPrimeQ() {
	return this.primeQ;
    }

    /**
     * Returns the primeExponentP.
     *
     * @return the primeExponentP
     */
    public BigInteger getPrimeExponentP() {
	return this.primeExponentP;
    }

    /**
     * Returns the primeExponentQ.
     *
     * @return the primeExponentQ
     */
    public BigInteger getPrimeExponentQ() {
	return this.primeExponentQ;
    }

    /**
     * Returns the crtCoefficient.
     *
     * @return the crtCoefficient
     */
    public BigInteger getCrtCoefficient() {
	return this.crtCoefficient;
    }
}
