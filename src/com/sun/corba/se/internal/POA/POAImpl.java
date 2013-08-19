/*
 * @(#)POAImpl.java	1.88 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.POA;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POAPackage.*;
import org.omg.PortableServer.ServantLocatorPackage.*;

import com.sun.corba.se.internal.ior.IIOPProfileTemplate ;
import com.sun.corba.se.internal.ior.ObjectKeyTemplate ;
import com.sun.corba.se.internal.ior.POAObjectKeyTemplate ;
import com.sun.corba.se.internal.ior.IORTemplate ;
import com.sun.corba.se.internal.ior.ObjectId ;
import com.sun.corba.se.internal.ior.IIOPAddress ;
import com.sun.corba.se.internal.ior.IIOPAddressImpl ;
import com.sun.corba.se.internal.ior.POAView ;
import com.sun.corba.se.internal.ior.POAId ;
import com.sun.corba.se.internal.ior.POAIdPOAView ;

import org.omg.IOP.TAG_INTERNET_IOP ;

import com.sun.corba.se.internal.core.*;
import com.sun.corba.se.internal.corba.*;
import com.sun.corba.se.internal.orbutil.ORBUtility; //d11638
import com.sun.corba.se.internal.orbutil.ORBConstants; //d11638

import java.util.*;

/**
 * POAImpl is the implementation of the Portable Object Adapter. It 
 * contains an implementation of the POA interfaces specified in
 * orbos/97-05-15.
 *
 */

public class POAImpl extends org.omg.CORBA.LocalObject implements POA, POAView {
    protected POAORB orb;
    private int numLevels;
    private POAId poaId ;
    private String name;
    private POAManagerImpl manager;
    private POAImpl parent;
    private AdapterActivator activator;
    private ServantManager servantManager;
    private Servant defaultServant;
    
    private Policies policies;
    protected ActiveObjectMap activeObjectMap;
    protected Map children;

    protected int scid ;

    private Integer sysIdCounter = new Integer(0);
    private int nInvocations = 0; // pending invocations on this POA
    protected IORTemplate iortemp;
    protected byte[] adapterId ;

    // adapterActivatorCV has to be null
    //   see its use in find_POA() and enter()
    private java.lang.Object adapterActivatorCV = null;
				// Wait on this CV for 
				// AdapterActivator upcalls to complete 

    private java.lang.Object invokeCV =
	new java.lang.Object(); // Wait on this CV for all active
                                // invocations to complete 

    // beingDestroyedCV has to be null
    //   see its use in destroy() and enter()
    private java.lang.Object beingDestroyedCV = null;
				// Wait on this CV for the destroy
				// method to complete doing its work
    // thread local variable to store a boolean to detect deadlock in POA.destroy().
    // Note: This doesn't cover the case where an application starts more than one
    // thread which call POA.destroy. This variable could be extended to maintain
    // the current POA the thread is destroying so that the destroy threads don't
    // get in the way of each other.
    protected ThreadLocal isDestroying = new ThreadLocal () {
        protected java.lang.Object initialValue() {
            return Boolean.FALSE;
        }
    };

    private boolean destroyed = false;
    private boolean etherealizeFlag = false; // to check if the value has be
    // set already if destroy is called multiple times
    private boolean etherealizeValue = false; // to hold value from first call to
    // destroy, and this value is used for subsequent destroy calls

    // XXX temporary for timing: DO NOT RELEASE WITH THIS FLAG SET!
    private boolean normalState = false ;
  
    public POAImpl(String name, POAManagerImpl manager, Policies policies,
                   POAImpl parent, AdapterActivator activator, POAORB orb) 
    {
	this.policies = policies;

	this.name      = name;
	this.manager   = manager;
	this.parent    = parent;
	this.activator = activator;
	this.orb       = orb;
	
	pre_initialize();

	if( parent != null ) {
	    // If this is not the Root POA, notify POAORB that IORInterceptors 
	    // should be invoked now.
	    orb.invokeIORInterceptors( this );
        }

	post_initialize() ;
    }

    public POAImpl makePOA( 
	String name, POAManagerImpl manager, Policies policies, 
	POAImpl parent, AdapterActivator activator, POAORB orb )
    {
	return new POAImpl( 
	    name, manager, policies, parent, activator, orb ) ;
    }

    protected void pre_initialize() 
    {
	if (parent == null)
	    numLevels = 0 ;
	else
	    numLevels = parent.getNumLevels() + 1 ;

	if (policies.retainServants())
	    activeObjectMap = new ActiveObjectMap(policies.isMultipleIds());
	children = new HashMap();
	manager.addPOA(this);

	if (policies.isSingleThreaded())
	    throw new org.omg.CORBA.NO_IMPLEMENT(
		"Single threaded model not implemented");

	// Construct the IORTemplate
	final POAORB poaorb = (POAORB)orb;
	int serverid;
	int port ;

	if ( policies.isTransient() ) {
	    serverid = poaorb.getTransientServerId();
	    port = poaorb.getServerEndpoint().getPort() ;
	    if (policies.servantCachingAllowed())
		scid = ORBConstants.SCTransientSCID ;
	    else 
		scid = ORBConstants.TransientSCID ;
	} else {
	    serverid = poaorb.getPersistentServerId();
	    port = poaorb.getPersistentServerPort(EndPoint.IIOP_CLEAR_TEXT);
	    if (policies.servantCachingAllowed())
		scid = ORBConstants.SCPersistentSCID ;
	    else
		scid = ORBConstants.PersistentSCID ;
	}

	String host = poaorb.getServerEndpoint().getHostName() ;

	IIOPAddress addr = new IIOPAddressImpl( host, port ) ;

	// get the orb-id corresponding this ORB for putting in the Object Key
	String orbId = poaorb.getORBId();

	poaId = new POAIdPOAView( this ) ;

	// Construct the objectKey 
	ObjectKeyTemplate oktemp = new POAObjectKeyTemplate( 
	    scid, serverid, orbId, poaId ) ;
	adapterId = oktemp.getAdapterId( orb ) ;

	GIOPVersion version = orb.getGIOPVersion() ;
	IIOPProfileTemplate temp = new StandardIIOPProfileTemplate( addr,
	    version.major, version.minor, oktemp, null, orb ) ;

	iortemp = new IORTemplate() ;
	iortemp.add( temp ) ;
    }

    protected void post_initialize()
    {
	iortemp.makeImmutable() ;
    }
    
    public final Policies getPolicies() {
	return policies;
    }

    protected synchronized boolean hasServantInMap(Servant servant){
        if (activeObjectMap != null ) //Has retain policy set.
            return activeObjectMap.contains(servant);
        else //Has non-retain policy set. 
            return false;
    }

    protected POA create_POA(String name,
                             POAManagerImpl manager,
                             Policies policies)
	throws AdapterAlreadyExists, InvalidPolicy {

	if (children.get(name) != null) 
	    adapterAlreadyExists();
	    
	if (manager == null)
	    manager = new POAManagerImpl(orb);

	POAImpl child = makePOA(name, manager, policies, this,
                                null, orb);

	children.put(name, child);
	return child;
    }

    protected void removeChild(String name) {
        children.remove(name);
    }

    // XXX visible for ServantCachingPOAClientSC
    void enter() throws POADestroyed
    {
	if (destroyed) {
            // Avoid deadlock if this is the thread that is processing the POA.destroy
            // because this is the only thread that can notify waiters on beingDestroyedCV
            if (beingDestroyedCV != null && isDestroying.get() == Boolean.FALSE) {
		try {
		    synchronized (beingDestroyedCV) {
			beingDestroyedCV.wait();
		    }
		} catch (InterruptedException ex) { }
	    }
	    throw new POADestroyed();
	}

	if (parent != null && parent.adapterActivatorCV != null) { 
	    try {
		synchronized (parent.adapterActivatorCV) {
		    parent.adapterActivatorCV.wait();
		}
	    } catch (InterruptedException ex) { }
        }
	    
	synchronized (this) {
	    nInvocations++;
	}

	manager.enter();
    }

    // XXX visible for ServantCachingPOAClientSC
    void exit() 
    {
	synchronized (this) {
	    nInvocations--;

	    if ((nInvocations == 0) && destroyed) {
		synchronized (invokeCV) {
		    invokeCV.notifyAll();
		}
	    }
	}

	manager.exit();
    }

    /** Called from the subcontract to get the servant to dispatch this
     *  invocation to. The actual servant dispatch code is in the subcontract.
     */
    public Servant getServant(
        byte[] id, CookieHolder cookieHolder, 
	String operation,
	com.sun.corba.se.internal.core.ServerRequest serverRequest)

	throws
	    ForwardRequest,
	    POADestroyed
    {
    	enter();
        orb.getCurrent().addThreadInfo(this, id, cookieHolder, operation);

	// This must be set just after "enter" and the thread stack
	// has been pushed so if an exception occurs before this point then
	// the exception reply will not try to do returnServant.
	// serverRequest may be null in the rmi-iiop isLocal case.
	if (serverRequest != null) {
	    serverRequest.setExecuteReturnServantInResponseConstructor(true);
	}

	Servant servant = internalGetServant(id, cookieHolder, operation);
	orb.getCurrent().setServant(servant);
	return servant;
    }

    /** Called from the subcontract to let this POA cleanup after an
     *  invocation. Note: If getServant was called, then returnServant
     *  MUST be called, even in the case of exceptions.  This may be
     *  called multiple times for a single request.
     */
    public void returnServant()
    {
        if ( !policies.retainServants() 
	     && policies.useServantManager() 
             && orb.getCurrent().preInvokeCalled()
	     && !orb.getCurrent().postInvokeCalled())
        {
	    if (servantManager == null ||
		! (servantManager instanceof ServantLocator) )
            {
		return;
	    }
	    ServantLocator locator = (ServantLocator)servantManager;
	    POACurrent c = orb.getCurrent();
	    try {
		locator.postinvoke(c.getObjectId(), 
				   c.getPOA(),
				   c.getOperation(),
				   c.getCookieHolder().value,
				   c.getServant());
	    } finally {
		orb.getCurrent().setPostInvokeCalled();
	    }
	}
    }

    /** Called in GenericPOAServerSC rmi-iiop isLocal case.
     */
    public void returnServantAndRemoveThreadInfo()
    {
	try {
	    returnServant();
	} finally {
	    removeThreadInfo();
	}
    }

    /** Called from the subcontract to let this POA cleanup after an
     *  invocation. Note: If getServant was called, then returnServant
     *  MUST be called, even in the case of exceptions.  This MUST
     *  be called only once for a single request.
     */
    public void removeThreadInfo()
    {
	exit();
	orb.getCurrent().removeThreadInfo();
    }

    private Servant internalGetServant(byte[] id, CookieHolder cookieHolder,
                              	       String operation)
	throws ForwardRequest, POADestroyed
    {
	boolean isSpecial = SpecialMethod.isSpecialMethod(operation);

	if (policies.retainServants() && policies.useActiveMapOnly())
	    {
                // CHANGED (RAM J) 08/07/2000 raise OBJECT_NOT_EXIST exception
                // if servant not found in the activeObjectMap. Previously,
                // we were returning null, which is incorrect.
            
                //return activeObjectMap.get(id);
                Servant servant = activeObjectMap.get(id);
                if (servant == null) {
		    if (! isSpecial) {
			objectNotExist(MinorCodes.NULL_SERVANT,
				       CompletionStatus.COMPLETED_NO);
		    } else {
			return null;
		    }
                }
                return servant;
	    }
	else if (policies.retainServants() && policies.useServantManager())
	    {
		Servant servant = activeObjectMap.get(id);
		if ( servant == null) {
		    if (servantManager == null)
			objAdapter(MinorCodes.POA_NO_SERVANT_MANAGER,
				   CompletionStatus.COMPLETED_NO);
		
		    if (!(servantManager instanceof ServantActivator))
			objAdapter(MinorCodes.POA_BAD_SERVANT_MANAGER,
				   CompletionStatus.COMPLETED_NO);

		    synchronized (servantManager) {
                        // Check again if the servant exists in activeObjectMap:
                        // this is necessary because two threads could call
                        // the first activeObjectMap.get(id) simultaneously
                        // and get nulls returned. We cant hold the
                        // activeObjectMap lock during the incarnate call
                        // because incarnate may take too long.
                        servant = activeObjectMap.get(id);
                        if (servant == null) {
                            ServantActivator activator =
                                (ServantActivator) servantManager;
                            servant = activator.incarnate(id, this);

			    if (servant == null) {
				if (! isSpecial) {
				    throw new OBJ_ADAPTER(MinorCodes.NULL_SERVANT,
						   CompletionStatus.COMPLETED_NO);
				} else {
				    return null;
				}
			    }

			    // here check for unique_id policy, and if the servant
			    // is already registered, for a different ID, then throw
			    // OBJ_ADAPTER exception, else activate it. Section 11.3.5.1
			    // 99-10-07.pdf
			    if (policies.isUniqueIds()) {
				// check if the servant already is associated with some	
				// id
				if (activeObjectMap.contains(servant)) {
				    objAdapter(MinorCodes.POA_SERVANT_NOT_UNIQUE,
					       CompletionStatus.COMPLETED_NO);
				}
			    }

			    activate(id, servant);
			}
		    }
		}
		return servant;
	    }
	else if (policies.retainServants() && policies.useDefaultServant())
	    {
		Servant servant = activeObjectMap.get(id);
		if (servant != null)
		    return servant;
		else if (defaultServant != null)
		    return defaultServant;
		else
		    objAdapter(MinorCodes.POA_NO_DEFAULT_SERVANT,
			       CompletionStatus.COMPLETED_NO);
	    }
	else if (!policies.retainServants() && policies.useServantManager())
	    {
		if (servantManager == null)
		    objAdapter(MinorCodes.POA_NO_SERVANT_MANAGER,
			       CompletionStatus.COMPLETED_NO);
	    
		if (! (servantManager instanceof ServantLocator) )
		    objAdapter(MinorCodes.POA_BAD_SERVANT_MANAGER,
			       CompletionStatus.COMPLETED_NO);

		ServantLocator locator = (ServantLocator) servantManager;
                // Try - finally is J2EE requirement.
                Servant servant;
                try{
                    servant = locator.preinvoke(id, this, operation, 
                                                cookieHolder);
                    if (servant == null) {
			if (! isSpecial) {
			    objectNotExist(MinorCodes.NULL_SERVANT,
					   CompletionStatus.COMPLETED_NO);
			} else {
			    return null;
			}
                    }
                } finally {
                    orb.getCurrent().setPreInvokeCalled();
                }
		setDelegate(servant, id);

		// Note: locator.postinvoke is called in internalReturnServant

		return servant;
	    }
	else if (!policies.retainServants() && policies.useDefaultServant())
	    {
		if (defaultServant != null)
		    return defaultServant;
		else
		    objAdapter(MinorCodes.POA_NO_DEFAULT_SERVANT,
			       CompletionStatus.COMPLETED_NO);
	    }
	else
	    throw new INTERNAL(MinorCodes.POA_INTERNAL_GET_SERVANT_ERROR,
			       CompletionStatus.COMPLETED_NO);

	return null; // to keep javac happy
    }

    void etherealizeAll() {
	if (policies.retainServants() && policies.useServantManager()
	    && servantManager != null)  {
	    Enumeration keys = activeObjectMap.keys();
	    while ( keys.hasMoreElements() ) {
		byte[] id = (byte[])keys.nextElement();
		Servant servant = activeObjectMap.get(id);
		boolean remaining_activations;
		if (activeObjectMap.hasMultipleIDs(servant))
		    remaining_activations = true;
		else
		    remaining_activations = false;
		synchronized (servantManager) {
		    ((ServantActivator)
		     servantManager).etherealize(id, this,
						 servant,
						 true,
						 remaining_activations);
		}
	    }
	}
    }
    
    /**
     * <code>create_POA</code>
     * <b>Section 3.3.8.2</b>
     */
    public synchronized POA create_POA(String name,
				       POAManager manager,
				       Policy[] policies)
	throws AdapterAlreadyExists, InvalidPolicy {
	return create_POA(name, (POAManagerImpl) manager,
			  new Policies(policies));
      
    }

    /**
     * <code>find_POA</code>
     * <b>Section 3.3.8.3</b>
     */
    public synchronized POA find_POA(String name, boolean activate)
	throws AdapterNonExistent 
    {
	POA found = (POA) children.get(name);
	if (found != null)
	    return found;
	else if (activate) {
	    if (activator == null)
		adapterNonExistent();
	    else {
		boolean status;
		try {
		    // this song and dance of creating a CV and then
		    // notifying it is necessary to ensure that
		    // invocations on the created child POA are queued
		    // until unknown_adapter returns (see POAImpl.enter)
		    adapterActivatorCV = new java.lang.Object();
		    status = activator.unknown_adapter(this, name);
		    synchronized (adapterActivatorCV) {
			adapterActivatorCV.notifyAll();
			adapterActivatorCV = null;
		    }
		} catch (SystemException ex) {
		    if (orb.poaDebugFlag)
			ORBUtility.dprint( this, 
			    "System exception in unknown_adapter call", 
			    ex ) ;

		    objAdapter(MinorCodes.ADAPTER_ACTIVATOR_EXCEPTION,
			       CompletionStatus.COMPLETED_NO);
		    return null;
		}
		if (status == false) {
                    // OMG Issue 3740 is resolved to throw AdapterNonExistent if
                    // unknown_adapter() returns false.
	            adapterNonExistent();
                }
		return (POA) children.get(name);
	    }
	}
	adapterNonExistent();
	return null;		// to keep javac quiet
    }


    /**
     * <code>destroy</code>
     * <b>Section 3.3.8.4</b>
     */
    public void destroy(boolean etherealize, boolean wait_for_completion) {

        // This is to avoid deadlock
        if (wait_for_completion && orb.isProcessingInvocation()) {
            throw new BAD_INV_ORDER(
                "Request to destroy POA with waiting for completion while servicing a request",
                0,
                CompletionStatus.COMPLETED_NO);
        }

        destroyInternal(etherealize, wait_for_completion);
    }

    // Split destroy into destroy and destroyInternal.
    // This guarantees to have at most one DestroyThread started
    // if wait_for_completion == false. The recursive call to destroyInternal
    // uses "true" for all children which avoids starting new DestroyThreads.
    //
    void destroyInternal(boolean etherealize, boolean wait_for_completion) {
        // According to spec, the destroy may be called multiple time, and
        // it is allowed to proceed with it's own setting of the wait flag,
        // but the etherealize value is used from the first call to
        // destroy.  Also all children should be destroyed before the
        // parent POA.
        synchronized (this) {
            if (!destroyed) {
                destroyed = true;
            }
            if (beingDestroyedCV == null) {
                beingDestroyedCV = new java.lang.Object();
            }
            if (!etherealizeFlag) {
                etherealizeValue = etherealize;
                etherealizeFlag = true;
            }
        }

        // Converted from anonymous class to local class
        // so that we can call performDestroy() directly.
        class DestroyThread extends Thread {
	    private POAImpl poa ;

	    public DestroyThread( POAImpl poa )
	    {
		this.poa = poa ;
	    }

            public void run() {
                performDestroy();
            }

            public void performDestroy() {
                poa.isDestroying.set(Boolean.TRUE);

		// Make sure that we have a copy of the children, notoy
		// an Iterator, otherwise ConcurrentModificationException can
		// occur.
		java.lang.Object[] childPoas = null ;
		synchronized (poa) {
		    childPoas = poa.children.values().toArray() ;
		}
		for (int ctr=0; ctr<childPoas.length; ctr++) {
		    POAImpl cpoa = (POAImpl)(childPoas[ctr]) ;

                    // use wait_for_completion == true on the recursive destroy call so that
                    // only a single DestroyThread is actually started.
		    cpoa.destroyInternal( etherealizeValue, true ) ;
		}

                if (etherealizeValue)
                    poa.etherealizeAll();

                // this check is required, because activeObjectMap would
                // be null for nonRetain  Policy
                if (activeObjectMap != null) {
                    poa.activeObjectMap.clear();
                    poa.activeObjectMap = null;
                }
            }
        };

        DestroyThread destroyer = new DestroyThread( this );
        if (wait_for_completion) {
            // No need to start a new thread if we wait anyway.
            // In this case the current thread does the work.
            destroyer.performDestroy();
        } else {
            // Catch exceptions since setDaemon can cause a
            // security exception to be thrown under netscape
            // in the Applet mode
            try { destroyer.setDaemon(true); } catch (Exception e) {}
            // start a new thread
            destroyer.start();
        }

	if (wait_for_completion) {
	    while (nInvocations != 0) {
	        try {
		    synchronized (invokeCV) {
                        invokeCV.wait();
		    }
	        } catch (InterruptedException ex) { } 
	    }
	}

        manager.removePOA(this);		
	if (parent != null) {
            parent.removeChild(name);
	}

	synchronized (beingDestroyedCV) {
	    beingDestroyedCV.notifyAll();
	    beingDestroyedCV = null;
            isDestroying.set(Boolean.FALSE);
	}
    }


    // XXX: Do the policy factory methods need to be synchronized?

    /**
     * <code>create_thread_policy</code>
     * <b>Section 3.3.8.5</b>
     */
    public ThreadPolicy
	create_thread_policy(ThreadPolicyValue
			     value) {
	return new ThreadPolicyImpl(value);
    }

    /**
     * <code>create_lifespan_policy</code>
     * <b>Section 3.3.8.5</b>
     */
    public LifespanPolicy
	create_lifespan_policy(LifespanPolicyValue
			       value) {
	return new LifespanPolicyImpl(value);
    }

    /**
     * <code>create_id_uniqueness_policy</code>
     * <b>Section 3.3.8.5</b>
     */
    public IdUniquenessPolicy
	create_id_uniqueness_policy(IdUniquenessPolicyValue
				    value) {
	return new IdUniquenessPolicyImpl(value);
    }

    /**
     * <code>create_id_assignment_policy</code>
     * <b>Section 3.3.8.5</b>
     */
    public IdAssignmentPolicy
	create_id_assignment_policy(IdAssignmentPolicyValue
				    value) {
	return new IdAssignmentPolicyImpl(value);
    }

    /**
     * <code>create_implicit_activation_policy</code>
     * <b>Section 3.3.8.5</b>
     */
    public ImplicitActivationPolicy
	create_implicit_activation_policy(ImplicitActivationPolicyValue
					  value) {
	return new ImplicitActivationPolicyImpl(value);
    }

    /**
     * <code>create_servant_retention_policy</code>
     * <b>Section 3.3.8.5</b>
     */
    public ServantRetentionPolicy
	create_servant_retention_policy(ServantRetentionPolicyValue
					value) {
	return new ServantRetentionPolicyImpl(value);
    }
    
    
    /**
     * <code>create_request_processing_policy</code>
     * <b>Section 3.3.8.5</b>
     */
    public RequestProcessingPolicy
	create_request_processing_policy(RequestProcessingPolicyValue
					 value) {
	return new RequestProcessingPolicyImpl(value);
    }
    
    /****** not needed for now
	    public TransactionPolicy
	    create_transaction_policy(TransactionPolicyValue
	    value) {
	    return new TransactionPolicyImpl(value);
	    }
    ********/

    /**
     * <code>the_name</code>
     * <b>Section 3.3.8.6</b>
     */
    public String the_name() {
	return name;
    }

    /**
     * <code>the_parent</code>
     * <b>Section 3.3.8.7</b>
     */
    public POA the_parent() 
    {
	return parent;
    }

    public POAView getParent() 
    {
	return parent;
    }

    public int getNumLevels() 
    {
	return numLevels ;
    }

    /**
     * <code>the_children</code>
     */
    synchronized public org.omg.PortableServer.POA[] the_children() {
	Collection coll = children.values() ;
	int size = coll.size() ;
	POA[] result = new POA[ size ] ;
	int index = 0 ;
	Iterator iter = coll.iterator() ;
	while (iter.hasNext()) {
	    POA poa = (POA)(iter.next()) ;
	    result[ index++ ] = poa ;
	}

	return result ;
    }

    /**
     * <code>the_POAManager</code>
     * <b>Section 3.3.8.8</b>
     */
    public POAManager the_POAManager() {
	return manager;
    }

    /**
     * <code>the_activator</code>
     * <b>Section 3.3.8.9</b>
     */
    public AdapterActivator the_activator() {
	return activator;
    }
    
    /**
     * <code>the_activator</code>
     * <b>Section 3.3.8.9</b>
     */
    public void the_activator(AdapterActivator
			      activator) {
	this.activator = activator;
    }

    /**
     * <code>get_servant_manager</code>
     * <b>Section 3.3.8.10</b>
     */
    public ServantManager get_servant_manager()
	throws WrongPolicy {
	if (!policies.useServantManager())
	    wrongPolicy();
	return servantManager;
    }


    // XXX: Need to sort this out w.r.t ServantActivator and
    //      ServantLocator.
    
    /**
     * <code>set_servant_manager</code>
     * <b>Section 3.3.8.10</b>
     */
    public synchronized void set_servant_manager(ServantManager servantManager)
	throws WrongPolicy {
        if ( this.servantManager != null ) {
            throw new BAD_INV_ORDER( MinorCodes.SERVANT_MANAGER_ALREADY_SET,
                                     CompletionStatus.COMPLETED_NO );
        }
	if (!policies.useServantManager()) {
	    wrongPolicy();
        }
	this.servantManager = servantManager;
    }
	
    /**
     * <code>get_servant</code>
     * <b>Section 3.3.8.12</b>
     */
    public Servant get_servant()
	throws NoServant, WrongPolicy {
	if (!policies.useDefaultServant())
	    wrongPolicy();
	if (defaultServant == null)
	    noServant();
	return defaultServant;
    }

    /**
     * <code>set_servant</code>
     * <b>Section 3.3.8.13</b>
     */
    public void set_servant(Servant defaultServant)
	throws WrongPolicy {
	if (!policies.useDefaultServant())
	    wrongPolicy();
	this.defaultServant = defaultServant;
	setDelegate(defaultServant,
		    "DefaultServant".getBytes());

    }

    /**
     * <code>activate_object</code>
     * <b>Section 3.3.8.14</b>
     */
    public synchronized byte[] activate_object(Servant servant)
	throws ServantAlreadyActive, WrongPolicy {

	if (!(policies.isSystemAssignedIds() &&
	      policies.retainServants()))
	    wrongPolicy();
	    
	if (policies.isUniqueIds() &&
	    activeObjectMap.contains(servant))
	    servantAlreadyActive();

	// Allocate a new system-generated object-id.
	byte[] id = newId();

	activate(id, servant);
	return id;
    }

    /**
     * <code>activate_object_with_id</code>
     * <b>Section 3.3.8.15</b>
     */
    public synchronized void activate_object_with_id(byte[] id,
						     Servant servant)
	throws ObjectAlreadyActive, ServantAlreadyActive, WrongPolicy
    {
	if (!policies.retainServants())
	    wrongPolicy();
	
	if (activeObjectMap.containsKey(id))
	    objectAlreadyActive();
	
	if (policies.isUniqueIds() &&
	    activeObjectMap.contains(servant))
	    servantAlreadyActive();

	activate(id, servant);
    }

    // This method is used by dispatch, where we already check for
    //  policies before activation
    private final void activate(byte[] id, Servant servant) 
    {
	byte[] idClone = (byte[])(id.clone()) ;

	setDelegate(servant, idClone );

        if (orb.shutdownDebugFlag) {
            System.out.println("Activating object " + servant + 
	        " with POA " + this);
        }

	activeObjectMap.put(idClone, servant);

        if (ShutdownUtilDelegate.instance != null) {
            ShutdownUtilDelegate.instance.registerPOAForServant(this, servant);
        }
    }

    /**
     * <code>deactivate_object</code>
     * <b>3.3.8.16</b>
     */
    public synchronized void deactivate_object(byte[] id)
	throws ObjectNotActive, WrongPolicy {

	if (!policies.retainServants())
	    wrongPolicy();

	if (policies.useServantManager() && servantManager == null)
	    objAdapter(MinorCodes.POA_NO_SERVANT_MANAGER,
		       CompletionStatus.COMPLETED_NO);
	    
	Servant s = activeObjectMap.get(id);
	if (s == null)
	    objectNotActive();

        if (orb.shutdownDebugFlag) {
            System.out.println("Deactivating object " + s + " with POA " + this);
        }
        // This is to make sure that TransientObjectManager releases all references
        //orb.disconnect(createReference(s, id));
	activeObjectMap.remove(id);
        if (ShutdownUtilDelegate.instance != null) {
            ShutdownUtilDelegate.instance.unregisterPOAForServant(this, s);
        }
	boolean remaining_activations =
	    activeObjectMap.hasMultipleIDs(s);

	if (servantManager != null)
	    synchronized (servantManager) {
		((ServantActivator)
		 servantManager).etherealize( id, this, s, 
                                              false, remaining_activations);
	    }
    }

    public IOR makeTransactionalIOR( String repId, byte[] oid ) 
    {
	return null ;
    }

    protected org.omg.CORBA.Object makeObjectReference(
	String repId, byte[] id, IORTemplate iortemp, int scid )
    {
	ObjectImpl o = new CORBAObjectImpl();

	// Create the IOR
	ObjectId oid = new ObjectId( id ) ;
	IOR ior = new IOR( orb, repId, iortemp, oid ) ;

	// Invoke the old proprietary object reference creation interceptor
	ior = orb.objectReferenceCreated( ior ) ;

	ClientSubcontract csub = orb.getSubcontractRegistry().
	    getClientSubcontract( scid ) ;
	csub.setOrb( orb ) ;
	csub.unmarshal( ior ) ;

	o._set_delegate( (Delegate)csub );

	return o;
    }

    //Needed to be changed from private to package for DelegateImpl.
    protected org.omg.CORBA.Object createReference(String repId, byte[] id) 
    {
	return makeObjectReference( repId, id, iortemp, scid ) ;
    }

    protected org.omg.CORBA.Object createReference(Servant servant,
                                                   byte[] id) 
    {
        String[] reposIds = servant._all_interfaces(this, id);

	return makeObjectReference( reposIds[0], id, iortemp, scid ) ;
    }

    // create a delegate and stick it in the servant.
    // this delegate is needed during dispatch for the ObjectImpl._orb()
    // method to work.
    private void setDelegate(Servant servant, byte[] id) 
    {
        //This new servant delegate no longer needs the id for its initialization.
        if ( orb.delegateImpl == null )
            throw new org.omg.CORBA.OBJ_ADAPTER("DelegateImpl not initialized.");
        servant._set_delegate(orb.delegateImpl);
    }

    /**
     * <code>create_reference</code>
     * <b>3.3.8.17</b>
     */
    public org.omg.CORBA.Object create_reference(String repId)
	throws WrongPolicy 
    {
	if (!policies.isSystemAssignedIds())
	    wrongPolicy();

	return createReference(repId, newId());
    }

    /**
     * <code>create_reference_with_id</code>
     * <b>3.3.8.18</b>
     */
    public org.omg.CORBA.Object
	create_reference_with_id(byte[] oid, String repId) 
    {
	// First check if a servant exists for this id
	if (policies.retainServants()) {
	    Servant s = activeObjectMap.get(oid);
	    if (s == null)
		return createReference(repId, oid);
	    String[] reposIds = s._all_interfaces(this,oid);
	    if (!repId.equals(reposIds[0]))
		badParam(MinorCodes.BAD_REPOSITORY_ID,
			 CompletionStatus.COMPLETED_NO);
	    return createReference(s, oid);
	}
	return createReference(repId, oid);
    }

    /**
     * <code>servant_to_id</code>
     * <b>3.3.8.19</b>
     */
    public synchronized byte[] servant_to_id(Servant servant)
	throws ServantNotActive, WrongPolicy {
	if (! (policies.retainServants() &&
	       (policies.isUniqueIds() ||
		policies.isImplicitlyActivated())) )
	    wrongPolicy();
	    
	if (policies.isUniqueIds() &&
	    activeObjectMap.contains(servant))
	    return activeObjectMap.getKey(servant);
	    
	if (policies.isImplicitlyActivated() &&
	    (policies.isMultipleIds() ||
	     !activeObjectMap.contains(servant)))
	    try {
		return activate_object(servant);
	    } catch (ServantAlreadyActive s) {
		// XXX should not happen
	    }
		
	servantNotActive();
	return null;                // to keep javac quiet
    }

    /**
     * <code>servant_to_reference</code>
     * <b>3.3.8.20</b>
     */
    public org.omg.CORBA.Object
	servant_to_reference(Servant servant)
	throws ServantNotActive, WrongPolicy {
	    
	byte[] oid = servant_to_id(servant);

	return createReference(servant, oid);
    }

    /**
     * <code>reference_to_servant</code>
     * <b>3.3.8.21</b>
     */
    public synchronized Servant
	reference_to_servant(org.omg.CORBA.Object reference)
	throws ObjectNotActive, WrongPolicy, WrongAdapter 
    {
        if( destroyed ) {
            objectNotExist(MinorCodes.ADAPTER_DESTROYED,
                CompletionStatus.COMPLETED_NO);
        }
	    
	if (!policies.retainServants() &&
	    !policies.useDefaultServant())
	    wrongPolicy();

	// reference_to_id should throw WrongAdapter
	// if the objref was not created by this POA
	byte [] id = reference_to_id(reference);
	
	if (policies.retainServants()) {
	    Servant s = activeObjectMap.get(id);
	    if (s != null)
		return s;
	}
	
	if (policies.useDefaultServant() && defaultServant != null)
	    return defaultServant;
	    
	objectNotActive();
	return null;
    }

    /**
     * <code>reference_to_id</code>
     * <b>3.3.8.22</b>
     */
    public synchronized byte[] reference_to_id(org.omg.CORBA.Object
				  reference)
	throws WrongAdapter, WrongPolicy {
        if( destroyed ) {
            objectNotExist(MinorCodes.ADAPTER_DESTROYED,
                CompletionStatus.COMPLETED_NO);
        }

	// According to 99-10-07.pdf (11.3.8.23), WrongPolicy is for future
	// extensions
	    
	// XXX: we need to be able to see if this POA was the
	//      one that created "reference". if not, we need
	//      to throw WrongAdapter

	ClientSC clientSC = (ClientSC) ((ObjectImpl) reference)._get_delegate();
	// check if the poaId in the reference matches the Id for this POA
	// if not then WrongAdapter Exception is thrown
	
	if (!clientSC.getPOAId().equals( poaId ))
	    wrongAdapter();

	return clientSC.getObjectId();
    }

    /**
     * <code>id_to_servant</code>
     * <b>3.3.8.23</b>
     */
    public synchronized Servant id_to_servant(byte[] id)
	throws ObjectNotActive, WrongPolicy {
        if( destroyed ) {
            objectNotExist(MinorCodes.ADAPTER_DESTROYED,
                CompletionStatus.COMPLETED_NO);
        }
	if (!policies.retainServants())
	    wrongPolicy();
	Servant s = activeObjectMap.get(id);
	if (s == null)
	    objectNotActive();
	return s;
    }

    /**
     * <code>id_to_reference</code>
     * <b>3.3.8.24</b>
     */
    public synchronized org.omg.CORBA.Object id_to_reference(byte[] id)
	throws ObjectNotActive, WrongPolicy {
        if( destroyed ) {
            objectNotExist(MinorCodes.ADAPTER_DESTROYED,
                CompletionStatus.COMPLETED_NO);
        }
	if (!policies.retainServants())
	    wrongPolicy();
	Servant s = activeObjectMap.get(id);
	if (s == null)
	    objectNotActive();
	return createReference(s, id);
    }

    /**
     * <code>id</code>
     * <b>11.3.8.26 in ptc/00-08-06</b>
     */
    public byte[] id() {
	return adapterId ;
    }

    private byte[] newId() {
	byte[] array = new byte[4];
	synchronized (sysIdCounter) {
	    int value = sysIdCounter.intValue();
	    ORBUtility.intToBytes(value, array, 0);
	    sysIdCounter = new Integer(++value);
	}
	return array;
    }

    // Errors

    static final void wrongPolicy() throws WrongPolicy {
	throw new WrongPolicy();
    }

    static final void wrongAdapter() throws WrongAdapter {
	throw new WrongAdapter();
    }

    static final void adapterAlreadyExists()
	throws AdapterAlreadyExists {
	throw new AdapterAlreadyExists();
    }

    static final void adapterNonExistent()
	throws AdapterNonExistent {
	throw new AdapterNonExistent();
    }

    static final void noServant()
	throws NoServant {
	throw new NoServant();
    }

    static final void servantAlreadyActive()
	throws ServantAlreadyActive {
	throw new ServantAlreadyActive();
    }

    static final void servantNotActive()
	throws ServantNotActive {
	throw new ServantNotActive();
    }

    static final void objectAlreadyActive()
	throws ObjectAlreadyActive {
	throw new ObjectAlreadyActive();
    }

    static final void objectNotActive()
	throws ObjectNotActive {
	throw new ObjectNotActive();
    }

    static final void objectNotExist(int minor, CompletionStatus status)
	throws OBJECT_NOT_EXIST {
	throw new OBJECT_NOT_EXIST(minor, status);
    }

    static final void objAdapter(int minor, CompletionStatus status)
	throws OBJ_ADAPTER {
	throw new OBJ_ADAPTER(minor, status);
    }

    static final void badParam(int minor, CompletionStatus status)
	throws BAD_PARAM {
	throw new org.omg.CORBA.BAD_PARAM(MinorCodes.BAD_REPOSITORY_ID,
					  CompletionStatus.COMPLETED_NO);
    }
    
    static final void debug(String s) {
	System.out.println(s);
    }

    public IORTemplate getIORTemplate() 
    {
	return iortemp ;
    }

    // Note: This method is made public so it can be accessed by the
    // Portable Interceptors package.  This should not be a security risk
    // since any decisions that can be made based on these policies will 
    // have been made long before any untrusted code can call this method.
    public Policy get_effective_policy( int type ) {
	return policies.get_effective_policy( type ) ;
    }
}
