/*
 * @(#)RSAPrivateKey.java	1.4 98/12/03
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
 * The interface to an RSA private key.
 *
 * @author Jan Luehe
 *
 * @version 1.4 98/12/03
 *
 * @see RSAPrivateCrtKey
 */

public interface RSAPrivateKey extends java.security.PrivateKey {

    /**
     * Returns the modulus.
     *
     * @return the modulus
     */
    public BigInteger getModulus();

    /**
     * Returns the private exponent.
     *
     * @return the private exponent
     */
    public BigInteger getPrivateExponent();
}
