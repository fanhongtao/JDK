/*
 * @(#)FileWriter.java	1.4 97/01/27
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
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
 * @version 	1.4, 97/01/27
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
