/*
 * @(#)AWTError.java	1.7 97/06/17
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

package java.awt;

/**
 * Thrown when a serious Abstract Window Toolkit error has occurred. 
 *
 * @version 	1.7 06/17/97
 * @author 	Arthur van Hoff
 */
public class AWTError extends Error {

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = -1819846354050686206L;

    /**
     * Constructs an instance of <code>AWTError</code> with the specified 
     * detail message. 
     * @param   msg   the detail message.
     * @since   JDK1.0
     */
    public AWTError(String msg) {
	super(msg);
    }
}
