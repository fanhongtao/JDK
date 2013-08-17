/*
 * @(#)Stack.java	1.17 98/07/01
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
 * The <code>Stack</code> class represents a last-in-first-out 
 * (LIFO) stack of objects. 
 *
 * @author  Jonathan Payne
 * @version 1.17, 07/01/98
 * @since   JDK1.0
 */
public
class Stack extends Vector {
    /**
     * Pushes an item onto the top of this stack. 
     *
     * @param   item   the item to be pushed onto this stack.
     * @return  the <code>item</code> argument.
     * @since   JDK1.0
     */
    public Object push(Object item) {
	addElement(item);

	return item;
    }

    /**
     * Removes the object at the top of this stack and returns that 
     * object as the value of this function. 
     *
     * @return     The object at the top of this stack.
     * @exception  EmptyStackException  if this stack is empty.
     * @since      JDK1.0
     */
    public synchronized Object pop() {
	Object	obj;
	int	len = size();

	obj = peek();
	removeElementAt(len - 1);

	return obj;
    }

    /**
     * Looks at the object at the top of this stack without removing it 
     * from the stack. 
     *
     * @return     the object at the top of this stack. 
     * @exception  EmptyStackException  if this stack is empty.
     * @since      JDK1.0
     */
    public synchronized Object peek() {
	int	len = size();

	if (len == 0)
	    throw new EmptyStackException();
	return elementAt(len - 1);
    }

    /**
     * Tests if this stack is empty.
     *
     * @return  <code>true</code> if this stack is empty;
     *          <code>false</code> otherwise.
     * @since   JDK1.0
     */
    public boolean empty() {
	return size() == 0;
    }

    /**
     * Returns where an object is on this stack. 
     *
     * @param   o   the desired object.
     * @return  the distance from the top of the stack where the object is]
     *          located; the return value <code>-1</code> indicates that the
     *          object is not on the stack.
     * @since   JDK1.0
     */
    public synchronized int search(Object o) {
	int i = lastIndexOf(o);

	if (i >= 0) {
	    return size() - i;
	}
	return -1;
    }

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = 1224463164541339165L;
}
