/*
 * @(#)DragSource.java	1.14 98/04/21
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
 * <p>
 * The DragSource class is a small class responsible for originating a
 * Drag and Drop gesture.
 * </p>
 *
 * @version 1.14
 * @since JDK1.2
 *
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
     * Default Cursor Constants
     */

    public static final Cursor DefaultCopyDrop = load("DnD.Cursor.CopyDrop");
    public static final Cursor DefaultMoveDrop = load("DnD.Cursor.MoveDrop");
    public static final Cursor DefaultLinkDrop = load("DnD.Cursor.LinkDrop");

    public static final Cursor DefaultCopyNoDrop = load("DnD.Cursor.CopyNoDrop");
    public static final Cursor DefaultMoveNoDrop = load("DnD.Cursor.MoveNoDrop");
    public static final Cursor DefaultLinkNoDrop = load("DnD.Cursor.LinkNoDrop");

    /*
     * The System FlavorMap
     */

    private static final FlavorMap defaultFlavorMap = SystemFlavorMap.getDefaultFlavorMap();

    private static DragSource dflt;

    /**
     * @return the platform DragSource
     */

    public static DragSource getDefaultDragSource() {
	if (dflt == null) dflt = new DragSource();

	return dflt;
    }

    /**
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
     * construct a DragSource
     */

    public DragSource() { super(); }

    /**
     * start a Drag.
     *
     * @param trigger		The DragGestureEvemt that initiated the Drag
     * @param dragCursor	The initial cursor or null for defaults
     * @param dragImage		The image to drag or null
     * @param imageOffset	The offset of the image origin from the hotspot
     *				of the cursor at the instant of the trigger
     * @param transferable	The subject data of the Drag
     * @param dsl		The DragSourceListener
     * @param flavorMap		The FlavorMap to use, or null
     *
     * @throw java.awt.dnd.InvalidDnDOperationException
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
     * start a Drag.
     *
     * @param trigger		The DragGestureEvent that initiated the Drag
     * @param actions		The drag "verbs" appropriate
     * @param dragCursor	The initial cursor or null for defaults
     * @param transferable	The subject data of the Drag
     * @param dsl		The DragSourceListener
     * @param flavorMap		The FlavorMap to use or null
     *
     * @throw java.awt.dnd.InvalidDnDOperationException
     */

    public void startDrag(DragGestureEvent   trigger,
			  Cursor	     dragCursor,
			  Transferable	     transferable,
			  DragSourceListener dsl,
			  FlavorMap	     flavorMap) throws InvalidDnDOperationException {
	startDrag(trigger, dragCursor, null, null, transferable, dsl, flavorMap);
    }

    /**
     * start a Drag.
     *
     * @param trigger		The DragGestureEvent that initiated the Drag
     * @param actions		The drag "verbs" appropriate
     * @param dragCursor	The initial cursor or null for defaults
     * @param dragImage		The image to drag or null
     * @param imageOffset	The offset of the image origin from the hotspot
     *				of the cursor at the instant of the trigger
     * @param transferable	The subject data of the Drag
     * @param dsl		The DragSourceListener
     *
     * @throw java.awt.dnd.InvalidDnDOperationException
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
     * start a Drag.
     *
     * @param trigger		The DragGestureEvent that initiated the Drag
     * @param actions		The drag "verbs" appropriate
     * @param dragCursor	The initial cursor or null for defaults
     * @param transferable	The subject data of the Drag
     * @param dsl		The DragSourceListener
     *
     * @throw java.awt.dnd.InvalidDnDOperationException
     */

    public void startDrag(DragGestureEvent   trigger,
			  Cursor	     dragCursor,
			  Transferable	     transferable,
			  DragSourceListener dsl) throws InvalidDnDOperationException {
	startDrag(trigger, dragCursor, null, null, transferable, dsl, null);
    }

    /**
     * Create the DragSourceContext to handle this Drag.
     * 
     * To incorporate a new DragSourceContext subclass, subclass DragSource and
     * override this method.
     *
     * @param dscp 		The DragSourceContextPeer for this Drag
     * @param trigger	        The DragGestureEvent that triggered the drag
     * @param dragCursor	The initial cursor
     * @param dragImage		The image to drag or null
     * @param imageOffset	The offset of the image origin from the hotspot
     *				of the cursor at the instant of the trigger
     * @param transferable	The subject data of the Drag
     * @param dsl		The DragSourceListener
     */

    protected DragSourceContext createDragSourceContext(DragSourceContextPeer dscp, DragGestureEvent dgl, Cursor dragCursor, Image dragImage, Point imageOffset, Transferable t, DragSourceListener dsl) {
	return new DragSourceContext(dscp, dgl, dragCursor, dragImage, imageOffset, t, dsl);
    }

    /**
     * @return the FlavorMap for this DragSource
     */

    public FlavorMap getFlavorMap() { return flavorMap; }

    /**
     * Creates a new DragSourceRecognizer that implements the specified
     * abstract subclass of DragGestureRecognizer, and sets the specified
     * Component and DragGestureListener on the newly created object.
     *
     * @param recognizerAbstractClass The requested abstract type
     * @param actions		      The permitted source drag actions
     * @param c			      The Component target 
     * @param dgl		      The DragGestureListener to notify
     *
     * @return the new DragGestureRecognizer or null
     */

    public DragGestureRecognizer createDragGestureRecognizer(Class recognizerAbstractClass, Component c, int actions, DragGestureListener dgl) {
	return Toolkit.getDefaultToolkit().createDragGestureRecognizer(recognizerAbstractClass, this, c, actions, dgl);
    }

	
    /**
     * Creates a new DragSourceRecognizer that implements the default
     * abstract subclass of DragGestureRecognizer for this DragSource,
     * and sets the specified Component and DragGestureListener on the
     * newly created object. 
     *
     * For this DragSource the defaut is MouseDragGestureRecognizer
     *
     * @param c	      The Component target for the recognizer
     * @param actions The permitted source actions
     * @param dgl     The DragGestureListener to notify
     *
     * @return the new DragGestureRecognizer or null
     */

    public DragGestureRecognizer createDefaultDragGestureRecognizer(Component c, int actions, DragGestureListener dgl) {
	return Toolkit.getDefaultToolkit().createDragGestureRecognizer(MouseDragGestureRecognizer.class, this, c, actions, dgl);
    }

    /*
     * fields
     */

    private transient FlavorMap flavorMap = defaultFlavorMap;
}
