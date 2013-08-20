/*
 * @(#)ECKey.java	1.2 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.security.interfaces;

import java.security.spec.ECParameterSpec;

/**
 * The interface to an elliptic curve (EC) key.
 *
 * @author Valerie Peng
 *
 * @version 1.2, 12/19/03
 * @since 1.5
 */
public interface ECKey {
    /**
     * Returns the domain parameters associated
     * with this key. The domain parameters are 
     * either explicitly specified or implicitly 
     * created during key generation.
     * @return the associated domain parameters.
     */
    ECParameterSpec getParams();
}
