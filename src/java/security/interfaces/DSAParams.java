/*
 * @(#)DSAParams.java	1.5 96/12/24
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
 */
 
package java.security.interfaces;

import java.math.BigInteger;

/**
 * Interface to a DSA-specific set of key parameters, which defines a 
 * DSA <em>key family</em>. DSA (Digital Signature Algorithm) is defined 
 * in NIST's FIPS-186.
 *
 * @see DSAKey
 * @see java.security.Key
 * @see java.security.Signature
 * 
 * @version 1.5, 97/01/16
 * @author Benjamin Renaud 
 * @author Josh Bloch 
 */
public interface DSAParams {

    /**
     * Returns the prime, <code>p</code>.
     * 
     * @return the prime, <code>p</code>. 
     */
    public BigInteger getP();

    /**
     * Returns the subprime, <code>q</code>.
     * 
     * @return the subprime, <code>q</code>. 
     */
    public BigInteger getQ();

    /**
     * Returns the base, <code>g</code>.
     * 
     * @return the base, <code>g</code>. 
     */
    public BigInteger getG();
}
