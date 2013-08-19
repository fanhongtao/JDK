/*
 * @(#)BasicComboBoxUI.java	1.155 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.accessibility.*;
import javax.swing.FocusManager;
import javax.swing.plaf.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import sun.awt.AppContext;

/**
 * Basic UI implementation for JComboBox. 
 * <p>
 * The combo box is a compound component which means that it is an agregate of
 * many simpler components. This class creates and manages the listeners
 * on the combo box and the combo box model. These listeners update the user 
 * interface in response to changes in the properties and state of the combo box.
 * <p>
 * All event handling is handled by listener classes created with the 
 * <code>createxxxListener()</code> methods and internal classes.
 * You can change the behavior of this class by overriding the
 * <code>createxxxListener()</code> methods and supplying your own
 * event listeners or subclassing from the ones supplied in this class.
 * <p>
 * For adding specific actions, 
 * overide <code>installKeyboardActions</code> to add actions in response to
 * KeyStroke bindings. See the article <a href="http://java.sun.com/products/jfc/tsc/special_report/kestrel/keybindings.html">Keyboard Bindings in Swing</a>
 * at <a href="http://java.sun.com/products/jfc/tsc"><em>The Swing Connection</em></a>.
 *
 * @version 1.155 01/23/03
 * @author Arnaud Weber
 * @author Tom Santos
 * @author Mark Davidson
 */
public class BasicComboBoxUI extends ComboBoxUI {
    protected JComboBox comboBox;
    /**
     * This protected field is implementation specific. Do not access directly
     * or override. 
     */
    protected boolean   hasFocus = false;

    // Control the selection behavior of the JComboBox when it is used
    // in the JTable DefaultCellEditor. 
    private boolean isTableCellEditor = false;
    private static final String IS_TABLE_CELL_EDITOR = "JComboBox.isTableCellEditor";

    // This list is for drawing the current item in the combo box.
    protected JList   listBox;

    // Used to render the currently selected item in the combo box.
    // It doesn't have anything to do with the popup's rendering.
    protected CellRendererPane currentValuePane = new CellRendererPane();

    // The implementation of ComboPopup that is used to show the popup.
    protected ComboPopup popup;

    // The Component that the ComboBoxEditor uses for editing
    protected Component editor;

    // The arrow button that invokes the popup.
    protected JButton   arrowButton;

    // Listeners that are attached to the JComboBox
    /**
     * This protected field is implementation specific. Do not access directly
     * or override. Override the listener construction method instead.
     *
     * @see #createKeyListener
     */
    protected KeyListener keyListener;
    /**
     * This protected field is implementation specific. Do not access directly
     * or override. Override the listener construction method instead.
     *
     * @see #createFocusListener
     */
    protected FocusListener focusListener;
    /**
     * This protected field is implementation specific. Do not access directly
     * or override. Override the listener construction method instead.
     *
     * @see #createPropertyChangeListener
     */
    protected PropertyChangeListener propertyChangeListener;

    private FocusListener editorFocusListener;
    private ActionListener editorActionListener;

    /**
     * This protected field is implementation specific. Do not access directly
     * or override. Override the listener construction method instead.
     *
     * @see #createItemListener
     */
    protected ItemListener itemListener;

    // Listeners that the ComboPopup produces.
    protected MouseListener popupMouseListener;
    protected MouseMotionListener popupMouseMotionListener;
    protected KeyListener popupKeyListener;

    // This is used for knowing when to cache the minimum preferred size.
    // If the data in the list changes, the cached value get marked for recalc.
    // Added to the current JComboBox model
    /**
     * This protected field is implementation specific. Do not access directly
     * or override. Override the listener construction method instead.
     *
     * @see #createListDataListener
     */
    protected ListDataListener listDataListener;

    // Flag for recalculating the minimum preferred size.
    protected boolean isMinimumSizeDirty = true;

    // Cached minimum preferred size.
    protected Dimension cachedMinimumSize = new Dimension( 0, 0 );

    // Flag for calculating the display size
    private boolean isDisplaySizeDirty = true;
    
    // Cached the size that the display needs to render the largest item
    private Dimension cachedDisplaySize = new Dimension( 0, 0 );

    // Key used for lookup of the DefaultListCellRenderer in the AppContext.
    private static final Object COMBO_UI_LIST_CELL_RENDERER_KEY =
                        new StringBuffer("DefaultListCellRendererKey");

    // Used for calculating the default size.
    private static ListCellRenderer getDefaultListCellRenderer() {
        ListCellRenderer renderer = (ListCellRenderer)AppContext.
                         getAppContext().get(COMBO_UI_LIST_CELL_RENDERER_KEY);

        if (renderer == null) {
            renderer = new DefaultListCellRenderer();
            AppContext.getAppContext().put(COMBO_UI_LIST_CELL_RENDERER_KEY,
                                           new DefaultListCellRenderer());
        }
        return renderer;
    }

    //========================
    // begin UI Initialization
    //

    public static ComponentUI createUI(JComponent c) {
        return new BasicComboBoxUI();
    }

    public void installUI( JComponent c ) {
        isMinimumSizeDirty = true;

        comboBox = (JComboBox)c;
        installDefaults();
        popup = createPopup();
        listBox = popup.getList();

	// Is this combo box a cell editor?
        Boolean inTable = (Boolean)c.getClientProperty(IS_TABLE_CELL_EDITOR );
	if (inTable != null) {
	    isTableCellEditor = inTable.equals(Boolean.TRUE) ? true : false;
	}

        if ( comboBox.getRenderer() == null || comboBox.getRenderer() instanceof UIResource ) {
            comboBox.setRenderer( createRenderer() );
        }

        if ( comboBox.getEditor() == null || comboBox.getEditor() instanceof UIResource ) {
            comboBox.setEditor( createEditor() );
        }

        installListeners();
        installComponents();

        comboBox.setLayout( createLayoutManager() );

        comboBox.setRequestFocusEnabled( true );

	installKeyboardActions();
    }

    public void uninstallUI( JComponent c ) {
        setPopupVisible( comboBox, false);
        popup.uninstallingUI();

        uninstallKeyboardActions();

        comboBox.setLayout( null );

        uninstallComponents();
        uninstallListeners();
        uninstallDefaults();

        if ( comboBox.getRenderer() == null || comboBox.getRenderer() instanceof UIResource ) {
            comboBox.setRenderer( null );
        }
        if ( comboBox.getEditor() == null || comboBox.getEditor() instanceof UIResource ) {
            comboBox.setEditor( null );
        }

        keyListener = null;
        focusListener = null;
        listDataListener = null;
        propertyChangeListener = null;
	editorActionListener = null;
	editorFocusListener = null;
        popup = null;
        listBox = null;
        comboBox = null;
    }

    /**
     * Installs the default colors, default font, default renderer, and default
     * editor into the JComboBox.
     */
    protected void installDefaults() {
        LookAndFeel.installColorsAndFont( comboBox,
                                          "ComboBox.background",
                                          "ComboBox.foreground",
                                          "ComboBox.font" );
        LookAndFeel.installBorder( comboBox, "ComboBox.border" );
    }

    /**
     * Create and install the listeners for the combo box and its model.
     * This method is called when the UI is installed.
     */
    protected void installListeners() {
	if ( (itemListener = createItemListener()) != null) {
	    comboBox.addItemListener( itemListener );
	}
        if ( (propertyChangeListener = createPropertyChangeListener()) != null ) {
            comboBox.addPropertyChangeListener( propertyChangeListener );
        }
        if ( (keyListener = createKeyListener()) != null ) {
            comboBox.addKeyListener( keyListener );
        }
        if ( (focusListener = createFocusListener()) != null ) {
            comboBox.addFocusListener( focusListener );
        }
	if ((popupMouseListener = popup.getMouseListener()) != null) {
	    comboBox.addMouseListener( popupMouseListener );
	}
	if ((popupMouseMotionListener = popup.getMouseMotionListener()) != null) {
	    comboBox.addMouseMotionListener( popupMouseMotionListener );
	}
	if ((popupKeyListener = popup.getKeyListener()) != null) {
	    comboBox.addKeyListener(popupKeyListener);
	}

        if ( comboBox.getModel() != null ) {
            if ( (listDataListener = createListDataListener()) != null ) {
                comboBox.getModel().addListDataListener( listDataListener );
            }
        }
    }

    /**
     * Uninstalls the default colors, default font, default renderer, and default
     * editor into the JComboBox.
     */
    protected void uninstallDefaults() {
        LookAndFeel.installColorsAndFont( comboBox,
                                          "ComboBox.background",
                                          "ComboBox.foreground",
                                          "ComboBox.font" );
        LookAndFeel.uninstallBorder( comboBox );
    }

    /**
     * Remove the installed listeners from the combo box and its model.
     * The number and types of listeners removed and in this method should be
     * the same that was added in <code>installListeners</code>
     */
    protected void uninstallListeners() {
        if ( keyListener != null ) {
            comboBox.removeKeyListener( keyListener );
        }
	if ( itemListener != null) {
	    comboBox.removeItemListener( itemListener );
	}
        if ( propertyChangeListener != null ) {
            comboBox.removePropertyChangeListener( propertyChangeListener );
        }
        if ( focusListener != null) {
            comboBox.removeFocusListener( focusListener );
        }
	if ( popupMouseListener != null) {
	    comboBox.removeMouseListener( popupMouseListener );
	}
	if ( popupMouseMotionListener != null) {
	    comboBox.removeMouseMotionListener( popupMouseMotionListener );
	}
	if (popupKeyListener != null) {
	    comboBox.removeKeyListener(popupKeyListener);
	}
        if ( comboBox.getModel() != null ) {
            if ( listDataListener != null ) {
                comboBox.getModel().removeListDataListener( listDataListener );
            }
        }
    }

    /**
     * Creates the popup portion of the combo box.
     *
     * @return an instance of <code>ComboPopup</code>
     * @see ComboPopup
     */
    protected ComboPopup createPopup() {
        BasicComboPopup popup = new BasicComboPopup( comboBox );
        popup.getAccessibleContext().setAccessibleParent(comboBox);
        return popup;
    }

    /**
     * Creates a <code>KeyListener</code> which will be added to the
     * combo box. If this method returns null then it will not be added
     * to the combo box.
     * 
     * @return an instance <code>KeyListener</code> or null
     */
    protected KeyListener createKeyListener() {
        return new KeyHandler();
    } 

    /**
     * Creates a <code>FocusListener</code> which will be added to the combo box.
     * If this method returns null then it will not be added to the combo box.
     *
     * @return an instance of a <code>FocusListener</code> or null
     */
    protected FocusListener createFocusListener() {
        return new FocusHandler();
    }

    /**
     * Creates a list data listener which will be added to the
     * <code>ComboBoxModel</code>. If this method returns null then
     * it will not be added to the combo box model.
     *
     * @return an instance of a <code>ListDataListener</code> or null
     */
    protected ListDataListener createListDataListener() {
        return new ListDataHandler();
    }

    /**
     * Creates an <code>ItemListener</code> which will be added to the 
     * combo box. If this method returns null then it will not 
     * be added to the combo box.
     * <p>
     * Subclasses may override this method to return instances of their own
     * ItemEvent handlers.
     *
     * @return an instance of an <code>ItemListener</code> or null
     */
    protected ItemListener createItemListener() {
        return null;
    }

    /**
     * Creates a <code>PropertyChangeListener</code> which will be added to
     * the combo box. If this method returns null then it will not
     * be added to the combo box.
     * 
     * @return an instance of a <code>PropertyChangeListener</code> or null
     */
    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyChangeHandler();
    }

    /**
     * Creates a layout manager for managing the components which make up the 
     * combo box.
     * 
     * @return an instance of a layout manager
     */
    protected LayoutManager createLayoutManager() {
        return new ComboBoxLayoutManager();
    }

    /**
     * Creates the default renderer that will be used in a non-editiable combo 
     * box. A default renderer will used only if a renderer has not been 
     * explicitly set with <code>setRenderer</code>.
     * 
     * @return a <code>ListCellRender</code> used for the combo box
     * @see javax.swing.JComboBox#setRenderer
     */
    protected ListCellRenderer createRenderer() {
        return new BasicComboBoxRenderer.UIResource();
    }

    /**
     * Creates the default editor that will be used in editable combo boxes.  
     * A default editor will be used only if an editor has not been 
     * explicitly set with <code>setEditor</code>.
     *
     * @return a <code>ComboBoxEditor</code> used for the combo box
     * @see javax.swing.JComboBox#setEditor
     */
    protected ComboBoxEditor createEditor() {
        return new BasicComboBoxEditor.UIResource();
    }

    //
    // end UI Initialization
    //======================


    //======================
    // begin Inner classes
    //

    /**
     * This listener checks to see if the key event isn't a navigation key.  If
     * it finds a key event that wasn't a navigation key it dispatches it to
     * JComboBox.selectWithKeyChar() so that it can do type-ahead.
     *
     * This public inner class should be treated as protected. 
     * Instantiate it only within subclasses of 
     * <code>BasicComboBoxUI</code>.
     */
    public class KeyHandler extends KeyAdapter {
        public void keyPressed( KeyEvent e ) {
            if ( comboBox.isEnabled() &&
                 !isNavigationKey( e.getKeyCode() ) &&
                 isTypeAheadKey( e ) ) {

                if ( comboBox.selectWithKeyChar(e.getKeyChar()) ) {
                    e.consume();
                }
            }
        } 

        boolean isTypeAheadKey( KeyEvent e ) {
            return !e.isAltDown() && !e.isControlDown() && !e.isMetaDown();
        }
    }

    /**
     * This listener hides the popup when the focus is lost.  It also repaints
     * when focus is gained or lost.
     *
     * This public inner class should be treated as protected. 
     * Instantiate it only within subclasses of 
     * <code>BasicComboBoxUI</code>.
     */
    public class FocusHandler implements FocusListener {
        public void focusGained( FocusEvent e ) {
            hasFocus = true;
            comboBox.repaint();
	    
	    if (comboBox.isEditable() && editor != null) {
		editor.requestFocus();
	    }

            // Notify assistive technologies that the combo box
            // gained focus.
            if (comboBox instanceof Accessible) {
                AccessibleContext ac = 
                    ((Accessible)comboBox).getAccessibleContext();
                if (ac != null) {
                    ac.firePropertyChange(
                        AccessibleContext.ACCESSIBLE_STATE_PROPERTY, 
                        null, AccessibleState.FOCUSED);
                }
            }
        }

        public void focusLost( FocusEvent e ) {
            hasFocus = false;
            // GES, 980818:
            // Note that the second check here is a workaround to bug
            // 4168483.  There is a bogus focusLost sent to the
            // ComboBox with isTemporary false when a mediumweight menu 
            // is popped up.  Until this is fixed in AWT, we make the
            // tradeoff of not popping down mediumweight popups when
            // the combobox loses focus.  Although this means that the
            // combobox does not remove such menus when you tab out,
            // it is seen as more desirable than the alternative which 
            // is that mediumweight combobox menus dissappear immediately
            // on popup, rendering them completely unusable.
            if ( !e.isTemporary() && comboBox.isLightWeightPopupEnabled()) {
                setPopupVisible(comboBox, false);
            }
            comboBox.repaint();

            // Notify assistive technologies that the combo box
            // lost focus.
            if (comboBox instanceof Accessible) {
                AccessibleContext ac = 
                    ((Accessible)comboBox).getAccessibleContext();
                if (ac != null) {
                    ac.firePropertyChange(
                        AccessibleContext.ACCESSIBLE_STATE_PROPERTY, 
                        AccessibleState.FOCUSED, null);
                }
            }
        }
    }

    /**
     * This listener watches for changes in the 
     * <code>ComboBoxModel</code>.
     * <p>
     * This public inner class should be treated as protected. 
     * Instantiate it only within subclasses of 
     * <code>BasicComboBoxUI</code>.
     *
     * @see #createListDataListener
     */
    public class ListDataHandler implements ListDataListener {
        public void contentsChanged( ListDataEvent e ) {
	    if ( !(e.getIndex0() == -1 && e.getIndex1() == -1) ) {
		isMinimumSizeDirty = true;
		comboBox.revalidate();
	    }

	    // set the editor with the selected item since this
	    // is the event handler for a selected item change.
	    if (comboBox.isEditable() && editor != null) {
		comboBox.configureEditor( comboBox.getEditor(), 
					  comboBox.getSelectedItem() ); 
	    }

	    comboBox.repaint();
	}

        public void intervalAdded( ListDataEvent e ) {
	    isDisplaySizeDirty = true;
	    contentsChanged( e );
        }

        public void intervalRemoved( ListDataEvent e ) {
	    isDisplaySizeDirty = true;
            contentsChanged( e );
        }
    }

    /**
     * This listener watches for changes to the selection in the 
     * combo box.
     * <p>
     * This public inner class should be treated as protected. 
     * Instantiate it only within subclasses of 
     * <code>BasicComboBoxUI</code>.
     *
     * @see #createItemListener
     */
    public class ItemHandler implements ItemListener {
	// This class used to implement behavior which is now redundant.
        public void itemStateChanged(ItemEvent e) {}
    }

    /**
     * This listener watches for bound properties that have changed in the
     * combo box. 
     * <p>
     * Subclasses which wish to listen to combo box property changes should
     * call the superclass methods to ensure that the combo box ui correctly 
     * handles property changes.
     * <p>
     * This public inner class should be treated as protected. 
     * Instantiate it only within subclasses of 
     * <code>BasicComboBoxUI</code>.
     * 
     * @see #createPropertyChangeListener
     */
    public class PropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();
	    JComboBox comboBox = (JComboBox)e.getSource();

            if ( propertyName.equals( "model" ) ) {
                ComboBoxModel newModel = (ComboBoxModel)e.getNewValue();
                ComboBoxModel oldModel = (ComboBoxModel)e.getOldValue();

                if ( oldModel != null && listDataListener != null ) {
                    oldModel.removeListDataListener( listDataListener );
                }

                if ( newModel != null && listDataListener != null ) {
                    newModel.addListDataListener( listDataListener );
                }

                if ( editor != null ) {
                    comboBox.configureEditor( comboBox.getEditor(), comboBox.getSelectedItem() ); 
                }
                isMinimumSizeDirty = true;
                isDisplaySizeDirty = true;
                comboBox.revalidate();
		comboBox.repaint();
            }
            else if ( propertyName.equals( "editor" ) && comboBox.isEditable() ) {
                addEditor();
                comboBox.revalidate();
            }
            else if ( propertyName.equals( "editable" ) ) {
                if ( comboBox.isEditable() ) {
                    comboBox.setRequestFocusEnabled( false );
                    addEditor();
                } else {
                    comboBox.setRequestFocusEnabled( true );
                    removeEditor();
                }

                updateToolTipTextForChildren();

                comboBox.revalidate();
            }
            else if ( propertyName.equals( "enabled" ) ) {
                boolean enabled = comboBox.isEnabled();
                if ( editor != null )
                    editor.setEnabled(enabled);
                if ( arrowButton != null )
                    arrowButton.setEnabled(enabled);
                comboBox.repaint();
            }
            else if ( propertyName.equals( "maximumRowCount" ) ) {
                if ( isPopupVisible( comboBox ) ) {
                    setPopupVisible(comboBox, false);
                    setPopupVisible(comboBox, true);
                }
            }
            else if ( propertyName.equals( "font" ) ) {
                listBox.setFont( comboBox.getFont() );
                if ( editor != null ) {
                    editor.setFont( comboBox.getFont() );
                }
                isMinimumSizeDirty = true;
                comboBox.validate();
            }
            else if ( propertyName.equals( JComponent.TOOL_TIP_TEXT_KEY ) ) {
                updateToolTipTextForChildren();
	    }
            else if ( propertyName.equals( BasicComboBoxUI.IS_TABLE_CELL_EDITOR ) ) {
                Boolean inTable = (Boolean)e.getNewValue();
		isTableCellEditor = inTable.equals(Boolean.TRUE) ? true : false;
            } 
	    else if (propertyName.equals("prototypeDisplayValue")) {
                isMinimumSizeDirty = true;
                isDisplaySizeDirty = true;
                comboBox.revalidate();
            } 
	    else if (propertyName.equals("renderer")) {
                isMinimumSizeDirty = true;
                isDisplaySizeDirty = true;
                comboBox.revalidate();
            }
        }     
    }

    // Syncronizes the ToolTip text for the components within the combo box to be the 
    // same value as the combo box ToolTip text.
    private void updateToolTipTextForChildren() {
        Component[] children = comboBox.getComponents();
        for ( int i = 0; i < children.length; ++i ) {
            if ( children[i] instanceof JComponent ) {
                ((JComponent)children[i]).setToolTipText( comboBox.getToolTipText() );
            }
        }
    }

    /**
     * This layout manager handles the 'standard' layout of combo boxes.  It puts
     * the arrow button to the right and the editor to the left.  If there is no
     * editor it still keeps the arrow button to the right.
     *
     * This public inner class should be treated as protected. 
     * Instantiate it only within subclasses of 
     * <code>BasicComboBoxUI</code>.
     */
    public class ComboBoxLayoutManager implements LayoutManager {
        public void addLayoutComponent(String name, Component comp) {}

        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            JComboBox cb = (JComboBox)parent;
            return parent.getPreferredSize();
        }

        public Dimension minimumLayoutSize(Container parent) {
            JComboBox cb = (JComboBox)parent;
            return parent.getMinimumSize();
        }

        public void layoutContainer(Container parent) {
            JComboBox cb = (JComboBox)parent;
            int width = cb.getWidth();
            int height = cb.getHeight();
            
            Insets insets = getInsets();
            int buttonSize = height - (insets.top + insets.bottom);
            Rectangle cvb;

            if ( arrowButton != null ) {
	        if(BasicGraphicsUtils.isLeftToRight(cb)) {
		    arrowButton.setBounds( width - (insets.right + buttonSize),
					   insets.top,
					   buttonSize, buttonSize);
		}
		else {
		    arrowButton.setBounds( insets.left, insets.top,
					   buttonSize, buttonSize);
		}
            }
            if ( editor != null ) {
                cvb = rectangleForCurrentValue();
                editor.setBounds(cvb);
            }
        }
    }

    //
    // end Inner classes
    //====================


    //===============================
    // begin Sub-Component Management
    //

    /**
     * Creates and initializes the components which make up the
     * aggregate combo box. This method is called as part of the UI
     * installation process.
     */
    protected void installComponents() {
        arrowButton = createArrowButton();
        comboBox.add( arrowButton );

        if (arrowButton != null)  {
            configureArrowButton();
        }

        if ( comboBox.isEditable() ) {
            addEditor();
        }

        comboBox.add( currentValuePane );
    }

    /**
     * The aggregate components which compise the combo box are 
     * unregistered and uninitialized. This method is called as part of the
     * UI uninstallation process.
     */
    protected void uninstallComponents() {
        if ( arrowButton != null ) {
            unconfigureArrowButton();
        }
        if ( editor != null ) {
            unconfigureEditor();
        }
        comboBox.removeAll(); // Just to be safe.
        arrowButton = null;
    }

    /**
     * This public method is implementation specific and should be private.
     * do not call or override. To implement a specific editor create a
     * custom <code>ComboBoxEditor</code>
     *
     * @see #createEditor
     * @see javax.swing.JComboBox#setEditor
     * @see javax.swing.ComboBoxEditor
     */
    public void addEditor() {
        removeEditor();
        editor = comboBox.getEditor().getEditorComponent();
        if ( editor != null ) {
            configureEditor(); 
            comboBox.add(editor);
        }
    }

    /**
     * This public method is implementation specific and should be private.
     * do not call or override. 
     *
     * @see #addEditor
     */
    public void removeEditor() {
        if ( editor != null ) {
            unconfigureEditor();
            comboBox.remove( editor );
	    editor = null;
        }
    }

    /**
     * This protected method is implementation specific and should be private.
     * do not call or override.
     * 
     * @see #addEditor
     */
    protected void configureEditor() {
        // Should be in the same state as the combobox
        editor.setEnabled(comboBox.isEnabled());

        editor.setFont( comboBox.getFont() );

        if ( editor instanceof Accessible ) {
            AccessibleContext ac = ((Accessible) editor).getAccessibleContext();
            if ( ac != null ) {
                ac.setAccessibleParent(comboBox);
            }
        }

        if (focusListener != null) {
            editor.addFocusListener(focusListener);
        }

        if (editorFocusListener == null)  {
            editorFocusListener = new EditorFocusListener();
        }
	editor.addFocusListener( editorFocusListener );

	if (editorActionListener == null) {
	    editorActionListener = new EditorActionListener();
	}
	comboBox.getEditor().addActionListener(editorActionListener);

        comboBox.configureEditor(comboBox.getEditor(),comboBox.getSelectedItem());
    }

    /**
     * This protected method is implementation specific and should be private.
     * Do not call or override.
     * 
     * @see #addEditor
     */
    protected void unconfigureEditor() {
        if (focusListener != null) {
            editor.removeFocusListener(focusListener);
        }

        if ( editorFocusListener != null )  {
            editor.removeFocusListener( editorFocusListener );
        }

	if ( editorActionListener != null) {
	    comboBox.getEditor().removeActionListener(editorActionListener);
	}
    }

    /**
     * This public method is implementation specific and should be private. Do
     * not call or override.
     *
     * @see #createArrowButton
     */
    public void configureArrowButton() {
        if ( arrowButton != null ) {
            arrowButton.setEnabled( comboBox.isEnabled() );
            arrowButton.setRequestFocusEnabled(false);
	    arrowButton.addMouseListener( popup.getMouseListener() );
	    arrowButton.addMouseMotionListener( popup.getMouseMotionListener() );
            arrowButton.resetKeyboardActions();
        }
    }

    /**
     * This public method is implementation specific and should be private. Do
     * not call or override.
     *
     * @see #createArrowButton
     */
    public void unconfigureArrowButton() {
        if ( arrowButton != null ) {
	    arrowButton.removeMouseListener( popup.getMouseListener() );
	    arrowButton.removeMouseMotionListener( popup.getMouseMotionListener() );
        }
    }

    /**
     * Creates an button which will be used as the control to show or hide
     * the popup portion of the combo box.
     *
     * @return a button which represents the popup control
     */
    protected JButton createArrowButton() {
        return new BasicArrowButton(BasicArrowButton.SOUTH,
				    UIManager.getColor("ComboBox.buttonBackground"),
				    UIManager.getColor("ComboBox.buttonShadow"),
				    UIManager.getColor("ComboBox.buttonDarkShadow"),
				    UIManager.getColor("ComboBox.buttonHighlight"));
    }

    //
    // end Sub-Component Management
    //===============================


    //================================
    // begin ComboBoxUI Implementation
    //

    /**
     * Tells if the popup is visible or not.
     */
    public boolean isPopupVisible( JComboBox c ) {
        return popup.isVisible();
    }

    /**
     * Hides the popup.
     */
    public void setPopupVisible( JComboBox c, boolean v ) {
        if ( v ) {
            popup.show();
        } else {
            popup.hide();
        }
    }

    /**
     * Determines if the JComboBox is focus traversable.  If the JComboBox is editable
     * this returns false, otherwise it returns true.
     */
    public boolean isFocusTraversable( JComboBox c ) {
        return !comboBox.isEditable();
    }

    //
    // end ComboBoxUI Implementation
    //==============================


    //=================================
    // begin ComponentUI Implementation

    public void paint( Graphics g, JComponent c ) {
        hasFocus = comboBox.hasFocus();
        if ( !comboBox.isEditable() ) {
            Rectangle r = rectangleForCurrentValue();
            paintCurrentValueBackground(g,r,hasFocus);
            paintCurrentValue(g,r,hasFocus);
        }
    }

    public Dimension getPreferredSize( JComponent c ) {
	return getMinimumSize(c);
    }

    /** 
     * The minumum size is the size of the display area plus insets plus the button.
     */
    public Dimension getMinimumSize( JComponent c ) {
        if ( !isMinimumSizeDirty ) {
            return new Dimension(cachedMinimumSize);
        }
        Dimension size = getDisplaySize();
        Insets insets = getInsets();
        size.height += insets.top + insets.bottom;
        int buttonSize = size.height - (insets.top + insets.bottom);
        size.width +=  insets.left + insets.right + buttonSize;

        cachedMinimumSize.setSize( size.width, size.height ); 
        isMinimumSizeDirty = false;

        return new Dimension(size);
    }

    public Dimension getMaximumSize( JComponent c ) {
	return new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);
    }

    // This is currently hacky...
    public int getAccessibleChildrenCount(JComponent c) {
        if ( comboBox.isEditable() ) {
            return 2;
        }
        else {
            return 1;
        }
    }

    // This is currently hacky...
    public Accessible getAccessibleChild(JComponent c, int i) {
        // 0 = the popup
        // 1 = the editor
        switch ( i ) {
        case 0:
            if ( popup instanceof Accessible ) {
                AccessibleContext ac = ((Accessible) popup).getAccessibleContext();
                ac.setAccessibleParent(comboBox);
                return(Accessible) popup;
            }
            break;
        case 1:
            if ( comboBox.isEditable() 
                 && (editor instanceof Accessible) ) {
                AccessibleContext ac = ((Accessible) editor).getAccessibleContext();
                ac.setAccessibleParent(comboBox);
                return(Accessible) editor;
            }
            break;
        }
        return null;
    }

    //
    // end ComponentUI Implementation
    //===============================


    //======================
    // begin Utility Methods
    //

    /**
     * Returns whether or not the supplied keyCode maps to a key that is used for
     * navigation.  This is used for optimizing key input by only passing non-
     * navigation keys to the type-ahead mechanism.  Subclasses should override this
     * if they change the navigation keys.
     */
    protected boolean isNavigationKey( int keyCode ) {
        return keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN ||
        // This is horrible, but necessary since these aren't 
        // supported until JDK 1.2
        keyCode == KeyStroke.getKeyStroke("KP_UP").getKeyCode() || 
        keyCode == KeyStroke.getKeyStroke("KP_DOWN").getKeyCode();
    }  

    /**
     * Selects the next item in the list.  It won't change the selection if the
     * currently selected item is already the last item.
     */
    protected void selectNextPossibleValue() {
        int si;

        if ( isTableCellEditor ) {
            si = listBox.getSelectedIndex();
        }
        else {
            si = comboBox.getSelectedIndex();
        }

        if ( si < comboBox.getModel().getSize() - 1 ) {
            if ( isTableCellEditor ) {
                listBox.setSelectedIndex( si + 1 );
                listBox.ensureIndexIsVisible( si + 1 );
            }
            else {
                comboBox.setSelectedIndex(si+1);
            }
            comboBox.repaint();
        }
    }

    /**
     * Selects the previous item in the list.  It won't change the selection if the
     * currently selected item is already the first item.
     */
    protected void selectPreviousPossibleValue() {
        int si;

        if ( isTableCellEditor ) {
            si = listBox.getSelectedIndex();
        }
        else {
            si = comboBox.getSelectedIndex();
        }

        if ( si > 0 ) {
            if ( isTableCellEditor ) {
                listBox.setSelectedIndex( si - 1 );
                listBox.ensureIndexIsVisible( si - 1 );
            }
            else {
                comboBox.setSelectedIndex(si-1);
            }

            comboBox.repaint();
        }
    }

    /**
     * Hides the popup if it is showing and shows the popup if it is hidden.
     */
    protected void toggleOpenClose() {
        setPopupVisible(comboBox, !isPopupVisible(comboBox));
    }

    /**
     * Returns the area that is reserved for drawing the currently selected item.
     */
    protected Rectangle rectangleForCurrentValue() {
        int width = comboBox.getWidth();
        int height = comboBox.getHeight();
        Insets insets = getInsets();
        int buttonSize = height - (insets.top + insets.bottom);
	if ( arrowButton != null ) {
            buttonSize = arrowButton.getWidth();
	}
	if(BasicGraphicsUtils.isLeftToRight(comboBox)) {
	    return new Rectangle(insets.left, insets.top,
			     width - (insets.left + insets.right + buttonSize),
                             height - (insets.top + insets.bottom));
	}
	else {
	    return new Rectangle(insets.left + buttonSize, insets.top,
			     width - (insets.left + insets.right + buttonSize),
                             height - (insets.top + insets.bottom));
	}
    }

    /**
     * Gets the insets from the JComboBox.
     */
    protected Insets getInsets() {
        return comboBox.getInsets();
    }

    //
    // end Utility Methods
    //====================


    //===============================
    // begin Painting Utility Methods
    //

    /**
     * Paints the currently selected item.
     */
    public void paintCurrentValue(Graphics g,Rectangle bounds,boolean hasFocus) {
        ListCellRenderer renderer = comboBox.getRenderer();
        Component c;

        if ( hasFocus && !isPopupVisible(comboBox) ) {
            c = renderer.getListCellRendererComponent( listBox,
                                                       comboBox.getSelectedItem(),
                                                       -1,
                                                       true,
                                                       false );
        }
        else {
            c = renderer.getListCellRendererComponent( listBox,
                                                       comboBox.getSelectedItem(),
                                                       -1,
                                                       false,
                                                       false );
            c.setBackground(UIManager.getColor("ComboBox.background"));
        }
        c.setFont(comboBox.getFont());
        if ( hasFocus && !isPopupVisible(comboBox) ) {
            c.setForeground(listBox.getSelectionForeground());
            c.setBackground(listBox.getSelectionBackground());
        }
        else {
            if ( comboBox.isEnabled() ) {
                c.setForeground(comboBox.getForeground());
                c.setBackground(comboBox.getBackground());
            }
            else {
                c.setForeground(UIManager.getColor("ComboBox.disabledForeground"));
                c.setBackground(UIManager.getColor("ComboBox.disabledBackground"));
            }
        }

        // Fix for 4238829: should lay out the JPanel.
        boolean shouldValidate = false;
        if (c instanceof JPanel)  {
            shouldValidate = true;
        }

        currentValuePane.paintComponent(g,c,comboBox,bounds.x,bounds.y,
                                        bounds.width,bounds.height, shouldValidate);
    }

    /**
     * Paints the background of the currently selected item.
     */
    public void paintCurrentValueBackground(Graphics g,Rectangle bounds,boolean hasFocus) {
        Color t = g.getColor();
        if ( comboBox.isEnabled() )
            g.setColor(UIManager.getColor("ComboBox.background"));
        else
            g.setColor(UIManager.getColor("ComboBox.disabledBackground"));
        g.fillRect(bounds.x,bounds.y,bounds.width,bounds.height);
        g.setColor(t);
    }

    /**
     * Repaint the currently selected item.
     */
    void repaintCurrentValue() {
        Rectangle r = rectangleForCurrentValue();
        comboBox.repaint(r.x,r.y,r.width,r.height);
    }

    //
    // end Painting Utility Methods
    //=============================


    //===============================
    // begin Size Utility Methods
    //

    /** 
     * Return the default size of an empty display area of the combo box using
     * the current renderer and font.
     *
     * @return the size of an empty display area
     * @see #getDisplaySize
     */
    protected Dimension getDefaultSize() {
	// Calculates the height and width using the default text renderer
	Dimension d = getSizeForComponent(getDefaultListCellRenderer().getListCellRendererComponent(listBox, " ", -1, false, false));

        return new Dimension(d.width, d.height);
    }

    /** 
     * Returns the calculated size of the display area. The display area is the
     * portion of the combo box in which the selected item is displayed. This 
     * method will use the prototype display value if it has been set. 
     * <p>
     * For combo boxes with a non trivial number of items, it is recommended to
     * use a prototype display value to significantly speed up the display 
     * size calculation.
     * 
     * @return the size of the display area calculated from the combo box items
     * @see javax.swing.JComboBox#setPrototypeDisplayValue
     */
    protected Dimension getDisplaySize() {
        if (!isDisplaySizeDirty)  {
            return new Dimension(cachedDisplaySize);
        }
	Dimension result = new Dimension();
        
        ListCellRenderer renderer = comboBox.getRenderer();
        if (renderer == null)  {
            renderer = new DefaultListCellRenderer();
        }

        Object prototypeValue = comboBox.getPrototypeDisplayValue();
        if (prototypeValue != null)  {
            // Calculates the dimension based on the prototype value
            result = getSizeForComponent(renderer.getListCellRendererComponent(listBox, 
									       prototypeValue,
									       -1, false, false));
        } else {
            // Calculate the dimension by iterating over all the elements in the combo
            // box list.
            ComboBoxModel model = comboBox.getModel();
            int modelSize = model.getSize();
	    Dimension d;

            Component cpn;
            
            if (modelSize > 0 ) {
                for (int i = 0; i < modelSize ; i++ ) {
                    // Calculates the maximum height and width based on the largest
                    // element
                    d = getSizeForComponent(renderer.getListCellRendererComponent(listBox, 
										  model.getElementAt(i),
										  -1, false, false));
                    result.width = Math.max(result.width,d.width);
                    result.height = Math.max(result.height,d.height);

                }
            } else {
		result = getDefaultSize();
		if (comboBox.isEditable()) {
		    result.width = 100;
		}
            }
        }

	if ( comboBox.isEditable() ) {
	    Dimension d = editor.getPreferredSize();
	    result.width = Math.max(result.width,d.width);
	    result.height = Math.max(result.height,d.height);
	}
        
        // Set the cached value
        cachedDisplaySize.setSize(result.width, result.height);
        isDisplaySizeDirty = false;

        return result;
    }

    /**
     * This has been refactored out in hopes that it may be investigated and
     * simplified for the next major release. adding/removing
     * the component to the currentValuePane and changing the font may be 
     * redundant operations.
     */
    private Dimension getSizeForComponent(Component comp) {
	currentValuePane.add(comp);
	comp.setFont(comboBox.getFont());
	Dimension d = comp.getPreferredSize();
	currentValuePane.remove(comp);
	return d;
    }
	

    //
    // end Size Utility Methods
    //=============================


    //=================================
    // begin Keyboard Action Management
    //

    /**
     * Adds keyboard actions to the JComboBox.  Actions on enter and esc are already
     * supplied.  Add more actions as you need them.
     */
    protected void installKeyboardActions() {
	InputMap km = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	SwingUtilities.replaceUIInputMap(comboBox, JComponent.
			     WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, km);


	ActionMap am = getActionMap();
	if (am != null) {
	    SwingUtilities.replaceUIActionMap(comboBox, am);
	}
    }

    InputMap getInputMap(int condition) {
	if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
	    return (InputMap)UIManager.get("ComboBox.ancestorInputMap");
	}
	return null;
    }

    ActionMap getActionMap() {
	return createActionMap();
    }

    static Action homeAction = new NavigationalAction(KeyEvent.VK_HOME);
    static Action endAction = new NavigationalAction(KeyEvent.VK_END);
    static Action pgUpAction = new NavigationalAction(KeyEvent.VK_PAGE_UP);
    static Action pgDownAction = new NavigationalAction(KeyEvent.VK_PAGE_DOWN);

    ActionMap createActionMap() {
	ActionMap map = new ActionMapUIResource();

	map.put("hidePopup", new HidePopupAction());
	map.put("pageDownPassThrough", pgDownAction);
	map.put("pageUpPassThrough", pgUpAction);
	map.put("homePassThrough", homeAction);
	map.put("endPassThrough", endAction);
	map.put("selectNext", new DownAction());
	map.put("togglePopup", new AltAction());
	map.put("spacePopup", new SpaceAction());
	map.put("selectPrevious", new UpAction());
	map.put("enterPressed", new EnterAction());

	return map;
    }

    boolean isTableCellEditor() {
	return isTableCellEditor;
    }

    /**
     * Removes the focus InputMap and ActionMap.
     */
    protected void uninstallKeyboardActions() {
	SwingUtilities.replaceUIInputMap(comboBox, JComponent.
				 WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
	SwingUtilities.replaceUIActionMap(comboBox, null);
    }


    //
    // Actions
    // 


    class HidePopupAction extends AbstractAction {
	public void actionPerformed( ActionEvent e ) {
	    JComboBox comboBox = (JComboBox)e.getSource();
	    if ( comboBox.isEnabled() ) {
		comboBox.firePopupMenuCanceled();
		comboBox.setPopupVisible(false);
	    }
	}

	public boolean isEnabled() {
	    return comboBox.isPopupVisible();
	}
    }


    static class NavigationalAction extends AbstractAction {
        int keyCode;

        NavigationalAction(int keyCode) {
	    this.keyCode = keyCode;
        }

	public void actionPerformed(ActionEvent ev) {
	    JComboBox comboBox = (JComboBox)ev.getSource();
	    int index = getNextIndex(comboBox);
            if (index >= 0 && index < comboBox.getItemCount()) {
                comboBox.setSelectedIndex(index);
            }
        }

        int getNextIndex(JComboBox comboBox) {
            switch (keyCode) {
            case KeyEvent.VK_PAGE_UP:
                int listHeight = comboBox.getMaximumRowCount();
                int index = comboBox.getSelectedIndex() - listHeight;
                return (index < 0 ? 0: index);
            case KeyEvent.VK_PAGE_DOWN:
                listHeight = comboBox.getMaximumRowCount();
                index = comboBox.getSelectedIndex() + listHeight;
                int max = comboBox.getItemCount();
                return (index < max ? index: max-1);
            case KeyEvent.VK_HOME:
                return 0;
            case KeyEvent.VK_END:
                return comboBox.getItemCount() - 1;
            default:
                return comboBox.getSelectedIndex();
            }
        }
    }

    static class DownAction extends AbstractAction {
	public void actionPerformed(ActionEvent e) {
	    JComboBox comboBox = (JComboBox)e.getSource();
	    if ( comboBox.isEnabled() && comboBox.isShowing() ) {
		if ( comboBox.isPopupVisible() ) {
		    BasicComboBoxUI ui = (BasicComboBoxUI)comboBox.getUI();
		    ui.selectNextPossibleValue();
		} else {
		    comboBox.setPopupVisible(true);
		}
	    }
	}
    }

    static class EnterAction extends AbstractAction {
	public void actionPerformed(ActionEvent e) {
	    JComboBox comboBox = (JComboBox)e.getSource();
	    if ( !comboBox.isEnabled() ) {
		return;
	    }
	    BasicComboBoxUI ui = (BasicComboBoxUI)comboBox.getUI();
	    if ( ui.isTableCellEditor() ) {
		// Forces the selection of the list item if the
		// combo box is in a JTable.
		comboBox.setSelectedIndex(ui.popup.getList().getSelectedIndex());
	    } 
	    else {
		if (comboBox.isPopupVisible()) {
		    comboBox.setPopupVisible(false);
		} else {
		    // Call the default button binding.
		    // This is a pretty messy way of passing an event through
		    // to the root pane.
		    JRootPane root = SwingUtilities.getRootPane(comboBox);
		    if (root != null) {
			InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			ActionMap am = root.getActionMap();
			if (im != null && am != null) {
			    Object obj = im.get(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0));
			    if (obj != null) {
				Action action = am.get(obj);
				if (action != null) {
				    action.actionPerformed(e);
				}
			    }
			}
		    }
		}
	    }
	}
    }

    static class AltAction extends AbstractAction {
	public void actionPerformed(ActionEvent e) {
	    JComboBox comboBox = (JComboBox)e.getSource();
	    if ( comboBox.isEnabled() ) {
		BasicComboBoxUI ui = (BasicComboBoxUI)comboBox.getUI();
		if ( ui.isTableCellEditor() ) {
		    // Forces the selection of the list item if the
		    // combo box is in a JTable.
		    comboBox.setSelectedIndex(ui.popup.getList().getSelectedIndex());
		} 
		else {
		    comboBox.setPopupVisible(!comboBox.isPopupVisible());
		}
            }
	}
    }

    // Same as the AltAction except that it doesn't invoke if
    // the space key is pressed in the editable text portion.
    static class SpaceAction extends AltAction {
	public void actionPerformed(ActionEvent e) {
	    JComboBox comboBox = (JComboBox)e.getSource();
	    if ( !comboBox.isEditable() ) {
		super.actionPerformed(e);
            }
	}
    }    

    static class UpAction extends AbstractAction {
	public void actionPerformed(ActionEvent e) {
	    JComboBox comboBox = (JComboBox)e.getSource();
	    if ( comboBox.isEnabled() ) {
		BasicComboBoxUI ui = (BasicComboBoxUI)comboBox.getUI();
		if (ui.isPopupVisible(comboBox)) {
                    ui.selectPreviousPossibleValue();
                }
            }
        }
    }

    //
    // end Keyboard Action Management
    //===============================


    class EditorFocusListener extends FocusAdapter {

	/**
	 * This will make the comboBox fire an ActionEvent if the editor
	 * value is different from the selected item in the model.  
	 * This allows for the entering of data in the combo box editor and
	 * sends notification when tabbing or clicking out of focus.
	 */ 
        public void focusLost( FocusEvent e ) {
	    ComboBoxEditor editor = comboBox.getEditor();
            Object item = editor.getItem();

            if (!e.isTemporary() && item != null && 
		!item.equals( comboBox.getSelectedItem())) {
                comboBox.actionPerformed
                    (new ActionEvent(editor, 0, "",
                                     EventQueue.getMostRecentEventTime(), 0));
            }
        }
    }

    class EditorActionListener implements ActionListener {

	// Fix for 4515752: Forward the Enter pressed on the
	// editable combo box to the default button if the item has
	// not changed.

	// Note: This could depend on event ordering. The first ActionEvent
	// from the editor may be handled by the JComboBox in which case, the
	// enterPressed action will always be invoked.
	public void actionPerformed(ActionEvent evt) {
	    Object item = comboBox.getEditor().getItem();

	    if (item != null && item.equals(comboBox.getSelectedItem())) {
		ActionMap am = comboBox.getActionMap();
		if (am != null) {
		    Action action = am.get("enterPressed");
		    if (action != null) {
			action.actionPerformed(new ActionEvent(comboBox, evt.getID(), 
							       evt.getActionCommand(),
							       evt.getModifiers()));
		    }
		}
	    }
	}
    }
}
