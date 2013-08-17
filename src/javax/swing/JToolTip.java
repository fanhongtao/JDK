/*
 * %W% %E%
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
import javax.swing.plaf.*;
import javax.accessibility.*;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * Used to display a "Tip" for a Component. Typically components provide api
 * to automate the process of using ToolTips. For example, any Swing component
 * can use the JComponent <code>setToolTipText</code> method to specify the text
 * for a standard tooltip. A component that wants to create a custom ToolTip
 * display can override JComponent's <code>createToolTip</code> method and use
 * a subclass of this class.
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JToolTip">JToolTip</a> key assignments.
 * <p>
 * See <a href="http://java.sun.com/docs/books/tutorial/ui/swing/tooltip.html">How to Use Tool Tips</a>
 * in <a href="http://java.sun.com/Series/Tutorial/index.html"><em>The Java Tutorial</em></a>
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
     * @return the ToolTipUI object that renders this component
     */
    public ToolTipUI getUI() {
        return (ToolTipUI)ui;
    }

    /**
     * Notification from the UIFactory that the L&F has changed. 
     * Called to replace the UI with the latest version from the 
     * UIFactory.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((ToolTipUI)UIManager.getUI(this));
    }


    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return "ToolTipUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }


    /**
     * Sets the text to show when the tool tip is displayed.
     *
     * @param tipText the String to display
     */
    public void setTipText(String tipText) {
        this.tipText = tipText;
    }

    /**
     * Returns the text that is shown when the tool tip is displayed.
     *
     * @return the String that is displayed
     */
    public String getTipText() {
        return tipText;
    }

    /**
     * Specifies the component that the tooltip describes.
     *
     * @see JComponent#createToolTip
     */
    public void setComponent(JComponent c) {
        component = c;
    }

    /**
     * Returns the component the tooltip applies to.
     * @return the component that the tooltip describes
     *
     * @see JComponent#createToolTip
     */
    public JComponent getComponent() {
        return component;
    }

    /**
     * Always return true since tooltips, by definition, 
     * should always be on top of all other windows.
     */
    // package private
    boolean alwaysOnTop() {
	return true;
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
     * Returns a string representation of this JToolTip. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * <P>
     * Overriding paramString() to provide information about the
     * specific new aspects of the JFC components.
     * 
     * @return  a string representation of this JToolTip.
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
     * Get the AccessibleContext associated with this JComponent
     *
     * @return the AccessibleContext of this JComponent
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJToolTip();
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
