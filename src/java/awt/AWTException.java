/*
 * @(#)AWTException.java	1.9 98/07/01
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
package java.awt;


/**
 * Signals that an Absract Window Toolkit exception has occurred.
 *
 * @version 	1.9 07/01/98
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
