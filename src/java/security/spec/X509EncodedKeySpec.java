/*
 * @(#)X509EncodedKeySpec.java	1.8 98/06/29
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.security.spec;

/**
 * This class represents the DER encoding of a public or private key,
 * according to the format specified in the X.509 standard.
 *
 * @author Jan Luehe
 *
 * @version 1.8, 00/05/10
 *
 * @see java.security.Key
 * @see java.security.KeyFactory
 * @see KeySpec
 * @see EncodedKeySpec
 * @see PKCS8EncodedKeySpec
 *
 * @since JDK1.2
 */

public class X509EncodedKeySpec extends EncodedKeySpec {

    /**
     * Creates a new X509EncodedKeySpec with the given encoded key.
     *
     * @param encodedKey the key, which is assumed to be
     * encoded according to the X.509 standard.
     */
    public X509EncodedKeySpec(byte[] encodedKey) {
	super(encodedKey);
    }

    /**
     * Returns the key bytes, encoded according to the X.509 standard.
     *
     * @return the X.509 encoding of the key.
     */
    public byte[] getEncoded() {
	return super.getEncoded();
    }

    /**
     * Returns the name of the encoding format associated with this
     * key specification.
     *
     * @return the string <code>"X.509"</code>.
     */
    public final String getFormat() {
	return "X.509";
    }
}
