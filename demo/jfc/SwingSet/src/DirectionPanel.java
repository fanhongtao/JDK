/*
 * @(#)DirectionPanel.java	1.9 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;


/**
 * @version 1.6 08/26/98
 * @author Jeff Dinkins
 * @author Brian Beck
 */ 

public class DirectionPanel extends JPanel {

    private ButtonGroup group;

    public DirectionPanel(boolean enable, String selection, ActionListener l) {
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	setAlignmentY(TOP_ALIGNMENT);
	setAlignmentX(LEFT_ALIGNMENT);

	Box firstThree = Box.createHorizontalBox();
	Box secondThree = Box.createHorizontalBox();
	Box thirdThree = Box.createHorizontalBox();

	if(!enable) {
	    selection = "None";
	}

        group = new ButtonGroup();
	DirectionButton b;
	b = (DirectionButton) firstThree.add(new DirectionButton(  tl_dot, tldn_dot, "NW", "Sets the orientation to the North-West", l, group, selection.equals("NW")));
	b.setEnabled(enable);
	b = (DirectionButton) firstThree.add(new DirectionButton(  tm_dot, tmdn_dot, "N",  "Sets the orientation to the North", l, group, selection.equals("N")));
	b.setEnabled(enable);
	b = (DirectionButton) firstThree.add(new DirectionButton(  tr_dot, trdn_dot, "NE", "Sets the orientation to the North-East", l, group, selection.equals("NE")));
	b.setEnabled(enable);
	b = (DirectionButton) secondThree.add(new DirectionButton( ml_dot, mldn_dot, "W", "Sets the orientation to the West", l, group, selection.equals("W")));
	b.setEnabled(enable);
	b = (DirectionButton) secondThree.add(new DirectionButton( c_dot,  cdn_dot,  "C", "Sets the orientation to the Center", l, group, selection.equals("C")));
	b.setEnabled(enable);
	b = (DirectionButton) secondThree.add(new DirectionButton( mr_dot, mrdn_dot, "E", "Sets the orientation to the East", l, group, selection.equals("E")));
	b.setEnabled(enable);
	b = (DirectionButton) thirdThree.add(new DirectionButton(  bl_dot, bldn_dot, "SW", "Sets the orientation to the South-West", l, group, selection.equals("SW")));
	b.setEnabled(enable);
	b = (DirectionButton) thirdThree.add(new DirectionButton(  bm_dot, bmdn_dot, "S", "Sets the orientation to the South", l, group, selection.equals("S")));
	b.setEnabled(enable);
	b = (DirectionButton) thirdThree.add(new DirectionButton(  br_dot, brdn_dot, "SE", "Sets the orientation to the South-East", l, group, selection.equals("SE")));
	b.setEnabled(enable);

	add(firstThree);
	add(secondThree);
	add(thirdThree);	
    }

    public String getSelection() {
        return group.getSelection().getActionCommand();
    }

    public void setSelection( String selection  ) {
        Enumeration e = group.getElements();
        while( e.hasMoreElements() ) {
            JRadioButton b = (JRadioButton)e.nextElement();
            if( b.getActionCommand().equals(selection) ) {
               b.setSelected(true);
            }
        }
    }
    
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
    
    public class DirectionButton extends JRadioButton {
        
        /**
         * A layout direction button
         */
        public DirectionButton(Icon icon, Icon downIcon, String direction,
                               String description, ActionListener l, 
                               ButtonGroup group, boolean selected)
        {
            super();
            this.addActionListener(l);
            setFocusPainted(false);
            setHorizontalTextPosition(CENTER);
            group.add(this);
            setIcon(icon);
            setSelectedIcon(downIcon);
            setActionCommand(direction);
            getAccessibleContext().setAccessibleName(direction);
            getAccessibleContext().setAccessibleDescription(description);
            setSelected(selected);
        }

        public boolean isFocusTraversable() {
            return false;
        }

        public void setBorder(Border b) {
        }
    }
}
