/*
 * Action.java
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.orbutil.fsm;

/**
 * Description goes here
 *
 * @version @(#)Action.java	1.11 03/12/19
 * @author Ken Cavanaugh
 */
public interface Action
{
	/** Called by the state engine to perform an action
	* before a state transition takes place.  The FSM is 
	* passed so that the Action may set the next state in
	* cases when that is required.  FSM and Input together
	* allow actions to be written that depend on the state and
	* input, but this should generally be avoided, as the 
	* reason for a state machine in the first place is to cleanly
	* separate the actions and control flow.   Note that an
	* action should complete in a timely manner.  If the state machine
	* is used for concurrency control with multiple threads, the
	* action must not allow multiple threads to run simultaneously
	* in the state machine, as the state could be corrupted.
	* Any exception thrown by the Action for the transition
	* will be propagated to doIt.  
	* @param FSM fsm is the state machine causing this action.
	* @param Input in is the input that caused the transition.
	*/
	public void doIt( FSM fsm, Input in ) ;
}

// end of Action.java

