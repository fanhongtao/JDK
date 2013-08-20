/*
 * @(#)file      Descriptor.java
 * @(#)author    IBM Corp.
 * @(#)version   1.23
 * @(#)lastedit      04/02/10
 */
/*
 * Copyright IBM Corp. 1999-2000.  All rights reserved.
 * 
 * The program is provided "as is" without any warranty express or implied,
 * including the warranty of non-infringement and the implied warranties of
 * merchantibility and fitness for a particular purpose. IBM will not be
 * liable for any damages suffered by you or any third party claim against 
 * you regarding the Program.
 *
 * Copyright 2004 Sun Microsystems, Inc.  All rights reserved.
 * This software is the proprietary information of Sun Microsystems, Inc.
 * Use is subject to license terms.
 * 
 * Copyright 2004 Sun Microsystems, Inc.  Tous droits reserves.
 * Ce logiciel est propriete de Sun Microsystems, Inc.
 * Distribue par des licences qui en restreignent l'utilisation. 
 *
 */

package javax.management;


import javax.management.RuntimeOperationsException;
import javax.management.MBeanException;

import com.sun.jmx.trace.Trace;

/**
 * This interface represents the behavioral metadata set for a JMX Element.
 * For examples, a descriptor is part of the ModelMBeanInfo, ModelMBeanNotificationInfo, ModelMBeanAttributeInfo,
 * ModelMBeanConstructorInfo, and ModelMBeanParameterInfo.
 * <P>
 * A descriptor consists of a collection of fields.  Each field is in fieldname=fieldvalue format.
 * <P>
 * All field names and values are not predefined. New fields can be defined and added by any program.
 * In the case of ModelMBean some fields have been predefined for consistency of implementation and support by the ModelMBeanInfo
 * ModelMBean*Info, and ModelMBean classes.
 *<P>
 *
 * @since 1.5
 */
public interface Descriptor extends java.io.Serializable, Cloneable
{

	/**
	 * Returns the value for a specific fieldname.
	 *
	 * @param fieldName The field name in question; if not found, null is returned.
	 *
	 * @return Object Field value.
	 *
	 * @exception RuntimeOperationsException for illegal value for field name.
	 *              
	 */
	public Object getFieldValue(String fieldName)
	throws RuntimeOperationsException;

	/**         
	 * Sets the value for a specific fieldname.	The field value will be validated before
	 * it is set.  If it is not valid, then an exception will be thrown. This will modify
	 * an existing field or add a new field.
	 *
	 * @param fieldName The field name to be set. Cannot be null or empty.
	 * @param fieldValue The field value to be set for the field
	 * name.  Can be null.
	 *
	 * @exception RuntimeOperationsException for illegal value for field name or field value.
	 *              
	 */
	public void setField(String fieldName, Object fieldValue)
	throws RuntimeOperationsException;


	/**
	 * Returns all of the  fields contained in this descriptor as a string array.
	 * 
	 * @return String array of fields in the format <i>fieldName=fieldValue</i>
	 *          If the value of a field is not a String, then the toString() method
	 *          will be called on it and the returned value used as the value for
	 *          the field in the returned array. Object values which are not Strings
	 *          will be enclosed in parentheses. If the descriptor is empty, you will get
	 *          an empty array.    
	 *
	 * @see #setFields
	 */
	public String[] getFields() ;


	/**
	 * Returns all the fields names in the descriptor.
	 * 
	 * @return String array of fields names. If the descriptor is empty, you will get
	 *          an empty array.
	 *
	 */
	public String[] getFieldNames() ;

	/**
	 * Returns all the field values in the descriptor as an array of Objects. The
	 * returned values are in the same order as the fieldNames String array parameter.
	 *
	 * @param fieldNames String array of the names of the fields that the values
	 * should be returned for.  If the array is empty then an empty array will be 
	 * returned.  If the array is 'null' then all values will be returned.  If a field 
	 * name in the array does not exist, then null is returned for the matching array
	 * element being returned.
	 *
	 * @return Object array of field values. If the descriptor is empty, you will get
	 *          an empty array.
	 *
	 */
	public Object[] getFieldValues(String[] fieldNames) ; 

	/**
	 * Removes a field from the descriptor.
	 *
	 * @param fieldName String name of the field to be removed.
	 * If the field is not found no exception is thrown.
	 */
	public void removeField(String fieldName) ;

	/** 
	 * Sets all Fields in the list to the new value in with the same index
	 * in the fieldValue array.  Array sizes must match.  
	 * The field value will be validated before it is set.  
	 * If it is not valid, then an exception will be thrown.
	 * If the arrays are empty, then no change will take effect.
     * 
	 * @param fieldNames String array of field names. The array and array elements cannot be null.
	 * @param fieldValues Object array of the corresponding field values.  The array cannot be null.
	 *                      Elements of the array can be null.
	 *  
	 * @exception RuntimeOperationsException for illegal value for field Names or field Values.
	 *              Neither can be null.  The array lengths must be equal. 
	 *              If the descriptor construction fails for any reason, this exception will be thrown.
	 *
	 * @see #getFields
	 */
	public void setFields(String[] fieldNames, Object[] fieldValues) 
	throws RuntimeOperationsException;


	/**
	 * Returns a new Descriptor which is a duplicate of the Descriptor.
	 *
	 * @exception RuntimeOperationsException for illegal value for field Names or field Values.
	 *              If the descriptor construction fails for any reason, this exception will be thrown.
	 */
	public Object clone() throws RuntimeOperationsException;


	/**
	 * Returns true if all of the fields have legal values given their
	 * names.
	 *
	 * @return true if the values are legal.
	 *
	 * @exception RuntimeOperationsException If the validity checking fails for any reason, this exception will be thrown.
	 */
	public boolean isValid() throws RuntimeOperationsException;

}

