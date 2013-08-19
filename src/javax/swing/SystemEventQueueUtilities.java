/*
 * @(#)SystemEventQueueUtilities.java	1.37 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import java.util.*;

import java.lang.reflect.InvocationTargetException;

import sun.awt.AppContext;

/**
 * Swing internal utilities for dealing with the AWT system event
 * queue.  Four methods are exported, see the individual method javadoc
 * for more information: addRunnableCanvas(), removeRunnableCanvas(),
 * postRunnable(), queueComponentWorkRequest().
 *
 * Note: most of the code in this class is no longer needed since
 * we're no longer supporting Swing in 1.1.x VM's and in 1.2 we're
 * guaranteed access to the AWT event queue.  However all of the entry 
 * points, save postRunnable(), are still used.
 * 
 * @see RepaintManager
 * @see JRootPane
 */
class SystemEventQueueUtilities
{
    private static final Object classLock = new Object();


    private static final Object rootTableKey = new Object() {
	public String toString() {
	   return "SystemEventQueueUtilties.rootTableKey";
	}
    };

    private static Map getRootTable() {
	Map rt = (Map)AppContext.getAppContext().get(rootTableKey);
	if (rt == null) {
	    synchronized (rootTableKey) {
		rt = (Map)AppContext.getAppContext().get(rootTableKey);
		if (rt == null) {
		    rt = new WeakHashMap(4);
		    AppContext.getAppContext().put(rootTableKey, rt);
		}
	    }
	}
	return rt;
    }


    /**
     * SystemEventQueue class.  This private class just exists to 
     * encapsulate the details of getting at the System Event queue 
     * in the Java 2 platform.  The rest of the SystemEventQueueUtilities 
     * class just uses SystemEventQueue.get() to access the event queue.
     */

    private static class SystemEventQueue 
    {
	// If the AWT system event queue is accessible then return it.
	// otherwise return null.  

	static EventQueue get() {
	    EventQueue retValue;
	    try {
	        retValue = Toolkit.getDefaultToolkit().getSystemEventQueue();
            }
	    catch (SecurityException se) {
	        // Should never happen.
                retValue = null;
            }
            return retValue;
	}

	// If the AWT system event queue is accessible then return it.
	// otherwise return null.  
	static EventQueue get(JRootPane rootPane) {
	    return get();
	}
    }

    /**
     * A Runnable with a component.  If we need to post this
     * runnable to the AWT system event queue, we'll find it's
     * JRootPane ancestor and use that as the key to the table
     * of RunnableCanvas's.
     *
     * @see RunnableCanvas
     */
    private static class ComponentWorkRequest implements Runnable
    {
	boolean isPending;
 	Component component;
  
 	ComponentWorkRequest(Component c) {
 	    /* As of 1.2, the component field is no longer used.  It was 
 	     * used by the RunnableCanvas class to find the JRootPane 
 	     * associated with a ComponentWorkRequest for JDK1.1.x.
 	     */
 	    // component = c;
  	}
	public void run() {
	    RepaintManager rm;
	    synchronized (this) {
		rm = RepaintManager.currentManager(component /*null*/);
		isPending = false;
	    }
	    rm.validateInvalidComponents();
	    rm.paintDirtyRegions();
	}
    }


    /**
     * This method is used by RepaintManager to queue a ComponentWorkRequest
     * with invokeLater().  It assumes that the root argument is either
     * and Applet or a Window, the root passed in obtained in a
     * slightly different manner than see SwingUtilities.getRoot(). If this
     * called with the root obtained in a different way than RepaintManager
     * currently uses, be sure to also tweak removeRunnableCanvas.
     */
    static void queueComponentWorkRequest(Component root)
    {
	ComponentWorkRequest req = (ComponentWorkRequest)(getRootTable().get(root));
	boolean newWorkRequest = (req == null);
	if (newWorkRequest) {
	    req = new ComponentWorkRequest(root);
	}

	/* At this point the ComponentWorkRequest may be accessible from
	 * an event dispatching thread so before updating it further
	 * we synchronize access to it.
	 */
	synchronized(req) {
	    if (newWorkRequest) {
		getRootTable().put(root, req);
	    }
	    if (!req.isPending) {
		SwingUtilities.invokeLater(req);
		req.isPending = true;
	    }
	}
    }


    /**
     * Associate a RunnableCanvas and a JRootPane to enable queuing
     * events for the root pane's parent window's event dispatching thread.
     * Adds a 1x1 RunnableCanvas to the root pane's layered pane.
     * <p>
     * Called by JRootPane.addNotify() to set up the RunnableCanvas.
     *
     * @see RunnableCanvas
     * @see JRootPane#addNotify
     */
    static void addRunnableCanvas(JRootPane rootPane)
    {
	synchronized (classLock) {
	    /* If we have access to the system event queue, we don't bother
	     * with a RunnableCanvas
	     */
	    if (SystemEventQueue.get(rootPane) != null) {
		return;
	    }

	    JLayeredPane layeredPane = rootPane.getLayeredPane();
	    if (layeredPane != null) {
		RunnableCanvas rc = new RunnableCanvas(rootPane);
		layeredPane.add(rc);
	    }
	}
    }


    /**
     * Remove the RunnableCanvas from the JRootPane and clear the
     * internal bookeeping associated with it.
     * <p>
     * Called by JRootPane.removeNotify()
     *
     * @see RunnableCanvas
     */
    static void removeRunnableCanvas(JRootPane rootPane) {
	synchronized (classLock) {
	    // We don't use SwingUtilities.getRoot, as it has different
	    // behavior then the RepaintManager call to add the initial root.
	    Component root = null;
	    for (Component c = rootPane; c != null; c = c.getParent()) {
		if ((c instanceof Window) ||
		    (c instanceof  java.applet.Applet)) {
		    root = c;
		    break;
		}
	    }
	    if (root != null) {
		getRootTable().remove(root);
	    }
	    RunnableCanvas.remove(rootPane);
	}
    }


    /**
     * Post an event to the AWT System event queue that, when dispatched,
     * will invoke the specified Runnable.  If lock is non-null this call
     * blocks (by waiting on the lock) until the doRun() method returns,
     * otherwise we return as soon as the event has been enqueued.  An
     * exception is only returned if lock is non-null, i.e. if we're
     * being called from invokeAndWait().
     * <p>
     * This method is only intended to support SwingUtilities.invokeLater()
     * and SwingUtilities.invokeAndWait().
     */
    static Exception postRunnable(Runnable doRun, Object lock)
    {
	EventQueue systemEventQueue = SystemEventQueue.get();

	RunnableEvent event = new RunnableEvent(doRun, lock);
	if (systemEventQueue != null) {
	    systemEventQueue.postEvent(event);
	}
	else {
	    postRunnableCanvasEvent(event);
	}
	return event.exception;
    }


    /**
     * Adds a RunnableEvent to all the remaining RunnableCanvases to restart
     * the TimerQueues thread.
     *
     * @see RunnableCanvas#postRunnableEventToAll
     */
    static void restartTimerQueueThread() {
	synchronized (classLock) {
	    if (SystemEventQueue.get() == null) {
		Runnable restarter = new TimerQueueRestart();
		RunnableEvent event = new RunnableEvent(restarter, null);
		RunnableCanvas.postRunnableEventToAll(event);
	    }
	}
    }


    /**
     * Runnable that will message the shared instance of the Timer Queue
     * to restart.
     *
     * @see #restartTimerQueueThread
     */
    private static class TimerQueueRestart implements Runnable {
	boolean attemptedStart;

	public synchronized void run() {
	    // Only try and restart the q once.
	    if(!attemptedStart) {
		TimerQueue q = TimerQueue.sharedInstance();

		synchronized(q) {
		    if(!q.running)
			q.start();
		}
		attemptedStart = true;
	    }
	}
    }

    /**
     * Event type used for dispatching runnable objects for
     * SwingUtilities.invokeLater() and SwingUtilities.invokeAndWait().
     *
     * @see #postRunnable
     */
    private static class RunnableEvent extends AWTEvent {
        static final int EVENT_ID = AWTEvent.RESERVED_ID_MAX + 1000;
	static final Component target = new RunnableTarget();
	final Runnable doRun;
	final Object lock;
	Exception exception;

        RunnableEvent(Runnable doRun, Object lock) {
            super(target, EVENT_ID);
	    this.doRun = doRun;
	    this.lock = lock;
        }
    }


    /**
     * Calls RunnableEvent.doRun.run().  If RunnableEvent.lock is non
     * null then we synchronize the run() call and save the exception
     * (if any) in the RunnableEvent.exception field.
     */
    private static void processRunnableEvent(RunnableEvent runnableEvent)
    {
	Object lock = runnableEvent.lock;
	if (lock == null) {
	    runnableEvent.doRun.run();
	}
	else {
	    synchronized(lock) {
		try {
		    runnableEvent.doRun.run();
		}
		catch (Exception e) {
		    runnableEvent.exception = e;
		}
		finally {
		    if (runnableEvent.lock != null) {
			runnableEvent.lock.notify();
		    }
		}
	    }
	}
    }


    /**
     * A dummy Component subclass that (only) handles RunnableEvents.  If the
     * AWT System event queue is accessible (i.e. we're running as
     * an application or as trusted code), RunnableEvents are dispatched
     * to this component.
     *
     * @see #processRunnableEvent
     */
    private static class RunnableTarget extends Component
    {
        RunnableTarget() {
            super();
            enableEvents(RunnableEvent.EVENT_ID);
        }

        protected void processEvent(AWTEvent event) {
	    if (event instanceof RunnableEvent) {
		processRunnableEvent((RunnableEvent)event);
	    }
        }
    }


    /**
     * Synchronized entry point to the applet support for AWT System
     * event queue access.  This method adds the event to the appropriate
     * runnable canvas's queue and then has the canvas repaint().  Note
     * that by the time the event dispatching thread gets to handling
     * the repaint() (by calling runnableCanvas.update()), many runnable
     * events may have been queued up.
     *
     * @see RunnableCanvas#addRunnableEvent
     * @see RunnableCanvas#update
     */
    private static void postRunnableCanvasEvent(RunnableEvent e) {
	synchronized (classLock) {
	    RunnableCanvas runnableCanvas = RunnableCanvas.lookup(e);

	    if (runnableCanvas == null) {

		/* If this is a ComponentWorkRequest and we were unable to
		 * queue it, then clear the pending flag.
		 */
		if (e.doRun instanceof ComponentWorkRequest) {
		    ComponentWorkRequest req = (ComponentWorkRequest)e.doRun;
		    synchronized(req) {
			req.isPending = false;
		    }
		}

		/* If this is a Timer event let it know that it didn't fire.
		 */
		if(e.doRun instanceof Timer.DoPostEvent) {
		    ((Timer.DoPostEvent)e.doRun).getTimer().cancelEvent();
		}

		/* We are unable to queue this event on a system event queue.  Make
		 * sure that any code that's waiting for the runnable to finish
		 * doesn't hang.
		 */
		if (e.lock != null) {
		    e.lock.notify();
		}
		return;
	    }

	    runnableCanvas.addRunnableEvent(e);
	    runnableCanvas.repaint();
	}
    }

    
    /**
     * Return the current threads ThreadGroup, even on IE4.0.
     * IE4.0 throws a SecurityException if you apply getThreadGroup()
     * to the event dispatching thread.  However a child of the
     * event dispatching thread (same thread group) is OK.  
     */
    private static ThreadGroup getThreadGroupSafely() {
	return new Thread().getThreadGroup();
    }


    /**
     * Applets don't have direct access to the AWT SystemEvent queue.  To
     * work around this we call RunnableCanvas.repaint() on a per applet
     * instance of this class.  The AWT deals with this by queuing a
     * java.awt.PaintEvent for the event dispatching thread which
     * is dispatched (Component.dispatchEvent()) the usual way.
     * Component.dispatchEvent() handles PaintEvents by calling our update()
     * method (on the event dispatching thread) which processes
     * the RunnableEvents stashed in the runnableEvents vector.
     */
     private static class RunnableCanvas extends Canvas
     {
	 private static final Graphics nullGraphics = new RunnableCanvasGraphics();
	 private static Hashtable runnableCanvasTable = new Hashtable(1);
	 private Vector runnableEvents = new Vector(2);
	 private boolean isRegistered = false;

	 RunnableCanvas(JRootPane rootPane) {
	     super();
	     setBounds(0, 0, 1, 1);

	     /* Remember the mapping from the current thread (and the current
	      * thread group) to this RunnableCanvas.  Note that if a mapping
	      * has already been defined, e.g. this rootPane belongs to an
	      * existing applet, then leave the table alone.  We're assuming that
	      * an applets addNotify method will always run before the addNotify
	      * method in any subsidiary windows the applet creates can run.
	      */
	     if (runnableCanvasTable.get(Thread.currentThread()) == null) {
		 try {
		     runnableCanvasTable.put(Thread.currentThread(), this);
		     runnableCanvasTable.put(getThreadGroupSafely(), this);
		     if (SwingUtilities.isEventDispatchThread()) {
			 isRegistered = true;
		     }
		 }
		 catch(Exception e) {
		     System.err.println("Can't register RunnableCanvas");
		     e.printStackTrace();
		 }
	     }
	     runnableCanvasTable.put(rootPane, this);
	     maybeRegisterEventDispatchThread();
	 }


	 /**
	  * If called on an event dispatching thread that we haven't seen
	  * before then make two hashtable entries in the runnableCanvasTable:
	  * <pre>
	  *   current thread => this RunnableCanvas
	  *   current thread group => this RunnableCanvas
	  * </pre>
	  * @see #lookup
	  */
	 private void maybeRegisterEventDispatchThread() {
	     /* Avoid the cost of a synchronized block (or method) in the
	      * common case, since this method is called each time paint is called.
	      */
	     if (!isRegistered) {
		 synchronized(this) {
		     if (!isRegistered && SwingUtilities.isEventDispatchThread()) {
			 Thread currentThread = Thread.currentThread();

			 /* If this event dispatching thread is already mapped to
			  * a runnableCanvas then don't replace the mapping (which
			  * we expect to be generated by the applet).
			  */
			 if (runnableCanvasTable.get(currentThread) != null) {
			     isRegistered = true;
			 }
			 else {
			     runnableCanvasTable.put(currentThread, this);
			     runnableCanvasTable.put(getThreadGroupSafely(), this);
			     isRegistered = true;
			 }
		     }
		 }
	     }
	 }


	 /**
	  * If we're running on the event dispatching thread then lookup
	  * the canvas with the current thread itself, otherwise use the
	  * current threads thread group.  If there is no match for the 
	  * ThreadGroup, the first visible RunnableCanvas is returned.
	  */
	 static RunnableCanvas lookup(RunnableEvent e) 
	 {
	     /* If this is a ComponentWorkRequest, find the components
	      * JRootPane ancestor and use that as the index into the
	      * runnableCanvasTable.  This case occurs when any thread,
	      * other than the event dispatching thead, calls repaint
	      */
	     if (e.doRun instanceof ComponentWorkRequest) {
		 ComponentWorkRequest req = (ComponentWorkRequest)e.doRun;
		 synchronized(req) {
		     JRootPane rootPane = SwingUtilities.getRootPane(req.component);
		     if(rootPane != null) {
			 return (RunnableCanvas)(runnableCanvasTable.get(rootPane));
		     }
		     /* Failure.  There doesn't appear to be a RunnableCanvas to use
		      * so indicate that a new request will need to be queued, see
		      * RepaintManager.queueWorkRequest().
		      */
		     req.isPending = false;
		     return null;
		 }
	     }

	     /* If the current thread is in the runnableCanvasTable
	      * (e.g. we're on the event dispatching thread) we're done.
	      */
	     Object rv = runnableCanvasTable.get(Thread.currentThread());
	     if (rv != null) {
		 return (RunnableCanvas)rv;
	     }

	     /* At this point we're assuming that the calling thread isn't
	      * a system thread (like an image observer thread), so it's safe 
	      * to lookup via the current threads ThreadGroup.
	      */
	     Object threadGroup;
	     try {
		 threadGroup = Thread.currentThread().getThreadGroup();
	     }
	     catch (SecurityException exc) {
		 return null;
	     }
	     RunnableCanvas rc = (RunnableCanvas)runnableCanvasTable.get(threadGroup);
	     
	     /* There's no RunnableCanvas associated with this thread group
	      * (so punt).  Return the first visible RunnableCanvas.
	      */
	     if(rc == null) {
		 Enumeration keys = runnableCanvasTable.keys();
		 if(keys == null) {
		     return null;
		 }
		 while(keys.hasMoreElements()) {
		     Object key = keys.nextElement();
		     if ((key instanceof JRootPane) && ((JRootPane)key).isShowing()) {
			 return (RunnableCanvas)runnableCanvasTable.get(key);
		     }
		 }
	     }

	     return rc;
	 }


	 /**
	  * Adds the event to all the RunnableCanvases.
	  *
	  * @see #restartTimerQueueThread
	  */
	 static void postRunnableEventToAll(RunnableEvent e) {
	     // Determine the RunnableCanvas for the current thread. It
	     // may be null.
	     RunnableCanvas currentThreadCanvas;
	     ThreadGroup tg;
	     try {
		 tg = new Thread().getThreadGroup();
	     }
	     catch (SecurityException se) {
		 tg = null;
	     }
	     if(tg != null) {
		 currentThreadCanvas = (RunnableCanvas)runnableCanvasTable.
		                        get(tg);
	     }
	     else
		 currentThreadCanvas = null;

	     // Add the event to all canvases, except the current one.
	     // Presumably the current one is no longer valid and will be
	     // going away shortly.
	     Enumeration keys = runnableCanvasTable.keys();
	     while(keys.hasMoreElements()) {
		 Object key = keys.nextElement();
		 if(key instanceof JRootPane) {
		     Object canvas = runnableCanvasTable.get(key);
		     if(canvas != currentThreadCanvas) {
			 RunnableCanvas rc = (RunnableCanvas)canvas;
			 rc.addRunnableEvent(e);
			 rc.repaint();
		     }
		 }
	     }
	 }


	 /**
	  * Remove the RunnableCanvas associated with this applet from the
	  * applets Layered pane and clear all of the runnableCanvasTable
	  * entries that point at it.
	  */
	 static void remove(JRootPane rootPane) {
	     RunnableCanvas rc = (RunnableCanvas)(runnableCanvasTable.get(rootPane));
	     if (rc != null) {
		 RunnableCanvas nextCanvas = null;
		 JLayeredPane layeredPane = rootPane.getLayeredPane();
		 layeredPane.remove((Component)rc);

		 Enumeration keys = runnableCanvasTable.keys();
		 while(keys.hasMoreElements()) {
		     Object key = keys.nextElement();
		     Object next = runnableCanvasTable.get(key);
		     if (rc == next) {
			 runnableCanvasTable.remove(key);
		     }
		     else if(nextCanvas == null) {
			 nextCanvas = (RunnableCanvas)next;
		     }
		 }

		 // If there are still events, either move them to another
		 // canvas, or mark the Timer type events as not having
		 // fired.
		 RunnableEvent[] events = rc.getRunnableCanvasEvents();
		 int numEvents = (events == null) ? 0 : events.length;
		 if(numEvents > 0) {
		     if(nextCanvas != null) {
			 for(int counter = 0; counter < numEvents; counter++) {
			     RunnableEvent e = events[counter];
			     if(e.doRun instanceof Timer.DoPostEvent)
				 nextCanvas.addRunnableEvent(e);
			 }
			 nextCanvas.repaint();
		     }
		     else {
			 // Mark all Timer type event as not having fired.
			 for(int counter = 0; counter < numEvents; counter++) {
			     RunnableEvent event = events[counter];
			     if(event.doRun instanceof Timer.DoPostEvent) {
				 ((Timer.DoPostEvent)event.doRun).getTimer().
				                     cancelEvent();
			     }
			 }
		     }
		 }
	     }
	 }


	 /**
	  * If there are events to be processed then we're showing.  Note
	  * that the AWT code that dispatches paint events short circuits
	  * (does nothing) if isShowing() returns false.
	  */
	 public boolean isShowing() {
	     return runnableEvents.size() > 0;
	 }


	 /**
	  * Reduce the cost of repainting (since we're not going to draw
	  * anything) by returning a constant no-op graphics object.
	  */
	 public Graphics getGraphics() {
	     return nullGraphics;
	 }


	 /**
	  * Testing purposes only.  This method shouldn't be called;
	  * the parent of this component should have a null layout
	  * manager.
	  */
	 public Dimension getPreferredSize() {
	     return new Dimension(1, 1);
	 }


	 /**
	  * Add a RunnableEvent to the queue that will be dispatched
	  * when this component is repainted.
	  * @see #update
	  */
	 synchronized void addRunnableEvent(RunnableEvent e) {
	     runnableEvents.addElement(e);
	 }


	 /**
	  * Return an (array) copy of the runnableEvents vector or
	  * null if the vector is empty.  The update method processes
	  * a copy of the vector so that we don't have to hold
	  * the synchronized lock while calling processRunnableEvent().
	  * @see #update
	  */
	 private synchronized RunnableEvent[] getRunnableCanvasEvents() {
	     int n = runnableEvents.size();
	     if (n == 0) {
		 return null;
	     }
	     else {
		 RunnableEvent[] rv = new RunnableEvent[n];
		 for(int i = 0; i < n; i++) {
		     rv[i] = (RunnableEvent)(runnableEvents.elementAt(i));
		 }
		 runnableEvents.removeAllElements();
		 return rv;
	     }
	 }


	 public void paint(Graphics g) {
	     maybeRegisterEventDispatchThread();
	 }


	 /**
	  * Process all of the RunnableEvents that have accumulated
	  * since RunnableCanvas.repaint() was called.
	  */
	 public void update(Graphics g) {
	     RunnableEvent[] events = getRunnableCanvasEvents();
	     if (events != null) {
		 for(int i = 0; i < events.length; i++) {
		     processRunnableEvent(events[i]);
		 }
	     }
	 }
    }


    /**
     * A no-op Graphics object for the RunnableCanvas component.
     * Most AWT Component implementations handle update events
     * like this:
     * <pre>
     *      Graphics g = getGraphics();
     *      Rectangle r = ((PaintEvent)e).getUpdateRect();
     *      g.clipRect(r.x, r.y, r.width, r.height);
     *      update(g)
     *      g.dispose();
     * </pre>
     * Since the RunnableCanvas component isn't really going to do
     * any painting we don't bother with creating/disposing real
     * graphics objects, or setting any of the properties.
     *
     */
    private static class RunnableCanvasGraphics extends Graphics
    {
	public Graphics create() {
	    return this;
	}

	/* We don't expect any of the following methods to be called but
	 * we still return marginally valid values for the get methods
	 * just in case.
	 */

	public Rectangle getClipBounds() {
	    return new Rectangle(0, 0, Short.MAX_VALUE, Short.MAX_VALUE);
	}

	public Shape getClip() {
	    return (Shape)(getClipBounds());
	}

	public  void dispose() {}
	public void translate(int x, int y) {}
	public Color getColor() { return Color.black; }
	public void setColor(Color c) {}
	public void setPaintMode() {}
	public void setXORMode(Color c) {}
	public Font getFont() { return null; }
	public void setFont(Font font) {}
	public FontMetrics getFontMetrics(Font f) { return null; }
	public void clipRect(int x, int y, int width, int height) {}
	public void setClip(int x, int y, int width, int height) {}
	public void setClip(Shape clip) {}
	public void copyArea(int x, int y, int w, int h, int dx, int dy) {}
	public void drawLine(int x1, int y1, int x2, int y2) {}
	public void fillRect(int x, int y, int width, int height) {}
	public void clearRect(int x, int y, int width, int height) {}
	public void drawRoundRect(int x, int y, int w, int h, int aw, int ah) {}
	public void fillRoundRect(int x, int y, int w, int h, int aw, int ah) {}
	public void drawOval(int x, int y, int w, int h) {}
	public void fillOval(int x, int y, int w, int h) {}
	public void drawArc(int x, int y, int w, int h, int sa, int aa) {}
	public void fillArc(int x, int y, int w, int h, int sa, int aa) {}
	public void drawPolyline(int xPoints[], int yPoints[], int nPoints) {}
	public void drawPolygon(int xPoints[], int yPoints[], int nPoints) {}
	public void fillPolygon(int xPoints[], int yPoints[], int nPoints) {}
	public void drawString(String str, int x, int y) {}
        public void drawString(java.text.AttributedCharacterIterator iterator, int x, int y) {}
	public boolean drawImage(Image i, int x, int y, ImageObserver o) { return false; }
	public boolean drawImage(Image i, int x, int y, int w, int h, ImageObserver o) { return false; }
	public boolean drawImage(Image i, int x, int y, Color bgcolor, ImageObserver o) { return false; }
	public boolean drawImage(Image i, int x, int y, int w, int h, Color c, ImageObserver o) { return false; }
	public boolean drawImage(Image i,
            int dx1, int dy1, int dx2, int dy2,
	    int sx1, int sy1, int sx2, int sy2, ImageObserver o)
	    { return false; }
	public boolean drawImage(Image i,
            int dx1, int dy1, int dx2, int dy2,
	    int sx1, int sy1, int sx2, int sy2, Color c, ImageObserver o)
	    { return false; }
    }
}
