/*
 * @(#)ImagePainter.java	1.5 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import java.awt.*;
import javax.swing.*;

/**
 * ImagePainter fills in the specified region using an Image. The Image
 * is split into 9 segments: north, north east, east, south east, south,
 * south west, west, north west and the center. The corners are defined
 * by way of an insets, and the remaining regions are either tiled or
 * scaled to fit.
 *
 * @version 1.5, 01/23/03
 * @author Scott Violet
 */
class ImagePainter extends SynthPainter {
    private Image image;
    private Insets sInsets;
    private Insets dInsets;
    private String path;
    private boolean tiles;
    private boolean paintCenter;
    private Object renderingHint;

    ImagePainter(boolean tiles, boolean paintCenter, Object renderingHint,
                 Insets sourceInsets, Insets destinationInsets) {
        this.sInsets = (Insets)sourceInsets.clone();
        if (destinationInsets == null) {
            dInsets = sInsets;
        }
        else {
            this.dInsets = (Insets)destinationInsets.clone();
        }
        this.tiles = tiles;
        this.paintCenter = paintCenter;
        this.renderingHint = renderingHint;
    }

    public ImagePainter(boolean tiles, boolean paintCenter,
                        Object renderingHint, Insets sourceInsets,
                        Insets destinationInsets, Image image) {
        this(tiles, paintCenter, renderingHint, sourceInsets,
             destinationInsets);
        this.image = image;
    }

    public ImagePainter(boolean tiles, boolean paintCenter,
                        Object renderingHint, Insets sourceInsets,
                        Insets destinationInsets, String path) {
        this(tiles, paintCenter, renderingHint, sourceInsets,
             destinationInsets);
        this.path = path;
    }

    public boolean getTiles() {
        return tiles;
    }

    public boolean getPaintsCenter() {
        return paintCenter;
    }

    public Object getRenderingHint() {
        return renderingHint;
    }

    public Insets getInsets(Insets insets) {
        if (insets == null) {
            return (Insets)this.dInsets.clone();
        }
        insets.left = this.dInsets.left;
        insets.right = this.dInsets.right;
        insets.top = this.dInsets.top;
        insets.bottom = this.dInsets.bottom;
        return insets;
    }

    public Image getImage() {
        if (image == null) {
            image = new ImageIcon(path).getImage();
        }
        return image;
    }

    public void paint(SynthContext state, Object paintKey,
                      Graphics g, int x, int y, int w, int h) {
        Image image;
        Object lastHint;
        Object renderingHint = getRenderingHint();


        if (renderingHint != null) {
            Graphics2D g2 = (Graphics2D)g;

            lastHint = g2.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
            if (lastHint == null) {
                lastHint = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
            }
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                renderingHint);
        }
        else {
            lastHint = null;
        }


        if ((image = getImage()) != null) {
            Insets sInsets = this.sInsets;
            Insets dInsets = this.dInsets;
            int iw = image.getWidth(null);
            int ih = image.getHeight(null);

            boolean stretch = !getTiles();

            // top left
            g.drawImage(image, x, y, x + dInsets.left, y + dInsets.top,
                            0, 0, sInsets.left, sInsets.right, null);
            // top
            drawChunk(image, g, stretch, x + dInsets.left, y,
                      x + w - dInsets.right, y + dInsets.top, sInsets.left, 0,
                          iw - sInsets.right, sInsets.top, true);
            // top right
            g.drawImage(image, x + w - dInsets.right, y, x + w,
                        y + dInsets.top, iw - sInsets.right, 0, iw,
                        sInsets.top, null);
            // right
            drawChunk(image, g, stretch, x + w - dInsets.right,
                      y + dInsets.top, x + w, y + h - dInsets.bottom,
                      iw - sInsets.right, sInsets.top, iw,
                      ih - sInsets.bottom, false);
            // bottom right
            g.drawImage(image, x + w - dInsets.right,
                        y + h - dInsets.bottom, x + w, y + h,
                        iw - sInsets.right, ih - sInsets.bottom, iw, ih,
                        null);
            // bottom
            drawChunk(image, g, stretch, x + dInsets.left,
                      y + h - dInsets.bottom, x + w - dInsets.right,
                      y + h, sInsets.left, ih - sInsets.bottom,
                      iw - sInsets.right, ih, true);
            // bottom left
            g.drawImage(image, x, y + h - dInsets.bottom, x + dInsets.left,
                        y + h, 0, ih - sInsets.bottom, sInsets.left, ih,
                        null);
            // left

            drawChunk(image, g, stretch, x, y + dInsets.top,
                      x + dInsets.left, y + h - dInsets.bottom,
                      0, sInsets.top, sInsets.left, ih - sInsets.bottom,
                      false);

            // center
            if (getPaintsCenter()) {
                g.drawImage(image, x + dInsets.left, y + dInsets.top,
                            x + w - dInsets.right, y + h - dInsets.bottom,
                            sInsets.left, sInsets.top, iw - sInsets.right,
                            ih - sInsets.bottom, null);
            }
        }

        if (renderingHint != null) {
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                             lastHint);
        }
    }

    private void drawChunk(Image image, Graphics g, boolean stretch,
                           int dx1, int dy1, int dx2, int dy2, int sx1,
                           int sy1, int sx2, int sy2,
                           boolean xDirection) {
        if (stretch) {
            g.drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
        }
        else {
            int xSize = sx2 - sx1;
            int ySize = sy2 - sy1;
            int deltaX;
            int deltaY;

            if (xDirection) {
                deltaX = xSize;
                deltaY = 0;
            }
            else {
                deltaX = 0;
                deltaY = ySize;
            }
            while (dx1 < dx2 && dy1 < dy2) {
                int newDX2 = Math.min(dx2, dx1 + xSize);
                int newDY2 = Math.min(dy2, dy1 + ySize);

                g.drawImage(image, dx1, dy1, newDX2, newDY2,
                            sx1, sy1, sx1 + newDX2 - dx1,
                            sy1 + newDY2 - dy1, null);
                dx1 += deltaX;
                dy1 += deltaY;
            }
        }
    }
}
