/*
 * @(#)Error.java	1.8 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.lang;

/**
 * An <code>Error</code> is a subclass of <code>Throwable</code> 
 * that indicates serious problems that a reasonable application 
 * should not try to catch. Most such errors are abnormal conditions. 
 * The <code>ThreadDeath</code> error, though a "normal" condition,
 * is also a subclass of <code>Error</code> because most applications
 * should not try to catch it. 
 * <p>
 * A method is not required to declare in its <code>throws</code> 
 * clause any subclasses of <code>Error</code> that might be thrown 
 * during the execution of the method but not caught, since these 
 * errors are abnormal conditions that should never occur. 
 *
 * @author  Frank Yellin
 * @version 1.8, 07/01/98
 * @see     java.lang.ThreadDeath
 * @since   JDK1.0
 */
public
class Error extends Throwable {
    /**
     * Constructs an <code>Error</code> with no specified detail message.
     *
     * @since   JDK1.0
     */
    public Error() {
	super();
    }

    /**
     * Constructs an Error with the specified detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public Error(String s) {
	super(s);
    }
}
