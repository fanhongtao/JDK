/*
 * @(#)NSORB.java	1.4 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.CosNaming;

// Get CORBA type
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.ORB;
import org.omg.CORBA.CompletionStatus;

import com.sun.corba.se.internal.POA.*;
import org.omg.CosNaming.*;

import org.omg.CORBA.Policy;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.RequestProcessingPolicyValue;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.ServantRetentionPolicyValue;

// Get org.omg.CosNaming types
import org.omg.CosNaming.NamingContext;

// Import transient naming context
import com.sun.corba.se.internal.CosNaming.TransientNamingContext;

public class NSORB extends POAORB {

    public NSORB( ) {
        super();
        this.setPersistentServerId( (int) 1000000 );
    }

    private org.omg.CORBA.Object initializeRootNamingContext( ) {
        org.omg.CORBA.Object rootContext = null;
        try {

            TransientNameService tns = new TransientNameService(this);
            return tns.initialNamingContext();
        } catch (org.omg.CORBA.SystemException e) {
            NamingUtils.printException(e);
            throw new org.omg.CORBA.INITIALIZE(
                MinorCodes.TRANS_NS_CANNOT_CREATE_INITIAL_NC_SYS,
                CompletionStatus.COMPLETED_NO);
        } catch (Exception e) {
            NamingUtils.printException(e);
            throw new org.omg.CORBA.INITIALIZE(
                MinorCodes.TRANS_NS_CANNOT_CREATE_INITIAL_NC,
                CompletionStatus.COMPLETED_NO);
        }
    }
    

    public org.omg.CORBA.Object getInitialService( String theKey ) {
        if( theKey.equals( "NameService" ) ) {
            return initializeRootNamingContext();
        }
        return null;
    }  

}


