/*
 * @(#)ClientSubcontract.java	1.30 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core;

/**
 * The client subcontract adds quality-of-service behavior on the client-side.
 * It is also the delegate.
 */
public interface ClientSubcontract {

    /**
     * Indicate whether to use local stub optimization for marshalling or not.
     */
    public boolean useLocalInvocation( org.omg.CORBA.Object self ) ;

    /**
     * Get the on-the-wire form of the object reference this
     * subcontract serves.
     */
    public IOR marshal();

    /**
     * Unmarshal subcontract specific information from the
     * IOR
     */
    public void unmarshal(IOR ior);

    /**
     * Create a ClientRequest instance.
     * For JavaIDL, this is invoked from the DII layer. For RMI/IIOP,
     * this is invoked from the delegate itself.
     */
    public ClientRequest createRequest(String method, boolean isOneWay);

    /**
     * Make an invocation.
     * For JavaIDL, this is invoked from the DII layer. For RMI/IIOP,
     * this is invoked from the delegate itself.
     */
    public ClientResponse invoke(ClientRequest request);

    /** Release the ClientResponse object after reply arguments have
     *  been unmarshaled. If an exception was unmarshaled, it must be provided.
     *  For JavaIDL, this is invoked from the DII layer. For RMI/IIOP,
     *  this is invoked from the delegate itself.
     */
    public void releaseReply(ClientResponse resp, String method,
                             Exception exception)
        throws org.omg.CORBA.WrongTransaction, org.omg.CORBA.SystemException;

    /**
     * Set the subcontract ID assigned by the ORB for this
     * class.
     */
    void setId(int id);

    int getId() ;

    /**
     * Set the ORB for this class.
     */
    void setOrb(ORB orb);

    /**
     * Unexport this object.
     */
    void unexport();
}
