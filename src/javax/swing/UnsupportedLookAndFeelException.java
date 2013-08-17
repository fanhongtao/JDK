/*
 * @(#)UnsupportedLookAndFeelException.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package javax.swing;

/**
 * An exception that indicates the request look & feel management classes
 * are not present on the user's system.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @author unattributed
 * @version 1.10 08/28/98
 */
public class UnsupportedLookAndFeelException extends Exception
{
    /**
     * Constructs an UnsupportedLookAndFeelException object.
     * @param s a message String
     */
    public UnsupportedLookAndFeelException(String s) {
	super(s);
    }
}
