/*
 * @(#)Error.java	1.2 00/01/12
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
 * @version 1.10, 09/21/98
 * @see     java.lang.ThreadDeath
 * @since   JDK1.0
 */
public
class Error extends Throwable {
    /**
     * Constructs an <code>Error</code> with no specified detail message.
     */
    public Error() {
	super();
    }

    /**
     * Constructs an Error with the specified detail message. 
     *
     * @param   s   the detail message.
     */
    public Error(String s) {
	super(s);
    }
}
