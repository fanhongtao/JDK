/*
 * @(#)DSAKey.java	1.14 00/02/02
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
 
package java.security.interfaces;

/**
 * The interface to a DSA public or private key. DSA (Digital Signature
 * Algorithm) is defined in NIST's FIPS-186.
 *
 * @see DSAParams
 * @see java.security.Key
 * @see java.security.Signature
 * 
 * @version 1.14 00/02/02
 * @author Benjamin Renaud 
 * @author Josh Bloch 
 */
public interface DSAKey {

    /**
     * Returns the DSA-specific key parameters. These parameters are
     * never secret.
     *
     * @return the DSA-specific key parameters.
     * 
     * @see DSAParams
     */
    public DSAParams getParams();
}
