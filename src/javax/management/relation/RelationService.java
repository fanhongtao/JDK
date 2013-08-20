/*
 * @(#)RelationService.java	1.42 04/04/13
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management.relation;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ListenerNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.MBeanServerNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.MalformedObjectNameException;

import com.sun.jmx.defaults.ServiceName;

import com.sun.jmx.trace.Trace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.Date;

/**
 * The Relation Service is in charge of creating and deleting relation types
 * and relations, of handling the consistency and of providing query
 * mechanisms.
 * <P>It implements the NotificationBroadcaster by extending
 * NotificationBroadcasterSupport to send notifications when a relation is
 * removed from it.
 * <P>It implements the NotificationListener interface to be able to receive
 * notifications concerning unregistration of MBeans referenced in relation
 * roles and of relation MBeans.
 * <P>It implements the MBeanRegistration interface to be able to retrieve
 * its ObjectName and MBean Server.
 *
 * @since 1.5
 */
public class RelationService extends NotificationBroadcasterSupport
    implements RelationServiceMBean, MBeanRegistration, NotificationListener {

    //
    // Private members
    //

    // Map associating:
    //      <relation id> -> <RelationSupport object/ObjectName>
    // depending if the relation has been created using createRelation()
    // method (so internally handled) or is an MBean added as a relation by the
    // user
    private HashMap myRelId2ObjMap = new HashMap();

    // Map associating:
    //      <relation id> -> <relation type name>
    private HashMap myRelId2RelTypeMap = new HashMap();

    // Map associating:
    //      <relation MBean Object Name> -> <relation id>
    private HashMap myRelMBeanObjName2RelIdMap = new HashMap();

    // Map associating:
    //       <relation type name> -> <RelationType object>
    private HashMap myRelType2ObjMap = new HashMap();

    // Map associating:
    //       <relation type name> -> ArrayList of <relation id>
    // to list all the relations of a given type
    private HashMap myRelType2RelIdsMap = new HashMap();

    // Map associating:
    //       <ObjectName> -> HashMap
    // the value HashMap mapping:
    //       <relation id> -> ArrayList of <role name>
    // to track where a given MBean is referenced.
    private HashMap myRefedMBeanObjName2RelIdsMap = new HashMap();

    // Flag to indicate if, when a notification is received for the
    // unregistration of an MBean referenced in a relation, if an immediate
    // "purge" of the relations (look for the relations no
    // longer valid) has to be performed , or if that will be performed only
    // when the purgeRelations method will be explicitly called.
    // true is immediate purge.
    private boolean myPurgeFlg = true;

    // Internal counter to provide sequence numbers for notifications sent by:
    // - the Relation Service
    // - a relation handled by the Relation Service
    private Long myNtfSeqNbrCounter = new Long(0);

    // ObjectName used to register the Relation Service in the MBean Server
    private ObjectName myObjName = null;

    // MBean Server where the Relation Service is registered
    private MBeanServer myMBeanServer = null;

    // Filter registered in the MBean Server with the Relation Service to be
    // informed of referenced MBean unregistrations
    private MBeanServerNotificationFilter myUnregNtfFilter = null;

    // List of unregistration notifications received (storage used if purge
    // of relations when unregistering a referenced MBean is not immediate but
    // on user request)
    private ArrayList myUnregNtfList = new ArrayList();

    //
    // Constructor
    //

    /**
     * Constructor.
     *
     * @param theImmediatePurgeFlg  flag to indicate when a notification is
     * received for the unregistration of an MBean referenced in a relation, if
     * an immediate "purge" of the relations (look for the relations no
     * longer valid) has to be performed , or if that will be performed only
     * when the purgeRelations method will be explicitly called.
     * <P>true is immediate purge.
     */
    public RelationService(boolean theImmediatePurgeFlg) {

	if (isTraceOn())
	    trace("Constructor: entering", null);

	setPurgeFlag(theImmediatePurgeFlg);

	if (isTraceOn())
	    trace("Constructor: exiting", null);
	return;
    }

    /**
     * Checks if the Relation Service is active.
     * Current condition is that the Relation Service must be registered in the
     * MBean Server
     *
     * @exception RelationServiceNotRegisteredException  if it is not
     * registered
     */
    public void isActive()
	throws RelationServiceNotRegisteredException {
	if (myMBeanServer == null) {
	    // MBean Server not set by preRegister(): relation service not
	    // registered
	    // Revisit [cebro] Localize message
	    String excMsg =
		"Relation Service not registered in the MBean Server.";
	    throw new RelationServiceNotRegisteredException(excMsg);
	}
	return;
    }

    //
    // MBeanRegistration interface
    //

    // Pre-registration: retrieves its ObjectName and MBean Server
    //
    // No exception thrown.
    public ObjectName preRegister(MBeanServer server,
				  ObjectName name)
	throws Exception {

	myMBeanServer = server;
	myObjName = name;
	return name;
    }

    // Post-registration: does nothing
    public void postRegister(Boolean registrationDone) {
	return;
    }

    // Pre-unregistration: does nothing
    public void preDeregister()
	throws Exception {
	return;
    }

    // Post-unregistration: does nothing
    public void postDeregister() {
	return;
    }

    //
    // Accessors
    //

    /**
     * Returns the flag to indicate if when a notification is received for the
     * unregistration of an MBean referenced in a relation, if an immediate
     * "purge" of the relations (look for the relations no longer valid)
     * has to be performed , or if that will be performed only when the
     * purgeRelations method will be explicitly called.
     * <P>true is immediate purge.
     *
     * @return true if purges are automatic.
     *
     * @see #setPurgeFlag
     */
    public boolean getPurgeFlag() {
	return myPurgeFlg;
    }

    /**
     * Sets the flag to indicate if when a notification is received for the
     * unregistration of an MBean referenced in a relation, if an immediate
     * "purge" of the relations (look for the relations no longer valid)
     * has to be performed , or if that will be performed only when the
     * purgeRelations method will be explicitly called.
     * <P>true is immediate purge.
     *
     * @param thePurgeFlg  flag
     *
     * @see #getPurgeFlag
     */
    public void setPurgeFlag(boolean thePurgeFlg) {

	myPurgeFlg = thePurgeFlg;
	return;
    }

    // Returns internal counter to be used for Sequence Numbers of
    // notifications to be raised by:
    // - a relation handled by this Relation Service (when updated)
    // - the Relation Service
    private Long getNotificationSequenceNumber() {
	Long result = null;
	synchronized(myNtfSeqNbrCounter) {
	    result = new Long(myNtfSeqNbrCounter.longValue() + 1);
	    myNtfSeqNbrCounter = new Long(result.longValue());
	}
	return result;
    }

    //
    // Relation type handling
    //

    /**
     * Creates a relation type (a RelationTypeSupport object) with given
     * role infos (provided by the RoleInfo objects), and adds it in the
     * Relation Service.
     *
     * @param theRelTypeName  name of the relation type
     * @param theRoleInfoArray  array of role infos
     *
     * @exception IllegalArgumentException  if null parameter
     * @exception InvalidRelationTypeException  If:
     * <P>- there is already a relation type with that name
     * <P>- the same name has been used for two different role infos
     * <P>- no role info provided
     * <P>- one null role info provided
     */
    public void createRelationType(String theRelTypeName,
				   RoleInfo[] theRoleInfoArray)
	throws IllegalArgumentException,
	       InvalidRelationTypeException {

	if (theRelTypeName == null || theRoleInfoArray == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn())
	    trace("createRelationType: entering", theRelTypeName);

	// Can throw an InvalidRelationTypeException
	RelationType relType =
	    new RelationTypeSupport(theRelTypeName, theRoleInfoArray);

	addRelationTypeInt(relType);

	if (isTraceOn())
	    trace("createRelationType: exiting", null);
	return;	    
    }

    /**
     * Adds given object as a relation type. The object is expected to
     * implement the RelationType interface.
     *
     * @param theRelTypeObj  relation type object (implementing the
     * RelationType interface)
     *
     * @exception IllegalArgumentException  if null parameter
     * @exception InvalidRelationTypeException  if:
     * <P>- the same name has been used for two different roles
     * <P>- no role info provided
     * <P>- one null role info provided
     * <P>- there is already a relation type with that name
     */
    public void addRelationType(RelationType theRelTypeObj)
	throws IllegalArgumentException,
	       InvalidRelationTypeException {

	if (theRelTypeObj == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn())
	    trace("addRelationType: entering", null);

	// Checks the role infos
	List roleInfoList = theRelTypeObj.getRoleInfos();
	if (roleInfoList == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "No role info provided.";
	    throw new InvalidRelationTypeException(excMsg);
	}

	RoleInfo[] roleInfoArray = new RoleInfo[roleInfoList.size()];
	int i = 0;
	for (Iterator roleInfoIter = roleInfoList.iterator();
	     roleInfoIter.hasNext();) {
	    RoleInfo currRoleInfo = (RoleInfo)(roleInfoIter.next());
	    roleInfoArray[i] = currRoleInfo;
	    i++;
	}
	// Can throw InvalidRelationTypeException
	RelationTypeSupport.checkRoleInfos(roleInfoArray);

	addRelationTypeInt(theRelTypeObj);

	if (isTraceOn())
	    trace("addRelationType: exiting", null);
	return;
     }

    /**
     * Retrieves names of all known relation types.
     *
     * @return ArrayList of relation type names (Strings)
     */
    public List getAllRelationTypeNames() {
	ArrayList result = null;
	synchronized(myRelType2ObjMap) {
	    result = new ArrayList(myRelType2ObjMap.keySet());
	}
	return result;
    }

    /**
     * Retrieves list of role infos (RoleInfo objects) of a given relation
     * type.
     *
     * @param theRelTypeName  name of relation type
     *
     * @return ArrayList of RoleInfo.
     *
     * @exception IllegalArgumentException  if null parameter
     * @exception RelationTypeNotFoundException  if there is no relation type
     * with that name.
     */
    public List getRoleInfos(String theRelTypeName)
	throws IllegalArgumentException,
	       RelationTypeNotFoundException {

	if (theRelTypeName == null) {
	    // Revisit [cebro[ Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn())
	    trace("getRoleInfos: entering", theRelTypeName);

	// Can throw a RelationTypeNotFoundException
	RelationType relType = getRelationType(theRelTypeName);

	if (isTraceOn())
	    trace("getRoleInfos: exiting", null);
	return relType.getRoleInfos();
    }

    /**
     * Retrieves role info for given role name of a given relation type.
     *
     * @param theRelTypeName  name of relation type
     * @param theRoleInfoName  name of role
     *
     * @return RoleInfo object.
     *
     * @exception IllegalArgumentException  if null parameter
     * @exception RelationTypeNotFoundException  if the relation type is not
     * known in the Relation Service
     * @exception RoleInfoNotFoundException  if the role is not part of the
     * relation type.
     */
    public RoleInfo getRoleInfo(String theRelTypeName,
				String theRoleInfoName)
	throws IllegalArgumentException,
	       RelationTypeNotFoundException,
               RoleInfoNotFoundException {

	if (theRelTypeName == null || theRoleInfoName == null) {
	    // Revisit [cebro[ Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn()) {
	    String str = "theRelTypeName " + theRelTypeName
				    + ", theRoleInfoName " + theRoleInfoName;
	    trace("getRoleInfo: entering", str);
	}	    

	// Can throw a RelationTypeNotFoundException
	RelationType relType = getRelationType(theRelTypeName);

	// Can throw a RoleInfoNotFoundException
	RoleInfo roleInfo = relType.getRoleInfo(theRoleInfoName);

	if (isTraceOn())
	    trace("getRoleInfo: exiting", null);
	return roleInfo;
    }

    /**
     * Removes given relation type from Relation Service.
     * <P>The relation objects of that type will be removed from the
     * Relation Service.
     *
     * @param theRelTypeName  name of the relation type to be removed
     *
     * @exception RelationServiceNotRegisteredException  if the Relation
     * Service is not registered in the MBean Server
     * @exception IllegalArgumentException  if null parameter
     * @exception RelationTypeNotFoundException  If there is no relation type
     * with that name
     */
    public void removeRelationType(String theRelTypeName)
	throws RelationServiceNotRegisteredException,
	       IllegalArgumentException,
	       RelationTypeNotFoundException {

	// Can throw RelationServiceNotRegisteredException
	isActive();

	if (theRelTypeName == null) {
	    // Revisit [cebro[ Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn())
	    trace("removeRelationType: entering", theRelTypeName);

	// Checks if the relation type to be removed exists
	// Can throw a RelationTypeNotFoundException
	RelationType relType = getRelationType(theRelTypeName);

	// Retrieves the relation ids for relations of that type
	ArrayList relIdList = null;
	synchronized(myRelType2RelIdsMap) {
	    // Note: take a copy of the list as it is a part of a map that
	    //       will be updated by removeRelation() below.
	    ArrayList relIdList1 = (ArrayList)
		(myRelType2RelIdsMap.get(theRelTypeName));
	    if (relIdList1 != null) {
		relIdList = (ArrayList)(relIdList1.clone());
	    }
	}

	// Removes the relation type from all maps
	synchronized(myRelType2ObjMap) {
	    myRelType2ObjMap.remove(theRelTypeName);
	}
	synchronized(myRelType2RelIdsMap) {
	    myRelType2RelIdsMap.remove(theRelTypeName);
	}

	// Removes all relations of that type
	if (relIdList != null) {
	    for (Iterator relIdIter = relIdList.iterator();
		 relIdIter.hasNext();) {
		String currRelId = (String)(relIdIter.next());
		// Note: will remove it from myRelId2RelTypeMap :)
		//
		// Can throw RelationServiceNotRegisteredException (detected
		// above)
		// Shall not throw a RelationNotFoundException
		try {
		    removeRelation(currRelId);
		} catch (RelationNotFoundException exc1) {
		    throw new RuntimeException(exc1.getMessage());
		}
	    }
	}

	if (isTraceOn())
	    trace("removeRelationType: exiting", null);
	return;	    
    }

    //
    // Relation handling
    //

    /**
     * Creates a simple relation (represented by a RelationSupport object) of
     * given relation type, and adds it in the Relation Service.
     * <P>Roles are initialized according to the role list provided in
     * parameter. The ones not initialized in this way are set to an empty
     * ArrayList of ObjectNames.
     * <P>A RelationNotification, with type RELATION_BASIC_CREATION, is sent.
     *
     * @param theRelId  relation identifier, to identify uniquely the relation
     * inside the Relation Service
     * @param theRelTypeName  name of the relation type (has to be created
     * in the Relation Service)
     * @param theRoleList  role list to initialize roles of the relation (can
     * be null).
     *
     * @exception RelationServiceNotRegisteredException  if the Relation
     * Service is not registered in the MBean Server
     * @exception IllegalArgumentException  if null parameter, except the role
     * list which can be null if no role initialization
     * @exception RoleNotFoundException  if a value is provided for a role
     * that does not exist in the relation type
     * @exception InvalidRelationIdException  if relation id already used
     * @exception RelationTypeNotFoundException  if relation type not known in
     * Relation Service
     * @exception InvalidRoleValueException if:
     * <P>- the same role name is used for two different roles
     * <P>- the number of referenced MBeans in given value is less than
     * expected minimum degree
     * <P>- the number of referenced MBeans in provided value exceeds expected
     * maximum degree
     * <P>- one referenced MBean in the value is not an Object of the MBean
     * class expected for that role
     * <P>- an MBean provided for that role does not exist
     */
    public void createRelation(String theRelId,
			       String theRelTypeName,
			       RoleList theRoleList)
	throws RelationServiceNotRegisteredException,
	       IllegalArgumentException,
               RoleNotFoundException,
               InvalidRelationIdException,
               RelationTypeNotFoundException,
               InvalidRoleValueException {

	// Can throw RelationServiceNotRegisteredException
	isActive();

	if (theRelId == null ||
	    theRelTypeName == null) {
	    // Revisit [cebro[ Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn()) {
	    StringBuffer strB =
		new StringBuffer("theRelId " + theRelId
				 + ", theRelTypeName " + theRelTypeName);
	    if (theRoleList != null) {
		strB.append(", theRoleList " + theRoleList.toString());
	    }
	    trace("createRelation: entering", strB.toString());
	}

	// Creates RelationSupport object
	// Can throw InvalidRoleValueException
	RelationSupport relObj = new RelationSupport(theRelId,
					       myObjName,
					       theRelTypeName,
					       theRoleList);

	// Adds relation object as a relation into the Relation Service
	// Can throw RoleNotFoundException, InvalidRelationId,
	// RelationTypeNotFoundException, InvalidRoleValueException
	//
	// Cannot throw MBeanException
	addRelationInt(true,
		       relObj,
		       null,
		       theRelId,
		       theRelTypeName,
		       theRoleList);

	if (isTraceOn())
	    trace("createRelation: exiting", null);
	return;		       
    }

    /**
     * Adds an MBean created by the user (and registered by him in the MBean
     * Server) as a relation in the Relation Service.
     * <P>To be added as a relation, the MBean must conform to the
     * following:
     * <P>- implement the Relation interface
     * <P>- have for RelationService ObjectName the ObjectName of current
     * Relation Service
     * <P>- have a relation id unique and unused in current Relation Service
     * <P>- have for relation type a relation type created in the Relation
     * Service
     * <P>- have roles conforming to the role info provided in the relation
     * type.
     *
     * @param theRelObjectName  ObjectName of the relation MBean to be added.
     *
     * @exception IllegalArgumentException  if null parameter
     * @exception RelationServiceNotRegisteredException  if the Relation
     * Service is not registered in the MBean Server
     * @exception NoSuchMethodException  If the MBean does not implement the
     * Relation interface
     * @exception InvalidRelationIdException  if:
     * <P>- no relation identifier in MBean
     * <P>- the relation identifier is already used in the Relation Service
     * @exception InstanceNotFoundException  if the MBean for given ObjectName
     * has not been registered
     * @exception InvalidRelationServiceException  if:
     * <P>- no Relation Service name in MBean
     * <P>- the Relation Service name in the MBean is not the one of the
     * current Relation Service
     * @exception RelationTypeNotFoundException  if:
     * <P>- no relation type name in MBean
     * <P>- the relation type name in MBean does not correspond to a relation
     * type created in the Relation Service
     * @exception InvalidRoleValueException  if:
     * <P>- the number of referenced MBeans in a role is less than
     * expected minimum degree
     * <P>- the number of referenced MBeans in a role exceeds expected
     * maximum degree
     * <P>- one referenced MBean in the value is not an Object of the MBean
     * class expected for that role
     * <P>- an MBean provided for a role does not exist
     * @exception RoleNotFoundException  if a value is provided for a role
     * that does not exist in the relation type
     */
    public void addRelation(ObjectName theRelObjectName)
	throws IllegalArgumentException,
	       RelationServiceNotRegisteredException,
	       NoSuchMethodException,
	       InvalidRelationIdException,
	       InstanceNotFoundException,
	       InvalidRelationServiceException,
               RelationTypeNotFoundException,
               RoleNotFoundException,
	       InvalidRoleValueException {

	if (theRelObjectName == null) {
	    // Revisit [cebro[ Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn())
	    trace("addRelation: entering", theRelObjectName.toString());

	// Can throw RelationServiceNotRegisteredException
	isActive();

	// Checks that the relation MBean implements the Relation interface.
	// It will also check that the provided ObjectName corresponds to a
	// registered MBean (else will throw an InstanceNotFoundException)
	if ((!(myMBeanServer.isInstanceOf(theRelObjectName, "javax.management.relation.Relation")))) {
	    // Revisit [cebro] Localize message
	    String excMsg = "This MBean does not implement the Relation interface.";
	    throw new NoSuchMethodException(excMsg);
	}
	// Checks there is a relation id in the relation MBean (its uniqueness
	// is checked in addRelationInt())
	// Can throw InstanceNotFoundException (but detected above)
	// No MBeanException as no exception raised by this method, and no
	// ReflectionException
	String relId = null;
	try {
	    relId = (String)(myMBeanServer.getAttribute(theRelObjectName,
                                                        "RelationId"));

	} catch (MBeanException exc1) {
	    throw new RuntimeException(
				     (exc1.getTargetException()).getMessage());
	} catch (ReflectionException exc2) {
	    throw new RuntimeException(exc2.getMessage());
	} catch (AttributeNotFoundException exc3) {
	    throw new RuntimeException(exc3.getMessage());
	}

	if (relId == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "This MBean does not provide a relation id.";
	    throw new InvalidRelationIdException(excMsg);
	}
	// Checks that the Relation Service where the relation MBean is
	// expected to be added is the current one
	// Can throw InstanceNotFoundException (but detected above)
	// No MBeanException as no exception raised by this method, no
	// ReflectionException
	ObjectName relServObjName = null;
	try {
	    relServObjName = (ObjectName)
		(myMBeanServer.getAttribute(theRelObjectName,
                                            "RelationServiceName"));

	} catch (MBeanException exc1) {
	    throw new RuntimeException(
				     (exc1.getTargetException()).getMessage());
	} catch (ReflectionException exc2) {
	    throw new RuntimeException(exc2.getMessage());
	} catch (AttributeNotFoundException exc3) {
	    throw new RuntimeException(exc3.getMessage());
	}

	boolean badRelServFlg = false;
	if (relServObjName == null) {
	    badRelServFlg = true;

	} else if (!(relServObjName.equals(myObjName))) {
	    badRelServFlg = true;
	}
	if (badRelServFlg) {
	    // Revisit [cebro] Localize message
	    String excMsg = "The Relation Service referenced in the MBean is not the current one.";
	    throw new InvalidRelationServiceException(excMsg);
	}
	// Checks that a relation type has been specified for the relation
	// Can throw InstanceNotFoundException (but detected above)
	// No MBeanException as no exception raised by this method, no
	// ReflectionException
	String relTypeName = null;
	try {
	    relTypeName = (String)(myMBeanServer.getAttribute(theRelObjectName,
                                                              "RelationTypeName"));

	} catch (MBeanException exc1) {
	    throw new RuntimeException(
				     (exc1.getTargetException()).getMessage());
	}catch (ReflectionException exc2) {
	    throw new RuntimeException(exc2.getMessage());
	} catch (AttributeNotFoundException exc3) {
	    throw new RuntimeException(exc3.getMessage());
	}
	if (relTypeName == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "No relation type provided.";
	    throw new RelationTypeNotFoundException(excMsg);
	}
	// Retrieves all roles without considering read mode
	// Can throw InstanceNotFoundException (but detected above)
	// No MBeanException as no exception raised by this method, no
	// ReflectionException
	RoleList roleList = null;
	try {
	    roleList = (RoleList)(myMBeanServer.invoke(theRelObjectName,
						       "retrieveAllRoles",
						       null,
						       null));
	} catch (MBeanException exc1) {
	    throw new RuntimeException(
				     (exc1.getTargetException()).getMessage());
	} catch (ReflectionException exc2) {
	    throw new RuntimeException(exc2.getMessage());
	}

	// Can throw RoleNotFoundException, InvalidRelationIdException,
	// RelationTypeNotFoundException, InvalidRoleValueException
	addRelationInt(false,
		       null,
		       theRelObjectName,
		       relId,
		       relTypeName,
		       roleList);
	// Adds relation MBean ObjectName in map
	synchronized(myRelMBeanObjName2RelIdMap) {
	    myRelMBeanObjName2RelIdMap.put(theRelObjectName, relId);
	}

	// Updates flag to specify that the relation is managed by the Relation
	// Service
	// This flag and setter are inherited from RelationSupport and not parts
	// of the Relation interface, so may be not supported.
	try {
	    myMBeanServer.setAttribute(theRelObjectName,
                                       new Attribute(
                                         "RelationServiceManagementFlag",
                                         new Boolean(true)));
	} catch (Exception exc) {
	    // OK : The flag is not supported.
	}

	// Updates listener information to received notification for
	// unregistration of this MBean
	ArrayList newRefList = new ArrayList();
	newRefList.add(theRelObjectName);
	updateUnregistrationListener(newRefList, null);

	if (isTraceOn())
	    trace("addRelation: exiting", null);
	return;
    }

    /**
     * If the relation is represented by an MBean (created by the user and
     * added as a relation in the Relation Service), returns the ObjectName of
     * the MBean.
     *
     * @param theRelId  relation id identifying the relation
     *
     * @return ObjectName of the corresponding relation MBean, or null if
     * the relation is not an MBean.
     *
     * @exception IllegalArgumentException  if null parameter
     * @exception RelationNotFoundException there is no relation associated
     * to that id
     */
    public ObjectName isRelationMBean(String theRelId)
	throws IllegalArgumentException,
	       RelationNotFoundException{

	if (theRelId == null) {
	    // Revisit [cebro[ Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn())
	    trace("isRelationMBean", theRelId);

	// Can throw RelationNotFoundException
	Object result = getRelation(theRelId);
	if (result instanceof ObjectName) {
	    return ((ObjectName)result);
	} else {
	    return null;
	}
    }

    /**
     * Returns the relation id associated to the given ObjectName if the
     * MBean has been added as a relation in the Relation Service.
     *
     * @param theObjName  ObjectName of supposed relation
     *
     * @return relation id (String) or null (if the ObjectName is not a
     * relation handled by the Relation Service)
     *
     * @exception IllegalArgumentException  if null parameter
     */
    public String isRelation(ObjectName theObjName)
	throws IllegalArgumentException {

	if (theObjName == null) {
	    // Revisit [cebro[ Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn())
	    trace("isRelation", theObjName.toString());

	String result = null;
	synchronized(myRelMBeanObjName2RelIdMap) {
	    String relId = (String)
			    (myRelMBeanObjName2RelIdMap.get(theObjName));
	    if (relId != null) {
		result = relId;
	    }
	}
	return result;
    }

    /**
     * Checks if there is a relation identified in Relation Service with given
     * relation id.
     *
     * @param theRelId  relation id identifying the relation
     *
     * @return boolean: true if there is a relation, false else
     *
     * @exception IllegalArgumentException  if null parameter
     */
    public Boolean hasRelation(String theRelId)
	throws IllegalArgumentException {

	if (theRelId == null) {
	    // Revisit [cebro[ Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn())
	    trace("hasRelation", theRelId);

	try {
	    // Can throw RelationNotFoundException
	    Object result = getRelation(theRelId);
	    return new Boolean(true);
	} catch (RelationNotFoundException exc) {
	    return new Boolean(false);
	}
    }

    /**
     * Returns all the relation ids for all the relations handled by the
     * Relation Service.
     *
     * @return ArrayList of String
     */
    public List getAllRelationIds() {
	ArrayList result = null;
	synchronized(myRelId2ObjMap) {
	    result = new ArrayList(myRelId2ObjMap.keySet());
	}
	return result;
    }

    /**
     * Checks if given Role can be read in a relation of the given type.
     *
     * @param theRoleName  name of role to be checked
     * @param theRelTypeName  name of the relation type
     *
     * @return an Integer wrapping an integer corresponding to possible
     * problems represented as constants in RoleUnresolved:
     * <P>- 0 if role can be read
     * <P>- integer corresponding to RoleStatus.NO_ROLE_WITH_NAME
     * <P>- integer corresponding to RoleStatus.ROLE_NOT_READABLE
     *
     * @exception IllegalArgumentException  if null parameter
     * @exception RelationTypeNotFoundException  if the relation type is not
     * known in the Relation Service
     */
    public Integer checkRoleReading(String theRoleName,
				    String theRelTypeName)
	throws IllegalArgumentException,
               RelationTypeNotFoundException {

	if (theRoleName == null || theRelTypeName == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn()) {
	    String str = "theRoleName " + theRoleName
				    + ", theRelTypeName " + theRelTypeName;
	    trace("checkRoleReading: entering", str);
	}

	Integer result = null;

	// Can throw a RelationTypeNotFoundException
	RelationType relType = getRelationType(theRelTypeName);

	try {
	    // Can throw a RoleInfoNotFoundException to be transformed into
	    // returned value RoleStatus.NO_ROLE_WITH_NAME
	    RoleInfo roleInfo = relType.getRoleInfo(theRoleName);

	    result =  checkRoleInt(1,
				   theRoleName,
				   null,
				   roleInfo,
				   false);

	} catch (RoleInfoNotFoundException exc) {
	    result = new Integer(RoleStatus.NO_ROLE_WITH_NAME);
	}

	if (isTraceOn())
	    trace("checkRoleReading: exiting", null);
	return result;
    }

    /**
     * Checks if given Role can be set in a relation of given type.
     *
     * @param theRole  role to be checked
     * @param theRelTypeName  name of relation type
     * @param theInitFlg  flag to specify that the checking is done for the
     * initialization of a role, write access shall not be verified.
     *
     * @return an Integer wrapping an integer corresponding to possible
     * problems represented as constants in RoleUnresolved:
     * <P>- 0 if role can be set
     * <P>- integer corresponding to RoleStatus.NO_ROLE_WITH_NAME
     * <P>- integer for RoleStatus.ROLE_NOT_WRITABLE
     * <P>- integer for RoleStatus.LESS_THAN_MIN_ROLE_DEGREE
     * <P>- integer for RoleStatus.MORE_THAN_MAX_ROLE_DEGREE
     * <P>- integer for RoleStatus.REF_MBEAN_OF_INCORRECT_CLASS
     * <P>- integer for RoleStatus.REF_MBEAN_NOT_REGISTERED
     *
     * @exception IllegalArgumentException  if null parameter
     * @exception RelationTypeNotFoundException  if unknown relation type
     */
    public Integer checkRoleWriting(Role theRole,
				    String theRelTypeName,
				    Boolean theInitFlg)
	throws IllegalArgumentException,
	       RelationTypeNotFoundException {

	if (theRole == null ||
	    theRelTypeName == null ||
	    theInitFlg == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn()) {
	    String str = new String("theRole " + theRole.toString()
				    + ", theRelTypeName " + theRelTypeName
				    + ", theInitFlg " + theInitFlg);
	    trace("checkRoleWriting: entering", str);
	}

	// Can throw a RelationTypeNotFoundException
	RelationType relType = getRelationType(theRelTypeName);

	String roleName = theRole.getRoleName();
	ArrayList roleValue = (ArrayList)(theRole.getRoleValue());
	boolean writeChkFlg = true;
	if (theInitFlg.booleanValue()) {
	    writeChkFlg = false;
	}

	RoleInfo roleInfo = null;
	try {
	    roleInfo = relType.getRoleInfo(roleName);
	} catch (RoleInfoNotFoundException exc) {
	    if (isTraceOn())
	    trace("checkRoleWriting: exiting", null);
	    return new Integer(RoleStatus.NO_ROLE_WITH_NAME);
	}

	Integer result = checkRoleInt(2,
				      roleName,
				      roleValue,
				      roleInfo,
				      writeChkFlg);

	if (isTraceOn())
	    trace("checkRoleWriting: exiting", null);
	return result;
    }

    /**
     * Sends a notification (RelationNotification) for a relation creation.
     * The notification type is:
     * <P>- RelationNotification.RELATION_BASIC_CREATION if the relation is an
     * object internal to the Relation Service
     * <P>- RelationNotification.RELATION_MBEAN_CREATION if the relation is a
     * MBean added as a relation.
     * <P>The source object is the Relation Service itself.
     * <P>It is called in Relation Service createRelation() and
     * addRelation() methods.
     *
     * @param theRelId  relation identifier of the updated relation
     *
     * @exception IllegalArgumentException  if null parameter
     * @exception RelationNotFoundException  if there is no relation for given
     * relation id
     */
    public void sendRelationCreationNotification(String theRelId)
	throws IllegalArgumentException,
	       RelationNotFoundException {

	if (theRelId == null) {
	    // Revisit [cebro[ Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn())
	    trace("sendRelationCreationNotification: entering", theRelId);

	// Message
	// Revisit [cebro] Localize message
	StringBuffer ntfMsg = new StringBuffer("Creation of relation ");
	ntfMsg.append(theRelId);

	// Can throw RelationNotFoundException
	sendNotificationInt(1,
			    ntfMsg.toString(),
			    theRelId,
			    null,
			    null,
			    null,
			    null);

	if (isTraceOn())
	    trace("sendRelationCreationNotification: exiting", null);
	return;	
    }

    /**
     * Sends a notification (RelationNotification) for a role update in the
     * given relation. The notification type is:
     * <P>- RelationNotification.RELATION_BASIC_UPDATE if the relation is an
     * object internal to the Relation Service
     * <P>- RelationNotification.RELATION_MBEAN_UPDATE if the relation is a
     * MBean added as a relation.
     * <P>The source object is the Relation Service itself.
     * <P>It is called in relation MBean setRole() (for given role) and
     * setRoles() (for each role) methods (implementation provided in
     * RelationSupport class).
     * <P>It is also called in Relation Service setRole() (for given role) and
     * setRoles() (for each role) methods.
     *
     * @param theRelId  relation identifier of the updated relation
     * @param theNewRole  new role (name and new value)
     * @param theOldRoleValue  old role value (List of ObjectName objects)
     *
     * @exception IllegalArgumentException  if null parameter
     * @exception RelationNotFoundException  if there is no relation for given
     * relation id
     */
    public void sendRoleUpdateNotification(String theRelId,
					   Role theNewRole,
					   List theOldRoleValue)
	throws IllegalArgumentException,
	       RelationNotFoundException {

	if (theRelId == null ||
	    theNewRole == null ||
	    theOldRoleValue == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (!(theOldRoleValue instanceof ArrayList))
	    theOldRoleValue = new ArrayList(theOldRoleValue);

	if (isTraceOn()) {
	    String str = new String("theRelId " + theRelId
				    + ", theNewRole " + theNewRole.toString()
				    + ", theOldRoleValue "
				    + theOldRoleValue.toString());
	    trace("sendRoleUpdateNotification: entering", str);
	}

	String roleName = theNewRole.getRoleName();
	ArrayList newRoleVal = (ArrayList)(theNewRole.getRoleValue());

	// Message
	String newRoleValString = Role.roleValueToString(newRoleVal);
	String oldRoleValString = Role.roleValueToString(theOldRoleValue);
	// Revisit [cebro] Localize message
	StringBuffer ntfMsg = new StringBuffer("Value of role ");
	ntfMsg.append(roleName);
	ntfMsg.append(" has changed\nOld value:\n");
	ntfMsg.append(oldRoleValString);
	ntfMsg.append("\nNew value:\n");
	ntfMsg.append(newRoleValString);

	// Can throw a RelationNotFoundException
	sendNotificationInt(2,
			    ntfMsg.toString(),
			    theRelId,
			    null,
			    roleName,
			    newRoleVal,
			    theOldRoleValue);

	if (isTraceOn())
	    trace("sendRoleUpdateNotification: exiting", null);
	return;	
    }

    /**
     * Sends a notification (RelationNotification) for a relation removal.
     * The notification type is:
     * <P>- RelationNotification.RELATION_BASIC_REMOVAL if the relation is an
     * object internal to the Relation Service
     * <P>- RelationNotification.RELATION_MBEAN_REMOVAL if the relation is a
     * MBean added as a relation.
     * <P>The source object is the Relation Service itself.
     * <P>It is called in Relation Service removeRelation() method.
     *
     * @param theRelId  relation identifier of the updated relation
     * @param theUnregMBeanList  List of ObjectNames of MBeans expected
     * to be unregistered due to relation removal (can be null)
     *
     * @exception IllegalArgumentException  if null parameter
     * @exception RelationNotFoundException  if there is no relation for given
     * relation id
     */
    public void sendRelationRemovalNotification(String theRelId,
						List theUnregMBeanList)
	throws IllegalArgumentException,
	       RelationNotFoundException {

	if (theRelId == null) {
	    String excMsg = "Invalid parameter";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn()) {
	    StringBuffer strB = new StringBuffer("theRelId " + theRelId);
	    if (theUnregMBeanList != null) {
		strB.append(", theUnregMBeanList "
			    + theUnregMBeanList.toString());
	    }
	    trace("sendRelationRemovalNotification: entering",
		  strB.toString());
	}

	// Message
	// Revisit [cebro] Include string for ObjectNames to be unregistered?
	StringBuffer ntfMsg = new StringBuffer("Removal of relation ");
	ntfMsg.append(theRelId);

	// Can throw RelationNotFoundException
	sendNotificationInt(3,
			    ntfMsg.toString(),
			    theRelId,
			    theUnregMBeanList,
			    null,
			    null,
			    null);

	if (isTraceOn())
	    trace("sendRelationRemovalNotification: exiting", null);
	return;	
    }

    /**
     * Handles update of the Relation Service role map for the update of given
     * role in given relation.
     * <P>It is called in relation MBean setRole() (for given role) and
     * setRoles() (for each role) methods (implementation provided in
     * RelationSupport class).
     * <P>It is also called in Relation Service setRole() (for given role) and
     * setRoles() (for each role) methods.
     * <P>To allow the Relation Service to maintain the consistency (in case
     * of MBean unregistration) and to be able to perform queries, this method
     * must be called when a role is updated.
     *
     * @param theRelId  relation identifier of the updated relation
     * @param theNewRole  new role (name and new value)
     * @param theOldRoleValue  old role value (List of ObjectName objects)
     *
     * @exception IllegalArgumentException  if null parameter
     * @exception RelationServiceNotRegisteredException  if the Relation
     * Service is not registered in the MBean Server
     * @exception RelationNotFoundException  if no relation for given id.
     */
    public void updateRoleMap(String theRelId,
			      Role theNewRole,
			      List theOldRoleValue)
	throws IllegalArgumentException,
	       RelationServiceNotRegisteredException,
               RelationNotFoundException {

	if (theRelId == null ||
	    theNewRole == null ||
	    theOldRoleValue == null) {
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn()) {
	    String str = new String("theRelId " + theRelId
				    + ", theNewRole " + theNewRole.toString()
				    + ", theOldRoleValue "
				    + theOldRoleValue.toString());
	    trace("updateRoleMap: entering", str);
	}

	// Can throw RelationServiceNotRegisteredException
	isActive();

	// Verifies the relation has been added in the Relation Service
	// Can throw a RelationNotFoundException
	Object result = getRelation(theRelId);

	String roleName = theNewRole.getRoleName();
	ArrayList newRoleValue = (ArrayList)(theNewRole.getRoleValue());
	// Note: no need to test if theOldRoleValue not null before cloning,
	//       tested above.
	ArrayList oldRoleValue = new ArrayList(theOldRoleValue);

	// List of ObjectNames of new referenced MBeans
	ArrayList newRefList = new ArrayList();

	for (Iterator newRoleIter = newRoleValue.iterator();
	     newRoleIter.hasNext();) {
	    ObjectName currObjName = (ObjectName)(newRoleIter.next());

	    // Checks if this ObjectName was already present in old value
	    // Note: use copy (oldRoleValue) instead of original
	    //       theOldRoleValue to speed up, as oldRoleValue is decreased
	    //       by removing unchanged references :)
	    int currObjNamePos = oldRoleValue.indexOf(currObjName);

	    if (currObjNamePos == -1) {
		// New reference to an ObjectName

		// Stores this reference into map
		// Returns true if new reference, false if MBean already
		// referenced
		boolean isNewFlg = addNewMBeanReference(currObjName,
							theRelId,
							roleName);

		if (isNewFlg) {
		    // Adds it into list of new reference
		    newRefList.add(currObjName);
		}

	    } else {
		// MBean was already referenced in old value

		// Removes it from old value (local list) to ignore it when
		// looking for remove MBean references
		oldRoleValue.remove(currObjNamePos);
	    }
	}

	// List of ObjectNames of MBeans no longer referenced
	ArrayList obsRefList = new ArrayList();

	// Each ObjectName remaining in oldRoleValue is an ObjectName no longer
	// referenced in new value
	for (Iterator oldRoleIter = oldRoleValue.iterator();
	     oldRoleIter.hasNext();) {

	    ObjectName currObjName = (ObjectName)(oldRoleIter.next());
	    // Removes MBean reference from map
	    // Returns true if the MBean is no longer referenced in any
	    // relation
	    boolean noLongerRefFlg = removeMBeanReference(currObjName,
							  theRelId,
							  roleName,
							  false);

	    if (noLongerRefFlg) {
		// Adds it into list of references to be removed
		obsRefList.add(currObjName);
	    }
	}

	// To avoid having one listener per ObjectName of referenced MBean,
	// and to increase performances, there is only one listener recording
	// all ObjectNames of interest
	updateUnregistrationListener(newRefList, obsRefList);

	if (isTraceOn())
	    trace("updateRoleMap: exiting", null);
	return;
    }

    /**
     * Removes given relation from the Relation Service.
     * <P>A RelationNotification notification is sent, its type being:
     * <P>- RelationNotification.RELATION_BASIC_REMOVAL if the relation was
     * only internal to the Relation Service
     * <P>- RelationNotification.RELATION_MBEAN_REMOVAL if the relation is
     * registered as an MBean.
     * <P>For MBeans referenced in such relation, nothing will be done,
     *
     * @param theRelId  relation id of the relation to be removed
     *
     * @exception RelationServiceNotRegisteredException  if the Relation
     * Service is not registered in the MBean Server
     * @exception IllegalArgumentException  if null parameter
     * @exception RelationNotFoundException  if no relation corresponding to
     * given relation id
     */
    public void removeRelation(String theRelId)
	throws RelationServiceNotRegisteredException,
	       IllegalArgumentException,
	       RelationNotFoundException {

	// Can throw RelationServiceNotRegisteredException
	isActive();

	if (theRelId == null) {
	    // Revisit [cebro[ Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn())
	    trace("removeRelation: entering", theRelId);

	// Checks there is a relation with this id
	// Can throw RelationNotFoundException
	Object result = getRelation(theRelId);

	// Removes it from listener filter
	if (result instanceof ObjectName) {
	    ArrayList obsRefList = new ArrayList();
	    obsRefList.add((ObjectName)result);
	    // Can throw a RelationServiceNotRegisteredException
	    updateUnregistrationListener(null, obsRefList);
	}

	// Sends a notification
	// Note: has to be done FIRST as needs the relation to be still in the
	//       Relation Service
	// No RelationNotFoundException as checked above

	// Revisit [cebro] Handle CIM "Delete" and "IfDeleted" qualifiers:
	//   deleting the relation can mean to delete referenced MBeans. In
	//   that case, MBeans to be unregistered are put in a list sent along
	//   with the notification below

	// Can throw a RelationNotFoundException (but detected above)
	sendRelationRemovalNotification(theRelId, null);

	// Removes the relation from various internal maps

	//  - MBean reference map
	// Retrieves the MBeans referenced in this relation
	// Note: here we cannot use removeMBeanReference() because it would
	//       require to know the MBeans referenced in the relation. For
	//       that it would be necessary to call 'getReferencedMBeans()'
	//       on the relation itself. Ok if it is an internal one, but if
	//       it is an MBean, it is possible it is already unregistered, so
	//       not available through the MBean Server.
	ArrayList refMBeanList = new ArrayList();
	// List of MBeans no longer referenced in any relation, to be
	// removed fom the map
	ArrayList nonRefObjNameList = new ArrayList();

	synchronized(myRefedMBeanObjName2RelIdsMap) {

	    for (Iterator refMBeanIter =
		     (myRefedMBeanObjName2RelIdsMap.keySet()).iterator();
		 refMBeanIter.hasNext();) {

		ObjectName currRefObjName = (ObjectName)(refMBeanIter.next());
		// Retrieves relations where the MBean is referenced
		HashMap relIdMap = (HashMap)
		    (myRefedMBeanObjName2RelIdsMap.get(currRefObjName));

		if (relIdMap.containsKey(theRelId)) {
		    relIdMap.remove(theRelId);
		    refMBeanList.add(currRefObjName);
		}

		if (relIdMap.isEmpty()) {
		    // MBean no longer referenced
		    // Note: do not remove it here because pointed by the
		    //       iterator!
		    nonRefObjNameList.add(currRefObjName);
		}
	    }

	    // Cleans MBean reference map by removing MBeans no longer
	    // referenced
	    for (Iterator nonRefObjNameIter = nonRefObjNameList.iterator();
		 nonRefObjNameIter.hasNext();) {
		ObjectName currRefObjName = (ObjectName)
		    (nonRefObjNameIter.next());
		myRefedMBeanObjName2RelIdsMap.remove(currRefObjName);
	    }
	}

	// - Relation id to object map
	synchronized(myRelId2ObjMap) {
	    myRelId2ObjMap.remove(theRelId);
	}

	if (result instanceof ObjectName) {
	    // - ObjectName to relation id map
	    synchronized(myRelMBeanObjName2RelIdMap) {
		myRelMBeanObjName2RelIdMap.remove((ObjectName)result);
	    }
	}

	// Relation id to relation type name map
	// First retrieves the relation type name
	String relTypeName = null;
	synchronized(myRelId2RelTypeMap) {
	    relTypeName = (String)(myRelId2RelTypeMap.get(theRelId));
	    myRelId2RelTypeMap.remove(theRelId);
	}
	// - Relation type name to relation id map
	synchronized(myRelType2RelIdsMap) {
	    ArrayList relIdList =
		(ArrayList)(myRelType2RelIdsMap.get(relTypeName));
	    if (relIdList != null) {
		// Can be null if called from removeRelationType()
		relIdList.remove(theRelId);
		if (relIdList.isEmpty()) {
		    // No other relation of that type
		    myRelType2RelIdsMap.remove(relTypeName);
		}
	    }
	}

	if (isTraceOn())
	    trace("removeRelation: exiting", null);
	return;
    }

    /**
     * Purges the relations.
     *
     * <P>Depending on the purgeFlag value, this method is either called
     * automatically when a notification is received for the unregistration of
     * an MBean referenced in a relation (if the flag is set to true), or not
     * (if the flag is set to false).
     * <P>In that case it is up to the user to call it to maintain the
     * consistency of the relations. To be kept in mind that if an MBean is
     * unregistered and the purge not done immediately, if the ObjectName is
     * reused and assigned to another MBean referenced in a relation, calling
     * manually this purgeRelations() method will cause trouble, as will
     * consider the ObjectName as corresponding to the unregistered MBean, not
     * seeing the new one.
     *
     * <P>The behavior depends on the cardinality of the role where the
     * unregistered MBean is referenced:
     * <P>- if removing one MBean reference in the role makes its number of
     * references less than the minimum degree, the relation has to be removed.
     * <P>- if the remaining number of references after removing the MBean
     * reference is still in the cardinality range, keep the relation and
     * update it calling its handleMBeanUnregistration() callback.
     *
     * @exception RelationServiceNotRegisteredException  if the Relation
     * Service is not registered in the MBean Server.
     */
    public void purgeRelations()
	throws RelationServiceNotRegisteredException {

	if (isTraceOn())
	    trace("purgeRelations: entering", null);

	// Can throw RelationServiceNotRegisteredException
	isActive();

	// Revisit [cebro] Handle the CIM "Delete" and "IfDeleted" qualifier:
	//    if the unregistered MBean has the "IfDeleted" qualifier,
	//    possible that the relation itself or other referenced MBeans
	//    have to be removed (then a notification would have to be sent
	//    to inform that they should be unregistered.


	// Clones the list of notifications to be able to still receive new
	// notifications while proceeding those ones
	ArrayList localUnregNtfList = null;
	synchronized(myUnregNtfList) {
	    localUnregNtfList = (ArrayList)(myUnregNtfList.clone());
	    // Resets list
	    myUnregNtfList = new ArrayList();
	}


	// Updates the listener filter to avoid receiving notifications for
	// those MBeans again
	// Makes also a local "myRefedMBeanObjName2RelIdsMap" map, mapping
	// ObjectName -> relId -> roles, to remove the MBean from the global
	// map
	// List of references to be removed from the listener filter
        ArrayList obsRefList = new ArrayList();
	// Map including ObjectNames for unregistered MBeans, with
	// referencing relation ids and roles
	HashMap localMBean2RelIdMap = new HashMap();

	synchronized(myRefedMBeanObjName2RelIdsMap) {
	    for (Iterator unregNtfIter = localUnregNtfList.iterator();
		 unregNtfIter.hasNext();) {

		MBeanServerNotification currNtf =
		    (MBeanServerNotification)(unregNtfIter.next());

		ObjectName unregMBeanName = currNtf.getMBeanName();

		// Adds the unregsitered MBean in the list of references to
		// remove from the listener filter
		obsRefList.add(unregMBeanName);

		// Retrieves the associated map of relation ids and roles
		HashMap relIdMap = (HashMap)
		    (myRefedMBeanObjName2RelIdsMap.get(unregMBeanName));
		localMBean2RelIdMap.put(unregMBeanName, relIdMap);

		myRefedMBeanObjName2RelIdsMap.remove(unregMBeanName);
	    }
	}

	// Updates the listener
	// Can throw RelationServiceNotRegisteredException
	updateUnregistrationListener(null, obsRefList);

	for (Iterator unregNtfIter = localUnregNtfList.iterator();
	     unregNtfIter.hasNext();) {

	    MBeanServerNotification currNtf =
		(MBeanServerNotification)(unregNtfIter.next());

	    ObjectName unregMBeanName = currNtf.getMBeanName();

	    // Retrieves the relations where the MBean is referenced
	    HashMap localRelIdMap = (HashMap)
		    (localMBean2RelIdMap.get(unregMBeanName));

	    // List of relation ids where the unregistered MBean is
	    // referenced 
	    Set localRelIdSet = localRelIdMap.keySet();
	    for (Iterator relIdIter = localRelIdSet.iterator();
		 relIdIter.hasNext();) {

		String currRelId = (String)(relIdIter.next());

		// List of roles of the relation where the MBean is
		// referenced
		ArrayList localRoleNameList = (ArrayList)
		    (localRelIdMap.get(currRelId));

		// Checks if the relation has to be removed or not,
		// regarding expected minimum role cardinality and current
		// number of references after removal of the current one
		// If the relation is kept, calls
		// handleMBeanUnregistration() callback of the relation to
		// update it
		//
		// Can throw RelationServiceNotRegisteredException
		//
		// Shall not throw RelationNotFoundException,
		// RoleNotFoundException, MBeanException
		try {
		    handleReferenceUnregistration(currRelId,
						  unregMBeanName,
						  localRoleNameList);
		} catch (RelationNotFoundException exc1) {
		    throw new RuntimeException(exc1.getMessage());
		} catch (RoleNotFoundException exc2) {
		    throw new RuntimeException(exc2.getMessage());
		}
	    }
	}

	if (isTraceOn())
	    trace("purgeRelations: exiting", null);
	return;
    }

    /**
     * Retrieves the relations where a given MBean is referenced.
     * <P>This corresponds to the CIM "References" and "ReferenceNames"
     * operations.
     *
     * @param theMBeanName  ObjectName of MBean
     * @param theRelTypeName  can be null; if specified, only the relations
     * of that type will be considered in the search. Else all relation types
     * are considered.
     * @param theRoleName  can be null; if specified, only the relations
     * where the MBean is referenced in that role will be returned. Else all
     * roles are considered.
     *
     * @return an HashMap, where the keys are the relation ids of the relations
     * where the MBean is referenced, and the value is, for each key,
     * an ArrayList of role names (as an MBean can be referenced in several
     * roles in the same relation).
     *
     * @exception IllegalArgumentException  if null parameter
     */
    public Map findReferencingRelations(ObjectName theMBeanName,
					String theRelTypeName,
					String theRoleName)
	throws IllegalArgumentException {

	if (theMBeanName == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn()) {
	    String str = new String("theMBeanName " + theMBeanName.toString()
				    + ", theRelTypeName " + theRelTypeName
				    + ", theRoleName " + theRoleName);
	    trace("findReferencingRelations: entering", str);
	}

	HashMap result = new HashMap();

	synchronized(myRefedMBeanObjName2RelIdsMap) {

	    // Retrieves the relations referencing the MBean
	    HashMap relId2RoleNamesMap =
		(HashMap)(myRefedMBeanObjName2RelIdsMap.get(theMBeanName));

	    if (relId2RoleNamesMap != null) {

		// Relation Ids where the MBean is referenced
		Set allRelIdSet = relId2RoleNamesMap.keySet();

		// List of relation ids of interest regarding the selected
		// relation type
		ArrayList relIdList = null;
		if (theRelTypeName == null) {
		    // Considers all relations
		    relIdList = new ArrayList(allRelIdSet);

		} else {

		    relIdList = new ArrayList();

		    // Considers only the relation ids for relations of given
		    // type
		    for (Iterator relIdIter = allRelIdSet.iterator();
			 relIdIter.hasNext();) {
			String currRelId = (String)(relIdIter.next());

			// Retrieves its relation type
			String currRelTypeName = null;
			synchronized(myRelId2RelTypeMap) {
			    currRelTypeName = (String)
				(myRelId2RelTypeMap.get(currRelId));
			}

			if (currRelTypeName.equals(theRelTypeName)) {

			    relIdList.add(currRelId);

			}
		    }
		}

		// Now looks at the roles where the MBean is expected to be
		// referenced

		for (Iterator relIdIter = relIdList.iterator();
		     relIdIter.hasNext();) {

		    String currRelId = (String)(relIdIter.next());
		    // Retrieves list of role names where the MBean is
		    // referenced
		    ArrayList currRoleNameList =
			(ArrayList)(relId2RoleNamesMap.get(currRelId));

		    if (theRoleName == null) {
			// All roles to be considered
			// Note: no need to test if list not null before
			//       cloning, MUST be not null else bug :(
			result.put(currRelId,
				   (ArrayList)(currRoleNameList.clone()));

		    }  else if (currRoleNameList.contains(theRoleName)) {
			// Filters only the relations where the MBean is
			// referenced in // given role
			ArrayList dummyList = new ArrayList();
			dummyList.add(theRoleName);
			result.put(currRelId, dummyList);
		    }
		}
	    }
	}

	if (isTraceOn())
	    trace("findReferencingRelations: exiting", null);
	return result;	    
    }

    /**
     * Retrieves the MBeans associated to given one in a relation.
     * <P>This corresponds to CIM Associators and AssociatorNames operations.
     *
     * @param theMBeanName  ObjectName of MBean
     * @param theRelTypeName  can be null; if specified, only the relations
     * of that type will be considered in the search. Else all
     * relation types are considered.
     * @param theRoleName  can be null; if specified, only the relations
     * where the MBean is referenced in that role will be considered. Else all
     * roles are considered.
     *
     * @return an HashMap, where the keys are the ObjectNames of the MBeans
     * associated to given MBean, and the value is, for each key, an ArrayList
     * of the relation ids of the relations where the key MBean is
     * associated to given one (as they can be associated in several different
     * relations).
     *
     * @exception IllegalArgumentException  if null parameter
     */
    public Map findAssociatedMBeans(ObjectName theMBeanName,
				    String theRelTypeName,
				    String theRoleName)
	throws IllegalArgumentException {

	if (theMBeanName == null) {
	    // Revisit [cebro[ Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn()) {
	    String str = new String("theMBeanName " + theMBeanName.toString()
				    + ", theRelTypeName " + theRelTypeName
				    + ", theRoleName " + theRoleName);
	    trace("findAssociatedMBeans: entering", str);
	}

	// Retrieves the map <relation id> -> <role names> for those
	// criterias
	HashMap relId2RoleNamesMap = (HashMap)
	    (findReferencingRelations(theMBeanName,
				      theRelTypeName,
				      theRoleName));

	HashMap result = new HashMap();

	for (Iterator relIdIter = (relId2RoleNamesMap.keySet()).iterator();
	     relIdIter.hasNext();) {

	    String currRelId = (String)(relIdIter.next());

	    // Retrieves ObjectNames of MBeans referenced in this relation
	    //
	    // Shall not throw a RelationNotFoundException if incorrect status
	    // of maps :(
	    HashMap objName2RoleNamesMap = null;
	    try {
		objName2RoleNamesMap = 
		    (HashMap)(getReferencedMBeans(currRelId));
	    } catch (RelationNotFoundException exc) {
		throw new RuntimeException(exc.getMessage());
	    }

	    // For each MBean associated to given one in a relation, adds the
	    // association <ObjectName> -> <relation id> into result map
	    for (Iterator objNameIter =
		     (objName2RoleNamesMap.keySet()).iterator();
		 objNameIter.hasNext();) {
		
		ObjectName currObjName = (ObjectName)(objNameIter.next());

		if (!(currObjName.equals(theMBeanName))) {

		    // Sees if this MBean is already associated to the given
		    // one in another relation
		    ArrayList currRelIdList =
			(ArrayList)(result.get(currObjName));
		    if (currRelIdList == null) {

			currRelIdList = new ArrayList();
			currRelIdList.add(currRelId);
			result.put(currObjName, currRelIdList);

		    } else {
			currRelIdList.add(currRelId);
		    }
		}
	    }
	}

	if (isTraceOn())
	    trace("findReferencingRelations: exiting", null);
	return result;
    }

    /**
     * Returns the relation ids for relations of the given type.
     *
     * @param theRelTypeName  relation type name
     *
     * @return an ArrayList of relation ids.
     *
     * @exception IllegalArgumentException  if null parameter
     * @exception RelationTypeNotFoundException  if there is no relation type
     * with that name.
     */
    public List findRelationsOfType(String theRelTypeName)
	throws IllegalArgumentException,
               RelationTypeNotFoundException {

	if (theRelTypeName == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn())
	    trace("findRelationsOfType: entering", theRelTypeName);

	// Can throw RelationTypeNotFoundException
	RelationType relType = getRelationType(theRelTypeName);

	ArrayList result = new ArrayList();
	synchronized(myRelType2RelIdsMap) {
	    ArrayList result1 = (ArrayList)
		(myRelType2RelIdsMap.get(theRelTypeName));
	    if (result1 != null) {
		result = (ArrayList)(result1.clone());
	    }
	}

	if (isTraceOn())
	    trace("findRelationsOfType: exiting", null);
	return result;
    }

    /**
     * Retrieves role value for given role name in given relation.
     *
     * @param theRelId  relation id
     * @param theRoleName  name of role
     *
     * @return the ArrayList of ObjectName objects being the role value
     *
     * @exception RelationServiceNotRegisteredException  if the Relation
     * Service is not registered
     * @exception IllegalArgumentException  if null parameter
     * @exception RelationNotFoundException  if no relation with given id
     * @exception RoleNotFoundException  if:
     * <P>- there is no role with given name
     * <P>or
     * <P>- the role is not readable.
     *
     * @see #setRole
     */
    public List getRole(String theRelId,
			String theRoleName)
	throws RelationServiceNotRegisteredException,
	       IllegalArgumentException,
               RelationNotFoundException,
               RoleNotFoundException {

	if (theRelId == null || theRoleName == null) {
	    // Revisit [cebro[ Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn()) {
	    String str = "theRelId " + theRelId
				    + ", theRoleName " + theRoleName;
	    trace("getRole: entering", str);
	}

	// Can throw RelationServiceNotRegisteredException
	isActive();

	// Can throw a RelationNotFoundException
	Object relObj = getRelation(theRelId);

	ArrayList result = null;

	if (relObj instanceof RelationSupport) {
	    // Internal relation
	    // Can throw RoleNotFoundException
	    result = (ArrayList)
		(((RelationSupport)relObj).getRoleInt(theRoleName,
						   true,
						   this,
						   false));

	} else {
	    // Relation MBean
	    Object[] params = new Object[1];
	    params[0] = theRoleName;
	    String[] signature = new String[1];
	    signature[0] = "java.lang.String";
	    // Can throw MBeanException wrapping a RoleNotFoundException:
	    // throw wrapped exception
	    //
	    // Shall not throw InstanceNotFoundException or ReflectionException
	    try {
		List invokeResult = (List)
		    (myMBeanServer.invoke(((ObjectName)relObj),
					  "getRole",
					  params,
					  signature));
		if (invokeResult == null || invokeResult instanceof ArrayList)
		    result = (ArrayList) invokeResult;
		else
		    result = new ArrayList(result);
	    } catch (InstanceNotFoundException exc1) {
		throw new RuntimeException(exc1.getMessage());
	    } catch (ReflectionException exc2) {
		throw new RuntimeException(exc2.getMessage());
	    } catch (MBeanException exc3) {
		Exception wrappedExc = exc3.getTargetException();
		if (wrappedExc instanceof RoleNotFoundException) {
		    throw ((RoleNotFoundException)wrappedExc);
		} else {
		    throw new RuntimeException(wrappedExc.getMessage());
		}
	    }
	}

	if (isTraceOn())
	    trace("getRole: exiting", null);
	return result;
    }

    /**
     * Retrieves values of roles with given names in given relation.
     *
     * @param theRelId  relation id
     * @param theRoleNameArray  array of names of roles to be retrieved
     *
     * @return a RoleResult object, including a RoleList (for roles
     * successfully retrieved) and a RoleUnresolvedList (for roles not
     * retrieved).
     *
     * @exception RelationServiceNotRegisteredException  if the Relation
     * Service is not registered in the MBean Server
     * @exception IllegalArgumentException  if null parameter
     * @exception RelationNotFoundException  if no relation with given id
     *
     * @see #setRoles
     */
    public RoleResult getRoles(String theRelId,
			       String[] theRoleNameArray)
	throws RelationServiceNotRegisteredException,
	       IllegalArgumentException,
	       RelationNotFoundException {

	if (theRelId == null || theRoleNameArray == null) {
	    // Revisit [cebro[ Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn())
	    trace("getRoles: entering", theRelId);

	// Can throw RelationServiceNotRegisteredException
	isActive();

	// Can throw a RelationNotFoundException
	Object relObj = getRelation(theRelId);

	RoleResult result = null;

	if (relObj instanceof RelationSupport) {
	    // Internal relation
	    result = ((RelationSupport)relObj).getRolesInt(theRoleNameArray,
							true,
							this);
	} else {
	    // Relation MBean
	    Object[] params = new Object[1];
	    params[0] = theRoleNameArray;
	    String[] signature = new String[1];
	    try {
		signature[0] = (theRoleNameArray.getClass()).getName();
	    } catch (Exception exc) {
		// OK : This is an array of java.lang.String
		//      so this should never happen...
	    }
	    // Shall not throw InstanceNotFoundException, ReflectionException
	    // or MBeanException
	    try {
		result = (RoleResult)
		    (myMBeanServer.invoke(((ObjectName)relObj),
					  "getRoles",
					  params,
					  signature));
	    } catch (InstanceNotFoundException exc1) {
		throw new RuntimeException(exc1.getMessage());
	    } catch (ReflectionException exc2) {
		throw new RuntimeException(exc2.getMessage());
	    } catch (MBeanException exc3) {
		throw new
		    RuntimeException((exc3.getTargetException()).getMessage());
	    }
	}

	if (isTraceOn())
	    trace("getRoles: exiting", null);
	return result;
    }

    /**
     * Returns all roles present in the relation.
     *
     * @param theRelId  relation id
     *
     * @return a RoleResult object, including a RoleList (for roles
     * successfully retrieved) and a RoleUnresolvedList (for roles not
     * readable).
     *
     * @exception IllegalArgumentException  if null parameter
     * @exception RelationNotFoundException  if no relation for given id
     * @exception RelationServiceNotRegisteredException  if the Relation
     * Service is not registered in the MBean Server
     */
    public RoleResult getAllRoles(String theRelId)
        throws IllegalArgumentException,
	       RelationNotFoundException,
               RelationServiceNotRegisteredException {

	if (theRelId == null) {
	    // Revisit [cebro[ Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn())
	    trace("getAllRoles: entering", theRelId);

	// Can throw a RelationNotFoundException
	Object relObj = getRelation(theRelId);

	RoleResult result = null;

	if (relObj instanceof RelationSupport) {
	    // Internal relation
	    result = ((RelationSupport)relObj).getAllRolesInt(true, this);

	} else {
	    // Relation MBean
	    // Shall not throw any Exception
	    try {
		result = (RoleResult)
		    (myMBeanServer.getAttribute(((ObjectName)relObj),
                                                "AllRoles"));
	    } catch (Exception exc) {
		throw new RuntimeException(exc.getMessage());
	    }
	}

	if (isTraceOn())
	    trace("getAllRoles: exiting", null);
	return result;
    }

    /**
     * Retrieves the number of MBeans currently referenced in the given role.
     *
     * @param theRelId  relation id
     * @param theRoleName  name of role
     *
     * @return the number of currently referenced MBeans in that role
     *
     * @exception IllegalArgumentException  if null parameter
     * @exception RelationNotFoundException  if no relation with given id
     * @exception RoleNotFoundException  if there is no role with given name
     */
    public Integer getRoleCardinality(String theRelId,
				      String theRoleName)
	throws IllegalArgumentException,
               RelationNotFoundException,
               RoleNotFoundException {

	if (theRelId == null || theRoleName == null) {
	    // Revisit [cebro[ Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn()) {
	    String str = "theRelId " + theRelId
				    + ", theRoleName " + theRoleName;
	    trace("getRoleCardinality: entering", str);
	}

	// Can throw a RelationNotFoundException
	Object relObj = getRelation(theRelId);

	Integer result = null;

	if (relObj instanceof RelationSupport) {
	    // Internal relation
	    // Can throw RoleNotFoundException
	    result = (Integer)
		(((RelationSupport)relObj).getRoleCardinality(theRoleName));

	} else {
	    // Relation MBean
	    Object[] params = new Object[1];
	    params[0] = theRoleName;
	    String[] signature = new String[1];
	    signature[0] = "java.lang.String";
	    // Can throw MBeanException wrapping RoleNotFoundException:
	    // throw wrapped exception
	    //
	    // Shall not throw InstanceNotFoundException or ReflectionException
	    try {
		result = (Integer)
		    (myMBeanServer.invoke(((ObjectName)relObj),
					  "getRoleCardinality",
					  params,
					  signature));
	    } catch (InstanceNotFoundException exc1) {
		throw new RuntimeException(exc1.getMessage());
	    } catch (ReflectionException exc2) {
		throw new RuntimeException(exc2.getMessage());
	    } catch (MBeanException exc3) {
		Exception wrappedExc = exc3.getTargetException();
		if (wrappedExc instanceof RoleNotFoundException) {
		    throw ((RoleNotFoundException)wrappedExc);
		} else {
		    throw new RuntimeException(wrappedExc.getMessage());
		}
	    }
	}

	if (isTraceOn())
	    trace("getRoleCardinality: exiting", null);
	return result;
    }

    /**
     * Sets the given role in given relation.
     * <P>Will check the role according to its corresponding role definition
     * provided in relation's relation type
     * <P>The Relation Service will keep track of the change to keep the
     * consistency of relations by handling referenced MBean unregistrations.
     *
     * @param theRelId  relation id
     * @param theRole  role to be set (name and new value)
     *
     * @exception RelationServiceNotRegisteredException  if the Relation
     * Service is not registered in the MBean Server
     * @exception IllegalArgumentException  if null parameter
     * @exception RelationNotFoundException  if no relation with given id
     * @exception RoleNotFoundException  if the role does not exist or is not
     * writable
     * @exception InvalidRoleValueException  if value provided for role is not
     * valid:
     * <P>- the number of referenced MBeans in given value is less than
     * expected minimum degree
     * <P>or
     * <P>- the number of referenced MBeans in provided value exceeds expected
     * maximum degree
     * <P>or
     * <P>- one referenced MBean in the value is not an Object of the MBean
     * class expected for that role
     * <P>or
     * <P>- an MBean provided for that role does not exist
     *
     * @see #getRole
     */
    public void setRole(String theRelId,
			Role theRole)
	throws RelationServiceNotRegisteredException,
	       IllegalArgumentException,
	       RelationNotFoundException,
	       RoleNotFoundException,
	       InvalidRoleValueException {

	if (theRelId == null || theRole == null) {
	    // Revisit [cebro[ Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn()) {
	    String str = new String("theRelId " + theRelId
				    + ", theRole " + theRole.toString());
	    trace("setRole: entering", str);
	}

	// Can throw RelationServiceNotRegisteredException
	isActive();

	// Can throw a RelationNotFoundException
	Object relObj = getRelation(theRelId);

	if (relObj instanceof RelationSupport) {
	    // Internal relation
	    // Can throw RoleNotFoundException,
	    // InvalidRoleValueException and
	    // RelationServiceNotRegisteredException
	    //
	    // Shall not throw RelationTypeNotFoundException
	    // (as relation exists in the RS, its relation type is known)
	    try {
		((RelationSupport)relObj).setRoleInt(theRole,
						  true,
						  this,
						  false);

	    } catch (RelationTypeNotFoundException exc) {
		throw new RuntimeException(exc.getMessage());
	    }

	} else {
	    // Relation MBean
	    Object[] params = new Object[1];
	    params[0] = theRole;
	    String[] signature = new String[1];
	    signature[0] = "javax.management.relation.Role";
	    // Can throw MBeanException wrapping RoleNotFoundException,
	    // InvalidRoleValueException
	    //
	    // Shall not MBeanException wrapping an MBeanException wrapping
	    // RelationTypeNotFoundException, or ReflectionException, or
	    // InstanceNotFoundException
	    try {
		myMBeanServer.setAttribute(((ObjectName)relObj),
                                           new Attribute("Role", theRole));

	    } catch (InstanceNotFoundException exc1) {
		throw new RuntimeException(exc1.getMessage());
	    } catch (ReflectionException exc3) {
		throw new RuntimeException(exc3.getMessage());
	    } catch (MBeanException exc2) {
		Exception wrappedExc = exc2.getTargetException();
		if (wrappedExc instanceof RoleNotFoundException) {
		    throw ((RoleNotFoundException)wrappedExc);
		} else if (wrappedExc instanceof InvalidRoleValueException) {
		    throw ((InvalidRoleValueException)wrappedExc);
		} else {
		    throw new RuntimeException(wrappedExc.getMessage());

		}
	    } catch (AttributeNotFoundException exc4) {
              throw new RuntimeException(exc4.getMessage());
            } catch (InvalidAttributeValueException exc5) {
              throw new RuntimeException(exc5.getMessage());
            }
	}

	if (isTraceOn())
	    trace("setRole: exiting", null);
	return;
    }

    /**
     * Sets the given roles in given relation.
     * <P>Will check the role according to its corresponding role definition
     * provided in relation's relation type
     * <P>The Relation Service keeps track of the changes to keep the
     * consistency of relations by handling referenced MBean unregistrations.
     *
     * @param theRelId  relation id
     * @param theRoleList  list of roles to be set
     *
     * @return a RoleResult object, including a RoleList (for roles
     * successfully set) and a RoleUnresolvedList (for roles not
     * set).
     *
     * @exception RelationServiceNotRegisteredException  if the Relation
     * Service is not registered in the MBean Server
     * @exception IllegalArgumentException  if null parameter
     * @exception RelationNotFoundException  if no relation with given id
     *
     * @see #getRoles
     */
    public RoleResult setRoles(String theRelId,
			       RoleList theRoleList)
	throws RelationServiceNotRegisteredException,
	       IllegalArgumentException,
               RelationNotFoundException {

	if (theRelId == null || theRoleList == null) {
	    // Revisit [cebro[ Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn()) {
	    String str = new String("theRelId " + theRelId
				    + ", theRoleList "
				    + theRoleList.toString());
	    trace("setRoles: entering", str);
	}

	// Can throw RelationServiceNotRegisteredException
	isActive();

	// Can throw a RelationNotFoundException
	Object relObj = getRelation(theRelId);

	RoleResult result = null;

	if (relObj instanceof RelationSupport) {
	    // Internal relation
	    // Can throw RelationServiceNotRegisteredException
	    //
	    // Shall not throw RelationTypeNotFoundException (as relation is
	    // known, its relation type exists)
	    try {
		result = ((RelationSupport)relObj).setRolesInt(theRoleList,
							    true,
							    this);
	    } catch (RelationTypeNotFoundException exc) {
		throw new RuntimeException(exc.getMessage());
	    }

	} else {
	    // Relation MBean
	    Object[] params = new Object[1];
	    params[0] = theRoleList;
	    String[] signature = new String[1];
	    signature[0] = "javax.management.relation.RoleList";
	    // Shall not throw InstanceNotFoundException or an MBeanException
	    // or ReflectionException
	    try {
		result = (RoleResult)
		    (myMBeanServer.invoke(((ObjectName)relObj),
					  "setRoles",
					  params,
					  signature));
	    } catch (InstanceNotFoundException exc1) {
		throw new RuntimeException(exc1.getMessage());
	    } catch (ReflectionException exc3) {
		throw new RuntimeException(exc3.getMessage());
	    } catch (MBeanException exc2) {
		throw new
		    RuntimeException((exc2.getTargetException()).getMessage());
	    }
	}

	if (isTraceOn())
	    trace("setRoles: exiting", null);
	return result;
    }

    /**
     * Retrieves MBeans referenced in the various roles of the relation.
     *
     * @param theRelId  relation id
     *
     * @return a HashMap mapping:
     * <P> ObjectName -> ArrayList of String (role
     * names)
     *
     * @exception IllegalArgumentException  if null parameter
     * @exception RelationNotFoundException  if no relation for given
     * relation id
     */
    public Map getReferencedMBeans(String theRelId)
	throws IllegalArgumentException,
	       RelationNotFoundException {

	if (theRelId == null) {
	    // Revisit [cebro[ Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn())
	    trace("getReferencedMBeans: entering", theRelId);

	// Can throw a RelationNotFoundException
	Object relObj = getRelation(theRelId);

	HashMap result = null;

	if (relObj instanceof RelationSupport) {
	    // Internal relation
	    result = (HashMap)(((RelationSupport)relObj).getReferencedMBeans());

	} else {
	    // Relation MBean
	    // No Exception
	    try {
		result = (HashMap)
		    (myMBeanServer.getAttribute(((ObjectName)relObj),
                                                "ReferencedMBeans"));
	    } catch (Exception exc) {
		throw new RuntimeException(exc.getMessage());
	    }
	}

	if (isTraceOn())
	    trace("getReferencedMBeans: exiting", null);
	return result;
    }

    /**
     * Returns name of associated relation type for given relation.
     *
     * @param theRelId  relation id
     *
     * @return the name of the associated relation type.
     *
     * @exception IllegalArgumentException  if null parameter
     * @exception RelationNotFoundException  if no relation for given
     * relation id
     */
    public String getRelationTypeName(String theRelId)
	throws IllegalArgumentException,
	       RelationNotFoundException {

	if (theRelId == null) {
	    // Revisit [cebro[ Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn())
	    trace("getRelationTypeName: entering", theRelId);

	// Can throw a RelationNotFoundException
	Object relObj = getRelation(theRelId);

	String result = null;

	if (relObj instanceof RelationSupport) {
	    // Internal relation
	    result = ((RelationSupport)relObj).getRelationTypeName();

	} else {
	    // Relation MBean
	    // No Exception
	    try {
		result = (String)
		    (myMBeanServer.getAttribute(((ObjectName)relObj),
                                                "RelationTypeName"));
	    } catch (Exception exc) {
		throw new RuntimeException(exc.getMessage());
	    }
	}

	if (isTraceOn())
	    trace("getRelationTypeName: exiting", null);
	return result;
    }

    //
    // NotificationListener Interface
    //

    /**
     * Invoked when a JMX notification occurs.
     * Currently handles notifications for unregistration of MBeans, either
     * referenced in a relation role or being a relation itself.
     *
     * @param theNtf  The notification.
     * @param theHandback  An opaque object which helps the listener to
     * associate information regarding the MBean emitter (can be null).
     */
    public void handleNotification(Notification theNtf,
				   Object theHandback) {

	if (theNtf == null) {
	    // Revisit [cebro[ Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn())
	    trace("handleNotification: entering", theNtf.toString());

	if (theNtf instanceof MBeanServerNotification) {

	    String ntfType = theNtf.getType();

	    if (ntfType.equals(
		       MBeanServerNotification.UNREGISTRATION_NOTIFICATION )) {
		ObjectName mbeanName =
		    ((MBeanServerNotification)theNtf).getMBeanName();

		// Note: use a flag to block access to
		// myRefedMBeanObjName2RelIdsMap only for a quick access
		boolean isRefedMBeanFlg = false;
		synchronized(myRefedMBeanObjName2RelIdsMap) {

		    if (myRefedMBeanObjName2RelIdsMap.containsKey(mbeanName)) {
			// Unregistration of a referenced MBean
			synchronized(myUnregNtfList) {
			    myUnregNtfList.add(theNtf);
			}
			isRefedMBeanFlg = true;
		    }
		    if (isRefedMBeanFlg && myPurgeFlg) {
			// Immediate purge
			// Can throw RelationServiceNotRegisteredException
			// but assume that will be fine :)
			try {
			    purgeRelations();
			} catch (Exception exc) {
			    throw new RuntimeException(exc.getMessage());
			}
		    }
		}

		// Note: do both tests as a relation can be an MBean and be
		//       itself referenced in another relation :)
		String relId = null;
		synchronized(myRelMBeanObjName2RelIdMap){
		    relId = (String)
			(myRelMBeanObjName2RelIdMap.get(mbeanName));
		}
		if (relId != null) {
		    // Unregistration of a relation MBean
		    // Can throw RelationTypeNotFoundException,
		    // RelationServiceNotRegisteredException
		    //
		    // Shall not throw RelationTypeNotFoundException or
		    // InstanceNotFoundException
		    try {
			removeRelation(relId);
		    } catch (Exception exc) {
			throw new RuntimeException(exc.getMessage());
		    } 
		}
	    }
	}

	if (isTraceOn())
	    trace("handleNotification: exiting", null);
	return;
    }

    //
    // NotificationBroadcaster interface
    //

    /**
     * Returns a NotificationInfo object containing the name of the Java class
     * of the notification and the notification types sent.
     */
    public MBeanNotificationInfo[] getNotificationInfo() {

	if (isTraceOn())
	    trace("getNotificationInfo: entering", null);

	MBeanNotificationInfo[] ntfInfoArray =
	    new MBeanNotificationInfo[1];

	String ntfClass = "javax.management.relation.RelationNotification";

	String[] ntfTypes = new String[] {
	    RelationNotification.RELATION_BASIC_CREATION,
	    RelationNotification.RELATION_MBEAN_CREATION,
	    RelationNotification.RELATION_BASIC_UPDATE,
	    RelationNotification.RELATION_MBEAN_UPDATE,
	    RelationNotification.RELATION_BASIC_REMOVAL,
	    RelationNotification.RELATION_MBEAN_REMOVAL,
	};

	String ntfDesc = "Sent when a relation is created, updated or deleted.";

	MBeanNotificationInfo ntfInfo =
	    new MBeanNotificationInfo(ntfTypes, ntfClass, ntfDesc);

	if (isTraceOn())
	    trace("getNotificationInfo: exiting", null);
	return new MBeanNotificationInfo[] {ntfInfo};
    }

    //
    // Misc
    //

    // Adds given object as a relation type.
    //
    // -param theRelTypeObj  relation type object
    //
    // -exception IllegalArgumentException  if null parameter
    // -exception InvalidRelationTypeException  if there is already a relation
    //  type with that name
    private void addRelationTypeInt(RelationType theRelTypeObj)
	throws IllegalArgumentException,
	       InvalidRelationTypeException {

	if (theRelTypeObj == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isDebugOn())
	    debug("addRelationTypeInt: entering", null);

	String relTypeName = theRelTypeObj.getRelationTypeName();

	// Checks that there is not already a relation type with that name
	// existing in the Relation Service
	try {
	    // Can throw a RelationTypeNotFoundException (in fact should ;)
	    RelationType relType = getRelationType(relTypeName);

	    if (relType != null) {
		// Revisit [cebro] Localize message
		String excMsg = "There is already a relation type in the Relation Service with name ";
		StringBuffer excMsgStrB = new StringBuffer(excMsg);
		excMsgStrB.append(relTypeName);
		throw new InvalidRelationTypeException(excMsgStrB.toString());
	    }

	} catch (RelationTypeNotFoundException exc) {
	    // OK : The RelationType could not be found.
	}

	// Adds the relation type
	synchronized(myRelType2ObjMap) {
	    myRelType2ObjMap.put(relTypeName, theRelTypeObj);
	}

	if (theRelTypeObj instanceof RelationTypeSupport) {
	    ((RelationTypeSupport)theRelTypeObj).setRelationServiceFlag(true);
	}

	if (isDebugOn())
	    debug("addRelationTypeInt: exiting", null);
	return;
     }

    // Retrieves relation type with given name
    //
    // -param theRelTypeName  expected name of a relation type created in the
    //  Relation Service
    //
    // -return RelationType object corresponding to given name
    //
    // -exception IllegalArgumentException  if null parameter
    // -exception RelationTypeNotFoundException  if no relation type for that
    //  name created in Relation Service
    //
    RelationType getRelationType(String theRelTypeName) 
	throws IllegalArgumentException,
	       RelationTypeNotFoundException {

	if (theRelTypeName == null) {
	    // Revisit [cebro[ Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isDebugOn())
	    debug("getRelationType: entering", theRelTypeName);

	// No null relation type accepted, so can use get()
	RelationType relType = null;
	synchronized(myRelType2ObjMap) {
	    relType = (RelationType)(myRelType2ObjMap.get(theRelTypeName));
	}

	if (relType == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "No relation type created in the Relation Service with the name ";
	    StringBuffer excMsgStrB = new StringBuffer(excMsg);
	    excMsgStrB.append(theRelTypeName);
	    throw new RelationTypeNotFoundException(excMsgStrB.toString());
	}

	if (isDebugOn())
	    debug("getRelationType: exiting", null);
	return relType;
    }

    // Retrieves relation corresponding to given relation id.
    // Returns either:
    // - a RelationSupport object if the relation is internal
    // or
    // - the ObjectName of the corresponding MBean
    //
    // -param theRelId  expected relation id
    //
    // -return RelationSupport object or ObjectName of relation with given id
    //
    // -exception IllegalArgumentException  if null parameter
    // -exception RelationNotFoundException  if no relation for that
    //  relation id created in Relation Service
    //
    Object getRelation(String theRelId) 
	throws IllegalArgumentException,
	       RelationNotFoundException {

	if (theRelId == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isDebugOn())
	    debug("getRelation: entering", theRelId);	    

	// No null relation  accepted, so can use get()
	Object rel = null;
	synchronized(myRelId2ObjMap) {
	    rel = myRelId2ObjMap.get(theRelId);
	}

	if (rel == null) {
	    StringBuffer excMsgStrB = new StringBuffer();
	    // Revisit [cebro] Localize message
	    String excMsg = "No relation associated to relation id ";
	    excMsgStrB.append(excMsg);
	    excMsgStrB.append(theRelId);
	    throw new RelationNotFoundException(excMsgStrB.toString());
	}

	if (isDebugOn())
	    debug("getRelation: exiting", null);
	return rel;
    }

    // Adds a new MBean reference (reference to an ObjectName) in the
    // referenced MBean map (myRefedMBeanObjName2RelIdsMap).
    //
    // -param theObjName  ObjectName of new referenced MBean
    // -param theRelId  relation id of the relation where the MBean is
    //  referenced
    // -param theRoleName  name of the role where the MBean is referenced
    //
    // -return boolean:
    //  - true  if the MBean was not referenced before, so really a new
    //    reference
    //  - false else
    //
    // -exception IllegalArgumentException  if null parameter
    private boolean addNewMBeanReference(ObjectName theObjName,
					 String theRelId,
					 String theRoleName)
	throws IllegalArgumentException {

	if (theObjName == null ||
	    theRelId == null ||
	    theRoleName == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isDebugOn()) {
	    String str = new String("theObjName " + theObjName.toString()
				    + ", theRelId " + theRelId
				    + ", theRoleName " + theRoleName);
	    debug("addNewMBeanReference: entering", str);
	}

	boolean isNewFlg = false;

	synchronized(myRefedMBeanObjName2RelIdsMap) {

	    // Checks if the MBean was already referenced
	    // No null value allowed, use get() directly
	    HashMap mbeanRefMap = (HashMap)
		(myRefedMBeanObjName2RelIdsMap.get(theObjName));

	    if (mbeanRefMap == null) {
		// MBean not referenced in any relation yet

		isNewFlg = true;

		// List of roles where the MBean is referenced in given
		// relation
		ArrayList roleNames = new ArrayList();
		roleNames.add(theRoleName);

		// Map of relations where the MBean is referenced
		mbeanRefMap = new HashMap();
		mbeanRefMap.put(theRelId, roleNames);

		myRefedMBeanObjName2RelIdsMap.put(theObjName, mbeanRefMap);

	    } else {
		// MBean already referenced in at least another relation
		// Checks if already referenced in another role in current
		// relation
		ArrayList roleNames = (ArrayList)(mbeanRefMap.get(theRelId));

		if (roleNames == null) {
		    // MBean not referenced in current relation

		    // List of roles where the MBean is referenced in given
		    // relation
		    roleNames = new ArrayList();
		    roleNames.add(theRoleName);

		    // Adds new reference done in current relation
		    mbeanRefMap.put(theRelId, roleNames);

		} else {
		    // MBean already referenced in current relation in another
		    // role
		    // Adds new reference done
		    roleNames.add(theRoleName);
		}
	    }
	}

	if (isDebugOn())
	    debug("addNewMBeanReference: exiting", null);
	return isNewFlg;
    }

    // Removes an obsolete MBean reference (reference to an ObjectName) in
    // the referenced MBean map (myRefedMBeanObjName2RelIdsMap).
    //
    // -param theObjName  ObjectName of MBean no longer referenced
    // -param theRelId  relation id of the relation where the MBean was
    //  referenced
    // -param theRoleName  name of the role where the MBean was referenced
    // -param theAllRolesFlg  flag, if true removes reference to MBean for all
    //  roles in the relation, not only for the one above
    //
    // -return boolean:
    //  - true  if the MBean is no longer reference in any relation
    //  - false else
    //
    // -exception IllegalArgumentException  if null parameter
    private boolean removeMBeanReference(ObjectName theObjName,
					 String theRelId,
					 String theRoleName,
					 boolean theAllRolesFlg)
	throws IllegalArgumentException {

	if (theObjName == null ||
	    theRelId == null ||
	    theRoleName == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isDebugOn()) {
	    String str = new String("theObjName " + theObjName.toString()
				    + ", theRelId " + theRelId
				    + ", theRoleName " + theRoleName
				    + ", theAllRolesFlg " + theAllRolesFlg);
	    debug("removeMBeanReference: entering", str);
	}

	boolean noLongerRefFlg = false;

	synchronized(myRefedMBeanObjName2RelIdsMap) {

	    // Retrieves the set of relations (designed via their relation ids)
	    // where the MBean is referenced
	    // Note that it is possible that the MBean has already been removed
	    // from the internal map: this is the case when the MBean is
	    // unregistered, the role is updated, then we arrive here.
	    HashMap mbeanRefMap = (HashMap)
		(myRefedMBeanObjName2RelIdsMap.get(theObjName));

	    if (mbeanRefMap == null) {
		// The MBean is no longer referenced
		if (isDebugOn())
		    debug("removeMBeanReference: exiting", null);
		return true;
	    }

	    ArrayList roleNames = new ArrayList();
	    if (!theAllRolesFlg) {
		// Now retrieves the roles of current relation where the MBean
		// was referenced
		roleNames = (ArrayList)(mbeanRefMap.get(theRelId));

		// Removes obsolete reference to role
		int obsRefIdx = roleNames.indexOf(theRoleName);
		if (obsRefIdx != -1) {
		    roleNames.remove(obsRefIdx);
		}
	    }

	    // Checks if there is still at least one role in current relation
	    // where the MBean is referenced
	    if (roleNames.isEmpty() || theAllRolesFlg) {
		// MBean no longer referenced in current relation: removes
		// entry
		mbeanRefMap.remove(theRelId);
	    }

	    // Checks if the MBean is still referenced in at least on relation
	    if (mbeanRefMap.isEmpty()) {
		// MBean no longer referenced in any relation: removes entry
		myRefedMBeanObjName2RelIdsMap.remove(theObjName);
		noLongerRefFlg = true;
	    }
	}

	if (isDebugOn())
	    debug("removeMBeanReference: exiting", null);
	return noLongerRefFlg;
    }

    // Updates the listener registered to the MBean Server to be informed of
    // referenced MBean unregistrations
    //
    // -param theNewRefList  ArrayList of ObjectNames for new references done
    //  to MBeans (can be null)
    // -param theObsRefList  ArrayList of ObjectNames for obsolete references
    //  to MBeans (can be null)
    //
    // -exception RelationServiceNotRegisteredException  if the Relation
    //  Service is not registered in the MBean Server.
    private void updateUnregistrationListener(List theNewRefList,
					      List theObsRefList)
	throws RelationServiceNotRegisteredException {

	if (theNewRefList != null && theObsRefList != null) {
	    if (theNewRefList.isEmpty() && theObsRefList.isEmpty()) {
		// Nothing to do :)
		return;
	    }
	}

	if (isDebugOn()) {
	    StringBuffer strB = new StringBuffer();
	    if (theNewRefList != null) {
		strB.append("theNewRefList " + theNewRefList.toString());
	    }
	    if (theObsRefList != null) {
		strB.append(", theObsRefList" + theObsRefList.toString());
	    }
	    debug("updateUnregistrationListener: entering", strB.toString());
	}

	// Can throw RelationServiceNotRegisteredException
	isActive();

	if (theNewRefList != null || theObsRefList != null) {

	    boolean newListenerFlg = false;
	    if (myUnregNtfFilter == null) {
		// Initialise it to be able to synchronise it :)
		myUnregNtfFilter = new MBeanServerNotificationFilter();
		newListenerFlg = true;
	    }

	    synchronized(myUnregNtfFilter) {

		// Enables ObjectNames in theNewRefList
		if (theNewRefList != null) {
		    for (Iterator newRefIter = theNewRefList.iterator();
			 newRefIter.hasNext();) {

			ObjectName newObjName = (ObjectName)
			    (newRefIter.next());
			myUnregNtfFilter.enableObjectName(newObjName);
		    }
		}

		if (theObsRefList != null) {
		    // Disables ObjectNames in theObsRefList
		    for (Iterator obsRefIter = theObsRefList.iterator();
			 obsRefIter.hasNext();) {

			ObjectName obsObjName = (ObjectName)
			    (obsRefIter.next());
			myUnregNtfFilter.disableObjectName(obsObjName);
		    }
		}

		// Creates ObjectName of the MBeanServerDelegate handling the
		// notifications
		ObjectName mbeanServerDelegateName = null;
		try {
		    mbeanServerDelegateName =
			new ObjectName(ServiceName.DELEGATE);
		} catch (MalformedObjectNameException exc) {
		    // OK : Should never happen...
		}

// Under test
		if (newListenerFlg) {
		    try {
			myMBeanServer.addNotificationListener(
						       mbeanServerDelegateName,
						       this,
						       myUnregNtfFilter,
						       null);
		    } catch (InstanceNotFoundException exc) {
			throw new
		       RelationServiceNotRegisteredException(exc.getMessage());
		    }
		}
// End test


//		if (!newListenerFlg) {
		    // The Relation Service was already registered as a
		    // listener:
		    // removes it
		    // Shall not throw InstanceNotFoundException (as the
		    // MBean Server Delegate is expected to exist) or
		    // ListenerNotFoundException (as it has been checked above
		    // that the Relation Service is registered)
//		    try {
//			myMBeanServer.removeNotificationListener(
//						      mbeanServerDelegateName,
//						      this);
//		    } catch (InstanceNotFoundException exc1) {
//			throw new RuntimeException(exc1.getMessage());
//		    } catch (ListenerNotFoundException exc2) {
//			throw new
//			    RelationServiceNotRegisteredException(exc2.getMessage());
//		    }
//		}

		// Adds Relation Service with current filter
		// Can throw InstanceNotFoundException if the Relation
		// Service is not registered, to be transformed into
		// RelationServiceNotRegisteredException
		//
		// Assume that there will not be any InstanceNotFoundException
		// for the MBean Server Delegate :)
//		try {
//		    myMBeanServer.addNotificationListener(
//						       mbeanServerDelegateName,
//						       this,
//						       myUnregNtfFilter,
//						       null);
//		} catch (InstanceNotFoundException exc) {
//		    throw new
//		       RelationServiceNotRegisteredException(exc.getMessage());
//		}
	    }
	}

	if (isDebugOn())
	    debug("updateUnregistrationListener: exiting", null);
	return;
    }	    

    // Adds a relation (being either a RelationSupport object or an MBean
    // referenced using its ObjectName) in the Relation Service.
    // Will send a notification RelationNotification with type:
    // - RelationNotification.RELATION_BASIC_CREATION for internal relation
    //   creation
    // - RelationNotification.RELATION_MBEAN_CREATION for an MBean being added
    //   as a relation.
    //
    // -param theRelBaseFlg  flag true if the relation is a RelationSupport
    //  object, false if it is an MBean
    // -param theRelObj  RelationSupport object (if relation is internal)
    // -param theRelObjName  ObjectName of the MBean to be added as a relation
    //  (only for the relation MBean)
    // -param theRelId  relation identifier, to uniquely identify the relation
    //  inside the Relation Service
    // -param theRelTypeName  name of the relation type (has to be created
    //  in the Relation Service)
    // -param theRoleList  role list to initialize roles of the relation
    //  (can be null)
    //
    // -exception IllegalArgumentException  if null paramater
    // -exception RelationServiceNotRegisteredException  if the Relation
    //  Service is not registered in the MBean Server
    // -exception RoleNotFoundException  if a value is provided for a role
    //  that does not exist in the relation type
    // -exception InvalidRelationIdException  if relation id already used
    // -exception RelationTypeNotFoundException  if relation type not known in
    //  Relation Service
    // -exception InvalidRoleValueException if:
    //  - the same role name is used for two different roles
    //  - the number of referenced MBeans in given value is less than
    //    expected minimum degree
    //  - the number of referenced MBeans in provided value exceeds expected
    //    maximum degree
    //  - one referenced MBean in the value is not an Object of the MBean
    //    class expected for that role
    //  - an MBean provided for that role does not exist
    private void addRelationInt(boolean theRelBaseFlg,
				RelationSupport theRelObj,
				ObjectName theRelObjName,
				String theRelId,
				String theRelTypeName,
				RoleList theRoleList)
	throws IllegalArgumentException,
	       RelationServiceNotRegisteredException,
               RoleNotFoundException,
               InvalidRelationIdException,
               RelationTypeNotFoundException,
               InvalidRoleValueException {

	if (theRelId == null ||
	    theRelTypeName == null ||
	    (theRelBaseFlg &&
	     (theRelObj == null ||
	      theRelObjName != null)) ||
	    (!theRelBaseFlg &&
	     (theRelObjName == null ||
	      theRelObj != null))) {
	    // Revisit [cebro[ Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isDebugOn()) {
	    StringBuffer strB = new StringBuffer("theRelBaseFlg "
						 + theRelBaseFlg
						 + ", theRelId " + theRelId
						 + ", theRelTypeName "
						 + theRelTypeName);
	    if (theRelObjName != null) {
		strB.append(",  theRelObjName " + theRelObjName.toString());
	    }
	    if (theRoleList != null) {
		strB.append(", theRoleList " + theRoleList.toString());
	    }
	    debug("addRelationInt: entering", strB.toString());
	}

	// Can throw RelationServiceNotRegisteredException
	isActive();

	// Checks if there is already a relation with given id
	try {
	    // Can throw a RelationNotFoundException (in fact should :)
	    Object rel = getRelation(theRelId);

	    if (rel != null) {
		// There is already a relation with that id
		// Revisit [cebro] Localize message
		String excMsg = "There is already a relation with id ";
		StringBuffer excMsgStrB = new StringBuffer(excMsg);
		excMsgStrB.append(theRelId);
		throw new InvalidRelationIdException(excMsgStrB.toString());
	    }
	} catch (RelationNotFoundException exc) {
	    // OK : The Relation could not be found.
	}

	// Retrieves the relation type
	// Can throw RelationTypeNotFoundException
	RelationType relType = getRelationType(theRelTypeName);

	// Checks that each provided role conforms to its role info provided in
	// the relation type
	// First retrieves a local list of the role infos of the relation type
	// to see which roles have not been initialized
	// Note: no need to test if list not null before cloning, not allowed
	//       to have an empty relation type.
	ArrayList roleInfoList = (ArrayList)
	    (((ArrayList)(relType.getRoleInfos())).clone());

	if (theRoleList != null) {

	    for (Iterator roleIter = theRoleList.iterator();
		 roleIter.hasNext();) {

		Role currRole = (Role)(roleIter.next());
		String currRoleName = currRole.getRoleName();
		ArrayList currRoleValue = (ArrayList)
		    (currRole.getRoleValue());
		// Retrieves corresponding role info
		// Can throw a RoleInfoNotFoundException to be converted into a
		// RoleNotFoundException
		RoleInfo roleInfo = null;
		try {
		    roleInfo = relType.getRoleInfo(currRoleName);
		} catch (RoleInfoNotFoundException exc) {
		    throw new RoleNotFoundException(exc.getMessage());
		}

		// Checks that role conforms to role info, 
		Integer status = checkRoleInt(2,
					      currRoleName,
					      currRoleValue,
					      roleInfo,
					      false);
		int pbType = status.intValue();
		if (pbType != 0) {
		    // A problem has occured: throws appropriate exception
		    // here InvalidRoleValueException
		    throwRoleProblemException(pbType, currRoleName);
		}

		// Removes role info for that list from list of role infos for
		// roles to be defaulted
		int roleInfoIdx = roleInfoList.indexOf(roleInfo);
		// Note: no need to check if != -1, MUST be there :)
		roleInfoList.remove(roleInfoIdx);
	    }
	}

	// Initializes roles not initialized by theRoleList
	// Can throw InvalidRoleValueException
	initialiseMissingRoles(theRelBaseFlg,
			       theRelObj,
			       theRelObjName,
			       theRelId,
			       theRelTypeName,
			       roleInfoList);

	// Creation of relation successfull!!!!

	// Updates internal maps
	// Relation id to object map
	synchronized(myRelId2ObjMap) {
	    if (theRelBaseFlg) {
		// Note: do not clone relation object, created by us :)
		myRelId2ObjMap.put(theRelId, theRelObj);
	    } else {
		myRelId2ObjMap.put(theRelId, theRelObjName);
	    }
	}

	// Relation id to relation type name map
	synchronized(myRelId2RelTypeMap) {
	    myRelId2RelTypeMap.put(theRelId,
				   theRelTypeName);
	}

	// Relation type to relation id map
	synchronized(myRelType2RelIdsMap) {
	    ArrayList relIdList = (ArrayList)
		(myRelType2RelIdsMap.get(theRelTypeName));
	    boolean firstRelFlg = false;
	    if (relIdList == null) {
		firstRelFlg = true;
		relIdList = new ArrayList();
	    }
	    relIdList.add(theRelId);
	    if (firstRelFlg) {
		myRelType2RelIdsMap.put(theRelTypeName, relIdList);
	    }
	}

	// Referenced MBean to relation id map
	// Only role list parameter used, as default initialization of roles
	// done automatically in initialiseMissingRoles() sets each
	// uninitialized role to an empty value.
	for (Iterator roleIter = theRoleList.iterator();
	     roleIter.hasNext();) {
	    Role currRole = (Role)(roleIter.next());
	    // Creates a dummy empty ArrayList of ObjectNames to be the old
	    // role value :)
	    ArrayList dummyList = new ArrayList();
	    // Will not throw a RelationNotFoundException (as the RelId2Obj map
	    // has been updated above) so catch it :)
	    try {
		updateRoleMap(theRelId, currRole, dummyList);

	    } catch (RelationNotFoundException exc) {
		// OK : The Relation could not be found.
	    }
	}

	// Sends a notification for relation creation
	// Will not throw RelationNotFoundException so catch it :)
	try {
	    sendRelationCreationNotification(theRelId);

	} catch (RelationNotFoundException exc) {
	    // OK : The Relation could not be found.
	}

	if (isDebugOn())
	    debug("addRelationInt: exiting", null);
	return;
    }

    // Checks that given role conforms to given role info.
    //
    // -param theChkType  type of check:
    //  - 1: read, just check read access
    //  - 2: write, check value and write access if theWriteChkFlg
    // -param theRoleName  role name
    // -param theRoleValue  role value
    // -param theRoleInfo  corresponding role info
    // -param theWriteChkFlg  boolean to specify a current write access and
    //  to check it
    //
    // -return Integer with value:
    //  - 0: ok
    //  - RoleStatus.NO_ROLE_WITH_NAME
    //  - RoleStatus.ROLE_NOT_READABLE
    //  - RoleStatus.ROLE_NOT_WRITABLE
    //  - RoleStatus.LESS_THAN_MIN_ROLE_DEGREE
    //  - RoleStatus.MORE_THAN_MAX_ROLE_DEGREE
    //  - RoleStatus.REF_MBEAN_OF_INCORRECT_CLASS
    //  - RoleStatus.REF_MBEAN_NOT_REGISTERED
    //
    // -exception IllegalArgumentException  if null parameter
    private Integer checkRoleInt(int theChkType,
				 String theRoleName,
				 List theRoleValue,
				 RoleInfo theRoleInfo,
				 boolean theWriteChkFlg)
	throws IllegalArgumentException {

	if (theRoleName == null ||
	    theRoleInfo == null ||
	    (theChkType == 2 && theRoleValue == null)) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isDebugOn()) {
	    StringBuffer strB = new StringBuffer("theChkType "
						 + theChkType
						 + ", theRoleName "
						 + theRoleName
						 + ", theRoleInfo "
						 + theRoleInfo.toString()
						 + ", theWriteChkFlg "
						 + theWriteChkFlg);
	    if (theRoleValue != null) {
		strB.append(", theRoleValue " + theRoleValue.toString());
	    }
	    debug("checkRoleInt: entering", strB.toString());
	}

	// Compares names
	String expName = theRoleInfo.getName();
	if (!(theRoleName.equals(expName))) {
	    if (isDebugOn())
		debug("checkRoleInt: exiting", null);
	    return new Integer(RoleStatus.NO_ROLE_WITH_NAME);
	}

	// Checks read access if required
	if (theChkType == 1) {
	    boolean isReadable = theRoleInfo.isReadable();
	    if (!isReadable) {
		if (isDebugOn())
		    debug("checkRoleInt: exiting", null);
		return new Integer(RoleStatus.ROLE_NOT_READABLE);
	    } else {
		// End of check :)
		if (isDebugOn())
		    debug("checkRoleInt: exiting", null);
		return new Integer(0);
	    }
	}

	// Checks write access if required
	if (theWriteChkFlg) {
	    boolean isWritable = theRoleInfo.isWritable();
	    if (!isWritable) {
		if (isDebugOn())
		    debug("checkRoleInt: exiting", null);
		return new Integer(RoleStatus.ROLE_NOT_WRITABLE);
	    }
	}

	int refNbr = theRoleValue.size();

	// Checks minimum cardinality
	boolean chkMinFlg = theRoleInfo.checkMinDegree(refNbr);
	if (!chkMinFlg) {
	    if (isDebugOn())
		debug("checkRoleInt: exiting", null);
	    return new Integer(RoleStatus.LESS_THAN_MIN_ROLE_DEGREE);
	}

	// Checks maximum cardinality
	boolean chkMaxFlg = theRoleInfo.checkMaxDegree(refNbr);
	if (!chkMaxFlg) {
	    if (isDebugOn())
		debug("checkRoleInt: exiting", null);
	    return new Integer(RoleStatus.MORE_THAN_MAX_ROLE_DEGREE);
	}
	
	// Verifies that each referenced MBean is registered in the MBean
	// Server and that it is an instance of the class specified in the
	// role info, or of a subclass of it
	// Note that here again this is under the assumption that
	// referenced MBeans, relation MBeans and the Relation Service are
	// registered in the same MBean Server.
	String expClassName = theRoleInfo.getRefMBeanClassName();

	for (Iterator refMBeanIter = theRoleValue.iterator();
	     refMBeanIter.hasNext();) {
	    ObjectName currObjName = (ObjectName)(refMBeanIter.next());

	    // Checks it is registered
	    if (currObjName == null) {
		if (isDebugOn())
		    debug("checkRoleInt: exiting", null);
		return new Integer(RoleStatus.REF_MBEAN_NOT_REGISTERED);
	    }

	    // Checks if it is of the correct class
	    // Can throw an InstanceNotFoundException, if MBean not registered
	    try {
		boolean classSts = myMBeanServer.isInstanceOf(currObjName,
							      expClassName);
		if (!classSts) {
		    if (isDebugOn())
			debug("checkRoleInt: exiting", null);
		    return new Integer(RoleStatus.REF_MBEAN_OF_INCORRECT_CLASS);
		}

	    } catch (InstanceNotFoundException exc) {
		if (isDebugOn())
		    debug("checkRoleInt: exiting", null);
		return new Integer(RoleStatus.REF_MBEAN_NOT_REGISTERED);
	    }
	}

	if (isDebugOn())
	    debug("checkRoleInt: exiting", null);
	return new Integer(0);
    }
	

    // Initialises roles associated to given role infos to default value (empty
    // ArrayList of ObjectNames) in given relation.
    // It will succeed for every role except if the role info has a minimum
    // cardinality greater than 0. In that case, an InvalidRoleValueException
    // will be raised.
    //
    // -param theRelBaseFlg  flag true if the relation is a RelationSupport
    //  object, false if it is an MBean
    // -param theRelObj  RelationSupport object (if relation is internal)
    // -param theRelObjName  ObjectName of the MBean to be added as a relation
    //  (only for the relation MBean)
    // -param theRelId  relation id
    // -param theRelTypeName  name of the relation type (has to be created
    //  in the Relation Service)
    // -param theRoleInfoList  list of role infos for roles to be defaulted
    //
    // -exception IllegalArgumentException  if null paramater
    // -exception RelationServiceNotRegisteredException  if the Relation
    //  Service is not registered in the MBean Server
    // -exception InvalidRoleValueException  if role must have a non-empty
    //  value

    // Revisit [cebro] Handle CIM qualifiers as REQUIRED to detect roles which
    //    should have been initialized by the user
    private void initialiseMissingRoles(boolean theRelBaseFlg,
					RelationSupport theRelObj,
					ObjectName theRelObjName,
					String theRelId,
					String theRelTypeName,
					List theRoleInfoList)
	throws IllegalArgumentException,
	       RelationServiceNotRegisteredException,
	       InvalidRoleValueException {

	if ((theRelBaseFlg &&
	     (theRelObj == null ||
	      theRelObjName != null)) ||
	    (!theRelBaseFlg &&
	     (theRelObjName == null ||
	      theRelObj != null)) ||
	    theRelId == null ||
	    theRelTypeName == null ||
	    theRoleInfoList == null) {
	    // Revisit [cebro[ Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isDebugOn()) {
	    StringBuffer strB =
		new StringBuffer("theRelBaseFlg " + theRelBaseFlg
				 + ", theRelId " + theRelId
				 + ", theRelTypeName " + theRelTypeName
				 + ", theRoleInfoList " + theRoleInfoList);
	    if (theRelObjName != null) {
		strB.append(theRelObjName.toString());
	    }
	    debug("initialiseMissingRoles: entering", strB.toString());
	}

	// Can throw RelationServiceNotRegisteredException
	isActive();

	// For each role info (corresponding to a role not initialized by the
	// role list provided by the user), try to set in the relation a role
	// with an empty list of ObjectNames.
	// A check is performed to verify that the role can be set to an
	// empty value, according to its minimum cardinality
	for (Iterator roleInfoIter = theRoleInfoList.iterator();
	     roleInfoIter.hasNext();) {

	    RoleInfo currRoleInfo = (RoleInfo)(roleInfoIter.next());
	    String roleName = currRoleInfo.getName();

	    // Creates an empty value
	    ArrayList emptyValue = new ArrayList();
	    // Creates a role
	    Role role = new Role(roleName, emptyValue);

	    if (theRelBaseFlg) {

		// Internal relation
		// Can throw InvalidRoleValueException
		//
		// Will not throw RoleNotFoundException (role to be
		// initialized), or RelationNotFoundException, or
		// RelationTypeNotFoundException
		try {
		    theRelObj.setRoleInt(role, true, this, false);

		} catch (RoleNotFoundException exc1) {
		    throw new RuntimeException(exc1.getMessage());
		} catch (RelationNotFoundException exc2) {
		    throw new RuntimeException(exc2.getMessage());
		} catch (RelationTypeNotFoundException exc3) {
		    throw new RuntimeException(exc3.getMessage());    
		}

	    } else {

		// Relation is an MBean
		// Use standard setRole()
		Object[] params = new Object[1];
		params[0] = role;
		String[] signature = new String[1];
		signature[0] = "javax.management.relation.Role";
		// Can throw MBeanException wrapping
		// InvalidRoleValueException. Returns the target exception to
		// be homogeneous.
		//
		// Will not throw MBeanException (wrapping
		// RoleNotFoundException or MBeanException) or
		// InstanceNotFoundException, or ReflectionException
		//
		// Again here the assumption is that the Relation Service and
		// the relation MBeans are registered in the same MBean Server.
		try {
		    myMBeanServer.setAttribute(theRelObjName,
                                               new Attribute("Role", role));

		} catch (InstanceNotFoundException exc1) {
		    throw new RuntimeException(exc1.getMessage());
		} catch (ReflectionException exc3) {
		    throw new RuntimeException(exc3.getMessage());
		} catch (MBeanException exc2) {
		    Exception wrappedExc = exc2.getTargetException();
		    if (wrappedExc instanceof InvalidRoleValueException) {
			throw ((InvalidRoleValueException)wrappedExc);
		    } else {
			throw new RuntimeException(wrappedExc.getMessage());
		    }
		} catch (AttributeNotFoundException exc4) {
                  throw new RuntimeException(exc4.getMessage());
                } catch (InvalidAttributeValueException exc5) {
                  throw new RuntimeException(exc5.getMessage());
                }
	    }
	}

	if (isDebugOn())
	    debug("initializeMissingRoles: exiting", null);
	return;
    }

    // Throws an exception corresponding to a given problem type
    //
    // -param thePbType  possible problem, defined in RoleUnresolved
    // -param theRoleName  role name
    //
    // -exception IllegalArgumentException  if null parameter
    // -exception RoleNotFoundException  for problems:
    //  - NO_ROLE_WITH_NAME
    //  - ROLE_NOT_READABLE
    //  - ROLE_NOT_WRITABLE
    // -exception InvalidRoleValueException  for problems:
    //  - LESS_THAN_MIN_ROLE_DEGREE
    //  - MORE_THAN_MAX_ROLE_DEGREE
    //  - REF_MBEAN_OF_INCORRECT_CLASS
    //  - REF_MBEAN_NOT_REGISTERED
    static void throwRoleProblemException(int thePbType,
					  String theRoleName)
	throws IllegalArgumentException,
	       RoleNotFoundException,
	       InvalidRoleValueException {

	if (theRoleName == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	// Exception type: 1 = RoleNotFoundException
	//                 2 = InvalidRoleValueException
	int excType = 0;

	String excMsgPart = null;

	// Revisit [cebro] Localize messages
	switch (thePbType) {
	case RoleStatus.NO_ROLE_WITH_NAME:
	    excMsgPart = " does not exist in relation.";
	    excType = 1;
	    break;
	case RoleStatus.ROLE_NOT_READABLE:
	    excMsgPart = " is not readable.";
	    excType = 1;
	    break;
	case RoleStatus.ROLE_NOT_WRITABLE:
	    excMsgPart = " is not writable.";
	    excType = 1;
	    break;
	case RoleStatus.LESS_THAN_MIN_ROLE_DEGREE:
	    excMsgPart = " has a number of MBean references less than the expected minimum degree.";
	    excType = 2;
	    break;
	case RoleStatus.MORE_THAN_MAX_ROLE_DEGREE:
	    excMsgPart = " has a number of MBean references greater than the expected maximum degree.";
	    excType = 2;
	    break;
	case RoleStatus.REF_MBEAN_OF_INCORRECT_CLASS:
	    excMsgPart = " has an MBean reference to an MBean not of the expected class of references for that role.";
	    excType = 2;
	    break;
	case RoleStatus.REF_MBEAN_NOT_REGISTERED:
	    excMsgPart = " has a reference to null or to an MBean not registered.";
	    excType = 2;
	    break;
	}
	// No default as we must have been in one of those cases

	StringBuffer excMsgStrB = new StringBuffer(theRoleName);
	excMsgStrB.append(excMsgPart);
	String excMsg = excMsgStrB.toString();
	if (excType == 1) {
	    throw new RoleNotFoundException(excMsg);

	} else if (excType == 2) {
	    throw new InvalidRoleValueException(excMsg);
	}
    }

    // Sends a notification of given type, with given parameters
    //
    // -param theIntNtfType  integer to represent notification type:
    //  - 1 : create
    //  - 2 : update
    //  - 3 : delete
    // -param theMsg  human-readable message
    // -param theRelId  relation id of the created/updated/deleted relation
    // -param theUnregMBeanList  list of ObjectNames of referenced MBeans
    //  expected to be unregistered due to relation removal (only for removal,
    //  due to CIM qualifiers, can be null)
    // -param theRoleName  role name
    // -param theRoleNewValue  role new value (ArrayList of ObjectNames)
    // -param theOldRoleValue  old role value (ArrayList of ObjectNames)
    //
    // -exception IllegalArgument  if null parameter
    // -exception RelationNotFoundException  if no relation for given id
    private void sendNotificationInt(int theIntNtfType,
				     String theMsg,
				     String theRelId,
				     List theUnregMBeanList,
				     String theRoleName,
				     List theRoleNewValue,
				     List theOldRoleValue)
	throws IllegalArgumentException,
	       RelationNotFoundException {

	if (theMsg == null ||
	    theRelId == null ||
	    (theIntNtfType != 3 && theUnregMBeanList != null) ||
	    (theIntNtfType == 2 &&
	     (theRoleName == null ||
	      theRoleNewValue == null ||
	      theOldRoleValue == null))) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isDebugOn()) {
	    StringBuffer strB =
		new StringBuffer("theIntNtfType " + theIntNtfType
				 + ", theMsg " + theMsg
				 + ", theRelId " + theRelId);
	    if (theUnregMBeanList != null) {
		strB.append(", theUnregMBeanList " +
			    theUnregMBeanList.toString());
	    }
	    if (theRoleName != null) {
		strB.append(", theRoleName " + theRoleName);
	    }
	    if (theRoleNewValue != null) {
		strB.append(", theRoleNewValue " + theRoleNewValue.toString());
	    }
	    if (theOldRoleValue != null) {
		strB.append(", theOldRoleValue " + theOldRoleValue.toString());
	    }
	    debug("sendNotificationInt: entering", strB.toString());
	}

	// Relation type name
	// Note: do not use getRelationTypeName() as if it is a relation MBean
        //       it is already unregistered. 
	String relTypeName = null;
	synchronized(myRelId2RelTypeMap) {
	    relTypeName = (String)(myRelId2RelTypeMap.get(theRelId));
	}

	// ObjectName (for a relation MBean)
	// Can also throw a RelationNotFoundException, but detected above
	ObjectName relObjName = isRelationMBean(theRelId);

	String ntfType = null;
	if (relObjName != null) {
	    switch (theIntNtfType) {
	    case 1:
		ntfType = RelationNotification.RELATION_MBEAN_CREATION;
		break;
	    case 2:
		ntfType = RelationNotification.RELATION_MBEAN_UPDATE;
		break;
	    case 3:
		ntfType = RelationNotification.RELATION_MBEAN_REMOVAL;
		break;
	    }
	} else {
	    switch (theIntNtfType) {
	    case 1:
		ntfType = RelationNotification.RELATION_BASIC_CREATION;
		break;
	    case 2:
		ntfType = RelationNotification.RELATION_BASIC_UPDATE;
		break;
	    case 3:
		ntfType = RelationNotification.RELATION_BASIC_REMOVAL;
		break;
	    }
	}

	// Sequence number
	Long seqNbr = getNotificationSequenceNumber();

	// Timestamp
	Date currDate = new Date();
	long timeStamp = currDate.getTime();

	RelationNotification ntf = null;

	if (ntfType.equals(RelationNotification.RELATION_BASIC_CREATION) ||
	    ntfType.equals(RelationNotification.RELATION_MBEAN_CREATION) ||
	    ntfType.equals(RelationNotification.RELATION_BASIC_REMOVAL) ||
	    ntfType.equals(RelationNotification.RELATION_MBEAN_REMOVAL))

	    // Creation or removal
	    ntf = new RelationNotification(ntfType,
					   this,
					   seqNbr.longValue(),
					   timeStamp,
					   theMsg,
					   theRelId,
					   relTypeName,
					   relObjName,
					   theUnregMBeanList);

	else if (ntfType.equals(RelationNotification.RELATION_BASIC_UPDATE)
		 ||
		 ntfType.equals(RelationNotification.RELATION_MBEAN_UPDATE))
	    {
		// Update
		ntf = new RelationNotification(ntfType,
					       this,
					       seqNbr.longValue(),
					       timeStamp,
					       theMsg,
					       theRelId,
					       relTypeName,
					       relObjName,
					       theRoleName,
					       theRoleNewValue,
					       theOldRoleValue);
	    }

	sendNotification(ntf);

	if (isDebugOn())
	    debug("sendNotificationInt: exiting", null);
	return;
    }

    // Checks, for the unregistration of an MBean referenced in the roles given
    // in parameter, if the relation has to be removed or not, regarding
    // expected minimum role cardinality and current number of
    // references in each role after removal of the current one.
    // If the relation is kept, calls handleMBeanUnregistration() callback of
    // the relation to update it.
    //
    // -param theRelId  relation id
    // -param theObjName  ObjectName of the unregistered MBean
    // -param theRoleNameList  list of names of roles where the unregistered
    //  MBean is referenced.
    //
    // -exception IllegalArgumentException  if null parameter
    // -exception RelationServiceNotRegisteredException  if the Relation
    //  Service is not registered in the MBean Server
    // -exception RelationNotFoundException  if unknown relation id
    // -exception RoleNotFoundException  if one role given as parameter does
    //  not exist in the relation
    private void handleReferenceUnregistration(String theRelId,
					       ObjectName theObjName,
					       List theRoleNameList)
	throws IllegalArgumentException,
	       RelationServiceNotRegisteredException,
               RelationNotFoundException,
	       RoleNotFoundException {

	if (theRelId == null ||
	    theRoleNameList == null ||
	    theObjName == null) {
	    // Revisit [cebro[ Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isDebugOn()) {
	    String str =
		new String("theRelId " + theRelId
			   + ", theRoleNameList " + theRoleNameList.toString()
			   + "theObjName " + theObjName.toString());
	    debug("handleReferenceUnregistration: entering", str);
	}

	// Can throw RelationServiceNotRegisteredException
	isActive();

	// Retrieves the relation type name of the relation
	// Can throw RelationNotFoundException
	String currRelTypeName = getRelationTypeName(theRelId);

	// Retrieves the relation
	// Can throw RelationNotFoundException, but already detected above
	Object relObj = getRelation(theRelId);

	// Flag to specify if the relation has to be deleted
	boolean deleteRelFlg = false;

	for (Iterator roleNameIter = theRoleNameList.iterator();
	     roleNameIter.hasNext();) {

	    if (deleteRelFlg) {
		break;
	    }

	    String currRoleName = (String)(roleNameIter.next());
	    // Retrieves number of MBeans currently referenced in role
	    // BEWARE! Do not use getRole() as role may be not readable
	    //
	    // Can throw RelationNotFoundException (but already checked),
	    // RoleNotFoundException
	    int currRoleRefNbr =
		(getRoleCardinality(theRelId, currRoleName)).intValue();

	    // Retrieves new number of element in role
	    int currRoleNewRefNbr = currRoleRefNbr - 1;

	    // Retrieves role info for that role
	    //
	    // Shall not throw RelationTypeNotFoundException or
	    // RoleInfoNotFoundException
	    RoleInfo currRoleInfo = null;
	    try {
		currRoleInfo = getRoleInfo(currRelTypeName,
					   currRoleName);
	    } catch (RelationTypeNotFoundException exc1) {
		throw new RuntimeException(exc1.getMessage());
	    } catch (RoleInfoNotFoundException exc2) {
		throw new RuntimeException(exc2.getMessage());
	    }

	    // Checks with expected minimum number of elements
	    boolean chkMinFlg = currRoleInfo.checkMinDegree(currRoleNewRefNbr);

	    if (!chkMinFlg) {
		// The relation has to be deleted
		deleteRelFlg = true;
	    }
	}

	if (deleteRelFlg) {
	    // Removes the relation
	    removeRelation(theRelId);

	} else {

	    // Updates each role in the relation using
	    // handleMBeanUnregistration() callback
            //
            // BEWARE: this theRoleNameList list MUST BE A COPY of a role name
            //         list for a referenced MBean in a relation, NOT a
            //         reference to an original one part of the
            //         myRefedMBeanObjName2RelIdsMap!!!! Because each role
            //         which name is in that list will be updated (potentially
            //         using setRole(). So the Relation Service will update the
            //         myRefedMBeanObjName2RelIdsMap to refelect the new role
            //         value! 
	    for (Iterator roleNameIter = theRoleNameList.iterator();
		 roleNameIter.hasNext();) {

		String currRoleName = (String)(roleNameIter.next());

		if (relObj instanceof RelationSupport) {
		    // Internal relation
		    // Can throw RoleNotFoundException (but already checked)
		    //
		    // Shall not throw
		    // RelationTypeNotFoundException,
		    // InvalidRoleValueException (value was correct, removing
		    // one reference shall not invalidate it, else detected
		    // above)
		    try {
			((RelationSupport)relObj).handleMBeanUnregistrationInt(
						  theObjName,
						  currRoleName,
						  true,
						  this);
		    } catch (RelationTypeNotFoundException exc3) {
			throw new RuntimeException(exc3.getMessage());
		    } catch (InvalidRoleValueException exc4) {
			throw new RuntimeException(exc4.getMessage());
		    }

		} else {
		    // Relation MBean
		    Object[] params = new Object[2];
		    params[0] = theObjName;
		    params[1] = currRoleName;
		    String[] signature = new String[2];
		    signature[0] = "javax.management.ObjectName";
		    signature[1] = "java.lang.String";
		    // Shall not throw InstanceNotFoundException, or
		    // MBeanException (wrapping RoleNotFoundException or
		    // MBeanException or InvalidRoleValueException) or
		    // ReflectionException
		    try {
			myMBeanServer.invoke(((ObjectName)relObj),
					     "handleMBeanUnregistration",
					     params,
					     signature);
		    } catch (InstanceNotFoundException exc1) {
			throw new RuntimeException(exc1.getMessage());
		    } catch (ReflectionException exc3) {
			throw new RuntimeException(exc3.getMessage());
		    } catch (MBeanException exc2) {
			Exception wrappedExc = exc2.getTargetException();
			throw new RuntimeException(wrappedExc.getMessage());
		    }

		}
	    }
	}

	if (isDebugOn())
	    debug("handleReferenceUnregistration: exiting", null);
	return;
    }

    // stuff for Tracing

    private static String localClassName = "RelationService";

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
}
