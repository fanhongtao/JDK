/*
 * @(#)FontFormatException.java	1.7 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt;

/**
 * Thrown by method createFont in the <code>Font</code> class to indicate 
 * that the specified font is bad. 
 *
 * @author  Parry Kejriwal
 * @version 1.7, 01/23/03
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
