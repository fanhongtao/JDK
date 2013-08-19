/*
 * @(#)Utility.java	1.35 03/01/23
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

package com.sun.corba.se.internal.util;

import org.omg.CORBA.SystemException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.UserException;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.Principal;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.ValueFactory;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.portable.Streamable;

import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.rmi.Remote;
import org.omg.CORBA.ORB;
import org.omg.CORBA.DATA_CONVERSION;
import javax.rmi.PortableRemoteObject;
import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.Delegate;
import java.rmi.server.RemoteStub;
import org.omg.CORBA.portable.ObjectImpl;
import java.rmi.NoSuchObjectException;
import org.omg.CORBA.INV_OBJREF;
import com.sun.org.omg.SendingContext.CodeBase;
import java.rmi.RemoteException;
import java.io.Serializable;
import java.io.File;
import java.io.FileInputStream;
import org.omg.PortableServer.POA;

// import com.sun.corba.se.internal.util.RepositoryId; // d11638

/**
 *  Handy class full of static functions.
 */
public final class Utility {

    public static final String STUB_PREFIX = "_";
    public static final String RMI_STUB_SUFFIX = "_Stub";
    public static final String IDL_STUB_SUFFIX = "Stub";
    public static final String TIE_SUFIX = "_Tie";
    public static final String STUB_PACKAGE_PREFIX = "org.omg.stub.";
    private static IdentityHashtable tieCache = new IdentityHashtable();
    private static IdentityHashtable tieToStubCache = new IdentityHashtable();
    private static IdentityHashtable stubToTieCache = new IdentityHashtable();
    private static Object CACHE_MISS = new Object();
  
    /**
     * Ensure that stubs, ties, and implementation objects
     * are 'connected' to the runtime. Converts implementation
     * objects to a type suitable for sending on the wire.
     * @param obj the object to connect.
     * @param orb the ORB to connect to if obj is exported to IIOP.
     * @param convertToStub true if implementation types should be
     * converted to Stubs rather than just org.omg.CORBA.Object.
     * @return the connected object.
     * @exception NoSuchObjectException if obj is an implementation
     * which has not been exported.
     */
    public static Object autoConnect(Object obj, ORB orb, boolean convertToStub) {
      
        // Is it null?
        
        if (obj == null) {
            
            // Yes, so just return it.
            
            return obj;
        }
        
        // Is it an RMI Stub?
        
        if (obj instanceof Stub) {
            
            // Yes, is it connected?
            
            Stub it = (Stub)obj;
            try {
                it._get_delegate();
            } catch (BAD_OPERATION okay) {
                    
                // No, so do it...
                    
                try {
                    it.connect(orb);
                } catch (RemoteException e) {
               
                    // The stub could not be connected because it
                    // has an invalid IOR...
                    
                    throw new INV_OBJREF(obj.getClass().getName()+
			 " did not originate from connected object");
                }
            }                
            
            // Return the connected stub...
            
            return obj;
        }
        
        // Is it a Remote object?
        
        if (obj instanceof Remote) {
	        
	    // Yes. Is this an exported IIOP implemenation
	    // object?
        
	    Remote remoteObj = (Remote)obj;
            Tie theTie = Util.getTie(remoteObj);
            if (theTie != null) {
                
                // Yes, is it connected?
                
                try {
                    theTie.orb();
                } catch (BAD_OPERATION okay) {
                    
                    // No, so do it...
                    
                    theTie.orb(orb);               
                }                
               
                if (convertToStub) {
                    
                    // Return a stub for this object...
                    
                    Object result = loadStub(theTie,null,null,true);  // d10926
                    if (result != null) {
                        return result;
                    } else {
                        throw new INV_OBJREF("Could not load stub for "+
					     obj.getClass().getName());
                    }
                    
                } else {
                    
                // Return the result of calling thisObject()
                // on the connected tie...
            
                return theTie.thisObject();
                }
            } else {
                
                // This is an implementation object which has not been
                // exported to IIOP OR is a JRMP stub or implementation
                // object which cannot be marshalled into an ORB stream...
                
                throw new INV_OBJREF(obj.getClass().getName()+
				     " not exported or is a JRMP stub");
            }
        }
        
        // Didn't need to do anything, just return the input...
        
        return obj;
    }
                                            
    /*
     * Get a new instance of an RMI-IIOP Tie for the
     * given server object.
     */
    public static Tie loadTie(Remote obj) {
    	Tie result = null;
    	Class objClass = obj.getClass();
    	
    	// Have we tried to find this guy before?
    	
        synchronized (tieCache) {
            
    	    Object it = tieCache.get(obj);
        	
    	    if (it == null) {
        	    
    	        // No, so try it...
        	    
    	        try {

    	            // First try the classname...
            	    
    	            result = loadTie(objClass);
            	        
    	            // If we don't have a valid tie at this point,
    	            // walk up the parent chain until we either
    	            // load a tie or encounter PortableRemoteObject
    	            // or java.lang.Object...

                    while (result == null &&
                           (objClass = objClass.getSuperclass()) != null &&
                           objClass != PortableRemoteObject.class &&
                           objClass != Object.class) {
                                
                        result = loadTie(objClass);   
                    }
    	        } catch (Exception ex) {}
            
                // Did we get it?
                
                if (result == null) {
                    
                    // Nope, so cache that fact...
                    
                    tieCache.put(obj,CACHE_MISS);
                    
                } else {
                    
                    // Yes, so cache it...
                    
                    tieCache.put(obj,result);
                }
            } else {
                
                // Yes, return a new instance or fail again if
                // it was a miss last time...
                
                if (it != CACHE_MISS) {
                    try {
                        result = (Tie) it.getClass().newInstance();
                    } catch (Exception e) {
                    }
                }
            }
        }
        
        return result;    
    }
    
    /*
     * Load an RMI-IIOP Tie
     */
    private static Tie loadTie(Class theClass) {
	try {
    	    String className = tieName(theClass.getName());
	    //_REVISIT_ The spec does not specify a loadingContext parameter for
	    //the following call.  Would it be useful to pass one?  
    	    Class tieClass = loadClassForClass(className, 
                                     Util.getCodebase(theClass),
                                     null, theClass, theClass.getClassLoader());
    	    return (Tie) tieClass.newInstance();
        } catch (Exception err) {
            return null;    
        }
    }
 
    /*
     * Clear the stub/tie caches. Intended for use by
     * test code.
     */
    public static void clearCaches() {
        synchronized (tieToStubCache) {
            tieToStubCache.clear();
        }
        synchronized (tieCache) {
            tieCache.clear();
        }
        synchronized (stubToTieCache) {
            stubToTieCache.clear();
        }
    }
    
    /*
     * Load a class and check that it is assignable to a given type.
     * @param className the class name.
     * @param remoteCodebase the codebase to use. May be null.
     * @param loader the class loader of last resort. May be null.
     * @param expectedType the expected type. May be null.
     * @return the loaded class.
     */
    static Class loadClassOfType (String className,
                                  String remoteCodebase,
                                  ClassLoader loader,
                                  Class expectedType,
                                  ClassLoader expectedTypeClassLoader)
	throws ClassNotFoundException {
	
	Class loadedClass = null;

	try {
            //Sequence finding of the stubs according to spec
            try{
                //If-else is put here for speed up of J2EE.
                //According to the OMG spec, the if clause is not dead code.
                //It can occur if some compiler has allowed generation
                //into org.omg.stub hierarchy for non-offending
                //classes. This will encourage people to
                //produce non-offending class stubs in their own hierarchy.
                if(!PackagePrefixChecker
                   .hasOffendingPrefix(PackagePrefixChecker
                                       .withoutPackagePrefix(className))){
                    loadedClass = Util.loadClass
                        (PackagePrefixChecker.withoutPackagePrefix(className), 
                         remoteCodebase, 
                         loader);
                } else {
                    loadedClass = Util.loadClass
                        (className, 
                         remoteCodebase, 
                         loader);
                }
            } catch (ClassNotFoundException cnfe) {
                loadedClass = Util.loadClass
                    (className, 
                     remoteCodebase, 
                     loader);
            }
            if (expectedType == null)
	        return loadedClass;
	} catch (ClassNotFoundException cnfe) {
	    if (expectedType == null)
	        throw cnfe;
	}
	
        // If no class was not loaded, or if the loaded class is not of the 
	// correct type, make a further attempt to load the correct class
	// using the classloader of the expected type.
	// _REVISIT_ Is this step necessary, or should the Util,loadClass
	// algorithm always produce a valid class if the setup is correct?
	// Does the OMG standard algorithm need to be changed to include
	// this step?
        if (loadedClass == null || !expectedType.isAssignableFrom(loadedClass)){
            if (expectedType.getClassLoader() != expectedTypeClassLoader)
                throw new IllegalArgumentException(
                    "expectedTypeClassLoader not class loader of "  + 
                    "expected Type.");

            if (expectedTypeClassLoader != null)
		loadedClass = expectedTypeClassLoader.loadClass(className);
            else {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl == null)
                    cl = ClassLoader.getSystemClassLoader();

                loadedClass = cl.loadClass(className);
            }
        }

	return loadedClass;
    }

    /*
     * Load a class and check that it is compatible with a given type.
     * @param className the class name.
     * @param remoteCodebase the codebase to use. May be null.
     * @param loadingContext the loading context. May be null.
     * @param relatedType the related type. May be null.
     * @return the loaded class.
     */
    public static Class loadClassForClass (String className,
                                           String remoteCodebase,
                                           ClassLoader loader,
					   Class relatedType,
                                           ClassLoader relatedTypeClassLoader)
        throws ClassNotFoundException {

        if (relatedType == null)
	    return Util.loadClass(className, remoteCodebase, loader);

	Class loadedClass = null;
	try {
	    loadedClass = Util.loadClass(className, remoteCodebase, loader);
	} catch (ClassNotFoundException cnfe) {
	    if (relatedType.getClassLoader() == null)
        	throw cnfe;
	}
	
        // If no class was not loaded, or if the loaded class is not of the 
	// correct type, make a further attempt to load the correct class
	// using the classloader of the related type.
	// _REVISIT_ Is this step necessary, or should the Util,loadClass
	// algorithm always produce a valid class if the setup is correct?
	// Does the OMG standard algorithm need to be changed to include
	// this step?
        if (loadedClass == null || 
	    (loadedClass.getClassLoader() != null &&
	     loadedClass.getClassLoader().loadClass(relatedType.getName()) != 
                 relatedType))
        {
            if (relatedType.getClassLoader() != relatedTypeClassLoader)
                throw new IllegalArgumentException(
                    "relatedTypeClassLoader not class loader of relatedType.");

            if (relatedTypeClassLoader != null)
		loadedClass = relatedTypeClassLoader.loadClass(className);
        }
	
	return loadedClass;
    }

    /**
     * Get the helper for an IDLValue
     *
     * Throws MARSHAL exception if no helper found.
     */
    public static BoxedValueHelper getHelper(Class clazz, String codebase, 
        String repId)
    {
	String className = null;
        if (clazz != null) {
	    className = clazz.getName();
	    if (codebase == null)
	        codebase = Util.getCodebase(clazz);
	} else {
	    if (repId != null) 
                className = RepositoryId.cache.getId(repId).getClassName();
	    if (className == null) // no repId or unrecognized repId
		throw new org.omg.CORBA.MARSHAL(
                    MinorCodes.UNABLE_LOCATE_VALUE_HELPER,
                    CompletionStatus.COMPLETED_MAYBE);
	}

    	try {
            ClassLoader clazzLoader = 
                (clazz == null ? null : clazz.getClassLoader());
            Class helperClass = 
                loadClassForClass(className+"Helper", codebase, clazzLoader, 
                clazz, clazzLoader);
	    return (BoxedValueHelper)helperClass.newInstance();

    	} catch (ClassNotFoundException cnfe) {
            throw new org.omg.CORBA.MARSHAL(cnfe.toString(),
                MinorCodes.UNABLE_LOCATE_VALUE_HELPER,
                CompletionStatus.COMPLETED_MAYBE);
        } catch (IllegalAccessException iae) {
            throw new org.omg.CORBA.MARSHAL(iae.toString(),
                MinorCodes.UNABLE_LOCATE_VALUE_HELPER,
                CompletionStatus.COMPLETED_MAYBE);
        } catch (InstantiationException ie) {
            throw new org.omg.CORBA.MARSHAL(ie.toString(),
                MinorCodes.UNABLE_LOCATE_VALUE_HELPER,
                CompletionStatus.COMPLETED_MAYBE);
        } catch (ClassCastException cce) {
            throw new org.omg.CORBA.MARSHAL(cce.toString(),
                MinorCodes.UNABLE_LOCATE_VALUE_HELPER,
                CompletionStatus.COMPLETED_MAYBE);
        }    
    }

    /**
     * Get the factory for an IDLValue
     *
     * Throws MARSHAL exception if no factory found.
     */
    public static ValueFactory getFactory(Class clazz, String codebase, 
                               ORB orb, String repId)
    {
	ValueFactory factory = null;
	if ((orb != null) && (repId != null)) {
	    try {
                factory = ((org.omg.CORBA_2_3.ORB)orb).lookup_value_factory(
                    repId);
	    } catch (org.omg.CORBA.BAD_PARAM ex) {
	        // Try other way
	    }
	}

	String className = null;
        if (clazz != null) {
	    className = clazz.getName();
	    if (codebase == null)
	        codebase = Util.getCodebase(clazz);
	} else {
	    if (repId != null) 
                className = RepositoryId.cache.getId(repId).getClassName();
	    if (className == null) // no repId or unrecognized repId
		throw new org.omg.CORBA.MARSHAL(
                    MinorCodes.UNABLE_LOCATE_VALUE_FACTORY,
                    CompletionStatus.COMPLETED_MAYBE);
	}

	// if earlier search found a non-default factory, or the same default
	// factory that loadClassForClass would return, bale out now... 
	if (factory != null && 
	    (!factory.getClass().getName().equals(className+"DefaultFactory") ||
	     (clazz == null && codebase == null)))
	    return factory;

    	try {
            ClassLoader clazzLoader = 
                (clazz == null ? null : clazz.getClassLoader());
	    Class factoryClass = 
                loadClassForClass(className+"DefaultFactory", codebase,
                clazzLoader, clazz, clazzLoader);
	    return (ValueFactory)factoryClass.newInstance();

    	} catch (ClassNotFoundException cnfe) {
            throw new org.omg.CORBA.MARSHAL(cnfe.toString(),
                MinorCodes.UNABLE_LOCATE_VALUE_FACTORY,
                CompletionStatus.COMPLETED_MAYBE);
        } catch (IllegalAccessException iae) {
            throw new org.omg.CORBA.MARSHAL(iae.toString(),
                MinorCodes.UNABLE_LOCATE_VALUE_FACTORY, 
                CompletionStatus.COMPLETED_MAYBE);
        } catch (InstantiationException ie) {
            throw new org.omg.CORBA.MARSHAL(ie.toString(),
                MinorCodes.UNABLE_LOCATE_VALUE_FACTORY,
                CompletionStatus.COMPLETED_MAYBE);
        } catch (ClassCastException cce) {
            throw new org.omg.CORBA.MARSHAL(cce.toString(),
                MinorCodes.UNABLE_LOCATE_VALUE_FACTORY,
                CompletionStatus.COMPLETED_MAYBE);
        }    
    }

    /*
     * Load an RMI-IIOP Stub given a Tie.
     * @param tie the tie.
     * @param stubClass the stub class. May be null.
     * @param remoteCodebase the codebase to use. May be null.
     * @param onlyMostDerived if true, will fail if cannot load a stub for the
     * first repID in the tie. If false, will walk all repIDs.
     * @return the stub or null if not found.
     */
    public static Remote loadStub (Tie tie,
                                   Class stubClass,
                                   String remoteCodebase,
                                   boolean onlyMostDerived) {
 
        StubEntry entry = null;

        // Do we already have it cached?
        
        synchronized (tieToStubCache) {
            
            Object cached = tieToStubCache.get(tie);
       
            if (cached == null) {
                
                // No, so go try to load it...
                
                entry = loadStubAndUpdateCache(
                        tie,stubClass,remoteCodebase,onlyMostDerived);

            } else {
            
                // Yes, is it a stub?  If not, it was a miss last
                // time, so return null again...
               
                if (cached != CACHE_MISS) {
                    
                    // It's a stub.
                    
                    entry = (StubEntry) cached;
                    
                    // Does the cached stub meet the requirements
                    // of the caller? If the caller does not require
                    // the most derived stub and does not require
                    // a specific stub type, we don't have to check
                    // any further because the cached type is good
                    // enough...
                    
                    if (!entry.mostDerived && onlyMostDerived) {
                                
                        // We must reload because we do not have
                        // the most derived cached already...
                            
                        entry = 
                           loadStubAndUpdateCache(tie,null,remoteCodebase,true);
                               
                    } else if (stubClass != null 
                              && entry.stub.getClass() != stubClass) 
                    {
                                
                        // We do not have exactly the right stub. First, try to
                        // upgrade the cached stub by forcing it to the most
                        // derived stub...
                            
                        entry = 
                            loadStubAndUpdateCache(tie,null,remoteCodebase,true);

                        // If that failed, try again with the exact type
                        // we need...
                        
                        if (entry == null) {
                            entry = loadStubAndUpdateCache(tie,stubClass,
                                    remoteCodebase,onlyMostDerived);
                        }
                    
                    } else {
                                            
                        // Use the cached stub. Is the delegate set?
                        
                        try {
                            Delegate stubDel = 
                                ((ObjectImpl)entry.stub)._get_delegate();
                        } catch (Exception e2) {
                            
                            // No, so set it if we can...
                            
                            try {            
                                Delegate del = 
                                    ((ObjectImpl)tie)._get_delegate();
                                ((ObjectImpl)entry.stub)._set_delegate(del);
                            } catch (Exception e) {}
                        }
                    }
                }
            }
        }
        
        if (entry != null) {
            return (Remote)entry.stub;
        } else {
            return null;
        }
    }
    
    /*
     * Load an RMI-IIOP Stub given a Tie, but do not look in the cache.
     * This method must be called with the lock held for tieToStubCache.
     * @param tie the tie.
     * @param stubClass the stub class. May be null.
     * @param remoteCodebase the codebase to use. May be null.
     * @param onlyMostDerived if true, will fail if cannot load a stub for the
     * first repID in the tie. If false, will walk all repIDs.
     * @return the StubEntry or null if not found.
     */
     private static StubEntry loadStubAndUpdateCache (Tie tie,
						     Class stubClass,
						     String remoteCodebase,
						     boolean onlyMostDerived) 
    {


        Stub stub = null;
        StubEntry entry = null;
        boolean isObjectImpl = (tie instanceof ObjectImpl);

        // Were we given a stub class?
                
        if (stubClass != null) {
                    
            // Yes, so try to instantiate it...
                    
            try {
                stub = (Stub) stubClass.newInstance();
            } catch (Throwable e) {
                if (e instanceof ThreadDeath) {
                    throw (ThreadDeath) e;
                }
            }
        } else {
                    
            // Nope, so we must find it. First, get the list
            // of ids from the tie...
                    
            String[] ids = null;
            if( isObjectImpl ) {
                ids = ((ObjectImpl)tie)._ids();
            } else {
                // If Tie is extending Servant, Then the
                // Repository Id can be obtained from all_interfaces()
                // method.
                ids = ((org.omg.PortableServer.Servant)tie).
                      _all_interfaces( null, null );
            }
                                
            // If we were not given a remoteCodebase,
            // get one from the tie...
            
            if (remoteCodebase == null) {
                remoteCodebase = Util.getCodebase(tie.getClass());
            }
                    
            // Now walk all the RepIDs till we find it or fail...
                    
            for (int i = 0; i < ids.length; i++) {
            	        
    	        // Check for the java.rmi.Remote special case...
             
    	        if (ids[i].length() == 0) {
    	            stub = new org.omg.stub.java.rmi._Remote_Stub();
    	            break;
    	        }
            	        
    	        // Get the classname from the RepID...
            	        
    	        String className = stubNameFromRepID(ids[i]);

    	        // Now try to load it...
            	        
    	        try {
                    // _REVISIT_ The spec does not specify a 
                    // loadingContext parameter for the following call.
                    Class resultClass = null;
                    try{ 
                        //If-else is put here for speed up of J2EE.
                        //According to the OMG spec, the if clause is 
                        //not dead code.  It can occur if the compiler
                        //has allowed generation into org.omg.stub 
                        //hierarchy for non-offending classes. This 
                        //will encourage people to produce non-offending
                        //class stubs in their own hierarchy.
                        if(!PackagePrefixChecker
                           .hasOffendingPrefix(PackagePrefixChecker
                           .withoutPackagePrefix(className))){
                            resultClass = Util.loadClass
                            (PackagePrefixChecker.withoutPackagePrefix(
                                 className), 
                                 remoteCodebase, 
                                 tie.getClass().getClassLoader());
                        } else {
                            resultClass = Util.loadClass
                                (className, 
                                 remoteCodebase, 
                                 tie.getClass().getClassLoader());
                        }
                    } catch (ClassNotFoundException cnfe){
                        resultClass = Util.loadClass
                            (className, 
                             remoteCodebase, 
                             tie.getClass().getClassLoader());
                    }
                    stub = (Stub) resultClass.newInstance();
                    break;
    	        } catch (Exception e){}
            	        
    	        // If we failed to load it, see if we should continue...
            	        
    	        if (onlyMostDerived) {
            	            
    	            // Stop here.
            	                
    	            break;
    	        }
            }
        }
                
        // Did we get it?
                
        if (stub != null) {
                    
            // If the tie has a delegate set, grab it
            // and stuff it in the stub...
                        		        
            if( isObjectImpl ) {
                // Tie extends ObjectImpl
                try {
                    Delegate del = ((ObjectImpl)tie)._get_delegate();
                    ((ObjectImpl)stub)._set_delegate(del);
                } catch( Exception e1 ) {
                    // The tie does not have a delegate set, so stash
                    // this tie away using the stub as a key so that
                    // later, when the stub is connected, we can find
                    // and connect the tie as well...
                
                    synchronized (stubToTieCache) {
                        stubToTieCache.put(stub,tie);
                    }
                }
            } else {
                // Tie extends Servant
                try {
                    org.omg.CORBA.Object ref = 
                        ((org.omg.PortableServer.Servant)tie).
                        _this_object( );
                    ((ObjectImpl)stub)._set_delegate( 
                            ((ObjectImpl)ref)._get_delegate() );
                } catch( org.omg.CORBA.BAD_INV_ORDER bad) {
                    synchronized (stubToTieCache) {
                        stubToTieCache.put(stub,tie);
                    }
                } catch( Exception e ) {
                    // Exception is caught because of any of the 
                    // following reasons
                    // 1) POA is not associated with the TIE 
                    // 2) POA Policies for the tie-associated POA
                    //    does not support _this_object() call. 
                    throw new BAD_PARAM(MinorCodes.NO_POA,
                            CompletionStatus.COMPLETED_NO);
                }
            }
            // Update the cache...
            entry = new StubEntry(stub,onlyMostDerived);
            tieToStubCache.put(tie,entry);
                    
        } else {
            // Stub == null, so cache the miss...
            tieToStubCache.put(tie,CACHE_MISS);
        }
            
        return entry;
    }
                                   
    /*
     * If we loadStub(Tie,...) stashed away a tie which was
     * not connected, remove it from the cache and return
     * it.
     */
    public static Tie getAndForgetTie (Stub stub) {
        synchronized (stubToTieCache) {
            return (Tie) stubToTieCache.remove(stub);
        }
    }
    
    /*
     * Remove any cached Stub for the given tie.
     */
    public static void purgeStubForTie (Tie tie) {
        StubEntry entry;
        synchronized (tieToStubCache) {
            entry = (StubEntry)tieToStubCache.remove(tie);
        }
	if (entry != null) {
            synchronized (stubToTieCache) {
                stubToTieCache.remove(entry.stub);
            }
	}
    }
    
    /*
     * Remove cached tie/servant pair.
     */
    public static void purgeTieAndServant (Tie tie) {
	synchronized (tieCache) {
	    Object target = tie.getTarget();
	    if (target != null)
		tieCache.remove(target);
	}
    }

    /*
     * Convert a RepId to a stubName...
     */
    public static String stubNameFromRepID (String repID) {
        
        // Convert the typeid to a RepositoryId instance, get
        // the className and mangle it as needed...

        RepositoryId id = RepositoryId.cache.getId(repID);
        String className = id.getClassName();
        
        if (id.isIDLType()) {
            className = idlStubName(className);
        } else {
            className = stubName(className);
        }
        return className;
    }
  
    
    /*
     * Load an RMI-IIOP Stub.
     */
    public static Stub loadStub (ObjectImpl narrowFrom,
                                   Class narrowTo) {
        Stub result = null;
            
	try {
            
            // Get the codebase from the delegate to use when loading
            // the new stub, if possible...
            
            String codebase = null;
            try {
                // We can't assume that narrowFrom is a CORBA_2_3 ObjectImpl, yet
                // it may have a 2_3 Delegate that provides a codebase.  Swallow
                // the ClassCastException otherwise.
                Delegate delegate = narrowFrom._get_delegate();
                codebase = ((org.omg.CORBA_2_3.portable.Delegate)delegate).get_codebase(narrowFrom);

            } catch (ClassCastException e) {
            }

    	    String stubName = stubName(narrowTo.getName());
    	    
	    // _REVISIT_ Should the narrowFrom or narrowTo class be used as the 
	    // loadingContext in the following call?  The spec says narrowFrom,
	    // but this does not seem correct...
            Class resultClass = null;
            try {
    	        resultClass = loadClassOfType(stubName,
                                                codebase, 
						narrowFrom.getClass().getClassLoader(),
                                                narrowTo,
                                                narrowTo.getClassLoader());
            } catch(ClassNotFoundException cnfe) {
    	        resultClass = loadClassOfType(STUB_PACKAGE_PREFIX + stubName,
                                                codebase, 
						narrowFrom.getClass().getClassLoader(),
                                                narrowTo,
                                                narrowTo.getClassLoader());
	    }
    	    // Create a stub instance and set the delegate...
    	    result = (Stub) resultClass.newInstance();
            ((ObjectImpl)result)._set_delegate(narrowFrom._get_delegate());
    	    
        } catch (Exception err) {
        }
        
        return result;
    }
        
    /*
     * Load an RMI-IIOP Stub class.
     */
    //d11638 removed unused "CodeBase sender" parameter

    public static Class loadStubClass(String repID,
                                      String remoteCodebase,
				      Class expectedType) 
	throws ClassNotFoundException {	                                    

        // Get the repID and check for "" special case.
        // We should never be called with it (See CDRInputStream
        // and the loadStub() method)...
        
        if (repID.length() == 0) {
            throw new ClassNotFoundException();   
        }
        
        // Get the stubname from the repID and load
        // the class. If we have a valid 'sender', fall
        // back to using its codebase if we need to...

        String className = Utility.stubNameFromRepID(repID);
        ClassLoader expectedTypeClassLoader = (expectedType == null ? null : expectedType.getClassLoader());
        try {
              return loadClassOfType(className,
                                       remoteCodebase,
                                       expectedTypeClassLoader,
                                       expectedType,
                                       expectedTypeClassLoader);
        } catch (ClassNotFoundException e) {
            
	    return loadClassOfType(STUB_PACKAGE_PREFIX + className,
                                   remoteCodebase,
                                   expectedTypeClassLoader,
                                   expectedType,
                                   expectedTypeClassLoader);
                    
        }
    }

    /**
     * Create an RMI stub name.
     */
    public static String stubName (String className) {
        return
            PackagePrefixChecker.hasOffendingPrefix(stubNameForCompiler(className)) ?
            PackagePrefixChecker.packagePrefix() + stubNameForCompiler(className) :
            stubNameForCompiler(className);
    }
    public static String stubNameForCompiler (String className) {
        int index = className.indexOf('$');
        if (index < 0) {
            index = className.lastIndexOf('.');
        }
        if (index > 0) {
            return 
                className.substring(0,index+1) +
                STUB_PREFIX +
                className.substring(index+1) +
                RMI_STUB_SUFFIX;
        } else {
            return STUB_PREFIX +
                className +
                RMI_STUB_SUFFIX;
        }
    }

    /**
     * Create an RMI tie name.
     */
    public static String tieName (String className) {
        return
            PackagePrefixChecker.hasOffendingPrefix(tieNameForCompiler(className)) ?
            PackagePrefixChecker.packagePrefix() + tieNameForCompiler(className) :
            tieNameForCompiler(className);
    }

    public static String tieNameForCompiler (String className) {
        int index = className.indexOf('$');
        if (index < 0) {
            index = className.lastIndexOf('.');
        }
        if (index > 0) {
            return className.substring(0,index+1) +
		STUB_PREFIX +
		className.substring(index+1) +
		TIE_SUFIX;
        } else {
            return STUB_PREFIX +
		className +
		TIE_SUFIX;
        }
    }

    /**
     * Throws the CORBA equivalent of a java.io.NotSerializableException
     */
    public static void throwNotSerializableForCorba(String className) {
        throw new BAD_PARAM(className,
                            MinorCodes.NOT_SERIALIZABLE,
                            CompletionStatus.COMPLETED_MAYBE);
    }

    /**
     * Create an IDL stub name.
     */
    public static String idlStubName(String className) {
        String result = null;
        int index = className.lastIndexOf('.');
        if (index > 0) {
            result = className.substring(0,index+1) + 
		STUB_PREFIX +
		className.substring(index+1) + 
		IDL_STUB_SUFFIX;
        } else {
            result = STUB_PREFIX +
		className +
		IDL_STUB_SUFFIX;
        }
        return result;
    }
    
    public static void printStackTrace() 
    {
	Throwable thr = new Throwable( "Printing stack trace:" ) ;
	thr.fillInStackTrace() ;
	thr.printStackTrace() ;
    }

    /**
     * Read an object reference from the input stream and narrow
     * it to the desired type.
     * @param in the stream to read from.
     * @throws ClassCastException if narrowFrom cannot be cast to narrowTo.
     */
    public static Object readObjectAndNarrow(InputStream in,
                                             Class narrowTo)
	throws ClassCastException {
        Object result = in.read_Object();
	if (result != null) 
            return PortableRemoteObject.narrow(result, narrowTo);
	else
	    return null;
    }

    /**
     * Read an abstract interface type from the input stream and narrow
     * it to the desired type.
     * @param in the stream to read from.
     * @throws ClassCastException if narrowFrom cannot be cast to narrowTo.
     */
    public static Object readAbstractAndNarrow(org.omg.CORBA_2_3.portable.InputStream in,
                                               Class narrowTo)
	throws ClassCastException {
        Object result = in.read_abstract_interface();
	if (result != null) 
            return PortableRemoteObject.narrow(result, narrowTo);
	else
	    return null;
    }


    /** Converts an Ascii Character into Hexadecimal digit
     */
    static int hexOf( char x )
    {
	int val;

        val = x - '0';
        if (val >=0 && val <= 9)
            return val;

        val = (x - 'a') + 10;
        if (val >= 10 && val <= 15)
            return val;

        val = (x - 'A') + 10;
        if (val >= 10 && val <= 15)
            return val;

        throw new DATA_CONVERSION(MinorCodes.BAD_HEX_DIGIT,
                                  CompletionStatus.COMPLETED_NO);
    }
}

class StubEntry {
    Stub stub;
    boolean mostDerived;
    
    StubEntry(Stub stub, boolean mostDerived) {
        this.stub = stub;
        this.mostDerived = mostDerived;
    }
}
