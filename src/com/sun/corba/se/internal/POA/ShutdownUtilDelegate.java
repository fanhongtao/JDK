/*
 * @(#)ShutdownUtilDelegate.java	1.3 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.internal.POA;

import java.util.*;
import java.rmi.Remote;
import javax.rmi.CORBA.Tie;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POAPackage.*;

public class ShutdownUtilDelegate extends com.sun.corba.se.internal.iiop.ShutdownUtilDelegate
{
    static ShutdownUtilDelegate instance = null;

    public ShutdownUtilDelegate() {
        instance = this;
    }

    // Maps servants to POAs for deactivating servants when unexportObject is called.
    // Maintained by POAs activate_object and deactivate_object.
    private static Map exportedServantsToPOA = new WeakHashMap();

    synchronized POA lookupPOA (Servant servant) {
        return (POA)exportedServantsToPOA.get(servant);
    }

    synchronized void registerPOAForServant(POA poa, Servant servant) {
        exportedServantsToPOA.put(servant, poa);
    }

    synchronized void unregisterPOAForServant(POA poa, Servant servant) {
        exportedServantsToPOA.remove(servant);
    }
}
