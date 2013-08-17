/*
 * @(#)HtmlPanel.java	1.16 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.accessibility.*;

import java.awt.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;

/*
 * @version 1.16 01/11/29
 * @author Jeff Dinkins
 * @author Tim Prinzing
 * @author Peter Korn (accessibility support)
 */
public class HtmlPanel extends JPanel implements HyperlinkListener {
    SwingSet swing;
    JEditorPane html;

    public HtmlPanel(SwingSet swing) {
	this.swing = swing;
	setBorder(swing.emptyBorder10);
        setLayout(new BorderLayout());
	getAccessibleContext().setAccessibleName("HTML panel");
	getAccessibleContext().setAccessibleDescription("A panel for viewing HTML documents, and following their links");
	
	try {
           URL url = null;
            String prefix = "file:" +
                   System.getProperty("user.dir") +
                   System.getProperty("file.separator");
            try {
                url = new URL(prefix + "example.html");
            } catch (java.net.MalformedURLException exc) {
                   System.err.println("Attempted to open example.html "
                                      + "with a bad URL: " + url);
                   url = null;
            }
            
            if(url != null) {
                html = new JEditorPane(url);
                html.setEditable(false);
                html.addHyperlinkListener(this);
                JScrollPane scroller = new JScrollPane();
                scroller.setBorder(swing.loweredBorder);
                JViewport vp = scroller.getViewport();
                vp.add(html);
                vp.setBackingStoreEnabled(true);
                add(scroller, BorderLayout.CENTER);
            }
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
