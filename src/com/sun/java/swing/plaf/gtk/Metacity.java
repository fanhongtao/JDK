/*
 * @(#)Metacity.java	1.16 03/05/01
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;


import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

/**
 * @version 1.16, 05/01/03
 */
abstract class Metacity implements SynthConstants {
    static Metacity INSTANCE;

    static {
	String themeDir = null;
	String theme = getUserTheme();

	if (theme == null) {
	    theme = "Bluecurve";
	    themeDir = getThemeDir(theme);
	    if (themeDir == null) {
		theme = "Crux";
		themeDir = getThemeDir(theme);
	    }
	    if (themeDir == null) {
		theme = null;
	    }
	}
	if (theme != null && themeDir == null) {
	    themeDir = getThemeDir(theme);
	}
	if (themeDir != null) {
	    if (theme.equals("Bluecurve")) {
		INSTANCE = new MetacityBluecurve(themeDir);
	    } else if (theme.equals("Crux")) {
		INSTANCE = new MetacityCrux(themeDir);
	    }
	}
	if (INSTANCE == null) {
	    INSTANCE = new MetacityCrux(null);
	}
    }


    private FrameGeometry geometry;

    private static LayoutManager titlePaneLayout = new TitlePaneLayout();

    private ColorizeImageFilter imageFilter = new ColorizeImageFilter();
    protected String themeDir = null;
    protected SynthContext context;

    protected Metacity(String themeDir, FrameGeometry geometry) {
	this.themeDir = themeDir;
	this.geometry = geometry;
    }


    public static LayoutManager getTitlePaneLayout() {
	return titlePaneLayout;
    }



    abstract void paintButtonBackground(SynthContext context, Graphics g, int x, int y, int w,int h);
    abstract void paintFrameBorder(SynthContext context, Graphics g, int x0, int y0, int width, int height);
    abstract Insets getBorderInsets(SynthContext context, Insets insets);


    private static String getThemeDir(final String theme) {
	return (String)AccessController.doPrivileged(new PrivilegedAction() {
	    public Object run() {
		String[] dirs = new String[] {
		    "/usr/share/themes/"+theme+"/metacity-1",
		    "/usr/gnome/share/themes/"+theme+"/metacity-1",
		    "/opt/gnome2/share/themes/"+theme+"/metacity-1"
		};

		for (int i = 0; i < dirs.length; i++) {
		    if (new File(dirs[i], "metacity-theme-1.xml").canRead()) {
			return dirs[i];
		    }
		}
		return null;
	    }
	});
    }

    private static String getUserTheme() {
	return (String)AccessController.doPrivileged(new PrivilegedAction() {
	    public Object run() {
		try {
		    String theme = System.getProperty("swing.metacitythemename");
		    if (theme != null) {
			return theme;
		    }
		    String home = System.getProperty("user.home");
		    URL url = new URL("file:"+home+"/.gconf/apps/metacity/general/%25gconf.xml");
		    Reader reader = new InputStreamReader(url.openStream(), "ISO-8859-1");
		    char[] buf = new char[1024];
		    StringBuffer strBuf = new StringBuffer();
		    int n;
		    while ((n = reader.read(buf)) >= 0) {
			strBuf.append(buf, 0, n);
		    }	    
		    reader.close();
		    String str = strBuf.toString();
		    if (str != null) {
			int i = str.toLowerCase().indexOf("<entry name=\"theme\"");
			if (i >= 0) {
			    i = str.toLowerCase().indexOf("<stringvalue>", i);
			    if (i > 0) {
				i += "<stringvalue>".length();
				int i2 = str.indexOf("<", i);
				return str.substring(i, i2);
			    }
			}
		    }
		} catch (Exception ex) {
		    // OK to just ignore. We'll use a fallback theme.
		}
		return null;
	    }
	});
    }

    protected void tileImage(Graphics g, Image image, int x0, int y0, int w, int h, float[] alphas) {
	Graphics2D g2 = (Graphics2D)g;
	Composite oldComp = g2.getComposite();

	int sw = image.getWidth(null);
	int sh = image.getHeight(null);
	int y = y0;
	while (y < y0 + h) {
	    sh = Math.min(sh, y0 + h - y);
	    int x = x0;
	    while (x < x0 + w) {
		float f = (alphas.length - 1.0F) * x / (x0 + w);
		int i = (int)f;
		f -= (int)f;
		float alpha = (1-f) * alphas[i] + f * alphas[i+1];
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		int swm = Math.min(sw, x0 + w - x);
		g.drawImage(image, x, y, x+swm, y+sh, 0, 0, swm, sh, null);
		x += swm;
	    }
	    y += sh;
	}
	g2.setComposite(oldComp);
    }

    protected Color getColor(int state, ColorType type) {
 	return ((GTKStyle)context.getStyle()).getGTKColor(context.getComponent(),
 							  context.getRegion(),
 							  state, type);
    }


    protected static Color shadeColor(Color c, float f) {
	return GTKColorType.adjustColor(c, 1.0F, f, f);
    }



    protected Color blendColor(Color bg, Color fg, float alpha) {
	return new Color((int)(bg.getRed() + ((fg.getRed() - bg.getRed()) * alpha)),
			 (int)(bg.getRed() + ((fg.getRed() - bg.getRed()) * alpha)),
			 (int)(bg.getRed() + ((fg.getRed() - bg.getRed()) * alpha)));
    }


    protected void tintRect(Graphics g, int x, int y, int w, int h, Color c, float alpha) {
	if (g instanceof Graphics2D) {
	    Graphics2D g2 = (Graphics2D)g;
	    Composite oldComp = g2.getComposite();
	    AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
	    g2.setComposite(ac);
	    g2.setColor(c);
	    g2.fillRect(x, y, w, h);
	    g2.setComposite(oldComp);
	}
    }

    protected void drawVerticalGradient(Graphics g, Color color1, Color color2,
				      int x0, int y0, int width, int height, float alpha) {
	if (g instanceof Graphics2D) {
	    Graphics2D g2 = (Graphics2D)g;
	    Composite oldComp = g2.getComposite();
	    AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
	    g2.setComposite(ac);
	    g2.setPaint(new GradientPaint(x0, y0, color1, x0, y0 + height, color2));
	    g2.fillRect(x0, y0, width, height);
	    g2.setComposite(oldComp);
	}
    }

    protected void drawVerticalGradient(Graphics g, Color color1, Color color2,
				      int x0, int y0, int width, int height) {
	if (g instanceof Graphics2D) {
	    Graphics2D g2 = (Graphics2D)g;
	    g2.setPaint(new GradientPaint(x0, y0, color1, x0, y0 + height, color2));
	    g2.fillRect(x0, y0, width, height);
	}
    }

    protected void drawVerticalGradient(Graphics g, Color color1, Color color2, Color color3,
				      int x0, int y0, int width, int height) {
	if (g instanceof Graphics2D) {
	    Graphics2D g2 = (Graphics2D)g;
	    g2.setPaint(new GradientPaint(x0, y0, color1, x0, y0 + height/2, color2));
	    g2.fillRect(x0, y0, width, height/2);
	    g2.setPaint(new GradientPaint(x0, y0 + height/2, color2, x0, y0 + height, color3));
	    g2.fillRect(x0, y0 + height/2, width, height/2);
	}
    }

    protected void drawDiagonalGradient(Graphics g, Color color1, Color color2,
				      int x0, int y0, int width, int height, float alpha) {
	if (g instanceof Graphics2D) {
	    Graphics2D g2 = (Graphics2D)g;
	    Composite oldComp = g2.getComposite();
	    AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
	    g2.setComposite(ac);
	    g2.setPaint(new GradientPaint(x0, y0, color1, x0 + width, y0 + height, color2));
	    g2.fillRect(x0, y0, width, height);
	    g2.setComposite(oldComp);
	}
    }

    protected void drawDiagonalGradient(Graphics g, Color color1, Color color2,
				      int x0, int y0, int width, int height) {
	if (g instanceof Graphics2D) {
	    Graphics2D g2 = (Graphics2D)g;
	    g2.setPaint(new GradientPaint(x0, y0, color1, x0 + width, y0 + height, color2));
	    g2.fillRect(x0, y0, width, height);
	}
    }

    protected Image colorizeImage(Image image, Color c) {
	return imageFilter.colorize(image, c);
    }

    private class ColorizeImageFilter extends RGBImageFilter {
	double cr, cg, cb;

	public ColorizeImageFilter() {
	    canFilterIndexColorModel = true;
	}

	public void setColor(Color color) {
	    cr = color.getRed()   / 255.0;
	    cg = color.getGreen() / 255.0;
	    cb = color.getBlue()  / 255.0;
	}

	public Image colorize(Image fromImage, Color c) {
	    setColor(c);
	    ImageProducer producer = new FilteredImageSource(fromImage.getSource(), this);
	    return new ImageIcon(context.getComponent().createImage(producer)).getImage();
	}

	public int filterRGB(int x, int y, int rgb) {
	    // Assume all rgb values are shades of gray
	    double grayLevel = 2 * (rgb & 0xff) / 255.0;
	    double r, g, b;

	    if (grayLevel <= 1.0) {
		r = cr * grayLevel;
		g = cg * grayLevel;
		b = cb * grayLevel;
            } else {
		grayLevel -= 1.0;
		r = cr + (1.0 - cr) * grayLevel;
		g = cg + (1.0 - cg) * grayLevel;
		b = cb + (1.0 - cb) * grayLevel;
            }

	    return ((rgb & 0xff000000) +
		    (((int)(r * 255)) << 16) +
		    (((int)(g * 255)) << 8) +
		    (int)(b * 255));
	}
    }

    protected static JComponent findChild(JComponent parent, String name) {
	int n = parent.getComponentCount();
	for (int i = 0; i < n; i++) {
	    JComponent c = (JComponent)parent.getComponent(i);
	    if (name.equals(c.getName())) {
		return c;
	    }
	}
	return null;
    }


    protected static class TitlePaneLayout implements LayoutManager {
        public void addLayoutComponent(String name, Component c) {}
        public void removeLayoutComponent(Component c) {}    
        public Dimension preferredLayoutSize(Container c)  {
	    return minimumLayoutSize(c);
	}
    
        public Dimension minimumLayoutSize(Container c) {
	    JComponent titlePane = (JComponent)c;
	    Container titlePaneParent = titlePane.getParent();
	    JInternalFrame frame;
	    if (titlePaneParent instanceof JInternalFrame) {
		frame = (JInternalFrame)titlePaneParent;
	    } else if (titlePaneParent instanceof JInternalFrame.JDesktopIcon) {
		frame = ((JInternalFrame.JDesktopIcon)titlePaneParent).getInternalFrame();
	    } else {
		return null;
	    }

	    FrameGeometry gm = INSTANCE.getFrameGeometry();

            // Calculate width.
            int width = 22;
 
            if (frame.isClosable()) {
                width += 19;
            }
            if (frame.isMaximizable()) {
                width += 19;
            }
            if (frame.isIconifiable()) {
                width += 19;
            }

            FontMetrics fm = titlePane.getFontMetrics(titlePane.getFont());
            String frameTitle = frame.getTitle();
            int title_w = frameTitle != null ? fm.stringWidth(frameTitle) : 0;
            int title_length = frameTitle != null ? frameTitle.length() : 0;

            // Leave room for three characters in the title.
            if (title_length > 3) {
                int subtitle_w = fm.stringWidth(frameTitle.substring(0, 3) + "...");
                width += (title_w < subtitle_w) ? title_w : subtitle_w;
            } else {
                width += title_w;
            }

            // Calculate height.
            Icon icon = frame.getFrameIcon();
            int fontHeight = (fm.getHeight() + gm.title_vertical_pad +
			      gm.title_border.top + gm.title_border.bottom);
            int iconHeight = 0;
            if (icon != null) {
                // SystemMenuBar forces the icon to be 16x16 or less.
                iconHeight = Math.min(icon.getIconHeight(), 16);
            }
            int height = Math.max(fontHeight, iconHeight+2);

            return new Dimension(width, height);
	}
    
        public void layoutContainer(Container c) {
	    JComponent titlePane = (JComponent)c;
	    Container titlePaneParent = titlePane.getParent();
	    JInternalFrame frame;
	    if (titlePaneParent instanceof JInternalFrame) {
		frame = (JInternalFrame)titlePaneParent;
	    } else if (titlePaneParent instanceof JInternalFrame.JDesktopIcon) {
		frame = ((JInternalFrame.JDesktopIcon)titlePaneParent).getInternalFrame();
	    } else {
		return;
	    }
            boolean leftToRight = SynthLookAndFeel.isLeftToRight(frame);
	    FrameGeometry gm = INSTANCE.getFrameGeometry();

            int w = titlePane.getWidth();
            int h = titlePane.getHeight();

	    JComponent menuButton =
		findChild(titlePane, "InternalFrameTitlePane.menuButton");
	    JComponent minimizeButton =
		findChild(titlePane, "InternalFrameTitlePane.iconifyButton");
	    JComponent maximizeButton =
		findChild(titlePane, "InternalFrameTitlePane.maximizeButton");
	    JComponent closeButton =
		findChild(titlePane, "InternalFrameTitlePane.closeButton");

	    int buttonGap = 0;

            int buttonHeight = h - gm.title_border.top - gm.title_border.bottom;
	    int buttonWidth = (int)(buttonHeight / gm.aspect_ratio);

            Icon icon = frame.getFrameIcon();
            int iconHeight = (icon != null) ? icon.getIconHeight() : buttonHeight;

            int x = (leftToRight) ? gm.left_titlebar_edge : w - buttonWidth - gm.right_titlebar_edge;
	    int y = gm.title_border.top;

            menuButton.setBounds(x, y, buttonWidth, buttonHeight);

            x = (leftToRight) ? w - buttonWidth - gm.right_titlebar_edge : gm.left_titlebar_edge;

            if (frame.isClosable()) {
                closeButton.setBounds(x, y, buttonWidth, buttonHeight);
                x += (leftToRight) ? -(buttonWidth + buttonGap) : buttonWidth + buttonGap;
            } 

            if (frame.isMaximizable()) {
                maximizeButton.setBounds(x, y, buttonWidth, buttonHeight);
                x += (leftToRight) ? -(buttonWidth + buttonGap) : buttonWidth + buttonGap;
            }

            if (frame.isIconifiable()) {
                minimizeButton.setBounds(x, y, buttonWidth, buttonHeight);
            } 
        }
    } // end TitlePaneLayout

    protected FrameGeometry getFrameGeometry() {
	return geometry;
    }

    protected void setFrameGeometry(JComponent titlePane, FrameGeometry geometry) {
	this.geometry = geometry;
	if (geometry.top_height == 0) {
	    geometry.top_height = titlePane.getHeight();
	}
    }

    protected static class FrameGeometry {
	int left_width;
	int right_width;
	int top_height;
	int bottom_height;
	int left_titlebar_edge;
	int right_titlebar_edge;
	float aspect_ratio;
	int title_vertical_pad;
	Insets title_border;
	Insets button_border;
    }
}


