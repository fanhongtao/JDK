/*
 * @(#)ShutdownUtilDelegate.java	1.16 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.internal.iiop;

import org.omg.CORBA.*;
import java.util.*;
import java.rmi.*;
import java.io.*;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.UnknownException;
import javax.transaction.TransactionRequiredException;
import javax.transaction.TransactionRolledbackException;
import javax.transaction.InvalidTransactionException;

import javax.rmi.CORBA.Tie;

import com.sun.corba.se.internal.util.Utility;
import com.sun.corba.se.internal.orbutil.*;
import com.sun.corba.se.internal.core.ORBVersionImpl;
import com.sun.corba.se.internal.core.ORBVersion;
import com.sun.corba.se.internal.core.ClientSubcontract;

public class ShutdownUtilDelegate extends com.sun.corba.se.internal.javax.rmi.CORBA.Util
{
    static ShutdownUtilDelegate instance = null;

    public ShutdownUtilDelegate() {
        instance = this;
    }

    public boolean isLocal(javax.rmi.CORBA.Stub stub) throws RemoteException {

        try {
	    org.omg.CORBA.portable.Delegate delegate = stub._get_delegate() ;
	    return ((ClientSubcontract)delegate).useLocalInvocation( stub ) ;
        } catch (SystemException e) {
            throw javax.rmi.CORBA.Util.mapSystemException(e);
        }
    }

    protected void unregisterTargetsForORB(ORB orb) {
        super.unregisterTargetsForORB(orb);
    }

    /**
     * Maps a SystemException to a RemoteException.
     * @param ex the SystemException to map.
     * @return the mapped exception.
     */
    public RemoteException mapSystemException(SystemException ex) {

        if (ex instanceof UnknownException) {
            Throwable orig = ((UnknownException)ex).originalEx;
            if (orig instanceof Error) {
                return new ServerError("Error occurred in server thread",(Error)orig);
            } else if (orig instanceof RemoteException) {
                return new ServerException("RemoteException occurred in server thread",(Exception)orig);
            } else if (orig instanceof RuntimeException) {
                throw (RuntimeException) orig;
            }
        }

        // Build the message string...

        String name = ex.getClass().getName();
        String corbaName = name.substring(name.lastIndexOf('.')+1);
        String status;
        switch (ex.completed.value()) {
        case CompletionStatus._COMPLETED_YES:
            status = "Yes";
            break;
        case CompletionStatus._COMPLETED_NO:
            status = "No";
            break;
        case CompletionStatus._COMPLETED_MAYBE:
        default:
            status = "Maybe";
            break;
        }
        String message = "CORBA "+corbaName+" "+ex.minor+" "+status;

        // Now map to the correct RemoteException type...

        if (ex instanceof COMM_FAILURE) {
            return new MarshalException(message, ex);
        } else if (ex instanceof INV_OBJREF) {
            RemoteException newEx = new NoSuchObjectException(message);
            newEx.detail = ex;
            return newEx;
        } else if (ex instanceof NO_PERMISSION) {
            return new AccessException(message, ex);
        } else if (ex instanceof MARSHAL) {
            return new MarshalException(message, ex);
        } else if (ex instanceof OBJECT_NOT_EXIST) {
            RemoteException newEx = new NoSuchObjectException(message);
            newEx.detail = ex;
            return newEx;
        } else if (ex instanceof TRANSACTION_REQUIRED) {
            RemoteException newEx = new TransactionRequiredException(message);
            newEx.detail = ex;
            return newEx;
        } else if (ex instanceof TRANSACTION_ROLLEDBACK) {
            RemoteException newEx = new TransactionRolledbackException(message);
            newEx.detail = ex;
            return newEx;
        } else if (ex instanceof INVALID_TRANSACTION) {
            RemoteException newEx = new InvalidTransactionException(message);
            newEx.detail = ex;
            return newEx;
        } else if (ex instanceof BAD_PARAM) {

            Exception inner = ex;

            // Pre-Merlin Sun ORBs used the incorrect minor code for
            // this case.  See Java to IDL ptc-00-01-08 1.4.8.
            if (ex.minor == MinorCodes.LEGACY_SUN_NOT_SERIALIZABLE ||
                ex.minor == MinorCodes.NOT_SERIALIZABLE) {

                if (ex.getMessage() != null)
                    inner = new NotSerializableException(ex.getMessage());
                else
                    inner = new NotSerializableException();
            }

            return new MarshalException(message,inner);
        }

        // Just map to a generic RemoteException...

        return new RemoteException(message, ex);
    }


    // Duplicated from the se delegate superclass.  The subtle
    // difference is that it uses ORBUtility instead of Utility.
    // This is required since we can't assume that the Utility
    // version we're running with (may be from the JDK) has
    // the throwNotSerializableForCorba method that takes a
    // String parameter (added for JDK 1.4/J2EE 1.3).

    // Warning: Assumes Utility.autoConnect is available

    /**
     * Writes any java.lang.Object as a CORBA any.
     * @param out the stream in which to write the any.
     * @param obj the object to write as an any.
     */
    public void writeAny(org.omg.CORBA.portable.OutputStream out, 
                         java.lang.Object obj) {

        org.omg.CORBA.ORB orb = out.orb();

        // Create Any
        Any any = orb.create_any();

        // Make sure we have a connected object...

        java.lang.Object newObj = Utility.autoConnect(obj,orb,false);

        if (newObj instanceof org.omg.CORBA.Object) {
            any.insert_Object((org.omg.CORBA.Object)newObj);
        } else {
            if (newObj == null) {
                // Handle the null case, including backwards
                // compatibility issues
                any.insert_Value(null, createTypeCodeForNull(orb));
            } else {
                if (newObj instanceof Serializable) {
                    // If they're our Any and ORB implementations,
                    // we may want to do type code related versioning.
                    TypeCode tc = createTypeCode((Serializable)newObj, any, orb);
                    if (tc == null)
                        any.insert_Value((Serializable)newObj);
                    else
                        any.insert_Value((Serializable)newObj, tc);
                } else if (newObj instanceof Remote) {
                    ORBUtility.throwNotSerializableForCorba(newObj.getClass().getName());
                } else {
                    ORBUtility.throwNotSerializableForCorba(newObj.getClass().getName());
                }
            }
        }

        //d11638 Write the Any
        out.write_any(any);
    }

    /**
     * When using our own ORB and Any implementations, we need to get
     * the ORB version and create the type code appropriately.  This is
     * to overcome a bug in which the JDK 1.3.x ORBs used a tk_char
     * rather than a tk_wchar to describe a Java char field.
     *
     * This only works in RMI-IIOP with Util.writeAny since we actually
     * know what ORB and stream we're writing with when we insert
     * the value.
     *
     * Returns null if it wasn't possible to create the TypeCode (means
     * it wasn't our ORB or Any implementation).
     *
     * This does not handle null objs.
     */
    private TypeCode createTypeCode(Serializable obj,
                                    org.omg.CORBA.Any any,
                                    org.omg.CORBA.ORB orb) {

        if (any instanceof com.sun.corba.se.internal.corba.AnyImpl &&
            orb instanceof com.sun.corba.se.internal.corba.ORB) {

            com.sun.corba.se.internal.corba.AnyImpl anyImpl
                = (com.sun.corba.se.internal.corba.AnyImpl)any;

            com.sun.corba.se.internal.corba.ORB ourORB
                = (com.sun.corba.se.internal.corba.ORB)orb;

            return anyImpl.createTypeCodeForClass(obj.getClass(), ourORB);

        } else
            return null;
    }

    /**
     * This is used to create the TypeCode for a null reference.
     * It also handles backwards compatibility with JDK 1.3.x.
     *
     * This method will not return null.
     */
    private TypeCode createTypeCodeForNull(org.omg.CORBA.ORB orb) {
        if (orb instanceof com.sun.corba.se.internal.corba.ORB) {

            com.sun.corba.se.internal.corba.ORB ourORB
                = (com.sun.corba.se.internal.corba.ORB)orb;

            // Preserve backwards compatibility with Kestrel and Ladybird
            // by not fully implementing interop issue resolution 3857,
            // and returning a null TypeCode with a tk_value TCKind.
            // If we're not talking to Kestrel or Ladybird, fall through
            // to the abstract interface case (also used for foreign ORBs).
            if (!ORBVersionImpl.FOREIGN.equals(ourORB.getORBVersion()) &&
                ORBVersionImpl.NEWER.compareTo(ourORB.getORBVersion()) > 0) {

                return orb.get_primitive_tc(TCKind.tk_value);
            }
        }

        // Use tk_abstract_interface as detailed in the resolution

        // REVISIT: Define this in IDL and get the ID in generated code
        String abstractBaseID = "IDL:omg.org/CORBA/AbstractBase:1.0";

        return orb.create_abstract_interface_tc(abstractBaseID, "");
    }
}
