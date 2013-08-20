/*
 * @(#)Repository.java	1.37 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jmx.mbeanserver;


// java import
import java.util.ArrayList;
import java.util.Set;


// RI import
import javax.management.*;

/**
 * The Repository interface provides local access to the
 * implementation of Repository Service in use in the agent.
 *
 * @since 1.5
 * @since.unbundled JMX RI 1.2
 */
public interface Repository   { 


    /**
     * The purpose of this method is to provide a unified way to provide whatever
     * configuration information is needed by the specific underlying implementation
     * of the repository.
     *
     * @param configParameters An list containing the configuration parameters needed by the specific
     * Repository Service implementation.
     */
    public void setConfigParameters(ArrayList configParameters) ; 

    /**
     * Indicates whether or not the Repository Service supports filtering. If
     * the Repository Service does not support filtering, the MBean Server
     * will perform filtering.
     *
     * @return  true if filtering is supported, false otherwise.
     */
    public boolean isFiltering() ; 
    
    /**
     * Stores an MBean associated with its object name in the repository.
     *
     *@param object MBean to be stored in the repository.
     *@param name MBean object name.
     *     
     *@exception InstanceAlreadyExistsException  The MBean is already stored in the repository.
     */        
    public void addMBean(Object object, ObjectName name )
	throws InstanceAlreadyExistsException ;
    
    /**
     * Checks whether an MBean of the name specified is already stored in
     * the repository.
     *
     * @param name name of the MBean to find.
     *
     * @return  true if the MBean is stored in the repository, false otherwise.
     */
    public boolean contains(ObjectName name) ; 
    
    /**
     * Retrieves the MBean of the name specified from the repository. The
     * object name must match exactly.
     *
     * @param name name of the MBean to retrieve.
     *
     * @return  The retrieved MBean if it is contained in the repository, null otherwise.
     *
     */
    public Object retrieve(ObjectName name) ; 

    /**
     * Selects and retrieves the list of MBeans whose names match the specified
     * object name pattern and which match the specified query expression (optionally).
     *
     *
     * @param name The name of the MBean(s) to retrieve - may be a specific object or
     * a name pattern allowing multiple MBeans to be selected.     
     * @param query query expression to apply when selecting objects - this parameter will
     * be ignored when the Repository Service does not support filtering.
     *
     * @return  The list of MBeans selected. There may be zero, one or many MBeans returned
     * in the Set.
     *
     */
    public Set query(ObjectName name, QueryExp query);
       
    /**
     * Removes an MBean from the repository.
     *
     * @param name name of the MBean to remove.
     *
     * @exception InstanceNotFoundException The MBean does not exist in the repository.
     */
    public void remove(ObjectName name) throws InstanceNotFoundException ; 

    /**
     * Gets the number of MBeans stored in the repository.
     *
     * @return  Number of MBeans.
     */
    public Integer getCount() ; 

    /**
     * Gets the name of the domain currently used by default in the repository.
     *
     * @return  A string giving the name of the default domain name.
     */
    public String getDefaultDomain() ; 

    /**
     * Returns the list of domains in which any MBean is currently
     * registered.
     *
     * @since.unbundled JMX RI 1.2
     */
    public String[] getDomains();

 }
