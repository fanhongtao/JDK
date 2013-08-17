/*
 * @(#)CloneNotSupportedException.java	1.3 97/01/20
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
 * Thrown to indicate that the <code>clone</code> method in class 
 * <code>Object</code> has been called to clone an object, but that 
 * the object's class does not implement the <code>Cloneable</code> 
 * interface. 
 * <p>
 * Applications that override the <code>clone</code> method can also 
 * throw this exception to indicate that an object could not or 
 * should not be cloned.
 *
 * @author  unascribed
 * @version 1.3, 01/20/97
 * @see     java.lang.Cloneable
 * @see     java.lang.Object#clone()
 * @since   JDK1.0
 */

public
class CloneNotSupportedException extends Exception {
    /**
     * Constructs a <code>CloneNotSupportedException</code> with no 
     * detail message. 
     *
     * @since   JDK1.0
     */
    public CloneNotSupportedException() {
	super();
    }

    /**
     * Constructs a <code>CloneNotSupportedException</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public CloneNotSupportedException(String s) {
	super(s);
    }
}
