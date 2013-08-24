/*
 * @(#)BasicMenuItemUI.java	1.130 09/08/10
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package javax.swing.plaf.basic;

import sun.swing.MenuItemCheckIconFactory;
import com.sun.java.swing.SwingUtilities2;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.text.View;

import sun.swing.UIAction;

/**
 * BasicMenuItem implementation
 *
 * @version 1.130 08/10/09
 * @author Georges Saab
 * @author David Karlton
 * @author Arnaud Weber
 * @author Fredrik Lagerblad
 */
public class BasicMenuItemUI extends MenuItemUI
{
    protected JMenuItem menuItem = null;
    protected Color selectionBackground;
    protected Color selectionForeground;    
    protected Color disabledForeground;
    protected Color acceleratorForeground;
    protected Color acceleratorSelectionForeground;
    private   String acceleratorDelimiter;

    protected int defaultTextIconGap;
    protected Font acceleratorFont;

    protected MouseInputListener mouseInputListener;
    protected MenuDragMouseListener menuDragMouseListener;
    protected MenuKeyListener menuKeyListener;
    // BasicMenuUI also uses this.
    Handler handler;
    
    protected Icon arrowIcon = null;
    protected Icon checkIcon = null;

    protected boolean oldBorderPainted;

    /* diagnostic aids -- should be false for production builds. */
    private static final boolean TRACE =   false; // trace creates and disposes

    private static final boolean VERBOSE = false; // show reuse hits/misses
    private static final boolean DEBUG =   false;  // show bad params, misc.

    /* Client Property keys for text and accelerator text widths */
    static final String MAX_TEXT_WIDTH =  "maxTextWidth";
    static final String MAX_ACC_WIDTH  =  "maxAccWidth";

    static void loadActionMap(LazyActionMap map) {
        // NOTE: BasicMenuUI also calls into this method.
	map.put(new Actions(Actions.CLICK));
        BasicLookAndFeel.installAudioActionMap(map);
    }

    public static ComponentUI createUI(JComponent c) {
        return new BasicMenuItemUI();
    }

    public void installUI(JComponent c) {
        menuItem = (JMenuItem) c;

        installDefaults();
        installComponents(menuItem);
        installListeners();
        installKeyboardActions();
    }
	

    protected void installDefaults() {
        String prefix = getPropertyPrefix();

        acceleratorFont = UIManager.getFont("MenuItem.acceleratorFont");

        Object opaque = UIManager.get(getPropertyPrefix() + ".opaque");
        if (opaque != null) {
            LookAndFeel.installProperty(menuItem, "opaque", opaque);
        }
        else {
            LookAndFeel.installProperty(menuItem, "opaque", Boolean.TRUE);
        }
        if(menuItem.getMargin() == null || 
           (menuItem.getMargin() instanceof UIResource)) {
            menuItem.setMargin(UIManager.getInsets(prefix + ".margin"));
        }

        defaultTextIconGap = 4;   // Should be from table

        LookAndFeel.installBorder(menuItem, prefix + ".border");
        oldBorderPainted = menuItem.isBorderPainted(); // not used anymore
        LookAndFeel.installProperty(menuItem, "borderPainted",
                                    UIManager.get(prefix + ".borderPainted"));
        LookAndFeel.installColorsAndFont(menuItem,
                                         prefix + ".background",
                                         prefix + ".foreground",
                                         prefix + ".font");
        
        // MenuItem specific defaults
        if (selectionBackground == null || 
            selectionBackground instanceof UIResource) {
            selectionBackground = 
                UIManager.getColor(prefix + ".selectionBackground");
        }
        if (selectionForeground == null || 
            selectionForeground instanceof UIResource) {
            selectionForeground = 
                UIManager.getColor(prefix + ".selectionForeground");
        }
        if (disabledForeground == null || 
            disabledForeground instanceof UIResource) {
            disabledForeground = 
                UIManager.getColor(prefix + ".disabledForeground");
        }
        if (acceleratorForeground == null || 
            acceleratorForeground instanceof UIResource) {
            acceleratorForeground = 
                UIManager.getColor(prefix + ".acceleratorForeground");
        }
        if (acceleratorSelectionForeground == null || 
            acceleratorSelectionForeground instanceof UIResource) {
            acceleratorSelectionForeground = 
                UIManager.getColor(prefix + ".acceleratorSelectionForeground");
        }
	// Get accelerator delimiter
	acceleratorDelimiter = 
	    UIManager.getString("MenuItem.acceleratorDelimiter");
	if (acceleratorDelimiter == null) { acceleratorDelimiter = "+"; }
        // Icons
        if (arrowIcon == null ||
            arrowIcon instanceof UIResource) {
            arrowIcon = UIManager.getIcon(prefix + ".arrowIcon");
        }
        if (checkIcon == null ||
            checkIcon instanceof UIResource) {
            checkIcon = UIManager.getIcon(prefix + ".checkIcon");
            MenuItemCheckIconFactory iconFactory = 
                (MenuItemCheckIconFactory) UIManager.get(prefix 
                    + ".checkIconFactory");
            if (iconFactory != null
                    && iconFactory.isCompatible(checkIcon, prefix)) {
                checkIcon = iconFactory.getIcon(menuItem);
            }
        }
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
        menuItem.addPropertyChangeListener(getHandler());
    }

    protected void installKeyboardActions() {
        installLazyActionMap();
	updateAcceleratorBinding();
    }

    void installLazyActionMap() {
        LazyActionMap.installLazyActionMap(menuItem, BasicMenuItemUI.class,
                                           getPropertyPrefix() + ".actionMap");
    }

    public void uninstallUI(JComponent c) {
	menuItem = (JMenuItem)c;
        uninstallDefaults();
        uninstallComponents(menuItem);
        uninstallListeners();
        uninstallKeyboardActions();

	
	//Remove the textWidth and accWidth values from the parent's Client Properties.
	Container parent = menuItem.getParent();
	if ( (parent != null && parent instanceof JComponent)  && 
	     !(menuItem instanceof JMenu && ((JMenu) menuItem).isTopLevelMenu())) {
	    JComponent p = (JComponent) parent;
	    p.putClientProperty(BasicMenuItemUI.MAX_ACC_WIDTH, null );
	    p.putClientProperty(BasicMenuItemUI.MAX_TEXT_WIDTH, null ); 
	}

	menuItem = null;
    }


    protected void uninstallDefaults() {
        LookAndFeel.uninstallBorder(menuItem);
        if (menuItem.getMargin() instanceof UIResource)
            menuItem.setMargin(null);
        if (arrowIcon instanceof UIResource)
            arrowIcon = null;
        if (checkIcon instanceof UIResource)
            checkIcon = null;
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
        menuItem.removePropertyChangeListener(getHandler());

        mouseInputListener = null;
        menuDragMouseListener = null;
        menuKeyListener = null;
        handler = null;
    }

    protected void uninstallKeyboardActions() {
	SwingUtilities.replaceUIActionMap(menuItem, null);
        SwingUtilities.replaceUIInputMap(menuItem, JComponent.
                                         WHEN_IN_FOCUSED_WINDOW, null);
    }

    protected MouseInputListener createMouseInputListener(JComponent c) {
        return getHandler();
    }

    protected MenuDragMouseListener createMenuDragMouseListener(JComponent c) {
        return getHandler();
    }

    protected MenuKeyListener createMenuKeyListener(JComponent c) {
	return null;
    }

    Handler getHandler() {
        if (handler == null) {
            handler = new Handler();
        }
        return handler;
    }

    InputMap createInputMap(int condition) {
	if (condition == JComponent.WHEN_IN_FOCUSED_WINDOW) {
	    return new ComponentInputMapUIResource(menuItem);
	}
	return null;
    }

    void updateAcceleratorBinding() {
	KeyStroke accelerator = menuItem.getAccelerator();
        InputMap windowInputMap = SwingUtilities.getUIInputMap(
                       menuItem, JComponent.WHEN_IN_FOCUSED_WINDOW);

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
    static Rectangle zeroRect = new Rectangle(0,0,0,0);
    static Rectangle iconRect = new Rectangle();
    static Rectangle textRect = new Rectangle();
    static Rectangle acceleratorRect = new Rectangle();
    static Rectangle checkIconRect = new Rectangle();
    static Rectangle arrowIconRect = new Rectangle();
    static Rectangle viewRect = new Rectangle(Short.MAX_VALUE, Short.MAX_VALUE);
    static Rectangle r = new Rectangle();

    private void resetRects() {
        iconRect.setBounds(zeroRect);
        textRect.setBounds(zeroRect);
        acceleratorRect.setBounds(zeroRect);
        checkIconRect.setBounds(zeroRect);
        arrowIconRect.setBounds(zeroRect);
        viewRect.setBounds(0,0,Short.MAX_VALUE, Short.MAX_VALUE);
        r.setBounds(zeroRect);
    }

    protected Dimension getPreferredMenuItemSize(JComponent c,
                                                     Icon checkIcon,
                                                     Icon arrowIcon,
                                                     int defaultTextIconGap) {
        JMenuItem b = (JMenuItem) c;
        
        Icon icon = null;
        /* 
         * in case .checkIconFactory is defined for this UI and the icon is 
         * compatible with it then the icon is handled by the checkIcon.
         */
        MenuItemCheckIconFactory iconFactory = 
            (MenuItemCheckIconFactory) UIManager.get(getPropertyPrefix() 
                + ".checkIconFactory");
        if (iconFactory == null
                || ! iconFactory.isCompatible(checkIcon, getPropertyPrefix())) {
           icon = b.getIcon();  
        }
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

        Font font = b.getFont();
        FontMetrics fm = b.getFontMetrics(font);
        FontMetrics fmAccel = b.getFontMetrics( acceleratorFont );

        resetRects();
        
        layoutMenuItem(
                  fm, text, fmAccel, acceleratorText, icon, checkIcon, arrowIcon,
                  b.getVerticalAlignment(), b.getHorizontalAlignment(),
                  b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
                  viewRect, iconRect, textRect, acceleratorRect, checkIconRect, arrowIconRect,
                  text == null ? 0 : defaultTextIconGap,
                  defaultTextIconGap
                  );
        // find the union of the icon and text rects
        r.setBounds(textRect);
        r = SwingUtilities.computeUnion(iconRect.x,
                                        iconRect.y,
                                        iconRect.width,
                                        iconRect.height,
                                        r);
        // Find the result height
        r.height = Math.max(
            Math.max(r.height, checkIconRect.height),
            Math.max(arrowIconRect.height, acceleratorRect.height));
        //   r = iconRect.union(textRect);

	
	// To make the accelerator texts appear in a column, find the widest MenuItem text
	// and the widest accelerator text.

	//Get the parent, which stores the information.
	Container parent = menuItem.getParent();
	
	//Check the parent, and see that it is not a top-level menu.
        if (parent != null && parent instanceof JComponent && 
	    !(menuItem instanceof JMenu && ((JMenu) menuItem).isTopLevelMenu())) {
	    JComponent p = (JComponent) parent;
	    
	    //Get widest text so far from parent, if no one exists null is returned.
	    Integer maxTextWidth = (Integer) p.getClientProperty(BasicMenuItemUI.MAX_TEXT_WIDTH);
	    Integer maxAccWidth = (Integer) p.getClientProperty(BasicMenuItemUI.MAX_ACC_WIDTH);
	    
	    int maxTextValue = maxTextWidth!=null ? maxTextWidth.intValue() : 0;
	    int maxAccValue = maxAccWidth!=null ? maxAccWidth.intValue() : 0;
	    
	    //Compare the text widths, and adjust the r.width to the widest.
	    if (r.width < maxTextValue) {
		r.width = maxTextValue;
	    } else {
		p.putClientProperty(BasicMenuItemUI.MAX_TEXT_WIDTH, new Integer(r.width) );
	    }
	    
	  //Compare the accelarator widths.
	    if (acceleratorRect.width > maxAccValue) {
		maxAccValue = acceleratorRect.width;
		p.putClientProperty(BasicMenuItemUI.MAX_ACC_WIDTH, new Integer(acceleratorRect.width) );
	    }
	    
	    //Add on the widest accelerator 
	    r.width += maxAccValue;
	    r.width += defaultTextIconGap;
	    
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
        if(r.height%2 == 0 
                && Boolean.TRUE != 
                    UIManager.get(getPropertyPrefix() + ".evenHeight")) {
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
	return r.getSize();
    }

    /**
     * We draw the background in paintMenuItem()
     * so override update (which fills the background of opaque
     * components by default) to just call paint().
     *
     */
    public void update(Graphics g, JComponent c) {
        paint(g, c);
    }

    public void paint(Graphics g, JComponent c) {
        paintMenuItem(g, c, checkIcon, arrowIcon,
                      selectionBackground, selectionForeground,
                      defaultTextIconGap);
    }


    protected void paintMenuItem(Graphics g, JComponent c,
                                     Icon checkIcon, Icon arrowIcon,
                                     Color background, Color foreground,
                                     int defaultTextIconGap) {
        JMenuItem b = (JMenuItem) c;
        ButtonModel model = b.getModel();

        //   Dimension size = b.getSize();
        int menuWidth = b.getWidth();
        int menuHeight = b.getHeight();
        Insets i = c.getInsets();
	
        resetRects();

        viewRect.setBounds( 0, 0, menuWidth, menuHeight );

        viewRect.x += i.left;
        viewRect.y += i.top;
        viewRect.width -= (i.right + viewRect.x);
        viewRect.height -= (i.bottom + viewRect.y);


        Font holdf = g.getFont();
        Font f = c.getFont();
        g.setFont( f );
        FontMetrics fm = SwingUtilities2.getFontMetrics(c, g, f);
        FontMetrics fmAccel = SwingUtilities2.getFontMetrics(
                                   c, g, acceleratorFont);

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
        Icon icon = null;
        /* 
         * in case .checkIconFactory is defined for this UI and the icon is 
         * compatible with it then the icon is handled by the checkIcon.
         */
        MenuItemCheckIconFactory iconFactory = 
            (MenuItemCheckIconFactory) UIManager.get(getPropertyPrefix() 
                + ".checkIconFactory");
        if (iconFactory == null
                || ! iconFactory.isCompatible(checkIcon, getPropertyPrefix())) {
           icon = b.getIcon();  
        }
        // layout the text and icon
        String text = layoutMenuItem(
            fm, b.getText(), fmAccel, acceleratorText, icon,
            checkIcon, arrowIcon,
            b.getVerticalAlignment(), b.getHorizontalAlignment(),
            b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
            viewRect, iconRect, textRect, acceleratorRect, 
            checkIconRect, arrowIconRect,
            b.getText() == null ? 0 : defaultTextIconGap,
            defaultTextIconGap
        ); 
        // Paint background
	paintBackground(g, b, background);

        Color holdc = g.getColor();

        // Paint the Check
        if (checkIcon != null) {
            if(model.isArmed() || (c instanceof JMenu && model.isSelected())) {
                g.setColor(foreground);
            } else {
                g.setColor(holdc);
            }
            if( useCheckAndArrow() )
		checkIcon.paintIcon(c, g, checkIconRect.x, checkIconRect.y);
            g.setColor(holdc);
        }

        // Paint the Icon
        if(icon != null ) { 
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
              
            if (icon!=null)   
                icon.paintIcon(c, g, iconRect.x, iconRect.y);
        }

        // Draw the Text
        if(text != null) {
 	    View v = (View) c.getClientProperty(BasicHTML.propertyKey);
 	    if (v != null) {
 		v.paint(g, textRect);
 	    } else {
		paintText(g, b, textRect, text);
	    }
	}
	
        // Draw the Accelerator Text
        if(acceleratorText != null && !acceleratorText.equals("")) {

	  //Get the maxAccWidth from the parent to calculate the offset.
	  int accOffset = 0;
	  Container parent = menuItem.getParent();
	  if (parent != null && parent instanceof JComponent) {
	    JComponent p = (JComponent) parent;
	    Integer maxValueInt = (Integer) p.getClientProperty(BasicMenuItemUI.MAX_ACC_WIDTH);
	    int maxValue = maxValueInt != null ?
                maxValueInt.intValue() : acceleratorRect.width;

	    //Calculate the offset, with which the accelerator texts will be drawn with.
	    accOffset = maxValue - acceleratorRect.width;
	  }
	  
	  g.setFont( acceleratorFont );
            if(!model.isEnabled()) {
                // *** paint the acceleratorText disabled
	      if ( disabledForeground != null )
		  {
                  g.setColor( disabledForeground );
                  SwingUtilities2.drawString(b, g,acceleratorText,
                                                acceleratorRect.x - accOffset, 
                                                acceleratorRect.y + fmAccel.getAscent());
                }
                else
                {
                  g.setColor(b.getBackground().brighter());
                  SwingUtilities2.drawString(b, g,acceleratorText,
                                                acceleratorRect.x - accOffset, 
						acceleratorRect.y + fmAccel.getAscent());
                  g.setColor(b.getBackground().darker());
                  SwingUtilities2.drawString(b, g,acceleratorText,
                                                acceleratorRect.x - accOffset - 1, 
						acceleratorRect.y + fmAccel.getAscent() - 1);
                }
            } else {
                // *** paint the acceleratorText normally
                if (model.isArmed()|| (c instanceof JMenu && model.isSelected())) {
                    g.setColor( acceleratorSelectionForeground );
                } else {
                    g.setColor( acceleratorForeground );
                }
                SwingUtilities2.drawString(b, g,acceleratorText,
                                              acceleratorRect.x - accOffset,
                                              acceleratorRect.y + fmAccel.getAscent());
            }
        }

        // Paint the Arrow
        if (arrowIcon != null) {
            if(model.isArmed() || (c instanceof JMenu &&model.isSelected()))
                g.setColor(foreground);
            if(useCheckAndArrow())
                arrowIcon.paintIcon(c, g, arrowIconRect.x, arrowIconRect.y);
        }
        g.setColor(holdc);
        g.setFont(holdf);
    }

    /**
     * Draws the background of the menu item.
     * 
     * @param g the paint graphics
     * @param menuItem menu item to be painted
     * @param bgColor selection background color
     * @since 1.4
     */
    protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
	ButtonModel model = menuItem.getModel();
        Color oldColor = g.getColor();
        int menuWidth = menuItem.getWidth();
        int menuHeight = menuItem.getHeight();

        if(menuItem.isOpaque()) {
            if (model.isArmed()|| (menuItem instanceof JMenu && model.isSelected())) {
                g.setColor(bgColor);
                g.fillRect(0,0, menuWidth, menuHeight);
            } else {
                g.setColor(menuItem.getBackground());
                g.fillRect(0,0, menuWidth, menuHeight);
            }
            g.setColor(oldColor);
        }
        else if (model.isArmed() || (menuItem instanceof JMenu &&
                                     model.isSelected())) {
            g.setColor(bgColor);
            g.fillRect(0,0, menuWidth, menuHeight);
            g.setColor(oldColor);
        }
    }

    /**
     * Renders the text of the current menu item.
     * <p>
     * @param g graphics context
     * @param menuItem menu item to render
     * @param textRect bounding rectangle for rendering the text
     * @param text string to render
     * @since 1.4
     */
    protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text) {
	ButtonModel model = menuItem.getModel();
	FontMetrics fm = SwingUtilities2.getFontMetrics(menuItem, g);
	int mnemIndex = menuItem.getDisplayedMnemonicIndex();

	if(!model.isEnabled()) {
	    // *** paint the text disabled
	    if ( UIManager.get("MenuItem.disabledForeground") instanceof Color ) {
		g.setColor( UIManager.getColor("MenuItem.disabledForeground") );
		SwingUtilities2.drawStringUnderlineCharAt(menuItem, g,text,
                          mnemIndex, textRect.x,  textRect.y + fm.getAscent());
	    } else {
		g.setColor(menuItem.getBackground().brighter());
		SwingUtilities2.drawStringUnderlineCharAt(menuItem, g, text,
                           mnemIndex, textRect.x, textRect.y + fm.getAscent());
		g.setColor(menuItem.getBackground().darker());
		SwingUtilities2.drawStringUnderlineCharAt(menuItem, g,text,
                           mnemIndex,  textRect.x - 1, textRect.y +
                           fm.getAscent() - 1);
	    }
	} else {
	    // *** paint the text normally
	    if (model.isArmed()|| (menuItem instanceof JMenu && model.isSelected())) {
		g.setColor(selectionForeground); // Uses protected field.
	    }
	    SwingUtilities2.drawStringUnderlineCharAt(menuItem, g,text,
                           mnemIndex, textRect.x, textRect.y + fm.getAscent());
	}
    }


    /** 
     * Compute and return the location of the icons origin, the 
     * location of origin of the text baseline, and a possibly clipped
     * version of the compound labels string.  Locations are computed
     * relative to the viewRect rectangle. 
     */

    private String layoutMenuItem(
        FontMetrics fm,
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

        SwingUtilities.layoutCompoundLabel(
                            menuItem, fm, text, icon, verticalAlignment, 
                            horizontalAlignment, verticalTextPosition, 
                            horizontalTextPosition, viewRect, iconRect, textRect, 
                            textIconGap);

        /* Initialize the acceelratorText bounds rectangle textRect.  If a null 
         * or and empty String was specified we substitute "" here 
         * and use 0,0,0,0 for acceleratorTextRect.
         */
        if( (acceleratorText == null) || acceleratorText.equals("") ) {
            acceleratorRect.width = acceleratorRect.height = 0;
            acceleratorText = "";
        }
        else {
            acceleratorRect.width = SwingUtilities2.stringWidth(
                                         menuItem, fmAccel, acceleratorText);
            acceleratorRect.height = fmAccel.getHeight();
        }

        /* Initialize the checkIcon bounds rectangle's width & height.
         */

	if( useCheckAndArrow()) {
	    if (checkIcon != null) {
		checkIconRect.width = checkIcon.getIconWidth();
		checkIconRect.height = checkIcon.getIconHeight();
	    } 
	    else {
		checkIconRect.width = checkIconRect.height = 0;
	    }
	    
	    /* Initialize the arrowIcon bounds rectangle width & height.
	     */
	    
	    if (arrowIcon != null) {
		arrowIconRect.width = arrowIcon.getIconWidth();
		arrowIconRect.height = arrowIcon.getIconHeight();
	    } else {
		arrowIconRect.width = arrowIconRect.height = 0;
	    }
        }

        Rectangle labelRect = iconRect.union(textRect);

        int checkIconOffset = menuItemGap;
        Object checkIconOffsetObject = 
            UIManager.get(getPropertyPrefix() + ".checkIconOffset");
        if (checkIconOffsetObject instanceof Integer) {
            checkIconOffset = (Integer) checkIconOffsetObject;
        }
        if( BasicGraphicsUtils.isLeftToRight(menuItem) ) {
            /* get minimum text offset. It is defined for LTR case only. */
            int minimumTextOffset = 0;
            Object minimumTextOffsetObject = 
                UIManager.get(getPropertyPrefix() 
                    + ".minimumTextOffset");
            if (minimumTextOffsetObject instanceof Integer) {
                minimumTextOffset = (Integer) minimumTextOffsetObject;
            }
            textRect.x += menuItemGap;
            iconRect.x += menuItemGap;

            // Position the Accelerator text rect
            acceleratorRect.x = viewRect.x + viewRect.width - arrowIconRect.width 
                             - menuItemGap - acceleratorRect.width;
            
            // Position the Check and Arrow Icons 
            if (useCheckAndArrow()) {
                checkIconRect.x = viewRect.x + checkIconOffset;
                textRect.x += checkIconOffset + checkIconRect.width;
                textRect.x = Math.max(textRect.x, minimumTextOffset); 
                iconRect.x += checkIconOffset + checkIconRect.width;
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
        // NOTE: This class exists only for backward compatability. All
        // its functionality has been moved into Handler. If you need to add
        // new functionality add it to the Handler, but make sure this
        // class calls into the Handler.

        public void mouseClicked(MouseEvent e) {
            getHandler().mouseClicked(e);
        }
        public void mousePressed(MouseEvent e) {
            getHandler().mousePressed(e);
        }
        public void mouseReleased(MouseEvent e) {
            getHandler().mouseReleased(e);
        }
        public void mouseEntered(MouseEvent e) {
            getHandler().mouseEntered(e);
        }
        public void mouseExited(MouseEvent e) {
            getHandler().mouseExited(e);
        }
        public void mouseDragged(MouseEvent e) {
            getHandler().mouseDragged(e);
        }
        public void mouseMoved(MouseEvent e) {
            getHandler().mouseMoved(e);
        }
    }


    private static class Actions extends UIAction {
        private static final String CLICK = "doClick";

        Actions(String key) {
            super(key);
        }

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
            BasicLookAndFeel.playSound(menuItem, getPropertyPrefix() +
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


    // BasicMenuUI subclasses this.
    class Handler implements MenuDragMouseListener, 
                          MouseInputListener, PropertyChangeListener {
        //
        // MouseInputListener
        //
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
		if (path.length > 1 && path[path.length-1] == menuItem) {
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

        //
        // MenuDragListener
        //
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


        //
        // PropertyChangeListener
        //
	public void propertyChange(PropertyChangeEvent e) {
	    String name = e.getPropertyName();

	    if (name == "labelFor" || name == "displayedMnemonic" ||
		name == "accelerator") {
		updateAcceleratorBinding();
	    } else if (name == "text" || "font" == name ||
                       "foreground" == name) {
		// remove the old html view client property if one
		// existed, and install a new one if the text installed
		// into the JLabel is html source.
		JMenuItem lbl = ((JMenuItem) e.getSource());
		String text = lbl.getText();
		BasicHTML.updateRenderer(lbl, text);
	    }
	}
    }
}
