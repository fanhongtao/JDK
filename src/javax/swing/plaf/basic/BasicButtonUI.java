/*
 * @(#)BasicButtonUI.java	1.93 98/08/26
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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

/**
 * BasicButton implementation
 *
 * @version 1.93 08/26/98
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
    }

    protected void installDefaults(AbstractButton b) {
        // load shared instance defaults
        String pp = getPropertyPrefix();
        if(!defaults_initialized) {
            defaultTextIconGap = ((Integer)UIManager.get(pp + "textIconGap")).intValue();
            defaultTextShiftOffset = ((Integer)UIManager.get(pp + "textShiftOffset")).intValue();
        
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
            b.getText() == null ? 0 : defaultTextIconGap
        );

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
            paintText(g, c,textRect, text);
        }

        if (b.isFocusPainted() && b.hasFocus()) {
            // paint UI specific focus
            paintFocus(g,b,viewRect,textRect,iconRect);
        }
    }
    
    protected void paintIcon(Graphics g, JComponent c, Rectangle iconRect){
            AbstractButton b = (AbstractButton) c;                           
            ButtonModel model = b.getModel();
            Icon icon = null;
            if(!model.isEnabled()) {
                icon = (Icon) b.getDisabledIcon();
            } else if(model.isPressed() && model.isArmed()) {
                icon = (Icon) b.getPressedIcon();
                if(icon == null) {
                    // Use default icon
                    icon = (Icon) b.getIcon();
                } else {
                    // revert back to 0 offset
                    clearTextShiftOffset();
                }
            } else if(b.isRolloverEnabled() && model.isRollover()) {
                icon = (Icon) b.getRolloverIcon();
            }
              
            if (icon == null) {
                icon = (Icon) b.getIcon();
            }
               
            if(model.isPressed() && model.isArmed()) {
                icon.paintIcon(c, g, iconRect.x + getTextShiftOffset(),
                        iconRect.y + getTextShiftOffset());
            } else {
                icon.paintIcon(c, g, iconRect.x, iconRect.y);
            }

    }

    protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
        AbstractButton b = (AbstractButton) c;                       
        ButtonModel model = b.getModel();
        FontMetrics fm = g.getFontMetrics();

        /* Draw the Text */
        if(model.isEnabled()) {
            /*** paint the text normally */
            g.setColor(b.getForeground());
            BasicGraphicsUtils.drawString(g,text, model.getMnemonic(),
                                          textRect.x + getTextShiftOffset(),
                                          textRect.y + fm.getAscent() + getTextShiftOffset());
        }
        else {
            /*** paint the text disabled ***/
            g.setColor(b.getBackground().brighter());
            BasicGraphicsUtils.drawString(g,text,model.getMnemonic(),
                                          textRect.x, textRect.y + fm.getAscent());
            g.setColor(b.getBackground().darker());
            BasicGraphicsUtils.drawString(g,text,model.getMnemonic(),
                                          textRect.x - 1, textRect.y + fm.getAscent() - 1);
        }
    }
        
    protected void paintFocus(Graphics g, AbstractButton b,
                              Rectangle viewRect, Rectangle textRect, Rectangle iconRect){
    }
  


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
        return getPreferredSize(c);
    }

    public Dimension getPreferredSize(JComponent c) {
        AbstractButton b = (AbstractButton)c;
        return BasicGraphicsUtils.getPreferredButtonSize(b, defaultTextIconGap);
    }

    public Dimension getMaximumSize(JComponent c) {
        return getPreferredSize(c);
    }


}
