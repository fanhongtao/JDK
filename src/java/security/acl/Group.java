/*
 * @(#)Group.java	1.11 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.security.acl;

import java.util.Enumeration;
import java.security.Principal;

/**
 * This interface is used to represent a group of principals. (A principal
 * represents an entity such as an individual user or a company). <p>     
 *
 * Note that Group extends Principal. Thus, either a Principal or a Group can 
 * be passed as an argument to methods containing a Principal parameter. For 
 * example, you can add either a Principal or a Group to a Group object by 
 * calling the object's <code>addMember</code> method, passing it the 
 * Principal or Group.
 *
 * @author 	Satish Dharmaraj
 */
public interface Group extends Principal {

    /**
     * Adds the specified member to the group. 
     *  
     * @param user the principal to add to this group.
     * 
     * @return true if the member was successfully added, 
     * false if the principal was already a member.
     */
    public boolean addMember(Principal user);

    /**
     * Removes the specified member from the group.
     * 
     * @param user the principal to remove from this group.
     * 
     * @return true if the principal was removed, or 
     * false if the principal was not a member.
     */
    public boolean removeMember(Principal user);

    /**
     * Returns true if the passed principal is a member of the group. 
     * This method does a recursive search, so if a principal belongs to a 
     * group which is a member of this group, true is returned.
     * 
     * @param member the principal whose membership is to be checked.
     * 
     * @return true if the principal is a member of this group, 
     * false otherwise.
     */
    public boolean isMember(Principal member);


    /**
     * Returns an enumeration of the members in the group.
     * The returned objects can be instances of either Principal 
     * or Group (which is a subclass of Principal).
     * 
     * @return an enumeration of the group members.
     */
    public Enumeration members();

}
