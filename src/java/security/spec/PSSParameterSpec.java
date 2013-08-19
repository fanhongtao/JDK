/*
 * @(#)PSSParameterSpec.java	1.3 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security.spec;

import java.math.BigInteger;

/**
 * This class specifies a parameter spec for RSA PSS encoding scheme, 
 * as defined in the PKCS#1 v2.1.
 *
 * @author Valerie Peng
 *
 * @version 1.3 03/01/23
 *
 * @see AlgorithmParameterSpec
 * @see java.security.Signature
 *
 * @since 1.4
 */

public class PSSParameterSpec implements AlgorithmParameterSpec {

    private int saltLen = 0;

   /**
    * Creates a new <code>PSSParameterSpec</code>
    * given the salt length as defined in PKCS#1.
    *
    * @param saltLen the length of salt in bits to be used in PKCS#1 
    * PSS encoding.
    * @exception IllegalArgumentException if <code>saltLen</code> is
    * less than 0.
    */
    public PSSParameterSpec(int saltLen) {
	if (saltLen < 0) {
	    throw new IllegalArgumentException("invalid saltLen value");
	}
	this.saltLen = saltLen;
    }

    /**
     * Returns the salt length in bits.
     *
     * @return the salt length.
     */
    public int getSaltLength() {
	return saltLen;
    }
}
