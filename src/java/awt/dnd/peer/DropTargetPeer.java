/*
 * @(#)DropTargetPeer.java	1.10 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.dnd.peer;

import java.awt.dnd.DropTarget;

/**
 * <p>
 * The DropTargetPeer class is the interface to the platform dependent
 * DnD facilities. Since the DnD system is based on the native platform's
 * facilities, a DropTargetPeer will be associated with a ComponentPeer
 * of the nearsest enclosing native Container (in the case of lightweights)
 * </p>
 *
 * @version 	1.10, 12/19/03
 * @since 1.2
 *
 */

public interface DropTargetPeer {

    /**
     * Add the DropTarget to the System
     *
     * @param dt The DropTarget effected
     */

    void addDropTarget(DropTarget dt);

    /**
     * Remove the DropTarget from the system
     *
     * @param dt The DropTarget effected
     */

    void removeDropTarget(DropTarget dt);
}
