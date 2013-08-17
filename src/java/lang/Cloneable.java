/*
 * @(#)Cloneable.java	1.8 98/09/21
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
 * A class implements the <code>Cloneable</code> interface to 
 * indicate to the {@link java.lang.Object#clone()} method that it 
 * is legal for that method to make a 
 * field-for-field copy of instances of that class. 
 * <p>
 * Attempts to clone instances that do not implement the 
 * <code>Cloneable</code> interface result in the exception 
 * <code>CloneNotSupportedException</code> being thrown. 
 * <p>
 * The interface <tt>Cloneable</tt> declares no methods.
 *
 * @author  unascribed
 * @version 1.8, 09/21/98
 * @see     java.lang.CloneNotSupportedException
 * @since   JDK1.0
 */
public interface Cloneable { 

}
