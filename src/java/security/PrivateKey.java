/*
 * @(#)PrivateKey.java	1.25 00/02/02
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
 * @version 1.25 00/02/02
 * @author Benjamin Renaud
 * @author Josh Bloch
 */

public interface PrivateKey extends Key {
    // Declare serialVersionUID to be compatible with JDK1.1
    static final long serialVersionUID = 6034044314589513430L;
}
