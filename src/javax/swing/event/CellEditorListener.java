/*
 * @(#)CellEditorListener.java	1.13 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.event;

import javax.swing.event.ChangeEvent;
import java.util.EventListener;

/**
 * CellEditorListener defines the interface for an object that listens
 * to changes in a CellEditor
 *
 * @version 1.13 12/19/03
 * @author Alan Chung
 */

public interface CellEditorListener extends java.util.EventListener {

    /** This tells the listeners the editor has ended editing */
    public void editingStopped(ChangeEvent e);

    /** This tells the listeners the editor has canceled editing */
    public void editingCanceled(ChangeEvent e);
}

