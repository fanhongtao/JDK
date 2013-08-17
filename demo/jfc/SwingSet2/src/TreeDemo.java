/*
 * @(#)TreeDemo.java	1.3 99/10/10
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
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.accessibility.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.io.*;
import java.applet.*;
import java.net.*;

/**
 * JTree Demo
 *
 * @version 1.3 10/10/99
 * @author Jeff Dinkins
 */
public class TreeDemo extends DemoModule {

    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
	TreeDemo demo = new TreeDemo(null);
	demo.mainImpl();
    }

    /**
     * TreeDemo Constructor
     */
    public TreeDemo(SwingSet2 swingset) {
	// Set the title for this demo, and an icon used to represent this
	// demo inside the SwingSet2 app.
	super(swingset, "TreeDemo", "toolbar/JTree.gif");

	getDemoPanel().add(createTree(), BorderLayout.CENTER);
    }
 
    public JScrollPane createTree() {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(getString("TreeDemo.music"));
        DefaultMutableTreeNode catagory = null ;
	DefaultMutableTreeNode artist = null;
	DefaultMutableTreeNode record = null;

	// open tree data 
	URL url = getClass().getResource("/resources/tree.txt");

	try {
	    // convert url to buffered string
	    InputStream is = url.openStream();
	    InputStreamReader isr = new InputStreamReader(is);
	    BufferedReader reader = new BufferedReader(isr);

	    // read one line at a time, put into tree
	    String line = reader.readLine();
	    while(line != null) {
		// System.out.println("reading in: ->" + line + "<-");
		char linetype = line.charAt(0);
		switch(linetype) {
		   case 'C':
		     catagory = new DefaultMutableTreeNode(line.substring(2));
		     top.add(catagory);
		     break;
		   case 'A':
		     if(catagory != null) {
		         catagory.add(artist = new DefaultMutableTreeNode(line.substring(2)));
		     }
		     break;
		   case 'R':
		     if(artist != null) {
		         artist.add(record = new DefaultMutableTreeNode(line.substring(2)));
		     }
		     break;
		   case 'S':
		     if(record != null) {
		         record.add(new DefaultMutableTreeNode(line.substring(2)));
		     }
		     break;
		   default:
		     break;
		}
		line = reader.readLine();
	    }
	} catch (IOException e) {
	}

	JTree tree = new JTree(top) {
	    public Insets getInsets() {
		return new Insets(5,5,5,5);
	    }
	};
	return new JScrollPane(tree);
    }

}
