// $Id: XPathExpressionException.java,v 1.3.12.1 2004/06/15 00:05:01 rameshm Exp $

/*
 * @(#)XPathExpressionException.java	1.6 04/07/26
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.xml.xpath;

/**
 * <code>XPathExpressionException</code> represents an error in an XPath expression.</p>
 *
 * @author  <a href="mailto:Norman.Walsh@Sun.com">Norman Walsh</a>
 * @author  <a href="mailto:Jeff.Suttor@Sun.com">Jeff Suttor</a>
 * @version $Revision: 1.3.12.1 $, $Date: 2004/06/15 00:05:01 $
 * @since 1.5
 */
public class XPathExpressionException extends XPathException {

    /**
     * <p>Stream Unique Identifier.</p>
     */
    private static final long serialVersionUID = -1837080260374986980L;

    /**
     * <p>Constructs a new <code>XPathExpressionException</code> with the specified detail <code>message</code>.</p>
     *
     * <p>The <code>cause</code> is not initialized.</p>
     *
     * <p>If <code>message</code> is <code>null</code>, then a <code>NullPointerException</code> is thrown.</p>
     *
     * @param message The detail message.
     */
    public XPathExpressionException(String message) {
        super(message);
    }

    /**
     * <p>Constructs a new <code>XPathExpressionException</code> with the specified <code>cause</code>.</p>
     *
     * <p>If <code>cause</code> is <code>null</code>, then a <code>NullPointerException</code> is thrown.</p>
     *
     * @param cause The cause.
     *
     * @throws NullPointerException if <code>cause</code> is <code>null</code>.
     */
    public XPathExpressionException(Throwable cause) {
        super(cause);
    }
}
