/*
 * @(#)ResponseWaitingRoom.java	1.15 05/11/17
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.pept.transport;

import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.encoding.InputObject;

/**
 * @author Harold Carr
 */
public interface ResponseWaitingRoom 
{
    public void registerWaiter(MessageMediator messageMediator);

    // REVISIT: maybe return void (or MessageMediator).
    public InputObject waitForResponse(MessageMediator messageMediator);
    
    public void responseReceived(InputObject inputObject);

    public void unregisterWaiter(MessageMediator messageMediator);

    public int numberRegistered();
}

// End of file.

