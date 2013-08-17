/*
 * @(#)IRObject.java	1.13 99/04/22
 *
 * Copyright 1997-1999 by Sun Microsystems, Inc.,
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
*/
public interface IRObject
    extends org.omg.CORBA.Object, org.omg.CORBA.portable.IDLEntity
{
    /**
    * Returns the <code>DefinitionKind</code> corresponding to this Interface Repository object.
    * @return the <code>DefinitionKind</code> corresponding to this Interface Repository object.
    */
    org.omg.CORBA.DefinitionKind def_kind();
    /**
     * Destroys this object. If the object is a Container,
     * this method is applied to all its contents. If the object contains an IDLType
     * attribute for an anonymous type, that IDLType is destroyed.
     * If the object is currently contained in some other object, it is removed.
     * If the method is invoked on a <code>Repository</code> or on a <code>PrimitiveDef</code>
     * then the <code>BAD_INV_ORDER</code> exception is raised with minor value 2.
     * An attempt to destroy an object that would leave the repository in an
     * incoherent state causes <code>BAD_INV_ORDER</code> exception to be raised
     * with the minor code 1.
     * @exception <code>BAD_INV_ORDER</code>
     */
    void destroy();
}
