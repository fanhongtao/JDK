/*
 * @(#)XPStyle.java	1.19 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * <p>These classes are designed to be used while the
 * corresponding <code>LookAndFeel</code> class has been installed
 * (<code>UIManager.setLookAndFeel(new <i>XXX</i>LookAndFeel())</code>).
 * Using them while a different <code>LookAndFeel</code> is installed
 * may produce unexpected results, including exceptions.
 * Additionally, changing the <code>LookAndFeel</code>
 * maintained by the <code>UIManager</code> without updating the
 * corresponding <code>ComponentUI</code> of any
 * <code>JComponent</code>s may also produce unexpected results,
 * such as the wrong colors showing up, and is generally not
 * encouraged.
 * 
 */

package com.sun.java.swing.plaf.windows;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.security.AccessController;
import java.util.*;
import java.util.prefs.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.text.JTextComponent;

import sun.security.action.GetPropertyAction;

/**
 * Implements Windows XP Styles for the Windows Look and Feel.
 *
 * @version 1.19 12/19/03
 * @author Leif Samuelsson
 */
class XPStyle {
    // Singleton instance of this class
    private static XPStyle xp;

    private static Boolean themeActive = null;

    private HashMap map;
    private String styleFile;
    private String themeFile;

    static {
	invalidateStyle();
    }

    /** Static method for clearing the hashmap and loading the
     * current XP style and theme
     */
    static synchronized void invalidateStyle() {
	xp = null;
	themeActive = null;
    }

    /** Get the singleton instance of this class
     *
     * @return the signleton instance of this class or null if XP styles
     * are not active or if this is not Windows XP
     */
    static synchronized XPStyle getXP() {
	if (themeActive == null) {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
	    themeActive = (Boolean)toolkit.getDesktopProperty("win.xpstyle.themeActive");
	    if (themeActive == null) {
		themeActive = Boolean.FALSE;
	    }
   	    if (themeActive.booleanValue() &&
		AccessController.doPrivileged(new GetPropertyAction("swing.noxp")) == null &&
		!(UIManager.getLookAndFeel() instanceof WindowsClassicLookAndFeel)) {
		xp = new XPStyle();
	    }
	}
	return xp;
    }

    /** Get a named <code>String</code> value from the current style
     *
     * @param key a <code>String</code>
     * @return a <code>String</code> or null if key is not found
     *    in the current style
     */
    private static String getString(String key) {
	// Example: getString("a.b(c).d") => return getString("a.b", "c", "d");

	String category = "";
	String state = "";
	String attributeKey = key;

	int i = key.lastIndexOf('.');
	if (i > 0 && key.length() > i+1) {
	    category = key.substring(0, i);
	    attributeKey = key.substring(i+1);

	    i = category.lastIndexOf('(');
	    if (i > 0) {
		int i2 = category.indexOf(')', i);
		if (i2 == category.length() - 1) {
		    state = category.substring(i+1, i2);
		    category = category.substring(0, i);
		}
	    }
	}
	return getString(category, state, attributeKey);
    }

    /** Get a named <code>String</code> value from the current style
     *
     * @param category a <code>String</code>
     * @param state a <code>String</code>
     * @param attributeKey a <code>String</code>
     * @return a <code>String</code> or null if key is not found
     *    in the current style
     */
    static String getString(String category, String state, String attributeKey) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Map resources = (Map)toolkit.getDesktopProperty("win.xpstyle.resources.strings");

	String value;
	if (category == null || category.length() == 0) {
	    value = (String)resources.get(attributeKey);
	} else {
	    if (state == null || state.length() == 0) {
		value = (String)resources.get(category + "." + attributeKey);
	    } else {
		value = (String)resources.get(category + "(" + state + ")." + attributeKey);
	    }
	}
	if (value == null) {
	    // Look for inheritance. For example, the attributeKey "transparent" in
	    // category "window.closebutton" can be inherited from category "window".
	    int i = category.lastIndexOf('.');
	    if (i > 0) {
		value = getString(category.substring(0, i), state, attributeKey);
	    }
	}
	if (value == null && state != null && state.length() > 0) {
	    // Try again without a state specified
	    value = getString(category, "", attributeKey);
	}
	return value;
    }

    static BufferedImage getBitmapResource(String key) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Map resources = (Map)toolkit.getDesktopProperty("win.xpstyle.resources.images");
	return (BufferedImage)resources.get(key);
    }


    /** Get a named <code>int</code> value from the current style
     *
     * @param key a <code>String</code>
     * @return an <code>int</code> or null if key is not found
     *    in the current style
     */
    int getInt(String key, int fallback) {
	return parseInt(getString(key), fallback);
    }

    private int parseInt(String str, int fallback) {
	if (str != null) {
	    try {
		return Integer.parseInt(str);
	    } catch (NumberFormatException nfe) {
		abandonXP();
	    }
	}
	return fallback;
    }

    /** Get a named <code>Dimension</code> value from the current style
     *
     * @param key a <code>String</code>
     * @return a <code>Dimension</code> or null if key is not found
     *    in the current style
     */
    synchronized Dimension getDimension(String key) {
	Dimension d = (Dimension)map.get("Dimension "+key);
	if (d == null) {
	    String str = getString(key);
	    if (str != null) {
		StringTokenizer tok = new StringTokenizer(str, " \t,");
		d = new Dimension(parseInt(tok.nextToken(), 0),
				  parseInt(tok.nextToken(), 0));
		map.put("Dimension "+key, d);
	    }
	}
	return d;
    }

    /** Get a named <code>Point</code> (e.g. a location or an offset) value
     *  from the current style
     *
     * @param key a <code>String</code>
     * @return a <code>Point</code> or null if key is not found
     *    in the current style
     */
    synchronized Point getPoint(String key) {
	Point p = (Point)map.get("Point "+key);
	if (p == null) {
	    String str = getString(key);
	    if (str != null) {
		StringTokenizer tok = new StringTokenizer(str, " \t,");
		p = new Point(parseInt(tok.nextToken(), 0),
			      parseInt(tok.nextToken(), 0));
		map.put("Point "+key, p);
	    }
	}
	return p;
    }

    /** Get a named <code>Insets</code> value from the current style
     *
     * @param key a <code>String</code>
     * @return an <code>Insets</code> object or null if key is not found
     *    in the current style
     */
    synchronized Insets getMargin(String key) {
	Insets insets = (Insets)map.get("Margin "+key);
	if (insets == null) {
	    String str = getString(key);
	    if (str != null) {
		StringTokenizer tok = new StringTokenizer(str, " \t,");
		insets = new Insets(0, 0, 0, 0);
		insets.left   = parseInt(tok.nextToken(), 0);
		insets.right  = parseInt(tok.nextToken(), 0);
		insets.top    = parseInt(tok.nextToken(), 0);
		insets.bottom = parseInt(tok.nextToken(), 0);
		map.put("Margin "+key, insets);
	    }
	}
	return insets;
    }

    /** Get a named <code>Color</code> value from the current style
     *
     * @param key a <code>String</code>
     * @return a <code>Color</code> or null if key is not found
     *    in the current style
     */
    synchronized Color getColor(String key, Color fallback) {
	Color color = (Color)map.get("Color "+key);
	if (color == null) {
	    String str = getString(key);
	    if (str != null) {
		StringTokenizer tok = new StringTokenizer(str, " \t,");
		int r = parseInt(tok.nextToken(), 0);
		int g = parseInt(tok.nextToken(), 0);
		int b = parseInt(tok.nextToken(), 0);
		if (r >= 0 && g >=0 && b >= 0) {
		    color = new Color(r, g, b);
		    map.put("Color "+key, color);
		}
	    }
	}
	return (color != null) ? color : fallback;
    }


    /** Get a named <code>Border</code> value from the current style
     *
     * @param key a <code>String</code>
     * @return a <code>Border</code> or null if key is not found
     *    in the current style or if the style for the particular
     *    category is not defined as "borderfill".
     */
    synchronized Border getBorder(String category) {
	if (category == "menu") {
	    // Special case because XP has no skin for menus
	    if (getBoolean("sysmetrics.flatmenus") == Boolean.TRUE) {
		// TODO: The classic border uses this color, but we should create
		// a new UI property called "PopupMenu.borderColor" instead.
		return new XPFillBorder(UIManager.getColor("InternalFrame.borderShadow"), 1);
	    } else {
		return null;	// Will cause L&F to use classic border
	    }
	}
	Border border = (Border)map.get("Border "+category);
	if (border == null) {
	    String bgType = getString(category + ".bgtype");
	    if ("borderfill".equalsIgnoreCase(bgType)) {
		int thickness = getInt(category + ".bordersize", 1);
		Color color = getColor(category + ".bordercolor", Color.black);
		border = new XPFillBorder(color, thickness);
	    } else if ("imagefile".equalsIgnoreCase(bgType)) {
		Insets m = getMargin(category + ".sizingmargins");
		if (m != null) {
		    if (getBoolean(category + ".borderonly") == Boolean.TRUE) {
			border = new XPImageBorder(category);
		    } else {
			border = new XPEmptyBorder(m);
		    }
		}
	    }
	    if (border != null) {
		map.put("Border "+category, border);
	    }
	}
	return border;
    }

    private class XPFillBorder extends LineBorder implements UIResource {
	XPFillBorder(Color color, int thickness) {
	    super(color, thickness);
	}

        public Insets getBorderInsets(Component c)       {
	    return getBorderInsets(c, new Insets(0,0,0,0));
        }

        public Insets getBorderInsets(Component c, Insets insets)       {
            Insets margin = null;
            //
            // Ideally we'd have an interface defined for classes which
            // support margins (to avoid this hackery), but we've
            // decided against it for simplicity
            //
           if (c instanceof AbstractButton) {
               margin = ((AbstractButton)c).getMargin();
           } else if (c instanceof JToolBar) {
               margin = ((JToolBar)c).getMargin();
           } else if (c instanceof JTextComponent) {
               margin = ((JTextComponent)c).getMargin();
           }
	   insets.top    = (margin != null? margin.top : 0)    + thickness;
	   insets.left   = (margin != null? margin.left : 0)   + thickness;
	   insets.bottom = (margin != null? margin.bottom : 0) + thickness;
	   insets.right =  (margin != null? margin.right : 0)  + thickness;
	       
	   return insets;
        }
    }

    private class XPImageBorder extends AbstractBorder implements UIResource {
	Skin skin;

	XPImageBorder(String category) {
	    this.skin = getSkin(category);
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
	    skin.paintSkin(g, x, y, width, height, 0);
	}

        public Insets getBorderInsets(Component c)       {
	    return getBorderInsets(c, new Insets(0,0,0,0));
        }

        public Insets getBorderInsets(Component c, Insets insets)       {
            Insets margin = null;
	    Insets borderInsets = skin.getContentMargin();
            //
            // Ideally we'd have an interface defined for classes which
            // support margins (to avoid this hackery), but we've
            // decided against it for simplicity
            //
           if (c instanceof AbstractButton) {
               margin = ((AbstractButton)c).getMargin();
           } else if (c instanceof JToolBar) {
               margin = ((JToolBar)c).getMargin();
           } else if (c instanceof JTextComponent) {
               margin = ((JTextComponent)c).getMargin();
           }
	   insets.top    = (margin != null? margin.top : 0)    + borderInsets.top;
	   insets.left   = (margin != null? margin.left : 0)   + borderInsets.left;
	   insets.bottom = (margin != null? margin.bottom : 0) + borderInsets.bottom;
	   insets.right =  (margin != null? margin.right : 0)  + borderInsets.right;
	       
	   return insets;
        }
    }

    private class XPEmptyBorder extends EmptyBorder implements UIResource {
	XPEmptyBorder(Insets m) {
	    super(m.top+2, m.left+2, m.bottom+2, m.right+2);
	}

        public Insets getBorderInsets(Component c)       {
	    return getBorderInsets(c, getBorderInsets());
        }

        public Insets getBorderInsets(Component c, Insets insets)       {
	    Insets margin = null;
	    if (c instanceof AbstractButton) {
		margin = ((AbstractButton)c).getMargin();
	    } else if (c instanceof JToolBar) {
		margin = ((JToolBar)c).getMargin();
	    } else if (c instanceof JTextComponent) {
		margin = ((JTextComponent)c).getMargin();
	    }
	    if (margin != null) {
		insets.top    = margin.top + 2;
		insets.left   = margin.left + 2;
		insets.bottom = margin.bottom + 2;
		insets.right  = margin.right + 2;
	    }
	    return insets;
        }
    }


    /** Get an <code>XPStyle.Skin</code> object from the current style
     * for a named category (component type)
     *
     * @param category a <code>String</code>
     * @return an <code>XPStyle.Skin</code> object or null if the category is
     * not found in the current style
     */
    synchronized Skin getSkin(String category) {
	Skin skin = (Skin)map.get("Skin "+category);
	if (skin == null) {
	    skin = new Skin(category);
	    map.put("Skin "+category, skin);
	}
	return skin;
    }

    /** A class which encapsulates attributes for a given category
     * (component type) and which provides methods for painting backgrounds
     * and glyphs
     */
    class Skin {
	private String category;
	private Image image;
	private Insets contentMargin;
	private int w, h;
	private String imageSelectType;
	private Image scaledImage;
	private int nScaledImages;
	private int scaledToWidth  = 0;
	private int scaledToHeight = 0;
	private Image glyphImage;
	private int frameCount;
	private Insets paintMargin;
	private boolean tile;
	private boolean sourceShrink;
	private boolean verticalFrames;

	/** The background image for the skin.
	 *  If this is null then width and height are not valid.
	 */
	Image getImage() {
	    if (image != null) {
		return image;
	    } else if (scaledImage != null) {
		return scaledImage;
	    } else {
		return null;
	    }
	}

	/** The content margin for a component skin is useful in
	 * determining the minimum and preferred sizes for a component.
	 */
	Insets getContentMargin() {
	    // This is only called by WindowsTableHeaderUI so far.
	    return paintMargin;
	}

	/** The width of the source image for this skin.
	 *  Not valid if getImage() returns null
	 */
	int getWidth() {
	    return w;
	}

	/** The height of the source image for this skin.
	 *  Not valid if getImage() returns null
	 */
	int getHeight() {
	    return h;
	}

	private Skin(String category) {
	    this.category = category;
	    XPStyle xp = getXP();

	    // Load main image
	    image = xp.getImage(category+".imagefile",
				getBoolean(category+".transparent"),
				xp.getColor(category+".transparentcolor", null));

	    // Look for additional (prescaled) images
	    nScaledImages = 0;
	    while (true) {
		if (xp.getString(category+".imagefile"+(nScaledImages+1)) == null) {
		    break;
		}
		nScaledImages++;
	    }
	    if (nScaledImages > 0) {
		int index = (nScaledImages / 2) + 1;
		imageSelectType = getString(category+".imageselecttype").toLowerCase();
		if ("dpi".equals(imageSelectType)) {
		    int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
		    index = 1;
		    for (int i = nScaledImages; i >= 1; i--) {
			int minDpi = xp.getInt(category+".mindpi"+i, -1);
			if (minDpi > 0 && dpi >= minDpi) {
			    index = i;
			    break;
			}
		    }
		}
		scaledImage = getScaledImage(index);
	    }

	    frameCount     = getInt(category+".imagecount", 1);
	    paintMargin    = getMargin(category+".sizingmargins");
	    contentMargin  = getMargin(category+".contentmargins");
	    tile           = "tile".equalsIgnoreCase(getString(category+".sizingtype"));
	    sourceShrink   = (getBoolean(category+".sourceshrink") == Boolean.TRUE);
	    verticalFrames = "vertical".equalsIgnoreCase(getString(category+".imagelayout"));
	    glyphImage    = xp.getImage(category+".glyphimagefile",
					xp.getBoolean(category+".glyphtransparent"),
					null);

	    Image im = image;
	    if (im == null && scaledImage != null) {
		im = scaledImage;
	    }
	    if (im != null) {
		// Sanity check
		if (frameCount < 1) {
		    abandonXP();
		}
		this.w = getImageWidth(im);
		this.h = getImageHeight(im);
	    }
	}

	private int getImageWidth(Image im) {
	    return im.getWidth(null)  / (verticalFrames ? 1 : frameCount);
	}

	private int getImageHeight(Image im) {
	    return im.getHeight(null) / (verticalFrames ? frameCount : 1);
	}

	private Image getScaledImage(int i) {
	    Boolean useTransparency = xp.getBoolean(category+".transparent");
	    if (useTransparency == null) {
		useTransparency = xp.getBoolean(category+".glyphtransparent");
	    }
	    return xp.getImage(category+".imagefile"+i,
			       useTransparency,
			       xp.getColor(category+".transparentcolor", null));
	}

	private Image getScaledImage(int dw, int dh) {
	    if ("size".equals(imageSelectType) && nScaledImages > 1
		&& (scaledToWidth != dw || scaledToHeight != dh)) {
		scaledImage = getScaledImage(1);
		for (int i = 2; i <= nScaledImages; i++) {
		    Image im = getScaledImage(i);
		    int w = dw;
		    int h = dh;
		    if (contentMargin != null) {
			w -= (contentMargin.left + contentMargin.right);
			h -= (contentMargin.top + contentMargin.bottom);
		    }
		    if (getImageWidth(im) <= w && getImageHeight(im) <= h) {
			scaledImage = im;
		    } else {
			break;
		    }
		}
		scaledToWidth  = dw;
		scaledToHeight = dh;
	    }
	    return scaledImage;
	}

	/** Paint a skin at x, y.
	 *
	 * @param g   the graphics context to use for painting
	 * @param dx  the destination <i>x</i> coordinate.
	 * @param dy  the destination <i>y</i> coordinate.
	 * @param index which subimage to paint (usually depends on component state)
	 */
	void paintSkin(Graphics g, int dx, int dy, int index) {
	    paintSkin(g, dx, dy, w, h, index);
	}

	/** Paint a skin in an area defined by a rectangle.
	 *
	 * @param g the graphics context to use for painting
	 * @param r     a <code>Rectangle</code> defining the area to fill, may cause
	 *                     the image to be stretched or tiled
	 * @param index which subimage to paint (usually depends on component state)
	 */
	void paintSkin(Graphics g, Rectangle r, int index) {
	    paintSkin(g, r.x, r.y, r.width, r.height, index);
	}

	/** Paint a skin at a defined position and size
	 *
	 * @param g   the graphics context to use for painting
	 * @param dx  the destination <i>x</i> coordinate.
	 * @param dy  the destination <i>y</i> coordinate.
	 * @param dw  the width of the area to fill, may cause
	 *                  the image to be stretched or tiled
	 * @param dh  the height of the area to fill, may cause
	 *                  the image to be stretched or tiled
	 * @param index which subimage to paint (usually depends on component state)
	 */
	void paintSkin(Graphics g, int dx, int dy, int dw, int dh, int index) {
	    // Sanity check
	    if ((image != null || scaledImage != null || glyphImage != null)
		&& (index < 0 || index >= frameCount)) {
		abandonXP();
	    }
	    if (image != null) {
		int sy = 0;
		if (h - ((paintMargin != null) ? (paintMargin.top+paintMargin.bottom) : 0) > dh
		    && tile && !sourceShrink) {
		    // Special case for vertical progress bar where the source image is
		    // higher than the chunk size and the bottom of the image needs to
		    // be painted instead of the top.
		    sy = h - dh;
		}
		paint9(g, image, dx, dy, dw, dh,
		       verticalFrames ? 0 : (index*w),
		       (verticalFrames ? (index*h) : 0) + sy,
		       w, h, paintMargin, tile, sourceShrink);
	    }
	    // Paint glyph on top of background image
	    Image im = getScaledImage(dw, dh);
	    if (im == null) {
		im = glyphImage;
	    }
	    if (im != null) {
		// The glyph is pre-scaled, so don't stretch it here
		int sw = getImageWidth(im);
		int sh = getImageHeight(im);
		int sx = verticalFrames ? 0 : (index*sw);
		int sy = verticalFrames ? (index*sh) : 0;
		dx += (dw-sw)/2;
		dy += (dh-sh)/2;
		g.drawImage(im, dx, dy, dx+sw,  dy+sh, sx, sy, sx+sw, sy+sh, null);
	    }
	}

	private void paint9(Graphics g, Image im,
		    int dx, int dy, int dw, int dh,
		    int sx, int sy, int sw, int sh,
		    Insets margin, boolean tile, boolean sourceShrink) {

	    int th, bh, lw, rw;

	    if (margin != null) {
		th = margin.top;
		bh = margin.bottom;
		lw = margin.left;
		rw = margin.right;
	    } else {
		th = bh = lw = rw = 0;
	    }

	    if (tile) {
		// mid middle, left, right
		paintTile(g, im,    dx+lw,    dy+th, dw-lw-rw, dh-th-bh,    sx+lw,    sy+th, sw-lw-rw, sh-th-bh, sourceShrink);
		paintTile(g, im,       dx,    dy+th,       lw, dh-th-bh,       sx,    sy+th,       lw, sh-th-bh, sourceShrink);
		paintTile(g, im, dx+dw-rw,    dy+th,       rw, dh-th-bh, sx+sw-rw,    sy+th,       rw, sh-th-bh, sourceShrink);
		// top middle
		paintTile(g, im,    dx+lw,       dy, dw-lw-rw,       th,    sx+lw,       sy, sw-lw-rw,       th, sourceShrink);
		// bottom middle
		paintTile(g, im,    dx+lw, dy+dh-bh, dw-lw-rw,       bh,    sx+lw, sy+sh-bh, sw-lw-rw,       bh, sourceShrink);
	    } else {
		// mid middle, left, right
		g.drawImage(im,    dx+lw,    dy+th, dx+dw-rw, dy+dh-bh,    sx+lw,    sy+th, sx+sw-rw, sy+sh-bh, null);
		g.drawImage(im,       dx,    dy+th,    dx+lw, dy+dh-bh,       sx,    sy+th,    sx+lw, sy+sh-bh, null);
		g.drawImage(im, dx+dw-rw,    dy+th,    dx+dw, dy+dh-bh, sx+sw-rw,    sy+th,    sx+sw, sy+sh-bh, null);
		// top middle
		g.drawImage(im,    dx+lw,       dy, dx+dw-rw,    dy+th,    sx+lw,       sy, sx+sw-rw,    sy+th, null);
		// bottom middle
		g.drawImage(im,    dx+lw, dy+dh-bh, dx+dw-rw,    dy+dh,    sx+lw, sy+sh-bh, sx+sw-rw,    sy+sh, null);
	    }
	    // top left, right
	    g.drawImage(im,       dx,       dy,    dx+lw,    dy+th,       sx,       sy,    sx+lw,    sy+th, null);
	    g.drawImage(im, dx+dw-rw,       dy,    dx+dw,    dy+th, sx+sw-rw,       sy,    sx+sw,    sy+th, null);
	    // bottom left, right
	    g.drawImage(im,       dx, dy+dh-bh,    dx+lw,    dy+dh,       sx, sy+sh-bh,    sx+lw,    sy+sh, null);
	    g.drawImage(im, dx+dw-rw, dy+dh-bh,    dx+dw,    dy+dh, sx+sw-rw, sy+sh-bh,    sx+sw,    sy+sh, null);
	}

	private void paintTile(Graphics g, Image im,
			       int dx, int dy, int dw, int dh,
			       int sx, int sy, int sw, int sh,
			       boolean sourceShrink) {

	    if (dw <= 0 || dh <= 0 || sw <= 0 || sh <= 0) {
		return;
	    }

	    if (sourceShrink && (sw > dw || sh > dh)) {
		if (sw > dw && sh > dh) {
		    // shrink width and height
		    g.drawImage(im, dx, dy, dx+dw, dy+dh, sx, sy, sx+sw, sy+sh, null);
		} else if (sh > dh) {
		    // tile width, shrink height
		    int x = dx;
		    BufferedImage bImage = new BufferedImage(sw, dh, BufferedImage.TYPE_INT_ARGB);
		    Graphics bg = bImage.getGraphics();
		    bg.drawImage(im, 0, 0, sw, dh, sx, sy, sx+sw, sy+sh, null);
		    while (x < dx + dw) {
			int swm = Math.min(sw, dx + dw - x);
			g.drawImage(bImage, x, dy, x+swm, dy+dh, 0, 0, swm, dh, null);
			x += swm;
		    }
		    bg.dispose();
		} else {
		    // shrink width, tile height
		    int y = dy;
		    BufferedImage bImage = new BufferedImage(dw, sh, BufferedImage.TYPE_INT_ARGB);
		    Graphics bg = bImage.getGraphics();
		    bg.drawImage(im, 0, 0, dw, sh, sx, sy, sx+sw, sy+sh, null);
		    while (y < dy + dh) {
			sh = Math.min(sh, dy + dh - y);
			g.drawImage(bImage, dx, y, dx+dw, y+sh, 0, 0, dw, sh, null);
			y += sh;
		    }
		    bg.dispose();
		}
	    } else {
		// tile width and height
		int y = dy;
		while (y < dy + dh) {
		    sh = Math.min(sh, dy + dh - y);
		    int x = dx;
		    while (x < dx + dw) {
			int swm = Math.min(sw, dx + dw - x);
			g.drawImage(im, x, y, x+swm, y+sh, sx, sy, sx+swm, sy+sh, null);
			x += swm;
		    }
		    y += sh;
		}
	    }
	}

    }

    static class GlyphButton extends JButton {
	private Skin skin;
	private Image glyphImage;
	private boolean vertical;

        public GlyphButton(String category) {
	    //setRolloverEnabled(true);
	    XPStyle xp = getXP();
	    skin          = xp.getSkin(category);
	    glyphImage    = xp.getImage(category+".glyphimagefile",
					xp.getBoolean(category+".glyphtransparent"),
					null);
	    setBorder(null);
	    setContentAreaFilled(false);
	}   

    	public boolean isFocusTraversable() {
	    return false;
	}

	public void paintComponent(Graphics g) {
	    int index = 0;
	    if (!isEnabled()) {
		index = 3;
	    } else if (getModel().isPressed()) {
		index = 2;
	    } else if (getModel().isRollover()) {
		index = 1;
	    }
	    Dimension d = getSize();
	    skin.paintSkin(g, 0, 0, d.width, d.height, index);
        }

	protected void paintBorder(Graphics g) {    
	}

        public Dimension getPreferredSize() {
            return new Dimension(16, 16);
        }

        public Dimension getMinimumSize() {
            return new Dimension(5, 5);
        }

        public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
    }

    // Private constructor
    private XPStyle() {
	map = new HashMap();

	Toolkit toolkit = Toolkit.getDefaultToolkit();
	styleFile = (String)toolkit.getDesktopProperty("win.xpstyle.dllName");
	if (styleFile != null) {
            themeFile = getString("themeFile");
	}
	// Note: All further access to the map must be synchronized
    }

    private synchronized Image getImage(String key,
					Boolean useTransparency,
					Color transparentColor) {
	String imageName = getString(key);
	if (imageName == null) {
            return null;
        }

        String imageKey = "Image " + imageName;
	BufferedImage image = (BufferedImage)map.get(imageKey);
         
	if (image != null) {
            return image;
        }
	
	image = getBitmapResource(imageName);

	if (image == null) {
            return null;
        }

	int oldTransparency = image.getColorModel().getTransparency();

	// Transparency can be forced to false for 32-bit images and forced
	// to true for 8/24-bit images. Do nothing if useTransparency is null.
        if (useTransparency == Boolean.FALSE && oldTransparency != Transparency.OPAQUE) {
            image = convertToOpaque(image);
        } else if (useTransparency == Boolean.TRUE && oldTransparency == Transparency.OPAQUE) {
	    // Assume we only use images from 8/24-bit bitmaps here
	    image = convertToTransparent(image, transparentColor);
	}
        // We cache images separately because multiple keys/skins
        // can point to the same image
        map.put(imageKey, image);
	return image;
    }

    private BufferedImage convertToOpaque(BufferedImage src) {
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().
            getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage dest = (BufferedImage)gc.
            createCompatibleImage(src.getWidth(), src.getHeight(), Transparency.OPAQUE);
        ColorConvertOp op = new ColorConvertOp(src.getColorModel().getColorSpace(),
            dest.getColorModel().getColorSpace(), null);
        op.filter(src, dest);
        return dest;
    }

    private BufferedImage convertToTransparent(BufferedImage src, Color transparentColor) {
	int w = src.getWidth();
	int h = src.getHeight();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage dest = gc.createCompatibleImage(w, h, Transparency.BITMASK);
	int trgb;
	if (transparentColor != null) {
	    trgb = transparentColor.getRGB() | 0xff000000;
	} else {
	    trgb = 0xffff00ff;
	}
	// Copy pixels a scan line at a time
	int buf[] = new int[w];
        for (int y = 0; y < h; y++) {
	    src.getRGB(0, y, w, 1, buf, 0, w);
            for (int x = 0; x < w; x++) {
		if (buf[x] == trgb) {
		    buf[x] = 0;
		}
            }
	    dest.setRGB(0, y, w, 1, buf, 0, w);
	}
	return dest;
    }

    private Boolean getBoolean(String key) {
	String value = getString(key);
	return (value != null) ? Boolean.valueOf("true".equalsIgnoreCase(value)) : null;
    }


    private void abandonXP() {
	if (AccessController.doPrivileged(new GetPropertyAction("swing.debug")) != null) {
	    System.err.println("An error occured in XPStyle while reading resource "+themeFile + " in " + styleFile);
	    new Exception().printStackTrace();
	}
	xp = null;
    }
}
