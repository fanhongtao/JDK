/*
 * @(#)ClassCircularityError.java	1.10 98/09/21
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
 * Thrown when a circularity has been detected while initializing a class.
 *
 * @author     unascribed
 * @version    1.10, 09/21/98
 * @since      JDK1.0
 */
public class ClassCircularityError extends LinkageError {
    /**
     * Constructs a <code>ClassCircularityError</code> with no detail  message.
     */
    public ClassCircularityError() {
	super();
    }

    /**
     * Constructs a <code>ClassCircularityError</code> with the 
     * specified detail message. 
     *
     * @param      s   the detail message.
     */
    public ClassCircularityError(String s) {
	super(s);
    }
}
