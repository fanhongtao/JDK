/*
 * @(#)ActivationGroupID.java	1.8 98/07/08
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

import java.rmi.server.UID;

/**
 * The identifier for a registered activation group serves several
 * purposes: <ul>
 * <li>identifies the group uniquely within the activation system, and
 * <li>contains a reference to the group's activation system so that the
 * group can contact its activation system when necessary.</ul><p>
 *
 * The <code>ActivationGroupID</code> is returned from the call to
 * <code>ActivationSystem.registerGroup</code> and is used to identify
 * the group within the activation system. This group id is passed
 * as one of the arguments to the activation group's special constructor
 * when an activation group is created/recreated.
 *
 * @author 	Ann Wollrath
 * @version	1.8, 07/08/98
 * @see 	ActivationGroup
 * @see		ActivationGroupDesc
 * @since	JDK1.2
 */
public class ActivationGroupID implements java.io.Serializable {
    /**
     * @serial The group's activation system.
     */
    private ActivationSystem system;

    /**
     * @serial The group's unique id.
     */
    private UID uid = new UID();

    /** indicate compatibility with JDK 1.2 version of class */
    private  static final long serialVersionUID = -1648432278909740833L;

    /**
     * Constructs a unique group id.
     *
     * @param system the group's activation system
     * @since JDK1.2
     */
    public ActivationGroupID(ActivationSystem system) {
	this.system = system;
    }

    /**
     * Returns the group's activation system.
     * @return the group's activation system
     * @since JDK1.2
     */
    public ActivationSystem getSystem() {
	return system;
    }
    
    /**
     * Returns a hashcode for the group's identifier.  Two group
     * identifiers that refer to the same remote group will have the
     * same hash code.
     *
     * @see java.util.Hashtable
     * @since JDK1.2
     */
    public int hashCode() {
	return uid.hashCode();
    }

    /**
     * Compares two group identifiers for content equality.
     * Returns true if both of the following conditions are true:
     * 1) the unique identifiers are equivalent (by content), and
     * 2) the activation system specified in each
     *    refers to the same remote object.
     *
     * @param	obj	the Object to compare with
     * @return	true if these Objects are equal; false otherwise.
     * @see		java.util.Hashtable
     * @since JDK1.2
     */
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	} else if (obj instanceof ActivationGroupID) {
	    ActivationGroupID id = (ActivationGroupID)obj;
	    return (uid.equals(id.uid) && system.equals(id.system));
	} else {
	    return false;
	}
    }
}
