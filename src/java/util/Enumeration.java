/*
 * @(#)Enumeration.java	1.11 97/01/28
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
 * @version 1.11, 01/28/97
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
