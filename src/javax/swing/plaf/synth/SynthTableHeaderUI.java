/*
 * @(#)SynthTableHeaderUI.java	1.18 06/03/16
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.synth;

import java.awt.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.*;

import sun.swing.plaf.synth.*;
import sun.swing.table.*;

/**
 * SynthTableHeaderUI implementation
 *
 * @version 1.18, 03/16/06
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

    @Override
    protected void rolloverColumnUpdated(int oldColumn, int newColumn) {
        header.repaint(header.getHeaderRect(oldColumn));
        header.repaint(header.getHeaderRect(newColumn));
    }

    private class HeaderRenderer extends DefaultTableCellHeaderRenderer {
        HeaderRenderer() {
            setHorizontalAlignment(JLabel.LEADING);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row, int column) {

            boolean hasRollover = (column == getRolloverColumn());
            if (isSelected || hasRollover || hasFocus) {
                SynthLookAndFeel.setSelectedUI((SynthLabelUI)SynthLookAndFeel.
                             getUIOfType(getUI(), SynthLabelUI.class),
                             isSelected, hasFocus, table.isEnabled(),
                             hasRollover);
            } else {
                SynthLookAndFeel.resetSelectedUI();
            }

            super.getTableCellRendererComponent(table, value, isSelected,
                                                hasFocus, row, column);

            return this;
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
    }
}
