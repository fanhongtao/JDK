/*
 * @(#)OpenType.java	3.28 06/07/27
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package javax.management.openmbean;


// java import
//
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

// jmx import
//


/**
 * The <code>OpenType</code> class is the parent abstract class of all classes which describe the actual <i>open type</i> 
 * of open data values. 
 * <p>
 * An <i>open type</i> is defined by:
 * <ul>
 *  <li>the fully qualified Java class name of the open data values this type describes; 
 *      note that only a limited set of Java classes is allowed for open data values 
 *      (see {@link #ALLOWED_CLASSNAMES ALLOWED_CLASSNAMES}),</li>
 *  <li>its name,</li>
 *  <li>its description.</li>
 * </ul>
 * 
 * @version     3.28  06/07/27
 * @author      Sun Microsystems, Inc.
 *
 * @since 1.5
 * @since.unbundled JMX 1.1
 */
public abstract class OpenType implements Serializable {

    /* Serial version */    
    static final long serialVersionUID = -9195195325186646468L;


    /**
     * The ALLOWED_CLASSNAMES array is kept for compatibility reasons, but:
     * our implementation should use only ALLOWED_CLASSNAMES_LIST which is
     * unmodifiable, and not use the ALLOWED_CLASSNAMES array which is
     * modifiable by external code.
     */
    static final List<String> ALLOWED_CLASSNAMES_LIST = 
      Collections.unmodifiableList(
        Arrays.asList(
	  "java.lang.Void",
	  "java.lang.Boolean",
	  "java.lang.Character",
	  "java.lang.Byte",
	  "java.lang.Short",
	  "java.lang.Integer",
	  "java.lang.Long",
	  "java.lang.Float",
	  "java.lang.Double",
	  "java.lang.String",
	  "java.math.BigDecimal",
	  "java.math.BigInteger",
	  "java.util.Date",
	  "javax.management.ObjectName",
	  CompositeData.class.getName(),	// better refer to these two class names like this, rather than hardcoding a string,
	  TabularData.class.getName()) );	// in case the package of these classes should change (who knows...)

    /**
     * List of the fully qualified names of the Java classes allowed for open data values.
     * A multidimensional array of any one of these classes is also an allowed for open data values.
     * 
       <pre>ALLOWED_CLASSNAMES = { 
	"java.lang.Void",
	"java.lang.Boolean",
	"java.lang.Character",
	"java.lang.Byte",
	"java.lang.Short",
	"java.lang.Integer",
	"java.lang.Long",
	"java.lang.Float",
	"java.lang.Double",
	"java.lang.String",
	"java.math.BigDecimal",
	"java.math.BigInteger",
	"java.util.Date",
	"javax.management.ObjectName",
	CompositeData.class.getName(),
	TabularData.class.getName() } ;
       </pre>
     *
     */
    public static final String[] ALLOWED_CLASSNAMES = 
	ALLOWED_CLASSNAMES_LIST.toArray(new String[0]);

    /**
     * @serial The fully qualified Java class name of open data values this type describes.
     */
    private String  className;
 
    /**
     * @serial The type description (should not be null or empty).
     */
    private String  description;

    /**
     * @serial The name given to this type (should not be null or empty).
     */
    private String  typeName;

    /**
     * @serial Tells if this type describes an array (checked in constructor).
     */
    private transient boolean isArray = false;
 

    /* *** Constructor *** */

    /**
     * Constructs an <code>OpenType</code> instance (actually a subclass instance as <code>OpenType</code> is abstract),
     * checking for the validity of the given parameters.
     * The validity constraints are described below for each parameter.
     * <br>&nbsp;
     * @param  className  The fully qualified Java class name of the open data values this open type describes.
     *			  The valid Java class names allowed for open data values are listed in
     *			  {@link #ALLOWED_CLASSNAMES ALLOWED_CLASSNAMES}. 
     *			  A multidimensional array of any one of these classes is also an allowed class,
     *			  in which case the class name follows the rules defined by the method
     *			  {@link Class#getName() getName()} of <code>java.lang.Class</code>.
     *			  For example, a 3-dimensional array of Strings has for class name
     *			  &quot;<code>[[[Ljava.lang.String;</code>&quot; (without the quotes).
     * <br>&nbsp;
     * @param  typeName  The name given to the open type this instance represents; cannot be a null or empty string.
     * <br>&nbsp;
     * @param  description  The human readable description of the open type this instance represents; 
     *			    cannot be a null or empty string.
     * <br>&nbsp;
     * @throws IllegalArgumentException  if <var>className</var>, <var>typeName</var> or <var>description</var>
     *					 is a null or empty string
     * <br>&nbsp;
     * @throws OpenDataException  if <var>className</var> is not one of the allowed Java class names for open data
     */
    protected OpenType(String  className, 
		       String  typeName, 
		       String  description) throws OpenDataException {
	
	// Check parameters that cannot be null or empty
	//
	if ( (className == null) || (className.trim().equals("")) ) {
	    throw new IllegalArgumentException("Argument className cannot be null or empty.");
	}
	if ( (typeName == null) || (typeName.trim().equals("")) ) {
	    throw new IllegalArgumentException("Argument typeName cannot be null or empty.");
	}
	if ( (description == null) || (description.trim().equals("")) ) {
	    throw new IllegalArgumentException("Argument description cannot be null or empty.");
	}

	// remove leading and trailing white spaces, if any
	//
	className   = className.trim();
	typeName    = typeName.trim();
	description = description.trim();

	// Check if className describes an array class, and determines its elements' class name.
	// (eg: a 3-dimensional array of Strings has for class name: "[[[Ljava.lang.String;")
	//
	int n = 0;
	while (className.startsWith("[", n)) {
	    n++;
	}
	String eltClassName; // class name of array elements
	boolean isArray = false;
	if (n > 0) {
	    // removes the n leading '[' + the 'L' characters and the last ';' character
	    eltClassName = className.substring(n+1, className.length()-1); // see javadoc of String.substring(begin,end)
	    isArray = true;
	} else {
	    // not an array
	    eltClassName = className;
	}

	// Check that eltClassName's value is one of the allowed basic data types for open data
	//
	if ( ! ALLOWED_CLASSNAMES_LIST.contains(eltClassName) ) {
	    throw new OpenDataException("Argument className=\""+ className +
					"\" is not one of the allowed Java class names for open data.");
	}

	// Now initializes this OpenType instance
	//
	this.className   = className;
	this.typeName    = typeName;
	this.description = description;
	this.isArray     = isArray;
    }
 

    /* *** Open type information methods *** */

    /**
     * Returns the fully qualified Java class name of the open data values this open type describes.
     * The only possible Java class names for open data values are listed in 
     * {@link #ALLOWED_CLASSNAMES ALLOWED_CLASSNAMES}. 
     * A multidimensional array of any one of these classes is also an allowed class,
     * in which case the class name follows the rules defined by the method
     * {@link Class#getName() getName()} of <code>java.lang.Class</code>.
     * For example, a 3-dimensional array of Strings has for class name
     * &quot;<code>[[[Ljava.lang.String;</code>&quot; (without the quotes).
     *
     * @return the class name.
     */
    public String getClassName() {

	return className;
    }

    /**
     * Returns the name of this <code>OpenType</code> instance.
     *
     * @return the type name.
     */
    public String getTypeName() {

	return typeName;
    }

    /**
     * Returns the text description of this <code>OpenType</code> instance.
     *
     * @return the description.
     */
    public String getDescription() {

	return description;
    }

    /**
     * Returns <code>true</code> if the open data values this open
     * type describes are arrays, <code>false</code> otherwise.
     *
     * @return true if this is an array type.
     */
    public boolean isArray() {

	return isArray;
    }

    /**
     * Tests whether <var>obj</var> is a value for this open type.
     *
     * @param obj the object to be tested for validity.
     *
     * @return <code>true</code> if <var>obj</var> is a value for this
     * open type, <code>false</code> otherwise.
     */
    public abstract boolean isValue(Object obj) ;


    /* *** Methods overriden from class Object *** */

    /**
     * Compares the specified <code>obj</code> parameter with this
     * open type instance for equality.
     *
     * @param obj the object to compare to.
     *
     * @return true if this object and <code>obj</code> are equal.
     */
    public abstract boolean equals(Object obj) ;

    public abstract int hashCode() ;

    /**
     * Returns a string representation of this open type instance.
     *
     * @return the string representation.
     */
    public abstract String toString() ;

    /**
     * Deserializes an {@link OpenType} from an {@link ObjectInputStream}.
     */
    private void readObject(ObjectInputStream in)
	    throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      isArray = (className.startsWith("["));
    }

}

