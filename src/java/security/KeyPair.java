/*
 * @(#)KeyPair.java	1.7 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.7 01/12/10
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
