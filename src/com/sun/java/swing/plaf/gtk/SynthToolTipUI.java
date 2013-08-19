/*
 * @(#)SynthToolTipUI.java	1.5 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.ToolTipUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.View;


/**
 * Standard tool tip L&F.
 * <p>
 *
 * @version 1.5, 01/23/03 (based on BasicToolTipUI v 1.36)
 * @author Dave Moore
 */
class SynthToolTipUI extends ToolTipUI implements PropertyChangeListener,
               SynthUI {
    private SynthStyle style;


    public static ComponentUI createUI(JComponent c) {
        return new SynthToolTipUI();
    }

    public SynthToolTipUI() {
        super();
    }

    public void installUI(JComponent c) {
	installDefaults(c);
	installComponents(c);
	installListeners(c);
    }

    public void uninstallUI(JComponent c) {
	// REMIND: this is NOT getting called
	uninstallDefaults(c);
	uninstallComponents(c);
	uninstallListeners(c);
    }

    protected void installDefaults(JComponent c){
        fetchStyle(c);
        componentChanged(c);
    }

    private void fetchStyle(JComponent c) {
        SynthContext context = getContext(c, ENABLED);
        style = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
    }
    
    protected void uninstallDefaults(JComponent c){
        SynthContext context = getContext(c, ENABLED);
        style.uninstallDefaults(context);
        context.dispose();
        style = null;
    }

    /* Unfortunately this has to remain private until we can make API additions.
     */
    private void installComponents(JComponent c){
 	BasicHTML.updateRenderer(c, ((JToolTip)c).getTipText());
    }
     
    /* Unfortunately this has to remain private until we can make API additions.
     */
    private void uninstallComponents(JComponent c){
 	BasicHTML.updateRenderer(c, "");
    }

    protected void installListeners(JComponent c) {
        c.addPropertyChangeListener(this);
    }

    protected void uninstallListeners(JComponent c) {
        c.removePropertyChangeListener(this);
    }

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
        JComponent comp = ((JToolTip)c).getComponent();

        if (comp != null && !comp.isEnabled()) {
            return DISABLED;
        }
        return SynthLookAndFeel.getComponentState(c);
    }

    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        paint(context, g);
        context.dispose();
    }

    public void paint(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        paint(context, g);
        context.dispose();
    }

    protected void paint(SynthContext context, Graphics g) {
        JToolTip tip = (JToolTip)context.getComponent();
	String tipText = tip.getToolTipText();

        Insets insets = tip.getInsets();
	View v = (View)tip.getClientProperty(BasicHTML.propertyKey);
	if (v != null) {
            Rectangle paintTextR = new Rectangle(insets.left, insets.top,
                  tip.getWidth() - (insets.left + insets.right),
                  tip.getHeight() - (insets.top + insets.bottom));
	    v.paint(g, paintTextR);
	} else {
            g.setColor(context.getStyle().getColor(context,
                                                   ColorType.TEXT_FOREGROUND));
            g.setFont(style.getFont(context));
            context.getStyle().getSynthGraphics(context).paintText(
                context, g, tip.getTipText(), insets.left, insets.top, -1);
	}
    }

    public Dimension getPreferredSize(JComponent c) {
        SynthContext context = getContext(c);
	Insets insets = c.getInsets();
	Dimension prefSize = new Dimension(insets.left+insets.right,
					   insets.top+insets.bottom);
	String text = ((JToolTip)c).getTipText();

	if (text != null) {
	    View v = (c != null) ? (View) c.getClientProperty("html") : null;
	    if (v != null) {
		prefSize.width += (int) v.getPreferredSpan(View.X_AXIS);
		prefSize.height += (int) v.getPreferredSpan(View.Y_AXIS);
	    } else {
                Font font = context.getStyle().getFont(context);
                FontMetrics fm = Toolkit.getDefaultToolkit().
                                         getFontMetrics(font);
		prefSize.width += context.getStyle().getSynthGraphics(context).
                                  computeStringWidth(context, font, fm, text);
		prefSize.height += fm.getHeight();
	    }
        }
        context.dispose();
	return prefSize;
    }

    public Dimension getMinimumSize(JComponent c) {
	Dimension d = getPreferredSize(c);
 	View v = (View) c.getClientProperty(BasicHTML.propertyKey);
 	if (v != null) {
 	    d.width -= v.getPreferredSpan(View.X_AXIS) -
                       v.getMinimumSpan(View.X_AXIS);
 	}
 	return d;
    }

    public Dimension getMaximumSize(JComponent c) {
	Dimension d = getPreferredSize(c);
 	View v = (View) c.getClientProperty(BasicHTML.propertyKey);
 	if (v != null) {
 	    d.width += v.getMaximumSpan(View.X_AXIS) -
                       v.getPreferredSpan(View.X_AXIS);
 	}
 	return d;
    }

    /**
     * Invoked when the <code>JCompoment</code> associated with the
     * <code>JToolTip</code> has changed, or at initialization time. This
     * should update any state dependant upon the <code>JComponent</code>.
     *
     * @param c the JToolTip the JComponent has changed on.
     */
    private void componentChanged(JComponent c) {
    }


    public void propertyChange(PropertyChangeEvent e) {
        String name = e.getPropertyName();
        if (SynthLookAndFeel.shouldUpdateStyle(e)) {
            fetchStyle((JToolTip)e.getSource());
        }
        if (name.equals("tiptext") || "font".equals(name) ||
            "foreground".equals(name)) {
            // remove the old html view client property if one
            // existed, and install a new one if the text installed
            // into the JLabel is html source.
            JToolTip tip = ((JToolTip) e.getSource());
            String text = tip.getTipText();
            BasicHTML.updateRenderer(tip, text);
        }
        else if ("component".equals(name)) {
            componentChanged((JToolTip)e.getSource());
	}
    }
}
