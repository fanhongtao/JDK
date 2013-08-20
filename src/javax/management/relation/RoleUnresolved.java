/*
 * @(#)RoleUnresolved.java	1.26 03/12/19
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
 * Represents an unresolved role: a role not retrieved from a relation due
 * to a problem. It provides the role name, value (if problem when trying to
 * set the role) and an integer defining the problem (constants defined in
 * RoleStatus).
 *
 * @since 1.5
 */
public class RoleUnresolved implements Serializable {

    // Serialization compatibility stuff:
    // Two serial forms are supported in this class. The selected form depends
    // on system property "jmx.serial.form":
    //  - "1.0" for JMX 1.0
    //  - any other value for JMX 1.1 and higher
    //
    // Serial version for old serial form 
    private static final long oldSerialVersionUID = -9026457686611660144L;
    //
    // Serial version for new serial form 
    private static final long newSerialVersionUID = -48350262537070138L;
    //
    // Serializable fields in old serial form
    private static final ObjectStreamField[] oldSerialPersistentFields = 
    {
      new ObjectStreamField("myRoleName", String.class),
      new ObjectStreamField("myRoleValue", ArrayList.class),
      new ObjectStreamField("myPbType", int.class)
    };
    //
    // Serializable fields in new serial form
    private static final ObjectStreamField[] newSerialPersistentFields = 
    {
      new ObjectStreamField("roleName", String.class),
      new ObjectStreamField("roleValue", List.class),
      new ObjectStreamField("problemType", int.class)
    };
    //
    // Actual serial version and serial form
    private static final long serialVersionUID;
    /** @serialField roleName String Role name 
     *  @serialField roleValue List Role value ({@link List} of {@link ObjectName} objects)
     *  @serialField problemType int Problem type
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
    private String roleName = null;

    /**
     * @serial Role value ({@link List} of {@link ObjectName} objects)
     */
    private List roleValue = null;

    /**
     * @serial Problem type
     */
    private int problemType;

    //
    // Constructor
    //

    /**
     * Constructor.
     *
     * @param theRoleName  name of the role
     * @param theRoleValue  value of the role (if problem when setting the
     * role)
     * @param thePbType  type of problem (according to known problem types,
     * listed as static final members).
     *
     * @exception IllegalArgumentException  if null parameter or incorrect
     * problem type
     */
    public RoleUnresolved(String theRoleName,
			  List theRoleValue,
			  int thePbType)
	throws IllegalArgumentException {

	if (theRoleName == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	setRoleName(theRoleName);
	setRoleValue(theRoleValue);
	// Can throw IllegalArgumentException
	setProblemType(thePbType);
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
	return roleName;
    }

    /**
     * Retrieves role value.
     *
     * @return an ArrayList of ObjectName objects, the one provided to be set
     * in given role. Null if the unresolved role is returned for a read
     * access.
     *
     * @see #setRoleValue
     */
    public List getRoleValue() {
	return roleValue;
    }

    /**
     * Retrieves problem type.
     *
     * @return an integer corresponding to a problem, those being described as
     * static final members of current class.
     *
     * @see #setProblemType
     */
    public int getProblemType() {
	return problemType;
    }

    /**
     * Sets role name.
     *
     * @param theRoleName the new role name.
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

	roleName = theRoleName;
	return;
    }

    /**
     * Sets role value.
     *
     * @param theRoleValue  List of ObjectName objects for referenced
     * MBeans not set in role.
     *
     * @see #getRoleValue
     */
    public void setRoleValue(List theRoleValue) {

	if (theRoleValue != null) {
	    roleValue = new ArrayList(theRoleValue);
	} else {
	    roleValue = null;
	}
	return;
    }

    /**
     * Sets problem type.
     *
     * @param thePbType  integer corresponding to a problem. Must be one of
     * those described as static final members of current class.
     *
     * @exception IllegalArgumentException  if incorrect problem type
     *
     * @see #getProblemType
     */
    public void setProblemType(int thePbType)
	throws IllegalArgumentException {

	if (!(RoleStatus.isRoleStatus(thePbType))) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Incorrect problem type.";
	    throw new IllegalArgumentException(excMsg);
	}
	problemType = thePbType;
	return;
    }

    /**
     * Clone this object.
     *
     * @return an independent clone.
     */
    public Object clone() {
	try {
	    return new RoleUnresolved(roleName, roleValue, problemType);
	} catch (IllegalArgumentException exc) {
	    return null; // :)
	}
    }

    /**
     * Return a string describing this object.
     *
     * @return a description of this RoleUnresolved object.
     */
    public String toString() {
	StringBuffer result = new StringBuffer();
	result.append("role name: " + roleName);
	if (roleValue != null) {
	    result.append("; value: ");
	    for (Iterator objNameIter = roleValue.iterator();
		 objNameIter.hasNext();) {
		ObjectName currObjName = (ObjectName)(objNameIter.next());
		result.append(currObjName.toString());
		if (objNameIter.hasNext()) {
		    result.append(", ");
		}
	    }
	}
	result.append("; problem type: " + problemType);
	return result.toString();
    }

    /**
     * Deserializes a {@link RoleUnresolved} from an {@link ObjectInputStream}.
     */
    private void readObject(ObjectInputStream in)
	    throws IOException, ClassNotFoundException {
      if (compat)
      {
        // Read an object serialized in the old serial form
        //
        ObjectInputStream.GetField fields = in.readFields();
	roleName = (String) fields.get("myRoleName", null);
	if (fields.defaulted("myRoleName"))
        {
          throw new NullPointerException("myRoleName");
        }
	roleValue = (List) fields.get("myRoleValue", null);
	if (fields.defaulted("myRoleValue"))
        {
          throw new NullPointerException("myRoleValue");
        }
	problemType = fields.get("myPbType", (int)0);
	if (fields.defaulted("myPbType"))
        {
          throw new NullPointerException("myPbType");
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
     * Serializes a {@link RoleUnresolved} to an {@link ObjectOutputStream}.
     */
    private void writeObject(ObjectOutputStream out)
	    throws IOException {
      if (compat)
      {
        // Serializes this instance in the old serial form
        //
        ObjectOutputStream.PutField fields = out.putFields();
	fields.put("myRoleName", roleName);
	fields.put("myRoleValue", (ArrayList)roleValue);
	fields.put("myPbType", problemType);
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
