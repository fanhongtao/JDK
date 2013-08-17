/*
 * @(#)Document.java	1.27 98/08/26
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

import javax.swing.event.*;

/**
 * <p>
 * Container for text that supports editing and provides notification of
 * changes (serves as the model in an MVC relationship).  Support is 
 * provided to mark up the text with structure that tracks changes.  The
 * unit of structure is called an element.  Views will typically be built
 * from an element structure.  Each element can have an arbitrary set of
 * attributes associated with it.  The interface itself is intended to be
 * free of any policy for structure that is provided, as the nature of 
 * the document structure should be determined by the implementation.  
 * </p>
 * <p align=center><img src="doc-files/document.gif"></p>
 * <p>
 * Typically there will be only one document structure, but the interface
 * supports building an arbitrary number of structural projections over the 
 * text data. The document can have multiple root elements to support 
 * multiple document structures.  Some examples might be:
 * </p>
 * <ul>
 * <li>Logical document structure.
 * <li>View projections.
 * <li>Lexical token streams.
 * <li>Parse trees.
 * <li>Conversions to formats other than the native format.
 * <li>Modification specifications.
 * <li>Annotations.
 * </ul>
 *
 * @author  Timothy Prinzing
 * @version 1.27 08/26/98
 *
 * @see DocumentEvent
 * @see DocumentListener
 * @see Element
 * @see Position
 * @see AttributeSet
 */
public interface Document {

    /**
     * Returns number of characters of content currently 
     * in the document.
     *
     * @return number of characters >= 0
     */
    public int getLength();

    /**
     * Registers the given observer to begin receiving notifications
     * when changes are made to the document.
     *
     * @param listener the observer to register
     * @see Document#removeDocumentListener
     */
    public void addDocumentListener(DocumentListener listener);

    /**
     * Unregisters the given observer from the notification list
     * so it will no longer receive change updates.  
     *
     * @param listener the observer to register
     * @see Document#addDocumentListener
     */
    public void removeDocumentListener(DocumentListener listener);

    /**
     * Registers the given observer to begin receiving notifications
     * when undoable edits are made to the document.
     *
     * @param listener the observer to register
     * @see javax.swing.event.UndoableEditEvent
     */
    public void addUndoableEditListener(UndoableEditListener listener);

    /**
     * Unregisters the given observer from the notification list
     * so it will no longer receive updates.
     *
     * @param listener the observer to register
     * @see javax.swing.event.UndoableEditEvent
     */
    public void removeUndoableEditListener(UndoableEditListener listener);

    /**
     * Gets properties associated with the document.  Allows one to
     * store things like the document title, author, etc.
     *
     * @param key a non-null property
     * @return the properties
     */
    public Object getProperty(Object key);

    /**
     * Puts a new property on the list.
     *
     * @param key the non-null property key
     * @param value the property value
     */
    public void putProperty(Object key, Object value);

    /**
     * Removes a portion of the content of the document.  This
     * will cause notification to be sent to the observers of
     * the document (unless an exception is thrown).
     *
     * @param offs  the offset from the begining >= 0
     * @param len   the number of characters to remove >= 0
     * @exception BadLocationException  some portion of the removal range
     *   was not a valid part of the document.  The location in the exception
     *   is the first bad position encountered.
     * @see DocumentEvent
     * @see DocumentListener
     */
    public void remove(int offs, int len) throws BadLocationException;

    /**
     * Inserts a string of content.  This will cause the observers of the
     * the document to be notified, unless an exception is thrown.
     *
     * A position marks a location in the document between items.  
     *
     * If the attributes that have been defined exactly match the
     * current attributes defined at the position, the element 
     * representing the content at that position will simply be expanded.  
     * If the attributes defined are different, a new content element
     * will be created that matches the attributes.  Does nothing with null
     * or empty strings.
     *
     * @param offset  the offset into the document to insert the content >= 0.
     *    All positions that track change at or after the given location 
     *    will move.  
     * @param str    the string to insert
     * @param a      the attributes to associate with the inserted
     *   content.  This may be null if there are no attributes.
     * @exception BadLocationException  the given insert position is not a valid 
     * position within the document
     * @see DocumentEvent
     * @see DocumentListener
     */
    public void insertString(int offset, String str, AttributeSet a) throws BadLocationException;

    /**
     * Fetches the text contained within the given portion 
     * of the document.
     *
     * @param offset  the offset into the document representing the desired 
     *   start of the text >= 0
     * @param length  the length of the desired string >= 0
     * @return the text, in a String of length >= 0
     * @exception BadLocationException  some portion of the given range
     *   was not a valid part of the document.  The location in the exception
     *   is the first bad position encountered.
     */
    public String getText(int offset, int length) throws BadLocationException;

    /**
     * Fetches the text contained within the given portion 
     * of the document.
     *
     * @param offset  the offset into the document representing the desired 
     *   start of the text >= 0
     * @param length  the length of the desired string >= 0
     * @param txt the Segment object to return the text in
     *
     * @exception BadLocationException  Some portion of the given range
     *   was not a valid part of the document.  The location in the exception
     *   is the first bad position encountered.
     */
    public void getText(int offset, int length, Segment txt) throws BadLocationException;

    /**
     * Returns a position that represents the start of the document.  The 
     * position returned can be counted on to track change and stay 
     * located at the beginning of the document.
     *
     * @return the position
     */
    public Position getStartPosition();
    
    /**
     * Returns a position that represents the end of the document.  The
     * position returned can be counted on to track change and stay 
     * located at the end of the document.
     *
     * @return the position
     */
    public Position getEndPosition();

    /**
     * Returns a position that will track change as the document
     * is altered.  If the relative position pos is null, the
     * start of the document will be used.
     *
     * @param offs  the offset from the start of the document >= 0
     * @return the position
     * @exception BadLocationException  if the given position does not
     *   represent a valid location in the associated document
     */
    public Position createPosition(int offs) throws BadLocationException;

    /**
     * Returns all of the root elements that are defined.
     *
     * @return the root element
     */
    public Element[] getRootElements();

    /**
     * Returns the root element that views should be based upon,
     * unless some other mechanism for assigning views to element
     * structures is provided.
     *
     * @return the root element
     */
    public Element getDefaultRootElement();

    /**
     * This allows the model to be safely rendered in the presence
     * of currency, if the model supports being updated asynchronously.
     * The given runnable will be executed in a way that allows it
     * to safely read the model with no changes while the runnable
     * is being executed.  The runnable itself may <em>not</em>
     * make any mutations.  
     *
     * @param r a Runnable used to render the model
     */
    public void render(Runnable r);

    /**
     * The property name for the description of the stream
     * used to initialize the document.  This should be used
     * if the document was initialized from a stream and 
     * anything is known about the stream.
     */
    public static final String StreamDescriptionProperty = "stream";

    /**
     * The property name for the title of the document, if 
     * there is one.
     */
    public static final String TitleProperty = "title";


}
