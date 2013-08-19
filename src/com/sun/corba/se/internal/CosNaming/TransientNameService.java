/*
 * @(#)TransientNameService.java	1.45 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.CosNaming;

// Get CORBA type
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.ORB;
import org.omg.CORBA.CompletionStatus;

import org.omg.CORBA.Policy;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.RequestProcessingPolicyValue;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.ServantRetentionPolicyValue;

// Get org.omg.CosNaming types
import org.omg.CosNaming.NamingContext;

// Import transient naming context
import com.sun.corba.se.internal.CosNaming.TransientNamingContext;
import com.sun.corba.se.internal.orbutil.ORBConstants;
import com.sun.corba.se.internal.POA.*;

/**
 * Class TransientNameService implements a transient name service
 * using TransientNamingContexts and TransientBindingIterators, which
 * implement the org.omg.CosNaming::NamingContext and org.omg.CosNaming::BindingIterator
 * interfaces specfied by the OMG Common Object Services Specification.
 * <p>
 * The TransientNameService creates the initial NamingContext object.
 * @see NamingContextImpl
 * @see BindingIteratorImpl
 * @see TransientNamingContext
 * @see TransientBindingIterator
 */
public class TransientNameService
{
    /**
     * Constructs a new TransientNameService, and creates an initial
     * NamingContext, whose object
     * reference can be obtained by the initialNamingContext method.
     * @param orb The ORB object
     * @exception org.omg.CORBA.INITIALIZE Thrown if
     * the TransientNameService cannot initialize.
     */
    public TransientNameService(com.sun.corba.se.internal.POA.POAORB orb )
        throws org.omg.CORBA.INITIALIZE
    {
        // Default constructor uses "NameService" as the key for the Root Naming
        // Context. If default constructor is used then INS's object key for
        // Transient Name Service is "NameService"
        initialize( orb, "NameService" );
    }

    /**
     * Constructs a new TransientNameService, and creates an initial
     * NamingContext, whose object
     * reference can be obtained by the initialNamingContext method.
     * @param orb The ORB object
     * @param nameserviceName Stringified key used for INS Service registry
     * @exception org.omg.CORBA.INITIALIZE Thrown if
     * the TransientNameService cannot initialize.
     */
    public TransientNameService(com.sun.corba.se.internal.POA.POAORB orb,
        String serviceName ) throws org.omg.CORBA.INITIALIZE
    {
        // This constructor gives the flexibility of providing the Object Key
        // for the Root Naming Context that is registered with INS.
        initialize( orb, serviceName );
    }


    /** 
     * This method initializes Transient Name Service by associating Root 
     * context with POA and registering the root context with INS Object Keymap.
     */ 
    private void initialize( com.sun.corba.se.internal.POA.POAORB orb,
        String nameServiceName )
        throws org.omg.CORBA.INITIALIZE
    {
        try {
            POA rootPOA = (POA) orb.resolve_initial_references( "RootPOA" );
            rootPOA.the_POAManager().activate();

            int i = 0;
            Policy[] poaPolicy = new Policy[3];
            poaPolicy[i++] = rootPOA.create_lifespan_policy(
                LifespanPolicyValue.TRANSIENT);
            poaPolicy[i++] = rootPOA.create_id_assignment_policy(
                IdAssignmentPolicyValue.SYSTEM_ID);
            poaPolicy[i++] = rootPOA.create_servant_retention_policy(
                ServantRetentionPolicyValue.RETAIN);

            POA nsPOA = rootPOA.create_POA( "TNameService", null, poaPolicy );
            nsPOA.the_POAManager().activate();

            // Create an initial context
            TransientNamingContext initialContext =
                new TransientNamingContext(orb, null, nsPOA);
            byte[] rootContextId = nsPOA.activate_object( initialContext );
            initialContext.localRoot =
                nsPOA.id_to_reference( rootContextId );
            theInitialNamingContext = initialContext.localRoot;
            orb.register_initial_reference( nameServiceName, 
                theInitialNamingContext );
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



    /**
   * Return the initial NamingContext.
   * @return the object reference for the initial NamingContext.
   */
    public org.omg.CORBA.Object initialNamingContext()
    {
	return theInitialNamingContext;
    }


    // The initial naming context for this name service
    private org.omg.CORBA.Object theInitialNamingContext;
}
