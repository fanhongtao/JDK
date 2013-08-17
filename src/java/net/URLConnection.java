/*
 * @(#)URLConnection.java	1.33 98/08/17
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * The abstract class <code>URLConnection</code> is the superclass 
 * of all classes that represent a communications link between the 
 * application and a URL. Instances of this class can be used both to 
 * read from and to write to the resource referenced by the URL. In 
 * general, creating a connection to a URL is a multistep process: 
 * <p>
 * <center><table border>
 * <tr><th><code>openConnection()</code></th>
 *     <th><code>connect()</code></th></tr>
 * <tr><td>Manipulate parameters that affect the connection to the remote 
 *         resource.</td>
 *     <td>Interact with the resource; query header fields and
 *         contents.</td></tr>
 * </table>
 * ----------------------------&gt;
 * <br>time</center>
 *
 * <ol>
 * <li>The connection object is created by invoking the
 *     <code>openConnection</code> method on a URL.
 * <li>The setup parameters and general request properties are manipulated.
 * <li>The actual connection to the remote object is made, using the
 *    <code>connect</code> method.
 * <li>The remote object becomes available. The header fields and the contents
 *     of the remote object can be accessed.
 * </ol>
 * <p>
 * The setup parameters are modified using the following methods: 
 * <ul><code>
 *   <li>setAllowUserInteraction
 *   <li>setDoInput
 *   <li>setDoOutput
 *   <li>setIfModifiedSince
 *   <li>setUseCaches
 * </code></ul>
 * <p>
 * and the general request properties are modified using the method:
 * <ul><code>
 *   <li>setRequestProperty
 * </code></ul>
 * <p>
 * Default values for the <code>AllowUserInteraction</code> and 
 * <code>UseCaches</code> parameters can be set using the methods 
 * <code>setDefaultAllowUserInteraction</code> and 
 * <code>setDefaultUseCaches</code>. Default values for general 
 * request properties can be set using the 
 * <code>setDefaultRequestProperty</code> method. 
 * <p>
 * Each of the above <code>set</code> methods has a corresponding 
 * <code>get</code> method to retrieve the value of the parameter or 
 * general request property. The specific parameters and general 
 * request properties that are applicable are protocol specific. 
 * <p>
 * The following methods are used to access the header fields and 
 * the contents after the connection is made to the remote object:
 * <ul><code>
 *   <li>getContent
 *   <li>getHeaderField
 *   <li>getInputStream
 *   <li>getOutputStream
 * </code></ul>
 * <p>
 * Certain header fields are accessed frequently. The methods:
 * <ul><code>
 *   <li>getContentEncoding
 *   <li>getContentLength
 *   <li>getContentType
 *   <li>getDate
 *   <li>getExpiration
 *   <li>getLastModified
 * </code></ul>
 * <p>
 * provide convenient access to these fields. The 
 * <code>getContentType</code> method is used by the 
 * <code>getContent</code> method to determine the type of the remote 
 * object; subclasses may find it convenient to override the 
 * <code>getContentType</code> method.  
 * <p>
 * In the common case, all of the pre-connection parameters and 
 * general request properties can be ignored: the pre-connection 
 * parameters and request properties default to sensible values. For 
 * most clients of this interface, there are only two interesting 
 * methods: <code>getInputStream</code> and <code>getObject</code>, 
 * which are mirrored in the <code>URL</code> class by convenience methods.
 * <p>
 * More information on the request properties and header fields of 
 * an <code>http</code> connection can be found at:
 * <ul><code>
 *   http://www.w3.org/hypertext/WWW/Protocols/HTTP1.0/draft-ietf-http-spec.html
 * </code></ul>
 * <p>
 * Note about <code>fileNameMap</code>: In versions prior to JDK 1.1.6,
 * field <code>fileNameMap</code> of <code>URLConnection</code> was public.
 * In JDK 1.1.6 and later, <code>fileNameMap</code> is private; accessor
 * method <code>getFileNameMap()</code> and mutator method 
 * <code>setFileNameMap(FileNameMap)</code> are added to access it.  
 * This change is also described on the 
 * <a href="http://java.sun.com/products/jdk/1.1/compatibility.html">
 * JDK 1.1.x Compatibility</a> page.
 *
 * @author  James Gosling
 * @version 1.33, 08/17/98
 * @see     java.net.URL#openConnection()
 * @see     java.net.URLConnection#connect()
 * @see     java.net.URLConnection#getContent()
 * @see     java.net.URLConnection#getContentEncoding()
 * @see     java.net.URLConnection#getContentLength()
 * @see     java.net.URLConnection#getContentType()
 * @see     java.net.URLConnection#getDate()
 * @see     java.net.URLConnection#getExpiration()
 * @see     java.net.URLConnection#getHeaderField(int)
 * @see     java.net.URLConnection#getHeaderField(java.lang.String)
 * @see     java.net.URLConnection#getInputStream()
 * @see     java.net.URLConnection#getLastModified()
 * @see     java.net.URLConnection#getOutputStream()
 * @see     java.net.URLConnection#setAllowUserInteraction(boolean)
 * @see     java.net.URLConnection#setDefaultRequestProperty(java.lang.String, java.lang.String)
 * @see     java.net.URLConnection#setDefaultUseCaches(boolean)
 * @see     java.net.URLConnection#setDoInput(boolean)
 * @see     java.net.URLConnection#setDoOutput(boolean)
 * @see     java.net.URLConnection#setIfModifiedSince(long)
 * @see     java.net.URLConnection#setRequestProperty(java.lang.String, java.lang.String)
 * @see     java.net.URLConnection#setUseCaches(boolean)
 * @since   JDK1.0
 */
abstract public class URLConnection {
   /**
     * The URL represents the remote object on the World Wide Web to 
     * which this connection is opened. 
     * <p>
     * The value of this field can be accessed by the 
     * <code>getURL</code> method. 
     * <p>
     * The default value of this variable is the value of the URL 
     * argument in the <code>URLConnection</code> constructor. 
     *
     * @see     java.net.URLConnection#getURL()
     * @see     java.net.URLConnection#url
     * @since   JDK1.0
     */
    protected URL url;

   /**
     * This variable is set by the <code>setDoInput</code> method. Its 
     * value is returned by the <code>getDoInput</code> method. 
     * <p>
     * A URL connection can be used for input and/or output. Setting the 
     * <code>doInput</code> flag to <code>true</code> indicates that 
     * the application intends to read data from the URL connection. 
     * <p>
     * The default value of this field is <code>true</code>. 
     *
     * @see     java.net.URLConnection#getDoInput()
     * @see     java.net.URLConnection#setDoInput(boolean)
     * @since   JDK1.0
     */
    protected boolean doInput = true;

   /**
     * This variable is set by the <code>setDoOutput</code> method. Its 
     * value is returned by the <code>getDoInput</code> method. 
     * <p>
     * A URL connection can be used for input and/or output. Setting the 
     * <code>doOutput</code> flag to <code>true</code> indicates 
     * that the application intends to write data to the URL connection. 
     * <p>
     * The default value of this field is <code>false</code>. 
     *
     * @see     java.net.URLConnection#getDoOutput()
     * @see     java.net.URLConnection#setDoOutput(boolean)
     * @since   JDK1.0
     */
    protected boolean doOutput = false;

    private static boolean defaultAllowUserInteraction = false;

   /**
     * If <code>true</code>, this <code>URL</code> is being examined in 
     * a context in which it makes sense to allow user interactions such 
     * as popping up an authentication dialog. If <code>false</code>, 
     * then no user interaction is allowed. 
     * <p>
     * The value of this field can be set by the 
     * <code>setAllowUserInteraction</code> method.
     * Its value is returned by the 
     * <code>getAllowUserInteraction</code> method.
     * Its default value is the value of the argument in the last invocation 
     * of the <code>setDefaultAllowUserInteraction</code> method. 
     *
     * @see     java.net.URLConnection#getAllowUserInteraction()
     * @see     java.net.URLConnection#setAllowUserInteraction(boolean)
     * @see     java.net.URLConnection#setDefaultAllowUserInteraction(boolean)
     * @since   JDK1.0
     */
    protected boolean allowUserInteraction = defaultAllowUserInteraction;

    private static boolean defaultUseCaches = true;

   /**
     * If <code>true</code>, the protocol is allowed to use caching 
     * whenever it can. If <code>false</code>, the protocol must always 
     * try to get a fresh copy of the object. 
     * <p>
     * This field is set by the <code>setUseCaches</code> method. Its 
     * value is returned by the <code>getUseCaches</code> method.
     * <p>
     * Its default value is the value given in the last invocation of the 
     * <code>setDefaultUseCaches</code> method. 
     *
     * @see     java.net.URLConnection#setUseCaches(boolean)
     * @see     java.net.URLConnection#getUseCaches()
     * @see     java.net.URLConnection#setDefaultUseCaches(boolean)
     * @since   JDK1.0
     */
    protected boolean useCaches = defaultUseCaches;

   /**
     * Some protocols support skipping the fetching of the object unless 
     * the object has been modified more recently than a certain time. 
     * <p>
     * A nonzero value gives a time as the number of seconds since 
     * January 1, 1970, GMT. The object is fetched only if it has been 
     * modified more recently than that time. 
     * <p>
     * This variable is set by the <code>setIfModifiedSince</code> 
     * method. Its value is returned by the 
     * <code>getIfModifiedSince</code> method.
     * <p>
     * The default value of this field is <code>0</code>, indicating 
     * that the fetching must always occur. 
     *
     * @see     java.net.URLConnection#getIfModifiedSince()
     * @see     java.net.URLConnection#setIfModifiedSince(long)
     * @since   JDK1.0
     */
    protected long ifModifiedSince = 0;

   /**
     * If <code>false</code>, this connection object has not created a 
     * communications link to the specified URL. If <code>true</code>, 
     * the communications link has been established. 
     *
     * @since   JDK1.0
     */
    protected boolean connected = false;

   /**
    * @since   JDK1.1
    */
    private static FileNameMap fileNameMap;

    /**
     * Returns the FileNameMap.
     *
     * @returns	the FileNameMap
     * @since   JDK1.1
     */
    public static FileNameMap getFileNameMap() {
	return fileNameMap;
    }

    /**
     * Sets the FileNameMap.
     *
     * @param map the FileNameMap to be set
     * @since   JDK1.1
     */
    public static void setFileNameMap(FileNameMap map) {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) sm.checkSetFactory();
	fileNameMap = map;
    }

    /**
     * Opens a communications link to the resource referenced by this 
     * URL, if such a connection has not already been established. 
     * <p>
     * If the <code>connect</code> method is called when the connection 
     * has already been opened (indicated by the <code>connected</code> 
     * field having the value <code>true</code>), the call is ignored. 
     * <p>
     * URLConnection objects go through two phases: first they are
     * created, then they are connected.  After being created, and
     * before being connected, various options can be specified
     * (e.g., doInput and UseCaches).  After connecting, it is an
     * error to try to set them.  Operations that depend on being
     * connected, like getContentLength, will implicitly perform the
     * connection, if necessary.
     *
     * @exception  IOException  if an I/O error occurs while opening the
     *               connection.
     * @see        java.net.URLConnection#connected
     * @since      JDK1.0
     */
    abstract public void connect() throws IOException;

    /**
     * Constructs a URL connection to the specified URL. A connection to 
     * the object referenced by the URL is not created. 
     *
     * @param   url   the specified URL.
     * @since   JDK1.0
     */
    protected URLConnection(URL url) {
	this.url = url;
    }

    /**
     * Returns the value of this <code>URLConnection</code>'s <code>URL</code>
     * field.
     *
     * @return  the value of this <code>URLConnection</code>'s <code>URL</code>
     *          field.
     * @see     java.net.URLConnection#url
     * @since   JDK1.0
     */
    public URL getURL() {
	return url;
    }

    /**
     * Returns the value of the <code>content-length</code> header field.
     *
     * @return  the content length of the resource that this connection's URL
     *          references, or <code>-1</code> if the content length is
     *          not known.
     * @since   JDK1.0
     */
    public int getContentLength() {
	return getHeaderFieldInt("content-length", -1);
    }

    /**
     * Returns the value of the <code>content-type</code> header field.
     *
     * @return  the content type of the resource that the URL references,
     *          or <code>null</code> if not known.
     * @see     java.net.URLConnection#getHeaderField(java.lang.String)
     * @since   JDK1.0
     */
    public String getContentType() {
	return getHeaderField("content-type");
    }

    /**
     * Returns the value of the <code>content-encoding</code> header field.
     *
     * @return  the content encoding of the resource that the URL references,
     *          or <code>null</code> if not known.
     * @see     java.net.URLConnection#getHeaderField(java.lang.String)
     * @since   JDK1.0
     */
    public String getContentEncoding() {
	return getHeaderField("content-encoding");
    }

    /**
     * Returns the value of the <code>expires</code> header field. 
     *
     * @return  the expiration date of the resource that this URL references,
     *          or 0 if not known. The value is the number of seconds since
     *          January 1, 1970 GMT.
     * @see     java.net.URLConnection#getHeaderField(java.lang.String)
     * @since   JDK1.0
     */
    public long getExpiration() {
	return getHeaderFieldDate("expires", 0);
    }

    /**
     * Returns the value of the <code>date</code> header field. 
     *
     * @return  the sending date of the resource that the URL references,
     *          or <code>0</code> if not known. The value returned is the
     *          number of seconds since January 1, 1970 GMT.
     * @see     java.net.URLConnection#getHeaderField(java.lang.String)
     * @since   JDK1.0
     */
    public long getDate() {
	return getHeaderFieldDate("date", 0);
    }

    /**
     * Returns the value of the <code>last-modified</code> header field. 
     * The result is the number of seconds since January 1, 1970 GMT.
     *
     * @return  the date the resource referenced by this
     *          <code>URLConnection</code> was last modified, or 0 if not known.
     * @see     java.net.URLConnection#getHeaderField(java.lang.String)
     * @since   JDK1.0
     */
    public long getLastModified() {
	return getHeaderFieldDate("last-modified", 0);
    }

    /**
     * Returns the value of the specified header field. Names of  
     * header fields to pass to this method can be obtained from 
     * getHeaderFieldKey.
     *
     * @param   name   the name of a header field. 
     * @return  the value of the named header field, or <code>null</code>
     *          if there is no such field in the header.
     * @see     java.net.URLConnection#getHeaderFieldKey(int)
     * @since   JDK1.0
     */
    public String getHeaderField(String name) {
	return null;
    }

    /**
     * Returns the value of the named field parsed as a number.
     * <p>
     * This form of <code>getHeaderField</code> exists because some 
     * connection types (e.g., <code>http-ng</code>) have pre-parsed 
     * headers. Classes for that connection type can override this method 
     * and short-circuit the parsing. 
     *
     * @param   name      the name of the header field.
     * @param   Default   the default value.
     * @return  the value of the named field, parsed as an integer. The
     *          <code>Default</code> value is returned if the field is
     *          missing or malformed.
     * @since   JDK1.0
     */
    public int getHeaderFieldInt(String name, int Default) {
	try {
	    return Integer.parseInt(getHeaderField(name));
	} catch(Throwable t) {}
	return Default;
    }

    /**
     * Returns the value of the named field parsed as date.
     * The result is the number of seconds since January 1, 1970 GMT
     * represented by the named field. 
     * <p>
     * This form of <code>getHeaderField</code> exists because some 
     * connection types (e.g., <code>http-ng</code>) have pre-parsed 
     * headers. Classes for that connection type can override this method 
     * and short-circuit the parsing. 
     *
     * @param   name     the name of the header field.
     * @param   Default   a default value.
     * @return  the value of the field, parsed as a date. The value of the
     *          <code>Default</code> argument is returned if the field is
     *          missing or malformed.
     * @since   JDK1.0
     */
    public long getHeaderFieldDate(String name, long Default) {
	try {
	    return Date.parse(getHeaderField(name));
	} catch(Throwable t) {}
	return Default;
    }

    /**
     * Returns the key for the <code>n</code><sup>th</sup> header field.
     *
     * @param   n   an index.
     * @return  the key for the <code>n</code><sup>th</sup> header field,
     *          or <code>null</code> if there are fewer than <code>n</code>
     *          fields.
     * @since   JDK1.0
     */
    public String getHeaderFieldKey(int n) {
	return null;
    }

    /**
     * Returns the value for the <code>n</code><sup>th</sup> header field. 
     * It returns <code>null</code> if there are fewer than 
     * <code>n</code> fields. 
     * <p>
     * This method can be used in conjunction with the 
     * <code>getHeaderFieldKey</code> method to iterate through all 
     * the headers in the message. 
     *
     * @param   n   an index.
     * @return  the value of the <code>n</code><sup>th</sup> header field.
     * @see     java.net.URLConnection#getHeaderFieldKey(int)
     * @since   JDK1.0
     */
    public String getHeaderField(int n) {
	return null;
    }

    /**
     * Retrieves the contents of this URL connection. 
     * <p>
     * This method first determines the content type of the object by 
     * calling the <code>getContentType</code> method. If this is 
     * the first time that the application has seen that specific content 
     * type, a content handler for that content type is created: 
     * <ol>
     * <li>If the application has set up a content handler factory instance
     *     using the <code>setContentHandlerFactory</code> method, the
     *     <code>createContentHandler</code> method of that instance is called
     *     with the content type as an argument; the result is a content
     *     handler for that content type.
     * <li>If no content handler factory has yet been set up, or if the
     *     factory's <code>createContentHandler</code> method returns
     *     <code>null</code>, then the application loads the class named:
     *     <ul><code>
     *         sun.net.www.content.&lt;<i>contentType</i>&gt;
     *     </code></ul>
     *     where &lt;<i>contentType</i>&gt; is formed by taking the
     *     content-type string, replacing all slash characters with a
     *     <code>period</code> ('.'), and all other non-alphanumeric characters
     *     with the underscore character '<code>_</code>'. The alphanumeric
     *     characters are specifically the 26 uppercase ASCII letters
     *     '<code>A</code>' through '<code>Z</code>', the 26 lowercase ASCII
     *     letters '<code>a</code>' through '<code>z</code>', and the 10 ASCII
     *     digits '<code>0</code>' through '<code>9</code>'. If the specified
     *     class does not exist, or is not a subclass of
     *     <code>ContentHandler</code>, then an
     *     <code>UnknownServiceException</code> is thrown.
     * </ol>
     *
     * @return     the object fetched. The <code>instanceOf</code> operation
     *               should be used to determine the specific kind of object
     *               returned.
     * @exception  IOException              if an I/O error occurs while
     *               getting the content.
     * @exception  UnknownServiceException  if the protocol does not support
     *               the content type.
     * @see        java.net.ContentHandlerFactory#createContentHandler(java.lang.String)
     * @see        java.net.URLConnection#getContentType()
     * @see        java.net.URLConnection#setContentHandlerFactory(java.net.ContentHandlerFactory)
     * @since      JDK1.0
     */
    public Object getContent() throws IOException {
	return getContentHandler().getContent(this);
    }

    /**
     * Returns an input stream that reads from this open connection.
     *
     * @return     an input stream that reads from this open connection.
     * @exception  IOException              if an I/O error occurs while
     *               creating the input stream.
     * @exception  UnknownServiceException  if the protocol does not support
     *               input.
     * @since   JDK1.0
     */
    public InputStream getInputStream() throws IOException {
	throw new UnknownServiceException("protocol doesn't support input");
    }

    /**
     * Returns an output stream that writes to this connection.
     *
     * @return     an output stream that writes to this connection.
     * @exception  IOException              if an I/O error occurs while
     *               creating the output stream.
     * @exception  UnknownServiceException  if the protocol does not support
     *               output.
     * @since   JDK1.0
     */
    public OutputStream getOutputStream() throws IOException {
	throw new UnknownServiceException("protocol doesn't support output");
    }

    /**
     * Returns a <code>String</code> representation of this URL connection.
     *
     * @return  a string representation of this <code>URLConnection</code>.
     * @since   JDK1.0
     */
    public String toString() {
	return this.getClass().getName() + ":" + url;
    }

    /**
     * Sets the value of the <code>doInput</code> field for this 
     * <code>URLConnection</code> to the specified value. 
     * <p>
     * A URL connection can be used for input and/or output.  Set the DoInput
     * flag to true if you intend to use the URL connection for input,
     * false if not.  The default is true unless DoOutput is explicitly
     * set to true, in which case DoInput defaults to false.
     *
     * @param   value   the new value.
     * @see     java.net.URLConnection#doInput
     * @since   JDK1.0
     */
    public void setDoInput(boolean doinput) {
	if (connected)
	    throw new IllegalAccessError("Already connected");
	doInput = doinput;
    }

    /**
     * Returns the value of this <code>URLConnection</code>'s
     * <code>doInput</code> flag.
     *
     * @return  the value of this <code>URLConnection</code>'s
     *          <code>doInput</code> flag.
     * @see     java.net.URLConnection#doInput
     * @since   JDK1.0
     */
    public boolean getDoInput() {
	return doInput;
    }

    /**
     * Sets the value of the <code>doOutput</code> field for this 
     * <code>URLConnection</code> to the specified value. 
     * <p>
     * A URL connection can be used for input and/or output.  Set the DoOutput
     * flag to true if you intend to use the URL connection for output,
     * false if not.  The default is false.
     *
     * @param   value   the new value.
     * @see     java.net.URLConnection#doOutput
     * @since   JDK1.0
     */
    public void setDoOutput(boolean dooutput) {
	if (connected)
	    throw new IllegalAccessError("Already connected");
	doOutput = dooutput;
    }

    /**
     * Returns the value of this <code>URLConnection</code>'s
     * <code>doOutput</code> flag.
     *
     * @return  the value of this <code>URLConnection</code>'s
     *          <code>doOutput</code> flag.
     * @see     java.net.URLConnection#doOutput
     * @since   JDK1.0
     */
    public boolean getDoOutput() {
	return doOutput;
    }

    /**
     * Set the value of the <code>allowUserInteraction</code> field of 
     * this <code>URLConnection</code>. 
     *
     * @param   allowuserinteraction   the new value.
     * @see     java.net.URLConnection#allowUserInteraction
     * @since   JDK1.0
     */
    public void setAllowUserInteraction(boolean allowuserinteraction) {
	if (connected)
	    throw new IllegalAccessError("Already connected");
	allowUserInteraction = allowuserinteraction;
    }

    /**
     * Returns the value of the <code>allowUserInteraction</code> field for
     * this object.
     *
     * @return  the value of the <code>allowUserInteraction</code> field for
     *          this object.
     * @see     java.net.URLConnection#allowUserInteraction
     * @since   JDK1.0
     */
    public boolean getAllowUserInteraction() {
	return allowUserInteraction;
    }

    /**
     * Sets the default value of the 
     * <code>allowUserInteraction</code> field for all future 
     * <code>URLConnection</code> objects to the specified value. 
     *
     * @param   defaultallowuserinteraction   the new value.
     * @see     java.net.URLConnection#allowUserInteraction
     * @since   JDK1.0
     */
    public static void setDefaultAllowUserInteraction(boolean defaultallowuserinteraction) {
	defaultAllowUserInteraction = defaultallowuserinteraction;
    }

    /**
     * Returns the default value of the <code>allowUserInteraction</code>
     * field.
     * <p>
     * Ths default is "sticky", being a part of the static state of all
     * URLConnections.  This flag applies to the next, and all following
     * URLConnections that are created.
     *
     * @return  the default value of the <code>allowUserInteraction</code>
     *          field.
     * @see     java.net.URLConnection#allowUserInteraction
     * @since   JDK1.0
     */
    public static boolean getDefaultAllowUserInteraction() {
	return defaultAllowUserInteraction;
    }

    /**
     * Sets the value of the <code>useCaches</code> field of this 
     * <code>URLConnection</code> to the specified value. 
     * <p>
     * Some protocols do caching of documents.  Occasionally, it is important
     * to be able to "tunnel through" and ignore the caches (e.g., the
     * "reload" button in a browser).  If the UseCaches flag on a connection
     * is true, the connection is allowed to use whatever caches it can.
     *  If false, caches are to be ignored.
     *  The default value comes from DefaultUseCaches, which defaults to
     * true.
     *
     * @see     java.net.URLConnection#useCaches
     * @since   JDK1.0
     */
    public void setUseCaches(boolean usecaches) {
	if (connected)
	    throw new IllegalAccessError("Already connected");
	useCaches = usecaches;
    }

    /**
     * Returns the value of this <code>URLConnection</code>'s
     * <code>useCaches</code> field.
     *
     * @return  the value of this <code>URLConnection</code>'s
     *          <code>useCaches</code> field.
     * @see     java.net.URLConnection#useCaches
     * @since   JDK1.0
     */
    public boolean getUseCaches() {
	return useCaches;
    }

    /**
     * Sets the value of the <code>ifModifiedSince</code> field of 
     * this <code>URLConnection</code> to the specified value.
     *
     * @param   value   the new value.
     * @see     java.net.URLConnection#ifModifiedSince
     * @since   JDK1.0
     */
    public void setIfModifiedSince(long ifmodifiedsince) {
	if (connected)
	    throw new IllegalAccessError("Already connected");
	ifModifiedSince = ifmodifiedsince;
    }

    /**
     * Returns the value of this object's <code>ifModifiedSince</code> field.
     *
     * @return  the value of this object's <code>ifModifiedSince</code> field.
     * @see     java.net.URLConnection#ifModifiedSince
     * @since   JDK1.0
     */
    public long getIfModifiedSince() {
	return ifModifiedSince;
    }

   /**
     * Returns the default value of a <code>URLConnection</code>'s
     * <code>useCaches</code> flag.
     * <p>
     * Ths default is "sticky", being a part of the static state of all
     * URLConnections.  This flag applies to the next, and all following
     * URLConnections that are created.
     *
     * @return  the default value of a <code>URLConnection</code>'s
     *          <code>useCaches</code> flag.
     * @see     java.net.URLConnection#useCaches
     * @since   JDK1.0
     */
    public boolean getDefaultUseCaches() {
	return defaultUseCaches;
    }

   /**
     * Sets the default value of the <code>useCaches</code> field to the 
     * specified value. 
     *
     * @param   defaultusecaches   the new value.
     * @see     java.net.URLConnection#useCaches
     * @since   JDK1.0
     */
    public void setDefaultUseCaches(boolean defaultusecaches) {
	defaultUseCaches = defaultusecaches;
    }

    /**
     * Sets the general request property. 
     *
     * @param   key     the keyword by which the request is known
     *                  (e.g., "<code>accept</code>").
     * @param   value   the value associated with it.
     * @since   JDK1.0
     */
    public void setRequestProperty(String key, String value) {
	if (connected)
	    throw new IllegalAccessError("Already connected");
    }

    /**
     * Returns the value of the named general request property for this
     * connection.
     *
     * @return  the value of the named general request property for this
     *           connection.
     * @since   JDK1.0
     */
    public String getRequestProperty(String key) {
	if (connected)
	    throw new IllegalAccessError("Already connected");
	return null;
    }

    /**
     * Sets the default value of a general request property. When a 
     * <code>URLConnection</code> is created, it is initialized with 
     * these properties. 
     *
     * @param   key     the keyword by which the request is known
     *                  (e.g., "<code>accept</code>").
     * @param   value   the value associated with the key.
     * @since   JDK1.0
     */
    public static void setDefaultRequestProperty(String key, String value) {
    }

    /**
     * Returns the value of the default request property. Default request 
     * properties are set for every connection. 
     *
     * @return  the value of the default request property for the specified key.
     * @see     java.net.URLConnection#setDefaultRequestProperty(java.lang.String, java.lang.String)
     * @since   JDK1.0
     */
    public static String getDefaultRequestProperty(String key) {
	return null;
    }

    /**
     * The ContentHandler factory.
     */
    static ContentHandlerFactory factory;

    /**
     * Sets the <code>ContentHandlerFactory</code> of an 
     * application. It can be called at most once by an application. 
     * <p>
     * The <code>ContentHandlerFactory</code> instance is used to 
     * construct a content handler from a content type 
     *
     * @param      fac   the desired factory.
     * @exception  Error  if the factory has already been defined.
     * @see        java.net.ContentHandlerFactory
     * @see        java.net.URLConnection#getContent()
     * @since      JDK1.0
     */
    public static synchronized void setContentHandlerFactory(ContentHandlerFactory fac) {
	if (factory != null) {
	    throw new Error("factory already defined");
	}
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkSetFactory();
	}
	factory = fac;
    }

    private static Hashtable handlers = new Hashtable();
    private static ContentHandler UnknownContentHandlerP = new UnknownContentHandler();

    /**
     * Gets the Content Handler appropriate for this connection.
     * @param connection the connection to use.
     */
    synchronized ContentHandler getContentHandler()
    throws UnknownServiceException
    {
	String contentType = getContentType();
	ContentHandler handler = null;
	if (contentType == null)
	    throw new UnknownServiceException("no content-type");
	try {
	    handler = (ContentHandler) handlers.get(contentType);
	    if (handler != null)
		return handler;
	} catch(Exception e) {
	}
	if (factory != null)
	    handler = factory.createContentHandler(contentType);
	if (handler == null) {
	    try {
		handler = lookupContentHandlerClassFor(contentType);
	    } catch(Exception e) {
		e.printStackTrace();
		handler = UnknownContentHandlerP;
	    }
	    handlers.put(contentType, handler);
	}
	return handler;
    }

    private static final String contentClassPrefix = "sun.net.www.content";
    private static final String contentPathProp = "java.content.handler.pkgs";

    /**
     * Looks for a content handler in a user-defineable set of places.
     * By default it looks in sun.net.www.content, but users can define a 
     * vertical-bar delimited set of class prefixes to search through in 
     * addition by defining the java.content.handler.pkgs property.
     * The class name must be of the form:
     * <pre>
     *     {package-prefix}.{major}.{minor}
     * e.g.
     *     YoyoDyne.experimental.text.plain
     * </pre>
     */
    private ContentHandler lookupContentHandlerClassFor(String contentType)
	throws InstantiationException, IllegalAccessException, ClassNotFoundException {
	String contentHandlerClassName = typeToPackageName(contentType);

	String contentHandlerPkgPrefixes = getContentHandlerPkgPrefixes();

	StringTokenizer packagePrefixIter =
	    new StringTokenizer(contentHandlerPkgPrefixes, "|");
	
	while (packagePrefixIter.hasMoreTokens()) {
	    String packagePrefix = packagePrefixIter.nextToken().trim();

	    try {
		String name = packagePrefix + "." + contentHandlerClassName;
		ContentHandler handler =
		    (ContentHandler) Class.forName(name).newInstance();
		return handler;
	    } catch(Exception e) {
	    }
	}
	
	return UnknownContentHandlerP;
    }

    /**
     * Utility function to map a MIME content type into an equivalent
     * pair of class name components.  For example: "text/html" would
     * be returned as "text.html"
     */
    private String typeToPackageName(String contentType) {
	int len = contentType.length();
	char nm[] = new char[len];
	contentType.getChars(0, len, nm, 0);
	for (int i = 0; i < len; i++) {
	    char c = nm[i];
	    if (c == '/') {
		nm[i] = '.';
	    } else if (!('A' <= c && c <= 'Z' ||
		       'a' <= c && c <= 'z' ||
		       '0' <= c && c <= '9')) {
		nm[i] = '_';
	    }
	}
	return new String(nm);
    }


    /**
     * Returns a vertical bar separated list of package prefixes for potential
     * content handlers.  Tries to get the java.content.handler.pkgs property
     * to use as a set of package prefixes to search.  Whether or not
     * that property has been defined, the sun.net.www.content is always
     * the last one on the returned package list.
     */
    private String getContentHandlerPkgPrefixes() {
	String packagePrefixList = System.getProperty(contentPathProp, "");
	if (packagePrefixList != "") {
	    packagePrefixList += "|";
	}
	
	return packagePrefixList + contentClassPrefix;
    }

    /**
     * Tries to determine the content type of an object, based 
     * on the specified "file" component of a URL.
     * This is a convenience method that can be used by 
     * subclasses that override the <code>getContentType</code> method. 
     *
     * @param   fname   a filename.
     * @return  a guess as to what the content type of the object is,
     *          based upon its file name.
     * @see     java.net.URLConnection#getContentType()
     * @since   JDK1.0
     */
    protected static String guessContentTypeFromName(String fname) {
	String contentType = null;
	if (fileNameMap != null) {
	    contentType = fileNameMap.getContentTypeFor(fname);
	}

	return contentType;
    }

    /**
     * Tries to determine the type of an input stream based on the 
     * characters at the beginning of the input stream. This method can 
     * be used by subclasses that override the 
     * <code>getContentType</code> method. 
     * <p>
     * Ideally, this routine would not be needed. But many 
     * <code>http</code> servers return the incorrect content type; in 
     * addition, there are many nonstandard extensions. Direct inspection 
     * of the bytes to determine the content type is often more accurate 
     * than believing the content type claimed by the <code>http</code> server.
     *
     * @param      is   an input stream that supports marks.
     * @return     a guess at the content type, or <code>null</code> if none
     *             can be determined.
     * @exception  IOException  if an I/O error occurs while reading the
     *               input stream.
     * @see        java.io.InputStream#mark(int)
     * @see        java.io.InputStream#markSupported()
     * @see        java.net.URLConnection#getContentType()
     * @since      JDK1.0
     */
    static public String guessContentTypeFromStream(InputStream is) throws IOException
    {
	is.mark(10);
	int c1 = is.read();
	int c2 = is.read();
	int c3 = is.read();
	int c4 = is.read();
	int c5 = is.read();
	int c6 = is.read();
	is.reset();
	if (c1 == 0xCA && c2 == 0xFE && c3 == 0xBA && c4 == 0xBE)
	    return "application/java-vm";
	if (c1 == 0xAC && c2 == 0xED)
	    // next two bytes are version number, currently 0x00 0x05
	    return "application/x-java-serialized-object";
	if (c1 == 'G' && c2 == 'I' && c3 == 'F' && c4 == '8')
	    return "image/gif";
	if (c1 == '#' && c2 == 'd' && c3 == 'e' && c4 == 'f')
	    return "image/x-bitmap";
	if (c1 == '!' && c2 == ' ' && c3 == 'X' && c4 == 'P' && c5 == 'M' && c6 == '2')
	    return "image/x-pixmap";
	if (c1 == '<')
	    if (c2 == '!'
		    || (c6 == '>'
		    && (c2 == 'h' && (c3 == 't' && c4 == 'm' && c5 == 'l' ||
				      c3 == 'e' && c4 == 'a' && c5 == 'd')
		      || c2 == 'b' && c3 == 'o' && c4 == 'd' && c5 == 'y')))
		return "text/html";

	if (c1 == 0x2E && c2 == 0x73 && c3 == 0x6E && c4 == 0x64)
	    return "audio/basic";  // .au format, big endian
	if (c1 == 0x64 && c2 == 0x6E && c3 == 0x73 && c4 == 0x2E)
	    return "audio/basic";  // .au format, little endian
	if (c1 == '<')
	    if (c2 == '!'
		|| ((c2 == 'h' && (c3 == 't' && c4 == 'm' && c5 == 'l' ||
				   c3 == 'e' && c4 == 'a' && c5 == 'd')
		     || c2 == 'b' && c3 == 'o' && c4 == 'd' && c5 == 'y'))
		|| ((c2 == 'H' && (c3 == 'T' && c4 == 'M' && c5 == 'L' ||
				   c3 == 'E' && c4 == 'A' && c5 == 'D')
		     || c2 == 'B' && c3 == 'O' && c4 == 'D' && c5 == 'Y')))
		return "text/html";

	if (c1 == 0xFF && c2 == 0xD8 && c3 == 0xFF && c4 == 0xE0)
	    return "image/jpeg";
	if (c1 == 0xFF && c2 == 0xD8 && c3 == 0xFF && c4 == 0xEE)
	    return "image/jpg";

	if (c1 == 'R' && c2 == 'I' && c3 == 'F' && c4 == 'F')
	    /* I don't know if this is official but evidence
	     * suggests that .wav files start with "RIFF" - brown
	     */
	    return "audio/x-wav";  
	return null;
    }
}

class UnknownContentHandler extends ContentHandler {
    public Object getContent(URLConnection uc) throws IOException {
	return uc.getInputStream();
    }
}
