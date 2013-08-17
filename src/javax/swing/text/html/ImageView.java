/*
 * @(#)ImageView.java	1.27 98/08/26
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
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

import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.io.*;
import java.net.*;
import java.util.Dictionary;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

/**
 * View of an Image, intended to support the HTML <IMG> tag.
 * Supports scaling via the HEIGHT and WIDTH parameters.
 *
 * @author  Jens Alfke
 * @version 1.27 08/26/98
 * @see IconView
 */
class ImageView extends View implements ImageObserver,
					MouseListener, MouseMotionListener,
					Runnable {

    // --- Attribute Values ------------------------------------------
    
    public static final String
    	TOP = "top",
    	TEXTTOP = "texttop",
    	MIDDLE = "middle",
    	ABSMIDDLE = "absmiddle",
    	CENTER = "center",
    	BOTTOM = "bottom";
    

    // --- Construction ----------------------------------------------

    /**
     * Creates a new view that represents an IMG element.
     *
     * @param elem the element to create a view for
     */
    public ImageView(Element elem) {
    	super(elem);
    	initialize(elem);
	StyleSheet sheet = getStyleSheet();
	attr = sheet.getViewAttributes(this);
    }
    
    
    private void initialize( Element elem ) {
        fElement = elem;
        
	// Request image from document's cache:
	AttributeSet attr = elem.getAttributes();
	URL src = getSourceURL();
	if( src != null ) {
	    Dictionary cache = (Dictionary) getDocument().getProperty(IMAGE_CACHE_PROPERTY);
	    if( cache != null )
	        fImage = (Image) cache.get(src);
	    else
	        fImage = Toolkit.getDefaultToolkit().getImage(src);
	}
	
	// Get height/width from params or image or defaults:
	fHeight = getIntAttr(HTML.Attribute.HEIGHT,-1);
	boolean customHeight = (fHeight>0);
	if( !customHeight && fImage != null )
	    fHeight = fImage.getHeight(this);
	if( fHeight <= 0 )
	    fHeight = DEFAULT_HEIGHT;
		
	fWidth = getIntAttr(HTML.Attribute.WIDTH,-1);
	boolean customWidth = (fWidth>0);
	if( !customWidth && fImage != null )
	    fWidth = fImage.getWidth(this);
	if( fWidth <= 0 )
	    fWidth = DEFAULT_WIDTH;
	
	// Make sure the image starts loading:
	if( fImage != null )
	    if( customWidth && customHeight )
	        Toolkit.getDefaultToolkit().prepareImage(fImage,fHeight,fWidth,this);
	    else
	        Toolkit.getDefaultToolkit().prepareImage(fImage,-1,-1,this);
	
	if( DEBUG ) {
	    if( fImage != null )
	    	System.out.println("ImageInfo: new on "+src+" ("+fWidth+"x"+fHeight+")");
	    else
	    	System.out.println("ImageInfo: couldn't get image at "+src);
	    if(isLink()) System.out.println("           It's a link! Border = "+getBorder());
	    //((AbstractDocument.AbstractElement)elem).dump(System.out,4);
	}
    }
    
    /**
     * Fetches the attributes to use when rendering.  This is
     * implemented to multiplex the attributes specified in the
     * model with a StyleSheet.
     */
    public AttributeSet getAttributes() {
	return attr;
    }

    /** Is this image within a link? */
    boolean isLink( ) {
        //! It would be nice to cache this but in an editor it can change
        // See if I have an HREF attribute courtesy of the enclosing A tag:
	AttributeSet anchorAttr = (AttributeSet)
	    fElement.getAttributes().getAttribute(HTML.Tag.A);
	if (anchorAttr != null) {
	    return anchorAttr.isDefined(HTML.Attribute.HREF);
	}
	return false;
    }
    
    /** Returns the size of the border to use. */
    int getBorder( ) {
        return getIntAttr(HTML.Attribute.BORDER, isLink() ?DEFAULT_BORDER :0);
    }
    
    /** Returns the amount of extra space to add along an axis. */
    int getSpace( int axis ) {
    	return getIntAttr( axis==X_AXIS ?HTML.Attribute.HSPACE :HTML.Attribute.VSPACE,
    			   0 );
    }
    
    /** Returns the border's color, or null if this is not a link. */
    Color getBorderColor( ) {
    	StyledDocument doc = (StyledDocument) getDocument();
        return doc.getForeground(getAttributes());
    }
    
    /** Returns the image's vertical alignment. */
    float getVerticalAlignment( ) {
	String align = (String) fElement.getAttributes().getAttribute(HTML.Attribute.ALIGN);
	if( align != null ) {
	    align = align.toLowerCase();
	    if( align.equals(TOP) || align.equals(TEXTTOP) )
	        return 0.0f;
	    else if( align.equals(this.CENTER) || align.equals(MIDDLE)
					       || align.equals(ABSMIDDLE) )
	        return 0.5f;
	}
	return 1.0f;		// default alignment is bottom
    }
    
    boolean hasPixels( ImageObserver obs ) {
        return fImage != null && fImage.getHeight(obs)>0
			      && fImage.getWidth(obs)>0;
    }
    

    /** Return a URL for the image source, 
        or null if it could not be determined. */
    private URL getSourceURL( ) {
 	String src = (String) fElement.getAttributes().getAttribute(HTML.Attribute.SRC);
 	if( src==null ) return null;
	URL reference = (URL) fElement.getDocument().getProperty(Document.StreamDescriptionProperty);
        try {
 	    return new URL(reference,src); 	
        } catch (MalformedURLException e) {
	    return null;
        }
    }
    
    /** Look up an integer-valued attribute. <b>Not</b> recursive. */
    private int getIntAttr(HTML.Attribute name, int deflt ) {
    	AttributeSet attr = fElement.getAttributes();
    	if( attr.isDefined(name) ) {		// does not check parents!
    	    int i;
 	    String val = (String) attr.getAttribute(name);
 	    if( val == null )
 	    	i = deflt;
 	    else
 	    	try{
 	            i = Math.max(0, Integer.parseInt(val));
 	    	}catch( NumberFormatException x ) {
 	    	    i = deflt;
 	    	}
	    return i;
	} else
	    return deflt;
    }
    

    /**
     * Establishes the parent view for this view.
     * Seize this moment to cache the AWT Container I'm in.
     */
    public void setParent(View parent) {
	super.setParent(parent);
	fContainer = parent!=null ?getContainer() :null;
	if( parent==null && fComponent!=null ) {
	    fComponent.getParent().remove(fComponent);
	    fComponent = null;
	}
    }

    /** My attributes may have changed. */
    public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f) {
if(DEBUG) System.out.println("ImageView: changedUpdate begin...");
    	super.changedUpdate(e,a,f);
    	float align = getVerticalAlignment();
    	
    	int height = fHeight;
    	int width  = fWidth;
    	
    	initialize(getElement());
    	
    	boolean hChanged = fHeight!=height;
    	boolean wChanged = fWidth!=width;
    	if( hChanged || wChanged || getVerticalAlignment()!=align ) {
    	    if(DEBUG) System.out.println("ImageView: calling preferenceChanged");
    	    getParent().preferenceChanged(this,hChanged,wChanged);
    	}
if(DEBUG) System.out.println("ImageView: changedUpdate end; valign="+getVerticalAlignment());
    }


    // --- Painting --------------------------------------------------------

    /**
     * Paints the image.
     *
     * @param g the rendering surface to use
     * @param a the allocated region to render into
     * @see View#paint
     */
    public void paint(Graphics g, Shape a) {
	Color oldColor = g.getColor();
	fBounds = a.getBounds();
        int border = getBorder();
	int x = fBounds.x + border + getSpace(X_AXIS);
	int y = fBounds.y + border + getSpace(Y_AXIS);
	int width = fWidth;
	int height = fHeight;
	int sel = getSelectionState();
	
	// Make sure my Component is in the right place:
/*
	if( fComponent == null ) {
	    fComponent = new Component() { };
	    fComponent.addMouseListener(this);
	    fComponent.addMouseMotionListener(this);
	    fComponent.setCursor(Cursor.getDefaultCursor());	// use arrow cursor
	    fContainer.add(fComponent);
	}
	fComponent.setBounds(x,y,width,height);
	*/
	// If no pixels yet, draw gray outline and icon:
	if( ! hasPixels(this) ) {
	    g.setColor(Color.lightGray);
	    g.drawRect(x,y,width-1,height-1);
	    g.setColor(oldColor);
	    loadIcons();
	    Icon icon = fImage==null ?sMissingImageIcon :sPendingImageIcon;
	    if( icon != null )
	        icon.paintIcon(getContainer(), g, x, y);
	}
		    
	// Draw image:
	if( fImage != null ) {
	    g.drawImage(fImage,x, y,width,height,this);
	    // Use the following instead of g.drawImage when
	    // BufferedImageGraphics2D.setXORMode is fixed (4158822).

	    //  Use Xor mode when selected/highlighted.
	    //! Could darken image instead, but it would be more expensive.
/*
	    if( sel > 0 )
	    	g.setXORMode(Color.white);
	    g.drawImage(fImage,x, y,
	    		width,height,this);
	    if( sel > 0 )
	        g.setPaintMode();
*/
	}
	
	// If selected exactly, we need a black border & grow-box:
	Color bc = getBorderColor();
	if( sel == 2 ) {
	    // Make sure there's room for a border:
	    int delta = 2-border;
	    if( delta > 0 ) {
	    	x += delta;
	    	y += delta;
	    	width -= delta<<1;
	    	height -= delta<<1;
	    	border = 2;
	    }
	    bc = null;
	    g.setColor(Color.black);
	    // Draw grow box:
	    g.fillRect(x+width-5,y+height-5,5,5);
	}

	// Draw border:
	if( border > 0 ) {
	    if( bc != null ) g.setColor(bc);
	    // Draw a thick rectangle:
	    for( int i=1; i<=border; i++ )
	        g.drawRect(x-i, y-i, width-1+i+i, height-1+i+i);
	    g.setColor(oldColor);
	}
    }

    /** Request that this view be repainted.
        Assumes the view is still at its last-drawn location. */
    protected void repaint( long delay ) {
    	if( fContainer != null && fBounds!=null ) {
	    fContainer.repaint(delay,
	   	      fBounds.x,fBounds.y,fBounds.width,fBounds.height);
    	}
    }
    
    /** Determines whether the image is selected, and if it's the only thing selected.
    	@return  0 if not selected, 1 if selected, 2 if exclusively selected.
    		 "Exclusive" selection is only returned when editable. */
    protected int getSelectionState( ) {
    	int p0 = fElement.getStartOffset();
    	int p1 = fElement.getEndOffset();
    	JTextComponent textComp = (JTextComponent)fContainer;
    	Highlighter highlighter = textComp.getHighlighter();
    	Highlighter.Highlight[] hi = highlighter.getHighlights();
    	for( int i=hi.length-1; i>=0; i-- ) {
    	    Highlighter.Highlight h =  hi[i];
    	    int start = h.getStartOffset();
    	    int end   = h.getEndOffset();
    	    if( start<=p0 && end>=p1 ) {
    	    	if( start==p0 && end==p1 && isEditable() )
    	    	    return 2;
    	    	else
    	    	    return 1;
    	    }
    	}
    	return 0;
    }
    
    protected boolean isEditable( ) {
    	return fContainer instanceof JEditorPane
    	    && ((JEditorPane)fContainer).isEditable();
    }
    
    /** Returns the text editor's highlight color. */
    protected Color getHighlightColor( ) {
    	JTextComponent textComp = (JTextComponent)fContainer;
    	return textComp.getSelectionColor();
    }

    // --- Progressive display ---------------------------------------------
    
    public boolean imageUpdate( Image img, int flags, int x, int y,
    				int width, int height ) {
    	
    	if( fImage==null )
    	    return false;
    	    
    	// Bail out if there was an error:
        if( (flags & (ABORT|ERROR)) != 0 ) {
            fImage = null;
            repaint(0);
            return false;
        }
        
        // Resize image if necessary:
        boolean resized = false;
        if( (flags & ImageObserver.HEIGHT) != 0 )
            if( ! getElement().getAttributes().isDefined(HTML.Attribute.HEIGHT) ) {
                fHeight = height;
                resized = true;
            }
        if( (flags & ImageObserver.WIDTH) != 0 )
            if( ! getElement().getAttributes().isDefined(HTML.Attribute.WIDTH) ) {
                fWidth = width;
                resized = true;
            }
        if( resized ) {
            // May need to resize myself, asynchronously:
            if( DEBUG ) System.out.println("ImageView: resized to "+fWidth+"x"+fHeight);
	    SwingUtilities.invokeLater(this);	// call run() later
	    return true;
        }
	
	// Repaint when done or when new pixels arrive:
	if( (flags & (FRAMEBITS|ALLBITS)) != 0 )
	    repaint(0);
	else if( (flags & SOMEBITS) != 0 )
	    if( sIsInc )
	        repaint(sIncRate);
        
        return true;
    }
        
    public void run( ) {
if(DEBUG)System.out.println("ImageView: Called preferenceChanged");
        preferenceChanged(this,true,true);
    }
    
    /**
     * Static properties for incremental drawing.
     * Swiped from Component.java
     * @see #imageUpdate
     */
    private static boolean sIsInc;
    private static int sIncRate;
    static {
	String s;

	s = System.getProperty("awt.image.incrementaldraw");
	sIsInc = (s == null || s.equals("true"));

	s = System.getProperty("awt.image.redrawrate");
	sIncRate = (s != null) ? Integer.parseInt(s) : 100;
    }

    // --- Layout ----------------------------------------------------------

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
//if(DEBUG)System.out.println("ImageView: getPreferredSpan");
        int extra = 2*(getBorder()+getSpace(axis));
	switch (axis) {
	case View.X_AXIS:
	    return fWidth+extra;
	case View.Y_AXIS:
	    return fHeight+extra;
	default:
	    throw new IllegalArgumentException("Invalid axis: " + axis);
	}
    }

    /**
     * Determines the desired alignment for this view along an
     * axis.  This is implemented to give the alignment to the
     * bottom of the icon along the y axis, and the default
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
	switch (axis) {
	case View.Y_AXIS:
	    return getVerticalAlignment();
	default:
	    return super.getAlignment(axis);
	}
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
	int p0 = getStartOffset();
	int p1 = getEndOffset();
	if ((pos >= p0) && (pos <= p1)) {
	    Rectangle r = a.getBounds();
	    if (pos == p1) {
		r.x += r.width;
	    }
	    r.width = 0;
	    return r;
	}
	return null;
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
    public int viewToModel(float x, float y, Shape a, Position.Bias[] bias) {
	Rectangle alloc = (Rectangle) a;
	if (x < alloc.x + alloc.width) {
	    bias[0] = Position.Bias.Forward;
	    return getStartOffset();
	}
	bias[0] = Position.Bias.Backward;
	return getEndOffset();
    }

    /**
     * Set the size of the view. (Ignored.)
     *
     * @param width the width
     * @param height the height
     */
    public void setSize(float width, float height) {
    	// Ignore this -- image size is determined by the tag attrs and
    	// the image itself, not the surrounding layout!
    }
    
    /** Change the size of this image. This alters the HEIGHT and WIDTH
    	attributes of the Element and causes a re-layout. */
    protected void resize( int width, int height ) {
    	if( width==fWidth && height==fHeight )
    	    return;
    	
    	fWidth = width;
    	fHeight= height;
    	
    	// Replace attributes in document:
	MutableAttributeSet attr = new SimpleAttributeSet();
	attr.addAttribute(HTML.Attribute.WIDTH ,Integer.toString(width));
	attr.addAttribute(HTML.Attribute.HEIGHT,Integer.toString(height));
	((StyledDocument)getDocument()).setCharacterAttributes(
			fElement.getStartOffset(),
			fElement.getEndOffset(),
			attr, false);
    }
    
    // --- Mouse event handling --------------------------------------------
    
    /** Select or grow image when clicked. */
    public void mousePressed(MouseEvent e){
    	Dimension size = fComponent.getSize();
    	if( e.getX() >= size.width-7 && e.getY() >= size.height-7
    			&& getSelectionState()==2 ) {
    	    // Click in selected grow-box:
    	    if(DEBUG)System.out.println("ImageView: grow!!! Size="+fWidth+"x"+fHeight);
    	    Point loc = fComponent.getLocationOnScreen();
    	    fGrowBase = new Point(loc.x+e.getX() - fWidth,
    	    			  loc.y+e.getY() - fHeight);
    	    fGrowProportionally = e.isShiftDown();
    	} else {
    	    // Else select image:
    	    fGrowBase = null;
    	    JTextComponent comp = (JTextComponent)fContainer;
    	    int start = fElement.getStartOffset();
    	    int end = fElement.getEndOffset();
    	    int mark = comp.getCaret().getMark();
    	    int dot  = comp.getCaret().getDot();
    	    if( e.isShiftDown() ) {
    	    	// extend selection if shift key down:
    	    	if( mark <= start )
    	    	    comp.moveCaretPosition(end);
    	    	else
    	    	    comp.moveCaretPosition(start);
    	    } else {
    	    	// just select image, without shift:
    	    	if( mark!=start )
    	            comp.setCaretPosition(start);
    	        if( dot!=end )
    	            comp.moveCaretPosition(end);
    	    }
    	}
    }
    
    /** Resize image if initial click was in grow-box: */
    public void mouseDragged(MouseEvent e ) {
    	if( fGrowBase != null ) {
    	    Point loc = fComponent.getLocationOnScreen();
    	    int width = Math.max(2, loc.x+e.getX() - fGrowBase.x);
    	    int height= Math.max(2, loc.y+e.getY() - fGrowBase.y);
    	    
    	    if( e.isShiftDown() && fImage!=null ) {
    	    	// Make sure size is proportional to actual image size:
    	    	int imgWidth = fImage.getWidth(this);
    	    	int imgHeight = fImage.getHeight(this);
    	    	if( imgWidth>0 && imgHeight>0 ) {
    	    	    float prop = (float)imgHeight / (float)imgWidth;
    	    	    float pwidth = height / prop;
    	    	    float pheight= width * prop;
    	    	    if( pwidth > width )
    	    	        width = (int) pwidth;
    	    	    else
    	    	        height = (int) pheight;
    	    	}
    	    }
    	    
    	    resize(width,height);
    	}
    }

    public void mouseReleased(MouseEvent e){
    	fGrowBase = null;
    	//! Should post some command to make the action undo-able
    }

    /** On double-click, open image properties dialog. */
    public void mouseClicked(MouseEvent e){
    	if( e.getClickCount() == 2 ) {
    	    //$ IMPLEMENT
    	}
    }

    public void mouseEntered(MouseEvent e){
    }
    public void mouseMoved(MouseEvent e ) {
    }
    public void mouseExited(MouseEvent e){
    }
    

    // --- Static icon accessors -------------------------------------------

    private Icon makeIcon(final String gifFile) throws IOException {
        /* Copy resource into a byte array.  This is
         * necessary because several browsers consider
         * Class.getResource a security risk because it
         * can be used to load additional classes.
         * Class.getResourceAsStream just returns raw
         * bytes, which we can convert to an image.
         */
        InputStream resource;

	//PENDING(prinz): Use new security model
	resource = ImageView.class.getResourceAsStream(gifFile);

        if (resource == null) {
            System.err.println(ImageView.class.getName() + "/" + 
                               gifFile + " not found.");
            return null; 
        }
        BufferedInputStream in = 
            new BufferedInputStream(resource);
        ByteArrayOutputStream out = 
            new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        int n;
        while ((n = in.read(buffer)) > 0) {
            out.write(buffer, 0, n);
        }
        in.close();
        out.flush();

        buffer = out.toByteArray();
        if (buffer.length == 0) {
            System.err.println("warning: " + gifFile + 
                               " is zero-length");
            return null;
        }
        return new ImageIcon(buffer);
    }

    private void loadIcons( ) {
        try{
            if( sPendingImageIcon == null )
            	sPendingImageIcon = makeIcon(PENDING_IMAGE_SRC);
            if( sMissingImageIcon == null )
            	sMissingImageIcon = makeIcon(MISSING_IMAGE_SRC);
	}catch( Exception x ) {
	    System.err.println("ImageView: Couldn't load image icons");
	}
    }
    
    protected StyleSheet getStyleSheet() {
	HTMLDocument doc = (HTMLDocument) getDocument();
	return doc.getStyleSheet();
    }

    // --- member variables ------------------------------------------------

    private AttributeSet attr;
    private Element   fElement;
    private Image     fImage;
    private int       fHeight,fWidth;
    private Container fContainer;
    private Rectangle fBounds;
    private Component fComponent;
    private Point     fGrowBase;        // base of drag while growing image
    private boolean   fGrowProportionally;	// should grow be proportional?
    
    // --- constants and static stuff --------------------------------

    private static Icon sPendingImageIcon,
    			sMissingImageIcon;
    private static final String
        PENDING_IMAGE_SRC = "icons/image-delayed.gif",  // both stolen from HotJava
        MISSING_IMAGE_SRC = "icons/image-failed.gif";
    
    private static final boolean DEBUG = false;
    
    //$ move this someplace public
    static final String IMAGE_CACHE_PROPERTY = "imageCache";
    
    // Height/width to use before we know the real size:
    private static final int
        DEFAULT_WIDTH = 32,
        DEFAULT_HEIGHT= 32,
    // Default value of BORDER param:      //? possibly move into stylesheet?
        DEFAULT_BORDER=  2;

}
