/*
 * @(#)RelationTypeSupport.java	1.31 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management.relation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;

import java.security.AccessController;
import java.security.PrivilegedAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.trace.Trace;


/**
 * A RelationTypeSupport object implements the RelationType interface.
 * <P>It represents a relation type, providing role information for each role
 * expected to be supported in every relation of that type.
 * 
 * <P>A relation type includes a relation type name and a list of
 * role infos (represented by RoleInfo objects).
 *
 * <P>A relation type has to be declared in the Relation Service:
 * <P>- either using the createRelationType() method, where a RelationTypeSupport
 * object will be created and kept in the Relation Service
 * <P>- either using the addRelationType() method where the user has to create
 * an object implementing the RelationType interface, and this object will be
 * used as representing a relation type in the Relation Service.
 *
 * @since 1.5
 */
public class RelationTypeSupport implements RelationType {

    // Serialization compatibility stuff:
    // Two serial forms are supported in this class. The selected form depends
    // on system property "jmx.serial.form":
    //  - "1.0" for JMX 1.0
    //  - any other value for JMX 1.1 and higher
    //
    // Serial version for old serial form 
    private static final long oldSerialVersionUID = -8179019472410837190L;
    //
    // Serial version for new serial form 
    private static final long newSerialVersionUID = 4611072955724144607L;
    //
    // Serializable fields in old serial form
    private static final ObjectStreamField[] oldSerialPersistentFields = 
    {
      new ObjectStreamField("myTypeName", String.class),
      new ObjectStreamField("myRoleName2InfoMap", HashMap.class),
      new ObjectStreamField("myIsInRelServFlg", boolean.class)
    };
    //
    // Serializable fields in new serial form
    private static final ObjectStreamField[] newSerialPersistentFields = 
    {
      new ObjectStreamField("typeName", String.class),
      new ObjectStreamField("roleName2InfoMap", Map.class),
      new ObjectStreamField("isInRelationService", boolean.class)
    };
    //
    // Actual serial version and serial form
    private static final long serialVersionUID;
    /**
     * @serialField typeName String Relation type name
     * @serialField roleName2InfoMap Map {@link Map} holding the mapping: 
     *              &lt;role name ({@link String})&gt; -&gt; &lt;role info ({@link RoleInfo} object)&gt;
     * @serialField isInRelationService boolean Flag specifying whether the relation type has been declared in the
     *              Relation Service (so can no longer be updated)
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
     * @serial Relation type name
     */
    private String typeName = null;

    /**
     * @serial {@link Map} holding the mapping: 
     *           &lt;role name ({@link String})&gt; -&gt; &lt;role info ({@link RoleInfo} object)&gt;
     */
    private Map roleName2InfoMap = new HashMap();

    /**
     * @serial Flag specifying whether the relation type has been declared in the
     *         Relation Service (so can no longer be updated)
     */
    private boolean isInRelationService = false;

    //
    // Constructors
    //

    /**
     * Constructor where all role definitions are dynamically created and
     * passed as parameter.
     *
     * @param theRelTypeName  Name of relation type
     * @param theRoleInfoArray  List of role definitions (RoleInfo objects)
     *
     * @exception IllegalArgumentException  if null parameter
     * @exception InvalidRelationTypeException  if:
     * <P>- the same name has been used for two different roles
     * <P>- no role info provided
     * <P>- one null role info provided
     */
    public RelationTypeSupport(String theRelTypeName,
			    RoleInfo[] theRoleInfoArray)
	throws IllegalArgumentException,
	       InvalidRelationTypeException {

	if (theRelTypeName == null || theRoleInfoArray == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn())
	    trace("Constructor: entering", theRelTypeName);

	// Can throw InvalidRelationTypeException, ClassNotFoundException
	// and NotCompliantMBeanException
	initMembers(theRelTypeName, theRoleInfoArray);

	if (isTraceOn())
	    trace("Constructor: exiting", null);
	return;
    }

    /**
     * Constructor to be used for subclasses.
     *
     * @param theRelTypeName  Name of relation type.
     *
     * @exception IllegalArgumentException  if null parameter.
     */
    protected RelationTypeSupport(String theRelTypeName)
    {
	if (theRelTypeName == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn())
	    trace("Protected constructor: entering", theRelTypeName);

	typeName = theRelTypeName;

	if (isTraceOn())
	    trace("Protected constructor: exiting", null);
	return;
    }

    //
    // Accessors
    //

    /**
     * Returns the relation type name.
     *
     * @return the relation type name.
     */
    public String getRelationTypeName() {
	return typeName;
    }

    /**
     * Returns the list of role definitions (ArrayList of RoleInfo objects).
     */
    public List getRoleInfos() {
	return new ArrayList(roleName2InfoMap.values());
    }

    /**
     * Returns the role info (RoleInfo object) for the given role info name
     * (null if not found).
     *
     * @param theRoleInfoName  role info name
     *
     * @return RoleInfo object providing role definition
     * does not exist
     *
     * @exception IllegalArgumentException  if null parameter
     * @exception RoleInfoNotFoundException  if no role info with that name in
     * relation type.
     */
    public RoleInfo getRoleInfo(String theRoleInfoName)
	throws IllegalArgumentException,
	       RoleInfoNotFoundException {

	if (theRoleInfoName == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn())
	    trace("getRoleInfo: entering", theRoleInfoName);

	// No null RoleInfo allowed, so use get()
	RoleInfo result = (RoleInfo)(roleName2InfoMap.get(theRoleInfoName));

	if (result == null) {
	    StringBuffer excMsgStrB = new StringBuffer();
	    // Revisit [cebro] Localize message
	    String excMsg = "No role info for role ";
	    excMsgStrB.append(excMsg);
	    excMsgStrB.append(theRoleInfoName);
	    throw new RoleInfoNotFoundException(excMsgStrB.toString());
	}

	if (isTraceOn())
	    trace("getRoleInfo: exiting", null);
	return result;
    }

    //
    // Misc
    //

    /**
     * Add a role info.
     * This method of course should not be used after the creation of the
     * relation type, because updating it would invalidate that the relations
     * created associated to that type still conform to it.
     * Can throw a RuntimeException if trying to update a relation type
     * declared in the Relation Service.
     *
     * @param theRoleInfo  role info to be added.
     *
     * @exception IllegalArgumentException  if null parameter.
     * @exception InvalidRelationTypeException  if there is already a role
     *  info in current relation type with the same name.
     */
    protected void addRoleInfo(RoleInfo theRoleInfo)
	throws IllegalArgumentException,
	       InvalidRelationTypeException {

	if (theRoleInfo == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isDebugOn())
	    debug("addRoleInfo: entering", theRoleInfo.toString());

	if (isInRelationService) {
	    // Trying to update a declared relation type
	    // Revisit [cebro] Localize message
	    String excMsg = "Relation type cannot be updated as it is declared in the Relation Service.";
	    throw new RuntimeException(excMsg);
	}

	String roleName = theRoleInfo.getName();

	// Checks if the role info has already been described
	if (roleName2InfoMap.containsKey(roleName)) {
	    StringBuffer excMsgStrB = new StringBuffer();
	    // Revisit [cebro] Localize message
	    String excMsg = "Two role infos provided for role ";
	    excMsgStrB.append(excMsg);
	    excMsgStrB.append(roleName);
	    throw new InvalidRelationTypeException(excMsgStrB.toString());
	}

	roleName2InfoMap.put(roleName,
			       new RoleInfo(theRoleInfo));

	if (isDebugOn())
	    debug("addRoleInfo: exiting", null);
	return;
    }

    // Sets the internal flag to specify that the relation type has been
    // declared in the Relation Service
    void setRelationServiceFlag(boolean theFlg) {
	isInRelationService = theFlg;
	return;
    }

    // Initializes the members, i.e. type name and role info list.
    //
    // -param theRelTypeName  Name of relation type
    // -param theRoleInfoArray  List of role definitions (RoleInfo objects)
    //
    // -exception IllegalArgumentException  if null parameter
    // -exception InvalidRelationTypeException  If:
    //  - the same name has been used for two different roles
    //  - no role info provided
    //  - one null role info provided
    private void initMembers(String theRelTypeName,
			     RoleInfo[] theRoleInfoArray)
	throws IllegalArgumentException,
	       InvalidRelationTypeException {

	if (theRelTypeName == null || theRoleInfoArray == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isDebugOn())
	    debug("initMembers: entering", theRelTypeName);

	typeName = theRelTypeName;

	// Verifies role infos before setting them
	// Can throw InvalidRelationTypeException
	checkRoleInfos(theRoleInfoArray);

	for (int i = 0; i < theRoleInfoArray.length; i++) {
	    RoleInfo currRoleInfo = theRoleInfoArray[i];
	    roleName2InfoMap.put(new String(currRoleInfo.getName()),
				   new RoleInfo(currRoleInfo));
	}

	if (isDebugOn())
	    debug("initMembers: exiting", null);
	return;
    }

    // Checks the given RoleInfo array to verify that:
    // - the array is not empty
    // - it does not contain a null element
    // - a given role name is used only for one RoleInfo
    //
    // -param theRoleInfoArray  array to be checked
    //
    // -exception IllegalArgumentException
    // -exception InvalidRelationTypeException  If:
    //  - the same name has been used for two different roles
    //  - no role info provided
    //  - one null role info provided
    static void checkRoleInfos(RoleInfo[] theRoleInfoArray)
	throws IllegalArgumentException,
	       InvalidRelationTypeException {

	if (theRoleInfoArray == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (theRoleInfoArray.length == 0) {
	    // No role info provided
	    // Revisit [cebro] Localize message
	    String excMsg = "No role info provided.";
	    throw new InvalidRelationTypeException(excMsg);
	}


	ArrayList roleNameList = new ArrayList();

	for (int i = 0; i < theRoleInfoArray.length; i++) {
	    RoleInfo currRoleInfo = theRoleInfoArray[i];

	    if (currRoleInfo == null) {
		// Revisit [cebro] Localize message
		String excMsg = "Null role info provided.";
		throw new InvalidRelationTypeException(excMsg);
	    }

	    String roleName = currRoleInfo.getName();

	    // Checks if the role info has already been described
	    if (roleNameList.contains(roleName)) {
		StringBuffer excMsgStrB = new StringBuffer();
		// Revisit [cebro] Localize message
		String excMsg = "Two role infos provided for role ";
		excMsgStrB.append(excMsg);
		excMsgStrB.append(roleName);
		throw new InvalidRelationTypeException(excMsgStrB.toString());
	    }
	    roleNameList.add(roleName);
	}

	return;
    }

    // stuff for Tracing

    private static String localClassName = "RelationTypeSupport";

    // trace level
    private boolean isTraceOn() {
        return Trace.isSelected(Trace.LEVEL_TRACE, Trace.INFO_RELATION);
    }

//    private void trace(String className, String methodName, String info) {
//        Trace.send(Trace.LEVEL_TRACE, Trace.INFO_RELATION, className, methodName, info);
//    }

    private void trace(String methodName, String info) {
        Trace.send(Trace.LEVEL_TRACE, Trace.INFO_RELATION, localClassName, methodName, info);
	Trace.send(Trace.LEVEL_TRACE, Trace.INFO_RELATION, "", "", "\n");
    }

//    private void trace(String className, String methodName, Exception e) {
//        Trace.send(Trace.LEVEL_TRACE, Trace.INFO_RELATION, className, methodName, e);
//    }

//    private void trace(String methodName, Exception e) {
//        Trace.send(Trace.LEVEL_TRACE, Trace.INFO_RELATION, localClassName, methodName, e);
//    }

    // debug level
    private boolean isDebugOn() {
        return Trace.isSelected(Trace.LEVEL_DEBUG, Trace.INFO_RELATION);
    }

//    private void debug(String className, String methodName, String info) {
//        Trace.send(Trace.LEVEL_DEBUG, Trace.INFO_RELATION, className, methodName, info);
//    }

    private void debug(String methodName, String info) {
        Trace.send(Trace.LEVEL_DEBUG, Trace.INFO_RELATION, localClassName, methodName, info);
	Trace.send(Trace.LEVEL_DEBUG, Trace.INFO_RELATION, "", "", "\n");
    }

//    private void debug(String className, String methodName, Exception e) {
//        Trace.send(Trace.LEVEL_DEBUG, Trace.INFO_RELATION, className, methodName, e);
//    }

//    private void debug(String methodName, Exception e) {
//        Trace.send(Trace.LEVEL_DEBUG, Trace.INFO_RELATION, localClassName, methodName, e);
//    }

    /**
     * Deserializes a {@link RelationTypeSupport} from an {@link ObjectInputStream}.
     */
    private void readObject(ObjectInputStream in)
	    throws IOException, ClassNotFoundException {
      if (compat)
      {
        // Read an object serialized in the old serial form
        //
        ObjectInputStream.GetField fields = in.readFields();
	typeName = (String) fields.get("myTypeName", null);
	if (fields.defaulted("myTypeName"))
        {
          throw new NullPointerException("myTypeName");
        }
	roleName2InfoMap = (Map) fields.get("myRoleName2InfoMap", null);
	if (fields.defaulted("myRoleName2InfoMap"))
        {
          throw new NullPointerException("myRoleName2InfoMap");
        }
	isInRelationService = fields.get("myIsInRelServFlg", false);
	if (fields.defaulted("myIsInRelServFlg"))
        {
          throw new NullPointerException("myIsInRelServFlg");
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
     * Serializes a {@link RelationTypeSupport} to an {@link ObjectOutputStream}.
     */
    private void writeObject(ObjectOutputStream out)
	    throws IOException {
      if (compat)
      {
        // Serializes this instance in the old serial form
        //
        ObjectOutputStream.PutField fields = out.putFields();
	fields.put("myTypeName", typeName);
	fields.put("myRoleName2InfoMap", (HashMap)roleName2InfoMap);
	fields.put("myIsInRelServFlg", isInRelationService);
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
