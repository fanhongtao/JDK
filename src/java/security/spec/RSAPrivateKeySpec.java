/*
 * @(#)RSAPrivateKeySpec.java	1.10 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security.spec;

import java.math.BigInteger;

/**
 * This class specifies an RSA private key.
 *
 * @author Jan Luehe
 *
 * @version 1.10 03/12/19
 *
 * @see java.security.Key
 * @see java.security.KeyFactory
 * @see KeySpec
 * @see PKCS8EncodedKeySpec
 * @see RSAPublicKeySpec
 * @see RSAPrivateCrtKeySpec
 */

public class RSAPrivateKeySpec implements KeySpec {

    private BigInteger modulus;
    private BigInteger privateExponent;

    /**
     * Creates a new RSAPrivateKeySpec.
     *
     * @param modulus the modulus
     * @param privateExponent the private exponent
     */
    public RSAPrivateKeySpec(BigInteger modulus, BigInteger privateExponent) {
	this.modulus = modulus;
	this.privateExponent = privateExponent;
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
     * Returns the private exponent.
     *
     * @return the private exponent
     */
    public BigInteger getPrivateExponent() {
	return this.privateExponent;
    }
}
