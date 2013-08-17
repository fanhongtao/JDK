/*
 * @(#)HTMLWriter.java	1.23 99/04/22
 *
 * Copyright 1998, 1999 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package javax.swing.text.html;

import javax.swing.text.*;
import java.io.Writer;
import java.util.Stack;
import java.util.Enumeration;
import java.util.Vector;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;

/**
 * This is a writer for HTMLDocuments.
 *
 * @author  Sunita Mani
 * @version 1.23, 04/22/99
 */


public class HTMLWriter extends AbstractWriter {

    /*
     * Stores all elements for which end tags have to
     * be emitted.
     */
    private Stack blockElementStack = new Stack();
    private boolean inContent = false;
    private boolean inPre = false;
    /** When inPre is true, this will indicate the end offset of the pre
     * element. */
    private int preEndOffset;
    private boolean inTextArea = false;
    private boolean newlineOutputed = false;
    private boolean completeDoc;

    /*
     * Stores all embedded tags. i.e tags that are
     * stored as attributes in other tags. For example,
     * <b>, <i>, <font>, <a>.  Basically tags that
     * are essentially character level attributes.
     */
    private Vector tags = new Vector(10);

    /**
     * Values for the tags.
     */
    private Vector tagValues = new Vector(10);

    /**
     * Used when writing out a string.
     */
    private char[] tempChars;

    /**
     * Used when indenting. Will contain the spaces.
     */
    private char[] indentChars;

    /**
     * Used when writing out content.
     */
    private Segment segment;

    /*
     * This is used in closeOutUnwantedEmbeddedTags.
     */
    private Vector tagsToRemove = new Vector(10);

    /**
     * Set to true after the head has been output.
     */
    private boolean wroteHead;

    //
    // The following are temporary until new API can be added.
    //
    /** This class does its own writing. */
    private Writer out;
    /** If this is true when outputting a string and the length exceeds
     * maxLineLength a newline will be output. */
    private int maxLineLength = 80;
    private int currLength;
    private int indentLevel = 0;
    private int indentSpace = 2;
    // If (indentLevel * indentSpace) becomes >= maxLineLength, this will
    // get incremened instead of indentLevel to avoid indenting going greater
    // than line length.
    private int offsetIndent = 0;
    /** String used for end of line. If the Document has the property
     * EndOfLineStringProperty, it will be used for newlines. Otherwise
     * the System property line.separator will be used. */
    private String newline;
    /** If this is true, the line is empty. indent() does not make this
     * true. */
    private boolean isLineEmpty;
    private int startOffset;
    private int endOffset;


    /**
     * Creates a new HTMLWriter.
     *
     * @param a  Writer
     * @param an HTMLDocument
     *
     */
    public HTMLWriter(Writer w, HTMLDocument doc) {
	this(w, doc, 0, doc.getLength());
    }

    /**
     * Creates a new HTMLWriter.
     *
     * @param a  Writer
     * @param an HTMLDocument
     * @param pos The location in the document to fetch the
     *   content.
     * @param len The amount to write out.
     */
    public HTMLWriter(Writer w, HTMLDocument doc, int pos, int len) {
	super(w, doc, pos, len);
	completeDoc = (pos == 0 && len == doc.getLength());
	this.maxLineLength = 80;
	this.out = w;
	Object docNewline = doc.getProperty(DefaultEditorKit.
				       EndOfLineStringProperty);
	if (docNewline instanceof String) {
	    newline = (String)docNewline;
	}
	else {
	    try {
		newline = System.getProperty("line.separator");
	    } catch (SecurityException se) {}
	    if (newline == null) {
		// Should not get here, but if we do it means we could not
		// find a newline string, use \n in this case.
		newline = "\n";
	    }
	}
	startOffset = pos;
	endOffset = pos + len;
    }

    /**
     * This is method that iterates over the the
     * Element tree and controls the writing out of
     * all the tags and its attributes.
     *
     * @exception IOException on any I/O error
     * @exception BadLocationException if pos represents an invalid
     *            location within the document.
     *
     */
    public void write() throws IOException, BadLocationException {
	ElementIterator it = getElementIterator();
	Element current = null;
	Element next = null;

	wroteHead = false;
	tempChars = null;
	currLength = 0;
	isLineEmpty = true;
	if (segment == null) {
	    segment = new Segment();
	}
	inPre = false;
	while ((next = it.next()) != null) {
	    if (!inRange(next)) {
		continue;
	    }
	    if (current != null) {
		
		/*
		  if next is child of current increment indent
		*/

		if (indentNeedsIncrementing(current, next)) {
		    incrIndent();
		} else if (current.getParentElement() != next.getParentElement()) {
		    /*
		       next and current are not siblings
		       so emit end tags for items on the stack until the
		       item on top of the stack, is the parent of the
		       next.
		    */
		    Element top = (Element)blockElementStack.peek();
		    while (top != next.getParentElement()) {
			/*
			   pop() will return top.
			*/
			blockElementStack.pop();
			if (!synthesizedElement(top)) {
			    if (!matchNameAttribute(top.getAttributes(), HTML.Tag.PRE)) {
				decrIndent();
			    }
			    endTag(top);
			}
			top = (Element)blockElementStack.peek();
		    }
		} else if (current.getParentElement() == next.getParentElement()) {
		    /*
		       if next and current are siblings the indent level
		       is correct.  But, we need to make sure that if current is
		       on the stack, we pop it off, and put out its end tag.
		    */
		    Element top = (Element)blockElementStack.peek();
		    if (top == current) {
			blockElementStack.pop();
			endTag(top);
		    }
		}
	    }
	    if (!next.isLeaf() || isFormElementWithContent(next.getAttributes())) {
		blockElementStack.push(next);
		startTag(next);
	    } else {
		emptyTag(next);
	    }
	    current = next;
	}

	/* Emit all remaining end tags */

	/* A null parameter ensures that all embedded tags
	   currently in the tags vector have their
	   corresponding end tags written out.
	*/
	closeOutUnwantedEmbeddedTags(null);

	while (!blockElementStack.empty()) {
	    current = (Element)blockElementStack.pop();
	    if (!synthesizedElement(current)) {
		if (!matchNameAttribute(current.getAttributes(), HTML.Tag.PRE)) {
		    decrIndent();
		}
		endTag(current);
	    }
	}

	if (completeDoc) {
	    writeAdditionalComments();
	}

	segment.array = null;
    }


    /**
     * Writes out the attribute set.  Ignores all
     * attributes with a key of type HTML.Tag,
     * attributes with a key of type StyleConstants,
     * and attributes with a key of type
     * HTML.Attribute.ENDTAG.
     *
     * @param     an AttributeSet.
     * @exception IOException on any I/O error
     *
     */
    protected void writeAttributes(AttributeSet attr) throws IOException {
	// translate css attributes to html
	convAttr.removeAttributes(convAttr);
	convertToHTML32(attr, convAttr);

	Enumeration names = convAttr.getAttributeNames();
	while (names.hasMoreElements()) {
	    Object name = names.nextElement();
	    if (name instanceof HTML.Tag || 
		name instanceof StyleConstants || 
		name == HTML.Attribute.ENDTAG) {
		continue;
	    }
	    write(" " + name + "=\"" + convAttr.getAttribute(name) + "\"");
	}
    }

    /**
     * Writes out all empty elements i.e tags that have no
     * corresponding end tag.
     *
     * @param     an Element.
     * @exception IOException on any I/O error
     * @exception BadLocationException if pos represents an invalid
     *            location within the document.
     */
    protected void emptyTag(Element elem) throws BadLocationException, IOException {

	if (!inContent && !inPre) {
	    indent();
	}

	AttributeSet attr = elem.getAttributes();
	closeOutUnwantedEmbeddedTags(attr);
	writeEmbeddedTags(attr);

	if (matchNameAttribute(attr, HTML.Tag.CONTENT)) {
	    inContent = true;
	    text(elem);
	} else if (matchNameAttribute(attr, HTML.Tag.COMMENT)) {
	    comment(elem);
	}  else {
	    boolean isBlock = isBlockTag(elem.getAttributes());
	    if (inContent && isBlock ) {
		writeNewline();
		indent();
	    }

	    Object nameTag = (attr != null) ? attr.getAttribute
		              (StyleConstants.NameAttribute) : null;
	    Object endTag = (attr != null) ? attr.getAttribute
		              (HTML.Attribute.ENDTAG) : null;

	    boolean outputEndTag = false;
	    // If an instance of an UNKNOWN Tag, or an instance of a 
	    // tag that is only visible during editing
	    //
	    if (nameTag != null && endTag != null &&
		(endTag instanceof String) &&
		((String)endTag).equals("true")) {
		outputEndTag = true;
	    }

	    if (completeDoc && matchNameAttribute(attr, HTML.Tag.HEAD)) {
		if (outputEndTag) {
		    // Write out any styles.
		    writeStyles(((HTMLDocument)getDocument()).getStyleSheet());
		}
		wroteHead = true;
	    }

	    write('<');
	    if (outputEndTag) {
		write('/');
	    }
	    write(elem.getName());
	    writeAttributes(attr);
	    write('>');
	    if (matchNameAttribute(attr, HTML.Tag.TITLE) && !outputEndTag) {
		Document doc = elem.getDocument();
		String title = (String)doc.getProperty(Document.TitleProperty);
		write(title);
	    } else if (!inContent || isBlock) {
		writeNewline();
		if (isBlock && inContent) {
		    indent();
		}
	    }
	}
    }

    /**
     * Determines if the HTML.Tag associated with the
     * element is a block tag.
     *
     * @param   AttributeSet.
     * @return  true if tag is block tag, false otherwise.
     */
    protected boolean isBlockTag(AttributeSet attr) {
	Object o = attr.getAttribute(StyleConstants.NameAttribute);
	if (o instanceof HTML.Tag) {
	    HTML.Tag name = (HTML.Tag) o;
	    return name.isBlock();
	}
	return false;
    }


    /**
     * Writes out a start tag for the element.
     * Ignores all synthesized elements.
     *
     * @param     an Element.
     * @exception IOException on any I/O error
     */
    protected void startTag(Element elem) throws IOException, BadLocationException {

	if (synthesizedElement(elem)) {
	    return;
	}

	// Determine the name, as an HTML.Tag.
	AttributeSet attr = elem.getAttributes();
	Object nameAttribute = attr.getAttribute(StyleConstants.NameAttribute);
	HTML.Tag name;
	if (nameAttribute instanceof HTML.Tag) {
	    name = (HTML.Tag)nameAttribute;
	}
	else {
	    name = null;
	}

	if (name == HTML.Tag.PRE) {
	    inPre = true;
	    preEndOffset = elem.getEndOffset();
	}

	// write out end tags for item on stack
	closeOutUnwantedEmbeddedTags(attr);

	if (inContent) { 
	    writeNewline();
	    inContent = false;
	    newlineOutputed = false;
	}

	if (completeDoc && name == HTML.Tag.BODY && !wroteHead) {
	    // If the head has not been output, output it and the styles.
	    wroteHead = true;
	    indent();
	    write("<head>");
	    writeNewline();
	    incrIndent();
	    writeStyles(((HTMLDocument)getDocument()).getStyleSheet());
	    decrIndent();
	    writeNewline();
	    indent();
	    write("</head>");
	    writeNewline();
	}

	indent();
	write('<');
	write(elem.getName());
	writeAttributes(attr);
	write('>');
	if (name != HTML.Tag.PRE) {
	    writeNewline();
	}

	if (name == HTML.Tag.TEXTAREA) {
	    textAreaContent(elem.getAttributes());
	} else if (name == HTML.Tag.SELECT) {
	    selectContent(elem.getAttributes());
	} else if (completeDoc && name == HTML.Tag.BODY) {
	    // Write out the maps, which is not stored as Elements in
	    // the Document.
	    writeMaps(((HTMLDocument)getDocument()).getMaps());
	}
	else if (name == HTML.Tag.HEAD) {
	    wroteHead = true;
	}
    }

    
    /**
     * Writes out text that is contained in a TEXTAREA form
     * element.
     *
     * @param AttributeSet
     * @exception IOException on any I/O error
     * @exception BadLocationException if pos represents an invalid
     *            location within the document.
     */
    protected void textAreaContent(AttributeSet attr) throws BadLocationException, IOException {
	Document doc = (Document)attr.getAttribute(StyleConstants.ModelAttribute);
	if (doc != null && doc.getLength() > 0) {
	    if (segment == null) {
		segment = new Segment();
	    }
	    doc.getText(0, doc.getLength(), segment);
	    if (segment.count > 0) {
		inTextArea = true;
		incrIndent();
		indent();
		write(segment.array, segment.offset, segment.count, true,
		      true);
		writeNewline();
		inTextArea = false;
		decrIndent();
	    }
	}
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
	int start = Math.max(startOffset, elem.getStartOffset());
	int end = Math.min(endOffset, elem.getEndOffset());
	if (start < end) {
	    if (segment == null) {
		segment = new Segment();
	    }
	    getDocument().getText(start, end - start, segment);
	    newlineOutputed = false;
	    if (segment.count > 0) {
		if (segment.array[segment.offset + segment.count - 1] == '\n'){
		    newlineOutputed = true;
		}
		if (inPre && end == preEndOffset) {
		    if (segment.count > 1) {
			segment.count--;
		    }
		    else {
			return;
		    }
		}
		write(segment.array, segment.offset, segment.count, !inPre,
		      true);
	    }
	}
    }

    /**
     * Writes out the content of the SELECT form element.
     * 
     * @param AttributeSet associcated with the form element.
     * @exception IOException on any I/O error
     */
    protected void selectContent(AttributeSet attr) throws IOException {
	Object model = attr.getAttribute(StyleConstants.ModelAttribute);
	incrIndent();
	if (model instanceof OptionListModel) {
	    OptionListModel listModel = (OptionListModel)model;
	    int size = listModel.getSize();
	    for (int i = 0; i < size; i++) {
		Option option = (Option)listModel.getElementAt(i);
		writeOption(option);
	    }
	} else if (model instanceof OptionComboBoxModel) {
	    OptionComboBoxModel comboBoxModel = (OptionComboBoxModel)model;
	    int size = comboBoxModel.getSize();
	    for (int i = 0; i < size; i++) {
		Option option = (Option)comboBoxModel.getElementAt(i);
		writeOption(option);
	    }
	}
	decrIndent();
    }


    /**
     * Writes out the content of the Option form element.
     * @param Option.
     * @exception IOException on any I/O error
     * 
     */
    protected void writeOption(Option option) throws IOException {
	
	indent();
	write('<');
	write("option ");
	if (option.getValue() != null) {
	    write("value="+ option.getValue());
	}
	if (option.isSelected()) {
	    write(" selected");
	}
	write('>');
	if (option.getLabel() != null) {
	    write(option.getLabel());
	}
	writeNewline();
    }

    /**
     * Writes out an end tag for the element.
     *
     * @param     an Element.
     * @exception IOException on any I/O error
     */
    protected void endTag(Element elem) throws IOException {
	if (synthesizedElement(elem)) {
	    return;
	}
	if (matchNameAttribute(elem.getAttributes(), HTML.Tag.PRE)) {
	    inPre = false;
	}

	// write out end tags for item on stack
	closeOutUnwantedEmbeddedTags(elem.getAttributes());
	if (inContent) { 
	    if (!newlineOutputed) {
		writeNewline();
	    }
	    newlineOutputed = false;
	    inContent = false;
	}
	indent();
        write('<');
        write('/');
        write(elem.getName());
        write('>');
	writeNewline();
    }



    /**
     * Writes out comments.
     *
     * @param     an element.
     * @exception IOException on any I/O error
     * @exception BadLocationException if pos represents an invalid
     *            location within the document.
     */
    protected void comment(Element elem) throws BadLocationException, IOException {
	AttributeSet as = elem.getAttributes();
	if (matchNameAttribute(as, HTML.Tag.COMMENT)) {
	    Object comment = as.getAttribute(HTML.Attribute.COMMENT);
	    if (comment instanceof String) {
		writeComment((String)comment);
	    }
	    else {
		writeComment(null);
	    }
	}
    }


    /**
     * Writes out comment string.
     *
     * @param     string the comment.
     * @exception IOException on any I/O error
     * @exception BadLocationException if pos represents an invalid
     *            location within the document.
     */
    void writeComment(String string) throws IOException {
	write("<!--");
	if (string != null) {
	    write(string);
	}
	write("-->");
	writeNewline();
    }


    /**
     * Writes out any additional comments (comments outside of the body)
     * stored under the property HTMLDocument.AdditionalComments.
     */
    void writeAdditionalComments() throws IOException {
	Object comments = getDocument().getProperty
	                                (HTMLDocument.AdditionalComments);

	if (comments instanceof Vector) {
	    Vector v = (Vector)comments;
	    for (int counter = 0, maxCounter = v.size(); counter < maxCounter;
		 counter++) {
		writeComment(v.elementAt(counter).toString());
	    }
	}
    }


    /**
     * This method returns true, if the element is a
     * synthesized element.  Currently we are only testing
     * for the p-implied tag.
     */
    protected boolean synthesizedElement(Element elem) {
	if (matchNameAttribute(elem.getAttributes(), HTML.Tag.IMPLIED)) {
	    return true;
	}
	return false;
    }


    /**
     * This method return true if the StyleConstants.NameAttribute is
     * equal to the tag that is passed in as a parameter.
     */
    protected boolean matchNameAttribute(AttributeSet attr, HTML.Tag tag) {
	Object o = attr.getAttribute(StyleConstants.NameAttribute);
	if (o instanceof HTML.Tag) {
	    HTML.Tag name = (HTML.Tag) o;
	    if (name == tag) {
		return true;
	    }
	}
	return false;
    }

    /**
     * This method searches for embedded tags in the AttributeSet
     * and writes them out.  It also stores these tags in a vector
     * so that when appropriate the corresponding end tags can be
     * written out.
     *
     * @exception IOException on any I/O error
     */
    protected void writeEmbeddedTags(AttributeSet attr) throws IOException {
	
	// translate css attributes to html
	attr = convertToHTML(attr, oConvAttr);

	Enumeration names = attr.getAttributeNames();
	while (names.hasMoreElements()) {
	    Object name = names.nextElement();
	    if (name instanceof HTML.Tag) {
		HTML.Tag tag = (HTML.Tag)name;
		if (tag == HTML.Tag.FORM || tags.contains(tag)) {
		    continue;
		}
		write('<');
		write(tag.toString());
		Object o = attr.getAttribute(tag);
		if (o != null && o instanceof AttributeSet) {
		    writeAttributes((AttributeSet)o);
		}
		write('>');
		tags.addElement(tag);
		tagValues.addElement(o);
	    }
	}
    }


    /**
     * This method searches the attribute set for a tag, both of which
     * are passed in as a parameter.  Returns true if no match is found
     * and false otherwise.
     */
    private boolean noMatchForTagInAttributes(AttributeSet attr, HTML.Tag t,
					      Object tagValue) {
	if (attr != null && attr.isDefined(t)) {
	    Object newValue = attr.getAttribute(t);

	    if ((tagValue == null) ? (newValue == null) :
		(newValue != null && tagValue.equals(newValue))) {
		return false;
	    }
	}
	return true;
    }


    /**
     * This method searches the attribute set and for each tag
     * that is stored in the tag vector.  If the tag isnt found,
     * then the tag is removed from the vector and a corresponding
     * end tag is written out.
     *
     * @exception IOException on any I/O error
     */
    protected void closeOutUnwantedEmbeddedTags(AttributeSet attr) throws IOException {

	tagsToRemove.removeAllElements();

	// translate css attributes to html
	attr = convertToHTML(attr, null);

	HTML.Tag t;
	Object tValue;
	int firstIndex = -1;
	int size = tags.size();
	// First, find all the tags that need to be removed.
	for (int i = size - 1; i >= 0; i--) {
	    t = (HTML.Tag)tags.elementAt(i);
	    tValue = tagValues.elementAt(i);
	    if ((attr == null) || noMatchForTagInAttributes(attr, t, tValue)) {
		firstIndex = i;
		tagsToRemove.addElement(t);
	    }
	}
	if (firstIndex != -1) {
	    // Then close them out.
	    boolean removeAll = ((size - firstIndex) == tagsToRemove.size());
	    for (int i = size - 1; i >= firstIndex; i--) {
		t = (HTML.Tag)tags.elementAt(i);
		if (removeAll || tagsToRemove.contains(t)) {
		    tags.removeElementAt(i);
		    tagValues.removeElementAt(i);
		}
		write('<');
		write('/');
		write(t.toString());
		write('>');
	    }
	    // Have to output any tags after firstIndex that still remaing,
	    // as we closed them out, but they should remain open.
	    size = tags.size();
	    for (int i = firstIndex; i < size; i++) {
		t = (HTML.Tag)tags.elementAt(i);
		write('<');
		write(t.toString());
		Object o = tagValues.elementAt(i);
		if (o != null && o instanceof AttributeSet) {
		    writeAttributes((AttributeSet)o);
		}
		write('>');
	    }
	}
    }


    /**
     * Determines if the element associated with the attributeset
     * is a TEXTAREA or SELECT.  If true, returns true else
     * false
     */
    private boolean isFormElementWithContent(AttributeSet attr) {
	if (matchNameAttribute(attr, HTML.Tag.TEXTAREA) ||
	    matchNameAttribute(attr, HTML.Tag.SELECT)) {
	    return true;
	}
	return false;
    }


    /**
     * This method determines whether a the indentation needs to be
     * incremented.  Basically, if next is a child of current, and
     * next is NOT a synthesized element, the indent level will be
     * incremented.  If there is a parent-child relationship and "next"
     * is a synthesized element, then its children must be indented.
     * This state is maintained by the indentNext boolean.
     *
     * @return boolean that returns true if indent level
     *         needs incrementing.
     */
    private boolean indentNext = false;
    private boolean indentNeedsIncrementing(Element current, Element next) {
	if ((next.getParentElement() == current) && !inPre) {
	    if (indentNext) {
		indentNext = false;
		return true;
	    } else if (synthesizedElement(next)) {
		indentNext = true;
	    } else if (!synthesizedElement(current)){
		return true;
	    }
	}
	return false;
    }

    /**
     * Outputs the maps as elements. Maps are not stored as elements in
     * the document, and as such this is used to output them.
     */
    void writeMaps(Enumeration maps) throws IOException {
	if (maps != null) {
	    while(maps.hasMoreElements()) {
		Map map = (Map)maps.nextElement();
		String name = map.getName();

		incrIndent();
		indent();
		write("<map");
		if (name != null) {
		    write(" name=\"");
		    write(name);
		    write("\">");
		}
		else {
		    write('>');
		}
		writeNewline();
		incrIndent();

		// Output the areas
		AttributeSet[] areas = map.getAreas();
		if (areas != null) {
		    for (int counter = 0, maxCounter = areas.length;
			 counter < maxCounter; counter++) {
			indent();
			write("<area");
			writeAttributes(areas[counter]);
			write("></area>");
			writeNewline();
		    }
		}
		decrIndent();
		indent();
		write("</map>");
		writeNewline();
		decrIndent();
	    }
	}
    }

    /**
     * Outputs the styles as a single element. Styles are not stored as
     * elements, but part of the document. For the time being styles are
     * written out as a comment, inside a style tag.
     */
    void writeStyles(StyleSheet sheet) throws IOException {
	if (sheet != null) {
	    Enumeration styles = sheet.getStyleNames();
	    if (styles != null) {
		boolean outputStyle = false;
		while (styles.hasMoreElements()) {
		    String name = (String)styles.nextElement();
		    // Don't write out the default style.
		    if (!StyleContext.DEFAULT_STYLE.equals(name) &&
			writeStyle(name, sheet.getStyle(name), outputStyle)) {
			outputStyle = true;
		    }
		}
		if (outputStyle) {
		    writeStyleEndTag();
		}
	    }
	}
    }

    /**
     * Outputs the named style. <code>outputStyle</code> indicates
     * whether or not a style has been output yet. This will return
     * true if a style is written.
     */
    boolean writeStyle(String name, Style style, boolean outputStyle)
	         throws IOException{
	boolean didOutputStyle = false;
	Enumeration attributes = style.getAttributeNames();
	if (attributes != null) {
	    while (attributes.hasMoreElements()) {
		Object attribute = attributes.nextElement();
		if (attribute instanceof CSS.Attribute) {
		    String value = style.getAttribute(attribute).toString();
		    if (value != null) {
			if (!outputStyle) {
			    writeStyleStartTag();
			    outputStyle = true;
			}
			if (!didOutputStyle) {
			    didOutputStyle = true;
			    indent();
			    write(name);
			    write(" {");
			}
			else {
			    write(";");
			}
			write(' ');
			write(attribute.toString());
			write(": ");
			write(value);
		    }
		}
	    }
	}
	if (didOutputStyle) {
	    write(" }");
	    writeNewline();
	}
	return didOutputStyle;
    }

    void writeStyleStartTag() throws IOException {
	indent();
	write("<style type=\"text/css\">");
	incrIndent();
	writeNewline();
	indent();
	write("<!--");
	incrIndent();
	writeNewline();
    }

    void writeStyleEndTag() throws IOException {
	decrIndent();
	indent();
	write("-->");
	writeNewline();
	decrIndent();
	indent();
	write("</style>");
	writeNewline();
	indent();
    }

    // --- conversion support ---------------------------

    /**
     * Convert the give set of attributes to be html for
     * the purpose of writing them out.  Any keys that
     * have been converted will not appear in the resultant
     * set.  Any keys not converted will appear in the 
     * resultant set the same as the received set.<p>
     * This will put the converted values into <code>to</code>, unless
     * it is null in which case a temporary AttributeSet will be returned.
     */
    AttributeSet convertToHTML(AttributeSet from, MutableAttributeSet to) {
	if (to == null) {
	    to = convAttr;
	}
	to.removeAttributes(to);
	if (writeCSS) {
	    convertToHTML40(from, to);
	} else {
	    convertToHTML32(from, to);
	}
	return to;
    }

    /**
     * If true, the writer will emit CSS attributes in preference
     * to HTML tags/attributes (i.e. It will emit an HTML 4.0
     * style).
     */
    private boolean writeCSS = false;

    /**
     * Buffer for the purpose of attribute conversion
     */
    private MutableAttributeSet convAttr = new SimpleAttributeSet();

    /**
     * Buffer for the purpose of attribute conversion. This can be
     * used if convAttr is being used.
     */
    private MutableAttributeSet oConvAttr = new SimpleAttributeSet();

    /**
     * Create an older style of HTML attributes.  This will 
     * convert character level attributes that have a StyleConstants
     * mapping over to an HTML tag/attribute.  Other CSS attributes
     * will be placed in an HTML style attribute.
     */
    private static void convertToHTML32(AttributeSet from, MutableAttributeSet to) {
	if (from == null) {
	    return;
	}
	Enumeration keys = from.getAttributeNames();
	String value = "";
	while (keys.hasMoreElements()) {
	    Object key = keys.nextElement();
	    if (key instanceof CSS.Attribute) {
		if ((key == CSS.Attribute.FONT_FAMILY) ||
		    (key == CSS.Attribute.FONT_SIZE) ||
		    (key == CSS.Attribute.COLOR)) {
		    
		    createFontAttribute((CSS.Attribute)key, from, to);
		} else if (key == CSS.Attribute.FONT_WEIGHT) {
		    // add a bold tag is weight is bold
		    CSS.FontWeight weightValue = (CSS.FontWeight) 
			from.getAttribute(CSS.Attribute.FONT_WEIGHT);
		    if ((weightValue != null) && (weightValue.getValue() > 400)) {
			to.addAttribute(HTML.Tag.B, SimpleAttributeSet.EMPTY);
		    }
		} else if (key == CSS.Attribute.FONT_STYLE) {
		    String s = from.getAttribute(key).toString();
		    if (s.indexOf("italic") >= 0) {
			to.addAttribute(HTML.Tag.I, SimpleAttributeSet.EMPTY);
		    }
		} else if (key == CSS.Attribute.TEXT_DECORATION) {
		    String decor = from.getAttribute(key).toString();
		    if (decor.indexOf("underline") >= 0) {
			to.addAttribute(HTML.Tag.U, SimpleAttributeSet.EMPTY);
		    }
		    if (decor.indexOf("line-through") >= 0) {
			to.addAttribute(HTML.Tag.STRIKE, SimpleAttributeSet.EMPTY);
		    }
		} else if (key == CSS.Attribute.VERTICAL_ALIGN) {
		    String vAlign = from.getAttribute(key).toString();
		    if (vAlign.indexOf("sup") >= 0) {
			to.addAttribute(HTML.Tag.SUP, SimpleAttributeSet.EMPTY);
		    }
		    if (vAlign.indexOf("sub") >= 0) {
			to.addAttribute(HTML.Tag.SUB, SimpleAttributeSet.EMPTY);
		    }
		} else if (key == CSS.Attribute.TEXT_ALIGN) {
		    to.addAttribute(HTML.Attribute.ALIGN, 
				    from.getAttribute(key).toString());
		} else {
		    // default is to store in a HTML style attribute
		    if (value.length() > 0) {
			value = value + "; ";
		    }
		    value = value + key + ": " + from.getAttribute(key);
		}
	    } else {
		to.addAttribute(key, from.getAttribute(key));
	    }
	}
	if (value.length() > 0) {
	    to.addAttribute(HTML.Attribute.STYLE, value);
	}
    }

    /**
     * Create/update an HTML &lt;font&gt; tag attribute.  The
     * value of the attribute should be a MutableAttributeSet so
     * that the attributes can be updated as they are discovered.
     */
    private static void createFontAttribute(CSS.Attribute a, AttributeSet from, 
				    MutableAttributeSet to) {
	MutableAttributeSet fontAttr = (MutableAttributeSet) 
	    to.getAttribute(HTML.Tag.FONT);
	if (fontAttr == null) {
	    fontAttr = new SimpleAttributeSet();
	    to.addAttribute(HTML.Tag.FONT, fontAttr);
	}
	// edit the parameters to the font tag
	String htmlValue = from.getAttribute(a).toString();
	if (a == CSS.Attribute.FONT_FAMILY) {
	    fontAttr.addAttribute(HTML.Attribute.FACE, htmlValue);
	} else if (a == CSS.Attribute.FONT_SIZE) {
	    fontAttr.addAttribute(HTML.Attribute.SIZE, htmlValue);
	} else if (a == CSS.Attribute.COLOR) {
	    fontAttr.addAttribute(HTML.Attribute.COLOR, htmlValue);
	}
    }
	
    /**
     * Copies the given AttributeSet to a new set, converting
     * any CSS attributes found to arguments of an HTML style
     * attribute.
     */
    private static void convertToHTML40(AttributeSet from, MutableAttributeSet to) {
	Enumeration keys = from.getAttributeNames();
	String value = "";
	while (keys.hasMoreElements()) {
	    Object key = keys.nextElement();
	    if (key instanceof CSS.Attribute) {
		value = value + " " + key + "=" + from.getAttribute(key) + ";";
	    } else {
		to.addAttribute(key, from.getAttribute(key));
	    }
	}
	if (value.length() > 0) {
	    to.addAttribute(HTML.Attribute.STYLE, value);
	}
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
     * Enables subclasses to set the number of characters they
     * want written per line.   The default is 100.
     *
     * @param the maximum line length.
     */
    protected void setLineLength(int l) {
	maxLineLength = l;
    }

    //
    // This subclasses the writing methods to only break a string when
    // canBreakString is true.
    // In a future release it is likely AbstractWriter will get this
    // functionality
    //

    /**
     * Conveneice method for write(char, false).
     */
    protected void write(char ch) throws IOException {
	write(ch, false);
    }

    /**
     * Convenience method for write(String, false).
     */
    protected void write(String content) throws IOException {
	write(content, false);
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
    private void write(char ch, boolean content) throws IOException {
	if (ch == NEWLINE) {
	    currLength = 0;
	    out.write(newline);
	    isLineEmpty = true;
	} else {
	    isLineEmpty = false;
	    out.write(ch);
	    ++currLength;
	    if (content && currLength >= maxLineLength) {
		writeNewline();
	    }
	}
    }

    /**
     * Writes out a string.
     *
     * @param String representing the content.
     * @exception IOException on any I/O error
     */
    private void write(String content, boolean isContent) throws IOException {
	int size = content.length();
	if (tempChars == null || tempChars.length < size) {
	    tempChars = new char[size];
	}
	content.getChars(0, size, tempChars, 0);
	write(tempChars, 0, size, isContent, isContent);
    }

    /**
     * Write a portion of an array of characters.
     */
    private void write(char[] chars, int startIndex, int length,
		       boolean isContent,
		       boolean replaceCharacterEntities) throws IOException {
	if (!isContent) {
	    // We can not break str, just track if a newline
	    // is in it.
	    int lastIndex = startIndex;
	    int endIndex = startIndex + length;
	    int newlineIndex = indexOf(chars, '\n', startIndex, endIndex);
	    while (newlineIndex != -1) {
		if (newlineIndex > lastIndex) {
		    if (replaceCharacterEntities) {
			write(chars, lastIndex, newlineIndex - lastIndex);
		    }
		    else {
			out.write(chars, lastIndex, newlineIndex - lastIndex);
		    }
		}
		writeNewline();
		lastIndex = newlineIndex + 1;
		newlineIndex = indexOf(chars, '\n', lastIndex, endIndex);
	    }
	    if (lastIndex < endIndex) {
		currLength += (endIndex - lastIndex);
		if (replaceCharacterEntities) {
		    write(chars, lastIndex, endIndex - lastIndex);
		}
		else {
		    out.write(chars, lastIndex, endIndex - lastIndex);
		}
	    }
	}
	else {
	    // We can break chars if the length exceeds maxLineLength.
	    int lastIndex = startIndex;
	    int endIndex = startIndex + length;

	    if (currLength >= maxLineLength && !isLineEmpty) {
		// This can happen if some tags have been written out.
		writeNewline();
	    }
	    while (lastIndex < endIndex) {
		int newlineIndex = indexOf(chars, '\n', lastIndex, endIndex);
		boolean needsNewline = false;

		if (newlineIndex != -1 && (currLength +
			      (newlineIndex - lastIndex)) < maxLineLength) {
		    if (newlineIndex > lastIndex) {
			write(chars, lastIndex, newlineIndex - lastIndex);
		    }
		    lastIndex = newlineIndex + 1;
		    needsNewline = true;
		}
		else if (newlineIndex == -1 && (currLength +
				(endIndex - lastIndex)) < maxLineLength) {
		    if (endIndex > lastIndex) {
			write(chars, lastIndex, endIndex - lastIndex);
		    }
		    currLength += (endIndex - lastIndex);
		    lastIndex = endIndex;
		}
		else {
		    // Need to break chars, find a place to split chars at,
		    // from lastIndex to endIndex,
		    // or maxLineLength - currLength whichever is smaller
		    int breakPoint = -1;
		    int maxBreak = Math.min(endIndex - lastIndex,
					    maxLineLength - currLength - 1);
		    int counter = 0;
		    while (counter < maxBreak) {
			if (Character.isWhitespace(chars[counter +
							lastIndex])) {
			    breakPoint = counter;
			}
			counter++;
		    }
		    if (breakPoint != -1) {
			// Found a place to break at.
			breakPoint += lastIndex + 1;
			write(chars, lastIndex, breakPoint - lastIndex);
			lastIndex = breakPoint;
		    }
		    else {
			// No where good to break.
			if (isLineEmpty) {
			    // If the current output line is empty, find the
			    // next whitespace, or write out the whole string.
			    // maxBreak will be negative if current line too
			    // long.
			    counter = Math.max(0, maxBreak);
			    maxBreak = endIndex - lastIndex;
			    while (counter < maxBreak) {
				if (Character.isWhitespace(chars[counter +
								lastIndex])) {
				    breakPoint = counter;
				    break;
				}
				counter++;
			    }
			    if (breakPoint == -1) {
				write(chars, lastIndex, endIndex - lastIndex);
				breakPoint = endIndex;
			    }
			    else {
				breakPoint += lastIndex;
				if (chars[breakPoint] == NEWLINE) {
				    write(chars, lastIndex, breakPoint++ -
					  lastIndex);
				}
				else {
				    write(chars, lastIndex, ++breakPoint -
					      lastIndex);
				}
			    }
			    lastIndex = breakPoint;
			}
			// else Iterate through again.
		    }
		    // Force a newline since line length too long.
		    needsNewline = true;
		}
		if (needsNewline || lastIndex < endIndex) {
		    writeNewline();
		    if (lastIndex < endIndex) {
			indent();
		    }
		}
	    }
	}
    }

    /**
     * Writes a portion of an array of characters. This does no
     * book keeping before writing out the characters. It will map
     * any characters outside of ascii to character level entities. You
     * should not normally call this, rather call write that takes
     * a boolean, or write that takes a String.
     */
    private void write(char[] chars, int start, int length) throws
	          IOException {
	int last = start;
	isLineEmpty = false;
	length += start;
	for (int counter = start; counter < length; counter++) {
	    // This will change, we need better support character level
	    // entities.
	    switch(chars[counter]) {
		// Character level entities.
	    case '<':
		if (counter > last) {
		    out.write(chars, last, counter - last);
		}
		last = counter + 1;
		out.write("&lt;");
		break;
	    case '>':
		if (counter > last) {
		    out.write(chars, last, counter - last);
		}
		last = counter + 1;
		out.write("&gt;");
		break;
	    case '&':
		if (counter > last) {
		    out.write(chars, last, counter - last);
		}
		last = counter + 1;
		out.write("&amp;");
		break;
	    case '"':
		if (counter > last) {
		    out.write(chars, last, counter - last);
		}
		last = counter + 1;
		out.write("&quot;");
		break;
		// Special characters
	    case '\n':
	    case '\t':
	    case '\r':
		break;
	    default:
		if (chars[counter] < ' ' || chars[counter] > 127) {
		    if (counter > last) {
			out.write(chars, last, counter - last);
		    }
		    last = counter + 1;
		    // If the character is outside of ascii, write the
		    // numeric value.
		    out.write("&#");
		    out.write(String.valueOf((int)chars[counter]));
		    out.write(';');
		}
		break;
	    }
	}
	if (last < length) {
	    out.write(chars, last, length - last);
	}
    }

    /**
     * Writes a newline.
     */
    private void writeNewline() throws IOException {
	out.write(newline);
	isLineEmpty = true;
	currLength = 0;
    }

    /**
     * Does indentation.  The number of spaces written
     * out is indent level times the space to map mapping.
     *
     * @exception IOException on any I/O error
     */
    protected void indent() throws IOException {
	int max = indentLevel * indentSpace;
	if (indentChars == null || max > indentChars.length) {
	    indentChars = new char[max];
	    for (int counter = 0; counter < max; counter++) {
		indentChars[counter] = ' ';
	    }
	}
	out.write(indentChars, 0, max);
	currLength += max;
    }

    /**
     * Support method to locate an occurence of a particular character.
     */
    private int indexOf(char[] chars, char sChar, int startIndex,
			int endIndex) {
	while(startIndex < endIndex) {
	    if (chars[startIndex] == sChar) {
		return startIndex;
	    }
	    startIndex++;
	}
	return -1;
    }
}
