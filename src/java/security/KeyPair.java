/*
 * @(#)KeyPair.java	1.9 98/12/03
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
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

/**
 * This class is a simple holder for a key pair (a public key and a
 * private key). It does not enforce any security, and, when initialized,
 * should be treated like a PrivateKey.
 *
 * @see PublicKey
 * @see PrivateKey
 *
 * @version 1.9 98/12/03
 * @author Benjamin Renaud
 */

public final class KeyPair implements java.io.Serializable {

    private PrivateKey privateKey;
    private PublicKey publicKey;

    /**
     * Constructs a key pair from the given public key and private key.
     *
     * <p>Note that this constructor only stores references to the public
     * and private key components in the generated key pair. This is safe,
     * because <code>Key</code> objects are immutable.
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
     * Returns a reference to the public key component of this key pair.
     *
     * @return a reference to the public key.
     */
    public PublicKey getPublic() {
	return publicKey;
    }

     /**
     * Returns a reference to the private key component of this key pair.
     *
     * @return a reference to the private key.
     */
   public PrivateKey getPrivate() {
	return privateKey;
    }
}
