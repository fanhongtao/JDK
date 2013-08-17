/*
 * @(#)Exception.java	1.22 97/01/20
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

package java.lang;

/**
 * The class <code>Exception</code> and its subclasses are a form of 
 * <code>Throwable</code> that indicates conditions that a reasonable 
 * application might want to catch.
 *
 * @author  Frank Yellin
 * @version 1.22, 01/20/97
 * @see     java.lang.Error
 * @since   JDK1.0
 */
public
class Exception extends Throwable {
    /**
     * Constructs an <code>Exception</code> with no specified detail message. 
     *
     * @since   JDK1.0
     */
    public Exception() {
	super();
    }

    /**
     * Constructs an <code>Exception</code> with the specified detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public Exception(String s) {
	super(s);
    }
}
