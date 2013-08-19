/*
 * @(#)INSSubcontract.java	1.4 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Licensed Materials - Property of IBM
 * RMI-IIOP v1.0
 * Copyright IBM Corp. 1998 1999  All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */


package com.sun.corba.se.internal.corba;

import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.ORB;


import com.sun.corba.se.internal.core.ServerSubcontract;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.core.INSObjectKeyMap;
import com.sun.corba.se.internal.core.INSObjectKeyEntry;
import com.sun.corba.se.internal.core.ServerRequest;
import com.sun.corba.se.internal.core.ServerResponse;
import com.sun.corba.se.internal.ior.ObjectKey;
import com.sun.corba.se.internal.orbutil.MinorCodes;



/**
 * INSSubcontract handles all INS related discovery request. The INSService
 * can be registered using ORB.register_initial_reference(), these registrations
 * will be maintained in INSObjectKeyMap. This  Singleton subcontract just 
 * finds the target IOR and does location forward.
 * NOTE: PI points are not invoked in either dispatch() or locate() method this
 * should be fixed in Tiger.
 */ 
public class INSSubcontract implements ServerSubcontract {

    private ORB orb = null;

    public INSSubcontract( ORB orb ) {
        this.orb = orb;
    }

    // Need to signal one of OBJECT_HERE, OBJECT_FORWARD, OBJECT_NOT_EXIST.
    public IOR locate(ObjectKey okey) { 
        // send a locate forward with the right IOR. If the insKey is not 
        // registered then it will throw OBJECT_NOT_EXIST Exception
        String insKey = new String( okey.getBytes(orb) );
        return getINSReference( insKey );
    }

    public ServerResponse dispatch(ServerRequest request) {
        // send a locate forward with the right IOR. If the insKey is not 
        // registered then it will throw OBJECT_NOT_EXIST Exception
        String insKey = new String( request.getObjectKey().getBytes(orb) );
        return request.createLocationForward( getINSReference( insKey ), null);
    }

    /**
     * getINSReference if it is registered in INSObjectKeyMap.
     */
    protected IOR getINSReference( String insKey ) {
        INSObjectKeyEntry entry =
            INSObjectKeyMap.getInstance().getEntry(insKey);
        if( entry != null ) {
            // If entry is not null then the locate is with an INS Object key,
            // so send a location forward with the right IOR.
            return entry.getIOR( );
        }
        throw new OBJECT_NOT_EXIST(MinorCodes.SERVANT_NOT_FOUND,
                                   CompletionStatus.COMPLETED_NO);
    }

    public boolean isServantSupported() {
        return false;
    }

    public void destroyObjref(Object objref) {
         throw new NO_IMPLEMENT();
    }

    public Object createObjref(IOR ior) {
         throw new NO_IMPLEMENT();
    }

    public Object createObjref(byte[] key, Object servant) {
         throw new NO_IMPLEMENT();
    }

    public Object getServant(IOR ior) {
         throw new NO_IMPLEMENT();
    }

    public Class getClientSubcontractClass() {
        return null;
    }

    public void setId(int id) {
         throw new NO_IMPLEMENT();
    }
  
    public int getId() {
         throw new NO_IMPLEMENT();
    }

    private INSSubcontract( ) {
        orb = ORB.init( (String[]) null, (java.util.Properties) null );
    }

    public void setOrb(com.sun.corba.se.internal.core.ORB orb) {
        this.orb = orb;
    }


}
