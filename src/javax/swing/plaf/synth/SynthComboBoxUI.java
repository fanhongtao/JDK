/*
 * @(#)SynthComboBoxUI.java	1.15 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.synth;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import javax.swing.*;
import javax.accessibility.*;
import javax.swing.FocusManager;
import javax.swing.plaf.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import sun.awt.AppContext;
import sun.swing.plaf.synth.SynthUI;

/**
 * Synth's ComboBoxUI.
 *
 * @version 1.15, 12/19/03
 * @author Scott Violet
 */
class SynthComboBoxUI extends BasicComboBoxUI implements
                              PropertyChangeListener, SynthUI {
    private SynthStyle style;
    private boolean useListColors;

    public static ComponentUI createUI(JComponent c) {
        return new SynthComboBoxUI();
    }

    protected void installDefaults() {
        updateStyle(comboBox);
    }

    private void updateStyle(JComboBox comboBox) {
        SynthStyle oldStyle = style;
        SynthContext context = getContext(comboBox, ENABLED);

        style = SynthLookAndFeel.updateStyle(context, this);
        if (style != oldStyle) {
            useListColors = style.getBoolean(context,
                                  "ComboBox.rendererUseListColors", true);
            if (oldStyle != null) {
                uninstallKeyboardActions();
                installKeyboardActions();
            }
        }
        context.dispose();
    }

    protected void installListeners() {
        comboBox.addPropertyChangeListener(this);
        super.installListeners();
    }

    protected void uninstallDefaults() {
        SynthContext context = getContext(comboBox, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;
    }

    protected void uninstallListeners() {
        comboBox.removePropertyChangeListener(this);
        super.uninstallListeners();
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

    protected ComboPopup createPopup() {
        SynthComboPopup popup = new SynthComboPopup( comboBox );
        return popup;
    }

    protected ListCellRenderer createRenderer() {
        return new SynthComboBoxRenderer();
    }

    protected ComboBoxEditor createEditor() {
        return new SynthComboBoxEditor();
    }

    //
    // end UI Initialization
    //======================


    public void propertyChange(PropertyChangeEvent e) {
        if (SynthLookAndFeel.shouldUpdateStyle(e)) {
            updateStyle(comboBox);
        }
    }

    protected JButton createArrowButton() {
        SynthArrowButton button = new SynthArrowButton(SwingConstants.SOUTH);
        button.setName("ComboBox.arrowButton");
        return button;
    }

    //=================================
    // begin ComponentUI Implementation

    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        context.getPainter().paintComboBoxBackground(context, g, 0, 0,
                                                  c.getWidth(), c.getHeight());
        paint(context, g);
        context.dispose();
    }

    public void paint(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        paint(context, g);
        context.dispose();
    }

    protected void paint(SynthContext context, Graphics g) {
        hasFocus = comboBox.hasFocus();
        if ( !comboBox.isEditable() ) {
            Rectangle r = rectangleForCurrentValue();
            paintCurrentValue(g,r,hasFocus);
        }
    }

    public void paintBorder(SynthContext context, Graphics g, int x,
                            int y, int w, int h) {
        context.getPainter().paintComboBoxBorder(context, g, x, y, w, h);
    }


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
                                                       false,
                                                       false );
        }
        else {
            c = renderer.getListCellRendererComponent( listBox,
                                                       comboBox.getSelectedItem(),
                                                       -1,
                                                       false,
                                                       false );
        }
        // Fix for 4238829: should lay out the JPanel.
        boolean shouldValidate = false;
        if (c instanceof JPanel)  {
            shouldValidate = true;
        }

        if (c instanceof UIResource) {
            c.setName("ComboBox.renderer");
            currentValuePane.paintComponent(g,c,comboBox,bounds.x,bounds.y,
                                        bounds.width,bounds.height, shouldValidate);
        }
        else {
            currentValuePane.paintComponent(g,c,comboBox,bounds.x,bounds.y,
                                        bounds.width,bounds.height, shouldValidate);
        }
    }

    /**
     * From BasicComboBoxRenderer v 1.18.
     */
    private class SynthComboBoxRenderer extends JLabel implements ListCellRenderer, UIResource {
        public SynthComboBoxRenderer() {
            super();
            setText(" ");
        }

        public String getName() {
            // As SynthComboBoxRenderer's are asked for a size BEFORE they
            // are parented getName is overriden to force the name to be
            // ComboBox.renderer if it isn't set. If we didn't do this the
            // wrong style could be used for size calculations.
            String name = super.getName();
            if (name == null) {
                return "ComboBox.renderer";
            }
            return name;
        }

        public Component getListCellRendererComponent(JList list, Object value,
                         int index, boolean isSelected, boolean cellHasFocus) {
            setName("ComboBox.listRenderer");
            SynthLookAndFeel.resetSelectedUI();
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
                if (!useListColors) {
                    SynthLookAndFeel.setSelectedUI(
                         (SynthLabelUI)SynthLookAndFeel.getUIOfType(getUI(),
                         SynthLabelUI.class), isSelected, cellHasFocus,
                         list.isEnabled());
                }
            }
            else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            setFont(list.getFont());

            if (value instanceof Icon) {
                setIcon((Icon)value);
                setText("");
            }
            else {
                String text = (value == null) ? " " : value.toString();

                if ("".equals(text)) {
                    text = " ";
                }
                setText(text);
            }
            return this;
        }

        public void paint(Graphics g) {
            super.paint(g);
            SynthLookAndFeel.resetSelectedUI();
        }
    }


    /**
     * From BasicCombBoxEditor v 1.24.
     */
    private static class SynthComboBoxEditor implements
                              ComboBoxEditor, UIResource {
        protected JTextField editor;
        private Object oldValue;

        public SynthComboBoxEditor() {
            editor = new JTextField("",9);
            editor.setName("ComboBox.textField");
        }

        public Component getEditorComponent() {
            return editor;
        }

        /** 
         * Sets the item that should be edited. 
         *
         * @param anObject the displayed value of the editor
         */
        public void setItem(Object anObject) {
            String text;

            if ( anObject != null )  {
                text = anObject.toString();
                oldValue = anObject;
            } else {
                text = "";
            }
            // workaround for 4530952
            if (!text.equals(editor.getText())) {
                editor.setText(text);
            }
        }

        public Object getItem() {
            Object newValue = editor.getText();
        
            if (oldValue != null && !(oldValue instanceof String))  {
                // The original value is not a string. Should return the value in it's
                // original type.
                if (newValue.equals(oldValue.toString())) {
                    return oldValue;
                } else {
                    // Must take the value from the editor and get the value and cast it to the new type.
                    Class cls = oldValue.getClass();
                    try {
                        Method method = cls.getMethod("valueOf", new Class[]{String.class});
                        newValue = method.invoke(oldValue, new Object[] { editor.getText()});
                    } catch (Exception ex) {
                        // Fail silently and return the newValue (a String object)
                    }
                }
            }
            return newValue;
        }

        public void selectAll() {
            editor.selectAll();
            editor.requestFocus();
        }

        public void addActionListener(ActionListener l) {
            editor.addActionListener(l);
        }

        public void removeActionListener(ActionListener l) {
            editor.removeActionListener(l);
        }
    }
}
