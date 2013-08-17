/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
