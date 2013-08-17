/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.Serializable;


/**
 * This is an implementation of the ComboPopup interface.  It is primarily for use by
 * BasicComboBoxUI and its subclasses.  BasicComboPopup extends JPopupMenu because
 * most combo boxes use a popup menu to display the list of possible selections.
 * BasicComboBoxUI only requires a ComboPopup, so subclasses of BasicComboBoxUI aren't
 * required to use this class.
 *
 * All event handling is handled by createxxxListener() methods and internal classes.
 * You can change the behavior of this class by overriding the createxxxListener()
 * methods and supplying your own event listeners or subclassing from the ones supplied
 * in this class.
 *
 * Inner classes for handling events:
 *    InvocationMouseHandler
 *    InvocationMouseMotionHandler
 *    InvocationKeyHandler
 *    ListSelectionHandler
 *    ListDataHandler
 *    ListMouseHandler
 *    ListMouseMotionHandler
 *    PropertyChangeHandler
 *    ItemHandler
 *
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.35 12/02/02
 * @author Tom Santos
 */
public class BasicComboPopup extends JPopupMenu implements ComboPopup {
    // An empty ListMode, this is used when the UI changes to allow
    // the JList to be gc'ed.
    static final ListModel EmptyListModel = new ListModel() {
	public int getSize() { return 0; }
	public Object getElementAt(int index) { return null; }
	public void addListDataListener(ListDataListener l) {}
	public void removeListDataListener(ListDataListener l) {}
    };

    protected JComboBox                comboBox;
    protected JList                    list;
    protected JScrollPane              scroller;

    // If the value is adjusting, any changes to the list selection won't affect the model.
    protected boolean                  valueIsAdjusting = false;

    // Listeners that are required by the ComboPopup interface
    protected MouseMotionListener      mouseMotionListener;
    protected MouseListener            mouseListener;
    protected KeyListener              keyListener;

    // Listeners that are attached to the list
    protected ListSelectionListener    listSelectionListener;
    protected ListDataListener         listDataListener;
    protected MouseListener            listMouseListener;
    protected MouseMotionListener      listMouseMotionListener;

    // Listeners that are attached to the JComboBox
    protected PropertyChangeListener   propertyChangeListener;
    protected ItemListener             itemListener;

    protected Timer                    autoscrollTimer;
    protected boolean                  hasEntered = false;
    protected boolean                  isAutoScrolling = false;
    protected int                      scrollDirection = SCROLL_UP;

    protected static final int         SCROLL_UP = 0;
    protected static final int         SCROLL_DOWN = 1;

    private boolean lightNav = false;

    private static final String LIGHTWEIGHT_KEYBOARD_NAVIGATION = "JComboBox.lightweightKeyboardNavigation";
    private static final String LIGHTWEIGHT_KEYBOARD_NAVIGATION_ON = "Lightweight";
    private static final String LIGHTWEIGHT_KEYBOARD_NAVIGATION_OFF = "Heavyweight";

    //========================================
    // begin ComboPopup method implementations
    //

    /**
     * Implementation of ComboPopup.show().
     */
    public void show() {
        Dimension popupSize = comboBox.getSize();
        popupSize.setSize( popupSize.width, getPopupHeightForRowCount( comboBox.getMaximumRowCount() ) );
        Rectangle popupBounds = computePopupBounds( 0, comboBox.getBounds().height,
                                                    popupSize.width, popupSize.height);
        scroller.setMaximumSize( popupBounds.getSize() );
        scroller.setPreferredSize( popupBounds.getSize() );
        scroller.setMinimumSize( popupBounds.getSize() );
        list.invalidate();
        syncListSelectionWithComboBoxSelection();
        list.ensureIndexIsVisible( list.getSelectedIndex() );

        setLightWeightPopupEnabled( comboBox.isLightWeightPopupEnabled() );

        show( comboBox, popupBounds.x, popupBounds.y );
    }

    /**
     * Implementation of ComboPopup.hide().
     */
    public void hide() {
        MenuSelectionManager manager = MenuSelectionManager.defaultManager();
        MenuElement [] selection = manager.getSelectedPath();
        for ( int i = 0 ; i < selection.length ; i++ ) {
            if ( selection[i] == this ) {
                manager.clearSelectedPath();
                break;
            }
        }
        comboBox.repaint();
    }

    /**
     * Implementation of ComboPopup.getList().
     */
    public JList getList() {
        return list;
    }

    /**
     * Implementation of ComboPopup.getMouseListener().
     */
    public MouseListener getMouseListener() {
        return mouseListener;
    }

    /**
     * Implementation of ComboPopup.getMouseMotionListener().
     */
    public MouseMotionListener getMouseMotionListener() {
        return mouseMotionListener;
    }

    /**
     * Implementation of ComboPopup.getKeyListener().
     */
    public KeyListener getKeyListener() {
        return keyListener;
    }

    /**
     * Called when the UI is uninstalling.  Since this popup isn't in the component
     * tree, it won't get it's uninstallUI() called.  It removes the listeners that
     * were added in addComboBoxListeners().
     */
    public void uninstallingUI() {
        comboBox.removePropertyChangeListener( propertyChangeListener );
        comboBox.removeItemListener( itemListener );
        uninstallComboBoxModelListeners( comboBox.getModel() );
        uninstallKeyboardActions();
	uninstallListListeners();
	// We do this, otherwise the listener the ui installs on
	// the model (the combobox model in this case) will keep a
	// reference to the list, causing the list (and us) to never get gced.
	list.setModel(EmptyListModel);
    }

    protected void uninstallComboBoxModelListeners( ComboBoxModel model ) {
        if ( model != null ) {
            model.removeListDataListener( listDataListener );
        }
    }

    protected void uninstallKeyboardActions() {
        comboBox.unregisterKeyboardAction( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0 ) );
    }

    //
    // end ComboPopup method implementations
    //======================================


    //===================================================================
    // begin Initialization routines
    //
    public BasicComboPopup( JComboBox combo ) {
        super();
        comboBox = combo;


        Object keyNav = combo.getClientProperty( LIGHTWEIGHT_KEYBOARD_NAVIGATION );
        if ( keyNav != null ) {
            if ( keyNav.equals( LIGHTWEIGHT_KEYBOARD_NAVIGATION_ON ) ) {
                lightNav = true;
            }
            else if ( keyNav.equals( LIGHTWEIGHT_KEYBOARD_NAVIGATION_OFF ) ) {
                lightNav = false;
            }
        }

        mouseListener = createMouseListener();
        mouseMotionListener = createMouseMotionListener();
        keyListener = createKeyListener();

        listSelectionListener = createListSelectionListener();
        listDataListener = createListDataListener();
        listMouseListener = createListMouseListener();
        listMouseMotionListener = createListMouseMotionListener();

        propertyChangeListener = createPropertyChangeListener();
        itemListener = createItemListener();

        list = createList();
        configureList();
        scroller = createScroller();
        configureScroller();
        configurePopup();
        installComboBoxListeners();
        installKeyboardActions();
    }

    /**
     * Creates the mouse listener that is returned by ComboPopup.getMouseListener().
     * Returns an instance of BasicComboPopup$InvocationMouseHandler.
     */
    protected MouseListener createMouseListener() {
        return new InvocationMouseHandler();
    }

    /**
     * Creates the mouse motion listener that is returned by
     * ComboPopup.getMouseMotionListener().
     * Returns an instance of BasicComboPopup$InvocationMouseMotionListener.
     */
    protected MouseMotionListener createMouseMotionListener() {
        return new InvocationMouseMotionHandler();
    }

    /**
     * Creates the key listener that is returned by ComboPopup.getKeyListener().
     * Returns an instance of BasicComboPopup$InvocationKeyHandler.
     */
    protected KeyListener createKeyListener() {
        return new InvocationKeyHandler();
    }

    /**
     * Creates a list selection listener that watches for selection changes in
     * the popup's list.
     * Returns an instance of BasicComboPopup$ListSelectionHandler.
     */
    protected ListSelectionListener createListSelectionListener() {
        return new ListSelectionHandler();
    }

    /**
     * Creates a list data listener that watches for inserted and removed items from the
     * combo box model.
     */
    protected ListDataListener createListDataListener() {
        return new ListDataHandler();
    }

    /**
     * Creates a mouse listener that watches for mouse events in
     * the popup's list.
     * Returns an instance of BasicComboPopup$ListMouseHandler.
     */
    protected MouseListener createListMouseListener() {
        return new ListMouseHandler();
    }

    /**
     * Creates a mouse motion listener that watches for mouse events in
     * the popup's list.
     * Returns an instance of BasicComboPopup$ListMouseMotionHandler.
     */
    protected MouseMotionListener createListMouseMotionListener() {
        return new ListMouseMotionHandler();
    }

    /**
     * Creates a property change listener that watches for changes in the bound
     * properties in the JComboBox.
     * Returns an instance of BasicComboPopup$PropertyChangeHandler.
     */
    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyChangeHandler();
    }

    /**
     * Creates an item listener that watches for changes in the selected
     * item in the JComboBox.
     * Returns an instance of BasicComboPopup$ItemHandler.
     */
    protected ItemListener createItemListener() {
        return new ItemHandler();
    }

    /**
     * Creates the JList that is used in the popup to display the items in the model.
     */
    protected JList createList() {
        return new JList( comboBox.getModel() );
    }

    /**
     * Called to configure the list created by createList().
     */
    protected void configureList() {
        list.setFont( comboBox.getFont() );
        list.setForeground( comboBox.getForeground() );
        list.setBackground( comboBox.getBackground() );
        list.setSelectionForeground( UIManager.getColor( "ComboBox.selectionForeground" ) );
        list.setSelectionBackground( UIManager.getColor( "ComboBox.selectionBackground" ) );
        list.setBorder( null );
        list.setCellRenderer( comboBox.getRenderer() );
        list.setRequestFocusEnabled( false );
        syncListSelectionWithComboBoxSelection();
        list.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        installListListeners();
    }

    /**
     * Called by configureList() to add the necessary listeners to the list.
     */
    protected void installListListeners() {
        list.addListSelectionListener( listSelectionListener );
        list.addMouseMotionListener( listMouseMotionListener );
        list.addMouseListener( listMouseListener );
    }


    void uninstallListListeners() {
        list.removeListSelectionListener( listSelectionListener );
        list.removeMouseMotionListener( listMouseMotionListener );
        list.removeMouseListener( listMouseListener );
    }

    /**
     * Creates the JScrollPane that is used in the popup to hold the list.
     */
    protected JScrollPane createScroller() {
        return new JScrollPane( list, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
    }

    /**
     * Called to configure the JScrollPane created by createScroller().
     */
    protected void configureScroller() {
        scroller.setRequestFocusEnabled( false );
        scroller.getVerticalScrollBar().setRequestFocusEnabled( false );
        scroller.setBorder( null );
    }

    /**
     * Called to configure this JPopupMenu (BasicComboPopup is a JPopupMenu).
     */
    protected void configurePopup() {
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        setBorderPainted( true );
        setBorder( BorderFactory.createLineBorder( Color.black ) );
        setOpaque( false );
        add( scroller );
        setDoubleBuffered( true );
        setRequestFocusEnabled( false );
    }

    /**
     * This method adds the necessary listeners to the JComboBox.
     */
    protected void installComboBoxListeners() {
        comboBox.addPropertyChangeListener( propertyChangeListener );
        comboBox.addItemListener( itemListener );
        installComboBoxModelListeners( comboBox.getModel() );
    }

    protected void installComboBoxModelListeners( ComboBoxModel model ) {
        if ( model != null ) {
            model.addListDataListener( listDataListener );
        }
    }

    protected void installKeyboardActions() {
        
        ActionListener action = new ActionListener() {
            public void actionPerformed(ActionEvent e){
            }
        };

        comboBox.registerKeyboardAction( action,
                                         KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0 ),
                                         JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
        
    }

    //
    // end Initialization routines
    //=================================================================


    //===================================================================
    // begin Event Listenters
    //

    /**
     * This listener knows how and when to invoke this popup menu.  It also helps
     * with click-and-drag scenarios by setting the selection if the mouse was
     * released over the list during a drag.
     */
    protected class InvocationMouseHandler extends MouseAdapter {
        public void mousePressed( MouseEvent e ) {
            Rectangle r;

            if ( !SwingUtilities.isLeftMouseButton(e) )
                return;

            if ( !comboBox.isEnabled() )
                return;

            delegateFocus( e );

            togglePopup();
        }

        public void mouseReleased( MouseEvent e ) {
            Component source = (Component)e.getSource();
            Dimension size = source.getSize();
            Rectangle bounds = new Rectangle( 0, 0, size.width - 1, size.height - 1 );
            if ( !bounds.contains( e.getPoint() ) ) {
                MouseEvent newEvent = convertMouseEvent( e );
                Point location = newEvent.getPoint();
                Rectangle r = new Rectangle();
                list.computeVisibleRect( r );
                if ( r.contains( location ) ) {
                    updateListBoxSelectionForEvent( newEvent, false );
                    comboBox.setSelectedIndex( list.getSelectedIndex() );
                }
                hide();
            }
            hasEntered = false;
            stopAutoScrolling();
        }
    }

    /**
     * This listener watches for dragging and updates the current selection in the
     * list if it is dragging over the list.
     */
    protected class InvocationMouseMotionHandler extends MouseMotionAdapter {
        public void mouseDragged( MouseEvent e ) {
            if ( isVisible() ) {
                MouseEvent newEvent = convertMouseEvent( e );
                Rectangle r = new Rectangle();
                list.computeVisibleRect( r );

                if ( newEvent.getPoint().y >= r.y && newEvent.getPoint().y <= r.y + r.height - 1 ) {
                    hasEntered = true;
                    if ( isAutoScrolling ) {
                        stopAutoScrolling();
                    }
                    Point location = newEvent.getPoint();
                    if ( r.contains( location ) ) {
                        valueIsAdjusting = true;
                        updateListBoxSelectionForEvent( newEvent, false );
                        valueIsAdjusting = false;
                    }
                }
                else {
                    if ( hasEntered ) {
                        int directionToScroll = newEvent.getPoint().y < r.y ? SCROLL_UP : SCROLL_DOWN;
                        if ( isAutoScrolling && scrollDirection != directionToScroll ) {
                            stopAutoScrolling();
                            startAutoScrolling( directionToScroll );
                        }
                        else if ( !isAutoScrolling ) {
                            startAutoScrolling( directionToScroll );
                        }
                    }
                    else {
                        if ( e.getPoint().y < 0 ) {
                            hasEntered = true;
                            startAutoScrolling( SCROLL_UP );
                        }
                    }
                }
            }
        }
    }

    /**
     * This listener watches for the spacebar being pressed and shows/hides the
     * popup accordingly.
     */
    public class InvocationKeyHandler extends KeyAdapter {
        public void keyReleased( KeyEvent e ) {
            if ( e.getKeyCode() == KeyEvent.VK_SPACE ||
                 e.getKeyCode() == KeyEvent.VK_ENTER ) {

                if ( isVisible() ) {
                    if ( lightNav ) {
                        comboBox.setSelectedIndex( list.getSelectedIndex() );
                    }
                    else {
                        togglePopup();
                    }
                }
                else if ( e.getKeyCode() == KeyEvent.VK_SPACE ) {
                    // Don't toggle if the popup is invisible and
                    // the key is an <Enter> (conflicts with default
                    // button)
                    togglePopup();
                }
            }
        }
    }

    /**
     * This listener watches for changes in the list's selection and reports
     * them to the combo box.
     */
    protected class ListSelectionHandler implements ListSelectionListener {
        public void valueChanged( ListSelectionEvent e ) {
            if ( !lightNav && !valueIsAdjusting && !e.getValueIsAdjusting() &&
                 list.getSelectedIndex() != comboBox.getSelectedIndex() &&
                 list.getSelectedIndex() < comboBox.getItemCount() &&
                 list.getSelectedIndex() >= -1 ) {

                valueIsAdjusting = true;
                comboBox.setSelectedIndex( list.getSelectedIndex() );
                list.ensureIndexIsVisible( list.getSelectedIndex() );
                valueIsAdjusting = false;
            }
        }
    }

    /**
     * Keeps the selected index in the list in-sync with the combo box's selection.
     *
     */
    public class ListDataHandler implements ListDataListener {
        public void contentsChanged( ListDataEvent e ) {
        }

        public void intervalAdded( ListDataEvent e ) {
            valueIsAdjusting = true;
            syncListSelectionWithComboBoxSelection();
            //list.ensureIndexIsVisible( list.getSelectedIndex() );
            valueIsAdjusting = false;
        }

        public void intervalRemoved( ListDataEvent e ) {
        }
    }

    /**
     * This listener hides the popup when the mouse is released in the list.
     */
    protected class ListMouseHandler extends MouseAdapter {
        public void mousePressed( MouseEvent e ) {
        }
        public void mouseReleased(MouseEvent anEvent) {
            comboBox.setSelectedIndex( list.getSelectedIndex() );
            hide();
        }
    }

    /**
     * This listener changes the selected item as you move the mouse over the list.
     * The selection change is not committed to the model, this is for user feedback only.
     */
    protected class ListMouseMotionHandler extends MouseMotionAdapter {
        public void mouseMoved( MouseEvent anEvent ) {
            Point location = anEvent.getPoint();
            Rectangle r = new Rectangle();
            list.computeVisibleRect( r );
            if ( r.contains( location ) ) {
                valueIsAdjusting = true;
                updateListBoxSelectionForEvent( anEvent, false );
                valueIsAdjusting = false;
            }
        }
    }

    /**
     * This listener watches for changes in the JComboBox's selection.  It updates
     * the list accordingly.
     */
    protected class ItemHandler implements ItemListener {
        public void itemStateChanged( ItemEvent e ) {
            if ( e.getStateChange() == ItemEvent.SELECTED &&
                 !valueIsAdjusting ) {
                valueIsAdjusting = true;
                syncListSelectionWithComboBoxSelection();
                valueIsAdjusting = false;
                list.ensureIndexIsVisible( comboBox.getSelectedIndex() );
            }
        }
    }

    /**
     * This listener watches for bound property changes in JComboBox.  If the model
     * or the renderer changes, the popup hides itself.
     */
    protected class PropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange( PropertyChangeEvent e ) {
            String propertyName = e.getPropertyName();

            if ( propertyName.equals("model") ) {
                uninstallComboBoxModelListeners( (ComboBoxModel)e.getOldValue() );
                list.setModel( (ComboBoxModel)e.getNewValue() );
                installComboBoxModelListeners( (ComboBoxModel)e.getNewValue() );

                if ( comboBox.getItemCount() > 0 ) {
                    comboBox.setSelectedIndex( 0 );
                }

                if ( isVisible() ) {
                    hide();
                }
            }
            else if ( propertyName.equals( "renderer" ) ) {
                list.setCellRenderer( comboBox.getRenderer() );
                if ( isVisible() ) {
                    hide();
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
	    else if (propertyName.equals("componentOrientation")) {
                // Pass along the new component orientation
                // to the list and the scroller

                ComponentOrientation o =(ComponentOrientation)e.getNewValue();

                JList list = getList();
                if (list!=null && list.getComponentOrientation()!=o) {
                    list.setComponentOrientation(o);
                }

                if (scroller!=null && scroller.getComponentOrientation()!=o) {
                    scroller.setComponentOrientation(o);
                }

                if (o!=getComponentOrientation()) {
                    setComponentOrientation(o);
                }
            }
        }
    }

    //
    // end Event Listeners
    //=================================================================


    /**
     * Overridden to unconditionally return false.
     */
    public boolean isFocusTraversable() {
        return false;
    }

    //===================================================================
    // begin Autoscroll methods
    //

    /**
     * Called by BasicComboPopup$InvocationMouseMotionHandler to handle auto-
     * scrolling the list.
     */
    protected void startAutoScrolling( int direction ) {
        if ( isAutoScrolling ) {
            autoscrollTimer.stop();
        }

        isAutoScrolling = true;

        if ( direction == SCROLL_UP ) {
            scrollDirection = SCROLL_UP;
            Point convertedPoint = SwingUtilities.convertPoint( scroller, new Point( 1, 1 ), list );
            int top = list.locationToIndex( convertedPoint );
            valueIsAdjusting = true;
            list.setSelectedIndex( top );
            valueIsAdjusting = false;

            ActionListener timerAction = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    autoScrollUp();
                }
            };

            autoscrollTimer = new Timer( 100, timerAction );
        }
        else if ( direction == SCROLL_DOWN ) {
            scrollDirection = SCROLL_DOWN;
            Dimension size = scroller.getSize();
            Point convertedPoint = SwingUtilities.convertPoint( scroller,
                                                                new Point( 1, (size.height - 1) - 2 ),
                                                                list );
            int bottom = list.locationToIndex( convertedPoint );
            valueIsAdjusting = true;
            list.setSelectedIndex( bottom );
            valueIsAdjusting = false;

            ActionListener timerAction = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    autoScrollDown();
                }
            };

            autoscrollTimer = new Timer( 100, timerAction );
        }
        autoscrollTimer.start();
    }

    protected void stopAutoScrolling() {
        isAutoScrolling = false;

        if ( autoscrollTimer != null ) {
            autoscrollTimer.stop();
            autoscrollTimer = null;
        }
    }

    protected void autoScrollUp() {
        int index = list.getSelectedIndex();
        if ( index > 0 ) {
            valueIsAdjusting = true;
            list.setSelectedIndex( index - 1 );
            valueIsAdjusting = false;
            list.ensureIndexIsVisible( index - 1 );
        }
    }

    protected void autoScrollDown() {
        int index = list.getSelectedIndex();
        int lastItem = list.getModel().getSize() - 1;
        if ( index < lastItem ) {
            valueIsAdjusting = true;
            list.setSelectedIndex( index + 1 );
            valueIsAdjusting = false;
            list.ensureIndexIsVisible( index + 1 );
        }
    }

    //
    // end Autoscroll methods
    //=================================================================


    //===================================================================
    // begin Utility methods
    //

    /**
     * This is is a utility method that helps event handlers figure out where to
     * send the focus when the popup is brought up.  The standard implementation
     * delegates the focus to the editor (if the combo box is editable) or to
     * the JComboBox if it is not editable.
     */
    protected void delegateFocus( MouseEvent e ) {
        if ( comboBox.isEditable() ) {
            comboBox.getEditor().getEditorComponent().requestFocus();
        }
        else {
            comboBox.requestFocus();
        }
    }

    /**
     * Makes the popup visible if it is hidden and makes it hidden if it is visible.
     */
    protected void togglePopup() {
        if ( isVisible() ) {
            hide();
        }
        else {
            show();
        }
    }

    void syncListSelectionWithComboBoxSelection() {
        int selectedIndex = comboBox.getSelectedIndex();

        if ( selectedIndex == -1 ) {
            list.clearSelection();
        }
        else {
            list.setSelectedIndex( selectedIndex );
        }
    }

    protected MouseEvent convertMouseEvent( MouseEvent e ) {
        Point convertedPoint = SwingUtilities.convertPoint( (Component)e.getSource(),
                                                            e.getPoint(), list );
        MouseEvent newEvent = new MouseEvent( (Component)e.getSource(),
                                              e.getID(),
                                              e.getWhen(),
                                              e.getModifiers(),
                                              convertedPoint.x,
                                              convertedPoint.y,
                                              e.getModifiers(),
                                              e.isPopupTrigger() );
        return newEvent;
    }

    protected int getPopupHeightForRowCount(int maxRowCount) {
        int currentElementCount = comboBox.getModel().getSize();
        int rowCount = Math.min( maxRowCount, currentElementCount );
        int height = 0;
        ListCellRenderer renderer = list.getCellRenderer();
        Object value = null;

        for ( int i = 0; i < rowCount; ++i ) {
            value = list.getModel().getElementAt( i );
            Component c = renderer.getListCellRendererComponent( list, value, i, false, false );
            height += c.getPreferredSize().height;
        }

        return height == 0 ? 100 : height;
/*
        if ( currentElementCount > 0 ) {
            Rectangle r = list.getCellBounds(0,0);

            if ( maxRowCount < currentElementCount )
                return (r.height * maxRowCount) + 2;
            else
                return (r.height * currentElementCount) + 2;

        }
        else
            return 100;
            */
    }

    /* REMIND: All this menu placement logic is now handled by 
     * javax.swing.DefaultPopupFactory
     * so this code should eventually be migrated to take advantage of it.
     */
    protected Rectangle computePopupBounds(int px,int py,int pw,int ph) {
        Rectangle absBounds = new Rectangle();
        Rectangle r = new Rectangle(px,py,pw,ph);
        Point p = new Point(0,0);
        Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        SwingUtilities.convertPointFromScreen(p,comboBox);
        absBounds.x = p.x;
        absBounds.y = p.y;
        absBounds.width = scrSize.width;
        absBounds.height= scrSize.height;
          if (py+ph > p.y+absBounds.height
             && ph < absBounds.height)
             return new Rectangle(0,-r.height,r.width,r.height); 
        else {
              return r;
        } 

    }


    /**
     * A utility method used by the event listeners.  Given a mouse event, it changes
     * the list selection to the list item below the mouse.
     */
    protected void updateListBoxSelectionForEvent(MouseEvent anEvent,boolean shouldScroll) {
        Point location = anEvent.getPoint();
        if ( list == null )
            return;
        int index = list.locationToIndex(location);
        if ( index == -1 ) {
            if ( location.y < 0 )
                index = 0;
            else
                index = comboBox.getModel().getSize() - 1;
        }
        if ( list.getSelectedIndex() != index ) {
            list.setSelectedIndex(index);
            if ( shouldScroll )
                list.ensureIndexIsVisible(index);
        }
    }

    //
    // end Utility methods
    //=================================================================
}


