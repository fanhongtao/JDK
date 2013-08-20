/*
 * @(#)DefaultDesktopManager.java	1.52 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package javax.swing;

import java.awt.*;
import java.beans.PropertyVetoException;
import java.beans.PropertyChangeEvent;
import javax.swing.border.Border;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/** This is an implementation of the <code>DesktopManager</code>.
  * It currently implements the basic behaviors for managing
  * <code>JInternalFrame</code>s in an arbitrary parent.
  * <code>JInternalFrame</code>s that are not children of a
  * <code>JDesktop</code> will use this component
  * to handle their desktop-like actions.
  * <p>This class provides a policy for the various JInternalFrame methods,
  * it is not meant to be called directly rather the various JInternalFrame
  * methods will call into the DesktopManager.</p>
  * @see JDesktopPane
  * @see JInternalFrame
  * @version 1.52 12/19/03
  * @author David Kloba
  * @author Steve Wilson
  */
public class DefaultDesktopManager implements DesktopManager, java.io.Serializable {
    final static String HAS_BEEN_ICONIFIED_PROPERTY = "wasIconOnce";

    final static int DEFAULT_DRAG_MODE = 0;
    final static int OUTLINE_DRAG_MODE = 1;
    final static int FASTER_DRAG_MODE = 2;
  
    int dragMode = DEFAULT_DRAG_MODE;

    private transient Rectangle currentBounds = null;
    private transient Graphics desktopGraphics = null;
    private transient Rectangle desktopBounds = null;   
    private transient Rectangle[] floatingItems = {};


    /** Normally this method will not be called. If it is, it
      * try to determine the appropriate parent from the desktopIcon of the frame.
      * Will remove the desktopIcon from its parent if it successfully adds the frame.
      */
    public void openFrame(JInternalFrame f) {
        if(f.getDesktopIcon().getParent() != null) {
            f.getDesktopIcon().getParent().add(f);
            removeIconFor(f);
        }
    }

    /**
     * Removes the frame, and, if necessary, the
     * <code>desktopIcon</code>, from its parent.
     * @param f the <code>JInternalFrame</code> to be removed
     */
    public void closeFrame(JInternalFrame f) {
        boolean findNext = f.isSelected();
	Container c = f.getParent();
	if (findNext)
	    try { f.setSelected(false); } catch (PropertyVetoException e2) { }
        if(c != null) {
            c.remove(f);
            c.repaint(f.getX(), f.getY(), f.getWidth(), f.getHeight());
        }
        removeIconFor(f);
        if(f.getNormalBounds() != null)
            f.setNormalBounds(null);
        if(wasIcon(f))
            setWasIcon(f, null);
	if (findNext) activateNextFrame(c);
    }

    /**
     * Resizes the frame to fill its parents bounds.
     * @param f the frame to be resized
     */
    public void maximizeFrame(JInternalFrame f) {
        if (f.isIcon()) {
            try {
                // In turn calls deiconifyFrame in the desktop manager.
                // That method will handle the maximization of the frame.
                f.setIcon(false);
            } catch (PropertyVetoException e2) {
            }
        } else {
            f.setNormalBounds(f.getBounds());
            Rectangle desktopBounds = f.getParent().getBounds();
            setBoundsForFrame(f, 0, 0,
                desktopBounds.width, desktopBounds.height);
        }

        // Set the maximized frame as selected.
        try {
            f.setSelected(true);
        } catch (PropertyVetoException e2) {
        }
    }

    /**
     * Restores the frame back to its size and position prior
     * to a <code>maximizeFrame</code> call.
     * @param f the <code>JInternalFrame</code> to be restored
     */
    public void minimizeFrame(JInternalFrame f) {
        // If the frame was an icon restore it back to an icon.
        if (f.isIcon()) {
            iconifyFrame(f);
            return;
        }

        if ((f.getNormalBounds()) != null) {
            Rectangle r = f.getNormalBounds();
	    f.setNormalBounds(null);
            try { f.setSelected(true); } catch (PropertyVetoException e2) { }
            setBoundsForFrame(f, r.x, r.y, r.width, r.height);
        }
    }

    /**
     * Removes the frame from its parent and adds its
     * <code>desktopIcon</code> to the parent.
     * @param f the <code>JInternalFrame</code> to be iconified
     */
    public void iconifyFrame(JInternalFrame f) {
        JInternalFrame.JDesktopIcon desktopIcon;
        Container c = f.getParent();
	JDesktopPane d = f.getDesktopPane();
	boolean findNext = f.isSelected();

        desktopIcon = f.getDesktopIcon();
        if(!wasIcon(f)) {
            Rectangle r = getBoundsForIconOf(f);
            desktopIcon.setBounds(r.x, r.y, r.width, r.height);
            setWasIcon(f, Boolean.TRUE);
        }

	if (c == null) {
            return;
        }

	if (c instanceof JLayeredPane) {
	    JLayeredPane lp = (JLayeredPane)c;
	    int layer = lp.getLayer(f);
	    lp.putLayer(desktopIcon, layer);
	}

        // If we are maximized we already have the normal bounds recorded
        // don't try to re-record them, otherwise we incorrectly set the
        // normal bounds to maximized state.
        if (!f.isMaximum()) {
            f.setNormalBounds(f.getBounds());
        }
        c.remove(f);
        c.add(desktopIcon);
        c.repaint(f.getX(), f.getY(), f.getWidth(), f.getHeight());
        try {
            f.setSelected(false);
        } catch (PropertyVetoException e2) {
        }

	// Get topmost of the remaining frames
	if (findNext) {
	  activateNextFrame(c);
	}
    }

    void activateNextFrame(Container c) {
      int i;
      JInternalFrame nextFrame = null;
      if (c == null) return;
      for (i = 0; i < c.getComponentCount(); i++) {
	if (c.getComponent(i) instanceof JInternalFrame) {
	  nextFrame = (JInternalFrame) c.getComponent(i);
	  break;
	}
      }
      if (nextFrame != null) {
	try { nextFrame.setSelected(true); }
	catch (PropertyVetoException e2) { }
	nextFrame.moveToFront();
      }
      
    }

    /**
     * Removes the desktopIcon from its parent and adds its frame
     * to the parent.
     * @param f the <code>JInternalFrame</code> to be de-iconified
     */
    public void deiconifyFrame(JInternalFrame f) {
        JInternalFrame.JDesktopIcon desktopIcon = f.getDesktopIcon();
        Container c = desktopIcon.getParent();
        if (c != null) {
            c.add(f);
            // If the frame is to be restored to a maximized state make
            // sure it still fills the whole desktop.
            if (f.isMaximum()) {
                Rectangle desktopBounds = c.getBounds();
                if (f.getWidth() != desktopBounds.width ||
                        f.getHeight() != desktopBounds.height) {
                    setBoundsForFrame(f, 0, 0,
                        desktopBounds.width, desktopBounds.height);
                }
            }
            removeIconFor(f);
            if (f.isSelected()) {
                f.moveToFront();
            } else {
                try {
                    f.setSelected(true);
                } catch (PropertyVetoException e2) {
                }
            }
        }
    }

    /** This will activate <b>f</b> moving it to the front. It will
      * set the current active frame's (if any)
      * <code>IS_SELECTED_PROPERTY</code> to <code>false</code>.
      * There can be only one active frame across all Layers.
      * @param f the <code>JInternalFrame</code> to be activated
      */
    public void activateFrame(JInternalFrame f) {
        Container p = f.getParent();
        Component[] c;
	JDesktopPane d = f.getDesktopPane();
	JInternalFrame currentlyActiveFrame =
	  (d == null) ? null : d.getSelectedFrame();
	// fix for bug: 4162443
        if(p == null) {
            // If the frame is not in parent, its icon maybe, check it
            p = f.getDesktopIcon().getParent();
            if(p == null)
                return;
        }
	// we only need to keep track of the currentActive InternalFrame, if any
	if (currentlyActiveFrame == null){
	  if (d != null) { d.setSelectedFrame(f);}
	} else if (currentlyActiveFrame != f) {  
	  // if not the same frame as the current active
	  // we deactivate the current 
	  if (currentlyActiveFrame.isSelected()) { 
	    try {
	      currentlyActiveFrame.setSelected(false);
	    }
	    catch(PropertyVetoException e2) {}
	  }
	  if (d != null) { d.setSelectedFrame(f);}
	}
	f.moveToFront();
    }
    
    // implements javax.swing.DesktopManager
    public void deactivateFrame(JInternalFrame f) {
      JDesktopPane d = f.getDesktopPane();
      JInternalFrame currentlyActiveFrame =
	  (d == null) ? null : d.getSelectedFrame();
      if (currentlyActiveFrame == f)
	d.setSelectedFrame(null);
    }

    // implements javax.swing.DesktopManager
    public void beginDraggingFrame(JComponent f) {
        setupDragMode(f);

	if (dragMode == FASTER_DRAG_MODE) {
	  floatingItems = findFloatingItems(f);
	  currentBounds = f.getBounds();
	  desktopBounds = f.getParent().getBounds();
	  desktopBounds.x = 0;
	  desktopBounds.y = 0;
	  desktopGraphics = f.getParent().getGraphics();
	  ((JInternalFrame)f).isDragging = true;
	}
	
    }

    private void setupDragMode(JComponent f) {
        JDesktopPane p = getDesktopPane(f);
        if (p != null) {
            String mode = (String)p.getClientProperty("JDesktopPane.dragMode");
            if (mode != null && mode.equals("outline")) {
                dragMode = OUTLINE_DRAG_MODE;
            } else if (mode != null && mode.equals("faster")
                    && f instanceof JInternalFrame
                    && ((JInternalFrame)f).isOpaque()) {
                dragMode = FASTER_DRAG_MODE;
            } else {
                if (p.getDragMode() == JDesktopPane.OUTLINE_DRAG_MODE ) {
                    dragMode = OUTLINE_DRAG_MODE;
                } else if ( p.getDragMode() == JDesktopPane.LIVE_DRAG_MODE
                        && f instanceof JInternalFrame
                        && ((JInternalFrame)f).isOpaque()) {
                    dragMode = FASTER_DRAG_MODE;
                } else {
                    dragMode = DEFAULT_DRAG_MODE;
                }
            }
        }
    }

    private transient Point currentLoc = null;

    /** 
      * Moves the visible location of the frame being dragged
      * to the location specified.  The means by which this occurs can vary depending 
      * on the dragging algorithm being used.  The actual logical location of the frame
      * might not change until <code>endDraggingFrame</code> is called.
      */
    public void dragFrame(JComponent f, int newX, int newY) {

        if (dragMode == OUTLINE_DRAG_MODE) {
	    JDesktopPane desktopPane = getDesktopPane(f);
	    if (desktopPane != null){
	      Graphics g = desktopPane.getGraphics();

	      g.setXORMode(Color.white);
	      if (currentLoc != null) {
	        g.drawRect(currentLoc.x, currentLoc.y,
                        f.getWidth()-1, f.getHeight()-1);
	      }
	      g.drawRect( newX, newY, f.getWidth()-1, f.getHeight()-1);
	      currentLoc = new Point (newX, newY);
	      g.dispose();
	    }
	} else if (dragMode == FASTER_DRAG_MODE) {
	  dragFrameFaster(f, newX, newY);
	} else {
	    setBoundsForFrame(f, newX, newY, f.getWidth(), f.getHeight());
	}
    }

    // implements javax.swing.DesktopManager
    public void endDraggingFrame(JComponent f) {
        if ( dragMode == OUTLINE_DRAG_MODE && currentLoc != null) {
	    setBoundsForFrame(f, currentLoc.x, currentLoc.y, f.getWidth(), f.getHeight() );
	    currentLoc = null;
	} else if (dragMode == FASTER_DRAG_MODE) { 
	    currentBounds = null;
	    if (desktopGraphics != null) {
	        desktopGraphics.dispose();
	        desktopGraphics = null;
	    }
	    desktopBounds = null;
	    ((JInternalFrame)f).isDragging = false;
	}
    }

    // implements javax.swing.DesktopManager
    public void beginResizingFrame(JComponent f, int direction) {
        setupDragMode(f);
    }

    /**
     * Calls <code>setBoundsForFrame</code> with the new values.
     * @param f the component to be resized
     * @param newX the new x-coordinate
     * @param newY the new y-coordinate
     * @param newWidth the new width
     * @param newHeight the new height
     */
    public void resizeFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {

        if ( dragMode == DEFAULT_DRAG_MODE || dragMode == FASTER_DRAG_MODE ) {
	    setBoundsForFrame(f, newX, newY, newWidth, newHeight);
	} else {
	    JDesktopPane desktopPane = getDesktopPane(f);
	    if (desktopPane != null){
	      Graphics g = desktopPane.getGraphics();

	      g.setXORMode(Color.white);
	      if (currentBounds != null) {
	        g.drawRect( currentBounds.x, currentBounds.y, currentBounds.width-1, currentBounds.height-1);
	      }
	      g.drawRect( newX, newY, newWidth-1, newHeight-1);
	      currentBounds = new Rectangle (newX, newY, newWidth, newHeight);
	      g.setPaintMode();
	      g.dispose();
	    }
	}

    }

    // implements javax.swing.DesktopManager
    public void endResizingFrame(JComponent f) {
        if ( dragMode == OUTLINE_DRAG_MODE && currentBounds != null) {
	    setBoundsForFrame(f, currentBounds.x, currentBounds.y, currentBounds.width, currentBounds.height );
	    currentBounds = null;
	}
    }


    /** This moves the <code>JComponent</code> and repaints the damaged areas. */
    public void setBoundsForFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
        boolean didResize = (f.getWidth() != newWidth || f.getHeight() != newHeight);
        f.setBounds(newX, newY, newWidth, newHeight);
        if(didResize) {
            f.validate();
        } 
    }

    /** Convenience method to remove the desktopIcon of <b>f</b> is necessary. */
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
      //
      // Get the icon for this internal frame and its preferred size
      //

      JInternalFrame.JDesktopIcon icon = f.getDesktopIcon();
      Dimension prefSize = icon.getPreferredSize();
      //
      // Get the parent bounds and child components.
      //

      Container c = f.getParent();
      if (c == null) {
	  c = f.getDesktopIcon().getParent();
      }

      if (c == null) {
	/* the frame has not yet been added to the parent; how about (0,0) ?*/
	return new Rectangle(0, 0, prefSize.width, prefSize.height);
      }
	
      Rectangle parentBounds = c.getBounds();
      Component [] components = c.getComponents();


      //
      // Iterate through valid default icon locations and return the
      // first one that does not intersect any other icons.
      //

      Rectangle availableRectangle = null;
      JInternalFrame.JDesktopIcon currentIcon = null;

      int x = 0;
      int y = parentBounds.height - prefSize.height;
      int w = prefSize.width;
      int h = prefSize.height;

      boolean found = false;

      while (!found) {

	availableRectangle = new Rectangle(x,y,w,h);

	found = true;

	for ( int i=0; i<components.length; i++ ) {

	  //
	  // Get the icon for this component
	  //

	  if ( components[i] instanceof JInternalFrame ) {
	    currentIcon = ((JInternalFrame)components[i]).getDesktopIcon();
	  }
	  else if ( components[i] instanceof JInternalFrame.JDesktopIcon ){
	    currentIcon = (JInternalFrame.JDesktopIcon)components[i];
	  } else
	    /* found a child that's neither an internal frame nor
	       an icon. I don't believe this should happen, but at
	       present it does and causes a null pointer exception.
	       Even when that gets fixed, this code protects against
	       the npe. hania */
	    continue;
	  
	  //
	  // If this icon intersects the current location, get next location.
	  //

	  if ( !currentIcon.equals(icon) ) {
	    if ( availableRectangle.intersects(currentIcon.getBounds()) ) {
	      found = false;
	      break;
	    }
	  }
	}

	if (currentIcon == null)
	  /* didn't find any useful children above. This probably shouldn't
	   happen, but this check protects against an npe if it ever does
	   (and it's happening now) */
	  return availableRectangle;

	x += currentIcon.getBounds().width;

	if ( x + w > parentBounds.width ) {
	  x = 0;
	  y -= h;
	}
      }
	    
      return(availableRectangle);
    }

    /**
     * Stores the bounds of the component just before a maximize call.
     * @param f the component about to be resized
     * @param r the normal bounds to be saved away
     */
    protected void setPreviousBounds(JInternalFrame f, Rectangle r)     {
	f.setNormalBounds(r);
    }

    /**
     * Gets the normal bounds of the component prior to the component
     * being maximized.
     * @param f the <code>JInternalFrame</code> of interest
     * @return the normal bounds of the component
     */
    protected Rectangle getPreviousBounds(JInternalFrame f)     {
        return f.getNormalBounds();
    }

    /**
     * Sets that the component has been iconized and the bounds of the
     * <code>desktopIcon</code> are valid.
     */
    protected void setWasIcon(JInternalFrame f, Boolean value)  {
	if (value != null) {
	    f.putClientProperty(HAS_BEEN_ICONIFIED_PROPERTY, value);
	}
    }

    /**
     * Returns <code>true</code> if the component has been iconized
     * and the bounds of the <code>desktopIcon</code> are valid,
     * otherwise returns <code>false</code>.
     *
     * @param f the <code>JInternalFrame</code> of interest
     * @return <code>true</code> if the component has been iconized;
     *    otherwise returns <code>false</code>
     */
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


  // =========== stuff for faster frame dragging ===================

   private void dragFrameFaster(JComponent f, int newX, int newY) {

      Rectangle previousBounds = new Rectangle(currentBounds.x,
                                               currentBounds.y,
                                               currentBounds.width,
                                               currentBounds.height);

   // move the frame
      currentBounds.x = newX;
      currentBounds.y = newY;
   
      emergencyCleanup(f);
      
      boolean floaterCollision = isFloaterCollision(previousBounds, currentBounds);
  
    // System.out.println(previousBounds);
      Rectangle visBounds = previousBounds.intersection(desktopBounds);
    //  System.out.println(previousBounds);
 

     // System.out.println(visBounds);

      if(!floaterCollision) {
      // blit the frame to the new location
	// if we're under a floater we can't blit	
      desktopGraphics.copyArea(visBounds.x,
                               visBounds.y,
                               visBounds.width,
                               visBounds.height,
                               newX - previousBounds.x,
                               newY - previousBounds.y);   
      }
 
      JComponent parent = (JComponent)f.getParent();
        
      f.setBounds(currentBounds);

      if(floaterCollision) {
	// since we couldn't blit we just redraw as fast as possible
        // the isDragging mucking is to avoid activating emergency cleanup
        ((JInternalFrame)f).isDragging = false;
	parent.paintImmediately(currentBounds);
        ((JInternalFrame)f).isDragging = true;
      }

      // fake out the repaint manager.  We'll take care of everything
      RepaintManager currentManager = RepaintManager.currentManager(f); 

      currentManager.markCompletelyClean(parent);
      currentManager.markCompletelyClean(f);

      // compute the minimal newly exposed area
      // if the rects intersect then we use computeDifference.  Otherwise
      // we'll repaint the entire previous bounds
      Rectangle[] dirtyRects = null;
      if ( previousBounds.intersects(currentBounds) ) {
	  dirtyRects = SwingUtilities.computeDifference(previousBounds, currentBounds);
      } else {
          dirtyRects = new Rectangle[1];
	  dirtyRects[0] = previousBounds;
	  //  System.out.println("no intersection");
      };

      // Fix the damage
      for (int i = 0; i < dirtyRects.length; i++) {
         parent.paintImmediately(dirtyRects[i]);
      }

      // new areas of blit were exposed
      if ( !(visBounds.equals(previousBounds)) ) {
         dirtyRects = SwingUtilities.computeDifference(previousBounds, desktopBounds);
         for (int i = 0; i < dirtyRects.length; i++) {
            dirtyRects[i].x += newX - previousBounds.x;
            dirtyRects[i].y += newY - previousBounds.y;
            ((JInternalFrame)f).isDragging = false;
            
            parent.paintImmediately(dirtyRects[i]);
            ((JInternalFrame)f).isDragging = true;
            
           // System.out.println(dirtyRects[i]);
         }
   
      }     
   }

   private boolean isFloaterCollision(Rectangle moveFrom, Rectangle moveTo) {
      if (floatingItems.length == 0) {
        // System.out.println("no floaters");
         return false;
      }

      for (int i = 0; i < floatingItems.length; i++) {
         boolean intersectsFrom = moveFrom.intersects(floatingItems[i]);         
         if (intersectsFrom) {
            return true;
         }
         boolean intersectsTo = moveTo.intersects(floatingItems[i]);
         if (intersectsTo) {
            return true;
         }
      }  

      return false;
   }

   private Rectangle[] findFloatingItems(JComponent f) {
      Container desktop = f.getParent();
      Component[] children = desktop.getComponents();
      int i = 0;
      for (i = 0; i < children.length; i++) {
         if (children[i] == f) {
            break;
         }
      }
      // System.out.println(i);
      Rectangle[] floaters = new Rectangle[i];
      for (i = 0; i < floaters.length; i++) {
         floaters[i] = children[i].getBounds();
      }

      return floaters;
   }

   /**
     * This method is here to clean up problems associated
     * with a race condition which can occur when the full contents
     * of a copyArea's source argument is not available onscreen.
     * This uses brute force to clean up in case of possible damage
     */
   private void emergencyCleanup(final JComponent f) {

        if ( ((JInternalFrame)f).danger ) {
           
           SwingUtilities.invokeLater( new Runnable(){
                                       public void run(){
  
                                       ((JInternalFrame)f).isDragging = false;
                                       f.paintImmediately(0,0, 
                                                          f.getWidth(),
                                                          f.getHeight());
                                        
                                        //finalFrame.repaint();
                                        ((JInternalFrame)f).isDragging = true;
                                        // System.out.println("repair complete");
                                       }});     
                                       
             ((JInternalFrame)f).danger = false;        
        }
    
   }


}

