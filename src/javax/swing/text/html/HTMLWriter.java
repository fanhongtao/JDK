/*
 * @(#)HTMLWriter.java	1.13 98/08/26
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
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
 * @version 1.13, 08/26/98
 */


public class HTMLWriter extends AbstractWriter {

    /*
     * Stores all elements for which end tags have to
     * be emitted.
     */
    private Stack blockElementStack = new Stack();
    private boolean inContent = false;
    private boolean inPre = false;
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
     * Creates a new HTMLWriter.
     *
     * @param a  Writer
     * @param an HTMLDocument
     *
     */
    public HTMLWriter(Writer w, HTMLDocument doc) {
	super(w, doc);
	completeDoc = true;
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
    }


    /**
     * Writes out the attribute set.  Ignores all
     * attributes whose key is of type HTML.Tag.
     *
     * @param     an AttributeSet.
     * @exception IOException on any I/O error
     *
     */
    protected void writeAttributes(AttributeSet attr) throws IOException {
	Enumeration names = attr.getAttributeNames();
	while (names.hasMoreElements()) {
	    Object name = names.nextElement();
	    if (name instanceof HTML.Tag || 
		name == StyleConstants.NameAttribute || 
		name == HTML.Attribute.ENDTAG ||
		name == StyleConstants.ModelAttribute) {
		continue;
	    }
	    write(" " + name + "=" + attr.getAttribute(name));
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
		write(NEWLINE);
		indent();
	    }
	    write('<');
	    Object nameTag = (attr != null) ? attr.getAttribute
		              (StyleConstants.NameAttribute) : null;
	    Object endTag = (attr != null) ? attr.getAttribute
		              (HTML.Attribute.ENDTAG) : null;

	    boolean outputEndTag = false;
	    // If an instance of an UNKNOWN Tag, or an instance of a 
	    // tag that is only visible during editing
	    //
	    if (nameTag != null && endTag != null && (endTag instanceof String) &&
		((String)endTag).equals("true")) {
		outputEndTag = true;
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
		write(NEWLINE);
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

	if (matchNameAttribute(elem.getAttributes(), HTML.Tag.PRE)) {
	    inPre = true;
	}
	// write out end tags for item on stack
	AttributeSet attr = elem.getAttributes();
	closeOutUnwantedEmbeddedTags(attr);

	if (inContent) { 
	    write(NEWLINE);
	    inContent = false;
	    newlineOutputed = false;
	}

	indent();
	write('<');
	write(elem.getName());
	writeAttributes(attr);
	write('>');
	write(NEWLINE);

	if (matchNameAttribute(elem.getAttributes(), HTML.Tag.TEXTAREA)) {
	    textAreaContent(elem.getAttributes());
	} else if (matchNameAttribute(elem.getAttributes(), HTML.Tag.SELECT)) {
	    selectContent(elem.getAttributes());
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
	    String str = doc.getText(0, doc.getLength());
	    if (str != null) {
		inTextArea = true;
		incrIndent();
		indent();
		write(str);
		write(NEWLINE);
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
	String contentStr = getText(elem);
	newlineOutputed = false;
	if (contentStr.length() > 0) { 
	    write(contentStr);
	    if (contentStr.endsWith("\n")) {
		newlineOutputed = true;
	    }
	} 
    }


    /**
     * Writes out a string.  If writing the contents of a
     * textarea, it handles the appropriate indentation for
     * embedded new lines. Otherwise it invokes the superclass
     * to handle the writing.
     *
     * @param String representing the content.
     * @exception IOException on any I/O error
     */
    protected void write(String content) throws IOException {

	if (inTextArea) {
	    StringTokenizer parser = new StringTokenizer(content, "\n");
	    try {
		while (parser.hasMoreTokens()) {
		    super.write(parser.nextToken());
		    if (parser.countTokens() != 0) {
			write(NEWLINE);
			indent();
		    }
		}
	    } catch (NoSuchElementException e) {
	    }
	} else {
	    super.write(content);
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
	write(NEWLINE);
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
		write(NEWLINE);
	    }
	    newlineOutputed = false;
	    inContent = false;
	}
	indent();
        write('<');
        write('/');
        write(elem.getName());
        write('>');
	write(NEWLINE);
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
	write('<');
	write('!');
	write('-');
	write('-');
	if (string != null) {
	    write(string);
	}
	write('-');
	write('-');
	write('>');
	write(NEWLINE);
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
	    }
	}
    }


    /**
     * This method searches the attribute set for a tag, both of which
     * are passed in as a parameter.  Returns true if no match is found
     * and false otherwise.
     */
    private boolean noMatchForTagInAttributes(AttributeSet attr, HTML.Tag t) {
	if (attr != null) {
	    Enumeration names = attr.getAttributeNames();
	    while (names.hasMoreElements()) {
		Object name = names.nextElement();
		if (name instanceof HTML.Tag) {
		    if (name == t) {
			return false;
		    }
		}
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

	HTML.Tag t;

	for (int i = tags.size() - 1; i >= 0; i--) {
	    t = (HTML.Tag)tags.elementAt(i);
	    if ((attr == null) || noMatchForTagInAttributes(attr, t)) {
		tags.removeElementAt(i);
		write('<');
		write('/');
		write(t.toString());
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
}
