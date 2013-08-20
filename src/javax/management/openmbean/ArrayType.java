/*
 * @(#)ArrayType.java	3.24 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package javax.management.openmbean;


// java import
//
import java.io.Serializable;

// jmx import
//


/**
 * The <code>ArrayType</code> class is the <i>open type</i> class whose instances describe 
 * all <i>open data</i> values which are n-dimensional arrays of <i>open data</i> values.
 *
 * @version     3.24  03/12/19
 * @author      Sun Microsystems, Inc.
 *
 * @since 1.5
 * @since.unbundled JMX 1.1
 */
public class ArrayType 
    extends OpenType
    implements Serializable {

    /* Serial version */
    static final long serialVersionUID = 720504429830309770L;
    
    /**
     * @serial The dimension of arrays described by this {@link ArrayType} instance
     */
    private int dimension;
    
    /**
     * @serial The <i>open type</i> of element values contained in the arrays described by
     *	       this {@link ArrayType} instance
     */
    private OpenType elementType;

    private transient Integer  myHashCode = null;	// As this instance is immutable, these two values
    private transient String   myToString = null;	// need only be calculated once.


    /* *** Constructor *** */

    /**
     * Constructs an <tt>ArrayType</tt> instance describing <i>open data</i> values which are 
     * arrays with dimension <var>dimension</var> of elements whose <i>open type</i> is <var>elementType</var>.
     * <p>
     * When invoked on an <tt>ArrayType</tt> instance, the {@link OpenType#getClassName() getClassName} method
     * returns the class name of the array instances it describes (following the rules defined by the 
     * {@link Class#getName() getName} method of <code>java.lang.Class</code>), not the class name of the array elements
     * (which is returned by a call to <tt>getElementOpenType().getClassName()</tt>).
     * <p>
     * The internal field corresponding to the type name of this <code>ArrayType</code> instance is also set to
     * the class name of the array instances it describes. 
     * In other words, the methods <code>getClassName</code> and <code>getTypeName</code> return the same string value.
     * The internal field corresponding to the description of this <code>ArrayType</code> instance is set to a string value
     * which follows the following template:<br>
     * <tt><i>&lt;dimension&gt;</i>-dimension array of <i>&lt;element_class_name&gt;</i></tt>
     * <p>
     * As an example, the following piece of code:
     * <pre>
     * ArrayType t = new ArrayType(3, SimpleType.STRING);
     * System.out.println("array class name       = "+ t.getClassName());
     * System.out.println("element class name     = "+ t.getElementOpenType().getClassName());
     * System.out.println("array type name        = "+ t.getTypeName());
     * System.out.println("array type description = "+ t.getDescription());
     * </pre>
     * would produce the following output:
     * <pre>
     * array class name       = [[[java.lang.String;
     * element class name     = java.lang.String
     * array type name        = [[[java.lang.String;
     * array type description = 3-dimension array of java.lang.String
     * </pre>
     * 
     * @param  dimension  the dimension of arrays described by this <tt>ArrayType</tt> instance;
     *			  must be greater than or equal to 1.
     *
     * @param  elementType  the <i>open type</i> of element values contained in the arrays described by
     *			    this <tt>ArrayType</tt> instance; must be an instance of either
     *			    <tt>SimpleType</tt>, <tt>CompositeType</tt> or <tt>TabularType</tt>.
     *
     * @throws IllegalArgumentException  if <var>dimension</var> is not a positive integer
     *
     * @throws OpenDataException  if <var>elementType</var> is an instance of <tt>ArrayType</tt>
     */
    public ArrayType(int      dimension,
		     OpenType elementType) throws OpenDataException {
	
	// Check and construct state defined by parent.
	//
	super(buildArrayClassName(dimension, elementType.getClassName()), 
	      buildArrayClassName(dimension, elementType.getClassName()), 
	      String.valueOf(dimension) +"-dimension array of "+ elementType.getClassName());
	
	// Check and construct state specific to ArrayType
	//
	this.dimension   = dimension;   // already checked >=1 in buildArrayClassName
	this.elementType = elementType; // cannot be ArrayType: super() would throw exception on the built classname
    }

    /**
     *
     */
    private static String buildArrayClassName(int dimension, String elementClassName) throws OpenDataException {

	if (dimension < 1) {
	    throw new IllegalArgumentException("Value of argument dimension must be greater than 0");
	}

	StringBuffer result = new StringBuffer();

	for (int i=1; i<dimension; i++) { // add (dimension - 1) '[' characters
	    result.append('[');
	}
	result.append("[L");
	result.append(elementClassName);
	result.append(';');

	return result.toString();
    }


    /* *** ArrayType specific information methods *** */

    /**
     * Returns the dimension of arrays described by this <tt>ArrayType</tt> instance.
     *
     * @return the dimension.
     */
    public int getDimension() {

	return dimension;
    }

    /**
     * Returns the <i>open type</i> of element values contained in the arrays described by this <tt>ArrayType</tt> instance.
     *
     * @return the element type.
     */
    public OpenType getElementOpenType() {

	return elementType;
    }

    /**
     * Tests whether <var>obj</var> is a value for this <code>ArrayType</code> instance.
     * <p>
     * This method returns <code>true</code> if and only if <var>obj</var> is not null, <var>obj</var> is an array
     * and any one of the following is <tt>true</tt>:
     * <p><ul>
     * <li>if this <code>ArrayType</code> instance describes an array of <tt>SimpleType</tt> elements,
     * <var>obj</var>'s class name is the same as the className field defined for this <code>ArrayType</code> instance
     * (ie the class name returned by the {@link OpenType#getClassName() getClassName} method, 
     *  which includes the dimension information),<br>&nbsp;
     * </li>
     * <li>if this <code>ArrayType</code> instance describes an array 
     * of classes implementing the TabularData interface or the CompositeData interface, 
     * <var>obj</var> is assignable to such a declared array,
     * and each element contained in <var>obj</var> is either null or a valid value for the element's open type
     * specified by this <code>ArrayType</code> instance.
     * </li></ul>
     * <p>
     *
     * @param obj the object to be tested.
     *
     * @return <code>true</code> if <var>obj</var> is a value for this <code>ArrayType</code> instance.
     */
    public boolean isValue(Object obj) {

	// if obj is null, return false
	//
	if (obj == null) {
	    return false;
	}

	Class  objClass     = obj.getClass();
	String objClassName = objClass.getName();

	// if obj is not an array, return false
	//
	if ( ! objClass.isArray() ) {
	    return false;
	}

	// Test if obj's class name is the same as for the array values that this instance describes
	// (this is fine if elements are of simple types, which are final classes)
	//
	if ( this.getClassName().equals(objClassName) ) {
	    return true;
	}

	// In case this ArrayType instance describes an array of classes implementing the TabularData or CompositeData interface,
	// we first check for the assignability of obj to such an array of TabularData or CompositeData, 
	// which ensures that:
	//  . obj is of the the same dimension as this ArrayType instance, 
	//  . it is declared as an array of elements which are either all TabularData or all CompositeData.
	//
	// If the assignment check is positive, 
	// then we have to check that each element in obj is of the same TabularType or CompositeType 
	// as the one described by this ArrayType instance.
	//
	// [About assignment check, note that the call below returns true: ]
	// [Class.forName("[Lpackage.CompositeData;").isAssignableFrom(Class.forName("[Lpackage.CompositeDataImpl;)")); ]
	//
	if ( (this.elementType.getClassName().equals(TabularData.class.getName()))  ||
	     (this.elementType.getClassName().equals(CompositeData.class.getName()))   ) {

	    /* this.getClassName() is
	     * "[Ljavax.management.openmbean.TabularData;" or the same
	     * thing for CompositeData, either one optionally preceded
	     * by n '[' characters.  So the class is necessarily known
	     * to the ClassLoader of ArrayType, and Class.forName is
	     * safe.  */
	    Class targetClass;
	    try {
		targetClass = Class.forName(this.getClassName());
	    } catch (ClassNotFoundException e) { // should not happen 
		return false; 
	    }
	    // assignment check: return false if negative
	    if  ( ! targetClass.isAssignableFrom(objClass) ) {
		return false;
	    }

	    // check that all elements in obj are valid values for this ArrayType
	    if ( ! checkElementsType( (Object[]) obj, this.dimension) ) { // we know obj's dimension is this.dimension
		return false;
	    }

	    return true;
	}

	// if previous tests did not return, then obj is not a value for this ArrayType instance
	return false;
    }

    /**
     * Returns true if and only if all elements contained in the array argument x_dim_Array of dimension dim
     * are valid values (ie either null or of the right openType) 
     * for the element open type specified by this ArrayType instance.
     * 
     * This method's implementation uses recursion to go down the dimensions of the array argument.
     */
    private boolean checkElementsType(Object[] x_dim_Array, int dim) {

	// if the elements of x_dim_Array are themselves array: go down recursively....
	if ( dim > 1 ) {
	    for (int i=0; i<x_dim_Array.length; i++) {
		if ( ! checkElementsType((Object[])x_dim_Array[i], dim-1) ) {
		    return false;
		}
	    }
	    return true;
	}
	// ...else, for a non-empty array, each element must be a valid value: either null or of the right openType
	else {
	    for (int i=0; i<x_dim_Array.length; i++) {
		if ( (x_dim_Array[i] != null) && (! this.getElementOpenType().isValue(x_dim_Array[i])) ) {
		    return false;
		}
	    }
	    return true;
	}
    }



    /* *** Methods overriden from class Object *** */

    /**
     * Compares the specified <code>obj</code> parameter with this <code>ArrayType</code> instance for equality.
     * <p> 
     * Two <code>ArrayType</code> instances are equal if and only if they describes array instances
     * which have the same dimension and elements' open type. 
     * 
     * @param  obj  the object to be compared for equality with this <code>ArrayType</code> instance;
     *		    if <var>obj</var> is <code>null</code> or is not an instance of the class <code>ArrayType</code>, 
     *              <code>equals</code> returns <code>false</code>.
     * 
     * @return  <code>true</code> if the specified object is equal to this <code>ArrayType</code> instance.
     */
    public boolean equals(Object obj) {

	// if obj is null, return false
	//
	if (obj == null) {
	    return false;
	}

	// if obj is not an ArrayType, return false
	//
	ArrayType other;
	try {
	    other = (ArrayType) obj;
	} catch (ClassCastException e) {
	    return false;
	}

	// if other's dimension is different than this instance's, return false
	//
	if (other.dimension != this.dimension) {
	    return false;
	}

	// Test if other's elementType field is the same as for this instance
	//
	return this.elementType.equals(other.elementType);
    }

    /**
     * Returns the hash code value for this <code>ArrayType</code> instance.
     * <p>
     * The hash code of a <code>ArrayType</code> instance is the sum of the hash codes
     * of all elements of information used in <code>equals</code> comparisons 
     * (ie: dimension and elements' type). 
     * This ensures that <code> t1.equals(t2) </code> implies that <code> t1.hashCode()==t2.hashCode() </code> 
     * for any two <code>ArrayType</code> instances <code>t1</code> and <code>t2</code>, 
     * as required by the general contract of the method
     * {@link Object#hashCode() Object.hashCode()}.
     * <p>
     * As <code>ArrayType</code> instances are immutable, the hash code for this instance is calculated once,
     * on the first call to <code>hashCode</code>, and then the same value is returned for subsequent calls.
     *
     * @return  the hash code value for this <code>ArrayType</code> instance
     */
    public int hashCode() {

	// Calculate the hash code value if it has not yet been done (ie 1st call to hashCode())
	//
	if (myHashCode == null) {
	    int value = 0;
	    value += this.dimension;
	    value += this.elementType.hashCode();
	    myHashCode = new Integer(value);
	}
	
	// return always the same hash code for this instance (immutable)
	//
	return myHashCode.intValue();
    }

    /**
     * Returns a string representation of this <code>ArrayType</code> instance.
     * <p>
     * The string representation consists of 
     * the name of this class (ie <code>javax.management.openmbean.ArrayType</code>), the type name,
     * the dimension and elements' type defined for this instance, 
     * <p>
     * As <code>ArrayType</code> instances are immutable, the string representation for this instance is calculated once,
     * on the first call to <code>toString</code>, and then the same value is returned for subsequent calls.
     * 
     * @return  a string representation of this <code>ArrayType</code> instance
     */
    public String toString() {

	// Calculate the string representation if it has not yet been done (ie 1st call to toString())
	//
	if (myToString == null) {
	    StringBuffer result = new StringBuffer();
	    result.append(this.getClass().getName());
	    result.append("(name=");
	    result.append(getTypeName());
	    result.append(",dimension=");
	    result.append(String.valueOf(this.dimension));
	    result.append(",elementType=");
	    result.append(this.elementType.toString());
	    result.append(")");
	    myToString = result.toString();
	}

	// return always the same string representation for this instance (immutable)
	//
	return myToString;
    }

}
