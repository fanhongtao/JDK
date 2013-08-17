/*
 * @(#)TextArea.java	1.34 97/01/27
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
 */
package java.awt;

import java.awt.peer.TextAreaPeer;

/**
 * A TextArea object is a multi-line area that displays text. It can
 * be set to allow editing or read-only modes.
 *
 * @version	1.34, 01/27/97
 * @author 	Sami Shaio
 */
public class TextArea extends TextComponent {

    /**
     * The number of rows in the TextArea.
     */
    int	rows;

    /**
     * The number of columns in the TextArea.
     */
    int	columns;

    private static final String base = "text";
    private static int nameCounter = 0;

    /**
     * Create and display both vertical and horizontal scrollbars.
     */
    public static final int SCROLLBARS_BOTH = 0;

    /**
     * Create and display vertical scrollbar only.
     */
    public static final int SCROLLBARS_VERTICAL_ONLY = 1;

    /**
     * Create and display horizontal scrollbar only.
     */
    public static final int SCROLLBARS_HORIZONTAL_ONLY = 2;

    /**
     * Do not create or display any scrollbars for the text area.
     */
    public static final int SCROLLBARS_NONE = 3;

    /**
     * Determines which scrollbars are created for the 
     * text area.
     */
    private int scrollbarVisibility;

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = 3692302836626095722L;

    /**
     * Constructs a new TextArea.
     */
    public TextArea() {
	this("", 0, 0, SCROLLBARS_BOTH);
    }

    /**
     * Constructs a new TextArea with the specified text displayed.
     * @param text the text to be displayed 
     */
    public TextArea(String text) {
	this(text, 0, 0, SCROLLBARS_BOTH);
    }

    /**
     * Constructs a new empty TextArea with the specified number of
     * rows and columns.
     * @param rows the number of rows
     * @param columns the number of columns
     */
    public TextArea(int rows, int columns) {
	this("", rows, columns);
    }

    /**
     * Constructs a new TextArea with the specified text and number
     * of rows and columns.
     * @param text the text to be displayed
     * @param rows the number of rows
     * @param columns the number of columns
     */
    public TextArea(String text, int rows, int columns) {
        this(text, rows, columns, SCROLLBARS_BOTH);
    }

    /**
     * Constructs a new TextArea with the specified text and number
     * of rows, columns, and scrollbar visibility.
     * @param text the text to be displayed
     * @param rows the number of rows
     * @param columns the number of columns
     * @param scrollbars the visibility of scrollbars
     */
    public TextArea(String text, int rows, int columns, int scrollbars) {
	super(text);
	this.name = base + nameCounter++;
	this.rows = rows;
	this.columns = columns;
	this.scrollbarVisibility = scrollbars;
    }

    /**
     * Creates the TextArea's peer.  The peer allows us to modify
     * the appearance of the TextArea without changing any of its
     * functionality.
     */
    public void addNotify() {
	peer = getToolkit().createTextArea(this);
	super.addNotify();
    }

    /**
     * Inserts the specified text at the specified position.
     * @param str the text to insert.
     * @param pos the position at which to insert.
     * @see TextComponent#setText
     * @see #replaceRange
     */
    public synchronized void insert(String str, int pos) {
    	insertText(str, pos);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by insert(String, int).
     */
    public void insertText(String str, int pos) {
	TextAreaPeer peer = (TextAreaPeer)this.peer;
	if (peer != null) {
	    peer.insertText(str, pos);
	} else {
	    text = text.substring(0, pos) + str + text.substring(pos);
	}
    }

    /**
     * Appends the given text to the end.
     * @param str the text to insert
     * @see #insert
     */
    public synchronized void append(String str) {
    	appendText(str);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by append(String).
     */
    public void appendText(String str) {
	if (peer != null) {
	    insertText(str, getText().length());
	} else {
	    text = text + str;
	}
    }

    /**
     * Replaces text from the indicated start to end position with the
     * new text specified.
     * @param str the text to use as the replacement.
     * @param start the start position.
     * @param end the end position.
     * @see #insert
     * @see #replaceRange
     */
    public synchronized void replaceRange(String str, int start, int end) {
	replaceText(str, start, end);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by replaceRange(String, int, int).
     */
    public void replaceText(String str, int start, int end) {
	TextAreaPeer peer = (TextAreaPeer)this.peer;
	if (peer != null) {
	    peer.replaceText(str, start, end);
	} else {
	    text = text.substring(0, start) + str + text.substring(end);
	}
    }

    /**
     * Returns the number of rows in the TextArea.
     */
    public int getRows() {
	return rows;
    }

    /**
     * Sets the number of rows for this TextArea.
     * @param rows the number of rows
     * @exception IllegalArgumentException If rows is less than 0.
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
     * Returns the number of columns in the TextArea.
     */
    public int getColumns() {
	return columns;
    }

    /**
     * Sets the number of columns for this TextArea.
     * @param columns the number of columns
     * @exception IllegalArgumentException If columns is less than 0.
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
     * Returns the enumerated value describing which scrollbars
     * the text area has.
     * @return the display policy for the scrollbars
     */
    public int getScrollbarVisibility() {
        return scrollbarVisibility;
    }


    /**
     * Returns the specified row and column Dimensions of the TextArea.
     * @param rows the preferred rows amount
     * @param columns the preferred columns amount
     */
    public Dimension getPreferredSize(int rows, int columns) {
    	return preferredSize(rows, columns);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getPreferredSize(int, int).
     */
    public Dimension preferredSize(int rows, int columns) {
	synchronized (Component.LOCK) {
	    TextAreaPeer peer = (TextAreaPeer)this.peer;
	    return (peer != null) ? 
		       peer.preferredSize(rows, columns) :
		       super.preferredSize();
	}
    }

    /**
     * Returns the preferred size Dimensions of the TextArea.
     */
    public Dimension getPreferredSize() {
	return preferredSize();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getPreferredSize().
     */
    public Dimension preferredSize() {
	synchronized (Component.LOCK) {
	    return ((rows > 0) && (columns > 0)) ? 
		       preferredSize(rows, columns) :
		       super.preferredSize();
	}
    }

    /**
     * Returns the specified minimum size Dimensions of the TextArea.
     * @param rows the minimum row size
     * @param columns the minimum column size
     */
    public Dimension getMinimumSize(int rows, int columns) {
    	return minimumSize(rows, columns);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getMinimumSize(int, int).
     */
    public Dimension minimumSize(int rows, int columns) {
	synchronized (Component.LOCK) {
	    TextAreaPeer peer = (TextAreaPeer)this.peer;
	    return (peer != null) ? 
		       peer.minimumSize(rows, columns) :
		       super.minimumSize();
	}
    }

    /**
     * Returns the minimum size Dimensions of the TextArea.
     */
    public Dimension getMinimumSize() {
	return minimumSize();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getMinimumSize().
     */
    public Dimension minimumSize() {
	synchronized (Component.LOCK) {
	    return ((rows > 0) && (columns > 0)) ? 
		       minimumSize(rows, columns) :
		       super.minimumSize();
	}
    }

    /**
     * Returns the String of parameters for this TextArea.
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

}
