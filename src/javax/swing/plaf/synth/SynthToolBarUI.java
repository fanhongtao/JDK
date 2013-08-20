/*
 * @(#)SynthToolBarUI.java	1.10 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.synth;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import java.beans.*;

import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicToolBarUI;
import sun.swing.plaf.synth.*;


/**
 * A Synth L&F implementation of ToolBarUI.  This implementation 
 * is a "combined" view/controller.
 * <p>
 *
 * @version 1.10, 12/19/03
 * @author Georges Saab
 * @author Jeff Shapiro
 */
class SynthToolBarUI extends BasicToolBarUI implements PropertyChangeListener,
           SynthUI {
    protected Icon handleIcon = null;
    protected Rectangle contentRect = new Rectangle();

    private SynthStyle style;
    private SynthStyle contentStyle;
    private SynthStyle dragWindowStyle;

    public static ComponentUI createUI(JComponent c) {
	return new SynthToolBarUI();
    }

    protected void installDefaults() {
        toolBar.setLayout(createLayout());
        updateStyle(toolBar);
    }

    protected void installListeners() {
        super.installListeners();
        toolBar.addPropertyChangeListener(this);
    }

    protected void uninstallListeners() {
        super.uninstallListeners();
        toolBar.removePropertyChangeListener(this);
    }

    private void updateStyle(JToolBar c) {
        SynthContext context = getContext(c, ENABLED);
        SynthStyle oldStyle = style;

        style = SynthLookAndFeel.updateStyle(context, this);
        if (oldStyle != style) {
            handleIcon =
                style.getIcon(context, "ToolBar.handleIcon");
            if (oldStyle != null) {
                uninstallKeyboardActions();
                installKeyboardActions();
            }
        }
        context.dispose();

        context = getContext(c, Region.TOOL_BAR_CONTENT, ENABLED);
        contentStyle = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();

        context = getContext(c, Region.TOOL_BAR_DRAG_WINDOW, ENABLED);
        dragWindowStyle = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
    }

    protected void uninstallDefaults() {
        SynthContext context = getContext(toolBar, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;

        handleIcon = null;

        context = getContext(toolBar, Region.TOOL_BAR_CONTENT, ENABLED);
        contentStyle.uninstallDefaults(context);
        context.dispose();
        contentStyle = null;

        context = getContext(toolBar, Region.TOOL_BAR_DRAG_WINDOW, ENABLED);
        dragWindowStyle.uninstallDefaults(context);
        context.dispose();
        dragWindowStyle = null;

        toolBar.setLayout(null);
    }

    protected void installComponents() {
    }

    protected void uninstallComponents() {
    }

    protected LayoutManager createLayout() {
        return new SynthToolBarLayoutManager();
    }

    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    private SynthContext getContext(JComponent c, int state) {
        return SynthContext.getContext(SynthContext.class, c,
                    SynthLookAndFeel.getRegion(c), style, state);
    }

    private SynthContext getContext(JComponent c, Region region) {
        return getContext(c, region, getComponentState(c, region));
    }

    private SynthContext getContext(JComponent c, Region region, int state) {
        return SynthContext.getContext(SynthContext.class, c, region,
                                       dragWindowStyle, state);
    }

    private Region getRegion(JComponent c) {
        return SynthLookAndFeel.getRegion(c);
    }

    private int getComponentState(JComponent c) {
        return SynthLookAndFeel.getComponentState(c);
    }

    private int getComponentState(JComponent c, Region region) {
        return SynthLookAndFeel.getComponentState(c);
    }

    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        context.getPainter().paintToolBarBackground(context,
                          g, 0, 0, c.getWidth(), c.getHeight());
        paint(context, g);
        context.dispose();
    }

    public void paint(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        paint(context, g);
        context.dispose();
    }

    public void paintBorder(SynthContext context, Graphics g, int x,
                            int y, int w, int h) {
        context.getPainter().paintToolBarBorder(context, g, x, y, w, h);
    }

    // Overloaded to do nothing so we can share listeners.
    protected void setBorderToNonRollover(Component c) {}

    // Overloaded to do nothing so we can share listeners.
    protected void setBorderToRollover(Component c) {}

    // Overloaded to do nothing so we can share listeners.
    protected void setBorderToNormal(Component c) {}

    protected void paint(SynthContext context, Graphics g) {
        if (handleIcon != null && toolBar.isFloatable()) {
            int startX = toolBar.getComponentOrientation().isLeftToRight() ?
                0 : toolBar.getWidth() -
                    SynthIcon.getIconWidth(handleIcon, context);
            SynthIcon.paintIcon(handleIcon, context, g, startX, 0,
                    SynthIcon.getIconWidth(handleIcon, context),
                    SynthIcon.getIconHeight(handleIcon, context));
        }

        SynthContext subcontext = getContext(toolBar, Region.TOOL_BAR_CONTENT);
        paintContent(subcontext, g, contentRect);
        subcontext.dispose();
    }

    public void paintContent(SynthContext context, Graphics g,
            Rectangle bounds) {
        SynthLookAndFeel.updateSubregion(context, g, bounds);
        context.getPainter().paintToolBarContentBackground(context, g,
                             bounds.x, bounds.y, bounds.width, bounds.height);
        context.getPainter().paintToolBarContentBorder(context, g,
                             bounds.x, bounds.y, bounds.width, bounds.height);
    }

    protected void paintDragWindow(Graphics g) {
        int w = dragWindow.getWidth();
        int h = dragWindow.getHeight();
        SynthContext context = getContext(toolBar,Region.TOOL_BAR_DRAG_WINDOW);
        SynthLookAndFeel.updateSubregion(context, g, new Rectangle(
                         0, 0, w, h));
        context.getPainter().paintToolBarDragWindowBackground(context,
                                                           g, 0, 0, w, h);
        context.getPainter().paintToolBarDragWindowBorder(context, g, 0,0,w,h);
        context.dispose();
    }

    //
    // PropertyChangeListener
    //

    public void propertyChange(PropertyChangeEvent e) {
        if (SynthLookAndFeel.shouldUpdateStyle(e)) {
            updateStyle((JToolBar)e.getSource());
        }
    }


    class SynthToolBarLayoutManager implements LayoutManager {
        public void addLayoutComponent(String name, Component comp) {}

        public void removeLayoutComponent(Component comp) {}

        public Dimension minimumLayoutSize(Container parent) {
            JToolBar tb = (JToolBar)parent;
            Dimension dim = new Dimension();
            SynthContext context = getContext(tb);

            if (tb.getOrientation() == JToolBar.HORIZONTAL) {
                dim.width = SynthIcon.getIconWidth(handleIcon, context);
                Dimension compDim;
                for (int i = 0; i < tb.getComponentCount(); i++) {
                    compDim = tb.getComponent(i).getMinimumSize();
                    dim.width += compDim.width;
                    dim.height = Math.max(dim.height, compDim.height);
                }
            } else {
                dim.height =
                    SynthIcon.getIconHeight(handleIcon, context);
                Dimension compDim;
                for (int i = 0; i < tb.getComponentCount(); i++) {
                    compDim = tb.getComponent(i).getMinimumSize();
                    dim.width = Math.max(dim.width, compDim.width);
                    dim.height += compDim.height;
                }
            }
            context.dispose();
            return dim;
        }

        public Dimension preferredLayoutSize(Container parent) {
            JToolBar tb = (JToolBar)parent;
            Dimension dim = new Dimension();
            SynthContext context = getContext(tb);

            if (tb.getOrientation() == JToolBar.HORIZONTAL) {
                dim.width = SynthIcon.getIconWidth(handleIcon, context);
                Dimension compDim;
                for (int i = 0; i < tb.getComponentCount(); i++) {
                    compDim = tb.getComponent(i).getPreferredSize();
                    dim.width += compDim.width;
                    dim.height = Math.max(dim.height, compDim.height);
                }
            } else {
                dim.height =
                    SynthIcon.getIconHeight(handleIcon, context);
                Dimension compDim;
                for (int i = 0; i < tb.getComponentCount(); i++) {
                    compDim = tb.getComponent(i).getPreferredSize();
                    dim.width = Math.max(dim.width, compDim.width);
                    dim.height += compDim.height;
                }
            }
            context.dispose();
            return dim;
        }

        public void layoutContainer(Container parent) {
            JToolBar tb = (JToolBar)parent;
            boolean ltr = tb.getComponentOrientation().isLeftToRight();
            SynthContext context = getContext(tb);
            int handleWidth = SynthIcon.getIconWidth(handleIcon, context);

            Component c;
            Dimension d;
            if (tb.getOrientation() == JToolBar.HORIZONTAL) {
                int x = ltr ? handleWidth : tb.getWidth() - handleWidth;
                for (int i = 0; i < tb.getComponentCount(); i++) {
                    c = tb.getComponent(i);
                    d = c.getPreferredSize();
                    c.setBounds(ltr ? x : x - d.width, 0, d.width, d.height);
                    x = ltr ? x + d.width : x - d.width;
                }
                contentRect.x = ltr ?
                        SynthIcon.getIconWidth(handleIcon, context) : 0;
                contentRect.y = 0;
                contentRect.width = tb.getWidth() - contentRect.x;
                contentRect.height = tb.getHeight();
            } else {
                int y = SynthIcon.getIconHeight(handleIcon, context);
                for (int i = 0; i < tb.getComponentCount(); i++) {
                    c = tb.getComponent(i);
                    d = c.getPreferredSize();
                    c.setBounds(0, y, d.width, d.height);
                    y += d.height;
                }
                contentRect.x = 0;
                contentRect.y =
                    SynthIcon.getIconHeight(handleIcon, context);
                contentRect.width = tb.getWidth();
                contentRect.height = tb.getHeight() - contentRect.y;
            }
            context.dispose();
        }
    }
}
