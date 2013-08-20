/*
 * @(#)RelationNotification.java	1.31 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management.relation;

import javax.management.Notification;
import javax.management.ObjectName;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;

import java.security.AccessController;
import java.security.PrivilegedAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.jmx.mbeanserver.GetPropertyAction;

/**
 * A notification of a change in the Relation Service.
 * A RelationNotification notification is sent when a relation is created via
 * the Relation Service, or an MBean is added as a relation in the Relation
 * Service, or a role is updated in a relation, or a relation is removed from
 * the Relation Service.
 *
 * @since 1.5
 */
public class RelationNotification extends Notification {

    // Serialization compatibility stuff:
    // Two serial forms are supported in this class. The selected form depends
    // on system property "jmx.serial.form":
    //  - "1.0" for JMX 1.0
    //  - any other value for JMX 1.1 and higher
    //
    // Serial version for old serial form 
    private static final long oldSerialVersionUID = -2126464566505527147L;
    //
    // Serial version for new serial form 
    private static final long newSerialVersionUID = -6871117877523310399L;
    //
    // Serializable fields in old serial form
    private static final ObjectStreamField[] oldSerialPersistentFields = 
    {
	new ObjectStreamField("myNewRoleValue", ArrayList.class),
	new ObjectStreamField("myOldRoleValue", ArrayList.class),
	new ObjectStreamField("myRelId", String.class),
	new ObjectStreamField("myRelObjName", ObjectName.class),
	new ObjectStreamField("myRelTypeName", String.class),
	new ObjectStreamField("myRoleName", String.class),
	new ObjectStreamField("myUnregMBeanList", ArrayList.class)
    };
    //
    // Serializable fields in new serial form
    private static final ObjectStreamField[] newSerialPersistentFields = 
    {
	new ObjectStreamField("newRoleValue", List.class),
	new ObjectStreamField("oldRoleValue", List.class),
	new ObjectStreamField("relationId", String.class),
	new ObjectStreamField("relationObjName", ObjectName.class),
	new ObjectStreamField("relationTypeName", String.class),
	new ObjectStreamField("roleName", String.class),
	new ObjectStreamField("unregisterMBeanList", List.class)
    };
    //
    // Actual serial version and serial form
    private static final long serialVersionUID;
    /**
     * @serialField relationId String Relation identifier of created/removed/updated relation
     * @serialField relationTypeName String Relation type name of created/removed/updated relation
     * @serialField relationObjName ObjectName {@link ObjectName} of the relation MBean of created/removed/updated relation
     *              (only if the relation is represented by an MBean)
     * @serialField unregisterMBeanList List List of {@link ObjectName}s of referenced MBeans to be unregistered due to
     *              relation removal
     * @serialField roleName String Name of updated role (only for role update)
     * @serialField oldRoleValue List Old role value ({@link ArrayList} of {@link ObjectName}s) (only for role update)
     * @serialField newRoleValue List New role value ({@link ArrayList} of {@link ObjectName}s) (only for role update)
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
    // Notification types
    //

    /**
     * Type for the creation of an internal relation.
     */
    public static final String RELATION_BASIC_CREATION = "jmx.relation.creation.basic";
    /**
     * Type for the relation MBean added into the Relation Service.
     */
    public static final String RELATION_MBEAN_CREATION = "jmx.relation.creation.mbean";
    /**
     * Type for an update of an internal relation.
     */
    public static final String RELATION_BASIC_UPDATE = "jmx.relation.update.basic";
    /**
     * Type for the update of a relation MBean.
     */
    public static final String RELATION_MBEAN_UPDATE = "jmx.relation.update.mbean";
    /**
     * Type for the removal from the Relation Service of an internal relation.
     */
    public static final String RELATION_BASIC_REMOVAL = "jmx.relation.removal.basic";
    /**
     * Type for the removal from the Relation Service of a relation MBean.
     */
    public static final String RELATION_MBEAN_REMOVAL = "jmx.relation.removal.mbean";

    //
    // Private members
    //

    /**
     * @serial Relation identifier of created/removed/updated relation
     */
    private String relationId = null;

    /**
     * @serial Relation type name of created/removed/updated relation
     */
    private String relationTypeName = null;

    /**
     * @serial {@link ObjectName} of the relation MBean of created/removed/updated relation
     *         (only if the relation is represented by an MBean)
     */
    private ObjectName relationObjName = null;

    /**
     * @serial List of {@link ObjectName}s of referenced MBeans to be unregistered due to
     *         relation removal
     */
    private List unregisterMBeanList = null;

    /**
     * @serial Name of updated role (only for role update)
     */
    private String roleName = null;

    /** 
     * @serial Old role value ({@link ArrayList} of {@link ObjectName}s) (only for role update)
     */
    private List oldRoleValue = null;

    /**
     * @serial New role value ({@link ArrayList} of {@link ObjectName}s) (only for role update)
     */
    private List newRoleValue = null;

    //
    // Constructors
    //

    /**
     * Creates a notification for either a relation creation (RelationSupport
     * object created internally in the Relation Service, or an MBean added as a
     * relation) or for a relation removal from the Relation Service.
     *
     * @param theNtfType  type of the notification; either:
     * <P>- RELATION_BASIC_CREATION
     * <P>- RELATION_MBEAN_CREATION
     * <P>- RELATION_BASIC_REMOVAL
     * <P>- RELATION_MBEAN_REMOVAL
     * @param theSrcObj  source object, sending the notification. Will always
     * be a RelationService object.
     * @param TheSeqNbr  sequence number to identify the notification
     * @param theTimeStamp  time stamp
     * @param theMsg  human-readable message describing the notification
     * @param theRelId  relation id identifying the relation in the Relation
     * Service
     * @param theRelTypeName  name of the relation type
     * @param theRelObjName  ObjectName of the relation object if it is an MBean
     * (null for relations internally handled by the Relation Service)
     * @param theUnregMBeanList  list of ObjectNames of referenced MBeans
     * expected to be unregistered due to relation removal (only for removal,
     * due to CIM qualifiers, can be null)
     *
     * @exception IllegalArgumentException  if:
     * <P>- no value for the notification type
     * <P>- the notification type is not RELATION_BASIC_CREATION,
     * RELATION_MBEAN_CREATION, RELATION_BASIC_REMOVAL or
     * RELATION_MBEAN_REMOVAL
     * <P>- no source object
     * <P>- the source object is not a Relation Service
     * <P>- no relation id
     * <P>- no relation type name
     */
    public RelationNotification(String theNtfType,
				Object theSrcObj,
				long TheSeqNbr,
				long theTimeStamp,
				String theMsg,
				String theRelId,
				String theRelTypeName,
				ObjectName theRelObjName,
				List theUnregMBeanList)
	throws IllegalArgumentException {

	super(theNtfType, theSrcObj, TheSeqNbr, theTimeStamp, theMsg);

	// Can throw IllegalArgumentException
	initMembers(1,
		    theNtfType,
		    theSrcObj,
		    TheSeqNbr,
		    theTimeStamp,
		    theMsg,
		    theRelId,
		    theRelTypeName,
		    theRelObjName,
		    theUnregMBeanList,
		    null,
		    null,
		    null);
	return;
    }

    /**
     * Creates a notification for a role update in a relation.
     *
     * @param theNtfType  type of the notification; either:
     * <P>- RELATION_BASIC_UPDATE
     * <P>- RELATION_MBEAN_UPDATE
     * @param theSrcObj  source object, sending the notification. Will always
     * be a RelationService object.
     * @param TheSeqNbr  sequence number to identify the notification
     * @param theTimeStamp  time stamp
     * @param theMsg  human-readable message describing the notification
     * @param theRelId  relation id identifying the relation in the Relation
     * Service
     * @param theRelTypeName  name of the relation type
     * @param theRelObjName  ObjectName of the relation object if it is an MBean
     * (null for relations internally handled by the Relation Service)
     * @param theRoleName  name of the updated role
     * @param theNewRoleValue  new value (List of ObjectName objects)
     * @param theOldRoleValue  old value (List of ObjectName objects)
     *
     * @exception IllegalArgumentException  if null parameter
     */
    public RelationNotification(String theNtfType,
				Object theSrcObj,
				long TheSeqNbr,
				long theTimeStamp,
				String theMsg,
				String theRelId,
				String theRelTypeName,
				ObjectName theRelObjName,
				String theRoleName,
				List theNewRoleValue,
				List theOldRoleValue
				)
	throws IllegalArgumentException {

	super(theNtfType, theSrcObj, TheSeqNbr, theTimeStamp, theMsg);

	// Can throw IllegalArgumentException
	initMembers(2,
		    theNtfType,
		    theSrcObj,
		    TheSeqNbr,
		    theTimeStamp,
		    theMsg,
		    theRelId,
		    theRelTypeName,
		    theRelObjName,
		    null,
		    theRoleName,
		    theNewRoleValue,
		    theOldRoleValue);
	return;
    }

    //
    // Accessors
    //

    /**
     * Returns the relation identifier of created/removed/updated relation.
     *
     * @return the relation id.
     */
    public String getRelationId() {
	return relationId;
    }

    /**
     * Returns the relation type name of created/removed/updated relation.
     *
     * @return the relation type name.
     */
    public String getRelationTypeName() {
	return  relationTypeName;
    }

    /**
     * Returns the ObjectName of the
     * created/removed/updated relation.
     *
     * @return the ObjectName if the relation is an MBean, otherwise null.
     */
    public ObjectName getObjectName() {
	return relationObjName;
    }

    /**
     * Returns the list of ObjectNames of MBeans expected to be unregistered
     * due to a relation removal (only for relation removal).
     *
     * @return a {@link List} of {@link ObjectName}.
     */
    public List getMBeansToUnregister() {
	List result = null;
	if (unregisterMBeanList != null) {
	    result = (List)((ArrayList)unregisterMBeanList).clone();
	} else {
	    result = Collections.EMPTY_LIST;
	}
	return result;
    }

    /**
     * Returns name of updated role of updated relation (only for role update).
     *
     * @return the name of the updated role.
     */
    public String getRoleName() {
	String result = null;
	if (roleName != null) {
	    result = roleName;
	}
	return result;
    }

    /**
     * Returns old value of updated role (only for role update).
     *
     * @return the old value of the updated role.
     */
    public List getOldRoleValue() {
	List result = null;
	if (oldRoleValue != null) {
	    result = (List)((ArrayList)oldRoleValue).clone();
	} else {
	    result = Collections.EMPTY_LIST;
	}
	return result;
    }

    /**
     * Returns new value of updated role (only for role update).
     *
     * @return the new value of the updated role.
     */
    public List getNewRoleValue() {
	List result = null;
	if (newRoleValue != null) {
	    result = (List)((ArrayList)newRoleValue).clone();
	} else {
	    result = Collections.EMPTY_LIST;
	}
	return result;
    }

    //
    // Misc
    //

    // Initialises members
    //
    // -param theNtfKind  1 for creation/removal, 2 for update
    // -param theNtfType  type of the notification; either:
    //  - RELATION_BASIC_UPDATE
    //  - RELATION_MBEAN_UPDATE
    //  for an update, or:
    //  - RELATION_BASIC_CREATION
    //  - RELATION_MBEAN_CREATION
    //  - RELATION_BASIC_REMOVAL
    //  - RELATION_MBEAN_REMOVAL
    //  for a creation or removal
    // -param theSrcObj  source object, sending the notification. Will always
    //  be a RelationService object.
    // -param TheSeqNbr  sequence number to identify the notification
    // -param theTimeStamp  time stamp
    // -param theMsg  human-readable message describing the notification
    // -param theRelId  relation id identifying the relation in the Relation
    //  Service
    // -param theRelTypeName  name of the relation type
    // -param theRelObjName  ObjectName of the relation object if it is an MBean
    //  (null for relations internally handled by the Relation Service)
    // -param theUnregMBeanList  list of ObjectNames of MBeans expected to be
    //  removed due to relation removal
    // -param theRoleName  name of the updated role
    // -param theNewRoleValue  new value (List of ObjectName objects)
    // -param theOldRoleValue  old value (List of ObjectName objects)
    //
    // -exception IllegalArgumentException  if:
    //  - no value for the notification type
    //  - incorrect notification type
    //  - no source object
    //  - the source object is not a Relation Service
    //  - no relation id
    //  - no relation type name
    //  - no role name (for role update)
    //  - no role old value (for role update)
    //  - no role new value (for role update)
    private void initMembers(int theNtfKind,
			     String theNtfType,
			     Object theSrcObj,
			     long TheSeqNbr,
			     long theTimeStamp,
			     String theMsg,
			     String theRelId,
			     String theRelTypeName,
			     ObjectName theRelObjName,
			     List theUnregMBeanList,
			     String theRoleName,
			     List theNewRoleValue,
			     List theOldRoleValue)
	throws IllegalArgumentException {

      boolean badInitFlg = false;

      if (theNtfType == null ||	  
	  theSrcObj == null ||
	  (!(theSrcObj instanceof RelationService)) ||
	  theRelId == null ||
	  theRelTypeName == null) {

	  badInitFlg = true;
      }

      if (theNtfKind == 1) {

       if ((!(theNtfType.equals(RelationNotification.RELATION_BASIC_CREATION)))
	   &&
	   (!(theNtfType.equals(RelationNotification.RELATION_MBEAN_CREATION)))
	   &&
	   (!(theNtfType.equals(RelationNotification.RELATION_BASIC_REMOVAL)))
	   &&
	   (!(theNtfType.equals(RelationNotification.RELATION_MBEAN_REMOVAL)))
	   ) {

	      // Creation/removal
	      badInitFlg = true;
         }

       } else if (theNtfKind == 2) {

       if (((!(theNtfType.equals(RelationNotification.RELATION_BASIC_UPDATE)))
	    &&
	    (!(theNtfType.equals(RelationNotification.RELATION_MBEAN_UPDATE))))
	   || theRoleName == null ||
	   theOldRoleValue == null ||
	   theNewRoleValue == null) {

	   // Role update
	   badInitFlg = true;
       }
      }

    if (badInitFlg) {
	// Revisit [cebro] Localize message
	String excMsg = "Invalid parameter.";
	throw new IllegalArgumentException(excMsg);
    }

    relationId = theRelId;
    relationTypeName = theRelTypeName;
    relationObjName = theRelObjName;
    if (theUnregMBeanList != null) {
	unregisterMBeanList = new ArrayList(theUnregMBeanList);
    }
    if (theRoleName != null) {
	roleName = theRoleName;
    }
    if (theOldRoleValue != null) {
	oldRoleValue = new ArrayList(theOldRoleValue);
    }
    if (theNewRoleValue != null) {
	newRoleValue = new ArrayList(theNewRoleValue);
    }
    return;
    }

    /**
     * Deserializes a {@link RelationNotification} from an {@link ObjectInputStream}.
     */
    private void readObject(ObjectInputStream in)
	    throws IOException, ClassNotFoundException {
      if (compat)
      {
        // Read an object serialized in the old serial form
        //
        ObjectInputStream.GetField fields = in.readFields();
	newRoleValue = (List) fields.get("myNewRoleValue", null);
	if (fields.defaulted("myNewRoleValue"))
        {
          throw new NullPointerException("newRoleValue");
        }
	oldRoleValue = (List) fields.get("myOldRoleValue", null);
	if (fields.defaulted("myOldRoleValue"))
        {
          throw new NullPointerException("oldRoleValue");
        }
	relationId = (String) fields.get("myRelId", null);
	if (fields.defaulted("myRelId"))
        {
          throw new NullPointerException("relationId");
        }
	relationObjName = (ObjectName) fields.get("myRelObjName", null);
	if (fields.defaulted("myRelObjName"))
        {
          throw new NullPointerException("relationObjName");
        }
	relationTypeName = (String) fields.get("myRelTypeName", null);
	if (fields.defaulted("myRelTypeName"))
        {
          throw new NullPointerException("relationTypeName");
        }
	roleName = (String) fields.get("myRoleName", null);
	if (fields.defaulted("myRoleName"))
        {
          throw new NullPointerException("roleName");
        }
	unregisterMBeanList = (List) fields.get("myUnregMBeanList", null);
	if (fields.defaulted("myUnregMBeanList"))
        {
          throw new NullPointerException("unregisterMBeanList");
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
     * Serializes a {@link RelationNotification} to an {@link ObjectOutputStream}.
     */
    private void writeObject(ObjectOutputStream out)
	    throws IOException {
      if (compat)
      {
        // Serializes this instance in the old serial form
        //
        ObjectOutputStream.PutField fields = out.putFields();
	fields.put("myNewRoleValue", newRoleValue);
	fields.put("myOldRoleValue", oldRoleValue);
	fields.put("myRelId", relationId);
	fields.put("myRelObjName", relationObjName);
	fields.put("myRelTypeName", relationTypeName);
	fields.put("myRoleName",roleName);
	fields.put("myUnregMBeanList", unregisterMBeanList);
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
