/*
 * @(#)DirectionButton.java	1.6 98/08/26
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
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;


/**
 * @version 1.6 08/26/98
 * @author Jeff Dinkins
 */ 
public class DirectionButton extends JRadioButton {

    // Chester's way cool layout buttons 
    public static ImageIcon bl_dot   = SwingSet.sharedInstance().loadImageIcon("images/layout/bl.gif","bottom left layout button");
    public static ImageIcon bldn_dot = SwingSet.sharedInstance().loadImageIcon("images/layout/bldn.gif","selected bottom left layout button");
    public static ImageIcon bm_dot   = SwingSet.sharedInstance().loadImageIcon("images/layout/bm.gif","bottom middle layout button");
    public static ImageIcon bmdn_dot = SwingSet.sharedInstance().loadImageIcon("images/layout/bmdn.gif","selected bottom middle layout button");
    public static ImageIcon br_dot   = SwingSet.sharedInstance().loadImageIcon("images/layout/br.gif","bottom right layout button");
    public static ImageIcon brdn_dot = SwingSet.sharedInstance().loadImageIcon("images/layout/brdn.gif","selected bottom right layout button");
    public static ImageIcon c_dot    = SwingSet.sharedInstance().loadImageIcon("images/layout/c.gif","center layout button");
    public static ImageIcon cdn_dot  = SwingSet.sharedInstance().loadImageIcon("images/layout/cdn.gif","selected center layout button");
    public static ImageIcon ml_dot   = SwingSet.sharedInstance().loadImageIcon("images/layout/ml.gif","middle left layout button");
    public static ImageIcon mldn_dot = SwingSet.sharedInstance().loadImageIcon("images/layout/mldn.gif","selected middle left layout button");
    public static ImageIcon mr_dot   = SwingSet.sharedInstance().loadImageIcon("images/layout/mr.gif","middle right layout button");
    public static ImageIcon mrdn_dot = SwingSet.sharedInstance().loadImageIcon("images/layout/mrdn.gif","selected middle right layout button");
    public static ImageIcon tl_dot   = SwingSet.sharedInstance().loadImageIcon("images/layout/tl.gif","top left layout button");
    public static ImageIcon tldn_dot = SwingSet.sharedInstance().loadImageIcon("images/layout/tldn.gif","selected top left layout button");
    public static ImageIcon tm_dot   = SwingSet.sharedInstance().loadImageIcon("images/layout/tm.gif","top middle layout button");
    public static ImageIcon tmdn_dot = SwingSet.sharedInstance().loadImageIcon("images/layout/tmdn.gif","selected top middle layout button");
    public static ImageIcon tr_dot   = SwingSet.sharedInstance().loadImageIcon("images/layout/tr.gif","top right layout button");
    public static ImageIcon trdn_dot = SwingSet.sharedInstance().loadImageIcon("images/layout/trdn.gif","selected top right layout button");

    
    /**
     * A layout direction button
     */
    public DirectionButton(Icon icon, Icon downIcon, String direction,
			   String description, ActionListener l, 
			   ButtonGroup group, boolean selected)
    {
	super();
	setSelected(selected);
	this.addActionListener(l);
	setFocusPainted(false);
	setHorizontalTextPosition(CENTER);
	group.add(this);
	setIcon(icon);
	setSelectedIcon(downIcon);
	setActionCommand(direction);
	getAccessibleContext().setAccessibleName(direction);
	getAccessibleContext().setAccessibleDescription(description);
    }

    public boolean isFocusTraversable() {
        return false;
    }

    public void setBorder(Border b) {
    }


    public static JPanel createDirectionPanel(boolean enable, String selected, ActionListener l) {
	JPanel p = SwingSet.createVerticalPanel(false);
	p.setAlignmentY(TOP_ALIGNMENT);
	p.setAlignmentX(LEFT_ALIGNMENT);

	Box firstThree = Box.createHorizontalBox();
	Box secondThree = Box.createHorizontalBox();
	Box thirdThree = Box.createHorizontalBox();

	if(!enable) {
	    selected = "None";
	}

	ButtonGroup group = new ButtonGroup();
	DirectionButton b;
	b = (DirectionButton) firstThree.add(new DirectionButton(  tl_dot, tldn_dot, "NW", "Sets the orientation to the North-West", l, group, selected.equals("NW")));
	b.setEnabled(enable);
	b = (DirectionButton) firstThree.add(new DirectionButton(  tm_dot, tmdn_dot, "N",  "Sets the orientation to the North", l, group, selected.equals("N")));
	b.setEnabled(enable);
	b = (DirectionButton) firstThree.add(new DirectionButton(  tr_dot, trdn_dot, "NE", "Sets the orientation to the North-East", l, group, selected.equals("NE")));
	b.setEnabled(enable);
	b = (DirectionButton) secondThree.add(new DirectionButton( ml_dot, mldn_dot, "W", "Sets the orientation to the West", l, group, selected.equals("W")));
	b.setEnabled(enable);
	b = (DirectionButton) secondThree.add(new DirectionButton( c_dot,  cdn_dot,  "C", "Sets the orientation to the Center", l, group, selected.equals("C")));
	b.setEnabled(enable);
	b = (DirectionButton) secondThree.add(new DirectionButton( mr_dot, mrdn_dot, "E", "Sets the orientation to the East", l, group, selected.equals("E")));
	b.setEnabled(enable);
	b = (DirectionButton) thirdThree.add(new DirectionButton(  bl_dot, bldn_dot, "SW", "Sets the orientation to the South-West", l, group, selected.equals("SW")));
	b.setEnabled(enable);
	b = (DirectionButton) thirdThree.add(new DirectionButton(  bm_dot, bmdn_dot, "S", "Sets the orientation to the South", l, group, selected.equals("S")));
	b.setEnabled(enable);
	b = (DirectionButton) thirdThree.add(new DirectionButton(  br_dot, brdn_dot, "SE", "Sets the orientation to the South-East", l, group, selected.equals("SE")));
	b.setEnabled(enable);

	p.add(firstThree);
	p.add(secondThree);
	p.add(thirdThree);
	
	return p;
    }
}
