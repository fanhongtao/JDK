/*
 * @(#)RemoteStub.java	1.10 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
