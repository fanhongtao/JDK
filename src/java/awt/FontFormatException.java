/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt;

/**
 * Thrown by method createFont in the <code>Font</code> class to indicate 
 * that the specified font is bad. 
 *
 * @author  Parry Kejriwal
 * @version 1.5, 02/06/02
 * @see     java.awt.Font
 * @since   1.3
 */
public
class FontFormatException extends Exception {
    /**
     * Report a FontFormatException for the reason specified.
     */
    public FontFormatException(String reason) {
      super (reason);
    }
}
