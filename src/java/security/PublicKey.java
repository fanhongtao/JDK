/*
 * @(#)PublicKey.java	1.20 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
