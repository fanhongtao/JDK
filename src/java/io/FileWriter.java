/*
 * @(#)FileWriter.java	1.5 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
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
 * Convenience class for writing character files.  The constructors of this
 * class assume that the default character encoding and the default byte-buffer
 * size are acceptable.  To specify these values yourself, construct an
 * OutputStreamWriter on a FileOutputStream.
 *
 * @see OutputStreamWriter
 * @see FileOutputStream
 *
 * @version 	1.5, 98/07/01
 * @author	Mark Reinhold
 * @since	JDK1.1
 */

public class FileWriter extends OutputStreamWriter {

    public FileWriter(String fileName) throws IOException {
	super(new FileOutputStream(fileName));
    }

    public FileWriter(String fileName, boolean append) throws IOException {
	super(new FileOutputStream(fileName, append));
    }

    public FileWriter(File file) throws IOException {
	super(new FileOutputStream(file));
    }

    public FileWriter(FileDescriptor fd) {
	super(new FileOutputStream(fd));
    }

}
