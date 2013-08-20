/*
 * @(#)MetalworksHelp.java	1.14 05/03/25
 * 
 * Copyright (c) 2005 Sun Microsystems, Inc. All Rights Reserved.
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
 * @(#)MetalworksHelp.java	1.14 05/03/25
 */

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;
import javax.swing.text.*;
import javax.swing.event.*;

/*
 * @version 1.14 03/25/05
 * @author Steve Wilson
 */
public class MetalworksHelp extends JInternalFrame {
  
    public MetalworksHelp() {
	super("Help", true, true, true, true);

       	setFrameIcon( (Icon)UIManager.get("Tree.openIcon")); // PENDING(steve) need more general palce to get this icon
	setBounds( 200, 25, 400, 400);
	HtmlPane html = new HtmlPane();
	setContentPane(html);
    }

}


class HtmlPane extends JScrollPane implements HyperlinkListener {
    JEditorPane html;

    public HtmlPane() {
	try {
            URL url = getClass().getResource("/resources/HelpFiles/toc.html");
            html = new JEditorPane(url);
	    html.setEditable(false);
	    html.addHyperlinkListener(this);

	    JViewport vp = getViewport();
	    vp.add(html);
	} catch (MalformedURLException e) {
	    System.out.println("Malformed URL: " + e);
	} catch (IOException e) {
	    System.out.println("IOException: " + e);
	}	
    }

    /**
     * Notification of a change relative to a 
     * hyperlink.
     */
    public void hyperlinkUpdate(HyperlinkEvent e) {
	if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
	    linkActivated(e.getURL());
	}
    }

    /**
     * Follows the reference in an
     * link.  The given url is the requested reference.
     * By default this calls <a href="#setPage">setPage</a>,
     * and if an exception is thrown the original previous
     * document is restored and a beep sounded.  If an 
     * attempt was made to follow a link, but it represented
     * a malformed url, this method will be called with a
     * null argument.
     *
     * @param u the URL to follow
     */
    protected void linkActivated(URL u) {
	Cursor c = html.getCursor();
	Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
	html.setCursor(waitCursor);
	SwingUtilities.invokeLater(new PageLoader(u, c));
    }

    /**
     * temporary class that loads synchronously (although
     * later than the request so that a cursor change
     * can be done).
     */
    class PageLoader implements Runnable {
	
	PageLoader(URL u, Cursor c) {
	    url = u;
	    cursor = c;
	}

        public void run() {
	    if (url == null) {
		// restore the original cursor
		html.setCursor(cursor);

		// PENDING(prinz) remove this hack when 
		// automatic validation is activated.
		Container parent = html.getParent();
		parent.repaint();
	    } else {
		Document doc = html.getDocument();
		try {
		    html.setPage(url);
		} catch (IOException ioe) {
		    html.setDocument(doc);
		    getToolkit().beep();
		} finally {
		    // schedule the cursor to revert after
		    // the paint has happended.
		    url = null;
		    SwingUtilities.invokeLater(this);
		}
	    }
	}

	URL url;
	Cursor cursor;
    }

}
