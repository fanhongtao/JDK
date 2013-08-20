/*
 * @(#)RoleList.java	1.20 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management.relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Collection; // for Javadoc

/**
 * A RoleList represents a list of roles (Role objects). It is used as
 * parameter when creating a relation, and when trying to set several roles in
 * a relation (via 'setRoles()' method). It is returned as part of a
 * RoleResult, to provide roles successfully retrieved.
 *
 * @since 1.5
 */
public class RoleList extends ArrayList {

    /* Serial version */
    private static final long serialVersionUID = 5568344346499649313L;

    //
    // Constructors
    //

    /**
     * Constructs an empty RoleList.
     */
    public RoleList() {
	super();
	return;
    }

    /**
     * Constructs an empty RoleList with the initial capacity
     * specified.
     *
     * @param theInitialCapacity  initial capacity
     */
    public RoleList(int theInitialCapacity) {
	super(theInitialCapacity);
	return;
    }

    /**
     * Constructs a RoleList containing the elements of the
     * List specified, in the order in which they are returned
     * by the List's iterator. The RoleList instance has
     * an initial capacity of 110% of the size of the List
     * specified.
     *
     * @param theList  list of Role objects
     *
     * @exception IllegalArgumentException  if:
     * <P>- null parameter
     * <P>or
     * <P>- an element in the List is not a Role
     */
    public RoleList(List theList)
	throws IllegalArgumentException {

	if (theList == null) {
	    String excMsg = "Invalid parameter";
	    throw new IllegalArgumentException(excMsg);
	}

	int i = 0;
	for (Iterator eltIter = theList.iterator();
	     eltIter.hasNext();) {
	    Object currElt = eltIter.next();
	    if (!(currElt instanceof Role)) {
		StringBuffer excMsgStrB = new StringBuffer();
		String excMsg = "An element is not a Role at index ";
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
     * Adds the Role specified as the last element of the list.
     *
     * @param theRole  the role to be added.
     *
     * @exception IllegalArgumentException  if the role is null.
     */
    public void add(Role theRole)
	throws IllegalArgumentException {

	if (theRole == null) {
	    String excMsg = "Invalid parameter";
	    throw new IllegalArgumentException(excMsg);
	}
	super.add(theRole);
	return;
    }

    /**
     * Inserts the role specified as an element at the position specified.
     * Elements with an index greater than or equal to the current position are
     * shifted up.
     *
     * @param theIndex  The position in the list where the new Role
     * object is to be inserted.
     * @param theRole  The Role object to be inserted.
     *
     * @exception IllegalArgumentException  if the role is null.
     * @exception IndexOutOfBoundsException  if accessing with an index
     * outside of the list.
     */
    public void add(int theIndex,
		    Role theRole)
	throws IllegalArgumentException,
               IndexOutOfBoundsException {

	if (theRole == null) {
	    String excMsg = "Invalid parameter";
	    throw new IllegalArgumentException(excMsg);
	}

	super.add(theIndex, theRole);
	return;
    }

    /**
     * Sets the element at the position specified to be the role
     * specified.
     * The previous element at that position is discarded.
     *
     * @param theIndex  The position specified.
     * @param theRole  The value to which the role element should be set.
     *
     * @exception IllegalArgumentException  if the role is null.
     * @exception IndexOutOfBoundsException  if accessing with an index
     * outside of the list.
     */
     public void set(int theIndex,
		     Role theRole)
	 throws IllegalArgumentException,
	        IndexOutOfBoundsException {

	if (theRole == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	super.set(theIndex, theRole);
	return;
     }

    /**
     * Appends all the elements in the RoleList specified to the end
     * of the list, in the order in which they are returned by the Iterator of
     * the RoleList specified.
     *
     * @param theRoleList  Elements to be inserted into the list (can be null)
     *
     * @return true if this list changed as a result of the call.
     *
     * @exception IndexOutOfBoundsException  if accessing with an index
     * outside of the list.
     *
     * @see ArrayList#addAll(Collection)
     */
    public boolean addAll(RoleList theRoleList)
	throws IndexOutOfBoundsException {

	if (theRoleList == null) {
	    return true;
	}

	return (super.addAll(theRoleList));
    }

    /**
     * Inserts all of the elements in the RoleList specified into this
     * list, starting at the specified position, in the order in which they are
     * returned by the Iterator of the RoleList specified.
     *
     * @param theIndex  Position at which to insert the first element from the
     * RoleList specified.
     * @param theRoleList  Elements to be inserted into the list.
     *
     * @return true if this list changed as a result of the call.
     *
     * @exception IllegalArgumentException  if the role is null.
     * @exception IndexOutOfBoundsException  if accessing with an index
     * outside of the list.
     *
     * @see ArrayList#addAll(int, Collection)
     */
    public boolean addAll(int theIndex,
			  RoleList theRoleList)
	throws IllegalArgumentException,
               IndexOutOfBoundsException {

	if (theRoleList == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	return (super.addAll(theIndex, theRoleList));
    }
}
