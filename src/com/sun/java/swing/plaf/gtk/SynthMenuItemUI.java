/*
 * @(#)SynthMenuItemUI.java	1.18 03/04/10
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import javax.swing.plaf.basic.BasicHTML;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.text.View;


/**
 * BasicMenuItem implementation
 *
 * @version 1.18, 04/10/03 (based on BasicMenuItemUI v 1.116)
 * @author Georges Saab
 * @author David Karlton
 * @author Arnaud Weber
 * @author Fredrik Lagerblad
 */
class SynthMenuItemUI extends MenuItemUI implements SynthUI {
    static final String MAX_TEXT_WIDTH =  "maxTextWidth";
    static final String MAX_ACC_WIDTH  =  "maxAccWidth";

    private SynthStyle style;
    private SynthStyle accStyle;
    protected JMenuItem menuItem = null;
    private   String acceleratorDelimiter;

    protected int defaultTextIconGap;

    protected MouseInputListener mouseInputListener;
    protected MenuDragMouseListener menuDragMouseListener;
    protected MenuKeyListener menuKeyListener;
    private   PropertyChangeListener propertyChangeListener;
    
    protected Icon arrowIcon = null;
    protected Icon checkIcon = null;

    /** Used for accelerator binding, lazily created. */
    InputMap windowInputMap;

    /* diagnostic aids -- should be false for production builds. */
    private static final boolean TRACE =   false; // trace creates and disposes

    private static final boolean VERBOSE = false; // show reuse hits/misses
    private static final boolean DEBUG =   false;  // show bad params, misc.

    public static ComponentUI createUI(JComponent c) {
        return new SynthMenuItemUI();
    }

    public static void loadActionMap(ActionMap map) {
        // NOTE: this needs to remain static. If you have a need to
        // have Actions that reference the UI in the ActionMap,
        // then you'll also need to change the registeration of the
        // ActionMap.
	map.put("doClick", new ClickAction());
    }

    public void installUI(JComponent c) {
        menuItem = (JMenuItem) c;

        installDefaults();
        installComponents(menuItem);
        installListeners();
        installKeyboardActions();
    }
	

    protected void installDefaults() {
        fetchStyle(menuItem);
    }

    private void fetchStyle(JMenuItem mi) {
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
        }
        context.dispose();

        SynthContext accContext = getContext(mi, Region.MENU_ITEM_ACCELERATOR,
                                             ENABLED);

        accStyle = SynthLookAndFeel.updateStyle(accContext, this);
        accContext.dispose();
    }

    /**
     * @since 1.3
     */
    protected void installComponents(JMenuItem menuItem){
 	BasicHTML.updateRenderer(menuItem, menuItem.getText());
    }

    protected String getPropertyPrefix() {
        return "MenuItem";
    }

    protected void installListeners() {
	if ((mouseInputListener = createMouseInputListener(menuItem)) != null) {
	    menuItem.addMouseListener(mouseInputListener);
	    menuItem.addMouseMotionListener(mouseInputListener);
	}
        if ((menuDragMouseListener = createMenuDragMouseListener(menuItem)) != null) {
	    menuItem.addMenuDragMouseListener(menuDragMouseListener);
	}
	if ((menuKeyListener = createMenuKeyListener(menuItem)) != null) {
	    menuItem.addMenuKeyListener(menuKeyListener);
	}
	if ((propertyChangeListener = createPropertyChangeListener(menuItem)) != null) {
	    menuItem.addPropertyChangeListener(propertyChangeListener);
	}
    }

    protected void installKeyboardActions() {
        registerActionMap();
	updateAcceleratorBinding();
    }

    public void uninstallUI(JComponent c) {
	menuItem = (JMenuItem)c;
        uninstallDefaults();
        uninstallComponents(menuItem);
        uninstallListeners();
        uninstallKeyboardActions();

        menuItem = null;
    }


    protected void uninstallDefaults() {
        if (menuItem.getMargin() instanceof UIResource)
            menuItem.setMargin(null);
        if (arrowIcon instanceof UIResource)
            arrowIcon = null;
        if (checkIcon instanceof UIResource)
            checkIcon = null;

        SynthContext context = getContext(menuItem, ENABLED);
        style.uninstallDefaults(context);
        context.dispose();
        style = null;

        SynthContext accContext = getContext(menuItem,
                                     Region.MENU_ITEM_ACCELERATOR, ENABLED);
        accStyle.uninstallDefaults(accContext);
        accContext.dispose();
        accStyle = null;
    }

    /**
     * @since 1.3
     */
    protected void uninstallComponents(JMenuItem menuItem){
	BasicHTML.updateRenderer(menuItem, "");
    }

    protected void uninstallListeners() {
	if (mouseInputListener != null) {
	    menuItem.removeMouseListener(mouseInputListener);
	    menuItem.removeMouseMotionListener(mouseInputListener);
	}
	if (menuDragMouseListener != null) {
	    menuItem.removeMenuDragMouseListener(menuDragMouseListener);
	}
	if (menuKeyListener != null) {
	    menuItem.removeMenuKeyListener(menuKeyListener);
	}
	if (propertyChangeListener != null) {
	    menuItem.removePropertyChangeListener(propertyChangeListener);
	}

        mouseInputListener = null;
        menuDragMouseListener = null;
        menuKeyListener = null;
	propertyChangeListener = null;
    }

    protected void uninstallKeyboardActions() {
	SwingUtilities.replaceUIActionMap(menuItem, null);
	if (windowInputMap != null) {
	    SwingUtilities.replaceUIInputMap(menuItem, JComponent.
					   WHEN_IN_FOCUSED_WINDOW, null);
	    windowInputMap = null;
	}
    }

    protected MouseInputListener createMouseInputListener(JComponent c) {
        return new MouseInputHandler();
    }

    protected MenuDragMouseListener createMenuDragMouseListener(JComponent c) {
        return new MenuDragMouseHandler();
    }

    protected MenuKeyListener createMenuKeyListener(JComponent c) {
	return new MenuKeyHandler();
    }

    private PropertyChangeListener createPropertyChangeListener(JComponent c) {
        return new PropertyChangeHandler();
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
            return DISABLED;
        }
        if (menuItem.isArmed()) {
            state = MOUSE_OVER;
        }
        else {
            state = SynthLookAndFeel.getComponentState(c);
        }
        if (menuItem.isSelected()) {
            state |= SELECTED;
        }
        return state;
    }

    private int getComponentState(JComponent c, Region region) {
        return getComponentState(c);
    }

    void registerActionMap() {
        LazyActionMap.installLazyActionMap(menuItem, SynthMenuItemUI.class,
                                           "MenuItem.actionMap");
    }

    InputMap createInputMap(int condition) {
	if (condition == JComponent.WHEN_IN_FOCUSED_WINDOW) {
	    return new ComponentInputMapUIResource(menuItem);
	}
	return null;
    }

    void updateAcceleratorBinding() {
	KeyStroke accelerator = menuItem.getAccelerator();

	if (windowInputMap != null) {
	    windowInputMap.clear();
	}
	if (accelerator != null) {
	    if (windowInputMap == null) {
		windowInputMap = createInputMap(JComponent.
						WHEN_IN_FOCUSED_WINDOW);
		SwingUtilities.replaceUIInputMap(menuItem,
			   JComponent.WHEN_IN_FOCUSED_WINDOW, windowInputMap);
	    }
	    windowInputMap.put(accelerator, "doClick");
	}
    }

    public Dimension getMinimumSize(JComponent c) {
	Dimension d = null;
 	View v = (View) c.getClientProperty(BasicHTML.propertyKey);
 	if (v != null) {
	    d = getPreferredSize(c);
 	    d.width -= v.getPreferredSpan(View.X_AXIS) - v.getMinimumSpan(View.X_AXIS);
 	}
 	return d;	
    }

    public Dimension getPreferredSize(JComponent c) {
        return getPreferredMenuItemSize(c,
                                        checkIcon, 
                                        arrowIcon, 
                                        defaultTextIconGap);
    }

    public Dimension getMaximumSize(JComponent c) {
	Dimension d = null;
 	View v = (View) c.getClientProperty(BasicHTML.propertyKey);
 	if (v != null) {
	    d = getPreferredSize(c);
 	    d.width += v.getMaximumSpan(View.X_AXIS) - v.getPreferredSpan(View.X_AXIS);
 	}
 	return d;
    }

    // these rects are used for painting and preferredsize calculations.
    // they used to be regenerated constantly.  Now they are reused.
    static Rectangle iconRect = new Rectangle();
    static Rectangle textRect = new Rectangle();
    static Rectangle acceleratorRect = new Rectangle();
    static Rectangle checkIconRect = new Rectangle();
    static Rectangle arrowIconRect = new Rectangle();
    static Rectangle viewRect = new Rectangle(Short.MAX_VALUE, Short.MAX_VALUE);
    static Rectangle r = new Rectangle();

    private void resetRects() {
        iconRect.setBounds(0, 0, 0, 0);
        textRect.setBounds(0, 0, 0, 0);
        acceleratorRect.setBounds(0, 0, 0, 0);
        checkIconRect.setBounds(0, 0, 0, 0);
        arrowIconRect.setBounds(0, 0, 0, 0);
        viewRect.setBounds(0,0,Short.MAX_VALUE, Short.MAX_VALUE);
        r.setBounds(0, 0, 0, 0);
    }

    protected Dimension getPreferredMenuItemSize(JComponent c,
                                                     Icon checkIcon,
                                                     Icon arrowIcon,
                                                     int defaultTextIconGap) {
        JMenuItem b = (JMenuItem) c;
        Icon icon = (Icon) b.getIcon(); 
        String text = b.getText();
        KeyStroke accelerator =  b.getAccelerator();
        String acceleratorText = "";

        if (accelerator != null) {
            int modifiers = accelerator.getModifiers();
            if (modifiers > 0) {
                acceleratorText = KeyEvent.getKeyModifiersText(modifiers);
                //acceleratorText += "-";
                acceleratorText += acceleratorDelimiter;
          }
            int keyCode = accelerator.getKeyCode();
            if (keyCode != 0) {
                acceleratorText += KeyEvent.getKeyText(keyCode);
            } else {
                acceleratorText += accelerator.getKeyChar();
            }
        }

        SynthContext context = getContext(c);
        SynthContext accContext = getContext(c, Region.MENU_ITEM_ACCELERATOR);
        Font font = context.getStyle().getFont(context);
        FontMetrics fm = b.getToolkit().getFontMetrics(font);
        FontMetrics fmAccel = b.getToolkit().getFontMetrics(
                      accContext.getStyle().getFont(accContext));

        resetRects();
        
        layoutMenuItem(
                  context, fm, accContext, text, fmAccel, acceleratorText,
                  icon, checkIcon, arrowIcon, b.getVerticalAlignment(),
                  b.getHorizontalAlignment(), b.getVerticalTextPosition(),
                  b.getHorizontalTextPosition(), viewRect, iconRect, textRect,
                  acceleratorRect, checkIconRect, arrowIconRect,
                  text == null ? 0 : defaultTextIconGap, defaultTextIconGap
                  );
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
	Container parent = menuItem.getParent();

        if (parent instanceof JPopupMenu) {
            JPopupMenu popup = (JPopupMenu)parent;
            ComponentUI ui = popup.getUI();
            if (ui instanceof SynthPopupMenuUI) {
                SynthPopupMenuUI popupUI = (SynthPopupMenuUI)ui;

                r.width = popupUI.adjustTextWidth(r.width);

                popupUI.adjustAcceleratorWidth(acceleratorRect.width);
	    
                r.width += popupUI.getMaxAcceleratorWidth();
            }
            else {
                // We don't have a SynthPopupMenuUI, fallback to storing the
                // values in the client properties of the JPopupMenu.
                //Get widest text so far from parent, if no one exists null is returned.
                Integer maxTextWidth = (Integer)popup.getClientProperty(
                                       MAX_TEXT_WIDTH);
                Integer maxAccWidth = (Integer)popup.
                              getClientProperty(MAX_ACC_WIDTH);
                int maxTextValue = maxTextWidth!=null ?
                                   maxTextWidth.intValue() : 0;
                int maxAccValue = maxAccWidth!=null ?
                                  maxAccWidth.intValue() : 0;
	    
                // Compare the text widths, and adjust the r.width to the
                // widest.
                if (r.width < maxTextValue) {
                    r.width = maxTextValue;
                } else {
                    popup.putClientProperty(MAX_TEXT_WIDTH,
                                            new Integer(r.width));
                }
	    
                // Compare the accelarator widths.
                if (acceleratorRect.width > maxAccValue) {
                    maxAccValue = acceleratorRect.width;
                    popup.putClientProperty(MAX_ACC_WIDTH, new Integer(
                                                acceleratorRect.width));
                }
	    
                //Add on the widest accelerator 
                r.width += maxAccValue;
            }
	}
	
	if( useCheckAndArrow() ) {
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
/*
	if(!(b instanceof JMenu && ((JMenu) b).isTopLevelMenu()) ) {
	    
	    // Container parent = menuItem.getParent();
	    JComponent p = (JComponent) parent;
	    
	    System.out.println("MaxText: "+p.getClientProperty(BasicMenuItemUI.MAX_TEXT_WIDTH));
	    System.out.println("MaxACC"+p.getClientProperty(BasicMenuItemUI.MAX_ACC_WIDTH));
	    
	    System.out.println("returning pref.width: " + r.width);
	    System.out.println("Current getSize: " + b.getSize() + "\n");
        }*/
        context.dispose();
        accContext.dispose();
	return r.getSize();
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

        SynthContext accContext = getContext(menuItem,
                                             Region.MENU_ITEM_ACCELERATOR);
        SynthStyle style = context.getStyle();
        Font f = style.getFont(context);
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics(f);
        FontMetrics accFM = g.getFontMetrics(accContext.getStyle().
                                             getFont(accContext));

        // get Accelerator text
        KeyStroke accelerator =  b.getAccelerator();
        String acceleratorText = "";
        if (accelerator != null) {
            int modifiers = accelerator.getModifiers();
            if (modifiers > 0) {
                acceleratorText = KeyEvent.getKeyModifiersText(modifiers);
                //acceleratorText += "-";
                acceleratorText += acceleratorDelimiter;
	    }

            int keyCode = accelerator.getKeyCode();
            if (keyCode != 0) {
                acceleratorText += KeyEvent.getKeyText(keyCode);
            } else {
                acceleratorText += accelerator.getKeyChar();
            }
        }
        
        // layout the text and icon
        String text = layoutMenuItem(context, fm, accContext,
            b.getText(), accFM, acceleratorText, b.getIcon(),
            checkIcon, arrowIcon,
            b.getVerticalAlignment(), b.getHorizontalAlignment(),
            b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
            viewRect, iconRect, textRect, acceleratorRect, 
            checkIconRect, arrowIconRect,
            b.getText() == null ? 0 : defaultTextIconGap,
            defaultTextIconGap
        );

        // Paint the Check
        if (checkIcon != null && useCheckAndArrow() ) {
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
                style.getSynthGraphics(context).paintText(context, g, text,
                         textRect.x, textRect.y, menuItem.
                         getDisplayedMnemonicIndex());
	    }
	}
	
        // Draw the Accelerator Text
        if(acceleratorText != null && !acceleratorText.equals("")) {
            // Get the maxAccWidth from the parent to calculate the offset.
            int accOffset = 0;
            Container parent = menuItem.getParent();
            if (parent != null && parent instanceof JPopupMenu) {
                ComponentUI ui = ((JPopupMenu)parent).getUI();
                if (ui instanceof SynthPopupMenuUI) {
                    SynthPopupMenuUI popupUI = (SynthPopupMenuUI)ui;
                    // Calculate the offset, with which the accelerator texts
                    // will be drawn with.
                    int max = popupUI.getMaxAcceleratorWidth();
                    if (max > 0) {
                        accOffset = max - acceleratorRect.width;
                    }
                }
                else {
                    // We don't have a SynthPopupMenuUI, fallback to using the
                    // values in the client properties of the JPopupMenu.
                    Integer maxValueInt = (Integer)((JComponent)parent).
                               getClientProperty(MAX_ACC_WIDTH);
                    int maxValue = maxValueInt != null ?
                           maxValueInt.intValue() : acceleratorRect.width;

                    // Calculate the offset, with which the accelerator texts
                    // will be drawn with.
                    accOffset = maxValue - acceleratorRect.width;
                }
            }

            SynthStyle accStyle = accContext.getStyle();

            g.setColor(accStyle.getColor(accContext,
                                         ColorType.TEXT_FOREGROUND));
            g.setFont(accStyle.getFont(accContext));
            accStyle.getSynthGraphics(accContext).paintText(
                     accContext, g, acceleratorText, acceleratorRect.x -
                     accOffset, acceleratorRect.y, -1);
        }

        // Paint the Arrow
        if (arrowIcon != null && useCheckAndArrow()) {
            SynthIcon.paintIcon(arrowIcon, context, g, arrowIconRect.x,
                    arrowIconRect.y, arrowIconRect.width, arrowIconRect.height);
        }
        accContext.dispose();
    }

    /** 
     * Compute and return the location of the icons origin, the 
     * location of origin of the text baseline, and a possibly clipped
     * version of the compound labels string.  Locations are computed
     * relative to the viewRect rectangle. 
     */

    private String layoutMenuItem(
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
        int menuItemGap
        )
    {

        context.getStyle().getSynthGraphics(context).layoutText(
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
            acceleratorRect.width = style.getSynthGraphics(accContext).
                    computeStringWidth(accContext, fmAccel.getFont(), fmAccel,
                                       acceleratorText);
            acceleratorRect.height = fmAccel.getHeight();
        }

        /* Initialize the checkIcon bounds rectangle's width & height.
         */

	if( useCheckAndArrow()) {
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
        if( SynthLookAndFeel.isLeftToRight(menuItem) ) {
            textRect.x += menuItemGap;
            iconRect.x += menuItemGap;

            // Position the Accelerator text rect
            acceleratorRect.x = viewRect.x + viewRect.width -
                arrowIconRect.width - menuItemGap - acceleratorRect.width;
            
            // Position the Check and Arrow Icons 
            if (useCheckAndArrow()) {
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
            if (useCheckAndArrow()) {
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
        if( useCheckAndArrow() ) {
            arrowIconRect.y = labelRect.y + (labelRect.height/2) - (arrowIconRect.height/2);
            checkIconRect.y = labelRect.y + (labelRect.height/2) - (checkIconRect.height/2);
        }

        /*
        System.out.println("Layout: text="+menuItem.getText()+"\n\tv="
                           +viewRect+"\n\tc="+checkIconRect+"\n\ti="
                           +iconRect+"\n\tt="+textRect+"\n\tacc="
                           +acceleratorRect+"\n\ta="+arrowIconRect+"\n");
        */
        
        return text;
    }

    /*
     * Returns false if the component is a JMenu and it is a top
     * level menu (on the menubar).
     */
    private boolean useCheckAndArrow(){
	boolean b = true;
	if((menuItem instanceof JMenu) &&
	   (((JMenu)menuItem).isTopLevelMenu())) {
	    b = false;
	}
	return b;
    }

    public MenuElement[] getPath() {
        MenuSelectionManager m = MenuSelectionManager.defaultManager();
        MenuElement oldPath[] = m.getSelectedPath();
        MenuElement newPath[];
        int i = oldPath.length;
        if (i == 0)
            return new MenuElement[0];
        Component parent = menuItem.getParent();
        if (oldPath[i-1].getComponent() == parent) {
            // The parent popup menu is the last so far
            newPath = new MenuElement[i+1];
            System.arraycopy(oldPath, 0, newPath, 0, i);
            newPath[i] = menuItem;
        } else {
            // A sibling menuitem is the current selection
            // 
            //  This probably needs to handle 'exit submenu into 
            // a menu item.  Search backwards along the current
            // selection until you find the parent popup menu,
            // then copy up to that and add yourself...
            int j;
            for (j = oldPath.length-1; j >= 0; j--) {
                if (oldPath[j].getComponent() == parent)
                    break;
            }
            newPath = new MenuElement[j+2];
            System.arraycopy(oldPath, 0, newPath, 0, j+1);
            newPath[j+1] = menuItem;
            /*
            System.out.println("Sibling condition -- ");
            System.out.println("Old array : ");
            printMenuElementArray(oldPath, false);
            System.out.println("New array : ");
            printMenuElementArray(newPath, false);
            */
        }
        return newPath;
    }

    void printMenuElementArray(MenuElement path[], boolean dumpStack) {
        System.out.println("Path is(");
        int i, j;
        for(i=0,j=path.length; i<j ;i++){
            for (int k=0; k<=i; k++)
                System.out.print("  ");
            MenuElement me = (MenuElement) path[i];
            if(me instanceof JMenuItem) 
                System.out.println(((JMenuItem)me).getText() + ", ");
            else if (me == null)
                System.out.println("NULL , ");
            else
                System.out.println("" + me + ", ");
        }
        System.out.println(")");

        if (dumpStack == true)
            Thread.dumpStack();
    }
    protected class MouseInputHandler implements MouseInputListener {
        public void mouseClicked(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {
        }
        public void mouseReleased(MouseEvent e) {
            MenuSelectionManager manager = 
                MenuSelectionManager.defaultManager();
            Point p = e.getPoint();
            if(p.x >= 0 && p.x < menuItem.getWidth() &&
               p.y >= 0 && p.y < menuItem.getHeight()) {
		doClick(manager);
            } else {
                manager.processMouseEvent(e);
            }
        }
        public void mouseEntered(MouseEvent e) {
            MenuSelectionManager manager = MenuSelectionManager.defaultManager();
	    int modifiers = e.getModifiers();
	    // 4188027: drag enter/exit added in JDK 1.1.7A, JDK1.2	    
	    if ((modifiers & (InputEvent.BUTTON1_MASK |
			      InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) !=0 ) {
		MenuSelectionManager.defaultManager().processMouseEvent(e);
	    } else {
	    manager.setSelectedPath(getPath());
	     }
        }
        public void mouseExited(MouseEvent e) {
            MenuSelectionManager manager = MenuSelectionManager.defaultManager();

	    int modifiers = e.getModifiers();
	    // 4188027: drag enter/exit added in JDK 1.1.7A, JDK1.2
	    if ((modifiers & (InputEvent.BUTTON1_MASK |
			      InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) !=0 ) {
		MenuSelectionManager.defaultManager().processMouseEvent(e);
	    } else {

		MenuElement path[] = manager.getSelectedPath();
		if (path.length > 1) {
		    MenuElement newPath[] = new MenuElement[path.length-1];
		    int i,c;
		    for(i=0,c=path.length-1;i<c;i++)
			newPath[i] = path[i];
		    manager.setSelectedPath(newPath);
		}
		}
        }

        public void mouseDragged(MouseEvent e) {
            MenuSelectionManager.defaultManager().processMouseEvent(e);
        }
        public void mouseMoved(MouseEvent e) {
        }
    }


    private class MenuDragMouseHandler implements MenuDragMouseListener {
        public void menuDragMouseEntered(MenuDragMouseEvent e) {
            MenuSelectionManager manager = e.getMenuSelectionManager();
            MenuElement path[] = e.getPath();
            manager.setSelectedPath(path);
        }
        public void menuDragMouseDragged(MenuDragMouseEvent e) {
            MenuSelectionManager manager = e.getMenuSelectionManager();
            MenuElement path[] = e.getPath();
            manager.setSelectedPath(path);
        }
        public void menuDragMouseExited(MenuDragMouseEvent e) {}
        public void menuDragMouseReleased(MenuDragMouseEvent e) {
            MenuSelectionManager manager = e.getMenuSelectionManager();
            MenuElement path[] = e.getPath();
            Point p = e.getPoint();
            if(p.x >= 0 && p.x < menuItem.getWidth() &&
               p.y >= 0 && p.y < menuItem.getHeight()) {
		doClick(manager);
            } else {
                manager.clearSelectedPath();
            }
        }
    }

    private class MenuKeyHandler implements MenuKeyListener {
	
	/**
	 * Handles the mnemonic key typed in the MenuItem if this menuItem is in
	 * a standalone popup menu. This invocation normally 
	 * handled in BasicMenuUI.MenuKeyHandler.menuKeyPressed. Ideally, the 
	 * MenuKeyHandlers for both BasicMenuItemUI and BasicMenuUI can be consolidated
	 * into BasicPopupMenuUI but that would require an semantic change. This
	 * would result in a performance win since we can shortcut a lot of the needless
	 * processing from MenuSelectionManager.processKeyEvent(). See 4670831.
	 */
        public void menuKeyTyped(MenuKeyEvent e) {
	    if (DEBUG) {
		System.out.println("in BasicMenuItemUI.menuKeyTyped for " + menuItem.getText());
	    }
            int key = menuItem.getMnemonic();
            if(key == 0 || e.getPath().length != 2) // Hack! Only proceed if in a JPopupMenu
                return;
            if(lower((char)key) == lower(e.getKeyChar())) {
                MenuSelectionManager manager = 
                    e.getMenuSelectionManager();
		doClick(manager);
                e.consume();
            }
        }
        public void menuKeyPressed(MenuKeyEvent e) {
	    if (DEBUG) {
		System.out.println("in BasicMenuItemUI.menuKeyPressed for " + menuItem.getText());
	    }
	}
        public void menuKeyReleased(MenuKeyEvent e) {}

        private char lower(char keyChar) {
	    return Character.toLowerCase(keyChar);
        }
    }

    private class PropertyChangeHandler implements PropertyChangeListener {
	public void propertyChange(PropertyChangeEvent e) {
	    String name = e.getPropertyName();

            if (SynthLookAndFeel.shouldUpdateStyle(e)) {
                fetchStyle((JMenuItem)e.getSource());
            }
	    if (name.equals("labelFor") || name.equals("displayedMnemonic") ||
		name.equals("accelerator")) {
		updateAcceleratorBinding();
	    } else if (name.equals("text") || "font".equals(name) ||
                       "foreground".equals(name)) {
		// remove the old html view client property if one
		// existed, and install a new one if the text installed
		// into the JLabel is html source.
		JMenuItem lbl = ((JMenuItem) e.getSource());
		String text = lbl.getText();
		BasicHTML.updateRenderer(lbl, text);
	    }
	}
    }  

    private static class ClickAction extends AbstractAction {
	public void actionPerformed(ActionEvent e) {
	    JMenuItem mi = (JMenuItem)e.getSource();
	    MenuSelectionManager.defaultManager().clearSelectedPath();
	    mi.doClick();
	}
    }

    /**
     * Call this method when a menu item is to be activated.
     * This method handles some of the details of menu item activation
     * such as clearing the selected path and messaging the 
     * JMenuItem's doClick() method.
     *
     * @param msm  A MenuSelectionManager. The visual feedback and 
     *             internal bookkeeping tasks are delegated to 
     *             this MenuSelectionManager. If <code>null</code> is
     *             passed as this argument, the 
     *             <code>MenuSelectionManager.defaultManager</code> is
     *             used.
     * @see MenuSelectionManager
     * @see JMenuItem#doClick(int)
     * @since 1.4
     */
    protected void doClick(MenuSelectionManager msm) {
	// Auditory cue
	if (! isInternalFrameSystemMenu()) {
            SynthLookAndFeel.playSound(menuItem, getPropertyPrefix() +
                                       ".commandSound");
	}
	// Visual feedback
	if (msm == null) {
	    msm = MenuSelectionManager.defaultManager();
	}
	msm.clearSelectedPath();
	menuItem.doClick(0);
    }

    /** 
     * This is to see if the menu item in question is part of the 
     * system menu on an internal frame.
     * The Strings that are being checked can be found in 
     * MetalInternalFrameTitlePaneUI.java,
     * WindowsInternalFrameTitlePaneUI.java, and
     * MotifInternalFrameTitlePaneUI.java.
     *
     * @since 1.4
     */
    private boolean isInternalFrameSystemMenu() {
	String actionCommand = menuItem.getActionCommand();
 	if ((actionCommand == "Close") ||
	    (actionCommand == "Minimize") ||
	    (actionCommand == "Restore") ||
	    (actionCommand == "Maximize")) {
	  return true;
	} else {
	  return false;
	} 
    }

}
