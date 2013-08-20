/*
 * @(#)DownloadResponse.java	1.5 03/01/23
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package jnlp.sample.servlet;
import java.io.*;
import java.util.*;
import java.net.URL;
import java.net.URLConnection;
import javax.servlet.http.HttpServletResponse;

/** 
 * A class used to encapsulate a file response, and
 * factory methods to create some common types.
 */
abstract public class DownloadResponse {    
    private static final String HEADER_LASTMOD      = "Last-Modified";    
    private static final String HEADER_JNLP_VERSION = "x-java-jnlp-version-id";
    private static final String JNLP_ERROR_MIMETYPE = "application/x-java-jnlp-error";
    
    public static final int STS_00_OK           = 0;
    public static final int ERR_10_NO_RESOURCE  = 10;
    public static final int ERR_11_NO_VERSION   = 11;
    public static final int ERR_20_UNSUP_OS     = 20;
    public static final int ERR_21_UNSUP_ARCH   = 21;
    public static final int ERR_22_UNSUP_LOCALE = 22;
    public static final int ERR_23_UNSUP_JRE    = 23;
    public static final int ERR_99_UNKNOWN      = 99;

    // HTTP Compression RFC 2616 : Standard headers
    public static final String CONTENT_ENCODING         = "content-encoding";
    // HTTP Compression RFC 2616 : Standard header for HTTP/Pack200 Compression
    public static final String GZIP_ENCODING            = "gzip";
    public static final String PACK200_GZIP_ENCODING    = "pack200-gzip";

    public DownloadResponse() { /* do nothing */ }
    
    public String toString() { return getClass().getName(); }
    
    /** Post information to an HttpResponse */
    abstract void sendRespond(HttpServletResponse response) throws IOException;
    
    /** Factory methods for error responses */
    static DownloadResponse getNotFoundResponse() { return new NotFoundResponse(); }
    static DownloadResponse getNoContentResponse() { return new NotFoundResponse(); }
    static DownloadResponse getJnlpErrorResponse(int jnlpErrorCode) { return new JnlpErrorResponse(jnlpErrorCode); }
    
    /** Factory method for file download responses */
    static DownloadResponse getFileDownloadResponse(byte[] content, String mimeType, long timestamp, String versionId) {
	return new ByteArrayFileDownloadResponse(content, mimeType, versionId, timestamp);
    }	
    
    static DownloadResponse getFileDownloadResponse(URL resource, String mimeType, long timestamp, String versionId) {
	return new ResourceFileDownloadResponse(resource, mimeType, versionId, timestamp);
    }	
    
    static DownloadResponse getFileDownloadResponse(File file, String mimeType, long timestamp, String versionId) {
	return new DiskFileDownloadResponse(file, mimeType, versionId, timestamp);
    }	
    
    //
    // Private classes implementing the various types
    //
    
    static private class NotFoundResponse extends DownloadResponse {	
	public void sendRespond(HttpServletResponse response) throws IOException {
	    response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}	       	
    }
    
    static private class NoContentResponse extends DownloadResponse {	
	public void sendRespond(HttpServletResponse response) throws IOException {
	    response.sendError(HttpServletResponse.SC_NO_CONTENT);
	}	
    }
    
    static public class JnlpErrorResponse extends DownloadResponse {
	private String _message;
	
	public JnlpErrorResponse(int jnlpErrorCode) {
	    String msg = Integer.toString(jnlpErrorCode);
	    String dsc = "No description";
	    try {
		dsc = JnlpDownloadServlet.getResourceBundle().getString("servlet.jnlp.err." + msg);
	    } catch (MissingResourceException mre) { /* ignore */}	    	    
	    _message = msg + " " + dsc;
	}
	
	public void sendRespond(HttpServletResponse response) throws IOException {
	    response.setContentType(JNLP_ERROR_MIMETYPE);
	    PrintWriter pw = response.getWriter();
	    pw.println(_message);
	};	
	
	public String toString() { return super.toString() + "[" + _message + "]"; }
    }
    
    static private abstract class FileDownloadResponse extends DownloadResponse {        
	private String _mimeType;
	private String _versionId;
	private long _lastModified;
	private String _fileName;
	
	FileDownloadResponse(String mimeType, String versionId, long lastModified) {
	    _mimeType = mimeType;
	    _versionId = versionId;
	    _lastModified = lastModified;
	    _fileName = null;
	}
	
	FileDownloadResponse(String mimeType, String versionId, long lastModified, String fileName) {
	    _mimeType = mimeType;
	    _versionId = versionId;
	    _lastModified = lastModified;
	    _fileName = fileName;
	}
	
	
	/** Information about response */
	String getMimeType()    { return _mimeType; }
	String getVersionId()   { return _versionId; }
	long getLastModified()  { return _lastModified;   }
	abstract int getContentLength() throws IOException;
	abstract InputStream getContent() throws IOException;
	
	/** Post information to an HttpResponse */
	public void sendRespond(HttpServletResponse response) throws IOException {		
	    // Set header information
	    response.setContentType(getMimeType());
	    response.setContentLength(getContentLength());
	    if (getVersionId() != null) response.setHeader(HEADER_JNLP_VERSION, getVersionId());
	    if (getLastModified() != 0) response.setDateHeader(HEADER_LASTMOD, getLastModified());			    
	    if (_fileName != null) {
	
		if (_fileName.endsWith(".pack.gz")) {
		    response.setHeader(CONTENT_ENCODING, PACK200_GZIP_ENCODING );
		} else if (_fileName.endsWith(".gz")) {
		    response.setHeader(CONTENT_ENCODING, GZIP_ENCODING );
		} else {
		    response.setHeader(CONTENT_ENCODING, null);
		}
	    }

	    // Send contents	   	
	    InputStream in = getContent();
	    OutputStream out = response.getOutputStream();
	    try {
		byte[] bytes = new byte[32 * 1024];
		int read;	
		while ((read = in.read(bytes)) != -1) {
		    out.write(bytes, 0, read);
		}
	    } finally {
		if (in != null) in.close();	    
	    }	
	}
	
	protected String getArgString() { 
	    long length = 0;
	    try {
		length = getContentLength();
	    } catch(IOException ioe) { /* ignore */ }
	    return "Mimetype=" + getMimeType() + 
		" VersionId=" + getVersionId() + 
		" Timestamp=" + new Date(getLastModified()) + 
		" Length=" + length; 
	}	
    }
    
    static private class ByteArrayFileDownloadResponse extends FileDownloadResponse {
	private byte[] _content;
	
	ByteArrayFileDownloadResponse(byte[] content, String mimeType, String versionId, long lastModified) {
	    super(mimeType, versionId, lastModified);
	    _content = content;
	}
	
	int getContentLength() { return _content.length; }
	InputStream getContent() { return new ByteArrayInputStream(_content); }	
	public String toString() { return super.toString() + "[ " + getArgString() + "]"; }		
    }
    
    static private class ResourceFileDownloadResponse extends FileDownloadResponse {
	URL _url;
	
	ResourceFileDownloadResponse(URL url, String mimeType, String versionId, long lastModified) {
	    super(mimeType, versionId, lastModified, url.toString());
	    _url= url;
	}
	
	int getContentLength() throws IOException { 
	    return _url.openConnection().getContentLength(); 
	}
	InputStream getContent() throws IOException { 
	    return _url.openConnection().getInputStream();
	}	
	public String toString() { return super.toString() + "[ " + getArgString() + "]"; }		
    }    
        
    static private class DiskFileDownloadResponse extends FileDownloadResponse {
	private File _file;	
	
	DiskFileDownloadResponse(File file, String mimeType, String versionId, long lastModified) {
	    super(mimeType, versionId, lastModified, file.getName());
	    _file = file;
	}
	
	int getContentLength() throws IOException { 
	    return (int)_file.length();
	}
	
	InputStream getContent() throws IOException { 
	    return new BufferedInputStream(new FileInputStream(_file));	   
	}

	public String toString() { return super.toString() + "[ " + getArgString() + "]"; }		
    }    
}



