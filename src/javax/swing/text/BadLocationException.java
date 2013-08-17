/*
 * @(#)BadLocationException.java	1.14 98/08/28
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
package javax.swing.text;

/**
 * This exception is to report bad locations within a document model
 * (that is, attempts to reference a location that doesn't exist).
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @author  Timothy Prinzing
 * @version 1.14 08/28/98
 */
public class BadLocationException extends Exception
{
    /**
     * Creates a new BadLocationException object.
     * 
     * @param s		a string indicating what was wrong with the arguments
     * @param offs      offset within the document that was requested >= 0
     */
    public BadLocationException(String s, int offs) {
	super(s);
	this.offs = offs;
    }

    /**
     * Returns the offset into the document that was not legal.
     *
     * @return the offset >= 0
     */
    public int offsetRequested() {
	return offs;
    }

    private int offs;
}
