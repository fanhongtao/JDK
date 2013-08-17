/*
 * @(#)BasicMenuItemUI.java	1.77 98/08/26
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

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.*;


/**
 * BasicMenuItem implementation
 *
 * @version 1.77 08/26/98
 * @author Georges Saab
 * @author David Karlton
 * @author Arnaud Weber
 */
public class BasicMenuItemUI extends MenuItemUI
{
    protected JMenuItem menuItem = null;
    protected Color selectionBackground;
    protected Color selectionForeground;    
    protected Color disabledForeground;
    protected Color acceleratorForeground;
    protected Color acceleratorSelectionForeground;
    
    protected int defaultTextIconGap;
    protected Font acceleratorFont;

    protected MouseInputListener mouseInputListener;
    protected MenuDragMouseListener menuDragMouseListener;
    protected MenuKeyListener menuKeyListener;
    
    protected Icon arrowIcon = null;
    protected Icon checkIcon = null;

    protected boolean oldBorderPainted;

    public static ComponentUI createUI(JComponent c) {
        return new BasicMenuItemUI();
    }

    public void installUI(JComponent c) {
        menuItem = (JMenuItem) c;

        installDefaults();
        installListeners();
        installKeyboardActions();

    }

    protected void installDefaults() {
        String prefix = getPropertyPrefix();

        acceleratorFont = UIManager.getFont("MenuItem.acceleratorFont");

        menuItem.setOpaque(true);
        if(menuItem.getMargin() == null || 
           (menuItem.getMargin() instanceof UIResource)) {
            menuItem.setMargin(UIManager.getInsets(prefix + ".margin"));
        }

        defaultTextIconGap = 4;   // Should be from table

        LookAndFeel.installBorder(menuItem, prefix + ".border");
        oldBorderPainted = menuItem.isBorderPainted();
        menuItem.setBorderPainted( ( (Boolean) (UIManager.get(prefix + ".borderPainted")) ).booleanValue() );
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
        // Icons
        if (arrowIcon == null ||
            arrowIcon instanceof UIResource) {
            arrowIcon = UIManager.getIcon(prefix + ".arrowIcon");
        }
        if (checkIcon == null ||
            checkIcon instanceof UIResource) {
            checkIcon = UIManager.getIcon(prefix + ".checkIcon");
        }
    }

    protected String getPropertyPrefix() {
        return "MenuItem";
    }

    protected void installListeners() {
        mouseInputListener = createMouseInputListener(menuItem);
        menuDragMouseListener = createMenuDragMouseListener(menuItem);
        menuKeyListener = createMenuKeyListener(menuItem);

        menuItem.addMouseListener(mouseInputListener);
        menuItem.addMouseMotionListener(mouseInputListener);
        menuItem.addMenuDragMouseListener(menuDragMouseListener);
        menuItem.addMenuKeyListener(menuKeyListener);
    }

    protected void installKeyboardActions() {}

    public void uninstallUI(JComponent c) {
        uninstallDefaults();
        uninstallListeners();
        uninstallKeyboardActions();

        menuItem = null;
    }


    protected void uninstallDefaults() {
        LookAndFeel.uninstallBorder(menuItem);
        menuItem.setBorderPainted( oldBorderPainted );
        if (arrowIcon instanceof UIResource)
            arrowIcon = null;
        if (checkIcon instanceof UIResource)
            checkIcon = null;
    }

    protected void uninstallListeners() {
        menuItem.removeMouseListener(mouseInputListener);
        menuItem.removeMouseMotionListener(mouseInputListener);
        menuItem.removeMenuDragMouseListener(menuDragMouseListener);
        menuItem.removeMenuKeyListener(menuKeyListener);

        mouseInputListener = null;
        menuDragMouseListener = null;
        menuKeyListener = null;
    }

    protected void uninstallKeyboardActions() {}

    protected MouseInputListener createMouseInputListener(JComponent c) {
        return new MouseInputHandler();
    }

    protected MenuDragMouseListener createMenuDragMouseListener(JComponent c) {
        return new MenuDragMouseHandler();
    }

    protected MenuKeyListener createMenuKeyListener(JComponent c) {
        return new MenuKeyHandler();
    }

    public Dimension getMinimumSize(JComponent c) {
        return null;
    }

    public Dimension getPreferredSize(JComponent c) {
        return getPreferredMenuItemSize(c,
                                        checkIcon, 
                                        arrowIcon, 
                                        defaultTextIconGap);
    }

    public Dimension getMaximumSize(JComponent c) {
        return null;
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
        Icon icon = (Icon) b.getIcon(); 
        String text = b.getText();
        KeyStroke accelerator =  b.getAccelerator();
        String acceleratorText = "";

        if (accelerator != null) {
            int modifiers = accelerator.getModifiers();
            if (modifiers > 0) {
                acceleratorText = KeyEvent.getKeyModifiersText(modifiers);
                acceleratorText += "+";
          }
            acceleratorText += KeyEvent.getKeyText(accelerator.getKeyCode());
        }

        Font font = b.getFont();
        FontMetrics fm = b.getToolkit().getFontMetrics(font);
        FontMetrics fmAccel = b.getToolkit().getFontMetrics( acceleratorFont );

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
        //   r = iconRect.union(textRect);

        // Add in the accelerator
        boolean acceleratorTextIsEmpty = (acceleratorText == null) || 
            acceleratorText.equals("");

        if (!acceleratorTextIsEmpty) {
            r.width += acceleratorRect.width;
            r.width += 7*defaultTextIconGap;
        }

        // Add in the checkIcon
        r.width += checkIconRect.width;
        r.width += 2*defaultTextIconGap;

        // Add in the arrowIcon
        r.width += 2*defaultTextIconGap;
        r.width += arrowIconRect.width;
        
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
        FontMetrics fm = g.getFontMetrics( f );
        FontMetrics fmAccel = g.getFontMetrics( acceleratorFont );

        // Paint background
        Color holdc = g.getColor();
        if(c.isOpaque()) {
            if (model.isArmed()|| (c instanceof JMenu && model.isSelected())) {
                g.setColor(background);
                g.fillRect(0,0, menuWidth, menuHeight);
            } else {
                g.setColor(b.getBackground());
                g.fillRect(0,0, menuWidth, menuHeight);
            }
            g.setColor(holdc);
        }

        // get Accelerator text
        KeyStroke accelerator =  b.getAccelerator();
        String acceleratorText = "";
        if (accelerator != null) {
            int modifiers = accelerator.getModifiers();
            if (modifiers > 0) {
                acceleratorText = KeyEvent.getKeyModifiersText(modifiers);
                acceleratorText += "+";
          }
            acceleratorText += KeyEvent.getKeyText(accelerator.getKeyCode());
        }
        
        // layout the text and icon
        String text = layoutMenuItem(
            fm, b.getText(), fmAccel, acceleratorText, b.getIcon(),
            checkIcon, arrowIcon,
            b.getVerticalAlignment(), b.getHorizontalAlignment(),
            b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
            viewRect, iconRect, textRect, acceleratorRect, 
            checkIconRect, arrowIconRect,
            b.getText() == null ? 0 : defaultTextIconGap,
            defaultTextIconGap
        );
          
        // Paint the Check
        if (checkIcon != null) {
            if(model.isArmed() || (c instanceof JMenu && model.isSelected())) {
                g.setColor(foreground);
            } else {
                g.setColor(b.getForeground());
            }
            checkIcon.paintIcon(c, g, checkIconRect.x, checkIconRect.y);
            g.setColor(holdc);
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
              
            if (icon!=null)   
                icon.paintIcon(c, g, iconRect.x, iconRect.y);
        }

        // Draw the Text
        if(text != null && !text.equals("")) {
            if(!model.isEnabled()) {
                // *** paint the text disabled
                if ( UIManager.get("MenuItem.disabledForeground") instanceof Color )
                {
                  g.setColor( UIManager.getColor("MenuItem.disabledForeground") );
                  BasicGraphicsUtils.drawString(g,text,model.getMnemonic(),
                                                textRect.x, textRect.y + fm.getAscent());
                }
                else
                {
                  g.setColor(b.getBackground().brighter());
                  BasicGraphicsUtils.drawString(g,text,model.getMnemonic(),
                                                textRect.x, textRect.y + fm.getAscent());
                  g.setColor(b.getBackground().darker());
                  BasicGraphicsUtils.drawString(g,text,model.getMnemonic(),
                                                textRect.x - 1, textRect.y + fm.getAscent() - 1);
                }
            } else {
                // *** paint the text normally
                if (model.isArmed()|| (c instanceof JMenu && model.isSelected())) {
                    g.setColor(foreground);
                } else {
                    g.setColor(b.getForeground());
                }
                BasicGraphicsUtils.drawString(g,text, 
                                              model.getMnemonic(),
                                              textRect.x,
                                              textRect.y + fm.getAscent());
            }
        }
          
        // Draw the Accelerator Text
        if(acceleratorText != null && !acceleratorText.equals("")) {
            g.setFont( acceleratorFont );
            if(!model.isEnabled()) {
                // *** paint the acceleratorText disabled
                if ( disabledForeground != null )
                {
                  g.setColor( disabledForeground );
                  BasicGraphicsUtils.drawString(g,acceleratorText,0,
                                                acceleratorRect.x, 
                                                acceleratorRect.y + fm.getAscent());
                }
                else
                {
                  g.setColor(b.getBackground().brighter());
                  BasicGraphicsUtils.drawString(g,acceleratorText,0,
                                                acceleratorRect.x, acceleratorRect.y + fm.getAscent());
                  g.setColor(b.getBackground().darker());
                  BasicGraphicsUtils.drawString(g,acceleratorText,0,
                                                acceleratorRect.x - 1, acceleratorRect.y + fm.getAscent() - 1);
                }
            } else {
                // *** paint the acceleratorText normally
                if (model.isArmed()|| (c instanceof JMenu && model.isSelected())) {
                    g.setColor( acceleratorSelectionForeground );
                } else {
                    g.setColor( acceleratorForeground );
                }
                BasicGraphicsUtils.drawString(g,acceleratorText, 0,
                                              acceleratorRect.x,
                                              acceleratorRect.y + fm.getAscent());
            }
        }

        // Paint the Arrow
        if (arrowIcon != null) {
            if(model.isArmed() || (c instanceof JMenu &&model.isSelected()))
                g.setColor(foreground);
            if( !(b.getParent() instanceof JMenuBar) )
                arrowIcon.paintIcon(c, g, arrowIconRect.x, arrowIconRect.y);
        }
        g.setColor(holdc);
        g.setFont(holdf);
    }


    /** 
     * Compute and return the location of the icons origin, the 
     * location of origin of the text baseline, and a possibly clipped
     * version of the compound labels string.  Locations are computed
     * relative to the viewR rectangle. 
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
        Rectangle viewR, 
        Rectangle iconR, 
        Rectangle textR,
        Rectangle acceleratorR,
        Rectangle checkIconR, 
        Rectangle arrowIconR, 
        int textIconGap,
        int menuItemGap
        )
    {

        SwingUtilities.layoutCompoundLabel(
                            menuItem, fm, text, icon, verticalAlignment, 
                            horizontalAlignment, verticalTextPosition, 
                            horizontalTextPosition, viewR, iconR, textR, 
                            textIconGap);

        /* Initialize the acceelratorText bounds rectangle textR.  If a null 
         * or and empty String was specified we substitute "" here 
         * and use 0,0,0,0 for acceleratorTextR.
         */

        boolean acceleratorTextIsEmpty = (acceleratorText == null) || 
            acceleratorText.equals("");

        if (acceleratorTextIsEmpty) {
            acceleratorR.width = acceleratorR.height = 0;
            acceleratorText = "";
        }
        else {
            acceleratorR.width = SwingUtilities.computeStringWidth( fmAccel, acceleratorText );
            acceleratorR.height = fmAccel.getHeight();
        }

        /* Initialize the checkIcon bounds rectangle checkIconR.
         */

        if (checkIcon != null) {
            checkIconR.width = checkIcon.getIconWidth();
            checkIconR.height = checkIcon.getIconHeight();
        } 
        else {
            checkIconR.width = checkIconR.height = 0;
        }

        /* Initialize the arrowIcon bounds rectangle arrowIconR.
         */

        if (arrowIcon != null) {
            arrowIconR.width = arrowIcon.getIconWidth();
            arrowIconR.height = arrowIcon.getIconHeight();
        } 
        else {
            arrowIconR.width = arrowIconR.height = 0;
        }
        
        textR.x += checkIconR.width + menuItemGap;
        iconR.x += checkIconR.width + menuItemGap;

        Rectangle labelR = iconR.union(textR);

        // Position the Accelerator text rect
        acceleratorR.x += (viewR.width - arrowIconR.width - 
                           menuItemGap - acceleratorR.width);
        acceleratorR.y = viewR.y + (viewR.height/2) - (acceleratorR.height/2);

        arrowIconR.x += (viewR.width - arrowIconR.width);
        arrowIconR.y = viewR.y + (labelR.height/2) - (arrowIconR.height/2);

        checkIconR.y = viewR.y + (labelR.height/2) - (checkIconR.height/2);
        checkIconR.x += viewR.x;

        /*
          System.out.println("Layout: v=" +viewR+"  c="+checkIconR+" i="+
          iconR+" t="+textR+" acc="+acceleratorR+" a="+arrowIconR);
          */
        return text;
    }

    
    /*    public void processKeyEvent(JMenuItem item,KeyEvent e,MenuElement path[],MenuSelectionManager manager) {
        int key = item.getMnemonic();
        if(key == 0)
            return;
        if(e.getID() == KeyEvent.KEY_PRESSED) {
            if(lower(key) == lower((int)(e.getKeyChar()))) {
                manager.clearSelectedPath();
                item.doClick(0);
                e.consume();
            }
        }
    }
*/
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
                manager.clearSelectedPath();
                menuItem.doClick(0);
            } else {
                manager.processMouseEvent(e);
            }
        }
        public void mouseEntered(MouseEvent e) {
            // System.out.println("menu item entered: " + menuItem.getText());
            MenuSelectionManager manager = MenuSelectionManager.defaultManager();
            manager.setSelectedPath(getPath());
        }
        public void mouseExited(MouseEvent e) {
            MenuSelectionManager manager = MenuSelectionManager.defaultManager();
            MenuElement path[] = manager.getSelectedPath();
            if (path.length > 1) {
                MenuElement newPath[] = new MenuElement[path.length-1];
                int i,c;
                for(i=0,c=path.length-1;i<c;i++)
                    newPath[i] = path[i];
                manager.setSelectedPath(newPath);
            }
        }
        public void mouseDragged(MouseEvent e) {
            MenuSelectionManager.defaultManager().processMouseEvent(e);
        }
        public void mouseMoved(MouseEvent e) {
        }
    }


    private class MenuDragMouseHandler implements MenuDragMouseListener {
        public void menuDragMouseEntered(MenuDragMouseEvent e) {}
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
                manager.clearSelectedPath();
                menuItem.doClick(0);
            } else {
                manager.clearSelectedPath();
            }
        }
    }

    private class MenuKeyHandler implements MenuKeyListener {
        public void menuKeyTyped(MenuKeyEvent e) {
            int key = menuItem.getMnemonic();
            if(key == 0)
                return;
            if(lower(key) == lower((int)(e.getKeyChar()))) {
                MenuSelectionManager manager = 
                    e.getMenuSelectionManager();
                manager.clearSelectedPath();
                menuItem.doClick(0);
                e.consume();
            }
        }
        public void menuKeyPressed(MenuKeyEvent e) {}
        public void menuKeyReleased(MenuKeyEvent e) {}

        private int lower(int ascii) {
            if(ascii >= 'A' && ascii <= 'Z')
                return ascii + 'a' - 'A';
            else
                return ascii;
        }

    }

}
