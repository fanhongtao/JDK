/*
 * @(#)SyncFactoryException.java	1.4 04/03/11
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.sql.rowset.spi;

import java.sql.SQLException;

/**
 * Indicates an error with <code>SyncFactory</code> mechanism. A disconnected
 * RowSet implementation cannot be used  without a <code>SyncProvider</code>
 * being successfully instantiated
 *
 * @version 0.2
 * @author Jonathan Bruce
 * @see javax.sql.rowset.spi.SyncFactory
 * @see javax.sql.rowset.spi.SyncFactoryException
 */
public class SyncFactoryException extends java.sql.SQLException {

    /**
     * Creates new <code>SyncFactoryException</code> without detail message.
     */
    public SyncFactoryException() {
    }

    /**
     * Constructs an <code>SyncFactoryException</code> with the specified 
     * detail message.
     *
     * @param msg the detail message.
     */
    public SyncFactoryException(String msg) {
        super(msg);
    }
        
    static final long serialVersionUID = -4354595476433200352L;
}


