/*
 * @(#)BasicButtonListener.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
 
package javax.swing.plaf.basic;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Button Listener
 *
 * @version 1.39 11/03/98
 * @author Jeff Dinkins 
 * @author Arnaud Weber (keyboard UI support)
 */

public class BasicButtonListener implements MouseListener, MouseMotionListener, 
                                   FocusListener, ChangeListener, PropertyChangeListener
{
    // used in mouseDragged
    private Rectangle tmpRect = new Rectangle();


    // Keyboard Actions used by the mnemonic accelerator and
    // the "spacebar" accelerator triggers
    private KeyStroke altPressedKeyStroke = null;
    private KeyStroke altReleasedKeyStroke = null;
    private KeyStroke nonAltReleasedKeyStroke = null;

    // These two keystrokes can be shared accross all buttons. 
    private static KeyStroke spacePressedKeyStroke = null;
    private static KeyStroke spaceReleasedKeyStroke = null;
  
    public BasicButtonListener(AbstractButton b) {
	if(spacePressedKeyStroke == null) {
	    spacePressedKeyStroke = KeyStroke.getKeyStroke(' ', 0, false);
	    spaceReleasedKeyStroke = KeyStroke.getKeyStroke(' ', 0,true);
	}
    }

    public void propertyChange(PropertyChangeEvent e) {
	String prop = e.getPropertyName();
	if(prop.equals(AbstractButton.MNEMONIC_CHANGED_PROPERTY)) {
	    uninstallKeyboardActions((AbstractButton) e.getSource());
	    installKeyboardActions((AbstractButton) e.getSource());
	}

	if(prop.equals(AbstractButton.CONTENT_AREA_FILLED_CHANGED_PROPERTY)) {
	    checkOpacity((AbstractButton) e.getSource() );
	}

	if(prop.equals(AbstractButton.TEXT_CHANGED_PROPERTY)) {
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
	AbstractButton b = (AbstractButton) c;

	PressedAction pressedAction = new PressedAction(b);
	ReleasedAction releasedAction = new ReleasedAction(b);

	b.registerKeyboardAction(pressedAction, spacePressedKeyStroke, JComponent.WHEN_FOCUSED);
	b.registerKeyboardAction(releasedAction, spaceReleasedKeyStroke, JComponent.WHEN_FOCUSED);

	int m = b.getMnemonic();
	if(m != 0) {
	    altPressedKeyStroke     = KeyStroke.getKeyStroke(m, ActionEvent.ALT_MASK, false);
	    altReleasedKeyStroke    = KeyStroke.getKeyStroke(m, ActionEvent.ALT_MASK, true);
	    nonAltReleasedKeyStroke = KeyStroke.getKeyStroke(m, 0, true);
	    
	    b.registerKeyboardAction(pressedAction, altPressedKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
	    b.registerKeyboardAction(releasedAction, altReleasedKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
	    b.registerKeyboardAction(releasedAction, nonAltReleasedKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
	} 
    }

    /**
     * Unregister's default key actions
     * @see #registerKeyboardActions
     */
    public void uninstallKeyboardActions(JComponent c) {
	AbstractButton b = (AbstractButton) c;

	// Don't null out the spacePressed/spaceReleased KeyStrokes,
	// they are shared accross all buttons
 	if(spacePressedKeyStroke != null) {
	    b.unregisterKeyboardAction(spacePressedKeyStroke);
	}

 	if(spaceReleasedKeyStroke != null) {
	    b.unregisterKeyboardAction(spaceReleasedKeyStroke);
	}

 	if(altPressedKeyStroke != null) {
	    b.unregisterKeyboardAction(altPressedKeyStroke);
	    altPressedKeyStroke = null;
	}

 	if(altReleasedKeyStroke != null) {
	    b.unregisterKeyboardAction(altReleasedKeyStroke);
	    altReleasedKeyStroke = null;
	}

 	if(nonAltReleasedKeyStroke != null) {
	    b.unregisterKeyboardAction(nonAltReleasedKeyStroke);
	    nonAltReleasedKeyStroke = null;
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
                root.setDefaultButton((JButton)b);
            }
        }
	b.repaint();
    }

    public void focusLost(FocusEvent e) {
	AbstractButton b = (AbstractButton) e.getSource();
	b.repaint();
    }

    public void mouseMoved(MouseEvent e) {
    };


    public void mouseDragged(MouseEvent e) {
	AbstractButton b = (AbstractButton) e.getSource();

	// HACK! We're forced to do this since mouseEnter and mouseExit aren't
	// reported while the mouse is down.
	ButtonModel model = b.getModel();

	if(model.isPressed()) {
            tmpRect.width = b.getWidth();
            tmpRect.height = b.getHeight();
            if(tmpRect.contains(e.getPoint())) {
                model.setArmed(true);
            } else {
                model.setArmed(false);
            }
        }
    };

    public void mouseClicked(MouseEvent e) {
    };
 
    public void mousePressed(MouseEvent e) {
        if ( SwingUtilities.isLeftMouseButton(e) ) {
	    AbstractButton b = (AbstractButton) e.getSource();
	    ButtonModel model = b.getModel();
	    if (!model.isEnabled()) {
		// Disabled buttons ignore all input...
		return;
	    }

	    // But because of the mouseDragged hack above, we can't do setArmed
	    // in mouseEnter. As a workaround, set it here just before setting
	    // focus.
	    model.setArmed(true);
	    model.setPressed(true);
	    if(!b.hasFocus()) {
		b.requestFocus();
	    }            
	}
    };
    
    public void mouseReleased(MouseEvent e) {
	AbstractButton b = (AbstractButton) e.getSource();
	ButtonModel model = b.getModel();
	model.setPressed(false);
	model.setArmed(false);
    };
 
    public void mouseEntered(MouseEvent e) {
	AbstractButton b = (AbstractButton) e.getSource();
	if(b.isRolloverEnabled()) {
	    b.getModel().setRollover(true);
	}
    };
 
    public void mouseExited(MouseEvent e) {
	AbstractButton b = (AbstractButton) e.getSource();
	if(b.isRolloverEnabled()) {
	    b.getModel().setRollover(false);
	}
    };
	

    static class PressedAction implements ActionListener {
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

   static class ReleasedAction implements ActionListener {
	AbstractButton b = null;
        ReleasedAction(AbstractButton b) {
	    this.b = b;
	}
	
	public void actionPerformed(ActionEvent e) {
	    b.getModel().setPressed(false);
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

