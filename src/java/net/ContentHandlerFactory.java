/*
 * @(#)ContentHandlerFactory.java	1.4 98/07/01
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

/**
 * This interface defines a factory for content handlers. An 
 * implementation of this interface should map a MIME type into an 
 * instance of <code>ContentHandler</code>. 
 * <p>
 * This interface is used by the <code>URLStreamHandler</code> class 
 * to create a <code>ContentHandler</code> for a MIME type. 
 *
 * @author  James Gosling
 * @version 1.4, 07/01/98
 * @see     java.net.ContentHandler
 * @see     java.net.URLStreamHandler
 * @since   JDK1.0
 */
public interface ContentHandlerFactory {
    /**
     * Creates a new <code>ContentHandler</code> to read an object from 
     * a <code>URLStreamHandler</code>. 
     *
     * @param   mimetype   the MIME type for which a content handler is desired.

     * @return  a new <code>ContentHandler</code> to read an object from a
     *          <code>URLStreamHandler</code>.
     * @see     java.net.ContentHandler
     * @see     java.net.URLStreamHandler
     * @since   JDK1.0
     */
    ContentHandler createContentHandler(String mimetype);
}
