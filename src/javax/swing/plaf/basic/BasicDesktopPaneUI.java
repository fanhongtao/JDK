/*
 * @(#)BasicDesktopPaneUI.java	1.28 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import javax.swing.*;
import javax.swing.plaf.*;

import java.beans.*;

import java.awt.event.*;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.*;
import java.util.Vector;

/**
 * Basic L&F for a desktop.
 *
 * @version 1.28 11/29/01
 * @author Steve Wilson
 */
public class BasicDesktopPaneUI extends DesktopPaneUI
{
    private static Dimension minSize = new Dimension(0,0);
    private static Dimension maxSize = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);

    protected JDesktopPane desktop;
    protected DesktopManager desktopManager;

    protected KeyStroke minimizeKey;
    protected KeyStroke maximizeKey;
    protected KeyStroke closeKey;
    protected KeyStroke navigateKey,navigateKey2;

    public static ComponentUI createUI(JComponent c) {
        return new BasicDesktopPaneUI();
    }

    public BasicDesktopPaneUI() {
    }

    public void installUI(JComponent c)   {
	desktop = (JDesktopPane)c;
	installDefaults();
	installDesktopManager();
	installKeyboardActions();
	
    }

    public void uninstallUI(JComponent c) {
	uninstallKeyboardActions();
	uninstallDesktopManager();
        uninstallDefaults();
	desktop = null;
    }

    protected void installDefaults() {
	if (desktop.getBackground() == null || 
	    desktop.getBackground() instanceof UIResource) {
	    desktop.setBackground(UIManager.getColor("Desktop.background"));
	}        
    }

    protected void uninstallDefaults() { }

    protected void installDesktopManager() {
	if(desktop.getDesktopManager() == null) {
	    desktopManager = new DefaultDesktopManager();
	    desktop.setDesktopManager(desktopManager);
	}
    }

    protected void uninstallDesktopManager() {
	if(desktop.getDesktopManager() == desktopManager) {
	    desktop.setDesktopManager(null);
	}
	desktopManager = null;
    }

    protected void installKeyboardActions(){
      minimizeKey = KeyStroke.getKeyStroke(KeyEvent.VK_F9, KeyEvent.CTRL_MASK);
      maximizeKey = KeyStroke.getKeyStroke(KeyEvent.VK_F10, KeyEvent.CTRL_MASK);
      closeKey = KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.CTRL_MASK);
      navigateKey = KeyStroke.getKeyStroke(KeyEvent.VK_F6, KeyEvent.CTRL_MASK);
      navigateKey2 = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.CTRL_MASK);
      registerKeyboardActions();
    }

    protected void registerKeyboardActions(){
      // minimize
      desktop.registerKeyboardAction(new MinimizeAction(),      
				     minimizeKey, 
				     JComponent.WHEN_IN_FOCUSED_WINDOW);
      // maximize
      desktop.registerKeyboardAction(new MaximizeAction(),
				     maximizeKey,
				     JComponent.WHEN_IN_FOCUSED_WINDOW);				     
      // close
      desktop.registerKeyboardAction(new CloseAction(),
				     closeKey,
				     JComponent.WHEN_IN_FOCUSED_WINDOW);
      // navigate key
      desktop.registerKeyboardAction(new NavigateAction(),
				     navigateKey2,
				     JComponent.WHEN_IN_FOCUSED_WINDOW);

      desktop.registerKeyboardAction(new NavigateAction(),
				     navigateKey,
				     JComponent.WHEN_IN_FOCUSED_WINDOW);


    }
 
    protected void unregisterKeyboardActions(){
      desktop.unregisterKeyboardAction(minimizeKey);
      desktop.unregisterKeyboardAction(maximizeKey);
      desktop.unregisterKeyboardAction(closeKey);
      desktop.unregisterKeyboardAction(navigateKey);
      desktop.unregisterKeyboardAction(navigateKey2);      
      minimizeKey = maximizeKey = closeKey = navigateKey = navigateKey2 = null;
    }

    protected void uninstallKeyboardActions(){ 
      unregisterKeyboardActions();
    }

    public void paint(Graphics g, JComponent c) {}

    public Dimension getPreferredSize(JComponent c) {return null;}

    public Dimension getMinimumSize(JComponent c) {
	return minSize;
	}
    public Dimension getMaximumSize(JComponent c){
	return maxSize;
    }

    protected class MinimizeAction extends AbstractAction {
      public void actionPerformed(ActionEvent e) {
	// get the active frame
	JInternalFrame f = desktop.getAllFrames()[0]; // current active frame
	if (f != null){
	  if (f.isMaximizable()){
	    try {
	      f.setIcon(true);
	    } catch(PropertyVetoException e0) { }
	  }
	}
      }
      public boolean isEnabled() { 
	return true;
      }
    }

    protected class MaximizeAction extends AbstractAction {
      public void actionPerformed(ActionEvent e) {
	JInternalFrame f = desktop.getAllFrames()[0]; // current active frame
	if (f != null){
	  if (f.isMaximizable()){
	    try {
	      f.setMaximum(true);
	    } catch(PropertyVetoException e0) { } 
	  }
	}
      }
      public boolean isEnabled() { 
	return true;
      }
    }

    protected class CloseAction extends AbstractAction {
      public void actionPerformed(ActionEvent e) {
		  // need to check for no JInternalFrame in the desktop
		  JInternalFrame[] results = desktop.getAllFrames();
		  JInternalFrame f = null;
		  if (results.length > 0)
			  f = results[0];
		  if (f != null){
			  if (f.isClosable()){
				  try {
					  f.setClosed(true);
				  } catch(PropertyVetoException e0) { } 
			  }
		  }
      }
      public boolean isEnabled() { 
	return true;
      }
    }

    protected class NavigateAction extends AbstractAction {
      	public void actionPerformed(ActionEvent e) {
	  // very basic support now - we need this in DefaultDesktopManager
	  // since we need to be in the loop of when Windows are activated
	  // closed - etc - the getAllFrames schema is very limited
	  JInternalFrame[] allFrames = desktop.getAllFrames();
	  int i = allFrames.length;
	  if (i >= 1){  
	    // basic Z-ordering in allFrame is kept from 0-n, where
	    // 0 is the active, and 1 is the most recent active, etc.
	    i--;
	  }
	  // icons always stay at the end - skip them for now
	  while (allFrames[i].isIcon()){
	    i--;
	    if (i == 0)
	      break;
	  } 
	  desktopManager.activateFrame(allFrames[i]);
	  try {
	    allFrames[i].setSelected(true);
	  } catch (PropertyVetoException e2){}
	}

	public boolean isEnabled() { 
	  return true;
	}

    }
}

