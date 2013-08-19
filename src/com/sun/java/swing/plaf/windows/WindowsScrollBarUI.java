/*
 * @(#)WindowsScrollBarUI.java	1.16 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.*;
import java.awt.image.*;
import java.lang.ref.*;
import java.util.*;
import javax.swing.plaf.basic.*;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;



/**
 * Windows rendition of the component.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 */
public class WindowsScrollBarUI extends BasicScrollBarUI {
    private Grid thumbGrid;
    private Grid highlightGrid;

    /**
     * Creates a UI for a JScrollBar.
     *
     * @param c the text field
     * @return the UI
     */
    public static ComponentUI createUI(JComponent c) {
        return new WindowsScrollBarUI();
    }

    protected void installDefaults() {
	super.installDefaults();

	if (XPStyle.getXP() != null) {
	    scrollbar.setBorder(null);
	}
    }

    public Dimension getPreferredSize(JComponent c) {
	Dimension d = super.getPreferredSize(c);

	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
	    if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
		d.width = xp.getInt("sysmetrics.scrollbarwidth", 17);
	    } else {
		d.height = xp.getInt("sysmetrics.scrollbarheight", 17);
	    }
	}
	return d;
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        thumbGrid = highlightGrid = null;
    }

    protected void configureScrollBarColors() {
        super.configureScrollBarColors();
	Color color = UIManager.getColor("ScrollBar.trackForeground");
        if (color != null && trackColor != null) {
            thumbGrid = Grid.getGrid(color, trackColor);
        }

	color = UIManager.getColor("ScrollBar.trackHighlightForeground");
        if (color != null && trackHighlightColor != null) {
            highlightGrid = Grid.getGrid(color, trackHighlightColor);
        }
    }

    protected JButton createDecreaseButton(int orientation)  {
        return new WindowsArrowButton(orientation,
				    UIManager.getColor("ScrollBar.thumb"),
				    UIManager.getColor("ScrollBar.thumbShadow"),
				    UIManager.getColor("ScrollBar.thumbDarkShadow"),
				    UIManager.getColor("ScrollBar.thumbHighlight"));
    }

    protected JButton createIncreaseButton(int orientation)  {
        return new WindowsArrowButton(orientation,
				    UIManager.getColor("ScrollBar.thumb"),
				    UIManager.getColor("ScrollBar.thumbShadow"),
				    UIManager.getColor("ScrollBar.thumbDarkShadow"),
				    UIManager.getColor("ScrollBar.thumbHighlight"));
    }


    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds){
	boolean v = (scrollbar.getOrientation() == JScrollBar.VERTICAL);

	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
	    JScrollBar sb = (JScrollBar)c;
	    int index = 0;
	    // Pending: Implement rollover 1 and pressed 2
	    if (!sb.isEnabled()) {
		index = 3;
	    }
	    String category = v ? "scrollbar.lowertrackvert" : "scrollbar.lowertrackhorz";
	    xp.getSkin(category).paintSkin(g, trackBounds, index);
	} else if (thumbGrid == null) {
            super.paintTrack(g, c, trackBounds);
        }
        else {
            thumbGrid.paint(g, trackBounds.x, trackBounds.y, trackBounds.width,
                            trackBounds.height);
            if (trackHighlight == DECREASE_HIGHLIGHT) {
                paintDecreaseHighlight(g);
            } 
            else if (trackHighlight == INCREASE_HIGHLIGHT) {
                paintIncreaseHighlight(g);
            }
        }
    }

    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
	boolean v = (scrollbar.getOrientation() == JScrollBar.VERTICAL);

	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
	    JScrollBar sb = (JScrollBar)c;
	    int index = 0;
	    // Pending: Implement rollover 1
	    if (!sb.isEnabled()) {
		index = 3;
	    } else if (isDragging) {
		index = 2;
	    }
	    // Paint thumb
	    XPStyle.Skin skin = xp.getSkin(v ? "scrollbar.thumbbtnvert" : "scrollbar.thumbbtnhorz");
	    skin.paintSkin(g, thumbBounds, index);
	    // Paint gripper
	    skin = xp.getSkin(v ? "scrollbar.grippervert" : "scrollbar.gripperhorz");
	    skin.paintSkin(g,
			   thumbBounds.x + (thumbBounds.width  - skin.getWidth()) / 2,
			   thumbBounds.y + (thumbBounds.height - skin.getHeight()) / 2,
			   skin.getWidth(), skin.getHeight(), index);

	} else {
	    super.paintThumb(g, c, thumbBounds);
	}
    }


    protected void paintDecreaseHighlight(Graphics g) {
        if (highlightGrid == null) {
            super.paintDecreaseHighlight(g);
        }
        else {
            Insets insets = scrollbar.getInsets();
            Rectangle thumbR = getThumbBounds();
            int x, y, w, h;

            if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
                x = insets.left;
                y = decrButton.getY() + decrButton.getHeight();
                w = scrollbar.getWidth() - (insets.left + insets.right);
                h = thumbR.y - y;
            } 
            else {
                x = decrButton.getX() + decrButton.getHeight();
                y = insets.top;
                w = thumbR.x - x;
                h = scrollbar.getHeight() - (insets.top + insets.bottom);
            }
            highlightGrid.paint(g, x, y, w, h);
	}
    }      
	

    protected void paintIncreaseHighlight(Graphics g) {
        if (highlightGrid == null) {
            super.paintDecreaseHighlight(g);
        }
        else {
            Insets insets = scrollbar.getInsets();
            Rectangle thumbR = getThumbBounds();
            int x, y, w, h;

            if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
                x = insets.left;
                y = thumbR.y + thumbR.height;
                w = scrollbar.getWidth() - (insets.left + insets.right);
                h = incrButton.getY() - y;
            }
            else {
                x = thumbR.x + thumbR.width;
                y = insets.top;
                w = incrButton.getX() - x;
                h = scrollbar.getHeight() - (insets.top + insets.bottom);
            }
            highlightGrid.paint(g, x, y, w, h);
        }
    }      


    /**
     * WindowsArrowButton is used for the buttons to position the
     * document up/down. It differs from BasicArrowButton in that the
     * preferred size is always a square.
     */
    private class WindowsArrowButton extends BasicArrowButton {

        public WindowsArrowButton(int direction, Color background, Color shadow,
			 Color darkShadow, Color highlight) {
	    super(direction, background, shadow, darkShadow, highlight);
	}

        public WindowsArrowButton(int direction) {
            super(direction);
        }

	public void paint(Graphics g) {
	    XPStyle xp = XPStyle.getXP();
	    if (xp != null) {
		XPStyle.Skin skin = xp.getSkin("scrollbar.arrowbtn");
		int index = 0;
		switch (direction) {
		    case NORTH: index =  0; break;
		    case SOUTH: index =  4; break;
		    case WEST:  index =  8; break;
		    case EAST:  index = 12; break;
		}

		skin.paintSkin(g, 0, 0, getSize().width, getSize().height, index);
	    } else {
		super.paint(g);
	    }
	}

        public Dimension getPreferredSize() {
            int size = 16;
            if (scrollbar != null) {
                switch (scrollbar.getOrientation()) {
                case JScrollBar.VERTICAL:
                    size = scrollbar.getWidth();
                    break;
                case JScrollBar.HORIZONTAL:
                    size = scrollbar.getHeight();
                    break;
                }
                size = Math.max(size, 5);
            }
            return new Dimension(size, size);
        }
    }


    /**
     * This should be pulled out into its own class if more classes need to
     * use it.
     * <p>
     * Grid is used to draw the track for windows scrollbars. Grids
     * are cached in a HashMap, with the key being the rgb components
     * of the foreground/background colors. Further the Grid is held through
     * a WeakRef so that it can be freed when no longer needed. As the
     * Grid is rather expensive to draw, it is drawn in a BufferedImage.
     */
    private static class Grid {
        private static final int BUFFER_SIZE = 64;
        private static HashMap map;

        private BufferedImage image;

        static {
            map = new HashMap();
        }

        public static Grid getGrid(Color fg, Color bg) {
            String key = fg.getRGB() + " " + bg.getRGB();
            WeakReference ref = (WeakReference)map.get(key);
            Grid grid = (ref == null) ? null : (Grid)ref.get();
            if (grid == null) {
                grid = new Grid(fg, bg);
                map.put(key, new WeakReference(grid));
            }
            return grid;
        }

        public Grid(Color fg, Color bg) {
            int cmap[] = { fg.getRGB(), bg.getRGB() };
            IndexColorModel icm = new IndexColorModel(8, 2, cmap, 0, false, -1,
                                                      DataBuffer.TYPE_BYTE);
            image = new BufferedImage(BUFFER_SIZE, BUFFER_SIZE,
                                      BufferedImage.TYPE_BYTE_INDEXED, icm);
            Graphics g = image.getGraphics();
            try {
                g.setClip(0, 0, BUFFER_SIZE, BUFFER_SIZE);
                paintGrid(g, fg, bg);
            }
            finally {
                g.dispose();
            }
        }

        /**
         * Paints the grid into the specified Graphics at the specified
         * location.
         */
        public void paint(Graphics g, int x, int y, int w, int h) {
            Rectangle clipRect = g.getClipBounds();
            int minX = Math.max(x, clipRect.x);
            int minY = Math.max(y, clipRect.y);
            int maxX = Math.min(clipRect.x + clipRect.width, x + w);
            int maxY = Math.min(clipRect.y + clipRect.height, y + h);

            if (maxX <= minX || maxY <= minY) {
                return;
            }
            int xOffset = (minX - x) % 2;
            for (int xCounter = minX; xCounter < maxX;
                 xCounter += BUFFER_SIZE) {
                int yOffset = (minY - y) % 2;
                int width = Math.min(BUFFER_SIZE - xOffset,
                                     maxX - xCounter);

                for (int yCounter = minY; yCounter < maxY;
                     yCounter += BUFFER_SIZE) {
                    int height = Math.min(BUFFER_SIZE - yOffset,
                                          maxY - yCounter);

                    g.drawImage(image, xCounter, yCounter,
                                xCounter + width, yCounter + height,
                                xOffset, yOffset,
                                xOffset + width, yOffset + height, null);
                    if (yOffset != 0) {
                        yCounter -= yOffset;
                        yOffset = 0;
                    }
                }
                if (xOffset != 0) {
                    xCounter -= xOffset;
                    xOffset = 0;
                }
            }
        }

        /**
         * Actually renders the grid into the Graphics <code>g</code>.
         */
        private void paintGrid(Graphics g, Color fg, Color bg) {
            Rectangle clipRect = g.getClipBounds();
            g.setColor(bg);
            g.fillRect(clipRect.x, clipRect.y, clipRect.width,
                       clipRect.height);
            g.setColor(fg);
            g.translate(clipRect.x, clipRect.y);
            int width = clipRect.width;
            int height = clipRect.height;
            int xCounter = clipRect.x % 2;
            for (int end = width - height; xCounter < end; xCounter += 2) {
                g.drawLine(xCounter, 0, xCounter + height, height);
            }
            for (int end = width; xCounter < end; xCounter += 2) {
                g.drawLine(xCounter, 0, width, width - xCounter);
            }

            int yCounter = ((clipRect.x % 2) == 0) ? 2 : 1;
            for (int end = height - width; yCounter < end; yCounter += 2) {
                g.drawLine(0, yCounter, width, yCounter + width);
            }
            for (int end = height; yCounter < end; yCounter += 2) {
                g.drawLine(0, yCounter, height - yCounter, height);
            }
            g.translate(-clipRect.x, -clipRect.y);
        }
    }
}
