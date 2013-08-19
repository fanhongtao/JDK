/*
 * @(#)BasicButtonListener.java	1.59 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package javax.swing.plaf.basic;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentInputMapUIResource;

/**
 * Button Listener
 *
 * @version 1.59 01/23/03
 * @author Jeff Dinkins 
 * @author Arnaud Weber (keyboard UI support)
 */

public class BasicButtonListener implements MouseListener, MouseMotionListener, 
                                   FocusListener, ChangeListener, PropertyChangeListener
{
    /** Set to true when the WindowInputMap is installed. */
    private boolean createdWindowInputMap;

    transient long lastPressedTimestamp = -1;
    transient boolean shouldDiscardRelease = false;
  
    public BasicButtonListener(AbstractButton b) {
    }

    public void propertyChange(PropertyChangeEvent e) {
	String prop = e.getPropertyName();
	if(prop.equals(AbstractButton.MNEMONIC_CHANGED_PROPERTY)) {
	    updateMnemonicBinding((AbstractButton)e.getSource());
	}

	if(prop.equals(AbstractButton.CONTENT_AREA_FILLED_CHANGED_PROPERTY)) {
	    checkOpacity((AbstractButton) e.getSource() );
	}

	if(prop.equals(AbstractButton.TEXT_CHANGED_PROPERTY) ||
           "font".equals(prop) || "foreground".equals(prop)) {
	    AbstractButton b = (AbstractButton) e.getSource();
	    BasicHTML.updateRenderer(b, b.getText());
	}
    }

    protected void checkOpacity(AbstractButton b) {
	b.setOpaque( b.isContentAreaFilled() );
    }

    /**
     * Register default key actions: pressing space to "click" a
     * button and registring the keyboard mnemonic (if any).
     */
    public void installKeyboardActions(JComponent c) {
	AbstractButton b = (AbstractButton)c;	
	// Update the mnemonic binding.
	updateMnemonicBinding(b);

	// Reset the ActionMap.
	ActionMap map = getActionMap(b);

	SwingUtilities.replaceUIActionMap(c, map);

	InputMap km = getInputMap(JComponent.WHEN_FOCUSED, c);

	SwingUtilities.replaceUIInputMap(c, JComponent.WHEN_FOCUSED, km);
    }

    /**
     * Unregister's default key actions
     */
    public void uninstallKeyboardActions(JComponent c) {
	if (createdWindowInputMap) {
	    SwingUtilities.replaceUIInputMap(c, JComponent.
					   WHEN_IN_FOCUSED_WINDOW, null);
	    createdWindowInputMap = false;
	}
	SwingUtilities.replaceUIInputMap(c, JComponent.WHEN_FOCUSED, null);
	SwingUtilities.replaceUIActionMap(c, null);
    }

    /**
     * Returns the ActionMap to use for <code>b</code>. Called as part of
     * <code>installKeyboardActions</code>.
     */
    ActionMap getActionMap(AbstractButton b) {
	return createActionMap(b);
    }

    /**
     * Returns the InputMap for condition <code>condition</code>. Called as
     * part of <code>installKeyboardActions</code>.
     */
    InputMap getInputMap(int condition, JComponent c) {
	if (condition == JComponent.WHEN_FOCUSED) {
	    ButtonUI ui = ((AbstractButton)c).getUI();
	    if (ui != null && (ui instanceof BasicButtonUI)) {
		return (InputMap)UIManager.get(((BasicButtonUI)ui).
				       getPropertyPrefix() +"focusInputMap");
	    }
	}
	return null;
    }

    /**
     * Creates and returns the ActionMap to use for the button.
     */
    ActionMap createActionMap(AbstractButton c) {
	ActionMap retValue = new javax.swing.plaf.ActionMapUIResource();

	retValue.put("pressed", new PressedAction((AbstractButton)c));
	retValue.put("released", new ReleasedAction((AbstractButton)c));
	return retValue;
    }

    /**
     * Resets the binding for the mnemonic in the WHEN_IN_FOCUSED_WINDOW
     * UI InputMap.
     */
    void updateMnemonicBinding(AbstractButton b) {
	int m = b.getMnemonic();
	if(m != 0) {
	    InputMap map;
	    if (!createdWindowInputMap) {
		map = new ComponentInputMapUIResource(b);
		SwingUtilities.replaceUIInputMap(b,
			       JComponent.WHEN_IN_FOCUSED_WINDOW, map);
		createdWindowInputMap = true;
	    }
	    else {
		map = SwingUtilities.getUIInputMap(b, JComponent.
						 WHEN_IN_FOCUSED_WINDOW);
	    }
	    if (map != null) {
		map.clear();
		map.put(KeyStroke.getKeyStroke(m, ActionEvent.ALT_MASK, false),
			"pressed");
		map.put(KeyStroke.getKeyStroke(m, ActionEvent.ALT_MASK, true),
		       "released");
		map.put(KeyStroke.getKeyStroke(m, 0, true), "released");
	    }
	} 
	else if (createdWindowInputMap) {
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
        if (b instanceof JButton && ((JButton)b).isDefaultCapable()) {
            JRootPane root = b.getRootPane();
            if (root != null) {
                root.putClientProperty("temporaryDefaultButton", b);
                root.setDefaultButton((JButton)b);
                root.putClientProperty("temporaryDefaultButton", null);
            }
        }
	b.repaint();
    }

    public void focusLost(FocusEvent e) {
	AbstractButton b = (AbstractButton) e.getSource();

	JRootPane root = b.getRootPane();
	if (root != null) {
	   JButton initialDefault = (JButton)root.getClientProperty("initialDefaultButton");
	   if (b != initialDefault) {
	       root.setDefaultButton(initialDefault);
	   }
	}

        b.getModel().setArmed(false);

	b.repaint();
    }

    public void mouseMoved(MouseEvent e) {
    };


    public void mouseDragged(MouseEvent e) {
    };

    public void mouseClicked(MouseEvent e) {
    };
 
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
	if (SwingUtilities.isLeftMouseButton(e)) {
	    // Support for multiClickThreshhold
            if (shouldDiscardRelease) {
	        shouldDiscardRelease = false;
	        return;
	    }
	    AbstractButton b = (AbstractButton) e.getSource();
	    ButtonModel model = b.getModel();
	    model.setPressed(false);
	    model.setArmed(false);
        }
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

