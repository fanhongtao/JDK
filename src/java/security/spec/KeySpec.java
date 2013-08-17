/*
 * @(#)KeySpec.java	1.9 98/06/29
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
 * A (transparent) specification of the key material
 * that constitutes a cryptographic key.
 *
 * <p>If the key is stored on a hardware device, its
 * specification may contain information that helps identify the key on the
 * device.
 *
 * <P> A key may be specified in an algorithm-specific way, or in an
 * algorithm-independent encoding format (such as ASN.1).
 * For example, a DSA private key may be specified by its components
 * <code>x</code>, <code>p</code>, <code>q</code>, and <code>g</code>
 * (see {@link DSAPrivateKeySpec}), or it may be
 * specified using its DER encoding
 * (see {@link PKCS8EncodedKeySpec}).
 *
 * <P> This interface contains no methods or constants. Its only purpose
 * is to group (and provide type safety for) all key specifications.
 * All key specifications must implement this interface.
 *
 * @author Jan Luehe
 *
 * @version 1.9, 00/05/10
 *
 * @see java.security.Key
 * @see java.security.KeyFactory
 * @see EncodedKeySpec
 * @see X509EncodedKeySpec
 * @see PKCS8EncodedKeySpec
 * @see DSAPrivateKeySpec
 * @see DSAPublicKeySpec
 *
 * @since JDK1.2
 */

public interface KeySpec { }
