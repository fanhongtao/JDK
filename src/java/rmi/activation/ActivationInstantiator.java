/*
 * @(#)ActivationInstantiator.java	1.7 98/07/08
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.rmi.activation;

import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * An <code>ActivationInstantiator</code> is responsible for creating
 * instances of "activatable" objects. A concrete subclass of
 * <code>ActivationGroup</code> implements the <code>newInstance</code>
 * method to handle creating objects within the group.
 *
 * @author 	Ann Wollrath
 * @version	1.7, 07/08/98
 * @see 	ActivationGroup
 * @since	JDK1.2
 */
public interface ActivationInstantiator extends Remote {

   /**
    * The activator calls an instantiator's <code>newInstance</code>
    * method in order to recreate in that group an object with the
    * activation identifier, <code>id</code>, and descriptor,
    * <code>desc</code>. The instantiator is responsible for: <ul>
    *
    * <li> determining the class for the object using the descriptor's
    * <code>getClassName</code> method,
    *
    * <li> loading the class from the code location obtained from the
    * descriptor (using the <code>getLocation</code> method),
    *
    * <li> creating an instance of the class by invoking the special
    * "activation" constructor of the object's class that takes two
    * arguments: the object's <code>ActivationID</code>, and the
    * <code>MarshalledObject</code> containing object specific
    * initialization data, and
    *
    * <li> returning a MarshalledObject containing the stub for the
    * remote object it created <p>
    *
    * @param id the object's activation identifier
    * @param desc the object's descriptor
    * @return a marshalled object containing the serialized
    * representation of remote object's stub
    * @exception ActivationException if object activation fails
    * @exception RemoteException if remote call fails
    * @since JDK1.2
    */
    public MarshalledObject newInstance(ActivationID id, ActivationDesc desc)
	throws ActivationException, RemoteException;

}
