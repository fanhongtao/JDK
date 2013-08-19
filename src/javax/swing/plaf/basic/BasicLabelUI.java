/*
 * @(#)BasicLabelUI.java	1.77 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import javax.swing.*;
import javax.swing.plaf.*;
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
 * A Windows L&F implementation of LabelUI.  This implementation 
 * is completely static, i.e. there's only one UIView implementation 
 * that's shared by all JLabel objects.
 *
 * @version 1.77 01/23/03
 * @author Hans Muller
 */
public class BasicLabelUI extends LabelUI implements  PropertyChangeListener
{
    protected static BasicLabelUI labelUI = new BasicLabelUI();

    /**
     * Forwards the call to SwingUtilities.layoutCompoundLabel().
     * This method is here so that a subclass could do Label specific
     * layout and to shorten the method name a little.
     * 
     * @see SwingUtilities#layoutCompoundLabel
     */
    protected String layoutCL(
        JLabel label,                  
        FontMetrics fontMetrics, 
        String text, 
        Icon icon, 
        Rectangle viewR, 
        Rectangle iconR, 
        Rectangle textR)
    {
        return SwingUtilities.layoutCompoundLabel(
            (JComponent) label,
            fontMetrics,
            text,
            icon,
            label.getVerticalAlignment(),
            label.getHorizontalAlignment(),
            label.getVerticalTextPosition(),
            label.getHorizontalTextPosition(),
            viewR,
            iconR,
            textR,
            label.getIconTextGap());
    }

    /**
     * Paint clippedText at textX, textY with the labels foreground color.
     * 
     * @see #paint
     * @see #paintDisabledText
     */
    protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY)
    {
        int mnemIndex = l.getDisplayedMnemonicIndex();
        g.setColor(l.getForeground());
        BasicGraphicsUtils.drawStringUnderlineCharAt(g, s, mnemIndex,
                                                     textX, textY);
    }


    /**
     * Paint clippedText at textX, textY with background.lighter() and then 
     * shifted down and to the right by one pixel with background.darker().
     * 
     * @see #paint
     * @see #paintEnabledText
     */
    protected void paintDisabledText(JLabel l, Graphics g, String s, int textX, int textY)
    {
        int accChar = l.getDisplayedMnemonicIndex();
        Color background = l.getBackground();
        g.setColor(background.brighter());
        BasicGraphicsUtils.drawStringUnderlineCharAt(g, s, accChar,
                                                   textX + 1, textY + 1);
        g.setColor(background.darker());
        BasicGraphicsUtils.drawStringUnderlineCharAt(g, s, accChar,
                                                   textX, textY);
    }


    /* These rectangles/insets are allocated once for this shared LabelUI
     * implementation.  Re-using rectangles rather than allocating
     * them in each paint call halved the time it took paint to run.
     */
    private static Rectangle paintIconR = new Rectangle();
    private static Rectangle paintTextR = new Rectangle();
    private static Rectangle paintViewR = new Rectangle();
    private static Insets paintViewInsets = new Insets(0, 0, 0, 0);
    

    /** 
     * Paint the label text in the foreground color, if the label
     * is opaque then paint the entire background with the background
     * color.  The Label text is drawn by paintEnabledText() or
     * paintDisabledText().  The locations of the label parts are computed
     * by layoutCL.
     * 
     * @see #paintEnabledText
     * @see #paintDisabledText
     * @see #layoutCL
     */
    public void paint(Graphics g, JComponent c) 
    {
        JLabel label = (JLabel)c;
        String text = label.getText();
        Icon icon = (label.isEnabled()) ? label.getIcon() : label.getDisabledIcon();

        if ((icon == null) && (text == null)) {
            return;
        }

        FontMetrics fm = g.getFontMetrics();
        Insets insets = c.getInsets(paintViewInsets);

        paintViewR.x = insets.left;
        paintViewR.y = insets.top;
        paintViewR.width = c.getWidth() - (insets.left + insets.right);
        paintViewR.height = c.getHeight() - (insets.top + insets.bottom);

        paintIconR.x = paintIconR.y = paintIconR.width = paintIconR.height = 0;
        paintTextR.x = paintTextR.y = paintTextR.width = paintTextR.height = 0;

        String clippedText = 
            layoutCL(label, fm, text, icon, paintViewR, paintIconR, paintTextR);

        if (icon != null) {
            icon.paintIcon(c, g, paintIconR.x, paintIconR.y);
        }

        if (text != null) {
	    View v = (View) c.getClientProperty(BasicHTML.propertyKey);
	    if (v != null) {
		v.paint(g, paintTextR);
	    } else {
		int textX = paintTextR.x;
		int textY = paintTextR.y + fm.getAscent();
		
		if (label.isEnabled()) {
		    paintEnabledText(label, g, clippedText, textX, textY);
		}
		else {
		    paintDisabledText(label, g, clippedText, textX, textY);
		}
	    }
        }
    }


    /* These rectangles/insets are allocated once for this shared LabelUI
     * implementation.  Re-using rectangles rather than allocating
     * them in each getPreferredSize call sped up the method substantially.
     */
    private static Rectangle iconR = new Rectangle();
    private static Rectangle textR = new Rectangle();
    private static Rectangle viewR = new Rectangle();
    private static Insets viewInsets = new Insets(0, 0, 0, 0);


    public Dimension getPreferredSize(JComponent c) 
    {
        JLabel label = (JLabel)c;
        String text = label.getText();
        Icon icon = (label.isEnabled()) ? label.getIcon() :
                                          label.getDisabledIcon();
        Insets insets = label.getInsets(viewInsets);
        Font font = label.getFont();

        int dx = insets.left + insets.right;
        int dy = insets.top + insets.bottom;

        if ((icon == null) && 
            ((text == null) || 
             ((text != null) && (font == null)))) {
            return new Dimension(dx, dy);
        }
        else if ((text == null) || ((icon != null) && (font == null))) {
            return new Dimension(icon.getIconWidth() + dx, 
                                 icon.getIconHeight() + dy);
        }
        else {
            FontMetrics fm = label.getToolkit().getFontMetrics(font);

            iconR.x = iconR.y = iconR.width = iconR.height = 0;
            textR.x = textR.y = textR.width = textR.height = 0;
            viewR.x = dx;
            viewR.y = dy;
            viewR.width = viewR.height = Short.MAX_VALUE;

            layoutCL(label, fm, text, icon, viewR, iconR, textR);
            int x1 = Math.min(iconR.x, textR.x);
            int x2 = Math.max(iconR.x + iconR.width, textR.x + textR.width);
            int y1 = Math.min(iconR.y, textR.y);
            int y2 = Math.max(iconR.y + iconR.height, textR.y + textR.height);
            Dimension rv = new Dimension(x2 - x1, y2 - y1);

            rv.width += dx;
            rv.height += dy;
            return rv;
        }
    }


    /**
     * @return getPreferredSize(c)
     */
    public Dimension getMinimumSize(JComponent c) {
        Dimension d = getPreferredSize(c);
	View v = (View) c.getClientProperty(BasicHTML.propertyKey);
	if (v != null) {
	    d.width -= v.getPreferredSpan(View.X_AXIS) - v.getMinimumSpan(View.X_AXIS);
	}
	return d;
    }

    /**
     * @return getPreferredSize(c)
     */
    public Dimension getMaximumSize(JComponent c) {
        Dimension d = getPreferredSize(c);
	View v = (View) c.getClientProperty(BasicHTML.propertyKey);
	if (v != null) {
	    d.width += v.getMaximumSpan(View.X_AXIS) - v.getPreferredSpan(View.X_AXIS);
	}
	return d;
    }


    public void installUI(JComponent c) { 
        installDefaults((JLabel)c);
        installComponents((JLabel)c);
        installListeners((JLabel)c);
        installKeyboardActions((JLabel)c);
    }

    
    public void uninstallUI(JComponent c) { 
        uninstallDefaults((JLabel)c);
        uninstallComponents((JLabel)c);
        uninstallListeners((JLabel)c);
        uninstallKeyboardActions((JLabel)c);
    }

  // below is my overly aggressive version of this (It subtlely breaks UI switching)
  // I don't have time to fix this properly now, but I'll come back to it for a future release
/*    private Color defaultForeground = null;
    private Color defaultBackground = null;
    private Font defaultFont = null;

    protected void installDefaults(JLabel c){

	if (defaultForeground == null) {
		defaultForeground = UIManager.getColor("Label.foreground");
		defaultBackground = UIManager.getColor("Label.background");
		defaultFont = UIManager.getFont("Label.font");
	}

	Color currentForeground = c.getForeground();
	Color currentBackground = c.getBackground();
	Font currentFont = c.getFont();

	if (currentForeground == null || currentForeground instanceof UIResource) {
	      c.setForeground(defaultForeground);
	}

	if (currentBackground == null || currentBackground instanceof UIResource) {
              c.setBackground(defaultBackground);
	}

	if (currentFont == null || currentFont instanceof UIResource) {
	      c.setFont(defaultFont);
	}
    }*/


  /* old version - simple, but slow... */
     protected void installDefaults(JLabel c){
         LookAndFeel.installColorsAndFont(c, "Label.background", "Label.foreground", "Label.font");
      }

    protected void installListeners(JLabel c){
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
		map = createActionMap();
		if (map != null) {
		    SwingUtilities.replaceUIActionMap(l, map);
		    UIManager.getLookAndFeelDefaults().put("Label.actionMap",
                                                           map);
		}
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

    ActionMap createActionMap() {
	ActionMap map = new ActionMapUIResource();
	map.put("press", new PressAction());
	map.put("release", new ReleaseAction());
	return map;
    }

    protected void uninstallDefaults(JLabel c){
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

    public static ComponentUI createUI(JComponent c) {
        return labelUI;
    }

    public void propertyChange(PropertyChangeEvent e) {
	String name = e.getPropertyName();
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
