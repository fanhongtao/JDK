/*
 * @(#)SynthLabelUI.java	1.15 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.FontMetrics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * A skinnable implementation of LabelUI.
 *
 * @version 1.15, 01/23/03 (based on BasicLabelUI v 1.76)
 * @author Hans Muller
 */
class SynthLabelUI extends LabelUI implements PropertyChangeListener,
                  SynthUI {
    private SynthStyle style;

    public static void loadActionMap(ActionMap map) {
        // NOTE: this needs to remain static. If you have a need to
        // have Actions that reference the UI in the ActionMap,
        // then you'll also need to change the registeration of the
        // ActionMap.
	map.put("press", new PressAction());
	map.put("release", new ReleaseAction());
    }

    /**
     * Returns the LabelUI implementation used for the skins look and feel.
     */
    public static ComponentUI createUI(JComponent c){
        return new SynthLabelUI();
    }


    public void installUI(JComponent c) {
        installDefaults((JLabel)c);
        installComponents((JLabel)c);
        installListeners((JLabel)c);
        installKeyboardActions((JLabel)c);
    }


    protected void installDefaults(JLabel c) {
        fetchStyle(c);
    }

    void forceFetchStyle(JLabel c) {
        style = null;
        fetchStyle(c);
    }

    private void fetchStyle(JLabel c) {
        SynthContext context = getContext(c, ENABLED);
        style = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
    }

    protected void installListeners(JLabel c) {
        c.addPropertyChangeListener(this);
    }

    protected void installComponents(JLabel c){
	BasicHTML.updateRenderer(c, c.getText());
    }

    protected void installKeyboardActions(JLabel l) {
        int dka = l.getDisplayedMnemonic();
        Component lf = l.getLabelFor();
        if ((dka != 0) && (lf != null)) {
	    ActionMap map = SwingUtilities.getUIActionMap(l);
	    if (map == null) {
                LazyActionMap.installLazyActionMap(l, SynthLabelUI.class,
                                                   "Label.actionMap");
	    }
	    InputMap inputMap = SwingUtilities.getUIInputMap
		            (l, JComponent.WHEN_IN_FOCUSED_WINDOW);
	    if (inputMap == null) {
		inputMap = new ComponentInputMapUIResource(l);
		SwingUtilities.replaceUIInputMap(l,
				JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap);
	    }
	    inputMap.clear();
	    inputMap.put(KeyStroke.getKeyStroke(dka, ActionEvent.ALT_MASK,
					      false), "press");
        }
	else {
	    InputMap inputMap = SwingUtilities.getUIInputMap
		            (l, JComponent.WHEN_IN_FOCUSED_WINDOW);
	    if (inputMap != null) {
		inputMap.clear();
	    }
	}
    }

    public void uninstallUI(JComponent c) { 
        uninstallDefaults((JLabel)c);
        uninstallComponents((JLabel)c);
        uninstallListeners((JLabel)c);
        uninstallKeyboardActions((JLabel)c);
    }

    protected void uninstallDefaults(JLabel c){
        SynthContext context = getContext(c, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;
    }

    protected void uninstallListeners(JLabel c){
        c.removePropertyChangeListener(this);
    }

    protected void uninstallComponents(JLabel c){
	BasicHTML.updateRenderer(c, "");
    }

    protected void uninstallKeyboardActions(JLabel c) {
	SwingUtilities.replaceUIInputMap(c, JComponent.WHEN_FOCUSED, null);
	SwingUtilities.replaceUIInputMap(c, JComponent.WHEN_IN_FOCUSED_WINDOW,
				       null);
	SwingUtilities.replaceUIActionMap(c, null);
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
        return SynthLookAndFeel.getComponentState(c);
    }

    /**
     * Notifies this UI delegate that it's time to paint the specified
     * component.  This method is invoked by <code>JComponent</code> 
     * when the specified component is being painted. 
     */
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
        JLabel label = (JLabel)context.getComponent();
        Icon icon = (label.isEnabled()) ? label.getIcon() :
                                          label.getDisabledIcon();

        g.setColor(context.getStyle().getColor(context,
                                               ColorType.TEXT_FOREGROUND));
        g.setFont(style.getFont(context));
        context.getStyle().getSynthGraphics(context).paintText(
            context, g, label.getText(), icon,
            label.getHorizontalAlignment(), label.getVerticalAlignment(),
            label.getHorizontalTextPosition(), label.getVerticalTextPosition(),
            label.getIconTextGap(), label.getDisplayedMnemonicIndex(), 0);
    }

    public Dimension getPreferredSize(JComponent c) {
        JLabel label = (JLabel)c;
        Icon icon = (label.isEnabled()) ? label.getIcon() :
                                          label.getDisabledIcon();
        SynthContext context = getContext(c);
        Dimension size = context.getStyle().getSynthGraphics(context).
            getPreferredSize(
               context, context.getStyle().getFont(context), label.getText(),
               icon, label.getHorizontalAlignment(),
               label.getVerticalAlignment(), label.getHorizontalTextPosition(),
               label.getVerticalTextPosition(), label.getIconTextGap(),
               label.getDisplayedMnemonicIndex());

        context.dispose();
        return size;
    }


    public Dimension getMinimumSize(JComponent c) {
        JLabel label = (JLabel)c;
        Icon icon = (label.isEnabled()) ? label.getIcon() :
                                          label.getDisabledIcon();
        SynthContext context = getContext(c);
        Dimension size = context.getStyle().getSynthGraphics(context).
            getMinimumSize(
               context, context.getStyle().getFont(context), label.getText(),
               icon, label.getHorizontalAlignment(),
               label.getVerticalAlignment(), label.getHorizontalTextPosition(),
               label.getVerticalTextPosition(), label.getIconTextGap(),
               label.getDisplayedMnemonicIndex());

        context.dispose();
        return size;
    }

    public Dimension getMaximumSize(JComponent c) {
        JLabel label = (JLabel)c;
        Icon icon = (label.isEnabled()) ? label.getIcon() :
                                          label.getDisabledIcon();
        SynthContext context = getContext(c);
        Dimension size = context.getStyle().getSynthGraphics(context).
               getMaximumSize(
               context, context.getStyle().getFont(context), label.getText(),
               icon, label.getHorizontalAlignment(),
               label.getVerticalAlignment(), label.getHorizontalTextPosition(),
               label.getVerticalTextPosition(), label.getIconTextGap(),
               label.getDisplayedMnemonicIndex());

        context.dispose();
        return size;
    }


    public void propertyChange(PropertyChangeEvent e) {
	String name = e.getPropertyName();
        if (SynthLookAndFeel.shouldUpdateStyle(e)) {
            fetchStyle((JLabel)e.getSource());
        }
	if (name.equals("text") || "font".equals(name) ||
            "foreground".equals(name)) {
	    // remove the old html view client property if one
	    // existed, and install a new one if the text installed
	    // into the JLabel is html source.
	    JLabel lbl = ((JLabel) e.getSource());
	    String text = lbl.getText();
	    BasicHTML.updateRenderer(lbl, text);
	}
        else if (name.equals("labelFor") ||
		 name.equals("displayedMnemonic")) {
            installKeyboardActions((JLabel) e.getSource());
        }
    }

    // When the accelerator is pressed, temporarily make the JLabel 
    // focusTraversable by registering a WHEN_FOCUSED action for the
    // release of the accelerator.  Then give it focus so it can 
    // prevent unwanted keyTyped events from getting to other components.
    static class PressAction extends AbstractAction {
        PressAction() {
        }

        public void actionPerformed(ActionEvent e) {
	   JLabel label = (JLabel)e.getSource();
	   Component labelFor = label.getLabelFor();
	   if(labelFor != null && labelFor.isEnabled()) {
	      InputMap inputMap = SwingUtilities.getUIInputMap(label, JComponent.WHEN_FOCUSED);
	      if (inputMap == null) {
	         inputMap = new InputMapUIResource();
		 SwingUtilities.replaceUIInputMap(label, JComponent.WHEN_FOCUSED, inputMap);
	      }
	      int dka = label.getDisplayedMnemonic();
	      inputMap.put(KeyStroke.getKeyStroke(dka, ActionEvent.ALT_MASK, true), "release");
              // Need this if the accelerator is released before the ALT key
	      inputMap.put(KeyStroke.getKeyStroke(0, ActionEvent.ALT_MASK, true), "release");
	      Component owner = label.getLabelFor();
	      label.requestFocus();
	   }
        }

    }

    // On the release of the accelerator, remove the keyboard action
    // that allows the label to take focus and then give focus to the
    // labelFor component.
    static class ReleaseAction extends AbstractAction {
        ReleaseAction() {
        }
        
        public void actionPerformed(ActionEvent e) {
	   JLabel label = (JLabel)e.getSource();
	   Component labelFor = label.getLabelFor();
	   if(labelFor != null && labelFor.isEnabled()) {
	      InputMap inputMap = SwingUtilities.getUIInputMap(label, JComponent.WHEN_FOCUSED);
	      if (inputMap != null) {
	         // inputMap should never be null.
	         inputMap.remove(KeyStroke.getKeyStroke (label.getDisplayedMnemonic(), ActionEvent.ALT_MASK, true));
	         inputMap.remove(KeyStroke.getKeyStroke(0, ActionEvent.ALT_MASK, true));
	      }
              label.getLabelFor().requestFocus();
	   }
        }
    }
}
