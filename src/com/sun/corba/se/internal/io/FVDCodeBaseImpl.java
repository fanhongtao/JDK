/*
 * @(#)FVDCodeBaseImpl.java	1.14 03/01/23
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

package com.sun.corba.se.internal.io;

import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.ObjectImpl;
import java.util.Properties;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;
import java.util.Hashtable;
import java.util.Stack;

import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.SendingContext._CodeBaseImplBase;
import com.sun.org.omg.SendingContext.CodeBase;
import com.sun.org.omg.SendingContext.CodeBaseHelper;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;

import com.sun.corba.se.internal.util.MinorCodes;


/**
 * This class acts as the remote interface to receivers wishing to retrieve
 * the information of a remote Class.
 */
public class FVDCodeBaseImpl extends _CodeBaseImplBase
{
    // Contains rep. ids as keys to FullValueDescriptions
    private static Hashtable fvds = new Hashtable();

    // Private ORBSingleton used when we need an ORB while not 
    // having a delegate set.
    private ORB orb = null;
    // backward compatability so that appropriate rep-id calculations
    // can take place
    // this needs to be transient to prevent serialization during
    // marshalling/unmarshalling

    private transient ValueHandlerImpl vhandler = null;

    void setValueHandler(ValueHandler vh)
    {
        vhandler = (com.sun.corba.se.internal.io.ValueHandlerImpl) vh;
    }

    // Operation to obtain the IR from the sending context
    public com.sun.org.omg.CORBA.Repository get_ir (){
	return null;
    }

    // Operations to obtain a URL to the implementation code
    public String implementation (String x){
	try{
	    // default to using the current ORB version in case the
	    // vhandler is not set
	    if (vhandler == null) {
	        vhandler = new ValueHandlerImpl(false);
	    }

            // Util.getCodebase may return null which would
            // cause a BAD_PARAM exception.
	    String result = Util.getCodebase(vhandler.getClassFromType(x));
            if (result == null)
                return "";
            else
                return result;
	}
	catch(ClassNotFoundException cnfe){
	    throw new MARSHAL(MinorCodes.MISSING_LOCAL_VALUE_IMPL,
				   CompletionStatus.COMPLETED_MAYBE);
	}
    }

    public String[] implementations (String[] x){
	String result[] = new String[x.length];

	for (int i = 0; i < x.length; i++)
	    result[i] = implementation(x[i]);

	return result;
    }

    // the same information
    public FullValueDescription meta (String x){
	try{

	    FullValueDescription result = (FullValueDescription)fvds.get(x);

	    if (result == null) {
				
	        // default to using the current ORB version in case the
	        // vhandler is not set
	        if (vhandler == null) {
	            vhandler = new ValueHandlerImpl(false);
	        }

		try{

		    result = ValueUtility.translate(_orb(), ObjectStreamClass.lookup(vhandler.getAnyClassFromType(x)), vhandler);
		}
		catch(Throwable t){
		    if (orb == null)
			orb = ORB.init(); //d11638
		    result = ValueUtility.translate(orb, ObjectStreamClass.lookup(vhandler.getAnyClassFromType(x)), vhandler);		
		}
		if (result != null){
		    fvds.put(x, result);
		}
		else {
		    throw new MARSHAL(MinorCodes.MISSING_LOCAL_VALUE_IMPL,
					   CompletionStatus.COMPLETED_MAYBE);

		}
	    }

				
	    return result;
	}
	catch(Throwable t){
			
	    throw new MARSHAL(MinorCodes.INCOMPATIBLE_VALUE_IMPL,
				   CompletionStatus.COMPLETED_MAYBE);

	}
    }

    public FullValueDescription[] metas (String[] x){
	FullValueDescription descriptions[] = new FullValueDescription[x.length];

	for (int i = 0; i < x.length; i++)
	    descriptions[i] = meta(x[i]);

	return descriptions;
    }

    // information
    public String[] bases (String x){
	try {

	   // default to using the current ORB version in case the
	   // vhandler is not set
	   if (vhandler == null) {
	       vhandler = new ValueHandlerImpl(false);
	    }

	    Stack repIds = new Stack();
	    Class parent = ObjectStreamClass.lookup(vhandler.getClassFromType(x)).forClass().getSuperclass();

	    while (!parent.equals(java.lang.Object.class)) {
		repIds.push(vhandler.createForAnyType(parent));
		parent = parent.getSuperclass();
	    }

	    String result[] = new String[repIds.size()];
	    for (int i = result.length - 1; i >= 0; i++)
		result[i] = (String)repIds.pop();

	    return result;
	}
	catch (Throwable t) {

	    throw new MARSHAL(MinorCodes.MISSING_LOCAL_VALUE_IMPL,
				   CompletionStatus.COMPLETED_MAYBE);

	}
    }

    //d11638 removed getServantIOR and connect methods

}


