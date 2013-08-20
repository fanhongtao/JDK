/*
 * @(#)PixmapEngine.java	1.14 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import javax.swing.plaf.synth.*;

import java.awt.*;
import java.security.AccessController;
import java.util.*;
import javax.swing.*;
import sun.security.action.GetPropertyAction;

/**
 * GTKEngine implementation that renders using images. The images to render
 * are dictated by the <code>PixmapStyle.Info</code>.
 *
 * @version 1.14, 12/19/03
 * @author Scott Violet
 */
class PixmapEngine extends GTKEngine implements GTKConstants {
    /**
     * By default we don't use smooth scaling as it is currently not optimized.
     */
    private static final Object RENDERING_HINT;

    private int _clipX1;
    private int _clipX2;
    private int _clipY1;
    private int _clipY2;

    static {
        if ("true".equals((String)AccessController.doPrivileged(
                   new GetPropertyAction("swing.pixmap.smoothScaling")))) {
            RENDERING_HINT = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
        }
        else {
            RENDERING_HINT = null;
        }
    }

    public void paintSlider(SynthContext context, Graphics g, int state,
                           int shadowType, String info,
                           int x, int y, int w, int h, int orientation) {
        if (!paintPixmap(g, x, y, w, h, ((PixmapStyle)context.getStyle()).
                         getInfo("SLIDER", info,state, shadowType, orientation,
                                 UNDEFINED, UNDEFINED), true)) {
            super.paintSlider(context, g, state, shadowType, info,
                              x, y, w, h, orientation);
        }
    }

    public void paintHline(SynthContext context, Graphics g, int state,
                           String info, int x, int y, int w, int h) {
        if (!paintPixmap(g, x, y, w, h, ((PixmapStyle)context.getStyle()).
                         getInfo("HLINE", info, state, UNDEFINED, UNDEFINED,
                         UNDEFINED, UNDEFINED), true)) {
            super.paintHline(context, g, state, info, x, y, w, h);
        }
    }

    public void paintVline(SynthContext context, Graphics g, int state,
                           String info, int x, int y, int w, int h) {
        if (!paintPixmap(g, x, y, w, h, ((PixmapStyle)context.getStyle()).
                         getInfo("VLINE", info, state, UNDEFINED, UNDEFINED,
                                 UNDEFINED, UNDEFINED), true)) {
            super.paintVline(context, g, state, info, x, y, w, h);
        }
    }

    public void paintArrow(SynthContext context, Graphics g, int state,
                           int shadowType, int direction, String info,
                           int x, int y, int w, int h) {
        if (!paintPixmap(g, x, y, w, h, ((PixmapStyle)context.getStyle()).
                         getInfo("ARROW", info, state, shadowType, UNDEFINED,
                         UNDEFINED, direction), true)) {
            super.paintArrow(context, g, state, shadowType, direction, info,
                             x, y, w, h);
        }
    }

    public void paintBox(SynthContext context, Graphics g, int state,
                         int shadowType, String info, int x, int y,
                         int w, int h) {
        int orientation;
        Region id = context.getRegion();
        if (id == Region.SCROLL_BAR) {
            if (((JScrollBar)context.getComponent()).getOrientation() ==
                                     SwingConstants.HORIZONTAL) {
                orientation = GTKConstants.HORIZONTAL;
            }
            else {
                orientation = GTKConstants.VERTICAL;
            }
        }
        else if (id == Region.SLIDER_TRACK) {
            if (((JSlider)context.getComponent()).getOrientation() ==
                                     SwingConstants.HORIZONTAL) {
                orientation = GTKConstants.HORIZONTAL;
            }
            else {
                orientation = GTKConstants.VERTICAL;
            }
        }
        else {
            orientation = UNDEFINED;
        }
        if (!paintPixmap(g, x, y, w, h, ((PixmapStyle)context.getStyle()).
                         getInfo("BOX", info, state, shadowType, orientation,
                                 UNDEFINED, UNDEFINED), true)) {
            super.paintBox(context, g, state, shadowType, info, x, y, w, h);
        }
    }

    public void paintBoxGap(SynthContext context, Graphics g, int state,
                            int shadow, String key, int x, int y,
                            int w, int h, int gapSide, int gapStart,
                            int gapSize) {
        PixmapStyle.Info info = ((PixmapStyle)context.getStyle()).getInfo(
              "BOX_GAP", key, state, shadow, UNDEFINED, gapSide, UNDEFINED);

        if (info != null) {
            // Yes, this appears to paint before the gap does.
            paintPixmap(g, x, y, w, h, info, true);

            // Determine the size of the opposite axis of the gap.
            int size = 0;
            Image startImage = info.getGapStartImage();
            Image image = info.getGapImage();
            Image endImage = info.getGapEndImage();
            if (gapSide == LEFT || gapSide == RIGHT) {
                if (startImage != null) {
                    size = startImage.getWidth(null);
                }
                else if (image != null) {
                    size = image.getWidth(null);
                }
                else if (endImage != null) {
                    size = endImage.getWidth(null);
                }
            }
            else {
                if (startImage != null) {
                    size = startImage.getHeight(null);
                }
                else if (image != null) {
                    size = image.getHeight(null);
                }
                else if (endImage != null) {
                    size = endImage.getHeight(null);
                }
            }
            if (size <= 0) {
                // No matching images.
                return;
            }
            paintGapImage(g, x, y, w, h, startImage, info.getGapStartInsets(),
                          gapSide, size, 0, gapStart);
            paintGapImage(g, x, y, w, h, image, info.getGapInsets(), gapSide,
                          size, gapStart, gapSize);
            paintGapImage(g, x, y, w, h, endImage, info.getGapEndInsets(),
                          gapSide, size, gapStart + gapSize,
                          Integer.MAX_VALUE);
        }
        else {
            super.paintBoxGap(context, g, state, shadow, key, x, y, w, h,
                              gapSide, gapStart,gapSize);
        }
    }

    public void paintHandle(SynthContext context, Graphics g, int paintState,
                            int shadowType, String info, int x, int y,
                            int w, int h, int orientation) {
        if (!paintPixmap(g, x, y, w, h, ((PixmapStyle)context.getStyle()).
                         getInfo("HANDLE", info, paintState, shadowType,
                                 orientation, UNDEFINED, UNDEFINED), true)) {
            super.paintHandle(context, g, paintState, shadowType, info, x, y,
                              w, h, orientation);
        }
    }

    public void paintOption(SynthContext context, Graphics g, int paintState,
                            int shadowType, String info, int x, int y,
                            int w, int h) {
        if (!paintPixmap(g, x, y, w, h, ((PixmapStyle)context.getStyle()).
                         getInfo("OPTION", info, paintState, shadowType,
                                 UNDEFINED, UNDEFINED, UNDEFINED), true)) {
            super.paintOption(context, g, paintState, shadowType, info, x, y,
                              w, h);
        }
    }

    public void paintFocus(SynthContext context, Graphics g, int state,
                           String key, int x, int y, int w, int h) {
        if (!paintPixmap(g, x, y, w, h, ((PixmapStyle)context.getStyle()).
                         getInfo( "FOCUS", key, state, UNDEFINED, UNDEFINED,
                         UNDEFINED, UNDEFINED), true)) {
            super.paintFocus(context, g, state, key, x, y, w, h);
        }
    }

    public void paintShadow(SynthContext context, Graphics g, int state,
                            int shadowType, String info, int x, int y,
                            int w, int h) {
        if (!paintPixmap(g, x, y, w, h, ((PixmapStyle)context.getStyle()).
                         getInfo("SHADOW", info, state, shadowType, UNDEFINED,
                                 UNDEFINED, UNDEFINED), false)) {
            super.paintShadow(context, g, state, shadowType, info, x, y, w, h);
        }
    }

    public void paintExpander(SynthContext context, Graphics g, int state,
                              int expanderStyle, String info, int x,
                              int y, int w, int h) {
        // It does not appear that there is a way to override this.
        super.paintExpander(context, g, state, expanderStyle, info, x, y, w,h);
    }

    public void paintCheck(SynthContext context, Graphics g, int state,
                           int shadowType, String info, int x, int y,
                           int w, int h) {
        if (!paintPixmap(g, x, y, w, h, ((PixmapStyle)context.getStyle()).
                        getInfo("CHECK", info, state, shadowType, UNDEFINED,
                                UNDEFINED, UNDEFINED), true)) {
            super.paintCheck(context, g, state, shadowType, info, x, y, w, h);
        }
    }

    public void paintExtension(SynthContext context, Graphics g, int state,
                               int shadowType, String info, int x, int y,
                               int w, int h, int placement, int tabIndex) {
        if (!paintPixmap(g, x, y, w, h, ((PixmapStyle)context.getStyle()).
                         getInfo("EXTENSION", info, state, shadowType,
                                 UNDEFINED, placement, UNDEFINED), true)) {
            super.paintExtension(context, g, state, shadowType, info, x, y,
                                 w, h, placement, tabIndex);
        }
    }

    public void paintFlatBox(SynthContext context, Graphics g, int state,
                             String key, int x, int y, int w, int h) {
        if (!paintPixmap(g, x, y, w, h, ((PixmapStyle)context.getStyle()).
                         getInfo("FLAT_BOX", key, state, UNDEFINED, UNDEFINED,
                                 UNDEFINED, UNDEFINED), true)) {
            super.paintFlatBox(context, g, state, key, x, y, w, h);
        }
    }


    /**
     * Paints a gap image. This renders the image into a portion of
     * the passed in region that is dictated
     * by the <code>gapSide</code> and <code>size</code> arguments. For
     * example, if <code>gapSide</code> is <code>GTKConstants.TOP</code>,
     * this will render the image into the space:
     * <table>
     * <tr><td>x origin<td> <code>x</code> + <code>gapStart</code>
     * <tr><td>y origin<td>  <code>y</code>
     * <tr><td>width<td> <code>gapSize</code>
     * <tr><td>height<td> <code>size</code>
     * </table>
     *
     * @param g Graphics object to paint to
     * @param x X origin
     * @param y Y origin
     * @param w Width to draw to
     * @param h Height to draw to
     * @param image Image to paint
     * @param insets Insets dicatating fixed portion and scaled portion of
     *               the image.
     * @param gapSide Side the gap is on, one of GTKConstants.LEFT,
     *        GTKConstants.RIGHT, GTKConstants.TOP or GTKConstants.BOTTOM
     * @param size Size of the gap, either width or height, dependant upon
     *        gapSide
     * @param gapStart Starting location of the gap. The axis the gap is
     *        on is dictated by the gapSide
     * @param gapSize size of the gap
     */
    private void paintGapImage(Graphics g, int x, int y, int w, int h,
                               Image image, Insets insets, int gapSide,
                               int size, int gapStart, int gapSize) {
        if (image != null && gapSize > 0) {
            switch(gapSide) {
            case LEFT:
                paintImage(g, x, y + gapStart, Math.min(w, size),
                      Math.min(h - y - gapStart, gapSize), image,insets, true,
                           false, true);
                break;
            case RIGHT:
                paintImage(g, x + w - Math.min(w, size),
                           y + gapStart, Math.min(w, size),
                           Math.min(h - y - gapStart, gapSize), image,
                           insets, true, false, true);
                break;
            case TOP:
                paintImage(g, x + gapStart, y, Math.min(w - x - gapStart,
                           gapSize), Math.min(h, size), image, insets, true,
                           false, true);
                break;
            case BOTTOM:
                paintImage(g, x + gapStart, y + h - Math.min(h, size),
                           Math.min(w - x - gapStart, gapSize),
                           Math.min(h, size), image, insets, true, false,true);
                break;
            }
         }
    }

    /**
     * Paints the image and overlay image from the passed in style.
     *
     * @param g Graphics object to paint to
     * @param x X origin
     * @param y Y origin
     * @param w Width to draw to
     * @param h Height to draw to
     * @param info Used to fetch image, insets and overlay image from
     */
    private boolean paintPixmap(Graphics g, int x, int y, int w, int h,
                                PixmapStyle.Info info, boolean drawCenter) {
        if (info != null) {
            Rectangle clip = g.getClipBounds();
            _clipX1 = clip.x;
            _clipY1 = clip.y;
            _clipX2 = _clipX1 + clip.width;
            _clipY2 = _clipY1 + clip.height;
            paintImage(g, x, y, w, h, info.getImage(), info.getImageInsets(),
                       info.getStretch(), false, drawCenter);
            paintImage(g, x, y, w, h, info.getOverlayImage(),
                       info.getOverlayInsets(), info.getOverlayStretch(),
                       true, drawCenter);
            return true;
         }
        return false;
    }

    /**
     * Paints the image in the specified region.
     *
     * @param g Graphics object to paint to
     * @param x X origin
     * @param y Y origin
     * @param w Width to draw to
     * @param h Height to draw to
     * @param image Image to render
     * @param insets Insets used to determine portion of image that is fixed.
     */
    private void paintImage(Graphics g, int x, int y, int w, int h,
                            Image image, Insets insets, boolean stretch,
                            boolean overlay, boolean drawCenter) {
        if (image == null) {
            return;
        }
        if (insets == null) {
            insets = GTKPainter.EMPTY_INSETS;
        }
        int iw = image.getWidth(null);
        int ih = image.getHeight(null);

        if (iw <= 0 || ih <= 0) {
            return;
        }
        Object lastHint;
        Object renderingHint = RENDERING_HINT;

        if (renderingHint != null && stretch) {
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

        if (!stretch) {
            if (overlay) {
                g.drawImage(image, x + w / 2 - iw / 2, y + h / 2 - ih / 2,
                            null);
            }
            else {
                int lastIY = 0;
                for (int yCounter = y, maxY = y + h; yCounter < maxY;
                         yCounter += (ih - lastIY), lastIY = 0) {
                    int lastIX = 0;
                    for (int xCounter = x, maxX = x + w; xCounter < maxX;
                             xCounter += (iw - lastIX), lastIX = 0) {
                        int dx2 = Math.min(maxX, xCounter + iw - lastIX);
                        int dy2 = Math.min(maxY, yCounter + ih - lastIY);
                        if (intersectsClip(xCounter, yCounter, dx2, dy2)) {
                            g.drawImage(image, xCounter, yCounter, dx2, dy2,
                                        lastIX, lastIY, lastIX + dx2 -xCounter,
                                        lastIY + dy2 - yCounter, null);
                        }
                    }
                }
            }
        }
        else {
            int it = insets.top;
            int il = insets.left;
            int ib = insets.bottom;
            int ir = insets.right;

            // Constrain the insets to the size of the image
            if (it + ib >= ih) {
                ib = it = Math.max(0, ih / 2 - 1);
            }
            if (il + ir >= iw) {
                il = ir = Math.max(0, iw / 2 - 1);
            }
            // Constrain the insets to the size of the region we're painting
            // in.
            if (it + ib > h) {
                it = ib = Math.max(2, h / 2 - 1);
            }
            if (il + ir > w) {
                il = ir = Math.max(2, w / 2 - 1);
            }
            // left
            if (il > 0 && it + ib < ih) {
                drawChunk(image, g, stretch, x, y + it, x + il, y + h - ib, 0,
                          it, il, ih - ib, false);
            }
            // top left
            if (il > 0 && it > 0) {
                g.drawImage(image, x, y, x + il, y + it, 0, 0, il, it, null);
            }
            // top
            if (it > 0 && il + ir < iw) {
                drawChunk(image, g, stretch, x + il, y, x + w - ir, y + it,
                          il, 0, iw - ir, it, true);
            }
            // top right
            if (ir < iw && it > 0) {
                g.drawImage(image, x + w - ir, y, x + w, y + it, iw - ir, 0,
                            iw, it, null);
            }
            // right
            if (ir < iw && it + ib < ih) {
                drawChunk(image, g, stretch, x + w - ir, y + it, x + w,
                          y + h - ib, iw - ir, it, iw, ih - ib, false);
            }
            // bottom right
            if (ir < iw && ib < ih) {
                g.drawImage(image, x + w - ir, y + h - ib, x + w, y + h,
                            iw - ir, ih - ib, iw, ih, null);
            }
            // bottom
            if (il + ir < iw && ib > 0) {
                drawChunk(image, g, stretch, x + il, y + h - ib, x + w - ir,
                          y + h, il, ih - ib, iw - ir, ih, true);
            }
            // bottom left
            if (il > 0 && ib > 0) {
                g.drawImage(image, x, y + h - ib, x + il,
                            y + h, 0, ih - ib, il, ih, null);
            }
            // center
            if (drawCenter && il + ir < iw && it + ib < ih) {
                g.drawImage(image, x + il, y + it, x + w - ir, y + h - ib,
                            il, it, iw - ir, ih - ib, null);
            }
        }

        if (lastHint != null) {
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                             lastHint);
        }
    }

    /**
     * Draws a portion of an image, stretched or tiled.
     *
     * @param image Image to render.
     * @param g Graphics to render to
     * @param stretch Whether the image should be stretched or timed in the
     *                provided space.
     * @param dx1 X origin to draw to
     * @param dy1 Y origin to draw to
     * @param dx2 End x location to draw to
     * @param dy2 End y location to draw to
     * @param sx1 X origin to draw from
     * @param sy1 Y origin to draw from
     * @param sx2 Max x location to draw from
     * @param sy2 Max y location to draw from
     * @param xDirection Used if the image is not stretched. If true it
     *        indicates the image should be tiled along the x axis.
     */
    private void drawChunk(Image image, Graphics g, boolean stretch,
                           int dx1, int dy1, int dx2, int dy2, int sx1,
                           int sy1, int sx2, int sy2,
                           boolean xDirection) {
        if (dx2 - dx1 <= 0 || dy2 - dy1 <= 0 ||
                              !intersectsClip(dx1, dy1, dx2, dy2)) {
            // Bogus location, nothing to paint
            return;
        }
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

                if (intersectsClip(dx1, dy1, newDX2, newDY2)) {
                    g.drawImage(image, dx1, dy1, newDX2, newDY2,
                                sx1, sy1, sx1 + newDX2 - dx1,
                                sy1 + newDY2 - dy1, null);
                }
                dx1 += deltaX;
                dy1 += deltaY;
            }
        }
    }

    /**
     * Returns true if the passed in region intersects the clip.
     */
    private boolean intersectsClip(int x1, int y1, int x2, int y2) {
        return ((x2 < x1 || x2 > _clipX1) &&
                (y2 < y1 || y2 > _clipY1) &&
                (_clipX2 < _clipX1 || _clipX2 > x1) &&
                (_clipY2 < _clipY1 || _clipY2 > y1));
    }
}
