/*
 * @(#)AbstractWriter.java	1.11 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.text;

import java.io.Writer;
import java.io.IOException;
import java.util.Enumeration;

/**
 * AbstractWriter is an abstract class that actually
 * does the work of writing out the element tree
 * including the attributes.  In terms of how much is
 * written out per line, the writer defaults to 100.
 * But this value can be set by subclasses.
 *
 * @author Sunita Mani
 * @version 1.11, 11/29/01
 */

public abstract class AbstractWriter {

    private ElementIterator it;
    private Writer out;
    private int indentLevel = 0;
    private int indentSpace = 2;
    private Document doc = null;
    private int maxLineLength = 100;
    private int currLength = 0;
    private int startOffset = 0;
    private int endOffset = 0;
    // If (indentLevel * indentSpace) becomes >= maxLineLength, this will
    // get incremened instead of indentLevel to avoid indenting going greater
    // than line length.
    private int offsetIndent = 0;
    protected static final char NEWLINE = '\n';

    /**
     * Creates a new AbstractWriter.
     * Initializes the ElementIterator with the default
     * root of the document.
     *
     * @param a Writer.
     * @param a Document
     */
    protected AbstractWriter(Writer w, Document doc) {
	this(w, doc, 0, doc.getLength());
    }
    
    /**
     * Creates a new AbstractWriter.
     * Initializes the ElementIterator with the
     * element passed in.
     *
     * @param a Writer
     * @param an Element
     * @param pos The location in the document to fetch the
     *   content.
     * @param len The amount to write out.
     */
    protected AbstractWriter(Writer w, Document doc, int pos, int len) {
	this.doc = doc;
	it = new ElementIterator(doc.getDefaultRootElement());
	out = w;
	startOffset = pos;
	endOffset = pos + len;
    }

    /**
     * Creates a new AbstractWriter.
     * Initializes the ElementIterator with the
     * element passed in.
     *
     * @param a Writer
     * @param an Element
     */
    protected AbstractWriter(Writer w, Element root) {
	this(w, root, 0, root.getEndOffset());
    }
     
    /**
     * Creates a new AbstractWriter.
     * Initializes the ElementIterator with the
     * element passed in.
     *
     * @param a Writer
     * @param an Element
     * @param pos The location in the document to fetch the
     *   content.
     * @param len The amount to write out.
     */
    protected AbstractWriter(Writer w, Element root, int pos, int len) {
	this.doc = root.getDocument();
	it = new ElementIterator(root);
	out = w;
	startOffset = pos;
	endOffset = pos + len;
    }

    /**
     * Fetches the ElementIterator.
     *
     * @return the ElementIterator.
     */
    protected ElementIterator getElementIterator() {
	return it;
    }

    /**
     * Fetches the document.
     *
     * @return the Document.
     */
    protected Document getDocument() {
	return doc;
    }

    /**
     * This method determines whether the current element
     * is in the range specified.  When no range is specified,
     * the range is initialized to be the entire document.
     * inRange() returns true if the range specified intersects
     * with the element's range.
     *
     * @param  an Element.
     * @return boolean that indicates whether the element
     *         is in the range.
     */
    protected boolean inRange(Element next) {

	if ((next.getStartOffset() >= startOffset && 
	     next.getStartOffset()  < endOffset) ||
	    (startOffset >= next.getStartOffset() &&
	     startOffset < next.getEndOffset())) {
	    return true;
	}
	return false;
    }

    /**
     * This abstract method needs to be implemented
     * by subclasses.  Its responsibility is to
     * iterate over the elements and use the write()
     * methods to generate output in the desired format.
     */
    abstract protected void write() throws IOException, BadLocationException;

    /**
     * Returns the text associated with the element.
     * The assumption here is that the element is a
     * leaf element.  Throws a BadLocationException
     * when encountered.
     *
     * @param     an Element.
     * @exception BadLocationException if pos represents an invalid
     *            location within the document.
     * @returns   the text as a String.
     */
    protected String getText(Element elem) throws BadLocationException {
	return doc.getText(elem.getStartOffset(),
			   elem.getEndOffset() - elem.getStartOffset());
    }


    /**
     * Writes out text.  If a range is specified when the constructor
     * is invoked, then only the appropriate range of text is written
     * out.
     *
     * @param     an Element.
     * @exception IOException on any I/O error
     * @exception BadLocationException if pos represents an invalid
     *            location within the document.
     */
    protected void text(Element elem) throws BadLocationException, IOException {
	String contentStr = getText(elem);
	if (contentStr.length() > 0) {
	    write(contentStr);
	}
    }

    /**
     * Enables subclasses to set the number of characters they
     * want written per line.   The default is 100.
     *
     * @param the maximum line length.
     */
    protected void setLineLength(int l) {
	maxLineLength = l;
    }

    /**
     * Enables subclasses to specify how many spaces an indent
     * maps to. When indentation takes place, the indent level
     * is multiplied by this mapping.  The default is 2.
     *
     * @param an int representing the space to indent mapping.
     */
    protected void setIndentSpace(int space) {
	indentSpace = space;
    }

    /**
     * Increments the indent level.
     */
    protected void incrIndent() {
	// Only increment to a certain point.
	if (offsetIndent > 0) {
	    offsetIndent++;
	}
	else {
	    if (++indentLevel * indentSpace >= maxLineLength) {
		offsetIndent++;
		--indentLevel;
	    }
	}
    }

    /**
     * Decrements the indent level.
     */
    protected void decrIndent() {
	if (offsetIndent > 0) {
	    --offsetIndent;
	}
	else {
	    indentLevel--;
	}
    }

    /**
     * Does indentation.  The number of spaces written
     * out is indent level times the space to map mapping.
     *
     * @exception IOException on any I/O error
     */
    protected void indent() throws IOException {
	int numOfSpaces = indentLevel*indentSpace;
	for (int i = 0; i < numOfSpaces; i++) {
	    write(' ');
	}
    }

    /**
     * Writes out a character.  If the character is
     * a newline then it resets the current length to
     * 0.  If the current length equals the maximum
     * line length, then a newline is outputed and the
     * current length is reset to 0.
     *
     * @param     a char.
     * @exception IOException on any I/O error
     */
    protected void write(char ch) throws IOException {

	out.write(ch);
	if (ch == NEWLINE) {
	    currLength = 0;
	} else {
	    ++currLength;
	    if (currLength == maxLineLength) {
		out.write(NEWLINE);
		currLength = 0;
		indent();
	    }
	}
    }

    /**
     * Writes out a string.  If writing out the string on
     * the current line results in the maximum line length
     * being exceeded, it then attempts to write this line out
     * on the next line.  However if the length of the
     * string itself exceeds the maximum line length, it
     * then recursively calls this method on the substring
     * from 0 to max line length, and then again from
     * max line length+1 to the end of the string -- inserting
     * new lines where necessary.
     *
     * @param     a String.
     * @exception IOException on any I/O error
     */
    protected void write(String str) throws IOException {

	int indentSize = indentLevel*indentSpace;
	int newlineIndex = str.indexOf(NEWLINE);
	if (currLength + str.length() <= maxLineLength) {
	    /* enuf space for the line */
	    out.write(str);
	    currLength += str.length();
	    if (newlineIndex >= 0) {
		currLength -= newlineIndex - 1;
	    }
	} else if (indentSize + str.length() <= maxLineLength) {

	    /* the line fits by itself on its own line */
	    out.write(NEWLINE);
	    currLength = 0;
	    indent();
	    out.write(str);
	    currLength = indentSize + str.length();
	    if (newlineIndex >= 0) {
		currLength -= newlineIndex - 1;
	    }
	} else {
	    /* the line is too big to fit by itself. */

	    int maxLength = maxLineLength - indentSize;
	    String substr = str.substring(0, maxLength);
	    write(substr);
	    substr = str.substring(maxLength, str.length());
	    write(substr);
	}
    }

    /**
     * Writes out the set of attributes as " <name>=<value>"
     * pairs. It throws an IOException when encountered.
     *
     * @param     an AttributeSet.
     * @exception IOException on any I/O error
     */
    protected void writeAttributes(AttributeSet attr) throws IOException {

	Enumeration names = attr.getAttributeNames();
	while (names.hasMoreElements()) {
	    Object name = names.nextElement();
	    write(" " + name + "=" + attr.getAttribute(name));
	}
    }
}
