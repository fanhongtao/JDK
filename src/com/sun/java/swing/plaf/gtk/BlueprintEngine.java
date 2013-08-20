/*
 * @(#)BlueprintEngine.java	1.23 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import java.awt.*;
import java.awt.image.*;
import java.security.AccessController;
import java.util.*;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.synth.*;
import sun.security.action.GetPropertyAction;
import sun.swing.plaf.synth.SynthUI;

/**
 * GTKEngine implementation that renders using images. The images to render
 * are dictated by the <code>BlueprintStyle.Info</code>.
 *
 * @version 1.23 12/19/03
 * @author Joshua Outwater
 */
class BlueprintEngine extends GTKEngine implements GTKConstants {
    /**
     * By default we don't use smooth scaling as it is currently not optimized.
     */
    private static final Object RENDERING_HINT;

    private int COMPONENT_NORTH_WEST = 1;
    private int COMPONENT_NORTH = 2;
    private int COMPONENT_NORTH_EAST = 4;
    private int COMPONENT_WEST = 8;
    private int COMPONENT_CENTER = 16;
    private int COMPONENT_EAST = 32;
    private int COMPONENT_SOUTH_EAST = 64;
    private int COMPONENT_SOUTH = 128;
    private int COMPONENT_SOUTH_WEST = 256;
    private int COMPONENT_ALL = 512;

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
        if (!paintSimpleImage(context, g, x, y, w, h, true,
                ((BlueprintStyle)context.getStyle()).
                         getInfo("SLIDER", info, state, shadowType, orientation,
                                 UNDEFINED, UNDEFINED, null))) {
            super.paintSlider(context, g, state, shadowType, info,
                              x, y, w, h, orientation);
        }
    }

    public void paintHline(SynthContext context, Graphics g, int state,
                           String info, int x, int y, int w, int h) {

        SynthStyle style = context.getStyle();
        Component c = context.getComponent();

        // We have a different style to use if we are the child of
        // a popup menu.
        c = c.getParent();
        if (c instanceof JPopupMenu) {
            SynthStyle newStyle = getStyle((JPopupMenu)c,
                    ((JPopupMenu)c).getUI());
            if (newStyle != null) {
                style = newStyle;
            }
        }

        BlueprintStyle.Info blueprintInfo =
                ((BlueprintStyle)style).getInfo("HLINE", info,
                        state, UNDEFINED, GTKConstants.HORIZONTAL,
                        UNDEFINED, UNDEFINED, null);
        if (blueprintInfo != null && blueprintInfo.getImage() != null) {
            themeBlueprintRender(context, g, x, y, w, h,
                    blueprintInfo.getImage(), blueprintInfo.getImageInsets(),
                    COMPONENT_ALL, blueprintInfo.getStretch(), false,
                    blueprintInfo.isBkgMask(), blueprintInfo.isRecolorable(),
                    blueprintInfo.getColorizeColor());
        } else {
            super.paintHline(context, g, state, info, x, y, w, h);
        }
    }

    public void paintVline(SynthContext context, Graphics g, int state,
                           String info, int x, int y, int w, int h) {
        BlueprintStyle.Info blueprintInfo =
                ((BlueprintStyle)context.getStyle()).getInfo("VLINE", info,
                        state, UNDEFINED, GTKConstants.VERTICAL,
                        UNDEFINED, UNDEFINED, null);
        if (blueprintInfo != null && blueprintInfo.getImage() != null) {
            themeBlueprintRender(context, g, x, y, w, h, blueprintInfo.getImage(),
                    blueprintInfo.getImageInsets(), COMPONENT_ALL,
                    blueprintInfo.getStretch(), false,
                    blueprintInfo.isBkgMask(), blueprintInfo.isRecolorable(),
                    blueprintInfo.getColorizeColor());
        } else {
            super.paintVline(context, g, state, info, x, y, w, h);
        }
    }

    public void paintArrow(SynthContext context, Graphics g, int state,
                           int shadowType, int direction, String info,
                           int x, int y, int w, int h) {
        Component c = context.getComponent();

        // Don't paint the arrow if we're in a spinner or combo box.
        // We get that from the image.
        if (c.getName() == "Spinner.nextButton" ||
                c.getName() == "Spinner.previousButton" ||
                c.getName() == "ComboBox.arrowButton") {
            return;
        }

        String parentType = null;
        c = c.getParent();
        if (c != null && c instanceof JComponent) {
            c = c.getParent();
            if (c != null && c instanceof JComponent) {
                parentType = getComponentType((JComponent)c);
            }
        }

        if (!paintSimpleImage(context, g, x, y, w, h, true,
                ((BlueprintStyle)context.getStyle()).
                        getInfo("ARROW", info, state, shadowType,
                            UNDEFINED, UNDEFINED, direction, parentType))) {
            super.paintArrow(context, g, state, shadowType, direction,
                    info, x, y, w, h);
        }
    }

    public void paintBox(SynthContext context, Graphics g, int state,
                         int shadowType, String info, int x, int y,
                         int w, int h) {
        int orientation;
        Region id = context.getRegion();
        Component c = context.getComponent();
        SynthStyle style = context.getStyle();

        // Blueprint checks to make sure that we aren't calling
        // paintBox on a slider/scrollbar with detail hscrollbar or
        // vscrollbar, because they do the work in paintArrow instead.
        // We do it here because we have the correct bounds for the whole
        // button.
        Integer arrowDirection =
            (Integer)((JComponent)c).getClientProperty("__arrow_direction__");
        if (info == "vscrollbar" || info == "hscrollbar" &&
                arrowDirection != null) {
            int direction = arrowDirection.intValue();
            switch (direction) {
            case SwingConstants.NORTH:
                direction = GTKConstants.ARROW_UP;
                break;
            case SwingConstants.SOUTH:
                direction = GTKConstants.ARROW_DOWN;
                break;
            case SwingConstants.EAST:
                direction = GTKConstants.ARROW_RIGHT;
                break;
            case SwingConstants.WEST:
                direction = GTKConstants.ARROW_LEFT;
                break;
            }

            c = (JComponent)c.getParent();
            if (c == null || !(c instanceof JComponent)) {
                return;
            }

            if (c instanceof JScrollBar) {
                SynthStyle newStyle = getStyle((JScrollBar)c,
                        ((JScrollBar)c).getUI());
                if (newStyle != null) {
                    style = newStyle;
                }

                if (paintSimpleImage(context, g, x, y, w, h, true,
                        ((BlueprintStyle)style).getInfo("STEPPER", info,
                                state, UNDEFINED, UNDEFINED, UNDEFINED,
                                direction, null))) {
                    return;
                }
                if (!paintSimpleImage(context, g, x, y, w, h, true,
                    ((BlueprintStyle)style).getInfo("BOX", info, state,
                                shadowType, UNDEFINED, UNDEFINED,
                                UNDEFINED, null))) {
                    super.paintBox(context, g, state, shadowType, info,
                        x, y, w, h);
                }
                return;
            }
        }

        // If the button is in a spinner get the style of the JSpinner.
        if (c.getName() == "Spinner.nextButton" ||
                c.getName() == "Spinner.previousButton" &&
                arrowDirection != null) {
            if (arrowDirection.intValue() == SwingConstants.NORTH) {
                info = "spinbutton_up";
            } else {
                info = "spinbutton_down";
            }
            c = c.getParent();
            if (c instanceof JSpinner) {
                SynthStyle newStyle = getStyle((JSpinner)c,
                        ((JSpinner)c).getUI());
                if (newStyle != null) {
                    style = newStyle;
                }
            }
        }

        if (id == Region.SCROLL_BAR) {
            if (((JScrollBar)c).getOrientation() ==
                    SwingConstants.HORIZONTAL) {
                orientation = GTKConstants.HORIZONTAL;
            }
            else {
                orientation = GTKConstants.VERTICAL;
            }
        }
        else if (id == Region.SLIDER_TRACK) {
            if (((JSlider)c).getOrientation() ==
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

        String parentType = null;
        if (c != null) {
            c = c.getParent();
            if (c != null && c instanceof JComponent) {
                parentType = getComponentType((JComponent)c);
            }
        }

        if (!paintSimpleImage(context, g, x, y, w, h, true,
                ((BlueprintStyle)style).getInfo("BOX", info, state,
                        shadowType, orientation, UNDEFINED, UNDEFINED,
                        parentType))) {
            super.paintBox(context, g, state, shadowType, info, x, y, w, h);
        }
    }

    public void paintBoxGap(SynthContext context, Graphics g, int state,
                            int shadow, String key, int x, int y,
                            int w, int h, int gapSide, int gapStart,
                            int gapSize) {
        BlueprintStyle.Info info = ((BlueprintStyle)context.getStyle()).getInfo(
              "BOX_GAP", key, state, shadow, UNDEFINED, gapSide, UNDEFINED,
              null);

        if (info != null) {
            paintGapImage(context, info, g, x, y, w, h, true, gapSide,
                    gapStart, gapSize);
        } else {
            super.paintBoxGap(context, g, state, shadow, key, x, y, w, h,
                              gapSide, gapStart, gapSize);
        }
    }

    public void paintHandle(SynthContext context, Graphics g, int paintState,
                            int shadowType, String info, int x, int y,
                            int w, int h, int orientation) {
        if (info == "handlebox" || info == "dockitem") {
            w -=2;
            h -=1;
        }

        if (!paintSimpleImage(context, g, x, y, w, h, true,
                ((BlueprintStyle)context.getStyle()).
                        getInfo("HANDLE", info, paintState, shadowType,
                                orientation, UNDEFINED, UNDEFINED, null))) {
            super.paintHandle(context, g, paintState, shadowType, info, x, y,
                    w, h, orientation);
        }
    }

    public void paintOption(SynthContext context, Graphics g, int paintState,
                            int shadowType, String info, int x, int y,
                            int w, int h) {
        if (!paintSimpleImage(context, g, x, y, w, h, true,
                    ((BlueprintStyle)context.getStyle()).
                         getInfo("OPTION", info, paintState, shadowType,
                                 UNDEFINED, UNDEFINED, UNDEFINED, null))) {
            super.paintOption(context, g, paintState, shadowType, info, x, y,
                              w, h);
        }
    }

    public void paintFocus(SynthContext context, Graphics g, int state,
                           String key, int x, int y, int w, int h) {
        if (!paintSimpleImage(context, g, x, y, w, h, true,
                ((BlueprintStyle)context.getStyle()).
                        getInfo("FOCUS", key, state, UNDEFINED, UNDEFINED,
                                UNDEFINED, UNDEFINED, null))) {
            super.paintFocus(context, g, state, key, x, y, w, h);
        }
    }

    public void paintShadow(SynthContext context, Graphics g, int state,
                            int shadowType, String info, int x, int y,
                            int w, int h) {
        Component c = context.getComponent();
        String parentType = null;
        SynthStyle style = context.getStyle();

        if (c.getName() == "ComboBox.textField") {
            parentType = "GtkCombo";
        } else if (c.getName() == "ComboBox.renderer") {
            c = c.getParent();
            if (c != null) {
                c = c.getParent();
                parentType = "GtkCombo";
            }
        }

        if (c instanceof JComboBox) {
            // Use the Style from the editor
            JComboBox cb = (JComboBox)c;
            Component editor = cb.getEditor().getEditorComponent();
            if (editor instanceof JTextField) {
                if (!cb.isEditable() && editor.getParent() == null) {
                    // GTKStyleFactory hands back a bogus Style when a
                    // Component doesn't have a parent. As the editor
                    // is only parented when the JComboBox is editable it
                    // means we can get back a bogus style. To force the
                    // real style to be assigned we parent the editor.
                    // YES, this is ugly!
                    cb.add(editor);
                    cb.remove(editor);
                }
                SynthStyle newStyle = getStyle((JTextField)editor,
                        ((JTextField)editor).getUI());
                if (newStyle != null) {
                    style = newStyle;
                }
            }
        }

        if (info == "menu" && parentType == "GtkHBox") {
            return;
        }

        if (!paintSimpleImage(context, g, x, y, w, h, true,
                ((BlueprintStyle)style).getInfo("SHADOW", info, state,
                        shadowType, UNDEFINED, UNDEFINED, UNDEFINED,
                        parentType))) {
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
        if (!paintSimpleImage(context, g, x, y, w, h, true,
                ((BlueprintStyle)context.getStyle()).
                        getInfo("CHECK", info, state, shadowType, UNDEFINED,
                                UNDEFINED, UNDEFINED, null))) {
            super.paintCheck(context, g, state, shadowType, info, x, y, w, h);
        }
    }

    public void paintExtension(SynthContext context, Graphics g, int state,
                               int shadowType, String info, int x, int y,
                               int w, int h, int placement, int tabIndex) {
        if (!paintSimpleImage(context, g, x, y, w, h, true,
                ((BlueprintStyle)context.getStyle()).
                         getInfo("EXTENSION", info, state, shadowType,
                                 UNDEFINED, placement, UNDEFINED, null))) {
            super.paintExtension(context, g, state, shadowType, info, x, y,
                                 w, h, placement, tabIndex);
        }
    }

    public void paintFlatBox(SynthContext context, Graphics g, int state,
                             String key, int x, int y, int w, int h) {
        if (key == "checkbutton" && state == SynthConstants.MOUSE_OVER) {
            return;
        }

        Component c = context.getComponent();

        String parentType = null;
        c = c.getParent();
        if (c instanceof CellRendererPane) {
            // Skip the CellRendererPane
            c = c.getParent();
        }
        if (c != null && c instanceof JComponent) {
            parentType = getComponentType((JComponent)c);
        }

        if (!paintSimpleImage(context, g, x, y, w, h, true,
                ((BlueprintStyle)context.getStyle()).
                         getInfo("FLAT_BOX", key, state, UNDEFINED, UNDEFINED,
                                 UNDEFINED, UNDEFINED, parentType))) {
            super.paintFlatBox(context, g, state, key, x, y, w, h);
        }
    }

    void paintBackground(SynthContext context, Graphics g, int state,
            Color color, int x, int y, int w, int h) {
        JComponent c = context.getComponent();
        if (c instanceof JPopupMenu) {
            if (paintSimpleImage(context, g, x, y, w, h, true,
                ((BlueprintStyle)context.getStyle()).
                         getInfo("BACKGROUND", null, state, UNDEFINED,
                             UNDEFINED, UNDEFINED, UNDEFINED, null))) {
                return;
            }
        }
        super.paintBackground(context, g, state, color, x, y, w, h);
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
     * @param context Context used to retrieve style information.
     * @param info Blueprint style info
     * @param g Graphics object to paint to
     * @param x X origin
     * @param y Y origin
     * @param w Width to draw to
     * @param h Height to draw to
     * @param drawCenter Whether or not the center is drawn.
     * @param gapSide Side the gap is on, one of GTKConstants.LEFT,
     *        GTKConstants.RIGHT, GTKConstants.TOP or GTKConstants.BOTTOM
     * @param gapStart Starting location of the gap. The axis the gap is
     *        on is dictated by the gapSide
     * @param gapSize size of the gap
     */
    private void paintGapImage(SynthContext context, BlueprintStyle.Info info,
                               Graphics g, int x, int y, int w, int h,
                               boolean drawCenter, int gapSide, int gapStart,
                               int gapSize) {
        
        Rectangle r1 = new Rectangle();
        Rectangle r2 = new Rectangle();
        Rectangle r3 = new Rectangle();
        int size = 0;
        int componentMask = COMPONENT_ALL;
        Image startImage = info.getGapStartImage();
        Image image = info.getGapImage();
        Image endImage = info.getGapEndImage();

        if (!drawCenter) {
            componentMask |= COMPONENT_CENTER;
        }

        // Blueprint doesn't look at each individual image for size, just the
        // starting image.
        if (startImage != null) {
            if (gapSide == TOP || gapSize == BOTTOM) {
                size = startImage.getHeight(null);
            } else {
                size = startImage.getWidth(null);
            }
        } else {
            if (gapSide == TOP || gapSize == BOTTOM) {
                size = ((BlueprintStyle)context.getStyle()).getYThickness();
            } else {
                size = ((BlueprintStyle)context.getStyle()).getXThickness();
            }
        }

        if (gapSize > 0) {
            switch(gapSide) {
            case TOP:
                if (!drawCenter) {
                    componentMask |= COMPONENT_NORTH_WEST | COMPONENT_NORTH |
                        COMPONENT_NORTH_EAST;
                }
                // gap start
                r1.x = x;
                r1.y = y;
                r1.width = gapStart;
                r1.height = size;
                // gap
                r2.x = x + gapStart;
                r2.y = y;
                r2.width = gapSize;
                r2.height = size;
                // gap end
                r3.x = x + gapStart + gapSize;
                r3.y = y;
                r3.width = w - (gapStart + gapSize);
                r3.height = size;
                break;
            case BOTTOM:
                if (!drawCenter) {
                    componentMask |= COMPONENT_SOUTH_WEST | COMPONENT_SOUTH |
                        COMPONENT_SOUTH_EAST;
                }
                // gap start
                r1.x = x;
                r1.y = y + h - size;
                r1.width = gapStart;
                r1.height = size;
                // gap
                r2.x = x + gapStart;
                r2.y = y + h - size;
                r2.width = gapSize;
                r2.height = size;
                // gap end
                r3.x = x + gapStart + gapSize;
                r3.y = y + h - size;
                r3.width = w - (gapStart + gapSize);
                r3.height = size;
                break;
            case LEFT:
                if (!drawCenter) {
                    componentMask |= COMPONENT_NORTH_WEST | COMPONENT_WEST |
                        COMPONENT_SOUTH_WEST;
                }
                // gap start
                r1.x = x;
                r1.y = y;
                r1.width = size;
                r1.height = gapStart;
                // gap
                r2.x = x;
                r2.y = y + gapStart;
                r2.width = size;
                r2.height = gapSize;
                // gap end
                r3.x = x;
                r3.y = y + gapStart + gapSize;
                r3.width = size;
                r3.height = h - (gapStart + gapSize);
                break;
            case RIGHT:
                if (!drawCenter) {
                    componentMask |= COMPONENT_NORTH_EAST | COMPONENT_EAST |
                        COMPONENT_SOUTH_EAST;
                }
                // gap start
                r1.x = x + w - size;
                r1.y = y;
                r1.width = size;
                r1.height = gapStart;
                // gap
                r2.x = x + w - size;
                r2.y = y + gapStart;
                r2.width = size;
                r2.height = gapSize;
                // gap end
                r3.x = x + w - size;
                r3.y = y + gapStart + gapSize;
                r3.width = size;
                r3.height = h - (gapStart + gapSize);
                break;
            }

            themeBlueprintRender(context, g, x, y, w, h, info.getImage(),
                    info.getImageInsets(), componentMask, true, false,
                    info.isBkgMask(), info.isRecolorable(),
                    info.getColorizeColor());

            // NOTE:
            // stretch should be queried from the info, but there is currently
            // no support for that field for gap images in BlueprintStyle.Info.
            if (startImage != null) {
                themeBlueprintRender(context, g, r1.x, r1.y, r1.width, r1.height,
                        startImage, info.getGapStartInsets(), COMPONENT_ALL,
                        true, false, false, false, null);
            }
            if (image != null) {
                themeBlueprintRender(context, g, r2.x, r2.y, r2.width, r2.height,
                        image, info.getGapInsets(), COMPONENT_ALL,
                        true, false, false, false, null);
            }
            if (endImage != null) {
                themeBlueprintRender(context, g, r3.x, r3.y, r3.width, r3.height,
                        endImage, info.getGapEndInsets(), COMPONENT_ALL,
                        true, false, false, false, null);
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
     * @param drawCenter Whether the center of the image should be drawn
     * @param info Used to fetch image, insets and overlay image from
     */
    private boolean paintSimpleImage(SynthContext context, Graphics g, int x, int y,
            int w, int h, boolean drawCenter, BlueprintStyle.Info info) {
        if (info != null) {
            Rectangle clip = g.getClipBounds();
            _clipX1 = clip.x;
            _clipY1 = clip.y;
            _clipX2 = _clipX1 + clip.width;
            _clipY2 = _clipY1 + clip.height;
            themeBlueprintRender(context, g, x, y, w, h, info.getImage(),
                    info.getImageInsets(), drawCenter ? COMPONENT_ALL :
                    COMPONENT_ALL | COMPONENT_CENTER, info.getStretch(),
                    false, info.isBkgMask(), info.isRecolorable(),
                    info.getColorizeColor());
            if (drawCenter) {
                themeBlueprintRender(context, g, x, y, w, h,
                        info.getOverlayImage(), info.getOverlayInsets(),
                        COMPONENT_ALL, info.getOverlayStretch(), true,
                        false, false, null);
            }
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
     * @param componentMask Mask defining the areas of the image to draw.
     * @param stretch Stretch the image to fit the drawing area.
     * @param center Centers the image to the middle of the drawing area.
     * @param isBkgMask Whether or not the image is a background mask.
     * @param isRecolorable If the image is recolorable.
     * @param colorizeColor Color to use if image is recolorable.
     */
    private void themeBlueprintRender(SynthContext context, Graphics g,
                                      int x, int y, int w, int h, Image image,
                                      Insets insets, int componentMask,
                                      boolean stretch, boolean center,
                                      boolean isBkgMask, boolean isRecolorable,
                                      Color colorizeColor) {
        if (image == null) {
            return;
        }
        if (insets == null) {
            insets = GTKPainter.EMPTY_INSETS;
        }
        int iw = image.getWidth(null);
        int ih = image.getHeight(null);
 
        if (isBkgMask) {
            // Colorize mask using the colorizeColor from info.
            BufferedImage i = new BufferedImage(iw, ih,
                                                BufferedImage.TYPE_INT_ARGB);
            Graphics2D g3 = i.createGraphics();
             
            boolean topParentReached = false;
            int steps = 0;
 
            Component compParent = context.getComponent();
 
            while (!topParentReached && steps <= 2) {
                compParent = compParent.getParent ();
                steps ++;
 
                if (compParent != null) {
                    Color color = compParent.getBackground ();
                    if (color != null) {
                        if (!color.equals (colorizeColor) && 
                            !color.equals (Color.black) && 
                            !(compParent instanceof JFileChooser)) {
                            colorizeColor = color;
                            topParentReached = true;
                        }
                    }
                } else {
                    topParentReached = true;
                }
            }
 
            if (colorizeColor == null) {
                colorizeColor = ((GTKStyle)context.getStyle()).
                    getGTKColor(context.getComponent(), context.getRegion(),
                                context.getComponentState(),
                                ColorType.BACKGROUND);
            }
            g3.setColor(colorizeColor);
            g3.fillRect(0, 0, iw, ih);
            g3.setComposite(AlphaComposite.DstIn);
            g3.drawImage(image, 0, 0, null);
            g3.dispose();
            image = i;
        } else if (isRecolorable) {
            // Create a copy of the image to manipulate the pixels.
            BufferedImage i = new BufferedImage(iw, ih,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g3 = i.createGraphics();
            g3.setComposite(AlphaComposite.Src);
            g3.drawImage(image, 0, 0, null);
            g3.dispose();

            int red = colorizeColor.getRed();
            int green = colorizeColor.getGreen();
            int blue = colorizeColor.getBlue();
            int alpha = colorizeColor.getAlpha();

            Color color = RGBtoHLS(red, green, blue);

            int hue = color.getRed();
            int lum = color.getGreen();
            int sat = color.getBlue();

            int[] pixels = null;
            // Get the pixel data from the image.
            pixels = i.getRaster().getPixels(0, 0, iw, ih, pixels);

            // Colorize the pixels.
            for (int index = 0; index < pixels.length; index+=4) {
                red = pixels[index];
                green = pixels[index + 1];
                blue = pixels[index + 2];

                color = RGBtoHLS(red, green, blue);
                red = hue;
                green = color.getGreen();
                blue = sat;

                color = HLStoRGB(red, green, blue);

                pixels[index] = color.getRed();
                pixels[index + 1] = color.getGreen();
                pixels[index + 2] = color.getBlue();
                pixels[index + 3] = Math.min(
                        pixels[index + 3], alpha);
            }
            // Set the pixel data for the image.
            i.getRaster().setPixels(0, 0, iw, ih, pixels);
            image = i;
        }

        if (stretch) {
//            themeBlueprintComputeHints();
        }

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
            if (center) {
                /* Center the image. */
                blueprintRender(image, g, 0, 0, iw, ih, x + (w / 2) - (iw / 2),
                        y + (h / 2) - (ih / 2), iw, ih);
            }
            else {
                /* Tile the image. */
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
            int srcX[] = new int[4];
            int srcY[] = new int[4];
            int destX[] = new int[4];
            int destY[] = new int[4];

            srcX[0] = 0;
            srcX[1] = insets.left;
            srcX[2] = iw - insets.right;
            srcX[3] = iw;

            srcY[0] = 0;
            srcY[1] = insets.top;
            srcY[2] = ih - insets.bottom;
            srcY[3] = ih;

            destX[0] = x;
            destX[1] = x + insets.left;
            destX[2] = x + w - insets.right;
            destX[3] = x + w;

            destY[0] = y;
            destY[1] = y + insets.top;
            destY[2] = y + h - insets.bottom;
            destY[3] = y + h;

            /* Scale the image. */
            if ((componentMask & COMPONENT_ALL) != 0) {
                componentMask = (COMPONENT_ALL - 1) & ~componentMask;
            }

            // top left
            if ((componentMask & COMPONENT_NORTH_WEST) != 0) {
                blueprintRender(image, g,
                        srcX[0], srcY[0],
                        srcX[1] - srcX[0], srcY[1] - srcY[0],
                        destX[0], destY[0],
                        destX[1] - destX[0], destY[1] - destY[0]);
            }
            // top
            if ((componentMask & COMPONENT_NORTH) != 0) {
                blueprintRender(image, g,
                        srcX[1], srcY[0],
                        srcX[2] - srcX[1], srcY[1] - srcY[0],
                        destX[1], destY[0],
                        destX[2] - destX[1], destY[1] - destY[0]);
            }
            // top right
            if ((componentMask & COMPONENT_NORTH_EAST) != 0) {
                blueprintRender(image, g,
                        srcX[2], srcY[0],
                        srcX[3] - srcX[2], srcY[1] - srcY[0],
                        destX[2], destY[0],
                        destX[3] - destX[2], destY[1] - destY[0]);
            }
            // left
            if ((componentMask & COMPONENT_WEST) != 0) {
                blueprintRender(image, g,
                        srcX[0], srcY[1],
                        srcX[1] - srcX[0], srcY[2] - srcY[1],
                        destX[0], destY[1],
                        destX[1] - destX[0], destY[2] - destY[1]);
            }
            // center
            if ((componentMask & COMPONENT_CENTER) != 0) {
                blueprintRender(image, g,
                        srcX[1], srcY[1],
                        srcX[2] - srcX[1], srcY[2] - srcY[1],
                        destX[1], destY[1],
                        destX[2] - destX[1], destY[2] - destY[1]);
            }
            // right
            if ((componentMask & COMPONENT_EAST) != 0) {
                blueprintRender(image, g,
                        srcX[2], srcY[1],
                        srcX[3] - srcX[2], srcY[2] - srcY[1],
                        destX[2], destY[1],
                        destX[3] - destX[2], destY[2] - destY[1]);
            }
            // bottom left
            if ((componentMask & COMPONENT_SOUTH_WEST) != 0) {
                blueprintRender(image, g,
                        srcX[0], srcY[2],
                        srcX[1] - srcX[0], srcY[3] - srcY[2],
                        destX[0], destY[2],
                        destX[1] - destX[0], destY[3] - destY[2]);
            }
            // bottom
            if ((componentMask & COMPONENT_SOUTH) != 0) {
                blueprintRender(image, g,
                        srcX[1], srcY[2],
                        srcX[2] - srcX[1], srcY[3] - srcY[2],
                        destX[1], destY[2],
                        destX[2] - destX[1], destY[3] - destY[2]);
            }
            // bottom right
            if ((componentMask & COMPONENT_SOUTH_EAST) != 0) {
                blueprintRender(image, g,
                        srcX[2], srcY[2],
                        srcX[3] - srcX[2], srcY[3] - srcY[2],
                        destX[2], destY[2],
                        destX[3] - destX[2], destY[3] - destY[2]);
            }
        }

        if (lastHint != null) {
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                             lastHint);
        }
    }

    /**
     * Draws a portion of an image stretched.
     *
     * @param image Image to render.
     * @param g Graphics to render to
     * @param srcX X origin to draw from
     * @param srxY Y origin to draw from
     * @param srcWidth Width of source
     * @param srcHeight Height of source
     * @param destX X origin to draw to
     * @param destY Y origin to draw to
     * @param destWidth Width of destination
     * @param destHeight Height of destination
     */
    private void blueprintRender(Image image, Graphics g,
                int srcX, int srcY, int srcWidth, int srcHeight,
                int destX, int destY, int destWidth, int destHeight) {
        if (destWidth <= 0 || destHeight <= 0 ||
                !intersectsClip(destX, destY,
                    destX + destWidth, destY + destHeight)) {
            // Bogus location, nothing to paint
            return;
        }

        if (srcWidth == 0 && srcHeight == 0) {
            // Paint bilinear gradient.
        } else if (srcHeight == 0 && destHeight == srcHeight) {
            // Paint horizontal gradient.
        } else if (srcHeight == 0 && destWidth == srcWidth) {
            // Paint vertical gradient.
        }

        g.drawImage(image, destX, destY, destX + destWidth, destY + destHeight,
                srcX, srcY, srcX + srcWidth, srcY + srcHeight, null);
    }

    private boolean hasAncestorOfTypeFromList(JComponent c, ArrayList list) {
        if (list == null) {
            return false;
        }

        Iterator itr = list.iterator();
        while (itr.hasNext()) {
            if (hasAncestorOfType(c, (String)itr.next())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAncestorOfType(JComponent c, String parentType) {
        String type = null;
        while (c != null) {
            type = getComponentType(c);
            if (type == parentType) {
                return true;
            }
            if (c.getParent() instanceof JComponent) {
                c = (JComponent)c.getParent();
            } else {
                c = null;
            }
        }
        return false;
    }

    private String getComponentType(JComponent c) {
        return GTKStyleFactory.gtkClassFor(SynthLookAndFeel.getRegion(c));
    }

    private SynthStyle getStyle(JComponent c, ComponentUI ui) {
        if (ui instanceof SynthUI) {
            SynthContext parentContext = ((SynthUI)ui).getContext(c);
            // Note that we don't dispose of the context here, while this
            // isn't good, it just means we won't be recycling as often as
            // we can.
            return parentContext.getStyle();
        }
        return null;
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

    /**
     * Convert RGB to HLS.
     *
     * @param r Red
     * @param g Green
     * @param b Blue
     * @return Color Where red = hue, green = lightness and blue = saturation.
     */
    private Color RGBtoHLS(int r, int g, int b) {
        int h, l, s;
        int min, max;
        int delta;

        if (r > g) {
            max = Math.max(r, b);
            min = Math.min(g, b);
        } else {
            max = Math.max(g, b);
            min = Math.min(r, b);
        }

        l = (max + min) / 2;

        if (max == min) {
            s = 0;
            h = 0;
        } else {
            delta = (max - min);

            if (l < 128) {
                s = 255 * delta / (max + min);
            } else {
                s = 255 * delta / (511 - max - min);
            }

            if (r == max) {
                h = (g - b) / delta;
            } else if (g == max) {
                h = 2 + (b - r) / delta;
            } else {
                h = 4 + (r - g) / delta;
            }

            h = (int)(h * 42.5);

            if (h < 0) {
                h+= 255;
            } else if (h > 255) {
                h -= 255;
            }
        }

        return new Color(h, l, s);
    }

    /**
     * Convert HLS to RGB.
     *
     * @param hue Hue
     * @param lightness Lightness
     * @param saturation Saturation
     * @return Color Resulting RGB color.
     */
    private Color HLStoRGB(int hue, int lightness, int saturation) {
        double h = hue;
        double l = lightness;
        double s = saturation;
        double m1, m2;

        if (s == 0) {
            hue = lightness;
            saturation = lightness;
        } else {
            if (l < 128) {
                m2 = (l * (255 + s)) / 65025.0;
            } else {
                m2 = (l + s - (l * s) / 255.0) / 255.0;
            }
            m1 = (l / 127.5) - m2;

            hue = HLSvalue(m1, m2, h + 85);
            lightness = HLSvalue(m1, m2, h);
            saturation = HLSvalue(m1, m2, h - 85);
        }
        return new Color(hue, lightness, saturation);
    }

    private int HLSvalue(double n1, double n2, double hue) {
        double value;

        if (hue > 255) {
            hue -= 255;
        } else if (hue < 0) {
            hue += 255;
        }

        if (hue < 42.5) {
            value = n1 + (n2 - n1) * (hue / 42.5);
        } else if (hue < 127.5) {
            value = n2;
        } else if (hue < 170) {
            value = n1 + (n2 - n1) * ((170 - hue) / 42.5);
        } else {
            value = n1;
        }

        return (int)(value * 255);
    }
}
