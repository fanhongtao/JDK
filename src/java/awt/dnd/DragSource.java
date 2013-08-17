/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.dnd;

import java.awt.AWTError;
import java.awt.AWTException;
import java.awt.event.InputEvent;
import java.awt.AWTPermission;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

import java.awt.datatransfer.Transferable;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceListener;
import java.awt.datatransfer.FlavorMap;
import java.awt.datatransfer.SystemFlavorMap;
import java.awt.dnd.InvalidDnDOperationException;

import java.awt.dnd.peer.DragSourceContextPeer;

import java.security.AccessController;

/**
 * The <code>DragSource</code> is the entity responsible 
 * for the initiation of the Drag
 * and Drop operation, and may be used in a number of scenarios: 
 * <UL>
 * <LI>1 default instance per JVM for the lifetime of that JVM. 
 * <LI>1 instance per class of potential Drag Initiator object (e.g
 * TextField). [implementation dependent] 
 * <LI>1 per instance of a particular 
 * <code>Component</code>, or application specific
 * object associated with a <code>Component</code> 
 * instance in the GUI. [implementation dependent] 
 * <LI>Some other arbitrary association. [implementation dependent] 
 *</UL>
 *
 * Once the <code>DragSource</code> is 
 * obtained, a <code>DragGestureRecognizer</code> should
 * also be obtained to associate the <code>DragSource</code>
 * with a particular
 * <code>Component</code>. 
 * <P>
 * The initial interpretation of the user's gesture, 
 * and the subsequent starting of the drag operation 
 * are the responsibility of the implementing
 * <code>Component</code>, which is usually 
 * implemented by a <code>DragGestureRecognizer</code>. 
 *<P>
 * When a drag gesture occurs, the 
 * <code>DragSource</code>'s 
 * startDrag() method shall be
 * invoked in order to cause processing 
 * of the user's navigational
 * gestures and delivery of Drag and Drop 
 * protocol notifications. A
 * <code>DragSource</code> shall only 
 * permit a single Drag and Drop operation to be
 * current at any one time, and shall 
 * reject any further startDrag() requests
 * by throwing an <code>IllegalDnDOperationException</code> 
 * until such time as the extant operation is complete. 
 * <P>
 * The startDrag() method invokes the 
 * createDragSourceContext() method to
 * instantiate an appropriate 
 * <code>DragSourceContext</code> 
 * and associate the <code>DragSourceContextPeer</code>
 * with that. 
 * <P>
 * If the Drag and Drop System is 
 * unable to initiate a drag operation for
 * some reason, the startDrag() method throws 
 * a <code>java.awt.dnd.InvalidDnDOperationException</code>
 * to signal such a condition. Typically this 
 * exception is thrown when the underlying platform
 * system is either not in a state to 
 * initiate a drag, or the parameters specified are invalid. 
 * <P>
 * Note that during the drag, the 
 * set of operations exposed by the source
 * at the start of the drag operation may not change 
 * until the operation is complete. 
 * The operation(s) are constant for the
 * duration of the operation with respect to the 
 * <code>DragSource</code>. 
 *
 * @version 	1.31, 02/06/02
 * @since 1.2
 */

public class DragSource {

    /*
     * load a system default cursor
     */

    private static Cursor load(String name) {
	try {
	    return (Cursor)Toolkit.getDefaultToolkit().getDesktopProperty(name);
	} catch (Exception e) {
	    e.printStackTrace();

	    throw new RuntimeException("failed to load system cursor: " + name + " : " + e.getMessage());
	}
    }

   
    /**
     * The default <code>Cursor</code> to use with a copy operation 
     * indicating that a drop is currently allowed.
     */
    public static final Cursor DefaultCopyDrop = load("DnD.Cursor.CopyDrop");

    /**
     * The default <code>Cursor</code> to use 
     * with a move operation indicating that 
     * a drop is currently allowed.
     */
    public static final Cursor DefaultMoveDrop = load("DnD.Cursor.MoveDrop");

    /**
     * The default <code>Cursor</code> to use with a 
     * link operation indicating that a 
     * drop is currently allowed.
     */
    public static final Cursor DefaultLinkDrop = load("DnD.Cursor.LinkDrop");

    /**
     * The default <code>Cursor</code> to use with 
     * a copy operation indicating that a drop is currently not allowed.
     */
    public static final Cursor DefaultCopyNoDrop = load("DnD.Cursor.CopyNoDrop");

    /**
     * The default <code>Cursor</code> to use with a move 
     * operation indicating that a drop is currently not allowed.
     */
    public static final Cursor DefaultMoveNoDrop = load("DnD.Cursor.MoveNoDrop");

    /**
     * The default <code>Cursor</code> to use 
     * with a link operation indicating 
     * that a drop is currently not allowed.
     */
    public static final Cursor DefaultLinkNoDrop = load("DnD.Cursor.LinkNoDrop");

    /*
     * The System FlavorMap
     */

    private static final FlavorMap defaultFlavorMap = SystemFlavorMap.getDefaultFlavorMap();

    private static DragSource dflt;

    /**
     * This method returns the <code>DragSource</code> 
     * object associated with the underlying platform.
     * <P>
     * @return the platform DragSource
     */

    public static DragSource getDefaultDragSource() {
	if (dflt == null) dflt = new DragSource();

	return dflt;
    }

    /**
     * This method returns a <code>boolean</code> 
     * indicating whether or not drag 
     * <code>Image</code> support 
     * is available on the underlying platform.
     * <P>
     * @return if the Drag Image support is available on this platform
     */

    public static boolean isDragImageSupported() {
	Toolkit t = Toolkit.getDefaultToolkit();

	Boolean supported;

	try {
	    supported = (Boolean)Toolkit.getDefaultToolkit().getDesktopProperty("DnD.isDragImageSupported");

	    return supported.booleanValue();
	} catch (Exception e) {
	    return false;
	}
    }

    /**
     * Construct a new <code>DragSource</code>.
     */

    public DragSource() { super(); }

    /**
     * Start a drag, given the <code>DragGestureEvent</code> 
     * that initiated the drag, the initial 
     * <code>Cursor</code> to use,
     * the <code>Image</code> to drag, 
     * the offset of the <code>Image</code> origin 
     * from the hotspot of the <code>Cursor</code> at 
     * the instant of the trigger,
     * the <code>Transferable</code> subject data 
     * of the drag, the <code>DragSourceListener</code>, 
     * and the <code>FlavorMap</code>. 
     * <P>
     * @param trigger	     The <code>DragGestureEvent</code> that initiated the drag
     * @param dragCursor     The initial <code>Cursor</code> or <code>null</code> for defaults
     * @param dragImage	     The image to drag or null,
     * @param imageOffset    The offset of the <code>Image</code> origin from the hotspot
     *			     of the <code>Cursor</code> at the instant of the trigger
     * @param transferable   The subject data of the drag
     * @param dsl	     The <code>DragSourceListener</code>
     * @param flavorMap	     The <code>FlavorMap</code> to use, or <code>null</code>
     * <P>
     * @throws <code>java.awt.dnd.InvalidDnDOperationException</code> 
     * if the Drag and Drop
     * system is unable to initiate a drag operation, or if the user
     * attempts to start a drag while an existing drag operation 
     * is still executing.
     */

    public void startDrag(DragGestureEvent   trigger,
			  Cursor	     dragCursor,
			  Image		     dragImage,
			  Point		     imageOffset,
			  Transferable	     transferable,
			  DragSourceListener dsl,
			  FlavorMap	     flavorMap) throws InvalidDnDOperationException {

	
	if (flavorMap != null) this.flavorMap = flavorMap;

	DragSourceContextPeer dscp = Toolkit.getDefaultToolkit().createDragSourceContextPeer(trigger);

	DragSourceContext     dsc = createDragSourceContext(dscp,
							    trigger,
							    dragCursor,
							    dragImage,
							    imageOffset,
							    transferable,
							    dsl
				    );

	if (dsc == null) {
	    throw new InvalidDnDOperationException();
	}
							    
	dscp.startDrag(dsc, dsc.getCursor(), dragImage, imageOffset); // may throw
}

    /**
     * Start a drag, given the <code>DragGestureEvent</code> 
     * that initiated the drag, the initial 
     * <code>Cursor</code> to use,
     * the <code>Transferable</code> subject data 
     * of the drag, the <code>DragSourceListener</code>, 
     * and the <code>FlavorMap</code>. 
     * <P>
     * @param trigger	     The <code>DragGestureEvent</code> that 
     * initiated the drag
     * @param dragCursor     The initial <code>Cursor</code> or 
     * <code>null</code> for defaults
     * @param transferable   The subject data of the drag
     * @param dsl	     The <code>DragSourceListener</code>
     * @param flavorMap	     The <code>FlavorMap to use or <code>null</code>
     * <P>
     * @throws <code>java.awt.dnd.InvalidDnDOperationException</code> 
     * if the Drag and Drop
     * system is unable to initiate a drag operation, or if the user
     * attempts to start a drag while an existing drag operation 
     * is still executing.
     */

    public void startDrag(DragGestureEvent   trigger,
			  Cursor	     dragCursor,
			  Transferable	     transferable,
			  DragSourceListener dsl,
			  FlavorMap	     flavorMap) throws InvalidDnDOperationException {
	startDrag(trigger, dragCursor, null, null, transferable, dsl, flavorMap);
    }

    /**
     * Start a drag, given the <code>DragGestureEvent</code> 
     * that initiated the drag, the initial <code>Cursor</code> 
     * to use,
     * the <code>Image</code> to drag, 
     * the offset of the <code>Image</code> origin 
     * from the hotspot of the <code>Cursor</code>
     * at the instant of the trigger,
     * the subject data of the drag, and 
     * the <code>DragSourceListener</code>. 
     * <P>
     * @param trigger		The <code>DragGestureEvent</code> that initiated the drag
     * @param dragCursor	The initial <code>Cursor</code> or <code>null</code> for defaults
     * @param dragImage		The <code>Image</code> to drag or <code>null</code>
     * @param imageOffset	The offset of the <code>Image</code> origin from the hotspot
     *				of the <code>Cursor</code> at the instant of the trigger
     * @param transferable	The subject data of the drag
     * @param dsl		The <code>DragSourceListener</code>
     * <P>
     * @throws <code>java.awt.dnd.InvalidDnDOperationException</code> 
     * if the Drag and Drop
     * system is unable to initiate a drag operation, or if the user
     * attempts to start a drag while an existing drag operation  
     * is still executing.
     */

    public void startDrag(DragGestureEvent   trigger,
			  Cursor	     dragCursor,
			  Image		     dragImage,
			  Point		     dragOffset,
			  Transferable	     transferable,
			  DragSourceListener dsl) throws InvalidDnDOperationException {
	startDrag(trigger, dragCursor, dragImage, dragOffset, transferable, dsl, null);
    }

    /**
     * Start a drag, given the <code>DragGestureEvent</code> 
     * that initiated the drag, the initial 
     * <code>Cursor</code> to 
     * use, 
     * the <code>Transferable</code> subject data 
     * of the drag, and the <code>DragSourceListener</code>. 
     * <P>
     * @param trigger		The <code>DragGestureEvent</code> that initiated the drag
     * @param dragCursor	The initial <code>Cursor</code> or <code>null</code> for defaults
     * @param transferable	The subject data of the drag
     * @param dsl		The <code>DragSourceListener</code>
     * <P>
     * @throws <code>java.awt.dnd.InvalidDnDOperationException</code> 
     * if the Drag and Drop
     * system is unable to initiate a drag operation, or if the user
     * attempts to start a drag while an existing drag operation 
     * is still executing.
     */

    public void startDrag(DragGestureEvent   trigger,
			  Cursor	     dragCursor,
			  Transferable	     transferable,
			  DragSourceListener dsl) throws InvalidDnDOperationException {
	startDrag(trigger, dragCursor, null, null, transferable, dsl, null);
    }

    /**
     * Create the <code>DragSourceContext</code> to handle this drag.
     * 
     * To incorporate a new <code>DragSourceContext</code> 
     * subclass, subclass <code>DragSource</code> and
     * override this method.
     * <P>
     * @param dscp 		The <code>DragSourceContextPeer</code> for this drag
     * @param trigger	        The <code>DragGestureEvent</code> that triggered the drag
     * @param dragCursor	The initial <code>Cursor</code> to display
     * @param dragImage		The <code>Image</code> to drag or <code>null</code>
     * @param imageOffset	The offset of the <code>Image</code> origin from the hotspot
     *				of the cursor at the instant of the trigger
     * @param transferable	The subject data of the drag
     * @param dsl		The <code>DragSourceListener</code>
     * <P>
     * @return the <code>DragSourceContext</code>
     */

    protected DragSourceContext createDragSourceContext(DragSourceContextPeer dscp, DragGestureEvent dgl, Cursor dragCursor, Image dragImage, Point imageOffset, Transferable t, DragSourceListener dsl) {
	return new DragSourceContext(dscp, dgl, dragCursor, dragImage, imageOffset, t, dsl);
    }

    /**
     * This method returns the 
     * <code>FlavorMap</code> for this <code>DragSource</code>.
     * <P>
     * @return the <code>FlavorMap</code> for this <code>DragSource</code>
     */

    public FlavorMap getFlavorMap() { return flavorMap; }

    /**
     * Creates a new <code>DragGestureRecognizer</code> 
     * that implements the specified
     * abstract subclass of 
     * <code>DragGestureRecognizer</code>, and 
     * sets the specified <code>Component</code> 
     * and <code>DragGestureListener</code> on 
     * the newly created object.
     * <P>
     * @param recognizerAbstractClass The requested abstract type
     * @param actions		      The permitted source drag actions
     * @param c			      The <code>Component</code> target 
     * @param dgl	 The <code>DragGestureListener</code> to notify
     * <P>
     * @return the new <code>DragGestureRecognizer</code> or <code>null</code>
     * if the Toolkit.createDragGestureRecognizer() method
     * has no implementation available for 
     * the requested <code>DragGestureRecognizer</code>
     * subclass and returns <code>null</code>.
     */

    public DragGestureRecognizer createDragGestureRecognizer(Class recognizerAbstractClass, Component c, int actions, DragGestureListener dgl) {
	return Toolkit.getDefaultToolkit().createDragGestureRecognizer(recognizerAbstractClass, this, c, actions, dgl);
    }

	
    /**
     * Creates a new <code>DragSourceRecognizer</code> 
     * that implements the default
     * abstract subclass of <code>DragGestureRecognizer</code>
     * for this <code>DragSource</code>,
     * and sets the specified <code>Component</code> 
     * and <code>DragGestureListener</code> on the
     * newly created object. 
     *
     * For this <code>DragSource</code> 
     * the default is <code>MouseDragGestureRecognizer</code>.
     * <P>
     * @param c	      The <code>Component</code> target for the recognizer
     * @param actions The permitted source actions
     * @param dgl     The <code>DragGestureListener</code> to notify
     * <P>
     * @return the new <code>DragGestureRecognizer</code> or <code>null</code>
     * if the Toolkit.createDragGestureRecognizer() method
     * has no implementation available for 
     * the requested <code>DragGestureRecognizer</code>
     * subclass and returns <code>null</code>.
     */

    public DragGestureRecognizer createDefaultDragGestureRecognizer(Component c, int actions, DragGestureListener dgl) {
	return Toolkit.getDefaultToolkit().createDragGestureRecognizer(MouseDragGestureRecognizer.class, this, c, actions, dgl);
    }

    /*
     * fields
     */

    private transient FlavorMap flavorMap = defaultFlavorMap;
}




















