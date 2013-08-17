/*
 * @(#)InlineView.java	1.9 98/09/10
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

import java.awt.FontMetrics;
import java.text.BreakIterator;
import javax.swing.text.*;

/**
 * Displays the <dfn>inline element</dfn> styles
 * based upon css attributes.
 *
 * @author  Timothy Prinzing
 * @version 1.9 09/10/98
 */
public class InlineView extends LabelView {

    /**
     * Constructs a new view wrapped on an element.
     *
     * @param elem the element
     */
    public InlineView(Element elem) {
	super(elem);
	StyleSheet sheet = getStyleSheet();
	attr = sheet.getViewAttributes(this);
    }

    /**
     * Fetches the attributes to use when rendering.  This is
     * implemented to multiplex the attributes specified in the
     * model with a StyleSheet.
     */
    public AttributeSet getAttributes() {
	return attr;
    }

    /**
     * Fetch the span of the longest word in the view.
     */
    float getLongestWordSpan() {
	// find the longest word
	float span = 0;
	try {
	    Document doc = getDocument();
	    int p0 = getStartOffset();
	    int p1 = getEndOffset();
	    String s = doc.getText(p0, p1 - p0);
	    int word0 = p0;
	    int word1 = p0;
	    if(s != null && s.length() > 0) {
		BreakIterator words = BreakIterator.getWordInstance();
		words.setText(s);
		int start = words.first();
		for (int end = words.next(); end != BreakIterator.DONE;
		     start = end, end = words.next()) {
		    
		    // update longest word boundary
		    if ((end - start) > (word1 - word0)) {
			word0 = start;
			word1 = end;
		    }
		}
	    }

	    // calculate the minimum
	    if ((word1 - word0) > 0) {
		FontMetrics metrics = getFontMetrics();
		String word = s.substring(word0, word1);
		span = metrics.stringWidth(word);
	    }
	} catch (BadLocationException ble) {
	    // If the text can't be retrieved, it can't influence the size.
	}
	return span;
    }
    
    /**
     * Set the cached properties from the attributes.
     */
    protected void setPropertiesFromAttributes() {
	super.setPropertiesFromAttributes();
	AttributeSet a = getAttributes();
	String decor = (String) a.getAttribute(CSS.Attribute.TEXT_DECORATION);
	boolean u = (decor != null) ? (decor.indexOf("underline") >= 0) : false;
	setUnderline(u);
	boolean s = (decor != null) ? (decor.indexOf("line-through") >= 0) : false;
	setStrikeThrough(s);
	String vAlign = (String) a.getAttribute(CSS.Attribute.VERTICAL_ALIGN);
	s = (vAlign != null) ? (vAlign.indexOf("sup") >= 0) : false;
	setSuperscript(s);
	s = (vAlign != null) ? (vAlign.indexOf("sub") >= 0) : false;
	setSubscript(s);
    }


    protected StyleSheet getStyleSheet() {
	HTMLDocument doc = (HTMLDocument) getDocument();
	return doc.getStyleSheet();
    }

    /**
     * Returns false if the contents of the view are merely a "\n".
     * If the contents are merely a newline, it is fair to say that
     * the element corresponding to this view was deliberately inserted
     * for purposes of editing.
     */
    public boolean isVisible() {
	int p0 = getStartOffset();
	int p1 = getEndOffset();
	if ((p1 - p0) == 1) {
	    Document doc = getDocument();
	    try {
		doc.getText(p0, p1 - p0, buff);
		if (Character.isWhitespace(buff.array[buff.offset])) {
		    return false;
		}
	    } catch (BadLocationException e) {
		return false;
	    }
	}
	return true;
    }

    AttributeSet attr;

    // buffer to fetch text, since only one thread at a time
    // will access this view type.
    static Segment buff = new Segment();
}
