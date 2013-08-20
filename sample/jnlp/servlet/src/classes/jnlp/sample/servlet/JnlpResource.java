/*
 * @(#)JnlpResource.java	1.6 03/01/23
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package jnlp.sample.servlet;
import javax.servlet.ServletContext;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.*;

/** 
 *  A JnlpResource encapsulate the information about a resource that is
 *  needed to process a JNLP Download Request.
 *
 *  The pattern matching arguments are: name, version-id, os, arch, and locale.
 *
 *  The outgoing arguments are:
 *     - path to resource in (WAR File)
 *     - product version-id (Version-id to return or null. Typically same as version-id above)
 *     - mime-type for content
 *     - lastModified date of WAR file resource
 * 
 */
public class JnlpResource {       
    private static final String JNLP_MIME_TYPE      = "application/x-java-jnlp-file";
    private static final String JAR_MIME_TYPE       = "application/x-java-archive";

    private static final String JAR_MIME_TYPE_NEW   = "application/java-archive";
    
    // Default extension for the JNLP file
    private static final String JNLP_EXTENSION      = ".jnlp";
    private static final String JAR_EXTENSION       = ".jar";
    
    private static String _jnlpExtension = JNLP_EXTENSION;
    private static String _jarExtension = JAR_EXTENSION;
    
    public static void setDefaultExtensions(String jnlpExtension, String jarExtension) {
	if (jnlpExtension != null && jnlpExtension.length() > 0) {
	    if (!jnlpExtension.startsWith(".")) jnlpExtension = "." + jnlpExtension;
	    _jnlpExtension = jnlpExtension;	
	}
	if (jarExtension != null && jarExtension.length() > 0) {
	    if (!jarExtension .startsWith(".")) jarExtension  = "." + jarExtension ;
	    _jarExtension = jarExtension;	
	}
    }   
    
    /* Pattern matching arguments */
    private String _name;	  // Name of resource with path (this is the same as path for non-version based)
    private String _versionId;    // Version-id for resource, or null if none
    private String[] _osList;     // List of OSes for which resource should be returned
    private String[] _archList;   // List of architectures for which the resource should be returned
    private String[] _localeList; // List of locales for which the resource should be returned
    /* Information used for reply */
    private String _path;            // Path to resource in WAR file (unique)
    private URL    _resource;        // URL to resource in WAR file (unique - same as above really)
    private long   _lastModified;    // Last modified in WAR file
    private String _mimeType;        // Mime-type for resource
    private String _returnVersionId; // Version Id to return
    private String _encoding;        // Accept encoding
    
    public JnlpResource(ServletContext context, String path) { 
	this(context, null, null, null, null, null, path, null); 
    }

    public JnlpResource(ServletContext context, 
			String name, 
			String versionId, 
			String[] osList, 
			String[] archList, 
			String[] localeList,
			String path,
			String returnVersionId) {
	this(context, name, versionId, osList, archList, localeList, path,
	     returnVersionId, null);
    }
    
    public JnlpResource(ServletContext context, 
			String name, 
			String versionId, 
			String[] osList, 
			String[] archList, 
			String[] localeList,
			String path,
			String returnVersionId,
			String encoding) {	
	// Matching arguments
	_encoding = encoding;
	_name = name;
	_versionId = versionId;	
	_osList = osList;
	_archList = archList;
	_localeList = localeList;

	_returnVersionId = returnVersionId;

	/* Check for existance and get last modified timestamp */
	try {
	    String orig_path = path.trim();
	    String search_path = orig_path;
	    _resource = context.getResource(orig_path);	
	    _mimeType = getMimeType(context, orig_path);
	    if (_resource != null) {
	
		boolean found = false;
		// pack200 compression
		if (encoding != null && _mimeType != null &&
		    (_mimeType.compareTo(JAR_MIME_TYPE) == 0 || _mimeType.compareTo(JAR_MIME_TYPE_NEW) == 0) &&
		    encoding.toLowerCase().indexOf(DownloadResponse.PACK200_GZIP_ENCODING) > -1){
		    search_path = orig_path + ".pack.gz";
		    _resource = context.getResource(search_path);
		    // Get last modified time
		    if (_resource != null) {
			_lastModified = getLastModified(context, _resource, search_path);
			if (_lastModified != 0) {
			    _path = search_path;
			    found = true;
			} else {
			    _resource = null;
			}
		    }
		}

		// gzip compression
		if (found == false && encoding != null && _mimeType != null &&
		    (_mimeType.compareTo(JAR_MIME_TYPE) == 0 || _mimeType.compareTo(JAR_MIME_TYPE_NEW) == 0) &&
		    encoding.toLowerCase().indexOf(DownloadResponse.GZIP_ENCODING) > -1){
		    search_path = orig_path + ".gz";
		    _resource = context.getResource(search_path);
		    // Get last modified time
		    if (_resource != null) {
			_lastModified = getLastModified(context, _resource, search_path);
			if (_lastModified != 0) {
			    _path = search_path;
			    found = true;
			} else {
			    _resource = null;
			}
		    }
		}
		
		if (found == false) {
		    // no compression
		    search_path = orig_path;
		    _resource = context.getResource(search_path);
		    // Get last modified time
		    if (_resource != null) {
			_lastModified = getLastModified(context, _resource, search_path);
			if (_lastModified != 0) {
			    _path = search_path;
			    found = true;
			} else {
			    _resource = null;
			}
		    }
		}		
	    }
	} catch(IOException ioe) {
	    _resource = null;
	}				    			    
    }

    long getLastModified(ServletContext context, URL resource, String path) {
	long lastModified = 0;
	URLConnection conn;
	try {
	    // Get last modified time
	    conn = resource.openConnection();
	    lastModified = conn.getLastModified();
	} catch (Exception e) {
	    // do nothing
	}
	
	if (lastModified == 0) {
	    // Arguably a bug in the JRE will not set the lastModified for file URLs, and
	    // always return 0. This is a workaround for that problem.
	    String filepath = context.getRealPath(path); 
	    if (filepath != null) {
		File f = new File(filepath);	    
		if (f.exists()) {
		    lastModified = f.lastModified();
		}
	    }
	}
	return lastModified;
    }

    /* Get resource specific attributes */
    public String getPath() { return _path; }
    public URL getResource() { return _resource; }             
    public String getMimeType() { return _mimeType; }    
    public long getLastModified() { return _lastModified; }    
    public boolean exists() { return _resource != null; }    
    public boolean isJnlpFile() { return _path.endsWith(_jnlpExtension); }
    public boolean isJarFile() { return _path.endsWith(_jarExtension); }
    
    /* Get JNLP version specific attributes */
    public String getName() { return _name; }	            
    public String getVersionId() { return _versionId; }	            
    public String[] getOSList()  { return _osList; }
    public String[] getArchList()  { return _archList; }
    public String[] getLocaleList()  { return _localeList; }
    public String   getReturnVersionId() { return _returnVersionId; }

    private String getMimeType(ServletContext context, String path) {	
	String mimeType = context.getMimeType(path);
	if (mimeType != null) return mimeType;	
	if (path.endsWith(_jnlpExtension)) return JNLP_MIME_TYPE;
	if (path.endsWith(_jarExtension)) return JAR_MIME_TYPE;
	return "application/unknown";
    }        
    
    /** Print info about an entry */
    public String toString() { 
	return "JnlpResource[WAR Path: " + _path + 
	    showEntry(" versionId=",_versionId) +
	    showEntry(" name=", _name) +
	    " lastModified=" + new Date(_lastModified) +
	    showEntry(" osList=", _osList) +
	    showEntry(" archList=", _archList) +
	    showEntry(" localeList=", _localeList) + "]" +
	    showEntry(" returnVersionId=", _returnVersionId) + "]";
	
    }
    
    private String showEntry(String msg, String value) {
	if (value == null) return "";
	return msg + value;		
    }
    
    private String showEntry(String msg, String[] value) {
	if (value == null) return "";
	return msg + java.util.Arrays.asList(value).toString();	
    }        
}




