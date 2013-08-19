/*
 * @(#)Printable.java	1.15 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.print;

import java.awt.Graphics;

/**
 * The <code>Printable</code> interface is implemented 
 * by the <code>print</code> methods of the current
 * page painter, which is called by the printing
 * system to render a page.  When building a 
 * {@link Pageable}, pairs of {@link PageFormat}
 * instances and instances that implement
 * this interface are used to describe each page. The 
 * instance implementing <code>Printable</code> is called to 
 * print the page's graphics.
 * @see java.awt.print.Pageable
 * @see java.awt.print.PageFormat
 * @see java.awt.print.PrinterJob
 */
public interface Printable {

    /**
     * Returned from {@link #print(Graphics, PageFormat, int)} 
     * to signify that the requested page was rendered.
     */
    int PAGE_EXISTS = 0;

    /**
     * Returned from <code>print</code> to signify that the
     * <code>pageIndex</code> is too large and that the requested page
     * does not exist.
     */
    int NO_SUCH_PAGE = 1;

    /**
     * Prints the page at the specified index into the specified 
     * {@link Graphics} context in the specified
     * format.  A <code>PrinterJob</code> calls the 
     * <code>Printable</code> interface to request that a page be
     * rendered into the context specified by 
     * <code>graphics</code>.  The format of the page to be drawn is
     * specified by <code>pageFormat</code>.  The zero based index
     * of the requested page is specified by <code>pageIndex</code>. 
     * If the requested page does not exist then this method returns
     * NO_SUCH_PAGE; otherwise PAGE_EXISTS is returned.
     * The <code>Graphics</code> class or subclass implements the
     * {@link PrinterGraphics} interface to provide additional
     * information.  If the <code>Printable</code> object
     * aborts the print job then it throws a {@link PrinterException}.
     * @param graphics the context into which the page is drawn 
     * @param pageFormat the size and orientation of the page being drawn
     * @param pageIndex the zero based index of the page to be drawn
     * @return PAGE_EXISTS if the page is rendered successfully
     *         or NO_SUCH_PAGE if <code>pageIndex</code> specifies a
     *	       non-existent page.
     * @exception java.awt.print.PrinterException
     *         thrown when the print job is terminated.
     */
    int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
	         throws PrinterException;

}
