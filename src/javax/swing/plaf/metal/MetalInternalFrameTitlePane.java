/*
 * @(#)MetalInternalFrameTitlePane.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
import java.beans.VetoableChangeListener;
import java.beans.PropertyVetoException;



/**
 * Package private class that manages a JLF title bar
 * @version 1.27 08/26/98
 * @author Steve Wilson
 */
// Could not extend BasicInternalFrameTitlePane because it's private
class MetalInternalFrameTitlePane extends JComponent 
                                implements LayoutManager, 
					   ActionListener, 
					   PropertyChangeListener {

  protected JMenuBar menuBar;
  protected boolean isPalette = false;					    
  JInternalFrame frame;
  // the three control buttons
  protected JButton iconButton;
  protected JButton maxButton;
  protected JButton closeButton;

  private static final Border handyEmptyBorder = new EmptyBorder(0,0,0,0);

  // the pictures/icons in these three buttons
  Icon maxIcon; 
  Icon altMaxIcon; 
  Icon iconIcon; 
  Icon closeIcon;

  int riseWidth = 0;	
  int interButtonSpacing = 2;
    
  MetalBumps activeBumps = new MetalBumps( 0, 0,
					   MetalLookAndFeel.getPrimaryControlHighlight(),
					   MetalLookAndFeel.getPrimaryControlDarkShadow(),
					   MetalLookAndFeel.getPrimaryControl() );
  MetalBumps inactiveBumps = new MetalBumps( 0, 0,
					     MetalLookAndFeel.getControlHighlight(),
					     MetalLookAndFeel.getControlDarkShadow(),
					     MetalLookAndFeel.getControl() );

  MetalBumps paletteBumps;
					    
  public MetalInternalFrameTitlePane(JInternalFrame f) {
    frame = f;

    setFont( UIManager.getFont("InternalFrame.font") );
    maxIcon = (Icon)UIManager.get("InternalFrame.maximizeIcon");
    altMaxIcon = (Icon)UIManager.get("InternalFrame.minimizeIcon");
    iconIcon = (Icon)UIManager.get("InternalFrame.iconizeIcon");
    closeIcon = (Icon)UIManager.get("InternalFrame.closeIcon");

/*    menuBar = new JMenuBar(){
      public boolean isFocusTraversable() { return false; }
      public void requestFocus() {}
      // PENDING(klobad) Should be able to configure Menu + Button instead
      public void paint(Graphics g) {}
      public boolean isOpaque() { return false; }
    };
    
    menuBar.setBorderPainted(false);*/
    
    iconButton = new NoFocusButton( iconIcon );
    iconButton.putClientProperty("paintActive", Boolean.TRUE);
    iconButton.setFocusPainted(false);
    iconButton.setBorder(handyEmptyBorder);
    iconButton.setOpaque(false);
    iconButton.addActionListener(this);
    iconButton.setActionCommand("Iconify");
    iconButton.getAccessibleContext().setAccessibleName("Iconify");
    

    maxButton = new NoFocusButton( maxIcon );
    maxButton.putClientProperty("paintActive", Boolean.TRUE);
    maxButton.setBorder(handyEmptyBorder);
    maxButton.setOpaque(false);
    maxButton.setFocusPainted(false);
    maxButton.addActionListener(this);
    maxButton.setActionCommand("Maximize");
    maxButton.getAccessibleContext().setAccessibleName("Maximize");
    

    closeButton = new NoFocusButton( closeIcon );
    closeButton.putClientProperty("paintActive", Boolean.TRUE);
    closeButton.setBorder(handyEmptyBorder);
    closeButton.setOpaque(false);
    closeButton.setFocusPainted(false);     
    closeButton.addActionListener(this);
    closeButton.setActionCommand("Close");
    closeButton.getAccessibleContext().setAccessibleName("Close");
    
    setLayout(this);
    
    //    add(menuBar);
    add(iconButton);
    add(maxButton);
    add(closeButton);             
    
    // Make sure these are ok to leave on?
    frame.addPropertyChangeListener(this);

  }
					     

  public void paintPalette(Graphics g)  {


    int width = getWidth();
    int height = getHeight();
    
    Color background;
    Color foreground;
    Color shadow;
    Color darkShadow;
    Color highlight;
    Color black;

    if (paletteBumps == null) {

      paletteBumps = new MetalBumps( 0, 0,
					     MetalLookAndFeel.getPrimaryControlHighlight(),
					     MetalLookAndFeel.getPrimaryControlInfo(),
					     MetalLookAndFeel.getPrimaryControlShadow() );
    }



    background = MetalLookAndFeel.getPrimaryControlShadow();
     
    darkShadow = MetalLookAndFeel.getPrimaryControlDarkShadow();


    g.setColor(background);
    g.fillRect(0, 0, width, height);

    g.setColor( darkShadow );
    g.drawLine ( 0, height - 1, width, height -1);


    int xOffset = 5;
    int bumpLength = getWidth() - (xOffset + riseWidth  + 2 );
    int bumpHeight = getHeight()  - 4;
    paletteBumps.setBumpArea( bumpLength, bumpHeight );
    paletteBumps.paintIcon( this, g, xOffset, 2);


    paintChildren(g); 
  }

  public void paint(Graphics g)  {
    if(isPalette) {
        paintPalette(g);
	return;
    }

    boolean isSelected = frame.isSelected();

    int width = getWidth();
    int height = getHeight();
    
    Color background;
    Color foreground;
    Color shadow;
    Color darkShadow;
    Color highlight;
    Color black;

    MetalBumps bumps;



    if (isSelected) {
        background = MetalLookAndFeel.getWindowTitleBackground();
        foreground = MetalLookAndFeel.getWindowTitleForeground();
	shadow = MetalLookAndFeel.getPrimaryControlShadow();
	darkShadow = MetalLookAndFeel.getPrimaryControlDarkShadow();
	highlight = MetalLookAndFeel.getPrimaryControlHighlight();
	black = MetalLookAndFeel.getPrimaryControlInfo();
	bumps = activeBumps;
    } else {
        background = MetalLookAndFeel.getWindowTitleInactiveBackground();
        foreground = MetalLookAndFeel.getWindowTitleInactiveForeground();
       	shadow = MetalLookAndFeel.getControlShadow();
	darkShadow = MetalLookAndFeel.getControlDarkShadow();
	highlight = MetalLookAndFeel.getControlHighlight();
	black = MetalLookAndFeel.getControlInfo();
	bumps = inactiveBumps;
    }

    Color fillColor = darkShadow;


    g.setColor(background);
    g.fillRect(0, 0, width, height);

    g.setColor( darkShadow );
    g.drawLine ( 0, height - 1, width, height -1);
    g.drawLine ( 0, 0, 0 ,0);    
    g.drawLine ( width - 1, 0 , width -1, 0);


    int titleLength = 0;
    int xOffset = 5;
    String frameTitle = frame.getTitle();

      Icon icon = frame.getFrameIcon();
      if ( icon != null ) {
	int iconY = ((height / 2) - (icon.getIconHeight() /2));
	icon.paintIcon(frame, g, xOffset, iconY);
        xOffset += icon.getIconWidth()+2;
      }


    if(frameTitle != null) {
      Font f = getFont();
      g.setFont(f);
      FontMetrics fm = g.getFontMetrics();
      int fHeight = fm.getHeight();
      titleLength = fm.stringWidth(frame.getTitle());

      g.setColor(foreground);

      int yOffset = ( (height - fm.getHeight() ) / 2 ) + fm.getAscent();
      g.drawString(frame.getTitle(),
		   xOffset,
		   yOffset );

    }
  

    int bumpYOffset = 3;
    xOffset += titleLength + interButtonSpacing;
    int bumpLength = getWidth() - (xOffset + riseWidth + interButtonSpacing + 5 );
    int bumpHeight = getHeight() - (2 * bumpYOffset);
    bumps.setBumpArea( bumpLength, bumpHeight );
    bumps.paintIcon( this, g, xOffset, bumpYOffset);


    paintChildren(g); 
  }
					     

    public void actionPerformed(ActionEvent e) {

      if("Close".equals(e.getActionCommand()) && frame.isClosable()){
	try {
	  frame.setClosed(true);
        } catch (PropertyVetoException e0) { }
      }
      else if("Iconify".equals(e.getActionCommand()) && frame.isIconifiable()) {
	if(!frame.isIcon())
	  try { frame.setIcon(true); } catch (PropertyVetoException e1) { }
	else
	  try { frame.setIcon(false); } catch (PropertyVetoException e1) { }

	  ButtonModel model = iconButton.getModel();
	  if ( model != null ) {
	      model.setRollover( false );
	  }
      } else if("Minimize".equals(e.getActionCommand()) && frame.isMaximizable()) {
	try { frame.setIcon(true); } catch (PropertyVetoException e2) { }
      } else if("Maximize".equals(e.getActionCommand()) && frame.isMaximizable()) {
	if(!frame.isMaximum()) {
	  try { frame.setMaximum(true); } catch (PropertyVetoException e5) { }
	} else {
	  try { frame.setMaximum(false); } catch (PropertyVetoException e6) { }
	}
      } else if("Restore".equals(e.getActionCommand()) && 
                frame.isMaximizable() && frame.isMaximum()) {
	try { frame.setMaximum(false); } catch (PropertyVetoException e4) { }
      } else if("Restore".equals(e.getActionCommand()) && 
                frame.isIconifiable() && frame.isIcon()) {
	try { frame.setIcon(false); } catch (PropertyVetoException e4) { }
      }
    }
					     
    public void propertyChange(PropertyChangeEvent evt) {
      String prop = (String)evt.getPropertyName();
      JInternalFrame f = (JInternalFrame)evt.getSource();
      boolean value = false;

      if ( frame.isSelected() ) {
	  iconButton.putClientProperty("paintActive", Boolean.TRUE);
	  closeButton.putClientProperty("paintActive", Boolean.TRUE);
	  maxButton.putClientProperty("paintActive", Boolean.TRUE);
	  repaint();
      } else {
	  iconButton.putClientProperty("paintActive", Boolean.FALSE);
	  closeButton.putClientProperty("paintActive", Boolean.FALSE);
	  maxButton.putClientProperty("paintActive", Boolean.FALSE);
	  repaint();
      }

      if(JInternalFrame.IS_SELECTED_PROPERTY.equals(prop)) {
	  repaint();
      } else if(JInternalFrame.IS_MAXIMUM_PROPERTY.equals(prop)) {
	value = ((Boolean)evt.getNewValue()).booleanValue();
	if(value)
       	  maxButton.setIcon(altMaxIcon);
	else
	  maxButton.setIcon(maxIcon);
      } else if(JInternalFrame.IS_ICON_PROPERTY.equals(prop)) {
	value = ((Boolean)evt.getNewValue()).booleanValue();
	if(value)
	  iconButton.setIcon(iconIcon);
	else
	  iconButton.setIcon(iconIcon);  
      } 
    }
    
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
      
      if ( isPalette )
	  return UIManager.getInt("InternalFrame.paletteTitleHeight");
	  
      FontMetrics fm =  Toolkit.getDefaultToolkit().getFontMetrics(getFont());
      int fontHeight = fm.getHeight();
      fontHeight += 7;
      int iconHeight = frame.getFrameIcon().getIconHeight();
      iconHeight += 5;
      
      int finalHeight = Math.max( fontHeight, iconHeight );

      return finalHeight;
    }	
				    
    public void layoutContainer(Container c) {

      int w = getWidth()-2;
      int y = 2;
      int extraCloseOffset = 7;
      
      int buttonHeight = closeButton.getIcon().getIconHeight();  // this includes the border
      int buttonWidth = closeButton.getIcon().getIconWidth();  // this includes the border


      int x = (w);
      
      if(frame.isClosable()) {
	if (isPalette) {
	    x -= (buttonWidth+4);
	   closeButton.setBounds(x, 2, buttonWidth+2, getHeight()-4);
	   x -= 3;
	} else {
	    x -= (buttonWidth+interButtonSpacing);
	    closeButton.setBounds(x , y, buttonWidth, buttonHeight);
	    x -= extraCloseOffset;
	}
      } else if(closeButton.getParent() != null) {
	closeButton.getParent().remove(closeButton);
      }
      
      if(frame.isMaximizable() && !isPalette ) {
	x -= (buttonWidth+interButtonSpacing);
	maxButton.setBounds(x, y, buttonWidth, buttonHeight);
      } else if(maxButton.getParent() != null) {
	maxButton.getParent().remove(maxButton);
      }

      if(frame.isIconifiable() && !isPalette ) {
	x -= (buttonWidth+interButtonSpacing);
	iconButton.setBounds(x, y, buttonWidth, buttonHeight);

      } else if(iconButton.getParent() != null) {
	iconButton.getParent().remove(iconButton);
      }

      riseWidth = w-x;

      
    } 


    public void setPalette(boolean b) {
        isPalette = b;

	if (isPalette) {
	    closeButton.setIcon(UIManager.getIcon("InternalFrame.paletteCloseIcon"));
	} else {
 	    closeButton.setIcon(closeIcon);
	}		
	repaint();
	revalidate();
    }

    private static class NoFocusButton extends JButton {
        public NoFocusButton(Icon icon) {
	    super(icon);
	    setFocusPainted(false);
	}
        public NoFocusButton() { setFocusPainted(false); }
	public boolean isFocusTraversable() { return false; }
	public void requestFocus() {};
        public boolean isOpaque() { return false; }
      public boolean isContentAreaFilled() { return false; }
    }  // end NoFocusButton			     
}    // End Title Pane Class
