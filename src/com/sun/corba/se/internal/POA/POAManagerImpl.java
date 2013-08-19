/*
 * @(#)POAManagerImpl.java	1.18 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.POA;

import java.util.*;
import org.omg.CORBA.TRANSIENT;
import org.omg.CORBA.OBJ_ADAPTER;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.LocalObject;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAManagerPackage.State;
import org.omg.PortableServer.POA;


/** POAManagerImpl is the implementation of the POAManager interface.
 *  Its public methods are activate(), hold_requests(), discard_requests()
 *  and deactivate().
 *
 *  A note on deadlocks: Since Java allows threads to acquire the same
 *  monitor multiple times, there will not be a deadlock on circular calls
 *  to synchronized methods (i.e. if a POAManager instance method calls an 
 *  object which calls another method on this same POAManager instance). 
 *  However, there is one "legal" deadlock which can occur: see the note 
 *  in the enter() method.
 */

public class POAManagerImpl extends org.omg.CORBA.LocalObject implements POAManager
{
    // POAManager states
    private static final State HOLDING = State.HOLDING;
    private static final State ACTIVE = State.ACTIVE;
    private static final State DISCARDING = State.DISCARDING;
    private static final State INACTIVE = State.INACTIVE;
   
    POAORB orb;
    private State state; // current state of this
    private Set poas = Collections.synchronizedSet(new HashSet(4)); // all poas controlled by this

    // Number of invocations in progress
    int nInvocations=0;	

    // Number of threads waiting for invocations to complete
    int nWaiters=0;

    private void countedWait()
    {
	try {
	    nWaiters++ ;
	    wait(); 
	} catch ( java.lang.InterruptedException ex ) {
	    // NOP
	} finally {
	    nWaiters-- ;
	}
    }

    private void notifyWaiters() 
    {
	if (nWaiters >0)
	    notifyAll() ;
    }

    public POAManagerImpl(POAORB orb)
    {
	this.orb = orb;
	state = HOLDING;
    }

    synchronized void addPOA(POA poa)
    {
        poas.add(poa);
        orb.addPoaManager(this);
    }


    synchronized void removePOA(POA poa)
    {
        poas.remove(poa);
        if ( poas.isEmpty() ) {
            orb.removePoaManager(this);
	}
    }

/****************************************************************************
 * The following four public methods are used to change the POAManager's state.
 *
 * A note on the design of synchronization code:
 * There are 4 places where a thread would need to wait for a condition:
 *      - in hold_requests, discard_requests, deactivate, enter 
 * There are 5 places where a thread notifies a condition:
 *      - in activate, hold_requests, discard_requests, deactivate, exit 
 * Since each notify needs to awaken waiters in several of the 4 places,
 * and since wait() in Java has the nice property of releasing the lock
 * on its monitor before sleeping, it seemed simplest to have just one
 * monitor object: "this". Thus all notifies will awaken all waiters.
 * On waking up, each waiter verifies that the condition it was waiting 
 * for is satisfied, otherwise it goes back into a wait().
 * 
 ****************************************************************************/

    /**
     * <code>activate</code>
     * <b>Spec: pages 3-14 thru 3-18</b>
     */
    public synchronized void activate()
        throws org.omg.PortableServer.POAManagerPackage.AdapterInactive
    {
	if ( state.value() == State._INACTIVE )
	    throw new org.omg.PortableServer.POAManagerPackage.AdapterInactive();
	// set the state to ACTIVE
	state = ACTIVE;
	
	// Notify any invocations that were waiting because the previous
	// state was HOLDING, as well as notify any threads that were waiting
	// inside hold_requests() or discard_requests(). 
	notifyWaiters();
    }

    /**
     * <code>hold_requests</code>
     * <b>Spec: pages 3-14 thru 3-18</b>
     */
    public synchronized void hold_requests(boolean wait_for_completion)
        throws org.omg.PortableServer.POAManagerPackage.AdapterInactive
    {
	if ( state.value() == State._INACTIVE )
	    throw new org.omg.PortableServer.POAManagerPackage.AdapterInactive();
	// set the state to HOLDING
	state  = HOLDING;

	// Notify any threads that were waiting in the wait() inside
	// discard_requests. This will cause discard_requests to return
	// (which is in conformance with the spec).
	notifyWaiters(); 

	if ( wait_for_completion ) {
	    while ( state.value() == State._HOLDING && nInvocations > 0 ) {
		countedWait() ;
	    }
	}
    }

    /**
     * <code>discard_requests</code>
     * <b>Spec: pages 3-14 thru 3-18</b>
     */
    public synchronized void discard_requests(boolean wait_for_completion)
        throws org.omg.PortableServer.POAManagerPackage.AdapterInactive
    {
	if ( state.value() == State._INACTIVE )
	    throw new org.omg.PortableServer.POAManagerPackage.AdapterInactive();
	// set the state to DISCARDING
	state = DISCARDING;

	// Notify any invocations that were waiting because the previous
	// state was HOLDING. Those invocations will henceforth be rejected with
	// a TRANSIENT exception. Also notify any threads that were waiting
	// inside hold_requests().
	notifyWaiters(); 

	if ( wait_for_completion ) {
	    while ( state.value() == State._DISCARDING && nInvocations > 0 ) {
		countedWait() ;
	    }
	}
    }

    /**
     * <code>deactivate</code>
     * <b>Spec: pages 3-14 thru 3-18</b>
     * Note: INACTIVE is a permanent state.
     */

    public synchronized void deactivate(boolean etherealize_objects, boolean wait_for_completion)
        throws org.omg.PortableServer.POAManagerPackage.AdapterInactive
    {
	if ( state.value() == State._INACTIVE )
	    throw new org.omg.PortableServer.POAManagerPackage.AdapterInactive();

        state = INACTIVE;

	// Notify any invocations that were waiting because the previous
	// state was HOLDING. Those invocations will then be rejected with
	// an OBJ_ADAPTER exception. Also notify any threads that were waiting
        // inside hold_requests() or discard_requests().
	notifyWaiters();

	if ( wait_for_completion ) {
	    while ( nInvocations > 0 ) { 
		countedWait() ;
	    }
	}

	if ( etherealize_objects ) {
	    if ( wait_for_completion ) {
                // stefanb 12/00: Why was this condition reversed? Spawning new threads means NOT to wait!
                etherealizePOAs();
	    } else {
                new Thread() {
                    public void run() {
                        POAManagerImpl.this.etherealizePOAs();
                    }
                }.start();
	    }	
	} else {
            orb.removePoaManager(this);
            // remove all POAs from poas, so that they can be GC'ed.
            poas.clear();
	}
    }

    /**
     * Added according to the spec CORBA V2.3; this returns the
     * state of the POAManager
     */

    public org.omg.PortableServer.POAManagerPackage.State get_state () {
	return state;
    }


    void etherealizePOAs() {
        Iterator iterator = (new HashSet(poas)).iterator();
        while (iterator.hasNext()) {
	    // if this is a RETAIN+USE_SERVANT_MGR poa
	    // then it must call etherealize for all its objects
            ((POAImpl)iterator.next()).etherealizeAll();
	}
        orb.removePoaManager(this);
        poas.clear();
    }

/****************************************************************************
 * The following methods are used on the invocation path.
 ****************************************************************************/

    // called from POA.find_POA before calling 
    // AdapterActivator.unknown_adapter.
    synchronized void checkIfActive()
    {
	checkState();
    }

    private void checkState()
    {
	while ( state.value() != State._ACTIVE ) {
	    switch ( state.value() ) {
	    case State._HOLDING:
		while ( state.value() == State._HOLDING ) {
		    countedWait() ;
		}
		break;
	    case State._DISCARDING:
		throw new TRANSIENT(MinorCodes.POA_DISCARDING,
				    CompletionStatus.COMPLETED_NO);
	    case State._INACTIVE:
		throw new OBJ_ADAPTER(MinorCodes.POA_INACTIVE,
				      CompletionStatus.COMPLETED_NO);
	    }
	}
    }

    synchronized void enter()
    {
        checkState();
	nInvocations++;
    }

    synchronized void exit()
    {
	nInvocations--; 

	if ( nInvocations == 0 ) {
	    // This notifies any threads that were in the 
	    // wait_for_completion loop in hold/discard/deactivate().
	    notifyWaiters();
	}
    }
}
