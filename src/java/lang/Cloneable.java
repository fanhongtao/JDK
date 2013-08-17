/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.11, 02/06/02
 * @see     java.lang.CloneNotSupportedException
 * @since   JDK1.0
 */
public interface Cloneable { 

}
