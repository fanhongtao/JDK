/*
 * @(#)ServerSubcontract.java	1.30 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core;

import com.sun.corba.se.internal.ior.ObjectKey;

/**
 * Server subcontract adds behavior on the server-side -- specifically
 * on the dispatch path. A single server subcontract instance serves
 * many server objects.
 */
public interface ServerSubcontract {
    /**
     * Create an object reference given a servant and/or user-key.
     *
     * @param key the user key part of the ORB key
     * @param servant the server object (may be null)
     */
    public Object createObjref(byte[] key, Object servant);

    /**
     * Create an object reference given an IOR.
     *
     * @param ior the ior to create an objref for.
     */
    public Object createObjref(IOR ior) throws Exception;

    /**
     * Destroy the object reference.
     */
    public void destroyObjref(Object objref);

    /**
     * Dispatch to the server object and return its
     * response.
     *
     * dispatch() should catch all exceptions and convert
     * them to a ServerResponse.
     *
     */
    public ServerResponse dispatch(ServerRequest request);

    /**
     * Handle a locate request.
     */
    public IOR locate(ObjectKey key);

    /**
     * Lookup and return the servant corresponding to the ior
     */
    public Object getServant(IOR ior);

    /**
     * Return a boolean indicating whether or not a servant is supported
     */

    public boolean isServantSupported();

    /**
     * Get the parallel client subcontract class
     */
    public Class getClientSubcontractClass();

    /**
     * Set the subcontract ID assigned by the ORB for this
     * class.
     */
    void setId(int id);

    int getId() ;
    /**
     * Set the ORB instance that created this SC
     */
    void setOrb(ORB orb);
}
