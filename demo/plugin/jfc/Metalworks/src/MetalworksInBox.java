/*
 * @(#)MetalworksInBox.java	1.6 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.*;


/**
  * This is a subclass of JInternalFrame which displays a tree.
  *
  * @version 1.6 12/03/01
  * @author Steve Wilson
  */
public class MetalworksInBox extends JInternalFrame {
  
    public MetalworksInBox() {
	super("In Box", true, true, true, true);

	DefaultMutableTreeNode unread;
	DefaultMutableTreeNode personal;
	DefaultMutableTreeNode business;
	DefaultMutableTreeNode spam;	

        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Mail Boxes");

	top.add( unread = new DefaultMutableTreeNode("Unread Mail") );
	top.add( personal = new DefaultMutableTreeNode("Personal") );
	top.add( business = new DefaultMutableTreeNode("Business") );
	top.add( spam = new DefaultMutableTreeNode("Spam") );

	unread.add( new DefaultMutableTreeNode("Buy Stuff Now") );
	unread.add( new DefaultMutableTreeNode("Read Me Now") );
	unread.add( new DefaultMutableTreeNode("Hot Offer") );
	unread.add( new DefaultMutableTreeNode("Re: Re: Thank You") );
	unread.add( new DefaultMutableTreeNode("Fwd: Good Joke") );

	personal.add( new DefaultMutableTreeNode("Hi") );
	personal.add( new DefaultMutableTreeNode("Good to hear from you") );
	personal.add( new DefaultMutableTreeNode("Re: Thank You") );

	business.add( new DefaultMutableTreeNode("Thanks for your order") );
	business.add( new DefaultMutableTreeNode("Price Quote") );
	business.add( new DefaultMutableTreeNode("Here is the invoice") );
	business.add( new DefaultMutableTreeNode("Project Metal: delivered on time") );
	business.add( new DefaultMutableTreeNode("Your salary raise approved") );

	spam.add( new DefaultMutableTreeNode("Buy Now") );
	spam.add( new DefaultMutableTreeNode("Make $$$ Now") );
	spam.add( new DefaultMutableTreeNode("HOT HOT HOT") );
	spam.add( new DefaultMutableTreeNode("Buy Now") );
	spam.add( new DefaultMutableTreeNode("Don't Miss This") );
	spam.add( new DefaultMutableTreeNode("Opportunity in Precious Metals") );
	spam.add( new DefaultMutableTreeNode("Buy Now") );
	spam.add( new DefaultMutableTreeNode("Last Chance") );
	spam.add( new DefaultMutableTreeNode("Buy Now") );
	spam.add( new DefaultMutableTreeNode("Make $$$ Now") );
	spam.add( new DefaultMutableTreeNode("To Hot To Handle") );
	spam.add( new DefaultMutableTreeNode("I'm waiting for your call") );

	JTree tree = new JTree(top);
	JScrollPane treeScroller = new JScrollPane(tree);
	treeScroller.setBackground(tree.getBackground());
	setContentPane(treeScroller);
	setSize( 325, 200);
	setLocation( 75, 75);

    }

 

}


