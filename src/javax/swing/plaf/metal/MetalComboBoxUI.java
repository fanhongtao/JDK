/*
 * @(#)MetalComboBoxUI.java	1.42 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.metal;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import java.io.Serializable;
import java.beans.*;


/**
 * Metal UI for JComboBox
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
 * @see MetalComboBoxEditor
 * @see MetalComboBoxButton
 * @version 1.42 01/23/03
 * @author Tom Santos
 */
public class MetalComboBoxUI extends BasicComboBoxUI {

    public static ComponentUI createUI(JComponent c) {
        return new MetalComboBoxUI();
    }

    public void paint(Graphics g, JComponent c) {
    }

    protected ComboBoxEditor createEditor() {
        return new MetalComboBoxEditor.UIResource();
    }

    protected ComboPopup createPopup() {
        return new MetalComboPopup( comboBox );
    }

    protected JButton createArrowButton() {
        JButton button = new MetalComboBoxButton( comboBox,
                                                  new MetalComboBoxIcon(),
                                                  comboBox.isEditable(),
                                                  currentValuePane,
                                                  listBox );
        button.setMargin( new Insets( 0, 1, 1, 3 ) );
        return button;
    }

    public PropertyChangeListener createPropertyChangeListener() {
        return new MetalPropertyChangeListener();
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <FooUI>.
     */          
    public class MetalPropertyChangeListener extends BasicComboBoxUI.PropertyChangeHandler {
        public void propertyChange(PropertyChangeEvent e) {
            super.propertyChange( e );
            String propertyName = e.getPropertyName();

            if ( propertyName.equals( "editable" ) ) {
		MetalComboBoxButton button = (MetalComboBoxButton)arrowButton;
		button.setIconOnly( comboBox.isEditable() );
		comboBox.repaint();
            } else if ( propertyName.equals( "background" ) ) {
                Color color = (Color)e.getNewValue();
                arrowButton.setBackground(color);
                listBox.setBackground(color);
                
            } else if ( propertyName.equals( "foreground" ) ) {
                Color color = (Color)e.getNewValue();
                arrowButton.setForeground(color);
                listBox.setForeground(color);
            }
        }
    }

    /**
     * As of Java 2 platform v1.4 this method is no longer used. Do not call or
     * override. All the functionality of this method is in the
     * MetalPropertyChangeListener.
     *
     * @deprecated As of Java 2 platform v1.4.
     */
    protected void editablePropertyChanged( PropertyChangeEvent e ) { }

    protected LayoutManager createLayoutManager() {
        return new MetalComboBoxLayoutManager();
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <FooUI>.
     */          
    public class MetalComboBoxLayoutManager extends BasicComboBoxUI.ComboBoxLayoutManager {
        public void layoutContainer( Container parent ) {
            layoutComboBox( parent, this );
        }
        public void superLayout( Container parent ) {
            super.layoutContainer( parent );
        }
    }

    // This is here because of a bug in the compiler.  
    // When a protected-inner-class-savvy compiler comes out we
    // should move this into MetalComboBoxLayoutManager.
    public void layoutComboBox( Container parent, MetalComboBoxLayoutManager manager ) {
        if ( comboBox.isEditable() ) {
            manager.superLayout( parent );
        }
        else {
            if ( arrowButton != null ) {
                Insets insets = comboBox.getInsets();
                int width = comboBox.getWidth();
                int height = comboBox.getHeight();
                arrowButton.setBounds( insets.left, insets.top,
                                       width - (insets.left + insets.right),
                                       height - (insets.top + insets.bottom) );
            }
        }
    }

    /**
     * As of Java 2 platform v1.4 this method is no
     * longer used.
     *
     * @deprecated As of Java 2 platform v1.4.
     */
    protected void removeListeners() {
        if ( propertyChangeListener != null ) {
            comboBox.removePropertyChangeListener( propertyChangeListener );
        }
    }

    // These two methods were overloaded and made public. This was probably a
    // mistake in the implementation. The functionality that they used to 
    // provide is no longer necessary and should be removed. However, 
    // removing them will create an uncompatible API change.

    public void configureEditor() {
	super.configureEditor();
    }

    public void unconfigureEditor() {
	super.unconfigureEditor();
    }

    public Dimension getMinimumSize( JComponent c ) {
        if ( !isMinimumSizeDirty ) {
            return new Dimension( cachedMinimumSize );
        }

        Dimension size = null;

        if ( !comboBox.isEditable() &&
             arrowButton != null &&
             arrowButton instanceof MetalComboBoxButton ) {

            MetalComboBoxButton button = (MetalComboBoxButton)arrowButton;
            Insets buttonInsets = button.getInsets();
            Insets insets = comboBox.getInsets();

            size = getDisplaySize();
            size.width += insets.left + insets.right;
            size.width += buttonInsets.left + buttonInsets.right;
            size.width += buttonInsets.right + button.getComboIcon().getIconWidth();
            size.height += insets.top + insets.bottom;
            size.height += buttonInsets.top + buttonInsets.bottom;
        }
        else if ( comboBox.isEditable() &&
                  arrowButton != null &&
                  editor != null ) {
            size = super.getMinimumSize( c );
            Insets margin = arrowButton.getMargin();
            size.height += margin.top + margin.bottom;
            size.width += margin.left + margin.right;
        }
        else {
            size = super.getMinimumSize( c );
        }

        cachedMinimumSize.setSize( size.width, size.height ); 
        isMinimumSizeDirty = false;

        return new Dimension( cachedMinimumSize );
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <FooUI>.
     *
     * This class is now obsolete and doesn't do anything and
     * is only included for backwards API compatibility. Do not call or 
     * override.
     * 
     * @deprecated As of Java 2 platform v1.4.
     */          
    public class MetalComboPopup extends BasicComboPopup {

	public MetalComboPopup( JComboBox cBox) {
	    super( cBox );
	}

	// This method was overloaded and made public. This was probably
	// mistake in the implementation. The functionality that they used to 
	// provide is no longer necessary and should be removed. However, 
	// removing them will create an uncompatible API change.

	public void delegateFocus(MouseEvent e) {
	    super.delegateFocus(e);
	}
    }
}


