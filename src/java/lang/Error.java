/*
 * @(#)Error.java	1.9 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.9, 12/10/01
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
