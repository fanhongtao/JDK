/*
 * @(#)ECField.java	1.4 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.security.spec;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * This interface represents an elliptic curve (EC) finite field.
 * All specialized EC fields must implements this interface.
 *
 * @see ECFieldFp
 * @see ECFieldF2m
 *
 * @author Valerie Peng
 * @version 1.4, 12/19/03
 *
 * @since 1.5
 */
public interface ECField {
    /**
     * Returns the field size in bits. Note: For prime finite
     * field ECFieldFp, size of prime p in bits is returned.
     * For characteristic 2 finite field ECFieldF2m, m is returned.
     * @return the field size in bits.
     */
    int getFieldSize();
}
