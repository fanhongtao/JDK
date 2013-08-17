/*
 * @(#)ProviderException.java	1.4 96/11/23
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

/** A runtime exception for Provider exceptions (such as
 * misconfiguration errors), which may be subclassed by Providers to
 * throw specialized, provider-specific runtime errors.
 *
 * @version 1.4, 00/08/15
 * @author Benjamin Renaud */

public class ProviderException extends RuntimeException {

    /**
     * Constructs a ProviderException with no detail message. A
     * detail message is a String that describes this particular
     * exception.
     */
    public ProviderException() {
	super();
    }

    /**
     * Constructs a ProviderException with the specified detail
     * message. A detail message is a String that describes this
     * particular exception.  
     *
     * @param s the detail message.  
     */
    public ProviderException(String s) {
	super(s);
    }
}
