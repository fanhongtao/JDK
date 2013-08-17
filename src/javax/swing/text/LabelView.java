/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.text;

import java.awt.*;
import javax.swing.event.*;

/**
 * A LabelView is a styled chunk of text that represents a view
 * mapped over an element in the text model.  It caches the
 * character level attributes used for rendering.
 *
 * @author Timothy Prinzing
 * @version 1.57 02/06/02
 */
public class LabelView extends GlyphView implements TabableView {

    /**
     * Constructs a new view wrapped on an element.
     *
     * @param elem the element
     */
    public LabelView(Element elem) {
	super(elem);
    }

    /**
     * Synchronize the view's cached values with the model.
     * This causes the font, metrics, color, etc to be 
     * recached if the cache has been invalidated.
     */
    final void sync() {
	if (font == null) {
	    setPropertiesFromAttributes();
	}
    }
    
    /**
     * Set whether or not the view is underlined.
     */
    protected void setUnderline(boolean u) {
	underline = u;
    }

    /**
     * Set whether or not the view has a strike/line
     * through it.
     */
    protected void setStrikeThrough(boolean s) {
	strike = s;
    }


    /**
     * Set whether or not the view represents a 
     * superscript.
     */
    protected void setSuperscript(boolean s) {
	superscript = s;
    }

    /**
     * Set whether or not the view represents a 
     * subscript.
     */
    protected void setSubscript(boolean s) {
	subscript = s;
    }

    /**
     * Set the cached properties from the attributes.
     */
    protected void setPropertiesFromAttributes() {
	AttributeSet attr = getAttributes();
	if (attr != null) {
            Document d = getDocument();
	    if (d instanceof StyledDocument) {
		StyledDocument doc = (StyledDocument) d;
		font = doc.getFont(attr);
		fg = doc.getForeground(attr);
		if (attr.isDefined(StyleConstants.Background)) {
		    bg = doc.getBackground(attr);
		} else {
		    bg = null;
		}
		setUnderline(StyleConstants.isUnderline(attr));
		setStrikeThrough(StyleConstants.isStrikeThrough(attr));
		setSuperscript(StyleConstants.isSuperscript(attr));
		setSubscript(StyleConstants.isSubscript(attr));
	    } else {
		throw new StateInvariantError("LabelView needs StyledDocument");
	    }
	}
     }

    /**
     * Fetch the FontMetrics used for this view.
     * @deprecated FontMetrics are not used for glyph rendering
     *  when running in the Java2 SDK.
     */
    protected FontMetrics getFontMetrics() {
	sync();
	return Toolkit.getDefaultToolkit().getFontMetrics(font);
    }

    /**
     * Fetch the background color to use to render the
     * glyphs.  If there is no background color, null should
     * be returned.  This is implemented to return a cached
     * background color.
     */
    public Color getBackground() {
	sync();
	return bg;
    }

    /**
     * Fetch the foreground color to use to render the
     * glyphs.  If there is no foreground color, null should
     * be returned.  This is implemented to return a cached
     * foreground color.
     */
    public Color getForeground() {
	sync();
	return fg;
    }

    /**
     * Fetch the font that the glyphs should be based
     * upon.  This is implemented to return a cached
     * font.
     */
     public Font getFont() {
	sync();
	return font;
    }

    /**
     * Determine if the glyphs should be underlined.  If true,
     * an underline should be drawn through the baseline.
     */
    public boolean isUnderline() {
	sync();
	return underline;
    }

    /**
     * Determine if the glyphs should have a strikethrough
     * line.  If true, a line should be drawn through the center
     * of the glyphs.
     */
    public boolean isStrikeThrough() {
	sync();
	return strike;
    }

    /**
     * Determine if the glyphs should be rendered as superscript.
     */
    public boolean isSubscript() {
	sync();
	return subscript;
    }

    /**
     * Determine if the glyphs should be rendered as subscript.
     */
    public boolean isSuperscript() {
	sync();
	return superscript;
    }

    // --- View methods ---------------------------------------------

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
	font = null;
    }

    // --- variables ------------------------------------------------

    private Font font;
    private Color fg;
    private Color bg;
    private boolean underline;
    private boolean strike;
    private boolean superscript;
    private boolean subscript;

}

