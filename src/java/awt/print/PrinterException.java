/*
 * @(#)PrinterException.java	1.9 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
