/*
 * @(#)HtmlDemo.java	1.4 99/07/23
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
import javax.swing.text.*;
import javax.swing.text.html.*;
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
 * Html Demo
 *
 * @version 1.4 99/07/23
 * @author Jeff Dinkins
 */
public class HtmlDemo extends DemoModule {

    JEditorPane html;
    
    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
	HtmlDemo demo = new HtmlDemo(null);
	demo.mainImpl();
    }
    
    /**
     * HtmlDemo Constructor
     */
    public HtmlDemo(SwingSet2 swingset) {
        // Set the title for this demo, and an icon used to represent this
        // demo inside the SwingSet2 app.
        super(swingset, "HtmlDemo", "toolbar/JEditorPane.gif");
	
        try {
	    URL url = null;
	    // System.getProperty("user.dir") +
	    // System.getProperty("file.separator");
	    String path = null;
	    try {
		path = "/resources/index.html";
		url = getClass().getResource(path);
            } catch (Exception e) {
		System.err.println("Failed to open " + path);
		url = null;
            }
	    
            if(url != null) {
                html = new JEditorPane(url);
                html.setEditable(false);
                html.addHyperlinkListener(createHyperLinkListener());
		
		JScrollPane scroller = new JScrollPane();
		JViewport vp = scroller.getViewport();
		vp.add(html);
                getDemoPanel().add(scroller, BorderLayout.CENTER);
            }
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL: " + e);
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
    }

    public HyperlinkListener createHyperLinkListener() {
	return new HyperlinkListener() {
	    public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
		    if (e instanceof HTMLFrameHyperlinkEvent) {
			((HTMLDocument)html.getDocument()).processHTMLFrameHyperlinkEvent(
			    (HTMLFrameHyperlinkEvent)e);
		    } else {
			try {
			    html.setPage(e.getURL());
			} catch (IOException ioe) {
			    System.out.println("IOE: " + ioe);
			}
		    }
		}
	    }
	};
    }
    
}
