/*
 * @(#)RSAPublicKeySpec.java	1.10 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security.spec;

import java.math.BigInteger;

/**
 * This class specifies an RSA public key.
 *
 * @author Jan Luehe
 *
 * @version 1.10 03/12/19
 *
 * @see java.security.Key
 * @see java.security.KeyFactory
 * @see KeySpec
 * @see X509EncodedKeySpec
 * @see RSAPrivateKeySpec
 * @see RSAPrivateCrtKeySpec
 */

public class RSAPublicKeySpec implements KeySpec {

    private BigInteger modulus;
    private BigInteger publicExponent;

    /**
     * Creates a new RSAPublicKeySpec.
     *
     * @param modulus the modulus
     * @param publicExponent the public exponent
     */
    public RSAPublicKeySpec(BigInteger modulus, BigInteger publicExponent) {
	this.modulus = modulus;
	this.publicExponent = publicExponent;
    }

    /**
     * Returns the modulus.
     *
     * @return the modulus
     */
    public BigInteger getModulus() {
	return this.modulus;
    }

    /**
     * Returns the public exponent.
     *
     * @return the public exponent
     */
    public BigInteger getPublicExponent() {
	return this.publicExponent;
    }
}
