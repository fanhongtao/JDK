/*
 * @(#)DocumentParser.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing.text.html.parser;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.ChangedCharSetException;

import java.util.*;
import java.io.*;
import java.net.*;

import sun.io.*;

/**
 * A Parser for HTML Documents. Read an InputStream of HTML and
 * invoke the appropriate methods in the ParserCallback class.
 *
 * @version 	1.15 11/06/98
 * @author      Sunita Mani
 */
public class DocumentParser extends javax.swing.text.html.parser.Parser {

    private int inbody;
    private int intitle;
    private int inhead;
    private int instyle;
    private boolean seentitle;
    private HTMLEditorKit.ParserCallback callback = null;
    private boolean ignoreCharSet = false;
    private static final boolean debugFlag = false;

    private static HTML.UnknownTag EndOfLineTag;

    static {
	EndOfLineTag = new HTML.UnknownTag("__EndOfLineTag__");
    }


    public DocumentParser(DTD dtd) {
	super(dtd);
    }
 
    public void parse(Reader in,  HTMLEditorKit.ParserCallback callback, boolean ignoreCharSet) throws IOException {
	this.ignoreCharSet = ignoreCharSet;
	this.callback = callback;
	parse(in);

	// This is a temporary way to notify the callback of the end of line
	// string.
	// In the future there will be a public way to determine this.
	SimpleAttributeSet attr = new SimpleAttributeSet();
	attr.addAttribute("__EndOfLineString__", getEndOfLineString());
	callback.handleSimpleTag(EndOfLineTag, attr, getCurrentPos());
    }

    /**
     * Handle Start Tag.
     */
    protected void handleStartTag(TagElement tag) {

	Element elem = tag.getElement();
	if (elem == dtd.body) {
	    inbody++;
	} else if (elem == dtd.html) {
	} else if (elem == dtd.head) {
	    inhead++;
	} else if (elem == dtd.title) {
	    intitle++;
	} else if (elem == dtd.style) {
	    instyle++;
	}	
	if (debugFlag) {
	    if (tag.fictional()) {
		debug("Start Tag: " + tag.getHTMLTag() + " pos: " + getCurrentPos());
	    } else {
		debug("Start Tag: " + tag.getHTMLTag() + " attributes: " + 
		      getAttributes() + " pos: " + getCurrentPos());
	    }
	}
	if (tag.fictional()) {
	    callback.handleStartTag(tag.getHTMLTag(), new SimpleAttributeSet(), getCurrentPos());
	} else {
	    callback.handleStartTag(tag.getHTMLTag(), getAttributes(), getCurrentPos());
	    flushAttributes();
	}
    }


    protected void handleComment(char text[]) {
	if (debugFlag) {
	    debug("comment: ->" + new String(text) + "<-"
		  + " pos: " + getCurrentPos());
	}
	callback.handleComment(text, getCurrentPos());
    }

    /**
     * Handle Empty Tag.
     */
    protected void handleEmptyTag(TagElement tag) throws ChangedCharSetException {

	Element elem = tag.getElement();
	if (elem == dtd.meta && !ignoreCharSet) {
	    SimpleAttributeSet atts = getAttributes();
	    if (atts != null) {
		String content = (String)atts.getAttribute(HTML.Attribute.CONTENT);
		if (content != null) {
		    if ("content-type".equalsIgnoreCase((String)atts.getAttribute(HTML.Attribute.HTTPEQUIV))) {
			throw new ChangedCharSetException(content, false);
		    } else if ("charset" .equalsIgnoreCase((String)atts.getAttribute(HTML.Attribute.HTTPEQUIV))) {
			throw new ChangedCharSetException(content, true);
		    }
		}
	    }
	}
	if (inbody != 0 || elem == dtd.meta || elem == dtd.base || elem == dtd.isindex || elem == dtd.style || elem == dtd.link) {
	    if (debugFlag) {
		if (tag.fictional()) {
		    debug("Empty Tag: " + tag.getHTMLTag() + " pos: " + getCurrentPos());
		} else {
		    debug("Empty Tag: " + tag.getHTMLTag() + " attributes: " 
			  + getAttributes() + " pos: " + getCurrentPos());
		}
	    }
	    if (tag.fictional()) {
		callback.handleSimpleTag(tag.getHTMLTag(), new SimpleAttributeSet(), getCurrentPos());
	    } else {
		callback.handleSimpleTag(tag.getHTMLTag(), getAttributes(), getCurrentPos());
		flushAttributes();
	    }
	}
    }

    /**
     * Handle End Tag.
     */
    protected void handleEndTag(TagElement tag) {
	Element elem = tag.getElement();
	if (elem == dtd.body) {
	    inbody--;
	} else if (elem == dtd.title) {
	    intitle--;
	    seentitle = true;
	} else if (elem == dtd.head) {
            inhead--;
	} else if (elem == dtd.style) {
            instyle--;
	}
	if (debugFlag) {
	    debug("End Tag: " + tag.getHTMLTag() + " pos: " + getCurrentPos());
	}
	callback.handleEndTag(tag.getHTMLTag(), getCurrentPos());

    }

    /**
     * Handle Text.
     */
    protected void handleText(char data[]) {
	if (data != null) {
	    if (inbody != 0 || ((instyle != 0) ||
				((intitle != 0) && !seentitle))) {
		if (debugFlag) {
		    debug("text:  ->" + new String(data) + "<-" + " pos: " + getCurrentPos());
		}
		callback.handleText(data, getCurrentPos());
	    }
	}
    }

    /*
     * Error handling.
     */
    protected void handleError(int ln, String errorMsg) {
	if (debugFlag) {
	    debug("Error: ->" + errorMsg + "<-" + " pos: " + getCurrentPos());
	}
	/* PENDING: need to improve the error string. */
	callback.handleError(errorMsg, getCurrentPos());
    }


    /*
     * debug messages
     */
    private void debug(String msg) {
	System.out.println(msg);
    }
}
