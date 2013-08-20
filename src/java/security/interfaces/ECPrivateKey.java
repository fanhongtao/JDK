/*
 * @(#)ECPrivateKey.java	1.3 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.security.interfaces;

import java.math.BigInteger;
import java.security.PrivateKey;

/**
 * The interface to an elliptic curve (EC) private key.
 *
 * @author Valerie Peng
 *
 * @version 1.3, 12/19/03
 *
 * @see PrivateKey
 * @see ECKey
 *
 * @since 1.5
 */
public interface ECPrivateKey extends PrivateKey, ECKey {
   /**
    * The class fingerprint that is set to indicate
    * serialization compatibility.
    */
    static final long serialVersionUID = -7896394956925609184L;

    /**
     * Returns the private value S.
     * @return the private value S.
     */
    BigInteger getS();
}
