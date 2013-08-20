/*
 * @(#)SimpleType.java	3.23 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package javax.management.openmbean;


// java import
//
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

// jmx import
//


/**
 * The <code>SimpleType</code> class is the <i>open type</i> class whose instances describe 
 * all <i>open data</i> values which are neither arrays, 
 * nor {@link CompositeData <code>CompositeData</code>} values, 
 * nor {@link TabularData <code>TabularData</code>} values.
 * It predefines all its possible instances as static fields, and has no public constructor. 
 * <p>
 * Given a <code>SimpleType</code> instance describing values whose Java class name is <i>className</i>,
 * the internal fields corresponding to the name and description of this <code>SimpleType</code> instance
 * are also set to <i>className</i>. 
 * In other words, its methods <code>getClassName</code>, <code>getTypeName</code> and <code>getDescription</code>
 * all return the same string value <i>className</i>.
 *
 * @version     3.23  03/12/19
 * @author      Sun Microsystems, Inc.
 *
 * @since 1.5
 * @since.unbundled JMX 1.1
 */
public final class SimpleType 
    extends OpenType
    implements Serializable {
    
    /* Serial version */    
    static final long serialVersionUID = 2215577471957694503L;

    // SimpleType instances.
    // IF YOU ADD A SimpleType, YOU MUST UPDATE OpenType and typeArray

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.lang.Void</code>.
     */
    public static final SimpleType VOID ;

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.lang.Boolean</code>.
     */
    public static final SimpleType BOOLEAN ;

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.lang.Character</code>.
     */
    public static final SimpleType CHARACTER ;

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.lang.Byte</code>.
     */
    public static final SimpleType BYTE ;

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.lang.Short</code>.
     */
    public static final SimpleType SHORT ;

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.lang.Integer</code>.
     */
    public static final SimpleType INTEGER ;

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.lang.Long</code>.
     */
    public static final SimpleType LONG ;

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.lang.Float</code>.
     */
    public static final SimpleType FLOAT ;

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.lang.Double</code>.
     */
    public static final SimpleType DOUBLE ;

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.lang.String</code>.
     */
    public static final SimpleType STRING ;

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.math.BigDecimal</code>.
     */
    public static final SimpleType BIGDECIMAL ;

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.math.BigInteger</code>.
     */
    public static final SimpleType BIGINTEGER ;

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.util.Date</code>.
     */
    public static final SimpleType DATE ;

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>javax.management.ObjectName</code>.
     */
    public static final SimpleType OBJECTNAME ;


    // Static initialization block of all possible instances of simple types
    //
    static 
    {
	SimpleType t;
	try {
	    t = new SimpleType("java.lang.Void");
	} catch (OpenDataException e) {
	    t = null; // should not happen
	}
	VOID = t;
	try {
	    t = new SimpleType("java.lang.Boolean");
	} catch (OpenDataException e) {
	    t = null; // should not happen
	}
	BOOLEAN = t;
	try {
	    t = new SimpleType("java.lang.Character");
	} catch (OpenDataException e) {
	    t = null; // should not happen
	}
	CHARACTER = t;
	try {
	    t = new SimpleType("java.lang.Byte");
	} catch (OpenDataException e) {
	    t = null; // should not happen
	}
	BYTE = t;
	try {
	    t = new SimpleType("java.lang.Short");
	} catch (OpenDataException e) {
	    t = null; // should not happen
	}
	SHORT = t;
	try {
	    t = new SimpleType("java.lang.Integer");
	} catch (OpenDataException e) {
	    t = null; // should not happen
	}
	INTEGER = t;
	try {
	    t = new SimpleType("java.lang.Long");
	} catch (OpenDataException e) {
	    t = null; // should not happen
	}
	LONG = t;
	try {
	    t = new SimpleType("java.lang.Float");
	} catch (OpenDataException e) {
	    t = null; // should not happen
	}
	FLOAT = t;
	try {
	    t = new SimpleType("java.lang.Double");
	} catch (OpenDataException e) {
	    t = null; // should not happen
	}
	DOUBLE = t;
	try {
	    t = new SimpleType("java.lang.String");
	} catch (OpenDataException e) {
	    t = null; // should not happen
	}
	STRING = t;
	try {
	    t = new SimpleType("java.math.BigDecimal");
	} catch (OpenDataException e) {
	    t = null; // should not happen
	}
	BIGDECIMAL = t;
	try {
	    t = new SimpleType("java.math.BigInteger");
	} catch (OpenDataException e) {
	    t = null; // should not happen
	}
	BIGINTEGER = t;
	try {
	    t = new SimpleType("java.util.Date");
	} catch (OpenDataException e) {
	    t = null; // should not happen
	}
	DATE = t;
	try {
	    t = new SimpleType("javax.management.ObjectName");
	} catch (OpenDataException e) {
	    t = null; // should not happen
	}
	OBJECTNAME = t;
    }

    private static final SimpleType[] typeArray = {
	VOID, BOOLEAN, CHARACTER, BYTE, SHORT, INTEGER, LONG, FLOAT,
	DOUBLE, STRING, BIGDECIMAL, BIGINTEGER, DATE, OBJECTNAME,
    };


    private transient Integer myHashCode = null;	// As this instance is immutable, these two values
    private transient String  myToString = null;	// need only be calculated once.


    /* *** Constructor *** */

    /**
     * Constructs a SimpleType instance simply by calling <code>super(className, className, className)</code>.
     *
     * @throws  OpenDataException  if <var>className</var> is not one of the allowed Java class names for open data
     */
    private SimpleType(String className) throws OpenDataException {
	
	// Check and construct state defined by parent.
	//
	super(className, className, className);
    }


    /* *** SimpleType specific information methods *** */

    /**
     * Tests whether <var>obj</var> is a value for this
     * <code>SimpleType</code> instance.  <p> This method returns
     * <code>true</code> if and only if <var>obj</var> is not null and
     * <var>obj</var>'s class name is the same as the className field
     * defined for this <code>SimpleType</code> instance (ie the class
     * name returned by the {@link OpenType#getClassName()
     * getClassName} method).
     *
     * @param obj the object to be tested.
     *
     * @return <code>true</code> if <var>obj</var> is a value for this
     * <code>SimpleType</code> instance.
     */
    public boolean isValue(Object obj) {

	// if obj is null, return false
	//
	if (obj == null) {
	    return false;
	}

	// Test if obj's class name is the same as for this instance
	//
	return this.getClassName().equals(obj.getClass().getName());
    }


    /* *** Methods overriden from class Object *** */

    /**
     * Compares the specified <code>obj</code> parameter with this <code>SimpleType</code> instance for equality. 
     * <p>
     * Two <code>SimpleType</code> instances are equal if and only if their 
     * {@link OpenType#getClassName() getClassName} methods return the same value.
     * 
     * @param  obj  the object to be compared for equality with this <code>SimpleType</code> instance;
     *		    if <var>obj</var> is <code>null</code> or is not an instance of the class <code>SimpleType</code>, 
     *              <code>equals</code> returns <code>false</code>.
     * 
     * @return  <code>true</code> if the specified object is equal to this <code>SimpleType</code> instance.
     */
    public boolean equals(Object obj) {

	/* If it weren't for readReplace(), we could replace this method
	   with just:
	   return (this == obj);
	*/

	if (!(obj instanceof SimpleType))
	    return false;

	SimpleType other = (SimpleType) obj;

	// Test if other's className field is the same as for this instance
	//
	return this.getClassName().equals(other.getClassName());
    }

    /**
     * Returns the hash code value for this <code>SimpleType</code> instance.
     * The hash code of a <code>SimpleType</code> instance is the the hash code of 
     * the string value returned by the {@link OpenType#getClassName() getClassName} method.
     * <p>
     * As <code>SimpleType</code> instances are immutable, the hash code for this instance is calculated once,
     * on the first call to <code>hashCode</code>, and then the same value is returned for subsequent calls.
     *
     * @return  the hash code value for this <code>SimpleType</code> instance
     */
    public int hashCode() {

	// Calculate the hash code value if it has not yet been done (ie 1st call to hashCode())
	//
	if (myHashCode == null) {
	    myHashCode = new Integer(this.getClassName().hashCode());
	}
	
	// return always the same hash code for this instance (immutable)
	//
	return myHashCode.intValue();
    }

    /**
     * Returns a string representation of this <code>SimpleType</code> instance.
     * <p>
     * The string representation consists of 
     * the name of this class (ie <code>javax.management.openmbean.SimpleType</code>) and the type name 
     * for this instance (which is the java class name of the values this <code>SimpleType</code> instance represents).
     * <p>
     * As <code>SimpleType</code> instances are immutable, the string representation for this instance is calculated once,
     * on the first call to <code>toString</code>, and then the same value is returned for subsequent calls.
     * 
     * @return  a string representation of this <code>SimpleType</code> instance
     */
    public String toString() {

	// Calculate the string representation if it has not yet been done (ie 1st call to toString())
	//
	if (myToString == null) {
	    myToString = this.getClass().getName()+ "(name="+ getTypeName() +")";
	}

	// return always the same string representation for this instance (immutable)
	//
	return myToString;
    }

    private static final Map canonicalTypes = new HashMap();
    static {
	for (int i = 0; i < typeArray.length; i++) {
	    final SimpleType type = typeArray[i];
	    canonicalTypes.put(type, type);
	}
    }

    /**
     * Replace an object read from an {@link
     * java.io.ObjectInputStream} with the unique instance for that
     * value.
     *
     * @return the replacement object.
     *
     * @exception ObjectStreamException if the read object cannot be
     * resolved.
     */
    public Object readResolve() throws ObjectStreamException {
	final SimpleType canonical = (SimpleType) canonicalTypes.get(this);
	if (canonical == null) {
	    // Should not happen
	    throw new InvalidObjectException("Invalid SimpleType: " + this);
	}
	return canonical;
    }
}
