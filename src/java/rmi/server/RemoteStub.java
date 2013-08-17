/*
 * @(#)RemoteStub.java	1.9 98/08/12
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package java.rmi.server;

/**
 * The RemoteStub class is the common superclass to all client stubs
 * and provides the framework to support a wide range of remote
 * reference semantics.  Stub objects are surrogates that support
 * exactly the same set of remote interfaces defined by the actual
 * implementation of the remote object.
 */
abstract public class RemoteStub extends RemoteObject {

    private static final long serialVersionUID = -1585587260594494182L;

    /**
     * Constructor for RemoteStub.
     */
    protected RemoteStub() {
    	super();
    }
    
    /**
     * Constructor for RemoteStub, with the specified remote reference.
     */
    protected RemoteStub(RemoteRef ref) {
	super(ref);
    }

    static protected void setRef(RemoteStub stub, RemoteRef ref)
    {
	stub.ref = ref;
    }
}
