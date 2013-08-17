/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;


import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.applet.*;


/**
 * This class manages repaint requests, allowing the number
 * of repaints to be minimized, for example by collapsing multiple 
 * requests into a single repaint for members of a component tree.
 *
 * @version 1.42 12/02/02
 * @author Arnaud Weber
 */
public class RepaintManager 
{
    Hashtable dirtyComponents = new Hashtable();
    Hashtable tmpDirtyComponents = new Hashtable();
    Vector    invalidComponents;
    boolean   doubleBufferingEnabled = true;
    Image     doubleBuffer;
    Dimension doubleBufferSize;
    private Dimension doubleBufferMaxSize;
    /** This is set to true from resetDoubleBuffer, if this is true, the
     * next time the doubleBuffer is asked for (getOffscreenBuffer) the image
     * will be recreated. */
    private boolean resetDoubleBuffer;

    private static final Object repaintManagerKey = RepaintManager.class;

    /** 
     * Return the RepaintManager for the calling thread given a Component.
     * 
     * @param c a Component -- unused in the default implementation, but could
     *          be used by an overridden version to return a different RepaintManager
     *          depending on the Component
     * @return the RepaintManager object
     */
    public static RepaintManager currentManager(Component c) {
        RepaintManager result = (RepaintManager) SwingUtilities.appContextGet(repaintManagerKey);
        if(result == null) {
            result = new RepaintManager();
            SwingUtilities.appContextPut(repaintManagerKey, result);
        }
	return result;
    }

    
    /**
     * Return the RepaintManager for the calling thread given a JComponent.
     * <p>
     * Note: This method exists for backward binary compatibility with earlier
     * versions of the Swing library. It simply returns the result returned by
     * {@link #currentManager(Component)}. 
     *
     * @param c a JComponent -- unused
     * @return the RepaintManager object
     */
    public static RepaintManager currentManager(JComponent c) {
	return currentManager((Component)c);
    }


    /**
     * Set the RepaintManager that should be used for the calling 
     * thread. <b>aRepaintManager</b> will become the current RepaintManager
     * for the calling thread's thread group.
     * @param aRepaintManager  the RepaintManager object to use
     */
    public static void setCurrentManager(RepaintManager aRepaintManager) {
        if (aRepaintManager != null) {
            SwingUtilities.appContextPut(repaintManagerKey, aRepaintManager);
        } else {
            SwingUtilities.appContextRemove(repaintManagerKey);
        }
    }

    /** 
     * Create a new RepaintManager instance. You rarely call this constructor.
     * directly. To get the default RepaintManager, use 
     * RepaintManager.currentManager(JComponent) (normally "this").
     */
    public RepaintManager() {
    }


    /**
     * Mark the component as in need of layout and queue a runnable
     * for the event dispatching thread that will validate the components
     * first isValidateRoot() ancestor. 
     * 
     * @see JComponent#isValidateRoot
     * @see #removeInvalidComponent
     */
    public synchronized void addInvalidComponent(JComponent invalidComponent) 
    {
        Component validateRoot = null;

	/* Find the first JComponent ancestor of this component whose
	 * isValidateRoot() method returns true.  
	 */
        for(Component c = invalidComponent; c != null; c = c.getParent()) {
	    if ((c instanceof CellRendererPane) || (c.getPeer() == null)) {
		return;
	    }
	    if ((c instanceof JComponent) && (((JComponent)c).isValidateRoot())) {
		validateRoot = c;
		break;
	    }
	}
        
	/* There's no validateRoot to apply validate to, so we're done.
	 */
	if (validateRoot == null) {
	    return;
	}

	/* If the validateRoot and all of its ancestors aren't visible
	 * then we don't do anything.  While we're walking up the tree
	 * we find the root Window or Applet.
	 */
	Component root = null;
	
	for(Component c = validateRoot; c != null; c = c.getParent()) {
	    if (!c.isVisible() || (c.getPeer() == null)) {
		return;
	    }
	    if ((c instanceof Window) || (c instanceof Applet)) {
		root = c;
		break;
	    }
	}

	if (root == null) {
	    return;
	}
	   
	/* Lazily create the invalidateComponents vector and add the
	 * validateRoot if it's not there already.  If this validateRoot
	 * is already in the vector, we're done.
	 */
	if (invalidComponents == null) {
	    invalidComponents = new Vector();
	}
	else {
	    int n = invalidComponents.size();
	    for(int i = 0; i < n; i++) {
		if(validateRoot == (Component)(invalidComponents.elementAt(i))) {
		    return;
		}
	    }
	}
	invalidComponents.addElement(validateRoot);

	/* Queues a Runnable that calls RepaintManager.validateInvalidComponents() 
	 * and RepaintManager.paintDirtyRegions() with SwingUtilities.invokeLater().
	 */
	SystemEventQueueUtilities.queueComponentWorkRequest(root);
    }


    /** 
     * Remove a component from the list of invalid components.
     * 
     * @see #addInvalidComponent
     */
    public synchronized void removeInvalidComponent(JComponent component) {
        if(invalidComponents != null) {
            int index = invalidComponents.indexOf(component);
            if(index != -1) {
                invalidComponents.removeElementAt(index);
            }
        }
    }


    /** 
     * Add a component in the list of components that should be refreshed.
     * If <i>c</i> already has a dirty region, the rectangle <i>(x,y,w,h)</i> 
     * will be unioned with the region that should be redrawn. 
     * 
     * @see JComponent#repaint
     */
    public synchronized void addDirtyRegion(JComponent c, int x, int y, int w, int h) 
    {
	/* Special cases we don't have to bother with.
	 */
        if ((w <= 0) || (h <= 0) || (c == null)) {
            return;
        }

	if ((c.getWidth() <= 0) || (c.getHeight() <= 0)) {
	    return;
	}

	Rectangle r = (Rectangle)dirtyComponents.get(c);
	if (r != null) {
	    // A non-null r implies c is already marked as dirty,
	    // and that the parent is valid. Therefore we can
	    // just union the rect and bail.
	    SwingUtilities.computeUnion(x, y, w, h, r);
	    return;
	}

	/* Make sure that c and all it ancestors (up to an Applet or
	 * Window) are visible.  This loop has the same effect as 
	 * checking c.isShowing() (and note that it's still possible 
	 * that c is completely obscured by an opaque ancestor in 
	 * the specified rectangle).
	 */
	Component root = null;

	for (Container p = c; p != null; p = p.getParent()) {
	    if (!p.isVisible() || (p.getPeer() == null)) {
		return;
	    }
	    if ((p instanceof Window) || (p instanceof Applet)) {
                // Iconified frames are still visible!
                if (p instanceof Frame &&
                        ((Frame)p).getState() == Frame.ICONIFIED) {
                    return;
                }
		root = p;
		break;
	    }
	}

	if (root == null) return;

	dirtyComponents.put(c, new Rectangle(x, y, w, h));


	/* Queues a Runnable that calls validateInvalidComponents() and
	 * rm.paintDirtyRegions() with SwingUtilities.invokeLater().
	 */
	SystemEventQueueUtilities.queueComponentWorkRequest(root);
    }
    

    /** Return the current dirty region for a component.
     *  Return an empty rectangle if the component is not
     *  dirty.
     */
    public Rectangle getDirtyRegion(JComponent aComponent) {
	Rectangle r = null;
	synchronized(this) {
	    r = (Rectangle)dirtyComponents.get(aComponent);
	}
	if(r == null)
	    return new Rectangle(0,0,0,0);
	else
	    return new Rectangle(r);
    }

    /** 
     * Mark a component completely dirty. <b>aComponent</b> will be
     * completely painted during the next paintDirtyRegions() call.
     */
    public void markCompletelyDirty(JComponent aComponent) {
	addDirtyRegion(aComponent,0,0,Integer.MAX_VALUE,Integer.MAX_VALUE);
    }
	    
    /** 
     * Mark a component completely clean. <b>aComponent</b> will not
     * get painted during the next paintDirtyRegions() call.
     */
    public void markCompletelyClean(JComponent aComponent) {
	synchronized(this) {
		dirtyComponents.remove(aComponent);
	}
    }

    /** 
     * Convenience method that returns true if <b>aComponent</b> will be completely
     * painted during the next paintDirtyRegions(). If computing dirty regions is
     * expensive for your component, use this method and avoid computing dirty region
     * if it return true.
     */
    public boolean isCompletelyDirty(JComponent aComponent) {
	Rectangle r;
	
	r = getDirtyRegion(aComponent);
	if(r.width == Integer.MAX_VALUE &&
	   r.height == Integer.MAX_VALUE)
	    return true;
	else
	    return false;
    }


    /** 
     * Validate all of the components that have been marked invalid.
     * @see #addInvalidComponent
     */
    public void validateInvalidComponents() {
        Vector ic;
        synchronized(this) {
            if(invalidComponents == null) {
                return;
	    }
            ic = invalidComponents;
            invalidComponents = null;
        }
	int n = ic.size();
        for(int i = 0; i < n; i++) {
            ((Component)ic.elementAt(i)).validate();
        }
    }
    

    /**
     * Paint all of the components that have been marked dirty.
     * 
     * @see #addDirtyRegion
     */
    public void paintDirtyRegions() {
        int i, count;
	Vector roots;
        JComponent dirtyComponent;

	synchronized(this) {  // swap for thread safety
	    Hashtable tmp = tmpDirtyComponents;
	    tmpDirtyComponents = dirtyComponents;
	    dirtyComponents = tmp;
	    dirtyComponents.clear();
	}

        count = tmpDirtyComponents.size();
        if (count == 0) {
            return;
        } 

        Rectangle rect;
        int localBoundsX = 0;
        int localBoundsY = 0;
        int localBoundsH = 0;
        int localBoundsW = 0;	
        Enumeration keys;

        roots = new Vector(count);
        keys = tmpDirtyComponents.keys();

        while(keys.hasMoreElements()) {
            dirtyComponent = (JComponent) keys.nextElement();
            collectDirtyComponents(tmpDirtyComponents, dirtyComponent, roots);
        }

        count = roots.size();
        //        System.out.println("roots size is " + count);
        for(i=0 ; i < count ; i++) {
            dirtyComponent = (JComponent) roots.elementAt(i);
            rect = (Rectangle) tmpDirtyComponents.get(dirtyComponent);
            //            System.out.println("Should refresh :" + rect);
            localBoundsH = dirtyComponent.getHeight();
            localBoundsW = dirtyComponent.getWidth();

            SwingUtilities.computeIntersection(localBoundsX, 
					       localBoundsY,
					       localBoundsW,
					       localBoundsH,
					       rect);
            // System.out.println("** paint of " + dirtyComponent + rect);
            dirtyComponent.paintImmediately(rect.x,rect.y,rect.width,rect.height);
        }
	tmpDirtyComponents.clear();
    }


    Rectangle tmp = new Rectangle();

    void collectDirtyComponents(Hashtable dirtyComponents,
				JComponent dirtyComponent,
				Vector roots) {
        int dx, dy, rootDx, rootDy;
        Component component, rootDirtyComponent,parent;
	//Rectangle tmp;
        Rectangle cBounds;
        boolean opaqueAncestorFound = false;

        // Find the highest parent which is dirty.  When we get out of this
        // rootDx and rootDy will contain the translation from the
        // rootDirtyComponent's coordinate system to the coordinates of the
        // original dirty component.  The tmp Rect is also used to compute the
        // visible portion of the dirtyRect.

        component = rootDirtyComponent = dirtyComponent;

        cBounds = dirtyComponent._bounds;

        dx = rootDx = 0;
        dy = rootDy = 0;
        tmp.setBounds((Rectangle) dirtyComponents.get(dirtyComponent));

        // System.out.println("Collect dirty component for bound " + tmp + 
        //                                   "component bounds is " + cBounds);;
        SwingUtilities.computeIntersection(0,0,cBounds.width,cBounds.height,tmp);

        if (tmp.isEmpty()) {
            // System.out.println("Empty 1");
            return;
        } 

        if(dirtyComponent.isOpaque())
            opaqueAncestorFound = true;

        for(;;) {
            parent = component.getParent();
            if(parent == null) 
                break;

            if(!(parent instanceof JComponent))
                break;

            component = parent;

            if(((JComponent)component).isOpaque())
                opaqueAncestorFound = true;

            dx += cBounds.x;
            dy += cBounds.y;
            tmp.setLocation(tmp.x + cBounds.x,
                            tmp.y + cBounds.y);

            cBounds = ((JComponent)component)._bounds;
            tmp = SwingUtilities.computeIntersection(0,0,cBounds.width,cBounds.height,tmp);

            if (tmp.isEmpty()) {
                // System.out.println("Empty 2");
                return;
            }

            if (dirtyComponents.get(component) != null) {
                rootDirtyComponent = component;
                rootDx = dx;
                rootDy = dy;
            }
        } 

        if (dirtyComponent != rootDirtyComponent) {
	    Rectangle r;
            tmp.setLocation(tmp.x + rootDx - dx,
			    tmp.y + rootDy - dy);
	    r = (Rectangle)dirtyComponents.get(rootDirtyComponent);
	    SwingUtilities.computeUnion(tmp.x,tmp.y,tmp.width,tmp.height,r);
        }

        // If we haven't seen this root before, then we need to add it to the
        // list of root dirty Views.

        if (!roots.contains(rootDirtyComponent)) 
            roots.addElement(rootDirtyComponent);	
    }


    /**
     * Returns a string that displays and identifies this
     * object's properties.
     *
     * @return a String representation of this object
     */
    public synchronized String toString() {
	StringBuffer sb = new StringBuffer();
	if(dirtyComponents != null) 
	    sb.append("" + dirtyComponents);
        return sb.toString();
    }

    /**
     * Return the offscreen buffer that should be used as a double buffer with the component <code>c</code>
     * By default there is a double buffer per RepaintManager.
     * The buffer might be smaller than <code>(proposedWidth,proposedHeight)</code>
     * This happens when the maximum double buffer size as been set for the receiving
     * repaint manager.
     */
    public Image getOffscreenBuffer(Component c,int proposedWidth,int proposedHeight) {
        Image result;
        int width,height;
	Dimension maxSize = getDoubleBufferMaximumSize();

	if (resetDoubleBuffer) {
	    doubleBuffer = null;
	    resetDoubleBuffer = false;
	}
	if (proposedWidth < 1) {
	    width = 1;
	}
        else if(proposedWidth > maxSize.width)
            width = maxSize.width;
        else
            width = proposedWidth;

	if (proposedHeight < 1) {
	    height = 1;
	}
        else if(proposedHeight > maxSize.height) 
            height = maxSize.height;
        else
            height = proposedHeight;

        if(doubleBuffer != null) {
            if(doubleBuffer.getWidth(null) < width || doubleBuffer.getHeight(null) < height) {
                doubleBuffer = null;
	    }
        }

	int go_width = width;
        int go_height = height;

        if(doubleBuffer != null) {
           go_width = doubleBufferSize.width;
	   go_height = doubleBufferSize.height;

	   if( doubleBufferSize.width < width ) {
	       go_width = width;
	       doubleBuffer = null;
	   }
	   
	   if( doubleBufferSize.height < height ) {
	       go_height = height;
	       doubleBuffer = null;
	   }
        }

	Image retValue = doubleBuffer;

	if(doubleBuffer == null) {
            retValue = c.createImage( go_width , go_height );
            doubleBufferSize = new Dimension( go_width , go_height );
	    if (c instanceof JComponent) {
		((JComponent)c).setCreatedDoubleBuffer(true);
		doubleBuffer = retValue;
	    }
	    // JComponent will inform us when it is no longer valid
	    // (via removeNotify) we have no such hook to other components,
	    // therefore we don't keep a ref to the Component
	    // (indirectly through the Image) by stashing the image.
	}

        return retValue;
    }

    /** Set the maximum double buffer size. **/
    public void setDoubleBufferMaximumSize(Dimension d) {
        doubleBufferMaxSize = d;
        if(doubleBuffer != null) {
            if(doubleBuffer.getWidth(null) > d.width || doubleBuffer.getHeight(null) > d.height) {
		doubleBuffer = null;
	    }
        }
    }

    /**
     * Returns the maximum double buffer size.
     *
     * @return a Dimension object representing the maximum size
     */
    public Dimension getDoubleBufferMaximumSize() {
	if (doubleBufferMaxSize == null) {
	    doubleBufferMaxSize = Toolkit.getDefaultToolkit().getScreenSize();
	}
        return doubleBufferMaxSize;
    }

    /**
     * Enables or disables double buffering.
     *
     * @param aFlag  true to activate double buffering
     */
    public void setDoubleBufferingEnabled(boolean aFlag) {
        doubleBufferingEnabled = aFlag;
    }

    /**
     * Returns true if this object is double buffered.
     *
     * @return true if this object is double buffered
     */
    public boolean isDoubleBufferingEnabled() {
      return doubleBufferingEnabled;
    }

    /**
     * This resets the double buffer. Actually, it marks the double buffer
     * as invalid, the double buffer will then be recreated on the next
     * invocation of getOffscreenBuffer.
     */
    void resetDoubleBuffer() {
	resetDoubleBuffer = true;
    }
}
