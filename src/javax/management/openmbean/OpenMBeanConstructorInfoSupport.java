/*
 * @(#)OpenMBeanConstructorInfoSupport.java	3.22 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package javax.management.openmbean;


// java import
//
import java.io.Serializable;
import java.util.Arrays;


// jmx import
//
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanParameterInfo;


/**
 * Describes a constructor of an Open MBean.
 *
 * @version     3.22  03/12/19
 * @author      Sun Microsystems, Inc.
 *
 * @since 1.5
 * @since.unbundled JMX 1.1
 */
public class OpenMBeanConstructorInfoSupport 
    extends MBeanConstructorInfo 
    implements OpenMBeanConstructorInfo, Serializable {
    
    /* Serial version */
    static final long serialVersionUID = -4400441579007477003L;


    private transient Integer myHashCode = null;	// As this instance is immutable, these two values
    private transient String  myToString = null;	// need only be calculated once.

    /**
     * Constructs an <tt>OpenMBeanConstructorInfoSupport</tt> instance, which describes the constructor 
     * of a class of open MBeans with the specified <var>name</var>, <var>description</var> and <var>signature</var>.
     * <p>
     * The <var>signature</var> array parameter is internally copied, so that subsequent changes 
     * to the array referenced by <var>signature</var> have no effect on this instance.
     *
     * @param name  cannot be a null or empty string.
     *
     * @param description  cannot be a null or empty string.
     *
     * @param signature  can be null or empty if there are no parameters to describe.
     *
     * @throws IllegalArgumentException  if <var>name</var> or <var>description</var> are null or empty string.
     *
     * @throws ArrayStoreException  If <var>signature</var> is not an array of instances of a subclass of <tt>MBeanParameterInfo</tt>.
     */
    public OpenMBeanConstructorInfoSupport(String name, 
					   String description, 
					   OpenMBeanParameterInfo[] signature) {

	super(name, 
	      description, 
	      ( signature == null ?  null : arrayCopyCast(signature) )); // may throw an ArrayStoreException

	// check parameters that should not be null or empty (unfortunately it is not done in superclass :-( ! )
	//
	if ( (name == null) || (name.trim().equals("")) ) {
	    throw new IllegalArgumentException("Argument name cannot be null or empty.");
	}
	if ( (description == null) || (description.trim().equals("")) ) {
	    throw new IllegalArgumentException("Argument description cannot be null or empty.");
	}

    }

    private static MBeanParameterInfo[] arrayCopyCast(OpenMBeanParameterInfo[] src) throws ArrayStoreException {

	MBeanParameterInfo[] dst = new MBeanParameterInfo[src.length];
	System.arraycopy(src, 0, dst, 0, src.length); // may throw an ArrayStoreException
	return dst;
    }


    /* ***  Commodity methods from java.lang.Object  *** */


    /**
     * Compares the specified <var>obj</var> parameter with this <code>OpenMBeanConstructorInfoSupport</code> instance for equality. 
     * <p>
     * Returns <tt>true</tt> if and only if all of the following statements are true:
     * <ul>
     * <li><var>obj</var> is non null,</li>
     * <li><var>obj</var> also implements the <code>OpenMBeanConstructorInfo</code> interface,</li>
     * <li>their names are equal</li>
     * <li>their signatures are equal.</li>
     * </ul>
     * This ensures that this <tt>equals</tt> method works properly for <var>obj</var> parameters which are
     * different implementations of the <code>OpenMBeanConstructorInfo</code> interface.
     * <br>&nbsp;
     * @param  obj  the object to be compared for equality with this <code>OpenMBeanConstructorInfoSupport</code> instance;
     * 
     * @return  <code>true</code> if the specified object is equal to this <code>OpenMBeanConstructorInfoSupport</code> instance.
     */
    public boolean equals(Object obj) { 

	// if obj is null, return false
	//
	if (obj == null) {
	    return false;
	}

	// if obj is not a OpenMBeanConstructorInfo, return false
	//
	OpenMBeanConstructorInfo other;
	try {
	    other = (OpenMBeanConstructorInfo) obj;
	} catch (ClassCastException e) {
	    return false;
	}

	// Now, really test for equality between this OpenMBeanConstructorInfo implementation and the other:
	//
	
	// their Name should be equal
	if ( ! this.getName().equals(other.getName()) ) {
	    return false;
	}

	// their Signatures should be equal
	if ( ! Arrays.equals(this.getSignature(), other.getSignature()) ) {
	    return false;
	}
       
	// All tests for equality were successfull
	//
	return true;
    }

    /**
     * Returns the hash code value for this <code>OpenMBeanConstructorInfoSupport</code> instance. 
     * <p>
     * The hash code of an <code>OpenMBeanConstructorInfoSupport</code> instance is the sum of the hash codes
     * of all elements of information used in <code>equals</code> comparisons 
     * (ie: its name and signature, where the signature hashCode is calculated by a call to 
     *  <tt>java.util.Arrays.asList(this.getSignature).hashCode()</tt>). 
     * <p>
     * This ensures that <code> t1.equals(t2) </code> implies that <code> t1.hashCode()==t2.hashCode() </code> 
     * for any two <code>OpenMBeanConstructorInfoSupport</code> instances <code>t1</code> and <code>t2</code>, 
     * as required by the general contract of the method
     * {@link Object#hashCode() Object.hashCode()}.
     * <p>
     * However, note that another instance of a class implementing the <code>OpenMBeanConstructorInfo</code> interface
     * may be equal to this <code>OpenMBeanConstructorInfoSupport</code> instance as defined by {@link #equals(java.lang.Object)}, 
     * but may have a different hash code if it is calculated differently.
     * <p>
     * As <code>OpenMBeanConstructorInfoSupport</code> instances are immutable, the hash code for this instance is calculated once,
     * on the first call to <code>hashCode</code>, and then the same value is returned for subsequent calls.
     *
     * @return  the hash code value for this <code>OpenMBeanConstructorInfoSupport</code> instance
     */
    public int hashCode() {

	// Calculate the hash code value if it has not yet been done (ie 1st call to hashCode())
	//
	if (myHashCode == null) {
	    int value = 0;
	    value += this.getName().hashCode();
	    value += Arrays.asList(this.getSignature()).hashCode();
	    myHashCode = new Integer(value);
	}
	
	// return always the same hash code for this instance (immutable)
	//
	return myHashCode.intValue();
    }

    /**
     * Returns a string representation of this <code>OpenMBeanConstructorInfoSupport</code> instance. 
     * <p>
     * The string representation consists of the name of this class (ie <code>javax.management.openmbean.OpenMBeanConstructorInfoSupport</code>), 
     * and of the name and signature of the described constructor.
     * <p>
     * As <code>OpenMBeanConstructorInfoSupport</code> instances are immutable, 
     * the string representation for this instance is calculated once,
     * on the first call to <code>toString</code>, and then the same value is returned for subsequent calls.
     * 
     * @return  a string representation of this <code>OpenMBeanConstructorInfoSupport</code> instance
     */
    public String toString() { 

	// Calculate the hash code value if it has not yet been done (ie 1st call to toString())
	//
	if (myToString == null) {
	    myToString = new StringBuffer()
		.append(this.getClass().getName())
		.append("(name=")
		.append(this.getName())
		.append(",signature=")
		.append(Arrays.asList(this.getSignature()).toString())
		.append(")")
		.toString();
	}

	// return always the same string representation for this instance (immutable)
	//
	return myToString;
    }

}
