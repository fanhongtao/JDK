/*
 * @(#)RoleInfo.java	1.34 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management.relation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.management.NotCompliantMBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.loading.ClassLoaderRepository;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Introspector;


/**
 * A RoleInfo object summarises a role in a relation type.
 *
 * @since 1.5
 */
public class RoleInfo implements Serializable {

    // Serialization compatibility stuff:
    // Two serial forms are supported in this class. The selected form depends
    // on system property "jmx.serial.form":
    //  - "1.0" for JMX 1.0
    //  - any other value for JMX 1.1 and higher
    //
    // Serial version for old serial form 
    private static final long oldSerialVersionUID = 7227256952085334351L;
    //
    // Serial version for new serial form 
    private static final long newSerialVersionUID = 2504952983494636987L;
    //
    // Serializable fields in old serial form
    private static final ObjectStreamField[] oldSerialPersistentFields = 
    {
      new ObjectStreamField("myName", String.class),
      new ObjectStreamField("myIsReadableFlg", boolean.class),
      new ObjectStreamField("myIsWritableFlg", boolean.class),
      new ObjectStreamField("myDescription", String.class),
      new ObjectStreamField("myMinDegree", int.class),
      new ObjectStreamField("myMaxDegree", int.class),
      new ObjectStreamField("myRefMBeanClassName", String.class)
    };
    //
    // Serializable fields in new serial form
    private static final ObjectStreamField[] newSerialPersistentFields = 
    {
      new ObjectStreamField("name", String.class),
      new ObjectStreamField("isReadable", boolean.class),
      new ObjectStreamField("isWritable", boolean.class),
      new ObjectStreamField("description", String.class),
      new ObjectStreamField("minDegree", int.class),
      new ObjectStreamField("maxDegree", int.class),
      new ObjectStreamField("referencedMBeanClassName", String.class)
    };
    //
    // Actual serial version and serial form
    private static final long serialVersionUID;
    /**
     * @serialField name String Role name
     * @serialField isReadable boolean Read access mode: <code>true</code> if role is readable
     * @serialField isWritable boolean Write access mode: <code>true</code> if role is writable
     * @serialField description String Role description
     * @serialField minDegree int Minimum degree (i.e. minimum number of referenced MBeans in corresponding role)
     * @serialField maxDegree int Maximum degree (i.e. maximum number of referenced MBeans in corresponding role)
     * @serialField referencedMBeanClassName String Name of class of MBean(s) expected to be referenced in corresponding role
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
    // Public constants
    //

    /**
     * To specify an unlimited cardinality.
     */
    public static int ROLE_CARDINALITY_INFINITY = -1;

    //
    // Private members
    //

    /**
     * @serial Role name
     */
    private String name = null;

    /**
     * @serial Read access mode: <code>true</code> if role is readable
     */
    private boolean isReadable;

    /**
     * @serial Write access mode: <code>true</code> if role is writable
     */
    private boolean isWritable;

    /**
     * @serial Role description
     */
    private String description = null;

    /**
     * @serial Minimum degree (i.e. minimum number of referenced MBeans in corresponding role)
     */
    private int minDegree;

    /**
     * @serial Maximum degree (i.e. maximum number of referenced MBeans in corresponding role)
     */
    private int maxDegree;

    /**
     * @serial Name of class of MBean(s) expected to be referenced in corresponding role
     */
    private String referencedMBeanClassName = null;

    //
    // Constructors
    //

    /**
     * Constructor.
     *
     * @param theName  name of the role.
     * @param theRefMBeanClassName  name of the class of MBean(s) expected to
     * be referenced in corresponding role.  If an MBean <em>M</em> is in
     * this role, then the MBean server must return true for
     * {@link MBeanServer#isInstanceOf isInstanceOf(M, theRefMBeanClassName)}.
     * @param theIsReadable  flag to indicate if the corresponding role
     * can be read
     * @param theIsWritable  flag to indicate if the corresponding role
     * can be set
     * @param theMinDegree  minimum degree for role, i.e. minimum number of
     * MBeans to provide in corresponding role
     * Must be less or equal than theMaxDegree.
     * (ROLE_CARDINALITY_INFINITY for unlimited)
     * @param theMaxDegree  maximum degree for role, i.e. maximum number of
     * MBeans to provide in corresponding role
     * Must be greater or equal than theMinDegree
     * (ROLE_CARDINALITY_INFINITY for unlimited)
     * @param theDescription  description of the role (can be null)
     *
     * @exception IllegalArgumentException  if null parameter
     * @exception InvalidRoleInfoException  if the minimum degree is
     * greater than the maximum degree.
     * @exception ClassNotFoundException As of JMX 1.2, this exception
     * can no longer be thrown.  It is retained in the declaration of
     * this class for compatibility with existing code.
     * @exception NotCompliantMBeanException  if the class theRefMBeanClassName
     * is not a MBean class.
     */
    public RoleInfo(String theName,
		    String theRefMBeanClassName,
		    boolean theIsReadable,
		    boolean theIsWritable,
		    int theMinDegree,
		    int theMaxDegree,
		    String theDescription)
    throws IllegalArgumentException,
	   InvalidRoleInfoException,
           ClassNotFoundException,
           NotCompliantMBeanException {

	init(theName,
	     theRefMBeanClassName,
	     theIsReadable,
	     theIsWritable,
	     theMinDegree,
	     theMaxDegree,
	     theDescription);
	return;
    }

    /**
     * Constructor.
     *
     * @param theName  name of the role
     * @param theRefMBeanClassName  name of the class of MBean(s) expected to
     * be referenced in corresponding role.  If an MBean <em>M</em> is in
     * this role, then the MBean server must return true for
     * {@link MBeanServer#isInstanceOf isInstanceOf(M, theRefMBeanClassName)}.
     * @param theIsReadable  flag to indicate if the corresponding role
     * can be read
     * @param theIsWritable  flag to indicate if the corresponding role
     * can be set
     *
     * <P>Minimum and maximum degrees defaulted to 1.
     * <P>Description of role defaulted to null.
     *
     * @exception IllegalArgumentException  if null parameter
     * @exception ClassNotFoundException As of JMX 1.2, this exception
     * can no longer be thrown.  It is retained in the declaration of
     * this class for compatibility with existing code.
     * @exception NotCompliantMBeanException As of JMX 1.2, this
     * exception can no longer be thrown.  It is retained in the
     * declaration of this class for compatibility with existing code.
     */
    public RoleInfo(String theName,
		    String theRefMBeanClassName,
		    boolean theIsReadable,
		    boolean theIsWritable)
    throws IllegalArgumentException,
	   ClassNotFoundException,
           NotCompliantMBeanException {

	try {
	    init(theName,
		 theRefMBeanClassName,
		 theIsReadable,
		 theIsWritable,
		 1,
		 1,
		 null);
	} catch (InvalidRoleInfoException exc) {
	    // OK : Can never happen as the minimum
	    //      degree equals the maximum degree.
	}

	return;
    }

    /**
     * Constructor.
     *
     * @param theName  name of the role
     * @param theRefMBeanClassName  name of the class of MBean(s) expected to
     * be referenced in corresponding role.  If an MBean <em>M</em> is in
     * this role, then the MBean server must return true for
     * {@link MBeanServer#isInstanceOf isInstanceOf(M, theRefMBeanClassName)}.
     *
     * <P>IsReadable and IsWritable defaulted to true.
     * <P>Minimum and maximum degrees defaulted to 1.
     * <P>Description of role defaulted to null.
     *
     * @exception IllegalArgumentException  if null parameter
     * @exception ClassNotFoundException As of JMX 1.2, this exception
     * can no longer be thrown.  It is retained in the declaration of
     * this class for compatibility with existing code.
     * @exception NotCompliantMBeanException As of JMX 1.2, this
     * exception can no longer be thrown.  It is retained in the
     * declaration of this class for compatibility with existing code.
      */
    public RoleInfo(String theName,
		    String theRefMBeanClassName)
    throws IllegalArgumentException,
	   ClassNotFoundException,
           NotCompliantMBeanException {

	try {
	    init(theName,
		 theRefMBeanClassName,
		 true,
		 true,
		 1,
		 1,
		 null);
	} catch (InvalidRoleInfoException exc) {
	    // OK : Can never happen as the minimum
	    //      degree equals the maximum degree.
	}

	return;
    }

    /**
     * Copy constructor.
     *
     * @param theRoleInfo the RoleInfo to be copied.
     *
     * @exception IllegalArgumentException  if null parameter
     */
    public RoleInfo(RoleInfo theRoleInfo)
	throws IllegalArgumentException {

	if (theRoleInfo == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	try {
	    init(theRoleInfo.getName(),
		 theRoleInfo.getRefMBeanClassName(),
		 theRoleInfo.isReadable(),
		 theRoleInfo.isWritable(),
		 theRoleInfo.getMinDegree(),
		 theRoleInfo.getMaxDegree(),
		 theRoleInfo.getDescription());
	} catch (InvalidRoleInfoException exc3) {
	    // OK : Can never happen as the minimum degree and the maximum
	    //      degree were already checked at the time the theRoleInfo
	    //      instance was created.
	}
    }

    //
    // Accessors
    //

    /**
     * Returns the name of the role.
     *
     * @return the name of the role.
     */
    public String getName() {
	return name;
    }

    /**
     * Returns read access mode for the role (true if it is readable).
     *
     * @return true if the role is readable.
     */
    public boolean isReadable() {
	return isReadable;
    }

    /**
     * Returns write access mode for the role (true if it is writable).
     *
     * @return true if the role is writable.
     */
    public boolean isWritable() {
	return isWritable;
    }

    /**
     * Returns description text for the role.
     *
     * @return the description of the role.
     */
    public String getDescription() {
	return description;
    }

    /**
     * Returns minimum degree for corresponding role reference.
     *
     * @return the minimum degree.
     */
    public int getMinDegree() {
	return minDegree;
    }

    /**
     * Returns maximum degree for corresponding role reference.
     *
     * @return the maximum degree.
     */
    public int getMaxDegree() {
	return maxDegree;
    }

    /**
     * <p>Returns name of type of MBean expected to be referenced in
     * corresponding role.</p>
     *
     * @return the name of the referenced type.
     */
    public String getRefMBeanClassName() {
	return referencedMBeanClassName;
    }

    /**
     * Returns a boolean to specify if given value is greater or equal than
     * expected minimum degree (true if yes).
     *
     * @param theValue  value
     *
     * @return true if greater or equal than minimum degree, false otherwise.
     */
    public boolean checkMinDegree(int theValue) {
	if (theValue >= ROLE_CARDINALITY_INFINITY &&
	    (minDegree == ROLE_CARDINALITY_INFINITY
	     || theValue >= minDegree)) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Returns a boolean to specify if given value is less or equal than
     * expected maximum degree (true if yes).
     *
     * @param theValue  value
     *
     * @return true if less or equal than maximum degree, false otherwise.
     */
    public boolean checkMaxDegree(int theValue) {
	if (theValue >= ROLE_CARDINALITY_INFINITY &&
	    (maxDegree == ROLE_CARDINALITY_INFINITY ||
	     (theValue != ROLE_CARDINALITY_INFINITY &&
	      theValue <= maxDegree))) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Returns a string describing the role info.
     *
     * @return a description of the role info.
     */
    public String toString() {
	StringBuffer result = new StringBuffer();
	result.append("role info name: " + name);
	result.append("; isReadable: " + isReadable);
	result.append("; isWritable: " + isWritable);
	result.append("; description: " + description);
	result.append("; minimum degree: " + minDegree);
	result.append("; maximum degree: " + maxDegree);
	result.append("; MBean class: " + referencedMBeanClassName);
	return result.toString();
    }

    //
    // Misc
    //

    // Initialisation
    private void init(String theName,
		      String theRefMBeanClassName,
		      boolean theIsReadable,
		      boolean theIsWritable,
		      int theMinDegree,
		      int theMaxDegree,
		      String theDescription)
	    throws IllegalArgumentException,
		   InvalidRoleInfoException {

	if (theName == null ||
	    theRefMBeanClassName == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	name = theName;
	isReadable = theIsReadable;
	isWritable = theIsWritable;
	if (theDescription != null) {
	    description = theDescription;
	}

	boolean invalidRoleInfoFlg = false;
	StringBuffer excMsgStrB = new StringBuffer();
	if (theMaxDegree != ROLE_CARDINALITY_INFINITY &&
	    (theMinDegree == ROLE_CARDINALITY_INFINITY ||
	     theMinDegree > theMaxDegree)) {
	    // Revisit [cebro] Localize message
	    excMsgStrB.append("Minimum degree ");
	    excMsgStrB.append(theMinDegree);
	    excMsgStrB.append(" is greater than maximum degree ");
	    excMsgStrB.append(theMaxDegree);
	    invalidRoleInfoFlg = true;

	} else if (theMinDegree < ROLE_CARDINALITY_INFINITY ||
		   theMaxDegree < ROLE_CARDINALITY_INFINITY) {
	    // Revisit [cebro] Localize message
	    excMsgStrB.append("Minimum or maximum degree has an illegal value, must be [0, ROLE_CARDINALITY_INFINITY].");
	    invalidRoleInfoFlg = true;
	}
	if (invalidRoleInfoFlg) {
	    throw new InvalidRoleInfoException(excMsgStrB.toString());
	}
	minDegree = theMinDegree;
	maxDegree = theMaxDegree;

        referencedMBeanClassName = theRefMBeanClassName;

	return;
    }    

    /**
     * Deserializes a {@link RoleInfo} from an {@link ObjectInputStream}.
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
	isReadable = fields.get("myIsReadableFlg", false);
	if (fields.defaulted("myIsReadableFlg"))
        {
          throw new NullPointerException("myIsReadableFlg");
        }
	isWritable = fields.get("myIsWritableFlg", false);
	if (fields.defaulted("myIsWritableFlg"))
        {
          throw new NullPointerException("myIsWritableFlg");
        }
	description = (String) fields.get("myDescription", null);
	if (fields.defaulted("myDescription"))
        {
          throw new NullPointerException("myDescription");
        }
	minDegree = fields.get("myMinDegree", (int)0);
	if (fields.defaulted("myMinDegree"))
        {
          throw new NullPointerException("myMinDegree");
        }
	maxDegree = fields.get("myMaxDegree", (int)0);
	if (fields.defaulted("myMaxDegree"))
        {
          throw new NullPointerException("myMaxDegree");
        }
	referencedMBeanClassName = (String) fields.get("myRefMBeanClassName", null);
	if (fields.defaulted("myRefMBeanClassName"))
        {
          throw new NullPointerException("myRefMBeanClassName");
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
     * Serializes a {@link RoleInfo} to an {@link ObjectOutputStream}.
     */
    private void writeObject(ObjectOutputStream out)
	    throws IOException {
      if (compat)
      {
        // Serializes this instance in the old serial form
        //
        ObjectOutputStream.PutField fields = out.putFields();
	fields.put("myName", name);
	fields.put("myIsReadableFlg", isReadable);
	fields.put("myIsWritableFlg", isWritable);
	fields.put("myDescription", description);
	fields.put("myMinDegree", minDegree);
	fields.put("myMaxDegree", maxDegree);
	fields.put("myRefMBeanClassName", referencedMBeanClassName);
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
