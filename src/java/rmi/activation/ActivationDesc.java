/*
 * @(#)ActivationDesc.java	1.19 98/08/10
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.rmi.activation;

import java.rmi.MarshalledObject;

/**
 * An activation descriptor contains the information necessary to
 * activate an object: <ul>
 * <li> the object's group identifier,
 * <li> the object's class name,
 * <li> the object's code location (the location of the class), and
 * <li> a "marshalled" object that can contain object specific
 * initialization data. </ul> <p>
 *
 * A descriptor registered with the activation system can be used to
 * recreate/activate the object specified by the descriptor. The
 * <code>MarshalledObject</code> in the object's descriptor is passed
 * as the second argument to the remote object's constructor for
 * object to use during reinitialization/activation.
 *
 * @author 	Ann Wollrath
 * @version	1.19, 08/10/98
 * @since 	JDK1.2
 * @see         java.rmi.activation.Activatable
 */
public final class ActivationDesc implements java.io.Serializable
{
    /**
     * @serial the group's identifier 
     */
    private ActivationGroupID groupID;

    /**
     * @serial the object's class name 
     */
    private String className;
    
    /**
     * @serial the object's code location
     */
    private String location;

    /** 
     * @serial the object's initialization data 
     */
    private MarshalledObject data;

    /**
     * @serial indicates whether the object should be restarted
     */
    private boolean restart;

    /** indicate compatibility with JDK 1.2 version of class */
    private static final long serialVersionUID = 7455834104417690957L;

    /**
     * Constructs an object descriptor for an object whose class name
     * is <code>className</code>, that can be loaded from the
     * code <code>location</code> and whose initialization
     * information is <code>data</code>. If this form of the constructor
     * is used, the <code>groupID</code> defaults to the current id for
     * <code>ActivationGroup</code> for this VM. All objects with the
     * same <code>ActivationGroupID</code> are activated in the same VM.
     * Objects specified by a descriptor created by this constructor
     * will not restart automatically when the RMI activation daemon
     * starts, but will be activated on demand (via a method call to
     * the activatable object).<p>
     *
     * This constructor will throw <code>ActivationException</code> if there is
     * no current activation group for this VM.  To create an
     * <code>ActivationGroup</code> use the
     * <code>ActivationGroup.createGroup</code> method.
     *
     * @param className the object's fully package qualified class name
     * @param location the object's code location (from where the class is
     * loaded)
     * @param data the object's initialization (activation) data contained
     * in marshalled form.
     * @exception ActivationException if the current group is nonexistent
     * @since JDK1.2
     */
    public ActivationDesc(String className,
			  String location, 
			  MarshalledObject data)
	throws ActivationException
    {
	this(ActivationGroup.internalCurrentGroupID(),
	     className, location, data, false);
    }
    
    /**
     * Constructs an object descriptor for an object whose class name
     * is <code>className</code>, that can be loaded from the
     * code <code>location</code> and whose initialization
     * information is <code>data</code>. If this form of the constructor
     * is used, the <code>groupID</code> defaults to the current id for
     * <code>ActivationGroup</code> for this VM. All objects with the
     * same <code>ActivationGroupID</code> are activated in the same VM.<p>
     *
     * This constructor will throw <code>ActivationException</code> if there is
     * no current activation group for this VM.  To create an
     * <code>ActivationGroup</code> use the
     * <code>ActivationGroup.createGroup</code> method.
     *
     * @param className the object's fully package qualified class name
     * @param location the object's code location (from where the class is
     * loaded)
     * @param data the object's initialization (activation) data contained
     * in marshalled form.
     * @param restart if true, the object is restarted when the activator
     * is restarted; if false, the object is activated on demand.
     * @exception ActivationException if the current group is nonexistent
     * @since JDK1.2
     */
    public ActivationDesc(String className,
			  String location, 
			  MarshalledObject data,
			  boolean restart)
	throws ActivationException
    {
	this(ActivationGroup.internalCurrentGroupID(),
	     className, location, data, restart);
    }

    /**
     * Constructs an object descriptor for an object whose class name
     * is <code>className</code> that can be loaded from the
     * code <code>location</code> and whose initialization
     * information is <code>data</code>. All objects with the same
     * <code>groupID</code> are activated in the same Java VM.
     *
     * @param groupID the group's identifier (obtained from registering
     * <code>ActivationSystem.registerGroup</code> method). The group
     * indicates the VM in which the object should be activated.
     * @param className the object's fully package-qualified class name
     * @param location the object's code location (from where the class is
     * loaded)
     * @param data  the object's initialization (activation) data contained
     * in marshalled form.
     * @exception IllegalArgumentException if <code>groupID</code> is null
     * @since JDK1.2
     */
   public ActivationDesc(ActivationGroupID groupID,
			 String className, 
			 String location, 
			 MarshalledObject data)
    {
	this(groupID, className, location, data, false);
    }

    /**
     * Constructs an object descriptor for an object whose class name
     * is <code>className</code> that can be loaded from the
     * code <code>location</code> and whose initialization
     * information is <code>data</code>. All objects with the same
     * <code>groupID</code> are activated in the same Java VM.
     *
     * @param groupID the group's identifier (obtained from registering
     * <code>ActivationSystem.registerGroup</code> method). The group
     * indicates the VM in which the object should be activated.
     * @param className the object's fully package-qualified class name
     * @param location the object's code location (from where the class is
     * loaded)
     * @param data  the object's initialization (activation) data contained
     * in marshalled form.
     * @param restart if true, the object is restarted when the activator
     * is restarted; if false, the object is activated on demand.
     * @exception IllegalArgumentException if <code>groupID</code> is null
     * @since JDK1.2
     */
   public ActivationDesc(ActivationGroupID groupID,
			 String className, 
			 String location, 
			 MarshalledObject data,
			 boolean restart)
    {
	if (groupID == null)
	    throw new IllegalArgumentException("groupID can't be null");
	this.groupID = groupID;
	this.className = className;
	this.location = location;
	this.data = data;
	this.restart = restart;
    }
    
    /**
     * Returns the group identifier for the object specified by this
     * descriptor. A group provides a way to aggregate objects into a
     * single Java virtual machine. RMI creates/activates objects with
     * the same <code>groupID</code> in the same virtual machine.
     *
     * @return the group identifier
     * @since JDK1.2
     */
    public ActivationGroupID getGroupID() {
	return groupID;
    }
    
    /**
     * Returns the class name for the object specified by this
     * descriptor.
     * @return the class name
     * @since JDK1.2
     */
    public String getClassName() {
	return className;
    }
    
    /**
     * Returns the code location for the object specified by
     * this descriptor.
     * @return the code location
     * @since JDK1.2
     */
    public String getLocation() {
	return location;
    }
    
    /**
     * Returns a "marshalled object" containing intialization/activation
     * data for the object specified by this descriptor.
     * @return the object specific "initialization" data
     * @since JDK1.2
     */
    public MarshalledObject getData() {
	return data;
    }

    /**
     * Returns the "restart" mode of the object associated with
     * this activation descriptor.
     *
     * @return true if the activatable object associated with this
     * activation descriptor should be restarted via the activation
     * daemon when the daemon comes up; otherwise it returns false
     * (meaning that the object is only activated on demand via a
     * method call). 
     * @since JDK1.2
     */
    public boolean getRestartMode() {
	return restart;
    }

    /**
     * Compares two activation descriptors for content equality.
     *
     * @param	obj	the Object to compare with
     * @return	true if these Objects are equal; false otherwise.
     * @see		java.util.Hashtable
     * @since JDK1.2
     */
    public boolean equals(Object obj) {
	
	if (obj instanceof ActivationDesc) {
	    ActivationDesc desc = (ActivationDesc) obj;
	    return
		((groupID == null ? desc.groupID == null :
		  groupID.equals(desc.groupID)) &&
		 (className == null ? desc.className == null :
		  className.equals(desc.className)) &&
		 (location == null ? desc.location == null:
		  location.equals(desc.location)) &&
		 (data == null ? desc.data == null :
		  data.equals(desc.data)) &&
		 (restart == desc.restart));

	} else {
	    return false;
	}
    }

    /**
     * Return the same hashCode for similar <code>ActivationDesc</code>s.
     * @return an integer
     * @see java.util.Hashtable
     */
    public int hashCode()
    {
	return ((location == null
		    ? 0
		    : location.hashCode() << 24) ^
		(groupID == null
		    ? 0
		    : groupID.hashCode() << 16) ^
		(className == null
		    ? 0
		    : className.hashCode() << 9) ^
		(data == null
		    ? 0
		    : data.hashCode() << 1) ^
		(restart
		    ? 1
		    : 0));
    }
}

