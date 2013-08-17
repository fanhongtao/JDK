/*
 * @(#)LabelView.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package javax.swing.text;

import java.awt.*;
import javax.swing.event.*;
import javax.swing.text.AbstractDocument.BidiElement;
import java.awt.geom.*;
import java.awt.font.*;
import java.text.*;

/**
 * A LabelView is a styled chunk of text that represents a view
 * mapped over an element in the text model.  The view supports breaking
 * for the purpose of formatting.   The fragments produced
 * by breaking share the view that has primary responsibility
 * for the element (i.e. they are nested classes and carry only 
 * a small amount of state of their own) so they can share its 
 * resources.
 * <p>
 * This view is generally responsible for displaying character
 * level attributes in some way.  Since this view represents 
 * text that may have tabs embedded in it, it implements the
 * <code>TabableView</code> interface.  Tabs will only be
 * expanded if this view is embedded in a container that does
 * tab expansion.  ParagraphView is an example of a container
 * that does tab expansion.
 *
 * @author Timothy Prinzing
 * @author Brian Beck
 * @version 1.14 04/22/99
 */
public class LabelView extends View /*implements TabableView */{

    /**
     * Instance of FontRenderContext that is used when a LabelFragment is
     * created and there is no associated JTextComponent, or the graphics
     * is null.
     */
    private static final FontRenderContext DefaultRenderContext =
	                    new FontRenderContext
	                        (new AffineTransform(), false, false);

    /**
     * Constructs a new view wrapped on an element.
     *
     * @param elem the element
     */
    public LabelView(Element elem) {
	super(elem);
	text = new Segment();        
    }

    /**
     * Load the text buffer with the given range of text.  This is used by
     * the fragments broken off of this view as well as this view itself.
     */
    final void loadText(int p0, int p1) {
	try {
	    Document doc = getDocument();
	    doc.getText(p0, p1 - p0, text);
	} catch (BadLocationException bl) {
	    throw new StateInvariantError("LabelView: Stale view: " + bl);
	}
    }

    /**
     * Synchronize the view's cached properties with the model.  This causes
     * the font, underline, color, etc. to be recached if the cache has been
     * invalidated.
     */
    final void syncProperties() {
	if (font == null) {
	    setPropertiesFromAttributes();
	}
    }

    /**
     * Synchronize the view's cached fragments.  Causes the fragments to be
     * recreated if the cache has been invalidated.
     */
    private final void syncFragments() {
        if( fragments != null) {
            return;
        }

        int startOffset = getStartOffset();
        int endOffset = getEndOffset();
        Element elem = getElement();
        
        AbstractDocument doc = (AbstractDocument)getDocument();
        Element paragraph = doc.getParagraphElement( startOffset );
        int paragraphStart = paragraph.getStartOffset();
        int paragraphEnd = paragraph.getEndOffset();
        if( endOffset > paragraphEnd )
            throw new StateInvariantError("LabelView may not span paragraphs");

        Element bidiRoot = doc.getBidiRootElement();
        int bidiStartIndex = bidiRoot.getElementIndex( startOffset );
        int bidiEndIndex = bidiRoot.getElementIndex( endOffset-1 );
        if( bidiStartIndex > bidiEndIndex )
            throw new StateInvariantError("0 length element encountered.");

        // REMIND (bcb) its not clear that this is the right way to get
        // a font render context.
        syncProperties();
        FontRenderContext frc;
	Container container = getContainer();
	if (container == null) {
	    frc = DefaultRenderContext;
	}
	else {
	    Graphics2D g2d = (Graphics2D)container.getGraphics();
	    if (g2d != null) {
		frc = g2d.getFontRenderContext();
	    } else {
		frc = DefaultRenderContext;
	    }
	}

        fragments = new LabelFragment[ bidiEndIndex - bidiStartIndex + 1 ];

        int p0 = startOffset;
        for( int i=bidiStartIndex; i<=bidiEndIndex; i++ ) {
            BidiElement bidiElem = (BidiElement)bidiRoot.getElement( i );
            int bidiStart = bidiElem.getStartOffset();
            int bidiEnd = bidiElem.getEndOffset();
            int p1 = Math.min( endOffset, bidiEnd );
            int contextStart = Math.max( paragraphStart, bidiStart);
            int contextEnd = Math.min( paragraphEnd, bidiEnd );
            loadText( contextStart, contextEnd );
            ExtendedTextLabel glyphs
                = StandardExtendedTextLabel.create(text.array,
                                                   text.offset, text.count,
                                                   text.offset+p0-contextStart,
                                                   p1-p0,
                                                   bidiElem.isLeftToRight(),
                                                   font, frc);
            fragments[i-bidiStartIndex]=new LabelFragment(elem,p0,p1,glyphs);
            //REMIND(bcb) is this the correct parent?
            fragments[i-bidiStartIndex].setParent(this);
            
            p0 = p1;
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
        Document d = getDocument();
	if (attr != null) {
	    if (d instanceof StyledDocument) {
		StyledDocument doc = (StyledDocument) d;
		font = doc.getFont(attr);
		fg = doc.getForeground(attr);
		if (attr.isDefined(StyleConstants.Background)) {
		    bg = doc.getBackground(attr);
		}
		else {
		    bg = null;
		}
		setStrikeThrough(StyleConstants.isStrikeThrough(attr));
		setSuperscript(StyleConstants.isSuperscript(attr));
		setSubscript(StyleConstants.isSubscript(attr));
		setUnderline(StyleConstants.isUnderline(attr));
	    } else {
		throw new StateInvariantError("LabelView needs StyledDocument");
	    }
	}
     }

    /**
     * Fetch the FontMetrics used for this view.
     */
    /* REMIND(bcb) who needs this?  Should use line metrics instead.*/
    protected FontMetrics getFontMetrics() {
	syncProperties();
        return Toolkit.getDefaultToolkit().getFontMetrics(font);
    }

    
    /**
     * Fetch the Font used for this view.
     */
     protected Font getFont() {
	syncProperties();
	return font;
    }


    // --- TabableView methods --------------------------------------

    /**
     * Determines the desired span when using the given 
     * tab expansion implementation.  
     *
     * @param x the position the view would be located
     *  at for the purpose of tab expansion >= 0.
     * @param e how to expand the tabs when encountered.
     * @return the desired span >= 0
     * @see TabableView#getTabbedSpan
     */
    /* REMIND(bcb) Not Implemented
    public float getTabbedSpan(float x, TabExpander e) {
	expander = e;
	this.x = (int) x;
	return getPreferredSpan(X_AXIS, getStartOffset(), getEndOffset(), this.x);
    }
    */
    /**
     * Determines the span along the same axis as tab 
     * expansion for a portion of the view.  This is
     * intended for use by the TabExpander for cases
     * where the tab expansion involves aligning the
     * portion of text that doesn't have whitespace 
     * relative to the tab stop.  There is therefore
     * an assumption that the range given does not
     * contain tabs.
     * <p>
     * This method can be called while servicing the
     * getTabbedSpan or getPreferredSize.  It has to
     * arrange for its own text buffer to make the
     * measurements.
     *
     * @param p0 the starting document offset >= 0
     * @param p1 the ending document offset >= p0
     * @return the span >= 0
     */
    /* REMIND(bcb) Not implemented
    public float getPartialSpan(int p0, int p1) {
	// PENDING should probably use a static buffer since there 
	// should be only one thread accessing it.
	syncProperties();
	int width = 0;
	try {
	    Segment s = new Segment();
	    getDocument().getText(p0, p1 - p0, s);
	    width = Utilities.getTabbedTextWidth(s, metrics, x, expander, p0);
	} catch (BadLocationException bl) {
	}
	return width;
    }
    */

    // --- View methods ---------------------------------------------

    /**
     * Renders a portion of a text style run.
     *
     * @param g the rendering surface to use
     * @param a the allocated region to render into
     */
    public void paint(Graphics g, Shape a) {
        syncFragments();
        if( fragments.length > 1 )
            throw new StateInvariantError("Method invalid for multi-directional LabelView");
	fragments[0].paint(g, a);
    }

    /**
     * Determines the preferred span for this view along an
     * axis. 
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns  the span the view would like to be rendered into >= 0.
     *           Typically the view is told to render into the span
     *           that is returned, although there is no guarantee.  
     *           The parent may choose to resize or break the view.
     */
    public float getPreferredSpan(int axis) {
        syncFragments();
        float span = 0;
        for( int i=0; i<fragments.length; i++ )
            span +=fragments[i].getPreferredSpan(axis);
        return span;
    }

    /**
     * Determines the desired alignment for this view along an
     * axis.  For the label, the alignment is along the font
     * baseline for the y axis, and the superclasses alignment
     * along the x axis.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns the desired alignment.  This should be a value
     *   between 0.0 and 1.0 inclusive, where 0 indicates alignment at the
     *   origin and 1.0 indicates alignment to the full span
     *   away from the origin.  An alignment of 0.5 would be the
     *   center of the view.
     */
    public float getAlignment(int axis) {
        syncFragments();
        if( fragments.length > 1 )
            throw new StateInvariantError("Method invalid for multi-directional LabelView");
        return fragments[0].getAlignment( axis );
    }


    /**
     * Provides a mapping from the document model coordinate space
     * to the coordinate space of the view mapped to it.
     *
     * @param pos the position to convert >= 0
     * @param a the allocated region to render into
     * @return the bounding box of the given position
     * @exception BadLocationException  if the given position does not represent a
     *   valid location in the associated document
     * @see View#modelToView
     */
    public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
        syncFragments();
        if( fragments.length > 1 )
            throw new StateInvariantError("Method invalid for multi-directional LabelView");
	return fragments[0].modelToView(pos, a, b);
    }

    /**
     * Provides a mapping from the view coordinate space to the logical
     * coordinate space of the model.
     *
     * @param x the X coordinate >= 0
     * @param y the Y coordinate >= 0
     * @param a the allocated region to render into
     * @return the location within the model that best represents the
     *  given point of view >= 0
     * @see View#viewToModel
     */
    public int viewToModel(float x, float y, Shape a, Position.Bias[] biasReturn) {
        syncFragments();
        if( fragments.length > 1 )
            throw new StateInvariantError("Method invalid for multi-directional LabelView");
	return fragments[0].viewToModel(x, y, a, biasReturn);
    }

    /**
     * Provides a way to determine the next visually represented model
     * location that one might place a caret.  Some views may not be
     * visible, they might not be in the same order found in the model, or
     * they just might not allow access to some of the locations in the
     * model.
     *
     * @param pos the position to convert >= 0
     * @param a the allocated region to render into
     * @param direction the direction from the current position that can
     *  be thought of as the arrow keys typically found on a keyboard.
     *  This may be SwingConstants.WEST, SwingConstants.EAST, 
     *  SwingConstants.NORTH, or SwingConstants.SOUTH.  
     * @return the location within the model that best represents the next
     *  location visual position.
     * @exception BadLocationException
     * @exception IllegalArgumentException for an invalid direction
     */
    public int getNextVisualPositionFrom(int pos, Position.Bias b, Shape a, 
                                         int direction,
                                         Position.Bias[] biasRet) 
        throws BadLocationException {
        syncFragments();
        if( fragments.length > 1 )
            throw new StateInvariantError("Method invalid for multi-directional LabelView");
        return fragments[0].getNextVisualPositionFrom(pos,b,a,direction,
                                                      biasRet);
    }
    
    /**
     * Determines how attractive a break opportunity in 
     * this view is.  This can be used for determining which
     * view is the most attractive to call <code>breakView</code>
     * on in the process of formatting.  The
     * higher the weight, the more attractive the break.  A
     * value equal to or lower than <code>View.BadBreakWeight</code>
     * should not be considered for a break.  A value greater
     * than or equal to <code>View.ForcedBreakWeight</code> should
     * be broken.
     * <p>
     * This is implemented to forward to the superclass for 
     * the Y_AXIS.  Along the X_AXIS the following values
     * may be returned.
     * <dl>
     * <dt><b>View.ExcellentBreakWeight</b>
     * <dd>if there is whitespace proceeding the desired break 
     *   location.  
     * <dt><b>View.BadBreakWeight</b>
     * <dd>if the desired break location results in a break
     *   location of the starting offset.
     * <dt><b>View.GoodBreakWeight</b>
     * <dd>if the other conditions don't occur.
     * </dl>
     * This will normally result in the behavior of breaking
     * on a whitespace location if one can be found, otherwise
     * breaking between characters.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @param pos the potential location of the start of the 
     *   broken view >= 0.  This may be useful for calculating tab
     *   positions.
     * @param len specifies the relative length from <em>pos</em>
     *   where a potential break is desired >= 0.
     * @return the weight, which should be a value between
     *   View.ForcedBreakWeight and View.BadBreakWeight.
     * @see LabelView
     * @see ParagraphView
     * @see BadBreakWeight
     * @see GoodBreakWeight
     * @see ExcellentBreakWeight
     * @see ForcedBreakWeight
     */
    public int getBreakWeight(int axis, float pos, float len) {
        syncFragments();
        if( fragments.length > 1 )
            throw new StateInvariantError("Method invalid for multi-directional LabelView");
	return fragments[0].getBreakWeight(axis, pos, len);
    }

    /**
     * Breaks this view on the given axis at the given length.
     * This is implemented to attempt to break on a whitespace
     * location, and returns a fragment with the whitespace at
     * the end.  If a whitespace location can't be found, the
     * nearest character is used.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @param p0 the location in the model where the
     *  fragment should start it's representation >= 0.
     * @param pos the position along the axis that the
     *  broken view would occupy >= 0.  This may be useful for
     *  things like tab calculations.
     * @param len specifies the distance along the axis
     *  where a potential break is desired >= 0.  
     * @return the fragment of the view that represents the
     *  given span, if the view can be broken.  If the view
     *  doesn't support breaking behavior, the view itself is
     *  returned.
     * @see View#breakView
     */
    public View breakView(int axis, int p0, float pos, float len) {
        syncFragments();
        if( fragments.length > 1 )
            throw new StateInvariantError("Method invalid for multi-directional LabelView");
        return fragments[0].breakView( axis, p0, pos, len );
    }

    /**
     * Creates a view that represents a portion of the element.
     * This is potentially useful during formatting operations
     * for taking measurements of fragments of the view.  If 
     * the view doesn't support fragmenting (the default), it 
     * should return itself.  
     * <p>
     * This view does support fragmenting.  It is implemented
     * to return a nested class that shares state in this view 
     * representing only a portion of the view.
     *
     * @param p0 the starting offset >= 0.  This should be a value
     *   greater or equal to the element starting offset and
     *   less than the element ending offset.
     * @param p1 the ending offset > p0.  This should be a value    
     *   less than or equal to the elements end offset and
     *   greater than the elements starting offset.
     * @returns the view fragment, or itself if the view doesn't
     *   support breaking into fragments.
     * @see LabelView
     */
    public View createFragment(int p0, int p1) /*throws BadLocationException*/ {

        syncFragments();
        
        for( int i=0; i<fragments.length; i++ ) {
            View v = fragments[i];           
            int fragmentStart = v.getStartOffset();
            int fragmentEnd = v.getEndOffset();
           
            if( p0 < fragmentStart ) {
                return this;
                //throw new BadLocationException("Fragment start outside of range",
                //                               p0);
            } else if( p0 == fragmentStart ) {
                if( p1 < fragmentEnd ) {
                    return v.createFragment( p0, p1 );
                }
                else if( p1 == fragmentEnd ) {
                    return v;
                } else if( p1 > fragmentEnd ) {
                    throw new StateInvariantError("frags can't span dir boundaries");
                }
            } else if( p0 < fragmentEnd ) {
                if( p1 <= fragmentEnd ) {
                    return v.createFragment( p0, p1 );
                } else {
                    throw new StateInvariantError("frags can't span dir boundaries");
                }
            }                                                                                  
        }

        //throw new BadLocationException();
        return this;
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
        // Null font indicates attribute cache needs updating
        font = null;

        // REMIND(bcb) the fragment only needs to be invalidated if the
        // font has changed.
        fragments = null;
    }

    /**
     * Gives notification that something was added to the document
     * in a location that this view is responsible for.
     *
     * @param changes the change information from the associated document
     * @param a the current allocation of the view
     * @param f the factory to use to rebuild if the view has children
     * @see View#insertUpdate
     */

    public void insertUpdate(DocumentEvent e, Shape a, ViewFactory f) {
        fragments = null;
	super.insertUpdate(e, a, f);
    }

    /**
     * Gives notification that something was removed from the document
     * in a location that this view is responsible for.
     *
     * @param changes the change information from the associated document
     * @param a the current allocation of the view
     * @param f the factory to use to rebuild if the view has children
     * @see View#removeUpdate
     */
    public void removeUpdate(DocumentEvent changes, Shape a, ViewFactory f) {
        fragments = null;
        super.removeUpdate(changes, a, f);
    }

    public String toString() {
        String s = "LabelView: elem = " + getElement().toString();
        if( fragments == null )
            s += "\tfragments = null\n";
        else {
            for( int i=0; i<fragments.length; i++ ) {
                s += "\tfragment = " + fragments[i].toString() + "\n";
            }
        }
        return s;
    }
    
    // --- variables ------------------------------------------------

    /**
     * Property cache
     */
    Font font;
    Color fg;
    Color bg;
    boolean underline;
    boolean strike;
    boolean superscript;
    boolean subscript;
    
    Segment text;

    /**
     * Cache of LabelFragments, one for each directional run represented
     * by this view.  The LabelFragments do all the interesting work.  In
     * general, LabelView delegates its work to these fragments.
     */
    LabelFragment[] fragments;
    
    /**
     * how to expand tabs
     */
    TabExpander expander;

    /**
     * location for determining tab expansion against.
     */
    int x;

    
    /**
     * A label view that represents a run of text of a single style and
     * single direction.
     */
    class LabelFragment extends View /*implements TabableView*/{

	/**
	 * Constructs a new view wrapped on an element.
	 *
	 * @param elem the element
	 * @param p0 the beginning of the range
	 * @param p1 the end of the range
         * @param glyphs glyphs representing this view's text
	 */
        public LabelFragment(Element elem, int p0, int p1,
                                ExtendedTextLabel glyphs) {
	    super(elem);
	    offset = (short) (p0 - elem.getStartOffset());
	    length = (short) (p1 - p0);
            this.glyphs = glyphs;

            //REMIND(bcb) it may be cheaper/better to either pass this in or
            //store it in the glyph vector.  Also AbstractDoc.isLeftToRight is
            //broken because it can't detect mixed direction text.
            Document d = getDocument();
            if( d instanceof AbstractDocument ) {
		rightToLeft = !((AbstractDocument)d).isLeftToRight(p0, p1);
            }
	}

	// --- TabableView methods --------------------------------------

	/**
	 * Determines the desired span when using the given 
	 * tab expansion implementation.  
	 *
	 * @param x the position the view would be located
	 *  at for the purpose of tab expansion >= 0.
	 * @param e how to expand the tabs when encountered.
	 * @return the desired span >= 0
	 * @see TabableView#getTabbedSpan
	 */
        /* REMIND(bcb) not yet ready for tab support.
        public float getTabbedSpan(float x, TabExpander e) {
	    LabelView.this.expander = e;
	    this.x = (int) x;
	    return LabelView.this.getPreferredSpan(X_AXIS, getStartOffset(), 
						   getEndOffset(), this.x);
	}
        */
	/**
	 * Determine the span along the same axis as tab 
	 * expansion for a portion of the view.  This is
	 * intended for use by the TabExpander for cases
	 * where the tab expansion involves aligning the
	 * portion of text that doesn't have whitespace 
	 * relative to the tab stop.  There is therefore
	 * an assumption that the range given does not
	 * contain tabs.
	 */
        /* REMIND(bcb) not yet ready for tab support.
        public float getPartialSpan(int p0, int p1) {
	    return LabelView.this.getPartialSpan(p0, p1);
	}
        */
        
	// --- View methods ----------------------------

	/**
	 * This returns the attributes of the LabelView that created
	 * the receiver.
	 */
	public AttributeSet getAttributes() {
	    return LabelView.this.getAttributes();
	}

	/**
	 * Fetches the starting offset of the portion of the model that this
         * view is responsible for.
         *
	 * @return the starting offset into the model
	 * @see View#getStartOffset
	 */
        public int getStartOffset() {
	    Element e = getElement();
	    return e.getStartOffset() + offset;
	}

	/**
	 * Fetches the ending offset of the portion of the model that this
         * view is responsible for.
	 *
	 * @return the ending offset into the model
	 * @see View#getEndOffset
	 */
        public int getEndOffset() {
	    Element e = getElement();
	    return e.getStartOffset() + offset + length;
	}

	/**
	 * Renders a portion of a text style run.
	 *
	 * @param g the rendering surface to use
	 * @param a the allocated region to render into
	 */
        public void paint(Graphics g, Shape a) {
            syncProperties();
	    if (LabelView.this.bg != null) {
		Rectangle alloc = (a instanceof Rectangle) ? (Rectangle)a :
		                  a.getBounds();
		g.setColor(LabelView.this.bg);
		g.fillRect(alloc.x, alloc.y, alloc.width, alloc.height);
	    }
	    Component comp = getContainer();
	    if (comp instanceof JTextComponent) {
		JTextComponent c = (JTextComponent) comp;
		Highlighter h = c.getHighlighter();
		if (h instanceof LayeredHighlighter) {
		    ((LayeredHighlighter)h).paintLayeredHighlights
			(g, getStartOffset(), getEndOffset(), a, c, this);
		}
	    }
            if (Utilities.isComposedTextElement(getElement())) {
                paintComposedText((Graphics2D)g, a.getBounds(), getStartOffset(), getEndOffset()); 
            } else {
                paintText( (Graphics2D)g, a );
            }
	}

        void paintText(Graphics2D g, Shape a) {
            Rectangle alloc = a.getBounds();
            float y = (float)alloc.y + glyphs.getLineMetrics().getAscent();
            g.setFont( font );
            g.setColor( fg );
            glyphs.draw( g, (float)alloc.x, y );

            if( underline || strike ) {
                LineMetrics metrics = glyphs.getLineMetrics();
                float lineY;
                float thickness;
                if( underline ) {
                    lineY = y + metrics.getUnderlineOffset();
                    thickness = metrics.getUnderlineThickness();
                } else {
                    lineY = y + metrics.getStrikethroughOffset();
                    thickness = metrics.getStrikethroughThickness();
                }
                Rectangle2D line
                    = new Rectangle2D.Float( (float)alloc.x, lineY,
                                             (float)alloc.width, thickness );
                g.fill( line );
            }
	}
        
        /**
         * Paint the selection highlight over the portion of this view
         * that is selected.
         */
        void paintSelection(Graphics g, Shape a) {
            JTextComponent c = (JTextComponent)getContainer();
            //System.out.println("paintSel c = " + c);
            //System.out.println("paintSel parent = " + getParent());
            if(c == null) 
                return;
            Color selBG = c.getSelectionColor();
            //System.out.println("paintSel: selBG = " + selBG);
            if( selBG == null ) 
                return;

            int p0 = getStartOffset();
            int p1 = getEndOffset();
            Position.Bias[] selStartBias = new Position.Bias[1];
            Position.Bias[] selEndBias = new Position.Bias[1];
            int selStart = c.getSelectionStart(selStartBias);
            int selEnd = c.getSelectionEnd(selEndBias);
            //System.out.println( "\tSelection start = " + selStart + " selection end = " + selEnd);
                
            if(selStart == selEnd) {
                // There is no selection.
                return;
            }
            
            int pMin;
            
            int pMax;
            
            if(selStart <= p0) {
                pMin = p0;
                selStartBias[0] = Position.Bias.Forward;
            }
            else
                pMin = Math.min(selStart, p1);
            if(selEnd >= p1) {
                pMax = p1;
                selEndBias[0] = Position.Bias.Backward;
            }
            else
                pMax = Math.max(selEnd, p0);
            // If pMin == pMax (also == p0), selection isn't in this
            // block.
            if(pMin == pMax)
                return;
                     
            /* paint the selection's background */
            try {
                Rectangle rMax = modelToView(pMax,a,selEndBias[0]).getBounds();
                Rectangle rMin = modelToView(pMin,a,selStartBias[0]).getBounds();
                Rectangle r = rMin.union(rMax);
                g.setColor( selBG );
                /*
                System.out.println("\tPMin = " + pMin + " PMax = " + pMax);
                System.out.println("\tRMin = " + rMin);
                System.out.println("\tRMax = " + rMax);
                System.out.println("\tR = " + r);
                */
                g.fillRect(r.x, r.y, r.width, r.height);
            } catch (BadLocationException e ) {}
        }

        /**
	 * Determines the preferred span for this view along an
	 * axis. 
	 *
	 * @param axis may be either X_AXIS or Y_AXIS
	 * @returns  the span the view would like to be rendered into.
	 *           Typically the view is told to render into the span
	 *           that is returned, although there is no guarantee.  
	 *           The parent may choose to resize or break the view.
	 */
        public float getPreferredSpan(int axis) {
            Rectangle2D bounds = glyphs.getLogicalBounds( 0f, 0f );
            switch (axis) {
            case View.X_AXIS:
                return Math.max((float)bounds.getWidth(), 1f);
            case View.Y_AXIS:
                return (float)bounds.getHeight();
            default:
                throw new IllegalArgumentException("Invalid axis: " + axis);
            }
	}

	/**
	 * Determines the desired alignment for this view along an
	 * axis.  For the label, the alignment is along the font
	 * baseline for the y axis, and the superclasses alignment
	 * along the x axis.
	 *
	 * @param axis may be either X_AXIS or Y_AXIS
	 * @returns the desired alignment.  This should be a value
	 *   between 0.0 and 1.0 where 0 indicates alignment at the
	 *   origin and 1.0 indicates alignment to the full span
	 *   away from the origin.  An alignment of 0.5 would be the
	 *   center of the view.
	 */
        public float getAlignment(int axis) {
            if (axis == View.Y_AXIS) {
                LineMetrics metrics = glyphs.getLineMetrics();
                return metrics.getAscent() / metrics.getHeight();
            } 
            return super.getAlignment(axis);
	}


	/**
	 * Provides a mapping from the document model coordinate space
	 * to the coordinate space of the view mapped to it.
	 *
	 * @param pos the position to convert
	 * @param a the allocated region to render into
	 * @return the bounding box of the given position
	 * @exception BadLocationException  if the given position does not represent a
	 *   valid location in the associated document
	 * @see View#modelToView
	 */
        public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {

            int startOffset = getStartOffset();
            int endOffset = getEndOffset();

            // Make sure this is a position we're responsible for.
            if( (pos > endOffset || pos < startOffset)
                || (pos == endOffset && b == Position.Bias.Forward)
                || (pos == startOffset && b == Position.Bias.Backward) ) {
                String s = "modelToView - Position (" + pos + "," + b
                           + ") not in view's range of ("
                           + getStartOffset() + "," + getEndOffset() + ").";
                throw new BadLocationException(s, pos);
            }

            // Convert pos into an index
            int index = pos - startOffset;
            float width = 0;
            if( rightToLeft ) {
                if( b == Position.Bias.Forward ) {
                    float charX = glyphs.getCharX( index );
                    float advance = glyphs.getCharAdvance( index );
                    width = charX + advance;
                } else {
                    width = glyphs.getCharX( index-1 );
                }
            } else {
                if( b == Position.Bias.Forward ) {
                    width = glyphs.getCharX( index );
                } else {
                    float charX = glyphs.getCharX( index-1 );
                    float advance = glyphs.getCharAdvance( index-1 );
                    width = charX + advance;
                }
            }

            Rectangle alloc = a.getBounds();
            
            float height = glyphs.getLineMetrics().getHeight();

            //REMIND(bcb) should we be returning old geom or new 2D geom
            return new Rectangle(alloc.x + (int)width, alloc.y, 0, (int)height);      
	}

	/**
	 * Provides a mapping from the view coordinate space to the logical
	 * coordinate space of the model.
	 *
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @param a the allocated region to render into
	 * @return the location within the model that best represents the
	 *  given point of view
	 * @see View#viewToModel
	 */
        public int viewToModel(float x, float y, Shape a, Position.Bias[] biasReturn) {

            Rectangle alloc = (a instanceof Rectangle) 
                              ? (Rectangle)a : a.getBounds();

            //System.out.println("LabelView.viewToModel(x="+x+", y="+y+ ", shape = "+alloc);

            // Check for points out of our visual bounds and return one of
            // the two end offsets.
            if( x < alloc.x ) {
                if( rightToLeft ) {
                    biasReturn[0] = Position.Bias.Backward;
                    return getEndOffset();
                } else {
                    biasReturn[0] = Position.Bias.Forward;
                    return getStartOffset();
                }
            }
            if( x >= alloc.x + alloc.width ) {
                if( rightToLeft ) {
                    biasReturn[0] = Position.Bias.Forward;
                    return getStartOffset();
                } else {
                    biasReturn[0] = Position.Bias.Backward;
                    return getEndOffset();
                }
            }
            
            float width = x - alloc.x;
            int index = glyphs.getCharIndexAtWidth(width);
	    if (index >= glyphs.getNumCharacters()) {
		// This will usually happen when representing and end of
		// line, or end of document.
		if (rightToLeft) {
		    // Could really be either here.
                    biasReturn[0] = Position.Bias.Forward;
		    return getStartOffset();
		}
		else {
                    biasReturn[0] = Position.Bias.Backward;
		    return getEndOffset();
		}
	    }
            float charX = glyphs.getCharX( index );
            float advance = glyphs.getCharAdvance( index );
            int offset = getStartOffset();
            if( width < (charX + (advance/2)) ){
                if( rightToLeft ) {
                    offset += index + 1;
                    biasReturn[0] = Position.Bias.Backward;
                } else {
                    offset += index;
                    biasReturn[0] = Position.Bias.Forward;
                }
            } else {
                if( rightToLeft ) {
                    offset += index;
                    biasReturn[0] = Position.Bias.Forward;
                } else {
                    offset += index + 1;
                    biasReturn[0] = Position.Bias.Backward;
                }
            }
            //System.out.println("offset = " + offset + " bias = " + biasReturn[0]);
            return offset;
        }

    
        /**
         * Provides a way to determine the next visually represented model
         * location that one might place a caret.  Some views may not be
         * visible, they might not be in the same order found in the model, or
         * they just might not allow access to some of the locations in the
         * model.
         *
         * @param pos the position to convert >= 0
         * @param a the allocated region to render into
         * @param direction the direction from the current position that can
         *  be thought of as the arrow keys typically found on a keyboard.
         *  This may be SwingConstants.WEST, SwingConstants.EAST, 
         *  SwingConstants.NORTH, or SwingConstants.SOUTH.  
         * @return the location within the model that best represents the next
         *  location visual position.
         * @exception BadLocationException
         * @exception IllegalArgumentException for an invalid direction */
        public int getNextVisualPositionFrom(int pos, Position.Bias b, Shape a, 
                                             int direction,
                                             Position.Bias[] biasRet) 
            throws BadLocationException {

            //System.out.println("LabelView:getNextVisPos pos = " +  pos
            //                 + " bias = " + b + " direction = " + direction);
            int startOffset = getStartOffset();
            int endOffset = getEndOffset();
            
            switch (direction) {
            case NORTH:
                break;
            case SOUTH:
                break;
            case EAST:
                if(startOffset == getDocument().getLength()) {
                    if(pos == -1) {
                        biasRet[0] = Position.Bias.Forward;
                        return startOffset;
                    }
                    // End case for bidi text where newline is at beginning
                    // of line.
                    return -1;
                }
                if(rightToLeft) {
                    if(pos == -1) {
                        loadText(endOffset - 1, endOffset);
                        if(text.array[text.offset] == '\n') {
                            biasRet[0] = Position.Bias.Forward;
                            return endOffset - 1;
                        }
                        biasRet[0] = Position.Bias.Backward;
                        return endOffset;
                    }
                    if(pos == startOffset) {
                        return -1;
                    }
                    biasRet[0] = Position.Bias.Forward;
                    while( (--pos >= startOffset)
                          && (glyphs.getCharAdvance(pos-startOffset) == 0) );
                    if( pos < startOffset )
                        return -1;
                    else
                        return pos;
                }
                if(pos == -1) {
                    biasRet[0] = Position.Bias.Forward;
                    return startOffset;
                }
                if(pos == endOffset) {
                    return -1;
                }
                if(++pos == endOffset) {
                    loadText(endOffset - 1, endOffset);
                    if(text.array[text.offset] == '\n') {
                        return -1;
                    }
                    biasRet[0] = Position.Bias.Backward;
                }
                else {
                    biasRet[0] = Position.Bias.Forward;
                }
                return pos;
            case WEST:
                if(startOffset == getDocument().getLength()) {
                    if(pos == -1) {
                        biasRet[0] = Position.Bias.Forward;
                        return startOffset;
                    }
                    // End case for bidi text where newline is at beginning
                    // of line.
                    return -1;
                }
                if(rightToLeft) {
                    if(pos == -1) {
                        biasRet[0] = Position.Bias.Forward;
                        return startOffset;
                    }
                    if(pos == endOffset) {
                        return -1;
                    }
                    if(++pos == endOffset) {
                        loadText(endOffset - 1, endOffset);
                        if(text.array[text.offset] == '\n') {
                            return -1;
                        }
                        biasRet[0] = Position.Bias.Backward;
                    }
                    else {
                        biasRet[0] = Position.Bias.Forward;
                    }
                    return pos;
                }
                if(pos == -1) {
                    loadText(endOffset - 1, endOffset);
                    if(text.array[text.offset] == '\n') {
                        biasRet[0] = Position.Bias.Forward;
                        return endOffset - 1;
                    }
                    biasRet[0] = Position.Bias.Backward;
                    return endOffset;
                }
                if(pos == startOffset) {
                    return -1;
                }
                biasRet[0] = Position.Bias.Forward;
                return (pos - 1);
            default:
                throw new IllegalArgumentException("Bad direction: " + direction);
            }
            return pos;
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
        /* REMIND(bcb) this should not be necessary.
        public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f) {
	    LabelView.this.changedUpdate(e, a, f);
	}
        */

	/**
	 * @see LabelView#getBreakWeight
	 */
        public int getBreakWeight(int axis, float pos, float len) {
            int p0 = getStartOffset();
            int p1 = getEndOffset();
            if (axis == View.X_AXIS) {
                syncProperties();
                loadText(p0, p1);
                
                // REMIND(bcb) The spec says len will always be >= 0.
                // Currently Currently ParagraphView doesn't respect this.
                // Until its fixed, this check will protect us.
                if( len < 0 )
                    return BadBreakWeight;
                
                //int index = glyphs.getCharIndexAtWidth(len );
                int index = glyphs.getLineBreakIndex( 0, len );
                if (index == 0) {
                    // break is at the start offset
                    return BadBreakWeight;
                }
                for (int i = text.offset + Math.min(index, text.count - 1); 
                     i >= text.offset; i--) {

                    char ch = text.array[i];
                    if (Character.isWhitespace(ch)) {
                        // found whitespace
                        return ExcellentBreakWeight;
                    }
                }
                // no whitespace
                return GoodBreakWeight;
            }
            return super.getBreakWeight(axis, pos, len);
	}

	/**
	 * Breaks this view on the given axis at the given length.
	 *
	 * @param axis may be either X_AXIS or Y_AXIS
	 * @param offset the location in the model where the
	 *  fragment should start it's representation.
	 * @param pos the position along the axis that the
	 *  broken view would occupy.  This may be useful for
	 *  things like tab calculations.
	 * @param len specifies the distance along the axis
	 *  where a potential break is desired.  
	 * @param a the current allocation of the view
	 * @return the fragment of the view that represents the
	 *  given span, if the view can be broken.  If the view
	 *  doesn't support breaking behavior, the view itself is
	 *  returned.
	 * @see View#breakView
	 */
        public View breakView(int axis, int p0, float pos, float len) {

            if (axis == View.X_AXIS) {
                syncProperties();
                loadText(p0, getEndOffset());
                int index = glyphs.getLineBreakIndex(p0-getStartOffset(), len);
                //int index = glyphs.getCharIndexAtWidth( len);

                for (int i = text.offset + Math.min(index, text.count - 1); 
                     i >= text.offset; i--) {
                        
                    char ch = text.array[i];
                    if (Character.isWhitespace(ch)) {
                        // found whitespace, break here
                        index = i - text.offset + 1;
                        break;
                    }
                }
                int p1 = p0 + index;
                LabelFragment frag = (LabelFragment)LabelView.this
                                     .createFragment(p0, p1);
                frag.x = (int) pos;
                return frag;
            }
            return this;
            
        }

        /**
         * Creates a view that represents a portion of the element.
         * This is potentially useful during formatting operations
         * for taking measurements of fragments of the view.  If 
         * the view doesn't support fragmenting (the default), it 
         * should return itself.  
         * <p>
         * This view does support fragmenting.  It is implemented
         * to return a nested class that shares state in this view 
         * representing only a portion of the view.
         *
         * @param p0 the starting offset >= 0.  This should be a value
         *   greater or equal to the element starting offset and
         *   less than the element ending offset.
         * @param p1 the ending offset > p0.  This should be a value
         *   less than or equal to the elements end offset and
         *   greater than the elements starting offset.
         * @returns the view fragment, or itself if the view doesn't
         *   support breaking into fragments.
         * @see LabelView
         */
        public View createFragment(int p0, int p1) {
            //System.out.println("createfrag("+p0+","+p1+")");
            //REMIND(bcb) this is the brute force way to create a fragment
            //from another fragment.  It should be possible to do this without
            //reshaping.
            
            int startOffset = getStartOffset();
            int endOffset = getEndOffset();

            AbstractDocument doc = (AbstractDocument)getDocument();
            Element paragraph = doc.getParagraphElement( startOffset );
            int paragraphStart = paragraph.getStartOffset();
            int paragraphEnd = paragraph.getEndOffset();
            if( endOffset > paragraphEnd )
                throw new StateInvariantError("LabelFragment may not span paragraphs");

            Element bidiRoot = doc.getBidiRootElement();
            int index = bidiRoot.getElementIndex( startOffset );
            BidiElement bidiElem = (BidiElement)bidiRoot.getElement( index );
            if( bidiElem.getEndOffset() < endOffset )
                throw new StateInvariantError("LabelFragments may not span directional boundaries.");

            // REMIND (bcb) its not clear that this is the right way to get
            // a font render context.
            syncProperties();
	    FontRenderContext frc;
	    Container container = getContainer();
	    if (container == null) {
		frc = DefaultRenderContext;
	    }
	    else {
		frc = ((Graphics2D)container.getGraphics()).
		                             getFontRenderContext();
	    }
        
            int bidiStart = bidiElem.getStartOffset();
            int bidiEnd = bidiElem.getEndOffset();
            int contextStart = Math.max( paragraphStart, bidiStart);
            int contextEnd = Math.min( paragraphEnd, bidiEnd );
            loadText( contextStart, contextEnd );
            ExtendedTextLabel glyphs
                = StandardExtendedTextLabel.create(text.array,
                                                   text.offset, text.count,
                                                   text.offset+p0-contextStart,
                                                   p1-p0,
                                                   bidiElem.isLeftToRight(),
                                                   font, frc);
            
            return new LabelFragment( getElement(), p0, p1, glyphs );
        }

        
	/**
	 * Fetches the container hosting the view.  This is useful for
	 * things like scheduling a repaint, finding out the host 
	 * components font, etc.  The hosting LabelView is used to
	 * satisfy the request since it is connected to the view 
	 * hierarchy for the life of the element it represents where
	 * the fragments are fairly transient.
	 *
	 * @return the container, null if none
	 */
        public Container getContainer() {
	    return LabelView.this.getContainer();
	}

        public String toString() {
            String s = "LabelFrag: elem(" + getStartOffset() + ", "
                       + getEndOffset() + ")\n";
            return s;
        }

        /**
         * Paints the composed text in this element.
         */
        void paintComposedText(Graphics2D g2d, Rectangle alloc, int p0, int p1) {
	    AttributeSet attrSet = getElement().getAttributes();
	    AttributedString as = 
                (AttributedString)attrSet.getAttribute(StyleConstants.ComposedTextAttribute);
	    int start = getElement().getStartOffset();
	    int y = alloc.y + (int)glyphs.getLineMetrics().getAscent();
	    int x = alloc.x;

	    /*
	     * Add text attributes
	     */
	    as.addAttribute(TextAttribute.FONT, font);
	    as.addAttribute(TextAttribute.FOREGROUND, fg);
	    if (StyleConstants.isBold(attrSet)) {
	        as.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
	    }
	    if (StyleConstants.isItalic(attrSet)) {
	        as.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
	    }
	    if (underline) {
	        as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
	    }
	    if (strike) {
	        as.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
	    }
	    if (superscript) {
	        as.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);
	    }
	    if (subscript) {
	        as.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB);
	    }

	    // draw
	    AttributedCharacterIterator aci = as.getIterator(null, p0 - start, p1 - start);
	    TextLayout layout = new TextLayout(aci, g2d.getFontRenderContext());
	    layout.draw(g2d, x, y);
	}
        
	// ---- variables ---------------------------------
	short offset;
	short length;
	int x;
        boolean rightToLeft;
        ExtendedTextLabel glyphs;
    }
}

