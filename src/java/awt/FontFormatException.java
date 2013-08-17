/*
 * @(#)FontFormatException.java	1.4 00/02/02
 *
 * Copyright 1999, 2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.awt;

/**
 * Thrown by method createFont in the <code>Font</code> class to indicate 
 * that the specified font is bad. 
 *
 * @author  Parry Kejriwal
 * @version 1.4, 02/02/00
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
