/*
 * @(#)RSAKey.java	1.3 00/02/02
 *
 * Copyright 1999, 2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
 
package java.security.interfaces;

import java.math.BigInteger;

/**
 * The interface to an RSA public or private key.
 *
 * @author Jan Luehe
 * @version 1.3 02/02/00
 *
 * @see RSAPublicKey
 * @see RSAPrivateKey
 *
 * @since 1.3
 */

public interface RSAKey {

    /**
     * Returns the modulus.
     *		
     * @return the modulus
     */
    public BigInteger getModulus();
}
