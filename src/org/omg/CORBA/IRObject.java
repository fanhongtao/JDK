/*
 * @(#)IRObject.java	1.10 98/09/21
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
/*
 * File: ./org/omg/CORBA/IRObject.java
 * From: ./ir.idl
 * Date: Fri Aug 28 16:03:31 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CORBA;
/**
An IRObject IDL interface represents the most generic interface
from which all other Interface Repository interfaces are derived,
even the Repository itself.
 */public interface IRObject
    extends org.omg.CORBA.Object, org.omg.CORBA.portable.IDLEntity {
    org.omg.CORBA.DefinitionKind def_kind();
    void destroy()
;
}
