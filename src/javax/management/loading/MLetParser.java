/*
 * @(#)MLetParser.java	1.26 04/04/16
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management.loading;


// java import
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.io.Reader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

import com.sun.jmx.trace.Trace;


/**
 * This class is used for parsing URLs. 
 *
 * @since 1.5
 */
class MLetParser {

/*
  * ------------------------------------------
  *   PRIVATE VARIABLES
  * ------------------------------------------
  */
    
    /**
     * The current character
     */
    private int c;

    /**
     * Tag to parse.
     */
    private static String tag = "mlet";
    
    /**
     * The name of this class to be used for trace messages
     */
    private String dbgTag = "MLetParser";
    
    
  /*
  * ------------------------------------------
  *   CONSTRUCTORS
  * ------------------------------------------
  */

    /**
     * Create an MLet parser object
     */
    public MLetParser() {
    }
    
    /*
     * ------------------------------------------
     *   PUBLIC METHODS
     * ------------------------------------------
     */
    
    /**
     * Scan spaces.
     */
    public void skipSpace(Reader in) throws IOException {
	while ((c >= 0) && ((c == ' ') || (c == '\t') || (c == '\n') || (c == '\r'))) {
	    c = in.read();
	}
    }
    
    /**
     * Scan identifier
     */
    public String scanIdentifier(Reader in) throws IOException {
	StringBuffer buf = new StringBuffer();
	while (true) {
	    if (((c >= 'a') && (c <= 'z')) ||
		((c >= 'A') && (c <= 'Z')) ||
		((c >= '0') && (c <= '9')) || (c == '_')) {
		buf.append((char)c);
		c = in.read();
	    } else {
		return buf.toString();
	    }
	}
    }
    
    /**
     * Scan tag
     */
    public Hashtable scanTag(Reader in) throws IOException {
	Hashtable atts = new Hashtable();
	skipSpace(in);
	while (c >= 0 && c != '>') {
	    String att = scanIdentifier(in);
	    String val = "";
	    skipSpace(in);
	    if (c == '=') {
		int quote = -1;
		c = in.read();
		skipSpace(in);
		if ((c == '\'') || (c == '\"')) {
		    quote = c;
		    c = in.read();
		}
		StringBuffer buf = new StringBuffer();
		while ((c > 0) &&
		       (((quote < 0) && (c != ' ') && (c != '\t') &&
			 (c != '\n') && (c != '\r') && (c != '>'))
			|| ((quote >= 0) && (c != quote)))) {
		    buf.append((char)c);
		    c = in.read();
		}
		if (c == quote) {
		    c = in.read();
		}
		skipSpace(in);
		val = buf.toString();
	    }
	    atts.put(att.toLowerCase(), val);
	    skipSpace(in);
	}
	return atts;
    }
    
    /**
     * Scan an html file for <mlet> tags
     */
    public Vector parse(URL url) throws IOException {
	String mth = "parse";
	// Warning Messages    
	String requiresNameWarning = "<param name=... value=...> tag requires name parameter.";
	String paramOutsideWarning = "<param> tag outside <mlet> ... </mlet>.";
	String requiresCodeWarning = "<mlet> tag requires either code or object parameter.";
	String requiresJarsWarning = "<mlet> tag requires archive parameter.";
	
	URLConnection conn;
	
	conn = url.openConnection();
	Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
	
	// The original URL may have been redirected - this
	// sets it to whatever URL/codebase we ended up getting
	//
	url = conn.getURL();
	
	Vector mlets = new Vector();
	Hashtable atts = null;  
	
	Vector types = new Vector();
	Vector values = new Vector();
	
	// debug("parse","*** Parsing " + url );
	while(true) {
	    c = in.read();
	    if (c == -1)
		break;
	    if (c == '<') {
		c = in.read();
		if (c == '/') {
		    c = in.read();
		    String nm = scanIdentifier(in);
		    if (nm.equalsIgnoreCase(tag)) {
			if (atts != null) {
			    // Constructor parameters
			    if ((types.size() == values.size()) && ((!types.isEmpty()) && (!values.isEmpty()))) {
				atts.put("types", types.clone());
				atts.put("values", values.clone());
			    }
			    mlets.addElement(new MLetContent(url, atts));
			}
			atts = null;
			types.removeAllElements();
			values.removeAllElements();
		    }
		} else {
		    String nm = scanIdentifier(in);
		    if (nm.equalsIgnoreCase("arg")) {
			Hashtable t = scanTag(in);
			String att = (String) t.get("type");
			if (att == null) {
			    if (isTraceOn()) {
				trace(mth, requiresNameWarning);
			    }
			    throw new IOException(requiresNameWarning);
			} else {
			    if (atts != null) {
				types.addElement(att);
			    } else {
				if (isTraceOn()) {
				    trace(mth, paramOutsideWarning);
				}
				throw new IOException(paramOutsideWarning);
			    }
			}			  
			String val = (String) t.get("value");
			if (val == null) {
			    if (isTraceOn()) {
				trace(mth, requiresNameWarning);
			    }
			    throw new IOException(requiresNameWarning);
			} else {
			    if (atts != null) {
				values.addElement(val);
			    } else {
				if (isTraceOn()) {
				    trace(mth, paramOutsideWarning);
				}
				throw new IOException(paramOutsideWarning);
			    }
			}
		    }
		    else {
			if (nm.equalsIgnoreCase(tag)) {
			    atts = scanTag(in);
			    if (atts.get("code") == null && atts.get("object") == null) {
				if (isTraceOn()) {
				    trace(mth, requiresCodeWarning);
				}
				atts = null;
				throw new IOException(requiresCodeWarning);
			    }
			    if (atts.get("archive") == null) {
				if (isTraceOn()) {
				    trace(mth, requiresJarsWarning);
				}
				atts = null;
				throw new IOException(requiresJarsWarning);
			    }
			}
		    }
		}
	    }
	}  
	in.close();
	return mlets;
    }

    /**
     * Parse the document pointed by the URL urlname
     */
    public Vector parseURL(String urlname) throws IOException {
	// Parse the document
	//
	URL url = null;
	if (urlname.indexOf(':') <= 1) {
	    String userDir = System.getProperty("user.dir");
	    String prot;
	    if (userDir.charAt(0) == '/' ||
		userDir.charAt(0) == File.separatorChar) {
		prot = "file:";
	    } else {
		prot = "file:/";
	    }
	    url =
		new URL(prot + userDir.replace(File.separatorChar, '/') + "/");
	    url = new URL(url, urlname);
	} else {
	    url = new URL(urlname);
	}
	// Return list of parsed MLets
	//
	return parse(url);
    }

    /*
     * ------------------------------------------
     *   PRIVATE METHODS
     * ------------------------------------------
     */
    
    // TRACES & DEBUG
    //---------------
    
    private boolean isTraceOn() {
        return Trace.isSelected(Trace.LEVEL_TRACE, Trace.INFO_MLET);
    }

    private void trace(String clz, String func, String info) {
        Trace.send(Trace.LEVEL_TRACE, Trace.INFO_MLET, clz, func, info);
    }

    private void trace(String func, String info) {
        trace(dbgTag, func, info);
    }

    private boolean isDebugOn() {
        return Trace.isSelected(Trace.LEVEL_DEBUG, Trace.INFO_MLET);
    }

    private void debug(String clz, String func, String info) {
        Trace.send(Trace.LEVEL_DEBUG, Trace.INFO_MLET, clz, func, info);
    }

    private void debug(String func, String info) {
        debug(dbgTag, func, info);
    }

}
