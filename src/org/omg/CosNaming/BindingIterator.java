/*
 * @(#)BindingIterator.java	1.7 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
