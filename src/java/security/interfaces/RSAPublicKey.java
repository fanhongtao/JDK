/*
 * @(#)RSAPublicKey.java	1.11 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security.interfaces;

import java.math.BigInteger;

/**
 * The interface to an RSA public key.
 *
 * @author Jan Luehe
 *
 * @version 1.11 05/11/17
 */

public interface RSAPublicKey extends java.security.PublicKey, RSAKey
{
    static final long serialVersionUID = -8727434096241101194L;

    /**
     * Returns the public exponent.
     *
     * @return the public exponent
     */
    public BigInteger getPublicExponent();
}
