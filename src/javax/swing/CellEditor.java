/*
 * @(#)CellEditor.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing;

import java.util.EventObject;
import javax.swing.event.*;

/**
 * This interface defines the methods any general editor should be able
 * to implement. <p>
 *
 * Having this interface enables complex components (the client of the
 * editor) such as JList, JTree, and JTable to allow any generic editor to
 * edit values in a table cell, or tree cell, etc.  Without this generic
 * editor interface, JTable would have to know about specific editors,
 * such as JTextField, JCheckBox, JComboBox, etc.  In addition, without
 * this interface, clients of editors such as JTable would not be able
 * to work with any editors developed in the future by the user
 * or a 3rd party ISV. <p>
 *
 * To use this interface, a developer creating a new editor can have the
 * new component implement the interface.  Or the developer can
 * choose a wrapper based approch and provide a companion object which
 * implements the CellEditor interface (See JCellEditor for example).  The
 * wrapper approch is particularly useful if the user want to use a
 * 3rd party ISV editor with JTable, but the ISV didn't implement the
 * CellEditor interface.  The user can simply create an object that
 * contains an instance of the 3rd party editor object and "translate"
 * the CellEditor API into the 3rd party editor's API.
 *
 * @see javax.swing.event.CellEditorListener
 *
 * @version 1.16 08/26/98
 * @author Alan Chung
 */
public interface CellEditor {

    /** Returns the value contained in the editor**/
    public Object getCellEditorValue();

    /**
     * Ask the editor if it can start editing using <I>anEvent</I>.
     * <I>anEvent</I> is in the invoking component coordinate system.
     * The editor can not assume the Component returned by
     * getCellEditorComponent() is installed.  This method is intended
     * for the use of client to avoid the cost of setting up and installing
     * the editor component if editing is not possible.
     * If editing can be started this method returns true.
     * 
     * @param	anEvent		the event the editor should use to consider
     *				whether to begin editing or not.
     * @return	true if editing can be started.
     * @see #shouldSelectCell
     */
    public boolean isCellEditable(EventObject anEvent);

    /**
     * Tell the editor to start editing using <I>anEvent</I>.  It is
     * up to the editor if it want to start editing in different states
     * depending on the exact type of <I>anEvent</I>.  For example, with
     * a text field editor, if the event is a mouse event the editor
     * might start editing with the cursor at the clicked point.  If
     * the event is a keyboard event, it might want replace the value
     * of the text field with that first key, etc.  <I>anEvent</I>
     * is in the invoking component's coordinate system.  A null value
     * is a valid parameter for <I>anEvent</I>, and it is up to the editor
     * to determine what is the default starting state.  For example,
     * a text field editor might want to select all the text and start
     * editing if <I>anEvent</I> is null.  The editor can assume
     * the Component returned by getCellEditorComponent() is properly
     * installed in the clients Component hierarchy before this method is
     * called. <p>
     *
     * The return value of shouldSelectCell() is a boolean indicating whether
     * the editing cell should be selected or not.  Typically, the return
     * value is true, because is most cases the editing cell should be
     * selected.  However, it is useful to return false to keep the selection
     * from changing for some types of edits.  eg. A table that contains
     * a column of check boxes, the user might want to be able to change
     * those checkboxes without altering the selection.  (See Netscape
     * Communicator for just such an example)  Of course, it is up to
     * the client of the editor to use the return value, but it doesn't
     * need to if it doesn't want to.
     *
     * @param	anEvent		the event the editor should use to start
     *				editing.
     * @return	true if the editor would like the editing cell to be selected
     * @see #isCellEditable
     */
    public boolean shouldSelectCell(EventObject anEvent);

    /**
     * Tell the editor to stop editing and accept any partially edited
     * value as the value of the editor.  The editor returns false if
     * editing was not stopped, useful for editors which validates and
     * can not accept invalid entries.
     *
     * @return	true if editing was stopped
     */
    public boolean stopCellEditing();

    /**
     * Tell the editor to cancel editing and not accept any partially
     * edited value.
     */
    public void cancelCellEditing();

    /**
     * Add a listener to the list that's notified when the editor starts,
     * stops, or cancels editing.
     *
     * @param	l		the CellEditorListener
     */  
    public void addCellEditorListener(CellEditorListener l);

    /**
     * Remove a listener from the list that's notified
     *
     * @param	l		the CellEditorListener
     */  
    public void removeCellEditorListener(CellEditorListener l);
}
