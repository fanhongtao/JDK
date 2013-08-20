/*
 * @(#)Delegate.java	1.13 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
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

package org.omg.CORBA_2_3.portable;

/** 
 * Delegate class provides the ORB vendor specific implementation
 * of CORBA object.  It extends org.omg.CORBA.portable.Delegate and
 * provides new methods that were defined by CORBA 2.3.
 *
 * @see org.omg.CORBA.portable.Delegate
 * @author  OMG
 * @version 1.13 12/19/03
 * @since   JDK1.2
 */

public abstract class Delegate extends org.omg.CORBA.portable.Delegate {

    /** Returns the codebase for object reference provided.
     * @param self the object reference whose codebase needs to be returned.
     * @return the codebase as a space delimited list of url strings or
     * null if none.
     */
    public java.lang.String get_codebase(org.omg.CORBA.Object self) {
        return null;
    }
}
