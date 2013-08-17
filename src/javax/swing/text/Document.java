/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.text;

import javax.swing.event.*;

/**
 * <p>
 * The Document is a container for text that serves as the model 
 * for swing text components.  The goal for this interface is to
 * scale from very simple needs (plain text textfield) to
 * complex needs (HTML or XML documents for example).
 *
 * <p><b><font size=+1>Content</font></b>
 * <p>
 * At the simplest level, text can be 
 * modeled as a linear sequence of characters. To support 
 * internationalization, the Swing text model uses 
 * <a href="http://www.unicode.org/">unicode</a> characters. 
 * The sequence of characters displayed in a text component is 
 * generally referred to as the component's <em>content</em>.
 * <p>
 * To refer to locations within the sequence, the coordinates
 * used are the location between two characters.  As the diagram 
 * below shows, a location in a text document can be referred to 
 * as a position, or an offset. This position is zero-based.
 * <p align=center><img src="doc-files/Document-coord.gif">
 * <p>
 * In the example, if the content of a document is the
 * sequence "The quick brown fox," as shown in the preceding diagram, 
 * the location just before the word "The" is 0, and the location after 
 * the word "The" and before the whitespace that follows it is 3. 
 * The entire sequence of characters in the sequence "The" is called a 
 * <em>range</em>.
 * <p>The following methods give access to the character data
 * that makes up the content.
 * <ul>
 * <li><a href="#getLength">getLength</a>
 * <li><a href="#getText(int, int)">getText(int, int)</a>
 * <li><a href="#getText(int, int, javax.swing.text.Segment)">getText(int, int, Segment)</a>
 * </ul>
 * <p><b><font size=+1>Structure</font></b>
 * <p>
 * Text is rarely represented simply as featureless content. Rather, 
 * text typically has some sort of structure associated with it.
 * Exactly what structure is modeled is up to a particular Document
 * implementation.  It might be as simple as no structure (i.e. a
 * simple text field), or it might be something like diagram below.
 * <p align=center><img src="doc-files/Document-structure.gif">
 * <p>
 * The unit of structure (i.e. a node of the tree) is referred to
 * by the <a href="Element.html">Element</a> interface.  Each Element
 * can be tagged with a set of attributes.  These attributes
 * (name/value pairs) are defined by the 
 * <a href="AttributeSet.html">AttributeSet</a> interface.
 * <p>The following methods give access to the document structure.
 * <ul>
 * <li><a href="#getDefaultRootElement">getDefaultRootElement</a>
 * <li><a href="#getRootElements">getRootElements</a>
 * </ul>
 *
 * <p><b><font size=+1>Mutations</font></b>
 * <p>
 * All documents need to be able to add and remove simple text.
 * Typically, text is inserted and removed via gestures from
 * a keyboard or a mouse.  What effect the insertion or removal
 * has upon the document structure is entirely up to the
 * implementation of the document.
 * <p>The following methods are related to mutation of the 
 * document content:
 * <ul>
 * <li><a href="#insertString">insertString</a>
 * <li><a href="#remove">remove</a>
 * <li><a href="#createPosition">createPosition</a>
 * </ul>
 *
 * <p><b><font size=+1>Notification</font></b>
 * <p>
 * Mutations to the Document must be communicated to interested
 * observers.  The notification of change follows the event model
 * guidelines that are specified for JavaBeans.  In the JavaBeans
 * event model, once an event notification is dispatched, all listeners 
 * must be notified before any further mutations occur to the source 
 * of the event.  Further, order of delivery is not guaranteed.
 * <p>
 * Notification is provided as two seperate events, 
 * <a href="../event/DocumentEvent.html">DocumentEvent<a>, and
 * <a href="../event/UndoableEditEvent.html">UndoableEditEvent</a>.
 * If a mutation is made to a Document through its api, 
 * a DocumentEvent will be sent to all of the registered 
 * DocumentListeners.  If the Document implementation supports 
 * undo/redo capabilities, an UndoableEditEvent will be sent
 * to all of the registered UndoableEditListeners.
 * If an undoable edit is undone, a DocumentEvent should be
 * fired from the Document to indicate it has changed again.
 * In this case however, there should be no UndoableEditEvent
 * generated since that edit is actually the source of the change
 * rather than a mutation to the Document made through it's 
 * api.
 * <p align=center><img src="doc-files/Document-notification.gif">
 * <p>
 * Referring to the above diagram, suppose that the component shown 
 * on the left mutates the document object represented by the blue 
 * rectangle. The document responds by dispatching a DocumentEvent to 
 * both component views and sends an UndoableEditEvent to the listening 
 * logic, which maintains a history buffer.
 * <p>
 * Now suppose that the component shown on the right mutates the same 
 * document.  Again, the document dispatches a DocumentEvent to both 
 * component views and sends an UndoableEditEvent to the listening logic 
 * that is maintaining the history buffer. 
 * <p>
 * If the history buffer is then rolled back (i.e. the last UndoableEdit
 * undone), a DocumentEvent is sent to both views, causing both of them to 
 * reflect the undone mutation to the document (that is, the
 * removal of the right component's mutation). If the history buffer again 
 * rolls back another change, another DocumentEvent is sent to both views, 
 * causing them to reflect the undone mutation to the document -- that is, 
 * the removal of the left component's mutation. 
 * <p>
 * The methods related to observing mutations to the document are:
 * <ul>
 * <li><a href="#addDocumentListener">addDocumentListener</a>
 * <li><a href="#removeDocumentListener">removeDocumentListener</a>
 * <li><a href="#addUndoableEditListener">addUndoableEditListener</a>
 * <li><a href="#removeUndoableEditListener">removeUndoableEditListener</a>
 * </ul>
 *
 * <p><b><font size=+1>Properties</font></b>
 * <p>
 * Document implementations will generally have some set of properties
 * associated with them at runtime.  Two well known properties are the
 * <a href="#StreamDescriptionProperty">StreamDescriptionProperty</a>
 * which can be used to describe where the Document came from, and
 * <a href="#TitleProperty">TitleProperty</a> which can be used to
 * name the Document.  The methods related to the properties are:
 * <ul>
 * <li><a href="#getProperty">getProperty</a>
 * <li><a href="#putProperty">putProperty</a>
 * </ul>
 *
 * @author  Timothy Prinzing
 * @version 1.33 02/06/02
 *
 * @see javax.swing.event.DocumentEvent
 * @see javax.swing.event.DocumentListener
 * @see javax.swing.event.UndoableEditEvent
 * @see javax.swing.event.UndoableEditListener
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
     * Removes a portion of the content of the document.  
     * This will cause a DocumentEvent of type 
     * DocumentEvent.EventType.REMOVE to be sent to the 
     * registered DocumentListeners, unless an exception
     * is thrown.  The notification will be sent to the
     * listeners by calling the removeUpdate method on the
     * DocumentListeners.
     * <p>
     * To ensure reasonable behavior in the face 
     * of concurrency, the event is dispatched after the 
     * mutation has occurred. This means that by the time a 
     * notification of removal is dispatched, the document
     * has already been updated and any marks created by
     * createPosition have already changed.
     * For a removal, the end of the removal range is collapsed 
     * down to the start of the range, and any marks in the removal 
     * range are collapsed down to the start of the range.
     * <p align=center><img src="doc-files/Document-remove.gif">
     * <p>
     * If the Document structure changed as result of the removal,
     * the details of what Elements were inserted and removed in
     * response to the change will also be contained in the generated
     * DocumentEvent. It is up to the implementation of a Document
     * to decide how the structure should change in response to a
     * remove.
     * <p>
     * If the Document supports undo/redo, an UndoableEditEvent will
     * also be generated.  
     *
     * @param offs  the offset from the begining >= 0
     * @param len   the number of characters to remove >= 0
     * @exception BadLocationException  some portion of the removal range
     *   was not a valid part of the document.  The location in the exception
     *   is the first bad position encountered.
     * @see javax.swing.event.DocumentEvent
     * @see javax.swing.event.DocumentListener
     * @see javax.swing.event.UndoableEditEvent
     * @see javax.swing.event.UndoableEditListener
     */
    public void remove(int offs, int len) throws BadLocationException;

    /**
     * Inserts a string of content.  This will cause a DocumentEvent
     * of type DocumentEvent.EventType.INSERT to be sent to the
     * registered DocumentListers, unless an exception is thrown.
     * The DocumentEvent will be delivered by calling the
     * insertUpdate method on the DocumentListener.
     * The offset and length of the generated DocumentEvent
     * will indicate what change was actually made to the Document.
     * <p align=center><img src="doc-files/Document-insert.gif">
     * <p>
     * If the Document structure changed as result of the insertion,
     * the details of what Elements were inserted and removed in
     * response to the change will also be contained in the generated
     * DocumentEvent.  It is up to the implementation of a Document
     * to decide how the structure should change in response to an
     * insertion.
     * <p>
     * If the Document supports undo/redo, an UndoableEditEvent will
     * also be generated.  
     *
     * @param offset  the offset into the document to insert the content >= 0.
     *    All positions that track change at or after the given location 
     *    will move.  
     * @param str    the string to insert
     * @param a      the attributes to associate with the inserted
     *   content.  This may be null if there are no attributes.
     * @exception BadLocationException  the given insert position is not a valid 
     * position within the document
     * @see javax.swing.event.DocumentEvent
     * @see javax.swing.event.DocumentListener
     * @see javax.swing.event.UndoableEditEvent
     * @see javax.swing.event.UndoableEditListener
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
     * This method allows an application to mark a place in
     * a sequence of character content. This mark can then be 
     * used to tracks change as insertions and removals are made 
     * in the content. The policy is that insertions always
     * occur prior to the current position (the most common case) 
     * unless the insertion location is zero, in which case the 
     * insertion is forced to a position that follows the
     * original position. 
     *
     * @param offs  the offset from the start of the document >= 0
     * @return the position
     * @exception BadLocationException  if the given position does not
     *   represent a valid location in the associated document
     */
    public Position createPosition(int offs) throws BadLocationException;

    /**
     * Returns all of the root elements that are defined.
     * <p>
     * Typically there will be only one document structure, but the interface
     * supports building an arbitrary number of structural projections over the 
     * text data. The document can have multiple root elements to support 
     * multiple document structures.  Some examples might be:
     * </p>
     * <ul>
     * <li>Text direction.
     * <li>Lexical token streams.
     * <li>Parse trees.
     * <li>Conversions to formats other than the native format.
     * <li>Modification specifications.
     * <li>Annotations.
     * </ul>
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
