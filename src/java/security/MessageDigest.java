/*
 * @(#)MessageDigest.java	1.34 98/07/01
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

import java.util.*;
import java.lang.*;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

/**
 * This MessageDigest class provides the functionality of a message digest
 * algorithm, such as MD5 or SHA. Message digests are secure one-way
 * hash functions that take arbitrary-sized data and output a
 * fixed-length hash value.
 *
 * <p>Like other algorithm-based classes in Java Security, 
 * MessageDigest has two major components:
 *
 * <dl>
 *
 * <dt><b>Message Digest API</b> (Application Program Interface)
 *
 * <dd>This is the interface of methods called by applications needing 
 * message digest services. The API consists of all public methods.
 *
 * <dt><b>Message Digest SPI</b> (Service Provider Interface)
 *
 * <dd>This is the interface implemented by providers that supply
 * specific algorithms. It consists of all methods whose names
 * are prefixed by <em>engine</em>. Each such method is called by    
 * a correspondingly-named public API method. For example,
 * the <code>engineReset</code> method is    
 * called by the <code>reset</code> method.    
 * The SPI methods are abstract; providers must supply a    
 * concrete implementation.
 *
 * </dl>
 *
 * <p>A MessageDigest object starts out initialized. The data is 
 * processed through it using the <a href="#update(byte)">update</a>
 * methods. At any point <a href="#reset">reset</a> can be called
 * to reset the digest. Once all the data to be updated has been
 * updated, one of the <a href="#digest()">digest</a> methods should 
 * be called to complete the hash computation.
 *
 * <p>The <code>digest</code> method can be called once for a given number 
 * of updates. After <code>digest</code> has been called, the MessageDigest
 * object is reset to its initialized state.
 *
 * <p>Implementations are free to implement the Cloneable interface,
 * and doing so will let client applications test cloneability
 * using <code>instanceof Cloneable</code> before cloning: <p>    
 *
 * <pre>
 * MessageDigest md = MessageDigest.getInstance("SHA");
 *
 * if (md instanceof Cloneable) {
 *     md.update(toChapter1);
 *     MessageDigest tc1 = md.clone();
 *     byte[] toChapter1Digest = tc1.digest;
 *     md.update(toChapter2);
 *     ...etc.
 * } else {
 *     throw new DigestException("couldn't make digest of partial content");
 * }
 * </pre>
 *
 * <p>Note that if a given implementation is not cloneable, it is
 * still possible to compute intermediate digests by instantiating
 * several instances, if the number of digests is known in advance.
 *
 * @see DigestInputStream
 * @see DigestOutputStream
 *
 * @version 1.31 97/02/03
 * @author Benjamin Renaud 
 */
public abstract class MessageDigest {

    /* Are we in debugging mode? */
    private static boolean debug = false;

    /* The digest bits, if any. */
    private byte[] digestBits;

    private String algorithm;

    /**
     * Creates a message digest with the specified algorithm name.
     * 
     * @param algorithm the standard name of the digest algorithm. 
     * See Appendix A in the <a href=
     * "../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard algorithm names.
     */
    protected MessageDigest(String algorithm) {
	this.algorithm = algorithm;
    }

    /**
     * Generates a MessageDigest object that implements the specified digest
     * algorithm. If the default provider package contains a MessageDigest
     * subclass implementing the algorithm, an instance of that subclass
     * is returned. If the algorithm is not available in the default 
     * package, other packages are searched.
     *
     * @param algorithm the name of the algorithm requested. 
     * See Appendix A in the <a href=
     * "../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard algorithm names.
     *
     * @return a Message Digest object implementing the specified
     * algorithm.
     *
     * @exception NoSuchAlgorithmException if the algorithm is
     * not available in the caller's environment.  
     */
    public static MessageDigest getInstance(String algorithm) 
    throws NoSuchAlgorithmException { 
	try {
	    return (MessageDigest)Security.getImpl(algorithm, 
						   "MessageDigest", null);
	} catch(NoSuchProviderException e) {
	    throw new InternalError("please send a bug report via " +
				    System.getProperty("java.vendor.url.bug"));
	}
    }

    /**
     * Generates a MessageDigest object implementing the specified
     * algorithm, as supplied from the specified provider, if such an 
     * algorithm is available from the provider.
     *
     * @param algorithm the name of the algorithm requested. 
     * See Appendix A in the <a href=
     * "../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard algorithm names.
     *
     * @param provider the name of the provider.
     *
     * @return a Message Digest object implementing the specified
     * algorithm.
     *
     * @exception NoSuchAlgorithmException if the algorithm is
     * not available in the package supplied by the requested
     * provider.
     *
     * @exception NoSuchProviderException if the provider is not
     * available in the environment. 
     * 
     * @see Provider 
     */
    public static MessageDigest getInstance(String algorithm, String provider)
    throws NoSuchAlgorithmException, NoSuchProviderException {
	return (MessageDigest)Security.getImpl(algorithm, 
					       "MessageDigest", provider);
    }

    /**
     * Updates the digest using the specified byte.    
     * 
     * @param input the byte with which to update the digest.
     */
    public void update(byte input) {
	engineUpdate(input);
    }


    /**
     * Updates the digest using the specified array of bytes, starting
     * at the specified offset.
     * 
     * @param input the array of bytes.
     *
     * @param offset the offset to start from in the array of bytes.
     *
     * @param len the number of bytes to use, starting at 
     * <code>offset</code>.  
     */
    public void update(byte[] input, int offset, int len) {
	engineUpdate(input, offset, len);
    }

    /**
     * Updates the digest using the specified array of bytes.
     * 
     * @param input the array of bytes.
     */
    public void update(byte[] input) {
	engineUpdate(input, 0, input.length);
    }

    /**
     * Completes the hash computation by performing final operations
     * such as padding. The digest is reset after this call is made.
     *
     * @return the array of bytes for the resulting hash value.  
     */
    public byte[] digest() {
	/* Resetting is the responsibility of implementors. */
	digestBits = engineDigest();
	return digestBits;
    }

    /**
     * Performs a final update on the digest using the specified array 
     * of bytes, then completes the digest computation. That is, this
     * method first calls <a href = "#update(byte[])">update</a> on the
     * array, then calls <a href = "#digest()">digest()</a>.
     *
     * @param input the input to be updated before the digest is
     * completed.
     *
     * @return the array of bytes for the resulting hash value.  
     */
    public byte[] digest(byte[] input) {
	update(input);
	return digest();
    }

    /**
     * Helper function that prints unsigned two character hex digits.
     */
    private static void hexDigit(PrintStream p, byte x) {
	char c;
	
	c = (char) ((x >> 4) & 0xf);
	if (c > 9) {
	    c = (char) ((c - 10) + 'a');
	} else {
	    c = (char) (c + '0');
	}
	p.write(c);

	c = (char) (x & 0xf);
	if (c > 9) {
	    c = (char)((c - 10) + 'a');
	} else {
	    c = (char)(c + '0');
	}
	p.write(c);
    }

    /**
     * Returns a string representation of this message digest object.  
     */
    public String toString() {
	ByteArrayOutputStream ou = new ByteArrayOutputStream();
	PrintStream p = new PrintStream(ou);
		
	p.print(this.getClass().getName()+" Message Digest ");
	if (digestBits != null) {
	    p.print("<");
	    for(int i = 0; i < digestBits.length; i++)
 	        hexDigit(p, digestBits[i]);
	    p.print(">");
	} else {
	    p.print("<incomplete>");
	}
	p.println();
	return (ou.toString());
    }

    /**
     * Compares two digests for equality. Does a simple byte compare.
     * 
     * @param digesta one of the digests to compare.
     * 
     * @param digestb the other digest to compare.    
     *
     * @return true if the digests are equal, false otherwise.
     */
    public static boolean isEqual(byte digesta[], byte digestb[]) {
	int	i;
		
	if (digesta.length != digestb.length)
	    return false;

	for (i = 0; i < digesta.length; i++) {
	    if (digesta[i] != digestb[i]) {
		return false;
	    }
	}
	return true;
    }

    /**
     * Resets the digest for further use.
     */
    public void reset() {
	engineReset();
	digestBits = null;
    }

    /** 
     * Returns a string that identifies the algorithm, independent of
     * implementation details. The name should be a standard
     * Java Security name (such as "SHA", "MD5", and so on). 
     * See Appendix A in the <a href=
     * "../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard algorithm names.
     */
    public final String getAlgorithm() {
	return algorithm;
    }

    /**
     * <b>SPI</b>: Updates the digest using the specified byte.
     *
     * @param input the byte to use for the update.
     */
    protected abstract void engineUpdate(byte input);

    /**
     * <b>SPI</b>: Updates the digest using the specified array of bytes,    
     * starting at the specified offset. This should be a no-op if
     * the digest has been finalized.
     *
     * @param input the array of bytes to use for the update.
     *
     * @param offset the offset to start from in the array of bytes.
     *
     * @param len the number of bytes to use, starting at 
     * <code>offset</code>.
     */
    protected abstract void engineUpdate(byte[] input, int offset, int len);

    /**
     * <b>SPI</b>: Completes the hash computation by performing final
     * operations such as padding. Once <code>engineDigest</code> has 
     * been called, the engine should be reset (see <a href =
     * "#reset">reset</a>).  Resetting is the responsibility of the
     * engine implementor.
     *
     * @return the array of bytes for the resulting hash value.  
     */
    protected abstract byte[] engineDigest();

    /**
     * <b>SPI</b>: Resets the digest for further use.
     */
    protected abstract void engineReset();    


    /**    
     * Returns a clone if the implementation is cloneable.    
     * 
     * @return a clone if the implementation is cloneable.
     *
     * @exception CloneNotSupportedException if this is called on an
     * implementation that does not support <code>Cloneable</code>.
     */
    public Object clone() throws CloneNotSupportedException {
	if (this instanceof Cloneable) {
	    return super.clone();
	} else {
	    throw new CloneNotSupportedException();
	}
    }
  

    private void debug(String statement) {
	if (debug) {
	    System.err.println(statement);
	}
    }


}
