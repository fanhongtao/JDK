/*
 * @(#)GTKIconFactory.java	1.23 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import javax.swing.plaf.synth.*;
import java.awt.*;
import java.lang.reflect.*;
import javax.swing.*;
import javax.swing.plaf.*;
import sun.swing.plaf.synth.*;

/**
 * @version 1.23, 12/19/03
 */
class GTKIconFactory {
    // Tree icons
    private static SynthIcon expandedIcon;
    private static SynthIcon collapsedIcon;

    private static SynthIcon radioButtonIcon;

    private static SynthIcon checkBoxIcon;

    private static SynthIcon menuArrowIcon;
    private static SynthIcon menuCheckIcon;

    private static SynthIcon menuItemArrowIcon;
    private static SynthIcon menuItemCheckIcon;

    private static SynthIcon checkBoxMenuItemArrowIcon;
    private static SynthIcon checkBoxMenuItemCheckIcon;

    private static SynthIcon radioButtonMenuItemArrowIcon;
    private static SynthIcon radioButtonMenuItemCheckIcon;

    //
    // Tree methods
    // 
    public static SynthIcon getTreeExpandedIcon() {
        if (expandedIcon == null) {
            expandedIcon =
                new SynthExpanderIcon("paintTreeExpandedIcon");
        }
        return expandedIcon;
    }

    public static void paintTreeExpandedIcon(SynthContext context, Graphics g,
                                      int x, int y, int w, int h) {
        ((GTKStyle)context.getStyle()).getEngine(context).paintExpander(
               context, g, GTKLookAndFeel.synthStateToGTKState(
               context.getRegion(), context.getComponentState()),
               GTKConstants.EXPANDER_EXPANDED, "treeview", x, y, w, h);
    }

    public static SynthIcon getTreeCollapsedIcon() {
        if (collapsedIcon == null) {
            collapsedIcon =
                new SynthExpanderIcon("paintTreeCollapsedIcon");
        }
        return collapsedIcon;
    }


    public static void paintTreeCollapsedIcon(SynthContext context, Graphics g,
                                      int x, int y, int w, int h) {
        ((GTKStyle)context.getStyle()).getEngine(context).paintExpander(
               context, g, GTKLookAndFeel.synthStateToGTKState(
               context.getRegion(), context.getComponentState()),
               GTKConstants.EXPANDER_COLLAPSED, "treeview", x, y, w, h);
    }

    //
    // Radio button
    //
    public static SynthIcon getRadioButtonIcon() {
        if (radioButtonIcon == null) {
            radioButtonIcon = new DelegatingIcon("paintRadioButtonIcon",
                                                 13, 13);
        }
        return radioButtonIcon;
    }

    public static void paintRadioButtonIcon(SynthContext context, Graphics g,
                                            int x, int y, int w, int h) {
        GTKStyle style = (GTKStyle)context.getStyle();
        int state = context.getComponentState();
        int gtkState = GTKLookAndFeel.synthStateToGTKState(context.getRegion(),
                                                           state);
        int shadowType = GTKConstants.SHADOW_OUT;
        // RadioButton painting appears to be special cased to pass
        // SELECTED into the engine even though text colors are PRESSED.
        if ((state & SynthConstants.SELECTED) != 0) {
            gtkState = SynthConstants.SELECTED;
        }
        if (gtkState == SynthConstants.SELECTED) {
            shadowType = GTKConstants.SHADOW_IN;
        }
        ((GTKStyle)context.getStyle()).getEngine(
                context).paintOption(context, g, gtkState, shadowType,
                    "radiobutton", x, y, w, h);
    }

    //
    // CheckBox
    //
    public static SynthIcon getCheckBoxIcon() {
        if (checkBoxIcon == null) {
            checkBoxIcon = new DelegatingIcon("paintCheckBoxIcon", 13, 13);
        }
        return checkBoxIcon;
    }

    public static void paintCheckBoxIcon(SynthContext context, Graphics g,
                                         int x, int y, int w, int h) {
        GTKStyle style = (GTKStyle)context.getStyle();
        int state = context.getComponentState();
        int shadowType = GTKConstants.SHADOW_OUT;
        if (((JCheckBox)context.getComponent()).isSelected()) {
            shadowType = GTKConstants.SHADOW_IN;
        }
        ((GTKStyle)context.getStyle()).getEngine(
                context).paintCheck(context, g,
                GTKLookAndFeel.synthStateToGTKState(context.getRegion(),
                context.getComponentState()), shadowType, "checkbutton",
                x, y, w, h);
    }

    //
    // Menus
    // 
    public static SynthIcon getMenuArrowIcon() {
        if (menuArrowIcon == null) {
            menuArrowIcon = new DelegatingIcon("paintMenuArrowIcon", 13, 13);
        }
        return menuArrowIcon;
    }

    public static void paintMenuArrowIcon(SynthContext context, Graphics g,
                                          int x, int y, int w, int h) {
        GTKStyle style = (GTKStyle)context.getStyle();
        int gtkState = GTKLookAndFeel.synthStateToGTKState(
                context.getRegion(), context.getComponentState());
        int shadow = GTKConstants.SHADOW_OUT;
        if (gtkState == SynthConstants.MOUSE_OVER) {
            shadow = GTKConstants.SHADOW_IN;
        }
        int arrowDir;
        if (context.getComponent().getComponentOrientation().isLeftToRight()) {
            arrowDir = GTKConstants.ARROW_RIGHT;
        }
        else {
            arrowDir = GTKConstants.ARROW_LEFT;
        }
        style.getEngine(context).paintArrow(context, g, gtkState,
             shadow, arrowDir, "menuitem", x + 3, y + 3, 7, 7);
    }

    public static SynthIcon getMenuItemArrowIcon() {
        if (menuItemArrowIcon == null) {
            menuItemArrowIcon = new DelegatingIcon("paintMenuItemArrowIcon",
                                                   13, 13);
        }
        return menuItemArrowIcon;
    }

    public static void paintMenuItemArrowIcon(SynthContext context, Graphics g,
                                          int x, int y, int w, int h) {
        // Don't paint anything.  We are just reserving space so we align the
        // menu items correctly.
    }

    public static SynthIcon getCheckBoxMenuItemArrowIcon() {
        if (checkBoxMenuItemArrowIcon == null) {
            checkBoxMenuItemArrowIcon = new DelegatingIcon(
                "paintCheckBoxMenuItemArrowIcon", 13, 13);
        }
        return checkBoxMenuItemArrowIcon;
    }

    public static void paintCheckBoxMenuItemArrowIcon(SynthContext context,
                            Graphics g, int x, int y, int w, int h) {
        // Don't paint anything.  We are just reserving space so we align the
        // menu items correctly.
    }

    public static SynthIcon getCheckBoxMenuItemCheckIcon() {
        if (checkBoxMenuItemCheckIcon == null) {
            checkBoxMenuItemCheckIcon = new DelegatingIcon(
                "paintCheckBoxMenuItemCheckIcon", 13, 13);
        }
        return checkBoxMenuItemCheckIcon;
    }

    public static void paintCheckBoxMenuItemCheckIcon(
             SynthContext context, Graphics g, int x, int y, int w, int h) {
        GTKStyle style = (GTKStyle)context.getStyle();
        int state = context.getComponentState();
        int shadowType = GTKConstants.SHADOW_OUT;
        int gtkState = GTKLookAndFeel.synthStateToGTKState(
                                      context.getRegion(), state);
        if ((state & SynthConstants.MOUSE_OVER) != 0) {
            gtkState = SynthConstants.MOUSE_OVER;
        }
        if ((state & SynthConstants.SELECTED) != 0) {
            shadowType = GTKConstants.SHADOW_IN;
        }
        style.getEngine(context).paintCheck(context, g, gtkState,
                    shadowType, "check", x, y, w, h);
    }

    public static SynthIcon getRadioButtonMenuItemArrowIcon() {
        if (radioButtonMenuItemArrowIcon == null) {
            radioButtonMenuItemArrowIcon = new DelegatingIcon(
                "paintRadioButtonMenuItemArrowIcon", 13, 13);
        }
        return radioButtonMenuItemArrowIcon;
    }

    public static void paintRadioButtonMenuItemArrowIcon(SynthContext context,
                            Graphics g, int x, int y, int w, int h) {
        // Don't paint anything.  We are just reserving space so we align the
        // menu items correctly.
    }

    public static SynthIcon getRadioButtonMenuItemCheckIcon() {
        if (radioButtonMenuItemCheckIcon == null) {
            radioButtonMenuItemCheckIcon = new DelegatingIcon(
                "paintRadioButtonMenuItemCheckIcon", 13, 13);
        }
        return radioButtonMenuItemCheckIcon;
    }

    public static void paintRadioButtonMenuItemCheckIcon(
             SynthContext context, Graphics g, int x, int y, int w, int h) {
        GTKStyle style = (GTKStyle)context.getStyle();
        int state = context.getComponentState();
        int gtkState = GTKLookAndFeel.synthStateToGTKState(
                                      context.getRegion(), state);
        if ((state & SynthConstants.MOUSE_OVER) != 0) {
            gtkState = SynthConstants.MOUSE_OVER;
        }
        int shadowType = GTKConstants.SHADOW_OUT;
        if ((state & SynthConstants.SELECTED) != 0) {
            shadowType = GTKConstants.SHADOW_IN;
        }
        ((GTKStyle)context.getStyle()).getEngine(
                context).paintOption(context, g, gtkState, shadowType,
                    "option", x, y, w, h);
    }

    //
    // ToolBar Handle
    // 
    public static SynthIcon getToolBarHandleIcon() {
        return new ToolBarHandleIcon();
    }

    public static void paintToolBarHandleIcon(SynthContext context,
            Graphics g, int x, int y, int w, int h) {
        int orientation =
            ((JToolBar)context.getComponent()).getOrientation() ==
                JToolBar.HORIZONTAL ?
                    GTKConstants.HORIZONTAL : GTKConstants.VERTICAL;
        GTKStyle style = (GTKStyle)context.getStyle();
        int gtkState = GTKLookAndFeel.synthStateToGTKState(
                context.getRegion(), context.getComponentState());
        style.getEngine(context).paintHandle(context, g, gtkState,
                    GTKConstants.SHADOW_OUT, "handlebox", x, y, w, h,
                    orientation);
    }


    private static class DelegatingIcon extends SynthIcon implements
                                   UIResource {
        private static final Class[] PARAM_TYPES = new Class[] {
            SynthContext.class, Graphics.class, int.class, int.class,
            int.class, int.class };
        private int width;
        private int height;
        private Object method;

        DelegatingIcon(String methodName, int width, int height) {
            this.method = methodName;
            this.width = width;
            this.height = height;
        }

        public void paintIcon(SynthContext context, Graphics g, int x, int y,
                              int w, int h) {
            if (context != null) {
                try {
                    getMethod().invoke(GTKIconFactory.class, new Object[] {
                                context, g, new Integer(x), new Integer(y),
                                new Integer(w), new Integer(h) });
                } catch (IllegalAccessException iae) {
                } catch (InvocationTargetException ite) {
                }
            }
        }

        public int getIconWidth(SynthContext context) {
            return width;
        }

        public int getIconHeight(SynthContext context) {
            return height;
        }

        private Method getMethod() {
            if (method instanceof String) {
                Method[] methods = GTKIconFactory.class.getMethods();
                try {
                    method = GTKIconFactory.class.getMethod((String)method,
                                                            PARAM_TYPES);
                } catch (NoSuchMethodException nsme) {
                    System.out.println("NSME: " + nsme);
                }
            }
            return (Method)method;
        }
    }

    private static class SynthExpanderIcon extends SynthIcon {
        private static final Class[] PARAM_TYPES = new Class[] {
            SynthContext.class, Graphics.class, int.class, int.class,
            int.class, int.class };

            private int width = -1;
            private int height = -1;
            private Object method;

        SynthExpanderIcon(String method) {
            this.method = method;
        }

        public void paintIcon(SynthContext context, Graphics g, int x, int y,
                              int w, int h) {
            if (context != null) {
                try {
                    getMethod().invoke(GTKIconFactory.class, new Object[] {
                                context, g, new Integer(x), new Integer(y),
                                new Integer(w), new Integer(h) });
                    updateSizeIfNecessary(context);
                } catch (IllegalAccessException iae) {
                } catch (InvocationTargetException ite) {
                }
            }
        }

        public int getIconWidth(SynthContext context) {
            updateSizeIfNecessary(context);
            return width;
        }

        public int getIconHeight(SynthContext context) {
            updateSizeIfNecessary(context);
            return height;
        }

        private void updateSizeIfNecessary(SynthContext context) {
            if (width == -1 && context != null) {
                width = height = context.getStyle().getInt(context,
                        "Tree.expanderSize", 10);
            }
        }

        private Method getMethod() {
            if (method instanceof String) {
                Method[] methods = GTKIconFactory.class.getMethods();
                try {
                    method = GTKIconFactory.class.getMethod((String)method,
                                                            PARAM_TYPES);
                } catch (NoSuchMethodException nsme) {
                    System.out.println("NSME: " + nsme);
                }
            }
            return (Method)method;
        }
    }


    // GTK has a separate widget for the handle box, to mirror this
    // we create a unique icon per ToolBar and lookup the style for the
    // HandleBox.
    private static class ToolBarHandleIcon extends SynthIcon {
        private SynthStyle style;

        public void paintIcon(SynthContext context, Graphics g, int x, int y,
                              int w, int h) {
            if (context != null) {
                context = getContext(context);
                paintToolBarHandleIcon(context, g, x, y, w, h);
            }
        }

        public int getIconWidth(SynthContext context) {
            if (((JToolBar)context.getComponent()).getOrientation() ==
                    JToolBar.HORIZONTAL) {
                return 10;
            } else {
                return context.getComponent().getWidth();
            }
        }

        public int getIconHeight(SynthContext context) {
            if (((JToolBar)context.getComponent()).getOrientation() ==
                    JToolBar.HORIZONTAL) {
                return context.getComponent().getHeight();
            } else {
                return 10;
            }
        }

        private SynthContext getContext(SynthContext context) {
            if (style == null) {
                style = SynthLookAndFeel.getStyleFactory().getStyle(
                             context.getComponent(), GTKRegion.HANDLE_BOX);
            }
            return new SynthContext(context.getComponent(),
                                    GTKRegion.HANDLE_BOX,
                                    style, SynthConstants.ENABLED);
        }
    }
}
