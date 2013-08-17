/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.dnd;

import java.util.EventListener;

import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;

/**
 * The <code>DropTargetListener</code> interface 
 * is the callback interface used by the
 * <code>DropTarget</code> class to provide 
 * notification of DnD operations that involve
 * the subject <code>DropTarget</code>. Methods of
 * this interface may be implemented to provide
 * "drag under" visual feedback to the user throughout
 * the Drag and Drop operation.
 *
 * @version 	1.17, 02/06/02
 * @since 1.2
 */

public interface DropTargetListener extends EventListener {

    /**
     * Called when a drag operation has 
     * encountered the <code>DropTarget</code>.
     * <P>
     * @param dtde the <code>DropTargetDragEvent</code> 
     */

    void dragEnter(DropTargetDragEvent dtde);

    /**
     * Called when a drag operation is ongoing 
     * on the <code>DropTarget</code>.
     * <P>
     * @param dtde the <code>DropTargetDragEvent</code> 
     */

    void dragOver(DropTargetDragEvent dtde);

    /**
     * Called if the user has modified 
     * the current drop gesture.
     * <P>
     * @param dtde the <code>DropTargetDragEvent</code>
     */

    void dropActionChanged(DropTargetDragEvent dtde);

    /**
     * The drag operation has departed 
     * the <code>DropTarget</code> without dropping.
     * <P>
     * @param dte the <code>DropTargetEvent</code> 
     */

    void dragExit(DropTargetEvent dte);

    /**
     * The drag operation has terminated 
     * with a drop on this <code>DropTarget</code>.
     * This method is responsible for undertaking
     * the transfer of the data associated with the
     * gesture. The <code>DropTargetDropEvent</code> 
     * provides a means to obtain a <code>Transferable</code>
     * object that represents the data object(s) to 
     * be transfered.<P>
     * From this method, the <code>DropTargetListener</code>
     * shall accept or reject the drop via the   
     * acceptDrop(int dropAction) or rejectDrop() methods of the 
     * <code>DropTargetDropEvent</code> parameter.
     * <P>
     * Subsequent to acceptDrop(), but not before,
     * <code>DropTargetDropEvent</code>'s getTransferable()
     * method may be invoked, and data transfer may be 
     * performed via the returned <code>Transferable</code>'s 
     * getTransferData() method.
     * <P>
     * At the completion of a drop, an implementation
     * of this method is required to signal the success/failure
     * of the drop by passing an appropriate
     * <code>boolean</code> to the <code>DropTargetDropEvent</code>'s
     * dropComplete(boolean success) method.
     * <P>
     * Note: The actual processing of the data transfer is not
     * required to finish before this method returns. It may be
     * deferred until later.
     * <P>
     * @param dtde the <code>DropTargetDropEvent</code> 
     */

    void drop(DropTargetDropEvent dtde);
}







