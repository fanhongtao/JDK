/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt;

import java.awt.peer.TextAreaPeer;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import javax.accessibility.*;

/**
 * A <code>TextArea</code> object is a multi-line region
 * that displays text. It can be set to allow editing or
 * to be read-only.
 * <p>
 * The following image shows the appearance of a text area:
 * <p>
 * <img src="doc-files/TextArea-1.gif"
 * ALIGN=center HSPACE=10 VSPACE=7>
 * <p>
 * This text area could be created by the following line of code:
 * <p>
 * <hr><blockquote><pre>
 * new TextArea("Hello", 5, 40);
 * </pre></blockquote><hr>
 * <p>
 * @version	1.58, 02/06/02
 * @author 	Sami Shaio
 * @since       JDK1.0
 */
public class TextArea extends TextComponent {

    /**
     * The number of rows in the TextArea.
     * This parameter will determine the text area's height.  
     * Guaranteed to be non-negative.  
     *
     * @serial
     * @see getRows()
     * @see setRows()
     */
    int	rows;

    /**
     * The number of columns in the TextArea.
     * A column is an approximate average character
     * width that is platform-dependent.
     * This parameter will determine the text area's width.  
     * Guaranteed to be non-negative.  
     *
     * @serial
     * @see getColumns()
     * @see setColumns()
     */
    int	columns;

    private static final String base = "text";
    private static int nameCounter = 0;

    /**
     * Create and display both vertical and horizontal scrollbars.
     * @since JDK1.1
     */
    public static final int SCROLLBARS_BOTH = 0;

    /**
     * Create and display vertical scrollbar only.
     * @since JDK1.1
     */
    public static final int SCROLLBARS_VERTICAL_ONLY = 1;

    /**
     * Create and display horizontal scrollbar only.
     * @since JDK1.1
     */
    public static final int SCROLLBARS_HORIZONTAL_ONLY = 2;

    /**
     * Do not create or display any scrollbars for the text area.
     * @since JDK1.1
     */
    public static final int SCROLLBARS_NONE = 3;

    /**
     * Determines which scrollbars are created for the
     * text area. It can be one of four values :
     * <code>SCROLLBARS_BOTH</code> = both scrollbars.<BR>
     * <code>SCROLLBARS_HORIZONTAL_ONLY</code> = Horizontal bar only.<BR>
     * <code>SCROLLBARS_VERTICAL_ONLY</code> = Vertical bar only.<BR>
     * <code>SCROLLBARS_NONE</code> = No scrollbars.<BR>
     *
     * @serial
     * @see getScrollbarVisibility()
     */
    private int scrollbarVisibility;

    /*
     * JDK 1.1 serialVersionUID
     */
     private static final long serialVersionUID = 3692302836626095722L;

    /**
     * Initialize JNI field and method ids
     */
    private static native void initIDs();

    static {
        /* ensure that the necessary native libraries are loaded */
	Toolkit.loadLibraries();
	initIDs();
    }

    /**
     * Constructs a new text area.
     * This text area is created with scrollbar visibility equal to 
     * {@link #SCROLLBARS_BOTH}, so both vertical and horizontal 
     * scrollbars will be visible for this text area.
     */
    public TextArea() {
	this("", 0, 0, SCROLLBARS_BOTH);
    }

    /**
     * Constructs a new text area with the specified text.
     * This text area is created with scrollbar visibility equal to 
     * {@link #SCROLLBARS_BOTH}, so both vertical and horizontal 
     * scrollbars will be visible for this text area.
     * @param     text the text to be displayed.
     */
    public TextArea(String text) {
	this(text, 0, 0, SCROLLBARS_BOTH);
    }

    /**
     * Constructs a new empty text area with the specified number of
     * rows and columns.  A column is an approximate average character
     * width that is platform-dependent.  The text area is created with 
     * scrollbar visibility equal to {@link #SCROLLBARS_BOTH}, so both 
     * vertical and horizontal scrollbars will be visible for this 
     * text area.
     * @param rows the number of rows
     * @param columns the number of columns
     */
    public TextArea(int rows, int columns) {
	this("", rows, columns, SCROLLBARS_BOTH);
    }

    /**
     * Constructs a new text area with the specified text,
     * and with the specified number of rows and columns.
     * A column is an approximate average character
     * width that is platform-dependent.  The text area is created with 
     * scrollbar visibility equal to {@link #SCROLLBARS_BOTH}, so both 
     * vertical and horizontal scrollbars will be visible for this 
     * text area.
     * @param     text      the text to be displayed.
     * @param     rows      the number of rows.
     * @param     columns   the number of columns.
     */
    public TextArea(String text, int rows, int columns) {
        this(text, rows, columns, SCROLLBARS_BOTH);
    }

    /**
     * Constructs a new text area with the specified text,
     * and with the rows, columns, and scroll bar visibility
     * as specified.
     * <p>
     * The <code>TextArea</code> class defines several constants
     * that can be supplied as values for the
     * <code>scrollbars</code> argument: 
     * <code>SCROLLBARS_BOTH</code>, 
     * <code>SCROLLBARS_VERTICAL_ONLY</code>, 
     * <code>SCROLLBARS_HORIZONTAL_ONLY</code>, 
     * and <code>SCROLLBARS_NONE</code>. Any other value for the 
     * <code>scrollbars</code> argument is invalid and will result in 
     * this text area being created with scrollbar visibility equal to 
     * the default value of {@link #SCROLLBARS_BOTH}.
     * @param      text       the text to be displayed. If
     *             <code>text</code> is <code>null</code>, the empty
     *             string <code>""</code> will be displayed.
     * @param      rows       the number of rows.  If
     *             <code>rows</code> is less than <code>0</code>,
     *             <code>rows</code> is set to <code>0</code>.
     * @param      columns    the number of columns.  If
     *             <code>columns</code> is less than <code>0</code>,
     *             <code>columns</code> is set to <code>0</code>.
     * @param      scrollbars  a constant that determines what
     *             scrollbars are created to view the text area.
     * @since      JDK1.1
     */
    public TextArea(String text, int rows, int columns, int scrollbars) {
	super(text);

        this.rows = (rows >= 0) ? rows : 0;
        this.columns = (columns >= 0) ? columns : 0;

        if ((scrollbars >= SCROLLBARS_BOTH) && (scrollbars <= SCROLLBARS_NONE)) {
       	    this.scrollbarVisibility = scrollbars;
        } else {
            this.scrollbarVisibility = SCROLLBARS_BOTH;
        }
    }

    /**
     * Construct a name for this component.  Called by getName() when the
     * name is null.
     */
    String constructComponentName() {
        synchronized (getClass()) {
	    return base + nameCounter++;
	}
    }

    /**
     * Creates the TextArea's peer.  The peer allows us to modify
     * the appearance of the TextArea without changing any of its
     * functionality.
     */
    public void addNotify() {
        synchronized (getTreeLock()) {
	    if (peer == null)
	        peer = getToolkit().createTextArea(this);
	    super.addNotify();
	}
    }

    /**
     * Inserts the specified text at the specified position
     * in this text area.
     * @param      str the text to insert.
     * @param      pos the position at which to insert.
     * @see        java.awt.TextComponent#setText
     * @see        java.awt.TextArea#replaceRange
     * @see        java.awt.TextArea#append
     * @since      JDK1.1
     */
    public void insert(String str, int pos) {
    	insertText(str, pos);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>insert(String, int)</code>.
     */
    public synchronized void insertText(String str, int pos) {
	TextAreaPeer peer = (TextAreaPeer)this.peer;
	if (peer != null) {
	    peer.insertText(str, pos);
	} else {
	    text = text.substring(0, pos) + str + text.substring(pos);
	}
    }

    /**
     * Appends the given text to the text area's current text.
     * @param     str the text to append.
     * @see       java.awt.TextArea#insert
     */
    public void append(String str) {
    	appendText(str);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>append(String)</code>.
     */
    public synchronized void appendText(String str) {
	if (peer != null) {
	    insertText(str, getText().length());
	} else {
	    text = text + str;
	}
    }

    /**
     * Replaces text between the indicated start and end positions
     * with the specified replacement text.
     * @param     str      the text to use as the replacement.
     * @param     start    the start position.
     * @param     end      the end position.
     * @see       java.awt.TextArea#insert
     * @since     JDK1.1
     */
    public void replaceRange(String str, int start, int end) {
	replaceText(str, start, end);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>replaceRange(String, int, int)</code>.
     */
    public synchronized void replaceText(String str, int start, int end) {
	TextAreaPeer peer = (TextAreaPeer)this.peer;
	if (peer != null) {
	    peer.replaceText(str, start, end);
	} else {
	    text = text.substring(0, start) + str + text.substring(end);
	}
    }

    /**
     * Gets the number of rows in the text area.
     * @return    the number of rows in the text area.
     * @see       java.awt.TextArea#setRows
     * @see       java.awt.TextArea#getColumns
     * @since     JDK1
     */
    public int getRows() {
	return rows;
    }

    /**
     * Sets the number of rows for this text area.
     * @param       rows   the number of rows.
     * @see         java.awt.TextArea#getRows
     * @see         java.awt.TextArea#setColumns
     * @exception   IllegalArgumentException   if the value
     *                 supplied for <code>rows</code>
     *                 is less than <code>0</code>.
     * @since       JDK1.1
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
     * Gets the number of columns in this text area.
     * @return    the number of columns in the text area.
     * @see       java.awt.TextArea#setColumns
     * @see       java.awt.TextArea#getRows
     */
    public int getColumns() {
	return columns;
    }

    /**
     * Sets the number of columns for this text area.
     * @param       columns   the number of columns.
     * @see         java.awt.TextArea#getColumns
     * @see         java.awt.TextArea#setRows
     * @exception   IllegalArgumentException   if the value
     *                 supplied for <code>columns</code>
     *                 is less than <code>0</code>.
     * @since       JDK1.1
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
     * Gets an enumerated value that indicates which scroll bars
     * the text area uses.
     * <p>
     * The <code>TextArea</code> class defines four integer constants
     * that are used to specify which scroll bars are available.
     * <code>TextArea</code> has one constructor that gives the
     * application discretion over scroll bars.
     * @return     an integer that indicates which scroll bars are used.
     * @see        java.awt.TextArea#SCROLLBARS_BOTH
     * @see        java.awt.TextArea#SCROLLBARS_VERTICAL_ONLY
     * @see        java.awt.TextArea#SCROLLBARS_HORIZONTAL_ONLY
     * @see        java.awt.TextArea#SCROLLBARS_NONE
     * @see        java.awt.TextArea#TextArea(java.lang.String, int, int, int)
     * @since      JDK1.1
     */
    public int getScrollbarVisibility() {
        return scrollbarVisibility;
    }


    /**
     * Determines the preferred size of a text area with the specified
     * number of rows and columns.
     * @param     rows   the number of rows.
     * @param     cols   the number of columns.
     * @return    the preferred dimensions required to display
     *                       the text area with the specified
     *                       number of rows and columns.
     * @see       java.awt.Component#getPreferredSize
     * @since     JDK1.1
     */
    public Dimension getPreferredSize(int rows, int columns) {
    	return preferredSize(rows, columns);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getPreferredSize(int, int)</code>.
     */
    public Dimension preferredSize(int rows, int columns) {
        synchronized (getTreeLock()) {
	    TextAreaPeer peer = (TextAreaPeer)this.peer;
	    return (peer != null) ? 
		       peer.preferredSize(rows, columns) :
		       super.preferredSize();
        }
    }

    /**
     * Determines the preferred size of this text area.
     * @return    the preferred dimensions needed for this text area.
     * @see       java.awt.Component#getPreferredSize
     * @since     JDK1.1
     */
    public Dimension getPreferredSize() {
	return preferredSize();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getPreferredSize()</code>.
     */
    public Dimension preferredSize() {
        synchronized (getTreeLock()) {
	    return ((rows > 0) && (columns > 0)) ? 
			preferredSize(rows, columns) :
			super.preferredSize();
        }
    }

    /**
     * Determines the minimum size of a text area with the specified
     * number of rows and columns.
     * @param     rows   the number of rows.
     * @param     cols   the number of columns.
     * @return    the minimum dimensions required to display
     *                       the text area with the specified
     *                       number of rows and columns.
     * @see       java.awt.Component#getMinimumSize
     * @since     JDK1.1
     */
    public Dimension getMinimumSize(int rows, int columns) {
    	return minimumSize(rows, columns);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getMinimumSize(int, int)</code>.
     */
    public Dimension minimumSize(int rows, int columns) {
        synchronized (getTreeLock()) {
	    TextAreaPeer peer = (TextAreaPeer)this.peer;
	    return (peer != null) ? 
		       peer.minimumSize(rows, columns) :
		       super.minimumSize();
        }
    }

    /**
     * Determines the minimum size of this text area.
     * @return    the preferred dimensions needed for this text area.
     * @see       java.awt.Component#getPreferredSize
     * @since     JDK1.1
     */
    public Dimension getMinimumSize() {
	return minimumSize();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getMinimumSize()</code>.
     */
    public Dimension minimumSize() {
        synchronized (getTreeLock()) {
	    return ((rows > 0) && (columns > 0)) ? 
			minimumSize(rows, columns) :
			super.minimumSize();
        }
    }

    /**
     * Returns the parameter string representing the state of
     * this text area. This string is useful for debugging.
     * @return      the parameter string of this text area.
     */
    protected String paramString() {
	String sbVisStr;
	switch (scrollbarVisibility) {
	    case SCROLLBARS_BOTH:
		sbVisStr = "both";
		break;
	    case SCROLLBARS_VERTICAL_ONLY:
		sbVisStr = "vertical-only";
		break;
	    case SCROLLBARS_HORIZONTAL_ONLY:
		sbVisStr = "horizontal-only";
		break;
	    case SCROLLBARS_NONE:
		sbVisStr = "none";
		break;
	    default:
		sbVisStr = "invalid display policy";
	}

	return super.paramString() + ",rows=" + rows +
	    ",columns=" + columns +
	  ", scrollbarVisibility=" + sbVisStr;
    }


    /*
     * Serialization support.
     */
    /**
     * The textArea Serialized Data Version.
     *
     * @serial
     */
    private int textAreaSerializedDataVersion = 1;


    /**
     * Read the ObjectInputStream.  
     */
    private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException
    {
        s.defaultReadObject();

        // Make sure the state we just read in for columns, rows, 
        // and scrollbarVisibility has legal values
        if (columns < 0) {
            columns = 0;
        }
        if (rows < 0) {
            rows = 0;
        }

        if ((scrollbarVisibility < SCROLLBARS_BOTH) || 
            (scrollbarVisibility > SCROLLBARS_NONE)) {
            this.scrollbarVisibility = SCROLLBARS_BOTH;
        }
    }


/////////////////
// Accessibility support
////////////////


    /**
     * Gets the AccessibleContext associated with this TextArea. 
     * For text areas, the AccessibleContext takes the form of an 
     * AccessibleAWTTextArea. 
     * A new AccessibleAWTTextArea instance is created if necessary.
     *
     * @return an AccessibleAWTTextArea that serves as the 
     *         AccessibleContext of this TextArea
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTTextArea();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the 
     * <code>TextArea</code> class.  It provides an implementation of the 
     * Java Accessibility API appropriate to text area user-interface elements.
     */
    protected class AccessibleAWTTextArea extends AccessibleAWTTextComponent {

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


}

