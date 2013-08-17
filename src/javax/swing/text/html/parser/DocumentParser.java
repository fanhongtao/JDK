/*
 * @(#)DocumentParser.java	1.13 98/08/26
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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
 * @version 	1.13 08/26/98
 * @author      Sunita Mani
 */
public class DocumentParser extends javax.swing.text.html.parser.Parser {

    private int inbody;
    private int intitle;
    private int inhead;
    private boolean seentitle;
    private HTMLEditorKit.ParserCallback callback = null;
    private boolean ignoreCharSet = false;
    private static final boolean debugFlag = false;

    public DocumentParser(DTD dtd) {
	super(dtd);
    }
 
    public void parse(Reader in,  HTMLEditorKit.ParserCallback callback, boolean ignoreCharSet) throws IOException {
	this.ignoreCharSet = ignoreCharSet;
	this.callback = callback;
	parse(in);
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
	} else if (inbody != 0 || elem == dtd.meta || elem == dtd.base || elem == dtd.isindex) {
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
	    if (inbody != 0 || ((intitle != 0) && !seentitle)) {
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
