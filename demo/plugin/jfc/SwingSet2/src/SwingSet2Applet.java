/*
 * @(#)SwingSet2Applet.java	1.5 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.colorchooser.*;
import javax.swing.filechooser.*;
import javax.accessibility.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.io.*;
import java.applet.*;
import java.net.*;

/**
 * 
 *
 * @version 1.5 12/03/01
 * @author Jeff Dinkins
 */

public class SwingSet2Applet extends JApplet {
    public void init() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new SwingSet2(this), BorderLayout.CENTER);
    }

    public URL getURL(String filename) {
        URL codeBase = this.getCodeBase();
        URL url = null;
	
        try {
            url = new URL(codeBase, filename);
	    System.out.println(url);
        } catch (java.net.MalformedURLException e) {
            System.out.println("Error: badly specified URL");
            return null;
        }

        return url;
    }


}
