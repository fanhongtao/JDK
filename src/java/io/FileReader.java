/*
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
 * @version 	1.11, 02/02/06
 * @author	Mark Reinhold
 * @since	JDK1.1
 */
public class FileReader extends InputStreamReader {

   /**
    * Creates a new <tt>FileReader</tt>, given the name of the
    * file to read from.
    *
    * @param fileName the name of the file to read from
    * @throws <tt>FileNotFoundException</tt> if the specified 
    * file is not found
    */
    public FileReader(String fileName) throws FileNotFoundException {
	super(new FileInputStream(fileName));
    }

   /**
    * Creates a new <tt>FileReader</tt>, given the <tt>File</tt> 
    * to read from.
    *
    * @param file the <tt>File</tt> to read from
    * @throws <tt>FileNotFoundException</tt> if the specified 
    * file is not found
    */
    public FileReader(File file) throws FileNotFoundException {
	super(new FileInputStream(file));
    }

   /**
    * Creates a new <tt>FileReader</tt>, given the 
    * <tt>FileDescriptor</tt> to read from.
    *
    * @param fd the FileDescriptor to read from
    */
    public FileReader(FileDescriptor fd) {
	super(new FileInputStream(fd));
    }

}

