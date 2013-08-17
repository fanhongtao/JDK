/*
 * @(#)RuntimeException.java	1.4 97/01/20
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
 * <code>RuntimeException</code> is the superclass of those 
 * exceptions that can be thrown during the normal operation of the 
 * Java Virtual Machine. 
 * <p>
 * A method is not required to declare in its <code>throws</code> 
 * clause any subclasses of <code>RuntimeException</code> that might 
 * be thrown during the execution of the method but not caught. 
 *
 *
 * @author  Frank Yellin
 * @version 1.4, 01/20/97
 * @since   JDK1.0
 */
public
class RuntimeException extends Exception {
    /**
     * Constructs a <code>RuntimeException</code> with no detail  message.
     *
     * @since   JDK1.0
     */
    public RuntimeException() {
	super();
    }

    /**
     * Constructs a <code>RuntimeException</code> with the specified 
     * detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public RuntimeException(String s) {
	super(s);
    }
}
