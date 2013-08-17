/*
 * @(#)Panel.java	1.21 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt;

import java.awt.peer.PanelPeer;

/**
 * <code>Panel</code> is the simplest container class. A panel   
 * provides space in which an application can attach any other 
 * component, including other panels. 
 * <p>
 * The default layout manager for a panel is the 
 * <code>FlowLayout</code> layout manager.
 *
 * @version 	1.21, 12/10/01
 * @author 	Sami Shaio
 * @see     java.awt.FlowLayout
 * @since   JDK1.0
 */
public class Panel extends Container {
    final static LayoutManager panelLayout = new FlowLayout();

    private static final String base = "panel";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = -2728009084054400034L;

    /**
     * Creates a new panel using the default layout manager. 
     * The default layout manager for all panels is the 
     * <code>FlowLayout</code> class.
     * @since       JDK1.0
     */
    public Panel() {
	this(panelLayout);
    }

    /**
     * Creates a new panel with the specified layout manager.
     * @param layout the layout manager for this panel.
     * @since JDK1.1
     */
    public Panel(LayoutManager layout) {
	setLayout(layout);
    }

    /**
     * Construct a name for this component.  Called by getName() when the
     * name is null.
     */
    String constructComponentName() {
        return base + nameCounter++;
    }

    /**
     * Creates the Panel's peer.  The peer allows you to modify the
     * appearance of the panel without changing its functionality.
     */

    public void addNotify() {
        synchronized (getTreeLock()) {
	    if (peer == null)
			peer = getToolkit().createPanel(this);
	    super.addNotify();
        }
    }

}
