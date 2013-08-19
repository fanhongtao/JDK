/*
 * @(#)XPStyle.java	1.10 03/03/20
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
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
 * @version 1.10 03/20/03
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
		AccessController.doPrivileged(new GetPropertyAction("swing.noxp")) == null) {
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
    synchronized String getString(String key) {
	return (String)map.get(key);
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
		    border = new XPEmptyBorder(m);
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
	private Image image;
	private Insets contentMargin;
	private int w, h;
	private Image scaledImage;
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
	    return contentMargin;
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
	    XPStyle xp = getXP();
	    // Load main image
	    image = xp.getImage(category+".imagefile",
				xp.getBoolean(category+".transparent", true));

	    // Look for additional (prescaled) images
	    int n = 0;
	    while (true) {
		if (xp.getString(category+".imagefile"+(n+1)) == null) {
		    break;
		}
		n++;
	    }
	    if (n > 0) {
		int index = (n / 2) + 1;
		if ("dpi".equalsIgnoreCase(getString(category+".imageselecttype"))) {
		    int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
		    index = 1;
		    for (int i = n; i >= 1; i--) {
			int minDpi = xp.getInt(category+".mindpi"+i, -1);
			if (minDpi > 0 && dpi >= minDpi) {
			    index = i;
			    break;
			}
		    }
		}
		scaledImage = xp.getImage(category+".imagefile"+index,
					  xp.getBoolean(category+".transparent", false) ||
					  xp.getBoolean(category+".glyphtransparent", false)); 
	    }

	    frameCount     = getInt(category+".imagecount", 1);
	    paintMargin    = getMargin(category+".sizingmargins");
	    contentMargin  = getMargin(category+".contentmargins");
	    tile           = "tile".equalsIgnoreCase(getString(category+".sizingtype"));
	    sourceShrink   = getBoolean(category+".sourceshrink", false);
	    verticalFrames = "vertical".equalsIgnoreCase(getString(category+".imagelayout"));
	    glyphImage    = xp.getImage(category+".glyphimagefile",
					xp.getBoolean(category+".glyphtransparent", false));

	    Image im = image;
	    if (im == null && scaledImage != null) {
		im = scaledImage;
	    }
	    if (im != null) {
		// Sanity check
		if (frameCount < 1) {
		    abandonXP();
		}
		this.w = im.getWidth(null)  / (verticalFrames ? 1 : frameCount);
		this.h = im.getHeight(null) / (verticalFrames ? frameCount : 1);
	    }
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
	    if (scaledImage != null) {
		Image im = scaledImage;
		int sw = im.getWidth(null)  / (verticalFrames ? 1 : frameCount);
		int sh = im.getHeight(null) / (verticalFrames ? frameCount : 1);
		int sx = verticalFrames ? 0 : (index*sw);
		int sy = verticalFrames ? (index*sh) : 0;
		dx += (w-sw)/2;
		dy += (h-sh)/2;
		g.drawImage(im, dx, dy, dx+sw,  dy+sh, sx, sy, sx+sw, sy+sh, null);
	    } else if (glyphImage != null) {
		int gsw = glyphImage.getWidth(null)  / (verticalFrames ? 1 : frameCount);
		int gsh = glyphImage.getHeight(null) / (verticalFrames ? frameCount : 1);
		dx += (dw - gsw) / 2;
		dy += (dh - gsh) / 2;

		if (dx >= 0 && dy >= 0) {
		    int gsx = 0, gsy = 0;
		    if (verticalFrames) {
			gsy = index * gsh;
		    } else {
			gsx = index * gsw;
		    }
		    g.drawImage(glyphImage, dx, dy, dx+gsw, dy+gsh, gsx, gsy, gsx+gsw, gsy+gsh, null);
		}
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
					xp.getBoolean(category+".glyphtransparent", true));
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
	     String sizeName     = (String)toolkit.getDesktopProperty("win.xpstyle.sizeName");
	     String colorName    = (String)toolkit.getDesktopProperty("win.xpstyle.colorName");
	     if (sizeName != null && colorName != null) {
		 String[] sizeNames =
		     splitTextResource(getTextResourceByInt(styleFile, 1, "SIZENAMES"));
		 String[] colorNames =
		     splitTextResource(getTextResourceByInt(styleFile, 1, "COLORNAMES"));
		 String[] themeFileNames =
		     splitTextResource(getTextResourceByInt(styleFile, 1, "FILERESNAMES"));

		 if (sizeNames != null && colorNames != null && themeFileNames != null) {
		     themeFile = null;
		     for (int color = 0; color < colorNames.length; color++) {
			 for (int size = 0; size < sizeNames.length; size++) {
			     if (sizeName.equals(sizeNames[size]) &&
				 colorName.equals(colorNames[color]) &&
				 (color * sizeNames.length + size) < themeFileNames.length) {

				 themeFile = themeFileNames[color * sizeNames.length + size];
				 break;
			     }
			 }
		     }

		     if (themeFile != null) {
			 String themeData = getTextResourceByName(styleFile, themeFile, "TEXTFILE");
			 if (themeData != null) {
			     merge(themeData);
			 }
		     }
		 }
	     }
	}
	// Note: All further access to the map must be synchronized
    }


    private static native int[] getBitmapResource(String path, String resource);
    private static native String getTextResourceByName(String path, String resource, String resType);
    private static native String getTextResourceByInt(String path, int resource, String resType);

    private void merge(String bytes) {
	StringTokenizer tok = new StringTokenizer(bytes, "\r\n");
	String category = "";
	while (tok.hasMoreElements()) {
	    String line = tok.nextToken().trim();
	    char[] chars = line.toCharArray();
	    int len = chars.length;
	    if (len > 1) {
		// Modify "[Category]" to "category."
		if (chars[0] == '[') {
		    chars[len-1] = '.';
		    toLowerCase(chars, 1, len-1);
		    category = new String(chars, 1, len-1);
		} else {
		    int i = line.indexOf('=');
		    if (i >= 0) {
			while (i > 0 && (chars[i-1] == ' ' || chars[i-1] == '\t')) {
			    i--;
			}
			toLowerCase(chars, 0, i);
			String key = category + new String(chars, 0, i);
			while (i < len &&
			       (chars[i] == ' ' || chars[i] == '\t' || chars[i] == '=')) {
			    i++;
			}
			String value = new String(chars, i, len-i);
			i = value.indexOf(';');
			if (i >= 0) {
			    value = value.substring(0, i);
			}
			map.put(key, value.trim());
		    }
		}
	    }
	}
    }

    private void toLowerCase(char[] a, int start, int end) {
	for (int i = start; i < end; i++) {
	    a[i] = Character.toLowerCase(a[i]);
	}
    }

    private synchronized Image getImage(String key, boolean useTransparency) {
	Image image = null;
	String imageName = getString(key);
	if (imageName != null) {
	    // We cache images separately because multiple keys/skins
	    // can point to the same image
	    image = (Image)map.get("Image "+imageName);
	    if (image == null) {
		// Replace \ and . with _ and convert to uppercase
		int i;
		String resourceName = imageName;
		while ((i = resourceName.indexOf("\\")) >= 0
		       || (i = resourceName.indexOf(".")) >= 0) {
		    resourceName = resourceName.substring(0, i) + "_" + resourceName.substring(i+1);
		}
		resourceName = resourceName.toUpperCase();
		int[] bits = getBitmapResource(styleFile, resourceName);
		if (bits != null) {
		    // The last two ints in the array are the width and the transparency value
		    int width = bits[bits.length-2];
		    int transparency = useTransparency ? bits[bits.length-1] : Transparency.OPAQUE;
		    int height = (bits.length - 2) / width;

		    GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().
			getDefaultScreenDevice().getDefaultConfiguration();
		    BufferedImage bImage =
			(BufferedImage)gc.createCompatibleImage(width, height, transparency);
					
		    bImage.setRGB(0, 0, width, height, bits, 0, width);
		    image = bImage;
		    map.put("Image "+imageName, image);
		}
	    }
	}
	return image;
    }

    private boolean getBoolean(String key, boolean fallback) {
	String value = getString(key);
	return ((value == null) ? fallback : "true".equalsIgnoreCase(value));
    }


    private void abandonXP() {
	if (AccessController.doPrivileged(new GetPropertyAction("swing.debug")) != null) {
	    System.err.println("An error occured in XPStyle while reading resource "+themeFile + " in " + styleFile);
	    new Exception().printStackTrace();
	}
	xp = null;
    }

    private String[] splitTextResource(String str) {
	if (str == null) {
	    return null;
	}
	StringTokenizer tok = new StringTokenizer(str, "\0");
	String[] array = new String[tok.countTokens()];
	for (int i = 0; tok.hasMoreTokens(); i++) {
	    array[i] = tok.nextToken();
	}
	return array;
    }
}
