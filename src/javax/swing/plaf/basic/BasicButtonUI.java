/*
 * @(#)BasicButtonUI.java	1.107 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package javax.swing.plaf.basic;

import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.View;

/**
 * BasicButton implementation
 *
 * @version 1.107 01/23/03
 * @author Jeff Dinkins
 */
public class BasicButtonUI extends ButtonUI{
    // Shared UI object
    private final static BasicButtonUI buttonUI = new BasicButtonUI();

    // Visual constants
    protected int defaultTextIconGap;
    
    // Offset controlled by set method 
    private int shiftOffset = 0;
    protected int defaultTextShiftOffset;

    // Has the shared instance defaults been initialized?
    private boolean defaults_initialized = false;

    private final static String propertyPrefix = "Button" + ".";

    // ********************************
    //          Create PLAF
    // ********************************
    public static ComponentUI createUI(JComponent c) {
        return buttonUI;
    }

    protected String getPropertyPrefix() {
        return propertyPrefix;
    }


    // ********************************
    //          Install PLAF
    // ********************************
    public void installUI(JComponent c) {
        installDefaults((AbstractButton) c);
        installListeners((AbstractButton) c);
        installKeyboardActions((AbstractButton) c);
	BasicHTML.updateRenderer(c, ((AbstractButton) c).getText());
    }

    private Color defaultForeground = null;
    private Color defaultBackground = null;
    private Font defaultFont = null;
    private Border defaultBorder = null;

    protected void installDefaults(AbstractButton b) {
        // load shared instance defaults
        String pp = getPropertyPrefix();
        if(!defaults_initialized) {
            defaultTextIconGap = ((Integer)UIManager.get(pp + "textIconGap")).intValue();
            defaultTextShiftOffset = ((Integer)UIManager.get(pp + "textShiftOffset")).intValue();
        
	    // next four lines part of optimized component defaults installation
	   /* defaultForeground = UIManager.getColor(pp + "foreground");
	    defaultBackground = UIManager.getColor(pp + "background");
	    defaultFont = UIManager.getFont(pp + "font");
	    defaultBorder = UIManager.getBorder(pp + "border");*/

            defaults_initialized = true;
        }

        // set the following defaults on the button
        if (b.isContentAreaFilled()) {
            b.setOpaque(true); 
        } else {
            b.setOpaque(false);
        }

        if(b.getMargin() == null || (b.getMargin() instanceof UIResource)) {
            b.setMargin(UIManager.getInsets(pp + "margin"));
        }

	// *** begin optimized defaults install ***

/*	Color currentForeground = b.getForeground();
	Color currentBackground = b.getBackground();
	Font currentFont = b.getFont();
	Border currentBorder = b.getBorder();

	if (currentForeground == null || currentForeground instanceof UIResource) {
	      b.setForeground(defaultForeground);
	}

	if (currentBackground == null || currentBackground instanceof UIResource) {
              b.setBackground(defaultBackground);
	}

	if (currentFont == null || currentFont instanceof UIResource) {
	      b.setFont(defaultFont);
	}

	if (currentBorder == null || currentBorder instanceof UIResource) {
	      b.setBorder(defaultBorder);
	} */

	// *** end optimized defaults install ***

	// old code below works for component defaults installation, but it is slow
	LookAndFeel.installColorsAndFont(b, pp + "background", pp + "foreground", pp + "font");
        LookAndFeel.installBorder(b, pp + "border");

    }

    protected void installListeners(AbstractButton b) {
        BasicButtonListener listener = createButtonListener(b);
        if(listener != null) {
            // put the listener in the button's client properties so that
            // we can get at it later
            b.putClientProperty(this, listener);

            b.addMouseListener(listener);
            b.addMouseMotionListener(listener);
            b.addFocusListener(listener);
            b.addPropertyChangeListener(listener);
            b.addChangeListener(listener);
        }
    }
    
    protected void installKeyboardActions(AbstractButton b){
        BasicButtonListener listener = (BasicButtonListener) b.getClientProperty(this);
        if(listener != null) {
            listener.installKeyboardActions(b);
        }
    }

        
    // ********************************
    //         Uninstall PLAF
    // ********************************
    public void uninstallUI(JComponent c) {
        uninstallKeyboardActions((AbstractButton) c);
        uninstallListeners((AbstractButton) c);
        uninstallDefaults((AbstractButton) c);
	BasicHTML.updateRenderer(c, "");
    }

    protected void uninstallKeyboardActions(AbstractButton b) {
        BasicButtonListener listener = (BasicButtonListener) b.getClientProperty(this);
        if(listener != null) {
            listener.uninstallKeyboardActions(b);
        }
    }

    protected void uninstallListeners(AbstractButton b) {
        BasicButtonListener listener = (BasicButtonListener) b.getClientProperty(this);
        b.putClientProperty(this, null);
        if(listener != null) {
            b.removeMouseListener(listener);
            b.removeMouseListener(listener);
            b.removeMouseMotionListener(listener);
            b.removeFocusListener(listener);
            b.removeChangeListener(listener);
            b.removePropertyChangeListener(listener);
        }
    }

    protected void uninstallDefaults(AbstractButton b) {
        LookAndFeel.uninstallBorder(b);
        defaults_initialized = false;
    }
  
    // ********************************
    //        Create Listeners 
    // ********************************
    protected BasicButtonListener createButtonListener(AbstractButton b) {
        return new BasicButtonListener(b);
    }

    public int getDefaultTextIconGap(AbstractButton b) {
        return defaultTextIconGap;
    }

    /* These rectangles/insets are allocated once for all 
     * ButtonUI.paint() calls.  Re-using rectangles rather than 
     * allocating them in each paint call substantially reduced the time
     * it took paint to run.  Obviously, this method can't be re-entered.
     */
    private static Rectangle viewRect = new Rectangle();
    private static Rectangle textRect = new Rectangle();
    private static Rectangle iconRect = new Rectangle();

    // ********************************
    //          Paint Methods
    // ********************************

    public void paint(Graphics g, JComponent c) 
    {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();

        FontMetrics fm = g.getFontMetrics();

        Insets i = c.getInsets();

        viewRect.x = i.left;
        viewRect.y = i.top;
        viewRect.width = b.getWidth() - (i.right + viewRect.x);
        viewRect.height = b.getHeight() - (i.bottom + viewRect.y);

        textRect.x = textRect.y = textRect.width = textRect.height = 0;
        iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;

        Font f = c.getFont();
        g.setFont(f);

        // layout the text and icon
        String text = SwingUtilities.layoutCompoundLabel(
            c, fm, b.getText(), b.getIcon(), 
            b.getVerticalAlignment(), b.getHorizontalAlignment(),
            b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
            viewRect, iconRect, textRect, 
	    b.getText() == null ? 0 : b.getIconTextGap());

        clearTextShiftOffset();

        // perform UI specific press action, e.g. Windows L&F shifts text
        if (model.isArmed() && model.isPressed()) {
            paintButtonPressed(g,b); 
        }

        // Paint the Icon
        if(b.getIcon() != null) { 
            paintIcon(g,c,iconRect);
        }

        if (text != null && !text.equals("")){
	    View v = (View) c.getClientProperty(BasicHTML.propertyKey);
	    if (v != null) {
		v.paint(g, textRect);
	    } else {
		paintText(g, b, textRect, text);
	    }
        }

        if (b.isFocusPainted() && b.hasFocus()) {
            // paint UI specific focus
            paintFocus(g,b,viewRect,textRect,iconRect);
        }
    }
    
    protected void paintIcon(Graphics g, JComponent c, Rectangle iconRect){
            AbstractButton b = (AbstractButton) c;                           
            ButtonModel model = b.getModel();
            Icon icon = b.getIcon();
            Icon tmpIcon = null;

	    if(icon == null) {
	       return;
	    }

            if(!model.isEnabled()) {
		if(model.isSelected()) {
                   tmpIcon = (Icon) b.getDisabledSelectedIcon();
		} else {
                   tmpIcon = (Icon) b.getDisabledIcon();
		}
            } else if(model.isPressed() && model.isArmed()) {
                tmpIcon = (Icon) b.getPressedIcon();
                if(tmpIcon != null) {
                    // revert back to 0 offset
                    clearTextShiftOffset();
                }
            } else if(b.isRolloverEnabled() && model.isRollover()) {
		if(model.isSelected()) {
                   tmpIcon = (Icon) b.getRolloverSelectedIcon();
		} else {
                   tmpIcon = (Icon) b.getRolloverIcon();
		}
            } else if(model.isSelected()) {
                tmpIcon = (Icon) b.getSelectedIcon();
	    }
              
	    if(tmpIcon != null) {
	        icon = tmpIcon;
	    }
               
            if(model.isPressed() && model.isArmed()) {
                icon.paintIcon(c, g, iconRect.x + getTextShiftOffset(),
                        iconRect.y + getTextShiftOffset());
            } else {
                icon.paintIcon(c, g, iconRect.x, iconRect.y);
            }

    }

    /**
     * As of Java 2 platform v 1.4 this method should not be used or overriden.
     * Use the paintText method which takes the AbstractButton argument.
     */
    protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
        AbstractButton b = (AbstractButton) c;                       
        ButtonModel model = b.getModel();
        FontMetrics fm = g.getFontMetrics();
        int mnemonicIndex = b.getDisplayedMnemonicIndex();

	/* Draw the Text */
	if(model.isEnabled()) {
	    /*** paint the text normally */
	    g.setColor(b.getForeground());
	    BasicGraphicsUtils.drawStringUnderlineCharAt(g,text, mnemonicIndex,
					  textRect.x + getTextShiftOffset(),
					  textRect.y + fm.getAscent() + getTextShiftOffset());
	}
	else {
	    /*** paint the text disabled ***/
	    g.setColor(b.getBackground().brighter());
	    BasicGraphicsUtils.drawStringUnderlineCharAt(g,text, mnemonicIndex,
					  textRect.x, textRect.y + fm.getAscent());
	    g.setColor(b.getBackground().darker());
	    BasicGraphicsUtils.drawStringUnderlineCharAt(g,text, mnemonicIndex,
					  textRect.x - 1, textRect.y + fm.getAscent() - 1);
	}
    }

    /**
     * Method which renders the text of the current button.
     * <p>
     * @param g Graphics context
     * @param b Current button to render
     * @param textRect Bounding rectangle to render the text.
     * @param text String to render
     * @since 1.4
     */
    protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text) {
	paintText(g, (JComponent)b, textRect, text);
    }

    // Method signature defined here overriden in subclasses. 
    // Perhaps this class should be abstract?
    protected void paintFocus(Graphics g, AbstractButton b,
                              Rectangle viewRect, Rectangle textRect, Rectangle iconRect){
    }
  


    // Method signature defined here overriden in subclasses. 
    // Perhaps this class should be abstract?
    protected void paintButtonPressed(Graphics g, AbstractButton b){
    }

    protected void clearTextShiftOffset(){
        this.shiftOffset = 0;
    }

    protected void setTextShiftOffset(){
        this.shiftOffset = defaultTextShiftOffset;
    }

    protected int getTextShiftOffset() {
        return shiftOffset;
    }

    // ********************************
    //          Layout Methods
    // ********************************
    public Dimension getMinimumSize(JComponent c) {
        Dimension d = getPreferredSize(c);
	View v = (View) c.getClientProperty(BasicHTML.propertyKey);
	if (v != null) {
	    d.width -= v.getPreferredSpan(View.X_AXIS) - v.getMinimumSpan(View.X_AXIS);
	}
	return d;
    }

    public Dimension getPreferredSize(JComponent c) {
        AbstractButton b = (AbstractButton)c;
        return BasicGraphicsUtils.getPreferredButtonSize(b, b.getIconTextGap());
    }

    public Dimension getMaximumSize(JComponent c) {
        Dimension d = getPreferredSize(c);
	View v = (View) c.getClientProperty(BasicHTML.propertyKey);
	if (v != null) {
	    d.width += v.getMaximumSpan(View.X_AXIS) - v.getPreferredSpan(View.X_AXIS);
	}
	return d;
    }


}
