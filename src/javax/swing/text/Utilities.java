/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.text;

import java.lang.reflect.Method;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.text.*;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.font.TextAttribute;
import java.text.AttributedString;

/**
 * A collection of methods to deal with various text
 * related activities.
 * 
 * @author  Timothy Prinzing
 * @version 1.32 02/06/02
 */
public class Utilities {

    /**
     * Draws the given text, expanding any tabs that are contained
     * using the given tab expansion technique.  This particular
     * implementation renders in a 1.1 style coordinate system
     * where ints are used and 72dpi is assumed.
     * 
     * @param s  the source of the text
     * @param x  the X origin >= 0
     * @param y  the Y origin >= 0
     * @param g  the graphics context
     * @param e  how to expand the tabs.  If this value is null, 
     *   tabs will be expanded as a space character.
     * @param startOffset starting offset of the text in the document >= 0
     * @returns  the X location at the end of the rendered text
     */
    public static final int drawTabbedText(Segment s, int x, int y, Graphics g, 
					   TabExpander e, int startOffset) {
	FontMetrics metrics = g.getFontMetrics();
	int nextX = x;
	char[] txt = s.array;
	int txtOffset = s.offset;
	int flushLen = 0;
	int flushIndex = s.offset;
	int n = s.offset + s.count;
	for (int i = txtOffset; i < n; i++) {
	    if (txt[i] == '\t') {
		if (flushLen > 0) {
		    g.drawChars(txt, flushIndex, flushLen, x, y);
		    flushLen = 0;
		}
		flushIndex = i + 1;
		if (e != null) {
		    nextX = (int) e.nextTabStop((float) nextX, startOffset + i - txtOffset);
		} else {
		    nextX += metrics.charWidth(' ');
		}
		x = nextX;
	    } else if ((txt[i] == '\n') || (txt[i] == '\r')) {
		if (flushLen > 0) {
		    g.drawChars(txt, flushIndex, flushLen, x, y);
		    flushLen = 0;
		}
		flushIndex = i + 1;
		x = nextX;
	    } else {
		flushLen += 1;
		nextX += metrics.charWidth(txt[i]);
	    }
	} 
	if (flushLen > 0) {
	    g.drawChars(txt, flushIndex, flushLen, x, y);
	}
	return nextX;
    }

    /**
     * Determines the width of the given segment of text taking tabs 
     * into consideration.  This is implemented in a 1.1 style coordinate 
     * system where ints are used and 72dpi is assumed.
     *
     * @param s  the source of the text
     * @param metrics the font metrics to use for the calculation
     * @param x  the X origin >= 0
     * @param e  how to expand the tabs.  If this value is null, 
     *   tabs will be expanded as a space character.
     * @param startOffset starting offset of the text in the document >= 0
     * @returns  the width of the text
     */
    public static final int getTabbedTextWidth(Segment s, FontMetrics metrics, int x, 
					       TabExpander e, int startOffset) {
	int nextX = x;
	char[] txt = s.array;
	int txtOffset = s.offset;
	int n = s.offset + s.count;
	for (int i = txtOffset; i < n; i++) {
	    if (txt[i] == '\t') {
		if (e != null) {
		    nextX = (int) e.nextTabStop((float) nextX,
						startOffset + i - txtOffset);
		} else {
		    nextX += metrics.charWidth(' ');
		}
	    } else if(txt[i] != '\n') {
		nextX += metrics.charWidth(txt[i]);
	    }
	    // Ignore newlines, they take up space and we shouldn't be
	    // counting them.
	}
	return nextX - x;
    }

    /**
     * Determines the relative offset into the given text that
     * best represents the given span in the view coordinate
     * system.  This is implemented in a 1.1 style coordinate 
     * system where ints are used and 72dpi is assumed.
     *
     * @param s  the source of the text
     * @param metrics the font metrics to use for the calculation
     * @param x0 the starting view location representing the start
     *   of the given text >= 0.
     * @param x  the target view location to translate to an
     *   offset into the text >= 0.
     * @param e  how to expand the tabs.  If this value is null, 
     *   tabs will be expanded as a space character.
     * @param startOffset starting offset of the text in the document >= 0
     * @returns  the offset into the text >= 0
     */
    public static final int getTabbedTextOffset(Segment s, FontMetrics metrics, 
					     int x0, int x, TabExpander e,
					     int startOffset) {
	return getTabbedTextOffset(s, metrics, x0, x, e, startOffset, true);
    }

    public static final int getTabbedTextOffset(Segment s, 
						FontMetrics metrics,
						int x0, int x, TabExpander e,
						int startOffset, 
						boolean round) {
	int currX = x0;
	int nextX = currX;
	// s may be a shared segment, so it is copied prior to calling
	// the tab expander
	char[] txt = s.array;
	int txtOffset = s.offset;
	int txtCount = s.count;
	int n = s.offset + s.count;
	for (int i = s.offset; i < n; i++) {
	    if (txt[i] == '\t') {
		if (e != null) {
		    nextX = (int) e.nextTabStop((float) nextX,
						startOffset + i - txtOffset);
		} else {
		    nextX += metrics.charWidth(' ');
		}
	    } else {
		nextX += metrics.charWidth(txt[i]);
	    }
	    if ((x >= currX) && (x < nextX)) {
		// found the hit position... return the appropriate side
		if ((round == false) || ((x - currX) < (nextX - x))) {
		    return i - txtOffset;
		} else {
		    return i + 1 - txtOffset;
		}
	    }
	    currX = nextX;
	}

	// didn't find, return end offset
	return txtCount;
    }

    /**
     * Determine where to break the given text to fit
     * within the the given span.  This tries to find a
     * whitespace boundary.
     * @param s  the source of the text
     * @param metrics the font metrics to use for the calculation
     * @param x0 the starting view location representing the start
     *   of the given text.
     * @param x  the target view location to translate to an
     *   offset into the text.
     * @param e  how to expand the tabs.  If this value is null, 
     *   tabs will be expanded as a space character.
     * @param startOffset starting offset in the document of the text
     * @returns  the offset into the given text.
     */
    public static final int getBreakLocation(Segment s, FontMetrics metrics,
					     int x0, int x, TabExpander e,
					     int startOffset) {
	char[] txt = s.array;
	int txtOffset = s.offset;
	int txtCount = s.count;
	int index = Utilities.getTabbedTextOffset(s, metrics, x0, x, 
						  e, startOffset, false);
	for (int i = txtOffset + Math.min(index, txtCount - 1); 
	     i >= txtOffset; i--) {
	    
	    char ch = txt[i];
	    if (Character.isWhitespace(ch)) {
		// found whitespace, break here
		index = i - txtOffset + 1;
		break;
	    }
	}
	return index;
    }

    /**
     * Determines the starting row model position of the row that contains
     * the specified model position.  The component given must have a
     * size to compute the result.  If the component doesn't have a size
     * a value of -1 will be returned.
     *
     * @param c the editor
     * @param offs the offset in the document >= 0
     * @return the position >= 0 if the request can be computed, otherwise
     *  a value of -1 will be returned.
     * @exception BadLocationException if the offset is out of range
     */
    public static final int getRowStart(JTextComponent c, int offs) throws BadLocationException {
	Rectangle r = c.modelToView(offs);
	if (r == null) {
	    return -1;
	}
	int lastOffs = offs;
	int y = r.y;
	while ((r != null) && (y == r.y)) {
	    offs = lastOffs;
	    lastOffs -= 1;
	    r = (lastOffs >= 0) ? c.modelToView(lastOffs) : null;
	}
	return offs;
    }

    /**
     * Determines the ending row model position of the row that contains
     * the specified model position.  The component given must have a
     * size to compute the result.  If the component doesn't have a size
     * a value of -1 will be returned.
     *
     * @param c the editor
     * @param offs the offset in the document >= 0
     * @return the position >= 0 if the request can be computed, otherwise
     *  a value of -1 will be returned.
     * @exception BadLocationException if the offset is out of range
     */
    public static final int getRowEnd(JTextComponent c, int offs) throws BadLocationException {
	Rectangle r = c.modelToView(offs);
	if (r == null) {
	    return -1;
	}
	int n = c.getDocument().getLength();
	int lastOffs = offs;
	int y = r.y;
	while ((r != null) && (y == r.y)) {
	    offs = lastOffs;
	    lastOffs += 1;
	    r = (lastOffs <= n) ? c.modelToView(lastOffs) : null;
	}
	return offs;
    }

    /**
     * Determines the position in the model that is closest to the given 
     * view location in the row above.  The component given must have a
     * size to compute the result.  If the component doesn't have a size
     * a value of -1 will be returned.
     *
     * @param c the editor
     * @param offs the offset in the document >= 0
     * @param x the X coordinate >= 0
     * @return the position >= 0 if the request can be computed, otherwise
     *  a value of -1 will be returned.
     * @exception BadLocationException if the offset is out of range
     */
    public static final int getPositionAbove(JTextComponent c, int offs, int x) throws BadLocationException {
	int lastOffs = getRowStart(c, offs) - 1;
	if (lastOffs < 0) {
	    return -1;
	}
	int bestSpan = Short.MAX_VALUE;
	int y = 0;
	Rectangle r = null;
	if (lastOffs >= 0) {
	    r = c.modelToView(lastOffs);
	    y = r.y;
	}
	while ((r != null) && (y == r.y)) {
	    int span = Math.abs(r.x - x);
	    if (span < bestSpan) {
		offs = lastOffs;
		bestSpan = span;
	    }
	    lastOffs -= 1;
	    r = (lastOffs >= 0) ? c.modelToView(lastOffs) : null;
	}
	return offs;
    }

    /**
     * Determines the position in the model that is closest to the given 
     * view location in the row below.  The component given must have a
     * size to compute the result.  If the component doesn't have a size
     * a value of -1 will be returned.
     *
     * @param c the editor
     * @param offs the offset in the document >= 0
     * @param x the X coordinate >= 0
     * @return the position >= 0 if the request can be computed, otherwise
     *  a value of -1 will be returned.
     * @exception BadLocationException if the offset is out of range
     */
    public static final int getPositionBelow(JTextComponent c, int offs, int x) throws BadLocationException {
	int lastOffs = getRowEnd(c, offs) + 1;
	if (lastOffs <= 0) {
	    return -1;
	}
	int bestSpan = Short.MAX_VALUE;
	int n = c.getDocument().getLength();
	int y = 0;
	Rectangle r = null;
	if (lastOffs <= n) {
	    r = c.modelToView(lastOffs);
	    y = r.y;
	}
	while ((r != null) && (y == r.y)) {
	    int span = Math.abs(x - r.x);
	    if (span < bestSpan) {
		offs = lastOffs;
		bestSpan = span;
	    }
	    lastOffs += 1;
	    r = (lastOffs <= n) ? c.modelToView(lastOffs) : null;
	}
	return offs;
    }

    /**
     * Determines the start of a word for the given model location.
     * Uses BreakIterator.getWordInstance() to actually get the words.
     * 
     * @param c the editor
     * @param offs the offset in the document >= 0
     * @returns the location in the model of the word start >= 0.
     * @exception BadLocationException if the offset is out of range
     */
    public static final int getWordStart(JTextComponent c, int offs) throws BadLocationException {
	Document doc = c.getDocument();
	Element line = getParagraphElement(c, offs);
	if (line == null) {
	    throw new BadLocationException("No word at " + offs, offs);
	}
	int lineStart = line.getStartOffset();
	int lineEnd = Math.min(line.getEndOffset(), doc.getLength());
	
	String s = doc.getText(lineStart, lineEnd - lineStart);
	if(s != null && s.length() > 0) {
	    BreakIterator words = BreakIterator.getWordInstance();
	    words.setText(s);
	    int wordPosition = offs - lineStart;
	    if(wordPosition >= words.last()) {
		wordPosition = words.last() - 1;
	    } 
	    words.following(wordPosition);
	    offs = lineStart + words.previous();
	}
	return offs;
    }

    /**
     * Determines the end of a word for the given location.
     * Uses BreakIterator.getWordInstance() to actually get the words.
     * 
     * @param c the editor
     * @param offs the offset in the document >= 0
     * @returns the location in the model of the word end >= 0.
     * @exception BadLocationException if the offset is out of range
     */
    public static final int getWordEnd(JTextComponent c, int offs) throws BadLocationException {
	Document doc = c.getDocument();
	Element line = getParagraphElement(c, offs);
	if (line == null) {
	    throw new BadLocationException("No word at " + offs, offs);
	}
	int lineStart = line.getStartOffset();
	int lineEnd = Math.min(line.getEndOffset(), doc.getLength());
	
	String s = doc.getText(lineStart, lineEnd - lineStart);
	if(s != null && s.length() > 0) {
	    BreakIterator words = BreakIterator.getWordInstance();
	    words.setText(s);
	    int wordPosition = offs - lineStart;
	    if(wordPosition >= words.last()) {
		wordPosition = words.last() - 1;
	    } 
	    offs = lineStart + words.following(wordPosition);
	}
	return offs;
    }

    /**
     * Determines the start of the next word for the given location.
     * Uses BreakIterator.getWordInstance() to actually get the words.
     * 
     * @param c the editor
     * @param offs the offset in the document >= 0
     * @returns the location in the model of the word start >= 0.
     * @exception BadLocationException if the offset is out of range
     */
    public static final int getNextWord(JTextComponent c, int offs) throws BadLocationException {
	int nextWord;
	Element line = getParagraphElement(c, offs);
	for (nextWord = getNextWordInParagraph(line, offs, false);
	     nextWord == BreakIterator.DONE; 
	     nextWord = getNextWordInParagraph(line, offs, true)) {

	    // didn't find in this line, try the next line
	    offs = line.getEndOffset();
	    line = getParagraphElement(c, offs);
	}
	return nextWord;
    }

    /**
     * Finds the next word in the given elements text.  The first
     * parameter allows searching multiple paragraphs where even
     * the first offset is desired.
     * Returns the offset of the next word, or BreakIterator.DONE
     * if there are no more words in the element.
     */
    static int getNextWordInParagraph(Element line, int offs, boolean first) throws BadLocationException {
	if (line == null) {
	    throw new BadLocationException("No more words", offs);
	}
	Document doc = line.getDocument();
	int lineStart = line.getStartOffset();
	int lineEnd = Math.min(line.getEndOffset(), doc.getLength());
	if ((offs >= lineEnd) || (offs < lineStart)) {
	    throw new BadLocationException("No more words", offs);
	}
	String s = doc.getText(lineStart, lineEnd - lineStart);
	BreakIterator words = BreakIterator.getWordInstance();
	words.setText(s);
	if ((first && (words.first() == (offs - lineStart))) &&	
	    (! Character.isWhitespace(s.charAt(words.first())))) {

	    return offs;
	}
	int wordPosition = words.following(offs - lineStart);
	if ((wordPosition == BreakIterator.DONE) || 
	    (wordPosition >= s.length())) {
		// there are no more words on this line.
		return BreakIterator.DONE;
	}
	// if we haven't shot past the end... check to 
	// see if the current boundary represents whitespace.
	// if so, we need to try again
	char ch = s.charAt(wordPosition);
	if (! Character.isWhitespace(ch)) {
	    return lineStart + wordPosition;
	}

	// it was whitespace, try again.  The assumption
	// is that it must be a word start if the last
	// one had whitespace following it.
	wordPosition = words.next();
	if (wordPosition != BreakIterator.DONE) {
	    offs = lineStart + wordPosition;
	    if (offs != lineEnd) {
		return offs;
	    }
	}
	return BreakIterator.DONE;
    }


    /**
     * Determine the start of the prev word for the given location.
     * Uses BreakIterator.getWordInstance() to actually get the words.
     * 
     * @param c the editor
     * @param offs the offset in the document >= 0
     * @returns the location in the model of the word start >= 0.
     * @exception BadLocationException if the offset is out of range
     */
    public static final int getPreviousWord(JTextComponent c, int offs) throws BadLocationException {
	int prevWord;
	Element line = getParagraphElement(c, offs);
	for (prevWord = getPrevWordInParagraph(line, offs);
	     prevWord == BreakIterator.DONE; 
	     prevWord = getPrevWordInParagraph(line, offs)) {

	    // didn't find in this line, try the prev line
	    offs = line.getStartOffset() - 1;
	    line = getParagraphElement(c, offs);
	}
	return prevWord;
    }

    /**
     * Finds the previous word in the given elements text.  The first
     * parameter allows searching multiple paragraphs where even
     * the first offset is desired.
     * Returns the offset of the next word, or BreakIterator.DONE
     * if there are no more words in the element.
     */
    static int getPrevWordInParagraph(Element line, int offs) throws BadLocationException {
	if (line == null) {
	    throw new BadLocationException("No more words", offs);
	}
	Document doc = line.getDocument();
	int lineStart = line.getStartOffset();
	int lineEnd = line.getEndOffset();
	if ((offs > lineEnd) || (offs < lineStart)) {
	    throw new BadLocationException("No more words", offs);
	}
	String s = doc.getText(lineStart, lineEnd - lineStart);
	BreakIterator words = BreakIterator.getWordInstance();
	words.setText(s);
	if (words.following(offs - lineStart) == BreakIterator.DONE) {
	    words.last();
	}
	int wordPosition = words.previous();
	if (wordPosition == (offs - lineStart)) {
	    wordPosition = words.previous();
	}

	if (wordPosition == BreakIterator.DONE) {
	    // there are no more words on this line.
	    return BreakIterator.DONE;
	}
	// if we haven't shot past the end... check to 
	// see if the current boundary represents whitespace.
	// if so, we need to try again
	char ch = s.charAt(wordPosition);
	if (! Character.isWhitespace(ch)) {
	    return lineStart + wordPosition;
	}

	// it was whitespace, try again.  The assumption
	// is that it must be a word start if the last
	// one had whitespace following it.
	wordPosition = words.previous();
	if (wordPosition != BreakIterator.DONE) {
	    return lineStart + wordPosition;
	}
	return BreakIterator.DONE;
    }

    /**
     * Determines the element to use for a paragraph/line.
     *
     * @param c the editor
     * @param offs the starting offset in the document >= 0
     * @return the element
     */
    public static final Element getParagraphElement(JTextComponent c, int offs) {
	Document doc = c.getDocument();
	if (doc instanceof StyledDocument) {
	    return ((StyledDocument)doc).getParagraphElement(offs);
	}
	Element map = doc.getDefaultRootElement();
	int index = map.getElementIndex(offs);
	Element paragraph = map.getElement(index);
	if ((offs >= paragraph.getStartOffset()) && (offs < paragraph.getEndOffset())) {
	    return paragraph;
	}
	return null;
    }

    static boolean isComposedTextElement(Element elem) {
        AttributeSet as = elem.getAttributes();
	return isComposedTextAttributeDefined(as);
    }

    static boolean isComposedTextAttributeDefined(AttributeSet as) {
	return ((as != null) && 
	        (as.isDefined(StyleConstants.ComposedTextAttribute)));
    }

    /**
     * Draws the given composed text passed from an input method.
     *
     * @param attr the attributes containing the composed text
     * @param g  the graphics context
     * @param x  the X origin
     * @param y  the Y origin
     * @param p0 starting offset in the composed text to be rendered
     * @param p1 ending offset in the composed text to be rendered
     * @returns  the new insertion position
     */
    static int drawComposedText(AttributeSet attr, Graphics g, int x, int y,
    				int p0, int p1) throws BadLocationException {
        Graphics2D g2d = (Graphics2D)g;
        AttributedString as = (AttributedString)attr.getAttribute(
	    StyleConstants.ComposedTextAttribute);
	as.addAttribute(TextAttribute.FONT, g.getFont());

	if (p0 >= p1)
	    return x;

	AttributedCharacterIterator aci = as.getIterator(null, p0, p1);
    
	// Create text layout
	TextLayout layout = new TextLayout(aci, g2d.getFontRenderContext());

	// draw
	layout.draw(g2d, x, y);

	return x + (int)layout.getAdvance();
    }

    /**
     * Paints the composed text in a GlyphView
     */
    static void paintComposedText(Graphics g, Rectangle alloc, GlyphView v) {
	if (g instanceof Graphics2D) {
	    Graphics2D g2d = (Graphics2D) g;
	    int p0 = v.getStartOffset();
	    int p1 = v.getEndOffset();
	    AttributeSet attrSet = v.getElement().getAttributes();
	    AttributedString as = 
		(AttributedString)attrSet.getAttribute(StyleConstants.ComposedTextAttribute);
	    int start = v.getElement().getStartOffset();
	    int y = alloc.y + (int) v.getGlyphPainter().getAscent(v);
	    int x = alloc.x;
	    
	    //Add text attributes
	    as.addAttribute(TextAttribute.FONT, v.getFont());
	    as.addAttribute(TextAttribute.FOREGROUND, v.getForeground());
	    if (StyleConstants.isBold(v.getAttributes())) {
		as.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
	    }
	    if (StyleConstants.isItalic(v.getAttributes())) {
		as.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
	    }
	    if (v.isUnderline()) {
		as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
	    }
	    if (v.isStrikeThrough()) {
		as.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
	    }
	    if (v.isSuperscript()) {
		as.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);
	    }
	    if (v.isSubscript()) {
		as.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB);
	    }
	
	    // draw
	    AttributedCharacterIterator aci = as.getIterator(null, p0 - start, p1 - start);
	    TextLayout layout = new TextLayout(aci, g2d.getFontRenderContext());
	    layout.draw(g2d, x, y);
	}
    }

    /*
     * Convenience function for determining ComponentOrientation.  Helps us
     * avoid having Munge directives throughout the code.
     */
    static boolean isLeftToRight( java.awt.Component c ) {
        return c.getComponentOrientation().isLeftToRight();
    }

}
