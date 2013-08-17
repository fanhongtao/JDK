/*
 * @(#)PrivateKey.java	1.16 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package java.security;


/**
 * <p>A private key.  This interface contains no methods or constants.  It
 * merely serves to group (and provide type safety for) all private key 
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
 * 
 * @version 1.13 97/01/17
 * @author Benjamin Renaud 
 * @author Josh Bloch
 */

public interface PrivateKey extends Key {
}
