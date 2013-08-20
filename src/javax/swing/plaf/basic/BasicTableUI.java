/*
 * @(#)BasicTableUI.java	1.140 04/06/14
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.TooManyListenersException;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.text.*;
import javax.swing.table.*;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import sun.swing.DefaultLookup;
import sun.swing.UIAction;

/**
 * BasicTableUI implementation
 *
 * @version 1.140 06/14/04
 * @author Philip Milne
 */
public class BasicTableUI extends TableUI
{

//
// Instance Variables
//

    // The JTable that is delegating the painting to this UI.
    protected JTable table;
    protected CellRendererPane rendererPane;

    // Listeners that are attached to the JTable
    protected KeyListener keyListener;
    protected FocusListener focusListener;
    protected MouseInputListener mouseInputListener;

    private Handler handler;

//
//  Helper class for keyboard actions
//

    private static class Actions extends UIAction {
        private static final String CANCEL_EDITING = "cancel";
        private static final String SELECT_ALL = "selectAll";
        private static final String CLEAR_SELECTION = "clearSelection";
        private static final String START_EDITING = "startEditing";

        private static final String NEXT_ROW = "selectNextRow";
        private static final String NEXT_ROW_CELL = "selectNextRowCell";
	private static final String NEXT_ROW_EXTEND_SELECTION =
                "selectNextRowExtendSelection";
        private static final String NEXT_ROW_CHANGE_LEAD =
                "selectNextRowChangeLead";
        private static final String PREVIOUS_ROW = "selectPreviousRow";
        private static final String PREVIOUS_ROW_CELL = "selectPreviousRowCell";
	private static final String PREVIOUS_ROW_EXTEND_SELECTION =
                "selectPreviousRowExtendSelection";
        private static final String PREVIOUS_ROW_CHANGE_LEAD =
                "selectPreviousRowChangeLead";

        private static final String NEXT_COLUMN = "selectNextColumn";
        private static final String NEXT_COLUMN_CELL = "selectNextColumnCell";
	private static final String NEXT_COLUMN_EXTEND_SELECTION =
                "selectNextColumnExtendSelection";
        private static final String NEXT_COLUMN_CHANGE_LEAD =
                "selectNextColumnChangeLead";
        private static final String PREVIOUS_COLUMN = "selectPreviousColumn";
        private static final String PREVIOUS_COLUMN_CELL =
                "selectPreviousColumnCell";
	private static final String PREVIOUS_COLUMN_EXTEND_SELECTION =
                "selectPreviousColumnExtendSelection";
        private static final String PREVIOUS_COLUMN_CHANGE_LEAD =
                "selectPreviousColumnChangeLead";

        private static final String SCROLL_LEFT_CHANGE_SELECTION =
                "scrollLeftChangeSelection";
        private static final String SCROLL_LEFT_EXTEND_SELECTION =
                "scrollLeftExtendSelection";
        private static final String SCROLL_RIGHT_CHANGE_SELECTION =
                "scrollRightChangeSelection";
        private static final String SCROLL_RIGHT_EXTEND_SELECTION =
                "scrollRightExtendSelection";

	private static final String SCROLL_UP_CHANGE_SELECTION =
                "scrollUpChangeSelection";
        private static final String SCROLL_UP_EXTEND_SELECTION =
                "scrollUpExtendSelection";
        private static final String SCROLL_DOWN_CHANGE_SELECTION =
                "scrollDownChangeSelection";
        private static final String SCROLL_DOWN_EXTEND_SELECTION =
                "scrollDownExtendSelection";

        private static final String FIRST_COLUMN =
                "selectFirstColumn";
        private static final String FIRST_COLUMN_EXTEND_SELECTION =
                "selectFirstColumnExtendSelection";
        private static final String LAST_COLUMN =
                "selectLastColumn";
        private static final String LAST_COLUMN_EXTEND_SELECTION =
                "selectLastColumnExtendSelection";

        private static final String FIRST_ROW =
                "selectFirstRow";
        private static final String FIRST_ROW_EXTEND_SELECTION =
                "selectFirstRowExtendSelection";
        private static final String LAST_ROW =
                "selectLastRow";
        private static final String LAST_ROW_EXTEND_SELECTION =
                "selectLastRowExtendSelection";

        // add the lead item to the selection without changing lead or anchor
        private static final String ADD_TO_SELECTION = "addToSelection";

        // toggle the selected state of the lead item and move the anchor to it
        private static final String TOGGLE_AND_ANCHOR = "toggleAndAnchor";

        // extend the selection to the lead item
        private static final String EXTEND_TO = "extendTo";

        // move the anchor to the lead and ensure only that item is selected
        private static final String MOVE_SELECTION_TO = "moveSelectionTo";

        protected int dx;
        protected int dy;
	protected boolean extend;
	protected boolean inSelection;
        protected boolean forwards;
        protected boolean vertically;
        protected boolean toLimit;

	protected int leadRow;
	protected int leadColumn;

        Actions(String name) {
            super(name);
        }

        Actions(String name, int dx, int dy, boolean extend,
                boolean inSelection) {
            super(name);

            // Actions spcifying true for "inSelection" are
            // fairly sensitive to bad parameter values. They require
            // that one of dx and dy be 0 and the other be -1 or 1.
            // Bogus parameter values could cause an infinite loop.
            // To prevent any problems we massage the params here
            // and complain if we get something we can't deal with.
            if (inSelection) {
                this.inSelection = true;

                // look at the sign of dx and dy only
                dx = sign(dx);
                dy = sign(dy);

                // make sure one is zero, but not both
                assert (dx == 0 || dy == 0) && !(dx == 0 && dy == 0);
            }

            this.dx = dx;
            this.dy = dy;
            this.extend = extend;
        }

        Actions(String name, boolean extend, boolean forwards,
                boolean vertically, boolean toLimit) {
            this(name, 0, 0, extend, false);
            this.forwards = forwards;
            this.vertically = vertically;
            this.toLimit = toLimit;
        }

	private static int clipToRange(int i, int a, int b) {
	    return Math.min(Math.max(i, a), b-1);
	}

	private void moveWithinTableRange(JTable table, int dx, int dy) {
            leadRow = clipToRange(leadRow+dy, 0, table.getRowCount());
            leadColumn = clipToRange(leadColumn+dx, 0, table.getColumnCount());
	}

        private static int sign(int num) {
            return (num < 0) ? -1 : ((num == 0) ? 0 : 1);
        }

        /**
         * Called to move within the selected range of the given JTable.
         * This method uses the table's notion of selection, which is
         * important to allow the user to navigate between items visually
         * selected on screen. This notion may or may not be the same as
         * what could be determined by directly querying the selection models.
         * It depends on certain table properties (such as whether or not
         * row or column selection is allowed). When performing modifications,
         * it is recommended that caution be taken in order to preserve
         * the intent of this method, especially when deciding whether to
         * query the selection models or interact with JTable directly.
         */
	private boolean moveWithinSelectedRange(JTable table, int dx, int dy,
                ListSelectionModel rsm, ListSelectionModel csm) {

            // Note: The Actions constructor ensures that only one of
            // dx and dy is 0, and the other is either -1 or 1

            // find out how many items the table is showing as selected
            // and the range of items to navigate through
            int totalCount;
            int minX, maxX, minY, maxY;

            boolean rs = table.getRowSelectionAllowed();
            boolean cs = table.getColumnSelectionAllowed();

            // both column and row selection
            if (rs && cs) {
                totalCount = table.getSelectedRowCount() * table.getSelectedColumnCount();
                minX = csm.getMinSelectionIndex();
                maxX = csm.getMaxSelectionIndex();
                minY = rsm.getMinSelectionIndex();
                maxY = rsm.getMaxSelectionIndex();
            // row selection only
            } else if (rs) {
                totalCount = table.getSelectedRowCount();
                minX = 0;
                maxX = table.getColumnCount() - 1;
                minY = rsm.getMinSelectionIndex();
                maxY = rsm.getMaxSelectionIndex();
            // column selection only
            } else if (cs) {
                totalCount = table.getSelectedColumnCount();
                minX = csm.getMinSelectionIndex();
                maxX = csm.getMaxSelectionIndex();
                minY = 0;
                maxY = table.getRowCount() - 1;
            // no selection allowed
            } else {
                totalCount = 0;
                // A bogus assignment to stop javac from complaining
                // about unitialized values. In this case, these
                // won't even be used.
                minX = maxX = minY = maxY = 0;
            }

            // For some cases, there is no point in trying to stay within the
            // selected area. Instead, move outside the selection, wrapping at
            // the table boundaries. The cases are:
            boolean stayInSelection;

            // - nothing selected
            if (totalCount == 0 ||
                    // - one item selected, and the lead is already selected
                    (totalCount == 1 && table.isCellSelected(leadRow, leadColumn))) {

                stayInSelection = false;

                maxX = table.getColumnCount() - 1;
                maxY = table.getRowCount() - 1;

                // the mins are calculated like this in case the max is -1
                minX = Math.min(0, maxX);
                minY = Math.min(0, maxY);
            } else {
                stayInSelection = true;
            }

            // In cases where the lead is not within the search range,
            // we need to bring it within one cell for the the search
            // to work properly. Check these here.
            leadRow = Math.min(Math.max(leadRow, minY - 1), maxY + 1);
            leadColumn = Math.min(Math.max(leadColumn, minX - 1), maxX + 1);

            // find the next position, possibly looping until it is selected
            do {
                calcNextPos(dx, minX, maxX, dy, minY, maxY);
            } while (stayInSelection && !table.isCellSelected(leadRow, leadColumn));

            return stayInSelection;
	}

        /**
         * Find the next lead row and column based on the given
         * dx/dy and max/min values.
         */
        private void calcNextPos(int dx, int minX, int maxX,
                                 int dy, int minY, int maxY) {

            if (dx != 0) {
                leadColumn += dx;
                if (leadColumn > maxX) {
                    leadColumn = minX;
                    leadRow++;
                    if (leadRow > maxY) {
                        leadRow = minY;
                    }
                } else if (leadColumn < minX) {
                    leadColumn = maxX;
                    leadRow--;
                    if (leadRow < minY) {
                        leadRow = maxY;
                    }
                }
            } else {
                leadRow += dy;
                if (leadRow > maxY) {
                    leadRow = minY;
                    leadColumn++;
                    if (leadColumn > maxX) {
                        leadColumn = minX;
                    }
                } else if (leadRow < minY) {
                    leadRow = maxY;
                    leadColumn--;
                    if (leadColumn < minX) {
                        leadColumn = maxX;
                    }
                }
            }
        }

        public void actionPerformed(ActionEvent e) {
            String key = getName();
            JTable table = (JTable)e.getSource();

            ListSelectionModel rsm = table.getSelectionModel();
            leadRow = rsm.getLeadSelectionIndex();

            ListSelectionModel csm = table.getColumnModel().getSelectionModel();
            leadColumn =   csm.getLeadSelectionIndex();

            if (!table.getComponentOrientation().isLeftToRight()) {
                if (key == SCROLL_LEFT_CHANGE_SELECTION ||
                        key == SCROLL_LEFT_EXTEND_SELECTION) {
                    forwards = true;
                } else if (key == SCROLL_RIGHT_CHANGE_SELECTION ||
                        key == SCROLL_RIGHT_EXTEND_SELECTION) {
                    forwards = false;
                }
            }

            if (key == SCROLL_LEFT_CHANGE_SELECTION ||        // Paging Actions
                    key == SCROLL_LEFT_EXTEND_SELECTION ||
                    key == SCROLL_RIGHT_CHANGE_SELECTION ||
                    key == SCROLL_RIGHT_EXTEND_SELECTION ||
	            key == SCROLL_UP_CHANGE_SELECTION ||
                    key == SCROLL_UP_EXTEND_SELECTION ||
                    key == SCROLL_DOWN_CHANGE_SELECTION ||
                    key == SCROLL_DOWN_EXTEND_SELECTION ||
                    key == FIRST_COLUMN ||
                    key == FIRST_COLUMN_EXTEND_SELECTION ||
                    key == FIRST_ROW ||
                    key == FIRST_ROW_EXTEND_SELECTION ||
                    key == LAST_COLUMN ||
                    key == LAST_COLUMN_EXTEND_SELECTION ||
                    key == LAST_ROW ||
                    key == LAST_ROW_EXTEND_SELECTION) {
                if (toLimit) {
                    if (vertically) {
                        int rowCount = table.getRowCount();
                        this.dx = 0;
                        this.dy = forwards ? rowCount : -rowCount;
                    }
                    else {
                        int colCount = table.getColumnCount();
                        this.dx = forwards ? colCount : -colCount;
                        this.dy = 0;
                    }
                }
                else {
                    if (!(table.getParent().getParent() instanceof
                            JScrollPane)) {
                        return;
                    }
    
                    Dimension delta = table.getParent().getSize();

                    int start = (vertically) ? rsm.getLeadSelectionIndex() :
                                               csm.getLeadSelectionIndex();

                    if (vertically) {
                        Rectangle r = table.getCellRect(start, 0, true);
                        r.y += forwards ? delta.height : -delta.height;
                        this.dx = 0;
                        int newRow = table.rowAtPoint(r.getLocation());
                        if (newRow == -1 && forwards) {
                            newRow = table.getRowCount();
                        }
                        this.dy = newRow - start;
                    }
                    else {
                        Rectangle r = table.getCellRect(0, start, true);
                        r.x += forwards ? delta.width : -delta.width;
                        int newColumn = table.columnAtPoint(r.getLocation());
                        if (newColumn == -1 && forwards) {
                            newColumn = table.getColumnCount();
                        }
                        this.dx = newColumn - start;
                        this.dy = 0;
                    }
                }
            }
            if (key == NEXT_ROW ||  // Navigate Actions
                    key == NEXT_ROW_CELL ||
                    key == NEXT_ROW_EXTEND_SELECTION ||
                    key == NEXT_ROW_CHANGE_LEAD ||
                    key == NEXT_COLUMN ||
                    key == NEXT_COLUMN_CELL ||
                    key == NEXT_COLUMN_EXTEND_SELECTION ||
                    key == NEXT_COLUMN_CHANGE_LEAD ||
                    key == PREVIOUS_ROW ||
                    key == PREVIOUS_ROW_CELL ||
                    key == PREVIOUS_ROW_EXTEND_SELECTION ||
                    key == PREVIOUS_ROW_CHANGE_LEAD ||
                    key == PREVIOUS_COLUMN ||
                    key == PREVIOUS_COLUMN_CELL ||
                    key == PREVIOUS_COLUMN_EXTEND_SELECTION ||
                    key == PREVIOUS_COLUMN_CHANGE_LEAD ||
                    // Paging Actions.
                    key == SCROLL_LEFT_CHANGE_SELECTION ||
                    key == SCROLL_LEFT_EXTEND_SELECTION ||
                    key == SCROLL_RIGHT_CHANGE_SELECTION ||
                    key == SCROLL_RIGHT_EXTEND_SELECTION ||
	            key == SCROLL_UP_CHANGE_SELECTION ||
                    key == SCROLL_UP_EXTEND_SELECTION ||
                    key == SCROLL_DOWN_CHANGE_SELECTION ||
                    key == SCROLL_DOWN_EXTEND_SELECTION ||
                    key == FIRST_COLUMN ||
                    key == FIRST_COLUMN_EXTEND_SELECTION ||
                    key == FIRST_ROW ||
                    key == FIRST_ROW_EXTEND_SELECTION ||
                    key == LAST_COLUMN ||
                    key == LAST_COLUMN_EXTEND_SELECTION ||
                    key == LAST_ROW ||
                    key == LAST_ROW_EXTEND_SELECTION) {

                if (table.isEditing() &&
                        !table.getCellEditor().stopCellEditing()) {
                    return;
                }
    
                // Unfortunately, this strategy introduces bugs because
                // of the asynchronous nature of requestFocus() call below.
                // Introducing a delay with invokeLater() makes this work
                // in the typical case though race conditions then allow
                // focus to disappear altogether. The right solution appears
                // to be to fix requestFocus() so that it queues a request
                // for the focus regardless of who owns the focus at the
                // time the call to requestFocus() is made. The optimisation
                // to ignore the call to requestFocus() when the component
                // already has focus may ligitimately be made as the
                // request focus event is dequeued, not before.
    
                // boolean wasEditingWithFocus = table.isEditing() &&
                // table.getEditorComponent().isFocusOwner();

                boolean changeLead = false;
                if (key == NEXT_ROW_CHANGE_LEAD || key == PREVIOUS_ROW_CHANGE_LEAD) {
                    changeLead = (rsm.getSelectionMode()
                                     == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                } else if (key == NEXT_COLUMN_CHANGE_LEAD || key == PREVIOUS_COLUMN_CHANGE_LEAD) {
                    changeLead = (csm.getSelectionMode()
                                     == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                }

                if (changeLead) {
                    moveWithinTableRange(table, dx, dy);
                    if (dy != 0) {
                        // casting should be safe since the action is only enabled
                        // for DefaultListSelectionModel
                        ((DefaultListSelectionModel)rsm).moveLeadSelectionIndex(leadRow);
                    } else {
                        // casting should be safe since the action is only enabled
                        // for DefaultListSelectionModel
                        ((DefaultListSelectionModel)csm).moveLeadSelectionIndex(leadColumn);
                    }

                    Rectangle cellRect = table.getCellRect(leadRow, leadColumn, false);
                    if (cellRect != null) {
                        table.scrollRectToVisible(cellRect);
                    }
                } else if (!inSelection) {
                    moveWithinTableRange(table, dx, dy);
                    table.changeSelection(leadRow, leadColumn, false, extend);
                }
                else {
                    if (moveWithinSelectedRange(table, dx, dy, rsm, csm)) {
                        // this is the only way we have to set both the lead
                        // and the anchor without changing the selection
                        if (rsm.isSelectedIndex(leadRow)) {
                            rsm.addSelectionInterval(leadRow, leadRow);
                        } else {
                            rsm.removeSelectionInterval(leadRow, leadRow);
                        }

                        if (csm.isSelectedIndex(leadColumn)) {
                            csm.addSelectionInterval(leadColumn, leadColumn);
                        } else {
                            csm.removeSelectionInterval(leadColumn, leadColumn);
                        }

                        Rectangle cellRect = table.getCellRect(leadRow, leadColumn, false);
                        if (cellRect != null) {
                            table.scrollRectToVisible(cellRect);
                        }
                    }
                    else {
                        table.changeSelection(leadRow, leadColumn,
                                false, false);
                    }
                }
    
                /*
                if (wasEditingWithFocus) {
                    table.editCellAt(leadRow, leadColumn);
                    final Component editorComp = table.getEditorComponent();
                    if (editorComp != null) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                editorComp.requestFocus();
                            }
                        });
                    }
                }
                */
            } else if (key == CANCEL_EDITING) {
                table.removeEditor();
            } else if (key == SELECT_ALL) {
                table.selectAll();
            } else if (key == CLEAR_SELECTION) {
                table.clearSelection();
            } else if (key == START_EDITING) {
                if (!table.hasFocus()) {
                    CellEditor cellEditor = table.getCellEditor();
                    if (cellEditor != null && !cellEditor.stopCellEditing()) {
                        return;
                    }
                    table.requestFocus();
                    return;
                }
                table.editCellAt(leadRow, leadColumn);
                Component editorComp = table.getEditorComponent();
                if (editorComp != null) {
                    editorComp.requestFocus();
                }
            } else if (key == ADD_TO_SELECTION) {
                if (!table.isCellSelected(leadRow, leadColumn)) {
                    int oldAnchorRow = rsm.getAnchorSelectionIndex();
                    int oldAnchorColumn = csm.getAnchorSelectionIndex();
                    rsm.setValueIsAdjusting(true);
                    csm.setValueIsAdjusting(true);
                    table.changeSelection(leadRow, leadColumn, true, false);
                    rsm.setAnchorSelectionIndex(oldAnchorRow);
                    csm.setAnchorSelectionIndex(oldAnchorColumn);
                    rsm.setValueIsAdjusting(false);
                    csm.setValueIsAdjusting(false);
                }
            } else if (key == TOGGLE_AND_ANCHOR) {
                table.changeSelection(leadRow, leadColumn, true, false);
            } else if (key == EXTEND_TO) {
                table.changeSelection(leadRow, leadColumn, false, true);
            } else if (key == MOVE_SELECTION_TO) {
                table.changeSelection(leadRow, leadColumn, false, false);
            }
        }

        public boolean isEnabled(Object sender) {
            String key = getName();

            if (sender instanceof JTable &&
                Boolean.TRUE.equals(((JTable)sender).getClientProperty("Table.isFileList"))) {
                if (key == NEXT_COLUMN ||
                        key == NEXT_COLUMN_CELL ||
                        key == NEXT_COLUMN_EXTEND_SELECTION ||
                        key == NEXT_COLUMN_CHANGE_LEAD ||
                        key == PREVIOUS_COLUMN ||
                        key == PREVIOUS_COLUMN_CELL ||
                        key == PREVIOUS_COLUMN_EXTEND_SELECTION ||
                        key == PREVIOUS_COLUMN_CHANGE_LEAD ||
                        key == SCROLL_LEFT_CHANGE_SELECTION ||
                        key == SCROLL_LEFT_EXTEND_SELECTION ||
                        key == SCROLL_RIGHT_CHANGE_SELECTION ||
                        key == SCROLL_RIGHT_EXTEND_SELECTION ||
                        key == FIRST_COLUMN ||
                        key == FIRST_COLUMN_EXTEND_SELECTION ||
                        key == LAST_COLUMN ||
                        key == LAST_COLUMN_EXTEND_SELECTION ||
                        key == NEXT_ROW_CELL ||
                        key == PREVIOUS_ROW_CELL) {

                    return false;
                }
            }

            if (key == CANCEL_EDITING && sender instanceof JTable) {
                return ((JTable)sender).isEditing();
            } else if (key == NEXT_ROW_CHANGE_LEAD ||
                       key == PREVIOUS_ROW_CHANGE_LEAD) {
                // discontinuous selection actions are only enabled for
                // DefaultListSelectionModel
                return sender != null &&
                       ((JTable)sender).getSelectionModel()
                           instanceof DefaultListSelectionModel;
            } else if (key == NEXT_COLUMN_CHANGE_LEAD ||
                       key == PREVIOUS_COLUMN_CHANGE_LEAD) {
                // discontinuous selection actions are only enabled for
                // DefaultListSelectionModel
                return sender != null &&
                       ((JTable)sender).getColumnModel().getSelectionModel()
                           instanceof DefaultListSelectionModel;
            } else if (key == ADD_TO_SELECTION && sender instanceof JTable) {
                // This action is typically bound to SPACE.
                // If the table is already in an editing mode, SPACE should
                // simply enter a space character into the table, and not
                // select a cell. Likewise, if the lead cell is already selected
                // then hitting SPACE should just enter a space character
                // into the cell and begin editing. In both of these cases
                // this action will be disabled.
                JTable table = (JTable)sender;
                int leadRow = table.getSelectionModel().
                                  getLeadSelectionIndex();
                int leadCol = table.getColumnModel().getSelectionModel().
                                  getLeadSelectionIndex();
                return !(table.isEditing() || table.isCellSelected(leadRow, leadCol));
            }

            return true;
        }
    }


//
//  The Table's Key listener
//

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicTableUI.
     * <p>As of Java 2 platform v1.3 this class is no longer used.
     * Instead <code>JTable</code>
     * overrides <code>processKeyBinding</code> to dispatch the event to
     * the current <code>TableCellEditor</code>.
     */
     public class KeyHandler implements KeyListener {
        // NOTE: This class exists only for backward compatability. All
        // its functionality has been moved into Handler. If you need to add
        // new functionality add it to the Handler, but make sure this      
        // class calls into the Handler.
        public void keyPressed(KeyEvent e) {
            getHandler().keyPressed(e);
        }

        public void keyReleased(KeyEvent e) {
            getHandler().keyReleased(e);
        }

        public void keyTyped(KeyEvent e) {
            getHandler().keyTyped(e);
        }
    }

//
//  The Table's focus listener
//

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicTableUI.
     */
    public class FocusHandler implements FocusListener {
        // NOTE: This class exists only for backward compatability. All
        // its functionality has been moved into Handler. If you need to add
        // new functionality add it to the Handler, but make sure this      
        // class calls into the Handler.
        public void focusGained(FocusEvent e) {
            getHandler().focusGained(e);
        }

        public void focusLost(FocusEvent e) {
            getHandler().focusLost(e);
        }
    }

//
//  The Table's mouse and mouse motion listeners
//

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicTableUI.
     */
    public class MouseInputHandler implements MouseInputListener {
        // NOTE: This class exists only for backward compatability. All
        // its functionality has been moved into Handler. If you need to add
        // new functionality add it to the Handler, but make sure this      
        // class calls into the Handler.
        public void mouseClicked(MouseEvent e) {
            getHandler().mouseClicked(e);
        }

        public void mousePressed(MouseEvent e) {
            getHandler().mousePressed(e);
	}

        public void mouseReleased(MouseEvent e) {
            getHandler().mouseReleased(e);
        }

        public void mouseEntered(MouseEvent e) {
            getHandler().mouseEntered(e);
        }

        public void mouseExited(MouseEvent e) {
            getHandler().mouseExited(e);
        }

        public void mouseMoved(MouseEvent e) {
            getHandler().mouseMoved(e);
        }

        public void mouseDragged(MouseEvent e) {
            getHandler().mouseDragged(e);
        }
    }

    private class Handler implements FocusListener, MouseInputListener,
            PropertyChangeListener {

        // FocusListener
        private void repaintLeadCell( ) {
	    int rc = table.getRowCount();
	    int cc = table.getColumnCount();
            int lr = table.getSelectionModel().getLeadSelectionIndex();
            int lc = table.getColumnModel().getSelectionModel().
                    getLeadSelectionIndex();
	    if (lr < 0 || lr >= rc || lc < 0 || lc >= cc) {
		return;
	    }

            Rectangle dirtyRect = table.getCellRect(lr, lc, false);
            table.repaint(dirtyRect);
        }

        public void focusGained(FocusEvent e) {
            repaintLeadCell();
        }

        public void focusLost(FocusEvent e) {
            repaintLeadCell();
        }


        // KeyListener
        public void keyPressed(KeyEvent e) { }

        public void keyReleased(KeyEvent e) { }

        public void keyTyped(KeyEvent e) {
            KeyStroke keyStroke = KeyStroke.getKeyStroke(e.getKeyChar(),
                    e.getModifiers());

            // We register all actions using ANCESTOR_OF_FOCUSED_COMPONENT
            // which means that we might perform the appropriate action
            // in the table and then forward it to the editor if the editor
            // had focus. Make sure this doesn't happen by checking our
            // InputMaps.
	    InputMap map = table.getInputMap(JComponent.WHEN_FOCUSED);
	    if (map != null && map.get(keyStroke) != null) {
		return;
	    }
	    map = table.getInputMap(JComponent.
				  WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	    if (map != null && map.get(keyStroke) != null) {
		return;
	    }

	    keyStroke = KeyStroke.getKeyStrokeForEvent(e);

            // The AWT seems to generate an unconsumed \r event when
            // ENTER (\n) is pressed.
            if (e.getKeyChar() == '\r') {
                return;
            }

            int leadRow = table.getSelectionModel().getLeadSelectionIndex();
            int leadColumn = table.getColumnModel().getSelectionModel().
                    getLeadSelectionIndex();
            if (leadRow != -1 && leadColumn != -1 && !table.isEditing()) {
                if (!table.editCellAt(leadRow, leadColumn)) {
                    return;
                }
            }

            // Forwarding events this way seems to put the component
            // in a state where it believes it has focus. In reality
            // the table retains focus - though it is difficult for
            // a user to tell, since the caret is visible and flashing.

            // Calling table.requestFocus() here, to get the focus back to
            // the table, seems to have no effect.

            Component editorComp = table.getEditorComponent();
            if (table.isEditing() && editorComp != null) {
                if (editorComp instanceof JComponent) {
                    JComponent component = (JComponent)editorComp;
		    map = component.getInputMap(JComponent.WHEN_FOCUSED);
		    Object binding = (map != null) ? map.get(keyStroke) : null;
		    if (binding == null) {
			map = component.getInputMap(JComponent.
					 WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			binding = (map != null) ? map.get(keyStroke) : null;
		    }
		    if (binding != null) {
			ActionMap am = component.getActionMap();
			Action action = (am != null) ? am.get(binding) : null;
			if (action != null && SwingUtilities.
			    notifyAction(action, keyStroke, e, component,
					 e.getModifiers())) {
			    e.consume();
			}
		    }
                }
            }
        }


        // MouseInputListener

        // Component receiving mouse events during editing.
        // May not be editorComponent.
        private Component dispatchComponent;
	private boolean selectedOnPress;

        public void mouseClicked(MouseEvent e) {}

        private void setDispatchComponent(MouseEvent e) {
            Component editorComponent = table.getEditorComponent();
            Point p = e.getPoint();
            Point p2 = SwingUtilities.convertPoint(table, p, editorComponent);
            dispatchComponent =
                    SwingUtilities.getDeepestComponentAt(editorComponent,
                            p2.x, p2.y);
        }

        private boolean repostEvent(MouseEvent e) {
	    // Check for isEditing() in case another event has
	    // caused the editor to be removed. See bug #4306499.
            if (dispatchComponent == null || !table.isEditing()) {
                return false;
            }
            MouseEvent e2 = SwingUtilities.convertMouseEvent(table, e,
                    dispatchComponent);
            dispatchComponent.dispatchEvent(e2);
            return true;
        }

        private void setValueIsAdjusting(boolean flag) {
            table.getSelectionModel().setValueIsAdjusting(flag);
            table.getColumnModel().getSelectionModel().
                    setValueIsAdjusting(flag);
        }

	private boolean shouldIgnore(MouseEvent e) {
	    return e.isConsumed() || (!(SwingUtilities.isLeftMouseButton(e) &&
                        table.isEnabled()));
	}

        public void mousePressed(MouseEvent e) {
	    if (e.isConsumed()) {
		selectedOnPress = false;
		return;
	    }
	    selectedOnPress = true;
	    adjustFocusAndSelection(e);
	}

	void adjustFocusAndSelection(MouseEvent e) {
	    if (shouldIgnore(e)) {
	        return;
	    }

            Point p = e.getPoint();
            int row = table.rowAtPoint(p);
            int column = table.columnAtPoint(p);
            // Fix for 4835633
            if (pointOutsidePrefSize(table, row, column, p)) { 
                // If shift is down in multi-select, we should just return.
                // For single select or non-shift-click, clear the selection
                if (e.getID() ==  MouseEvent.MOUSE_PRESSED &&
                    (!e.isShiftDown() ||
                     table.getSelectionModel().getSelectionMode() ==
                     ListSelectionModel.SINGLE_SELECTION)) {
                    table.clearSelection();
                    TableCellEditor tce = table.getCellEditor();
                    if (tce != null) {
                        tce.stopCellEditing();
                    }
                }
                return;
            }
	    // The autoscroller can generate drag events outside the
            // table's range.
            if ((column == -1) || (row == -1)) {
                return;
            }

            if (table.editCellAt(row, column, e)) {
                setDispatchComponent(e);
                repostEvent(e);
            }
	    else if (table.isRequestFocusEnabled()) {
                table.requestFocus();
	    }

            CellEditor editor = table.getCellEditor();
            if (editor == null || editor.shouldSelectCell(e)) {
		boolean adjusting = (e.getID() == MouseEvent.MOUSE_PRESSED) ?
                        true : false;
                setValueIsAdjusting(adjusting);
                boolean ctrl = e.isControlDown();
                table.changeSelection(row, column, ctrl, !ctrl && e.isShiftDown());
	    }
        }

        public void mouseReleased(MouseEvent e) {
	    if (selectedOnPress) {
		if (shouldIgnore(e)) {
		    return;
		}

		repostEvent(e);
		dispatchComponent = null;
		setValueIsAdjusting(false);
	    } else {
		adjustFocusAndSelection(e);
	    }
        }


        public void mouseEntered(MouseEvent e) {}

        public void mouseExited(MouseEvent e) {}

        public void mouseMoved(MouseEvent e) {}

        public void mouseDragged(MouseEvent e) {
	    if (shouldIgnore(e)) {
	        return;
	    }

            repostEvent(e);

            CellEditor editor = table.getCellEditor();
            if (editor == null || editor.shouldSelectCell(e)) {
                Point p = e.getPoint();
                int row = table.rowAtPoint(p);
                int column = table.columnAtPoint(p);
	        // The autoscroller can generate drag events outside the
                // table's range.
                if ((column == -1) || (row == -1)) {
                    return;
                }
                // Fix for 4835633
                // Until we support drag-selection, dragging should not change
                // the selection (act like single-select).
                Object bySize = table.getClientProperty("Table.isFileList");
                if (bySize instanceof Boolean &&
                    ((Boolean)bySize).booleanValue()) {
                    return;
                }
	        table.changeSelection(row, column, false, true);
            }
        }


        // PropertyChangeListener
	public void propertyChange(PropertyChangeEvent event) {
	    String changeName = event.getPropertyName();

	    if ("componentOrientation" == changeName) {
		JTableHeader header = table.getTableHeader();
		if (header != null) {
		    header.setComponentOrientation(
                            (ComponentOrientation)event.getNewValue());
		}
            } else if ("transferHandler" == changeName) {
                DropTarget dropTarget = table.getDropTarget();
                if (dropTarget instanceof UIResource) {
                    if (defaultDropTargetListener == null) {
                        defaultDropTargetListener =
                            new TableDropTargetListener();
                    }
                    try {
                        dropTarget.addDropTargetListener(
                                defaultDropTargetListener);
                    } catch (TooManyListenersException tmle) {
                        // should not happen... swing drop target is multicast
                    }
                }
            }
	}
    }


    /*
     * Returns true if the given point is outside the preferredSize of the
     * item at the given row of the table.  (Column must be 0).
     * Returns false if the "Table.isFileList" client property is not set.
     */
    private static boolean pointOutsidePrefSize(JTable table,
                                                int row, int column, Point p) {
        Object bySize = table.getClientProperty("Table.isFileList");
        if (bySize instanceof Boolean && ((Boolean)bySize).booleanValue()) {
            if (table.convertColumnIndexToModel(column) != 0 || row == -1) {
                return true;
            }
            TableCellRenderer tcr = table.getCellRenderer(row, column);
            Object value = table.getValueAt(row, column);
            Component cell = tcr.getTableCellRendererComponent(table, value, false,
                    false, row, column);
            Dimension itemSize = cell.getPreferredSize();
            Rectangle cellBounds = table.getCellRect(row, column, false);
            cellBounds.width = itemSize.width;
            cellBounds.height = itemSize.height;

            // See if coords are inside
            // ASSUME: mouse x,y will never be < cell's x,y
            assert (p.x >= cellBounds.x && p.y >= cellBounds.y);
            if (p.x > cellBounds.x + cellBounds.width ||
                    p.y > cellBounds.y + cellBounds.height) {
                return true;
            }
        }
        return false;
    }

//
//  Factory methods for the Listeners
//

    private Handler getHandler() {
        if (handler == null) {
            handler = new Handler();
        }
        return handler;
    }

    /**
     * Creates the key listener for handling keyboard navigation in the JTable.
     */
    protected KeyListener createKeyListener() {
	return null;
    }

    /**
     * Creates the focus listener for handling keyboard navigation in the JTable.
     */
    protected FocusListener createFocusListener() {
        return getHandler();
    }

    /**
     * Creates the mouse listener for the JTable.
     */
    protected MouseInputListener createMouseInputListener() {
        return getHandler();
    }

//
//  The installation/uninstall procedures and support
//

    public static ComponentUI createUI(JComponent c) {
        return new BasicTableUI();
    }

//  Installation

    public void installUI(JComponent c) {
        table = (JTable)c;

        rendererPane = new CellRendererPane();
        table.add(rendererPane);
        installDefaults();
        installDefaults2();
        installListeners();
        installKeyboardActions();
    }

    /**
     * Initialize JTable properties, e.g. font, foreground, and background.
     * The font, foreground, and background properties are only set if their
     * current value is either null or a UIResource, other properties are set
     * if the current value is null.
     *
     * @see #installUI
     */
    protected void installDefaults() {
        LookAndFeel.installColorsAndFont(table, "Table.background",
                                         "Table.foreground", "Table.font");
        // JTable's original row height is 16.  To correctly display the
        // contents on Linux we should have set it to 18, Windows 19 and
        // Solaris 20.  As these values vary so much it's too hard to
        // be backward compatable and try to update the row height, we're
        // therefor NOT going to adjust the row height based on font.  If the
        // developer changes the font, it's there responsability to update
        // the row height.

        LookAndFeel.installProperty(table, "opaque", Boolean.TRUE);

        Color sbg = table.getSelectionBackground();
        if (sbg == null || sbg instanceof UIResource) {
            table.setSelectionBackground(UIManager.getColor("Table.selectionBackground"));
        }

        Color sfg = table.getSelectionForeground();
        if (sfg == null || sfg instanceof UIResource) {
            table.setSelectionForeground(UIManager.getColor("Table.selectionForeground"));
        }

        Color gridColor = table.getGridColor();
        if (gridColor == null || gridColor instanceof UIResource) {
            table.setGridColor(UIManager.getColor("Table.gridColor"));
        }

        // install the scrollpane border
        Container parent = table.getParent();  // should be viewport
        if (parent != null) {
            parent = parent.getParent();  // should be the scrollpane
            if (parent != null && parent instanceof JScrollPane) {
                LookAndFeel.installBorder((JScrollPane)parent, "Table.scrollPaneBorder");
            }
        }
    }

    private void installDefaults2() {
	TransferHandler th = table.getTransferHandler();
	if (th == null || th instanceof UIResource) {
	    table.setTransferHandler(defaultTransferHandler);
	}
	DropTarget dropTarget = table.getDropTarget();
	if (dropTarget instanceof UIResource) {
            if (defaultDropTargetListener == null) {
                defaultDropTargetListener = 
                    new TableDropTargetListener();
            }
	    try {
		dropTarget.addDropTargetListener(defaultDropTargetListener);
	    } catch (TooManyListenersException tmle) {
		// should not happen... swing drop target is multicast
	    }
	}
    }

    /**
     * Attaches listeners to the JTable.
     */
    protected void installListeners() {
        focusListener = createFocusListener();
        keyListener = createKeyListener();
        mouseInputListener = createMouseInputListener();

        table.addFocusListener(focusListener);
        table.addKeyListener(keyListener);
	table.addMouseListener(defaultDragRecognizer);
	table.addMouseMotionListener(defaultDragRecognizer);
        table.addMouseListener(mouseInputListener);
        table.addMouseMotionListener(mouseInputListener);
        table.addPropertyChangeListener(getHandler());
    }

    /**
     * Register all keyboard actions on the JTable.
     */
    protected void installKeyboardActions() {
        LazyActionMap.installLazyActionMap(table, BasicTableUI.class,
                "Table.actionMap");

	InputMap inputMap = getInputMap(JComponent.
					WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	SwingUtilities.replaceUIInputMap(table,
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
				inputMap);
    }

    InputMap getInputMap(int condition) {
        if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
            InputMap keyMap =
                (InputMap)DefaultLookup.get(table, this,
                                            "Table.ancestorInputMap");
            return keyMap;
        }
        return null;
    }

    static void loadActionMap(LazyActionMap map) {
        // IMPORTANT: There is a very close coupling between the parameters
        // passed to the Actions constructor. Only certain parameter
        // combinations are supported. For example, the following Action would
        // not work as expected:
        //     new Actions(Actions.NEXT_ROW_CELL, 1, 4, false, true)
        // Actions which move within the selection only (having a true
        // inSelection parameter) require that one of dx or dy be
        // zero and the other be -1 or 1. The point of this warning is
        // that you should be very careful about making sure a particular
        // combination of parameters is supported before changing or
        // adding anything here.
        
        map.put(new Actions(Actions.NEXT_COLUMN, 1, 0,
                false, false));
        map.put(new Actions(Actions.NEXT_COLUMN_CHANGE_LEAD, 1, 0,
                false, false));
        map.put(new Actions(Actions.PREVIOUS_COLUMN, -1, 0,
                false, false));
        map.put(new Actions(Actions.PREVIOUS_COLUMN_CHANGE_LEAD, -1, 0,
                false, false));
        map.put(new Actions(Actions.NEXT_ROW, 0, 1,
                false, false));
        map.put(new Actions(Actions.NEXT_ROW_CHANGE_LEAD, 0, 1,
                false, false));
        map.put(new Actions(Actions.PREVIOUS_ROW, 0, -1,
                false, false));
        map.put(new Actions(Actions.PREVIOUS_ROW_CHANGE_LEAD, 0, -1,
                false, false));
        map.put(new Actions(Actions.NEXT_COLUMN_EXTEND_SELECTION,
                1, 0, true, false));
        map.put(new Actions(Actions.PREVIOUS_COLUMN_EXTEND_SELECTION,
                -1, 0, true, false));
        map.put(new Actions(Actions.NEXT_ROW_EXTEND_SELECTION,
                0, 1, true, false));
        map.put(new Actions(Actions.PREVIOUS_ROW_EXTEND_SELECTION,
                0, -1, true, false));
        map.put(new Actions(Actions.SCROLL_UP_CHANGE_SELECTION,
	        false, false, true, false));
        map.put(new Actions(Actions.SCROLL_DOWN_CHANGE_SELECTION,
	        false, true, true, false));
        map.put(new Actions(Actions.FIRST_COLUMN,
	        false, false, false, true));
        map.put(new Actions(Actions.LAST_COLUMN,
	        false, true, false, true));

        map.put(new Actions(Actions.SCROLL_UP_EXTEND_SELECTION,
		true, false, true, false));
        map.put(new Actions(Actions.SCROLL_DOWN_EXTEND_SELECTION,
                true, true, true, false));
        map.put(new Actions(Actions.FIRST_COLUMN_EXTEND_SELECTION,
                true, false, false, true));
        map.put(new Actions(Actions.LAST_COLUMN_EXTEND_SELECTION,
                true, true, false, true));

	map.put(new Actions(Actions.FIRST_ROW, false, false, true, true));
	map.put(new Actions(Actions.LAST_ROW, false, true, true, true));

	map.put(new Actions(Actions.FIRST_ROW_EXTEND_SELECTION,
                true, false, true, true));
	map.put(new Actions(Actions.LAST_ROW_EXTEND_SELECTION,
                true, true, true, true));

	map.put(new Actions(Actions.NEXT_COLUMN_CELL,
                1, 0, false, true));
	map.put(new Actions(Actions.PREVIOUS_COLUMN_CELL,
                -1, 0, false, true));
	map.put(new Actions(Actions.NEXT_ROW_CELL, 0, 1, false, true));
	map.put(new Actions(Actions.PREVIOUS_ROW_CELL,
                0, -1, false, true));

	map.put(new Actions(Actions.SELECT_ALL));
        map.put(new Actions(Actions.CLEAR_SELECTION));
	map.put(new Actions(Actions.CANCEL_EDITING));
	map.put(new Actions(Actions.START_EDITING));

        map.put(TransferHandler.getCutAction().getValue(Action.NAME),
                TransferHandler.getCutAction());
        map.put(TransferHandler.getCopyAction().getValue(Action.NAME),
                TransferHandler.getCopyAction());
        map.put(TransferHandler.getPasteAction().getValue(Action.NAME),
                TransferHandler.getPasteAction());

	map.put(new Actions(Actions.SCROLL_LEFT_CHANGE_SELECTION,
                false, false, false, false));
	map.put(new Actions(Actions.SCROLL_RIGHT_CHANGE_SELECTION,
                false, true, false, false));
	map.put(new Actions(Actions.SCROLL_LEFT_EXTEND_SELECTION,
                true, false, false, false));
	map.put(new Actions(Actions.SCROLL_RIGHT_EXTEND_SELECTION,
                true, true, false, false));

        map.put(new Actions(Actions.ADD_TO_SELECTION));
        map.put(new Actions(Actions.TOGGLE_AND_ANCHOR));
        map.put(new Actions(Actions.EXTEND_TO));
        map.put(new Actions(Actions.MOVE_SELECTION_TO));
    }

//  Uninstallation

    public void uninstallUI(JComponent c) {
        uninstallDefaults();
        uninstallListeners();
        uninstallKeyboardActions();

        table.remove(rendererPane);
        rendererPane = null;
        table = null;
    }

    protected void uninstallDefaults() {
	if (table.getTransferHandler() instanceof UIResource) {
	    table.setTransferHandler(null);
	}
    }

    protected void uninstallListeners() {
        table.removeFocusListener(focusListener);
        table.removeKeyListener(keyListener);
	table.removeMouseListener(defaultDragRecognizer);
	table.removeMouseMotionListener(defaultDragRecognizer);
        table.removeMouseListener(mouseInputListener);
        table.removeMouseMotionListener(mouseInputListener);
        table.removePropertyChangeListener(getHandler());

        focusListener = null;
        keyListener = null;
        mouseInputListener = null;
        handler = null;
    }

    protected void uninstallKeyboardActions() {
	SwingUtilities.replaceUIInputMap(table, JComponent.
				   WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
        SwingUtilities.replaceUIActionMap(table, null);
    }

//
// Size Methods
//

    private Dimension createTableSize(long width) {
	int height = 0;
	int rowCount = table.getRowCount();
	if (rowCount > 0 && table.getColumnCount() > 0) {
	    Rectangle r = table.getCellRect(rowCount-1, 0, true);
	    height = r.y + r.height;
	}
	// Width is always positive. The call to abs() is a workaround for
	// a bug in the 1.1.6 JIT on Windows.
	long tmp = Math.abs(width);
        if (tmp > Integer.MAX_VALUE) {
            tmp = Integer.MAX_VALUE;
        }
	return new Dimension((int)tmp, height);
    }

    /**
     * Return the minimum size of the table. The minimum height is the
     * row height times the number of rows.
     * The minimum width is the sum of the minimum widths of each column.
     */
    public Dimension getMinimumSize(JComponent c) {
        long width = 0;
        Enumeration enumeration = table.getColumnModel().getColumns();
        while (enumeration.hasMoreElements()) {
            TableColumn aColumn = (TableColumn)enumeration.nextElement();
            width = width + aColumn.getMinWidth();
        }
        return createTableSize(width);
    }

    /**
     * Return the preferred size of the table. The preferred height is the
     * row height times the number of rows.
     * The preferred width is the sum of the preferred widths of each column.
     */
    public Dimension getPreferredSize(JComponent c) {
        long width = 0;
        Enumeration enumeration = table.getColumnModel().getColumns();
        while (enumeration.hasMoreElements()) {
            TableColumn aColumn = (TableColumn)enumeration.nextElement();
            width = width + aColumn.getPreferredWidth();
        }
        return createTableSize(width);
    }

    /**
     * Return the maximum size of the table. The maximum height is the
     * row heighttimes the number of rows.
     * The maximum width is the sum of the maximum widths of each column.
     */
    public Dimension getMaximumSize(JComponent c) {
        long width = 0;
        Enumeration enumeration = table.getColumnModel().getColumns();
        while (enumeration.hasMoreElements()) {
            TableColumn aColumn = (TableColumn)enumeration.nextElement();
            width = width + aColumn.getMaxWidth();
        }
        return createTableSize(width);
    }

//
//  Paint methods and support
//

    /** Paint a representation of the <code>table</code> instance
     * that was set in installUI().
     */
    public void paint(Graphics g, JComponent c) {
        Rectangle clip = g.getClipBounds();

        Rectangle bounds = table.getBounds();
        // account for the fact that the graphics has already been translated
        // into the table's bounds
        bounds.x = bounds.y = 0;

	if (table.getRowCount() <= 0 || table.getColumnCount() <= 0 ||
                // this check prevents us from painting the entire table
                // when the clip doesn't intersect our bounds at all
                !bounds.intersects(clip)) {

	    return;
	}

	Point upperLeft = clip.getLocation();
	Point lowerRight = new Point(clip.x + clip.width - 1, clip.y + clip.height - 1);
        int rMin = table.rowAtPoint(upperLeft);
        int rMax = table.rowAtPoint(lowerRight);
        // This should never happen (as long as our bounds intersect the clip,
        // which is why we bail above if that is the case).
        if (rMin == -1) {
	    rMin = 0;
        }
        // If the table does not have enough rows to fill the view we'll get -1.
        // (We could also get -1 if our bounds don't intersect the clip,
        // which is why we bail above if that is the case).
        // Replace this with the index of the last row.
        if (rMax == -1) {
	    rMax = table.getRowCount()-1;
        }

        boolean ltr = table.getComponentOrientation().isLeftToRight();
        int cMin = table.columnAtPoint(ltr ? upperLeft : lowerRight); 
        int cMax = table.columnAtPoint(ltr ? lowerRight : upperLeft);        
        // This should never happen.
        if (cMin == -1) {
	    cMin = 0;
        }
	// If the table does not have enough columns to fill the view we'll get -1.
        // Replace this with the index of the last column.
        if (cMax == -1) {
	    cMax = table.getColumnCount()-1;
        }

        // Paint the grid.
        paintGrid(g, rMin, rMax, cMin, cMax);

        // Paint the cells.
	paintCells(g, rMin, rMax, cMin, cMax);
    }

    /*
     * Paints the grid lines within <I>aRect</I>, using the grid
     * color set with <I>setGridColor</I>. Paints vertical lines
     * if <code>getShowVerticalLines()</code> returns true and paints
     * horizontal lines if <code>getShowHorizontalLines()</code>
     * returns true.
     */
    private void paintGrid(Graphics g, int rMin, int rMax, int cMin, int cMax) {
        g.setColor(table.getGridColor());

	Rectangle minCell = table.getCellRect(rMin, cMin, true);
	Rectangle maxCell = table.getCellRect(rMax, cMax, true);
        Rectangle damagedArea = minCell.union( maxCell );

        if (table.getShowHorizontalLines()) {
	    int tableWidth = damagedArea.x + damagedArea.width;
	    int y = damagedArea.y;
	    for (int row = rMin; row <= rMax; row++) {
		y += table.getRowHeight(row);
		g.drawLine(damagedArea.x, y - 1, tableWidth - 1, y - 1);
	    }
	}
        if (table.getShowVerticalLines()) {
	    TableColumnModel cm = table.getColumnModel();
	    int tableHeight = damagedArea.y + damagedArea.height;
	    int x;
	    if (table.getComponentOrientation().isLeftToRight()) {
		x = damagedArea.x;
		for (int column = cMin; column <= cMax; column++) {
		    int w = cm.getColumn(column).getWidth();
		    x += w;
		    g.drawLine(x - 1, 0, x - 1, tableHeight - 1);
		}
	    } else {
		x = damagedArea.x + damagedArea.width;
		for (int column = cMin; column < cMax; column++) {
		    int w = cm.getColumn(column).getWidth();
		    x -= w;
		    g.drawLine(x - 1, 0, x - 1, tableHeight - 1);
		}
		x -= cm.getColumn(cMax).getWidth();
		g.drawLine(x, 0, x, tableHeight - 1);
	    }
	}
    }

    private int viewIndexForColumn(TableColumn aColumn) {
        TableColumnModel cm = table.getColumnModel();
        for (int column = 0; column < cm.getColumnCount(); column++) {
            if (cm.getColumn(column) == aColumn) {
                return column;
            }
        }
        return -1;
    }

    private void paintCells(Graphics g, int rMin, int rMax, int cMin, int cMax) {
	JTableHeader header = table.getTableHeader();
	TableColumn draggedColumn = (header == null) ? null : header.getDraggedColumn();

	TableColumnModel cm = table.getColumnModel();
	int columnMargin = cm.getColumnMargin();

        Rectangle cellRect;
	TableColumn aColumn;
	int columnWidth;
	if (table.getComponentOrientation().isLeftToRight()) {
	    for(int row = rMin; row <= rMax; row++) {
		cellRect = table.getCellRect(row, cMin, false);
                for(int column = cMin; column <= cMax; column++) {
                    aColumn = cm.getColumn(column);
                    columnWidth = aColumn.getWidth();
                    cellRect.width = columnWidth - columnMargin;
                    if (aColumn != draggedColumn) {
                        paintCell(g, cellRect, row, column);
                    }
                    cellRect.x += columnWidth;
        	}
	    }
	} else {
	    for(int row = rMin; row <= rMax; row++) {
                cellRect = table.getCellRect(row, cMin, false);
                aColumn = cm.getColumn(cMin);
                if (aColumn != draggedColumn) {
                    columnWidth = aColumn.getWidth();
                    cellRect.width = columnWidth - columnMargin;
                    paintCell(g, cellRect, row, cMin);
                }
                for(int column = cMin+1; column <= cMax; column++) {
                    aColumn = cm.getColumn(column);
                    columnWidth = aColumn.getWidth();
                    cellRect.width = columnWidth - columnMargin;
                    cellRect.x -= columnWidth;
                    if (aColumn != draggedColumn) {
                        paintCell(g, cellRect, row, column);
                    }
        	}
	    }
	}

        // Paint the dragged column if we are dragging.
        if (draggedColumn != null) {
	    paintDraggedArea(g, rMin, rMax, draggedColumn, header.getDraggedDistance());
	}

	// Remove any renderers that may be left in the rendererPane.
	rendererPane.removeAll();
    }

    private void paintDraggedArea(Graphics g, int rMin, int rMax, TableColumn draggedColumn, int distance) {
        int draggedColumnIndex = viewIndexForColumn(draggedColumn);

        Rectangle minCell = table.getCellRect(rMin, draggedColumnIndex, true);
	Rectangle maxCell = table.getCellRect(rMax, draggedColumnIndex, true);

	Rectangle vacatedColumnRect = minCell.union(maxCell);

	// Paint a gray well in place of the moving column.
	g.setColor(table.getParent().getBackground());
	g.fillRect(vacatedColumnRect.x, vacatedColumnRect.y,
		   vacatedColumnRect.width, vacatedColumnRect.height);

	// Move to the where the cell has been dragged.
	vacatedColumnRect.x += distance;

	// Fill the background.
	g.setColor(table.getBackground());
	g.fillRect(vacatedColumnRect.x, vacatedColumnRect.y,
		   vacatedColumnRect.width, vacatedColumnRect.height);

	// Paint the vertical grid lines if necessary.
	if (table.getShowVerticalLines()) {
	    g.setColor(table.getGridColor());
	    int x1 = vacatedColumnRect.x;
	    int y1 = vacatedColumnRect.y;
	    int x2 = x1 + vacatedColumnRect.width - 1;
	    int y2 = y1 + vacatedColumnRect.height - 1;
	    // Left
	    g.drawLine(x1-1, y1, x1-1, y2);
	    // Right
	    g.drawLine(x2, y1, x2, y2);
	}

	for(int row = rMin; row <= rMax; row++) {
	    // Render the cell value
	    Rectangle r = table.getCellRect(row, draggedColumnIndex, false);
	    r.x += distance;
	    paintCell(g, r, row, draggedColumnIndex);

	    // Paint the (lower) horizontal grid line if necessary.
	    if (table.getShowHorizontalLines()) {
		g.setColor(table.getGridColor());
		Rectangle rcr = table.getCellRect(row, draggedColumnIndex, true);
		rcr.x += distance;
		int x1 = rcr.x;
		int y1 = rcr.y;
		int x2 = x1 + rcr.width - 1;
		int y2 = y1 + rcr.height - 1;
		g.drawLine(x1, y2, x2, y2);
	    }
	}
    }

    private void paintCell(Graphics g, Rectangle cellRect, int row, int column) {
        if (table.isEditing() && table.getEditingRow()==row &&
                                 table.getEditingColumn()==column) {
            Component component = table.getEditorComponent();
	    component.setBounds(cellRect);
            component.validate();
        }
        else {
            TableCellRenderer renderer = table.getCellRenderer(row, column);
            Component component = table.prepareRenderer(renderer, row, column);
            rendererPane.paintComponent(g, component, table, cellRect.x, cellRect.y,
                                        cellRect.width, cellRect.height, true);
        }
    }


    private static final TableDragGestureRecognizer defaultDragRecognizer = new TableDragGestureRecognizer();

    /**
     * Drag gesture recognizer for JTable components
     */
    static class TableDragGestureRecognizer extends BasicDragGestureRecognizer {

	/**
	 * Determines if the following are true:
	 * <ul>
	 * <li>the press event is located over a selection
	 * <li>the dragEnabled property is true
	 * <li>A TranferHandler is installed
	 * </ul>
	 * <p>
	 * This is implemented to perform the superclass behavior
	 * followed by a check if the dragEnabled
	 * property is set and if the location picked is selected.
	 */
        protected boolean isDragPossible(MouseEvent e) {
	    if (super.isDragPossible(e)) {
		JTable table = (JTable) this.getComponent(e);
		if (table.getDragEnabled()) {
		    Point p = e.getPoint();
		    int row = table.rowAtPoint(p);
		    int column = table.columnAtPoint(p);
            // For 4835633.  Otherwise, you can drag a file by clicking below
            // it.
            if (pointOutsidePrefSize(table, row, column, p)) {
                return false;
            }
		    if ((column != -1) && (row != -1) && table.isCellSelected(row, column)) {
			return true;
		    }
		}
	    }
	    return false;
	}
    }

    private static DropTargetListener defaultDropTargetListener = null;

    /**
     * A DropTargetListener to extend the default Swing handling of drop operations
     * by moving the tree selection to the nearest location to the mouse pointer.
     * Also adds autoscroll capability.
     */
    static class TableDropTargetListener extends BasicDropTargetListener {

	/**
	 * called to save the state of a component in case it needs to
	 * be restored because a drop is not performed.
	 */
        protected void saveComponentState(JComponent comp) {
	    JTable table = (JTable) comp;
	    rows = table.getSelectedRows();
	    cols = table.getSelectedColumns();
	}

	/**
	 * called to restore the state of a component
	 * because a drop was not performed.
	 */
        protected void restoreComponentState(JComponent comp) {
	    JTable table = (JTable) comp;
	    table.clearSelection();
	    for (int i = 0; i < rows.length; i++) {
		table.addRowSelectionInterval(rows[i], rows[i]);
	    }
	    for (int i = 0; i < cols.length; i++) {
		table.addColumnSelectionInterval(cols[i], cols[i]);
	    }
	}

	/**
	 * called to set the insertion location to match the current
	 * mouse pointer coordinates.
	 */
        protected void updateInsertionLocation(JComponent comp, Point p) {
	    JTable table = (JTable) comp;
            int row = table.rowAtPoint(p);
            int col = table.columnAtPoint(p);
	    if (row != -1) {
		table.setRowSelectionInterval(row, row);
	    }
	    if (col != -1) {
		table.setColumnSelectionInterval(col, col);
	    }
	}

	private int[] rows;
	private int[] cols;
    }

    private static final TransferHandler defaultTransferHandler = new TableTransferHandler();

    static class TableTransferHandler extends TransferHandler implements UIResource {

	/**
	 * Create a Transferable to use as the source for a data transfer.
	 *
	 * @param c  The component holding the data to be transfered.  This
	 *  argument is provided to enable sharing of TransferHandlers by
	 *  multiple components.
	 * @return  The representation of the data to be transfered.
	 *
	 */
        protected Transferable createTransferable(JComponent c) {
	    if (c instanceof JTable) {
		JTable table = (JTable) c;
		int[] rows;
		int[] cols;
		
		if (!table.getRowSelectionAllowed() && !table.getColumnSelectionAllowed()) {
		    return null;
		}
		
                if (!table.getRowSelectionAllowed()) {
                    int rowCount = table.getRowCount();

                    rows = new int[rowCount];
                    for (int counter = 0; counter < rowCount; counter++) {
                        rows[counter] = counter;
                    }
                } else {
		    rows = table.getSelectedRows();
		}
		
                if (!table.getColumnSelectionAllowed()) {
                    int colCount = table.getColumnCount();

                    cols = new int[colCount];
                    for (int counter = 0; counter < colCount; counter++) {
                        cols[counter] = counter;
                    }
                } else {
		    cols = table.getSelectedColumns();
		}
                
		if (rows == null || cols == null || rows.length == 0 || cols.length == 0) {
		    return null;
		}
                
                StringBuffer plainBuf = new StringBuffer();
                StringBuffer htmlBuf = new StringBuffer();
                
                htmlBuf.append("<html>\n<body>\n<table>\n");
                
                for (int row = 0; row < rows.length; row++) {
                    htmlBuf.append("<tr>\n");
                    for (int col = 0; col < cols.length; col++) {
                        Object obj = table.getValueAt(rows[row], cols[col]);
                        String val = ((obj == null) ? "" : obj.toString());
                        plainBuf.append(val + "\t");
                        htmlBuf.append("  <td>" + val + "</td>\n");
                    }
                    // we want a newline at the end of each line and not a tab
                    plainBuf.deleteCharAt(plainBuf.length() - 1).append("\n");
                    htmlBuf.append("</tr>\n");
                }

                // remove the last newline
                plainBuf.deleteCharAt(plainBuf.length() - 1);
                htmlBuf.append("</table>\n</body>\n</html>");
                
                return new BasicTransferable(plainBuf.toString(), htmlBuf.toString());
	    }

	    return null;
	}

        public int getSourceActions(JComponent c) {
	    return COPY;
	}

    }
}  // End of Class BasicTableUI
