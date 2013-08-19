/*
 * @(#)PortableRemoteObject.java	1.8 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Licensed Materials - Property of IBM
 * RMI-IIOP v1.0
 * Copyright IBM Corp. 1998 1999  All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

package com.sun.corba.se.internal.javax.rmi;	

import java.lang.reflect.Method ;

import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.Stub;

import java.rmi.RemoteException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;

import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.SystemException;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.RemoteStub;
import java.rmi.server.ExportException;

import java.net.URL;

import com.sun.corba.se.internal.util.JDKBridge;
import com.sun.corba.se.internal.util.Utility;
import com.sun.corba.se.internal.util.RepositoryId;

import java.security.AccessController;
import sun.security.action.GetPropertyAction;

/**
 * Server implementation objects may either inherit from
 * javax.rmi.PortableRemoteObject or they may implement a remote interface
 * and then use the exportObject method to register themselves as a server object.
 * The toStub method takes a server implementation and returns a stub that
 * can be used to access that server object.
 * The connect method makes a Remote object ready for remote communication.
 * The unexportObject method is used to deregister a server object, allowing it to become
 * available for garbage collection.
 * The narrow method takes an object reference or abstract interface type and 
 * attempts to narrow it to conform to
 * the given interface. If the operation is successful the result will be an
 * object of the specified type, otherwise an exception will be thrown.
 */
public class PortableRemoteObject 
	implements javax.rmi.CORBA.PortableRemoteObjectDelegate {

    /**
     * Makes a server object ready to receive remote calls. Note
     * that subclasses of PortableRemoteObject do not need to call this
     * method, as it is called by the constructor.
     * @param obj the server object to export.
     * @exception RemoteException if export fails.
     */
    public void exportObject(Remote obj)
	throws RemoteException {

        if (obj == null) {
            throw new NullPointerException("invalid argument");
        }
    
        // Has this object already been exported to IIOP?
        
        if (Util.getTie(obj) != null) {
        
            // Yes, so this is an error...
            
            throw new ExportException (obj.getClass().getName() + " already exported");
        }
        
        // Can we load a Tie?
        
        Tie theTie = Utility.loadTie(obj);
        
        if (theTie != null) {
        
            // Yes, so export it to IIOP...
            
            Util.registerTarget(theTie,obj);
            
        } else {
            
            // No, so export to JRMP. If this is called twice for the
            // same object, it will throw an ExportException...
            
	    UnicastRemoteObject.exportObject(obj);
        }
    }
   
    /**
     * Returns a stub for the given server object.
     * @param obj the server object for which a stub is required. Must either be a subclass
     * of PortableRemoteObject or have been previously the target of a call to
     * {@link #exportObject}. 
     * @return the most derived stub for the object.
     * @exception NoSuchObjectException if a stub cannot be located for the given server object.
     */
    public Remote toStub (Remote obj) 
	throws NoSuchObjectException {
       
        Remote result = null;
        
        if (obj == null) {
            throw new NullPointerException("invalid argument");
        }
        
        // If the class is already an IIOP stub then return it.

        if (obj instanceof javax.rmi.CORBA.Stub) {
            return obj;
        }
        
        // If the class is already a JRMP stub then return it.

        if (obj instanceof java.rmi.server.RemoteStub) {
            return obj;
        }
            
        // Has it been exported to IIOP?
        
        Tie theTie = Util.getTie(obj);
        
        if (theTie != null) {
            
            // Yes, so load a stub for it...
            
            result = Utility.loadStub(theTie,null,null,true);

        } else {
            
            // No. Can we load a tie for it?
            
            if (Utility.loadTie(obj) == null) {
            
                // No, so ask JRMP to find the stub. The 1.1.6 and 1.2
                // implementations differ, and have been built into
                // the JDKBridge class...
                
                result = java.rmi.server.RemoteObject.toStub(obj);
            }
        }
        
        if (result == null) {
            throw new NoSuchObjectException("object not exported");
        }
        
        return result;
    }

    /**
     * Deregisters a server object from the runtime, allowing the object to become
     * available for garbage collection.
     * @param obj the object to unexport.
     * @exception NoSuchObjectException if the remote object is not
     * currently exported.
     */
    public void unexportObject(Remote obj) 
	throws NoSuchObjectException {
	    
        if (obj == null) {
            throw new NullPointerException("invalid argument");
        }
        
        // Was a stub passed?
        
        if (obj instanceof javax.rmi.CORBA.Stub ||
            obj instanceof java.rmi.server.RemoteStub) {
            throw new NoSuchObjectException("Can only unexport a server object.");
        }
        
        // Was this object exported to IIOP?
        
        Tie theTie = Util.getTie(obj);
        
        if (theTie != null) {
            
            // Yes, so unexport from IIOP...
            
	    Util.unexportObject(obj);
	        
	} else {
	        
	    // No, can we find a Tie for it?
	        
	    if (Utility.loadTie(obj) == null) {
	        
    	        // No, so unexport from JRMP. This is only possible when running
    	        // on a 1.2 vm, because 1.1 had no unexport API method, so we
    	        // use a method on JDKBridge which is compiled on 1.2.  If we're on
    	        // 1.1 it does nothing.
    	        
    	        UnicastRemoteObject.unexportObject(obj,true);
    	        
            } else {
                
                // Yes, so someone is trying to unexport an IIOP object that
                // was never exported...
                
                throw new NoSuchObjectException("Object not exported.");
            }
	}
    }

    /**
     * Checks to ensure that an object of a remote or abstract interface type
     * can be cast to a desired type.
     * @param narrowFrom the object to check.
     * @param narrowTo the desired type.
     * @return an object which can be cast to the desired type.
     * @throws ClassCastException if narrowFrom cannot be cast to narrowTo.
     */
    public java.lang.Object narrow ( java.lang.Object narrowFrom,
                                            java.lang.Class narrowTo)
	throws ClassCastException {

        java.lang.Object result = null;

        if (narrowFrom == null) {
            return null;
        }

        if (narrowTo == null) {
            throw new NullPointerException("invalid argument");
        }

	Class narrowFromClass  = narrowFrom.getClass();
        
        try { 
            
            // Will a cast work?

            if (narrowTo.isAssignableFrom(narrowFromClass)) {
                
                // Yep, so we're done...
                
                result = narrowFrom;

            } else {
            
                // No. Is narrowTo an interface that might be
                // implemented by a servant running on iiop?
                
                if (narrowTo.isInterface() && 
// What was this test supposed to achieve?
//                  narrowTo != java.rmi.Remote.class &&
                    narrowTo != java.io.Serializable.class &&
                    narrowTo != java.io.Externalizable.class) {
            
                    // Yes. Ok, so assume the current stub (narrowFrom) is an
                    // ObjectImpl (it should be a _"xxx"_Stub). If it is not,
                    // we'll catch it below and end up failing with a
                    // ClassCastException...
                    
                    org.omg.CORBA.portable.ObjectImpl narrowObj 
                        = (org.omg.CORBA.portable.ObjectImpl) narrowFrom;                
                    
                    // Create an id from the narrowTo type...
                    
                    String id = RepositoryId.createForAnyType(narrowTo);
                    
                    // Can the server act as the narrowTo type?
                    
                    if (narrowObj._is_a(id)) {
                    
                        // Yes, so try to load a stub for it...
                        
                        result = Utility.loadStub(narrowObj,narrowTo);
                    }
                }
            }
        } catch(Exception error) {
            result = null;
        }
        
        if (result == null) {
            throw new ClassCastException();
        }
        
        return result;
    }
 
    /**
     * Makes a Remote object ready for remote communication. This normally
     * happens implicitly when the object is sent or received as an argument
     * on a remote method call, but in some circumstances it is useful to
     * perform this action by making an explicit call.  See the 
     * {@link Stub#connect} method for more information. 
     * @param target the object to connect.
     * @param source a previously connected object.
     * @throws RemoteException if <code>source</code> is not connected
     * or if <code>target</code> is already connected to a different ORB than
     * <code>source</code>.
     */
    public void connect (Remote target, Remote source)
	throws RemoteException {
    
        if (target == null || source == null) {
            throw new NullPointerException("invalid argument");
        }
 
        // See if we can get an ORB from the 'source'
        // object...
 
        ORB orb = null;
        
        try {
            if (source instanceof ObjectImpl) {
                orb = ((ObjectImpl)source)._orb();
            } else {
                
                // Is this a servant that was exported to iiop?
                
                Tie tie = Util.getTie(source);
                if (tie == null) {
                    
                    // No, can we get a tie for it?  If not,
                    // assume that source is a JRMP object...
                    
                    if (Utility.loadTie(source) != null) {
                    
                        // Yes, so it is an iiop object which
                        // has not been exported...
                        
                        throw new RemoteException("'source' object not exported"); 
                    }
                } else {
                    orb = tie.orb();
                }
            }
        } catch (SystemException e) {
                
            // The object was not connected...
            
            throw new RemoteException("'source' object not connected"); 
        }

        // Decide what the target object is...
        
        boolean targetIsIIOP;
        Tie targetTie = null;
        if (target instanceof java.rmi.server.RemoteStub) {
            targetIsIIOP = false;
        } else if (target instanceof javax.rmi.CORBA.Stub) {
            targetIsIIOP = true;
        } else {
            
            // Is it a servant exported to iiop?
            
            targetTie = Util.getTie(target);
            if (targetTie != null) {
                targetIsIIOP = true;
            } else {

                // No. Can we get a tie for it?
                
                if (Utility.loadTie(target) != null) {
                    
                    // Yes, so it is an iiop servant that has not
                    // been exported...
                    
                    throw new RemoteException("'target' servant not exported");
                    
                } else {
                    
                    // No, so not iiop...
                    
                    targetIsIIOP = false;
                }    
            }
        }

        // Ok, is the target object JRMP?
        
        if (!targetIsIIOP) {
            
            // Yes. Do we have an ORB from the source object? 
            // If not, we're done - there is nothing to do to
            // connect a JRMP object. If so, it is an error because
            // the caller mixed JRMP and IIOP...
            
            if (orb != null) {
                throw new RemoteException("'source' object exported to IIOP, 'target' is JRMP");                
            }
        } else {
            
            // The target object is IIOP. Make sure we have a
            // valid ORB from the source object...
            
            if (orb == null) {
                throw new RemoteException("'source' object is JRMP, 'target' is IIOP");                
            }
            
            // And, finally, connect it up...
            
            try {
                if (targetTie != null) {
                
                    // Is the tie already connected?
                    
                    try {
                        ORB existingOrb = targetTie.orb();
                        
                        // Yes. Is it the same orb? 
                        
                        if (existingOrb == orb) {
                            
                            // Yes, so nothing to do...
                            
                            return;
                            
                        } else {
                            
                            // No, so this is an error...
                            
                            throw new RemoteException("'target' object was already connected");
                        }
                    } catch (SystemException e) {}
                    
                    // No, so do it...
                
                    targetTie.orb(orb);
                
                } else {
                    ((javax.rmi.CORBA.Stub)target).connect(orb);
                }
            } catch (SystemException e) {
                
                // The stub or tie was already connected...
                
                throw new RemoteException("'target' object was already connected"); 
            }
        }
    }

}
