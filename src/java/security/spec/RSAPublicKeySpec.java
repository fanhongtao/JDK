/*
 * @(#)RSAPublicKeySpec.java	1.6 00/02/02
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.security.spec;

import java.math.BigInteger;

/**
 * This class specifies an RSA public key.
 *
 * @author Jan Luehe
 *
 * @version 1.6 00/02/02
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
