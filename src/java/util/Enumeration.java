/*
 * @(#)Enumeration.java	1.12 98/07/01
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

package java.util;

/**
 * An object that implements the Enumeration interface generates a 
 * series of elements, one at a time. Successive calls to the 
 * <code>nextElement</code> method return successive elements of the 
 * series. 
 * <p>
 * For example, to print all elements of a vector <i>v</i>:
 * <blockquote><pre>
 *     for (Enumeration e = v.elements() ; e.hasMoreElements() ;) {
 *         System.out.println(e.nextElement());<br>
 *     }
 * </pre></blockquote>
 * <p>
 * Methods are provided to enumerate through the elements of a 
 * vector, the keys of a hashtable, and the values in a hashtable. 
 * Enumerations are also used to specify the input streams to a 
 * <code>SequenceInputStream</code>. 
 *
 * @see     java.io.SequenceInputStream
 * @see     java.util.Enumeration#nextElement()
 * @see     java.util.Hashtable
 * @see     java.util.Hashtable#elements()
 * @see     java.util.Hashtable#keys()
 * @see     java.util.Vector
 * @see     java.util.Vector#elements()
 *
 * @author  Lee Boynton
 * @version 1.12, 07/01/98
 * @since   JDK1.0
 */
public interface Enumeration {
    /**
     * Tests if this enumeration contains more elements.
     *
     * @return  <code>true</code> if this enumeration contains more elements;
     *          <code>false</code> otherwise.
     * @since   JDK1.0
     */
    boolean hasMoreElements();

    /**
     * Returns the next element of this enumeration.
     *
     * @return     the next element of this enumeration. 
     * @exception  NoSuchElementException  if no more elements exist.
     * @since      JDK1.0
     */
    Object nextElement();
}
