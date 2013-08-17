/*
 * @(#)Panel.java	1.20 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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
 * @version 	1.20, 07/01/98
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
