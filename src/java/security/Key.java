/*
 * @(#)Key.java	1.31 98/07/01
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

/**
 * The Key interface is the top-level interface for all keys. It
 * defines the functionality shared by all key objects. All keys
 * have three characteristics:
 * 
 * <UL>
 * 
 * <LI>An Algorithm
 * 
 * <P>This is the key algorithm for that key. The key algorithm is usually
 * an encryption or asymmetric operation algorithm (such as DSA or
 * RSA), which will work with those algorithms and with related
 * algorithms (such as MD5 with RSA, SHA-1 with RSA, Raw DSA, etc.)
 * The name of the algorithm of a key is obtained using the 
 * <a href = "#getAlgorithm">getAlgorithm</a> method.<P>
 * 
 * <LI>An Encoded Form
 * 
 * <P>This is an external encoded form for the key used when a standard
 * representation of the key is needed outside the Java Virtual Machine,
 * as when transmitting the key to some other party. The key
 * is encoded according to a standard format (such as X.509
 * or PKCS#8), and is returned using the 
 * <a href = "#getEncoded">getEncoded</a> method.<P>
 * 
 * <LI>A Format
 * 
 * <P>This is the name of the format of the encoded key. It is returned 
 * by the <a href = "#getFormat">getFormat</a> method.<P>
 * 
 * </UL>
 * 
 * Keys are generally obtained through key generators, certificates,
 * or various Identity classes used to manage keys. There are no
 * provisions in this release for the parsing of encoded keys and
 * certificates.
 *
 * @see PublicKey
 * @see PrivateKey
 * @see KeyPair
 * @see KeyPairGenerator
 * @see Identity
 * @see IdentityScope
 * @see Signer
 *
 * @version 1.28, 97/01/29
 * @author Benjamin Renaud 
 */

public interface Key  extends java.io.Serializable {

    /**
     * Returns the standard algorithm name this key is for. For
     * example, "DSA" would indicate that this key is a DSA key. 
     * Note that this method may return null, when the
     * algorithm this key is for is unknown.
     * 
     * <p>See Appendix A in the <a href=
     * "../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard algorithm names.
     * 
     * @return the name of the algorithm this key is for, or null
     * if the algorithm this key is for is unknown.  
     */
    public String getAlgorithm();

    /**
     * Returns the format used to encode the key or null if the key does not
     * support encoding.
     * 
     * @return the format used to encode the key.
     */
    public String getFormat();

    /**
     * Returns the encoded key.
     * 
     * @return the encoded key, or null if the key does not support
     * encoding.
     *
     */
    public byte[] getEncoded();
}
