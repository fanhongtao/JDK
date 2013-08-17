/*
 * @(#)FileNameMap.java	1.8 98/09/21
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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
