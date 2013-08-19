/*
 * @(#)FreezableList.java	1.11 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.ior ;

import java.util.Collection ;
import java.util.List ;
import java.util.ListIterator ;
import java.util.Iterator ;

/** Simple class that delegates all List operations to 
* another list.  It also can be frozen, which means that
* a number of operations can be performed on the list,
* and then the list can be made immutable, so that no
* further changes are possible.  A FreezableList is frozen
* using the makeImmutable method.
*/
public class FreezableList implements List {
    private List delegate = null ;
    private boolean immutable = false ;

    public boolean equals( Object obj )
    {
	if (obj == null)
	    return false ;

	if (!(obj instanceof FreezableList))
	    return false ;

	FreezableList other = (FreezableList)obj ;

	return delegate.equals( other.delegate ) &&
	    (immutable == other.immutable) ;
    }

    public FreezableList( List delegate, boolean immutable  )
    {
	this.delegate = delegate ;
	this.immutable = immutable ;
    }

    public FreezableList( List delegate )
    {
	this( delegate, false ) ;
    }

    public void makeImmutable()
    {
	immutable = true ;
    }

    public boolean isImmutable()
    {
	return immutable ;
    }

    // Methods defined in List

    public int size()
    {
	return delegate.size() ;
    }

    public boolean isEmpty()
    {
	return delegate.isEmpty() ;
    }

    public boolean contains(Object o)
    {
	return delegate.contains(o) ;
    }

    public Iterator iterator()
    {
	return new FreezableIterator( delegate.iterator(), this ) ;
    }

    public Object[] toArray()
    {
	return delegate.toArray() ;
    }

    public Object[] toArray(Object a[])
    {
	return delegate.toArray(a) ;
    }

    public boolean add(Object o)
    {
	if (immutable)
	    throw new UnsupportedOperationException() ;

	return delegate.add(o) ;
    }

    public boolean remove(Object o)
    {
	if (immutable)
	    throw new UnsupportedOperationException() ;

	return delegate.remove(o) ;
    }

    public boolean containsAll(Collection c)
    {
	return delegate.containsAll(c) ;
    }

    public boolean addAll(Collection c)
    {
	if (immutable)
	    throw new UnsupportedOperationException() ;

	return delegate.addAll(c) ;
    }

    public boolean addAll(int index, Collection c)
    {
	if (immutable)
	    throw new UnsupportedOperationException() ;

	return delegate.addAll(index, c) ;
    }

    public boolean removeAll(Collection c)
    {
	if (immutable)
	    throw new UnsupportedOperationException() ;

	return delegate.removeAll(c) ;
    }

    public boolean retainAll(Collection c)
    {
	if (immutable)
	    throw new UnsupportedOperationException() ;

	return delegate.retainAll(c) ;
    }

    public void clear()
    {
	if (immutable)
	    throw new UnsupportedOperationException() ;

	delegate.clear() ;
    }

    public int hashCode()
    {
	return delegate.hashCode() ;
    }

    public Object get(int index)
    {
	return delegate.get(index) ;
    }

    public Object set(int index, Object element)
    {
	if (immutable)
	    throw new UnsupportedOperationException() ;

	return delegate.set(index, element) ;
    }

    public void add(int index, Object element)
    {
	if (immutable)
	    throw new UnsupportedOperationException() ;

	delegate.add(index, element) ;
    }

    public Object remove(int index)
    {
	if (immutable)
	    throw new UnsupportedOperationException() ;

	return delegate.remove(index) ;
    }

    public int indexOf(Object o)
    {
	return delegate.indexOf(o) ;
    }

    public int lastIndexOf(Object o)
    {
	return delegate.lastIndexOf(o) ;
    }

    public ListIterator listIterator()
    {
	return new FreezableListIterator( delegate.listIterator(), this ) ;
    }

    public ListIterator listIterator(int index)
    {
	return new FreezableListIterator( delegate.listIterator( index ), this ) ;
    }

    public List subList(int fromIndex, int toIndex)
    {
	List list = delegate.subList(fromIndex, toIndex) ;
	List result = new FreezableList( list, immutable ) ;
	return result ;
    }
    
    public String toString()
    {
	return delegate.toString() ;
    }

    class FreezableIterator implements Iterator {
	protected Iterator iter ;
	protected FreezableList list ;

	public FreezableIterator( Iterator iter, FreezableList list ) 
	{
	    this.iter = iter ;
	    this.list = list ;
	}

	public boolean hasNext()
	{
	    return iter.hasNext() ;
	}

	public Object next()
	{
	    return iter.next() ;
	}

	public void remove()
	{
	    if (list.isImmutable())
		throw new UnsupportedOperationException() ;
	    else
		iter.remove() ;
	}
    }

    class FreezableListIterator extends FreezableIterator 
	implements ListIterator 
    {
	public FreezableListIterator( ListIterator iter, FreezableList list ) 
	{
	    super( iter, list ) ;
	}

	public boolean hasPrevious()
	{
	    return ((ListIterator)iter).hasPrevious() ;
	}

	public Object previous()
	{
	    return ((ListIterator)iter).previous() ;
	}

	public int nextIndex() 
	{
	    return ((ListIterator)iter).nextIndex() ;
	}

	public int previousIndex()
	{
	    return ((ListIterator)iter).previousIndex() ;
	}

	public void set(Object o)
	{
	    if (list.isImmutable())
		throw new UnsupportedOperationException() ;
	    else
		((ListIterator)iter).set(o) ;
	}

	public void add(Object o)
	{
	    if (list.isImmutable())
		throw new UnsupportedOperationException() ;
	    else
		((ListIterator)iter).add(o) ;
	}
    }
    }
