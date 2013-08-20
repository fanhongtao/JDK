/*
 * @(#)ServerRequestDispatcher.java	1.3 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.pept.protocol;

import com.sun.corba.se.pept.protocol.MessageMediator;

/**
 * <code>ServerRequestDispatcher</code> coordinates the request (and possible
 * response) processing for a specific <em>protocol</em>.

 * @author Harold Carr
 */
public interface ServerRequestDispatcher
{
    /**
     * This method coordinates the processing of a message received
     * on the server side.
     *
     * For example, this may involve finding an "object adapter" which
     * would return Ties/Servants to handle the request.
     */
    public void dispatch(MessageMediator messageMediator);
}

// End of file.
