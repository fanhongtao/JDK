/** 
 * @(#)JComboBox.java	1.54 98/08/28
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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
 * Swing's implementation of a ComboBox -- a combination of a text field and 
 * drop-down list that lets the user either type in a value or select it from 
 * a list that is displayed when the user asks for it. The editing capability 
 * can also be disabled so that the JComboBox acts only as a drop down list.
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
 *
 * @version 1.54 08/28/98
 * @author Arnaud Weber
 */
public class JComboBox extends JComponent 
    implements ItemSelectable,ListDataListener,ActionListener, Accessible 
{
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
    protected boolean lightWeightPopupEnabled = true;

    /**
     * Creates a JComboBox that takes its items from an existing ComboBoxModel.
     *
     * @param aModel the ComboBoxModel that provides the displayed list of items
     */
    public JComboBox(ComboBoxModel aModel) {
        super();
        setModel(aModel);
        installAncestorListener();
        setOpaque(true);
        updateUI();
    }

    /** 
     * Creates a JComboBox that contains the elements in the specified array.
     */
    public JComboBox(final Object items[]) {
        super();
        setModel(new DefaultComboBoxModel(items));
        installAncestorListener();
        updateUI();
    }

    /**
     * Creates a JComboBox that contains the elements in the specified Vector.
     */
    public JComboBox(Vector items) {
        super();
        setModel(new DefaultComboBoxModel(items));
        installAncestorListener();
        updateUI();
    }

    /**
     * Creates a JComboBox with a default data model.
     * The default data model is an empty list of objects. 
     * Use <code>addItem</code> to add items.
     */
    public JComboBox() {
        super();
        setModel(new DefaultComboBoxModel());
        installAncestorListener();
        updateUI();
    }

    protected void installAncestorListener() {
        addAncestorListener(new AncestorListener(){
                            public void ancestorAdded(AncestorEvent event){ hidePopup();}
                           public void ancestorRemoved(AncestorEvent event){ hidePopup();}
                           public void ancestorMoved(AncestorEvent event){ hidePopup();}});
    }

    /**
     * Sets the L&F object that renders this component.
     *
     * @param ui  the ComboBoxUI L&F object
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
     * Notification from the UIFactory that the L&F has changed. 
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((ComboBoxUI)UIManager.getUI(this));
    }


    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return "ComboBoxUI"
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
        return (ComboBoxUI)ui;
    }

    /**
     * Sets the data model that the JComboBox uses to obtain the list of items.
     *
     * @param aModel the ComboBoxModel that provides the displayed list of items
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
     * Returns the data model currently used by the JComboBox.
     *
     * @return the ComboBoxModel that provides the displayed list of items
     */
    public ComboBoxModel getModel() {
        return dataModel;
    }

    /*
     * Properties
     */

    /**
     * When displaying the popup, JComboBox choose to use a light weight popup if
     * it fits. This method allows you to disable this feature. You have to do disable
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
     * Determines whether the JComboBox field is editable. An editable JComboBox
     * allows the user to type into the field or selected an item from the list
     * to initialize the field, after which it can be edited. (The editing affects
     * only the field, the list item remains intact.) A non editable JComboBox 
     * displays the selected item inthe field, but the selection cannot be modified.
     *
     * @param aFlag a boolean value, where true indicates that the field is editable
     * 
     * @beaninfo
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
     * Returns true if the JComboBox is editable.
     * 
     * @return true if the JComboBox is editable, else false
     */
    public boolean isEditable() {
        return isEditable;
    }

    /**
     * Sets the maximum number of rows the JComboBox displays.
     * If the number of objects in the model is greater than count,
     * the combo box uses a scrollbar.
     *
     * @param count an int specifying the maximum number of items to display
     *              in the list before using a scrollbar
     * @beaninfo
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
     * @return an int specifying the maximum number of items that are displayed
     *         in the list before using a scrollbar
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
     * To display the selected item, <code>aRenderer.getListCellRendererComponent</code>
     * is called, passing the list object and an index of -1.
     *  
     * @param aRenderer  the ListCellRenderer that displays the selected item.
     * @see #setEditor
     * @beaninfo
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
     * Returns the renderer used to display the selected item in the JComboBox
     * field.
     *  
     * @return  the ListCellRenderer that displays the selected item.
     */
    public ListCellRenderer getRenderer() {
        return renderer;
    }

    /**
     * Sets the editor used to paint and edit the selected item in the JComboBox
     * field. The editor is used only if the receiving JComboBox is editable. 
     * If not editable, the combo box uses the renderer to paint the selected item.
     *  
     * @param anEditor  the ComboBoxEditor that displays the selected item
     * @see #setRenderer
     * @beaninfo
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
     * Returns the editor used to paint and edit the selected item in the JComboBox
     * field.
     *  
     * @return the ComboBoxEditor that displays the selected item
     */
    public ComboBoxEditor getEditor() {
        return editor;
    }

    /*
     * Selection
     */
    /** 
     * Sets the selected item in the JComboBox by specifying the object in the list.
     * If <code>anObject</code> is in the list, the list displays with 
     * <code>anObject</code> selected. If the object does not exist in the list,
     * the default data model selects the first item in the list.
     *
     * @param anObject  the list object to select
     * @beaninfo
     *    preferred:   true
     *    description: Sets the selected item in the JComboBox.
     */
    public void setSelectedItem(Object anObject) {
        dataModel.setSelectedItem(anObject);
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
     * @param anIndex an int specifying the list item to select, where 0 specifies
     *                the first item in the list
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
     * Returns the index of the currently selected item in the list. The result is not
     * always defined if the JComboBox box allows selected items that are not in the
     * list. 
     * Returns -1 if there is no selected item or if the user specified an item
     * which is not in the list.
     
     * @return an int specifying the currently selected list item, where 0 specifies
     *                the first item in the list, or -1 if no item is selected or if
     *                the currently selected item is not in the list
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
     * This method works only if the JComboBox uses the default data model.
     * JComboBox uses the default data model when created with the 
     * empty constructor and no other model has been set.
     *
     * @param anObject the Object to add to the list
     */
    public void addItem(Object anObject) {
        checkMutableComboBoxModel();
        ((MutableComboBoxModel)dataModel).addElement(anObject);
    }

    /** 
     * Inserts an item into the item list at a given index. 
     * This method works only if the JComboBox uses the default data model.
     * JComboBox uses the default data model when created with the 
     * empty constructor and no other model has been set.
     *
     * @param anObject the Object to add to the list
     * @param index    an int specifying the position at which to add the item
     */
    public void insertItemAt(Object anObject, int index) {
        checkMutableComboBoxModel();
        ((MutableComboBoxModel)dataModel).insertElementAt(anObject,index);
    }

    /** 
     * Removes an item from the item list.
     * This method works only if the JComboBox uses the default data model.
     * JComboBox uses the default data model when created with the empty constructor
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
     * This method works only if the JComboBox uses the default data model.
     * JComboBox uses the default data model when created with the 
     * empty constructor and no other model has been set.
     *
     * @param anIndex  an int specifying the idex of the item to remove, where 0
     *                 indicates the first item in the list
     */
    public void removeItemAt(int anIndex) {
        checkMutableComboBoxModel();
	((MutableComboBoxModel)dataModel).removeElementAt( anIndex );
    }

    /** 
     * Removes all items from the item list.
     * This method works only if the JComboBox uses the default data model.
     * JComboBox uses the default data model when created with the empty constructor
     * and no other model has been set.
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

    void checkMutableComboBoxModel() {
        if ( !(dataModel instanceof MutableComboBoxModel) )
            throw new InternalError("Cannot use this method with a non-Mutable data model.");
    }

    /** 
     * Causes the combo box to display its popup window 
     * @see #setPopupVisible
     */
    public void showPopup() {
        setPopupVisible(true);
    }

    /** 
     * Causes the combo box to close its popup window 
     * @see #setPopupVisible
     */
    public void hidePopup() {
        setPopupVisible(false);
    }

    /**
     * Set the visiblity of the popup
     */
    public void setPopupVisible(boolean v) {
        getUI().setPopupVisible(this, v);
    }
	
    /** 
     * Determine the visibility of the popup
     */
    public boolean isPopupVisible() {
	return getUI().isPopupVisible(this);
    }

    /** Selection **/

    /** 
     * Adds an ItemListener. <code>aListener</code> will receive an event when
     * the selected item changes.
     *
     * @param aListener  the ItemListener that is to be notified
     */
    public void addItemListener(ItemListener aListener) {
        listenerList.add(ItemListener.class,aListener);
    }

    /** Removes an ItemListener
     *
     * @param aListener  the ItemListener to remove
     */
    public void removeItemListener(ItemListener aListener) {
        listenerList.remove(ItemListener.class,aListener);
    }

    /** 
     * Adds an ActionListener. The listener will receive an action event
     * the user finishes making a selection.
     *
     * @param l  the ActionListener that is to be notified
     */
    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class,l);
    }

    /** Removes an ActionListener 
     *
     * @param l  the ActionListener to remove
     */
    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class,l);
    }

    /** 
     * Sets the action commnand that should be included in the event
     * sent to action listeners.
     *
     * @param aCommand  a string containing the "command" that is sent
     *                  to action listeners. The same listener can then
     *                  do different things depending on the command it
     *                  receives.
     */
    public void setActionCommand(String aCommand) {
        actionCommand = aCommand;
    }

    /** 
     * Returns the action commnand that is included in the event sent to
     *  action listeners.
     *
     * @return  the string containing the "command" that is sent
     *          to action listeners.
     */
    public String getActionCommand() {
        return actionCommand;
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.
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
     * Notify all listeners that have registered interest for
     * notification on this event type.
     *  
     * @see EventListenerList
     */
    protected void fireActionEvent() {
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
    }

    /**
     * This method is called when the selected item changes. Its default implementation
     *  notifies the item listeners
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
    }

    /** 
     * Returns an array containing the selected item. This method is implemented for 
     * compatibility with ItemSelectable.
     *
     * @returns an array of Objects containing one element -- the selected item
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

    /** This method is public as an implementation side effect. 
     *  do not call or override. 
     */
    public void actionPerformed(ActionEvent e) {
        Object newItem = getEditor().getItem();
        getModel().setSelectedItem(newItem);
        getUI().setPopupVisible(this, false);
    }

    /** This method is public as an implementation side effect. 
     *  do not call or override. 
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
	    if ( !selectedItemReminder.equals(newSelectedItem) )
	        selectedItemChanged();
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
     * Selects the list item that correponds to the specified keyboard character
     * and returns true, if there is an item corresponding to that character.
     * Otherwise, returns false.
     *
     * @param keyChar a char, typically this is a keyboard key typed by the user
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
     * The"interval" includes the first and last values removed. 
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
     * @param anEditor the ComboBoxEditor that displays the list item in the
     *                 combo box field and allows it to be edited
     * @param anItem   the object to display and edit in the field
     */
    public void configureEditor(ComboBoxEditor anEditor,Object anItem) {
        anEditor.setItem(anItem);
    }

    /**
     * Handles KeyEvents, looking for the Tab key. If the Tab key is found,
     * the popup window is closed.
     *
     * @param e  the KeyEvent containing the keyboard key that was pressed  
     */
    public void processKeyEvent(KeyEvent e) {
        if ( e.getKeyCode() == KeyEvent.VK_TAB ) {
            hidePopup();
        }
        super.processKeyEvent(e);
    }

    /**
     * Returns true if the component can receive the focus. In this case,
     * the component returns false if it is editable, so that the Editor
     * object receives the focus, instead of the component.
     *
     * @return true if the component can receive the focus, else false. 
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
     * @return the KeySelectionManager currently in use
     */
    public KeySelectionManager getKeySelectionManager() {
        return keySelectionManager;
    }

    /* Accessing the model */
    /**
     * Returns the number of items in the list.
     *
     * @return an int equal to the number of items in the list
     */
    public int getItemCount() {
        return dataModel.getSize();
    }

    /**
     * Returns the list item at the specified index.
     *
     * @param index  an int indicating the list position, where the first
     *               item starts at zero
     * @return the Object at that list position
     */
    public Object getItemAt(int index) {
        return dataModel.getElementAt(index);
    }

    /**
     * Returns an instance of the default key-selection manager.
     *
     * @return the KeySelectionManager currently used by the list
     * @see #setKeySelectionManager
     */
    protected KeySelectionManager createDefaultKeySelectionManager() {
        return new DefaultKeySelectionManager();
    }


    /**
     * The interface that defines a KeySelectionManager. To qualify as
     * a KeySelectionManager, the class needs to implement the method
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
     * See readObject() and writeObject() in JComponent for more 
     * information about serialization in Swing.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
	if ((ui != null) && (getUIClassID().equals(uiClassID))) {
	    ui.installUI(this);
	}
    }


    /**
     * Returns a string representation of this JComboBox. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * <P>
     * Overriding paramString() to provide information about the
     * specific new aspects of the JFC components.
     * 
     * @return  a string representation of this JComboBox.
     */
    protected String paramString() {
	String selectedItemReminderString = (selectedItemReminder != null ?
					     selectedItemReminder.toString() :
					     "");
	String isEditableString = (isEditable ?	"true" : "false");
	String lightWeightPopupEnabledString = (lightWeightPopupEnabled ?
						"true" : "false");

	return super.paramString() +
	",isEditable=" + isEditableString +
	",lightWeightPopupEnabled=" + lightWeightPopupEnabledString +
	",maximumRowCount=" + maximumRowCount +
	",selectedItemReminder=" + selectedItemReminderString;
    }


///////////////////
// Accessiblity support
///////////////////

    /**
     * Get the AccessibleContext associated with this JComponent
     *
     * @return the AccessibleContext of this JComponent
     */
    public AccessibleContext getAccessibleContext() {
        if ( accessibleContext == null ) {
            accessibleContext = new AccessibleJComboBox();
        }
        return accessibleContext;
    }

    /**
     * The class used to obtain the accessible role for this object.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected class AccessibleJComboBox extends AccessibleJComponent 
	implements AccessibleAction {

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
         * Get the AccessibleAction associated with this object if one
         * exists.  Otherwise return null.
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
                // [[[PENDING:  WDW -- need to provide a localized string]]]
                return new String("togglePopup");
            } else {
                return null;
            }
        }
    
        /**
         * Returns the number of Actions available in this object.
         * If there is more than one, the first one is the "default"
         * action.
         *
         * @return the number of Actions in this object
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
            } else {
                return false;
            }
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
