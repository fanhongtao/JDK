/*
 * @(#)PublicKey.java	1.19 98/07/01
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

import java.io.*;

/**
 * <p>A public key.  This interface contains no methods or constants.  It
 * merely serves to group (and provide type safety for) all public key 
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
 * 
 * @version 1.16 97/01/17
 */
public interface PublicKey extends Key {
}
