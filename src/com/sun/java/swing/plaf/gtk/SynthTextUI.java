/*
 * @(#)SynthTextUI.java	1.11 03/02/18
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.plaf.*;
import javax.swing.border.Border;
import java.beans.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.im.InputContext;
import java.awt.datatransfer.*;
import java.util.*;
import java.io.*;

/**
 * <p>
 * Basis of a text components look-and-feel in the Synth look and
 * feel.  This provides the
 * basic editor view and controller services that may be useful
 * when creating a look-and-feel for an extension of
 * <code>JTextComponent</code>.
 * <p>
 * Most state is held in the associated <code>JTextComponent</code>
 * as bound properties, and the UI installs default values for the 
 * various properties.  This default will install something for
 * all of the properties.  Typically, a LAF implementation will
 * do more however.  At a minimum, a LAF would generally install
 * key bindings.
 * <p>
 * This class also provides some concurrency support if the 
 * <code>Document</code> associated with the JTextComponent is a subclass of
 * <code>AbstractDocument</code>.  Access to the View (or View hierarchy) is
 * serialized between any thread mutating the model and the Swing
 * event thread (which is expected to render, do model/view coordinate
 * translation, etc).  <em>Any access to the root view should first
 * acquire a read-lock on the AbstractDocument and release that lock
 * in a finally block.</em>
 * <p>
 * An important method to define is the {@link #getPropertyPrefix} method
 * which is used as the basis of the keys used to fetch defaults
 * from the UIManager.  The string should reflect the type of 
 * TextUI (eg. TextField, TextArea, etc) without the particular 
 * LAF part of the name (eg Metal, Motif, etc).
 * <p>
 * To build a view of the model, one of the following strategies 
 * can be employed.
 * <ol>
 * <li>
 * One strategy is to simply redefine the 
 * ViewFactory interface in the UI.  By default, this UI itself acts
 * as the factory for View implementations.  This is useful
 * for simple factories.  To do this reimplement the 
 * {@link #create} method.
 * <li>
 * A common strategy for creating more complex types of documents
 * is to have the EditorKit implementation return a factory.  Since
 * the EditorKit ties all of the pieces necessary to maintain a type
 * of document, the factory is typically an important part of that
 * and should be produced by the EditorKit implementation.
 * <li>
 * A less common way to create more complex types is to have
 * the UI implementation create a.
 * separate object for the factory.  To do this, the 
 * {@link #createViewFactory} method should be reimplemented to 
 * return some factory.
 * </ol>
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @author  Shannon Hickey
 * @version 1.11 02/18/03 (based on revision 1.82 of BasicTextUI)
 */
abstract class SynthTextUI extends TextUI implements SynthUI, ViewFactory {

    private SynthStyle style;
    private static final EditorKit defaultKit = new DefaultEditorKit();
    transient JTextComponent editor;
    transient boolean painted;
    transient RootView rootView = new RootView();
    transient UpdateHandler updateHandler = new UpdateHandler();
    private static final TransferHandler defaultTransferHandler = new TextTransferHandler();
    private static DropTargetListener defaultDropTargetListener = null;
    private static final TextDragGestureRecognizer defaultDragRecognizer = new TextDragGestureRecognizer();
    private static final Position.Bias[] discardBias = new Position.Bias[1];
    
    public static class SynthCaret extends DefaultCaret implements UIResource {}
    public static class SynthHighlighter extends DefaultHighlighter implements UIResource {}

    
    void forceFetchStyle(JTextComponent comp) {
        style = null;
        fetchStyle(comp);
    }

    private void fetchStyle(JTextComponent comp) {
        SynthContext context = getContext(comp, ENABLED);
        SynthStyle oldStyle = style;

        style = SynthLookAndFeel.updateStyle(context, this);

        if (style != oldStyle) {
            String prefix = getPropertyPrefix();

            Color color = editor.getCaretColor();
            if (color == null || color instanceof UIResource) {
                editor.setCaretColor((Color)style.get(context, prefix + ".caretForeground"));
            }
            
            Color fg = editor.getForeground();
            if (fg == null || fg instanceof UIResource) {
                editor.setSelectionColor(style.getColor(context, ColorType.TEXT_FOREGROUND));
            }

            context.setComponentState(SELECTED | FOCUSED);
            
            Color s = editor.getSelectionColor();
            if (s == null || s instanceof UIResource) {
                editor.setSelectionColor(style.getColor(context, ColorType.TEXT_BACKGROUND));
            }
            
            Color sfg = editor.getSelectedTextColor();
            if (sfg == null || sfg instanceof UIResource) {
                editor.setSelectedTextColor(style.getColor(context, ColorType.TEXT_FOREGROUND));
            }
            
            context.setComponentState(DISABLED);
            
            Color dfg = editor.getDisabledTextColor();
            if (dfg == null || dfg instanceof UIResource) {
                editor.setDisabledTextColor((Color)style.get(context, ColorType.FOREGROUND));
            }
            
            Insets margin = editor.getMargin();
            if (margin == null || margin instanceof UIResource) {
                margin = (Insets)style.get(context, prefix + ".margin");

                if (margin == null) {
                    // Some places assume margins are non-null.
                    margin = SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS;
                }
                editor.setMargin(margin);
            }
            
            Caret caret = editor.getCaret();
            if (caret instanceof UIResource) {
                Object o = style.get(context, prefix + ".caretBlinkRate");
                if (o != null && o instanceof Integer) {
                    Integer rate = (Integer)o;
                    caret.setBlinkRate(rate.intValue());
                }
            }
        }

        context.dispose();
    }

    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    private SynthContext getContext(JComponent c, int state) {
        return SynthContext.getContext(SynthContext.class, c,
                    SynthLookAndFeel.getRegion(c), style, state);
    }

    private Region getRegion(JComponent c) {
        return SynthLookAndFeel.getRegion(c);
    }

    private int getComponentState(JComponent c) {
        return SynthLookAndFeel.getComponentState(c);
    }
    
    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        paint(context, g);
        context.dispose();
    }
    
    public void paint(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        paint(context, g);
        context.dispose();
    }

    /**
     * Paints the interface.  This is routed to the
     * paintSafely method under the guarantee that
     * the model won't change from the view of this thread
     * while it's rendering (if the associated model is
     * derived from AbstractDocument).  This enables the 
     * model to potentially be updated asynchronously.
     */
    protected void paint(SynthContext context, Graphics g) {
        if ((rootView.getViewCount() > 0) && (rootView.getView(0) != null)) {
            Document doc = editor.getDocument();
            if (doc instanceof AbstractDocument) {
                ((AbstractDocument)doc).readLock();
            }
            try {
                paintSafely(g);
            } finally {
                if (doc instanceof AbstractDocument) {
                    ((AbstractDocument)doc).readUnlock();
                }
            }
        }
    }

    /**
     * Creates a new UI.
     */
    public SynthTextUI() {
        painted = false;
    }

    /**
     * Creates the object to use for a caret.  By default an
     * instance of SynthCaret is created.  This method
     * can be redefined to provide something else that implements
     * the InputPosition interface or a subclass of Caret.
     *
     * @return the caret object
     */
    protected Caret createCaret() {
        return new SynthCaret();
    }

    /**
     * Creates the object to use for adding highlights.  By default
     * an instance of SynthHighlighter is created.  This method
     * can be redefined to provide something else that implements
     * the Highlighter interface or a subclass of DefaultHighlighter.
     *
     * @return the highlighter
     */
    protected Highlighter createHighlighter() {
        return new SynthHighlighter();
    }

    /**
     * This method gets called when a bound property is changed
     * on the associated JTextComponent.  This is a hook
     * which UI implementations may change to reflect how the
     * UI displays bound properties of JTextComponent subclasses.
     * This is implemented to do nothing (i.e. the response to
     * properties in JTextComponent itself are handled prior
     * to calling this method).
     *
     * @param evt the property change event
     */
    protected void propertyChange(PropertyChangeEvent evt) {
    }

    /**
     * Gets the name used as a key to look up properties through the
     * UIManager.  This is used as a prefix to all the standard
     * text properties.
     *
     * @return the name
     */
    protected abstract String getPropertyPrefix();

    protected void installDefaults() {
        editor.addMouseListener(defaultDragRecognizer);
        editor.addMouseMotionListener(defaultDragRecognizer);

        String prefix = getPropertyPrefix();
        
        Caret caret = editor.getCaret();
        if (caret == null || caret instanceof UIResource) {
            editor.setCaret(createCaret());
        }

        Highlighter highlighter = editor.getHighlighter();
        if (highlighter == null || highlighter instanceof UIResource) {
            editor.setHighlighter(createHighlighter());
        }

        // this must come after creation of the caret since
        // fetchStyle might have to set the caret's blink rate
        fetchStyle(editor);

        TransferHandler th = editor.getTransferHandler();
        if (th == null || th instanceof UIResource) {
            editor.setTransferHandler(getTransferHandler());
        }
        DropTarget dropTarget = editor.getDropTarget();
        if (dropTarget instanceof UIResource) {
            if (defaultDropTargetListener == null) {
                defaultDropTargetListener = new TextDropTargetListener();
            }
            try {
                dropTarget.addDropTargetListener(defaultDropTargetListener);
            } catch (TooManyListenersException tmle) {
                // should not happen... swing drop target is multicast
            }
        }
    }

    protected void uninstallDefaults() {
        SynthContext context = getContext(editor, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;

        editor.removeMouseListener(defaultDragRecognizer);
        editor.removeMouseMotionListener(defaultDragRecognizer);

        if (editor.getCaretColor() instanceof UIResource) {
            editor.setCaretColor(null);
        }
                                                                                         
        if (editor.getSelectionColor() instanceof UIResource) {
            editor.setSelectionColor(null);
        }

        if (editor.getDisabledTextColor() instanceof UIResource) {
            editor.setDisabledTextColor(null);
        }

        if (editor.getSelectedTextColor() instanceof UIResource) {
            editor.setSelectedTextColor(null);
        }

        if (editor.getMargin() instanceof UIResource) {
            editor.setMargin(null);
        }

        if (editor.getCaret() instanceof UIResource) {
            editor.setCaret(null);
        }

        if (editor.getHighlighter() instanceof UIResource) {
            editor.setHighlighter(null);
        }

        if (editor.getTransferHandler() instanceof UIResource) {
            editor.setTransferHandler(null);
        }
    }

    /**
     * Installs listeners for the UI.
     */
    protected void installListeners() {
    }

    /**
     * Uninstalls listeners for the UI.
     */
    protected void uninstallListeners() {
    }

    protected void installKeyboardActions() {
        editor.setKeymap(JTextComponent.getKeymap(JTextComponent.DEFAULT_KEYMAP));
        
        InputMap km = getInputMap();
        if (km != null) {
            SwingUtilities.replaceUIInputMap(editor, JComponent.WHEN_FOCUSED,
                                             km);
        }
        
        ActionMap map = getActionMap();
        if (map != null) {
            SwingUtilities.replaceUIActionMap(editor, map);
        }

        updateFocusAcceleratorBinding(false);
    }

    /**
     * Get the InputMap to use for the UI.  
     */
    InputMap getInputMap() {
        SynthContext context = getContext(editor, ENABLED);
        SynthStyle style = context.getStyle();
        
        InputMap map = new InputMapUIResource();
        InputMap shared =
            (InputMap)style.get(context, getPropertyPrefix() + ".focusInputMap");

        if (shared != null) {
            map.setParent(shared);
        }
        
        context.dispose();

        return map;
    }

    /**
     * Invoked when the focus accelerator changes, this will update the
     * key bindings as necessary.
     */
    void updateFocusAcceleratorBinding(boolean changed) {
        char accelerator = editor.getFocusAccelerator();

        if (changed || accelerator != '\0') {
            InputMap km = SwingUtilities.getUIInputMap
                        (editor, JComponent.WHEN_IN_FOCUSED_WINDOW);

            if (km == null && accelerator != '\0') {
                km = new ComponentInputMapUIResource(editor);
                SwingUtilities.replaceUIInputMap(editor, JComponent.
                                                 WHEN_IN_FOCUSED_WINDOW, km);
                ActionMap am = getActionMap();
                SwingUtilities.replaceUIActionMap(editor, am);
            }
            if (km != null) {
                km.clear();
                if (accelerator != '\0') {
                    km.put(KeyStroke.getKeyStroke(accelerator,
                                                  ActionEvent.ALT_MASK),
                           "requestFocus");
                }
            }
        }
    }

    /**
     * Invoked when editable property is changed.
     *
     * removing 'TAB' and 'SHIFT-TAB' from traversalKeysSet in case 
     * editor is editable
     * adding 'TAB' and 'SHIFT-TAB' to traversalKeysSet in case 
     * editor is non editable
     */ 
    void updateFocusTraversalKeys() {
        /*
         * Fix for 4514331 Non-editable JTextArea and similar 
         * should allow Tab to keyboard - accessibility 
         */
        EditorKit editorKit = getEditorKit(editor);
        if ( editorKit != null
             && editorKit instanceof DefaultEditorKit) {
            Set storedForwardTraversalKeys = editor.
                getFocusTraversalKeys(KeyboardFocusManager.
                                      FORWARD_TRAVERSAL_KEYS);
            Set storedBackwardTraversalKeys = editor.
                getFocusTraversalKeys(KeyboardFocusManager.
                                      BACKWARD_TRAVERSAL_KEYS);
            Set forwardTraversalKeys = 
                new HashSet(storedForwardTraversalKeys);
            Set backwardTraversalKeys = 
                new HashSet(storedBackwardTraversalKeys);
            if (editor.isEditable()) {
                forwardTraversalKeys.
                    remove(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
                backwardTraversalKeys.
                    remove(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 
                                                  InputEvent.SHIFT_MASK));
            } else {
                forwardTraversalKeys.add(KeyStroke.
                                         getKeyStroke(KeyEvent.VK_TAB, 0));
                backwardTraversalKeys.
                    add(KeyStroke.
                        getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_MASK));
            }
            editor.setFocusTraversalKeys(KeyboardFocusManager.
                                         FORWARD_TRAVERSAL_KEYS, 
                                         forwardTraversalKeys);
            editor.setFocusTraversalKeys(KeyboardFocusManager.
                                         BACKWARD_TRAVERSAL_KEYS, 
                                         backwardTraversalKeys);
        }

    }

    /**
     * Returns the <code>TransferHandler</code> that will be installed if
     * their isn't one installed on the <code>JTextComponent</code>.
     */
    TransferHandler getTransferHandler() {
        return defaultTransferHandler;
    }

    /**
     * Fetch an action map to use.
     */
    ActionMap getActionMap() {
        String mapName = getPropertyPrefix() + ".actionMap";
        ActionMap map = (ActionMap)UIManager.get(mapName);

        if (map == null) {
            map = createActionMap();
            if (map != null) {
                UIManager.getLookAndFeelDefaults().put(mapName, map);
            }
        }
        ActionMap componentMap = new ActionMapUIResource();
        componentMap.put("requestFocus", new FocusAction());
        /* 
         * fix for bug 4515750 
         * JTextField & non-editable JTextArea bind return key - default btn not accessible
         *
         * Wrap the return action so that it is only enabled when the
         * component is editable. This allows the default button to be
         * processed when the text component has focus and isn't editable.
         * 
         */
        if (getEditorKit(editor) instanceof DefaultEditorKit) {
            if (map != null) {
                Object obj = map.get(DefaultEditorKit.insertBreakAction);
                if (obj != null  
                    && obj instanceof DefaultEditorKit.InsertBreakAction) {
                    Action action =  new TextActionWrapper((TextAction)obj);
                    componentMap.put(action.getValue(Action.NAME),action);
                }
            }
        }
        if (map != null) {
            componentMap.setParent(map);
        }
        return componentMap;
    }

    /**
     * Create a default action map.  This is basically the
     * set of actions found exported by the component.
     */
    ActionMap createActionMap() {
        ActionMap map = new ActionMapUIResource();
        Action[] actions = editor.getActions();
        //System.out.println("building map for UI: " + getPropertyPrefix());
        int n = actions.length;
        for (int i = 0; i < n; i++) {
            Action a = actions[i];
            map.put(a.getValue(Action.NAME), a);
            //System.out.println("  " + a.getValue(Action.NAME));
        }
        map.put(TransferHandler.getCutAction().getValue(Action.NAME),
                TransferHandler.getCutAction());
        map.put(TransferHandler.getCopyAction().getValue(Action.NAME),
                TransferHandler.getCopyAction());
        map.put(TransferHandler.getPasteAction().getValue(Action.NAME),
                TransferHandler.getPasteAction());
        return map;
    }

    protected void uninstallKeyboardActions() {
        editor.setKeymap(null);
        SwingUtilities.replaceUIInputMap(editor, JComponent.
                                         WHEN_IN_FOCUSED_WINDOW, null);
        SwingUtilities.replaceUIActionMap(editor, null);
    }

    /**
     * Fetches the text component associated with this
     * UI implementation.  This will be null until
     * the ui has been installed.
     *
     * @return the editor component
     */
    protected final JTextComponent getComponent() {
        return editor;
    }

    /**
     * Flags model changes.
     * This is called whenever the model has changed.
     * It is implemented to rebuild the view hierarchy
     * to represent the default root element of the
     * associated model.
     */
    protected void modelChanged() {
        // create a view hierarchy
        ViewFactory f = rootView.getViewFactory();
        Document doc = editor.getDocument();
        Element elem = doc.getDefaultRootElement();
        setView(f.create(elem));
    }

    /**
     * Sets the current root of the view hierarchy and calls invalidate().
     * If there were any child components, they will be removed (i.e.
     * there are assumed to have come from components embedded in views).
     *
     * @param v the root view
     */
    protected final void setView(View v) {
        editor.removeAll();
        rootView.setView(v);
        painted = false;
        editor.revalidate();
        editor.repaint();
    }

    /**
     * Paints the interface safely with a guarantee that
     * the model won't change from the view of this thread.  
     * This does the following things, rendering from 
     * back to front.
     * <ol>
     * <li>
     * The highlights (if any) are painted.
     * <li>
     * The view hierarchy is painted.
     * <li>
     * The caret is painted.
     * </ol>
     *
     * @param g the graphics context
     */
    protected void paintSafely(Graphics g) {
        painted = true;
        Highlighter highlighter = editor.getHighlighter();
        Caret caret = editor.getCaret();
        
        // paint the highlights
        if (highlighter != null) {
            highlighter.paint(g);
        }

        // paint the view hierarchy
        Rectangle alloc = getVisibleEditorRect();
        rootView.paint(g, alloc);
            
        // paint the caret
        if (caret != null) {
            caret.paint(g);
        }
    }

    /**
     * Installs the UI for a component.  This does the following
     * things.
     * <ol>
     * <li>
     * Set the associated component to opaque (can be changed
     * easily by a subclass or on JTextComponent directly),
     * which is the most common case.  This will cause the
     * component's background color to be painted.
     * <li>
     * Install the default caret and highlighter into the 
     * associated component.
     * <li>
     * Attach to the editor and model.  If there is no 
     * model, a default one is created.
     * <li>
     * create the view factory and the view hierarchy used
     * to represent the model.
     * </ol>
     *
     * @param c the editor component
     * @see ComponentUI#installUI
     */
    public void installUI(JComponent c) {
        if (c instanceof JTextComponent) {
            editor = (JTextComponent) c;

            // install defaults
            installDefaults();

            editor.setAutoscrolls(true);

            // attach to the model and editor
            editor.addPropertyChangeListener(updateHandler);
            Document doc = editor.getDocument();
            if (doc == null) {
                // no model, create a default one.  This will
                // fire a notification to the updateHandler 
                // which takes care of the rest. 
                editor.setDocument(getEditorKit(editor).createDefaultDocument());
            } else {
                doc.addDocumentListener(updateHandler);
                modelChanged();
            }

            // install keymap
            installListeners();
            installKeyboardActions();

            LayoutManager oldLayout = editor.getLayout();
            if ((oldLayout == null) || (oldLayout instanceof UIResource)) {
                // by default, use default LayoutManger implementation that
                // will position the components associated with a View object.
                editor.setLayout(updateHandler);
            }

        } else {
            throw new Error("TextUI needs JTextComponent");
        }
    }

    /**
     * Deinstalls the UI for a component.  This removes the listeners,
     * uninstalls the highlighter, removes views, and nulls out the keymap.
     *
     * @param c the editor component
     * @see ComponentUI#uninstallUI
     */
    public void uninstallUI(JComponent c) {
        // detach from the model
        editor.removePropertyChangeListener(updateHandler);
        editor.getDocument().removeDocumentListener(updateHandler);

        // view part
        painted = false;
        uninstallDefaults();
        rootView.setView(null);
        c.removeAll();
        LayoutManager lm = c.getLayout();
        if (lm instanceof UIResource) {
            c.setLayout(null);
        }

        // controller part
        uninstallKeyboardActions();
        uninstallListeners();
        
        editor = null;
    }

    /**
     * Gets the preferred size for the editor component.  If the component
     * has been given a size prior to receiving this request, it will
     * set the size of the view hierarchy to reflect the size of the component
     * before requesting the preferred size of the view hierarchy.  This
     * allows formatted views to format to the current component size before
     * answering the request.  Other views don't care about currently formatted
     * size and give the same answer either way.
     *
     * @param c the editor component
     * @return the size
     */
    public Dimension getPreferredSize(JComponent c) {
        Document doc = editor.getDocument();
        Insets i = c.getInsets();
        Dimension d = c.getSize();

        if (doc instanceof AbstractDocument) {
            ((AbstractDocument)doc).readLock();
        }
        try {
            if ((d.width > (i.left + i.right)) && (d.height > (i.top + i.bottom))) {
                rootView.setSize(d.width - i.left - i.right, d.height - i.top - i.bottom);
            }
            else if (d.width == 0 && d.height == 0) {
                // Probably haven't been layed out yet, force some sort of
                // initial sizing.
		rootView.setSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
            }
            d.width = (int) Math.min((long) rootView.getPreferredSpan(View.X_AXIS) +
                                     (long) i.left + (long) i.right, Integer.MAX_VALUE);
            d.height = (int) Math.min((long) rootView.getPreferredSpan(View.Y_AXIS) +
                                      (long) i.top + (long) i.bottom, Integer.MAX_VALUE);
        } finally {
            if (doc instanceof AbstractDocument) {
                ((AbstractDocument)doc).readUnlock();
            }
        }
        return d;
    }

    /**
     * Gets the minimum size for the editor component.
     *
     * @param c the editor component
     * @return the size
     */
    public Dimension getMinimumSize(JComponent c) {
        Document doc = editor.getDocument();
        Insets i = c.getInsets();
        Dimension d = new Dimension();
        if (doc instanceof AbstractDocument) {
            ((AbstractDocument)doc).readLock();
        }
        try {
            d.width = (int) rootView.getMinimumSpan(View.X_AXIS) + i.left + i.right;
            d.height = (int)  rootView.getMinimumSpan(View.Y_AXIS) + i.top + i.bottom;
        } finally {
            if (doc instanceof AbstractDocument) {
                ((AbstractDocument)doc).readUnlock();
            }
        }
        return d;
    }

    /**
     * Gets the maximum size for the editor component.
     *
     * @param c the editor component
     * @return the size
     */
    public Dimension getMaximumSize(JComponent c) {
        Document doc = editor.getDocument();
        Insets i = c.getInsets();
        Dimension d = new Dimension();
        if (doc instanceof AbstractDocument) {
            ((AbstractDocument)doc).readLock();
        }
        try {
            d.width = (int) Math.min((long) rootView.getMaximumSpan(View.X_AXIS) + 
                                     (long) i.left + (long) i.right, Integer.MAX_VALUE);
            d.height = (int) Math.min((long) rootView.getMaximumSpan(View.Y_AXIS) + 
                                      (long) i.top + (long) i.bottom, Integer.MAX_VALUE);
        } finally {
            if (doc instanceof AbstractDocument) {
                ((AbstractDocument)doc).readUnlock();
            }
        }
        return d;
    }

    // ---- TextUI methods -------------------------------------------


    /**
     * Gets the allocation to give the root View.  Due
     * to an unfortunate set of historical events this 
     * method is inappropriately named.  The Rectangle
     * returned has nothing to do with visibility.  
     * The component must have a non-zero positive size for 
     * this translation to be computed.
     *
     * @return the bounding box for the root view
     */
    protected Rectangle getVisibleEditorRect() {
        Rectangle alloc = editor.getBounds();
        if ((alloc.width > 0) && (alloc.height > 0)) {
            alloc.x = alloc.y = 0;
            Insets insets = editor.getInsets();
            alloc.x += insets.left;
            alloc.y += insets.top;
            alloc.width -= insets.left + insets.right;
            alloc.height -= insets.top + insets.bottom;
            return alloc;
        }
        return null;
    }

    /**
     * Converts the given location in the model to a place in
     * the view coordinate system.
     * The component must have a non-zero positive size for 
     * this translation to be computed.
     *
     * @param tc the text component for which this UI is installed
     * @param pos the local location in the model to translate >= 0
     * @return the coordinates as a rectangle, null if the model is not painted
     * @exception BadLocationException  if the given position does not
     *   represent a valid location in the associated document
     * @see TextUI#modelToView
     */
    public Rectangle modelToView(JTextComponent tc, int pos) throws BadLocationException {
        return modelToView(tc, pos, Position.Bias.Forward);
    }

    /**
     * Converts the given location in the model to a place in
     * the view coordinate system.
     * The component must have a non-zero positive size for 
     * this translation to be computed.
     *
     * @param tc the text component for which this UI is installed
     * @param pos the local location in the model to translate >= 0
     * @return the coordinates as a rectangle, null if the model is not painted
     * @exception BadLocationException  if the given position does not
     *   represent a valid location in the associated document
     * @see TextUI#modelToView
     */
    public Rectangle modelToView(JTextComponent tc, int pos, Position.Bias bias) throws BadLocationException {
        Document doc = editor.getDocument();
        if (doc instanceof AbstractDocument) {
            ((AbstractDocument)doc).readLock();
        }
        try {
            Rectangle alloc = getVisibleEditorRect();
            if (alloc != null) {
                rootView.setSize(alloc.width, alloc.height);
                Shape s = rootView.modelToView(pos, alloc, bias);
                if (s != null) {
                  return s.getBounds();
                }
            }
        } finally {
            if (doc instanceof AbstractDocument) {
                ((AbstractDocument)doc).readUnlock();
            }
        }
        return null;
    }

    /**
     * Converts the given place in the view coordinate system
     * to the nearest representative location in the model.
     * The component must have a non-zero positive size for 
     * this translation to be computed.
     *
     * @param tc the text component for which this UI is installed
     * @param pt the location in the view to translate.  This
     *  should be in the same coordinate system as the mouse events.
     * @return the offset from the start of the document >= 0,
     *   -1 if not painted
     * @see TextUI#viewToModel
     */
    public int viewToModel(JTextComponent tc, Point pt) {
        return viewToModel(tc, pt, discardBias);
    }

    /**
     * Converts the given place in the view coordinate system
     * to the nearest representative location in the model.
     * The component must have a non-zero positive size for 
     * this translation to be computed.
     *
     * @param tc the text component for which this UI is installed
     * @param pt the location in the view to translate.  This
     *  should be in the same coordinate system as the mouse events.
     * @return the offset from the start of the document >= 0,
     *   -1 if the component doesn't yet have a positive size.
     * @see TextUI#viewToModel
     */
    public int viewToModel(JTextComponent tc, Point pt,
                           Position.Bias[] biasReturn) {
        int offs = -1;
        Document doc = editor.getDocument();
        if (doc instanceof AbstractDocument) {
            ((AbstractDocument)doc).readLock();
        }
        try {
            Rectangle alloc = getVisibleEditorRect();
            if (alloc != null) {
                rootView.setSize(alloc.width, alloc.height);
                offs = rootView.viewToModel(pt.x, pt.y, alloc, biasReturn);
            }
        } finally {
            if (doc instanceof AbstractDocument) {
                ((AbstractDocument)doc).readUnlock();
            }
        }
        return offs;
    }

    /**
     * Provides a way to determine the next visually represented model 
     * location that one might place a caret.  Some views may not be visible,
     * they might not be in the same order found in the model, or they just
     * might not allow access to some of the locations in the model.
     *
     * @param pos the position to convert >= 0
     * @param a the allocated region to render into
     * @param direction the direction from the current position that can
     *  be thought of as the arrow keys typically found on a keyboard.
     *  This may be SwingConstants.WEST, SwingConstants.EAST, 
     *  SwingConstants.NORTH, or SwingConstants.SOUTH.  
     * @return the location within the model that best represents the next
     *  location visual position.
     * @exception BadLocationException
     * @exception IllegalArgumentException for an invalid direction
     */
    public int getNextVisualPositionFrom(JTextComponent t, int pos,
                    Position.Bias b, int direction, Position.Bias[] biasRet)
                    throws BadLocationException{
        Document doc = editor.getDocument();
        if (doc instanceof AbstractDocument) {
            ((AbstractDocument)doc).readLock();
        }
        try {
            if (painted) {
                Rectangle alloc = getVisibleEditorRect();
                rootView.setSize(alloc.width, alloc.height);
                return rootView.getNextVisualPositionFrom(pos, b, alloc, direction,
                                                          biasRet);
            }
        } finally {
            if (doc instanceof AbstractDocument) {
                ((AbstractDocument)doc).readUnlock();
            }
        }
        return -1;
    }

    /**
     * Causes the portion of the view responsible for the
     * given part of the model to be repainted.  Does nothing if
     * the view is not currently painted.
     *
     * @param tc the text component for which this UI is installed
     * @param p0 the beginning of the range >= 0
     * @param p1 the end of the range >= p0
     * @see TextUI#damageRange
     */
    public void damageRange(JTextComponent tc, int p0, int p1) {
        damageRange(tc, p0, p1, Position.Bias.Forward, Position.Bias.Backward);
    }

    /**
     * Causes the portion of the view responsible for the 
     * given part of the model to be repainted.
     *
     * @param p0 the beginning of the range >= 0
     * @param p1 the end of the range >= p0
     */
    public void damageRange(JTextComponent t, int p0, int p1,
                            Position.Bias p0Bias, Position.Bias p1Bias) {
        if (painted) {
            Rectangle alloc = getVisibleEditorRect();
            Document doc = t.getDocument();
            if (doc instanceof AbstractDocument) {
                ((AbstractDocument)doc).readLock();
            }
            try {
                rootView.setSize(alloc.width, alloc.height);
                Shape toDamage = rootView.modelToView(p0, p0Bias,
                                                      p1, p1Bias, alloc);
                Rectangle rect = (toDamage instanceof Rectangle) ?
                                 (Rectangle)toDamage : toDamage.getBounds();
                editor.repaint(rect.x, rect.y, rect.width, rect.height);
            } catch (BadLocationException e) {
            } finally {
                if (doc instanceof AbstractDocument) {
                    ((AbstractDocument)doc).readUnlock();
                }
            }
        }
    }

    /**
     * Fetches the EditorKit for the UI.
     *
     * @param tc the text component for which this UI is installed
     * @return the editor capabilities
     * @see TextUI#getEditorKit
     */
    public EditorKit getEditorKit(JTextComponent tc) {
        return defaultKit;
    }

    /**
     * Fetches a View with the allocation of the associated 
     * text component (i.e. the root of the hierarchy) that 
     * can be traversed to determine how the model is being
     * represented spatially.
     * <p>
     * <font color=red><b>NOTE:</b>The View hierarchy can
     * be traversed from the root view, and other things
     * can be done as well.  Things done in this way cannot
     * be protected like simple method calls through the TextUI.
     * Therefore, proper operation in the presence of concurrency
     * must be arranged by any logic that calls this method!
     * </font>
     *
     * @param tc the text component for which this UI is installed
     * @return the view
     * @see TextUI#getRootView
     */
    public View getRootView(JTextComponent tc) {
        return rootView;
    }


    /**
     * Returns the string to be used as the tooltip at the passed in location.
     * This forwards the method onto the root View.
     *
     * @see javax.swing.text.JTextComponent#getToolTipText
     * @see javax.swing.text.View#getToolTipText
     * @since 1.4
     */
    public String getToolTipText(JTextComponent t, Point pt) {
        if (!painted) {
            return null;
        }
        Document doc = editor.getDocument();
        String tt = null;
        Rectangle alloc = getVisibleEditorRect();

        if (doc instanceof AbstractDocument) {
            ((AbstractDocument)doc).readLock();
        }
        try {
            tt = rootView.getToolTipText(pt.x, pt.y, alloc);
        } finally {
            if (doc instanceof AbstractDocument) {
                ((AbstractDocument)doc).readUnlock();
            }
        }
        return tt;
    }

    // --- ViewFactory methods ------------------------------

    /**
     * Creates a view for an element.
     * If a subclass wishes to directly implement the factory
     * producing the view(s), it should reimplement this 
     * method.  By default it simply returns null indicating
     * it is unable to represent the element.
     *
     * @param elem the element
     * @return the view
     */
    public View create(Element elem) {
        return null;
    }

    /**
     * Creates a view for an element.
     * If a subclass wishes to directly implement the factory
     * producing the view(s), it should reimplement this 
     * method.  By default it simply returns null indicating
     * it is unable to represent the part of the element.
     *
     * @param elem the element
     * @param p0 the starting offset >= 0
     * @param p1 the ending offset >= p0
     * @return the view
     */
    public View create(Element elem, int p0, int p1) {
        return null;
    }



    /**
     * Root view that acts as a gateway between the component
     * and the View hierarchy.
     */
    class RootView extends View {

        RootView() {
            super(null);
        }

        void setView(View v) {
            if (view != null) {
                // get rid of back reference so that the old
                // hierarchy can be garbage collected.
                view.setParent(null);
            }
            view = v;
            if (view != null) {
                view.setParent(this);
            }
        }

        /**
         * Fetches the attributes to use when rendering.  At the root
         * level there are no attributes.  If an attribute is resolved
         * up the view hierarchy this is the end of the line.
         */
        public AttributeSet getAttributes() {
            return null;
        }

        /**
         * Determines the preferred span for this view along an axis.
         *
         * @param axis may be either X_AXIS or Y_AXIS
         * @return the span the view would like to be rendered into.
         *         Typically the view is told to render into the span
         *         that is returned, although there is no guarantee.
         *         The parent may choose to resize or break the view.
         */
        public float getPreferredSpan(int axis) {
            if (view != null) {
                return view.getPreferredSpan(axis);
            }
            return 10;
        }

        /**
         * Determines the minimum span for this view along an axis.
         *
         * @param axis may be either X_AXIS or Y_AXIS
         * @return the span the view would like to be rendered into.
         *         Typically the view is told to render into the span
         *         that is returned, although there is no guarantee.
         *         The parent may choose to resize or break the view.
         */
        public float getMinimumSpan(int axis) {
            if (view != null) {
                return view.getMinimumSpan(axis);
            }
            return 10;
        }

        /**
         * Determines the maximum span for this view along an axis.
         *
         * @param axis may be either X_AXIS or Y_AXIS
         * @return the span the view would like to be rendered into.
         *         Typically the view is told to render into the span
         *         that is returned, although there is no guarantee.
         *         The parent may choose to resize or break the view.
         */
        public float getMaximumSpan(int axis) {
            return Integer.MAX_VALUE;
        }

        /**
         * Specifies that a preference has changed.
         * Child views can call this on the parent to indicate that
         * the preference has changed.  The root view routes this to
         * invalidate on the hosting component.
         * <p>
         * This can be called on a different thread from the
         * event dispatching thread and is basically unsafe to
         * propagate into the component.  To make this safe,
         * the operation is transferred over to the event dispatching 
         * thread for completion.  It is a design goal that all view
         * methods be safe to call without concern for concurrency,
         * and this behavior helps make that true.
         *
         * @param child the child view
         * @param width true if the width preference has changed
         * @param height true if the height preference has changed
         */ 
        public void preferenceChanged(View child, boolean width, boolean height) {
            editor.revalidate();
        }

        /**
         * Determines the desired alignment for this view along an axis.
         *
         * @param axis may be either X_AXIS or Y_AXIS
         * @return the desired alignment, where 0.0 indicates the origin
         *     and 1.0 the full span away from the origin
         */
        public float getAlignment(int axis) {
            if (view != null) {
                return view.getAlignment(axis);
            }
            return 0;
        }

        /**
         * Renders the view.
         *
         * @param g the graphics context
         * @param allocation the region to render into
         */
        public void paint(Graphics g, Shape allocation) {
            if (view != null) {
                Rectangle alloc = (allocation instanceof Rectangle) ?
                          (Rectangle)allocation : allocation.getBounds();
                setSize(alloc.width, alloc.height);
                view.paint(g, allocation);
            }
        }
        
        /**
         * Sets the view parent.
         *
         * @param parent the parent view
         */
        public void setParent(View parent) {
            throw new Error("Can't set parent on root view");
        }

        /** 
         * Returns the number of views in this view.  Since
         * this view simply wraps the root of the view hierarchy
         * it has exactly one child.
         *
         * @return the number of views
         * @see #getView
         */
        public int getViewCount() {
            return 1;
        }

        /** 
         * Gets the n-th view in this container.
         *
         * @param n the number of the view to get
         * @return the view
         */
        public View getView(int n) {
            return view;
        }

        /**
         * Returns the child view index representing the given position in
         * the model.  This is implemented to return the index of the only
         * child.
         *
         * @param pos the position >= 0
         * @return  index of the view representing the given position, or 
         *   -1 if no view represents that position
         * @since 1.3
         */
        public int getViewIndex(int pos, Position.Bias b) {
            return 0;
        }
    
        /**
         * Fetches the allocation for the given child view. 
         * This enables finding out where various views
         * are located, without assuming the views store
         * their location.  This returns the given allocation
         * since this view simply acts as a gateway between
         * the view hierarchy and the associated component.
         *
         * @param index the index of the child
         * @param a  the allocation to this view.
         * @return the allocation to the child
         */
        public Shape getChildAllocation(int index, Shape a) {
            return a;
        }

        /**
         * Provides a mapping from the document model coordinate space
         * to the coordinate space of the view mapped to it.
         *
         * @param pos the position to convert
         * @param a the allocated region to render into
         * @return the bounding box of the given position
         */
        public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
            if (view != null) {
                return view.modelToView(pos, a, b);
            }
            return null;
        }

        /**
         * Provides a mapping from the document model coordinate space
         * to the coordinate space of the view mapped to it.
         *
         * @param p0 the position to convert >= 0
         * @param b0 the bias toward the previous character or the
         *  next character represented by p0, in case the 
         *  position is a boundary of two views. 
         * @param p1 the position to convert >= 0
         * @param b1 the bias toward the previous character or the
         *  next character represented by p1, in case the 
         *  position is a boundary of two views. 
         * @param a the allocated region to render into
         * @return the bounding box of the given position is returned
         * @exception BadLocationException  if the given position does
         *   not represent a valid location in the associated document
         * @exception IllegalArgumentException for an invalid bias argument
         * @see View#viewToModel
         */
        public Shape modelToView(int p0, Position.Bias b0, int p1, Position.Bias b1, Shape a) throws BadLocationException {
            if (view != null) {
                return view.modelToView(p0, b0, p1, b1, a);
            }
            return null;
        }

        /**
         * Provides a mapping from the view coordinate space to the logical
         * coordinate space of the model.
         *
         * @param x x coordinate of the view location to convert
         * @param y y coordinate of the view location to convert
         * @param a the allocated region to render into
         * @return the location within the model that best represents the
         *    given point in the view
         */
        public int viewToModel(float x, float y, Shape a, Position.Bias[] bias) {
            if (view != null) {
                int retValue = view.viewToModel(x, y, a, bias);
                return retValue;
            }
            return -1;
        }

        /**
         * Provides a way to determine the next visually represented model 
         * location that one might place a caret.  Some views may not be visible,
         * they might not be in the same order found in the model, or they just
         * might not allow access to some of the locations in the model.
         *
         * @param pos the position to convert >= 0
         * @param a the allocated region to render into
         * @param direction the direction from the current position that can
         *  be thought of as the arrow keys typically found on a keyboard.
         *  This may be SwingConstants.WEST, SwingConstants.EAST, 
         *  SwingConstants.NORTH, or SwingConstants.SOUTH.  
         * @return the location within the model that best represents the next
         *  location visual position.
         * @exception BadLocationException
         * @exception IllegalArgumentException for an invalid direction
         */
        public int getNextVisualPositionFrom(int pos, Position.Bias b, Shape a, 
                                             int direction,
                                             Position.Bias[] biasRet) 
            throws BadLocationException {
            if( view != null ) {
                int nextPos = view.getNextVisualPositionFrom(pos, b, a,
                                                     direction, biasRet);
                if(nextPos != -1) {
                    pos = nextPos;
                }
                else {
                    biasRet[0] = b;
                }
            } 
            return pos;
        }

        /**
         * Gives notification that something was inserted into the document
         * in a location that this view is responsible for.
         *
         * @param e the change information from the associated document
         * @param a the current allocation of the view
         * @param f the factory to use to rebuild if the view has children
         */
        public void insertUpdate(DocumentEvent e, Shape a, ViewFactory f) {
            if (view != null) {
                view.insertUpdate(e, a, f);
            }
        }
        
        /**
         * Gives notification that something was removed from the document
         * in a location that this view is responsible for.
         *
         * @param e the change information from the associated document
         * @param a the current allocation of the view
         * @param f the factory to use to rebuild if the view has children
         */
        public void removeUpdate(DocumentEvent e, Shape a, ViewFactory f) {
            if (view != null) {
                view.removeUpdate(e, a, f);
            }
        }

        /**
         * Gives notification from the document that attributes were changed
         * in a location that this view is responsible for.
         *
         * @param e the change information from the associated document
         * @param a the current allocation of the view
         * @param f the factory to use to rebuild if the view has children
         */
        public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f) {
            if (view != null) {
                view.changedUpdate(e, a, f);
            }
        }

        /**
         * Returns the document model underlying the view.
         *
         * @return the model
         */
        public Document getDocument() {
            return editor.getDocument();
        }
        
        /**
         * Returns the starting offset into the model for this view.
         *
         * @return the starting offset
         */
        public int getStartOffset() {
            if (view != null) {
                return view.getStartOffset();
            }
            return getElement().getStartOffset();
        }

        /**
         * Returns the ending offset into the model for this view.
         *
         * @return the ending offset
         */
        public int getEndOffset() {
            if (view != null) {
                return view.getEndOffset();
            }
            return getElement().getEndOffset();
        }

        /**
         * Gets the element that this view is mapped to.
         *
         * @return the view
         */
        public Element getElement() {
            if (view != null) {
                return view.getElement();
            }
            return editor.getDocument().getDefaultRootElement();
        }

        /**
         * Breaks this view on the given axis at the given length.
         *
         * @param axis may be either X_AXIS or Y_AXIS
         * @param len specifies where a break is desired in the span
         * @param the current allocation of the view
         * @return the fragment of the view that represents the given span
         *   if the view can be broken, otherwise null
         */
        public View breakView(int axis, float len, Shape a) {
            throw new Error("Can't break root view");
        }

        /**
         * Determines the resizability of the view along the
         * given axis.  A value of 0 or less is not resizable.
         *
         * @param axis may be either X_AXIS or Y_AXIS
         * @return the weight
         */
        public int getResizeWeight(int axis) {
            if (view != null) {
                return view.getResizeWeight(axis);
            }
            return 0;
        }

        /**
         * Sets the view size.
         *
         * @param width the width
         * @param height the height
         */
        public void setSize(float width, float height) {
            if (view != null) {
                view.setSize(width, height);
            }
        }

        /**
         * Fetches the container hosting the view.  This is useful for
         * things like scheduling a repaint, finding out the host 
         * components font, etc.  The default implementation
         * of this is to forward the query to the parent view.
         *
         * @return the container
         */
        public Container getContainer() {
            return editor;
        }
        
        /**
         * Fetches the factory to be used for building the
         * various view fragments that make up the view that
         * represents the model.  This is what determines
         * how the model will be represented.  This is implemented
         * to fetch the factory provided by the associated
         * EditorKit unless that is null, in which case this
         * simply returns the SynthTextUI itself which allows
         * subclasses to implement a simple factory directly without
         * creating extra objects.  
         *
         * @return the factory
         */
        public ViewFactory getViewFactory() {
            EditorKit kit = getEditorKit(editor);
            ViewFactory f = kit.getViewFactory();
            if (f != null) {
                return f;
            }
            return SynthTextUI.this;
        }

        private View view;

    }

    /**
     * Handles updates from various places.  If the model is changed,
     * this class unregisters as a listener to the old model and 
     * registers with the new model.  If the document model changes,
     * the change is forwarded to the root view.  If the focus
     * accelerator changes, a new keystroke is registered to request
     * focus.
     */
    class UpdateHandler implements PropertyChangeListener, DocumentListener, LayoutManager2, UIResource {

        // --- PropertyChangeListener methods -----------------------

        /**
         * This method gets called when a bound property is changed.
         * We are looking for document changes on the editor.
         */
        public final void propertyChange(PropertyChangeEvent evt) {
            if (SynthLookAndFeel.shouldUpdateStyle(evt)) {
                fetchStyle((JTextComponent)evt.getSource());
            }

            Object oldValue = evt.getOldValue();
            Object newValue = evt.getNewValue();
            String propertyName = evt.getPropertyName();
            if ((oldValue instanceof Document) || (newValue instanceof Document)) {
                if (oldValue != null) {
                    ((Document)oldValue).removeDocumentListener(this);
                }
                if (newValue != null) {
                    ((Document)newValue).addDocumentListener(this);
		    if ("document".equals(propertyName)) {
			SynthTextUI.this.propertyChange(evt);
			modelChanged();
			return;
		    }
                }
                modelChanged();
            }
            if (JTextComponent.FOCUS_ACCELERATOR_KEY.equals(propertyName)) {
                updateFocusAcceleratorBinding(true);
            } else if ("componentOrientation".equals(propertyName)) {
                // Changes in ComponentOrientation require the views to be
                // rebuilt.
                modelChanged();
            } else if ("font".equals(propertyName)) {
                modelChanged();
            } else if ("transferHandler".equals(propertyName)) {
                DropTarget dropTarget = editor.getDropTarget();
                if (dropTarget instanceof UIResource) {
                    if (defaultDropTargetListener == null) {
                        defaultDropTargetListener = new TextDropTargetListener();
                    }
                    try {
                        dropTarget.addDropTargetListener(defaultDropTargetListener);
                    } catch (TooManyListenersException tmle) {
                        // should not happen... swing drop target is multicast
                    }
                }
            } 
            SynthTextUI.this.propertyChange(evt);
        }

        // --- DocumentListener methods -----------------------

        /**
         * The insert notification.  Gets sent to the root of the view structure
         * that represents the portion of the model being represented by the
         * editor.  The factory is added as an argument to the update so that
         * the views can update themselves in a dynamic (not hardcoded) way.
         *
         * @param e  The change notification from the currently associated
         *  document.
         * @see DocumentListener#insertUpdate
         */
        public final void insertUpdate(DocumentEvent e) {
            Document doc = e.getDocument();
            Object o = doc.getProperty("i18n");
            if (o instanceof Boolean) {
                Boolean i18nFlag = (Boolean) o;
                if (i18nFlag.booleanValue() != i18nView) {
                    // i18n flag changed, rebuild the view
                    i18nView = i18nFlag.booleanValue();
                    modelChanged();
                    return;
                }
            }

            // normal insert update
            Rectangle alloc = (painted) ? getVisibleEditorRect() : null;
            rootView.insertUpdate(e, alloc, rootView.getViewFactory());
        }

        /**
         * The remove notification.  Gets sent to the root of the view structure
         * that represents the portion of the model being represented by the
         * editor.  The factory is added as an argument to the update so that
         * the views can update themselves in a dynamic (not hardcoded) way.
         *
         * @param e  The change notification from the currently associated
         *  document.
         * @see DocumentListener#removeUpdate
         */
        public final void removeUpdate(DocumentEvent e) {
            Rectangle alloc = (painted) ? getVisibleEditorRect() : null;
            rootView.removeUpdate(e, alloc, rootView.getViewFactory());
        }

        /**
         * The change notification.  Gets sent to the root of the view structure
         * that represents the portion of the model being represented by the
         * editor.  The factory is added as an argument to the update so that
         * the views can update themselves in a dynamic (not hardcoded) way.
         *
         * @param e  The change notification from the currently associated
         *  document.
         * @see DocumentListener#changeUpdate
         */
        public final void changedUpdate(DocumentEvent e) {
            Rectangle alloc = (painted) ? getVisibleEditorRect() : null;
            rootView.changedUpdate(e, alloc, rootView.getViewFactory());
        }

        // --- LayoutManager2 methods --------------------------------

        /**
         * Adds the specified component with the specified name to
         * the layout.
         * @param name the component name
         * @param comp the component to be added
         */
        public void addLayoutComponent(String name, Component comp) {
            // not supported
        }

        /**
         * Removes the specified component from the layout.
         * @param comp the component to be removed
         */
        public void removeLayoutComponent(Component comp) {
            if (constraints != null) {
                // remove the constraint record
                constraints.remove(comp);
            }
        }

        /**
         * Calculates the preferred size dimensions for the specified 
         * panel given the components in the specified parent container.
         * @param parent the component to be laid out
         *  
         * @see #minimumLayoutSize
         */
        public Dimension preferredLayoutSize(Container parent) {
            // should not be called (JComponent uses UI instead)
            return null;
        }

        /** 
         * Calculates the minimum size dimensions for the specified 
         * panel given the components in the specified parent container.
         * @param parent the component to be laid out
         * @see #preferredLayoutSize
         */
        public Dimension minimumLayoutSize(Container parent) {
            // should not be called (JComponent uses UI instead)
            return null;
        }

        /** 
         * Lays out the container in the specified panel.  This is
         * implemented to position all components that were added
         * with a View object as a constraint.  The current allocation
         * of the associated View is used as the location of the 
         * component.
         * <p>
         * A read-lock is acquired on the document to prevent the
         * view tree from being modified while the layout process
         * is active.
         *
         * @param parent the component which needs to be laid out 
         */
        public void layoutContainer(Container parent) {
            if ((constraints != null) && (! constraints.isEmpty())) {
                Rectangle alloc = getVisibleEditorRect();
                if (alloc != null) {
                    Document doc = editor.getDocument();
                    if (doc instanceof AbstractDocument) {
                        ((AbstractDocument)doc).readLock();
                    }
                    try {
                        rootView.setSize(alloc.width, alloc.height);
                        Enumeration components = constraints.keys();
                        while (components.hasMoreElements()) {
                            Component comp = (Component) components.nextElement();
                            View v = (View) constraints.get(comp);
                            Shape ca = calculateViewPosition(alloc, v);
                            if (ca != null) {
                                Rectangle compAlloc = (ca instanceof Rectangle) ? 
                                    (Rectangle) ca : ca.getBounds();
                                comp.setBounds(compAlloc);
                            }
                        }
                    } finally {
                        if (doc instanceof AbstractDocument) {
                            ((AbstractDocument)doc).readUnlock();
                        }
                    }                   
                }
            }
        }

        /**
         * Find the Shape representing the given view.
         */
        Shape calculateViewPosition(Shape alloc, View v) {
            int pos = v.getStartOffset();
            View child = null;
            for (View parent = rootView; (parent != null) && (parent != v); parent = child) {
                int index = parent.getViewIndex(pos, Position.Bias.Forward);
                alloc = parent.getChildAllocation(index, alloc);
                child = parent.getView(index);
            }
            return (child != null) ? alloc : null;
        }

        /**
         * Adds the specified component to the layout, using the specified
         * constraint object.  We only store those components that were added
         * with a constraint that is of type View.
         *
         * @param comp the component to be added
         * @param constraint  where/how the component is added to the layout.
         */
        public void addLayoutComponent(Component comp, Object constraint) {
            if (constraint instanceof View) {
                if (constraints == null) {
                    constraints = new Hashtable(7);
                }
                constraints.put(comp, constraint);
            }
        }

        /** 
         * Returns the maximum size of this component.
         * @see java.awt.Component#getMinimumSize()
         * @see java.awt.Component#getPreferredSize()
         * @see LayoutManager
         */
        public Dimension maximumLayoutSize(Container target) {
            // should not be called (JComponent uses UI instead)
            return null;
        }

        /**
         * Returns the alignment along the x axis.  This specifies how
         * the component would like to be aligned relative to other 
         * components.  The value should be a number between 0 and 1
         * where 0 represents alignment along the origin, 1 is aligned
         * the furthest away from the origin, 0.5 is centered, etc.
         */
        public float getLayoutAlignmentX(Container target) {
            return 0.5f;
        }

        /**
         * Returns the alignment along the y axis.  This specifies how
         * the component would like to be aligned relative to other 
         * components.  The value should be a number between 0 and 1
         * where 0 represents alignment along the origin, 1 is aligned
         * the furthest away from the origin, 0.5 is centered, etc.
         */
        public float getLayoutAlignmentY(Container target) {
            return 0.5f;
        }

        /**
         * Invalidates the layout, indicating that if the layout manager
         * has cached information it should be discarded.
         */
        public void invalidateLayout(Container target) {
        }

        /**
         * The "layout constraints" for the LayoutManager2 implementation.
         * These are View objects for those components that are represented
         * by a View in the View tree.
         */
        private Hashtable constraints;

        private boolean i18nView = false;
    }

    /**
     * Wrapper for text actions to return isEnabled false in case editor is non editable
     */
    class TextActionWrapper extends TextAction {
        public TextActionWrapper(TextAction action) {
            super((String)action.getValue(Action.NAME));
            this.action = action;
        }
        /**
         * The operation to perform when this action is triggered.
         *
         * @param e the action event
         */
        public void actionPerformed(ActionEvent e) {
            action.actionPerformed(e);
        }
        public boolean isEnabled() { 
            return (editor == null || editor.isEditable()) ? action.isEnabled() : false;
        }
        TextAction action = null;
    }


    /**
     * Registered in the ActionMap.
     */
    class FocusAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            editor.requestFocus();
        }

        public boolean isEnabled() {
            return editor.isEditable();
        }
    }

    /**
     * Drag gesture recognizer for text components.
     */
    static class TextDragGestureRecognizer extends SynthDragGestureRecognizer {

        /**
         * Determines if the following are true:
         * <ul>
         * <li>the press event is located over a selection
         * <li>the dragEnabled property is true
         * <li>A TranferHandler is installed
         * </ul>
         * <p>
         * This is implemented to check for a TransferHandler.
         * Subclasses should perform the remaining conditions.
         */
        protected boolean isDragPossible(MouseEvent e) {
            if (super.isDragPossible(e)) {
                JTextComponent c = (JTextComponent) this.getComponent(e);
                if (c.getDragEnabled()) {
                    Caret caret = c.getCaret();
                    int dot = caret.getDot();
                    int mark = caret.getMark();
                    if (dot != mark) {
                        Point p = new Point(e.getX(), e.getY());
                        int pos = c.viewToModel(p);
                        
                        int p0 = Math.min(dot, mark);
                        int p1 = Math.max(dot, mark);
                        if ((pos >= p0) && (pos < p1)) {
                            return true;
                        }
                    }
                }
                
            }
            return false;
        }
    }

    /**
     * A DropTargetListener to extend the default Swing handling of drop operations
     * by moving the caret to the nearest location to the mouse pointer.
     */
    static class TextDropTargetListener extends SynthDropTargetListener {

        /**
         * called to save the state of a component in case it needs to
         * be restored because a drop is not performed.
         */
        protected void saveComponentState(JComponent comp) {
            JTextComponent c = (JTextComponent) comp;
            Caret caret = c.getCaret();
            dot = caret.getDot();
            mark = caret.getMark();
            visible = caret.isVisible();
            caret.setVisible(true);
        }

        /**
         * called to restore the state of a component 
         * because a drop was not performed.
         */
        protected void restoreComponentState(JComponent comp) {
            JTextComponent c = (JTextComponent) comp;
            Caret caret = c.getCaret();
            caret.setDot(mark);
            caret.moveDot(dot);
            caret.setVisible(visible);
        }

        /**
         * called to restore the state of a component
         * because a drop was performed.
         */
        protected void restoreComponentStateForDrop(JComponent comp) {
            JTextComponent c = (JTextComponent) comp;
            Caret caret = c.getCaret();
            caret.setVisible(visible);
        }

        /**
         * called to set the insertion location to match the current
         * mouse pointer coordinates.
         */
        protected void updateInsertionLocation(JComponent comp, Point p) {
            JTextComponent c = (JTextComponent) comp;
            c.setCaretPosition(c.viewToModel(p));
        }

        int dot;
        int mark;
        boolean visible;
    }

    static class TextTransferHandler extends TransferHandler implements UIResource {
        
        private JTextComponent exportComp;
        private boolean shouldRemove;
        private int p0;
        private int p1;
        
        /**
         * Try to find a flavor that can be used to import a Transferable.  
         * The set of usable flavors are tried in the following order:
         * <ol>
         *     <li>First, an attempt is made to find a flavor matching the content type
         *         of the EditorKit for the component.
         *     <li>Second, an attempt to find a text/plain flavor is made.
         *     <li>Third, an attempt to find a flavor representing a String reference
         *         in the same VM is made.
         *     <li>Lastly, DataFlavor.stringFlavor is searched for.
         * </ol>
         */
        protected DataFlavor getImportFlavor(DataFlavor[] flavors, JTextComponent c) {
            DataFlavor plainFlavor = null;
            DataFlavor refFlavor = null;
            DataFlavor stringFlavor = null;
            
            if (c instanceof JEditorPane) {
                for (int i = 0; i < flavors.length; i++) {
                    String mime = flavors[i].getMimeType();
                    if (mime.startsWith(((JEditorPane)c).getEditorKit().getContentType())) {
                        return flavors[i];
                    } else if (plainFlavor == null && mime.startsWith("text/plain")) {
                        plainFlavor = flavors[i];
                    } else if (refFlavor == null && mime.startsWith("application/x-java-jvm-local-objectref")
                                                 && flavors[i].getRepresentationClass() == java.lang.String.class) {
                        refFlavor = flavors[i];
                    } else if (stringFlavor == null && flavors[i].equals(DataFlavor.stringFlavor)) {
                        stringFlavor = flavors[i];
                    }
                }
                if (plainFlavor != null) {
                    return plainFlavor;
                } else if (refFlavor != null) {
                    return refFlavor;
                } else if (stringFlavor != null) {
                    return stringFlavor;
                }
                return null;
            }
            
            
            for (int i = 0; i < flavors.length; i++) {
                String mime = flavors[i].getMimeType();
                if (mime.startsWith("text/plain")) {
                    return flavors[i];
                } else if (refFlavor == null && mime.startsWith("application/x-java-jvm-local-objectref")
                                             && flavors[i].getRepresentationClass() == java.lang.String.class) {
                    refFlavor = flavors[i];
                } else if (stringFlavor == null && flavors[i].equals(DataFlavor.stringFlavor)) {
                    stringFlavor = flavors[i];
                }
            }
            if (refFlavor != null) {
                return refFlavor;
            } else if (stringFlavor != null) {
                return stringFlavor;
            }
            return null;
        }

        /**
         * Import the given stream data into the text component.
         */
        protected void handleReaderImport(Reader in, JTextComponent c, boolean useRead)
                                               throws BadLocationException, IOException {
            if (useRead) {
                int startPosition = c.getSelectionStart();
                int endPosition = c.getSelectionEnd();
                int length = endPosition - startPosition;
                EditorKit kit = c.getUI().getEditorKit(c);
                Document doc = c.getDocument();
                if (length > 0) {
                    doc.remove(startPosition, length);
                }
                kit.read(in, doc, startPosition);
            } else {
                char[] buff = new char[1024];
                int nch;
                boolean lastWasCR = false;
                int last;
                StringBuffer sbuff = null;
                
                // Read in a block at a time, mapping \r\n to \n, as well as single
                // \r to \n.
                while ((nch = in.read(buff, 0, buff.length)) != -1) {
                    if (sbuff == null) {
                        sbuff = new StringBuffer(nch);
                    }
                    last = 0;
                    for(int counter = 0; counter < nch; counter++) {
                        switch(buff[counter]) {
                        case '\r':
                            if (lastWasCR) {
                                if (counter == 0) {
                                    sbuff.append('\n');
                                } else {
                                    buff[counter - 1] = '\n';
                                }
                            } else {
                                lastWasCR = true;
                            }
                            break;
                        case '\n':
                            if (lastWasCR) {
                                if (counter > (last + 1)) {
                                    sbuff.append(buff, last, counter - last - 1);
                                }
                                // else nothing to do, can skip \r, next write will
                                // write \n
                                lastWasCR = false;
                                last = counter;
                            }
                            break;
                        default:
                            if (lastWasCR) {
                                if (counter == 0) {
                                    sbuff.append('\n');
                                } else {
                                    buff[counter - 1] = '\n';
                                }
                                lastWasCR = false;
                            }
                            break;
                        }
                    }
                    if (last < nch) {
                        if (lastWasCR) {
                            if (last < (nch - 1)) {
                                sbuff.append(buff, last, nch - last - 1);
                            }
                        } else {
                            sbuff.append(buff, last, nch - last);
                        }
                    }
                }
                if (lastWasCR) {
                    sbuff.append('\n');
                }
                c.replaceSelection(sbuff != null ? sbuff.toString() : "");
            }
        }

        // --- TransferHandler methods ------------------------------------

        /**
         * This is the type of transfer actions supported by the source.  Some models are 
         * not mutable, so a transfer operation of COPY only should
         * be advertised in that case.
         * 
         * @param c  The component holding the data to be transfered.  This
         *  argument is provided to enable sharing of TransferHandlers by
         *  multiple components.
         * @return  This is implemented to return NONE if the component is a JPasswordField
         *  since exporting data via user gestures is not allowed.  If the text component is
         *  editable, COPY_OR_MOVE is returned, otherwise just COPY is allowed.
         */
        public int getSourceActions(JComponent c) {
            int actions = NONE;
            if (! (c instanceof JPasswordField)) {
                if (((JTextComponent)c).isEditable()) {
                    actions = COPY_OR_MOVE;
                } else {
                    actions = COPY;
                }
            }
            return actions;
        }

        /**
         * Create a Transferable to use as the source for a data transfer.
         *
         * @param comp  The component holding the data to be transfered.  This
         *  argument is provided to enable sharing of TransferHandlers by
         *  multiple components.
         * @return  The representation of the data to be transfered. 
         *  
         */
        protected Transferable createTransferable(JComponent comp) {
            exportComp = (JTextComponent)comp;
            shouldRemove = true;
            p0 = exportComp.getSelectionStart();
            p1 = exportComp.getSelectionEnd();
            return (p0 != p1) ? (new TextTransferable(exportComp, p0, p1)) : null;
        }

        /**
         * This method is called after data has been exported.  This method should remove 
         * the data that was transfered if the action was MOVE.
         *
         * @param source The component that was the source of the data.
         * @param data   The data that was transferred or possibly null
         *               if the action is <code>NONE</code>.
         * @param action The actual action that was performed.  
         */
        protected void exportDone(JComponent source, Transferable data, int action) {
            // only remove the text if shouldRemove has not been set to
            // false by importData and only if the action is a move
            if (shouldRemove && action == MOVE) {
                TextTransferable t = (TextTransferable)data;
                t.removeText();
            }
            
            exportComp = null;
        }

        /**
         * This method causes a transfer to a component from a clipboard or a 
         * DND drop operation.  The Transferable represents the data to be
         * imported into the component.  
         *
         * @param comp  The component to receive the transfer.  This
         *  argument is provided to enable sharing of TransferHandlers by
         *  multiple components.
         * @param t     The data to import
         * @return  true if the data was inserted into the component, false otherwise.
         */
        public boolean importData(JComponent comp, Transferable t) {
            JTextComponent c = (JTextComponent)comp;

            // if we are importing to the same component that we exported from
            // then don't actually do anything if the drop location is inside
            // the drag location and set shouldRemove to false so that exportDone
            // knows not to remove any data
            if (c == exportComp && c.getCaretPosition() >= p0 && c.getCaretPosition() <= p1) {
                shouldRemove = false;
                return true;
            }

            boolean imported = false;
            DataFlavor importFlavor = getImportFlavor(t.getTransferDataFlavors(), c);
            if (importFlavor != null) {
                try {
                    boolean useRead = false;
                    if (comp instanceof JEditorPane) {
                        JEditorPane ep = (JEditorPane)comp;
                        if (!ep.getContentType().startsWith("text/plain") &&
                                importFlavor.getMimeType().startsWith(ep.getContentType())) {
                            useRead = true;
                        }
                    }
                    Reader r = importFlavor.getReaderForText(t);
                    handleReaderImport(r, c, useRead);
                    imported = true;
                } catch (UnsupportedFlavorException ufe) {
                } catch (BadLocationException ble) {
                } catch (IOException ioe) {
                }
            }
            return imported;
        }

        /**
         * This method indicates if a component would accept an import of the given
         * set of data flavors prior to actually attempting to import it. 
         *
         * @param comp  The component to receive the transfer.  This
         *  argument is provided to enable sharing of TransferHandlers by
         *  multiple components.
         * @param flavors  The data formats available
         * @return  true if the data can be inserted into the component, false otherwise.
         */
        public boolean canImport(JComponent comp, DataFlavor[] flavors) {
            JTextComponent c = (JTextComponent)comp;
            if (!(c.isEditable() && c.isEnabled())) {
                return false;
            }
            return (getImportFlavor(flavors, c) != null);
        }

        /**
         * A possible implementation of the Transferable interface
         * for text components.  For a JEditorPane with a rich set
         * of EditorKit implementations, conversions could be made
         * giving a wider set of formats.  This is implemented to
         * offer up only the active content type and text/plain
         * (if that is not the active format) since that can be
         * extracted from other formats.
         */
        static class TextTransferable extends SynthTransferable {

            TextTransferable(JTextComponent c, int start, int end) {
                super(null, null);
                
                this.c = c;
                
                Document doc = c.getDocument();

                try {
                    p0 = doc.createPosition(start);
                    p1 = doc.createPosition(end);

                    plainData = c.getSelectedText();

                    if (c instanceof JEditorPane) {
                        JEditorPane ep = (JEditorPane)c;
                        
                        mimeType = ep.getContentType();

                        if (mimeType.startsWith("text/plain")) {
                            return;
                        }

                        StringWriter sw = new StringWriter(p1.getOffset() - p0.getOffset());
                        ep.getEditorKit().write(sw, doc, p0.getOffset(), p1.getOffset() - p0.getOffset());
                        
                        if (mimeType.startsWith("text/html")) {
                            htmlData = sw.toString();
                        } else {
                            richText = sw.toString();
                        }
                    }
                } catch (BadLocationException ble) {
                } catch (IOException ioe) {
                }
            }

            void removeText() {
                if ((p0 != null) && (p1 != null) && (p0.getOffset() != p1.getOffset())) {
                    try {
                        Document doc = c.getDocument();
                        doc.remove(p0.getOffset(), p1.getOffset() - p0.getOffset());
                    } catch (BadLocationException e) {
                    }
                }
            }

            // ---- EditorKit other than plain or HTML text -----------------------

            /** 
             * If the EditorKit is not for text/plain or text/html, that format
             * is supported through the "richer flavors" part of SynthTransferable.
             */
            protected DataFlavor[] getRicherFlavors() {
                if (richText == null) {
                    return null;
                }

                try {
                    DataFlavor[] flavors = new DataFlavor[3];
                    flavors[0] = new DataFlavor(mimeType + ";class=java.lang.String");
                    flavors[1] = new DataFlavor(mimeType + ";class=java.io.Reader");
                    flavors[2] = new DataFlavor(mimeType + ";class=java.io.InputStream;charset=unicode");
                    return flavors;
                } catch (ClassNotFoundException cle) {
                    // fall through to unsupported (should not happen)
                }

                return null;
            }

            /**
             * The only richer format supported is the file list flavor
             */
            protected Object getRicherData(DataFlavor flavor) throws UnsupportedFlavorException {
                if (richText == null) {
                    return null;
                }

                if (String.class.equals(flavor.getRepresentationClass())) {
                    return richText;
                } else if (Reader.class.equals(flavor.getRepresentationClass())) {
                    return new StringReader(richText);
                } else if (InputStream.class.equals(flavor.getRepresentationClass())) {
                    return new StringBufferInputStream(richText);
                }
                throw new UnsupportedFlavorException(flavor);
            }

            Position p0;
            Position p1;
            String mimeType;
            String richText;
            JTextComponent c;
        }

    }

}
