/*
 * @(#)Panel.java	1.2 00/01/12
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
 * @version 	1.23, 08/25/98
 * @author 	Sami Shaio
 * @see     java.awt.FlowLayout
 * @since   JDK1.0
 */
public class Panel extends Container {
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
     */
    public Panel() {
	this(new FlowLayout());
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
        synchronized (getClass()) {
	    return base + nameCounter++;
	}
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
