/* 
 * @(#)Unmarshal.java	1.2 04/01/26
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */ 

package com.sun.jmx.remote.internal;

import java.io.IOException;
import java.rmi.MarshalledObject;

public interface Unmarshal {
    public Object get(MarshalledObject mo)
	    throws IOException, ClassNotFoundException;
}
