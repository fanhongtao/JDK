/*
 * @(#)SynthDesktopIconUI.java	1.8 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

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
 * Synth L&F for a minimized window on a desktop.
 *
 * @version 1.8, 01/23/03 (originally from version 1.30 of BasicDesktopIconUI)
 * @author David Kloba
 * @author Joshua Outwater
 * @author Rich Schiavi
 * @author Steve Wilson
 */
class SynthDesktopIconUI extends DesktopIconUI implements SynthUI {
    private SynthStyle style;

    protected JInternalFrame.JDesktopIcon desktopIcon;
    protected JInternalFrame frame;

    // This component is only used for windows look and feel, but it's package
    // private.  This should be protected. (joutwate 2/22/2001)
    protected JComponent iconPane;

    public static ComponentUI createUI(JComponent c)    {
        return new SynthDesktopIconUI();
    }

    public SynthDesktopIconUI() {
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
	uninstallDefaults();
	uninstallComponents();
	uninstallListeners();
	frame = null;
	desktopIcon = null;
    }

    protected void installComponents() {
	iconPane = new SynthInternalFrameTitlePane(frame);
	desktopIcon.setLayout(new BorderLayout());
	desktopIcon.add(iconPane, BorderLayout.CENTER);
    }

    protected void uninstallComponents() {
	desktopIcon.setLayout(null);
	desktopIcon.remove(iconPane);
        iconPane = null;
    }

    protected void installListeners() {
        EventHandler handler = createEventHandler();

	desktopIcon.addMouseMotionListener(handler);
	desktopIcon.addMouseListener(handler);
        desktopIcon.addPropertyChangeListener(handler);
    }

    protected void uninstallListeners() {
        EventHandler handler = getEventHandler(desktopIcon);

	desktopIcon.removeMouseMotionListener(handler);
	desktopIcon.removeMouseListener(handler);
        desktopIcon.removePropertyChangeListener(handler);
    }

    protected void installDefaults() {
        fetchStyle(desktopIcon);
    }

    private void fetchStyle(JComponent c) {
        SynthContext context = getContext(c, ENABLED);
        style = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
    }

    protected void uninstallDefaults() {
        SynthContext context = getContext(desktopIcon, ENABLED);
        style.uninstallDefaults(context);
        context.dispose();
        style = null;
    }

    EventHandler createEventHandler() {
        return new EventHandler();
    }
    
    public Dimension getPreferredSize(JComponent c) {
        return desktopIcon.getLayout().preferredLayoutSize(desktopIcon);
    }

    public Dimension getMinimumSize(JComponent c) {
        Dimension dim = new Dimension(iconPane.getMinimumSize());
        Border border = frame.getBorder();

        if (border != null) {
            dim.height += border.getBorderInsets(frame).bottom +
                          border.getBorderInsets(frame).top;
        }
        return dim;
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

    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    private SynthContext getContext(JComponent c, int state) {
        Region region = getRegion(c);
        return SynthContext.getContext(SynthContext.class, c, region,
                                       style, state);
    }

    private int getComponentState(JComponent c) {
        return SynthLookAndFeel.getComponentState(c);
    }

    Region getRegion(JComponent c) {
        return SynthLookAndFeel.getRegion(c);
    }

    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        paint(context, g);
        context.dispose();
    }

    public void paint(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        paint(context, g);
        context.dispose();
    }

    protected void paint(SynthContext context, Graphics g) {
        // PENDING: Josh, should this paint an icon?
    }

    private EventHandler getEventHandler(JComponent c) {
        return (EventHandler)SynthLookAndFeel.getSynthEventListener(c);
    }

    /**
     * Listens for mouse movements and acts on them.
     *
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    private class EventHandler extends MouseInputAdapter implements PropertyChangeListener, SynthEventListener {
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

        public void propertyChange(PropertyChangeEvent evt) {
            if (SynthLookAndFeel.shouldUpdateStyle(evt)) {
                fetchStyle((JInternalFrame.JDesktopIcon)evt.getSource());
            }
        }
    }; /// End MotionListener

}


