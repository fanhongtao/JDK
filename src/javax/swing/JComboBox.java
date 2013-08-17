/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

import java.beans.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.border.*;

import javax.accessibility.*;

/**
 * A component that combines a button or text field and a drop-down list.
 * The user can select a value from 
 * the drop-down list, which appears at the user's request.
 * If you make the combo box editable,
 * then the combo box includes a text field
 * into which the user can type a value.
 * For examples and information on using combo boxes see
 * <a
 href="http://java.sun.com/docs/books/tutorial/uiswing/components/combobox.html">How to Use Combo Boxes</a>, 
 * a section in <em>The Java Tutorial</em>.
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JComboBox">JComboBox</a> key assignments.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @beaninfo
 *   attribute: isContainer false
 * description: A combination of a text field and a drop-down list.
 *
 * @version 1.83 02/06/02
 * @author Arnaud Weber
 */
public class JComboBox extends JComponent 
implements ItemSelectable,ListDataListener,ActionListener, Accessible {
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ComboBoxUI";

    protected ComboBoxModel    dataModel;
    protected ListCellRenderer renderer;
    protected ComboBoxEditor       editor;
    protected int maximumRowCount = 8;
    protected boolean isEditable  = false;
    protected Object selectedItemReminder = null;
    protected KeySelectionManager keySelectionManager = null;
    protected String actionCommand = "comboBoxChanged";
    protected boolean lightWeightPopupEnabled = JPopupMenu.getDefaultLightWeightPopupEnabled();
    boolean firedActionEventOnContentsChanged = false; // Flag for keeping actionEvents under control
    boolean firingActionEvent = false;

    /**
     * Creates a <code>JComboBox</code> that takes it's items from an
     * existing <code>ComboBoxModel</code>.  Since the
     * <code>ComboBoxModel</code> is provided, a combo box created using
     * this constructor does not create a default combo box model and
     * may impact how the insert, remove and add methods behave.
     *
     * @param aModel the <code>ComboBoxModel</code> that provides the 
     * 		displayed list of items
     * @see DefaultComboBoxModel
     */
    public JComboBox(ComboBoxModel aModel) {
        super();
        setModel(aModel);
        init();
    }

    /** 
     * Creates a <code>JComboBox</code> that contains the elements
     * in the specified array.  By default the first item in the array
     * (and therefore the data model) becomes selected.
     *
     * @param items  an array of objects to insert into the combo box
     * @see DefaultComboBoxModel
     */
    public JComboBox(final Object items[]) {
        super();
        setModel(new DefaultComboBoxModel(items));
        init();
    }

    /**
     * Creates a <code>JComboBox</code> that contains the elements
     * in the specified Vector.  By default the first item in the vector
     * and therefore the data model) becomes selected.
     *
     * @param items  an array of vectors to insert into the combo box
     * @see DefaultComboBoxModel
     */
    public JComboBox(Vector items) {
        super();
        setModel(new DefaultComboBoxModel(items));
        init();
    }

    /**
     * Creates a <code>JComboBox</code> with a default data model.
     * The default data model is an empty list of objects.
     * Use <code>addItem</code> to add items.  By default the first item
     * in the data model becomes selected.
     *
     * @see DefaultComboBoxModel
     */
    public JComboBox() {
        super();
        setModel(new DefaultComboBoxModel());
        init();
    }

    private void init()
    {
        installAncestorListener();
        setOpaque(true);
        updateUI();
    }

    protected void installAncestorListener() {
        addAncestorListener(new AncestorListener(){
                                public void ancestorAdded(AncestorEvent event){ hidePopup();}
                                public void ancestorRemoved(AncestorEvent event){ hidePopup();}
                                public void ancestorMoved(AncestorEvent event){ 
                                    if (event.getSource() != JComboBox.this)
                                        hidePopup();
                                }});
    }

    /**
     * Sets the L&F object that renders this component.
     *
     * @param ui  the <code>ComboBoxUI</code> L&F object
     * @see UIDefaults#getUI
     *
     * @beaninfo
     *       expert: true
     *  description: The ComboBoxUI implementation that defines the combo box look and feel.
     */
    public void setUI(ComboBoxUI ui) {
        super.setUI(ui);
    }

    /**
     * Notification from the <code>UIFactory</code> that the L&F has changed. 
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((ComboBoxUI)UIManager.getUI(this));
    }


    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return the string "ComboBoxUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }


    /**
     * Returns the L&F object that renders this component.
     *
     * @return the ComboBoxUI object that renders this component
     */
    public ComboBoxUI getUI() {
        return(ComboBoxUI)ui;
    }

    /**
     * Sets the data model that the <code>JComboBox</code> uses to obtain
     * the list of items.
     *
     * @param aModel the <code>ComboBoxModel</code> that provides the
     *	displayed list of items
     * 
     * @beaninfo
     *        bound: true
     *  description: Model that the combo box uses to get data to display.
     */
    public void setModel(ComboBoxModel aModel) {
        ComboBoxModel oldModel = dataModel;
        if ( dataModel != null )
            dataModel.removeListDataListener(this);
        dataModel = aModel;
        firePropertyChange( "model", oldModel, dataModel);
        dataModel.addListDataListener(this);
        invalidate();
    }

    /**
     * Returns the data model currently used by the <code>JComboBox</code>.
     *
     * @return the <code>ComboBoxModel</code> that provides the displayed
     * 			list of items
     */
    public ComboBoxModel getModel() {
        return dataModel;
    }

    /*
     * Properties
     */

    /**
     * When displaying the popup, <code>JComboBox</code> choose to use
     * a light weight popup if it fits.
     * This method allows you to disable this feature. You have to do disable
     * it if your application mixes light weight and heavy weights components.
     *
     * @beaninfo
     *       expert: true
     *  description: When set, disables using light weight popups.
     */
    public void setLightWeightPopupEnabled(boolean aFlag) {
        lightWeightPopupEnabled = aFlag;
    }

    /**
     * Returns true if lightweight (all-Java) popups are in use,
     * or false if heavyweight (native peer) popups are being used.
     *
     * @return true if lightweight popups are in use
     */
    public boolean isLightWeightPopupEnabled() { 
        return lightWeightPopupEnabled;
    }

    /**
     * Determines whether the <code>JComboBox</code> field is editable.
     * An editable <code>JComboBox</code> allows the user to type into the
     * field or selected an item from the list to initialize the field,
     * after which it can be edited. (The editing affects only the field,
     * the list item remains intact.) A non editable <code>JComboBox</code> 
     * displays the selected item in the field,
     * but the selection cannot be modified.
     *
     * @param aFlag a boolean value, where true indicates that the
     *			field is editable
     * 
     * @beaninfo
     *        bound: true
     *    preferred: true
     *  description: If true, the user can type a new value in the combo box.
     */
    public void setEditable(boolean aFlag) {
        boolean didChange = aFlag != isEditable;
        isEditable = aFlag;
        if ( didChange ) {
            firePropertyChange( "editable", !isEditable, isEditable );
        }
    }

    /**
     * Returns true if the <code>JComboBox</code> is editable.
     * By default, a combo box is not editable.
     * 
     * @return true if the <code>JComboBox</code> is editable, else false
     */
    public boolean isEditable() {
        return isEditable;
    }

    /**
     * Sets the maximum number of rows the <code>JComboBox</code> displays.
     * If the number of objects in the model is greater than count,
     * the combo box uses a scrollbar.
     *
     * @param count an integer specifying the maximum number of items to
     *              display in the list before using a scrollbar
     * @beaninfo
     *        bound: true
     *    preferred: true
     *  description: The maximum number of rows the popup should have
     */
    public void setMaximumRowCount(int count) {
        int oldCount = maximumRowCount;
        maximumRowCount = count;
        firePropertyChange( "maximumRowCount", oldCount, maximumRowCount );
    }

    /**
     * Returns the maximum number of items the combo box can display 
     * without a scrollbar
     *
     * @return an integer specifying the maximum number of items that are 
     *         displayed in the list before using a scrollbar
     */
    public int getMaximumRowCount() {
        return maximumRowCount;
    }

    /**
     * Sets the renderer that paints the item selected from the list in
     * the JComboBox field. The renderer is used if the JComboBox is not
     * editable. If it is editable, the editor is used to render and edit
     * the selected item.
     * <p>
     * The default renderer displays a string, obtained
     * by calling the selected object's <code>toString</code> method.
     * Other renderers can handle graphic images and composite items.
     * <p>
     * To display the selected item,
     * <code>aRenderer.getListCellRendererComponent</code>
     * is called, passing the list object and an index of -1.
     *  
     * @param aRenderer  the <code>ListCellRenderer</code> that
     *			displays the selected item
     * @see #setEditor
     * @beaninfo
     *      bound: true
     *     expert: true
     *  description: The renderer that paints the item selected in the list.
     */
    public void setRenderer(ListCellRenderer aRenderer) {
        ListCellRenderer oldRenderer = renderer;
        renderer = aRenderer;
        firePropertyChange( "renderer", oldRenderer, renderer );
        invalidate();
    }

    /**
     * Returns the renderer used to display the selected item in the 
     * <code>JComboBox</code> field.
     *  
     * @return  the <code>ListCellRenderer</code> that displays
     *			the selected item.
     */
    public ListCellRenderer getRenderer() {
        return renderer;
    }

    /**
     * Sets the editor used to paint and edit the selected item in the 
     * <code>JComboBox</code> field.  The editor is used only if the
     * receiving <code>JComboBox</code> is editable. If not editable,
     * the combo box uses the renderer to paint the selected item.
     *  
     * @param anEditor  the <code>ComboBoxEditor</code> that
     *			displays the selected item
     * @see #setRenderer
     * @beaninfo
     *     bound: true
     *    expert: true
     *  description: The editor that combo box uses to edit the current value
     */
    public void setEditor(ComboBoxEditor anEditor) {
        ComboBoxEditor oldEditor = editor;

        if ( editor != null )
            editor.removeActionListener(this);
        editor = anEditor;
        if ( editor != null ) {
            editor.addActionListener(this);
        }
        firePropertyChange( "editor", oldEditor, editor );
    }

    /**
     * Returns the editor used to paint and edit the selected item in the 
     * <code>JComboBox</code> field.
     *  
     * @return the <code>ComboBoxEditor</code> that displays the selected item
     */
    public ComboBoxEditor getEditor() {
        return editor;
    }

    /*
     * Selection
     */
    /** 
     * Sets the selected item in the <code>JComboBox</code> by
     * specifying the object in the list.
     * If <code>anObject</code> is in the list, the list displays with 
     * <code>anObject</code> selected. 
     *
     * @param anObject  the list object to select
     * @beaninfo
     *    preferred:   true
     *    description: Sets the selected item in the JComboBox.
     */
    public void setSelectedItem(Object anObject) {
        firedActionEventOnContentsChanged = false;
        dataModel.setSelectedItem(anObject);
        if ( !firedActionEventOnContentsChanged ) {
            fireActionEvent();
        }
        else {
            firedActionEventOnContentsChanged = false;
        }
    }

    /**
     * Returns the currently selected item.
     *
     * @return  the currently selected list object from the data model
     */
    public Object getSelectedItem() {
        return dataModel.getSelectedItem();
    }

    /**
     * Selects the item at index <code>anIndex</code>.
     *
     * @param anIndex an integer specifying the list item to select,
     *			where 0 specifies
     *                	the first item in the list
     * @exception IllegalArgumentException if <code>anIndex</code> < -1 or
     *			<code>anIndex</code> is greater than or equal to size
     * @beaninfo
     *   preferred: true
     *  description: The item at index is selected.
     */
    public void setSelectedIndex(int anIndex) {
        int size = dataModel.getSize();

        if ( anIndex == -1 ) {
            setSelectedItem( null );
        }
        else if ( anIndex < -1 || anIndex >= size ) {
            throw new IllegalArgumentException("setSelectedIndex: " + anIndex + " out of bounds");
        }
        else {
            setSelectedItem(dataModel.getElementAt(anIndex));
        }
    }

    /**
     * Returns the first item in the list that matches the given item.
     * The result is not always defined if the <code>JComboBox</code>
     * allows selected items that are not in the list. 
     * Returns -1 if there is no selected item or if the user specified
     * an item which is not in the list.
     
     * @return an integer specifying the currently selected list item,
     *			where 0 specifies
     *                	the first item in the list;
     *			or -1 if no item is selected or if
     *                	the currently selected item is not in the list
     */
    public int getSelectedIndex() {
        Object sObject = dataModel.getSelectedItem();
        int i,c;
        Object obj;

        for ( i=0,c=dataModel.getSize();i<c;i++ ) {
            obj = dataModel.getElementAt(i);
            if ( obj.equals(sObject) )
                return i;
        }
        return -1;
    }

    /** 
     * Adds an item to the item list.
     * This method works only if the <code>JComboBox</code> uses the
     * default data model. <code>JComboBox</code>
     * uses the default data model when created with the 
     * empty constructor and no other model has been set.
     * <p>
     * <strong>Warning:</strong>
     * Focus and keyboard navigation problems may arise if you add duplicate 
     * String objects. A workaround is to add new objects instead of String 
     * objects and make sure that the toString() method is defined. 
     * For example:
     * <pre>
     *   comboBox.addItem(makeObj("Item 1"));
     *   comboBox.addItem(makeObj("Item 1"));
     *   ...
     *   private Object makeObj(final String item)  {
     *     return new Object() { public String toString() { return item; } };
     *   }
     * </pre>
     *
     * @param anObject the Object to add to the list
     */
    public void addItem(Object anObject) {
        checkMutableComboBoxModel();
        ((MutableComboBoxModel)dataModel).addElement(anObject);
    }

    /** 
     * Inserts an item into the item list at a given index. 
     * This method works only if the <code>JComboBox</code> uses the
     * default data model. <code>JComboBox</code>
     * uses the default data model when created with the 
     * empty constructor and no other model has been set.
     *
     * @param anObject the <code>Object</code> to add to the list
     * @param index    an integer specifying the position at which
     *			to add the item
     */
    public void insertItemAt(Object anObject, int index) {
        checkMutableComboBoxModel();
        ((MutableComboBoxModel)dataModel).insertElementAt(anObject,index);
    }

    /** 
     * Removes an item from the item list.
     * This method works only if the <code>JComboBox</code> uses the
     * default data model. <code>JComboBox</code>
     * uses the default data model when created with the empty constructor
     * and no other model has been set.
     *
     * @param anObject  the object to remove from the item list
     */
    public void removeItem(Object anObject) {
        checkMutableComboBoxModel();
        ((MutableComboBoxModel)dataModel).removeElement(anObject);
    }

    /**  
     * Removes the item at <code>anIndex</code>
     * This method works only if the <code>JComboBox</code> uses the
     * default data model. <code>JComboBox</code>
     * uses the default data model when created with the 
     * empty constructor and no other model has been set.
     *
     * @param anIndex  an int specifying the idex of the item to remove,
     *			where 0
     *                 	indicates the first item in the list
     */
    public void removeItemAt(int anIndex) {
        checkMutableComboBoxModel();
        ((MutableComboBoxModel)dataModel).removeElementAt( anIndex );
    }

    /** 
     * Removes all items from the item list.
     */
    public void removeAllItems() {
        checkMutableComboBoxModel();
        MutableComboBoxModel model = (MutableComboBoxModel)dataModel;
        int size = model.getSize();

        if ( model instanceof DefaultComboBoxModel ) {
            ((DefaultComboBoxModel)model).removeAllElements();
        }
        else {
            for ( int i = 0; i < size; ++i ) {
                Object element = model.getElementAt( 0 );
                model.removeElement( element );
            }
        }
    }

    /** 
     * Checks that the <code>dataModel</code> is an instance of 
     * <code>MutableComboBoxModel</code>.  If not, it throws an exception.
     * @exception RuntimeException if <code>dataModel</code> is not an
     *		instance of <code>MutableComboBoxModel</code>.
     */
    void checkMutableComboBoxModel() {
        if ( !(dataModel instanceof MutableComboBoxModel) )
            throw new RuntimeException("Cannot use this method with a non-Mutable data model.");
    }

    /** 
     * Causes the combo box to display its popup window.
     * @see #setPopupVisible
     */
    public void showPopup() {
        setPopupVisible(true);
    }

    /** 
     * Causes the combo box to close its popup window.
     * @see #setPopupVisible
     */
    public void hidePopup() {
        setPopupVisible(false);
    }

    /**
     * Sets the visibility of the popup.
     */
    public void setPopupVisible(boolean v) {
        getUI().setPopupVisible(this, v);
    }

    /** 
     * Determines the visibility of the popup.
     */
    public boolean isPopupVisible() {
        return getUI().isPopupVisible(this);
    }

    /** Selection **/

    /** 
     * Adds an <code>ItemListener</code>.
     * <code>aListener</code> will receive an event when
     * the selected item changes.
     *
     * @param aListener  the <code>ItemListener</code> that is to be notified
     */
    public void addItemListener(ItemListener aListener) {
        listenerList.add(ItemListener.class,aListener);
    }

    /** Removes an <code>ItemListener</code>.
     *
     * @param aListener  the <code>ItemListener</code> to remove
     */
    public void removeItemListener(ItemListener aListener) {
        listenerList.remove(ItemListener.class,aListener);
    }

    /** 
     * Adds an <code>ActionListener</code>. The listener will
     * receive an action event
     * the user finishes making a selection.
     *
     * @param l  the <code>ActionListener</code> that is to be notified
     */
    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class,l);
    }

    /** Removes an <code>ActionListener</code>.
     *
     * @param l  the <code>ActionListener</code> to remove
     */
    public void removeActionListener(ActionListener l) {
	if ((l != null) && (getAction() == l)) {
	    setAction(null);
	} else {
	    listenerList.remove(ActionListener.class, l);
	}
    }

    /** 
     * Sets the action commnand that should be included in the event
     * sent to action listeners.
     *
     * @param aCommand  a string containing the "command" that is sent
     *                  to action listeners; the same listener can then
     *                  do different things depending on the command it
     *                  receives
     */
    public void setActionCommand(String aCommand) {
        actionCommand = aCommand;
    }

    /** 
     * Returns the action command that is included in the event sent to
     * action listeners.
     *
     * @return  the string containing the "command" that is sent
     *          to action listeners.
     */
    public String getActionCommand() {
        return actionCommand;
    }

    private Action action;
    private PropertyChangeListener actionPropertyChangeListener;

    /**
     * Sets the <code>Action</code> for the <code>ActionEvent</code> source.
     * The new <code>Action</code> replaces any previously set
     * <code>Action</code> but does not affect <code>ActionListeners</code>
     * independantly added with <code>addActionListener</code>. 
     * If the <code>Action</code> is already a registered
     * <code>ActionListener</code> for the <code>ActionEvent</code> source,
     * it is not re-registered.
     *
     * <p>
     * A side-effect of setting the <code>Action</code> is that the
     * <code>ActionEvent</code> source's properties are immedately set
     * from the values in the <code>Action</code> (performed by the method 
     * <code>configurePropertiesFromAction</code>) and subsequently
     * updated as the <code>Action</code>'s properties change (via a
     * <code>PropertyChangeListener</code> created by the method
     * <code>createActionPropertyChangeListener</code>.
     *
     * @param a the <code>Action</code> for the <code>JComboBox</code>,
     *			or <code>null</code>.
     * @since 1.3
     * @see Action
     * @see #getAction
     * @see #configurePropertiesFromAction
     * @see #createActionPropertyChangeListener
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: the Action instance connected with this ActionEvent source
     */
    public void setAction(Action a) {
	Action oldValue = getAction();
	if (action==null || !action.equals(a)) {
	    action = a;
	    if (oldValue!=null) {
		removeActionListener(oldValue);
		oldValue.removePropertyChangeListener(actionPropertyChangeListener);
		actionPropertyChangeListener = null;
	    }
	    configurePropertiesFromAction(action);
	    if (action!=null) {		
		// Don't add if it is already a listener
		if (!isListener(ActionListener.class, action)) {
		    addActionListener(action);
		}
		// Reverse linkage:
		actionPropertyChangeListener = createActionPropertyChangeListener(action);
		action.addPropertyChangeListener(actionPropertyChangeListener);
	    }
	    firePropertyChange("action", oldValue, action);
	    revalidate();
	    repaint();
	}
    }

    private boolean isListener(Class c, ActionListener a) {
	boolean isListener = false;
	Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==c && listeners[i+1]==a) {
		    isListener=true;
	    }
	}
	return isListener;
    }

    /**
     * Returns the currently set <code>Action</code> for this
     * <code>ActionEvent</code> source, or <code>null</code> if no
     * <code>Action</code> is set.
     *
     * @return the <code>Action</code> for this <code>ActionEvent</code>
     *		source; or <code>null</code>
     * @since 1.3
     * @see Action
     * @see #setAction
     */
    public Action getAction() {
	return action;
    }

    /**
     * Factory method which sets the <code>ActionEvent</code> source's
     * properties according to values from the <code>Action</code> instance.
     * The properties which are set may differ for subclasses.
     * By default, the properties which get set are 
     * <code>Enabled</code> and <code>ToolTipText</code>.
     *
     * @param a the <code>Action</code> from which to get the properties,
     *				or <code>null</code>
     * @since 1.3
     * @see Action
     * @see #setAction
     */
    protected void configurePropertiesFromAction(Action a) {
	setEnabled((a!=null?a.isEnabled():true));
 	setToolTipText((a!=null?(String)a.getValue(Action.SHORT_DESCRIPTION):null));	
    }

    /**
     * Factory method which creates the <code>PropertyChangeListener</code>
     * used to update the <code>ActionEvent</code> source as properties change
     * on its <code>Action</code> instance.
     * Subclasses may override this in order to provide their own
     * <code>PropertyChangeListener</code> if the set of
     * properties which should be kept up to date differs from the
     * default properties (Text, Icon, Enabled, ToolTipText).
     *
     * Note that <code>PropertyChangeListeners</code> should avoid holding
     * strong references to the <code>ActionEvent</code> source,
     * as this may hinder garbage collection of the <code>ActionEvent</code>
     * source and all components in its containment hierarchy.  
     *
     * @since 1.3
     * @see Action
     * @see #setAction
     */
    protected PropertyChangeListener createActionPropertyChangeListener(Action a) {
        return new AbstractActionPropertyChangeListener(this, a) {
	    public void propertyChange(PropertyChangeEvent e) {	    
		String propertyName = e.getPropertyName();
		JComboBox comboBox = (JComboBox)getTarget();
		if (comboBox == null) {   //WeakRef GC'ed in 1.2
		    Action action = (Action)e.getSource();
		    action.removePropertyChangeListener(this);
		} else {
		    if (e.getPropertyName().equals(Action.SHORT_DESCRIPTION)) {
			String text = (String) e.getNewValue();
			comboBox.setToolTipText(text);
		    } else if (propertyName.equals("enabled")) {
			Boolean enabledState = (Boolean) e.getNewValue();
			comboBox.setEnabled(enabledState.booleanValue());
			comboBox.repaint();
		    } 
		}
	    }
	};
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.
     * @param e  the event of interest
     *  
     * @see EventListenerList
     */
    protected void fireItemStateChanged(ItemEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for ( int i = listeners.length-2; i>=0; i-=2 ) {
            if ( listeners[i]==ItemListener.class ) {
                // Lazily create the event:
                // if (changeEvent == null)
                // changeEvent = new ChangeEvent(this);
                ((ItemListener)listeners[i+1]).itemStateChanged(e);
            }
        }
    }   

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.
     * @param e  the event of interest
     *  
     * @see EventListenerList
     */
    protected void fireActionEvent() {
        if ( !firingActionEvent ) {
            firingActionEvent = true;
            ActionEvent e = null;
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for ( int i = listeners.length-2; i>=0; i-=2 ) {
                if ( listeners[i]==ActionListener.class ) {
                    if ( e == null )
                        e = new ActionEvent(this,ActionEvent.ACTION_PERFORMED,getActionCommand());
                    ((ActionListener)listeners[i+1]).actionPerformed(e);
                }
            }
            firingActionEvent = false;
        }
    }

    /**
     * This method is called when the selected item changes.
     * Its default implementation notifies the item listeners.
     */
    protected void selectedItemChanged() {
        if ( selectedItemReminder != null ) {
            fireItemStateChanged(new ItemEvent(this,ItemEvent.ITEM_STATE_CHANGED,
                                               selectedItemReminder,
                                               ItemEvent.DESELECTED));
        }

        selectedItemReminder = getModel().getSelectedItem();

        if ( selectedItemReminder != null )
            fireItemStateChanged(new ItemEvent(this,ItemEvent.ITEM_STATE_CHANGED,
                                               selectedItemReminder,
                                               ItemEvent.SELECTED));
        fireActionEvent();
        firedActionEventOnContentsChanged = true;
    }

    /** 
     * Returns an array containing the selected item.
     * This method is implemented for compatibility with
     * <code>ItemSelectable</code>.
     *
     * @returns an array of <code>Objects</code> containing one
     *		element -- the selected item
     */
    public Object[] getSelectedObjects() {
        Object selectedObject = getSelectedItem();
        if ( selectedObject == null )
            return new Object[0];
        else {
            Object result[] = new Object[1];
            result[0] = selectedObject;
            return result;
        }
    }

    /** 
     * This method is public as an implementation side effect. 
     * do not call or override. 
     */
    public void actionPerformed(ActionEvent e) {
        Object newItem = getEditor().getItem();
        firedActionEventOnContentsChanged = false;
        getUI().setPopupVisible(this, false);
        getModel().setSelectedItem(newItem);
        if ( !firedActionEventOnContentsChanged ) {
            fireActionEvent();
        }
        else {
            firedActionEventOnContentsChanged = false;
        }
    }

    /**
     * This method is public as an implementation side effect. 
     * do not call or override. 
     *
     * @see javax.swing.event.ListDataListener
     */
    public void contentsChanged(ListDataEvent e) {
        ComboBoxModel mod = getModel();
        Object newSelectedItem = mod.getSelectedItem();

        if ( selectedItemReminder == null ) {
            if ( newSelectedItem != null )
                selectedItemChanged();
        }
        else {
            if ( !selectedItemReminder.equals(newSelectedItem) ) {
                selectedItemChanged();
            }
        }

        if ( !isEditable() && newSelectedItem != null ) {
            int i,c;
            boolean shouldResetSelectedItem = true;
            Object o;
            Object selectedItem = mod.getSelectedItem();

            for ( i=0,c=mod.getSize();i<c;i++ ) {
                o = mod.getElementAt(i);
                if ( o.equals(selectedItem) ) {
                    shouldResetSelectedItem = false;
                    break;
                }
            }

            if ( shouldResetSelectedItem ) {
                if ( mod.getSize() > 0 )
                    setSelectedIndex(0);
                else
                    setSelectedItem(null);
            }
        }
    }

    /**
     * Selects the list item that correponds to the specified keyboard
     * character and returns true, if there is an item corresponding
     * to that character.  Otherwise, returns false.
     *
     * @param keyChar a char, typically this is a keyboard key
     *			typed by the user
     */
    public boolean selectWithKeyChar(char keyChar) {
        int index;

        if ( keySelectionManager == null )
            keySelectionManager = createDefaultKeySelectionManager();

        index = keySelectionManager.selectionForKey(keyChar,getModel());
        if ( index != -1 ) {
            setSelectedIndex(index);
            return true;
        }
        else
            return false;
    }

    /**
     * Invoked items have been added to the internal data model.
     * The "interval" includes the first and last values added. 
     *
     * @see javax.swing.event.ListDataListener
     */
    public void intervalAdded(ListDataEvent e) {
        contentsChanged(e);
    }

    /**
     * Invoked when values have been removed from the data model. 
     * The "interval" includes the first and last values removed. 
     *
     * @see javax.swing.event.ListDataListener
     */
    public void intervalRemoved(ListDataEvent e) {
        contentsChanged(e);
    }

    /**
     * Enables the combo box so that items can be selected. When the
     * combo box is disabled, items cannot be selected and values
     * cannot be typed into its field (if it is editable).
     *
     * @param b a boolean value, where true enables the component and
     *          false disables it
     * @beaninfo
     *        bound: true
     *    preferred: true
     *  description: Whether the combo box is enabled.
     */
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        firePropertyChange( "enabled", !isEnabled(), isEnabled() );
    }

    /**
     * Initializes the editor with the specified item.
     *                                 
     * @param anEditor the <code>ComboBoxEditor</code> that displays
     *			the list item in the
     *                 	combo box field and allows it to be edited
     * @param anItem   the object to display and edit in the field
     */
    public void configureEditor(ComboBoxEditor anEditor,Object anItem) {
        anEditor.setItem(anItem);
    }

    /**
     * Handles <code>KeyEvent</code>s, looking for the Tab key.
     * If the Tab key is found, the popup window is closed.
     *
     * @param e  the <code>KeyEvent</code> containing the keyboard
     *		key that was pressed  
     */
    public void processKeyEvent(KeyEvent e) {
        if ( e.getKeyCode() == KeyEvent.VK_TAB ) {
            hidePopup();
        }
        super.processKeyEvent(e);
    }

    /**
     * Returns true if the component can receive the focus. In this case,
     * the component returns false if it is editable, so that the
     * <code>Editor</code> object receives the focus,
     * instead of the component.
     *
     * @return true if the component can receive the focus, else false
     */
    public boolean isFocusTraversable() {
        return getUI().isFocusTraversable(this);
    }

    /**
     * Sets the object that translates a keyboard character into a list
     * selection. Typically, the first selection with a matching first
     * character becomes the selected item.
     *
     * @beaninfo
     *       expert: true
     *  description: The objects that changes the selection when a key is pressed.
     */
    public void setKeySelectionManager(KeySelectionManager aManager) {
        keySelectionManager = aManager;
    }

    /**
     * Returns the list's key-selection manager.
     *
     * @return the <code>KeySelectionManager</code> currently in use
     */
    public KeySelectionManager getKeySelectionManager() {
        return keySelectionManager;
    }

    /* Accessing the model */
    /**
     * Returns the number of items in the list.
     *
     * @return an integer equal to the number of items in the list
     */
    public int getItemCount() {
        return dataModel.getSize();
    }

    /**
     * Returns the list item at the specified index.  If <code>index</code>
     * is out of range (less than zero or greater than or equal to size)
     * it will return <code>null</code>.
     *
     * @param index  an integer indicating the list position, where the first
     *               item starts at zero
     * @return the <code>Object</code> at that list position; or
     *			<code>null</code> if out of range
     */
    public Object getItemAt(int index) {
        return dataModel.getElementAt(index);
    }

    /**
     * Returns an instance of the default key-selection manager.
     *
     * @return the <code>KeySelectionManager</code> currently used by the list
     * @see #setKeySelectionManager
     */
    protected KeySelectionManager createDefaultKeySelectionManager() {
        return new DefaultKeySelectionManager();
    }


    /**
     * The interface that defines a <code>KeySelectionManager</code>.
     * To qualify as a <code>KeySelectionManager</code>,
     * the class needs to implement the method
     * that identifies the list index given a character and the 
     * combo box data model.
     */
    public interface KeySelectionManager {
        /** Given <code>aKey</code> and the model, returns the row
         *  that should become selected. Return -1 if no match was
         *  found. 
         *
         * @param  aKey  a char value, usually indicating a keyboard key that
         *               was pressed
         * @param aModel a ComboBoxModel -- the component's data model, containing
         *               the list of selectable items 
         * @return an int equal to the selected row, where 0 is the
         *         first item and -1 is none. 
         */
        int selectionForKey(char aKey,ComboBoxModel aModel);
    }

    class DefaultKeySelectionManager implements KeySelectionManager, Serializable {
        public int selectionForKey(char aKey,ComboBoxModel aModel) {
            int i,c;
            int currentSelection = -1;
            Object selectedItem = aModel.getSelectedItem();
            String v;
            String pattern;

            if ( selectedItem != null ) {
                selectedItem = selectedItem.toString();

                for ( i=0,c=aModel.getSize();i<c;i++ ) {
                    if ( selectedItem.equals(aModel.getElementAt(i).toString()) ) {
                        currentSelection  =  i;
                        break;
                    }

                }
            }

            pattern = ("" + aKey).toLowerCase();
            aKey = pattern.charAt(0);

            for ( i = ++currentSelection, c = aModel.getSize() ; i < c ; i++ ) {
                v = aModel.getElementAt(i).toString().toLowerCase();
                if ( v.length() > 0 && v.charAt(0) == aKey )
                    return i;
            }

            for ( i = 0 ; i < currentSelection ; i ++ ) {
                v = aModel.getElementAt(i).toString().toLowerCase();
                if ( v.length() > 0 && v.charAt(0) == aKey )
                    return i;
            }
            return -1;
        }
    }


    /** 
     * See <code>readObject</code> and <code>writeObject</code> in
     * </code>JComponent</code> for more 
     * information about serialization in Swing.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        if ((ui != null) && (getUIClassID().equals(uiClassID))) {
            ui.installUI(this);
        }
    }


    /**
     * Returns a string representation of this <code>JComboBox</code>.
     * This method is intended to be used only for debugging purposes,
     * and the content and format of the returned string may vary between   
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this <code>JComboBox</code>
     */
    protected String paramString() {
        String selectedItemReminderString = (selectedItemReminder != null ?
                                             selectedItemReminder.toString() :
                                             "");
        String isEditableString = (isEditable ? "true" : "false");
        String lightWeightPopupEnabledString = (lightWeightPopupEnabled ?
                                                "true" : "false");

        return super.paramString() +
        ",isEditable=" + isEditableString +
        ",lightWeightPopupEnabled=" + lightWeightPopupEnabledString +
        ",maximumRowCount=" + maximumRowCount +
        ",selectedItemReminder=" + selectedItemReminderString;
    }


///////////////////
// Accessibility support
///////////////////

    /**
     * Gets the AccessibleContext associated with this JComboBox. 
     * For combo boxes, the AccessibleContext takes the form of an 
     * AccessibleJComboBox. 
     * A new AccessibleJComboBox instance is created if necessary.
     *
     * @return an AccessibleJComboBox that serves as the 
     *         AccessibleContext of this JComboBox
     */
    public AccessibleContext getAccessibleContext() {
        if ( accessibleContext == null ) {
            accessibleContext = new AccessibleJComboBox();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the 
     * <code>JComboBox</code> class.  It provides an implementation of the 
     * Java Accessibility API appropriate to Combo Box user-interface elements.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected class AccessibleJComboBox extends AccessibleJComponent 
    implements AccessibleAction, AccessibleSelection {

        /**
         * Returns the number of accessible children in the object.  If all
         * of the children of this object implement Accessible, than this
         * method should return the number of children of this object.
         *
         * @return the number of accessible children in the object.
         */
        public int getAccessibleChildrenCount() {
            // Always delegate to the UI if it exists
            if (ui != null) {
                return ui.getAccessibleChildrenCount(JComboBox.this);
            } else {
                return super.getAccessibleChildrenCount();
            }
        }

        /**
         * Returns the nth Accessible child of the object.
         * The child at index zero represents the popup.
         * If the combo box is editable, the child at index one
         * represents the editor.
         *
         * @param i zero-based index of child
         * @return the nth Accessible child of the object
         */
        public Accessible getAccessibleChild(int i) {
            // Always delegate to the UI if it exists
            if (ui != null) {
                return ui.getAccessibleChild(JComboBox.this, i);
            } else {
               return super.getAccessibleChild(i);
            }
        }

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the
         * object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.COMBO_BOX;
        }

        /**
         * Get the AccessibleAction associated with this object.  In the
         * implementation of the Java Accessibility API for this class, 
	 * return this object, which is responsible for implementing the
         * AccessibleAction interface on behalf of itself.
	 * 
	 * @return this object
         */
        public AccessibleAction getAccessibleAction() {
            return this;
        }

        /**
         * Return a description of the specified action of the object.
         *
         * @param i zero-based index of the actions
         */
        public String getAccessibleActionDescription(int i) {
            if (i == 0) {
                return UIManager.getString("ComboBox.togglePopupText");
            }
            else {
                return null;
            }
        }

        /**
         * Returns the number of Actions available in this object.  The 
         * default behavior of a combo box is to have one action.
         *
         * @return 1, the number of Actions in this object
         */
        public int getAccessibleActionCount() {
            return 1;
        }

        /**
         * Perform the specified Action on the object
         *
         * @param i zero-based index of actions
         * @return true if the the action was performed; else false.
         */
        public boolean doAccessibleAction(int i) {
            if (i == 0) {
                setPopupVisible(!isPopupVisible());
                return true;
            }
            else {
                return false;
            }
        }


        /**
         * Get the AccessibleSelection associated with this object.  In the
         * implementation of the Java Accessibility API for this class, 
	 * return this object, which is responsible for implementing the
         * AccessibleSelection interface on behalf of itself.
	 * 
	 * @return this object
         */
        public AccessibleSelection getAccessibleSelection() {
	    return this;
        }

	/**
	 * Returns the number of Accessible children currently selected.
	 * If no children are selected, the return value will be 0.
	 *
	 * @return the number of items currently selected.
	 */
	public int getAccessibleSelectionCount() {
	    Object o = JComboBox.this.getSelectedItem();
	    if (o != null) {
		return 1;
	    } else {
		return 0;
	    }
	}
	
	/**
	 * Returns an Accessible representing the specified selected child
	 * in the popup.  If there isn't a selection, or there are 
	 * fewer children selected than the integer passed in, the return
	 * value will be null.
	 * <p>Note that the index represents the i-th selected child, which
	 * is different from the i-th child.
	 *
	 * @param i the zero-based index of selected children
	 * @return the i-th selected child
	 * @see #getAccessibleSelectionCount
	 */
	public Accessible getAccessibleSelection(int i) {
	    // Get the popup
	    Accessible a = 
		JComboBox.this.getUI().getAccessibleChild(JComboBox.this, 0);
	    if (a != null && 
		a instanceof javax.swing.plaf.basic.ComboPopup) {

		// get the popup list
		JList list = ((javax.swing.plaf.basic.ComboPopup)a).getList();
		
		// return the i-th selection in the popup list
		AccessibleContext ac = list.getAccessibleContext();
		if (ac != null) {
		    AccessibleSelection as = ac.getAccessibleSelection();
		    if (as != null) {
			return as.getAccessibleSelection(i);
		    }
		}
	    }
	    return null;
	}
	
	/**
	 * Determines if the current child of this object is selected.
	 *
	 * @return true if the current child of this object is selected; 
	 * 		else false
	 * @param i the zero-based index of the child in this Accessible
	 * object.
	 * @see AccessibleContext#getAccessibleChild
	 */
	public boolean isAccessibleChildSelected(int i) {
	    return JComboBox.this.getSelectedIndex() == i;
	}
	
	/**
	 * Adds the specified Accessible child of the object to the object's
	 * selection.  If the object supports multiple selections,
	 * the specified child is added to any existing selection, otherwise
	 * it replaces any existing selection in the object.  If the
	 * specified child is already selected, this method has no effect.
	 *
	 * @param i the zero-based index of the child
	 * @see AccessibleContext#getAccessibleChild
	 */
	public void addAccessibleSelection(int i) {
	    JComboBox.this.setSelectedIndex(i);
	}
	
	/**
	 * Removes the specified child of the object from the object's
	 * selection.  If the specified item isn't currently selected, this
	 * method has no effect.
	 *
	 * @param i the zero-based index of the child
	 * @see AccessibleContext#getAccessibleChild
	 */
	public void removeAccessibleSelection(int i) {
	    if (JComboBox.this.getSelectedIndex() == i) {
		clearAccessibleSelection();
	    }
	}

	/**
	 * Clears the selection in the object, so that no children in the
	 * object are selected.
	 */
	public void clearAccessibleSelection() {
	    JComboBox.this.setSelectedIndex(-1);
	}
	
	/**
	 * Causes every child of the object to be selected
	 * if the object supports multiple selections.
	 */
	public void selectAllAccessibleSelection() {
	    // do nothing since multiple selection is not supported
	}

//        public Accessible getAccessibleAt(Point p) {
//            Accessible a = getAccessibleChild(1);
//            if ( a != null ) {
//                return a; // the editor
//            }
//            else {
//                return getAccessibleChild(0); // the list
//            }
//        }

    } // innerclass AccessibleJComboBox
}
