/*
 * @(#)PublicKey.java	1.26 98/12/03
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
 * <p>A public key. This interface contains no methods or constants.
 * It merely serves to group (and provide type safety for) all public key
 * interfaces.
 *
 * Note: The specialized public key interfaces extend this interface.
 * See, for example, the DSAPublicKey interface in
 * <code>java.security.interfaces</code>.
 *
 * @see Key
 * @see PrivateKey
 * @see Certificate
 * @see Signature#initVerify
 * @see java.security.interfaces.DSAPublicKey
 * @see java.security.interfaces.RSAPublicKey
 *
 * @version 1.26 98/12/03
 */

public interface PublicKey extends Key {
    // Declare serialVersionUID to be compatible with JDK1.1
    static final long serialVersionUID = 7187392471159151072L;
}
