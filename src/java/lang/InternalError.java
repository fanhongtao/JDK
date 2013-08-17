/*
 * @(#)InternalError.java	1.13 97/01/20
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
 * Thrown to indicate some unexpected internal error has occurred in 
 * the Java Virtual Machine. 
 *
 * @author  unascribed
 * @version 1.13, 01/20/97
 * @since   JDK1.0
 */
public
class InternalError extends VirtualMachineError {
    /**
     * Constructs an <code>InternalError</code> with no detail message. 
     *
     * @since   JDK1.0
     */
    public InternalError() {
	super();
    }

    /**
     * Constructs an <code>InternalError</code> with the specified 
     * detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public InternalError(String s) {
	super(s);
    }
}
