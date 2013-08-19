/*
 * @(#)LabelView.java	1.62 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.text;

import java.awt.*;
import javax.swing.event.*;

/**
 * A <code>LabelView</code> is a styled chunk of text
 * that represents a view mapped over an element in the
 * text model.  It caches the character level attributes
 * used for rendering.
 *
 * @author Timothy Prinzing
 * @version 1.62 01/23/03
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
     * re-cached if the cache has been invalidated.
     */
    final void sync() {
	if (font == null) {
	    setPropertiesFromAttributes();
	}
    }
    
    /**
     * Sets whether or not the view is underlined.
     * Note that this setter is protected and is really
     * only meant if you need to update some additional
     * state when set.
     *
     * @param u true if the view is underlined, otherwise
     *          false
     * @see #isUnderline
     */
    protected void setUnderline(boolean u) {
	underline = u;
    }

    /**
     * Sets whether or not the view has a strike/line
     * through it.
     * Note that this setter is protected and is really
     * only meant if you need to update some additional
     * state when set.
     *
     * @param s true if the view has a strike/line
     *          through it, otherwise false
     * @see #isStrikeThrough
     */
    protected void setStrikeThrough(boolean s) {
	strike = s;
    }


    /**
     * Sets whether or not the view represents a 
     * superscript.
     * Note that this setter is protected and is really
     * only meant if you need to update some additional
     * state when set.
     *
     * @param s true if the view represents a
     *          superscript, otherwise false
     * @see #isSuperscript
     */
    protected void setSuperscript(boolean s) {
	superscript = s;
    }

    /**
     * Sets whether or not the view represents a 
     * subscript.
     * Note that this setter is protected and is really
     * only meant if you need to update some additional
     * state when set.
     *
     * @param s true if the view represents a
     *          subscript, otherwise false
     * @see #isSubscript
     */
    protected void setSubscript(boolean s) {
	subscript = s;
    }

    /**
     * Sets the cached properties from the attributes.
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
     * Fetches the <code>FontMetrics</code> used for this view.
     * @deprecated FontMetrics are not used for glyph rendering
     *  when running in the Java2 SDK.
     */
    protected FontMetrics getFontMetrics() {
	sync();
	return Toolkit.getDefaultToolkit().getFontMetrics(font);
    }

    /**
     * Fetches the background color to use to render the glyphs.
     * This is implemented to return a cached background color,
     * which defaults to <code>null</code>.
     *
     * @return the cached background color
     */
    public Color getBackground() {
	sync();
	return bg;
    }

    /**
     * Fetches the foreground color to use to render the glyphs.
     * This is implemented to return a cached foreground color,
     * which defaults to <code>null</code>.
     *
     * @return the cached foreground color
     */
    public Color getForeground() {
	sync();
	return fg;
    }

    /**
     * Fetches the font that the glyphs should be based upon.
     * This is implemented to return a cached font.
     *
     * @return the cached font
     */
     public Font getFont() {
	sync();
	return font;
    }

    /**
     * Determines if the glyphs should be underlined.  If true,
     * an underline should be drawn through the baseline.  This
     * is implemented to return the cached underline property.
     *
     * <p>When you request this property, <code>LabelView</code>
     * re-syncs its state with the properties of the
     * <code>Element</code>'s <code>AttributeSet</code>.
     * If <code>Element</code>'s <code>AttributeSet</code>
     * does not have this property set, it will revert to false.
     *
     * @return the value of the cached
     *     <code>underline</code> property
     */
    public boolean isUnderline() {
	sync();
	return underline;
    }

    /**
     * Determines if the glyphs should have a strikethrough
     * line.  If true, a line should be drawn through the center
     * of the glyphs.  This is implemented to return the
     * cached <code>strikeThrough</code> property.
     *
     * <p>When you request this property, <code>LabelView</code>
     * re-syncs its state with the properties of the
     * <code>Element</code>'s <code>AttributeSet</code>.
     * If <code>Element</code>'s <code>AttributeSet</code>
     * does not have this property set, it will revert to false.
     *
     * @return the value of the cached
     *     <code>strikeThrough</code> property
     */
    public boolean isStrikeThrough() {
	sync();
	return strike;
    }

    /**
     * Determines if the glyphs should be rendered as superscript.
     * @return the value of the cached subscript property
     *
     * <p>When you request this property, <code>LabelView</code>
     * re-syncs its state with the properties of the
     * <code>Element</code>'s <code>AttributeSet</code>.
     * If <code>Element</code>'s <code>AttributeSet</code>
     * does not have this property set, it will revert to false.
     *
     * @return the value of the cached
     *     <code>subscript</code> property
     */
    public boolean isSubscript() {
	sync();
	return subscript;
    }

    /**
     * Determines if the glyphs should be rendered as subscript.
     *
     * <p>When you request this property, <code>LabelView</code>
     * re-syncs its state with the properties of the
     * <code>Element</code>'s <code>AttributeSet</code>.
     * If <code>Element</code>'s <code>AttributeSet</code>
     * does not have this property set, it will revert to false.
     *
     * @return the value of the cached
     *     <code>superscript</code> property
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
        super.changedUpdate(e, a, f);
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

