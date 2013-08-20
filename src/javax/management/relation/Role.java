/*
 * @(#)Role.java	1.32 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management.relation;

import javax.management.ObjectName;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import java.security.AccessController;
import java.security.PrivilegedAction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;

import com.sun.jmx.mbeanserver.GetPropertyAction;

/**
 * Represents a role: includes a role name and referenced MBeans (via their
 * ObjectNames). The role value is always represented as an ArrayList
 * collection (of ObjectNames) to homogenize the access.
 *
 * @since 1.5
 */
public class Role implements Serializable {

    // Serialization compatibility stuff:
    // Two serial forms are supported in this class. The selected form depends
    // on system property "jmx.serial.form":
    //  - "1.0" for JMX 1.0
    //  - any other value for JMX 1.1 and higher
    //
    // Serial version for old serial form 
    private static final long oldSerialVersionUID = -1959486389343113026L;
    //
    // Serial version for new serial form 
    private static final long newSerialVersionUID = -279985518429862552L;
    //
    // Serializable fields in old serial form
    private static final ObjectStreamField[] oldSerialPersistentFields = 
    {
      new ObjectStreamField("myName", String.class),
      new ObjectStreamField("myObjNameList", ArrayList.class)
    };
    //
    // Serializable fields in new serial form
    private static final ObjectStreamField[] newSerialPersistentFields = 
    {
      new ObjectStreamField("name", String.class),
      new ObjectStreamField("objectNameList", List.class)
    };
    //
    // Actual serial version and serial form
    private static final long serialVersionUID;
    /**
     * @serialField name String Role name
     * @serialField objectNameList List {@link List} of {@link ObjectName}s of referenced MBeans
     */
    private static final ObjectStreamField[] serialPersistentFields;
    private static boolean compat = false;  
    static {
	try {
	    PrivilegedAction act = new GetPropertyAction("jmx.serial.form");
	    String form = (String) AccessController.doPrivileged(act);
	    compat = (form != null && form.equals("1.0"));
	} catch (Exception e) {
	    // OK : Too bad, no compat with 1.0
	}
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

    //
    // Private members
    //

    /**
     * @serial Role name
     */
    private String name = null;

    /**
     * @serial {@link List} of {@link ObjectName}s of referenced MBeans
     */
    private List objectNameList = new ArrayList();

    //
    // Constructors
    //

    /**
     * <p>Make a new Role object.
     * No check is made that the ObjectNames in the role value exist in
     * an MBean server.  That check will be made when the role is set
     * in a relation.
     *
     * @param theRoleName  role name
     * @param theRoleValue  role value (List of ObjectName objects)
     *
     * @exception IllegalArgumentException  if null parameter
     */
    public Role(String theRoleName,
		List theRoleValue)
	throws IllegalArgumentException {

	if (theRoleName == null || theRoleValue == null) {
	    String excMsg = "Invalid parameter";
	    throw new IllegalArgumentException(excMsg);
	}

	setRoleName(theRoleName);
	setRoleValue(theRoleValue);

	return;
    }

    //
    // Accessors
    //

    /**
     * Retrieves role name.
     *
     * @return the role name.
     *
     * @see #setRoleName
     */
    public String getRoleName() {
	return name;
    }

    /**
     * Retrieves role value.
     *
     * @return ArrayList of ObjectName objects for referenced MBeans.
     *
     * @see #setRoleValue
     */
    public List getRoleValue() {
	return objectNameList;
    }

    /**
     * Sets role name.
     *
     * @param theRoleName  role name
     *
     * @exception IllegalArgumentException  if null parameter
     *
     * @see #getRoleName
     */
    public void setRoleName(String theRoleName)
	throws IllegalArgumentException {

	if (theRoleName == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	name = theRoleName;
	return;
    }

    /**
     * Sets role value.
     *
     * @param theRoleValue  List of ObjectName objects for referenced
     * MBeans.
     *
     * @exception IllegalArgumentException  if null parameter
     *
     * @see #getRoleValue
     */
    public void setRoleValue(List theRoleValue)
	throws IllegalArgumentException {

	if (theRoleValue == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	objectNameList = new ArrayList(theRoleValue);
	return;
    }

    /**
     * Returns a string describing the role.
     *
     * @return the description of the role.
     */
    public String toString() {
	StringBuffer result = new StringBuffer();
	result.append("role name: " + name + "; role value: ");
	for (Iterator objNameIter = objectNameList.iterator();
	     objNameIter.hasNext();) {
	    ObjectName currObjName = (ObjectName)(objNameIter.next());
	    result.append(currObjName.toString());
	    if (objNameIter.hasNext()) {
		result.append(", ");
	    }
	}
	return result.toString();
    }

    //
    // Misc
    //

    /**
     * Clone the role object.
     *
     * @return a Role that is an independent copy of the current Role object.
     */
    public Object clone() {

	try {
	    return new Role(name, objectNameList);
	} catch (IllegalArgumentException exc) {
	    return null; // can't happen
	}
    }

    /**
     * Returns a string for the given role value.
     *
     * @param theRoleValue  List of ObjectName objects
     *
     * @return A String consisting of the ObjectNames separated by
     * newlines (\n).
     *
     * @exception IllegalArgumentException  if null parameter
     */
    public static String roleValueToString(List theRoleValue)
	throws IllegalArgumentException {

	if (theRoleValue == null) {
	    String excMsg = "Invalid parameter";
	    throw new IllegalArgumentException(excMsg);
	}

	StringBuffer result = new StringBuffer();
	for (Iterator objNameIter = theRoleValue.iterator();
	     objNameIter.hasNext();) {
	    ObjectName currObjName = (ObjectName)(objNameIter.next());
	    result.append(currObjName.toString());
	    if (objNameIter.hasNext()) {
		result.append("\n");
	    }
	}
	return result.toString();
    }

    /**
     * Deserializes a {@link Role} from an {@link ObjectInputStream}.
     */
    private void readObject(ObjectInputStream in)
	    throws IOException, ClassNotFoundException {
      if (compat)
      {
        // Read an object serialized in the old serial form
        //
        ObjectInputStream.GetField fields = in.readFields();
	name = (String) fields.get("myName", null);
	if (fields.defaulted("myName"))
        {
          throw new NullPointerException("myName");
        }
	objectNameList = (List) fields.get("myObjNameList", null);
	if (fields.defaulted("myObjNameList"))
        {
          throw new NullPointerException("myObjNameList");
        }
      }
      else
      {
        // Read an object serialized in the new serial form
        //
        in.defaultReadObject();
      }
    }


    /**
     * Serializes a {@link Role} to an {@link ObjectOutputStream}.
     */
    private void writeObject(ObjectOutputStream out)
	    throws IOException {
      if (compat)
      {
        // Serializes this instance in the old serial form
        //
        ObjectOutputStream.PutField fields = out.putFields();
	fields.put("myName", name);
	fields.put("myObjNameList", (ArrayList)objectNameList);
	out.writeFields();
      }
      else
      {
        // Serializes this instance in the new serial form
        //
        out.defaultWriteObject();
      }
    }
}
