/*
 * @(#)RSAPrivateKey.java	1.11 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security.interfaces;

import java.math.BigInteger;

/**
 * The interface to an RSA private key.
 *
 * @author Jan Luehe
 *
 * @version 1.11 03/12/19
 *
 * @see RSAPrivateCrtKey
 */

public interface RSAPrivateKey extends java.security.PrivateKey, RSAKey
{

    static final long serialVersionUID = 5187144804936595022L;

    /**
     * Returns the private exponent.
     *
     * @return the private exponent
     */
    public BigInteger getPrivateExponent();
}
