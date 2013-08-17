/*
 * @(#)EncodedKeySpec.java	1.12 98/12/03
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
 * This class represents a public or private key in encoded format.
 *
 * @author Jan Luehe
 *
 * @version 1.12 98/12/03
 *
 * @see java.security.Key
 * @see java.security.KeyFactory
 * @see KeySpec
 * @see X509EncodedKeySpec
 * @see PKCS8EncodedKeySpec
 *
 * @since JDK1.2
 */

public abstract class EncodedKeySpec implements KeySpec {

    private byte[] encodedKey;

    /**
     * Creates a new EncodedKeySpec with the given encoded key.
     *
     * @param encodedKey the encoded key.
     */
    public EncodedKeySpec(byte[] encodedKey) {
	this.encodedKey = (byte[])encodedKey.clone();
    }

    /**
     * Returns the encoded key.
     *
     * @return the encoded key.
     */
    public byte[] getEncoded() {
	return (byte[])this.encodedKey.clone();
    }

    /**
     * Returns the name of the encoding format associated with this
     * key specification.
     *
     * <p>If the opaque representation of a key
     * (see {@link java.security.Key Key}) can be transformed
     * (see {@link java.security.KeyFactory KeyFactory})
     * into this key specification (or a subclass of it),
     * <code>getFormat</code> called
     * on the opaque key returns the same value as the
     * <code>getFormat</code> method
     * of this key specification.
     *
     * @return a string representation of the encoding format.
     */
    public abstract String getFormat();
}
