/*
 * @(#)GTKPainter.java	1.79 06/06/07
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import sun.swing.plaf.synth.SynthUI;
import sun.awt.UNIXToolkit;

import javax.swing.plaf.synth.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import com.sun.java.swing.plaf.gtk.GTKConstants.ArrowType;
import com.sun.java.swing.plaf.gtk.GTKConstants.ExpanderStyle;
import com.sun.java.swing.plaf.gtk.GTKConstants.Orientation;
import com.sun.java.swing.plaf.gtk.GTKConstants.PositionType;
import com.sun.java.swing.plaf.gtk.GTKConstants.ShadowType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @version 1.79, 06/07/06
 * @author Joshua Outwater
 * @author Scott Violet
 */
// Need to support:
// default_outside_border: Insets when default.
// interior_focus: Indicates if focus should appear inside border, or
//                       outside border.
// focus-line-width: Integer giving size of focus border
// focus-padding: Integer giving padding between border and focus
//        indicator.
// focus-line-pattern: 
//
class GTKPainter extends SynthPainter {
    private static final PositionType[] POSITIONS = {
        PositionType.BOTTOM, PositionType.RIGHT,
        PositionType.TOP, PositionType.LEFT
    };
    
    private static final ShadowType SHADOWS[] = {
        ShadowType.NONE, ShadowType.IN, ShadowType.OUT,
        ShadowType.ETCHED_IN, ShadowType.OUT
    };
    
    private final static GTKEngine ENGINE = GTKEngine.INSTANCE;
    final static GTKPainter INSTANCE = new GTKPainter();

    private GTKPainter() {
    }

    private String getName(SynthContext context) {
        return (context.getRegion().isSubregion()) ? null :
               context.getComponent().getName();
    }

    public void paintCheckBoxBackground(SynthContext context,
            Graphics g, int x, int y, int w, int h) {
        paintRadioButtonBackground(context, g, x, y, w, h);
    }

    public void paintCheckBoxMenuItemBackground(SynthContext context,
            Graphics g, int x, int y, int w, int h) {
        paintRadioButtonMenuItemBackground(context, g, x, y, w, h);
    }

    // FORMATTED_TEXT_FIELD
    public void paintFormattedTextFieldBackground(SynthContext context,
                                          Graphics g, int x, int y,
                                          int w, int h) {
        paintTextBackground(context, g, x, y, w, h);
    }

    //
    // TOOL_DRAG_WINDOW
    //
    public void paintToolBarDragWindowBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paintToolBarBackground(context, g, x, y, w, h);
    }


    //
    // TOOL_BAR
    //
    public void paintToolBarBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        Region id = context.getRegion();
        int gtkState = GTKLookAndFeel.synthStateToGTKState(
                id, context.getComponentState());
        synchronized (UNIXToolkit.GTK_LOCK) {
            if (! ENGINE.paintCachedImage(g, x, y, w, h, id, gtkState)) {
                ENGINE.startPainting(g, x, y, w, h, id, gtkState);
                ENGINE.paintFlatBox(g, context, id, gtkState, ShadowType.OUT,
                        "handlebox_bin", x, y, w, h, ColorType.BACKGROUND);
                ENGINE.finishPainting();
            }
        }
    }

    //
    // PASSWORD_FIELD
    //
    public void paintPasswordFieldBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        paintTextBackground(context, g, x, y, w, h);
    }
    
    //
    // TEXT_FIELD
    //
    public void paintTextFieldBackground(SynthContext context, Graphics g,
                                         int x, int y, int w, int h) {
        if (getName(context) == "Tree.cellEditor") {
            paintTreeCellEditorBackground(context, g, x, y, w, h);
        } else {
            paintTextBackground(context, g, x, y, w, h);
        }
    }

    //
    // RADIO_BUTTON
    //
    // NOTE: this is called for JCheckBox too
    public void paintRadioButtonBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        Region id = context.getRegion();
        int gtkState = GTKLookAndFeel.synthStateToGTKState(
                id, context.getComponentState());
        if (gtkState == SynthConstants.MOUSE_OVER) {
            synchronized (UNIXToolkit.GTK_LOCK) {
                if (! ENGINE.paintCachedImage(g, x, y, w, h, id)) {
                    ENGINE.startPainting(g, x, y, w, h, id);
                    ENGINE.paintFlatBox(g, context, id,
                            SynthConstants.MOUSE_OVER, ShadowType.ETCHED_OUT,
                            "checkbutton", x, y, w, h, ColorType.BACKGROUND);
                    ENGINE.finishPainting();
                }
            }
        }
    }

    //
    // RADIO_BUTTON_MENU_ITEM
    //
    // NOTE: this is called for JCheckBoxMenuItem too
    public void paintRadioButtonMenuItemBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        Region id = context.getRegion();
        int gtkState = GTKLookAndFeel.synthStateToGTKState(
                id, context.getComponentState());
        if (gtkState == SynthConstants.MOUSE_OVER) {
            synchronized (UNIXToolkit.GTK_LOCK) {
                if (! ENGINE.paintCachedImage(g, x, y, w, h, id)) {
                    ShadowType shadow = (GTKLookAndFeel.is2_2() ?
                        ShadowType.NONE : ShadowType.OUT);
                    ENGINE.startPainting(g, x, y, w, h, id);
                    ENGINE.paintBox(g, context, id, gtkState,
                            shadow, "menuitem", x, y, w, h);
                    ENGINE.finishPainting();
                }
            }
        }
    }

    // 
    // LABEL
    //
    public void paintLabelBackground(SynthContext context,
                                     Graphics g, int x, int y,
                                     int w, int h) {
        String name = getName(context);
        if (name == "TableHeader.renderer" ||
            name == "GTKFileChooser.directoryListLabel" ||
            name == "GTKFileChooser.fileListLabel") {
            
            paintButtonBackgroundImpl(context, g, Region.BUTTON, "button",
                    x, y, w, h, true, false, false, false);
        } else if (name == "ComboBox.renderer") {
            paintTextBackground(context, g, x, y, w, h);
        }
    }

    //
    // INTERNAL_FRAME
    // 
    public void paintInternalFrameBorder(SynthContext context,
                                      Graphics g, int x, int y,
                                      int w, int h) {
        Metacity.INSTANCE.paintFrameBorder(context, g, x, y, w, h);
    }

    //
    // DESKTOP_PANE
    //
    public void paintDesktopPaneBackground(SynthContext context,
                                           Graphics g, int x, int y,
                                           int w, int h) {
        // Does not call into ENGINE for better performance
        fillArea(context, g, x, y, w, h, ColorType.BACKGROUND);
    }

    //
    // DESKTOP_ICON
    //
    public void paintDesktopIconBorder(SynthContext context,
                                           Graphics g, int x, int y,
                                           int w, int h) {
        Metacity.INSTANCE.paintFrameBorder(context, g, x, y, w, h);
    }

    public void paintButtonBackground(SynthContext context, Graphics g,
                                      int x, int y, int w, int h) {
        String name = getName(context);
        if (name != null && name.startsWith("InternalFrameTitlePane.")) {
            Metacity.INSTANCE.paintButtonBackground(context, g, x, y, w, h);

        } else {
            AbstractButton button = (AbstractButton)context.getComponent();
            boolean paintBG = button.isContentAreaFilled() &&
                              button.isBorderPainted();
            boolean paintFocus = button.isFocusPainted();
            boolean defaultCapable = (button instanceof JButton) &&
                    ((JButton)button).isDefaultCapable();
            boolean toolButton = (button.getParent() instanceof JToolBar);
            paintButtonBackgroundImpl(context, g, Region.BUTTON, "button",
                    x, y, w, h, paintBG, paintFocus, defaultCapable, toolButton);
        }
    }

    private void paintButtonBackgroundImpl(SynthContext context, Graphics g,
            Region id, String detail, int x, int y, int w, int h,
            boolean paintBackground, boolean paintFocus,
            boolean defaultCapable, boolean toolButton) {
        int state = context.getComponentState();
        synchronized (UNIXToolkit.GTK_LOCK) {
            if (ENGINE.paintCachedImage(g, x, y, w, h, id, state, detail,
                    paintBackground, paintFocus, defaultCapable, toolButton)) {
                return;
            }
            ENGINE.startPainting(g, x, y, w, h, id, state, detail,
                paintBackground, paintFocus, defaultCapable, toolButton);

            // Paint the default indicator
            GTKStyle style = (GTKStyle)context.getStyle();
            if (defaultCapable) {
                Insets defaultInsets = (Insets)style.getClassSpecificInsetsValue(
                        context, "default-border",
                        GTKStyle.BUTTON_DEFAULT_BORDER_INSETS);
            
                if (paintBackground && (state & SynthConstants.DEFAULT) != 0) {
                    ENGINE.paintBox(g, context, id, SynthConstants.ENABLED,
                            ShadowType.IN, "buttondefault", x, y, w, h);
                }
                x += defaultInsets.left;
                y += defaultInsets.top;
                w -= (defaultInsets.left + defaultInsets.right);
                h -= (defaultInsets.top + defaultInsets.bottom);
            }

            boolean interiorFocus = style.getClassSpecificBoolValue(
                    context, "interior-focus", true);
            int focusSize = style.getClassSpecificIntValue(
                    context, "focus-line-width",1);
            int focusPad = style.getClassSpecificIntValue(
                    context, "focus-padding", 1);
            
            int totalFocusSize = focusSize + focusPad;
            int xThickness = style.getXThickness();
            int yThickness = style.getYThickness();

            // Render the box.
            if (!interiorFocus &&
                    (state & SynthConstants.FOCUSED) == SynthConstants.FOCUSED) {
                x += totalFocusSize;
                y += totalFocusSize;
                w -= 2 * totalFocusSize;
                h -= 2 * totalFocusSize;
            }
        
            int gtkState = GTKLookAndFeel.synthStateToGTKState(id, state);
            if ((paintBackground && !toolButton) ||
                (gtkState != SynthConstants.ENABLED))
            {
                ShadowType shadowType = ShadowType.OUT;
                if ((state & (SynthConstants.PRESSED |
                              SynthConstants.SELECTED)) != 0) {
                    shadowType = ShadowType.IN;
                }
                ENGINE.paintBox(g, context, id, gtkState,
                        shadowType, detail, x, y, w, h);
            }
            
            // focus
            if (paintFocus && (state & SynthConstants.FOCUSED) != 0) {
                if (interiorFocus) {
                    x += xThickness + focusPad;
                    y += yThickness + focusPad;
                    w -= 2 * (xThickness + focusPad);
                    h -= 2 * (yThickness + focusPad);
                } else {
                    x -= totalFocusSize;
                    y -= totalFocusSize;
                    w += 2 * totalFocusSize;
                    h += 2 * totalFocusSize;
                }
                ENGINE.paintFocus(g, context, id, gtkState, detail, x, y, w, h);
            }
            ENGINE.finishPainting();
        }
    }

    //
    // ARROW_BUTTON
    //
    public void paintArrowButtonForeground(SynthContext context, Graphics g,
                                           int x, int y, int w, int h,
                                           int direction) {
        Region id = context.getRegion();
        Component c = context.getComponent();
        String name = c.getName();

        ArrowType arrowType = null;
        switch (direction) {
            case SwingConstants.NORTH:
                arrowType = ArrowType.UP; break;
            case SwingConstants.SOUTH:
                arrowType = ArrowType.DOWN; break;
            case SwingConstants.EAST:
                arrowType = ArrowType.RIGHT; break;
            case SwingConstants.WEST:
                arrowType = ArrowType.LEFT; break;
        }

        String detail = "arrow";
        if (name == "ScrollBar.button") {
            if (arrowType == ArrowType.UP || arrowType == ArrowType.DOWN) {
                detail = "vscrollbar";
            } else {
                detail = "hscrollbar";
            }
            id = Region.SCROLL_BAR;
        } else if (name == "Spinner.nextButton" ||
                   name == "Spinner.previousButton") {
            detail = "spinbutton";
        } else if (name == "ComboBox.arrowButton") {
            id = Region.COMBO_BOX;
        } else {
            assert false;
        }
        
        int gtkState = GTKLookAndFeel.synthStateToGTKState(
                id, context.getComponentState());
        ShadowType shadowType = (gtkState == SynthConstants.PRESSED ?
            ShadowType.IN : ShadowType.OUT);
        synchronized (UNIXToolkit.GTK_LOCK) {
            if (ENGINE.paintCachedImage(g, x, y, w, h,
                    gtkState, name, direction)) {
                return;
            }
            ENGINE.startPainting(g, x, y, w, h, gtkState, name, direction);
            ENGINE.paintArrow(g, context, id, gtkState,
                    shadowType, arrowType, detail, x, y, w, h);
            ENGINE.finishPainting();
        }
    }

    public void paintArrowButtonBackground(SynthContext context,
            Graphics g, int x, int y, int w, int h) {
        Region id = context.getRegion();
        AbstractButton button = (AbstractButton)context.getComponent();
        boolean paintBG = button.isContentAreaFilled() &&
                          button.isBorderPainted();
        boolean focusPainted = button.isFocusPainted();

        String name = button.getName();
        String detail = "button";
        if (name == "ScrollBar.button") {
            Component parent = button.getParent();
            if (parent instanceof JScrollBar) {
                if (((JScrollBar)parent).getOrientation() ==
                        SwingConstants.HORIZONTAL) {
                    detail = "hscrollbar";
                } else {
                    detail = "vscrollbar";
                }
                id = Region.SCROLL_BAR;
            }
        } else if (name == "Spinner.previousButton") {
            detail = "spinbutton_down";
        } else if (name == "Spinner.nextButton") {
            detail = "spinbutton_up";
        } else if (name == "ComboBox.arrowButton") {
            id = Region.BUTTON;
        }
        paintButtonBackgroundImpl(context, g, id, detail, x, y, w, h,
                paintBG, focusPainted, false, false);
    }


    //
    // LIST
    //
    public void paintListBackground(SynthContext context, Graphics g,
                                    int x, int y, int w, int h) {
        // Does not call into ENGINE for better performance
        fillArea(context, g, x, y, w, h, GTKColorType.TEXT_BACKGROUND);
    }
    
    public void paintMenuBarBackground(SynthContext context, Graphics g,
                                       int x, int y, int w, int h) {
        Region id = context.getRegion();
        synchronized (UNIXToolkit.GTK_LOCK) {
            if (ENGINE.paintCachedImage(g, x, y, w, h, id)) {
                return;
            }
            GTKStyle style = (GTKStyle)context.getStyle();
            int shadow = style.getClassSpecificIntValue(
                    context, "shadow-type", 2);
            ShadowType shadowType = SHADOWS[shadow];
            int gtkState = GTKLookAndFeel.synthStateToGTKState(
                    id, context.getComponentState());
            ENGINE.startPainting(g, x, y, w, h, id);
            ENGINE.paintBox(g, context, id, gtkState,
                shadowType, "menubar", x, y, w, h);
            ENGINE.finishPainting();
        }
    }

    //
    // MENU
    //
    public void paintMenuBackground(SynthContext context,
                                     Graphics g,
                                     int x, int y, int w, int h) {
        paintMenuItemBackground(context, g, x, y, w, h);
    }

    // This is called for both MENU and MENU_ITEM
    public void paintMenuItemBackground(SynthContext context,
                                     Graphics g,
                                     int x, int y, int w, int h) {
        int gtkState = GTKLookAndFeel.synthStateToGTKState(
                context.getRegion(), context.getComponentState());
        if (gtkState == SynthConstants.MOUSE_OVER) {
            Region id = Region.MENU_ITEM;
            synchronized (UNIXToolkit.GTK_LOCK) {
                if (! ENGINE.paintCachedImage(g, x, y, w, h, id)) {
                    ShadowType shadow = (GTKLookAndFeel.is2_2() ?
                        ShadowType.NONE : ShadowType.OUT);
                    ENGINE.startPainting(g, x, y, w, h, id);
                    ENGINE.paintBox(g, context, id, gtkState, shadow,
                            "menuitem", x, y, w, h);
                    ENGINE.finishPainting();
                }
            }
        }
    }

    public void paintPopupMenuBackground(SynthContext context, Graphics g,
                                        int x, int y, int w, int h) {
        Region id = context.getRegion();
        int gtkState = GTKLookAndFeel.synthStateToGTKState(
                id, context.getComponentState());
        synchronized (UNIXToolkit.GTK_LOCK) {
            if (ENGINE.paintCachedImage(g, x, y, w, h, id, gtkState)) {
                return;
            }
            ENGINE.startPainting(g, x, y, w, h, id, gtkState);
            ENGINE.paintBox(g, context, id, gtkState,
                    ShadowType.OUT, "menu", x, y, w, h);

            GTKStyle style = (GTKStyle)context.getStyle();
            int xThickness = style.getXThickness();
            int yThickness = style.getYThickness();
            ENGINE.paintBackground(g, context, id, gtkState,
                    style.getGTKColor(context, gtkState, GTKColorType.BACKGROUND),
                    x + xThickness, y + yThickness,
                    w - xThickness - xThickness, h - yThickness - yThickness);
            ENGINE.finishPainting();
        }
    }

    public void paintProgressBarBackground(SynthContext context,
                                            Graphics g,
                                            int x, int y, int w, int h) {
        Region id = context.getRegion();
        synchronized (UNIXToolkit.GTK_LOCK) {
            if (! ENGINE.paintCachedImage(g, x, y, w, h, id)) {
                ENGINE.startPainting(g, x, y, w, h, id);
                ENGINE.paintBox(g, context, id, SynthConstants.ENABLED,
                        ShadowType.IN, "trough", x, y, w, h);
                ENGINE.finishPainting();
            }
        }
    }

    public void paintProgressBarForeground(SynthContext context, Graphics g,
                                            int x, int y, int w, int h,
                                            int orientation) {
        Region id = context.getRegion();
        synchronized (UNIXToolkit.GTK_LOCK) {
            if (! ENGINE.paintCachedImage(g, x, y, w, h, id, "fg")) {
                ENGINE.startPainting(g, x, y, w, h, id, "fg");
                ENGINE.paintBox(g, context, id, SynthConstants.MOUSE_OVER,
                        ShadowType.OUT, "bar", x, y, w, h);
                ENGINE.finishPainting();
            }
        }
    }

    public void paintViewportBorder(SynthContext context, Graphics g,
                                           int x, int y, int w, int h) {
        Region id = context.getRegion();
        synchronized (UNIXToolkit.GTK_LOCK) {
            if (! ENGINE.paintCachedImage(g, x, y, w, h, id)) {
                ENGINE.startPainting(g, x, y, w, h, id);
                ENGINE.paintShadow(g, context, id, SynthConstants.ENABLED,
                        ShadowType.IN, "scrolled_window", x, y, w, h);
                ENGINE.finishPainting();
            }
        }
    }

    public void paintScrollPaneBorder(SynthContext context, Graphics g,
                                           int x, int y, int w, int h) {
        paintViewportBorder(context, g, x, y, w, h);
    }

    public void paintSeparatorBackground(SynthContext context,
                                          Graphics g,
                                          int x, int y, int w, int h) {
        Region id = context.getRegion();
        synchronized (UNIXToolkit.GTK_LOCK) {
            if (! ENGINE.paintCachedImage(g, x, y, w, h, id)) {
                ENGINE.startPainting(g, x, y, w, h, id);
                ENGINE.paintHline(g, context, id, SynthConstants.ENABLED,
                        "separator", x, y, w, h);
                ENGINE.finishPainting();
            }
        }
    }

    public void paintSliderTrackBackground(SynthContext context,
                                       Graphics g,
                                       int x, int y, int w,int h) {
        Region id = context.getRegion();
        int state = context.getComponentState();

        // For focused sliders, we paint focus rect outside the bounds passed.
        // Need to adjust for that.
        boolean focused = ((state & SynthConstants.FOCUSED) != 0);
        int focusSize = 0;
        if (focused) {
            GTKStyle style = (GTKStyle)context.getStyle();
            focusSize = style.getClassSpecificIntValue(
                                context, "focus-line-width", 1) +
                        style.getClassSpecificIntValue(
                                context, "focus-padding", 1);
            x -= focusSize;
            y -= focusSize;
            w += focusSize * 2;
            h += focusSize * 2;
        }

        synchronized (UNIXToolkit.GTK_LOCK) {
            if (! ENGINE.paintCachedImage(g, x, y, w, h, id, state)) {
                ENGINE.startPainting(g, x, y, w, h, id, state);
                int gtkState = GTKLookAndFeel.synthStateToGTKState(id, state);
                ENGINE.paintBox(g, context, id, gtkState, ShadowType.IN,
                        "trough", x + focusSize, y + focusSize,
                        w - 2 * focusSize, h - 2 * focusSize);

                if (focused) {
                    ENGINE.paintFocus(g, context, id, SynthConstants.ENABLED,
                            "trough", x, y, w, h);
                }
                ENGINE.finishPainting();
            }
        }
    }

    public void paintSliderThumbBackground(SynthContext context,
            Graphics g, int x, int y, int w, int h, int dir) {
        Region id = context.getRegion();
        int gtkState = GTKLookAndFeel.synthStateToGTKState(
                id, context.getComponentState());
        synchronized (UNIXToolkit.GTK_LOCK) {
            if (! ENGINE.paintCachedImage(g, x, y, w, h, id, gtkState, dir)) {
                Orientation orientation = (dir == JSlider.HORIZONTAL ?
                    Orientation.HORIZONTAL : Orientation.VERTICAL);
                String detail = (dir == JSlider.HORIZONTAL ?
                    "hscale" : "vscale");
                ENGINE.startPainting(g, x, y, w, h, id, gtkState, dir);
                ENGINE.paintSlider(g, context, id, gtkState,
                        ShadowType.OUT, detail, x, y, w, h, orientation);
                ENGINE.finishPainting();
            }
        }
    }

    //
    // SPINNER
    //
    public void paintSpinnerBackground(SynthContext context,
                                        Graphics g,
                                        int x, int y, int w, int h) {
        // This is handled in paintTextFieldBackground
    }

    //
    // SPLIT_PANE_DIVIDER
    //
    public void paintSplitPaneDividerBackground(SynthContext context,
                                       Graphics g,
                                       int x, int y, int w, int h) {
        Region id = context.getRegion();
        int gtkState = GTKLookAndFeel.synthStateToGTKState(
                id, context.getComponentState());
        JSplitPane splitPane = (JSplitPane)context.getComponent();
        Orientation orientation =
                (splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT ?
                    Orientation.VERTICAL : Orientation.HORIZONTAL);
        synchronized (UNIXToolkit.GTK_LOCK) {
            if (! ENGINE.paintCachedImage(g, x, y, w, h,
                    id, gtkState, orientation)) {
                ENGINE.startPainting(g, x, y, w, h, id, gtkState, orientation);
                ENGINE.paintHandle(g, context, id, gtkState,
                        ShadowType.OUT, "paned", x, y, w, h, orientation);
                ENGINE.finishPainting();
            }
        }
    }

    public void paintSplitPaneDragDivider(SynthContext context,
                                       Graphics g,int x, int y, int w, int h,
                                       int orientation) {
        paintSplitPaneDividerForeground(context, g, x, y, w, h, orientation);
    }

    public void paintTabbedPaneContentBackground(SynthContext context,
                                      Graphics g, int x, int y, int w, int h) {
        JTabbedPane pane = (JTabbedPane)context.getComponent();
        int selectedIndex = pane.getSelectedIndex();
        PositionType placement = GTKLookAndFeel.SwingOrientationConstantToGTK(
                                                        pane.getTabPlacement());

        int gapStart = 0;
        int gapSize = 0;
        if (selectedIndex != -1) {
            Rectangle tabBounds = pane.getBoundsAt(selectedIndex);

            if (placement == PositionType.TOP ||
                placement == PositionType.BOTTOM) {
                
                gapStart = tabBounds.x - 1;
                gapSize = tabBounds.width;
            }
            else {
                gapStart = tabBounds.y - 1;
                gapSize = tabBounds.height;
            }
        }

        Region id = context.getRegion();
        int gtkState = GTKLookAndFeel.synthStateToGTKState(
                id, context.getComponentState());
        synchronized (UNIXToolkit.GTK_LOCK) {
            if (! ENGINE.paintCachedImage(g, x, y, w, h,
                    id, gtkState, placement, gapStart, gapSize)) {
                ENGINE.startPainting(g, x, y, w, h,
                        id, gtkState, placement, gapStart, gapSize);
                ENGINE.paintBoxGap(g, context, id, gtkState, ShadowType.OUT,
                        "notebook", x, y, w, h, placement, gapStart, gapSize);
                ENGINE.finishPainting();
            }
        }
    }
    
    public void paintTabbedPaneTabBackground(SynthContext context,
                                           Graphics g,
                                           int x, int y, int w, int h,
                                           int tabIndex) {
        Region id = context.getRegion();
        int state = context.getComponentState();
        int gtkState = ((state & SynthConstants.SELECTED) != 0 ?
            SynthConstants.ENABLED : SynthConstants.PRESSED);
        JTabbedPane pane = (JTabbedPane)context.getComponent();
        int placement = pane.getTabPlacement();

        synchronized (UNIXToolkit.GTK_LOCK) {
            if (! ENGINE.paintCachedImage(g, x, y, w, h,
                    id, gtkState, placement, tabIndex)) {
                PositionType side = POSITIONS[placement - 1];
                ENGINE.startPainting(g, x, y, w, h,
                        id, gtkState, placement, tabIndex);
                ENGINE.paintExtension(g, context, id, gtkState,
                        ShadowType.OUT, "tab", x, y, w, h, side, tabIndex);
                ENGINE.finishPainting();
            }
        }
    }

    //
    // TEXT_AREA
    //
    public void paintTextAreaBackground(SynthContext context, Graphics g,
                                        int x, int y, int w, int h) {
        // Does not call into ENGINE for better performance
        fillArea(context, g, x, y, w, h, GTKColorType.TEXT_BACKGROUND);
    }

    //
    // TEXT_FIELD
    //
    // NOTE: Combobox and Label, Password and FormattedTextField calls this
    // too.
    private void paintTextBackground(SynthContext context, Graphics g,
                                     int x, int y, int w, int h) {
        // Text is odd in that it uses the TEXT_BACKGROUND vs BACKGROUND.
        JComponent c = context.getComponent();
        GTKStyle style = (GTKStyle)context.getStyle();
        if (c.isOpaque() && c.getBackground() instanceof ColorUIResource) {
            g.setColor(style.getGTKColor(context, SynthConstants.ENABLED,
                                         GTKColorType.TEXT_BACKGROUND));
            g.fillRect(x, y, w, h);
        }

        Region id = context.getRegion();
        int state = context.getComponentState();
        synchronized (UNIXToolkit.GTK_LOCK) {
            if (ENGINE.paintCachedImage(g, x, y, w, h, id, state)) {
                return;
            }
            
            int gtkState = GTKLookAndFeel.synthStateToGTKState(id, state);
            int focusSize = 0;
            boolean interiorFocus = style.getClassSpecificBoolValue(
                    context, "interior-focus", true);
            if (!interiorFocus && (state & SynthConstants.FOCUSED) != 0) {
                focusSize = style.getClassSpecificIntValue(context,
                        "focus-line-width",1);
                x += focusSize;
                y += focusSize;
                w -= 2 * focusSize;
                h -= 2 * focusSize;
            }
                
            int xThickness = style.getXThickness();
            int yThickness = style.getYThickness();
                
            ENGINE.startPainting(g, x, y, w, h, id, state);
            ENGINE.paintShadow(g, context, id, SynthConstants.ENABLED,
                    ShadowType.IN, "entry", x, y, w, h);
            ENGINE.paintFlatBox(g, context, id,
                    SynthConstants.ENABLED, ShadowType.NONE, "entry_bg",
                    x + xThickness, y + yThickness, w - (2 * xThickness),
                    h - (2 * yThickness), ColorType.TEXT_BACKGROUND);
                
            if (focusSize > 0) {
                x -= focusSize;
                y -= focusSize;
                w += 2 * focusSize;
                h += 2 * focusSize;
                ENGINE.paintFocus(g, context, id, gtkState,
                        "entry", x, y, w, h);
            }
            ENGINE.finishPainting();
        }
    }
            
    private void paintTreeCellEditorBackground(SynthContext context, Graphics g,
                                               int x, int y, int w, int h) {
        Region id = context.getRegion();
        int gtkState = GTKLookAndFeel.synthStateToGTKState(
                id, context.getComponentState());
        synchronized (UNIXToolkit.GTK_LOCK) {
            if (! ENGINE.paintCachedImage(g, x, y, w, h, id, gtkState)) {
                ENGINE.startPainting(g, x, y, w, h, id, gtkState);
                ENGINE.paintFlatBox(g, context, id, gtkState, ShadowType.NONE,
                        "entry_bg", x, y, w, h, ColorType.TEXT_BACKGROUND);
                ENGINE.finishPainting();
            }
        }
    }


    //
    // ROOT_PANE
    //
    public void paintRootPaneBackground(SynthContext context, Graphics g,
                                        int x, int y, int w, int h) {
        // Does not call into ENGINE for better performance
        fillArea(context, g, x, y, w, h, GTKColorType.BACKGROUND);
    }
    
    //
    // TOGGLE_BUTTON
    //
    public void paintToggleButtonBackground(SynthContext context,
                                            Graphics g,
                                            int x, int y, int w, int h) {
        Region id = context.getRegion();
        JToggleButton toggleButton = (JToggleButton)context.getComponent();
        boolean paintBG = toggleButton.isContentAreaFilled() &&
                          toggleButton.isBorderPainted();
        boolean paintFocus = toggleButton.isFocusPainted();
        paintButtonBackgroundImpl(context, g, id, "button",
                x, y, w, h, paintBG, paintFocus, false, false);
    }


    //
    // SCROLL_BAR
    //
    public void paintScrollBarBackground(SynthContext context,
                                          Graphics g,
                                          int x, int y, int w,int h) {
        Region id = context.getRegion();
        boolean focused = 
                (context.getComponentState() & SynthConstants.FOCUSED) != 0;
        synchronized (UNIXToolkit.GTK_LOCK) {
            if (ENGINE.paintCachedImage(g, x, y, w, h, id, focused)) {
                return;
            }
            ENGINE.startPainting(g, x, y, w, h, id, focused);

            GTKStyle style = (GTKStyle)context.getStyle();
            int focusSize = style.getClassSpecificIntValue(
                    context, "focus-line-width",1);
            int focusPad = style.getClassSpecificIntValue(
                    context, "focus-padding", 1);
            int totalFocus = focusSize + focusPad;
            ENGINE.paintBox(g, context, id, SynthConstants.PRESSED,
                    ShadowType.IN, "trough", x + totalFocus, y + totalFocus,
                    w - 2 * totalFocus, h - 2 * totalFocus);

            if (focused) {
                ENGINE.paintFocus(g, context, id,
                        SynthConstants.ENABLED, "trough", x, y, w, h);
            }
            ENGINE.finishPainting();
        }
    }


    //
    // SCROLL_BAR_THUMB
    //
    public void paintScrollBarThumbBackground(SynthContext context,
            Graphics g, int x, int y, int w, int h, int dir) {
        Region id = context.getRegion();
        int gtkState = GTKLookAndFeel.synthStateToGTKState(
                id, context.getComponentState());
        synchronized (UNIXToolkit.GTK_LOCK) {
            if (! ENGINE.paintCachedImage(g, x, y, w, h, id, gtkState, dir)) {
                ENGINE.startPainting(g, x, y, w, h, id, gtkState, dir);
                Orientation orientation = (dir == JScrollBar.HORIZONTAL ?
                    Orientation.HORIZONTAL : Orientation.VERTICAL);
                ENGINE.paintSlider(g, context, id, gtkState,
                        ShadowType.OUT, "slider", x, y, w, h, orientation);
                ENGINE.finishPainting();
            }
        }
    }

    public void paintToolBarContentBackground(SynthContext context,
            Graphics g, int x, int y, int w, int h) {
        Region id = context.getRegion();
        synchronized (UNIXToolkit.GTK_LOCK) {
            if (! ENGINE.paintCachedImage(g, x, y, w, h, id)) {
                ENGINE.startPainting(g, x, y, w, h, id);
                ENGINE.paintShadow(g, context, id, SynthConstants.ENABLED,
                        ShadowType.OUT, "frame", x, y, w, h);
                ENGINE.finishPainting();
            }
        }
    }

    //
    // TOOL_TIP
    //
    public void paintToolTipBackground(SynthContext context, Graphics g,
                                        int x, int y, int w,int h) {
        Region id = context.getRegion();
        synchronized (UNIXToolkit.GTK_LOCK) {
            if (! ENGINE.paintCachedImage(g, x, y, w, h, id)) {
                ENGINE.startPainting(g, x, y, w, h, id);
                ENGINE.paintFlatBox(g, context, id, SynthConstants.ENABLED,
                        ShadowType.OUT, "tooltip", x, y, w, h,
                        ColorType.BACKGROUND);
                ENGINE.finishPainting();
            }
        }
    }


    //
    // TREE_CELL
    //
    public void paintTreeCellBackground(SynthContext context, Graphics g,
                                        int x, int y, int w, int h) {
        Region id = context.getRegion();
        int state = context.getComponentState();
        int gtkState = GTKLookAndFeel.synthStateToGTKState(id, state);
        synchronized (UNIXToolkit.GTK_LOCK) {
            if (! ENGINE.paintCachedImage(g, x, y, w, h, id, state)) {
                ENGINE.startPainting(g, x, y, w, h, id, state);
                // the string arg should alternate based on row being painted,
                // but we currently don't pass that in.
                ENGINE.paintFlatBox(g, context, id, gtkState, ShadowType.NONE,
                        "cell_odd", x, y, w, h, ColorType.TEXT_BACKGROUND);
                ENGINE.finishPainting();
            }
        }
    }

    public void paintTreeCellFocus(SynthContext context, Graphics g,
                                    int x, int y, int w, int h) {
        Region id = Region.TREE_CELL;
        int state = context.getComponentState();
        paintFocus(context, g, id, state, "treeview", x, y, w, h);
    }


    //
    // TREE
    //
    public void paintTreeBackground(SynthContext context, Graphics g,
                                    int x, int y, int w, int h) {
        // As far as I can tell, these don't call into the ENGINE.
        fillArea(context, g, x, y, w, h, GTKColorType.TEXT_BACKGROUND);
    }


    //
    // VIEWPORT
    //
    public void paintViewportBackground(SynthContext context, Graphics g,
                                        int x, int y, int w, int h) {
        // As far as I can tell, these don't call into the ENGINE.
        // Also note that you don't want this to call into the ENGINE
        // as if it where to paint a background JViewport wouldn't scroll
        // correctly.
        fillArea(context, g, x, y, w, h, GTKColorType.TEXT_BACKGROUND);
    }

    void paintFocus(SynthContext context, Graphics g, Region id,
            int state, String detail, int x, int y, int w, int h) {
        int gtkState = GTKLookAndFeel.synthStateToGTKState(id, state);
        synchronized (UNIXToolkit.GTK_LOCK) {
            if (! ENGINE.paintCachedImage(g, x, y, w, h, id, gtkState, "focus")) {
                ENGINE.startPainting(g, x, y, w, h, id, gtkState, "focus");
                ENGINE.paintFocus(g, context, id, gtkState, detail, x, y, w, h);
                ENGINE.finishPainting();
            }
        }
    }

    void paintMetacityElement(SynthContext context, Graphics g,
            int gtkState, String detail, int x, int y, int w, int h,
            ShadowType shadow, ArrowType direction) {
        synchronized (UNIXToolkit.GTK_LOCK) {
            if (! ENGINE.paintCachedImage(
                    g, x, y, w, h, gtkState, detail, shadow, direction)) {
                ENGINE.startPainting(
                        g, x, y, w, h, gtkState, detail, shadow, direction);
                if (detail == "metacity-arrow") {
                    ENGINE.paintArrow(g, context, Region.INTERNAL_FRAME_TITLE_PANE,
                            gtkState, shadow, direction, "", x, y, w, h);
                    
                } else if (detail == "metacity-box") {
                    ENGINE.paintBox(g, context, Region.INTERNAL_FRAME_TITLE_PANE,
                            gtkState, shadow, "", x, y, w, h);
                    
                } else if (detail == "metacity-vline") {
                    ENGINE.paintVline(g, context, Region.INTERNAL_FRAME_TITLE_PANE,
                            gtkState, "", x, y, w, h);
                }
                ENGINE.finishPainting();
            }
        }
    }

    void paintIcon(SynthContext context, Graphics g,
            Method paintMethod, int x, int y, int w, int h) {
        int state = context.getComponentState();
        synchronized (UNIXToolkit.GTK_LOCK) {
            if (! ENGINE.paintCachedImage(g, x, y, w, h, state, paintMethod)) {
                ENGINE.startPainting(g, x, y, w, h, state, paintMethod);
                try {
                    paintMethod.invoke(this, context, g, state, x, y, w, h);
                } catch (IllegalAccessException iae) {
                    assert false;
                } catch (InvocationTargetException ite) {
                    assert false;
                }
                ENGINE.finishPainting();
            }
        }
    }

    void paintIcon(SynthContext context, Graphics g,
            Method paintMethod, int x, int y, int w, int h, Object direction) {
        int state = context.getComponentState();
        synchronized (UNIXToolkit.GTK_LOCK) {
            if (! ENGINE.paintCachedImage(g,
                    x, y, w, h, state, paintMethod, direction)) {
                ENGINE.startPainting(g,
                        x, y, w, h, state, paintMethod, direction);
                try {
                    paintMethod.invoke(this, context,
                            g, state, x, y, w, h, direction);
                } catch (IllegalAccessException iae) {
                    assert false;
                } catch (InvocationTargetException ite) {
                    assert false;
                }
                ENGINE.finishPainting();
            }
        }
    }

    // All icon painting methods are called from under GTK_LOCK
    
    public void paintTreeExpandedIcon(SynthContext context,
            Graphics g, int state, int x, int y, int w, int h) {
        ENGINE.paintExpander(g, context, Region.TREE,
                GTKLookAndFeel.synthStateToGTKState(context.getRegion(), state),
                ExpanderStyle.EXPANDED, "treeview", x, y, w, h);
    }

    public void paintTreeCollapsedIcon(SynthContext context,
            Graphics g, int state, int x, int y, int w, int h) {
        ENGINE.paintExpander(g, context, Region.TREE,
                GTKLookAndFeel.synthStateToGTKState(context.getRegion(), state),
                ExpanderStyle.COLLAPSED, "treeview", x, y, w, h);
    }

    public void paintCheckBoxIcon(SynthContext context,
            Graphics g, int state, int x, int y, int w, int h) {
        ShadowType shadowType = ShadowType.OUT;
        if (((JCheckBox)context.getComponent()).isSelected()) {
            shadowType = ShadowType.IN;
        }
        ENGINE.paintCheck(g, context, Region.CHECK_BOX,
                GTKLookAndFeel.synthStateToGTKState(context.getRegion(), state),
                shadowType, "checkbutton", x, y, w, h);
    }
    
    public void paintRadioButtonIcon(SynthContext context,
            Graphics g, int state, int x, int y, int w, int h) {
        int gtkState = GTKLookAndFeel.synthStateToGTKState(
                context.getRegion(), state);
        ShadowType shadowType = ShadowType.OUT;
        // RadioButton painting appears to be special cased to pass
        // SELECTED into the ENGINE even though text colors are PRESSED.
        if ((state & SynthConstants.SELECTED) != 0) {
            gtkState = SynthConstants.SELECTED;
        }
        if (gtkState == SynthConstants.SELECTED) {
            shadowType = ShadowType.IN;
        }
        ENGINE.paintOption(g, context, Region.RADIO_BUTTON, gtkState,
                shadowType, "radiobutton", x, y, w, h);
    }
    
    public void paintMenuArrowIcon(SynthContext context, Graphics g,
            int state, int x, int y, int w, int h, ArrowType dir) {
        int gtkState = GTKLookAndFeel.synthStateToGTKState(
                context.getRegion(), state);
        ShadowType shadow = ShadowType.OUT;
        if (gtkState == SynthConstants.MOUSE_OVER) {
            shadow = ShadowType.IN;
        }
        ENGINE.paintArrow(g, context, Region.MENU_ITEM, gtkState, shadow,
                dir, "menuitem", x + 3, y + 3, 7, 7);
    }
    
    public void paintMenuItemArrowIcon(SynthContext context,
            Graphics g, int state, int x, int y, int w, int h) {
        // Don't paint anything.  We are just reserving space so we align the
        // menu items correctly.
    }
    
    public void paintCheckBoxMenuItemArrowIcon(SynthContext context,
            Graphics g, int state, int x, int y, int w, int h) {
        // Don't paint anything.  We are just reserving space so we align the
        // menu items correctly.
    }
    
    public void paintRadioButtonMenuItemArrowIcon(SynthContext context,
            Graphics g, int state, int x, int y, int w, int h) {
        // Don't paint anything.  We are just reserving space so we align the
        // menu items correctly.
    }
    
    public void paintCheckBoxMenuItemCheckIcon(SynthContext context,
            Graphics g, int state, int x, int y, int w, int h) {
        ShadowType shadowType = ShadowType.OUT;
        int gtkState = GTKLookAndFeel.synthStateToGTKState(
                context.getRegion(), state);
        if ((state & SynthConstants.MOUSE_OVER) != 0) {
            gtkState = SynthConstants.MOUSE_OVER;
        }
        if ((state & SynthConstants.SELECTED) != 0) {
            shadowType = ShadowType.IN;
        }
        ENGINE.paintCheck(g, context, Region.CHECK_BOX_MENU_ITEM,
                gtkState, shadowType, "check", x, y, w, h);
    }
    
    public void paintRadioButtonMenuItemCheckIcon(SynthContext context,
            Graphics g, int state, int x, int y, int w, int h) {
        int gtkState = GTKLookAndFeel.synthStateToGTKState(
                context.getRegion(), state);
        if ((state & SynthConstants.MOUSE_OVER) != 0) {
            gtkState = SynthConstants.MOUSE_OVER;
        }
        ShadowType shadowType = ShadowType.OUT;
        if ((state & SynthConstants.SELECTED) != 0) {
            shadowType = ShadowType.IN;
        }
        ENGINE.paintOption(g, context, Region.RADIO_BUTTON_MENU_ITEM,
                gtkState, shadowType, "option", x, y, w, h);
    }
    
    public void paintToolBarHandleIcon(SynthContext context, Graphics g,
            int state, int x, int y, int w, int h, Orientation orientation) {
        int gtkState = GTKLookAndFeel.synthStateToGTKState(
                context.getRegion(), state);
        ENGINE.paintHandle(g, context, Region.TOOL_BAR, gtkState,
                ShadowType.OUT, "handlebox", x, y, w, h, orientation);
    }
    
    public void paintAscendingSortIcon(SynthContext context,
            Graphics g, int state, int x, int y, int w, int h) {
        ENGINE.paintArrow(g, context, Region.TABLE, SynthConstants.ENABLED,
                ShadowType.IN, ArrowType.UP, "arrow", x, y, w, h);
    }
    
    public void paintDescendingSortIcon(SynthContext context,
            Graphics g, int state, int x, int y, int w, int h) {
        ENGINE.paintArrow(g, context, Region.TABLE, SynthConstants.ENABLED,
                ShadowType.IN, ArrowType.DOWN, "arrow", x, y, w, h);
    }

    /*
     * Fill an area with color determined from this context's Style using the
     * specified GTKColorType
     */
    private void fillArea(SynthContext context, Graphics g,
                          int x, int y, int w, int h, ColorType colorType) {
        if (context.getComponent().isOpaque()) {
            Region id = context.getRegion();
            int gtkState = GTKLookAndFeel.synthStateToGTKState(id,
                    context.getComponentState());
            GTKStyle style = (GTKStyle)context.getStyle();
            
            g.setColor(style.getGTKColor(context, gtkState, colorType));
            g.fillRect(x, y, w, h);
        }
    }

    // Refer to GTKLookAndFeel for details on this.
    static class ListTableFocusBorder extends AbstractBorder implements
                          UIResource {

        private boolean selectedCell;

        public static ListTableFocusBorder getSelectedCellBorder() {
            return new ListTableFocusBorder(true); 
        }
        
        public static ListTableFocusBorder getUnselectedCellBorder() {
            return new ListTableFocusBorder(false); 
        }
        
        public ListTableFocusBorder(boolean selectedCell) {
            this.selectedCell = selectedCell;
        }

        private SynthContext getContext(Component c) {
            SynthContext context = null;
            
            Component parent = c;
            while(parent != null && 
                    !(parent instanceof JTable) && 
                    !(parent instanceof JList)) {
                parent = parent.getParent();
            }
             
            ComponentUI ui = null;
            if (parent instanceof JTable) {
                ui = ((JTable)parent).getUI();
            } else if (parent instanceof JList) {
                ui = ((JList)parent).getUI();
            }
            
            if (ui instanceof SynthUI) {
                context = ((SynthUI)ui).getContext((JComponent)parent);
            }
            
            return context;
        }
        
        public void paintBorder(Component c, Graphics g, int x, int y,
                                int w, int h) {
            SynthContext context = getContext(c);
            int state = (selectedCell? SynthConstants.SELECTED:
                         SynthConstants.FOCUSED | SynthConstants.ENABLED);

            if (context != null) {
                GTKPainter.INSTANCE.paintFocus(context, g,
                        Region.TABLE, state, "", x, y, w, h);
            } else {
                if (ENGINE instanceof GTKDefaultEngine) {
                    g.setColor(Color.BLACK);
                    ((GTKDefaultEngine)ENGINE)._paintFocus(
                            g, x, y, w, h,
                            GTKDefaultEngine.DEFAULT_FOCUS_PATTERN, 1);
                }
            }
        }
        
        public Insets getBorderInsets(Component c) {
            int size = 1;
            SynthContext context = getContext(c);
            if (context != null) {
                size = ((GTKStyle)context.getStyle()).getClassSpecificIntValue(
                    context, "focus-line-width", 1);   
            }
            return new Insets(size, size, size, size);
        }
        
        public Insets getBorderInsets(Component c, Insets i) {
            Insets ins = getBorderInsets(c);
            if (i == null) {
                return ins; 
            }
            i.left = ins.left;
            i.right = ins.right;
            i.top = ins.top;
            i.bottom = ins.bottom;
            return i;
        }
        
        public boolean isBorderOpaque() {
            return true;
        }
    }
}
