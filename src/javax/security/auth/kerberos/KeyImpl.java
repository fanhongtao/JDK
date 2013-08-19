/*
 * @(#)KeyImpl.java	1.9 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.security.auth.kerberos;

import java.io.*;
import java.util.Arrays;
import javax.crypto.SecretKey;
import javax.security.auth.Destroyable;
import javax.security.auth.DestroyFailedException;
import sun.misc.HexDumpEncoder;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.EncryptedData;
import sun.security.krb5.KrbException;
import sun.security.krb5.KrbCryptoException;
import sun.security.util.DerValue;

/**
 * This class encapsulates a Kerberos encryption key. It is not associated
 * with a principal and may represent an ephemeral session key.
 *
 * @author Mayank Upadhyay
 * @version 1.9, 01/23/03
 * @since 1.4
 */
class KeyImpl implements SecretKey, Destroyable, Serializable {

    private transient byte[] keyBytes;
    private transient int keyType;
    private transient boolean destroyed = false;


    /**
     * Constructs a KeyImpl from the given bytes.
     * 
     * @param keyBytes the raw bytes for the secret key
     * @param keyType the key type for the secret key as defined by the
     * Kerberos protocol specification.
     */
    public KeyImpl(byte[] keyBytes, 
		       int keyType) {
	this.keyBytes = (byte[]) keyBytes.clone();
	this.keyType = keyType;
    }

    /**
     * Constructs a KeyImpl from a password.
     *
     * @param principal the principal from which to derive the salt
     * @param password the password that should be used to compute the
     * key.
     * @param algorithm the name for the algorithm that this key wil be
     * used for. This parameter may be null in which case "DES" will be
     * assumed.
     */
    public KeyImpl(KerberosPrincipal principal,
		   char[] password,
		   String algorithm) {

	try {
	    PrincipalName princ = new PrincipalName(principal.getName());
	    EncryptionKey key = 
		new EncryptionKey(new StringBuffer().append(password), 
				princ.getSalt(),algorithm);
	    this.keyBytes = key.getBytes();
	    this.keyType = key.getEType();
	} catch (KrbException e) {
	    throw new IllegalArgumentException(e.getMessage());
	}
    }

    /**
     * Returns the keyType for this key as defined in the Kerberos Spec.
     */
    public final int getKeyType() {
	if (destroyed)
	    throw new IllegalStateException("This key is no longer valid");
	return keyType;
    }

    /*
     * Methods from java.security.Key
     */
    
    public final String getAlgorithm() {
	return getAlgorithmName(keyType);
    }

    private String getAlgorithmName(int eType) {
	if (destroyed)
	    throw new IllegalStateException("This key is no longer valid");
	if (eType == EncryptedData.ETYPE_NULL)
	    return "NULL";
	else 
	    //	    if (eType == ETYPE_DES_CBC_CRC ||
	    //		eType == ETYPE_DES_CBC_MD5)
	    return "DES";
    }
    
    public final String getFormat() {
	if (destroyed)
	    throw new IllegalStateException("This key is no longer valid");
	return "RAW";
    }
    
    public final byte[] getEncoded() {
	if (destroyed)
	    throw new IllegalStateException("This key is no longer valid");
	return (byte[])keyBytes.clone();
    }

    public void destroy() throws DestroyFailedException {
	if (!destroyed) {
	    Arrays.fill(keyBytes, (byte) 0);
	    destroyed = true;
	}
    }

    public boolean isDestroyed() {
	return destroyed;
    }

   /**
    * @serialData this <code>KeyImpl</code> is serialized by
    *			writing out the ASN1 Encoded bytes of the
    *			encryption key. The ASN1 encoding is defined in 
    *			RFC1510 and as  follows:
    *			EncryptionKey ::=   SEQUENCE {
    *				keytype[0]    INTEGER,
    *				keyvalue[1]   OCTET STRING    	
    *				}
    **/

    private synchronized void writeObject(ObjectOutputStream ois) 
		throws IOException {
	if (destroyed) {
	   throw new IOException ("This key is no longer valid");
	}

	try {
	   ois.writeObject((new EncryptionKey(keyType,keyBytes)).asn1Encode());
	} catch (Asn1Exception ae) {
	   throw new IOException(ae.getMessage());
	}
    }

    private synchronized void readObject(ObjectInputStream ois) 
		throws IOException , ClassNotFoundException {
	try {
	    EncryptionKey encKey = new EncryptionKey(new 
				     DerValue((byte[])ois.readObject()));
	    keyType = encKey.getEType();
	    keyBytes = encKey.getBytes();
	} catch (Asn1Exception ae) {
	    throw new IOException (ae.getMessage());
	}
    }

    public String toString() {
	HexDumpEncoder hd = new HexDumpEncoder();	
	return new String("EncryptionKey: keyType=" + keyType
                          + " keyBytes (hex dump)="
                          + (keyBytes == null || keyBytes.length == 0 ?
                             " Empty Key" :
                             '\n' + hd.encode(keyBytes)
                          + '\n'));

	
    }
}
