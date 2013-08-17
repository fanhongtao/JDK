/*
 * @(#)Transparency.java	1.10 98/03/18
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.awt;

/**
 * The <code>Transparency</code> interface defines the common transparency
 * modes for implementing classes.
 * @version 10 Feb 1997
 */
public interface Transparency {

    /**
     * Represents image data that is guaranteed to be completely opaque,
     * meaning that all pixels have an alpha value of 1.0.
     */
    public final static int OPAQUE            = 1;

    /**
     * Represents image data that is guaranteed to be either completely
     * opaque, with an alpha value of 1.0, or completely transparent,
     * with an alpha value of 0.0.
     */
    public final static int BITMASK = 2;

    /**
     * Represents image data that contains or might contain arbitrary
     * alpha values between and including 0.0 and 1.0.
     */
    public final static int TRANSLUCENT        = 3;

    /**
     * Returns the type of this <code>Transparency</code>.
     * @return the field type of this <code>Transparency</code>, which is
     *		either OPAQUE, BITMASK or TRANSLUCENT. 
     */
    public int getTransparency();
}
