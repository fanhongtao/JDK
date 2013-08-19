/*
 * @(#)CellEditorListener.java	1.12 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.event;

import javax.swing.event.ChangeEvent;
import java.util.EventListener;

/**
 * CellEditorListener defines the interface for an object that listens
 * to changes in a CellEditor
 *
 * @version 1.12 01/23/03
 * @author Alan Chung
 */

public interface CellEditorListener extends java.util.EventListener {

    /** This tells the listeners the editor has ended editing */
    public void editingStopped(ChangeEvent e);

    /** This tells the listeners the editor has canceled editing */
    public void editingCanceled(ChangeEvent e);
}

