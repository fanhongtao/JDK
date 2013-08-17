/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security.spec;

import java.math.BigInteger;

/**
 * This class specifies an RSA private key.
 *
 * @author Jan Luehe
 *
 * @version 1.7 02/02/06
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
