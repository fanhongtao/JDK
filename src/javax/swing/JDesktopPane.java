/*
 * @(#)JDesktopPane.java	1.24 98/08/28
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

import java.util.Vector;
import javax.swing.plaf.*;
import javax.accessibility.*;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

/**
 * A container used to create a multiple-document interface or a virtual desktop. 
 * You create JInternalFrame objects and add them to the JDesktopPane. 
 * JDesktopPane extends JLayeredPane to manage the potentially overlapping internal frames. It also 
 * maintains a reference to an instance of DesktopManager that is set by the UI 
 * class for the current Look and Feel (L&F). 
 * <p>
 * This class is normally used as the parent of JInternalFrames to provide a
 * pluggable DesktopManager object to the JInternalFrames. The installUI of the 
 * L&F specific implementation is responsible for setting the desktopManager 
 * variable appropriately. When the parent of a JInternalFrame is a JDesktopPane, 
 * it should delegate most of its behavior to the desktopManager (closing, resizing,
 * etc).
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JDesktopPane">JDesktopPane</a> key assignments.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @see JInternalFrame
 * @see JInternalFrame.JDesktopIcon
 * @see DesktopManager
 *
 * @version 1.24 08/28/98
 * @author David Kloba
 */
public class JDesktopPane extends JLayeredPane implements Accessible
{
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "DesktopPaneUI";

    DesktopManager desktopManager;

    /** 
     * Creates a new JDesktopPane.
     */
    public JDesktopPane() {
        updateUI();
    }

    /**
     * Returns the L&F object that renders this component.
     *
     * @return the DesktopPaneUI object that renders this component
     */
    public DesktopPaneUI getUI() {
        return (DesktopPaneUI)ui;
    }

    /**
     * Sets the L&F object that renders this component.
     *
     * @param ui  the DesktopPaneUI L&F object
     * @see UIDefaults#getUI
     */
    public void setUI(DesktopPaneUI ui) {
        super.setUI(ui);
    }

    /** 
     * Returns the DesktopManger that handles desktop-specific UI actions.
     *
     * @param d the DesktopManager currently in use 
     */
    public DesktopManager getDesktopManager() {
        return desktopManager;
    }

    /**
     * Sets the DesktopManger that will handle desktop-specific UI actions.
     *
     * @param d the DesktopManager to use 
     */
    public void setDesktopManager(DesktopManager d) {
        desktopManager = d;
    }

    /**
     * Notification from the UIManager that the L&F has changed. 
     * Replaces the current UI object with the latest version from the 
     * UIManager.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((DesktopPaneUI)UIManager.getUI(this));
    }


    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return "DesktopPaneUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }

    /** 
     * Returns all JInternalFrames currently displayed in the
     * desktop. Returns iconified frames as well as expanded frames.
     *
     * @return an array of JInternalFrame objects
     */
    public JInternalFrame[] getAllFrames() {
        int i, count;
        JInternalFrame[] results;
        Vector vResults = new Vector(10);
        Object next, tmp;

        count = getComponentCount();
        for(i = 0; i < count; i++) {
            next = getComponent(i);
            if(next instanceof JInternalFrame)
                vResults.addElement(next);
            else if(next instanceof JInternalFrame.JDesktopIcon)  {
                tmp = ((JInternalFrame.JDesktopIcon)next).getInternalFrame();
                if(tmp != null)
                    vResults.addElement(tmp);
            }
        }

        results = new JInternalFrame[vResults.size()];
        vResults.copyInto(results);

        return results;
    }

    /**
     * Returns all JInternalFrames currently displayed in the
     * specified layer of the desktop. Returns iconified frames as well
     * expanded frames.
     *
     * @param layer  an int specifying the desktop layer
     * @return an array of JInternalFrame objects
     * @see JLayeredPane
     */
    public JInternalFrame[] getAllFramesInLayer(int layer) {
        int i, count;
        JInternalFrame[] results;
        Vector vResults = new Vector(10);
        Object next, tmp;

        count = getComponentCount();
        for(i = 0; i < count; i++) {
            next = getComponent(i);
            if(next instanceof JInternalFrame)
                if(((JInternalFrame)next).getLayer() == layer)
                    vResults.addElement(next);
            else if(next instanceof JInternalFrame.JDesktopIcon)  {
                tmp = ((JInternalFrame.JDesktopIcon)next).getInternalFrame();
                if(tmp != null && ((JInternalFrame)tmp).getLayer() == layer)
                    vResults.addElement(tmp);
            }
        }

        results = new JInternalFrame[vResults.size()];
        vResults.copyInto(results);

        return results;
    }

    /**
     * Returns true to indicate that this component paints every pixel
     * in its range. (In other words, it does not have a transparent
     * background or foreground.)
     *
     * @return true
     * @see JComponent#isOpaque
     */
    public boolean isOpaque() {
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
     * Returns a string representation of this JDesktopPane. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * <P>
     * Overriding paramString() to provide information about the
     * specific new aspects of the JFC components.
     * 
     * @return  a string representation of this JDesktopPane.
     */
    protected String paramString() {
	String desktopManagerString = (desktopManager != null ?
				       desktopManager.toString() : "");

	return super.paramString() +
	",desktopManager=" + desktopManagerString;
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
            accessibleContext = new AccessibleJDesktopPane();
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
    protected class AccessibleJDesktopPane extends AccessibleJComponent {

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
         * object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.DESKTOP_PANE;
        }
    }
}

