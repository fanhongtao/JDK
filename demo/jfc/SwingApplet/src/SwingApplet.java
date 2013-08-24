/*
 * @(#)SwingApplet.java	1.19 05/11/17
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/*
 * @(#)SwingApplet.java	1.19 05/11/17
 */

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.applet.*;
import javax.swing.*;

/**
 * A very simple applet.
 */
public class SwingApplet extends JApplet {

    JButton button;

    public void init() {

	// Force SwingApplet to come up in the System L&F
	String laf = UIManager.getSystemLookAndFeelClassName();
	try {
	    UIManager.setLookAndFeel(laf);
	    // If you want the Cross Platform L&F instead, comment out the above line and
	    // uncomment the following:
	    // UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
	} catch (UnsupportedLookAndFeelException exc) {
	    System.err.println("Warning: UnsupportedLookAndFeel: " + laf);
	} catch (Exception exc) {
	    System.err.println("Error loading " + laf + ": " + exc);
	}

        getContentPane().setLayout(new FlowLayout());
        button = new JButton("Hello, I'm a Swing Button!");
        getContentPane().add(button);
    }

    public void stop() {
        if (button != null) {
            getContentPane().remove(button);
            button = null;
        }
    }
}


