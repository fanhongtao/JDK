/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import java.beans.*;
import java.util.EventListener;
import java.io.Serializable;


/**
 * Basic L&F for a minimized window on a desktop.
 *
 * @version 1.28 02/06/02
 * @author David Kloba
 * @author Steve Wilson
 * @author Rich Schiavi
 */
public class BasicDesktopIconUI extends DesktopIconUI {

    protected JInternalFrame.JDesktopIcon desktopIcon;
    protected JInternalFrame frame;

    JComponent   iconPane;
    MouseInputListener mouseInputListener;



    public static ComponentUI createUI(JComponent c)    {
        return new BasicDesktopIconUI();
    }

    public BasicDesktopIconUI() {
    }

    public void installUI(JComponent c)   {
	desktopIcon = (JInternalFrame.JDesktopIcon)c;
	frame = desktopIcon.getInternalFrame();
	installDefaults();
	installComponents();
	installListeners();
	JLayeredPane.putLayer(desktopIcon, JLayeredPane.getLayer(frame));
    }

    public void uninstallUI(JComponent c) {
        //	installDefaults( desktopIcon ); ?? install->uninstall??
	uninstallDefaults();
	uninstallComponents();
	uninstallListeners();
	desktopIcon = null;
	frame = null;
    }

    protected void installComponents() {
	frame = desktopIcon.getInternalFrame();
	iconPane = new BasicInternalFrameTitlePane(frame);
	desktopIcon.setLayout(new BorderLayout());
	desktopIcon.add(iconPane, BorderLayout.CENTER);
    }

    protected void uninstallComponents() {
	desktopIcon.setLayout(null);
	desktopIcon.remove(iconPane);
    }

    protected void installListeners() {
	mouseInputListener = createMouseInputListener();
	desktopIcon.addMouseMotionListener(mouseInputListener);
	desktopIcon.addMouseListener(mouseInputListener);
    }

    protected void uninstallListeners() {
	desktopIcon.removeMouseMotionListener(mouseInputListener);
	desktopIcon.removeMouseListener(mouseInputListener);
    }

    protected void installDefaults() {
        LookAndFeel.installBorder(desktopIcon, "DesktopIcon.border");
    }

    protected void uninstallDefaults() {
    }

    protected MouseInputListener createMouseInputListener() {
        return new MouseInputHandler();
    }
    
    public Dimension getPreferredSize(JComponent c) {
        JInternalFrame iframe = desktopIcon.getInternalFrame();
	Border border = iframe.getBorder();
        int w2 = 157;
	int h2 = 18;

	if(border != null)
	    h2 += border.getBorderInsets(iframe).bottom + 
                  border.getBorderInsets(iframe).top;

	return new Dimension(w2, h2);
    }

    public Dimension getMinimumSize(JComponent c) {
	return iconPane.getMinimumSize();
    } 

    public Dimension getMaximumSize(JComponent c){
	return iconPane.getMaximumSize();
    }

    public Insets getInsets(JComponent c) {
        JInternalFrame iframe = desktopIcon.getInternalFrame();
	Border border = iframe.getBorder();
	if(border != null)
	    return border.getBorderInsets(iframe);
	
	return new Insets(0,0,0,0);
    }

    public void deiconize() {
        try { frame.setIcon(false); } catch (PropertyVetoException e2) { }
    }

    /**
     * Listens for mouse movements and acts on them.
     *
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    public class MouseInputHandler extends MouseInputAdapter
    {
	// _x & _y are the mousePressed location in absolute coordinate system
        int _x, _y;
	// __x & __y are the mousePressed location in source view's coordinate system
	int __x, __y;
        Rectangle startingBounds;

        public void mouseReleased(MouseEvent e) {
            _x = 0;
            _y = 0;
            __x = 0;
            __y = 0;
            startingBounds = null;

	    JDesktopPane d;
	    if((d = desktopIcon.getDesktopPane()) != null) {
	        DesktopManager dm = d.getDesktopManager();
		dm.endDraggingFrame(desktopIcon);
	    }

        }
                
        public void mousePressed(MouseEvent e) {
            Point p = SwingUtilities.convertPoint((Component)e.getSource(), 
                        e.getX(), e.getY(), null);
            __x = e.getX();
            __y = e.getY();
            _x = p.x;
            _y = p.y;
            startingBounds = desktopIcon.getBounds();

	    JDesktopPane d;
	    if((d = desktopIcon.getDesktopPane()) != null) {
	        DesktopManager dm = d.getDesktopManager();
		dm.beginDraggingFrame(desktopIcon);
	    }

            try { frame.setSelected(true); } catch (PropertyVetoException e1) { }
	    if(desktopIcon.getParent() instanceof JLayeredPane) {
		((JLayeredPane)desktopIcon.getParent()).moveToFront(desktopIcon);
 	    }

            if(e.getClickCount() > 1) {
		if(frame.isIconifiable() && frame.isIcon()) {
                    deiconize();
		}
            }

 	}

         public void mouseMoved(MouseEvent e) {}

         public void mouseDragged(MouseEvent e) {   
            Point p; 
	    int newX, newY, newW, newH;
            int deltaX;
            int deltaY;
	    Dimension min;
	    Dimension max;
            p = SwingUtilities.convertPoint((Component)e.getSource(), 
                                        e.getX(), e.getY(), null);
        
		Insets i = desktopIcon.getInsets();
		int pWidth, pHeight;
		pWidth = ((JComponent)desktopIcon.getParent()).getWidth();
		pHeight = ((JComponent)desktopIcon.getParent()).getHeight();
		
		if (startingBounds == null) {
		  // (STEVE) Yucky work around for bug ID 4106552
		    return;
		}
		newX = startingBounds.x - (_x - p.x);
		newY = startingBounds.y - (_y - p.y);
		// Make sure we stay in-bounds
		if(newX + i.left <= -__x)
		    newX = -__x - i.left;
		if(newY + i.top <= -__y)
		    newY = -__y - i.top;
		if(newX + __x + i.right > pWidth)
		    newX = pWidth - __x - i.right;
		if(newY + __y + i.bottom > pHeight)
		    newY =  pHeight - __y - i.bottom;
		
		JDesktopPane d;
		if((d = desktopIcon.getDesktopPane()) != null) {
		    DesktopManager dm = d.getDesktopManager();
		    dm.dragFrame(desktopIcon, newX, newY);
		} else {
		    moveAndRepaint(desktopIcon, newX, newY, 
				desktopIcon.getWidth(), desktopIcon.getHeight());
		}
		return;
	}

        public void moveAndRepaint(JComponent f, int newX, int newY, 
					int newWidth, int newHeight) {
	    Rectangle r = f.getBounds();
	    f.setBounds(newX, newY, newWidth, newHeight);		
	    SwingUtilities.computeUnion(newX, newY, newWidth, newHeight, r);
	    f.getParent().repaint(r.x, r.y, r.width, r.height);
        }	
    }; /// End MotionListener

}


