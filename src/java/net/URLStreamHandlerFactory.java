/*
 * @(#)URLStreamHandlerFactory.java	1.18 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.18, 12/19/03
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
     * @see     java.net.URLStreamHandler
     */
    URLStreamHandler createURLStreamHandler(String protocol);
}
