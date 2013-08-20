/*
 * @(#)RoleUnresolvedList.java	1.21 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management.relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * A RoleUnresolvedList represents a list of RoleUnresolved objects,
 * representing roles not retrieved from a relation due to a problem
 * encountered when trying to access (read or write to roles).
 *
 * @since 1.5
 */
public class RoleUnresolvedList extends ArrayList {

    /* Serial version */
    private static final long serialVersionUID = 4054902803091433324L;

    //
    // Constructors
    //

    /**
     * Constructs an empty RoleUnresolvedList.
     */
    public RoleUnresolvedList() {
	super();
	return;
    }

    /**
     * Constructs an empty RoleUnresolvedList with the initial capacity
     * specified.
     *
     * @param theInitialCapacity  initial capacity
     */
    public RoleUnresolvedList(int theInitialCapacity) {
	super(theInitialCapacity);
	return;
    }

    /**
     * Constructs a RoleUnresolvedList containing the elements of the
     * List specified, in the order in which they are returned
     * by the List's iterator. The RoleUnresolvedList instance has
     * an initial capacity of 110% of the size of the List
     * specified.
     *
     * @param theList  list of RoleUnresolved objects
     *
     * @exception IllegalArgumentException  if:
     * <P>- null parameter
     * <P>or
     * <P>- an element in the List is not a RoleUnresolved
     */
    public RoleUnresolvedList(List theList)
	throws IllegalArgumentException {

	if (theList == null) {
	    String excMsg = "Invalid parameter";
	    throw new IllegalArgumentException(excMsg);
	}

	int i = 0;
	for (Iterator eltIter = theList.iterator();
	     eltIter.hasNext();) {
	    Object currElt = eltIter.next();
	    if (!(currElt instanceof RoleUnresolved)) {
		StringBuffer excMsgStrB = new StringBuffer();
		String excMsg = "An element is not a RoleUnresolved at index ";
		excMsgStrB.append(excMsg);
		excMsgStrB.append(i);
		throw new IllegalArgumentException(excMsgStrB.toString());
	    }
	    i++;
	    super.add(currElt);
	}
	return;
    }

    //
    // Accessors
    //

    /**
     * Adds the RoleUnresolved specified as the last element of the list.
     *
     * @param theRoleUnres - the unresolved role to be added.
     *
     * @exception IllegalArgumentException  if the unresolved role is null.
     */
    public void add(RoleUnresolved theRoleUnres)
	throws IllegalArgumentException {

	if (theRoleUnres == null) {
	    String excMsg = "Invalid parameter";
	    throw new IllegalArgumentException(excMsg);
	}
	super.add(theRoleUnres);
	return;
    }

    /**
     * Inserts the unresolved role specified as an element at the position
     * specified.
     * Elements with an index greater than or equal to the current position are
     * shifted up.
     *
     * @param index - The position in the list where the new
     * RoleUnresolved object is to be inserted.
     * @param theRoleUnres - The RoleUnresolved object to be inserted.
     *
     * @exception IllegalArgumentException  if the unresolved role is null.
     * @exception IndexOutOfBoundsException if index is out of range
     * (<code>index &lt; 0 || index &gt; size()</code>).
     */
    public void add(int index,
		    RoleUnresolved theRoleUnres)
	throws IllegalArgumentException,
	       IndexOutOfBoundsException {

	if (theRoleUnres == null) {
	    String excMsg = "Invalid parameter";
	    throw new IllegalArgumentException(excMsg);
	}

	super.add(index, theRoleUnres);
	return;
    }

    /**
     * Sets the element at the position specified to be the unresolved role
     * specified.
     * The previous element at that position is discarded.
     *
     * @param index - The position specified.
     * @param theRoleUnres - The value to which the unresolved role element
     * should be set.
     *
     * @exception IllegalArgumentException   if the unresolved role is null.
     * @exception IndexOutOfBoundsException if index is out of range
     * (<code>index &lt; 0 || index &gt;= size()</code>).
     */
     public void set(int index,
		     RoleUnresolved theRoleUnres)
	 throws IllegalArgumentException,
                IndexOutOfBoundsException {

	if (theRoleUnres == null) {
	    String excMsg = "Invalid parameter";
	    throw new IllegalArgumentException(excMsg);
	}

	super.set(index, theRoleUnres);
	return;
     }

    /**
     * Appends all the elements in the RoleUnresolvedList specified to the end
     * of the list, in the order in which they are returned by the Iterator of
     * the RoleUnresolvedList specified.
     *
     * @param theRoleUnresolvedList - Elements to be inserted into the list
     * (can be null).
     *
     * @return true if this list changed as a result of the call.
     *
     * @exception IndexOutOfBoundsException  if accessing with an index
     * outside of the list.
     */
    public boolean addAll(RoleUnresolvedList theRoleUnresolvedList)
	throws IndexOutOfBoundsException {

	if (theRoleUnresolvedList == null) {
	    return true;
	}

	return (super.addAll(theRoleUnresolvedList));
    }

    /**
     * Inserts all of the elements in the RoleUnresolvedList specified into
     * this list, starting at the specified position, in the order in which
     * they are returned by the Iterator of the RoleUnresolvedList specified.
     *
     * @param index - Position at which to insert the first element from the
     * RoleUnresolvedList specified.
     * @param theRoleUnresolvedList - Elements to be inserted into the list.
     *
     * @return true if this list changed as a result of the call.
     *
     * @exception IllegalArgumentException  if the role is null.
     * @exception IndexOutOfBoundsException if index is out of range
     * (<code>index &lt; 0 || index &gt; size()</code>).
     */
    public boolean addAll(int index,
			  RoleUnresolvedList theRoleUnresolvedList)
	throws IllegalArgumentException,
               IndexOutOfBoundsException {

	if (theRoleUnresolvedList == null) {
	    String excMsg = "Invalid parameter";
	    throw new IllegalArgumentException(excMsg);
	}

	return (super.addAll(index, theRoleUnresolvedList));
    }
}
