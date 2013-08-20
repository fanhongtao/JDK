/*
 * @(#)ImagePainter.java	1.8 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.plaf.synth;

import java.awt.*;
import java.net.*;
import javax.swing.*;

/**
 * ImagePainter fills in the specified region using an Image. The Image
 * is split into 9 segments: north, north east, east, south east, south,
 * south west, west, north west and the center. The corners are defined
 * by way of an insets, and the remaining regions are either tiled or
 * scaled to fit.
 *
 * @version 1.8, 12/19/03
 * @author Scott Violet
 */
class ImagePainter extends SynthPainter {
    private Image image;
    private Insets sInsets;
    private Insets dInsets;
    private URL path;
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
                        Insets destinationInsets, URL path) {
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
            image = new ImageIcon(path, null).getImage();
        }
        return image;
    }

    private void paint(Graphics g, int x, int y, int w, int h) {
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
                            0, 0, sInsets.left, sInsets.top, null);
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


    // SynthPainter
    public void paintArrowButtonBackground(SynthContext context,
                                           Graphics g, int x, int y,
                                           int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintArrowButtonBorder(SynthContext context,
                                       Graphics g, int x, int y,
                                       int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintArrowButtonForeground(SynthContext context,
                                           Graphics g, int x, int y,
                                           int w, int h,
                                           int direction) {
        paint(g, x, y, w, h);
    }

    // BUTTON
    public void paintButtonBackground(SynthContext context,
                                      Graphics g, int x, int y,
                                      int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintButtonBorder(SynthContext context,
                                  Graphics g, int x, int y,
                                  int w, int h) {
        paint(g, x, y, w, h);
    }

    // CHECK_BOX_MENU_ITEM
    public void paintCheckBoxMenuItemBackground(SynthContext context,
                                                Graphics g, int x, int y,
                                                int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintCheckBoxMenuItemBorder(SynthContext context,
                                            Graphics g, int x, int y,
                                            int w, int h) {
        paint(g, x, y, w, h);
    }

    // CHECK_BOX
    public void paintCheckBoxBackground(SynthContext context,
                                        Graphics g, int x, int y,
                                        int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintCheckBoxBorder(SynthContext context,
                                    Graphics g, int x, int y,
                                    int w, int h) {
        paint(g, x, y, w, h);
    }

    // COLOR_CHOOSER
    public void paintColorChooserBackground(SynthContext context,
                                            Graphics g, int x, int y,
                                            int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintColorChooserBorder(SynthContext context,
                                        Graphics g, int x, int y,
                                        int w, int h) {
        paint(g, x, y, w, h);
    }

    // COMBO_BOX
    public void paintComboBoxBackground(SynthContext context,
                                        Graphics g, int x, int y,
                                        int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintComboBoxBorder(SynthContext context,
                                        Graphics g, int x, int y,
                                        int w, int h) {
        paint(g, x, y, w, h);
    }

    // DESKTOP_ICON
    public void paintDesktopIconBackground(SynthContext context,
                                        Graphics g, int x, int y,
                                        int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintDesktopIconBorder(SynthContext context,
                                           Graphics g, int x, int y,
                                           int w, int h) {
        paint(g, x, y, w, h);
    }

    // DESKTOP_PANE
    public void paintDesktopPaneBackground(SynthContext context,
                                           Graphics g, int x, int y,
                                           int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintDesktopPaneBorder(SynthContext context,
                                       Graphics g, int x, int y,
                                       int w, int h) {
        paint(g, x, y, w, h);
    }

    // EDITOR_PANE
    public void paintEditorPaneBackground(SynthContext context,
                                          Graphics g, int x, int y,
                                          int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintEditorPaneBorder(SynthContext context,
                                      Graphics g, int x, int y,
                                      int w, int h) {
        paint(g, x, y, w, h);
    }

    // FILE_CHOOSER
    public void paintFileChooserBackground(SynthContext context,
                                          Graphics g, int x, int y,
                                          int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintFileChooserBorder(SynthContext context,
                                      Graphics g, int x, int y,
                                      int w, int h) {
        paint(g, x, y, w, h);
    }

    // FORMATTED_TEXT_FIELD
    public void paintFormattedTextFieldBackground(SynthContext context,
                                          Graphics g, int x, int y,
                                          int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintFormattedTextFieldBorder(SynthContext context,
                                      Graphics g, int x, int y,
                                      int w, int h) {
        paint(g, x, y, w, h);
    }

    // INTERNAL_FRAME_TITLE_PANE
    public void paintInternalFrameTitlePaneBackground(SynthContext context,
                                          Graphics g, int x, int y,
                                          int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintInternalFrameTitlePaneBorder(SynthContext context,
                                      Graphics g, int x, int y,
                                      int w, int h) {
        paint(g, x, y, w, h);
    }

    // INTERNAL_FRAME
    public void paintInternalFrameBackground(SynthContext context,
                                          Graphics g, int x, int y,
                                          int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintInternalFrameBorder(SynthContext context,
                                      Graphics g, int x, int y,
                                      int w, int h) {
        paint(g, x, y, w, h);
    }

    // LABEL
    public void paintLabelBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintLabelBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // LIST
    public void paintListBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintListBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // MENU_BAR
    public void paintMenuBarBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintMenuBarBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // MENU_ITEM
    public void paintMenuItemBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintMenuItemBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // MENU
    public void paintMenuBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintMenuBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // OPTION_PANE
    public void paintOptionPaneBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintOptionPaneBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // PANEL
    public void paintPanelBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintPanelBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // PANEL
    public void paintPasswordFieldBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintPasswordFieldBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // POPUP_MENU
    public void paintPopupMenuBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintPopupMenuBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // PROGRESS_BAR
    public void paintProgressBarBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintProgressBarBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintProgressBarForeground(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h, int orientation) {
        paint(g, x, y, w, h);
    }

    // RADIO_BUTTON_MENU_ITEM
    public void paintRadioButtonMenuItemBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintRadioButtonMenuItemBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // RADIO_BUTTON
    public void paintRadioButtonBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintRadioButtonBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // ROOT_PANE
    public void paintRootPaneBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintRootPaneBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // SCROLL_BAR
    public void paintScrollBarBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintScrollBarBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // SCROLL_BAR_THUMB
    public void paintScrollBarThumbBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h, int orientation) {
        paint(g, x, y, w, h);
    }

    public void paintScrollBarThumbBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h, int orientation) {
        paint(g, x, y, w, h);
    }

    // SCROLL_BAR_TRACK
    public void paintScrollBarTrackBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintScrollBarTrackBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // SCROLL_PANE
    public void paintScrollPaneBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintScrollPaneBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // SEPARATOR
    public void paintSeparatorBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintSeparatorBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintSeparatorForeground(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h, int orientation) {
        paint(g, x, y, w, h);
    }

    // SLIDER
    public void paintSliderBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintSliderBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // SLIDER_THUMB
    public void paintSliderThumbBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h, int orientation) {
        paint(g, x, y, w, h);
    }

    public void paintSliderThumbBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h, int orientation) {
        paint(g, x, y, w, h);
    }

    // SLIDER_TRACK
    public void paintSliderTrackBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintSliderTrackBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // SPINNER
    public void paintSpinnerBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintSpinnerBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // SPLIT_PANE_DIVIDER
    public void paintSplitPaneDividerBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintSplitPaneDividerForeground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h, int orientation) {
        paint(g, x, y, w, h);
    }

    public void paintSplitPaneDragDivider(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h, int orientation) {
        paint(g, x, y, w, h);
    }

    // SPLIT_PANE
    public void paintSplitPaneBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintSplitPaneBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // TABBED_PANE
    public void paintTabbedPaneBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintTabbedPaneBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // TABBED_PANE_TAB_AREA
    public void paintTabbedPaneTabAreaBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintTabbedPaneTabAreaBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // TABBED_PANE_TAB
    public void paintTabbedPaneTabBackground(SynthContext context, Graphics g,
                                         int x, int y, int w, int h,
                                         int tabIndex) {
        paint(g, x, y, w, h);
    }

    public void paintTabbedPaneTabBorder(SynthContext context, Graphics g,
                                         int x, int y, int w, int h,
                                         int tabIndex) {
        paint(g, x, y, w, h);
    }

    // TABBED_PANE_CONTENT
    public void paintTabbedPaneContentBackground(SynthContext context,
                                         Graphics g, int x, int y, int w,
                                         int h) {
        paint(g, x, y, w, h);
    }

    public void paintTabbedPaneContentBorder(SynthContext context, Graphics g,
                                         int x, int y, int w, int h) {
        paint(g, x, y, w, h);
    }

    // TABLE_HEADER
    public void paintTableHeaderBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintTableHeaderBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // TABLE
    public void paintTableBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintTableBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // TEXT_AREA
    public void paintTextAreaBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintTextAreaBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // TEXT_PANE
    public void paintTextPaneBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintTextPaneBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // TEXT_FIELD
    public void paintTextFieldBackground(SynthContext context,
                                          Graphics g, int x, int y,
                                          int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintTextFieldBorder(SynthContext context,
                                      Graphics g, int x, int y,
                                      int w, int h) {
        paint(g, x, y, w, h);
    }

    // TOGGLE_BUTTON
    public void paintToggleButtonBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintToggleButtonBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // TOOL_BAR
    public void paintToolBarBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintToolBarBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // TOOL_BAR_CONTENT
    public void paintToolBarContentBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintToolBarContentBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // TOOL_DRAG_WINDOW
    public void paintToolBarDragWindowBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintToolBarDragWindowBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // TOOL_TIP
    public void paintToolTipBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintToolTipBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // TREE
    public void paintTreeBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintTreeBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    // TREE_CELL
    public void paintTreeCellBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintTreeCellBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintTreeCellFocus(SynthContext context,
                                   Graphics g, int x, int y,
                                   int w, int h) {
        paint(g, x, y, w, h);
    }

    // VIEWPORT
    public void paintViewportBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paint(g, x, y, w, h);
    }

    public void paintViewportBorder(SynthContext context,
                                 Graphics g, int x, int y,
                                 int w, int h) {
        paint(g, x, y, w, h);
    }
}
