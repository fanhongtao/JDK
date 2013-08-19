/*
 * @(#)SpecialMethod.java	1.23 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.corba;

import javax.rmi.CORBA.Tie;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.*;

import com.sun.corba.se.internal.core.ServerRequest;
import com.sun.corba.se.internal.core.ServerResponse;

import com.sun.corba.se.internal.orbutil.MinorCodes;

public abstract class SpecialMethod {
    public abstract String getName();
    public abstract ServerResponse invoke(java.lang.Object servant,
					  ServerRequest request);

    public static final SpecialMethod getSpecialMethod(String operation) {
	for(int i = 0; i < methods.length; i++)
	    if (methods[i].getName().equals(operation))
		return methods[i];
	return null;
    }

    public static final boolean isSpecialMethod(String operation) {
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
    public String getName() {		// _non_existent
	return "_non_existent";
    }

    public ServerResponse invoke(java.lang.Object servant,
				 ServerRequest request)
    {
	boolean result = (servant == null) ? true : false;
	ServerResponse response = request.createResponse(null);
	response.write_boolean(result);
	return response;
    }
}

class NotExistent extends NonExistent {
    public String getName() {		// _not_existent
	return "_not_existent";
    }
}

class IsA extends SpecialMethod  {	// _is_a
    public String getName() {
	return "_is_a";
    }
    public ServerResponse invoke(java.lang.Object servant,
				 ServerRequest request)
    {
	if (servant == null) {
	    SystemException ex
		= new OBJECT_NOT_EXIST(MinorCodes.BAD_SKELETON,
				       CompletionStatus.COMPLETED_NO);
	    return
		request.createSystemExceptionResponse(ex, null);
	}
	
	String[] ids = ((ObjectImpl) servant)._ids();
	String clientId = request.read_string();
	boolean answer = false;
	for(int i = 0; i < ids.length; i++)
	    if (ids[i].equals(clientId)) {
    		answer = true;
    		break;
	    }
	    
	ServerResponse response = request.createResponse(null);
	response.write_boolean(answer);
	return response;
    }
}

class GetInterface extends SpecialMethod  {	// _get_interface
    public String getName() {
	return "_interface";
    }
    public ServerResponse invoke(java.lang.Object servant,
				 ServerRequest request)
    {
	if (servant == null) {
	    SystemException ex = new OBJECT_NOT_EXIST(MinorCodes.BAD_SKELETON,
						      CompletionStatus.COMPLETED_NO);
	    return request.createSystemExceptionResponse(ex, null);
	}
	SystemException ex = new NO_IMPLEMENT(MinorCodes.GETINTERFACE_NOT_IMPLEMENTED,
					      CompletionStatus.COMPLETED_NO);
	return request.createSystemExceptionResponse(ex, null);
    }
}

