/*
 * @(#)OpenMBeanParameterInfoSupport.java	3.28 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 

package javax.management.openmbean;


// java import
//
import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.lang.Comparable; // to be substituted for jdk1.1.x


// jmx import
//
import javax.management.MBeanParameterInfo;


/**
 * Describes a parameter used in one or more operations or constructors of an open MBean.
 *
 * @version     3.28  03/12/19
 * @author      Sun Microsystems, Inc.
 *
 * @since 1.5
 * @since.unbundled JMX 1.1
 */
public class OpenMBeanParameterInfoSupport 
    extends MBeanParameterInfo 
    implements OpenMBeanParameterInfo, Serializable {
    
    
    /* Serial version */
    static final long serialVersionUID = -7235016873758443122L;

    /**
     * @serial The open mbean parameter's <i>open type</i>
     */
    private OpenType    openType;

    /**
     * @serial The open mbean parameter's default value
     */
    private Object      defaultValue    = null;

    /**
     * @serial The open mbean parameter's legal values. This {@link Set} is unmodifiable
     */
    private Set         legalValues     = null;  // to be constructed unmodifiable

    /**
     * @serial The open mbean parameter's min value
     */
    private Comparable  minValue        = null;

    /**
     * @serial The open mbean parameter's max value
     */
    private Comparable  maxValue        = null;


    private transient Integer myHashCode = null;	// As this instance is immutable, these two values
    private transient String  myToString = null;	// need only be calculated once.


    /**
     * Constructs an <tt>OpenMBeanParameterInfoSupport</tt> instance, which describes the parameter 
     * used in one or more operations or constructors of a class of open MBeans,
     * with the specified <var>name</var>, <var>openType</var> and <var>description</var>. 
     *
     * @param name  cannot be a null or empty string.
     *
     * @param description  cannot be a null or empty string.
     *
     * @param openType  cannot be null.
     *
     * @throws IllegalArgumentException  if <var>name</var> or <var>description</var> are null or empty string,
     *					 or <var>openType</var> is null.
     */
    public OpenMBeanParameterInfoSupport(String   name, 
					 String   description,
					 OpenType openType) {

	// Construct parent's state
	//
	super(name, ( (openType==null) ? null : openType.getClassName() ), description);

	// check parameters that should not be null or empty (unfortunately it is not done in superclass :-( ! )
	//
	if ( (name == null) || (name.trim().equals("")) ) {
	    throw new IllegalArgumentException("Argument name cannot be null or empty.");
	}
	if ( (description == null) || (description.trim().equals("")) ) {
	    throw new IllegalArgumentException("Argument description cannot be null or empty.");
	}
	if (openType == null) {
	    throw new IllegalArgumentException("Argument openType cannot be null.");
	}

	// Initialize this instance's specific state
	//
	this.openType = openType;
    }


    /**
     * Constructs an <tt>OpenMBeanParameterInfoSupport</tt> instance, which describes the parameter 
     * used in one or more operations or constructors of a class of open MBeans,
     * with the specified <var>name</var>, <var>openType</var>, <var>description</var> and <var>defaultValue</var>. 
     *
     * @param name  cannot be a null or empty string.
     *
     * @param description  cannot be a null or empty string.
     *
     * @param openType  cannot be null.
     *
     * @param defaultValue  must be a valid value for the <var>openType</var> specified for this parameter;
     *			    default value not supported for <tt>ArrayType</tt> and <tt>TabularType</tt>;
     *			    can be null, in which case it means that no default value is set.
     *
     * @throws IllegalArgumentException  if <var>name</var> or <var>description</var> are null or empty string,
     *					 or <var>openType</var> is null.
     *
     * @throws OpenDataException  if <var>defaultValue</var> is not a valid value for the specified <var>openType</var>,
     *				  or <var>defaultValue</var> is non null and 
     *				  <var>openType</var> is an <tt>ArrayType</tt> or a <tt>TabularType</tt>.
     */
    public OpenMBeanParameterInfoSupport(String   name, 
					 String   description, 
					 OpenType openType, 
					 Object   defaultValue) throws OpenDataException {

	// First check and construct the part regarding name, openType and description
	//
	this(name, description, openType);

	// Check and initialize defaultValue
	//
	if (defaultValue != null) {
	    // Default value not supported for ArrayType and TabularType
	    if ( (openType.isArray()) || (openType instanceof TabularType) ) {
		throw new OpenDataException("Default value not supported for ArrayType and TabularType.");
	    }
	    // Check defaultValue's class
	    if ( ! openType.isValue(defaultValue) ) {
		throw new OpenDataException("Argument defaultValue's class [\""+ defaultValue.getClass().getName() +
					    "\"] does not match the one defined in openType[\""+ openType.getClassName() +"\"].");
	    }
	    // Then initializes defaultValue:
	    // no need to clone it: apart from arrays and TabularData, basic data types are immutable
	    this.defaultValue = defaultValue;
	}
    }

    /**
     * Constructs an <tt>OpenMBeanParameterInfoSupport</tt> instance, which describes the parameter 
     * used in one or more operations or constructors of a class of open MBeans,
     * with the specified <var>name</var>, <var>openType</var>, <var>description</var>, 
     * <var>defaultValue</var> and <var>legalValues</var>.
     *
     * The contents of <var>legalValues</var> are internally dumped into an unmodifiable <tt>Set</tt>,
     * so subsequent modifications of the array referenced by <var>legalValues</var> have no impact on 
     * this <tt>OpenMBeanParameterInfoSupport</tt> instance.
     *
     * @param name  cannot be a null or empty string.
     *
     * @param description  cannot be a null or empty string.
     *
     * @param openType  cannot be null.
     *
     * @param defaultValue  must be a valid value for the <var>openType</var> specified for this parameter;
     *			    default value not supported for <tt>ArrayType</tt> and <tt>TabularType</tt>;
     *			    can be null, in which case it means that no default value is set.
     *
     * @param legalValues   each contained value must be valid for the <var>openType</var> specified for this parameter;
     *			    legal values not supported for <tt>ArrayType</tt> and <tt>TabularType</tt>;
     *			    can be null or empty.
     *
     * @throws IllegalArgumentException  if <var>name</var> or <var>description</var> are null or empty string,
     *					 or <var>openType</var> is null.
     *
     * @throws OpenDataException  if <var>defaultValue</var> is not a valid value for the specified <var>openType</var>,
     *				  or one value in <var>legalValues</var> is not valid for the specified <var>openType</var>,
     *				  or <var>defaultValue</var> is non null and 
     *				  <var>openType</var> is an <tt>ArrayType</tt> or a <tt>TabularType</tt>,
     *				  or <var>legalValues</var> is non null and non empty and
     *				  <var>openType</var> is an <tt>ArrayType</tt> or a <tt>TabularType</tt>,
     *				  or <var>legalValues</var> is non null and non empty and
     *				  <var>defaultValue</var> is not contained in <var>legalValues</var>.
     */
    public OpenMBeanParameterInfoSupport(String   name, 
					 String   description, 
					 OpenType openType, 
					 Object   defaultValue,
					 Object[] legalValues) throws OpenDataException {

	// First check and construct the part regarding name, openType, description and defaultValue
	//
	this(name, description, openType, defaultValue);

	// Check and initialize legalValues
	//
	if ( (legalValues != null) && (legalValues.length > 0) ) {
	    // legalValues not supported for TabularType and arrays
	    if ( (openType instanceof TabularType) || (openType.isArray()) ) {
		throw new OpenDataException("Legal values not supported for TabularType and arrays");
	    }
	    // Check legalValues are valid with openType
	    for (int i = 0; i < legalValues.length; i++ ) {
		if ( ! openType.isValue(legalValues[i]) ) {
		    throw new OpenDataException("Element legalValues["+ i +"]="+ legalValues[i] +
						" is not a valid value for the specified openType ["+ openType.toString() +"].");
		}
	    }
	    // dump the legalValues array content into a Set: ensures uniqueness of elements
	    // (and we could not keep the array reference as array content could be modified by the caller)
	    Set tmpSet = new HashSet(legalValues.length+1, 1);
	    for (int i = 0; i < legalValues.length; i++ ) {
		tmpSet.add(legalValues[i]);
	    }
	    // initializes legalValues as an unmodifiable Set
	    this.legalValues = Collections.unmodifiableSet(tmpSet);
	}

	// Check that defaultValue is a legal value
	//
	if ( (this.hasDefaultValue()) && (this.hasLegalValues()) ) {
	    if ( ! this.legalValues.contains(defaultValue) ) {
		throw new OpenDataException("defaultValue is not contained in legalValues");
	    }
	}

    }


    /**
     * Constructs an <tt>OpenMBeanParameterInfoSupport</tt> instance, which describes the parameter 
     * used in one or more operations or constructors of a class of open MBeans,
     * with the specified <var>name</var>, <var>openType</var>, <var>description</var>, 
     * <var>defaultValue</var>, <var>minValue</var> and <var>maxValue</var>.
     *
     * It is possible to specify minimal and maximal values only for an open type 
     * whose values are <tt>Comparable</tt>.
     *
     * @param name  cannot be a null or empty string.
     *
     * @param description  cannot be a null or empty string.
     *
     * @param openType  cannot be null.
     *
     * @param defaultValue  must be a valid value for the <var>openType</var> specified for this parameter;
     *			    default value not supported for <tt>ArrayType</tt> and <tt>TabularType</tt>;
     *			    can be null, in which case it means that no default value is set.
     *
     * @param minValue   must be valid for the <var>openType</var> specified for this parameter;
     *			 can be null, in which case it means that no minimal value is set.
     *
     * @param maxValue   must be valid for the <var>openType</var> specified for this parameter;
     *			 can be null, in which case it means that no maximal value is set.
     *
     * @throws IllegalArgumentException  if <var>name</var> or <var>description</var> are null or empty string,
     *					 or <var>openType</var> is null.
     *
     * @throws OpenDataException  if <var>defaultValue</var>, <var>minValue</var> or <var>maxValue</var>
     *				  is not a valid value for the specified <var>openType</var>,
     *				  or <var>defaultValue</var> is non null and 
     *				  <var>openType</var> is an <tt>ArrayType</tt> or a <tt>TabularType</tt>,
     *				  or both <var>minValue</var> and <var>maxValue</var> are non-null and
     *				  <tt>minValue.compareTo(maxValue) > 0</tt> is <tt>true</tt>,
     *				  or both <var>defaultValue</var> and <var>minValue</var> are non-null and
     *				  <tt>minValue.compareTo(defaultValue) > 0</tt> is <tt>true</tt>,
     *				  or both <var>defaultValue</var> and <var>maxValue</var> are non-null and
     *				  <tt>defaultValue.compareTo(maxValue) > 0</tt> is <tt>true</tt>.
     */
    public OpenMBeanParameterInfoSupport(String     name, 
					 String     description, 
					 OpenType   openType, 
					 Object     defaultValue,
					 Comparable minValue,
					 Comparable maxValue) throws OpenDataException {

	// First check and construct the part regarding name, openType, description and defaultValue
	//
	this(name, description, openType, defaultValue);

	// Check and initialize minValue 
	//(note: no need to worry about Composite, Tabular and arrays as they are not Comparable)
	//
	if (minValue != null) {
	    if ( ! openType.isValue(minValue) ) {
		throw new OpenDataException("Argument minValue's class [\""+ minValue.getClass().getName() +
					    "\"] does not match openType's definition [\""+ openType.getClassName() +"\"].");
	    }
	    // then initializes minValue 
	    this.minValue = minValue;
	}

	// Check and initialize maxValue 
	//(note: no need to worry about Composite, Tabular and arrays as they are not Comparable)
	//
	if (maxValue != null) {
	    if ( ! openType.isValue(maxValue) ) {
		throw new OpenDataException("Argument maxValue's class [\""+ maxValue.getClass().getName() +
					    "\"] does not match openType's definition [\""+ openType.getClassName() +"\"].");
	    }
	    // then initializes maxValue 
	    this.maxValue = maxValue;
	}

	// Check that, if both specified, minValue <= maxValue
	//
	if (hasMinValue() && hasMaxValue()) {
	    if (minValue.compareTo(maxValue) > 0) {
		throw new OpenDataException("minValue cannot be greater than maxValue.");
	    }
	}

	// Check that minValue <= defaultValue <= maxValue
	//
	if ( (this.hasDefaultValue()) && (this.hasMinValue()) ) {
	    if (minValue.compareTo((Comparable)defaultValue) > 0) {
		throw new OpenDataException("minValue cannot be greater than defaultValue.");
	    }
	}
	if ( (this.hasDefaultValue()) && (this.hasMaxValue()) ) {
	    if (((Comparable)defaultValue).compareTo(maxValue) > 0) {
		throw new OpenDataException("defaultValue cannot be greater than maxValue.");
	    }
	}
    }

    /**
     * Returns the open type for the values of the parameter described by this <tt>OpenMBeanParameterInfoSupport</tt> instance.
     */
    public OpenType getOpenType() { 
	return openType;
    }

    /**
     * Returns the default value for the parameter described by this <tt>OpenMBeanParameterInfoSupport</tt> instance,
     * if specified, or <tt>null</tt> otherwise.
     */
    public Object getDefaultValue() {

	// Special case for ArrayType and TabularType
	// [JF] TODO: clone it so that it cannot be altered,
	// [JF] TODO: if we decide to support defaultValue as an array itself.
	// [JF] As of today (oct 2000) it is not supported so defaultValue is null for arrays. Nothing to do.

	return defaultValue;
    }

    /**
     * Returns an unmodifiable Set of legal values for the parameter described by this <tt>OpenMBeanParameterInfoSupport</tt> instance,
     * if specified, or <tt>null</tt> otherwise.
     */
    public Set getLegalValues() {

	// Special case for ArrayType and TabularType
	// [JF] TODO: clone values so that they cannot be altered,
	// [JF] TODO: if we decide to support LegalValues as an array itself.
	// [JF] As of today (oct 2000) it is not supported so legalValues is null for arrays. Nothing to do.

	// Returns our legalValues Set (set was constructed unmodifiable)
	return (legalValues);
    }

    /**
     * Returns the minimal value for the parameter described by this <tt>OpenMBeanParameterInfoSupport</tt> instance,
     * if specified, or <tt>null</tt> otherwise.
     */
    public Comparable getMinValue() {

	// Note: only comparable values have a minValue, so that's not the case of arrays and tabulars (always null).

	return minValue;
    }

    /**
     * Returns the maximal value for the parameter described by this <tt>OpenMBeanParameterInfoSupport</tt> instance,
     * if specified, or <tt>null</tt> otherwise.
     */
    public Comparable getMaxValue() {

	// Note: only comparable values have a maxValue, so that's not the case of arrays and tabulars (always null).

	return maxValue;
    }

    /**
     * Returns <tt>true</tt> if this <tt>OpenMBeanParameterInfoSupport</tt> instance specifies a non-null default value 
     * for the described parameter, <tt>false</tt> otherwise.
     */
    public boolean hasDefaultValue() {

	return (defaultValue != null);
    }

    /**
     * Returns <tt>true</tt> if this <tt>OpenMBeanParameterInfoSupport</tt> instance specifies a non-null set of legal values
     * for the described parameter, <tt>false</tt> otherwise.
     */
    public boolean hasLegalValues() {

	return (legalValues != null);
    }

    /**
     * Returns <tt>true</tt> if this <tt>OpenMBeanParameterInfoSupport</tt> instance specifies a non-null minimal value 
     * for the described parameter, <tt>false</tt> otherwise.
     */
    public boolean hasMinValue() {

	return (minValue != null);
    }

    /**
     * Returns <tt>true</tt> if this <tt>OpenMBeanParameterInfoSupport</tt> instance specifies a non-null maximal value 
     * for the described parameter, <tt>false</tt> otherwise.
     */
    public boolean hasMaxValue() {

	return (maxValue != null);
    }


    /**
     * Tests whether <var>obj</var> is a valid value for the parameter
     * described by this <code>OpenMBeanParameterInfo</code> instance.
     *
     * @param obj the object to be tested.
     *
     * @return <code>true</code> if <var>obj</var> is a valid value
     * for the parameter described by this
     * <code>OpenMBeanParameterInfo</code> instance,
     * <code>false</code> otherwise.
     */
    public boolean isValue(Object obj) {

	boolean result;

	if ( hasDefaultValue() && obj == null ) {
	    result = true;
	} 
	else if ( ! openType.isValue(obj) ) {
	    result = false;
	}
	else if ( hasLegalValues() && ! legalValues.contains(obj) ) {
	    result = false;
	}
	else if ( hasMinValue() && (minValue.compareTo(obj)>0) ) {
	    result = false;
	}
	else if ( hasMaxValue() && (maxValue.compareTo(obj)<0) ) {
	    result = false;
	}
	else {
	    result = true;
	}

	return result;
    }


    /* ***  Commodity methods from java.lang.Object  *** */


    /**
     * Compares the specified <var>obj</var> parameter with this <code>OpenMBeanParameterInfoSupport</code> instance for equality. 
     * <p>
     * Returns <tt>true</tt> if and only if all of the following statements are true:
     * <ul>
     * <li><var>obj</var> is non null,</li>
     * <li><var>obj</var> also implements the <code>OpenMBeanParameterInfo</code> interface,</li>
     * <li>their names are equal</li>
     * <li>their open types are equal</li>
     * <li>their default, min, max and legal values are equal.</li>
     * </ul>
     * This ensures that this <tt>equals</tt> method works properly for <var>obj</var> parameters which are
     * different implementations of the <code>OpenMBeanParameterInfo</code> interface.
     * <br>&nbsp;
     * @param  obj  the object to be compared for equality with this <code>OpenMBeanParameterInfoSupport</code> instance;
     * 
     * @return  <code>true</code> if the specified object is equal to this <code>OpenMBeanParameterInfoSupport</code> instance.
     */
    public boolean equals(Object obj) { 

	// if obj is null, return false
	//
	if (obj == null) {
	    return false;
	}

	// if obj is not a OpenMBeanParameterInfo, return false
	//
	OpenMBeanParameterInfo other;
	try {
	    other = (OpenMBeanParameterInfo) obj;
	} catch (ClassCastException e) {
	    return false;
	}

	// Now, really test for equality between this OpenMBeanParameterInfo implementation and the other:
	//
	
	// their Name should be equal
	if ( ! this.getName().equals(other.getName()) ) {
	    return false;
	}

	// their OpenType should be equal
	if ( ! this.getOpenType().equals(other.getOpenType()) ) {
	    return false;
	}

	// their DefaultValue should be equal
	if (this.hasDefaultValue()) {
	    if ( ! this.defaultValue.equals(other.getDefaultValue()) ) {
		return false;
	    }
	} else {
	    if (other.hasDefaultValue()) {
		return false;
	    }
	}
       
	// their MinValue should be equal
	if (this.hasMinValue()) {
	    if ( ! this.minValue.equals(other.getMinValue()) ) {
		return false;
	    }
	} else {
	    if (other.hasMinValue()) {
		return false;
	    }
	}
       
	// their MaxValue should be equal
	if (this.hasMaxValue()) {
	    if ( ! this.maxValue.equals(other.getMaxValue()) ) {
		return false;
	    }
	} else {
	    if (other.hasMaxValue()) {
		return false;
	    }
	}
       
	// their LegalValues should be equal
	if (this.hasLegalValues()) {
	    if ( ! this.legalValues.equals(other.getLegalValues()) ) {
		return false;
	    }
	} else {
	    if (other.hasLegalValues()) {
		return false;
	    }
	}
       
	// All tests for equality were successfull
	//
	return true;
    }

    /**
     * Returns the hash code value for this <code>OpenMBeanParameterInfoSupport</code> instance. 
     * <p>
     * The hash code of an <code>OpenMBeanParameterInfoSupport</code> instance is the sum of the hash codes
     * of all elements of information used in <code>equals</code> comparisons 
     * (ie: its name, its <i>open type</i>, and its default, min, max and legal values). 
     * <p>
     * This ensures that <code> t1.equals(t2) </code> implies that <code> t1.hashCode()==t2.hashCode() </code> 
     * for any two <code>OpenMBeanParameterInfoSupport</code> instances <code>t1</code> and <code>t2</code>, 
     * as required by the general contract of the method
     * {@link Object#hashCode() Object.hashCode()}.
     * <p>
     * However, note that another instance of a class implementing the <code>OpenMBeanParameterInfo</code> interface
     * may be equal to this <code>OpenMBeanParameterInfoSupport</code> instance as defined by {@link #equals(java.lang.Object)}, 
     * but may have a different hash code if it is calculated differently.
     * <p>
     * As <code>OpenMBeanParameterInfoSupport</code> instances are immutable, the hash code for this instance is calculated once,
     * on the first call to <code>hashCode</code>, and then the same value is returned for subsequent calls.
     *
     * @return  the hash code value for this <code>OpenMBeanParameterInfoSupport</code> instance
     */
    public int hashCode() {

	// Calculate the hash code value if it has not yet been done (ie 1st call to hashCode())
	//
	if (myHashCode == null) {
	    int value = 0;
	    value += this.getName().hashCode();
	    value += this.openType.hashCode();
	    if (this.hasDefaultValue()) {
		value += this.defaultValue.hashCode();
	    }
	    if (this.hasMinValue()) {
		value += this.minValue.hashCode();
	    }
	    if (this.hasMaxValue()) {
		value += this.maxValue.hashCode();
	    }
	    if (this.hasLegalValues()) {
		value += this.legalValues.hashCode();
	    }
	    myHashCode = new Integer(value);
	}
	
	// return always the same hash code for this instance (immutable)
	//
	return myHashCode.intValue();
    }

    /**
     * Returns a string representation of this <code>OpenMBeanParameterInfoSupport</code> instance. 
     * <p>
     * The string representation consists of the name of this class (ie <code>javax.management.openmbean.OpenMBeanParameterInfoSupport</code>), 
     * the string representation of the name and open type of the described parameter, 
     * and the string representation of its default, min, max and legal values.
     * <p>
     * As <code>OpenMBeanParameterInfoSupport</code> instances are immutable, the string representation for this instance is calculated once,
     * on the first call to <code>toString</code>, and then the same value is returned for subsequent calls.
     * 
     * @return  a string representation of this <code>OpenMBeanParameterInfoSupport</code> instance
     */
    public String toString() { 

	// Calculate the hash code value if it has not yet been done (ie 1st call to hashCode())
	//
	if (myToString == null) {
	    myToString = new StringBuffer()
		.append(this.getClass().getName())
		.append("(name=")
		.append(this.getName())
		.append(",openType=")
		.append(this.openType.toString())
		.append(",default=")
		.append(String.valueOf(this.defaultValue))
		.append(",min=")
		.append(String.valueOf(this.minValue))
		.append(",max=")
		.append(String.valueOf(this.maxValue))
		.append(",legals=")
		.append(String.valueOf(this.legalValues))
		.append(")")
		.toString();
	}
	
	// return always the same string representation for this instance (immutable)
	//
	return myToString;
    }

}
