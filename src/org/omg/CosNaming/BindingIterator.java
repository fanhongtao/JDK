/*
 * @(#)BindingIterator.java	1.6 98/09/21
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
 * File: ./org/omg/CosNaming/BindingIterator.java
 * From: nameservice.idl
 * Date: Tue Aug 11 03:12:09 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CosNaming;
/**
* The BindingIterator interface allows a client to iterate through
* the bindings using the next_one or next_n operations.
** The bindings iterator is obtained by using the <tt>list</tt>
* method on the <tt>NamingContext</tt>. 
* @see org.omg.CosNaming.NamingContext#list
 */public interface BindingIterator
    extends org.omg.CORBA.Object, org.omg.CORBA.portable.IDLEntity {
    /**
    * This operation returns the next binding. If there are no more
* bindings, false is returned.
** @param b the returned binding
     */
    boolean next_one(org.omg.CosNaming.BindingHolder b)
;
    /**
    * This operation returns at most the requested number of bindings.
** @param how_many the maximum number of bindings tro return <p>
** @param bl the returned bindings
     */
    boolean next_n(int how_many, org.omg.CosNaming.BindingListHolder bl)
;
    /**
    * This operation destroys the iterator.
     */
    void destroy()
;
}
