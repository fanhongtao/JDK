/*
 * @(#)FileReader.java	1.5 98/07/01
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
 * Convenience class for reading character files.  The constructors of this
 * class assume that the default character encoding and the default byte-buffer
 * size are appropriate.  To specify these values yourself, construct an
 * InputStreamReader on a FileInputStream.
 *
 * @see InputStreamReader
 * @see FileInputStream
 *
 * @version 	1.5, 98/07/01
 * @author	Mark Reinhold
 * @since	JDK1.1
 */

public class FileReader extends InputStreamReader {

    public FileReader(String fileName) throws FileNotFoundException {
	super(new FileInputStream(fileName));
    }

    public FileReader(File file) throws FileNotFoundException {
	super(new FileInputStream(file));
    }

    public FileReader(FileDescriptor fd) {
	super(new FileInputStream(fd));
    }

}
