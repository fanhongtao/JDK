/*
 * @(#)file      DescriptorSupport.java
 * @(#)author    IBM Corp.
 * @(#)version   1.54
 * @(#)lastedit      07/07/24
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
 * Copyright 2005 Sun Microsystems, Inc.  All rights reserved.
 * This software is the proprietary information of Sun Microsystems, Inc.
 * Use is subject to license terms.
 *
 * Copyright 2005 Sun Microsystems, Inc.  Tous droits reserves.
 * Ce logiciel est propriete de Sun Microsystems, Inc.
 * Distribue par des licences qui en restreignent l'utilisation.
 *
 */

package javax.management.modelmbean;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;

import java.lang.reflect.Constructor;

import java.security.AccessController;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.management.RuntimeOperationsException;
import javax.management.MBeanException;

import com.sun.jmx.mbeanserver.GetPropertyAction;

import com.sun.jmx.trace.Trace;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import sun.reflect.misc.ReflectUtil;

/**
 * This class represents the metadata set for a ModelMBean element.  A
 * descriptor is part of the ModelMBeanInfo,
 * ModelMBeanNotificationInfo, ModelMBeanAttributeInfo,
 * ModelMBeanConstructorInfo, and ModelMBeanParameterInfo.
 * <P>
 * A descriptor consists of a collection of fields.  Each field is in
 * fieldname=fieldvalue format.  Field names are not case sensitive,
 * case will be preserved on field values.
 * <P>
 * All field names and values are not predefined. New fields can be
 * defined and added by any program.  Some fields have been predefined
 * for consistency of implementation and support by the
 * ModelMBeanInfo, ModelMBeanAttributeInfo, ModelMBeanConstructorInfo,
 * ModelMBeanNotificationInfo, ModelMBeanOperationInfo and ModelMBean
 * classes.
 *
 * @since 1.5
 */

public class DescriptorSupport
	 implements javax.management.Descriptor
{

    // Serialization compatibility stuff:
    // Two serial forms are supported in this class. The selected form depends
    // on system property "jmx.serial.form":
    //  - "1.0" for JMX 1.0
    //  - any other value for JMX 1.1 and higher
    //
    // Serial version for old serial form
    private static final long oldSerialVersionUID = 8071560848919417985L;
    //
    // Serial version for new serial form
    private static final long newSerialVersionUID = -6292969195866300415L;
    //
    // Serializable fields in old serial form
    private static final ObjectStreamField[] oldSerialPersistentFields =
    {
      new ObjectStreamField("descriptor", HashMap.class),
      new ObjectStreamField("currClass", String.class)
    };
    //
    // Serializable fields in new serial form
    private static final ObjectStreamField[] newSerialPersistentFields =
    {
      new ObjectStreamField("descriptor", HashMap.class)
    };
    //
    // Actual serial version and serial form
    private static final long serialVersionUID;
    /**
     * @serialField descriptor HashMap The collection of fields representing this descriptor
     */
    private static final ObjectStreamField[] serialPersistentFields;
    private static final String serialForm;
    static {
	String form = null;
	boolean compat = false;
	try {
	    GetPropertyAction act = new GetPropertyAction("jmx.serial.form");
	    form = (String) AccessController.doPrivileged(act);
	    compat = "1.0".equals(form);  // form may be null
	} catch (Exception e) {
	    // OK: No compat with 1.0
	}
	serialForm = form;
	if (compat) {
	    serialPersistentFields = oldSerialPersistentFields;
	    serialVersionUID = oldSerialVersionUID;
	} else {
	    serialPersistentFields = newSerialPersistentFields;
	    serialVersionUID = newSerialVersionUID;
	}
    }
    //
    // END Serialization compatibility stuff

    /* Spec says that field names are case-insensitive, but that case
       is preserved.  This means that we need to be able to map from a
       name that may differ in case to the actual name that is used in
       the HashMap.  Thus, descriptorMap is a TreeMap with a Comparator
       that ignores case.

       Previous versions of this class had a field called "descriptor"
       of type HashMap where the keys were directly Strings.  This is
       hard to reconcile with the required semantics, so we fabricate
       that field virtually during serialization and deserialization
       but keep the real information in descriptorMap.
    */
    private transient SortedMap<String, Object> descriptorMap;

    private static final int DEFAULT_SIZE = 20;
    private static final String currClass = "DescriptorSupport";


    /**
     * Descriptor default constructor.
     * Default initial descriptor size is 20.  It will grow as needed.<br>
     * Note that the created empty descriptor is not a valid descriptor
     * (the method {@link #isValid isValid} returns <CODE>false</CODE>)
     */
    public DescriptorSupport() {
        if (tracing())
            trace("DescriptorSupport()", "Constructor");
        init(null);
    }

    /**
     * Descriptor constructor.  Takes as parameter the initial
     * capacity of the Map that stores the descriptor fields.
     * Capacity will grow as needed.<br> Note that the created empty
     * descriptor is not a valid descriptor (the method {@link
     * #isValid isValid} returns <CODE>false</CODE>).
     *
     * @param initNumFields The initial capacity of the Map that
     * stores the descriptor fields.
     *
     * @exception RuntimeOperationsException for illegal value for
     * initNumFields (&lt;= 0)
     * @exception MBeanException Wraps a distributed communication Exception.
     */
    public DescriptorSupport(int initNumFields)
	    throws MBeanException, RuntimeOperationsException {
        if (tracing()) {
            trace("Descriptor(initNumFields=" + initNumFields + ")",
                  "Constructor");
        }
	if (initNumFields <= 0) {
	    if (tracing()) {
		trace("Descriptor(maxNumFields)",
		      "Illegal arguments: initNumFields <= 0");
	    }
	    final String msg =
		"Descriptor field limit invalid: " + initNumFields;
	    final RuntimeException iae = new IllegalArgumentException(msg);
	    throw new RuntimeOperationsException(iae, msg);
	}
        init(null);
    }

    /**
     * Descriptor constructor taking a Descriptor as parameter.
     * Creates a new descriptor initialized to the values of the
     * descriptor passed in parameter.
     *
     * @param inDescr the descriptor to be used to initialize the
     * constructed descriptor. If it is null or contains no descriptor
     * fields, an empty Descriptor will be created.
     */
    public DescriptorSupport(DescriptorSupport inDescr) {
	if (tracing()) {
	    trace("Descriptor(Descriptor)","Constructor");
	}
	if (inDescr == null)
            init(null);
        else
            init(inDescr.descriptorMap);
    }


    /**
     * <p>Descriptor constructor taking an XML String.</p>
     *
     * <p>The format of the XML string is not defined, but an
     * implementation must ensure that the string returned by
     * {@link #toXMLString() toXMLString()} on an existing
     * descriptor can be used to instantiate an equivalent
     * descriptor using this constructor.</p>
     *
     * <p>In this implementation, all field values will be created
     * as Strings.  If the field values are not Strings, the
     * programmer will have to reset or convert these fields
     * correctly.</p>
     *
     * @param inStr An XML-formatted string used to populate this
     * Descriptor.  The format is not defined, but any
     * implementation must ensure that the string returned by
     * method {@link #toXMLString toXMLString} on an existing
     * descriptor can be used to instantiate an equivalent
     * descriptor when instantiated using this constructor.
     *
     * @exception RuntimeOperationsException If the String inStr
     * passed in parameter is null
     * @exception XMLParseException XML parsing problem while parsing
     * the input String
     * @exception MBeanException Wraps a distributed communication Exception.
     */
    /* At some stage we should rewrite this code to be cleverer.  Using
       a StringTokenizer as we do means, first, that we accept a lot of
       bogus strings without noticing they are bogus, and second, that we
       split the string being parsed at characters like > even if they
       occur in the middle of a field value. */
    public DescriptorSupport(String inStr)
	    throws MBeanException, RuntimeOperationsException,
		   XMLParseException {
	/* parse an XML-formatted string and populate internal
	 * structure with it */
	if (tracing()) {
	    trace("Descriptor(String ='" + inStr + "')","Constructor");
	}
	if (inStr == null) {
	    if (tracing()) {
		trace("Descriptor(String = null)","Illegal arguments");
	    }
	    final String msg = "String in parameter is null";
	    final RuntimeException iae = new IllegalArgumentException(msg);
	    throw new RuntimeOperationsException(iae, msg);
	}

	final String lowerInStr = inStr.toLowerCase();
	if (!lowerInStr.startsWith("<descriptor>")
	    || !lowerInStr.endsWith("</descriptor>")) {
	    throw new XMLParseException("No <descriptor>, </descriptor> pair");
	}

	// parse xmlstring into structures
        init(null);
	// create dummy descriptor: should have same size
	// as number of fields in xmlstring
	// loop through structures and put them in descriptor

	StringTokenizer st = new StringTokenizer(inStr, "<> \t\n\r\f");

	boolean inFld = false;
	boolean inDesc = false;
	String fieldName = null;
	String fieldValue = null;


	while (st.hasMoreTokens()) {  // loop through tokens
	    String tok = st.nextToken();

	    if (tok.equalsIgnoreCase("FIELD")) {
		inFld = true;
	    } else if (tok.equalsIgnoreCase("/FIELD")) {
		if ((fieldName != null) && (fieldValue != null)) {
		    fieldName =
			fieldName.substring(fieldName.indexOf('"') + 1,
					    fieldName.lastIndexOf('"'));
		    final Object fieldValueObject =
			parseQuotedFieldValue(fieldValue);
		    setField(fieldName, fieldValueObject);
		}
		fieldName = null;
		fieldValue = null;
		inFld = false;
	    } else if (tok.equalsIgnoreCase("DESCRIPTOR")) {
		inDesc = true;
	    } else if (tok.equalsIgnoreCase("/DESCRIPTOR")) {
		inDesc = false;
		fieldName = null;
		fieldValue = null;
		inFld = false;
	    } else if (inFld && inDesc) {
		// want kw=value, eg, name="myname" value="myvalue"
		int eq_separator = tok.indexOf("=");
		if (eq_separator > 0) {
		    String kwPart = tok.substring(0,eq_separator);
		    String valPart = tok.substring(eq_separator+1);
		    if (kwPart.equalsIgnoreCase("NAME"))
			fieldName = valPart;
		    else if (kwPart.equalsIgnoreCase("VALUE"))
			fieldValue = valPart;
		    else {  // xml parse exception
			final String msg =
			    "Expected `name' or `value', got `" + tok + "'";
			throw new XMLParseException(msg);
		    }
		} else { // xml parse exception
		    final String msg =
			"Expected `keyword=value', got `" + tok + "'";
		    throw new XMLParseException(msg);
		}
	    }
	}  // while tokens

	if (tracing()) {
	    trace("Descriptor(XMLString)","Exit");
	}
    }

    /**
     * Constructor taking field names and field values.  The array and
     * array elements cannot be null.
     *
     * @param fieldNames String array of field names.  No elements of
     * this array can be null.
     * @param fieldValues Object array of the corresponding field
     * values.  Elements of the array can be null. The
     * <code>fieldValue</code> must be valid for the
     * <code>fieldName</code> (as defined in method {@link #isValid
     * isValid})
     *
     * <p>Note: array sizes of parameters should match. If both arrays
     * are null or empty, then an empty descriptor is created.</p>
     *
     * @exception RuntimeOperationsException for illegal value for
     * field Names or field Values.  The array lengths must be equal.
     * If the descriptor construction fails for any reason, this
     * exception will be thrown.
     *
     */
    public DescriptorSupport(String[] fieldNames, Object[] fieldValues)
	    throws RuntimeOperationsException {
	if (tracing()) {
	    trace("Descriptor(fieldNames, fieldObjects)","Constructor");
	}

	if ((fieldNames == null) || (fieldValues == null) ||
	    (fieldNames.length != fieldValues.length)) {
	    if (tracing()) {
		trace("Descriptor(String[],Object[])","Illegal arguments");
	    }

	    final String msg =
		"Null or invalid fieldNames or fieldValues";
	    final RuntimeException iae = new IllegalArgumentException(msg);
	    throw new RuntimeOperationsException(iae, msg);
	}

	/* populate internal structure with fields */
        init(null);
	for (int i=0; i < fieldNames.length; i++) {
	    // setField will throw an exception if a fieldName is be null.
	    // the fieldName and fieldValue will be validated in setField.
	    setField(fieldNames[i], fieldValues[i]);
	}
	if (tracing()) {
	    trace("Descriptor(fieldNames, fieldObjects)","Exit");
	}
    }

    /**
     * Constructor taking fields in the <i>fieldName=fieldValue</i>
     * format.
     *
     * @param fields String array with each element containing a
     * field name and value.  If this array is null or empty, then the
     * default constructor will be executed. Null strings or empty
     * strings will be ignored.
     *
     * <p>All field values should be Strings.  If the field values are
     * not Strings, the programmer will have to reset or convert these
     * fields correctly.
     *
     * <p>Note: Each string should be of the form
     * <i>fieldName=fieldValue</i>.
     *
     * @exception RuntimeOperationsException for illegal value for
     * field Names or field Values.  The field must contain an
     * "=". "=fieldValue", "fieldName", and "fieldValue" are illegal.
     * FieldName cannot be null.  "fieldName=" will cause the value to
     * be null.  If the descriptor construction fails for any reason,
     * this exception will be thrown.
     *
     */
    public DescriptorSupport(String[] fields)
    {
	if (tracing()) {
	    trace("Descriptor(fields)","Constructor");
	}
        init(null);
	if (( fields == null ) || ( fields.length == 0))
            return;

        init(null);

	for (int i=0; i < fields.length; i++) {
	    if ((fields[i] == null) || (fields[i].equals(""))) {
		continue;
	    }
	    int eq_separator = fields[i].indexOf("=");
	    if (eq_separator < 0) {
		// illegal if no = or is first character
		if (tracing()) {
		    trace("Descriptor(String[])",
			  "Illegal arguments: field does not have '=' " +
			  "as a name and value separator");
		}
		final String msg = "Field in invalid format: no equals sign";
		final RuntimeException iae = new IllegalArgumentException(msg);
		throw new RuntimeOperationsException(iae, msg);
	    }

	    String fieldName = fields[i].substring(0,eq_separator);
	    String fieldValue = null;
	    if (eq_separator < fields[i].length()) {
		// = is not in last character
		fieldValue = fields[i].substring(eq_separator+1);
	    }

	    if (fieldName.equals("")) {
		if (tracing()) {
		    trace("Descriptor(String[])",
			  "Illegal arguments: fieldName is empty");
		}

		final String msg = "Field in invalid format: no fieldName";
		final RuntimeException iae = new IllegalArgumentException(msg);
		throw new RuntimeOperationsException(iae, msg);
	    }

	    setField(fieldName,fieldValue);
	}
	if (tracing()) {
	    trace("Descriptor(fields)","Exit");
	}
    }
    
    private void init(Map<String, ?> initMap) {
        descriptorMap =
                new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
        if (initMap != null)
            descriptorMap.putAll(initMap);
    }

    // Implementation of the Descriptor interface


    /**
     * Returns the value for a specific fieldname.
     *
     * @param inFieldName The field name in question; if not found,
     * null is returned.
     *
     * @return An Object representing the field value
     *
     * @exception RuntimeOperationsException for illegal value (null
     * or empty string) for field Names.
     */
    public synchronized Object getFieldValue(String inFieldName)
	    throws RuntimeOperationsException {

	if ((inFieldName == null) || (inFieldName.equals(""))) {
	    if (tracing()) {
		trace("getField()","Illegal arguments: null field name.");
	    }
	    final String msg = "Fieldname requested is null";
	    final RuntimeException iae = new IllegalArgumentException(msg);
	    throw new RuntimeOperationsException(iae, msg);
	}
	Object retValue = descriptorMap.get(inFieldName);
	if (tracing()) {
	    trace("getField(" + inFieldName + ")",
		  "Returns '" + retValue + "'");
	}
	return(retValue);
    }

    /**
     * Sets the string value for a specific fieldname. The value
     * must be valid for the field (as defined in method {@link
     * #isValid isValid}).  If the field does not exist, it is
     * added to the Descriptor.  If it does exist, the
     * value is replaced.
     *
     * @param inFieldName The field name to be set. Must
     * not be null or empty string.
     * @param fieldValue The field value to be set for the field
     * name. Can be null or empty string.
     *
     * @exception RuntimeOperationsException for illegal value for
     * field Names.
     *
     */
    public synchronized void setField(String inFieldName, Object fieldValue)
	    throws RuntimeOperationsException {

	// field name cannot be null or empty
	if ((inFieldName == null) || (inFieldName.equals(""))) {
	    if (tracing()) {
		trace("setField(String,String)",
		      "Illegal arguments: null or empty field name");
	    }

	    final String msg = "Fieldname to be set is null or empty";
	    final RuntimeException iae = new IllegalArgumentException(msg);
	    throw new RuntimeOperationsException(iae, msg);
	}

	if (!validateField(inFieldName, fieldValue)) {
	    if (tracing()) {
		trace("setField(fieldName,FieldValue)","Illegal arguments");
	    }

	    final String msg =
		"Field value invalid: " + inFieldName + "=" + fieldValue;
	    final RuntimeException iae = new IllegalArgumentException(msg);
	    throw new RuntimeOperationsException(iae, msg);
	}

	if (tracing()) {
	    if (fieldValue != null) {
		trace("setField(fieldName, fieldValue)",
		      "Entry: setting '" + inFieldName + "' to '" +
		      fieldValue + "'.");
	    }
	}

        // Since we do not remove any existing entry with this name,
	// the field will preserve whatever case it had, ignoring
	// any difference there might be in inFieldName.
	descriptorMap.put(inFieldName, fieldValue);
    }

    /**
     * Returns all the fields in the descriptor. The order is not the
     * order in which the fields were set.
     *
     * @return String array of fields in the format
     * <i>fieldName=fieldValue</i>. If there are no fields in the
     * descriptor, then an empty String array is returned. If a
     * fieldValue is not a String then the toString() method is called
     * on it and its returned value is used as the value for the field
     * enclosed in parenthesis.
     *
     * @see #setFields
     */
    public synchronized String[] getFields() {
	if (tracing()) {
	    trace("getFields()","Entry");
	}
	int numberOfEntries = descriptorMap.size();

	String[] responseFields = new String[numberOfEntries];
	Set returnedSet = descriptorMap.entrySet();

	int i = 0;
	Object currValue = null;
	Map.Entry currElement = null;

	if (tracing()) {
	    trace("getFields()","Returning " + numberOfEntries + " fields");
	}
	for (Iterator iter = returnedSet.iterator(); iter.hasNext(); i++) {
	    currElement = (Map.Entry) iter.next();

	    if (currElement == null) {
		if (tracing()) {
		    trace("getFields()","Element is null");
		}
	    } else {
		currValue = currElement.getValue();
		if (currValue == null) {
		    responseFields[i] = currElement.getKey() + "=";
		} else {
		    if (currValue instanceof java.lang.String) {
			responseFields[i] =
			    currElement.getKey() + "=" + currValue.toString();
		    } else {
			responseFields[i] =
			    currElement.getKey() + "=(" +
			    currValue.toString() + ")";
		    }
		}
	    }
	}

	if (tracing()) {
	    trace("getFields()","Exit");
	}

	return responseFields;
    }

    /**
     * Returns all the fields names in the descriptor. The order is
     * not the order in which the fields were set.
     *
     * @return String array of fields names. If the descriptor is
     * empty, you will get an empty array.
     *
     */
    public synchronized String[] getFieldNames() {
	if (tracing()) {
	    trace("getFieldNames()","Entry");
	}
	int numberOfEntries = descriptorMap.size();

	String[] responseFields = new String[numberOfEntries];
	Set returnedSet = descriptorMap.entrySet();

	int i = 0;

	if (tracing()) {
	    trace("getFieldNames()","Returning " + numberOfEntries + " fields");
	}

	for (Iterator iter = returnedSet.iterator(); iter.hasNext(); i++) {
	    Map.Entry currElement = (Map.Entry) iter.next();

	    if (( currElement == null ) || (currElement.getKey() == null)) {
		if (tracing()) {
		    trace("getFieldNames()","Field is null");
		}
	    } else {
		responseFields[i] = currElement.getKey().toString();
	    }
	}

	if (tracing()) {
	    trace("getFieldNames()","Exit");
	}

	return responseFields;
    }


    /**
     * Returns all the field values in the descriptor as an array of
     * Objects. The returned values are in the same order as the
     * fieldNames String array parameter.
     *
     * @param fieldNames String array of the names of the fields that
     * the values should be returned for.<br>
     * If the array is empty then an empty array will be returned.<br>
     * If the array is 'null' then all values will be returned. The
     * order is not the order in which the fields were set.<br>
     * If a field name in the array does not exist, then null is
     * returned for the matching array element being returned.
     *
     * @return Object array of field values. If the descriptor is
     * empty, you will get an empty array.
     */
    public synchronized Object[] getFieldValues(String[] fieldNames) {
	if (tracing()) {
	    trace("getFieldValues(fieldNames)","Entry");
	}
	// if fieldNames == null return all values
	// if fieldNames is String[0] return no values

	int numberOfEntries = descriptorMap.size();

	/* Following test is somewhat inconsistent but is called for
	   by the @return clause above. */
	if (numberOfEntries == 0)
	    return new Object[0];

	Object[] responseFields;
	if (fieldNames != null) {
	    responseFields = new Object[fieldNames.length];
	    // room for selected
	} else {
	    responseFields = new Object[numberOfEntries];
	    // room for all
	}

	int i = 0;

	if (tracing()) {
	    trace("getFieldValues()",
		  "Returning " + numberOfEntries + " fields");
	}

	if (fieldNames == null) {
	    for (Iterator iter = descriptorMap.values().iterator();
		 iter.hasNext(); i++)
		responseFields[i] = iter.next();
	} else {
	    for (i=0; i < fieldNames.length; i++) {
		if ((fieldNames[i] == null) || (fieldNames[i].equals(""))) {
		    responseFields[i] = null;
		} else {
		    responseFields[i] = getFieldValue(fieldNames[i]);
		}
	    }
	}


	if (tracing()) {
	    trace("getFieldValues()","Exit");
	}

	return responseFields;
    }

    /**
     * Sets all Fields in the list to the new value with the same
     * index in the fieldValue array.  Array sizes must match.  The
     * field value will be validated before it is set (by calling the
     * method {@link #isValid isValid}).  If it is not valid, then an
     * exception will be thrown.  If the arrays are empty, then no
     * change will take effect.
     *
     * @param fieldNames String array of field names. The array and
     * array elements cannot be null.
     * @param fieldValues Object array of the corresponding field
     * values.  The array cannot be null.  Elements of the array can
     * be null.
     *
     * @exception RuntimeOperationsException for illegal value for
     * field Names or field Values.  Neither can be null.  The array
     * lengths must be equal.
     *
     * @see #getFields
     */
    public synchronized void setFields(String[] fieldNames,
				       Object[] fieldValues)
	    throws RuntimeOperationsException {

	if (tracing()) {
	    trace("setFields(fieldNames, ObjectValues)","Entry");
	}


	if ((fieldNames == null) || (fieldValues == null) ||
	    (fieldNames.length != fieldValues.length)) {
	    if (tracing()) {
		trace("Descriptor.setFields(String[],Object[])",
		      "Illegal arguments");
	    }

	    final String msg = "FieldNames and FieldValues are null or invalid";
	    final RuntimeException iae = new IllegalArgumentException(msg);
	    throw new RuntimeOperationsException(iae, msg);
	}

	for (int i=0; i < fieldNames.length; i++) {
	    if (( fieldNames[i] == null) || (fieldNames[i].equals(""))) {
		if (tracing()) {
		    trace("Descriptor.setFields(String[],Object[])",
			  "Null field name encountered at " + i + " element");
		}

		final String msg = "FieldNames is null or invalid";
		final RuntimeException iae = new IllegalArgumentException(msg);
		throw new RuntimeOperationsException(iae, msg);
	    }
	    setField(fieldNames[i], fieldValues[i]);
	}
	if (tracing()) {
	    trace("Descriptor.setFields(fieldNames, fieldObjects)","Exit");
	}
    }

    /**
     * Returns a new Descriptor which is a duplicate of the Descriptor.
     *
     * @exception RuntimeOperationsException for illegal value for
     * field Names or field Values.  If the descriptor construction
     * fails for any reason, this exception will be thrown.
     */

    public synchronized Object clone() throws RuntimeOperationsException {
	if (tracing()) {
	    trace("Descriptor.clone()","Executed");
	}
	return(new DescriptorSupport(this));
    }

    /**
     * Removes a field from the descriptor.
     *
     * @param fieldName String name of the field to be removed.
     * If the field is not found no exception is thrown.
     */
    public synchronized void removeField(String fieldName) {
	if ((fieldName == null) || (fieldName.equals(""))) {
	    return;
	}

	descriptorMap.remove(fieldName);
    }


    /**
     * Returns true if all of the fields have legal values given their
     * names.
     * <P>
     * This implementation does not support  interoperating with a directory
     * or lookup service. Thus, conforming to the specification, no checking is
     * done on the <i>"export"</i> field.
     * <P>
     * Otherwise this implementation returns false if:
     * <P>
     * <UL>
     * <LI> name and descriptorType fieldNames are not defined, or
     * null, or empty, or not String
     * <LI> class, role, getMethod, setMethod fieldNames, if defined,
     * are null or not String
     * <LI> persistPeriod, currencyTimeLimit, lastUpdatedTimeStamp,
     * lastReturnedTimeStamp if defined, are null, or not a Numeric
     * String or not a Numeric Value >= -1
     * <LI> log fieldName, if defined, is null, or not a Boolean or
     * not a String with value "t", "f", "true", "false". These String
     * values must not be case sensitive.
     * <LI> visibility fieldName, if defined, is null, or not a
     * Numeric String or a not Numeric Value >= 1 and <= 4
     * <LI> severity fieldName, if defined, is null, or not a Numeric
     * String or not a Numeric Value >= 0 and <= 6<br>
     * <LI> persistPolicy fieldName, if defined, is null, or not a
     * following String :<br>
     *   "OnUpdate", "OnTimer", "NoMoreOftenThan", "Always",
     *   "Never". These String values must not be case sensitive.<br>
     * </UL>
     *
     * @exception RuntimeOperationsException If the validity checking
     * fails for any reason, this exception will be thrown.
     */

    public synchronized boolean isValid() throws RuntimeOperationsException {
	if (tracing()) {
	    trace("Descriptor.isValid()","Executed");
	}
	// verify that the descriptor is valid, by iterating over each field...

	Set returnedSet = descriptorMap.entrySet();

	if (returnedSet == null) {   // null descriptor, not valid
	    if (tracing()) {
		trace("Descriptor.isValid()","returns false (null set)");
	    }
	    return false;
	}
	// must have a name and descriptor type field
	String thisName = (String)(this.getFieldValue("name"));
	String thisDescType = (String)(getFieldValue("descriptorType"));

	if ((thisName == null) || (thisDescType == null) ||
	    (thisName.equals("")) || (thisDescType.equals(""))) {
	    return false;
	}

	// According to the descriptor type we validate the fields contained

	for (Iterator iter = returnedSet.iterator(); iter.hasNext();) {
	    Map.Entry currElement = (Map.Entry) iter.next();

	    if (currElement != null) {
		if (currElement.getValue() != null) {
		    // validate the field valued...
		    if (validateField((currElement.getKey()).toString(),
				      (currElement.getValue()).toString())) {
			continue;
		    } else {
			if (tracing()) {
			    trace("isValid()",
				  "Field " + currElement.getKey() + "=" +
				  currElement.getValue() + " is not valid");
			}
			return false;
		    }
		}
	    }
	}

	// fell through, all fields OK

	if (tracing()) {
	    trace("Descriptor.isValid()","returns true");
	}

	return true;
    }


    // worker routine for isValid()
    // name is not null
    // descriptorType is not null
    // getMethod and setMethod are not null
    // persistPeriod is numeric
    // currencyTimeLimit is numeric
    // lastUpdatedTimeStamp is numeric
    // visibility is 1-4
    // severity is 0-6
    // log is T or F
    // role is not null
    // class is not null
    // lastReturnedTimeStamp is numeric


    private boolean validateField(String fldName, Object fldValue) {
	if ((fldName == null) || (fldName.equals("")))
	    return false;
	String SfldValue = "";
	boolean isAString = false;
	if ((fldValue != null) && (fldValue instanceof java.lang.String)) {
	    SfldValue = (String) fldValue;
	    isAString = true;
	}

	boolean nameOrDescriptorType =
	    (fldName.equalsIgnoreCase("Name") ||
	     fldName.equalsIgnoreCase("DescriptorType"));
	if (nameOrDescriptorType ||
	    fldName.equalsIgnoreCase("SetMethod") ||
	    fldName.equalsIgnoreCase("GetMethod") ||
	    fldName.equalsIgnoreCase("Role") ||
	    fldName.equalsIgnoreCase("Class")) {
	    if (fldValue == null || !isAString)
		return false;
	    if (nameOrDescriptorType && SfldValue.equals(""))
		return false;
	    return true;
	} else if (fldName.equalsIgnoreCase("visibility")) {
	    long v;
	    if ((fldValue != null) && (isAString)) {
		v = toNumeric(SfldValue);
	    } else if (fldValue instanceof java.lang.Integer) {
		v = ((Integer)fldValue).intValue();
	    } else return false;

	    if (v >= 1 &&  v <= 4)
		return true;
	    else
		return false;
	} else if (fldName.equalsIgnoreCase("severity")) {

	    long v;
	    if ((fldValue != null) && (isAString)) {
		v = toNumeric(SfldValue);
	    } else if (fldValue instanceof java.lang.Integer) {
		v = ((Integer)fldValue).intValue();
	    } else return false;

	    return (v >= 0 && v <= 6);
	} else if (fldName.equalsIgnoreCase("PersistPolicy")) {
	    return (((fldValue != null) && (isAString)) &&
		    ( SfldValue.equalsIgnoreCase("OnUpdate") ||
		      SfldValue.equalsIgnoreCase("OnTimer") ||
		      SfldValue.equalsIgnoreCase("NoMoreOftenThan") ||
		      SfldValue.equalsIgnoreCase("Always") ||
		      SfldValue.equalsIgnoreCase("Never") ));
	} else if (fldName.equalsIgnoreCase("PersistPeriod") ||
		   fldName.equalsIgnoreCase("CurrencyTimeLimit") ||
		   fldName.equalsIgnoreCase("LastUpdatedTimeStamp") ||
		   fldName.equalsIgnoreCase("LastReturnedTimeStamp")) {

	    long v;
	    if ((fldValue != null) && (isAString)) {
		v = toNumeric(SfldValue);
	    } else if (fldValue instanceof java.lang.Number) {
		v = ((Number)fldValue).longValue();
	    } else return false;

	    return (v >= -1);
	} else if (fldName.equalsIgnoreCase("log")) {
	    return ((fldValue instanceof java.lang.Boolean) ||
		    (isAString &&
		     (SfldValue.equalsIgnoreCase("T") ||
		      SfldValue.equalsIgnoreCase("true") ||
		      SfldValue.equalsIgnoreCase("F") ||
		      SfldValue.equalsIgnoreCase("false") )));
	}

	// default to true, it is a field we aren't validating (user etc.)
	return true;
    }



    /**
     * <p>Returns an XML String representing the descriptor.</p>
     *
     * <p>The format is not defined, but an implementation must
     * ensure that the string returned by this method can be
     * used to build an equivalent descriptor when instantiated
     * using the constructor {@link #DescriptorSupport(String)
     * DescriptorSupport(String inStr)}.</p>
     *
     * <p>Fields which are not String objects will have toString()
     * called on them to create the value. The value will be
     * enclosed in parentheses.  It is not guaranteed that you can
     * reconstruct these objects unless they have been
     * specifically set up to support toString() in a meaningful
     * format and have a matching constructor that accepts a
     * String in the same format.</p>
     *
     * <p>If the descriptor is empty the following String is
     * returned: &lt;Descriptor&gt;&lt;/Descriptor&gt;</p>
     *
     * @return the XML string.
     *
     * @exception RuntimeOperationsException for illegal value for
     * field Names or field Values.  If the XML formated string
     * construction fails for any reason, this exception will be
     * thrown.
     */
    public synchronized String toXMLString() {
	StringBuffer buf = new StringBuffer("<Descriptor>");
	Set returnedSet = descriptorMap.entrySet();
	for (Iterator iter = returnedSet.iterator(); iter.hasNext(); ) {
	    final Map.Entry currElement = (Map.Entry) iter.next();
	    final String name = currElement.getKey().toString();
	    Object value = currElement.getValue();
	    String valueString = null;
	    /* Set valueString to non-null iff this is a string that
	       cannot be confused with the encoding of an object.  If it
	       could be so confused (surrounded by parentheses) then we
	       call makeFieldValue as for any non-String object and end
	       up with an encoding like "(java.lang.String/(thing))".  */
	    if (value instanceof String) {
		final String svalue = (String) value;
		if (!svalue.startsWith("(") || !svalue.endsWith(")"))
		    valueString = quote(svalue);
	    }
	    if (valueString == null)
		valueString = makeFieldValue(value);
	    buf.append("<field name=\"").append(name).append("\" value=\"")
		.append(valueString).append("\"></field>");
	}
	buf.append("</Descriptor>");
	return buf.toString();
    }

    private static final String[] entities = {
	" &#32;",
	"\"&quot;",
	"<&lt;",
	">&gt;",
	"&&amp;",
	"\r&#13;",
	"\t&#9;",
	"\n&#10;",
	"\f&#12;",
    };
    private static final Map<String,Character> entityToCharMap =
	new HashMap<String,Character>();
    private static final String[] charToEntityMap;

    static {
	char maxChar = 0;
	for (int i = 0; i < entities.length; i++) {
	    final char c = entities[i].charAt(0);
	    if (c > maxChar)
		maxChar = c;
	}
	charToEntityMap = new String[maxChar + 1];
	for (int i = 0; i < entities.length; i++) {
	    final char c = entities[i].charAt(0);
	    final String entity = entities[i].substring(1);
	    charToEntityMap[c] = entity;
	    entityToCharMap.put(entity, new Character(c));
	}
    }

    private static boolean isMagic(char c) {
	return (c < charToEntityMap.length && charToEntityMap[c] != null);
    }

    /*
     * Quote the string so that it will be acceptable to the (String)
     * constructor.  Since the parsing code in that constructor is fairly
     * stupid, we're obliged to quote apparently innocuous characters like
     * space, <, and >.  In a future version, we should rewrite the parser
     * and only quote " plus either \ or & (depending on the quote syntax).
     */
    private static String quote(String s) {
	boolean found = false;
	for (int i = 0; i < s.length(); i++) {
	    if (isMagic(s.charAt(i))) {
		found = true;
		break;
	    }
	}
	if (!found)
	    return s;
	StringBuffer buf = new StringBuffer();
	for (int i = 0; i < s.length(); i++) {
	    char c = s.charAt(i);
	    if (isMagic(c))
		buf.append(charToEntityMap[c]);
	    else
		buf.append(c);
	}
	return buf.toString();
    }

    private static String unquote(String s) throws XMLParseException {
	if (!s.startsWith("\"") || !s.endsWith("\""))
	    throw new XMLParseException("Value must be quoted: <" + s + ">");
	StringBuffer buf = new StringBuffer();
	final int len = s.length() - 1;
	for (int i = 1; i < len; i++) {
	    final char c = s.charAt(i);
	    final int semi;
	    final Character quoted;
	    if (c == '&'
		&& (semi = s.indexOf(';', i + 1)) >= 0
		&& ((quoted =
		    (Character) entityToCharMap.get(s.substring(i, semi+1)))
		    != null)) {
		buf.append(quoted);
		i = semi;
	    } else
		buf.append(c);
	}
	return buf.toString();
    }

    /**
     * Make the string that will go inside "..." for a value that is not
     * a plain String.
     * @throws RuntimeOperationsException if the value cannot be encoded.
     */
    private static String makeFieldValue(Object value) {
	if (value == null)
	    return "(null)";

	Class valueClass = value.getClass();
	try {
	    valueClass.getConstructor(new Class[] {String.class});
	} catch (NoSuchMethodException e) {
	    final String msg =
		"Class " + valueClass + " does not have a public " +
		"constructor with a single string arg";
	    final RuntimeException iae = new IllegalArgumentException(msg);
	    throw new RuntimeOperationsException(iae,
						 "Cannot make XML descriptor");
	} catch (SecurityException e) {
	    // OK: we'll pretend the constructor is there
	    // too bad if it's not: we'll find out when we try to
	    // reconstruct the DescriptorSupport
	}

	final String quotedValueString = quote(value.toString());
	
	return "(" + valueClass.getName() + "/" + quotedValueString + ")";
    }

    /*
     * Parse a field value from the XML produced by toXMLString().
     * Given a descriptor XML containing <field name="nnn" value="vvv">,
     * the argument to this method will be "vvv" (a string including the
     * containing quote characters).  If vvv begins and ends with parentheses,
     * then it may contain:
     * - the characters "null", in which case the result is null;
     * - a value of the form "some.class.name/xxx", in which case the
     * result is equivalent to `new some.class.name("xxx")';
     * - some other string, in which case the result is that string,
     * without the parentheses.
     */
    private static Object parseQuotedFieldValue(String s)
	    throws XMLParseException {
	s = unquote(s);
	if (s.equalsIgnoreCase("(null)"))
	    return null;
	if (!s.startsWith("(") || !s.endsWith(")"))
	    return s;
	final int slash = s.indexOf('/');
	if (slash < 0) {
	    // compatibility: old code didn't include class name
	    return s.substring(1, s.length() - 1);
	}
	final String className = s.substring(1, slash);
	final Constructor constr;
	try {
	    final ClassLoader contextClassLoader =
		Thread.currentThread().getContextClassLoader();
            if (contextClassLoader == null)
                ReflectUtil.checkPackageAccess(className);
	    final Class c =
		Class.forName(className, false, contextClassLoader);
	    constr = c.getConstructor(new Class[] {String.class});
	} catch (Exception e) {
	    throw new XMLParseException(e,
					"Cannot parse value: <" + s + ">");
	}
	final String arg = s.substring(slash + 1, s.length() - 1);
	try {
	    return constr.newInstance(new Object[] {arg});
	} catch (Exception e) {
	    final String msg =
		"Cannot construct instance of " + className +
		" with arg: <" + s + ">";
	    throw new XMLParseException(e, msg);
	}
    }

    /**
     * Returns <pv>a human readable string representing the
     * descriptor</pv>.  The string will be in the format of
     * "fieldName=fieldValue,fieldName2=fieldValue2,..."<br>
     *
     * If there are no fields in the descriptor, then an empty String
     * is returned.<br>
     *
     * If a fieldValue is an object then the toString() method is
     * called on it and its returned value is used as the value for
     * the field enclosed in parenthesis.
     *
     * @exception RuntimeOperationsException for illegal value for
     * field Names or field Values.  If the descriptor string fails
     * for any reason, this exception will be thrown.
     */
    public synchronized String toString() {
	if (tracing()) {
	    trace("Descriptor.toString()","Entry");
	}

	String respStr = "";
	String[] fields = getFields();

	if (tracing()) {
	    trace("Descriptor.toString()",
		  "Printing " + fields.length + " fields");
	}

	if ((fields == null) || (fields.length == 0)) {
	    if (tracing()) {
		trace("Descriptor.toString()","Empty Descriptor");
	    }
	    return respStr;
	}

	for (int i=0; i < fields.length; i++) {
	    if (i == (fields.length - 1)) {
		respStr = respStr.concat(fields[i]);
	    } else {
		respStr = respStr.concat(fields[i] + ", ");
	    }
	}

	if (tracing()) {
	    trace("Descriptor.toString()","Exit returning " + respStr);
	}

	return respStr;
    }

    // utility to convert to int, returns -2 if bogus.

    private long toNumeric(String inStr) {
	long result = -2;
	try {
	    result = java.lang.Long.parseLong(inStr);
	} catch (Exception e) {
	    return -2;
	}
	return result;
    }


    // Trace and debug functions

    private boolean tracing() {
	return Trace.isSelected(Trace.LEVEL_TRACE, Trace.INFO_MODELMBEAN);
    }

    private void trace(String inClass, String inMethod, String inText) {
	Trace.send(Trace.LEVEL_TRACE, Trace.INFO_MODELMBEAN, inClass,
		   inMethod,
		   Integer.toHexString(this.hashCode()) + " " + inText);
    }

    private void trace(String inMethod, String inText) {
	trace(currClass, inMethod, inText);
    }

    /**
     * Deserializes a {@link DescriptorSupport} from an {@link
     * ObjectInputStream}.
     */
    private void readObject(ObjectInputStream in)
	    throws IOException, ClassNotFoundException {
	ObjectInputStream.GetField fields = in.readFields();
	Map descriptor = (Map) fields.get("descriptor", null);
	init(null);
        descriptorMap.putAll(descriptor);
    }


    /**
     * Serializes a {@link DescriptorSupport} to an {@link ObjectOutputStream}.
     */
    /* If you set jmx.serial.form to "1.2.0" or "1.2.1", then we are
       bug-compatible with those versions.  Specifically, field names
       are forced to lower-case before being written.  This
       contradicts the spec, which, though it does not mention
       serialization explicitly, does say that the case of field names
       is preserved.  But in 1.2.0 and 1.2.1, this requirement was not
       met.  Instead, field names in the descriptor map were forced to
       lower case.  Those versions expect this to have happened to a
       descriptor they deserialize and e.g. getFieldValue will not
       find a field whose name is spelt with a different case.
    */
    private void writeObject(ObjectOutputStream out) throws IOException {
	ObjectOutputStream.PutField fields = out.putFields();
	boolean compat = "1.0".equals(serialForm);
	if (compat)
	    fields.put("currClass", currClass);
        
        /* Purge the field "targetObject" from the DescriptorSupport before
         * serializing since the referenced object is typically not
         * serializable.  We do this here rather than purging the "descriptor"
         * variable below because that HashMap doesn't do case-insensitivity.
         * See CR 6332962.
         */
        SortedMap<String, Object> startMap = descriptorMap;
        if (startMap.containsKey("targetObject")) {
            startMap = new TreeMap<String, Object>(descriptorMap);
            startMap.remove("targetObject");
        }

	final HashMap<String, Object> descriptor;
	if (compat || "1.2.0".equals(serialForm) ||
                "1.2.1".equals(serialForm)) {
            descriptor = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : startMap.entrySet())
                descriptor.put(entry.getKey().toLowerCase(), entry.getValue());
        } else
            descriptor = new HashMap<String, Object>(startMap);

	fields.put("descriptor", descriptor);
	out.writeFields();
    }
}
