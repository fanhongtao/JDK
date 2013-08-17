/*
 * @(#)RSAPublicKey.java	1.6 00/02/02
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
 * The interface to an RSA public key.
 *
 * @author Jan Luehe
 *
 * @version 1.6 00/02/02
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
