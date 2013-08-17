/*
 * @(#)FileNotFoundException.java	1.16 98/09/02
 *
 * Copyright 1994-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.io;


/**
 * Signals that an attempt to open the file denoted by a specified pathname
 * has failed.
 *
 * <p> This exception will be thrown by the {@link FileInputStream}, {@link
 * FileOutputStream}, and {@link RandomAccessFile} constructors when a file
 * with the specified pathname does not exist.  It will also be thrown by these
 * constructors if the file does exist but for some reason is inaccessible, for
 * example when an attempt is made to open a read-only file for writing.
 *
 * @author  unascribed
 * @version 1.16, 09/02/98
 * @since   JDK1.0
 */

public class FileNotFoundException extends IOException {

    /**
     * Constructs a <code>FileNotFoundException</code> with
     * <code>null</code> as its error detail message.
     */
    public FileNotFoundException() {
	super();
    }

    /**
     * Constructs a <code>FileNotFoundException</code> with the
     * specified detail message. The string <code>s</code> can be
     * retrieved later by the
     * <code>{@link java.lang.Throwable#getMessage}</code>
     * method of class <code>java.lang.Throwable</code>.
     *
     * @param   s   the detail message.
     */
    public FileNotFoundException(String s) {
	super(s);
    }

    /**
     * Constructs a <code>FileNotFoundException</code> with a detail message
     * consisting of the given pathname string followed by the given reason
     * string.  If the <code>reason</code> argument is <code>null</code> then
     * it will be omitted.  This private constructor is invoked only by native
     * I/O methods.
     *
     * @since JDK1.2
     */
    private FileNotFoundException(String path, String reason) {
	super(path + ((reason == null)
		      ? ""
		      : " (" + reason + ")"));
    }

}
