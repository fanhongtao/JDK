/*
 * @(#)FileNameMap.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.net;

/**
 * A simple interface which provides a mechanism to map between
 * between a file name and a MIME type string.
 *
 * @version 	1.8, 09/21/98
 * @author  Steven B. Byrne
 * @since   JDK1.1
 */
public interface FileNameMap {
    /**
     */
    public String getContentTypeFor(String fileName);
}
