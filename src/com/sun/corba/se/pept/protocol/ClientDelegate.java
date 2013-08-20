/*
 * @(#)ClientDelegate.java	1.11 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.pept.protocol;

import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.transport.ContactInfoList;

/**
 * <p>The presentation block interacts with the PEPt architecture
 * via the <code>ClientDelegate</code>.</p>
 *
 * @author Harold Carr
 */
public interface ClientDelegate 
{
    /**
     * The {@link com.sun.corba.se.pept.broker.Broker Broker} associated
     * with an invocation.
     *
     * @return {@link com.sun.corba.se.pept.broker.Broker Broker}
     */
    public Broker getBroker();

    /**
     * Get the
     * {@link com.sun.corba.se.pept.transport.ContactInfoList ContactInfoList}
     * which represents they encoding/protocol/transport combinations that
     * may be used to contact the service.
     *
     * @return 
     * {@link com.sun.corba.se.pept.transport.ContactInfoList ContactInfoList}
     */
    public ContactInfoList getContactInfoList();
}

// End of file.
