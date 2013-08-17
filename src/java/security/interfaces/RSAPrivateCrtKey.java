/*
 * @(#)RSAPrivateCrtKey.java	1.6 98/12/03
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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
