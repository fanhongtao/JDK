/*
 * @(#)SynthMenuItemUI.java	1.22 06/12/14
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.plaf.synth;

import javax.swing.plaf.basic.BasicHTML;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.text.View;
import sun.swing.plaf.synth.*;
import com.sun.java.swing.SwingUtilities2;


/**
 * Synth's MenuItemUI.
 *
 * @version 1.22, 12/14/06
 * @author Georges Saab
 * @author David Karlton
 * @author Arnaud Weber
 * @author Fredrik Lagerblad
 */
class SynthMenuItemUI extends BasicMenuItemUI implements
                                   PropertyChangeListener, SynthUI {
    private SynthStyle style;
    private SynthStyle accStyle;

    private String acceleratorDelimiter;

    public static ComponentUI createUI(JComponent c) {
        return new SynthMenuItemUI();
    }

    //
    // The next handful of static methods are used by both SynthMenuUI
    // and SynthMenuItemUI. This is necessitated by SynthMenuUI not
    // extending SynthMenuItemUI.
    //
    static Dimension getPreferredMenuItemSize(SynthContext context,
           SynthContext accContext, boolean useCheckAndArrow, JComponent c,
           Icon checkIcon, Icon arrowIcon, int defaultTextIconGap,
           String acceleratorDelimiter) {
        JMenuItem b = (JMenuItem) c;
        Icon icon = (Icon) b.getIcon(); 
        String text = b.getText();
        KeyStroke accelerator =  b.getAccelerator();
        String acceleratorText = "";

        if (accelerator != null) {
            int modifiers = accelerator.getModifiers();
            if (modifiers > 0) {
                acceleratorText = KeyEvent.getKeyModifiersText(modifiers);
                acceleratorText += acceleratorDelimiter;
          }
            int keyCode = accelerator.getKeyCode();
            if (keyCode != 0) {
                acceleratorText += KeyEvent.getKeyText(keyCode);
            } else {
                acceleratorText += accelerator.getKeyChar();
            }
        }

        Font font = context.getStyle().getFont(context);
        FontMetrics fm = b.getFontMetrics(font);
        FontMetrics fmAccel = b.getFontMetrics(accContext.getStyle().
                                               getFont(accContext));

        resetRects();
        
        layoutMenuItem(
                  context, fm, accContext, text, fmAccel, acceleratorText,
                  icon, checkIcon, arrowIcon, b.getVerticalAlignment(),
                  b.getHorizontalAlignment(), b.getVerticalTextPosition(),
                  b.getHorizontalTextPosition(), viewRect, iconRect, textRect,
                  acceleratorRect, checkIconRect, arrowIconRect,
                  text == null ? 0 : defaultTextIconGap, defaultTextIconGap,
                  useCheckAndArrow);
        // find the union of the icon and text rects
        r.setBounds(textRect);
        r = SwingUtilities.computeUnion(iconRect.x,
                                        iconRect.y,
                                        iconRect.width,
                                        iconRect.height,
                                        r);

	// To make the accelerator texts appear in a column,
        // find the widest MenuItem text and the widest accelerator text.

	// Get the parent, which stores the information.
	Container parent = b.getParent();

        if (parent instanceof JPopupMenu) {
            SynthPopupMenuUI popupUI = (SynthPopupMenuUI)SynthLookAndFeel.
                             getUIOfType(((JPopupMenu)parent).getUI(),
                                         SynthPopupMenuUI.class);

            if (popupUI != null) {
                r.width = popupUI.adjustTextWidth(r.width);

                popupUI.adjustAcceleratorWidth(acceleratorRect.width);
	    
                r.width += popupUI.getMaxAcceleratorWidth();
            }
	}
        else if (parent != null && !(b instanceof JMenu &&
                                     ((JMenu)b).isTopLevelMenu())) {
	    r.width += acceleratorRect.width;
        }
	
	if( useCheckAndArrow ) {
	    // Add in the checkIcon
	    r.width += checkIconRect.width;
	    r.width += defaultTextIconGap;

	    // Add in the arrowIcon
	    r.width += defaultTextIconGap;
	    r.width += arrowIconRect.width;
        }	

	r.width += 2*defaultTextIconGap;

        Insets insets = b.getInsets();
        if(insets != null) {
            r.width += insets.left + insets.right;
            r.height += insets.top + insets.bottom;
        }

        // if the width is even, bump it up one. This is critical
        // for the focus dash line to draw properly
        if(r.width%2 == 0) {
            r.width++;
        }

        // if the height is even, bump it up one. This is critical
        // for the text to center properly
        if(r.height%2 == 0) {
            r.height++;
        }
	return r.getSize();
    }

    static void paint(SynthContext context, SynthContext accContext,
                      Graphics g, Icon checkIcon, Icon arrowIcon,
                      boolean useCheckAndArrow, String acceleratorDelimiter,
                      int defaultTextIconGap) {
        JComponent c = context.getComponent();
        JMenuItem b = (JMenuItem)c;
        ButtonModel model = b.getModel();
        Insets i = b.getInsets();

        resetRects();

        viewRect.setBounds(0, 0, b.getWidth(), b.getHeight());

        viewRect.x += i.left;
        viewRect.y += i.top;
        viewRect.width -= (i.right + viewRect.x);
        viewRect.height -= (i.bottom + viewRect.y);

        SynthStyle style = context.getStyle();
        Font f = style.getFont(context);
        g.setFont(f);
        FontMetrics fm = SwingUtilities2.getFontMetrics(c, g, f);
        FontMetrics accFM = SwingUtilities2.getFontMetrics(c, g,
                                 accContext.getStyle().
                                             getFont(accContext));

        // get Accelerator text
        KeyStroke accelerator =  b.getAccelerator();
        String acceleratorText = "";
        if (accelerator != null) {
            int modifiers = accelerator.getModifiers();
            if (modifiers > 0) {
                acceleratorText = KeyEvent.getKeyModifiersText(modifiers);
                acceleratorText += acceleratorDelimiter;
	    }

            int keyCode = accelerator.getKeyCode();
            if (keyCode != 0) {
                acceleratorText += KeyEvent.getKeyText(keyCode);
            } else {
                acceleratorText += accelerator.getKeyChar();
            }
        }
        
        // layoutl the text and icon
        String text = layoutMenuItem(context, fm, accContext,
            b.getText(), accFM, acceleratorText, b.getIcon(),
            checkIcon, arrowIcon,
            b.getVerticalAlignment(), b.getHorizontalAlignment(),
            b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
            viewRect, iconRect, textRect, acceleratorRect, 
            checkIconRect, arrowIconRect,
            b.getText() == null ? 0 : defaultTextIconGap,
            defaultTextIconGap, useCheckAndArrow
        );

        // Paint the Check
        if (checkIcon != null && useCheckAndArrow ) {
            SynthIcon.paintIcon(checkIcon, context, g, checkIconRect.x,
                    checkIconRect.y, checkIconRect.width, checkIconRect.height);
        }

        // Paint the Icon
        if(b.getIcon() != null) {
            Icon icon;
            if(!model.isEnabled()) {
                icon = (Icon) b.getDisabledIcon();
            } else if(model.isPressed() && model.isArmed()) {
                icon = (Icon) b.getPressedIcon();
                if(icon == null) {
                    // Use default icon
                    icon = (Icon) b.getIcon();
                } 
            } else {
                icon = (Icon) b.getIcon();
            }
              
            if (icon!=null) {
                SynthIcon.paintIcon(icon, context, g, iconRect.x,
                    iconRect.y, iconRect.width, iconRect.height);
            }
        }

        // Draw the Text
        if(text != null) {
 	    View v = (View) c.getClientProperty(BasicHTML.propertyKey);
 	    if (v != null) {
 		v.paint(g, textRect);
 	    } else {
                g.setColor(style.getColor(context, ColorType.TEXT_FOREGROUND));
                g.setFont(style.getFont(context));
                style.getGraphicsUtils(context).paintText(context, g, text,
                        textRect.x, textRect.y, b.getDisplayedMnemonicIndex());
	    }
	}
	
        // Draw the Accelerator Text
        if(acceleratorText != null && !acceleratorText.equals("")) {
            // Get the maxAccWidth from the parent to calculate the offset.
            int accOffset = 0;
            Container parent = b.getParent();
            if (parent != null && parent instanceof JPopupMenu) {
                SynthPopupMenuUI popupUI = (SynthPopupMenuUI)
                                       ((JPopupMenu)parent).getUI();
                if (popupUI != null) {
                    // Calculate the offset, with which the accelerator texts
                    // will be drawn with.
                    int max = popupUI.getMaxAcceleratorWidth();
                    if (max > 0) {
                        accOffset = max - acceleratorRect.width;
                    }
                }
            }

            SynthStyle accStyle = accContext.getStyle();

            g.setColor(accStyle.getColor(accContext,
                                         ColorType.TEXT_FOREGROUND));
            g.setFont(accStyle.getFont(accContext));
            accStyle.getGraphicsUtils(accContext).paintText(
                     accContext, g, acceleratorText, acceleratorRect.x -
                     accOffset, acceleratorRect.y, -1);
        }

        // Paint the Arrow
        if (arrowIcon != null && useCheckAndArrow) {
            SynthIcon.paintIcon(arrowIcon, context, g, arrowIconRect.x,
                    arrowIconRect.y, arrowIconRect.width, arrowIconRect.height);
        }
    }

    /** 
     * Compute and return the location of the icons origin, the 
     * location of origin of the text baseline, and a possibly clipped
     * version of the compound labels string.  Locations are computed
     * relative to the viewRect rectangle. 
     */

    private static String layoutMenuItem(
        SynthContext context,
        FontMetrics fm,
        SynthContext accContext,
        String text,
        FontMetrics fmAccel,
        String acceleratorText,
        Icon icon,
        Icon checkIcon,
        Icon arrowIcon,
        int verticalAlignment,
        int horizontalAlignment,
        int verticalTextPosition,
        int horizontalTextPosition,
        Rectangle viewRect, 
        Rectangle iconRect, 
        Rectangle textRect,
        Rectangle acceleratorRect,
        Rectangle checkIconRect, 
        Rectangle arrowIconRect, 
        int textIconGap,
        int menuItemGap, boolean useCheckAndArrow
        )
    {

        context.getStyle().getGraphicsUtils(context).layoutText(
                context, fm, text, icon,horizontalAlignment, verticalAlignment,
                horizontalTextPosition, verticalTextPosition, viewRect,
                iconRect, textRect, textIconGap);

        /* Initialize the acceelratorText bounds rectangle textRect.  If a null 
         * or and empty String was specified we substitute "" here 
         * and use 0,0,0,0 for acceleratorTextRect.
         */
        if( (acceleratorText == null) || acceleratorText.equals("") ) {
            acceleratorRect.width = acceleratorRect.height = 0;
            acceleratorText = "";
        }
        else {
            SynthStyle style = accContext.getStyle();
            acceleratorRect.width = style.getGraphicsUtils(accContext).
                    computeStringWidth(accContext, fmAccel.getFont(), fmAccel,
                                       acceleratorText);
            acceleratorRect.height = fmAccel.getHeight();
        }

        /* Initialize the checkIcon bounds rectangle's width & height.
         */

	if( useCheckAndArrow) {
	    if (checkIcon != null) {
		checkIconRect.width = SynthIcon.getIconWidth(checkIcon,
                                                             context);
		checkIconRect.height = SynthIcon.getIconHeight(checkIcon,
                                                               context);
	    } 
	    else {
		checkIconRect.width = checkIconRect.height = 0;
	    }
	    
	    /* Initialize the arrowIcon bounds rectangle width & height.
	     */
	    
	    if (arrowIcon != null) {
		arrowIconRect.width = SynthIcon.getIconWidth(arrowIcon,
                                                             context);
		arrowIconRect.height = SynthIcon.getIconHeight(arrowIcon,
                                                               context);
	    } else {
		arrowIconRect.width = arrowIconRect.height = 0;
	    }
        }

        Rectangle labelRect = iconRect.union(textRect);
        if( SynthLookAndFeel.isLeftToRight(context.getComponent()) ) {
            textRect.x += menuItemGap;
            iconRect.x += menuItemGap;

            // Position the Accelerator text rect
            acceleratorRect.x = viewRect.x + viewRect.width -
                arrowIconRect.width - menuItemGap - acceleratorRect.width;
            
            // Position the Check and Arrow Icons 
            if (useCheckAndArrow) {
                checkIconRect.x = viewRect.x + menuItemGap;
                textRect.x += menuItemGap + checkIconRect.width;
                iconRect.x += menuItemGap + checkIconRect.width;
                arrowIconRect.x = viewRect.x + viewRect.width - menuItemGap
                                  - arrowIconRect.width;
            }
        } else {
            textRect.x -= menuItemGap;
            iconRect.x -= menuItemGap;

            // Position the Accelerator text rect
            acceleratorRect.x = viewRect.x + arrowIconRect.width + menuItemGap;

            // Position the Check and Arrow Icons 
            if (useCheckAndArrow) {
                checkIconRect.x = viewRect.x + viewRect.width - menuItemGap
                                  - checkIconRect.width;
                textRect.x -= menuItemGap + checkIconRect.width;
                iconRect.x -= menuItemGap + checkIconRect.width;      
                arrowIconRect.x = viewRect.x + menuItemGap;
            }
        }

        // Align the accelertor text and the check and arrow icons vertically
        // with the center of the label rect.  
        acceleratorRect.y = labelRect.y + (labelRect.height/2) - (acceleratorRect.height/2);
        if( useCheckAndArrow ) {
            arrowIconRect.y = labelRect.y + (labelRect.height/2) - (arrowIconRect.height/2);
            checkIconRect.y = labelRect.y + (labelRect.height/2) - (checkIconRect.height/2);
        }

        return text;
    }

    // these rects are used for painting and preferredsize calculations.
    // they used to be regenerated constantly.  Now they are reused.
    static Rectangle iconRect = new Rectangle();
    static Rectangle textRect = new Rectangle();
    static Rectangle acceleratorRect = new Rectangle();
    static Rectangle checkIconRect = new Rectangle();
    static Rectangle arrowIconRect = new Rectangle();
    static Rectangle viewRect = new Rectangle(Short.MAX_VALUE,Short.MAX_VALUE);
    static Rectangle r = new Rectangle();

    private static void resetRects() {
        iconRect.setBounds(0, 0, 0, 0);
        textRect.setBounds(0, 0, 0, 0);
        acceleratorRect.setBounds(0, 0, 0, 0);
        checkIconRect.setBounds(0, 0, 0, 0);
        arrowIconRect.setBounds(0, 0, 0, 0);
        viewRect.setBounds(0,0,Short.MAX_VALUE, Short.MAX_VALUE);
        r.setBounds(0, 0, 0, 0);
    }


    protected void installDefaults() {
        updateStyle(menuItem);
    }

    protected void installListeners() {
        super.installListeners();
        menuItem.addPropertyChangeListener(this);
    }

    private void updateStyle(JMenuItem mi) {
        SynthContext context = getContext(mi, ENABLED);
        SynthStyle oldStyle = style;

        style = SynthLookAndFeel.updateStyle(context, this);
        if (oldStyle != style) {
            String prefix = getPropertyPrefix();
            defaultTextIconGap = style.getInt(
                           context, prefix + ".textIconGap", 4);
            if (menuItem.getMargin() == null || 
                         (menuItem.getMargin() instanceof UIResource)) {
                Insets insets = (Insets)style.get(context, prefix + ".margin");

                if (insets == null) {
                    // Some places assume margins are non-null.
                    insets = SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS;
                }
                menuItem.setMargin(insets);
            }
            acceleratorDelimiter = style.getString(context, prefix +
                                            ".acceleratorDelimiter", "+");

            arrowIcon = style.getIcon(context, prefix + ".arrowIcon");

            checkIcon = style.getIcon(context, prefix + ".checkIcon");
            if (oldStyle != null) {
                uninstallKeyboardActions();
                installKeyboardActions();
            }
        }
        context.dispose();

        SynthContext accContext = getContext(mi, Region.MENU_ITEM_ACCELERATOR,
                                             ENABLED);

        accStyle = SynthLookAndFeel.updateStyle(accContext, this);
        accContext.dispose();
    }

    protected void uninstallDefaults() {
        SynthContext context = getContext(menuItem, ENABLED);
        style.uninstallDefaults(context);
        context.dispose();
        style = null;

        SynthContext accContext = getContext(menuItem,
                                     Region.MENU_ITEM_ACCELERATOR, ENABLED);
        accStyle.uninstallDefaults(accContext);
        accContext.dispose();
        accStyle = null;

        super.uninstallDefaults();
    }

    protected void uninstallListeners() {
        super.uninstallListeners();
        menuItem.removePropertyChangeListener(this);
    }

    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    SynthContext getContext(JComponent c, int state) {
        return SynthContext.getContext(SynthContext.class, c,
                    SynthLookAndFeel.getRegion(c), style, state);
    }

    public SynthContext getContext(JComponent c, Region region) {
        return getContext(c, region, getComponentState(c, region));
    }

    private SynthContext getContext(JComponent c, Region region, int state) {
        return SynthContext.getContext(SynthContext.class, c,
                                       region, accStyle, state);
    }

    private Region getRegion(JComponent c) {
        return SynthLookAndFeel.getRegion(c);
    }

    private int getComponentState(JComponent c) {
        int state;

        if (!c.isEnabled()) {
            state = DISABLED;
        } else {
            if (menuItem.isArmed()) {
                state = MOUSE_OVER;
            }
            else {
                state = SynthLookAndFeel.getComponentState(c);
            }
        }
        if (menuItem.isSelected()) {
            state |= SELECTED;
        }
        return state;
    }

    private int getComponentState(JComponent c, Region region) {
        return getComponentState(c);
    }

    protected Dimension getPreferredMenuItemSize(JComponent c,
                                                     Icon checkIcon,
                                                     Icon arrowIcon,
                                                     int defaultTextIconGap) {
        SynthContext context = getContext(c);
        SynthContext accContext = getContext(c, Region.MENU_ITEM_ACCELERATOR);
        Dimension value = getPreferredMenuItemSize(context, accContext,
                  true, c, checkIcon, arrowIcon, defaultTextIconGap,
                  acceleratorDelimiter);
        context.dispose();
        accContext.dispose();
        return value;
    }


    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        paintBackground(context, g, c);
        paint(context, g);
        context.dispose();
    }

    public void paint(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        paint(context, g);
        context.dispose();
    }

    protected void paint(SynthContext context, Graphics g) {
        SynthContext accContext = getContext(menuItem,
                                             Region.MENU_ITEM_ACCELERATOR);

        String prefix = getPropertyPrefix();
        paint(context, accContext, g,
                style.getIcon(getContext(context.getComponent()),
                    prefix + ".checkIcon"),
                style.getIcon(getContext(context.getComponent()),
                    prefix + ".arrowIcon"),
                true, acceleratorDelimiter, defaultTextIconGap);
        accContext.dispose();
    }

    void paintBackground(SynthContext context, Graphics g, JComponent c) {
        context.getPainter().paintMenuItemBackground(context, g, 0, 0,
                                                c.getWidth(), c.getHeight());
    }

    public void paintBorder(SynthContext context, Graphics g, int x,
                            int y, int w, int h) {
        context.getPainter().paintMenuItemBorder(context, g, x, y, w, h);
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (SynthLookAndFeel.shouldUpdateStyle(e)) {
            updateStyle((JMenuItem)e.getSource());
        }
    }
}
