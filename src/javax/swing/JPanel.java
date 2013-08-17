/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

import java.awt.*;

import javax.swing.plaf.*;
import javax.accessibility.*;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * JPanel is a generic lightweight container.
 * For examples and task-oriented documentation for JPanel, see
 * <a
 href="http://java.sun.com/docs/books/tutorial/uiswing/components/panel.html">How to Use Panels</a>,
 * a section in <em>The Java Tutorial</em>.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @beaninfo
 * description: A generic lightweight container.
 * 
 * @version 1.38 02/06/02
 * @author Arnaud Weber
 * @author Steve Wilson
 */
public class JPanel extends JComponent implements Accessible
{
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "PanelUI";

    private static final FlowLayout defaultLayout = new FlowLayout();
    
    /**
     * Creates a new JPanel with the specified layout manager and buffering
     * strategy.
     *
     * @param layout  the LayoutManager to use
     * @param isDoubleBuffered  a boolean, true for double-buffering, which
     *        uses additional memory space to achieve fast, flicker-free 
     *        updates
     */
    public JPanel(LayoutManager layout, boolean isDoubleBuffered) {
        setLayout(layout);
        setDoubleBuffered(isDoubleBuffered);
        setOpaque(true);
        updateUI();
    }

    /**
     * Create a new buffered JPanel with the specified layout manager
     *
     * @param layout  the LayoutManager to use
     */
    public JPanel(LayoutManager layout) {
        this(layout, true);
    }

    /**
     * Create a new JPanel with FlowLayout and the specified buffering
     * strategy. If <code>isDoubleBuffered</code> is true, the JPanel 
     * will use a double buffer.
     *
     * @param layout  the LayoutManager to use
     * @param isDoubleBuffered  a boolean, true for double-buffering, which
     *        uses additional memory space to achieve fast, flicker-free 
     *        updates
     */
    public JPanel(boolean isDoubleBuffered) {
        this(defaultLayout, isDoubleBuffered);
    }

    /**
     * Create a new JPanel with a double buffer and a flow layout
     */
    public JPanel() {
        this(defaultLayout, true);
    }

    /**
     * Notification from the UIFactory that the L&F
     * has changed. 
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((PanelUI)UIManager.getUI(this));
    }

    /**
     * Returns a string that specifies the name of the L&F class
     * that renders this component.
     *
     * @return "PanelUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     * @beaninfo
     *        expert: true
     *   description: A string that specifies the name of the L&F class.
     */
    public String getUIClassID() {
        return uiClassID;
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
     * Returns a string representation of this JPanel. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this JPanel.
     */
    protected String paramString() {
        String defaultLayoutString = (defaultLayout != null ?
				      defaultLayout.toString() : "");

	return super.paramString() +
	",defaultLayout=" + defaultLayoutString;
    }

/////////////////
// Accessibility support
////////////////

    /**
     * Gets the AccessibleContext associated with this JPanel. 
     * For JPanels, the AccessibleContext takes the form of an 
     * AccessibleJPanel. 
     * A new AccessibleJPanel instance is created if necessary.
     *
     * @return an AccessibleJPanel that serves as the 
     *         AccessibleContext of this JPanel
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJPanel();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the 
     * <code>JPanel</code> class.  It provides an implementation of the 
     * Java Accessibility API appropriate to panel user-interface 
     * elements.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected class AccessibleJPanel extends AccessibleJComponent {

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
         * object
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PANEL;
        }
    }
}

