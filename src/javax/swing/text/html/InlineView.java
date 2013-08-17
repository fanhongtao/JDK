/*
 * @(#)InlineView.java	1.12 98/09/25
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

import java.awt.Shape;
import java.awt.FontMetrics;
import java.text.BreakIterator;
import javax.swing.event.DocumentEvent;
import javax.swing.text.*;

/**
 * Displays the <dfn>inline element</dfn> styles
 * based upon css attributes.
 *
 * @author  Timothy Prinzing
 * @version 1.12 09/25/98
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
     * Gives notification from the document that attributes were changed
     * in a location that this view is responsible for.
     *
     * @param e the change information from the associated document
     * @param a the current allocation of the view
     * @param f the factory to use to rebuild if the view has children
     * @see View#changedUpdate
     */
    public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f) {
	super.changedUpdate(e, a, f);
	StyleSheet sheet = getStyleSheet();
	attr = sheet.getViewAttributes(this);
	preferenceChanged(null, true, true);
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
	Object decor = a.getAttribute(CSS.Attribute.TEXT_DECORATION);
	boolean u = (decor != null) ? 
	  (decor.toString().indexOf("underline") >= 0) : false;
	setUnderline(u);
	boolean s = (decor != null) ? 
	  (decor.toString().indexOf("line-through") >= 0) : false;
	setStrikeThrough(s);
        Object vAlign = a.getAttribute(CSS.Attribute.VERTICAL_ALIGN);
	s = (vAlign != null) ? (vAlign.toString().indexOf("sup") >= 0) : false;
	setSuperscript(s);
	s = (vAlign != null) ? (vAlign.toString().indexOf("sub") >= 0) : false;
	setSubscript(s);
    }


    protected StyleSheet getStyleSheet() {
	HTMLDocument doc = (HTMLDocument) getDocument();
	return doc.getStyleSheet();
    }

    AttributeSet attr;

}
