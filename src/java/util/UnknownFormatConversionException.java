/*
 * @(#)UnknownFormatConversionException.java	1.3 04/06/07
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util;

/**
 * Unchecked exception thrown when an unknown conversion is given.
 *
 * <p> Unless otherwise specified, passing a <tt>null</tt> argument to
 * any method or constructor in this class will cause a {@link
 * NullPointerException} to be thrown.
 *
 * @version 	1.3, 06/07/04
 * @since 1.5
 */
public class UnknownFormatConversionException extends IllegalFormatException {

    private static final long serialVersionUID = 19060418L;

    private String s;

    /**
     * Constructs an instance of this class with the unknown conversion.
     *
     * @param  s
     *         Unknown conversion
     */
    public UnknownFormatConversionException(String s) {
	this.s = s;
    }

    /**
     * Returns the unknown conversion.
     *
     * @return  The unknown conversion.
     */
    public String getConversion() {
	return s;
    }

    // javadoc inherited from Throwable.java
    public String getMessage() {
	return String.format("Conversion = '%s'", s);
    }
}
