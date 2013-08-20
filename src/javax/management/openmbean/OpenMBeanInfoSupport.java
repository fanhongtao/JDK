/*
 * @(#)OpenMBeanInfoSupport.java	3.22 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package javax.management.openmbean;


// java import
//
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;


// jmx import
//
import javax.management.MBeanInfo;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanNotificationInfo;



/**
 * The <tt>OpenMBeanInfoSupport</tt> class describes the management information of an <i>open MBean</i>:
 * it is a subclass of {@link javax.management.MBeanInfo}, and it implements the {@link OpenMBeanInfo} interface.
 * Note that an <i>open MBean</i> is recognized as such if its <tt>getMBeanInfo()</tt> method returns an instance of a class
 * which implements the OpenMBeanInfo interface, typically <tt>OpenMBeanInfoSupport</tt>.
 *
 * @version     3.22  03/12/19
 * @author      Sun Microsystems, Inc.
 *
 * @since 1.5
 * @since.unbundled JMX 1.1
 */
public class OpenMBeanInfoSupport 
    extends MBeanInfo
    implements OpenMBeanInfo, Serializable {
    
    /* Serial version */
    static final long serialVersionUID = 4349395935420511492L;


    private transient Integer myHashCode = null;	// As this instance is immutable, these two values
    private transient String  myToString = null;	// need only be calculated once.


    /**
     * Constructs an <tt>OpenMBeanInfoSupport</tt> instance, 
     * which describes a class of open MBeans with the specified 
     * <var>className</var>, <var>description</var>, <var>openAttributes</var>, 
     * <var>openConstructors</var> , <var>openOperations</var> and <var>notifications</var>.
     * <p>
     * The <var>openAttributes</var>, <var>openConstructors</var>,  
     * <var>openOperations</var> and <var>notifications</var>
     * array parameters are internally copied, so that subsequent changes 
     * to the arrays referenced by these parameters have no effect on this instance.
     * <p>
     *
     *
     * @param className		The fully qualified Java class name of 
     *				the open MBean described by this <CODE>OpenMBeanInfoSupport</CODE> instance.
     *
     * @param description	A human readable description of 
     *				the open MBean described by this <CODE>OpenMBeanInfoSupport</CODE> instance.
     *
     * @param openAttributes	The list of exposed attributes of the described open MBean;
     *				Must be an array of instances of a subclass of <tt>MBeanAttributeInfo</tt>, 
     *				typically <tt>OpenMBeanAttributeInfoSupport</tt>.
     *
     * @param openConstructors	The list of exposed public constructors of the described open MBean;
     *				Must be an array of instances of a subclass of <tt>MBeanConstructorInfo</tt>, 
     *				typically <tt>OpenMBeanConstructorInfoSupport</tt>.
     *
     * @param openOperations	The list of exposed operations of the described open MBean.
     *				Must be an array of instances of a subclass of <tt>MBeanOperationInfo</tt>, 
     *				typically <tt>OpenMBeanOperationInfoSupport</tt>.
     *
     * @param notifications	The list of notifications emitted by the described open MBean.
     *
     * @throws ArrayStoreException  If <var>openAttributes</var>, <var>openConstructors</var> or <var>openOperations</var>
     *				   is not an array of instances of a subclass of <tt>MBeanAttributeInfo</tt>,
     *				   <tt>MBeanConstructorInfo</tt> or <tt>MBeanOperationInfo</tt> respectively.
     */
    public OpenMBeanInfoSupport(String className, 
				String description,
				OpenMBeanAttributeInfo[] openAttributes, 
				OpenMBeanConstructorInfo[] openConstructors,
				OpenMBeanOperationInfo[] openOperations, 
				MBeanNotificationInfo[] notifications) {
	
	super(className, 
	      description, 
	      ( openAttributes   == null ?  null : attributesArrayCopyCast(openAttributes) ),     // may throw an ArrayStoreException
	      ( openConstructors == null ?  null : constructorsArrayCopyCast(openConstructors) ), // may throw an ArrayStoreException
	      ( openOperations   == null ?  null : operationsArrayCopyCast(openOperations) ),     // may throw an ArrayStoreException
	      ( notifications == null ?  null : notificationsArrayCopy(notifications) ));
 
    }


    private static MBeanAttributeInfo[] attributesArrayCopyCast(OpenMBeanAttributeInfo[] src) throws ArrayStoreException {

	MBeanAttributeInfo[] dst = new MBeanAttributeInfo[src.length];
	System.arraycopy(src, 0, dst, 0, src.length); // may throw an ArrayStoreException
	return dst;
    }

    private static MBeanConstructorInfo[] constructorsArrayCopyCast(OpenMBeanConstructorInfo[] src) throws ArrayStoreException {

	MBeanConstructorInfo[] dst = new MBeanConstructorInfo[src.length];
	System.arraycopy(src, 0, dst, 0, src.length); // may throw an ArrayStoreException
	return dst;
    }

    private static MBeanOperationInfo[] operationsArrayCopyCast(OpenMBeanOperationInfo[] src) throws ArrayStoreException {

	MBeanOperationInfo[] dst = new MBeanOperationInfo[src.length];
	System.arraycopy(src, 0, dst, 0, src.length); // may throw an ArrayStoreException
	return dst;
    }

    private static MBeanNotificationInfo[] notificationsArrayCopy(MBeanNotificationInfo[] src) {

	MBeanNotificationInfo[] dst = new MBeanNotificationInfo[src.length];
	System.arraycopy(src, 0, dst, 0, src.length); 
	return dst;
    }



    /* ***  Commodity methods from java.lang.Object  *** */


    /**
     * Compares the specified <var>obj</var> parameter with this <code>OpenMBeanInfoSupport</code> instance for equality. 
     * <p>
     * Returns <tt>true</tt> if and only if all of the following statements are true:
     * <ul>
     * <li><var>obj</var> is non null,</li>
     * <li><var>obj</var> also implements the <code>OpenMBeanInfo</code> interface,</li>
     * <li>their class names are equal</li>
     * <li>their infos on attributes, constructors, operations and notifications are equal</li>
     * </ul>
     * This ensures that this <tt>equals</tt> method works properly for <var>obj</var> parameters which are
     * different implementations of the <code>OpenMBeanInfo</code> interface.
     * <br>&nbsp;
     * @param  obj  the object to be compared for equality with this <code>OpenMBeanInfoSupport</code> instance;
     * 
     * @return  <code>true</code> if the specified object is equal to this <code>OpenMBeanInfoSupport</code> instance.
     */
    public boolean equals(Object obj) { 

	// if obj is null, return false
	//
	if (obj == null) {
	    return false;
	}

	// if obj is not a OpenMBeanInfo, return false
	//
	OpenMBeanInfo other;
	try {
	    other = (OpenMBeanInfo) obj;
	} catch (ClassCastException e) {
	    return false;
	}

	// Now, really test for equality between this OpenMBeanInfo implementation and the other:
	//
	
	// their MBean className should be equal
	if ( ! this.getClassName().equals(other.getClassName()) ) {
	    return false;
	}

	// their infos on attributes should be equal (order not significant => equality between sets, not arrays or lists)
	if ( ! new HashSet(Arrays.asList(this.getAttributes())).equals(new HashSet(Arrays.asList(other.getAttributes()))) ) {
	    return false;
	}
       
	// their infos on constructors should be equal (order not significant => equality between sets, not arrays or lists)
	if ( ! new HashSet(Arrays.asList(this.getConstructors())).equals(new HashSet(Arrays.asList(other.getConstructors()))) ) {
	    return false;
	}
       
	// their infos on operations should be equal (order not significant => equality between sets, not arrays or lists)
	if ( ! new HashSet(Arrays.asList(this.getOperations())).equals(new HashSet(Arrays.asList(other.getOperations()))) ) {
	    return false;
	}
       
	// their infos on notifications should be equal (order not significant => equality between sets, not arrays or lists)
	if ( ! new HashSet(Arrays.asList(this.getNotifications())).equals(new HashSet(Arrays.asList(other.getNotifications()))) ) {
	    return false;
	}
       
	// All tests for equality were successfull
	//
	return true;
    }

    /**
     * Returns the hash code value for this <code>OpenMBeanInfoSupport</code> instance. 
     * <p>
     * The hash code of an <code>OpenMBeanInfoSupport</code> instance is the sum of the hash codes
     * of all elements of information used in <code>equals</code> comparisons 
     * (ie: its class name, and its infos on attributes, constructors, operations and notifications, 
     * where the hashCode of each of these arrays is calculated by a call to 
     *  <tt>new java.util.HashSet(java.util.Arrays.asList(this.getSignature)).hashCode()</tt>). 
     * <p>
     * This ensures that <code> t1.equals(t2) </code> implies that <code> t1.hashCode()==t2.hashCode() </code> 
     * for any two <code>OpenMBeanInfoSupport</code> instances <code>t1</code> and <code>t2</code>, 
     * as required by the general contract of the method
     * {@link Object#hashCode() Object.hashCode()}.
     * <p>
     * However, note that another instance of a class implementing the <code>OpenMBeanInfo</code> interface
     * may be equal to this <code>OpenMBeanInfoSupport</code> instance as defined by {@link #equals(java.lang.Object)}, 
     * but may have a different hash code if it is calculated differently.
     * <p>
     * As <code>OpenMBeanInfoSupport</code> instances are immutable, the hash code for this instance is calculated once,
     * on the first call to <code>hashCode</code>, and then the same value is returned for subsequent calls.
     *
     * @return  the hash code value for this <code>OpenMBeanInfoSupport</code> instance
     */
    public int hashCode() {

	// Calculate the hash code value if it has not yet been done (ie 1st call to hashCode())
	//
	if (myHashCode == null) {
	    int value = 0;
	    value += this.getClassName().hashCode();
	    value += new HashSet(Arrays.asList(this.getAttributes())).hashCode();
	    value += new HashSet(Arrays.asList(this.getConstructors())).hashCode();
	    value += new HashSet(Arrays.asList(this.getOperations())).hashCode();
	    value += new HashSet(Arrays.asList(this.getNotifications())).hashCode();
	    myHashCode = new Integer(value);
	}
	
	// return always the same hash code for this instance (immutable)
	//
	return myHashCode.intValue();
    }

    /**
     * Returns a string representation of this <code>OpenMBeanInfoSupport</code> instance. 
     * <p>
     * The string representation consists of the name of this class (ie <code>javax.management.openmbean.OpenMBeanInfoSupport</code>), 
     * the MBean class name, 
     * and the string representation of infos on attributes, constructors, operations and notifications of the described MBean.
     * <p>
     * As <code>OpenMBeanInfoSupport</code> instances are immutable, 
     * the string representation for this instance is calculated once,
     * on the first call to <code>toString</code>, and then the same value is returned for subsequent calls.
     * 
     * @return  a string representation of this <code>OpenMBeanInfoSupport</code> instance
     */
    public String toString() { 

	// Calculate the hash code value if it has not yet been done (ie 1st call to toString())
	//
	if (myToString == null) {
	    myToString = new StringBuffer()
		.append(this.getClass().getName())
		.append("(mbean_class_name=")
		.append(this.getClassName())
		.append(",attributes=")
		.append(Arrays.asList(this.getAttributes()).toString())
		.append(",constructors=")
		.append(Arrays.asList(this.getConstructors()).toString())
		.append(",operations=")
		.append(Arrays.asList(this.getOperations()).toString())
		.append(",notifications=")
		.append(Arrays.asList(this.getNotifications()).toString())
		.append(")")
		.toString();
	}

	// return always the same string representation for this instance (immutable)
	//
	return myToString;
    }

}
