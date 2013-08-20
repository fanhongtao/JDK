// $Id: ParserConfigurationException.java,v 1.2 2003/12/06 00:21:41 jsuttor Exp $
/*
 * @(#)ParserConfigurationException.java	1.12 04/07/26
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.xml.parsers;

/**
 * Indicates a serious configuration error.
 *
 * @author <a href="mailto:Jeff.Suttor@Sun.com">Jeff Suttor</a>
 * @version $Revision: 1.2 $, $Date: 2003/12/06 00:21:41 $
 */

public class ParserConfigurationException extends Exception {

    /**
     * Create a new <code>ParserConfigurationException</code> with no
     * detail mesage.
     */

    public ParserConfigurationException() {
        super();
    }

    /**
     * Create a new <code>ParserConfigurationException</code> with
     * the <code>String</code> specified as an error message.
     *
     * @param msg The error message for the exception.
     */
    
    public ParserConfigurationException(String msg) {
        super(msg);
    }

}

