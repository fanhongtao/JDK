/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.metal;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.InternalFrameEvent;
import java.util.EventListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;


/**
 * Class that manages a JLF title bar
 * @version 1.40 02/06/02
 * @author Steve Wilson
 * @author Brian Beck
 * @since 1.3
 */

public class MetalInternalFrameTitlePane  extends BasicInternalFrameTitlePane {

    protected boolean isPalette = false;  	
    protected Icon paletteCloseIcon;
    protected int paletteTitleHeight;

    private static final Border handyEmptyBorder = new EmptyBorder(0,0,0,0);

    int buttonsWidth = 0;	
    
    MetalBumps activeBumps 
        = new MetalBumps( 0, 0,
                          MetalLookAndFeel.getPrimaryControlHighlight(),
                          MetalLookAndFeel.getPrimaryControlDarkShadow(),
                          MetalLookAndFeel.getPrimaryControl() );
    MetalBumps inactiveBumps 
        = new MetalBumps( 0, 0,
                          MetalLookAndFeel.getControlHighlight(),
                          MetalLookAndFeel.getControlDarkShadow(),
                          MetalLookAndFeel.getControl() );
    MetalBumps paletteBumps;

					    
    public MetalInternalFrameTitlePane(JInternalFrame f) {
        super( f );
    }

    protected void installDefaults() {
        super.installDefaults();
        setFont( UIManager.getFont("InternalFrame.font") );
        paletteTitleHeight
            = UIManager.getInt("InternalFrame.paletteTitleHeight");
        paletteCloseIcon = UIManager.getIcon("InternalFrame.paletteCloseIcon");
    }
    
    protected void createButtons() {
        super.createButtons();

        Boolean paintActive = frame.isSelected() ? Boolean.TRUE:Boolean.FALSE;
        iconButton.putClientProperty("paintActive", paintActive);
        iconButton.setBorder(handyEmptyBorder);
        iconButton.getAccessibleContext().setAccessibleName(
            UIManager.getString(
                "InternalFrameTitlePane.iconifyButtonAccessibleName"));
    
        maxButton.putClientProperty("paintActive", paintActive);
        maxButton.setBorder(handyEmptyBorder);
        maxButton.getAccessibleContext().setAccessibleName(
            UIManager.getString(
                "InternalFrameTitlePane.maximizeButtonAccessibleName"));
        
        closeButton.putClientProperty("paintActive", paintActive);
        closeButton.setBorder(handyEmptyBorder);
        closeButton.getAccessibleContext().setAccessibleName(
            UIManager.getString(
                "InternalFrameTitlePane.closeButtonAccessibleName"));

        // The palette close icon isn't opaque while the regular close icon is.
        // This makes sure palette close buttons have the right background.
        closeButton.setBackground(MetalLookAndFeel.getPrimaryControlShadow());
    }

    /**
     * Override the parent's method to do nothing. Metal frames do not 
     * have system menus.
     */
    protected void assembleSystemMenu() {}

    /**
     * Override the parent's method to do nothing. Metal frames do not 
     * have system menus.
     */
    protected void addSystemMenuItems(JMenu systemMenu) {}

    /**
     * Override the parent's method avoid creating a menu bar. Metal frames
     * do not have system menus.
     */
    protected void addSubComponents() {
        add(iconButton);
        add(maxButton);
        add(closeButton);
    }

    protected PropertyChangeListener createPropertyChangeListener() {
        return new MetalPropertyChangeHandler();
    }
    
    protected LayoutManager createLayout() {
        return new MetalTitlePaneLayout();
    }

    /*
     * These accessors allow our inner classes to get at member variables
     * they couldn't otherwise access.  This work's around a javac bug.
     */
    private JInternalFrame getFrame() { return frame; }
    private JButton getIconButton() { return iconButton; }
    private JButton getCloseButton() { return closeButton; }
    private JButton getMaxButton() { return maxButton; }

    class MetalPropertyChangeHandler
        extends BasicInternalFrameTitlePane.PropertyChangeHandler
    {
        public void propertyChange(PropertyChangeEvent evt) {
	    String prop = (String)evt.getPropertyName();
            if( prop.equals(JInternalFrame.IS_SELECTED_PROPERTY) ) {
                Boolean b = (Boolean)evt.getNewValue();
                getIconButton().putClientProperty("paintActive", b);
                getCloseButton().putClientProperty("paintActive", b);
                getMaxButton().putClientProperty("paintActive", b);
                repaint();
            }
            super.propertyChange(evt);
        }
    }

    class MetalTitlePaneLayout implements LayoutManager {    
        public void addLayoutComponent(String name, Component c) {}
        public void removeLayoutComponent(Component c) {}   
        public Dimension preferredLayoutSize(Container c)  {
            return getPreferredSize(c);
        }

        public Dimension getPreferredSize(Container c)  {
            return new Dimension(c.getSize().width, computeHeight());
        }
        
        public Dimension minimumLayoutSize(Container c) {
            return preferredLayoutSize(c);
        } 
    
        protected int computeHeight() {      
            if ( isPalette ) {
                return paletteTitleHeight;
            }
	  
            FontMetrics fm 
                = Toolkit.getDefaultToolkit().getFontMetrics(getFont());
            int fontHeight = fm.getHeight();
            fontHeight += 7;
            Icon icon = getFrame().getFrameIcon();
            int iconHeight = 0;
            if (icon != null) iconHeight = icon.getIconHeight();
            iconHeight += 5;
      
            int finalHeight = Math.max( fontHeight, iconHeight );

            return finalHeight;
        }	
				    
        public void layoutContainer(Container c) {
            JInternalFrame frame = getFrame();
            boolean leftToRight = MetalUtils.isLeftToRight(frame);
       
            int w = getWidth();
            int x = leftToRight ? w : 0;
            int y = 2;
            int spacing;
      
            // assumes all buttons have the same dimensions
            // these dimensions include the borders
            JButton closeButton = getCloseButton();
            int buttonHeight = closeButton.getIcon().getIconHeight(); 
            int buttonWidth = closeButton.getIcon().getIconWidth();

            if(frame.isClosable()) {
                if (isPalette) {
                    spacing = 3;
                    x += leftToRight ? -spacing -(buttonWidth+2) : spacing;
                    closeButton.setBounds(x, y, buttonWidth+2, getHeight()-4);
                    if( !leftToRight ) x += (buttonWidth+2);
                } else {
                    spacing = 4;
                    x += leftToRight ? -spacing -buttonWidth : spacing;
                    closeButton.setBounds(x, y, buttonWidth, buttonHeight);
                    if( !leftToRight ) x += buttonWidth;
                }
            }

            if(frame.isMaximizable() && !isPalette ) {
                JButton maxButton = getMaxButton();
                spacing = frame.isClosable() ? 10 : 4;
                x += leftToRight ? -spacing -buttonWidth : spacing;
                maxButton.setBounds(x, y, buttonWidth, buttonHeight);
                if( !leftToRight ) x += buttonWidth;
            } 
        
            if(frame.isIconifiable() && !isPalette ) {
                JButton iconButton = getIconButton();
                spacing = frame.isMaximizable() ? 2
                          : (frame.isClosable() ? 10 : 4);
                x += leftToRight ? -spacing -buttonWidth : spacing;
                iconButton.setBounds(x, y, buttonWidth, buttonHeight);      
                if( !leftToRight ) x += buttonWidth;
            }
        
            buttonsWidth = leftToRight ? w - x : x;
        } 
    }

    public void paintPalette(Graphics g)  {
        boolean leftToRight = MetalUtils.isLeftToRight(frame);

        int width = getWidth();
        int height = getHeight();
    
        if (paletteBumps == null) {
            paletteBumps 
                = new MetalBumps(0, 0,
                                 MetalLookAndFeel.getPrimaryControlHighlight(),
                                 MetalLookAndFeel.getPrimaryControlInfo(),
                                 MetalLookAndFeel.getPrimaryControlShadow() );
        }

        Color background = MetalLookAndFeel.getPrimaryControlShadow();     
        Color darkShadow = MetalLookAndFeel.getPrimaryControlDarkShadow();

        g.setColor(background);
        g.fillRect(0, 0, width, height);

        g.setColor( darkShadow );
        g.drawLine ( 0, height - 1, width, height -1);

        int xOffset = leftToRight ? 4 : buttonsWidth + 4;
        int bumpLength = width - buttonsWidth -2*4;
        int bumpHeight = getHeight()  - 4;
        paletteBumps.setBumpArea( bumpLength, bumpHeight );
        paletteBumps.paintIcon( this, g, xOffset, 2);
    }

    public void paintComponent(Graphics g)  {
        if(isPalette) {
            paintPalette(g);
            return;
        }

        boolean leftToRight = MetalUtils.isLeftToRight(frame);
        boolean isSelected = frame.isSelected();

        int width = getWidth();
        int height = getHeight();
    
        Color background;
        Color foreground;
        Color darkShadow;

        MetalBumps bumps;

        if (isSelected) {
            background = MetalLookAndFeel.getWindowTitleBackground();
            foreground = MetalLookAndFeel.getWindowTitleForeground();
            darkShadow = MetalLookAndFeel.getPrimaryControlDarkShadow();
            bumps = activeBumps;
        } else {
            background = MetalLookAndFeel.getWindowTitleInactiveBackground();
            foreground = MetalLookAndFeel.getWindowTitleInactiveForeground();
            darkShadow = MetalLookAndFeel.getControlDarkShadow();
            bumps = inactiveBumps;
        }

        g.setColor(background);
        g.fillRect(0, 0, width, height);

        g.setColor( darkShadow );
        g.drawLine ( 0, height - 1, width, height -1);
        g.drawLine ( 0, 0, 0 ,0);    
        g.drawLine ( width - 1, 0 , width -1, 0);


        int titleLength = 0;
        int xOffset = leftToRight ? 5 : width - 5;
        String frameTitle = frame.getTitle();

        Icon icon = frame.getFrameIcon();
        if ( icon != null ) {
            if( !leftToRight ) 
                xOffset -= icon.getIconWidth();
            int iconY = ((height / 2) - (icon.getIconHeight() /2));
            icon.paintIcon(frame, g, xOffset, iconY);
            xOffset += leftToRight ? icon.getIconWidth() + 5 : -5;
        }

        if(frameTitle != null) {
            Font f = getFont();
            g.setFont(f);
            FontMetrics fm = g.getFontMetrics();
            int fHeight = fm.getHeight();
            titleLength = fm.stringWidth(frameTitle);

            g.setColor(foreground);

            int yOffset = ( (height - fm.getHeight() ) / 2 ) + fm.getAscent();
            if( !leftToRight )
                xOffset -= titleLength;
            g.drawString( frameTitle, xOffset, yOffset );
            xOffset += leftToRight ? titleLength + 5  : -5;
        }
  
        int bumpXOffset;
        int bumpLength;
        if( leftToRight ) {
            bumpLength = width - buttonsWidth - xOffset -5;
            bumpXOffset = xOffset;
        } else {
            bumpLength = xOffset - buttonsWidth - 5;
            bumpXOffset = buttonsWidth + 5;
        }
        int bumpYOffset = 3;
        int bumpHeight = getHeight() - (2 * bumpYOffset);        
        bumps.setBumpArea( bumpLength, bumpHeight );
        bumps.paintIcon(this, g, bumpXOffset, bumpYOffset);
    }
					     				    
    public void setPalette(boolean b) {
        isPalette = b;

	if (isPalette) {
            closeButton.setIcon(paletteCloseIcon);
         if( frame.isMaximizable() )
                remove(maxButton);
            if( frame.isIconifiable() )
                remove(iconButton);
        } else {
 	    closeButton.setIcon(closeIcon);
            if( frame.isMaximizable() )
                add(maxButton);
            if( frame.isIconifiable() )
                add(iconButton);
	}		
	revalidate();
	repaint();
    }		     
}  
