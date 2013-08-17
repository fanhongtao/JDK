/*
 * @(#)ScrollPanePanel.java	1.7 98/08/26
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

import javax.swing.*;
import javax.accessibility.*;

import java.awt.*;
import java.awt.event.*;

/*
 * @version 1.7 08/26/98
 * @author Jeff Dinkins
 * @author Peter Korn (accessibility support)
 */
public class ScrollPanePanel extends JPanel      {

    public ScrollPanePanel()    {
        setLayout(new BorderLayout());
	add(new TigerScrollPane(), BorderLayout.CENTER);
    }

}

class TigerScrollPane extends JScrollPane {

    private JLabel makeLabel(String name, String description) {
	String filename = "images/" + name;
	ImageIcon image = SwingSet.sharedInstance().loadImageIcon(filename, description);
	return new JLabel(image);
    }

    public TigerScrollPane() {
	super();
	
	JLabel horizontalRule = makeLabel("scrollpane/header.gif", "Horizontal ruler carved out of stone");
	horizontalRule.getAccessibleContext().setAccessibleName("Horizontal rule");
	JLabel verticalRule = makeLabel("scrollpane/column.gif", "Vertical ruler carved out of stone");
	verticalRule.getAccessibleContext().setAccessibleName("Vertical rule");
	JLabel tiger = makeLabel("BigTiger.gif","A rather fierce looking tiger");
	tiger.getAccessibleContext().setAccessibleName("scrolled image");
	tiger.getAccessibleContext().setAccessibleDescription("A rather fierce looking tiger");

	JLabel cornerLL = makeLabel("scrollpane/corner.gif","Square chunk of stone (lower left)");
	cornerLL.getAccessibleContext().setAccessibleName("Lower left corner");
	cornerLL.getAccessibleContext().setAccessibleDescription("Square chunk of stone");
	JLabel cornerLR = makeLabel("scrollpane/corner.gif","Square chunk of stone (lower right)");
	cornerLR.getAccessibleContext().setAccessibleName("Lower right corner");
	cornerLR.getAccessibleContext().setAccessibleDescription("Square chunk of stone");
	JLabel cornerUL = makeLabel("scrollpane/corner.gif","Square chunk of stone (upper left)");
	cornerUL.getAccessibleContext().setAccessibleName("Upper left corner");
	cornerUL.getAccessibleContext().setAccessibleDescription("Square chunk of stone");
	JLabel cornerUR = makeLabel("scrollpane/corner.gif","Square chunk of stone (upper right)");
	cornerUR.getAccessibleContext().setAccessibleName("Upper right corner");
	cornerUR.getAccessibleContext().setAccessibleDescription("Square chunk of stone");
	    
	setViewportView(tiger);
	setRowHeaderView(verticalRule);
	setColumnHeaderView(horizontalRule);

	setCorner(LOWER_LEFT_CORNER, cornerLL);
	setCorner(LOWER_RIGHT_CORNER, cornerLR);
	setCorner(UPPER_LEFT_CORNER, cornerUL);
	setCorner(UPPER_RIGHT_CORNER, cornerUR);
    }
    
    public Dimension getMinimumSize() {
	return new Dimension(25, 25);
    }
    
    public boolean isOpaque() {
        return true;
    }
}

