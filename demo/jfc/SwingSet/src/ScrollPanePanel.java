/*
 * @(#)ScrollPanePanel.java	1.8 99/04/23
 *
 * Copyright (c) 1997-1999 by Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import javax.swing.*;
import javax.accessibility.*;

import java.awt.*;
import java.awt.event.*;

/*
 * @version 1.8 04/23/99
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

