/*
 * @(#)PrivateKey.java	1.15 98/07/01
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
