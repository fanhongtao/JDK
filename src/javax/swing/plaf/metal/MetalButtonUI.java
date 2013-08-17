/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package javax.swing.plaf.metal;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.plaf.*;

/**
 * MetalButtonUI implementation
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.23 02/06/02
 * @author Tom Santos
 */
public class MetalButtonUI extends BasicButtonUI {

    private final static MetalButtonUI metalButtonUI = new MetalButtonUI(); 

    private boolean defaults_initialized = false;

    protected Color focusColor;
    protected Color selectColor;
    protected Color disabledTextColor;
 
    // ********************************
    //          Create PLAF
    // ********************************
    public static ComponentUI createUI(JComponent c) {
        return metalButtonUI;
    }
 
    // ********************************
    //          Install
    // ********************************
    public void installDefaults(AbstractButton b) {
        super.installDefaults(b);
	if(!defaults_initialized) {
	    focusColor = UIManager.getColor(getPropertyPrefix() + "focus");
	    selectColor = UIManager.getColor(getPropertyPrefix() + "select");
	    disabledTextColor = UIManager.getColor(getPropertyPrefix() + "disabledText");
	    defaults_initialized = true;
	}
    }

    public void uninstallDefaults(AbstractButton b) {
	super.uninstallDefaults(b);
	defaults_initialized = false;
    }

    // ********************************
    //         Create Listeners
    // ********************************
    protected BasicButtonListener createButtonListener(AbstractButton b) {
	return new MetalButtonListener(b);
    }

    
    // ********************************
    //         Default Accessors 
    // ********************************
    protected Color getSelectColor() {
	return selectColor;
    }

    protected Color getDisabledTextColor() {
	return disabledTextColor;
    }

    protected Color getFocusColor() {
	return focusColor;
    }

    // ********************************
    //          Paint
    // ********************************
    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        if ( b.isContentAreaFilled() ) {
            Dimension size = b.getSize();
	    g.setColor(getSelectColor());
	    g.fillRect(0, 0, size.width, size.height);
	}
    }

    protected void paintFocus(Graphics g, AbstractButton b,
			      Rectangle viewRect, Rectangle textRect, Rectangle iconRect){

        Rectangle focusRect = new Rectangle();
	String text = b.getText();
	boolean isIcon = b.getIcon() != null;

        // If there is text
        if ( text != null && !text.equals( "" ) ) {
  	    if ( !isIcon ) {
	        focusRect.setBounds( textRect );
	    }
	    else {
	        focusRect.setBounds( iconRect.union( textRect ) );
	    }
        }
        // If there is an icon and no text
        else if ( isIcon ) {
  	    focusRect.setBounds( iconRect );
        }

        g.setColor(getFocusColor());
	g.drawRect((focusRect.x-1), (focusRect.y-1),
		  focusRect.width+1, focusRect.height+1);

    }


    protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
	AbstractButton b = (AbstractButton) c;			     
	ButtonModel model = b.getModel();
	FontMetrics fm = g.getFontMetrics();

	/* Draw the Text */
	if(model.isEnabled()) {
	    /*** paint the text normally */
	    g.setColor(b.getForeground());
	    BasicGraphicsUtils.drawString(g,text, model.getMnemonic(),
					  textRect.x,
					  textRect.y + fm.getAscent());
	}
	else {
	    /*** paint the text disabled ***/
	    g.setColor(getDisabledTextColor());
	    BasicGraphicsUtils.drawString(g,text,model.getMnemonic(),
					  textRect.x, textRect.y + fm.getAscent());

	}
    }

}

class MetalButtonListener extends BasicButtonListener
{

    public MetalButtonListener(AbstractButton b) {
      super(b);  
    }

    public void focusGained(FocusEvent e) { 
        Component c = (Component)e.getSource();
	c.repaint();
    }
}
   

