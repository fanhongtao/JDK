/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package javax.swing;
import javax.swing.plaf.*;
import javax.accessibility.*;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * Used to display a "Tip" for a Component. Typically components provide api
 * to automate the process of using <code>ToolTip</code>s.
 * For example, any Swing component can use the <code>JComponent</code>
 * <code>setToolTipText</code> method to specify the text
 * for a standard tooltip. A component that wants to create a custom
 * <code>ToolTip</code>
 * display can override <code>JComponent</code>'s <code>createToolTip</code>
 * method and use a subclass of this class.
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JToolTip">JToolTip</a> key assignments.
 * <p>
 * See <a href="http://java.sun.com/docs/books/tutorial/uiswing/components/tooltip.html">How to Use Tool Tips</a>
 * in <em>The Java Tutorial</em>
 * for further documentation.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @see JComponent#setToolTipText
 * @see JComponent#createToolTip
 * @version %I% %G%
 * @author Dave Moore
 * @author Rich Shiavi
 */
public class JToolTip extends JComponent implements Accessible {
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ToolTipUI";

    String tipText;
    JComponent component;

    /** Creates a tool tip. */
    public JToolTip() {
        updateUI();
    }

    /**
     * Returns the L&F object that renders this component.
     *
     * @return the <code>ToolTipUI</code> object that renders this component
     */
    public ToolTipUI getUI() {
        return (ToolTipUI)ui;
    }

    /**
     * Notification from the <code>UIFactory</code> that the L&F has changed. 
     * Called to replace the UI with the latest version from the 
     * <code>UIFactory</code>.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((ToolTipUI)UIManager.getUI(this));
    }


    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return the string "ToolTipUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }


    /**
     * Sets the text to show when the tool tip is displayed.
     * The string <code>tipText</code> may be <code>null</code>.
     *
     * @param tipText the <code>String</code> to display
     * @beaninfo
     *    preferred: true
     *        bound: true
     *  description: Sets the text of the tooltip
     */
    public void setTipText(String tipText) {
        String oldValue = this.tipText;
        this.tipText = tipText;
        firePropertyChange("tiptext", oldValue, tipText);
    }

    /**
     * Returns the text that is shown when the tool tip is displayed.
     * The returned value may be <code>null</code>.
     *
     * @return the <code>String</code> that is displayed
     */
    public String getTipText() {
        return tipText;
    }

    /**
     * Specifies the component that the tooltip describes.
     * The component <code>c</code> may be <code>null</code>
     * and will have no effect.
     *
     * @param c the <code>JComponent</code> being described
     * @see JComponent#createToolTip
     */
    public void setComponent(JComponent c) {
        component = c;
    }

    /**
     * Returns the component the tooltip applies to.
     * The returned value may be <code>null</code>.
     *
     * @return the component that the tooltip describes
     *
     * @see JComponent#createToolTip
     */
    public JComponent getComponent() {
        return component;
    }

    /**
     * Always returns true since tooltips, by definition, 
     * should always be on top of all other windows.
     */
    // package private
    boolean alwaysOnTop() {
	return true;
    }


    /** 
     * See <code>readObject</code> and <code>writeObject</code>
     * in <code>JComponent</code> for more 
     * information about serialization in Swing.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
	if ((ui != null) && (getUIClassID().equals(uiClassID))) {
	    ui.installUI(this);
	}
    }


    /**
     * Returns a string representation of this <code>JToolTip</code>.
     * This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this <code>JToolTip</code>
     */
    protected String paramString() {
        String tipTextString = (tipText != null ?
				tipText : "");

        return super.paramString() +
        ",tipText=" + tipTextString;
    }


/////////////////
// Accessibility support
////////////////

    /**
     * Gets the AccessibleContext associated with this JToolTip. 
     * For tool tips, the AccessibleContext takes the form of an 
     * AccessibleJToolTip. 
     * A new AccessibleJToolTip instance is created if necessary.
     *
     * @return an AccessibleJToolTip that serves as the 
     *         AccessibleContext of this JToolTip
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJToolTip();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the 
     * <code>JToolTip</code> class.  It provides an implementation of the 
     * Java Accessibility API appropriate to tool tip user-interface elements.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected class AccessibleJToolTip extends AccessibleJComponent {

        /**
         * Get the accessible description of this object.
         *
         * @return a localized String describing this object.
         */
        public String getAccessibleDescription() {
            if (accessibleDescription != null) {
                return accessibleDescription;
            } else {
                return getTipText();
            }
        }

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
         * object
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.TOOL_TIP;
        }
    }
}
