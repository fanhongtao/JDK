/*
 * @(#)Position.java	1.12 98/08/26
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
 * Represents a location within a document.  It is intended to abstract away
 * implementation details of the document and enable specification of
 * positions within the document that are capable of tracking of change as
 * the document is edited (i.e. offsets are fragile).
 *
 * @author  Timothy Prinzing
 * @version 1.12 08/26/98
 */
public interface Position {

    /**
     * Fetches the current offset within the document.
     *
     * @return the offset >= 0
     */
    public int getOffset();

    /**
     * A typesafe enumeration to indicate bias to a position
     * in the model.  A position indicates a location between
     * two characters.  The bias can be used to indicate an
     * interest toward one of the two sides of the position
     * in boundry conditions where a simple offset is
     * ambiguous.
     */
    public static final class Bias {

	/**
	 * Indicates to bias toward the next character
	 * in the model.
	 */
	public static final Bias Forward = new Bias("Forward");

	/**
	 * Indicates a bias toward the previous character
	 * in the model.
	 */
	public static final Bias Backward = new Bias("Backward");

	/**
	 * string representation
	 */
        public String toString() {
	    return name;
	}

        private Bias(String name) {
	    this.name = name;
	}

	private String name;
    }
}
