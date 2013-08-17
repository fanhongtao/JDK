/*
 * @(#)Identity.java	1.28 98/07/01
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

import java.io.Serializable;
import java.util.*;

/**
 * <p>This class represents identities: real-world objects such as people,
 * companies or organizations whose identities can be authenticated using 
 * their public keys. Identities may also be more abstract (or concrete) 
 * constructs, such as daemon threads or smart cards.
 *
 * <p>All Identity objects have a name and a public key. Names are
 * immutable. Identities may also be scoped. That is, if an Identity is
 * specified to have a particular scope, then the name and public
 * key of the Identity are unique within that scope.
 *
 * <p>An Identity also has a set of certificates (all certifying its own
 * public key). The Principal names specified in these certificates need 
 * not be the same, only the key.
 *
 * <p>An Identity can be subclassed, to include postal and email addresses,
 * telephone numbers, images of faces and logos, and so on.
 *
 * @see IdentityScope
 * @see Signer
 * @see Principal
 *
 * @version 	1.24, 01/27/97
 * @author Benjamin Renaud 
 */
public abstract 
class Identity implements Principal, Serializable {

    /**
     * The name for this identity.
     */
    private String name;

    /**
     * The public key for this identity.
     */
    private PublicKey publicKey;

    /**
     * Generic, descriptive information about the identity.
     */
    String info = "No further information available.";

    /**
     * The scope of the identity.
     */
    IdentityScope scope;

    /**
     * The certificates for this identity.
     */
    Vector certificates;

    /**
     * Constructor for serialization only.
     */
    protected Identity() {
	this("restoring...");
    }

    /**
     * Constructs an identity with the specified name and scope.
     *
     * @param name the identity name.  
     * @param scope the scope of the identity.
     *
     * @exception KeyManagementException if there is already an identity 
     * with the same name in the scope.
     */
    public Identity(String name, IdentityScope scope) throws
    KeyManagementException {
	this(name);
	this.scope = scope;
    }

    /**
     * Constructs an identity with the specified name and no scope.
     *
     * @param name the identity name.
     */
    public Identity(String name) {
	this.name = name;
    }

    /**
     * Returns this identity's name.
     *
     * @return the name of this identity.
     */
    public final String getName() {
	return name;
    }

    /**
     * Returns this identity's scope.
     *
     * @return the scope of this identity.
     */
    public final IdentityScope getScope() {
	return scope;
    }

    /**
     * Returns this identity's public key.
     * 
     * @return the public key for this identity.
     */
    public PublicKey getPublicKey() {
	return publicKey;
    }

    /**
     * Sets this identity's public key. The old key and all of this
     * identity's certificates are removed by this operation. 
     *
     * @param key the public key for this identity.
     *
     * @exception KeyManagementException if another identity in the 
     * identity's scope has the same public key, or if another exception occurs.  
     */
    /* Should we throw an exception if this is already set? */
    public void setPublicKey(PublicKey key) throws KeyManagementException {
	
	check("set.public.key");
	this.publicKey = key;
	certificates = new Vector();
    }

    /**
     * Specifies a general information string for this identity.
     *
     * @param info the information string.
     *
     * @see #getInfo
     */
    public void setInfo(String info) {
	check("set.info");
	this.info = info;
    }

    /**
     * Returns general information previously specified for this identity.
     *
     * @return general information about this identity.
     *
     * @see #setInfo
     */
    public String getInfo() {
	return info;
    }

    /**
     * Adds a certificate for this identity. If the identity has a public
     * key, the public key in the certificate must be the same, and if
     * the identity does not have a public key, the identity's
     * public key is set to be that specified in the certificate.
     *
     * @param certificate the certificate to be added.
     *
     * @exception KeyManagementException if the certificate is not valid,
     * if the public key in the certificate being added conflicts with
     * this identity's public key, or if another exception occurs.
     */
    public void addCertificate(Certificate certificate)
    throws KeyManagementException {

	check("add.certificate");

	if (certificates == null) {
	    certificates = new Vector();
	}
	if (publicKey != null) {
	    if (!keyEquals(publicKey, certificate.getPublicKey())) {
		throw new KeyManagementException(
		    "public key different from cert public key");
	    }
	} else {
	    publicKey = certificate.getPublicKey();
	}
	certificates.addElement(certificate);
    }

   private boolean keyEquals(Key aKey, Key anotherKey) {
	if (aKey.getFormat().equalsIgnoreCase(anotherKey.getFormat())) {
	    return MessageDigest.isEqual(aKey.getEncoded(), 
					 anotherKey.getEncoded());
	} else {
	    return false;
	}
    }


    /**
     * Removes a certificate from this identity.
     *
     * @param certificate the certificate to be removed.
     *
     * @exception KeyManagementException if the certificate is
     * missing, or if another exception occurs.
     */
    public void removeCertificate(Certificate certificate)
    throws KeyManagementException {
	check("remove.certificate");
	if (certificates != null) {
	    certificates.removeElement(certificate);
	}
    }

    /**
     * Returns a copy of all the certificates for this identity.  
     * 
     * @return a copy of all the certificates for this identity.  
     */
    public Certificate[] certificates() {
	if (certificates == null) {
	    return new Certificate[0];
	}
	int len = certificates.size();
	Certificate[] certs = new Certificate[len];
	certificates.copyInto(certs);
	return certs;
    }

    /**
     * Tests for equality between the specified object and this identity.
     * This first tests to see if the entities actually refer to the same
     * object, in which case it returns true. Next, it checks to see if
     * the entities have the same name and the same scope. If they do, 
     * the method returns true. Otherwise, it calls <a href = 
     * "#identityEquals">identityEquals</a>, which subclasses should 
     * override.
     *
     * @param identity the object to test for equality with this identity.  
     *
     * @return true if the objects are considered equal, false otherwise.
     *
     * @see #identityEquals 
     */
    public final boolean equals(Object identity) {

	if (identity == this) {
	    return true;
	}

	if (identity instanceof Identity) {
	    Identity i = (Identity)identity;
	    if (i.getScope() == scope && i.getName().equals(name)) {
		return true;
	    } else {
		return identityEquals(i);	    
	    }
	}
	return false;
    }

    /**
     * Tests for equality between the specified identity and this identity.
     * This method should be overriden by subclasses to test for equality. 
     * The default behavior is to return true if the names and public keys 
     * are equal.
     *
     * @param identity the identity to test for equality with this identity.
     * 
     * @return true if the identities are considered equal, false
     * otherwise. 
     *
     * @see #equals 
     */
    protected boolean identityEquals(Identity identity) {
	return (name.equals(identity.name) && 
		publicKey.equals(identity.publicKey));
    }

    /**
     * Returns a parsable name for identity: identityName.scopeName
     */
    String fullName() {
	String parsable = name;
	if (scope != null) {
	    parsable += "." + scope.getName();
	}
	return parsable;
    }

    /**
     * Returns a short string describing this identity, telling its
     * name and its scope (if any).
     *
     * @return information about this identity, such as its name and the  
     * name of its scope (if any).
     */
    public String toString() {
	check("print");
	String printable = name;
	if (scope != null) {
	    printable += "[" + scope.getName() + "]";
	}
	return printable;
    }

    /**
     * Returns a string representation of this identity, with
     * optionally more details than that provided by the
     * <code>toString</code> method without any arguments.
     *
     * @param detailed whether or not to provide detailed information.  
     *
     * @return information about this identity. If <code>detailed</code>
     * is true, then this method returns more information than that 
     * provided by the <code>toString</code> method without any arguments.
     *
     * @see #toString
     */
    public String toString(boolean detailed) {
	String out = toString();
	if (detailed) {
	    out += "\n";
	    out += printKeys();
	    out += "\n" + printCertificates();
	    if (info != null) {
		out += "\n\t" + info;
	    } else {
		out += "\n\tno additional information available.";
	    }
	}	  
	return out;
    }

    String printKeys() {
	String key = "";
	if (publicKey != null) {
	    key = "\tpublic key initialized";
	} else {
	    key = "\tno public key";
	}
	return key;
    }

    String printCertificates() {
	String out = "";
	if (certificates == null) {
	    return "\tno certificates";
	} else {
	    out += "\tcertificates: \n";
	    Enumeration e = certificates.elements();
	    int i = 1;
	    while (e.hasMoreElements()) {
		Certificate cert = (Certificate)e.nextElement();
		out += "\tcertificate " + i++ +
		    "\tfor  : " + cert.getPrincipal() + "\n";
		out += "\t\t\tfrom : " + 
		    cert.getGuarantor() + "\n";
	    }
	}
	return out;
    }
    
    /**
     * Returns a hashcode for this identity.
     *
     * @return a hashcode for this identity.
     */
    public int hashCode() {
	String scopedName = name;
	if (scope != null) {
	    scopedName += scope.getName();
	}
	return scopedName.hashCode();
    }

    void check(String directive) {
	staticCheck(this.getClass().getName() + "." + directive + "." +fullName());
    }

    static void staticCheck(String directive) {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkSecurityAccess(directive);
	}
    }
}

