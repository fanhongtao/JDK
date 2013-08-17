/*
 * @(#)DefaultDesktopManager.java	1.18 98/08/26
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */


package javax.swing;

import java.awt.*;
import java.beans.PropertyVetoException;
import java.beans.PropertyChangeEvent;
import javax.swing.border.Border;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/** This is an implementaion of the DesktopManager. It currently implements a
  * the basic behaviors for managing JInternalFrames in an arbitrary parent.
  * JInternalFrames that are not children of a JDesktop will use this component
  * to handle their desktop-like actions.
  * @see JDesktopPane
  * @see JInternalFrame
  * @version 1.18 08/26/98
  * @author David Kloba
  */
public class DefaultDesktopManager implements DesktopManager, java.io.Serializable {
    final static String PREVIOUS_BOUNDS_PROPERTY = "previousBounds";
    final static String HAS_BEEN_ICONIFIED_PROPERTY = "wasIconOnce";

    final static int DEFAULT_DRAG_MODE = 0;
    final static int OUTLINE_DRAG_MODE = 1;
  
    int dragMode = DEFAULT_DRAG_MODE;

    private static JInternalFrame currentActiveFrame = null;

    /** Normally this method will not be called. If it is, it
      * try to determine the appropriate parent from the desktopIcon of the frame.
      * Will remove the desktopIcon from it's parent if it successfully adds the frame.
      */
    public void openFrame(JInternalFrame f) {
        if(f.getDesktopIcon().getParent() != null) {
            f.getDesktopIcon().getParent().add(f);
            removeIconFor(f);
        }
    }

    /** Removes the frame, and if necessary the desktopIcon, from it's parent. */
    public void closeFrame(JInternalFrame f) {
        if(f.getParent() != null) {
            Container c = f.getParent();
            c.remove(f);
            c.repaint(f.getX(), f.getY(), f.getWidth(), f.getHeight());
        }
        removeIconFor(f);
        if(getPreviousBounds(f) != null)
            setPreviousBounds(f, null);
        if(wasIcon(f))
            setWasIcon(f, null);
    }

    /** Resizes the frame to fill it's parents bounds. */
    public void maximizeFrame(JInternalFrame f) {

        Rectangle p;
        if(!f.isIcon()) {
        setPreviousBounds(f, f.getBounds());
            p = f.getParent().getBounds();
        } else {
             Container c = f.getDesktopIcon().getParent();
            if(c == null)
                return;
            p = c.getBounds();
            try { f.setIcon(false); } catch (PropertyVetoException e2) { }
        }
        setBoundsForFrame(f, 0, 0, p.width, p.height);
        try { f.setSelected(true); } catch (PropertyVetoException e2) { }

        removeIconFor(f);
    }

    /** Restores the frame back to it's size and position prior to a maximizeFrame()
      * call.
      */
    public void minimizeFrame(JInternalFrame f) {
        if(getPreviousBounds(f) != null) {
            Rectangle r = getPreviousBounds(f);
            setPreviousBounds(f, null);
            try { f.setSelected(true); } catch (PropertyVetoException e2) { }
            if(f.isIcon())
                try { f.setIcon(false); } catch (PropertyVetoException e2) { }
            setBoundsForFrame(f, r.x, r.y, r.width, r.height);
        }
        removeIconFor(f);
    }

    /** Removes the frame from it's parent and adds it's desktopIcon to the parent. */
    public void iconifyFrame(JInternalFrame f) {
        JInternalFrame.JDesktopIcon desktopIcon;
        Container c;

        desktopIcon = f.getDesktopIcon();
        if(!wasIcon(f)) {
            Rectangle r = getBoundsForIconOf(f);
            desktopIcon.setBounds(r.x, r.y, r.width, r.height);
            setWasIcon(f, Boolean.TRUE);
        }

        c = f.getParent();
        c.remove(f);
        c.add(desktopIcon);
        c.repaint(f.getX(), f.getY(), f.getWidth(), f.getHeight());
        try { f.setSelected(false); } catch (PropertyVetoException e2) { }
    }

    /** Removes the desktopIcon from it's parent and adds it's frame to the parent. */
    public void deiconifyFrame(JInternalFrame f) {
        JInternalFrame.JDesktopIcon desktopIcon;
        Dimension size;

        desktopIcon = f.getDesktopIcon();
        if(desktopIcon.getParent() != null) {
            desktopIcon.getParent().add(f);
            removeIconFor(f);
            try { f.setSelected(true); } catch (PropertyVetoException e2) { }
        }
    }

    /** This will activate <b>f</b> moving it to the front. It will
      * set the current active frame (if any) IS_SELECTED_PROPERTY to false.
      * There can be only one active frame across all Layers.
      */
    public void activateFrame(JInternalFrame f) {
        Container p = f.getParent();
        Component[] c;

	// fix for bug: 4162443
        if(p == null) {
            // If the frame is not in parent, it's icon maybe, check it
            p = f.getDesktopIcon().getParent();
            if(p == null)
                return;
        }
	// we only need to keep track of the currentActive InternalFrame, if any
	if (currentActiveFrame == null){
	  currentActiveFrame = f;
	}
	else if (currentActiveFrame != f) {  
	  // if not the same frame as the current active
	  // we deactivate the current 
	  if (currentActiveFrame.isSelected()) { 
	    try {
	      currentActiveFrame.setSelected(false);
	    }
	    catch(PropertyVetoException e2) {}
	  }
	  currentActiveFrame = f;
	}
	f.moveToFront();
    }
    
    // implements javax.swing.DesktopManager
    public void deactivateFrame(JInternalFrame f) {
      if (currentActiveFrame == f)
	currentActiveFrame = null;
    }

    // implements javax.swing.DesktopManager
    public void beginDraggingFrame(JComponent f) {
        JDesktopPane p = getDesktopPane(f);
	String mode = (String)p.getClientProperty("JDesktopPane.dragMode");
	if (mode == null) {
	    dragMode = DEFAULT_DRAG_MODE;
	} else if (mode.equals("outline")) {
	    dragMode = OUTLINE_DRAG_MODE;
	}
    }

    transient Point currentLoc = null;

    /** Calls setBoundsForFrame() with the new values. */
    public void dragFrame(JComponent f, int newX, int newY) {
        if ( dragMode == DEFAULT_DRAG_MODE ) {
	    setBoundsForFrame(f, newX, newY, f.getWidth(), f.getHeight());
	} else {
	    JDesktopPane desktopPane = getDesktopPane(f);
	    Graphics g = desktopPane.getGraphics();

	    g.setXORMode(Color.white);
	    if (currentLoc != null) {
	        g.drawRect( currentLoc.x, currentLoc.y, f.getWidth()-1, f.getHeight()-1);
	    }
	    g.drawRect( newX, newY, f.getWidth()-1, f.getHeight()-1);
	    currentLoc = new Point (newX, newY);
	    g.setPaintMode();
	}
    }

    // implements javax.swing.DesktopManager
    public void endDraggingFrame(JComponent f) {
        if ( dragMode == OUTLINE_DRAG_MODE && currentLoc != null) {
	    setBoundsForFrame(f, currentLoc.x, currentLoc.y, f.getWidth(), f.getHeight() );
	    currentLoc = null;
	}
    }

    // implements javax.swing.DesktopManager
    public void beginResizingFrame(JComponent f, int direction) {}

    /** Calls setBoundsForFrame() with the new values. */
    public void resizeFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
        setBoundsForFrame(f, newX, newY, newWidth, newHeight);
    }

    // implements javax.swing.DesktopManager
    public void endResizingFrame(JComponent f) {}

    // PENDING(klobad) this should be optimized
    /** This moves the JComponent and repaints the damaged areas. */
    public void setBoundsForFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
        boolean didResize = (f.getWidth() != newWidth || f.getHeight() != newHeight);
        f.setBounds(newX, newY, newWidth, newHeight);
        if(didResize) {
            f.validate();
        }
    }

    /** Convience method to remove the desktopIcon of <b>f</b> is necessary. */
    protected void removeIconFor(JInternalFrame f) {
        JInternalFrame.JDesktopIcon di = f.getDesktopIcon();
        Container c = di.getParent();
        if(c != null) {
            c.remove(di);
            c.repaint(di.getX(), di.getY(), di.getWidth(), di.getHeight());
        }
    }

    /** The iconifyFrame() code calls this to determine the proper bounds
      * for the desktopIcon.
      */
    protected Rectangle getBoundsForIconOf(JInternalFrame f) {
        Rectangle p = f.getParent().getBounds();
        Dimension prefSize = f.getDesktopIcon().getPreferredSize();
        int x2, y2, w2, h2;

        w2 = prefSize.width;
        h2 = prefSize.height;
        x2 = 0;
        y2 = p.height - h2;

        if(f.getParent() == null && f.getDesktopIcon().getParent() == null) {
            return new Rectangle(x2, y2, w2, h2);
        }

        Container lp = (Container)f.getParent();
        if(lp == null)
            lp = f.getDesktopIcon().getParent();

        int pos = lp.getComponentCount();
        Component c[] = lp.getComponents();
        Rectangle b;
        int i, x3 = 0, y3 = p.height;
        JInternalFrame next;
        for(i = pos - 1; i >=0; i--)    {
            next = null;
            if(lp.getComponent(i) instanceof JInternalFrame)
                next = (JInternalFrame)lp.getComponent(i);
            else if(lp.getComponent(i) instanceof JInternalFrame.JDesktopIcon)
                next = ((JInternalFrame.JDesktopIcon)lp.getComponent(i)).getInternalFrame();
            if(next != null) {
                if(next != f && wasIcon(next)) {
                    b = next.getDesktopIcon().getBounds();
                    if(b.y < y3) {
                        y3 = b.y;
                        x3 = b.x + b.width;
                    } else if(b.y == y3) {
                        if(b.x + b.width > x3)
                            x3 = b.x + b.width;
                    }
                }
            }
        }

        if(y3 != p.height)
            y2 = y3;
        if(x3 + w2 > p.width) {
            y2 -= h2;
            x2 = 0;
        } else {
            x2 = x3;
        }

        return new Rectangle(x2, y2, w2, h2);
    }

    /** Stores the bounds of the component just before a maximize call. */
    protected void setPreviousBounds(JInternalFrame f, Rectangle r)     {
	if (r != null) {
	    f.putClientProperty(PREVIOUS_BOUNDS_PROPERTY, r);
	}
    }

    protected Rectangle getPreviousBounds(JInternalFrame f)     {
        return (Rectangle)f.getClientProperty(PREVIOUS_BOUNDS_PROPERTY);
    }

    /** Sets that the component has been iconized and the bounds of the
      * desktopIcon are valid.
      */
    protected void setWasIcon(JInternalFrame f, Boolean value)  {
	if (value != null) {
	    f.putClientProperty(HAS_BEEN_ICONIFIED_PROPERTY, value);
	}
    }

    protected boolean wasIcon(JInternalFrame f) {
        return (f.getClientProperty(HAS_BEEN_ICONIFIED_PROPERTY) == Boolean.TRUE);
    }


    JDesktopPane getDesktopPane( JComponent frame ) {
        JDesktopPane pane = null;
	Component c = frame.getParent();

        // Find the JDesktopPane
        while ( pane == null ) {
	    if ( c instanceof JDesktopPane ) {
	        pane = (JDesktopPane)c;
	    }
	    else if ( c == null ) {
	        break;
	    }
	    else {
	        c = c.getParent();
	    }
	}

	return pane;
    }

}

