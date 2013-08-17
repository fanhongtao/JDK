/*
 * @(#)NotSerializableException.java	1.4 97/01/22
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

/** Raised by a class or the serialization runtime when a class
 * may not be serialized. The argument should be the name of the class.
 *
 * @author  unascribed
 * @version 1.4, 01/22/97
 * @since   JDK1.1
 */
public class NotSerializableException extends ObjectStreamException {
    /**
     * @since   JDK1.1
     */
    public NotSerializableException(String classname) {
	super(classname);
    }

    /**
     * @since   JDK1.1
     */
    public NotSerializableException() {
	super();
    }
}
