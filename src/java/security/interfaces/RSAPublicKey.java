/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security.interfaces;

import java.math.BigInteger;

/**
 * The interface to an RSA public key.
 *
 * @author Jan Luehe
 *
 * @version 1.7 02/02/06
 */

public interface RSAPublicKey extends java.security.PublicKey, RSAKey
{
    /**
     * Returns the public exponent.
     *
     * @return the public exponent
     */
    public BigInteger getPublicExponent();
}
