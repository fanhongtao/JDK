/*
 * @(#)RemoteStub.java	1.7 96/11/18
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
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
