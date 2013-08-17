/*
 * @(#)JTextArea.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package javax.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.text.*;
import javax.swing.plaf.*;
import javax.accessibility.*;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

/**
 * A TextArea is a multi-line area that displays plain text. 
 * It is intended to be a lightweight component that provides source 
 * compatibility with the java.awt.TextArea class where it can
 * reasonably do so.  This component has capabilities not found in 
 * the java.awt.TextArea class.  The superclass should be consulted for 
 * additional capabilities.  Alternative multi-line text classes with
 * more capabilitites are JTextPane and JEditorPane.
 * <p>
 * The java.awt.TextArea internally handles scrolling.  JTextArea
 * is different in that it doesn't manage scrolling, but implements
 * the swing Scrollable interface.  This allows it to be placed 
 * inside a JScrollPane if scrolling behavior is desired, and used
 * directly if scrolling is not desired.
 * <p>
 * The java.awt.TextArea has the ability to do line wrapping. 
 * This was controlled by the horizontal scrolling policy.  Since
 * scrolling is not done by JTextArea directly, backward 
 * compatibility must be provided another way.  JTextArea has
 * a bound property for line wrapping that controls whether or
 * not it will wrap lines.
 * <p>
 * The java.awt.TextArea could be monitored for changes by adding
 * a TextListener for TextEvent's.  In the JTextComponent based
 * components, changes are broadcasted from the model via a
 * DocumentEvent to DocumentListeners.  The DocumentEvent gives 
 * the location of the change and the kind of change if desired.
 * The code fragment might look something like:
 * <pre><code>
 *    DocumentListener myListener = ??;
 *    JTextArea myArea = ??;
 *    myArea.getDocument().addDocumentListener(myListener);
 * </code></pre>
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JTextArea">JTextArea</a> key assignments.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @beaninfo
 *   attribute: isContainer false
 * @author  Timothy Prinzing
 * @version 1.58 10/13/98
 * @see JTextPane
 * @see JEditorPane
 */
public class JTextArea extends JTextComponent {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "TextAreaUI";

    /**
     * Constructs a new TextArea.  A default model is set, the initial string
     * is null, and rows/columns are set to 0.
     */
    public JTextArea() {
        this(null, null, 0, 0);
    }

    /**
     * Constructs a new TextArea with the specified text displayed.
     * A default model is created and rows/columns are set to 0.
     *
     * @param text the text to be displayed, or null
     */
    public JTextArea(String text) {
        this(null, text, 0, 0);
    }

    /**
     * Constructs a new empty TextArea with the specified number of
     * rows and columns.  A default model is created, and the initial
     * string is null.
     *
     * @param rows the number of rows >= 0
     * @param columns the number of columns >= 0
     */
    public JTextArea(int rows, int columns) {
        this(null, null, rows, columns);
    }

    /**
     * Constructs a new TextArea with the specified text and number
     * of rows and columns.  A default model is created.
     *
     * @param text the text to be displayed, or null
     * @param rows the number of rows >= 0
     * @param columns the number of columns >= 0
     */
    public JTextArea(String text, int rows, int columns) {
        this(null, text, rows, columns);
    }

    /**
     * Constructs a new JTextArea with the given document model, and defaults
     * for all of the other arguments (null, 0, 0).
     *
     * @param doc  the model to use
     */
    public JTextArea(Document doc) {
        this(doc, null, 0, 0);
    }

    /**
     * Constructs a new JTextArea with the specified number of rows
     * and columns, and the given model.  All of the constructors
     * feed through this constructor.
     *
     * @param doc the model to use, or create a default one if null
     * @param text the text to be displayed, null if none
     * @param rows the number of rows >= 0
     * @param columns the number of columns >= 0
     */
    public JTextArea(Document doc, String text, int rows, int columns) {
        super();
        this.rows = rows;
        this.columns = columns;
        if (doc == null) {
            doc = createDefaultModel();
        }
        setDocument(doc);
        if (text != null) {
            setText(text);
        }
    }

    /**
     * Returns the class ID for the UI.
     *
     * @return the ID ("TextAreaUI")
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * Creates the default implementation of the model
     * to be used at construction if one isn't explicitly 
     * given.  A new instance of PlainDocument is returned.
     *
     * @return the default document model
     */
    protected Document createDefaultModel() {
        return new PlainDocument();
    }

    /**
     * Sets the number of characters to expand tabs to.
     * This will be multiplied by the maximum advance for
     * variable width fonts.  A PropertyChange event ("tabSize") is fired
     * when the tab size changes.
     *
     * @param size number of characters to expand to
     * @see #getTabSize
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: the number of characters to expand tabs to
     */
    public void setTabSize(int size) {
        Document doc = getDocument();
        if (doc != null) {
            int old = getTabSize();
            doc.putProperty(PlainDocument.tabSizeAttribute, new Integer(size));
            firePropertyChange("tabSize", old, size);
        }
    }

    /**
     * Gets the number of characters used to expand tabs.  If the document is
     * null or doesn't have a tab setting, return a default of 8.
     *
     * @return the number of characters
     */
    public int getTabSize() {
        int size = 8;
        Document doc = getDocument();
        if (doc != null) {
            Integer i = (Integer) doc.getProperty(PlainDocument.tabSizeAttribute);
            if (i != null) {
                size = i.intValue();
            }
        }
        return size;
    }

    /**
     * Sets the line-wrapping policy of the text area.  If set
     * to true the lines will be wrapped if they are too long
     * to fit within the allocated width.  If set to false,
     * the lines will always be unwrapped.  A PropertyChange event ("lineWrap")
     * is fired when the policy is changed.
     *
     * @param wrap indicates if lines should be wrapped.
     * @see #getLineWrap
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: should lines be wrapped
     */
    public void setLineWrap(boolean wrap) {
        boolean old = this.wrap;
        this.wrap = wrap;
        firePropertyChange("lineWrap", old, wrap);
    }

    /**
     * Gets the line-wrapping policy of the text area.  If set
     * to true the lines will be wrapped if they are too long
     * to fit within the allocated width.  If set to false,
     * the lines will always be unwrapped.
     *
     * @returns if lines will be wrapped.
     */
    public boolean getLineWrap() {
        return wrap;
    }

    /**
     * Set the style of wrapping used if the text area is wrapping
     * lines.  If set to true the lines will be wrapped at word
     * boundries (ie whitespace) if they are too long
     * to fit within the allocated width.  If set to false,
     * the lines will be wrapped at character boundries.
     *
     * @param word indicates if word boundries should be used
     *   for line wrapping.
     * @see #getWrapStyleWord
     * @beaninfo
     *   preferred: false
     *       bound: true
     * description: should wrapping occur at word boundries
     */
    public void setWrapStyleWord(boolean word) {
        boolean old = this.word;
        this.word = word;
        firePropertyChange("wrapStyleWord", old, word);
    }

    /**
     * Get the style of wrapping used if the text area is wrapping
     * lines.  If set to true the lines will be wrapped at word
     * boundries (ie whitespace) if they are too long
     * to fit within the allocated width.  If set to false,
     * the lines will be wrapped at character boundries.
     *
     * @returns if the wrap style should be word boundries
     *  instead of character boundries.
     * @see #setWrapStyleWord
     */
    public boolean getWrapStyleWord() {
        return word;
    }

    /**
     * Translates an offset into the components text to a 
     * line number.
     *
     * @param offset the offset >= 0
     * @return the line number >= 0
     * @exception BadLocationException thrown if the offset is
     *   less than zero or greater than the document length.
     */
    public int getLineOfOffset(int offset) throws BadLocationException {
        Document doc = getDocument();
        if (offset < 0) {
            throw new BadLocationException("Can't translate offset to line", -1);
        } else if (offset > doc.getLength()) {
            throw new BadLocationException("Can't translate offset to line", doc.getLength()+1);
        } else {
            Element map = getDocument().getDefaultRootElement();
            return map.getElementIndex(offset);
        }
    }

    /**
     * Determines the number of lines contained in the area.
     *
     * @return the number of lines >= 0
     */
    public int getLineCount() {
	// There is an implicit break being modeled at the end of the
	// document to deal with boundry conditions at the end.  This
	// is not desired in the line count, so we detect it and remove
	// its effect if throwing off the count.
        Element map = getDocument().getDefaultRootElement();
        int n = map.getElementCount();
	Element lastLine = map.getElement(n-1);
	if ((lastLine.getEndOffset() - lastLine.getStartOffset()) > 1) {
	    return n;
	}
	return n - 1;
    }

    /**
     * Determines the offset of the start of the given line.
     *
     * @param line  the line number to translate >= 0
     * @return the offset >= 0
     * @exception BadLocationException thrown if the line is
     * less than zero or greater or equal to the number of
     * lines contained in the document (as reported by 
     * getLineCount).
     */
    public int getLineStartOffset(int line) throws BadLocationException {
        Element map = getDocument().getDefaultRootElement();
        if (line < 0) {
            throw new BadLocationException("Negative line", -1);
        } else if (line >= map.getElementCount()) {
            throw new BadLocationException("No such line", getDocument().getLength()+1);
        } else {
            Element lineElem = map.getElement(line);
            return lineElem.getStartOffset();
        }
    }

    /**
     * Determines the offset of the end of the given line.
     *
     * @param line  the line >= 0
     * @return the offset >= 0
     * @exception BadLocationException Thrown if the line is
     * less than zero or greater or equal to the number of
     * lines contained in the document (as reported by 
     * getLineCount).
     */
    public int getLineEndOffset(int line) throws BadLocationException {
        Element map = getDocument().getDefaultRootElement();
        if (line < 0) {
            throw new BadLocationException("Negative line", -1);
        } else if (line >= map.getElementCount()) {
            throw new BadLocationException("No such line", getDocument().getLength()+1);
        } else {
            Element lineElem = map.getElement(line);
            return lineElem.getEndOffset();
        }
    }

    // --- java.awt.TextArea methods ---------------------------------

    /**
     * Inserts the specified text at the specified position.  Does nothing
     * if the model is null or if the text is null or empty.
     * <p>
     * This method is thread safe, although most Swing methods
     * are not. Please see 
     * <A HREF="http://java.sun.com/products/jfc/swingdoc-archive/threads.html">Threads
     * and Swing</A> for more information.     
     *
     * @param str the text to insert
     * @param pos the position at which to insert >= 0
     * @exception IllegalArgumentException  if pos is an
     *  invalid position in the model
     * @see TextComponent#setText
     * @see #replaceRange
     */
    public void insert(String str, int pos) {
        Document doc = getDocument();
        if (doc != null) {
            try {
                doc.insertString(pos, str, null);
            } catch (BadLocationException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    /**
     * Appends the given text to the end of the document.  Does nothing if
     * the model is null or the string is null or empty.
     * <p>
     * This method is thread safe, although most Swing methods
     * are not. Please see 
     * <A HREF="http://java.sun.com/products/jfc/swingdoc-archive/threads.html">Threads
     * and Swing</A> for more information.     
     *
     * @param str the text to insert
     * @see #insert
     */
    public void append(String str) {
        Document doc = getDocument();
        if (doc != null) {
            try {
                doc.insertString(doc.getLength(), str, null);
            } catch (BadLocationException e) {
            }
        }
    }

    /**
     * Replaces text from the indicated start to end position with the
     * new text specified.  Does nothing if the model is null.  Simply
     * does a delete if the new string is null or empty.
     * <p>
     * This method is thread safe, although most Swing methods
     * are not. Please see 
     * <A HREF="http://java.sun.com/products/jfc/swingdoc-archive/threads.html">Threads
     * and Swing</A> for more information.     
     *
     * @param str the text to use as the replacement
     * @param start the start position >= 0
     * @param end the end position >= start
     * @exception IllegalArgumentException  if part of the range is an
     *  invalid position in the model
     * @see #insert
     * @see #replaceRange
     */
    public void replaceRange(String str, int start, int end) {
        if (end < start) {
            throw new IllegalArgumentException("end before start");
        }
        Document doc = getDocument();
        if (doc != null) {
            try {
                doc.remove(start, end - start);
                doc.insertString(start, str, null);
            } catch (BadLocationException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    /**
     * Turns off tab traversal once focus gained.
     *
     * @return true, to indicate that the focus is being managed
     */
    public boolean isManagingFocus() {
        return true;
    }

    /**
     * Make sure that TAB and Shift-TAB events get consumed, so that
     * awt doesn't attempt focus traversal.
     *
     */

    protected void processComponentKeyEvent(KeyEvent e)  {
        super.processComponentKeyEvent(e);
        // tab consumption
	// We are actually consuming any TABs modified in any way, because
	// we don't want awt to get anything it can use for focus traversal.
	if (isManagingFocus()) {
	  if ((e.getKeyCode() == KeyEvent.VK_TAB || e.getKeyChar() == '\t')) {
	    e.consume();
	  }
	}
    }

    /**
     * Returns the number of rows in the TextArea.
     *
     * @return the number of rows >= 0
     */
    public int getRows() {
        return rows;
    }

    /**
     * Sets the number of rows for this TextArea.  Calls invalidate() after
     * setting the new value.
     *
     * @param rows the number of rows >= 0
     * @exception IllegalArgumentException if rows is less than 0
     * @see #getRows
     * @beaninfo
     * description: the number of rows preferred for display
     */
    public void setRows(int rows) {
        int oldVal = this.rows;
        if (rows < 0) {
            throw new IllegalArgumentException("rows less than zero.");
        }
        if (rows != oldVal) {
            this.rows = rows;
            invalidate();
        }
    }

    /**
     * Defines the meaning of the height of a row.  This defaults to
     * the height of the font.
     *
     * @return the height >= 1
     */
    protected int getRowHeight() {
        if (rowHeight == 0) {
            FontMetrics metrics = getFontMetrics(getFont());
            rowHeight = metrics.getHeight();
        }
        return rowHeight;
    }

    /**
     * Returns the number of columns in the TextArea.
     *
     * @return number of columns >= 0
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Sets the number of columns for this TextArea.  Does an invalidate()
     * after setting the new value.
     *
     * @param columns the number of columns >= 0
     * @exception IllegalArgumentException if columns is less than 0
     * @see #getColumns
     * @beaninfo
     * description: the number of columns preferred for display
     */
    public void setColumns(int columns) {
        int oldVal = this.columns;
        if (columns < 0) {
            throw new IllegalArgumentException("columns less than zero.");
        }
        if (columns != oldVal) {
            this.columns = columns;
            invalidate();
        }
    }

    /**
     * Gets column width.
     * The meaning of what a column is can be considered a fairly weak
     * notion for some fonts.  This method is used to define the width
     * of a column.  By default this is defined to be the width of the
     * character <em>m</em> for the font used.  This method can be 
     * redefined to be some alternative amount.
     *
     * @return the column width >= 1
     */
    protected int getColumnWidth() {
        if (columnWidth == 0) {
            FontMetrics metrics = getFontMetrics(getFont());
            columnWidth = metrics.charWidth('m');
        }
        return columnWidth;
    }

    // --- Component methods -----------------------------------------

    /**
     * Returns the preferred size of the TextArea.  This is the
     * maximum of the size needed to display the text and the
     * size requested for the viewport.
     *
     * @return the size
     */
    public Dimension getPreferredSize() {
	Dimension d = super.getPreferredSize();
        d = (d == null) ? new Dimension(400,400) : d;
        if (columns != 0) {
	    d.width = Math.max(d.width, columns * getColumnWidth());
	}
	if (rows != 0) {
	    d.height = Math.max(d.height, rows * getRowHeight());
	}
	return d;
    }

    /**
     * Sets the current font.  This removes cached row height and column
     * width so the new font will be reflected, and calls revalidate().
     *
     * @param f the font to use as the current font
     */
    public void setFont(Font f) {
        super.setFont(f);
        rowHeight = 0; 
        columnWidth = 0;
    }


    /**
     * Returns a string representation of this JTextArea. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this JTextArea.
     */
    protected String paramString() {
        String wrapString = (wrap ?
			     "true" : "false");
        String wordString = (word ?
			     "true" : "false");

        return super.paramString() +
        ",colums=" + columns +
        ",columWidth=" + columnWidth +
        ",rows=" + rows +
        ",rowHeight=" + rowHeight +
        ",word=" + wordString +
        ",wrap=" + wrapString;
    }

    // --- Scrollable methods ----------------------------------------

    /**
     * Returns true if a viewport should always force the width of this 
     * Scrollable to match the width of the viewport.  This is implemented
     * to return true if the line wrapping policy is true, and false
     * if lines are not being wrapped.
     * 
     * @return true if a viewport should force the Scrollables width
     * to match its own.
     */
    public boolean getScrollableTracksViewportWidth() {
        return (wrap) ? true : super.getScrollableTracksViewportWidth();
    }

    /**
     * Returns the preferred size of the viewport if this component
     * is embedded in a JScrollPane.  This uses the desired column
     * and row settings if they have been set, otherwise the superclass
     * behavior is used.
     * 
     * @return The preferredSize of a JViewport whose view is this Scrollable.
     * @see JViewport#getPreferredSize
     */
    public Dimension getPreferredScrollableViewportSize() {
        Dimension size = super.getPreferredScrollableViewportSize();
        size = (size == null) ? new Dimension(400,400) : size;
        size.width = (columns == 0) ? size.width : columns * getColumnWidth();
        size.height = (rows == 0) ? size.height : rows * getRowHeight();
        return size;
    }

    /**
     * Components that display logical rows or columns should compute
     * the scroll increment that will completely expose one new row
     * or column, depending on the value of orientation.  This is implemented
     * to use the vaules returned by the <code>getRowHeight</code> and
     * <code>getColumnWidth</code> methods.
     * <p>
     * Scrolling containers, like JScrollPane, will use this method
     * each time the user requests a unit scroll.
     * 
     * @param visibleRect the view area visible within the viewport
     * @param orientation Either SwingConstants.VERTICAL or
     *   SwingConstants.HORIZONTAL.
     * @param direction Less than zero to scroll up/left,
     *   greater than zero for down/right.
     * @return The "unit" increment for scrolling in the specified direction
     * @exception IllegalArgumentException for an invalid orientation
     * @see JScrollBar#setUnitIncrement
     * @see #getRowHeight
     * @see #getColumnWidth
     */
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        switch (orientation) {
        case SwingConstants.VERTICAL:
            return getRowHeight();
        case SwingConstants.HORIZONTAL:
            return getColumnWidth();
        default:
            throw new IllegalArgumentException("Invalid orientation: " + orientation);
        }
    }

    /** 
     * See readObject() and writeObject() in JComponent for more 
     * information about serialization in Swing.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
	if ((ui != null) && (getUIClassID().equals(uiClassID))) {
	    ui.installUI(this);
	}
    }

/////////////////
// Accessibility support
////////////////


    /**
     * Get the AccessibleContext associated with this JTextArea.
     * Creates a new context if necessary.
     *
     * @return the AccessibleContext of this JTextArea
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJTextArea();
        }
        return accessibleContext;
    }

    /**
     * The class used to obtain the accessible role for this object.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected class AccessibleJTextArea extends AccessibleJTextComponent {

        /**
         * Gets the state set of this object.
         *
         * @return an instance of AccessibleStateSet describing the states
         * of the object
         * @see AccessibleStateSet
         */
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = super.getAccessibleStateSet();
            states.add(AccessibleState.MULTI_LINE);
            return states;
        }
    }

    // --- variables -------------------------------------------------

    private int rows;
    private int columns;
    private int columnWidth;
    private int rowHeight;
    private boolean wrap;
    private boolean word;

}
