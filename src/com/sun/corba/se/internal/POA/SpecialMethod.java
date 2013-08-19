/*
 * @(#)SpecialMethod.java	1.20 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.POA;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.*;
import org.omg.PortableServer.Servant ;

import com.sun.corba.se.internal.core.*;
import com.sun.corba.se.internal.POA.MinorCodes;


abstract class SpecialMethod {
    abstract String getName();
    abstract ServerResponse invoke(Servant servant,
			           com.sun.corba.se.internal.core.ServerRequest req);

    static final SpecialMethod getSpecialMethod(String operation) {
	for(int i = 0; i < methods.length; i++)
	    if (methods[i].getName().equals(operation))
		return methods[i];
	return null;
    }

    static final boolean isSpecialMethod(String operation) {
        for(int i = 0; i < methods.length; i++)
	    if (methods[i].getName().equals(operation))
		return true;
	return false;
    }
    
    static SpecialMethod[] methods = {
	new IsA(),
	new GetInterface(),
	new NonExistent(),
	new NotExistent()
    };
}

class NonExistent extends SpecialMethod {
    String getName() {		// _non_existent
	return "_non_existent";  
    }

    ServerResponse invoke(Servant servant,
			  com.sun.corba.se.internal.core.ServerRequest req) {
	boolean result = (servant == null) ? true : false;
	ServerResponse resp = req.createResponse(null);
	OutputStream os = (OutputStream)resp;
	os.write_boolean(result);
	return resp;
    }
}

class NotExistent extends NonExistent {
    public String getName() {		// _not_existent
	return "_not_existent";
    }
}

class IsA extends SpecialMethod  {	// _is_a
    String getName() {
	return "_is_a";
    }
    ServerResponse invoke(Servant servant,
			  com.sun.corba.se.internal.core.ServerRequest req) {
	if (servant == null)
	    POAImpl.objectNotExist(MinorCodes.NULL_SERVANT,
				   CompletionStatus.COMPLETED_NO);
	String[] ids = 
            servant._all_interfaces(servant._poa(),servant._object_id());
	String clientId = ((InputStream)req).read_string();
	boolean answer = false;
	for(int i = 0; i < ids.length; i++)
	    if (ids[i].equals(clientId)) {
		answer = true;
	    }
	    
	ServerResponse resp = req.createResponse(null);
	OutputStream os = (OutputStream)resp;
	os.write_boolean(answer);
	return resp;
    }
}

class GetInterface extends SpecialMethod  {	// _get_interface
    String getName() {
	return "_interface";
    }
    ServerResponse invoke(Servant servant,
			  com.sun.corba.se.internal.core.ServerRequest req) {
	if (servant == null)
	    POAImpl.objectNotExist(MinorCodes.NULL_SERVANT,
				   CompletionStatus.COMPLETED_NO);
	throw new
	    NO_IMPLEMENT(com.sun.corba.se.internal.orbutil.MinorCodes.GETINTERFACE_NOT_IMPLEMENTED,
			 CompletionStatus.COMPLETED_NO);
    }
}


