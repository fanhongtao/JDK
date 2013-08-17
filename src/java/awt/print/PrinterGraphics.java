/*
 * @(#)PrinterGraphics.java	1.1 98/04/22
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
 * The <code>PrinterGraphics</code> interface is implemented by 
 * {@link java.awt.Graphics} objects that are passed to 
 * {@link Printable} objects to render a page. It allows an 
 * application to find the {@link PrinterJob} object that is 
 * controlling the printing.
 */

public interface PrinterGraphics {

    /**
     * Returns the <code>PrinterJob</code> that is controlling the
     * current rendering request.
     * @return the <code>PrinterJob</code> controlling the current
     * rendering request.
     * @see java.awt.print.Printable
     */
    PrinterJob getPrinterJob();

}
