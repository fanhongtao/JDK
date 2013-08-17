/*
 * @(#)PrivateKey.java	1.22 98/06/29
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
 * <p>A private key. This interface contains no methods or constants.
 * It merely serves to group (and provide type safety for) all private key
 * interfaces.
 *
 * Note: The specialized private key interfaces extend this interface.
 * See, for example, the DSAPrivateKey interface in
 * <code>java.security.interfaces</code>.
 *
 * @see Key
 * @see PublicKey
 * @see Certificate
 * @see Signature#initVerify
 * @see java.security.interfaces.DSAPrivateKey
 * @see java.security.interfaces.RSAPrivateKey
 * @see java.security.interfaces.RSAPrivateCrtKey
 *
 * @version 1.22 00/05/10
 * @author Benjamin Renaud
 * @author Josh Bloch
 */

public interface PrivateKey extends Key {
    // Declare serialVersionUID to be compatible with JDK1.1
    static final long serialVersionUID = 6034044314589513430L;
}
