/*
 * @(#)RSAPrivateKey.java	1.7 00/02/02
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
 * The interface to an RSA private key.
 *
 * @author Jan Luehe
 *
 * @version 1.7 00/02/02
 *
 * @see RSAPrivateCrtKey
 */

public interface RSAPrivateKey extends java.security.PrivateKey, RSAKey
{
    /**
     * Returns the private exponent.
     *
     * @return the private exponent
     */
    public BigInteger getPrivateExponent();
}
