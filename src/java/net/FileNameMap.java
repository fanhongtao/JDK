/*
 * @(#)FileNameMap.java	1.11 00/02/02
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.net;

/**
 * A simple interface which provides a mechanism to map
 * between a file name and a MIME type string.
 *
 * @version 	1.11, 02/02/00
 * @author  Steven B. Byrne
 * @since   JDK1.1
 */
public interface FileNameMap {

    /**
     * Gets the MIME type for the specified file name.
     * @param fileName the specified file name
     * @return a <code>String</code> indicating the MIME
     * type for the specified file name.
     */
    public String getContentTypeFor(String fileName);
}
