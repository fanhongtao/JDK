/*
 * @(#)MLetContent.java	1.20 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management.loading;


// java import

import java.net.URL;
import java.net.MalformedURLException;
import java.util.Map;

/**
 * This class represents the contents of the <CODE>MLET</CODE> tag.
 * <p>
 * The <CODE>MLET</CODE> tag has the following syntax:
 * <p>
 * &lt;<CODE>MLET</CODE><BR>
 *      <CODE>CODE = </CODE><VAR>class</VAR><CODE> | OBJECT = </CODE><VAR>serfile</VAR><BR>
 *      <CODE>ARCHIVE = &quot;</CODE><VAR>archiveList</VAR><CODE>&quot;</CODE><BR>
 *      <CODE>[CODEBASE = </CODE><VAR>codebaseURL</VAR><CODE>]</CODE><BR>
 *      <CODE>[NAME = </CODE><VAR>mbeanname</VAR><CODE>]</CODE><BR>
 *      <CODE>[VERSION = </CODE><VAR>version</VAR><CODE>]</CODE><BR>
 * &gt;<BR>
 *	<CODE>[</CODE><VAR>arglist</VAR><CODE>]</CODE><BR>
 * &lt;<CODE>/MLET</CODE>&gt;
 * <p>
 * where:
 * <DL>
 * <DT><CODE>CODE = </CODE><VAR>class</VAR></DT>
 * <DD>
 * This attribute specifies the full Java class name, including package name, of the MBean to be obtained.
 * The compiled <CODE>.class</CODE> file of the MBean must be contained in one of the <CODE>.jar</CODE> files specified by the <CODE>ARCHIVE</CODE>
 * attribute. Either <CODE>CODE</CODE> or <CODE>OBJECT</CODE> must be present.
 * </DD>
 * <DT><CODE>OBJECT = </CODE><VAR>serfile</VAR></DT>
 * <DD>
 * This attribute specifies the <CODE>.ser</CODE> file that contains a serialized representation of the MBean to be obtained.
 * This file must be contained in one of the <CODE>.jar</CODE> files specified by the <CODE>ARCHIVE</CODE> attribute. If the <CODE>.jar</CODE> file contains a directory hierarchy, specify the path of the file within this hierarchy. Otherwise  a match will not be found. Either <CODE>CODE</CODE> or <CODE>OBJECT</CODE> must be present.
 * </DD>
 * <DT><CODE>ARCHIVE = &quot;</CODE><VAR>archiveList</VAR><CODE>&quot;</CODE></DT>
 * <DD>
 * This mandatory attribute specifies one or more <CODE>.jar</CODE> files 
 * containing MBeans or other resources used by
 * the MBean to be obtained. One of the <CODE>.jar</CODE> files must contain the file specified by the <CODE>CODE</CODE> or <CODE>OBJECT</CODE> attribute.
 * If archivelist contains more than one file:
 * <UL>
 * <LI>Each file must be separated from the one that follows it by a comma (,).
 * <LI><VAR>archivelist</VAR> must be enclosed in double quote marks.
 * </UL>
 * All <CODE>.jar</CODE> files in <VAR>archivelist</VAR> must be stored in the directory specified by the code base URL.
 * </DD>
 * <DT><CODE>CODEBASE = </CODE><VAR>codebaseURL</VAR></DT>
 * <DD>
 * This optional attribute specifies the code base URL of the MBean to be obtained. It identifies the directory that contains
 * the <CODE>.jar</CODE> files specified by the <CODE>ARCHIVE</CODE> attribute. Specify this attribute only if the <CODE>.jar</CODE> files are not in the same
 * directory as the MLet text file. If this attribute is not specified, the base URL of the MLet text file is used.
 * </DD>
 * <DT><CODE>NAME = </CODE><VAR>mbeanname</VAR></DT>
 * <DD>
 * This optional attribute specifies the object name to be assigned to the
 * MBean instance when the MLet service registers it. If 
 * <VAR>mbeanname</VAR> starts with the colon character (:), the domain 
 * part of the object name is the domain of the agent. The MLet service 
 * invokes the <CODE>getDomain()</CODE> method of the Framework class to 
 * obtain this information.
 * </DD>
 * <DT><CODE>PERSISTENT = </CODE><VAR>true | false</VAR></DT>
 * <DD>
 * This optional attribute specifies the persistency or not persistency of the
 * MBean instance when the MLet service registers it.
 * </DD>
 * <DT><CODE>VERSION = </CODE><VAR>version</VAR></DT>
 * <DD>
 * This optional attribute specifies the version number of the MBean and 
 * associated <CODE>.jar</CODE> files to be obtained. This version number can 
 * be used to specify that the <CODE>.jar</CODE> files are loaded from the 
 * server to update those stored locally in the cache the next time the MLet
 * text file is loaded. <VAR>version</VAR> must be a series of non-negative 
 * decimal integers each separated by a period from the one that precedes it.
 * </DD>
 * <DT><VAR>paramlist</VAR></DT>
 * <DD>
 * This optional attribute specifies a list of one or more parameters for the 
 * MBean to be instantiated. Each parameter in <VAR>paramlist</VAR> corresponds to a modification in the 
 * modification list. Use the following syntax to specify each item in
 * <VAR>paramlist</VAR>:</DD>
 * <DL>
 * <P>
 * <DT>&lt;<CODE>PARAM NAME=</CODE><VAR>propertyName</VAR> <CODE>VALUE=</CODE><VAR>value</VAR>&gt;</DT>
 * <P>
 * <DD>where:</DD>
 * <UL>
 * <LI><VAR>propertyName</VAR> is the name of the property in the modification
 * <LI><VAR>value</VAR> is the value in the modification</UL>
 * </DL>
 * <P>The MLet service passes all the values in the modification list as 
 * <CODE>String</CODE> objects. 
 * </DL>
 * 
 * <p><STRONG>Note - </STRONG>Multiple <CODE>MLET</CODE> tags with the same 
 * code base URL share the same instance of the <CODE>MLetClassLoader</CODE> 
 * class.
 *
 * @version     3.3     02/08/99
 * @author      Sun Microsystems, Inc
 *
 * @since 1.5
 */
 class MLetContent {

  
    /**
     * A hash table of the attributes of the <CODE>MLET</CODE> tag 
     * and their values.
     * @serial
     */
    private Map attributes;
  
    /**
     * The MLet text file's base URL.
     * @serial
     */
    private URL documentURL;
  
    /**
     * The base URL.
     * @serial
     */
    private URL baseURL;


    /**
     * Creates an <CODE>MLet</CODE> instance initialized with attributes read
     * from an <CODE>MLET</CODE> tag in an MLet text file.
     *
     * @param url The URL of the MLet text file containing the <CODE>MLET</CODE> tag.
     * @param attributes A list of the attributes of the <CODE>MLET</CODE> tag.
     */
    public MLetContent(URL url, Map attributes) {
	this.documentURL = url;
	this.attributes = attributes;

	// Initialize baseURL
	//
	String att = (String)getParameter("codebase");
	if (att != null) {
	    if (!att.endsWith("/")) {
		att += "/";
	    }
	    try {
		baseURL = new URL(documentURL, att);
	    } catch (MalformedURLException e) {
		// OK : Move to next block as baseURL could not be initialized.
	    }
	}
	if (baseURL == null) {
	    String file = documentURL.getFile();
	    int i = file.lastIndexOf('/');
	    if (i > 0 && i < file.length() - 1) {
		try {
		    baseURL = new URL(documentURL, file.substring(0, i + 1));
		} catch (MalformedURLException e) {
		    // OK : Move to next block as baseURL could not be initialized.
		}
	    }
	}
	if (baseURL == null)
	    baseURL = documentURL;

    }
    
    // GETTERS AND SETTERS
    //--------------------

    /**
     * Gets the attributes of the <CODE>MLET</CODE> tag.
     * @return A hash table of the attributes of the <CODE>MLET</CODE> tag 
     * and their values.
     */
    public Map getAttributes() {
	return attributes;
    }
  
    /**
     * Gets the MLet text file's base URL.
     * @return The MLet text file's base URL.
     */
    public URL getDocumentBase() {
	return documentURL;
    }

    /**
     * Gets the code base URL.
     * @return The code base URL.
     */
    public URL getCodeBase() {
	return baseURL;
    }
  
    /**
     * Gets the list of <CODE>.jar</CODE> files specified by the <CODE>ARCHIVE</CODE> 
     * attribute of the <CODE>MLET</CODE> tag.
     * @return A comma-separated list of <CODE>.jar</CODE> file names.
     */
    public String getJarFiles() {
	return (String)getParameter("archive");
    }

    /**
     * Gets the value of the <CODE>CODE</CODE> 
     * attribute of the <CODE>MLET</CODE> tag.
     * @return The value of the <CODE>CODE</CODE> 
     * attribute of the <CODE>MLET</CODE> tag.
     */
    public String getCode() {
	return (String)getParameter("code");
    }

    /**
     * Gets the value of the <CODE>OBJECT</CODE>
     * attribute of the <CODE>MLET</CODE> tag.
     * @return The value of the <CODE>OBJECT</CODE> 
     * attribute of the <CODE>MLET</CODE> tag.
     */
    public String getSerializedObject() {
	return (String)getParameter("object");
    }

    /**
     * Gets the value of the <CODE>NAME</CODE>
     * attribute of the <CODE>MLET</CODE> tag.
     * @return The value of the <CODE>NAME</CODE>
     * attribute of the <CODE>MLET</CODE> tag.
     */
    public String getName() {
	return (String)getParameter("name");
    }

 
    /**
     * Gets the value of the <CODE>VERSION</CODE>
     * attribute of the <CODE>MLET</CODE> tag.
     * @return The value of the <CODE>VERSION</CODE>
     * attribute of the <CODE>MLET</CODE> tag.
     */
    public String getVersion() {
	return (String)getParameter("version");
    }

    /**
     * Gets the value of the specified    
     * attribute of the <CODE>MLET</CODE> tag.
     *
     * @param name A string representing the name of the attribute.
     * @return The value of the specified    
     * attribute of the <CODE>MLET</CODE> tag.
     */
    public Object getParameter(String name) {
	return (Object) attributes.get(name.toLowerCase());
    }
  
}
