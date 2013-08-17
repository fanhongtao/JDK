/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.tree;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.EventObject;
import java.util.Vector;

/**
 * A TreeCellEditor. You need to supply an instance of DefaultTreeCellRenderer
 * so that the icons can be obtained. You can optionaly supply a TreeCellEditor
 * that will be layed out according to the icon in the DefaultTreeCellRenderer.
 * If you do not supply a TreeCellEditor, a TextField will be used. Editing
 * is started on a triple mouse click, or after a click, pause, click and
 * a delay of 1200 miliseconds.
 *<p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @see javax.swing.JTree
 *
 * @version 1.18 02/06/02
 * @author Scott Violet
 */
public class DefaultTreeCellEditor implements ActionListener, TreeCellEditor,
            TreeSelectionListener {
    /** Editor handling the editing. */
    protected TreeCellEditor               realEditor;

    /** Renderer, used to get border and offsets from. */
    protected DefaultTreeCellRenderer      renderer;

    /** Editing container, will contain the editorComponent. */
    protected Container                    editingContainer;

    /** Component used in editing, obtained from the editingContainer. */
    transient protected Component          editingComponent;

    /** Should isCellEditable return true? This is set in configure...
     * based on the path being edited and the selected selected path. */
    protected boolean                      canEdit;

    /** Used in editing. Indicates x position to place editingComponent. */
    protected transient int                offset;

    /** JTree instance listening too. */
    protected transient JTree              tree;

    /** last path that was selected. */
    protected transient TreePath           lastPath;

    /** Used before starting the editing session. */
    protected transient Timer              timer;

    /** Row that was last passed into getTreeCellEditorComponent. */
    protected transient int                lastRow;

    /** True if the border selection color should be drawn. */
    protected Color                        borderSelectionColor;

    /** Icon to use when editing. */
    protected transient Icon               editingIcon;

    /** Font to paint with, null indicates font of renderer is to be used. */
    protected Font                         font;


    /**
     * Constructs a DefaultTreeCellEditor object for a JTree using the
     * specified renderer and a default editor. (Use this constructor
     * for normal editing.)
     *
     * @param tree      a JTree object
     * @param renderer  a DefaultTreeCellRenderer object
     */
    public DefaultTreeCellEditor(JTree tree,
				 DefaultTreeCellRenderer renderer) {
	this(tree, renderer, null);
    }

    /**
     * Constructs a DefaultTreeCellEditor object for a JTree using the
     * specified renderer and the specified editor. (Use this constructor
     * for specialized editing.)
     *
     * @param tree      a JTree object
     * @param renderer  a DefaultTreeCellRenderer object
     * @param editor    a TreeCellEditor object
     */
    public DefaultTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer,
				 TreeCellEditor editor) {
	this.renderer = renderer;
	realEditor = editor;
	if(realEditor == null)
	    realEditor = createTreeCellEditor();
	editingContainer = createContainer();
	setTree(tree);
	setBorderSelectionColor(UIManager.getColor
				("Tree.editorBorderSelectionColor"));
    }

    /**
      * Sets the color to use for the border.
      */
    public void setBorderSelectionColor(Color newColor) {
	borderSelectionColor = newColor;
    }

    /**
      * Returns the color the border is drawn.
      */
    public Color getBorderSelectionColor() {
	return borderSelectionColor;
    }

    /**
     * Sets the font to edit with. null indicates the renderers font should
     * be used. This will NOT override any font you have set in the editor
     * the receiver was instantied with. If null for an editor was passed in
     * a default editor will be created that will pick up this font.
     *
     * @param font  the editing Font
     * @see #getFont
     */
    public void setFont(Font font) {
	this.font = font;
    }

    /**
     * Gets the font used for editing.
     *
     * @return the editing Font
     * @see #setFont
     */
    public Font getFont() {
	return font;
    }

    //
    // TreeCellEditor
    //

    /**
     * Configures the editor.  Passed onto the realEditor.
     */
    public Component getTreeCellEditorComponent(JTree tree, Object value,
						boolean isSelected,
						boolean expanded,
						boolean leaf, int row) {
	setTree(tree);
	lastRow = row;
	determineOffset(tree, value, isSelected, expanded, leaf, row);

	editingComponent = realEditor.getTreeCellEditorComponent(tree, value,
					isSelected, expanded,leaf, row);


	TreePath        newPath = tree.getPathForRow(row);

	canEdit = (lastPath != null && newPath != null &&
		   lastPath.equals(newPath));

	Font            font = getFont();

	if(font == null) {
	    if(renderer != null)
		font = renderer.getFont();
	    if(font == null)
		font = tree.getFont();
	}
	editingContainer.setFont(font);
	return editingContainer;
    }

    /**
     * Returns the value currently being edited.
     */
    public Object getCellEditorValue() {
	return realEditor.getCellEditorValue();
    }

    /**
     * If the realEditor returns true to this message, prepareForEditing
     * is messaged and true is returned.
     */
    public boolean isCellEditable(EventObject event) {
	boolean            retValue = false;

	if(!realEditor.isCellEditable(event))
	    return false;
	if(canEditImmediately(event))
	    retValue = true;
	else if(canEdit && shouldStartEditingTimer(event)) {
	    startEditingTimer();
	}
	else if(timer != null && timer.isRunning())
	    timer.stop();
	if(retValue)
	    prepareForEditing();
	return retValue;
    }

    /**
     * Messages the realEditor for the return value.
     */
    public boolean shouldSelectCell(EventObject event) {
	return realEditor.shouldSelectCell(event);
    }

    /**
     * If the realEditor will allow editing to stop, the realEditor is
     * removed and true is returned, otherwise false is returned.
     */
    public boolean stopCellEditing() {
	if(realEditor.stopCellEditing()) {
	    if(editingComponent != null)
		editingContainer.remove(editingComponent);
	    editingComponent = null;
	    return true;
	}
	return false;
    }

    /**
     * Messages cancelCellEditing to the realEditor and removes it from this
     * instance.
     */
    public void cancelCellEditing() {
	realEditor.cancelCellEditing();
	if(editingComponent != null)
	    editingContainer.remove(editingComponent);
	editingComponent = null;
    }

    /**
     * Adds the CellEditorListener.
     */
    public void addCellEditorListener(CellEditorListener l) {
	realEditor.addCellEditorListener(l);
    }

    /**
      * Removes the previously added CellEditorListener l.
      */
    public void removeCellEditorListener(CellEditorListener l) {
	realEditor.removeCellEditorListener(l);
    }

    //
    // TreeSelectionListener
    //

    /**
     * Resets lastPath.
     */
    public void valueChanged(TreeSelectionEvent e) {
	if(tree != null) {
	    if(tree.getSelectionCount() == 1)
		lastPath = tree.getSelectionPath();
	    else
		lastPath = null;
	}
	if(timer != null) {
	    timer.stop();
	}
    }

    //
    // ActionListener (for Timer).
    //

    /**
     * Messaged when the timer fires, this will start the editing
     * session.
     */
    public void actionPerformed(ActionEvent e) {
	if(tree != null && lastPath != null) {
	    tree.startEditingAtPath(lastPath);
	}
    }

    //
    // Local methods
    //

    /**
     * Sets the tree currently editing for. This is needed to add
     * a selection listener.
     */
    protected void setTree(JTree newTree) {
	if(tree != newTree) {
	    if(tree != null)
		tree.removeTreeSelectionListener(this);
	    tree = newTree;
	    if(tree != null)
		tree.addTreeSelectionListener(this);
	    if(timer != null) {
		timer.stop();
	    }
	}
    }

    /**
     * Returns true if <code>event</code> is a MouseEvent and the click
     * count is 1.
     */
    protected boolean shouldStartEditingTimer(EventObject event) {
	if((event instanceof MouseEvent) &&
	    SwingUtilities.isLeftMouseButton((MouseEvent)event)) {
	    MouseEvent        me = (MouseEvent)event;

	    return (me.getClickCount() == 1 &&
		    inHitRegion(me.getX(), me.getY()));
	}
	return false;
    }

    /**
     * Starts the editing timer.
     */
    protected void startEditingTimer() {
	if(timer == null) {
	    timer = new Timer(1200, this);
	    timer.setRepeats(false);
	}
	timer.start();
    }

    /**
     * Returns true if <code>event</code> is null, or it is a MouseEvent
     * with a click count > 2 and inHitRegion returns true.
     */
    protected boolean canEditImmediately(EventObject event) {
	if((event instanceof MouseEvent) &&
	   SwingUtilities.isLeftMouseButton((MouseEvent)event)) {
	    MouseEvent       me = (MouseEvent)event;

	    return ((me.getClickCount() > 2) &&
		    inHitRegion(me.getX(), me.getY()));
	}
 	return (event == null);
    }

    /**
     * Should return true if the passed in location is a valid mouse location
     * to start editing from. This is implemented to return false if
     * <code>x</code> is <= the width of the icon and icon gap displayed
     * by the renderer. In other words this returns true if the user
     * clicks over the text part displayed by the renderer, and false
     * otherwise.
     */
    protected boolean inHitRegion(int x, int y) {
	if(lastRow != -1 && tree != null) {
	    Rectangle         bounds = tree.getRowBounds(lastRow);

	    if(bounds != null && x <= (bounds.x + offset) &&
	       offset < (bounds.width - 5)) {
		return false;
	    }
	}
	return true;
    }

    protected void determineOffset(JTree tree, Object value,
				   boolean isSelected, boolean expanded,
				   boolean leaf, int row) {
	if(renderer != null) {
	    if(leaf)
		editingIcon = renderer.getLeafIcon();
	    else if(expanded)
		editingIcon = renderer.getOpenIcon();
	    else
		editingIcon = renderer.getClosedIcon();
	    if(editingIcon != null)
		offset = renderer.getIconTextGap() +
		         editingIcon.getIconWidth();
	    else
		offset = renderer.getIconTextGap();
	}
	else {
	    editingIcon = null;
	    offset = 0;
	}
    }

    /**
     * Invoked just before editing is to start. Will add the
     * <code>editingComponent</code> to the
     * <code>editingContainer</code>.
     */
    protected void prepareForEditing() {
	editingContainer.add(editingComponent);
    }

    /**
     * Creates the container to manage placement of editingComponent.
     */
    protected Container createContainer() {
	return new EditorContainer();
    }

    /**
     * This is invoked if a TreeCellEditor is not supplied in the constructor.
     * It returns a TextField editor.
     */
    protected TreeCellEditor createTreeCellEditor() {
	Border              aBorder = UIManager.getBorder("Tree.editorBorder");
	DefaultCellEditor   editor = new DefaultCellEditor
	    (new DefaultTextField(aBorder)) {
	    public boolean shouldSelectCell(EventObject event) {
		boolean retValue = super.shouldSelectCell(event);
		getComponent().requestFocus();
		return retValue;
	    }
	};

	// One click to edit.
	editor.setClickCountToStart(1);
	return editor;
    }

    // Serialization support.
    private void writeObject(ObjectOutputStream s) throws IOException {
	Vector      values = new Vector();

	s.defaultWriteObject();
	// Save the realEditor, if its Serializable.
	if(realEditor != null && realEditor instanceof Serializable) {
	    values.addElement("realEditor");
	    values.addElement(realEditor);
	}
	s.writeObject(values);
    }

    private void readObject(ObjectInputStream s)
	throws IOException, ClassNotFoundException {
	s.defaultReadObject();

	Vector          values = (Vector)s.readObject();
	int             indexCounter = 0;
	int             maxCounter = values.size();

	if(indexCounter < maxCounter && values.elementAt(indexCounter).
	   equals("realEditor")) {
	    realEditor = (TreeCellEditor)values.elementAt(++indexCounter);
	    indexCounter++;
	}
    }


    /**
     * TextField used when no editor is supplied. This textfield locks into
     * the border it is constructed with. It also prefers its parents
     * font over its font. And if the renderer is not null and no font
     * has been specified the preferred height is that of the renderer.
     */
    public class DefaultTextField extends JTextField {
	/** Border to use. */
	protected Border         border;

        /**
         * Constructs a DefaultTreeCellEditor$DefaultTextField object.
         *
         * @param border  a Border object
         */
	public DefaultTextField(Border border) {
	    this.border = border;
	}

        /**
         * Overrides <code>JComponent.getBorder</code> to
         * returns the current border.
         */
	public Border getBorder() {
	    return border;
	}

        // implements java.awt.MenuContainer
	public Font getFont() {
	    Font     font = super.getFont();

	    // Prefer the parent containers font if our font is a
	    // FontUIResource
	    if(font instanceof FontUIResource) {
		Container     parent = getParent();

		if(parent != null && parent.getFont() != null)
		    font = parent.getFont();
	    }
	    return font;
	}

        /**
         * Overrides <code>JTextField.getPreferredSize</code> to
         * return the preferred size based on current font, if set,
         * or else use renderer's font.
         */
	public Dimension getPreferredSize() {
	    Dimension      size = super.getPreferredSize();

	    // If not font has been set, prefer the renderers height.
	    if(renderer != null &&
	       DefaultTreeCellEditor.this.getFont() == null) {
		Dimension     rSize = renderer.getPreferredSize();

		size.height = rSize.height;
	    }
	    return size;
	}
    }


    /**
     * Container responsible for placing the editingComponent.
     */
    public class EditorContainer extends Container {
        /**
         * Constructs an EditorContainer object.
         */
	public EditorContainer() {
	    setLayout(null);
	}

	// This should not be used. It will be removed when new API is
	// allowed.
	public void EditorContainer() {
	    setLayout(null);
	}

        /**
         * Overrides <code>Container.paint</code> to paint the node's
         * icon and use the selection color for the background.
         */
	public void paint(Graphics g) {
	    Dimension        size = getSize();

	    // Then the icon.
	    if(editingIcon != null) {
		int       yLoc = Math.max(0, (getSize().height -
					  editingIcon.getIconHeight()) / 2);

		editingIcon.paintIcon(this, g, 0, yLoc);
	    }

	    // Border selection color
	    Color       background = getBorderSelectionColor();
	    if(background != null) {
		g.setColor(background);
		g.drawRect(0, 0, size.width - 1, size.height - 1);
	    }
	    super.paint(g);
	}

	/**
	 * Lays out this Container.  If editing, the editor will be placed at
	 * offset in the x direction and 0 for y.
	 */
	public void doLayout() {
	    if(editingComponent != null) {
		Dimension             cSize = getSize();

		editingComponent.getPreferredSize();
		editingComponent.setLocation(offset, 0);
		editingComponent.setBounds(offset, 0,
					   cSize.width - offset,
					   cSize.height);
	    }
	}

	/**
	 * Returns the preferred size for the Container.  This will be
	 * the preferred size of the editor offset by offset.
	 */
	public Dimension getPreferredSize() {
	    if(editingComponent != null) {
		Dimension         pSize = editingComponent.getPreferredSize();

		pSize.width += offset + 5;

		Dimension         rSize = (renderer != null) ?
		                          renderer.getPreferredSize() : null;

		if(rSize != null)
		    pSize.height = Math.max(pSize.height, rSize.height);
		if(editingIcon != null)
		    pSize.height = Math.max(pSize.height,
					    editingIcon.getIconHeight());

		// Make sure height is at least 100.
		pSize.width = Math.max(pSize.width, 100);
		return pSize;
	    }
	    return new Dimension(0, 0);
	}
    }
}
