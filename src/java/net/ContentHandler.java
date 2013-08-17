/*
 * @(#)ContentHandler.java	1.8 98/07/01
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

/**
 * The abstract class <code>ContentHandler</code> is the superclass 
 * of all classes that read an <code>Object</code> from a 
 * <code>URLConnection</code>. 
 * <p>
 * An application does not generally call the 
 * <code>getContent</code> method in this class directly. Instead, an 
 * application calls the <code>getContent</code> method in class 
 * <code>URL</code> or in <code>URLConnection</code>.
 * The application's content handler factory (an instance of a class that 
 * implements the interface <code>ContentHandlerFactory</code> set 
 * up by a call to <code>setContentHandler</code>) is 
 * called with a <code>String</code> giving the MIME type of the 
 * object being received on the socket. The factory returns an 
 * instance of a subclass of <code>ContentHandler</code>, and its 
 * <code>getContent</code> method is called to create the object. 
 *
 * @author  James Gosling
 * @version 1.8, 07/01/98
 * @see     java.net.ContentHandler#getContent(java.net.URLConnection)
 * @see     java.net.ContentHandlerFactory
 * @see     java.net.URL#getContent()
 * @see     java.net.URLConnection
 * @see     java.net.URLConnection#getContent()
 * @see     java.net.URLConnection#setContentHandlerFactory(java.net.ContentHandlerFactory)
 * @since   JDK1.0
 */
abstract public class ContentHandler {
    /** 
     * Given a URL connect stream positioned at the beginning of the 
     * representation of an object, this method reads that stream and 
     * creates an object from it. 
     *
     * @param      urlc   a URL connection.
     * @return     the object read by the <code>ContentHandler</code>.
     * @exception  IOException  if an I/O error occurs while reading the object.
     * @since      JDK1.0
     */
    abstract public Object getContent(URLConnection urlc) throws IOException;
}
