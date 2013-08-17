/*
 * @(#)RSAPrivateCrtKey.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.security.interfaces;

import java.math.BigInteger;

/**
 * The interface to an RSA private key, as defined in the PKCS#1 standard,
 * using the <i>Chinese Remainder Theorem</i> (CRT) information values.
 *
 * @author Jan Luehe
 *
 * @version 1.6 98/12/03
 *
 * @see RSAPrivateKey
 */

public interface RSAPrivateCrtKey extends RSAPrivateKey {

    /**
     * Returns the public exponent.
     *
     * @return the public exponent
     */
    public BigInteger getPublicExponent();

    /**
     * Returns the primeP.

     * @return the primeP
     */
    public BigInteger getPrimeP();

    /**
     * Returns the primeQ.
     *
     * @return the primeQ
     */
    public BigInteger getPrimeQ();

    /**
     * Returns the primeExponentP.
     *
     * @return the primeExponentP
     */
    public BigInteger getPrimeExponentP();

    /**
     * Returns the primeExponentQ.
     *
     * @return the primeExponentQ
     */
    public BigInteger getPrimeExponentQ();

    /**
     * Returns the crtCoefficient.
     *
     * @return the crtCoefficient
     */
    public BigInteger getCrtCoefficient();
}
