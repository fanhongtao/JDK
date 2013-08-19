/*
 * @(#)BasicButtonListener.java	1.54 01/03/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package com.sun.java.swing.plaf.gtk;
 
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.basic.BasicHTML;

/**
 * Button Listener
 *
 * @version 1.9, 01/23/03 (based on BasicButtonListener v 1.58)
 * @author Jeff Dinkins 
 * @author Arnaud Weber (keyboard UI support)
 */

class SynthButtonListener implements MouseListener,
               MouseMotionListener,FocusListener, ChangeListener,
               PropertyChangeListener, SynthEventListener, LazyActionMap.Loader
{
    private static long lastPressedTimestamp = -1;
    private static boolean shouldDiscardRelease = false;
  
    public SynthButtonListener() {
    }

    public void propertyChange(PropertyChangeEvent e) {
	String prop = e.getPropertyName();
        AbstractButton button = (AbstractButton)e.getSource();

        if (AbstractButton.MNEMONIC_CHANGED_PROPERTY.equals(prop)) {
	    updateMnemonicBinding(button);
	}
        else if (AbstractButton.CONTENT_AREA_FILLED_CHANGED_PROPERTY.equals(
                                prop)) {
            checkOpacity(button);
	}
	else if (AbstractButton.TEXT_CHANGED_PROPERTY.equals(prop) ||
                 "font".equals(prop) || "foreground".equals(prop)) {
	    BasicHTML.updateRenderer(button, button.getText());
	}
        if (SynthLookAndFeel.shouldUpdateStyle(e)) {
	    ButtonUI ui = button.getUI();
	    if (ui != null && (ui instanceof SynthButtonUI)) {
                ((SynthButtonUI)ui).fetchStyle(button);
	    }
        }
    }

    protected void checkOpacity(AbstractButton b) {
	b.setOpaque(b.isContentAreaFilled());
    }

    /**
     * Register default key actions: pressing space to "click" a
     * button and registring the keyboard mnemonic (if any).
     */
    public void installKeyboardActions(JComponent c) {
	AbstractButton b = (AbstractButton)c;	
	// Update the mnemonic binding.
	updateMnemonicBinding(b);

        LazyActionMap.installLazyActionMap(c, this);

	InputMap km = getInputMap(JComponent.WHEN_FOCUSED, c);

	SwingUtilities.replaceUIInputMap(c, JComponent.WHEN_FOCUSED, km);
    }

    /**
     * Unregister's default key actions
     */
    public void uninstallKeyboardActions(JComponent c) {
        SwingUtilities.replaceUIInputMap(c, JComponent.WHEN_IN_FOCUSED_WINDOW,
                                         null);
	SwingUtilities.replaceUIInputMap(c, JComponent.WHEN_FOCUSED, null);
	SwingUtilities.replaceUIActionMap(c, null);
    }

    /**
     * Returns the InputMap for condition <code>condition</code>. Called as
     * part of <code>installKeyboardActions</code>.
     */
    InputMap getInputMap(int condition, JComponent c) {
	if (condition == JComponent.WHEN_FOCUSED) {
	    ButtonUI ui = ((AbstractButton)c).getUI();
	    if (ui != null && (ui instanceof SynthButtonUI)) {
                SynthButtonUI buttonUI = (SynthButtonUI)ui;
                SynthContext context = buttonUI.getContext(c,
                                                   SynthConstants.ENABLED);
                InputMap map = (InputMap)context.getStyle().get(
                    context, buttonUI.getPropertyPrefix() +"focusInputMap");

                context.dispose();
                return map;
	    }
	}
	return null;
    }

    /**
     * Creates and returns the ActionMap to use for the button.
     */
    public void loadActionMap(JComponent c, ActionMap map) {
        map.put("pressed", new PressedAction((AbstractButton)c));
	map.put("released", new ReleasedAction((AbstractButton)c));
    }

    /**
     * Resets the binding for the mnemonic in the WHEN_IN_FOCUSED_WINDOW
     * UI InputMap.
     */
    void updateMnemonicBinding(AbstractButton b) {
	int m = b.getMnemonic();
	if (m != 0) {
	    InputMap map = SwingUtilities.getUIInputMap(
                                b, JComponent.WHEN_IN_FOCUSED_WINDOW);

            if (map == null) {
		map = new ComponentInputMapUIResource(b);
		SwingUtilities.replaceUIInputMap(b,
			       JComponent.WHEN_IN_FOCUSED_WINDOW, map);
	    }
            map.clear();
            map.put(KeyStroke.getKeyStroke(m, SynthLookAndFeel.
                              getAcceleratorModifier(), false), "pressed");
            map.put(KeyStroke.getKeyStroke(m, SynthLookAndFeel.
                              getAcceleratorModifier(), true), "released");
            map.put(KeyStroke.getKeyStroke(m, 0, true), "released");
	} 
        else {
	    InputMap map = SwingUtilities.getUIInputMap(b, JComponent.
					     WHEN_IN_FOCUSED_WINDOW);
	    if (map != null) {
		map.clear();
	    }
	}
    }

    public void stateChanged(ChangeEvent e) {
	AbstractButton b = (AbstractButton) e.getSource();
        b.repaint();
    }

    public void focusGained(FocusEvent e) { 
        AbstractButton b = (AbstractButton) e.getSource();

        if (defaultButtonFollowsFocus()) {
            if (b instanceof JButton && ((JButton)b).isDefaultCapable()) {
                JRootPane root = b.getRootPane();
                if (root != null) {
                    JButton defaultButton = root.getDefaultButton();
                    if (defaultButton != null) {
                        root.putClientProperty("temporaryDefaultButton", b);
                        root.setDefaultButton((JButton)b);
                        root.putClientProperty("temporaryDefaultButton", null);
                    }
                }
            }
        }
        b.repaint();
    }

    /**
     * Returns true if the default button should follow focus, the default.
     * A false return value means that the default button is only ever set
     * by the developer.
     */
    protected boolean defaultButtonFollowsFocus() {
      // PENDING: this should be a property.
      return false;
    }

    public void focusLost(FocusEvent e) {
	AbstractButton b = (AbstractButton) e.getSource();

	JRootPane root = b.getRootPane();
	if (root != null) {
	   JButton initialDefault = (JButton)root.getClientProperty("initialDefaultButton");
	   if (initialDefault != null && b != initialDefault) {
	       root.setDefaultButton(initialDefault);
	   }
	}
        b.getModel().setArmed(false);
	b.repaint();
    }

    public void mouseMoved(MouseEvent e) {
    }


    public void mouseDragged(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }
 
    public void mousePressed(MouseEvent e) {
       if (SwingUtilities.isLeftMouseButton(e) ) {
	  AbstractButton b = (AbstractButton) e.getSource();

	  if(b.contains(e.getX(), e.getY())) {
	      long multiClickThreshhold = b.getMultiClickThreshhold();
	      long lastTime = lastPressedTimestamp;
	      long currentTime = lastPressedTimestamp = e.getWhen();
	      if (lastTime != -1 && currentTime - lastTime < multiClickThreshhold) {
		  shouldDiscardRelease = true;
		  return;
	      }

	     ButtonModel model = b.getModel();
	     if (!model.isEnabled()) {
	        // Disabled buttons ignore all input...
	   	return;
	     }
	     if (!model.isArmed()) {
		// button not armed, should be
                model.setArmed(true);
	     }
	     model.setPressed(true);
	     if(!b.hasFocus() && b.isRequestFocusEnabled()) {
	        b.requestFocus();
	     }            
	  } 
       }
    };
    
    public void mouseReleased(MouseEvent e) {
	// Support for multiClickThreshhold
        if (shouldDiscardRelease) {
	    shouldDiscardRelease = false;
	    return;
	}
	AbstractButton b = (AbstractButton) e.getSource();
	ButtonModel model = b.getModel();
	model.setPressed(false);
	model.setArmed(false);
    };
 
    public void mouseEntered(MouseEvent e) {
	AbstractButton b = (AbstractButton) e.getSource();
        ButtonModel model = b.getModel();
        if(b.isRolloverEnabled()) {
            model.setRollover(true);
        }
        if (model.isPressed())
		model.setArmed(true);
    };
 
    public void mouseExited(MouseEvent e) {
	AbstractButton b = (AbstractButton) e.getSource();
        ButtonModel model = b.getModel();
        if(b.isRolloverEnabled()) {
            model.setRollover(false);
        }
        model.setArmed(false);
    };

    static class PressedAction extends AbstractAction {
	AbstractButton b = null;
        PressedAction(AbstractButton b) {
	    this.b = b;
	}
	
	public void actionPerformed(ActionEvent e) {
	    ButtonModel model = b.getModel();
	    model.setArmed(true);
	    model.setPressed(true);
	    if(!b.hasFocus()) {
		b.requestFocus();
	    }
        }

	public boolean isEnabled() {
	    if(!b.getModel().isEnabled()) {
		return false;
	    } else {
		return true;
	    }
	}
    }

   static class ReleasedAction extends AbstractAction {
	AbstractButton b = null;
        ReleasedAction(AbstractButton b) {
	    this.b = b;
	}
	
	public void actionPerformed(ActionEvent e) {
            ButtonModel model = b.getModel();
            model.setPressed(false);
            model.setArmed(false);
        }

	public boolean isEnabled() {
	    if(!b.getModel().isEnabled()) {
		return false;
	    } else {
		return true;
	    }
	}
    }

}

