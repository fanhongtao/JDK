/*
 * @(#)JDesktopPane.java	1.46 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing;

import java.util.Vector;
import javax.swing.plaf.*;
import javax.accessibility.*;

import java.awt.Component;
import java.awt.Container;
import java.awt.DefaultFocusTraversalPolicy;
import java.awt.FocusTraversalPolicy;
import java.awt.Window;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

/**
 * A container used to create a multiple-document interface or a virtual desktop. 
 * You create <code>JInternalFrame</code> objects and add them to the 
 * <code>JDesktopPane</code>. <code>JDesktopPane</code> extends
 * <code>JLayeredPane</code> to manage the potentially overlapping internal
 * frames. It also maintains a reference to an instance of
 * <code>DesktopManager</code> that is set by the UI 
 * class for the current look and feel (L&F).  Note that <code>JDesktopPane</code>
 * does not support borders.
 * <p>
 * This class is normally used as the parent of <code>JInternalFrames</code>
 * to provide a pluggable <code>DesktopManager</code> object to the
 * <code>JInternalFrames</code>. The <code>installUI</code> of the 
 * L&F specific implementation is responsible for setting the
 * <code>desktopManager</code> variable appropriately.
 * When the parent of a <code>JInternalFrame</code> is a <code>JDesktopPane</code>, 
 * it should delegate most of its behavior to the <code>desktopManager</code>
 * (closing, resizing, etc).
 * <p>
 * For the keyboard keys used by this component in the standard look and
 * feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JDesktopPane"><code>JDesktopPane</code> key assignments</a>.
 * For further documentation and examples see
 * <a href="http://java.sun.com/docs/books/tutorial/uiswing/components/internalframe.html">How to Use Internal Frames</a>,
 * a section in <em>The Java Tutorial</em>.
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
 * @see JInternalFrame
 * @see JInternalFrame.JDesktopIcon
 * @see DesktopManager
 *
 * @version 1.46 01/23/03
 * @author David Kloba
 */
public class JDesktopPane extends JLayeredPane implements Accessible
{
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "DesktopPaneUI";

    transient DesktopManager desktopManager;

    private transient JInternalFrame selectedFrame = null;

    /**
      * Indicates that the entire contents of the item being dragged
      * should appear inside the desktop pane.
      *
      * @see #OUTLINE_DRAG_MODE
      * @see #setDragMode
      */
    public static int LIVE_DRAG_MODE = 0;

    /**
      * Indicates that an outline only of the item being dragged
      * should appear inside the desktop pane.
      *
      * @see #LIVE_DRAG_MODE
      * @see #setDragMode
      */
    public static int OUTLINE_DRAG_MODE = 1;

    private int dragMode = LIVE_DRAG_MODE;

    /** 
     * Creates a new <code>JDesktopPane</code>.
     */
    public JDesktopPane() {
        setFocusCycleRoot(true);

        setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
            public Component getDefaultComponent(Container c) {
                JInternalFrame jifArray[] = getAllFrames();
                Component comp = null;
                for (int i = 0; i < jifArray.length; i++) {
                    comp = jifArray[i].getFocusTraversalPolicy().getDefaultComponent(jifArray[i]);
                    if (comp != null) {
                        break;
                    }
                }
                return comp;
            }
        });
        updateUI();
    }

    /**
     * Returns the L&F object that renders this component.
     *
     * @return the <code>DesktopPaneUI</code> object that
     *   renders this component
     */
    public DesktopPaneUI getUI() {
        return (DesktopPaneUI)ui;
    }

    /**
     * Sets the L&F object that renders this component.
     *
     * @param ui  the DesktopPaneUI L&F object
     * @see UIDefaults#getUI
     * @beaninfo
     *        bound: true
     *       hidden: true
     *    attribute: visualUpdate true
     *  description: The UI object that implements the Component's LookAndFeel. 
     */
    public void setUI(DesktopPaneUI ui) {
        super.setUI(ui);
    }

    /** 
     * Sets the "dragging style" used by the desktop pane. 
     * You may want to change to one mode or another for
     * performance or aesthetic reasons.
     *
     * @param dragMode the style of drag to use for items in the Desktop 
     *
     * @see #LIVE_DRAG_MODE
     * @see #OUTLINE_DRAG_MODE
     *
     * @beaninfo
     *        bound: true
     *  description: Dragging style for internal frame children.
     *         enum: LIVE_DRAG_MODE JDesktopPane.LIVE_DRAG_MODE
     *               OUTLINE_DRAG_MODE JDesktopPane.OUTLINE_DRAG_MODE
     */
    public void setDragMode(int dragMode) {
       /* if (!(dragMode == LIVE_DRAG_MODE || dragMode == OUTLINE_DRAG_MODE)) {
            throw new IllegalArgumentException("Not a valid drag mode");
        }*/
        firePropertyChange("dragMode", this.dragMode, dragMode);
        this.dragMode = dragMode;
     }

    /** 
     * Gets the current "dragging style" used by the desktop pane.
     * @return either <code>Live_DRAG_MODE</code> or
     *   <code>OUTLINE_DRAG_MODE</code>
     * @see #setDragMode
     */
     public int getDragMode() {
         return dragMode;
     }

    /** 
     * Returns the <code>DesktopManger</code> that handles
     * desktop-specific UI actions.
     */
    public DesktopManager getDesktopManager() {
        return desktopManager;
    }

    /**
     * Sets the <code>DesktopManger</code> that will handle
     * desktop-specific UI actions.
     *
     * @param d the <code>DesktopManager</code> to use 
     */
    public void setDesktopManager(DesktopManager d) {
        desktopManager = d;
    }

    /**
     * Notification from the <code>UIManager</code> that the L&F has changed. 
     * Replaces the current UI object with the latest version from the 
     * <code>UIManager</code>.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((DesktopPaneUI)UIManager.getUI(this));
    }


    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return the string "DesktopPaneUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }

    /** 
     * Returns all <code>JInternalFrames</code> currently displayed in the
     * desktop. Returns iconified frames as well as expanded frames.
     *
     * @return an array of <code>JInternalFrame</code> objects
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

    /** Returns the currently active <code>JInternalFrame</code>
      * in this <code>JDesktopPane</code>, or <code>null</code>
      * if no <code>JInternalFrame</code> is currently active.
      *
      * @return the currently active <code>JInternalFrame</code> or
      *   <code>null</code>
      * @since 1.3
      */

    public JInternalFrame getSelectedFrame() {
      return selectedFrame;
    }

    /** Sets the currently active <code>JInternalFrame</code>
     *  in this <code>JDesktopPane</code>.
     *
     * @param f the internal frame that's currently selected
     * @since 1.3
     */

    public void setSelectedFrame(JInternalFrame f) {
      selectedFrame = f;
    }

    /**
     * Returns all <code>JInternalFrames</code> currently displayed in the
     * specified layer of the desktop. Returns iconified frames as well
     * expanded frames.
     *
     * @param layer  an int specifying the desktop layer
     * @return an array of <code>JInternalFrame</code> objects
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
            if(next instanceof JInternalFrame) {
                if(((JInternalFrame)next).getLayer() == layer)
                    vResults.addElement(next);
            } else if(next instanceof JInternalFrame.JDesktopIcon)  {
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
        if (getUIClassID().equals(uiClassID)) {
            byte count = JComponent.getWriteObjCounter(this);
            JComponent.setWriteObjCounter(this, --count);
            if (count == 0 && ui != null) {
                ui.installUI(this);
            }
        }
    }


    /**
     * Returns a string representation of this <code>JDesktopPane</code>.
     * This method is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this <code>JDesktopPane</code>
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
     * Gets the <code>AccessibleContext</code> associated with this
     * <code>JDesktopPane</code>. For desktop panes, the
     * <code>AccessibleContext</code> takes the form of an 
     * <code>AccessibleJDesktopPane</code>. 
     * A new <code>AccessibleJDesktopPane</code> instance is created if necessary.
     *
     * @return an <code>AccessibleJDesktopPane</code> that serves as the 
     *         <code>AccessibleContext</code> of this <code>JDesktopPane</code>
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJDesktopPane();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the 
     * <code>JDesktopPane</code> class.  It provides an implementation of the 
     * Java Accessibility API appropriate to desktop pane user-interface 
     * elements.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases. The current serialization support is
     * appropriate for short term storage or RMI between applications running
     * the same version of Swing.  As of 1.4, support for long term storage
     * of all JavaBeans<sup><font size="-2">TM</font></sup>
     * has been added to the <code>java.beans</code> package.
     * Please see {@link java.beans.XMLEncoder}.
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

