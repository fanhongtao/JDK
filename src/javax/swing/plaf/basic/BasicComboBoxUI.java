/**
 * @(#)BasicComboBoxUI.java	1.89 98/09/20
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

/**
 * Basic UI for JComboBox.  This class adds and removes components from the
 * JComboBox.  The arrow button and the editor are managed by this object.
 * The popup menu is handled by BasicComboPopup.  BasicComboPopup supplies
 * this class with a MouseListener, MouseMotionListener, and a KeyListener.
 * These listeners are added to the arrow button and the JComboBox by default.
 * Subclasses of BasicComboBoxUI should attach the listeners to whichever
 * components they like.
 *
 * installListeners() is where listeners get added to the JComboBox (and model).
 * configureEditor() is where listeners get added to the editor.
 * configureArrowButton() is where listeners get added to the arrow button.
 * 
 * Inner classes for handling events:
 *    FocusHandler
 *    ItemHandler
 *    ListDataHandler
 *    PropertyChangeHandler
 *    KeyHandler
 *
 *
 * @version 1.89 09/20/98
 * @author Tom Santos
 */
public class BasicComboBoxUI extends ComboBoxUI {
    protected JComboBox comboBox;
    protected boolean   hasFocus = false;
    private boolean lightNav = false;

    private static final String LIGHTWEIGHT_KEYBOARD_NAVIGATION = "JComboBox.lightweightKeyboardNavigation";
    private static final String LIGHTWEIGHT_KEYBOARD_NAVIGATION_ON = "Lightweight";
    private static final String LIGHTWEIGHT_KEYBOARD_NAVIGATION_OFF = "Heavyweight";

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
    protected KeyListener keyListener;
    protected FocusListener focusListener;
    protected ItemListener itemListener;
    protected PropertyChangeListener propertyChangeListener;

    // Listeners that the ComboPopup produces.
    // These get attached to any component that wishes to invoke the popup.
    protected MouseListener popupMouseListener;
    protected MouseMotionListener popupMouseMotionListener;
    protected KeyListener popupKeyListener;

    // This is used for knowing when to cache the minimum preferred size.
    // If the data in the list changes, the cached value get marked for recalc.
    protected ListDataListener listDataListener;

    // Flag for recalculating the minimum preferred size.
    protected boolean isMinimumSizeDirty = true;

    // Cached minimum preferred size.
    protected Dimension cachedMinimumSize = new Dimension( 0, 0 );

    // Cached the size that the display needs to render the largest item
    Dimension cachedDisplaySize = new Dimension( 0, 0 );


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

	Object keyNav = c.getClientProperty( LIGHTWEIGHT_KEYBOARD_NAVIGATION );
	if ( keyNav != null ) {
	    if ( keyNav.equals( LIGHTWEIGHT_KEYBOARD_NAVIGATION_ON ) ) {
	        lightNav = true;
	    }
	    else if ( keyNav.equals( LIGHTWEIGHT_KEYBOARD_NAVIGATION_OFF ) ) {
	        lightNav = false;
	    }
	}

        if ( comboBox.getRenderer() == null || comboBox.getRenderer() instanceof UIResource ) {
	    comboBox.setRenderer( createRenderer() );
        }
	
        if ( comboBox.getEditor() == null || comboBox.getEditor() instanceof UIResource ) {
	    comboBox.setEditor( createEditor() );
        }

        installComponents();
        installListeners();


	if ( arrowButton != null ) {
	    configureArrowButton();
	}
	
	if ( editor != null ) {
	    configureEditor();
	}

        comboBox.setLayout( createLayoutManager() );

        comboBox.setRequestFocusEnabled( true );

        // An invokeLater() was used here because updateComponentTree() resets
        // our sub-components after this method is completed.  By delaying, we
        // can set what we need after updateComponentTree() has set all of the
        // values to defaults.
        Runnable initializer = new Runnable() {
            public void run(){
                // This test for comboBox being null is required because it's possible for the UI
                // to become uninstalled before this block of code is executed.
                if ( comboBox != null ) {
                    if ( editor != null ) {
                        editor.setFont( comboBox.getFont() );
                    }
                    installKeyboardActions();
                }
            }
        };
        SwingUtilities.invokeLater( initializer );
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
        popupKeyListener = null;
        popupMouseListener = null;
        popupMouseMotionListener = null;
	propertyChangeListener = null;
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
     * Attaches listeners to the JComboBox and JComboBoxModel.
     */
    protected void installListeners() {
        if ( (itemListener = createItemListener()) != null ) {
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
	if ( (popupMouseListener = popup.getMouseListener()) != null ) {
	    comboBox.addMouseListener( popupMouseListener );
	}
	if ( (popupMouseMotionListener = popup.getMouseMotionListener()) != null ) {
	    comboBox.addMouseMotionListener( popupMouseMotionListener );
	}
	if ( (popupKeyListener = popup.getKeyListener()) != null ) {
	    comboBox.addKeyListener( popupKeyListener );
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
     * Removes listeners from the JComboBox and JComboBoxModel.
     */
    protected void uninstallListeners() {
        if ( keyListener != null ) {
	    comboBox.removeKeyListener( keyListener );
	}
        if ( itemListener != null ) {
	    comboBox.removeItemListener( itemListener );
	}
        if ( propertyChangeListener != null ) {
	    comboBox.removePropertyChangeListener( propertyChangeListener );
	}
	if ( focusListener != null ) {
	    comboBox.removeFocusListener( focusListener );
	}
	if ( popupMouseListener != null ) {
	    comboBox.removeMouseListener( popupMouseListener );
	}
	if ( popupMouseMotionListener != null ) {
            comboBox.removeMouseMotionListener( popupMouseMotionListener );
	}
	if ( popupKeyListener != null ) {
	    comboBox.removeKeyListener( popupKeyListener );
	}
        if ( comboBox.getModel() != null ) {
	    if ( listDataListener != null ) {
	        comboBox.getModel().removeListDataListener( listDataListener );
	    }
        }
    }

    /**
     * Creates an implementation of the ComboPopup interface.
     * Returns an instance of BasicComboPopup.
     */
    protected ComboPopup createPopup() {
        BasicComboPopup popup = new BasicComboPopup( comboBox );
        popup.getAccessibleContext().setAccessibleParent(comboBox);
        return popup;
    }

    /**
     * Creates the key listener for handling type-ahead.
     * Returns an instance of BasicComboBoxUI$KeyHandler.
     */
    protected KeyListener createKeyListener() {
        return new KeyHandler();
    } 

    /**
     * Creates the focus listener that hides the popup when the focus is lost.
     * Returns an instance of BasicComboBoxUI$FocusHandler.
     */
    protected FocusListener createFocusListener() {
        return new FocusHandler();
    }

    /**
     * Creates the list data listener that is used for caching the preferred sizes.
     * Returns an instance of BasicComboBoxUI$ListDataHandler.
     */
    protected ListDataListener createListDataListener() {
        return new ListDataHandler();
    }

    /**
     * Creates the item listener that watches for updates in the current selection
     * so that it can update the display.
     * Returns an instance of BasicComboBoxUI$ItemHandler.
     */
    protected ItemListener createItemListener() {
        return new ItemHandler();
    }

    /**
     * Creates the list data listener that is used for caching the preferred sizes.
     * Returns an instance of BasicComboBoxUI$PropertyChangeHandler.
     */
    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyChangeHandler();
    }

    /**
     * Creates the standard combo box layout manager that has the arrow button to
     * the right and the editor to the left.
     * Returns an instance of BasicComboBoxUI$ComboBoxLayoutManager.
     */
    protected LayoutManager createLayoutManager() {
        return new ComboBoxLayoutManager();
    }

    /**
     * Creates the renderer that is to be used in the combo box.  This method only gets called if
     * a custom renderer has nto already been installed in the JComboBox.
     */
    protected ListCellRenderer createRenderer() {
        return new BasicComboBoxRenderer.UIResource();
    }

    /**
     * Creates the editor that is to be used in editable combo boxes.  This method only gets called if
     * a custom editor has not already been installed in the JComboBox.
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
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <FooUI>.
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
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <FooUI>.
     */
    public class FocusHandler implements FocusListener {
        public void focusGained( FocusEvent e ) {
            hasFocus = true;
            comboBox.repaint();
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
        }
    }

    /**
     * This listener watches for changes in the data and revalidates.
     *
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <FooUI>.
     */
    public class ListDataHandler implements ListDataListener {
        public void contentsChanged( ListDataEvent e ) {
	    if ( !(e.getIndex0() == -1 && e.getIndex1() == -1) ) {
                isMinimumSizeDirty = true;
		comboBox.revalidate();
	    }

            if ( editor != null ) {
                comboBox.configureEditor( comboBox.getEditor(), comboBox.getSelectedItem() );
            }

	    comboBox.repaint();
        }

        public void intervalAdded( ListDataEvent e ) {
	    int index0 = e.getIndex0();
	    int index1 = e.getIndex1();

	    if ( index0 == 0 && comboBox.getItemCount() - ((index1 - index0) + 1) == 0 ) {
	        contentsChanged( e );
	    }
	    else if ( !(index0 == -1 && index1 == -1) ) {
	        ListCellRenderer renderer = comboBox.getRenderer();
		ComboBoxModel model = comboBox.getModel();
		Component c;
		Dimension size;
		int widestWidth = cachedDisplaySize.width;
		int tallestHeight = cachedDisplaySize.height;

	        for ( int i = index0; i <= index1; ++i ) {
		    c = renderer.getListCellRendererComponent( listBox, model.getElementAt( i ), -1, false, false );
		    currentValuePane.add( c );
		    c.setFont( comboBox.getFont() );
		    size = c.getPreferredSize();
		    widestWidth = Math.max( widestWidth, size.width );
		    tallestHeight = Math.max( tallestHeight, size.height );
		    currentValuePane.remove( c );
		}

		if ( cachedDisplaySize.width < widestWidth || cachedDisplaySize.height < tallestHeight ) {
		    if ( cachedDisplaySize.width < widestWidth ) {
		        cachedMinimumSize.width += widestWidth - cachedDisplaySize.width;
		    }
		    
		    if ( cachedDisplaySize.height < tallestHeight ) {
		        cachedMinimumSize.height += tallestHeight - cachedDisplaySize.height;
		    }
		    
		    cachedDisplaySize.setSize( tallestHeight, widestWidth );
		    
		    comboBox.revalidate();

		    if ( editor != null ) {
		        comboBox.configureEditor( comboBox.getEditor(), comboBox.getSelectedItem() ); 
		    }
		}
	    }
        }

        public void intervalRemoved( ListDataEvent e ) {
	    contentsChanged( e );
        }
    }

    /**
     * This listener watches for changes to the selection in the combo box and
     * updates the display of the currently selected item.
     *
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <FooUI>.
     */
    public class ItemHandler implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            ComboBoxModel model = comboBox.getModel();
            Object v = model.getSelectedItem();
            if ( editor != null ) {
                comboBox.configureEditor(comboBox.getEditor(),v);
            }
            comboBox.repaint();
        }
    }

    /**
     * This listener watches for bound properties that have changed in the JComboBox.
     * It looks for the model and editor being swapped-out and updates appropriately.
     * It also looks for changes in the editable, enabled, and maximumRowCount properties.
     *
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <FooUI>.
     */
    public class PropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();

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
		comboBox.revalidate();
		comboBox.repaint();
	    }
            else if ( propertyName.equals( "editor" ) && comboBox.isEditable() ) {
                removeEditor();
                addEditor();
            }
            else if ( propertyName.equals( "editable" ) ) {
                if ( comboBox.isEditable() ) {
                    comboBox.setRequestFocusEnabled( false );
		    if ( popupKeyListener != null ) {
		        comboBox.removeKeyListener( popupKeyListener );
			    }
                    addEditor();
		    if ( editor != null ) {
		        configureEditor(); 
		    }
                }
                else {
                    comboBox.setRequestFocusEnabled( true );
		    if ( popupKeyListener != null ) {
		        comboBox.addKeyListener( popupKeyListener );
		    }
                    removeEditor();
                }
		
		// Double revalidation seems to be necessary here.  Switching to editable can cause
		// strange layout problems otherwise.
		comboBox.revalidate();
		comboBox.validate();
		comboBox.revalidate();
		comboBox.repaint();
            }
            else if ( propertyName.equals( "enabled" ) ) {
                boolean cbIsEnabled = comboBox.isEnabled();
                if ( cbIsEnabled ) {
                    if ( editor != null )
                        editor.setEnabled(true);
                    if ( arrowButton != null )
                        arrowButton.setEnabled(true);
                }
                else {
                    if ( editor != null )
                        editor.setEnabled(false);
                    if ( arrowButton != null )
                        arrowButton.setEnabled(false);
                }
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
	    }
	    else if ( propertyName.equals( LIGHTWEIGHT_KEYBOARD_NAVIGATION ) ) {
	        Object newValue = e.getNewValue();
		if ( newValue.equals( LIGHTWEIGHT_KEYBOARD_NAVIGATION_ON ) ) {
		    lightNav = true;
		}
		else if ( newValue.equals( LIGHTWEIGHT_KEYBOARD_NAVIGATION_OFF ) ) {
		    lightNav = false;
		}
	    }
        }     
    }

    /**
     * This layout manager handles the 'standard' layout of combo boxes.  It puts
     * the arrow button to the right and the editor to the left.  If there is no
     * editor it still keeps the arrow button to the right.
     *
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <FooUI>.
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
                arrowButton.setBounds( width - (insets.right + buttonSize),
                                       insets.top,
                                       buttonSize, buttonSize);
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
     * The editor and arrow button are added to the JComboBox here.
     */
    protected void installComponents() {
        arrowButton = createArrowButton();
        comboBox.add( arrowButton );

        if ( comboBox.isEditable() ) {
            addEditor();
        }

        comboBox.add( currentValuePane );
    }

    /**
     * The editor and/or arrow button are removed from the JComboBox here.
     * This method calls removeAll() on the JComboBox just to make sure that
     * everything gets removed.
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
     * Adds the editor to the JComboBox.
     */
    public void addEditor() {
        removeEditor();
        editor = comboBox.getEditor().getEditorComponent();
        comboBox.add(editor);
    }

    /**
     * Removes the editor from the JComboBox.  It also calls unconfigureEditor()
     */
    public void removeEditor() {
        if ( editor != null ) {
            unconfigureEditor();
            comboBox.remove( editor );
        }
    }

    /**
     * Configures the editor by setting its font and adding listeners.
     */
    protected void configureEditor() {
        editor.setFont( comboBox.getFont() );
	if ( popupKeyListener != null ) {
	    editor.addKeyListener( popupKeyListener );
	}

        if ( editor instanceof Accessible ) {
            AccessibleContext ac = ((Accessible) editor).getAccessibleContext();
            if ( ac != null ) {
                ac.setAccessibleParent(comboBox);
            }
        }

        comboBox.configureEditor(comboBox.getEditor(),comboBox.getSelectedItem());
    }

    /**
     * Unconfigures the editor by removing listeners.
     */
    protected void unconfigureEditor() {
        if ( popupKeyListener != null ) {
	    editor.removeKeyListener( popupKeyListener );
	}
    }

    /**
     * Configures the arrow button by adding listeners.
     */
    public void configureArrowButton() {
        if ( arrowButton != null ) {
	    arrowButton.setEnabled( comboBox.isEnabled() );
            arrowButton.setRequestFocusEnabled(false);
	    if ( popupMouseListener != null ) {
	        arrowButton.addMouseListener( popupMouseListener );
	    }
	    if ( popupMouseMotionListener != null ) {
	        arrowButton.addMouseMotionListener( popupMouseMotionListener );
	    }
            arrowButton.resetKeyboardActions();
        }
    }

    /**
     * Unconfigures the arrow button by removing listeners.
     */
    public void unconfigureArrowButton() {
        if ( arrowButton != null ) {
	    if ( popupMouseListener != null ) {
	        arrowButton.removeMouseListener( popupMouseListener );
	    }
	    if ( popupMouseMotionListener != null ) {
	        arrowButton.removeMouseMotionListener( popupMouseMotionListener );
	    }
        }
    }

    /**
     * Creates the arrow button.  Subclasses can create any button they like.
     * The default behavior of this class is to attach various listeners to the
     * button returned by this method.
     * Returns an instance of BasicArrowButton.
     */
    protected JButton createArrowButton() {
        return new BasicArrowButton(BasicArrowButton.SOUTH);
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
        }
        else {
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
        Dimension size = getMinimumSize( c );
        size.width += 4; // Added for a little 'elbow room'.
        return size;
    }

    public Dimension getMinimumSize( JComponent c ) {
        if ( !isMinimumSizeDirty ) {
            return new Dimension( cachedMinimumSize );
        }
        Dimension size;
        Insets insets = getInsets();
        size = getDisplaySize();
        size.height += insets.top + insets.bottom;
        int buttonSize = size.height - (insets.top + insets.bottom);
        size.width +=  insets.left + insets.right + buttonSize;

        cachedMinimumSize.setSize( size.width, size.height ); 
        isMinimumSizeDirty = false;

        return size;
    }

    public Dimension getMaximumSize( JComponent c ) {
        Dimension size = getPreferredSize( c );
        size.width = Short.MAX_VALUE;
        return size;
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
                return (Accessible) popup;
            }
            break;
        case 1:
            if ( comboBox.isEditable() 
                 && (editor instanceof Accessible) ) {
                AccessibleContext ac = ((Accessible) editor).getAccessibleContext();
                ac.setAccessibleParent(comboBox);
                return (Accessible) editor;
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
        return keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN;
    }  

    /**
     * Selects the next item in the list.  It won't change the selection if the
     * currently selected item is already the last item.
     */
    protected void selectNextPossibleValue() {
        int si;

	if ( lightNav ) {
	    si = listBox.getSelectedIndex();
	}
	else {
	    si = comboBox.getSelectedIndex();
	}

        if ( si < comboBox.getModel().getSize() - 1 ) {
	    if ( lightNav ) {
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

	if ( lightNav ) {
	    si = listBox.getSelectedIndex();
	}
	else {
	    si = comboBox.getSelectedIndex();
	}

        if ( si > 0 ) {
	    if ( lightNav ) {
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
        return new Rectangle(insets.left, insets.top,
                             width - (insets.left + insets.right + buttonSize),
                             height - (insets.top + insets.bottom));
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

        if ( comboBox.getSelectedIndex() == -1 ) {
           return;
        }
        
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
        currentValuePane.paintComponent(g,c,comboBox,bounds.x,bounds.y,
                                        bounds.width,bounds.height);
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
     * Return the dimension the the combo box should have if by default
     * if there is no current value and no value in the list of possible
     * values.
     */
    protected Dimension getDefaultSize() {
        return new Dimension(100,20);
    }

    protected Dimension getDisplaySize() {
        int i,c;
        Dimension result = new Dimension();
        ListCellRenderer renderer = comboBox.getRenderer();
        ComboBoxModel model = comboBox.getModel();
        Component cpn;
        Dimension d;

        if ( renderer != null && model.getSize() > 0 ) {
            for ( i=0,c=model.getSize();i<c;i++ ) {
                cpn = renderer.getListCellRendererComponent(listBox, model.getElementAt(i),-1, false, false);
                currentValuePane.add(cpn);
                cpn.setFont(comboBox.getFont());
                d = cpn.getPreferredSize();
                currentValuePane.remove(cpn);
                result.width = Math.max(result.width,d.width);
                result.height = Math.max(result.height,d.height);
            }

            if ( comboBox.isEditable() ) {
                d = editor.getPreferredSize();
                result.width = Math.max(result.width,d.width);
                result.height = Math.max(result.height,d.height);
            }

	    cachedDisplaySize.setSize( result.width, result.height );

            return result;
        }
        else
            return getDefaultSize();

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
        AbstractAction escAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e){
	        setPopupVisible(comboBox, false);
            }
            public boolean isEnabled(){
                return comboBox.isEnabled();
            }
        };

        comboBox.registerKeyboardAction( escAction,
					 KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),
					 JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    /**
     * Removes the keyboard actions that were added by installKeyboardActions().
     */
    protected void uninstallKeyboardActions() {
        comboBox.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0));
    }

    //
    // end Keyboard Action Management
    //===============================
}
