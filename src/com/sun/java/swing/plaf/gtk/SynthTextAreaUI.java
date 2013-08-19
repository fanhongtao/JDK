/*
 * @(#)SynthTextAreaUI.java	1.4 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * Provides the look and feel for a plain text editor in the
 * Synth look and feel. In this implementation the default UI
 * is extended to act as a simple view factory.
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
 * @version 1.4 01/23/03 (based on revision 1.67 of BasicTextAreaUI)
 */
class SynthTextAreaUI extends SynthTextUI {

    /**
     * Creates a UI for a JTextArea.
     *
     * @param ta a text area
     * @return the UI
     */
    public static ComponentUI createUI(JComponent ta) {
        return new SynthTextAreaUI();
    }

    /**
     * Fetches the name used as a key to look up properties through the
     * UIManager.  This is used as a prefix to all the standard
     * text properties.
     *
     * @return the name ("TextArea")
     */
    protected String getPropertyPrefix() {
        return "TextArea";
    }

    /**
     * This method gets called when a bound property is changed
     * on the associated JTextComponent.  This is a hook
     * which UI implementations may change to reflect how the
     * UI displays bound properties of JTextComponent subclasses.
     * This is implemented to rebuild the View when the
     * <em>WrapLine</em> or the <em>WrapStyleWord</em> property changes.
     *
     * @param evt the property change event
     */
    protected void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("lineWrap") ||
            evt.getPropertyName().equals("wrapStyleWord") ||
                evt.getPropertyName().equals("tabSize")) {
            // rebuild the view
            modelChanged();
        } else if ("editable".equals(evt.getPropertyName())) {
            updateFocusTraversalKeys();
        }
    }

    /**
     * Creates the view for an element.  Returns a WrappedPlainView or
     * PlainView.
     *
     * @param elem the element
     * @return the view
     */
    public View create(Element elem) {
        Document doc = elem.getDocument();
        Object i18nFlag = doc.getProperty("i18n"/*AbstractDocument.I18NProperty*/);
        if ((i18nFlag != null) && i18nFlag.equals(Boolean.TRUE)) {
            // build a view that support bidi
            return createI18N(elem);
        } else {
            JTextComponent c = getComponent();
            if (c instanceof JTextArea) {
                JTextArea area = (JTextArea) c;
                View v;
                if (area.getLineWrap()) {
                    v = new WrappedPlainView(elem, area.getWrapStyleWord());
                } else {
                    v = new PlainView(elem);
                }
                return v;
            }
        }
        return null;
    }

    View createI18N(Element elem) {
        String kind = elem.getName();
        if (kind != null) {
            if (kind.equals(AbstractDocument.ContentElementName)) {
                return new PlainParagraph(elem);
            } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                return new BoxView(elem, View.Y_AXIS);
            }
        }
        return null;
    }

    /**
     * Paragraph for representing plain-text lines that support
     * bidirectional text.
     */
    static class PlainParagraph extends ParagraphView {

        PlainParagraph(Element elem) {
            super(elem);
            layoutPool = new LogicalView(elem);
            layoutPool.setParent(this);
        }

        public void setParent(View parent) {
            super.setParent(parent);
            if (parent != null) {
                setPropertiesFromAttributes();
            }
        }

        protected void setPropertiesFromAttributes() {
            Component c = getContainer();
            if ((c != null) && (! c.getComponentOrientation().isLeftToRight())) {
                setJustification(StyleConstants.ALIGN_RIGHT);
            } else {
                setJustification(StyleConstants.ALIGN_LEFT);
            }
        }

        /**
         * Fetch the constraining span to flow against for
         * the given child index.
         */
        public int getFlowSpan(int index) {
            Component c = getContainer();
            if (c instanceof JTextArea) {
                JTextArea area = (JTextArea) c;
                if (! area.getLineWrap()) {
                    // no limit if unwrapped
                    return Integer.MAX_VALUE;
                }
            }
            return super.getFlowSpan(index);
        }

        protected SizeRequirements calculateMinorAxisRequirements(int axis, 
                                                                  SizeRequirements r) {
            SizeRequirements req = super.calculateMinorAxisRequirements(axis, r);
            Component c = getContainer();
            if (c instanceof JTextArea) {
                JTextArea area = (JTextArea) c;
                if (! area.getLineWrap()) {
                    // min is pref if unwrapped
                    req.minimum = req.preferred;
                } else {
                    req.minimum = 0;
                    req.preferred = getWidth();
                    if (req.preferred == Short.MAX_VALUE) {
                        // We have been initially set to MAX_VALUE, but we
                        // don't want this as our preferred. Short is used
                        // here as FlowView returns it as the max size.
                        req.preferred = 100;
                    }
                }
            }
            return req;
        }

        /**
         * Sets the size of the view.  If the size has changed, layout
         * is redone.  The size is the full size of the view including
         * the inset areas.  
         *
         * @param width the width >= 0
         * @param height the height >= 0
         */
        public void setSize(float width, float height) {
            if ((int) width != getWidth()) {
                preferenceChanged(null, true, true);
            }
            super.setSize(width, height);
        }

        /**
         * This class can be used to represent a logical view for 
         * a flow.  It keeps the children updated to reflect the state
         * of the model, gives the logical child views access to the
         * view hierarchy, and calculates a preferred span.  It doesn't
         * do any rendering, layout, or model/view translation.
         */
        static class LogicalView extends CompositeView {
            
            LogicalView(Element elem) {
                super(elem);
            }

            protected int getViewIndexAtPosition(int pos) {
                Element elem = getElement();
                if (elem.getElementCount() > 0) {
                    return elem.getElementIndex(pos);
                }
                return 0;
            }

            protected boolean updateChildren(DocumentEvent.ElementChange ec, 
                                             DocumentEvent e, ViewFactory f) {
                return false;
            }

            protected void loadChildren(ViewFactory f) {
                Element elem = getElement();
                if (elem.getElementCount() > 0) {
                    super.loadChildren(f);
                } else {
                    View v = new GlyphView(elem);
                    append(v);
                }
            }

            public float getPreferredSpan(int axis) {
                if( getViewCount() != 1 )
                    throw new Error("One child view is assumed.");
                
                View v = getView(0);
                return v.getPreferredSpan(axis);
            }

            /**
             * Forward the DocumentEvent to the given child view.  This
             * is implemented to reparent the child to the logical view
             * (the children may have been parented by a row in the flow
             * if they fit without breaking) and then execute the superclass 
             * behavior.
             *
             * @param v the child view to forward the event to.
             * @param e the change information from the associated document
             * @param a the current allocation of the view
             * @param f the factory to use to rebuild if the view has children
             * @see #forwardUpdate
             * @since 1.3
             */
            protected void forwardUpdateToView(View v, DocumentEvent e, 
                                               Shape a, ViewFactory f) {
                v.setParent(this);
                super.forwardUpdateToView(v, e, a, f);
            }

            // The following methods don't do anything useful, they
            // simply keep the class from being abstract.

            public void paint(Graphics g, Shape allocation) {
            }

            protected boolean isBefore(int x, int y, Rectangle alloc) {
                return false;
            }

            protected boolean isAfter(int x, int y, Rectangle alloc) {
                return false;
            }

            protected View getViewAtPoint(int x, int y, Rectangle alloc) {
                return null;
            }

            protected void childAllocation(int index, Rectangle a) {
            }
        }
    }

}
