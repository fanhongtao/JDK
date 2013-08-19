/*
 * @(#)BufferManagerFactory.java	1.12 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop;

import com.sun.corba.se.internal.iiop.BufferManagerReadGrow;
import com.sun.corba.se.internal.iiop.BufferManagerWriteGrow;
import com.sun.corba.se.internal.iiop.BufferManagerWriteCollect;
import com.sun.corba.se.internal.iiop.BufferManagerWriteStream;
import com.sun.corba.se.internal.iiop.Connection;
import com.sun.corba.se.internal.iiop.IIOPOutputStream;
import com.sun.corba.se.internal.iiop.IIOPInputStream;
import com.sun.corba.se.internal.core.GIOPVersion;
import com.sun.corba.se.internal.corba.ORB;
import com.sun.corba.se.internal.orbutil.ORBConstants;

import org.omg.CORBA.INTERNAL;

/**
 * Creates read/write buffer managers to handle over/under flow
 * in CDR*putStream.
 */

public class BufferManagerFactory
{

    public static final int GROW    = 0;
    public static final int COLLECT = 1;
    public static final int STREAM  = 2;


    /**
     * Case: Given to ClientRequest (who gives it to CDROutputStream)
     *       constructor.
     *
     * ClientDelegate.createRequest - remote case
     *
     * newBufferManagerWrite(conn);
     *  new ClientRequestImpl(.... writeBufferManager ...);
     *    IIOPOutputStream constructor
     *      CDROutputStream constructor
     *
     * Does:
     *
     *  this.connection = connection;
     *
     *  If collecting (i.e., GIOP 1.2 phase one implementation):
     *     this.bufQ = new BufQ();
     *
     * This will return a different concrete instance depending
     * on the strategy: grow, collect or stream.
     *
     * This given connection is ignored in all cases except streaming.
     */

    /*
    public static BufferManagerWrite newBufferManagerWrite(int strategy,
							   Connection connection)
    {
	switch (strategy) {
	case GROW : return new BufferManagerWriteGrow();
	default   : throw new INTERNAL();
	}
    }
    */

    // The next two methods allow creation of BufferManagers based on GIOP version.
    // We may want more criteria to be involved in this decision.
    // These are only used for sending messages (so could be fragmenting)
    public static BufferManagerRead newBufferManagerRead(GIOPVersion version,
                                                         org.omg.CORBA.ORB orb)
    {
        // REVISIT - On the reading side, shouldn't we monitor the incoming
        // fragments on a given connection to determine what fragment size
        // they're using, then use that ourselves?

        switch (version.intValue()) 
        {
            case GIOPVersion.VERSION_1_0:
                return new BufferManagerReadGrow();
            case GIOPVersion.VERSION_1_1:
            case GIOPVersion.VERSION_1_2:
                return new BufferManagerReadStream();
            default:
                // REVISIT - what is appropriate?
                throw new INTERNAL();
        }
    }

    public static BufferManagerWrite newBufferManagerWrite(GIOPVersion version,
                                                           org.omg.CORBA.ORB orb,
                                                           int size)
    {
        int strategy;

        switch (version.intValue()) 
        {
            case GIOPVersion.VERSION_1_0:
                return new BufferManagerWriteGrow(size);
            case GIOPVersion.VERSION_1_1:
                if (orb != null)
                    strategy = ((ORB)orb).getGIOPBuffMgrStrategy(version);
                else
                    strategy = ORBConstants.DEFAULT_GIOP_11_BUFFMGR;

                if (strategy == GROW) 
                    return new BufferManagerWriteGrow(size);
                else
                    return new BufferManagerWriteCollect(size);
            case GIOPVersion.VERSION_1_2:
                if (orb != null) 
                    strategy = ((ORB)orb).getGIOPBuffMgrStrategy(version);
                else
                    strategy = ORBConstants.DEFAULT_GIOP_12_BUFFMGR;

                if (strategy == GROW)
                    return new BufferManagerWriteGrow(size);
                else
                if (strategy == COLLECT)
                    return new BufferManagerWriteCollect(size);
                else
                    return new BufferManagerWriteStream(size);

            default:
                // REVISIT - what is appropriate?
                throw new INTERNAL();
        }
    }
    
    public static BufferManagerWrite newBufferManagerWrite(GIOPVersion version,
                                                           org.omg.CORBA.ORB orb)
    {
        int size;
        int strategy;

        switch (version.intValue()) 
        {
            case GIOPVersion.VERSION_1_0:
                if (orb != null)
                    size = ((ORB)orb).getGIOPBufferSize();
                else
                    size = ORBConstants.GIOP_DEFAULT_BUFFER_SIZE;
                return new BufferManagerWriteGrow(size);
            case GIOPVersion.VERSION_1_1:
                if (orb != null){
                    size = ((ORB)orb).getGIOPFragmentSize();
                    strategy = ((ORB)orb).getGIOPBuffMgrStrategy(version);
                }
                else {
                    size = ORBConstants.GIOP_DEFAULT_FRAGMENT_SIZE;
                    strategy = ORBConstants.DEFAULT_GIOP_11_BUFFMGR;
                }
                if (strategy == GROW) {
                    return new BufferManagerWriteGrow(size); //"Fragment" size is initial size;
                }
                return new BufferManagerWriteCollect(size);
            case GIOPVersion.VERSION_1_2:
                if (orb != null){
                    size = ((ORB)orb).getGIOPFragmentSize();
                    strategy = ((ORB)orb).getGIOPBuffMgrStrategy(version);
                }
                else {
                    size = ORBConstants.GIOP_DEFAULT_FRAGMENT_SIZE;
                    strategy = ORBConstants.DEFAULT_GIOP_12_BUFFMGR;
                }
                if (strategy == GROW) {
                    return new BufferManagerWriteGrow(size); //"Fragment" size is initial size;
                } if (strategy == COLLECT) {
                    return new BufferManagerWriteCollect(size);
                }
                return new BufferManagerWriteStream(size);
            default:
                // REVISIT - what is appropriate?
                throw new INTERNAL();
        }
    }

    // The next two methods are used for non-fragmenting cases only

    // REVISIT - this downcasting is really bad.  TypeCodeImpl passes an
    // ORBSingleton in here.  We should have a different constructor on
    // CDR that takes one and just uses the constant buffer size.
    public static BufferManagerWrite defaultBufferManagerWrite(org.omg.CORBA.ORB orb) {
        if (orb == null || !(orb instanceof com.sun.corba.se.internal.corba.ORB))
            return new BufferManagerWriteGrow(ORBConstants.GIOP_DEFAULT_BUFFER_SIZE);
        else
            return new BufferManagerWriteGrow(((ORB)orb).getGIOPBufferSize());
    }

    public static BufferManagerRead defaultBufferManagerRead() {
        return new BufferManagerReadGrow();
    }

    /**
     * Case: Called from ReaderThread.
     *       The BufferManagerRead instance is given to the
     *       ServerRequest constructor.
     *
     * IIOPConnection.createInputStream
     *
     * newBufferManagerRead()
     *   ClientResponseImpl(... BufferManagerRead ...)
     *     IIOPInputStream constructor
     *        CDRInputStream constructor
     *
     *  Does:
     *
     *   this.bufQ = new BufQ();
     *
     * This will return a different concrete instance
     * depending on the strategy: grow, collect or stream.
     */

    /*
    public static BufferManagerRead newBufferManagerRead (int strategy)
    {
	switch (strategy) {
	case GROW : return new BufferManagerReadGrow();
	default   : throw new INTERNAL();
	}
    }
    */

}
