/*
 * @(#)FileNameMap.java	1.9 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.net;

/**
 * A simple interface which provides a mechanism to map between
 * between a file name and a MIME type string.
 *
 * @version 	1.9, 11/29/01
 * @author  Steven B. Byrne
 * @since   JDK1.1
 */
public interface FileNameMap {
    /**
     */
    public String getContentTypeFor(String fileName);
}
