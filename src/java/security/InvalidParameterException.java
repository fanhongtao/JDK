/*
 * @(#)InvalidParameterException.java	1.9 96/11/23
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

package java.security;

/**
 * This exception is thrown when an invalid parameter is passed 
 * to a method.
 *
 * @author Benjamin Renaud
 * @version 1.9, 00/08/15
 */

public class InvalidParameterException extends IllegalArgumentException {

    /**
     * Constructs an InvalidParameterException with no detail message.
     * A detail message is a String that describes this particular
     * exception.
     */
    public InvalidParameterException() {
	super();
    }

    /**
     * Constructs an InvalidParameterException with the specified
     * detail message.  A detail message is a String that describes
     * this particular exception.
     *
     * @param msg the detail message.  
     */
    public InvalidParameterException(String msg) {
	super(msg);
    }
}
