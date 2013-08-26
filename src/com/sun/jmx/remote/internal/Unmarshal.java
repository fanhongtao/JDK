/* 
 * @(#)Unmarshal.java	1.4 10/03/23
 * 
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */ 

package com.sun.jmx.remote.internal;

import java.io.IOException;
import java.rmi.MarshalledObject;

public interface Unmarshal {
    public Object get(MarshalledObject mo)
	    throws IOException, ClassNotFoundException;
}
