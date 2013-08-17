/*
 * @(#)IncompatibleClassChangeError.java	1.10 97/01/20
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
 * Thrown when an incompatible class change has occurred to some class 
 * definition. The definition of some class, on which the currently 
 * executing method depends, has since changed. 
 *
 * @author  unascribed
 * @version 1.10, 01/20/97
 * @since   JDK1.0
 */
public
class IncompatibleClassChangeError extends LinkageError {
    /**
     * Constructs an <code>IncompatibleClassChangeError</code> with no 
     * detail message. 
     *
     * @since   JDK1.0
     */
    public IncompatibleClassChangeError () {
	super();
    }

    /**
     * Constructs an <code>IncompatibleClassChangeError</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public IncompatibleClassChangeError(String s) {
	super(s);
    }
}
