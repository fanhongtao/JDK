/*
 * @(#)KeyPair.java	1.6 99/02/09
 *
 * Copyright 1995-1999 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
 
package java.security;

import java.util.*;
import java.io.*;
/**
 * <p>This class is a simple holder for a key pair (a public key and a
 * private key). It does not enforce any security, and, when initialized, 
 * should be treated like a PrivateKey.
 *
 * @see PublicKey
 * @see PrivateKey
 *
 * @version 1.6 99/02/09
 * @author Benjamin Renaud
 */
public final class KeyPair { 

    private PrivateKey privateKey;
    private PublicKey publicKey;

    /**
     * Constructs a key with the specified public key and private key.
     * 
     * @param publicKey the public key.
     * 
     * @param privateKey the private key.
     */
    public KeyPair(PublicKey publicKey, PrivateKey privateKey) {
	this.publicKey = publicKey;
	this.privateKey = privateKey;
    }

    /**
     * Returns the public key from this key pair.
     * 
     * @return the public key.
     */
    public PublicKey getPublic() {
	return publicKey;
    }

     /**
     * Returns the private key from this key pair.
     * 
     * @return the private key.
     */
   public PrivateKey getPrivate() {
	return privateKey;
    }    
}
