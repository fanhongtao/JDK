/*
 * @(#)Util.java	1.27 03/01/23
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

package com.sun.corba.se.internal.javax.rmi.CORBA; // Util (sed marker, don't remove!)

import java.rmi.RemoteException;
import javax.rmi.CORBA.ValueHandler;

import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.Any;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ObjectImpl;

import org.omg.CORBA.TCKind;

import javax.rmi.CORBA.Tie;

import java.util.Hashtable;
import java.util.Enumeration;
import java.io.Serializable;

import java.rmi.MarshalException;
import java.rmi.NoSuchObjectException;
import java.rmi.AccessException;
import java.rmi.Remote;
import java.rmi.ServerError;
import java.rmi.ServerException;
import java.rmi.ServerRuntimeException;

import javax.transaction.TransactionRequiredException;
import javax.transaction.TransactionRolledbackException;
import javax.transaction.InvalidTransactionException;

import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.NO_PERMISSION;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.TRANSACTION_REQUIRED;
import org.omg.CORBA.TRANSACTION_ROLLEDBACK;
import org.omg.CORBA.INVALID_TRANSACTION;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.UnknownException;

import com.sun.corba.se.internal.io.ValueHandlerImpl;
import com.sun.corba.se.internal.util.Utility;
import com.sun.corba.se.internal.util.IdentityHashtable;
import com.sun.corba.se.internal.util.JDKBridge;
import java.io.NotSerializableException;
import java.rmi.UnexpectedException;
import java.rmi.MarshalException;

import java.rmi.server.RMIClassLoader;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

/**
 * Provides utility methods that can be used by stubs and ties to
 * perform common operations.
 */
public abstract class Util implements javax.rmi.CORBA.UtilDelegate {

    // Runs as long as there are exportedServants
    private static KeepAlive keepAlive = null;
    // Maps targets to ties.
    private static IdentityHashtable exportedServants = new IdentityHashtable();
    private static ValueHandlerImpl valueHandlerSingleton = new ValueHandlerImpl();
    
    public abstract RemoteException mapSystemException(SystemException ex);

    public abstract void writeAny(OutputStream out, Object obj);


    /**
     * Reads a java.lang.Object as a CORBA any.
     * @param in the stream from which to read the any.
     * @return the object read from the stream.
     */
    public Object readAny(InputStream in) {

       //d11638 Read the Any
       Any any = in.read_any();
        if ( any.type().kind().value() == TCKind._tk_objref )
	    return any.extract_Object ();
        else
	return any.extract_Value();
    }

    /**
     * Writes a java.lang.Object as a CORBA Object. If <code>obj</code> is
     * an exported RMI-IIOP server object, the tie is found
     * and wired to <code>obj</code>, then written to <code>out.write_Object(org.omg.CORBA.Object)</code>. 
     * If <code>obj</code> is a CORBA Object, it is written to 
     * <code>out.write_Object(org.omg.CORBA.Object)</code>.
     * @param out the stream in which to write the object.
     * @param obj the object to write.
     */
    public void writeRemoteObject(OutputStream out,
                                         java.lang.Object obj) {

        // Make sure we have a connected object, then
        // write it out...
    
        Object newObj = Utility.autoConnect(obj,out.orb(),false);
	out.write_Object((org.omg.CORBA.Object)newObj);
    }
    
    /**
     * Writes a java.lang.Object as either a value or a CORBA Object. 
     * If <code>obj</code> is a value object or a stub object, it is written to 
     * <code>out.write_abstract_interface(java.lang.Object)</code>. If <code>obj</code> is an exported 
     * RMI-IIOP server object, the tie is found and wired to <code>obj</code>,
     * then written to <code>out.write_abstract_interface(java.lang.Object)</code>. 
     * @param out the stream in which to write the object.
     * @param obj the object to write.
     */
    public void writeAbstractObject(OutputStream out,
                                           java.lang.Object obj) {

        // Make sure we have a connected object, then
        // write it out...
    
        Object newObj = Utility.autoConnect(obj,out.orb(),false);
        ((org.omg.CORBA_2_3.portable.OutputStream)out).write_abstract_interface(newObj);
    }
    
    /**
     * Registers a target for a tie. Adds the tie to an internal table and calls
     * {@link Tie#setTarget} on the tie object.
     * @param tie the tie to register.
     * @param target the target for the tie.
     */
    public void registerTarget(javax.rmi.CORBA.Tie tie,
                                      java.rmi.Remote target) {
        
        synchronized (exportedServants) {
            
            // Do we already have this target registered?
            
            if (lookupTie(target) == null) {
                
                // No, so register it and set the target...
                
                exportedServants.put(target,tie);
                tie.setTarget(target);
            
                // Do we need to instantiate our keep-alive thread?
                
                if (keepAlive == null) {
                   
                    // Yes. Instantiate our keep-alive thread and start
                    // it up...

                    keepAlive = (KeepAlive)AccessController.doPrivileged(new PrivilegedAction() {
                        public java.lang.Object run() {
                            return new KeepAlive();
                        }
                    });
                    keepAlive.start();
                }
            }
        }
    }
    
    /**
     * Removes the associated tie from an internal table and calls {@link Tie#deactivate} 
     * to deactivate the object.
     * @param target the object to unexport.
     */
    public void unexportObject(java.rmi.Remote target) 
        throws java.rmi.NoSuchObjectException 
    {
        synchronized (exportedServants) {
            Tie cachedTie = lookupTie(target);
            if (cachedTie != null) {
                exportedServants.remove(target);
                Utility.purgeStubForTie(cachedTie);
		Utility.purgeTieAndServant(cachedTie);
                try {
                    cleanUpTie(cachedTie);
                } catch (BAD_OPERATION e) {
                } catch (org.omg.CORBA.OBJ_ADAPTER e) {
                    // This can happen when the target was never associated with a POA.
                    // We can safely ignore this case.
                }
                // Is it time to shut down our keep alive thread?
                
                if (exportedServants.isEmpty()) {
                
                    // Yep, so shut it down...
                           
                    keepAlive.quit();
                    keepAlive = null;
                }
            } else {
                throw new java.rmi.NoSuchObjectException("Tie not found" );
            }
        }
    }

    protected void unregisterTargetsForORB(ORB orb) 
    {
        for (Enumeration e = exportedServants.keys(); e.hasMoreElements(); ) {
            java.lang.Object key = e.nextElement();
            Remote target = (Remote)(key instanceof Tie ? ((Tie)key).getTarget() : key);
            // Bug 4476347: BAD_OPERATION is thrown if the ties delegate isn't set.
            // We can ignore this because it means the tie is not connected to an ORB.
            try {
                if (orb == getTie(target).orb()) {
                    try {
                        unexportObject(target);
                    } catch( java.rmi.NoSuchObjectException ex ) {
                        // We neglect this exception if at all if it is
                        // raised. It is not harmful.
                    }
                }
            } catch (BAD_OPERATION bad) { /* Ignore */ }
        }
    }

    // Needed to be overridden by subclass in the POA package
    protected void cleanUpTie(Tie cachedTie) 
        throws java.rmi.NoSuchObjectException 
    {
        cachedTie.setTarget(null);
        cachedTie.deactivate();
    }
    
    /**
     * Returns the tie (if any) for a given target object.
     * @return the tie or null if no tie is registered for the given target.
     */
    public Tie getTie (Remote target) {

        synchronized (exportedServants) {
            return lookupTie(target);
        }
    }

    /**
     * An unsynchronized version of getTie() for internal use.
     */
    private static Tie lookupTie (Remote target) {
        Tie result = (Tie)exportedServants.get(target);
        if (result == null && target instanceof Tie) {
            if (exportedServants.contains(target)) {
                result = (Tie)target;
            }
        }
        return result;
    }

    /**
     * Returns a singleton instance of a class that implements the
     * {@link ValueHandler} interface. 
     * @return a class which implements the ValueHandler interface.
     */
    public ValueHandler createValueHandler() {

        return valueHandlerSingleton;
    }

    /**
     * Returns the codebase, if any, for the given class. 
     * @param clz the class to get a codebase for.
     * @return a space-separated list of URLs, or null.
     */
    public String getCodebase(java.lang.Class clz) {
        return RMIClassLoader.getClassAnnotation(clz);
    }

    /**
     * Returns a class instance for the specified class. 
     * @param className the name of the class.
     * @param remoteCodebase a space-separated list of URLs at which
     * the class might be found. May be null.
     * @param loadingContext a class whose ClassLoader may be used to
     * load the class if all other methods fail.
     * @return the <code>Class</code> object representing the loaded class.
     * @exception ClassNotFoundException if class cannot be loaded.
     */
    public Class loadClass(String className,
                                  String remoteCodebase,
                                  ClassLoader loader)
	throws ClassNotFoundException {
        return JDKBridge.loadClass(className,remoteCodebase,loader);                                
    }


     /**
     * The <tt>isLocal</tt> method has the same semantics as the ObjectImpl._is_local
     * method, except that it can throw a RemoteException.
     * (no it doesn't but the spec says it should.)
     *   
     * The <tt>_is_local()</tt> method is provided so that stubs may determine if a
     * particular object is implemented by a local servant and hence local
     * invocation APIs may be used.
     * 
     * @param stub the stub to test.
     *
     * @return The <tt>_is_local()</tt> method returns true if
     * the servant incarnating the object is located in the same process as
     * the stub and they both share the same ORB instance.  The <tt>_is_local()</tt>
     * method returns false otherwise. The default behavior of <tt>_is_local()</tt> is
     * to return false.
     *
     * @throws RemoteException The Java to IDL specification does to
     * specify the conditions that cause a RemoteException to be thrown.
     */
    public boolean isLocal(javax.rmi.CORBA.Stub stub) throws RemoteException {
	return false ;
    }
    
    /**
     * Wraps an exception thrown by an implementation
     * method.  It returns the corresponding client-side exception. 
     * @param orig the exception to wrap.
     * @return the wrapped exception.
     */
    public RemoteException wrapException(Throwable orig) {

        if (orig instanceof SystemException) {
            return mapSystemException((SystemException)orig);
        }
    	
    	if (orig instanceof Error) {
    	    return new ServerError("Error occurred in server thread",(Error)orig);   
    	} else if (orig instanceof RemoteException) {
	    return new ServerException("RemoteException occurred in server thread",
				       (Exception)orig);   
    	} else if (orig instanceof RuntimeException) {
            throw (RuntimeException) orig;
    	}    	
        
        return new UnexpectedException(orig.toString());
    }

    /**
     * Copies or connects an array of objects. Used by local stubs
     * to copy any number of actual parameters, preserving sharing
     * across parameters as necessary to support RMI semantics.
     * @param obj the objects to copy or connect.
     * @param orb the ORB.
     * @return the copied or connected objects.
     * @exception RemoteException if any object could not be copied or connected.
     */
    public Object[] copyObjects (Object[] obj, ORB orb)
	throws RemoteException {
    
        boolean stringPresent = false;
        org.omg.CORBA_2_3.portable.OutputStream out = null;

        try {
       
            // Allocate a new array if we need to...
	    
            if (obj.getClass().getComponentType() != Object.class) {
                Object[] orig = obj;
                obj = new Object[obj.length];
                System.arraycopy(orig,0,obj,0,obj.length);
            }
	    
            // Decide what, if any, types need to be copied
            // and do so. Do not write Strings in this pass,
            // but connect any Remote objects...
            
            for (int i = 0; i < obj.length; i++) {
                Object it = obj[i];
                if (it == null) {
                    // Do nothing
                } else if (it instanceof SystemException) {
                    try {
			SystemException inEx = (SystemException)it;
			SystemException outEx = (SystemException)inEx.getClass().newInstance();
			outEx.minor = inEx.minor;
			outEx.completed = inEx.completed;
			obj[i] = outEx;
                    } catch (Exception e) {
                        throw new UnexpectedException(obj.toString());
                    }
                } else if (it instanceof Remote) {
                    
                    // Make sure it is connected and converted to
                    // a stub (if needed)...
                    
                    obj[i] = Utility.autoConnect(it,orb,true);
                    
                } else if (it instanceof String) {
                    stringPresent = true;
                } else if (it instanceof org.omg.CORBA.Object) {
                } else {
                    
                    // It's a value, so must be copied. First, make sure
                    // we've got our output stream...
                    
                    if (out == null) {
                        out = (org.omg.CORBA_2_3.portable.OutputStream)orb.create_output_stream();
                    }
                    
                    // Now write it...
                    
                    out.write_value((Serializable)it); 
                }
            }
            
            // Did we write anything? If not, we're done...
            
            if (out != null) {
            
                // Yes, did we find any strings? If so, they must also be
                // written...
            
                if (stringPresent) { 
                    for (int i = 0; i < obj.length; i++) {
                        if (obj[i] instanceof String) {
                            out.write_value((String)obj[i]);
                        }
                    }
                }
             
                // Ok, now read everything that we wrote back in
                // and update the array...
                
                org.omg.CORBA_2_3.portable.InputStream in = 
                    (org.omg.CORBA_2_3.portable.InputStream)out.create_input_stream();
                
                for (int i = 0; i < obj.length; i++) {
                    Object it = obj[i];
                    if (it == null) {
                    } else if (it instanceof SystemException) {
                    } else if (it instanceof Remote) {
                    } else if (it instanceof String) {
                    } else if (it instanceof org.omg.CORBA.Object) {
                    } else {
                        obj[i] = in.read_value(); 
                    }
                }
                
                if (stringPresent) {
                    for (int i = 0; i < obj.length; i++) {
                        if (obj[i] instanceof String) {
                            obj[i] = in.read_value();
            }
                    }
                }
            }
        } catch (ClassCastException ex) {
            throw new MarshalException("Exception occurred in server thread",
				       new NotSerializableException());
        } catch (SystemException ex) {
            throw mapSystemException(ex);
        }
           
        return obj;
    }

    /**
     * Copies or connects an object. Used by local stubs to copy 
     * an actual parameter, result object, or exception.
     * @param obj the object to copy.
     * @param orb the ORB.
     * @return the copy or connected object.
     * @exception RemoteException if the object could not be copied or connected.
     */
    public Object copyObject (Object obj, ORB orb)
	throws RemoteException {

        // Is it null?
        
        if (obj == null) {
            
            // Yes, so do nothing...
            
            return obj;
        }

        // Is it a SystemException?
        
        if (obj instanceof SystemException) {
           
	    // Yes, so copy it monomorphically...
           
	    try {
		SystemException in = (SystemException)obj;
		SystemException out = (SystemException)in.getClass().newInstance();
		out.minor = in.minor;
		out.completed = in.completed;
		return out;
	    } catch (Exception e) {
                throw new UnexpectedException(obj.toString());
	    }
        }
        
        // Is it a String?
        
        if (obj instanceof String) {
        
            // Yes, so do nothing...
            
            return obj;   
        }
        
        // Is it Remote?
        
        if (obj instanceof Remote) {
            
            // Yes, so make sure it is connected and converted
            // to a stub (if needed)...
            
            return Utility.autoConnect(obj,orb,true);
            }
 
        // Is it a CORBA object?
        
        if (obj instanceof org.omg.CORBA.Object) {
        
            // Yes, so do nothing...
            
            return obj;   
        }
  
        // Is it a single-dimensional primitive array?
        
        Class componentType = obj.getClass().getComponentType();
        if (componentType != null && componentType.isPrimitive()) {
        
            // Yes, so clone it...

            if (componentType == boolean.class) return ((boolean[])obj).clone();         
            if (componentType == byte.class) return ((byte[])obj).clone();         
            if (componentType == char.class) return ((char[])obj).clone();         
            if (componentType == short.class) return ((short[])obj).clone();         
            if (componentType == int.class) return ((int[])obj).clone();         
            if (componentType == long.class) return ((long[])obj).clone();         
            if (componentType == float.class) return ((float[])obj).clone();         
            if (componentType == double.class) return ((double[])obj).clone();                     
        }
  
        // Must be a value type. Note that types which are normally
        // marshalled as ANYs are handled here...
 
        try {
            org.omg.CORBA_2_3.portable.OutputStream out = 
                (org.omg.CORBA_2_3.portable.OutputStream)orb.create_output_stream();
                    
            out.write_value((Serializable)obj);
                
            org.omg.CORBA_2_3.portable.InputStream in = 
                (org.omg.CORBA_2_3.portable.InputStream)out.create_input_stream();
                
            return in.read_value();
        } catch (ClassCastException ex) {
            throw new MarshalException("Exception occurred in server thread",
				       new NotSerializableException());
        } catch (SystemException ex) {
            throw mapSystemException(ex);
        }
    }

}
	    
class KeepAlive extends Thread {

    boolean quit = false;
    
    public KeepAlive () {
        setDaemon(false);
    }
    
    public synchronized void run () {
        while (!quit) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
}
    
    public synchronized void quit () {
        quit = true;
        notifyAll();
    }  
}
