// $Id: XPathException.java,v 1.14.12.2 2004/06/15 00:07:08 rameshm Exp $

/*
 * @(#)XPathException.java	1.8 04/07/26
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.xml.xpath;

import java.io.PrintWriter;

/**
 * <code>XPathException</code> represents a generic XPath exception.</p>
 *
 * @author  <a href="Norman.Walsh@Sun.com">Norman Walsh</a>
 * @author <a href="mailto:Jeff.Suttor@Sun.COM">Jeff Suttor</a>
 * @version $Revision: 1.14.12.2 $, $Date: 2004/06/15 00:07:08 $
 * @since 1.5
 */
public class XPathException extends Exception {

    private final Throwable cause;
    
    /**
     * <p>Stream Unique Identifier.</p>
     */
    private static final long serialVersionUID = -1837080260374986980L;

    /**
     * <p>Constructs a new <code>XPathException</code> with the specified detail <code>message</code>.</p>
     *
     * <p>The <code>cause</code> is not initialized.</p>
     *
     * <p>If <code>message</code> is <code>null</code>, then a <code>NullPointerException</code> is thrown.</p>
     *
     * @param message The detail message.
     */
    public XPathException(String message) {
        super(message);
        if ( message == null ) {
            throw new NullPointerException ( "message can't be null");
        }
        this.cause = null;
    }

    /**
     * <p>Constructs a new <code>XPathException</code> with the specified <code>cause</code>.</p>
     *
     * <p>If <code>cause</code> is <code>null</code>, then a <code>NullPointerException</code> is thrown.</p>
     *
     * @param cause The cause.
     *
     * @throws NullPointerException if <code>cause</code> is <code>null</code>.
     */
    public XPathException(Throwable cause) {
        super();
        this.cause = cause;
        if ( cause == null ) {
            throw new NullPointerException ( "cause can't be null");
        }
    }
    
    public Throwable getCause() {
        return cause;
    }

    public void printStackTrace( java.io.PrintStream s ) {
        if( getCause() != null ) {
            getCause().printStackTrace(s);
          s.println("--------------- linked to ------------------");
        }

        super.printStackTrace(s);
    }
 
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    public void printStackTrace(PrintWriter s) {
        if( getCause() != null ) {
            getCause().printStackTrace(s);
          s.println("--------------- linked to ------------------");
        }

        super.printStackTrace(s);
    }
}
