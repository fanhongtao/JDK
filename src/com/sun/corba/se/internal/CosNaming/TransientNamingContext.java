/*
 * @(#)TransientNamingContext.java	1.51 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.CosNaming;

// Import general CORBA classes
import org.omg.CORBA.SystemException;
import org.omg.CORBA.Object;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;

// Import org.omg.CosNaming types
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.BindingTypeHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;

// Get base implementation
import com.sun.corba.se.internal.CosNaming.NamingContextImpl;

import com.sun.corba.se.internal.POA.*;

// Get a hash table
import java.util.Hashtable;

// Get a transient iterator
import com.sun.corba.se.internal.CosNaming.TransientBindingIterator;

import com.sun.corba.se.internal.POA.POAORB;

import com.sun.corba.se.internal.CosNaming.NamingContextDataStore;

import com.sun.corba.se.internal.CosNaming.MinorCodes;

/**
 * Class TransientNamingContext implements the methods defined
 * by NamingContextDataStore, and extends the NamingContextImpl class to
 * provide a servant implementation of CosNaming::NamingContext.
 * The TransientNamingContext uses a hash table
 * to store the mappings between bindings and object references and the
 * hash table is not persistent; thereby the name "transient".
 * This class should not be used directly; instead, the class
 * TransientNameService should be instantiated.
 * <p>
 * The keys in the hash table are InternalBindingKey objects, containing
 * a single NameComponent and implementing the proper functions, i.e.,
 * equals() and hashCode() in an efficient manner. The values in the hash
 * table are InternalBindingValues and store a org.omg.CosNaming::Binding and
 * the object reference associated with the binding. For iteration,
 * TransientBindingIterator objects are created, which are passed a cloned
 * copy of the hashtable. Since elements are inserted and deleted and
 * never modified, this provides stable iterators at the cost of cloning
 * the hash table.
 * <p>
 * To create and destroy object references, the TransientNamingContext
 * uses the orb.connect() and orb.disconnect() methods.
 *
 * @see NamingContextImpl
 * @see NamingContextDataStore
 * @see TransientBindingIterator
 * @see TransientNameService
 */
public class TransientNamingContext extends NamingContextImpl implements NamingContextDataStore
{
    /**
     * Constructs a new TransientNamingContext object.
     * @param orb an orb object.
     * @param initial the initial naming context.
     * @exception Exception a Java exception thrown of the base class cannot
     * initialize.   
     */
    public TransientNamingContext(POAORB orb, org.omg.CORBA.Object initial,
        POA nsPOA )
	throws java.lang.Exception
    {
	super(orb, nsPOA );
	this.localRoot = initial;
    }

    /**
     * Binds the object to the name component as the specified binding type.
     * It creates a InternalBindingKey object and a InternalBindingValue
     * object and inserts them in the hash table.
     * @param n A single org.omg.CosNaming::NameComponent under which the 
     * object will be bound.
     * @param obj An object reference to be bound under the supplied name.
     * @param bt The type of the binding (i.e., as object or as context).
     * @exception org.omg.CORBA.SystemException One of a fixed set of CORBA 
     * system exceptions.
     */
    public final void Bind(NameComponent n, org.omg.CORBA.Object obj,
			   BindingType bt)
	throws org.omg.CORBA.SystemException
    {
	// Create a key and a value
	InternalBindingKey key = new InternalBindingKey(n);
	NameComponent[] name = new NameComponent[1];
	name[0] = n;
	Binding b = new Binding(name,bt);
	InternalBindingValue value = new InternalBindingValue(b,null);
	value.theObjectRef = obj;
	// insert it
	InternalBindingValue oldValue =
	    (InternalBindingValue)this.theHashtable.put(key,value);

	if (oldValue != null) {
	    throw new org.omg.CORBA.INTERNAL(
                MinorCodes.TRANS_NC_BIND_ALREADY_BOUND,
	        CompletionStatus.COMPLETED_NO);
	}
    }

    /**
     * Resolves the supplied name to an object reference and returns
     * the type of the resolved binding. It creates a InternalBindingKey
     * and uses the key for looking up in the hash table. If nothing
     * is found an exception is thrown, otherwise the object reference
     * is returned and the binding type set.
     * @param n a NameComponent which is the name to be resolved.
     * @param bth the BindingType as an out parameter.
     * @return the object reference bound under the supplied name, null if not 
     * found.
     * @exception org.omg.CORBA.SystemException One of a fixed set of CORBA 
     * system exceptions.
     */
    public final org.omg.CORBA.Object Resolve(NameComponent n,
					      BindingTypeHolder bth)
	throws org.omg.CORBA.SystemException
    {
	// Is the initial naming context requested?
	if ( (n.id.length() == 0)
           &&(n.kind.length() == 0 ) )
        {
	    bth.value = BindingType.ncontext;
	    return localRoot;
	}
    
	// Create a key and lookup the value
	InternalBindingKey key = new InternalBindingKey(n);

	InternalBindingValue value = 
            (InternalBindingValue) this.theHashtable.get(key);
	if (value == null) {
	    return null;
        }
    
        // Copy out binding type and object reference
	bth.value = value.theBinding.binding_type;
	return value.theObjectRef;
    }

    /**
     * Deletes the binding with the supplied name. It creates a
     * InternalBindingKey and uses it to remove the value associated
     * with the key. If nothing is found an exception is thrown, otherwise
     * the element is removed from the hash table.
     * @param n a NameComponent which is the name to unbind
     * @return the object reference bound to the name, or null if not found.
     * @exception org.omg.CORBA.SystemException One of a fixed set of CORBA 
     * system exceptions.
     */
    public final org.omg.CORBA.Object Unbind(NameComponent n)
	throws org.omg.CORBA.SystemException
    {
	// Create a key and remove it from the hashtable
	InternalBindingKey key = new InternalBindingKey(n);
	InternalBindingValue value = 
            (InternalBindingValue)this.theHashtable.remove(key);

	// Return what was found
	if (value == null)
	    return null;
	else
	    return value.theObjectRef;
    }  

    /**
     * List the contents of this NamingContext. It creates a new
     * TransientBindingIterator object and passes it a clone of the
     * hash table and an orb object. It then uses the
     * newly created object to return the required number of bindings.
     * @param how_many The number of requested bindings in the BindingList.
     * @param bl The BindingList as an out parameter.
     * @param bi The BindingIterator as an out parameter.
     * @exception org.omg.CORBA.SystemException One of a fixed set of CORBA 
     * system exceptions.
     */
    public final void List(int how_many, BindingListHolder bl,
			   BindingIteratorHolder bi)
	throws org.omg.CORBA.SystemException
    {
	try {
	    // Create a new binding iterator servant with a copy of this 
            // hashtable. nsPOA is passed to the object so that it can 
            // de-activate itself from the Active Object Map when 
            // Binding Iterator.destroy is called.
	    TransientBindingIterator bindingIterator =
		new TransientBindingIterator(this.orb,
	        (Hashtable)this.theHashtable.clone(), nsPOA);
	    // Have it set the binding list
            bindingIterator.list(how_many,bl);
            
            byte[] objectId = nsPOA.activate_object( bindingIterator );
            org.omg.CORBA.Object obj = nsPOA.id_to_reference( objectId );
      
	    // Get the object reference for the binding iterator servant
	    org.omg.CosNaming.BindingIterator bindingRef = 
                org.omg.CosNaming.BindingIteratorHelper.narrow( obj );
      
	    bi.value = bindingRef;
	} catch (org.omg.CORBA.SystemException e) {
	    NamingUtils.errprint("List(): caught System Exception:");
	    NamingUtils.printException(e);
	    throw e;
	} catch (Exception e) {
	    // Convert to a CORBA system exception
	    NamingUtils.errprint("List(): caught exception:");
	    NamingUtils.printException(e);
	    throw new org.omg.CORBA.INTERNAL(MinorCodes.TRANS_NC_LIST_GOT_EXC,
					     CompletionStatus.COMPLETED_NO);
	}
    }
  
    /**
     * Create a new NamingContext. It creates a new TransientNamingContext
     * object, passing it the orb object. 
     * @return an object reference for a new NamingContext object implemented
     * by this Name Server.
     * @exception org.omg.CORBA.SystemException One of a fixed set of CORBA 
     * system exceptions.
     */
    public final org.omg.CosNaming.NamingContext NewContext()
	throws org.omg.CORBA.SystemException
    {
	try {
	    // Create a new servant
	    TransientNamingContext transContext =
		new TransientNamingContext(
                (POAORB) orb,localRoot, nsPOA);

            byte[] objectId = nsPOA.activate_object( transContext );
            org.omg.CORBA.Object obj = nsPOA.id_to_reference( objectId );
            return org.omg.CosNaming.NamingContextHelper.narrow( obj );

	} catch (org.omg.CORBA.SystemException e) {
	    NamingUtils.errprint("NewContext(): caught System Exception:");
	    NamingUtils.printException(e);
	    throw e;
	} catch (Exception e) {
	    // Convert to a CORBA system exception
	    NamingUtils.errprint("NewContext(): caught Exception:");
	    NamingUtils.printException(e);
	    throw new org.omg.CORBA.INTERNAL(MinorCodes.TRANS_NC_NEWCTX_GOT_EXC,
					     CompletionStatus.COMPLETED_NO);
	}
    }

    /**
     * Destroys this NamingContext by disconnecting from the ORB.
     * @exception org.omg.CORBA.SystemException One of a fixed set of CORBA 
     * system exceptions.
     */
    public final void Destroy()
	throws org.omg.CORBA.SystemException
    {
	// Destroy the object reference by disconnecting from the ORB
	try {
            byte[] objectId = nsPOA.servant_to_id( this );
            if( objectId != null ) {
                nsPOA.deactivate_object( objectId );
            }
	} catch (org.omg.CORBA.SystemException e) {
	    NamingUtils.errprint("Destroy(): caught System Exception:");
	    NamingUtils.printException(e);
	    throw e;
	} catch (Exception e) {
	    // Convert to a CORBA system exception
	    NamingUtils.errprint("Destroy(): caught exception:");
	    NamingUtils.printException(e);
	    throw new org.omg.CORBA.INTERNAL(
                MinorCodes.TRANS_NC_DESTROY_GOT_EXC,
	        CompletionStatus.COMPLETED_NO);
	}
    }

    /**
     * Return whether this NamingContext contains any bindings. It forwards
     * this request to the hash table.
     * @return true if this NamingContext contains no bindings.
     */
    public final boolean IsEmpty()
    {
	return this.theHashtable.isEmpty();
    }

    // A hashtable to store the bindings
    private final Hashtable  theHashtable = new Hashtable();

    /**
     * The local root naming context.
     */
    public org.omg.CORBA.Object localRoot;
}

