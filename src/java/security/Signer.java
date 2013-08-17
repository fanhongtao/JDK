/*
 * @(#)Signer.java	1.24 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
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

import java.io.*;

/**
 * This class is used to represent an Identity that can also digitally
 * sign data.
 *
 * <p>The management of a signer's private keys is an important and
 * sensitive issue that should be handled by subclasses as appropriate
 * to their intended use.
 *
 * @see Identity
 *
 * @version 	1.22, 01/31/97
 * @author Benjamin Renaud 
 */
public abstract class Signer extends Identity {

    private PrivateKey privateKey;

    /** 
     * Creates a signer. This constructor should only be used for 
     * serialization. 
     */      
    protected Signer() {
	super();
    }


    /** 
     * Creates a signer with the specified identity name.
     * 
     * @param name the identity name.   
     */
    public Signer(String name) {
	super(name);
    }
    
    /** 
     * Creates a signer with the specified identity name and scope.
     * 
     * @param name the identity name.   
     *
     * @param scope the scope of the identity. 
     * 
     * @exception KeyManagementException if there is already an identity 
     * with the same name in the scope.
     */
    public Signer(String name, IdentityScope scope)
    throws KeyManagementException {
	super(name, scope);
    }

    /**
     * Returns this signer's private key.
     * 
     * @return this signer's private key, or null if the private key has
     * not yet been set.
     */
    public PrivateKey getPrivateKey() {
	check("get.private.key");
	return privateKey;
    }

   /**
     * Sets the key pair (public key and private key) for this signer.
     *
     * @param pair an initialized key pair.
     *
     * @exception InvalidParameterException if the key pair is not
     * properly initialized.
     * @exception KeyException if the key pair cannot be set for any
     * other reason.
     */
    public final void setKeyPair(KeyPair pair) 
    throws InvalidParameterException, KeyException {
	check("set.private.keypair");
	PublicKey pub = pair.getPublic();
	PrivateKey priv = pair.getPrivate();

	if (pub == null || priv == null) {
	    throw new InvalidParameterException();
	}
	setPublicKey(pub);
	privateKey = priv;
    }

    String printKeys() {
	String keys = "";
	PublicKey publicKey = getPublicKey();
	if (publicKey != null && privateKey != null) {
	    keys = "\tpublic and private keys initialized";

	} else {
	    keys = "\tno keys";
	}
	return keys;
    }

    /**
     * Returns a string of information about the signer.
     *
     * @return a string of information about the signer.
     */
    public String toString() {
	return "[Signer]" + super.toString();
    }
}

