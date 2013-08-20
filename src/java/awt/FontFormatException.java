/*
 * @(#)FontFormatException.java	1.8 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt;

/**
 * Thrown by method createFont in the <code>Font</code> class to indicate 
 * that the specified font is bad. 
 *
 * @author  Parry Kejriwal
 * @version 1.8, 12/19/03
 * @see     java.awt.Font
 * @since   1.3
 */
public
class FontFormatException extends Exception {
    /**
     * Report a FontFormatException for the reason specified.
     * @param reason a <code>String</code> message indicating why
     *        the font is not accepted.
     */
    public FontFormatException(String reason) {
      super (reason);
    }
}
