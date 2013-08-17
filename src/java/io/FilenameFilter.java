/*
 * @(#)FilenameFilter.java	1.15 98/07/01
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
 * Instances of classes that implement this interface are used to 
 * filter filenames. These instances are used to filter directory 
 * listings in the <code>list</code> method of class 
 * <code>File</code>, and by the Abstract Window Toolkit's file 
 * dialog component. 
 *
 * @author  Arthur van Hoff
 * @author  Jonathan Payne
 * @version 1.15, 07/01/98
 * @see     java.awt.FileDialog#setFilenameFilter(java.io.FilenameFilter)
 * @see     java.io.File
 * @see     java.io.File#list(java.io.FilenameFilter)
 * @since   JDK1.0
 */
public
interface FilenameFilter {
    /**
     * Tests if a specified file should be included in a file list.
     *
     * @param   dir    the directory in which the file was found.
     * @param   name   the name of the file.
     * @return  <code>true</code> if the name should be included in the file
     *          list; <code>false</code> otherwise.
     * @since   JDK1.0
     */
    boolean accept(File dir, String name);
}
