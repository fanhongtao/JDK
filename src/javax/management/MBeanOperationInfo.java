/*
 * @(#)MBeanOperationInfo.java	1.31 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Describes a management operation exposed by an MBean.  Instances of
 * this class are immutable.  Subclasses may be mutable but this is
 * not recommended.
 *
 * @since 1.5
 */
public class MBeanOperationInfo extends MBeanFeatureInfo implements java.io.Serializable, Cloneable  {

    /* Serial version */
    static final long serialVersionUID = -6178860474881375330L;

    static final MBeanOperationInfo[] NO_OPERATIONS =
	new MBeanOperationInfo[0];

    /**
     * Indicates that the operation is read-like,
     * it basically returns information.
     */
    public static final int INFO = 0;

    /**
     * Indicates that the operation is a write-like,
     * and would modify the MBean in some way, typically by writing some value
     * or changing a configuration.
     */
    public static final int ACTION = 1 ;

    /**
     * Indicates that the operation is both read-like and write-like.
     */
    public static final int ACTION_INFO = 2;

    /**
     * Indicates that the operation has an "unknown" nature.
     */
    public static final int UNKNOWN = 3;

    /**
     * @serial The method's return value.
     */
    private final String type;

    /**
     * @serial The signature of the method, that is, the class names
     * of the arguments.
     */
    private final MBeanParameterInfo[] signature;

    /**
     * @serial The impact of the method, one of
     *         <CODE>INFO</CODE>,
     *         <CODE>ACTION</CODE>,
     *         <CODE>ACTION_INFO</CODE>,
     *         <CODE>UNKNOWN</CODE>
     */
    private final int impact;

    /** @see MBeanInfo#immutable */
    private final transient boolean immutable;


    /**
     * Constructs an <CODE>MBeanOperationInfo</CODE> object.
     *
     * @param method The <CODE>java.lang.reflect.Method</CODE> object
     * describing the MBean operation.
     * @param description A human readable description of the operation.
     */
    public MBeanOperationInfo(String description,
			      Method method)
	    throws IllegalArgumentException {

	this(method.getName(),
	     description,
	     methodSignature(method),
	     method.getReturnType().getName(),
	     UNKNOWN);
    }

    /**
     * Constructs an <CODE>MBeanOperationInfo</CODE> object.
     *
     * @param name The name of the method.
     * @param description A human readable description of the operation.
     * @param signature <CODE>MBeanParameterInfo</CODE> objects
     * describing the parameters(arguments) of the method.  This may be
     * null with the same effect as a zero-length array.
     * @param type The type of the method's return value.
     * @param impact The impact of the method, one of <CODE>INFO,
     * ACTION, ACTION_INFO, UNKNOWN</CODE>.
     */
    public MBeanOperationInfo(String name,
			      String description,
			      MBeanParameterInfo[] signature,
			      String type,
			      int impact)
	    throws IllegalArgumentException {

	super(name, description);

	if (signature == null || signature.length == 0)
	    signature = MBeanParameterInfo.NO_PARAMS;
	else
	    signature = (MBeanParameterInfo[]) signature.clone();
	this.signature = signature;
	this.type = type;
	this.impact = impact;
	this.immutable =
	    MBeanInfo.isImmutableClass(this.getClass(),
				       MBeanOperationInfo.class);
    }

    /**
     * <p>Returns a shallow clone of this instance.
     * The clone is obtained by simply calling <tt>super.clone()</tt>,
     * thus calling the default native shallow cloning mechanism
     * implemented by <tt>Object.clone()</tt>.
     * No deeper cloning of any internal field is made.</p>
     *
     * <p>Since this class is immutable, cloning is chiefly of interest
     * to subclasses.</p>
     */
     public Object clone () {
	 try {
	     return  super.clone() ;
	 } catch (CloneNotSupportedException e) {
	     // should not happen as this class is cloneable
	     return null;
	 }
     }

    /**
     * Returns the type of the method's return value.
     *
     * @return the return type.
     */
    public String getReturnType() {
	return type;
    }

    /**
     * <p>Returns the list of parameters for this operation.  Each
     * parameter is described by an <CODE>MBeanParameterInfo</CODE>
     * object.</p>
     *
     * <p>The returned array is a shallow copy of the internal array,
     * which means that it is a copy of the internal array of
     * references to the <CODE>MBeanParameterInfo</CODE> objects but
     * that each referenced <CODE>MBeanParameterInfo</CODE> object is
     * not copied.</p>
     *
     * @return  An array of <CODE>MBeanParameterInfo</CODE> objects.
     */
    public MBeanParameterInfo[] getSignature() {
	if (signature.length == 0)
	    return signature;
	else
	    return (MBeanParameterInfo[]) signature.clone();
    }

    private MBeanParameterInfo[] fastGetSignature() {
	if (immutable)
	    return signature;
	else
	    return getSignature();
    }

    /**
     * Returns the impact of the method, one of
     * <CODE>INFO</CODE>, <CODE>ACTION</CODE>, <CODE>ACTION_INFO</CODE>, <CODE>UNKNOWN</CODE>.
     *
     * @return the impact code.
     */
    public int getImpact() {
	return impact;
    }

    /**
     * Compare this MBeanOperationInfo to another.
     *
     * @param o the object to compare to.
     *
     * @return true iff <code>o</code> is an MBeanOperationInfo such
     * that its {@link #getName()}, {@link #getReturnType()}, {@link
     * #getDescription()}, {@link #getImpact()}, and {@link
     * #getSignature()} values are equal (not necessarily identical)
     * to those of this MBeanConstructorInfo.  Two signature arrays
     * are equal if their elements are pairwise equal.
     */
    public boolean equals(Object o) {
	if (o == this)
	    return true;
	if (!(o instanceof MBeanOperationInfo))
	    return false;
	MBeanOperationInfo p = (MBeanOperationInfo) o;
	return (p.getName().equals(getName()) &&
		p.getReturnType().equals(getReturnType()) &&
		p.getDescription().equals(getDescription()) &&
		p.getImpact() == getImpact() &&
		Arrays.equals(p.fastGetSignature(), fastGetSignature()));
    }

    /* We do not include everything in the hashcode.  We assume that
       if two operations are different they'll probably have different
       names or types.  The penalty we pay when this assumption is
       wrong should be less than the penalty we would pay if it were
       right and we needlessly hashed in the description and the
       parameter array.  */
    public int hashCode() {
	return getName().hashCode() ^ getReturnType().hashCode();
    }

    private static MBeanParameterInfo[] methodSignature(Method method) {
	final Class[] classes = method.getParameterTypes();
	final MBeanParameterInfo[] params =
	    new MBeanParameterInfo[classes.length];

	for (int i = 0; i < classes.length; i++) {
	    final String pn = "p" + (i + 1);
	    params[i] = new MBeanParameterInfo(pn, classes[i].getName(), "");
	}

	return params;
    }
}
