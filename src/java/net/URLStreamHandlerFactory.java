/*
 * @(#)URLStreamHandlerFactory.java	1.9 98/07/01
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
 * This interface defines a factory for <code>URL</code> stream 
 * protocol handlers. 
 * <p>
 * It is used by the <code>URL</code> class to create a 
 * <code>URLStreamHandler</code> for a specific protocol. 
 *
 * @author  Arthur van Hoff
 * @version 1.9, 07/01/98
 * @see     java.net.URL
 * @see     java.net.URLStreamHandler
 * @since   JDK1.0
 */
public interface URLStreamHandlerFactory {
    /**
     * Creates a new <code>URLStreamHandler</code> instance with the specified
     * protocol.
     *
     * @param   protocol   the protocol ("<code>ftp</code>",
     *                     "<code>http</code>", "<code>nntp</code>", etc.).
     * @return  a <code>URLStreamHandler</code> for the specific protocol.
     * @see     java.io.URLStreamHandler
     * @since   JDK1.0
     */
    URLStreamHandler createURLStreamHandler(String protocol);
}
