/*
 * @(#)MotifInternalFrameUI.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package com.sun.java.swing.plaf.motif;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.EventListener;

import javax.swing.plaf.basic.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.border.*;
import javax.swing.plaf.*;


/**
 * A Motif L&F implementation of InternalFrame.  
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.14 10/30/98
 * @author Tom Ball
 */
public class MotifInternalFrameUI extends BasicInternalFrameUI {

    Color color;
    Color highlight;
    Color shadow;
    MotifInternalFrameTitlePane titlePane;

    protected KeyStroke closeMenuKey;


/////////////////////////////////////////////////////////////////////////////
// ComponentUI Interface Implementation methods
/////////////////////////////////////////////////////////////////////////////
    public static ComponentUI createUI(JComponent w)    {
        return new MotifInternalFrameUI((JInternalFrame)w);
    }

    public MotifInternalFrameUI(JInternalFrame w)   {
        super(w);
    }
            
    public void installUI(JComponent c)   {
        super.installUI(c);
        setColors((JInternalFrame)c);
    }   

    protected void installDefaults() {
	Border frameBorder = frame.getBorder();
        if (frameBorder == null || frameBorder instanceof UIResource) {
            frame.setBorder(new MotifBorders.InternalFrameBorder(frame));
        }	
    }


    protected void installKeyboardActions(){
      super.installKeyboardActions();
      // we use JPopup in our TitlePane so need escape support
      closeMenuKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    }


    protected void uninstallDefaults() {
        LookAndFeel.uninstallBorder(frame);
    }

    private JInternalFrame getFrame(){
      return frame;
    }

    public JComponent createNorthPane(JInternalFrame w) {
        titlePane = new MotifInternalFrameTitlePane(w);
        return titlePane;
    }

    public Dimension getMaximumSize(JComponent x) {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    protected void uninstallKeyboardActions(){
      super.uninstallKeyboardActions();
      if (isKeyBindingRegistered()){
	frame.unregisterKeyboardAction(closeMenuKey);
	frame.getDesktopIcon().unregisterKeyboardAction(closeMenuKey);
      }
    }
    
    protected void setupMenuOpenKey(){
	frame.registerKeyboardAction(new ActionListener(){
	    public void actionPerformed(ActionEvent e){
		titlePane.showSystemMenu();
	    }
	    public boolean isEnabled(){
		return isKeyBindingActive();
	    }
	},
	    openMenuKey,
	    JComponent.WHEN_IN_FOCUSED_WINDOW);

	frame.getDesktopIcon().registerKeyboardAction(new ActionListener(){
	    public void actionPerformed(ActionEvent e){
	      JInternalFrame.JDesktopIcon icon = getFrame().getDesktopIcon();
	      MotifDesktopIconUI micon = (MotifDesktopIconUI)icon.getUI();
	      micon.showSystemMenu();
	    }
	    public boolean isEnabled(){
		return isKeyBindingActive();
	    }
	},
	    openMenuKey,
	    JComponent.WHEN_IN_FOCUSED_WINDOW);

    }       


    protected void setupMenuCloseKey(){
      	frame.registerKeyboardAction(new ActionListener(){
	  public void actionPerformed(ActionEvent e){
	    titlePane.hideSystemMenu();
	  }
	  public boolean isEnabled(){
	    return isKeyBindingActive();
	  }
	},
	  closeMenuKey,
	  JComponent.WHEN_IN_FOCUSED_WINDOW);

      	frame.getDesktopIcon().registerKeyboardAction(new ActionListener(){
	  public void actionPerformed(ActionEvent e){
	    JInternalFrame.JDesktopIcon icon = getFrame().getDesktopIcon();
	    MotifDesktopIconUI micon = (MotifDesktopIconUI)icon.getUI();
	    micon.hideSystemMenu();
	  }
	  public boolean isEnabled(){
	    return isKeyBindingActive();
	  }
	},
	  closeMenuKey,
	  JComponent.WHEN_IN_FOCUSED_WINDOW);


    }

    /** This method is called when the frame becomes selected.
      */
    protected void activateFrame(JInternalFrame f) {
        super.activateFrame(f);
        setColors(f);
    }
    /** This method is called when the frame is no longer selected.
      */
    protected void deactivateFrame(JInternalFrame f) {
        setColors(f);
        super.deactivateFrame(f);
    }

    void setColors(JInternalFrame frame) {
        if (frame.isSelected()) {
            color = UIManager.getColor("activeCaptionBorder");
        } else {
            color = UIManager.getColor("inactiveCaptionBorder");
        }
        highlight = color.brighter();
        shadow = color.darker().darker();
        titlePane.setColors(color, highlight, shadow);
    }


}
