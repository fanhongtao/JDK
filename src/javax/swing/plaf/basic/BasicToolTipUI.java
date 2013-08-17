/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.plaf.ToolTipUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.View;


/**
 * Standard tool tip L&F.
 * <p>
 *
 * @version 1.31 02/06/02
 * @author Dave Moore
 */
public class BasicToolTipUI extends ToolTipUI
{
    static BasicToolTipUI sharedInstance = new BasicToolTipUI();

    private   PropertyChangeListener propertyChangeListener;

    public static ComponentUI createUI(JComponent c) {
        return sharedInstance;
    }

    public BasicToolTipUI() {
        super();
    }

    public void installUI(JComponent c) {
	installDefaults(c);
	installComponents(c);
	installListeners(c);
    }

    public void uninstallUI(JComponent c) {
	// REMIND: this is NOT getting called
	uninstallDefaults(c);
	uninstallComponents(c);
	uninstallListeners(c);
    }

    protected void installDefaults(JComponent c){
	LookAndFeel.installColorsAndFont(c, "ToolTip.background",
					 "ToolTip.foreground",
					 "ToolTip.font");
        LookAndFeel.installBorder(c, "ToolTip.border");
    }
    
   protected void uninstallDefaults(JComponent c){
	LookAndFeel.uninstallBorder(c);
    }

    /* Unfortunately this has to remain private until we can make API additions.
     */
    private void installComponents(JComponent c){
 	BasicHTML.updateRenderer(c, ((JToolTip)c).getTipText());
    }
     
    /* Unfortunately this has to remain private until we can make API additions.
     */
    private void uninstallComponents(JComponent c){
 	BasicHTML.updateRenderer(c, "");
    }

    protected void installListeners(JComponent c) {
	propertyChangeListener = createPropertyChangeListener(c);
	
        c.addPropertyChangeListener(propertyChangeListener);      
    }

    protected void uninstallListeners(JComponent c) {
        c.removePropertyChangeListener(propertyChangeListener);

	propertyChangeListener = null;
    }

    /* Unfortunately this has to remain private until we can make API additions.
     */
    private PropertyChangeListener createPropertyChangeListener(JComponent c) {
        return new PropertyChangeHandler();
    }

    public void paint(Graphics g, JComponent c) {
        Font font = c.getFont();
        FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
        Dimension size = c.getSize();
        g.setColor(c.getBackground());
        g.fillRect(0, 0, size.width, size.height);
        g.setColor(c.getForeground());
        g.setFont(font);
	// fix for bug 4153892
	String tipText = ((JToolTip)c).getTipText();
	if (tipText == null) {
	    tipText = "";
	}
	View v = (View) c.getClientProperty(BasicHTML.propertyKey);
	if (v != null) {
	    Rectangle paintTextR = c.getBounds();
	    Insets insets = c.getInsets();
	    paintTextR.x += insets.left;
	    paintTextR.y += insets.top;
	    paintTextR.width -= insets.left+insets.right;
	    paintTextR.height -= insets.top+insets.bottom;
	    
	    v.paint(g, paintTextR);
	} else {
	    g.drawString(tipText, 3, 2 + metrics.getAscent());
	}
    }

    public Dimension getPreferredSize(JComponent c) {
        Font font = c.getFont();
        FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
	Insets insets = c.getInsets();
	Dimension prefSize = new Dimension(insets.left+insets.right,
					   insets.top+insets.bottom);
	String text = ((JToolTip)c).getTipText();

	if ((text == null) || text.equals("")) {
            text = "";
        }
        else {
	    View v = (c != null) ? (View) c.getClientProperty("html") : null;
	    if (v != null) {
		prefSize.width += (int) v.getPreferredSpan(View.X_AXIS);
		prefSize.height += (int) v.getPreferredSpan(View.Y_AXIS);
	    } else {
		prefSize.width += SwingUtilities.computeStringWidth(fm,text) + 6;
		prefSize.height += fm.getHeight() + 4;
	    }
        }
	return prefSize;
    }

    public Dimension getMinimumSize(JComponent c) {
	Dimension d = getPreferredSize(c);
 	View v = (View) c.getClientProperty(BasicHTML.propertyKey);
 	if (v != null) {
 	    d.width -= v.getPreferredSpan(View.X_AXIS) - v.getMinimumSpan(View.X_AXIS);
 	}
 	return d;
    }

    public Dimension getMaximumSize(JComponent c) {
	Dimension d = getPreferredSize(c);
 	View v = (View) c.getClientProperty(BasicHTML.propertyKey);
 	if (v != null) {
 	    d.width += v.getMaximumSpan(View.X_AXIS) - v.getPreferredSpan(View.X_AXIS);
 	}
 	return d;
    }

    private class PropertyChangeHandler implements PropertyChangeListener {
	public void propertyChange(PropertyChangeEvent e) {
	    String name = e.getPropertyName();
	    if (name.equals("tiptext")) {
		// remove the old html view client property if one
		// existed, and install a new one if the text installed
		// into the JLabel is html source.
		JToolTip tip = ((JToolTip) e.getSource());
		String text = tip.getTipText();
		BasicHTML.updateRenderer(tip, text);
	    }
	}
    }
}
