/*
 * @(#)AttributeList.java	1.25 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management;


// java import
import java.util.ArrayList;


/**
 * Represents a list of values for attributes of an
 * MBean. The methods used for the insertion of {@link javax.management.Attribute Attribute} objects in
 * the <CODE>AttributeList</CODE> overrides the corresponding methods in the superclass
 * <CODE>ArrayList</CODE>. This is needed in order to insure that the objects contained
 * in the <CODE>AttributeList</CODE> are only <CODE>Attribute</CODE> objects. This avoids getting
 * an exception when retrieving elements from the <CODE>AttributeList</CODE>.
 *
 * @since 1.5
 */
public class AttributeList extends ArrayList   { 
    

    /* Serial version */
    private static final long serialVersionUID = -4077085769279709076L;

    /**
     * Constructs an empty <CODE>AttributeList</CODE>.
     */
    public AttributeList() { 
	super();
    } 
    
    /**
     * Constructs an empty <CODE>AttributeList</CODE> with the initial capacity specified.
     *
     * @param initialCapacity the initial capacity of the
     * <code>AttributeList</code>, as specified by {@link
     * ArrayList#ArrayList(int)}.
     */    
    public AttributeList(int initialCapacity) { 
	super(initialCapacity);
    } 
    
    /**
     * Constructs an <CODE>AttributeList</CODE> containing the elements of the <CODE>AttributeList</CODE> specified,
     * in the order in which they are returned by the <CODE>AttributeList</CODE>'s iterator.
     * The <CODE>AttributeList</CODE> instance has an initial capacity of 110% of the
     * size of the <CODE>AttributeList</CODE> specified.
     *
     * @param list the <code>AttributeList</code> that defines the initial
     * contents of the new <code>AttributeList</code>.
     *
     * @see ArrayList#ArrayList(java.util.Collection)
     */
    public AttributeList(AttributeList list) { 
	super(list);
    } 
    
    
    /**
     * Adds the <CODE>Attribute</CODE> specified as the last element of the list.
     *
     *@param object  The attribute to be added.
     */     
    public void add(Attribute object)  { 
	super.add(object);
    } 
    
    /**
     * Inserts the attribute specified as an element at the position specified.
     * Elements with an index greater than or equal to the current position are
     * shifted up. If the index is out of range (index < 0 || index >
     * size() a RuntimeOperationsException should be raised, wrapping the
     * java.lang.IndexOutOfBoundsException thrown.
     *
     * @param object  The <CODE>Attribute</CODE> object to be inserted.      
     * @param index The position in the list where the new <CODE>Attribute</CODE> object is to be
     * inserted.
     */     
    public void add(int index, Attribute object)  { 
	try {
	    super.add(index, object);
	}
	catch (IndexOutOfBoundsException e) {
	    throw (new RuntimeOperationsException(e, "The specified index is out of range"));
	}
    } 
    
    /**
     * Sets the element at the position specified to be the attribute specified.
     * The previous element at that position is discarded. If the index is
     * out of range (index < 0 || index > size() a RuntimeOperationsException should
     * be raised, wrapping the java.lang.IndexOutOfBoundsException thrown.
     *
     * @param object  The value to which the attribute element should be set.      
     * @param index  The position specified.
     */          
    public void set(int index, Attribute object)  { 
	try {
	    super.set(index, object);
	}
	catch (IndexOutOfBoundsException e) {
	    throw (new RuntimeOperationsException(e, "The specified index is out of range"));
	}
    } 
    
    /**
     * Appends all the elements in the <CODE>AttributeList</CODE> specified to the end
     * of the list, in the order in which they are returned by the Iterator of
     * the <CODE>AttributeList</CODE> specified.
     *
     * @param list  Elements to be inserted into the list.
     *
     * @return true if this list changed as a result of the call.
     *
     * @see ArrayList#addAll(java.util.Collection)
     */          
    public boolean addAll(AttributeList list)  { 
	return (super.addAll(list));
    } 
    
    /**
     * Inserts all of the elements in the <CODE>AttributeList</CODE> specified into this
     * list, starting at the specified position, in the order in which they
     * are returned by the Iterator of the <CODE>AttributeList</CODE> specified. If
     * the index is out of range (index < 0 || index > size() a RuntimeOperationsException should
     * be raised, wrapping the java.lang.IndexOutOfBoundsException thrown.
     *
     * @param list  Elements to be inserted into the list.      
     * @param index  Position at which to insert the first element from the <CODE>AttributeList</CODE> specified.
     *
     * @return true if this list changed as a result of the call.
     *
     * @see ArrayList#addAll(int, java.util.Collection)
     */        
    public boolean addAll(int index, AttributeList list)  { 
	try {
	    return(super.addAll(index, list));
	}
	catch (IndexOutOfBoundsException e) {
	    throw (new RuntimeOperationsException(e, "The specified index is out of range"));
	}
    } 
    
}
