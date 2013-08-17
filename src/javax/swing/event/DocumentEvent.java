/*
 * @(#)DocumentEvent.java	1.15 98/08/26
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
package javax.swing.event;

import javax.swing.undo.*;
import javax.swing.text.*;

/**
 * Interface for document change notifications.
 *
 * @author  Timothy Prinzing
 * @version 1.15 08/26/98
 */
public interface DocumentEvent {

    /**
     * Returns the offset within the document of the start
     * of the change.
     *
     * @return the offset >= 0
     */
    public int getOffset();

    /**
     * Returns the length of the change.
     *
     * @return the length >= 0
     */
    public int getLength();

    /**
     * Gets the document that sourced the change event.
     *
     * @returns the document
     */
    public Document getDocument();

    /**
     * Gets the type of event.
     *
     * @return the type
     */
    public EventType getType();

    /**
     * Gets the change information for the given element. 
     * The change information describes what elements were
     * added and removed and the location.  If there were
     * no changes, null is returned.
     *
     * @param elem the element
     * @return the change information, or null if the 
     *   element was not modified
     */
    public ElementChange getChange(Element elem);

    /**
     * Typesafe enumeration for document event types
     */
    public static final class EventType {

        private EventType(String s) {
	    typeString = s;
	}

        /**
         * Insert type.
         */
	public static final EventType INSERT = new EventType("INSERT");

        /**
         * Remove type.
         */
	public static final EventType REMOVE = new EventType("REMOVE");

        /**
         * Change type.
         */
	public static final EventType CHANGE = new EventType("CHANGE");

        /**
         * Converts the type to a string.
         *
         * @return the string
         */
        public String toString() {
	    return typeString;
	}

	private String typeString;
    }

    /**
     * Describes changes made to an element.
     */
    public interface ElementChange {

	/**
	 * Returns the element represented.  This is the element
	 * that was changed.
         *
         * @return the element
	 */
	public Element getElement();

	/**
	 * Fetches the index within the element represented.
	 * This is the location that children were added
	 * and/or removed.
         *
         * @return the index >= 0
	 */
	public int getIndex();

	/**
	 * Gets the child elements that were removed from the
	 * given parent element.  The parent element is expected
	 * to be one of the elements listed in the elementsModified
	 * method.  The element array returned is sorted in the
	 * order that the elements used to lie in the document.
	 *
	 * @return the child elements
	 */
        public Element[] getChildrenRemoved();

	/**
	 * Gets the child elements that were added to the given
	 * parent element.  The parent element is expected to be
	 * one of the elements given in the elementsModified method.
	 * The element array returned is sorted in the order that
	 * the elements lie in the document.
	 *
	 * @return the child elements
	 */
        public Element[] getChildrenAdded();

    }
}
