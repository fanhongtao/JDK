/*
 * @(#)KeyStore.java	1.32 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security;

import java.io.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.*;

/**
 * This class represents an in-memory collection of keys and certificates.
 * It manages two types of entries:
 *
 * <ul>
 * <li><b>Key Entry</b>
 * <p>This type of keystore entry holds very sensitive cryptographic key
 * information, which is stored in a protected format to prevent unauthorized
 * access.
 *
 * <p>Typically, a key stored in this type of entry is a secret key, or a
 * private key accompanied by the certificate chain for the corresponding
 * public key.
 *
 * <p>Private keys and certificate chains are used by a given entity for
 * self-authentication. Applications for this authentication include software
 * distribution organizations which sign JAR files as part of releasing
 * and/or licensing software.<p>
 *
 * <li><b>Trusted Certificate Entry</b>
 * <p>This type of entry contains a single public key certificate belonging to
 * another party. It is called a <i>trusted certificate</i> because the
 * keystore owner trusts that the public key in the certificate indeed belongs
 * to the identity identified by the <i>subject</i> (owner) of the
 * certificate. 
 *
 * <p>This type of entry can be used to authenticate other parties.
 * </ul>
 *
 * <p>Each entry in a keystore is identified by an "alias" string. In the
 * case of private keys and their associated certificate chains, these strings
 * distinguish among the different ways in which the entity may authenticate
 * itself. For example, the entity may authenticate itself using different
 * certificate authorities, or using different public key algorithms.
 *
 * <p>Whether keystores are persistent, and the mechanisms used by the
 * keystore if it is persistent, are not specified here. This allows
 * use of a variety of techniques for protecting sensitive (e.g., private or
 * secret) keys. Smart cards or other integrated cryptographic engines
 * (SafeKeyper) are one option, and simpler mechanisms such as files may also
 * be used (in a variety of formats).
 *
 * <p>There are two ways to request a KeyStore object: by
 * specifying either just a keystore type, or both a keystore type
 * and a package provider.
 *
 * <ul>
 * <li>If just a keystore type is specified:
 * <pre>
 *      KeyStore ks = KeyStore.getInstance("JKS");
 * </pre>
 * the system will determine if there is an implementation of the keystore type
 * requested available in the environment, and if there is more than one, if
 * there is a preferred one.<p>
 * 
 * <li>If both a keystore type and a package provider are specified:
 * <pre>
 *      KeyStore ks = KeyStore.getInstance("JKS", "SUN");
 * </pre>
 * the system will determine if there is an implementation of the
 * keystore type in the package requested, and throw an exception if there
 * is not.
 *
 * </ul>
 *
 * <p>Before a keystore can be accessed, it must be
 * {@link #load(java.io.InputStream, char[]) loaded}. In order to create 
 * an empty keystore, you pass <code>null</code>
 * as the <code>InputStream</code> argument to the <code>load</code> method.
 *
 * @author Jan Luehe
 *
 * @version 1.32, 01/23/03
 *
 * @see java.security.PrivateKey
 * @see java.security.cert.Certificate
 *
 * @since 1.2
 */

public class KeyStore {

    /*
     * Constant to lookup in the Security properties file to determine
     * the default keystore type.
     * In the Security properties file, the default keystore type is given as:
     * <pre>
     * keystore.type=jks
     * </pre>
     */  
    private static final String KEYSTORE_TYPE = "keystore.type";

    // The keystore type
    private String type;

    // The provider
    private Provider provider;

    // The provider implementation
    private KeyStoreSpi keyStoreSpi;

    // Has this keystore been initialized (loaded)?
    private boolean initialized = false;

    /**
     * Creates a KeyStore object of the given type, and encapsulates the given
     * provider implementation (SPI object) in it.
     *
     * @param keyStoreSpi the provider implementation.
     * @param provider the provider.
     * @param type the keystore type.
     */
    protected KeyStore(KeyStoreSpi keyStoreSpi, Provider provider, String type)
    {
	this.keyStoreSpi = keyStoreSpi;
	this.provider = provider;
	this.type = type;
    }

    /**
     * Generates a keystore object of the given type.
     * 
     * <p>If the default provider package provides a keystore implementation
     * of the given type, an instance of <code>KeyStore</code> containing that
     * implementation is returned. If the requested keystore type is not
     * available in the default package, other packages are searched.
     *
     * @param type the type of keystore. 
     * See Appendix A in the <a href=
     * "../../../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard keystore types.
     *
     * @return a keystore object of the specified type.
     *
     * @exception KeyStoreException if the requested keystore type is
     * not available in the default provider package or any of the other
     * provider packages that were searched.  
     */
    public static KeyStore getInstance(String type) 
	throws KeyStoreException
    {
	try {
	    Object[] objs = Security.getImpl(type, "KeyStore", (String)null);
	    return new KeyStore((KeyStoreSpi)objs[0], (Provider)objs[1], type);
	} catch(NoSuchAlgorithmException nsae) {
	    throw new KeyStoreException(type + " not found");
	} catch(NoSuchProviderException nspe) {
	    throw new KeyStoreException(type + " not found");
	}
    }

    /**
     * Generates a keystore object for the specified keystore
     * type from the specified provider.
     *
     * @param type the type of keystore.
     * See Appendix A in the <a href=
     * "../../../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard keystore types.
     *
     * @param provider the name of the provider.
     *
     * @return a keystore object of the specified type, as
     * supplied by the specified provider.
     *
     * @exception KeyStoreException if the requested keystore type is not
     * available from the provider.
     * 
     * @exception NoSuchProviderException if the provider has not been
     * configured.
     *
     * @exception IllegalArgumentException if the provider name is null
     * or empty.
     *
     * @see Provider
     */
     public static KeyStore getInstance(String type, String provider)
	throws KeyStoreException, NoSuchProviderException
    {
	if (provider == null || provider.length() == 0)
	    throw new IllegalArgumentException("missing provider");
	try {
	    Object[] objs = Security.getImpl(type, "KeyStore", provider);
	    return new KeyStore((KeyStoreSpi)objs[0], (Provider)objs[1], type);
	} catch(NoSuchAlgorithmException nsae) {
	    throw new KeyStoreException(type + " not found");
	}
     }

    /**
     * Generates a keystore object for the specified keystore
     * type from the specified provider. Note: the <code>provider</code> 
     * doesn't have to be registered. 
     *
     * @param type the type of keystore.
     * See Appendix A in the <a href=
     * "../../../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard keystore types.
     *
     * @param provider the provider.
     *
     * @return a keystore object of the specified type, as
     * supplied by the specified provider.
     *
     * @exception KeyStoreException if the requested keystore type is not
     * available from the provider.
     *
     * @exception IllegalArgumentException if the <code>provider</code> is
     * null.
     *
     * @see Provider
     *
     * @since 1.4
     */
     public static KeyStore getInstance(String type, Provider provider)
	throws KeyStoreException
    {
	if (provider == null)
	    throw new IllegalArgumentException("missing provider");
	try {
	    Object[] objs = Security.getImpl(type, "KeyStore", provider);
	    return new KeyStore((KeyStoreSpi)objs[0], (Provider)objs[1], type);
	} catch(NoSuchAlgorithmException nsae) {
	    throw new KeyStoreException(type + " not found");
	}
     }

    /** 
     * Returns the provider of this keystore.
     * 
     * @return the provider of this keystore.
     */
    public final Provider getProvider()
    {
	return this.provider;
    }

    /**
     * Returns the type of this keystore.
     *
     * @return the type of this keystore.
     */
    public final String getType()
    {
	return this.type;
    }

    /**
     * Returns the key associated with the given alias, using the given
     * password to recover it.
     *
     * @param alias the alias name
     * @param password the password for recovering the key
     *
     * @return the requested key, or null if the given alias does not exist
     * or does not identify a <i>key entry</i>.
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded).
     * @exception NoSuchAlgorithmException if the algorithm for recovering the
     * key cannot be found
     * @exception UnrecoverableKeyException if the key cannot be recovered
     * (e.g., the given password is wrong).
     */
    public final Key getKey(String alias, char[] password)
	throws KeyStoreException, NoSuchAlgorithmException,
	    UnrecoverableKeyException
    {
	if (!initialized) {
	    throw new KeyStoreException("Uninitialized keystore");
	}
	return keyStoreSpi.engineGetKey(alias, password);
    }

    /**
     * Returns the certificate chain associated with the given alias.
     *
     * @param alias the alias name
     *
     * @return the certificate chain (ordered with the user's certificate first
     * and the root certificate authority last), or null if the given alias
     * does not exist or does not contain a certificate chain (i.e., the given 
     * alias identifies either a <i>trusted certificate entry</i> or a
     * <i>key entry</i> without a certificate chain).
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded).
     */
    public final Certificate[] getCertificateChain(String alias)
	throws KeyStoreException
    {
	if (!initialized) {
	    throw new KeyStoreException("Uninitialized keystore");
	}
	return keyStoreSpi.engineGetCertificateChain(alias);
    }

    /**
     * Returns the certificate associated with the given alias.
     *
     * <p>If the given alias name identifies a
     * <i>trusted certificate entry</i>, the certificate associated with that
     * entry is returned. If the given alias name identifies a
     * <i>key entry</i>, the first element of the certificate chain of that
     * entry is returned, or null if that entry does not have a certificate
     * chain.
     *
     * @param alias the alias name
     *
     * @return the certificate, or null if the given alias does not exist or
     * does not contain a certificate.
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded).
     */
    public final Certificate getCertificate(String alias)
	throws KeyStoreException
    {
	if (!initialized) {
	    throw new KeyStoreException("Uninitialized keystore");
	}
	return keyStoreSpi.engineGetCertificate(alias);
    }

    /**
     * Returns the creation date of the entry identified by the given alias.
     *
     * @param alias the alias name
     *
     * @return the creation date of this entry, or null if the given alias does
     * not exist
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded).
     */
    public final Date getCreationDate(String alias)
	throws KeyStoreException
    {
	if (!initialized) {
	    throw new KeyStoreException("Uninitialized keystore");
	}
	return keyStoreSpi.engineGetCreationDate(alias);
    }

    /**
     * Assigns the given key to the given alias, protecting it with the given
     * password.
     *
     * <p>If the given key is of type <code>java.security.PrivateKey</code>,
     * it must be accompanied by a certificate chain certifying the
     * corresponding public key.
     *
     * <p>If the given alias already exists, the keystore information
     * associated with it is overridden by the given key (and possibly
     * certificate chain).
     *
     * @param alias the alias name
     * @param key the key to be associated with the alias
     * @param password the password to protect the key
     * @param chain the certificate chain for the corresponding public
     * key (only required if the given key is of type
     * <code>java.security.PrivateKey</code>).
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded), the given key cannot be protected, or this operation fails
     * for some other reason
     */
    public final void setKeyEntry(String alias, Key key, char[] password,
				  Certificate[] chain)
	throws KeyStoreException
    {
	if (!initialized) {
	    throw new KeyStoreException("Uninitialized keystore");
	}
	if ((key instanceof PrivateKey) && (chain==null || chain.length==0)) {
	    throw new IllegalArgumentException("Private key must be "
					       + "accompanied by certificate "
					       + "chain");
	}
	keyStoreSpi.engineSetKeyEntry(alias, key, password, chain);
    }

    /**
     * Assigns the given key (that has already been protected) to the given
     * alias.
     * 
     * <p>If the protected key is of type
     * <code>java.security.PrivateKey</code>, it must be accompanied by a
     * certificate chain certifying the corresponding public key. If the
     * underlying keystore implementation is of type <code>jks</code>,
     * <code>key</code> must be encoded as an
     * <code>EncryptedPrivateKeyInfo</code> as defined in the PKCS #8 standard.
     *
     * <p>If the given alias already exists, the keystore information
     * associated with it is overridden by the given key (and possibly
     * certificate chain).
     *
     * @param alias the alias name
     * @param key the key (in protected format) to be associated with the alias
     * @param chain the certificate chain for the corresponding public
     * key (only useful if the protected key is of type
     * <code>java.security.PrivateKey</code>).
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded), or if this operation fails for some other reason.
     */
    public final void setKeyEntry(String alias, byte[] key,
				  Certificate[] chain)
	throws KeyStoreException
    {
	if (!initialized) {
	    throw new KeyStoreException("Uninitialized keystore");
	}
	keyStoreSpi.engineSetKeyEntry(alias, key, chain);
    }

    /**
     * Assigns the given certificate to the given alias.
     *
     * <p>If the given alias already exists in this keystore and identifies a
     * <i>trusted certificate entry</i>, the certificate associated with it is
     * overridden by the given certificate.
     *
     * @param alias the alias name
     * @param cert the certificate
     *
     * @exception KeyStoreException if the keystore has not been initialized,
     * or the given alias already exists and does not identify a
     * <i>trusted certificate entry</i>, or this operation fails for some
     * other reason.
     */
    public final void setCertificateEntry(String alias, Certificate cert)
	throws KeyStoreException
    {
	if (!initialized) {
	    throw new KeyStoreException("Uninitialized keystore");
	}
	keyStoreSpi.engineSetCertificateEntry(alias, cert);
    }

    /**
     * Deletes the entry identified by the given alias from this keystore.
     *
     * @param alias the alias name
     *
     * @exception KeyStoreException if the keystore has not been initialized,
     * or if the entry cannot be removed.
     */
    public final void deleteEntry(String alias)
	throws KeyStoreException
    {
	if (!initialized) {
	    throw new KeyStoreException("Uninitialized keystore");
	}
	keyStoreSpi.engineDeleteEntry(alias);
    }

    /**
     * Lists all the alias names of this keystore.
     *
     * @return enumeration of the alias names
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded).
     */
    public final Enumeration aliases()
	throws KeyStoreException
    {
	if (!initialized) {
	    throw new KeyStoreException("Uninitialized keystore");
	}
	return keyStoreSpi.engineAliases();
    }

    /**
     * Checks if the given alias exists in this keystore.
     *
     * @param alias the alias name
     *
     * @return true if the alias exists, false otherwise
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded).
     */
    public final boolean containsAlias(String alias)
	throws KeyStoreException
    {
	if (!initialized) {
	    throw new KeyStoreException("Uninitialized keystore");
	}
	return keyStoreSpi.engineContainsAlias(alias);
    }

    /**
     * Retrieves the number of entries in this keystore.
     *
     * @return the number of entries in this keystore
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded).
     */
    public final int size()
	throws KeyStoreException
    {
	if (!initialized) {
	    throw new KeyStoreException("Uninitialized keystore");
	}
	return keyStoreSpi.engineSize();
    }

    /**
     * Returns true if the entry identified by the given alias is a
     * <i>key entry</i>, and false otherwise.
     *
     * @param alias the alias for the keystore entry to be checked
     *
     * @return true if the entry identified by the given alias is a
     * <i>key entry</i>, false otherwise.
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded).
     */
    public final boolean isKeyEntry(String alias)
	throws KeyStoreException
    {
	if (!initialized) {
	    throw new KeyStoreException("Uninitialized keystore");
	}
	return keyStoreSpi.engineIsKeyEntry(alias);
    }

    /**
     * Returns true if the entry identified by the given alias is a
     * <i>trusted certificate entry</i>, and false otherwise.
     *
     * @param alias the alias for the keystore entry to be checked
     *
     * @return true if the entry identified by the given alias is a
     * <i>trusted certificate entry</i>, false otherwise.
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded).
     */
    public final boolean isCertificateEntry(String alias)
	throws KeyStoreException
    {
	if (!initialized) {
	    throw new KeyStoreException("Uninitialized keystore");
	}
	return keyStoreSpi.engineIsCertificateEntry(alias);
    }

    /**
     * Returns the (alias) name of the first keystore entry whose certificate
     * matches the given certificate.
     *
     * <p>This method attempts to match the given certificate with each
     * keystore entry. If the entry being considered
     * is a <i>trusted certificate entry</i>, the given certificate is
     * compared to that entry's certificate. If the entry being considered is
     * a <i>key entry</i>, the given certificate is compared to the first
     * element of that entry's certificate chain (if a chain exists).
     *
     * @param cert the certificate to match with.
     *
     * @return the (alias) name of the first entry with matching certificate,
     * or null if no such entry exists in this keystore.
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded).
     */
    public final String getCertificateAlias(Certificate cert)
	throws KeyStoreException
    {
	if (!initialized) {
	    throw new KeyStoreException("Uninitialized keystore");
	}
	return keyStoreSpi.engineGetCertificateAlias(cert);
    }

    /**
     * Stores this keystore to the given output stream, and protects its
     * integrity with the given password.
     *
     * @param stream the output stream to which this keystore is written.
     * @param password the password to generate the keystore integrity check
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded).
     * @exception IOException if there was an I/O problem with data
     * @exception NoSuchAlgorithmException if the appropriate data integrity
     * algorithm could not be found
     * @exception CertificateException if any of the certificates included in
     * the keystore data could not be stored
     */
    public final void store(OutputStream stream, char[] password)
	throws KeyStoreException, IOException, NoSuchAlgorithmException,
	    CertificateException
    {
	if (!initialized) {
	    throw new KeyStoreException("Uninitialized keystore");
	}
	keyStoreSpi.engineStore(stream, password);
    }

    /**
     * Loads this KeyStore from the given input stream.
     *
     * <p>If a password is given, it is used to check the integrity of the
     * keystore data. Otherwise, the integrity of the keystore is not checked.
     *
     * <p>In order to create an empty keystore, or if the keystore cannot
     * be initialized from a stream (e.g., because it is stored on a hardware
     * token device), you pass <code>null</code>
     * as the <code>stream</code> argument.
     *
     * <p> Note that if this KeyStore has already been loaded, it is
     * reinitialized and loaded again from the given input stream.
     *
     * @param stream the input stream from which the keystore is loaded, or
     * null if an empty keystore is to be created.
     * @param password the (optional) password used to check the integrity of
     * the keystore.
     *
     * @exception IOException if there is an I/O or format problem with the
     * keystore data
     * @exception NoSuchAlgorithmException if the algorithm used to check
     * the integrity of the keystore cannot be found
     * @exception CertificateException if any of the certificates in the
     * keystore could not be loaded
     */
    public final void load(InputStream stream, char[] password)
	throws IOException, NoSuchAlgorithmException, CertificateException
    {
	keyStoreSpi.engineLoad(stream, password);
	initialized = true;
    }

    /**
     * Returns the default keystore type as specified in the Java security
     * properties file, or the string &quot;jks&quot; (acronym for &quot;Java keystore&quot;)
     * if no such property exists.
     * The Java security properties file is located in the file named
     * &lt;JAVA_HOME&gt;/lib/security/java.security, where &lt;JAVA_HOME&gt;
     * refers to the directory where the SDK was installed.
     *
     * <p>The default keystore type can be used by applications that do not
     * want to use a hard-coded keystore type when calling one of the
     * <code>getInstance</code> methods, and want to provide a default keystore
     * type in case a user does not specify its own.
     *
     * <p>The default keystore type can be changed by setting the value of the
     * "keystore.type" security property (in the Java security properties
     * file) to the desired keystore type.
     *
     * @return the default keystore type as specified in the 
     * Java security properties file, or the string &quot;jks&quot;
     * if no such property exists.
     */
    public final static String getDefaultType() {
	String kstype;
	kstype = (String)AccessController.doPrivileged(new PrivilegedAction() {
	    public Object run() {
		return Security.getProperty(KEYSTORE_TYPE);
	    }
	});
	if (kstype == null) {
	    kstype = "jks";
	}
	return kstype;
    }
}
