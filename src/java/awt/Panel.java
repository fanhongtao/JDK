/*
 * @(#)Panel.java	1.15 97/01/27
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
 */
package java.awt;

import java.awt.peer.PanelPeer;

/**
 * A Panel Container class. This produces a generic container.
 *
 * @version 	1.15, 01/27/97
 * @author 	Sami Shaio
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
     * Creates a new panel. The default layout for all panels is
     * FlowLayout.
     */
    public Panel() {
	this(panelLayout);
    }

    /**
     * Creates a new panel with the specified layout manager.
     * @param layout the layout manager for this panel
     */
    public Panel(LayoutManager layout) {
        this.name = base + nameCounter++;
	setLayout(layout);
    }

    /**
     * Creates the Panel's peer.  The peer allows you to modify the
     * appearance of the panel without changing its functionality.
     */

    public void addNotify() {
	peer = getToolkit().createPanel(this);
	super.addNotify();
    }

}
