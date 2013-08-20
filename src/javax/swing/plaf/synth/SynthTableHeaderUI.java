/*
 * @(#)SynthTableHeaderUI.java	1.14 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.synth;

import javax.swing.table.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Enumeration;
import java.util.Date;
import java.awt.event.*;
import java.awt.*;
import java.beans.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import sun.swing.plaf.synth.SynthUI;

/**
 * SynthTableHeaderUI implementation
 *
 * @version 1.14, 12/19/03
 * @author Alan Chung
 * @author Philip Milne
 */
class SynthTableHeaderUI extends BasicTableHeaderUI implements
           PropertyChangeListener, SynthUI {

//
// Instance Variables
//
    
    private TableCellRenderer prevRenderer = null;    

    private SynthStyle style;

    public static ComponentUI createUI(JComponent h) {
        return new SynthTableHeaderUI();
    }

    protected void installDefaults() {
        prevRenderer = header.getDefaultRenderer();
        if (prevRenderer instanceof UIResource) {
            header.setDefaultRenderer(new HeaderRenderer());
        }
        updateStyle(header);
    }

    private void updateStyle(JTableHeader c) {
        SynthContext context = getContext(c, ENABLED);
        SynthStyle oldStyle = style;
        style = SynthLookAndFeel.updateStyle(context, this);
        if (style != oldStyle) {
            if (oldStyle != null) {
                uninstallKeyboardActions();
                installKeyboardActions();
            }
        }
        context.dispose();
    }

    protected void installListeners() {
        super.installListeners();
        header.addPropertyChangeListener(this);
    }

    protected void uninstallDefaults() {
        if (header.getDefaultRenderer() instanceof HeaderRenderer) {
            header.setDefaultRenderer(prevRenderer);
        }
               
        SynthContext context = getContext(header, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;
    }

    protected void uninstallListeners() {
        header.removePropertyChangeListener(this);
        super.uninstallListeners();
    }

    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        context.getPainter().paintTableHeaderBackground(context,
                          g, 0, 0, c.getWidth(), c.getHeight());
        paint(context, g);
        context.dispose();
    }

    public void paint(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        paint(context, g);
        context.dispose();
    }

    protected void paint(SynthContext context, Graphics g) {
        super.paint(g, context.getComponent());
    }

    public void paintBorder(SynthContext context, Graphics g, int x,
                            int y, int w, int h) {
        context.getPainter().paintTableHeaderBorder(context, g, x, y, w, h);
    }
//
// SynthUI
//
    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    private SynthContext getContext(JComponent c, int state) {
        return SynthContext.getContext(SynthContext.class, c,
                    SynthLookAndFeel.getRegion(c), style, state);
    }

    private Region getRegion(JComponent c) {
        return SynthLookAndFeel.getRegion(c);
    }

    private int getComponentState(JComponent c) {
        return SynthLookAndFeel.getComponentState(c);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (SynthLookAndFeel.shouldUpdateStyle(evt)) {
            updateStyle((JTableHeader)evt.getSource());
        }
    }


    private static class HeaderRenderer extends DefaultTableCellRenderer
                                 implements UIResource {
        HeaderRenderer() {
            setHorizontalAlignment(JLabel.LEADING);
        }

        public void setBorder(Border border) {
            if (border instanceof SynthBorder) {
                super.setBorder(border);
            }
        }

        public String getName() {
            String name = super.getName();
            if (name == null) {
                return "TableHeader.renderer";
            }
            return name;
        }

        public Component getTableCellRendererComponent(
                            JTable table, Object value, boolean isSelected,
                            boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
}
