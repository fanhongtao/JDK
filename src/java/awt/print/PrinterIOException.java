/*
 * @(#)PrinterIOException.java	1.7 98/10/16 
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

import java.io.IOException;

/**
 * The <code>PrinterIOException</code> class is a subclass of
 * {@link PrinterException} and is used to indicate that an IO error 
 * of some sort has occurred while printing.
 */

public class PrinterIOException extends PrinterException {

    /**
     * The IO error that terminated the print job.
     * @serial
     */
    private IOException mException;

    /**
     * Constructs a new <code>PrinterIOException</code>
     * with the string representation of the specified
     * {@link IOException}.
     * @param exception the specified <code>IOException</code>
     */
    public PrinterIOException(IOException exception) {
        super(exception.toString());
        mException = exception;
    }

    /**
     * Returns the <code>IOException</code> that terminated 
     * the print job.
     * @return the <code>IOException</code> that terminated
     * the print job.
     * @see java.io.IOException
     */
    public IOException getIOException() {
        return mException;
    }

}
