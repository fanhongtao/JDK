/*
 * @(#)BluecurveEngine.java	1.3 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;

/**
 * A bluecurve like engine.
 *
 * @version 1.3, 01/23/03
 * @author Scott Violet
 */
class BluecurveEngine extends GTKEngine {
    public void paintSlider(SynthContext context, Graphics g, int state,
                            int shadowType, String info,
                            int x, int y, int w, int h, int orientation) {
        Region region = context.getRegion();
        if (region == Region.SLIDER_THUMB) {
            BluecurveStyle style = (BluecurveStyle)context.getStyle();
            JComponent c = context.getComponent();
            paintBackground(context, g, state, style.getGTKColor(
                     c, region, state, GTKColorType.BACKGROUND), x, y, w, h);
            g.setColor(style.getGTKColor(c, region, state,
                                         BluecurveColorType.OUTER3));
            g.drawLine(x + 2, y, x + w - 3, y);
            g.drawLine(x, y + 2, x, y + h - 3);
            g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 3);
            g.drawLine(x + 2, y + h - 1, x + w - 3, y + h - 1);
            g.fillRect(x + 1, y + 1, 1, 1);
            g.fillRect(x + w - 2, y + 1, 1, 1);
            g.fillRect(x + 1, y + h - 2, 1, 1);
            g.fillRect(x + w - 2, y + h - 2, 1, 1);

            g.setColor(style.getGTKColor(c, region, state,
                                         BluecurveColorType.WHITE));
            g.drawLine(x + 2, y + 1, x + w - 3, y + 1);
            g.drawLine(x + 1, y + 2, x + 1, y + h - 3);

            g.setColor(style.getGTKColor(c, region, state,
                                         BluecurveColorType.INNER_RIGHT2));
            g.drawLine(x + 2, y + h - 2, x + w - 3, y + h - 2);
            g.drawLine(x + w - 2, y + 2, x + w - 2, y + h - 3);
            g.drawLine(x, y + 1, x + 1, y);
            g.drawLine(x, y + h - 2, x + 1, y + h - 1);
            g.drawLine(x + w - 2, y + h - 1, x + w - 1, y + h - 2);
            g.drawLine(x + w - 2, y, x + w - 1, y + 1);

            if (((JSlider)c).getOrientation() == SwingConstants.HORIZONTAL &&
                     w > 12) {
                paintHash(context, g, state, x + w / 2 - 5, y + h / 2 - 2, 3);
                paintHash(context, g, state, x + w / 2 - 3, y + h / 2 - 3, 6);
                paintHash(context, g, state, x + w / 2 +2, y + h / 2 - 1, 3);
            }
            else if (((JSlider)c).getOrientation() ==
                               SwingConstants.VERTICAL && h > 12) {
                paintHash(context, g, state, x + w / 2 - 2, y + h / 2 - 5, 3);
                paintHash(context, g, state, x + w / 2 - 3, y + h / 2 - 3, 6);
                paintHash(context, g, state, x + w / 2 - 1, y + h / 2 + 2, 3);
            }
        }
        else {
            super.paintSlider(context, g, state, shadowType, info, x, y, w, h,
                              orientation);
            if (context.getRegion() == Region.SCROLL_BAR_THUMB) {
                paintHashes(context, g, state, x, y, w, h, orientation, 3, 5);
            }
        }
    }

    private void paintHash(SynthContext context, Graphics g, int state,
                             int x, int y, int size) {
        GTKStyle style = (GTKStyle)context.getStyle();

        g.setColor(style.getGTKColor(context.getComponent(),
                                     context.getRegion(), state,
                                     BluecurveColorType.OUTER2));
        g.drawLine(x, y + size, x + size, y);

        g.setColor(style.getGTKColor(context.getComponent(),
                                     context.getRegion(), state,
                                     GTKColorType.WHITE));
        g.drawLine(x + 1, y + size, x + size, y + 1);
    }

    private void paintHashes(SynthContext context, Graphics g, int state,
                             int x, int y, int w, int h, int orientation,
                             int count, int size) {
        // 3 diagonal lines 5x5
        GTKStyle style = (GTKStyle)context.getStyle();
        if (orientation == GTKConstants.HORIZONTAL) {
            if (w < size * count + 4) {
                return;
            }
            int x0 = x + (w - size * count) / 2;
            int y0 = y + (h - size) / 2;

            g.setColor(style.getGTKColor(context.getComponent(),
                                         context.getRegion(), state,
                                         BluecurveColorType.OUTER2));
            for (int counter = 0; counter < count; counter++) {
                g.drawLine(x0 + counter * size, y0 + size,
                           x0 + (counter + 1) * size, y0);
            }

            g.setColor(style.getGTKColor(context.getComponent(),
                                         context.getRegion(), state,
                                         GTKColorType.WHITE));
            for (int counter = 0; counter < count; counter++) {
                g.drawLine(x0 + counter * size + 1, y0 + size,
                           x0 + (counter + 1) * size, y0 + 1);
            }
        }
        else if (orientation == GTKConstants.VERTICAL) {
            if (h < size * count + 4) {
                return;
            }
            int x0 = x + (w - size) / 2;
            int y0 = y + (h - size * count) / 2;

            g.setColor(style.getGTKColor(context.getComponent(),
                                         context.getRegion(), state,
                                         BluecurveColorType.OUTER2));
            for (int counter = 0; counter < count; counter++) {
                g.drawLine(x0, y0 + (counter + 1) * size, x0 + size,
                           y0 + (counter * size));
            }
            g.setColor(style.getGTKColor(context.getComponent(),
                                         context.getRegion(), state,
                                         GTKColorType.WHITE));
            for (int counter = 0; counter < count; counter++) {
                g.drawLine(x0 + 1, y0 + (counter + 1) * size, x0 + size,
                           y0 + counter * size + 1);
            }
        }
    }

    public void paintBox(SynthContext context, Graphics g, int state,
                         int shadowType, String info, int x, int y,
                         int w, int h) {
        GTKStyle style = (GTKStyle)context.getStyle();
        Region region = context.getRegion();
        if (info != "trough" || region != Region.SLIDER_TRACK) {
            paintBackground(context, g, state, 
                      style.getGTKColor(context.getComponent(), region, state,
                                        GTKColorType.BACKGROUND), x, y, w, h);
        }
        paintShadow(context, g, state, shadowType, info, x, y, w, h);
    }

    public void paintShadow(SynthContext context, Graphics g, int state,
                            int shadowType, String info, int x, int y,
                            int w, int h) {
        if (info == "menubar") {
            // This isn't really dark, but not sure what color they're using
            // here
            g.setColor(((GTKStyle)context.getStyle()).getGTKColor(
                           context.getComponent(), context.getRegion(), state,
                           BluecurveColorType.OUTER4));
            g.drawLine(x, y + h - 1, x + w, y + h - 1);
            return;
        }
        if (info == "buttondefault") {
            // YES, this appears to be special cased.
            g.setColor(((GTKStyle)context.getStyle()).getGTKColor(
                           context.getComponent(), context.getRegion(),
                           state, GTKColorType.BLACK));
            g.drawRect(x, y, w - 1, h - 1);
            return;
        }
        BluecurveStyle style = (BluecurveStyle)context.getStyle();
        JComponent c = context.getComponent();
        Region region = context.getRegion();
        int xThickness = style.getXThickness();
        int yThickness = style.getYThickness();

        if (info == "trough") {
            // YES, this appears to be special cased.
            xThickness = yThickness = 1;
            if (region == Region.SLIDER_TRACK) {
                if (((JSlider)c).getOrientation() ==SwingConstants.HORIZONTAL){
                    if (h > 5) {
                        y = y + h / 2 - 2;
                        h = 5;
                    }
                }
                else if (w > 5) {
                    x = x + w / 2 - 2;
                    w = 5;
                }
            }
        }
        else if (info == "bar") {
            if (xThickness < 2) {
                x -= xThickness;
                y -= yThickness;
                w += xThickness + xThickness;
                h += yThickness + yThickness;
                xThickness = yThickness = 2;
            }
        }
        if (xThickness < 0 && yThickness < 0) {
            // nothing to paint.
            return;
        }
        Color upperLeft = null, innerLeft = null, bottomRight = null,
              innerRight = null;
        if (info == "menu" || (info == "trough" &&
                    (region == Region.PROGRESS_BAR || region ==
                     Region.SLIDER_TRACK)) || info == "entry") {
            if (info != "menu" && info != "entry") {
                g.setColor(style.getGTKColor(c, region, state,
                                             BluecurveColorType.OUTER4));
                g.fillRect(x, y, w, h);
            }
            upperLeft = bottomRight = style.getGTKColor(c, region, state,
                              BluecurveColorType.OUTER2);
            if (shadowType == GTKConstants.SHADOW_OUT) {
                innerLeft = style.getGTKColor(c, region, state,
                                              BluecurveColorType.WHITE);
                innerRight = style.getGTKColor(c, region, state,
                                            BluecurveColorType.INNER_RIGHT2);
            }
            else {
                innerLeft = style.getGTKColor(c, region, state,
                                              BluecurveColorType.INNER_RIGHT2);
                innerRight = style.getGTKColor(c, region, state,
                                            BluecurveColorType.WHITE);
            }
        }
        else if (info != "menuitem" && info != "bar") {
            upperLeft = bottomRight = style.getGTKColor(c, region, state,
                              BluecurveColorType.OUTER3);
            if (shadowType == GTKConstants.SHADOW_OUT) {
                innerLeft = style.getGTKColor(c, region, state,
                                              BluecurveColorType.WHITE);
                innerRight = style.getGTKColor(c, region, state,
                                              BluecurveColorType.INNER_RIGHT2);
            }
            else {
                innerLeft = style.getGTKColor(c, region, state,
                                              BluecurveColorType.INNER_RIGHT2);
                innerRight = style.getGTKColor(c, region, state,
                                              BluecurveColorType.WHITE);
            }
        }
        else {
            upperLeft = bottomRight = style.getGTKColor(c, region,
                 SynthConstants.SELECTED, BluecurveColorType.OUTER);
            switch (shadowType) {
            case GTKConstants.SHADOW_OUT:
                innerLeft = style.getGTKColor(c, region,
                     SynthConstants.SELECTED, BluecurveColorType.INNER_LEFT);
                innerRight = style.getGTKColor(c, region,
                     SynthConstants.SELECTED, BluecurveColorType.INNER_RIGHT);
                break;
            case GTKConstants.SHADOW_IN:
                innerRight = style.getGTKColor(c, region,
                     SynthConstants.SELECTED, BluecurveColorType.INNER_LEFT);
                innerLeft = style.getGTKColor(c, region,
                     SynthConstants.SELECTED, BluecurveColorType.INNER_RIGHT);
                break;
            default:
                assert true : "Unknown shadow type!";
            }
        }
        _paintShadow(g, x, y, w, h, xThickness, yThickness, upperLeft,
                innerLeft, bottomRight, innerRight);
        if (info == "menuitem" || info == "bar") {
            // Draw the GradientPaint
            // PENDING: we could cache a GradientPaint to avoid so much garbage.
            int gw = Math.min(2, xThickness);
            int gh = Math.min(2, yThickness);
            Color topColor = style.getGTKColor(c, region,
                  SynthConstants.SELECTED,BluecurveColorType. TOP_GRADIENT);
            Color bottomColor = style.getGTKColor(c, region,
                  SynthConstants.SELECTED,BluecurveColorType. BOTTOM_GRADIENT);
            GradientPaint paint = new GradientPaint((float)gw, (float)gh,
                       topColor, (float)gw, (float)(h - gh - gh), bottomColor);
            g.translate(x, y);
            ((Graphics2D)g).setPaint(paint);
            g.fillRect(gw, gh, w - gw - gw, h - gh - gh);
            ((Graphics2D)g).setPaint(null);
            g.translate(-x, -y);
        }
    }

    public void paintArrow(SynthContext context, Graphics g, int state,
                           int shadowType, int direction, String info,
                           int x, int y, int w, int h) {
        // Draw the arrow
        int sizeW = w / 2;
        if (w % 2 == 1) {
            sizeW++;
        }
        int sizeH = h / 2;
        if (h % 2 == 1) {
            sizeH++;
        }
        int size = Math.max(2, Math.min(sizeW, sizeH));

        switch (direction) {
        case GTKConstants.ARROW_UP:
            x += w / 2 - 1;
            y += (h - size) / 2;
            break;
        case GTKConstants.ARROW_DOWN:
            x += w / 2 - 1;
            y += (h - size) / 2 + 1;
            break;
        case GTKConstants.ARROW_LEFT:
            x += (w - size) / 2;
            y += h / 2 - 1;
            break;
        case GTKConstants.ARROW_RIGHT:
            x += (w - size) / 2 + 1;
            y += h / 2 - 1;
            break;
        }

        GTKStyle style = (GTKStyle)context.getStyle();
        int mid, i, j;

        j = 0;
        mid = (size / 2) - 1;

        g.translate(x, y);

        // PENDING: this isn't the right color.
        g.setColor(style.getGTKColor(context.getComponent(),
                   context.getRegion(), state, BluecurveColorType.OUTER5));

        switch(direction) {
        case GTKConstants.ARROW_UP:
            for(i = 0; i < size; i++) {
                g.drawLine(mid-i, i, mid+i, i);
            }
            g.fillRect(mid - size + 2, size, 1, 1);
            g.fillRect(mid + size - 2, size, 1, 1);
            break;
        case GTKConstants.ARROW_DOWN:
            j = 0;
            for (i = size-1; i >= 0; i--) {
                g.drawLine(mid-i, j, mid+i, j);
                j++;
            }
            g.fillRect(mid - size + 2, -1, 1, 1);
            g.fillRect(mid + size - 2, -1, 1, 1);
            break;
        case GTKConstants.ARROW_LEFT:
            for (i = 0; i < size; i++) {
                g.drawLine(i, mid-i, i, mid+i);
            }
            g.fillRect(size, mid - size + 2, 1, 1);
            g.fillRect(size, mid + size - 2, 1, 1);
            break;
        case GTKConstants.ARROW_RIGHT:
            j = 0;
            for (i = size-1; i >= 0; i--)   {
                g.drawLine(j, mid-i, j, mid+i);
                j++;
            }
            g.fillRect(-1, mid - size + 2, 1, 1);
            g.fillRect(-1, mid + size - 2, 1, 1);
            break;
        }
        g.translate(-x, -y);	
    }

    public void paintHandle(SynthContext context, Graphics g, int paintState,
                            int shadowType, String info, int x, int y,
                            int w, int h, int orientation) {
        paintHashes(context, g, paintState, x, y, w, h, orientation, 5, 4);
    }

    public void paintOption(SynthContext context, Graphics g, int paintState,
                            int shadowType, String info, int x, int y,
                            int w, int h) {
        if (info == "option") {
            int componentState = context.getComponentState();
            if ((componentState & SynthConstants.SELECTED) != 0) {
                g.translate(x, y);
                int centerY = h / 2 - 1;
                JComponent component = context.getComponent();
                Region region = context.getRegion();
                GTKStyle style = (GTKStyle)context.getStyle();

                if ((componentState & SynthConstants.MOUSE_OVER) != 0) {
                    g.setColor(style.getGTKColor(component, region, paintState,
                                                 GTKColorType.WHITE));
                }
                else {
                    g.setColor(style.getGTKColor(component, region, paintState,
                                                 GTKColorType.BLACK));
                }
                g.fillRect(5, centerY, 5, 3);
                g.drawLine(6, centerY - 1, 8, centerY - 1);
                g.drawLine(6, centerY + 3, 8, centerY + 3);
                g.translate(-x, -y);
            }
            return;
        }
        super.paintOption(context, g, paintState, shadowType, info, x, y,
                          w, h);
        if (info == "radiobutton") {
            if ((context.getComponentState() & SynthConstants.SELECTED) != 0) {
                // PENDING: this should be a gradient.
                int centerY = h / 2 - 1;
                g.translate(x, y);
                g.setColor(((GTKStyle)context.getStyle()).getGTKColor(context.
                           getComponent(), context.getRegion(), paintState,
                           BluecurveColorType.OUTER));
                g.fillRect(5, centerY, 5, 3);
                g.drawLine(6, centerY - 1, 8, centerY - 1);
                g.drawLine(6, centerY + 3, 8, centerY + 3);
                g.translate(-x, -y);
            }
        }
    }

    public void paintExtension(SynthContext context, Graphics g, int state,
                               int shadowType, String info, int x, int y,
                               int w, int h, int placement) {
        _paintExtension(context, g, state, shadowType, x, y, w, h, placement,
                   BluecurveColorType.OUTER3, GTKColorType.BACKGROUND,
                   BluecurveColorType.OUTER3, BluecurveColorType.INNER_RIGHT2,
                        true);
    }

    public void paintBoxGap(SynthContext context, Graphics g, int state,
                            int shadowType, String info, int x, int y,
                            int w, int h, int boxGapType, int tabBegin,
                            int size) {
        _paintBoxGap(context, g, state, shadowType, x, y, w, h, boxGapType,
                     tabBegin, size, GTKColorType.BACKGROUND,
                     BluecurveColorType.OUTER3, BluecurveColorType.OUTER3,
                     BluecurveColorType.INNER_RIGHT2, true);
    }

    Color getFocusColor(SynthContext context, int state) {
        return ((BluecurveStyle)context.getStyle()).getGTKColor(
               context.getComponent(), context.getRegion(),
               SynthConstants.SELECTED, BluecurveColorType.OUTER3);
    }
}
