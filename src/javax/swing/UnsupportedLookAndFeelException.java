/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.13 02/06/02
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
