/*
 * @(#)LoaderHandler.java	1.4 97/02/11
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
 */

package java.rmi.server;

import java.net.MalformedURLException;
import java.net.URL;

public interface LoaderHandler {
    /**
     * Find loader handler package prefix: assumes that the implementation of
     * the LoaderHandler class is located in the package defined by the
     * prefix.
     */
    final static String packagePrefix =
    System.getProperty("java.rmi.loader.packagePrefix", "sun.rmi.server");

    /**
     * Load class using java.rmi.server.codebase property.
     *
     * @exception java.lang.ClassNotFoundException if the class could not be
     *              found.
     * @exception java.net.MalformedURLException   if the URL is malformed.
     */
    Class loadClass(String name)
	throws MalformedURLException, ClassNotFoundException;

    /**
     * Load class from codebase URL specified.
     *
     * @exception java.lang.ClassNotFoundException if the class could not be
     *              found.
     * @exception java.net.MalformedURLException   if the URL is malformed.
     */
    Class loadClass(URL codebase, String name)
	throws MalformedURLException, ClassNotFoundException;

    /**
     * Returns the security context of the given class loader
     * (e.g., a URL)
     */
    Object getSecurityContext(ClassLoader loader);
    
}
