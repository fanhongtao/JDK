/*
 * @(#)ECPublicKey.java	1.3 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.security.interfaces;

import java.security.PublicKey;
import java.security.spec.ECPoint;

/**
 * The interface to an elliptic curve (EC) public key.
 *
 * @author Valerie Peng
 *
 * @version 1.3, 12/19/03
 *
 * @see PublicKey
 * @see ECKey
 * @see java.security.spec.ECPoint
 *
 * @since 1.5
 */
public interface ECPublicKey extends PublicKey, ECKey {

   /**
    * The class fingerprint that is set to indicate
    * serialization compatibility.
    */
    static final long serialVersionUID = -3314988629879632826L;

    /**
     * Returns the public point W.
     * @return the public point W.
     */
    ECPoint getW();
}
