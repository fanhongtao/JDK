/*
 * @(#)WriteAbortedException.java	1.3 97/01/22
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

package java.io;

/*
 *
 * @author  unascribed
 * @version 1.3, 01/22/97
 * @since   JDK1.1
 */
public class WriteAbortedException extends ObjectStreamException {
    /*
     * @since   JDK1.1
     */
    public Exception detail;

    /**
     * A WriteAbortedException is thrown during a read when one of the
     * ObjectStreamExceptions was thrown during writing.  The exception
     * that terminated the write can be found in the detail field.
     * The stream is reset to it's initial state, all references to
     * objects already deserialized are discarded.
     * @since   JDK1.1
     */
    public WriteAbortedException(String s, Exception ex) { 
	super(s); 
	detail = ex;
    }

    /**
     * Produce the message, include the message from the nested
     * exception if there is one.
     * @since   JDK1.1
     */
    public String getMessage() {
	if (detail == null) 
	    return super.getMessage();
	else
	    return super.getMessage() + "; " + detail.toString();
    }
}
