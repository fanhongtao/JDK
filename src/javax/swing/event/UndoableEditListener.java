/*
 * @(#)UndoableEditListener.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing.event;

import javax.swing.undo.*;

/**
 * Interface implemented by a class interested in hearing about
 * undoable operations.
 *
 * @version 1.11 08/26/98
 * @author Ray Ryan
 */

public interface UndoableEditListener extends java.util.EventListener {

    /**
     * An undoable edit happened
     */
    void undoableEditHappened(UndoableEditEvent e);
}
