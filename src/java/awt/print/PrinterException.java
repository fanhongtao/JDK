/*
 * @(#)PrinterException.java	1.4 98/10/19
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.awt.print;

/**
 * The <code>PrinterException</code> class and its subclasses are used 
 * to indicate that an exceptional condition has occurred in the print
 * system.
 */

public class PrinterException extends Exception {

    /**
     * Constructs a new <code>PrinterException</code> object
     * without a detail message.
     */
    public PrinterException() {

    }

    /**
     * Constructs a new <code>PrinterException</code> object
     * with the specified detail message.
     * @param msg the message to generate when a 
     * <code>PrinterException</code> is thrown
     */
    public PrinterException(String msg) {
        super(msg);
    }
}
