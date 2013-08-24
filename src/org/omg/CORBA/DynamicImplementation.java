/*
 * @(#)DynamicImplementation.java	1.24 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.omg.CORBA;

import org.omg.CORBA.portable.ObjectImpl;

/**
 * @deprecated org.omg.CORBA.DynamicImplementation
 */
@Deprecated
public class DynamicImplementation extends org.omg.CORBA.portable.ObjectImpl {

    /**
      * @deprecated Deprecated by Portable Object Adapter
      */
    @Deprecated
    public void invoke(ServerRequest request) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public String[] _ids() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }
}
