/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * -Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduct the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT
 * BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT
 * OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN
 * IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that Software is not designed, licensed or intended for
 * use in the design, construction, operation or maintenance of any nuclear
 * facility.
 */

/*
 * @(#)MetalworksInBox.java	1.8 03/01/23
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
  * @version 1.8 01/23/03
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


