/*
 * @(#)PrinterGraphics.java	1.8 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
