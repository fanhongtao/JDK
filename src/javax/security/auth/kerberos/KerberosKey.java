/*
 * @(#)KerberosKey.java	1.13 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
  
package javax.security.auth.kerberos;

import javax.crypto.SecretKey;
import javax.security.auth.Destroyable;
import javax.security.auth.DestroyFailedException;

/**
 * This class encapsulates a long term secret key for a Kerberos
 * principal.<p>
 *
 * All Kerberos JAAS login modules that obtain a principal's password and
 * generate the secret key from it should use this class. Where available,
 * the login module might even read this secret key directly from a
 * Kerberos "keytab". Sometimes, such as when authenticating a server in
 * the absence of user-to-user authentication, the login module will store 
 * an instance of this class in the private credential set of a
 * {@link javax.security.auth.Subject Subject} during the commit phase of the
 * authentication process.<p>
 *
 * It might be necessary for the application to be granted a
 * {@link javax.security.auth.PrivateCredentialPermission 
 * PrivateCredentialPermission} if it needs to access the KerberosKey
 * instance from a Subject. This permission is not needed when the 
 * application depends on the default JGSS Kerberos mechanism to access the 
 * KerberosKey. In that case, however, the application will need an 
 * appropriate 
 * {@link javax.security.auth.kerberos.ServicePermission ServicePermission}.
 *
 * @author Mayank Upadhyay
 * @version 1.13, 01/23/03
 * @since 1.4
 */
public class KerberosKey implements SecretKey, Destroyable {


   /**
     * The principal that this secret key belongs to.
     *
     * @serial
     */
    private KerberosPrincipal principal;

   /**
     * the version number of this secret key
     *
     * @serial
     */
    private int versionNum;

   /**
    * <code>KeyImpl</code> is serialized by writing out the ASN1 Encoded bytes 
    *			of the 	encryption key. The ASN1 encoding is defined in 
    *			RFC1510 and as  follows:
    *			<pre>
    *			EncryptionKey ::=   SEQUENCE {
    *				keytype[0]    INTEGER,
    *				keyvalue[1]   OCTET STRING    	
    *				}
    *			</pre>
    *
    * @serial
    */

    private KeyImpl key;
    private transient boolean destroyed = false;

    /**
     * Constructs a KerberosKey from the given bytes when the key type and
     * key version number are known. This can used when reading the secret
     * key information from a Kerberos "keytab".
     * 
     * @param principal the principal that this secret key belongs to
     * @param keyBytes the raw bytes for the secret key
     * @param keyType the key type for the secret key as defined by the
     * Kerberos protocol specification.
     * @param versionNum the version number of this secret key
     */
    public KerberosKey(KerberosPrincipal principal,
		       byte[] keyBytes, 
		       int keyType,
		       int versionNum) {
	this.principal = principal;
	this.versionNum = versionNum;
	key = new KeyImpl(keyBytes, keyType);
    }

    /**
     * Constructs a KerberosKey from a principal's password.
     *
     * @param principal the principal that this password belongs to
     * @param password the password that should be used to compute the key
     * @param algorithm the name for the algorithm that this key wil be
     * used for. This parameter may be null in which case "DES" will be
     * assumed.
     */
    public KerberosKey(KerberosPrincipal principal,
		       char[] password,
		       String algorithm) {

	this.principal = principal;
	// Pass principal in for salt
	key = new KeyImpl(principal, password, algorithm);
    }

    /**
     * Returns the principal that this key belongs to.
     *
     * @return the principal this key belongs to.
     */
    public final KerberosPrincipal getPrincipal() {
	if (destroyed)
	    throw new IllegalStateException("This key is no longer valid");
	return principal;
    }

    /**
     * Returns the key version number.
     *
     * @return the key version number.
     */
    public final int getVersionNumber() {
	if (destroyed)
	    throw new IllegalStateException("This key is no longer valid");
	return versionNum;
    }

    /**
     * Returns the key type for this long-term key.
     *
     * @return the key type.
     */
    public final int getKeyType() {
	if (destroyed)
	    throw new IllegalStateException("This key is no longer valid");
	return key.getKeyType();
    }

    /*
     * Methods from java.security.Key
     */
    
    /** 
     * Returns the standard algorithm name for this key. For 
     * example, "DES" would indicate that this key is a DES key. 
     * See Appendix A in the <a href= 
     * "../../../../../guide/security/CryptoSpec.html#AppA"> 
     * Java Cryptography Architecture API Specification &amp; Reference
     * </a> 
     * for information about standard algorithm names.
     * 
     * @return the name of the algorithm associated with this key.
     */
    public final String getAlgorithm() {
	if (destroyed)
	    throw new IllegalStateException("This key is no longer valid");
	return key.getAlgorithm();
    }
    
    /**
     * Returns the name of the encoding format for this secret key.
     *
     * @return the String "RAW"
     */
    public final String getFormat() {
	if (destroyed)
	    throw new IllegalStateException("This key is no longer valid");
	return key.getFormat();
    }
    
    /**
     * Returns the key material of this secret key.
     *
     * @return the key material
     */
    public final byte[] getEncoded() {
	if (destroyed)
	    throw new IllegalStateException("This key is no longer valid");
	return key.getEncoded();
    }

    /**
     * Destroys this key. A call to any of its other methods after this
     * will cause an  IllegalStateException to be thrown.
     *
     * @throws DestroyFailedException if some error occurs while destorying 
     * this key.
     */
    public void destroy() throws DestroyFailedException {
	if (!destroyed) {
	    key.destroy();
	    principal = null;
	    destroyed = true;
	}
    }


    /** Determines if this key has been destroyed.*/
    public boolean isDestroyed() {
	return destroyed;
    }
   
   public String toString() {

	return "Kerberos Principal " + principal.toString() +
		"Key Version " + versionNum +
		"key "	+ key.toString();
   }

}
