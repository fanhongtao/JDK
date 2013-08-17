/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.dnd;

import java.util.EventListener;

import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;

/**
 * The <code>DragSourceListener</code> defines the 
 * event interface for originators of
 * Drag and Drop operations to track the state of the user's gesture, and to
 * provide appropriate &quot;drag over&quot; 
 * feedback to the user throughout the
 * Drag and Drop operation.
 *
 * @version 	1.16, 02/06/02
 * @since 1.2
 */

public interface DragSourceListener extends EventListener {

    /**
     * Called as the hotspot enters a platform dependent drop site.
     * This method is invoked when the following conditions are true:
     * <UL>
     * <LI>The logical cursor's hotspot initially intersects
     * a GUI <code>Component</code>'s  visible geometry.
     * <LI>That <code>Component</code> has an active 
     * <code>DropTarget</code> associated with it.
     * <LI>The <code>DropTarget</code>'s registered 
     * <code>DropTargetListener</code> dragEnter() method is invoked and
     * returns successfully.
     * <LI>The registered <code>DropTargetListener</code> invokes 
     * the <code>DropTargetDragEvent</code>'s acceptDrag() method to 
     * accept the drag based upon interrogation of the source's 
     * potential drop action(s) and available data types 
     * (<code>DataFlavor</code>s).
     * </UL>
     *<P>
     *@param dsde the <code>DragSourceDragEvent</code>
     */

    void dragEnter(DragSourceDragEvent dsde);

    /**
     * Called as the hotspot moves over a platform dependent drop site.
     * This method is invoked when the following conditions
     * are true:
     *<UL>
     *<LI>The cursor's logical hotspot has moved but still
     * intersects the visible geometry of the <code>Component</code>
     * associated with the previous dragEnter() invocation.
     * <LI>That <code>Component</code> still has a 
     * <code>DropTarget</code> associated with it.
     * <LI>That <code>DropTarget</code> is still active.
     * <LI>The <code>DropTarget</code>'s registered
     * <code>DropTargetListener</code> dragOver() method 
     * is invoked and returns successfully.
     * <LI>The <code>DropTarget</code> does not reject 
     * the drag via rejectDrag()
     * </UL>
     * <P>
     * @param dsde the <code>DragSourceDragEvent</code>
     */

    void dragOver(DragSourceDragEvent dsde);

    /**
     * Called when the user has modified the drop gesture.
     * This method is invoked when the state of the input
     * device(s) that the user is interacting with changes.
     * Such devices are typically the mouse buttons or keyboard
     * modifiers that the user is interacting with.
     * <P>
     * @param dsde the <code>DragSourceDragEvent</code>
     */

    void dropActionChanged(DragSourceDragEvent dsde);

    /**
     * Called as the hotspot exits a platform dependent drop site.
     * This method is invoked when the following conditions
     * are true:
     * <UL>
     * <LI>The cursor's logical hotspot no longer 
     * intersects the visible geometry of the <code>Component</code>
     * associated with the previous dragEnter() invocation.
     * </UL>
     * OR
     * <UL>
     * <LI>The <code>Component</code> that the logical cursor's hotspot
     * intersected that resulted in the previous dragEnter() invocation
     * no longer has an active <code>DropTarget</code> or 
     * <code>DropTargetListener</code> associated with it.
     * </UL>
     * OR
     * <UL>
     * <LI> The current <code>DropTarget</code>'s 
     * <code>DropTargetListener</code> has invoked rejectDrag()
     * since the last dragEnter() or dragOver() invocation.
     * </UL>
     * <P>
     * @param dse the <code>DragSourceEvent</code>
     */

    void dragExit(DragSourceEvent dse);

    /**
     * This method is invoked to signify that the Drag and Drop
     * operation is complete. The getDropSuccess() method of
     * the <code>DragSourceDropEvent</code> can be used to 
     * determine the termination state. The getDropAction() method
     * returns the operation that the <code>DropTarget</code>
     * selected (via the DropTargetDropEvent acceptDrop() parameter)
     * to apply to the Drop operation. Once this method is complete, the
     * current <code>DragSourceContext</code> and
     * associated resources become invalid.
     * <P>
     * @param dsde the <code>DragSourceDropEvent</code>
     */

    void dragDropEnd(DragSourceDropEvent dsde);
}






