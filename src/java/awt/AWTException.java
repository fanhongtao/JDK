/*
 * @(#)AWTException.java	1.16 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt;


/**
 * Signals that an Absract Window Toolkit exception has occurred.
 *
 * @version 	1.16 12/19/03
 * @author 	Arthur van Hoff
 */
public class AWTException extends Exception {
 
    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = -1900414231151323879L;

    /**
     * Constructs an instance of <code>AWTException</code> with the 
     * specified detail message. A detail message is an 
     * instance of <code>String</code> that describes this particular
     * exception. 
     * @param   msg     the detail message
     * @since   JDK1.0
     */
    public AWTException(String msg) {
	super(msg);
    }
}
