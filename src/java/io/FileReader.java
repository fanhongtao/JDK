/*
 * @(#)FileReader.java	1.8 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 	1.8, 01/11/29
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
