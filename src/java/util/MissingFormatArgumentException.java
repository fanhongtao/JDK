/*
 * @(#)MissingFormatArgumentException.java	1.2 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util;

/**
 * Unchecked exception thrown when there is a format specifier which does not
 * have a corresponding argument or if an argument index refers to an argument
 * that does not exist.
 *
 * <p> Unless otherwise specified, passing a <tt>null</tt> argument to any
 * method or constructor in this class will cause a {@link
 * NullPointerException} to be thrown.
 *
 * @version 	1.2, 12/19/03
 * @since 1.5
 */
public class MissingFormatArgumentException extends IllegalFormatException {

    private static final long serialVersionUID = 19190115L;

    private String s;

    /**
     * Constructs an instance of this class with the unmatched format
     * specifier.
     *
     * @param  s
     *         Format specifier which does not have a corresponding argument
     */
    public MissingFormatArgumentException(String s) {
	if (s == null)
	    throw new NullPointerException();
	this.s = s;
    }

    /**
     * Returns the unmatched format specifier.
     *
     * @return  The unmatched format specifier
     */
    public String getFormatSpecifier() {
	return s;
    }

    public String getMessage() {
	return "Format specifier '" + s + "'";
    }
}
