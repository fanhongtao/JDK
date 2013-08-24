/*
 * @(#)ReaderThread.java	1.3 05/11/17
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.pept.transport;

/**
 * @author Harold Carr
 */
public interface ReaderThread {
    public Connection getConnection();
    public void close();
}

// End of file.








