/*
 * @(#)BeanContextSupport.java	1.30 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.beans.beancontext;

import java.awt.Component;
import java.awt.Container;

import java.beans.Beans;
import java.beans.AppletInitializer;

import java.beans.DesignMode;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.beans.PropertyVetoException;

import java.beans.Visibility;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;


/**
 * <p>
 * This helper class provides a utility implementation of the
 * java.beans.beancontext.BeanContext interface.
 * </p>
 * <p>
 * Since this class directly implements the BeanContext interface, the class
 * can, and is intended to be used either by subclassing this implementation,
 * or via ad-hoc delegation of an instance of this class from another.
 * </p>
 *
 * @author Laurence P. G. Cable
 * @version 1.30
 * @since JDK1.2
 */

public class      BeanContextSupport extends BeanContextChildSupport
       implements BeanContext,
		  Serializable,
		  PropertyChangeListener,
		  VetoableChangeListener {

    /**
     * <p>
     * Construct a BeanContextSupport instance
     * </p>
     *
     * @param peer	The peer BeanContext we are supplying an implementation for, if null the this object is its own peer
     * @param lcle	The current Locale for this BeanContext. If 
     *                  <tt>lcle</tt> is <tt>null</tt>, the default locale 
     *                  is assigned to the BeanContext instance. 
     * @param dtime	The initial state, true if in design mode, false if runtime.
     * @param visible	The initial visibility.
     * @see java.util.Locale#getDefault()      
     * @see java.util.Locale@setDefault(java.util.Locale)  
     * 
     */

    public BeanContextSupport(BeanContext peer, Locale lcle, boolean dTime, boolean visible) {
	super(peer);

	locale          = lcle != null ? lcle : Locale.getDefault();
	designTime      = dTime;
	okToUseGui      = visible;

	initialize();
    }

    /**
     * Create an instance using the specified Locale and design mode.
     *
     * @param peer	The peer BeanContext we are supplying an implementation for, if null the this object is its own peer
     * @param lcle	The current Locale for this BeanContext. If 
     *                  <tt>lcle</tt> is <tt>null</tt>, the default locale 
     *                  is assigned to the BeanContext instance. 
     * @param dtime	The initial state, true if in design mode, false if runtime.
     * @see java.util.Locale#getDefault()      
     * @see java.util.Locale@setDefault(java.util.Locale)  
     * 
     */

    public BeanContextSupport(BeanContext peer, Locale lcle, boolean dtime) {
	this (peer, lcle, dtime, true);
    }

    /**
     * Create an instance using the specified locale
     *
     * @param peer	The peer BeanContext we are supplying an implementation for, if null the this object is its own peer
     * @param lcle	The current Locale for this BeanContext. If 
     *                  <tt>lcle</tt> is <tt>null</tt>, the default locale 
     *                  is assigned to the BeanContext instance.
     * @see java.util.Locale#getDefault()      
     * @see java.util.Locale@setDefault(java.util.Locale)  
     *  
     */

    public BeanContextSupport(BeanContext peer, Locale lcle) {
	this (peer, lcle, false, true);
    }

    /**
     * Create an instance using with a default locale
     *
     * @param peer	The peer BeanContext we are supplying an implementation for, if null the this object is its own peer
     */

    public BeanContextSupport(BeanContext peer) {
	this (peer, null, false, true);
    }

    /**
     * Create an instance that is not a delegate of another object
     */

    public BeanContextSupport() {
	this (null, null, false, true);
    }

    /**
     * @return the instance of BeanContext this object is providing the implemen
tation for.
     */
 
    public BeanContext getBeanContextPeer() { return (BeanContext)getBeanContextChildPeer(); }

    /**
     * <p>
     * The instantiateChild method is a convenience hook in BeanContext to simplify
     * the task of instantiating a Bean, nested, into a BeanContext.
     * </p>
     * <p>
     * The semantics of the beanName parameter are defined by java.beans.Beans.instantate.
     * </p>
     *
     * @param beanName the name of the Bean to instantiate within this BeanContext
     */

    public Object instantiateChild(String beanName)
	   throws IOException, ClassNotFoundException {
	BeanContext bc = getBeanContextPeer();

	return Beans.instantiate(bc.getClass().getClassLoader(), beanName, bc);
    }

    /**
     * @return number of children
     */

    public int size() {
	synchronized(children) {
	    return children.size();
	}
    }

    /**
     * @return if there are not children
     */

    public boolean isEmpty() {
	synchronized(children) {
	    return children.isEmpty();
	}
    }

    /**
     * @return if this object is a child
     */

    public boolean contains(Object o) {
	synchronized(children) {
	    return children.containsKey(o);
	}
    }

    /**
     * @return if this object is a child
     */

    public boolean containsKey(Object o) {
	synchronized(children) {
	    return children.containsKey(o);
	}
    }

    /**
     * @return the iterator
     */

    public Iterator iterator() {
	synchronized(children) {
	    return new BCSIterator(children.keySet().iterator());
	}
    }

    /**
     * @return an array of children
     */

    public Object[] toArray() {
	synchronized(children) {
	    return children.keySet().toArray();
	}
    }

    /**
     * @return an array of children
     */

    public Object[] toArray(Object[] arry) {
	synchronized(children) {
	    return children.keySet().toArray(arry);
	}
    }


    /************************************************************************/

    /**
     * protected final subclass that encapsulates an iterator but implements
     * a noop remove() method.
     */

    protected static final class BCSIterator implements Iterator {
	BCSIterator(Iterator i) { super(); src = i; }

	public boolean hasNext() { return src.hasNext(); }
	public Object  next()    { return src.next();    }
	public void    remove()  { /* do nothing */      }

	private Iterator src;
    }

    /************************************************************************/

    /*
     * protected nested class containing per child information, an instance
     * of which is associated with each child in the "children" hashtable.
     * subclasses can extend this class to include their own per-child state.
     *
     * Note that this 'value' is serialized with the corresponding child 'key'
     * when the BeanContextSupport is serialized.
     */

    protected class BCSChild implements Serializable {
	BCSChild(Object bcc, Object peer) {
	    super();

	    child     = bcc;
	    proxyPeer = peer;
	}

	Object  getChild()	            { return child; }

	void    setRemovePending(boolean v) { removePending = v; }

	boolean isRemovePending()           { return removePending; }

        boolean isProxyPeer()		    { return proxyPeer != null; }

	Object  getProxyPeer()		    { return proxyPeer; }
	/*
	 * fields
	 */


	private		  Object   child;
	private           Object   proxyPeer;

	private transient boolean  removePending; 
    }

    /**
     * <p>
     * Subclasses can override this method to insert their own subclass
     * of Child without having to override add() or the other Collection
     * methods that add children to the set.
     * </p>
     *
     * @param targetChild the child to create the Child on behalf of
     * @param peer        the peer if the tragetChild and the peer are related by an implementation of BeanContextProxy
     */

    protected BCSChild createBCSChild(Object targetChild, Object peer) {
	return new BCSChild(targetChild, peer);
    }

    /************************************************************************/

    /**
     * <p>
     * Adds/nests a child within this BeanContext
     * </p>
     * <p>
     * Invoked as a side effect of java.beans.Beans.instantiate().
     * </p>
     * 
     * @param targetChildren The child objects to nest within this BeanContext
     */

    public boolean add(Object targetChild) {

	if (targetChild == null) throw new IllegalArgumentException();

	// The specification requires that we do nothing if the child
	// is already nested herein.

	if (children.containsKey(targetChild)) return false; // test before locking

	synchronized(BeanContext.globalHierarchyLock) {
	    if (children.containsKey(targetChild)) return false; // check again

	    if (!validatePendingAdd(targetChild)) {
	        throw new IllegalStateException();
	    }


	    // The specification requires that we invoke setBeanContext() on the
	    // newly added child if it implements the java.beans.beancontext.BeanContextChild interface

	    BeanContextChild cbcc  = getChildBeanContextChild(targetChild);
	    BeanContextChild  bccp = null;

	    synchronized(targetChild) {

		if (targetChild instanceof BeanContextProxy) {
		    bccp = ((BeanContextProxy)targetChild).getBeanContextProxy();

		    if (bccp == null) throw new NullPointerException("BeanContextPeer.getBeanContextProxy()");
		}

	        BCSChild bcsc  = createBCSChild(targetChild, bccp);
		BCSChild pbcsc = null;

	        synchronized (children) {
		    children.put(targetChild, bcsc);

		    if (bccp != null) children.put(bccp, pbcsc = createBCSChild(bccp, targetChild));
		}

	        if (cbcc != null) synchronized(cbcc) {
		    try {
	                cbcc.setBeanContext(getBeanContextPeer());
	            } catch (PropertyVetoException pve) {

		        synchronized (children) {
			    children.remove(targetChild);

			    if (bccp != null) children.remove(bccp);
			}

	                throw new IllegalStateException();
	            }

	            cbcc.addPropertyChangeListener("beanContext", childPCL);
	            cbcc.addVetoableChangeListener("beanContext", childVCL);
		}

	        Visibility v = getChildVisibility(targetChild);

	        if (v != null) {
	            if (okToUseGui)
	                v.okToUseGui();
	            else
	                v.dontUseGui();
	        }

	        if (getChildSerializable(targetChild) != null) serializable++;

	        childJustAddedHook(targetChild, bcsc);

		if (bccp != null) {
	            v = getChildVisibility(bccp);

	            if (v != null) {
	                if (okToUseGui)
	                    v.okToUseGui();
	                else
	                    v.dontUseGui();
	            }

		    if (getChildSerializable(bccp) != null) serializable++;

	            childJustAddedHook(bccp, pbcsc);
		}


	    }

	    // The specification requires that we fire a notification of the change

	    fireChildrenAdded(new BeanContextMembershipEvent(getBeanContextPeer(), bccp == null ? new Object[] { targetChild } : new Object[] { targetChild, bccp } ));

        }

	return true;
    }

    /**
     * @param targetChildren The child objects to remove
     */

    public boolean remove(Object targetChild) {
	return remove(targetChild, true);
    }

    /*
     * internal remove used when removal caused by unexpected setBeanContext or
     * by remove() invocation
     */

    protected boolean remove(Object targetChild, boolean callChildSetBC) {

	if (targetChild == null) throw new IllegalArgumentException();

	synchronized(BeanContext.globalHierarchyLock) {
	    if (!containsKey(targetChild)) return false;

	    if (!validatePendingRemove(targetChild)) {
	        throw new IllegalStateException();
	    }

            BCSChild bcsc  = (BCSChild)children.get(targetChild);
	    BCSChild pbcsc = null;
	    Object   peer  = null;

	    // we are required to notify the child that it is no longer nested here if
	    // it implements java.beans.beancontext.BeanContextChild

	    synchronized(targetChild) {
	        if (callChildSetBC) {
	            BeanContextChild cbcc = getChildBeanContextChild(targetChild);
	            if (cbcc != null) synchronized(cbcc) {
	                cbcc.removePropertyChangeListener("beanContext", childPCL);
	                cbcc.removeVetoableChangeListener("beanContext", childVCL);

			try {
	                    cbcc.setBeanContext(null);
	                } catch (PropertyVetoException pve1) {
	                    cbcc.addPropertyChangeListener("beanContext", childPCL);
	                    cbcc.addVetoableChangeListener("beanContext", childVCL);
	    	            throw new IllegalStateException();
	                }

		    }
	        }

	        synchronized (children) {
		    children.remove(targetChild);

		    if (bcsc.isProxyPeer()) {
			pbcsc = (BCSChild)children.get(peer = bcsc.getProxyPeer());
			children.remove(peer);
		    }
		}

	        if (getChildSerializable(targetChild) != null) serializable--;

	        childJustRemovedHook(targetChild, bcsc);

		if (peer != null) {
	            if (getChildSerializable(peer) != null) serializable--;

	            childJustRemovedHook(peer, pbcsc);
		}
	    }

	    fireChildrenRemoved(new BeanContextMembershipEvent(getBeanContextPeer(), peer == null ? new Object[] { targetChild } : new Object[] { targetChild, peer } ));

	}

	return true;
    }

    /**
     * @return if all object objects in specified Collection are children.
     */

    public boolean containsAll(Collection c) {
	synchronized(children) {
	    Iterator i = c.iterator();
	    while (i.hasNext())
	        if(!contains(i.next()))
		    return false;

	    return true;
	}
    }

    /**
     * add Collection to set of Children (Unsupported)
     * implementations must synchronized on the hierarchy lock and "children" protected field
     */

    public boolean addAll(Collection c) {
	throw new UnsupportedOperationException();
    }

    /**
     * remove all specified children (Unsupported)
     * implementations must synchronized on the hierarchy lock and "children" protected field
     */

    public boolean removeAll(Collection c) {
	throw new UnsupportedOperationException();
    }


    /**
     * retain only specified children (Unsupported)
     * implementations must synchronized on the hierarchy lock and "children" protected field
     */

    public boolean retainAll(Collection c) {
	throw new UnsupportedOperationException();
    }

    /**
     * clear the children (Unsupported)
     * implementations must synchronized on the hierarchy lock and "children" protected field
     */

    public void clear() {
	throw new UnsupportedOperationException();
    }

    /**
     * Adds a BeanContextMembershipListener
     *
     * @param bcml the BeanContextMembershipListener to add
     * @throw NullPointerException
     */

    public void addBeanContextMembershipListener(BeanContextMembershipListener bcml) {
	if (bcml == null) throw new NullPointerException("listener");

	synchronized(bcmListeners) {
	    if (bcmListeners.contains(bcml))
		return;
	    else
	        bcmListeners.add(bcml);
	}
    }

    /**
     * Removes a BeanContextMembershipListener
     *
     * @param bcml the BeanContextMembershipListener to remove
     * @throw NullPointerException
     */

    public void removeBeanContextMembershipListener(BeanContextMembershipListener bcml) {
	if (bcml == null) throw new NullPointerException("listener");

	synchronized(bcmListeners) {
	    if (!bcmListeners.contains(bcml))
		return;
	    else
	        bcmListeners.remove(bcml);
	}
    }

    /**
     * @param name the name of the resource requested.
     * @param child the child object making the request.
     *
     * @return the requested resource as an InputStream
     * @throw  NullPointerException
     */

    public InputStream getResourceAsStream(String name, BeanContextChild bcc) {
	if (name == null) throw new NullPointerException("name");
	if (bcc  == null) throw new NullPointerException("bcc");

	boolean isChild;

	synchronized(children) {
	    isChild = children.containsValue(bcc);
	}

	if (isChild) {
	    ClassLoader cl = bcc.getClass().getClassLoader();

	    return cl != null ? cl.getResourceAsStream(name)
			      : ClassLoader.getSystemResourceAsStream(name);
	} else throw new IllegalArgumentException("Not a valid child");
    }

    /**
     * @param name the name of the resource requested.
     * @param child the child object making the request.
     *
     * @return the requested resource as an InputStream
     */

    public URL getResource(String name, BeanContextChild bcc) {
	if (name == null) throw new NullPointerException("name");
	if (bcc  == null) throw new NullPointerException("bcc");

	boolean isChild;

	synchronized(children) {
	    isChild = children.containsValue(bcc);
	}

	if (isChild) {
	    ClassLoader cl = bcc.getClass().getClassLoader();

	    return cl != null ? cl.getResource(name)
			      : ClassLoader.getSystemResource(name);
	} else throw new IllegalArgumentException("Not a valid child");
    }

    /**
     * @param dTime the new designTime value
     */

    public synchronized void setDesignTime(boolean dTime) {
	if (designTime != dTime) {
	    designTime = dTime;

	    firePropertyChange("designMode", new Boolean(!dTime), new Boolean(dTime));
	}
    }

  
    /**
     * @return Design time
     */

    public synchronized boolean isDesignTime() { return designTime; }

    /**
     * @param newLocale the new locale
     */

    public synchronized void setLocale(Locale newLocale) throws PropertyVetoException {

	if ((locale != null && !locale.equals(newLocale)) || newLocale != null) {
	    Locale old = locale;

	    fireVetoableChange("locale", old, newLocale); // throws

	    locale = newLocale;

	    firePropertyChange("locale", old, newLocale);
	}
    }

    /**
     * @return the current Locale of the BeanContext
     */

    public synchronized Locale getLocale() { return locale; }

    /*
     * <p>
     * This method is typically called from the environment in order to determine
     * if the implementor "needs" a GUI.
     * </p>
     * <p>
     * The algorithm used herein tests the BeanContextPeer, and its current children
     * to determine if they are either Containers, Components, or if they implement
     * Visibility and return needsGui() == true.
     * </p>
     */

    public synchronized boolean needsGui() {
	BeanContext bc = getBeanContextPeer();

	if (bc != this) {
	    if (bc instanceof Visibility) return ((Visibility)bc).needsGui();

	    if (bc instanceof Container || bc instanceof Component)
		return true;
	}

	synchronized(children) {
	    for (Iterator i = children.keySet().iterator(); i.hasNext();) {
	        Object c = i.next();

	        try {
		        return ((Visibility)c).needsGui();
		    } catch (ClassCastException cce) {
		        // do nothing ...
		    }

		    if (c instanceof Container || c instanceof Component)
		        return true;
	    }
	}

	return false;
    }

    /**
     * notify this instance that it may no longer render a GUI.
     */

    public synchronized void dontUseGui() {
	if (okToUseGui) {
	    okToUseGui = false;

	    // lets also tell the Children that can that they may not use their GUI's
	    synchronized(children) {
	        for (Iterator i = children.keySet().iterator(); i.hasNext();) {
		    Visibility v = getChildVisibility(i.next());

		    if (v != null) v.dontUseGui();
	       }
	    }
	}
    }

    /**
     * Notify this instance that it may now render a GUI
     */

    public synchronized void okToUseGui() {
	if (!okToUseGui) {
	    okToUseGui = true;

	    // lets also tell the Children that can that they may use their GUI's
	    synchronized(children) {
	        for (Iterator i = children.keySet().iterator(); i.hasNext();) {
		    Visibility v = getChildVisibility(i.next());

		    if (v != null) v.okToUseGui();
	        }
	    }
	}
    }

    /**
     * @return is this instance avoiding using its GUI?
     */

    public boolean avoidingGui() {
	return !okToUseGui && needsGui();
    }

    /**
     * @return if this BeanContext is currently being serialized
     */

    public boolean isSerializing() { return serializing; }

    /**
     * @return an iterator for all the current BCSChild values 
     */

    protected Iterator bcsChildren() { synchronized(children) { return children.values().iterator();  } }

    /**
     * called by writeObject after defaultWriteObject() but prior to
     * serialization of currently serializable children.
     *
     * This method may be overridden by subclasses to perform custom
     * serialization of their state prior to this superclass serializing
     * the children.
     *
     * This method should not however be used by subclasses to replace their
     * own implementation (if any) of writeObject().
     */

    protected void bcsPreSerializationHook(ObjectOutputStream oos) throws IOException {
    }

    /**
     * called by readObject after defaultReadObject() but prior to
     * deserialization of any children.
     *
     * This method may be overridden by subclasses to perform custom
     * deserialization of their state prior to this superclass deserializing
     * the children.
     *
     * This method should not however be used by subclasses to replace their
     * own implementation (if any) of readObject().
     */

    protected void bcsPreDeserializationHook(ObjectInputStream ois) throws IOException, ClassNotFoundException {
    }

    /**
     * called by readObject with the newly deserialized child and BCSChild
     */

    protected void childDeserializedHook(Object child, BCSChild bcsc) {
	synchronized(children) {
	    children.put(child, bcsc);
	}
    }

    /*
     * used by writeObject to serialize a Collection
     */

    protected final void serialize(ObjectOutputStream oos, Collection coll) throws IOException {
   	int      count   = 0;
	Object[] objects = coll.toArray();
    
	for (int i = 0; i < objects.length; i++) {
	    if (objects[i] instanceof Serializable)
		count++;
	    else
		objects[i] = null;
	}

        oos.writeInt(count); // number of subsequent objects

	for (int i = 0; count > 0; i++) {
	    Object o = objects[i];

	    if (o != null) {
		oos.writeObject(o);
		count--;
	    }
	}
    }

    /**
     * used by readObject to deserialize a collection
     */

    protected final void deserialize(ObjectInputStream ois, Collection coll) throws IOException, ClassNotFoundException {
	int count = 0;

	count = ois.readInt();

	while (count-- > 0) {
	    coll.add(ois.readObject());
	}
    }

    /** 
     *
     */

    public final void writeChildren(ObjectOutputStream oos) throws IOException {
	if (serializable <= 0) return;

	boolean prev = serializing;

	serializing = true;

	int count = 0;

	synchronized(children) {
	    Iterator i = children.entrySet().iterator();

	    while (i.hasNext() && count < serializable) {
	        Map.Entry entry = (Map.Entry)i.next();

	        if (entry.getKey() instanceof Serializable) {
	      	    try {
		        oos.writeObject(entry.getKey());   // child
		        oos.writeObject(entry.getValue()); // BCSChild
		    } catch (IOException ioe) {
		        serializing = prev;
		        throw ioe;
		    }
		    count++;
	        }
	    }
	}
	
        serializing = prev;

	if (count != serializable) {
	    throw new IOException("wrote different number of children than expected");
	}

    }

    /**
     * Serialize the BeanContextSupport, if this instance has a distinct 
     * peer (that is this object is acting as a delegate for another) then
     * the children of this instance are not serialized here due to a 
     * 'chicken and egg' problem that occurs on deserialization of the 
     * children at the same time as this instance. 
     *
     * Therefore in situations where there is a distinct peer to this instance
     * it should always call writeObject() followed by writeChildren() and
     * readObject() followed by readChildren().
     *
     * @param oos the ObjectOutputStream
     */

    private synchronized void writeObject(ObjectOutputStream oos) throws IOException, ClassNotFoundException {
	serializing = true;

	synchronized (BeanContext.globalHierarchyLock) {
	    try {
	        oos.defaultWriteObject(); // serialize the BeanContextSupport object

	        bcsPreSerializationHook(oos);

	        if (serializable > 0 && this.equals(getBeanContextPeer()))
	            writeChildren(oos);

	        serialize(oos, (Collection)bcmListeners);
	    } finally {
	        serializing = false;
	    }
 	}
    }

    /**
     * When an instance of this class is used as a delegate for the
     * implementation of the BeanContext protocols (and its subprotocols)
     * there exists a 'chicken and egg' problem during deserialization 
     */

    public final void readChildren(ObjectInputStream ois) throws IOException, ClassNotFoundException {
	int count = serializable; 

	while (count-- > 0) {
	    Object                      child = null;
	    BeanContextSupport.BCSChild bscc  = null;
	  
	    try {
	        child = ois.readObject();
	        bscc  = (BeanContextSupport.BCSChild)ois.readObject();
	    } catch (IOException ioe) {
		continue;
	    } catch (ClassNotFoundException cnfe) {
		continue;
	    }


	    synchronized(child) {
		BeanContextChild bcc = null;

		try {
		    bcc = (BeanContextChild)child;
		} catch (ClassCastException cce) {
		    // do nothing;
		}

		if (bcc != null) {
		    try {
			bcc.setBeanContext(getBeanContextPeer());

	               bcc.addPropertyChangeListener("beanContext", childPCL);
	               bcc.addVetoableChangeListener("beanContext", childVCL);
	
		    } catch (PropertyVetoException pve) {
			continue;
		    }
		}

		childDeserializedHook(child, bscc);
	    }
	}
    }

    /**
     * deserialize contents ... if this instance has a distinct peer the
     * children are *not* serialized here, the peer's readObject() must call
     * readChildren() after deserializing this instance.
     */

    private synchronized void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {

	synchronized(BeanContext.globalHierarchyLock) {
	    ois.defaultReadObject();

	    initialize();

	    bcsPreDeserializationHook(ois);

	    if (serializable > 0 && this.equals(getBeanContextPeer()))
	        readChildren(ois);

	    deserialize(ois, bcmListeners = new ArrayList(1));
	}
    }

    /**
     * subclasses may envelope to monitor veto child property changes.
     */

    public void vetoableChange(PropertyChangeEvent pce) throws PropertyVetoException {
	String propertyName = pce.getPropertyName();
	Object source	    = pce.getSource();

	synchronized(children) {
	    if ("beanContext".equals(propertyName) &&
	        containsKey(source)	               &&
	        !getBeanContextPeer().equals(pce.getNewValue())
	    ) {
	        if (!validatePendingRemove(source)) {
		    throw new PropertyVetoException("current BeanContext vetoes setBeanContext()", pce);
	        } else ((BCSChild)children.get(source)).setRemovePending(true);
	    }
	}
    }

    /**
     * subclasses may envelope to monitor child property changes.
     */

    public void propertyChange(PropertyChangeEvent pce) {
	String propertyName = pce.getPropertyName();
	Object source	    = pce.getSource();

	synchronized(children) {
	    if ("beanContext".equals(propertyName) &&
	        containsKey(source)		       &&
	        ((BCSChild)children.get(source)).isRemovePending()) {
	        BeanContext bc = getBeanContextPeer();

	        if (bc.equals(pce.getOldValue()) && !bc.equals(pce.getNewValue())) {
	            remove(source, false);
	        } else {
		    ((BCSChild)children.get(source)).setRemovePending(false);
	        }
	    }
        }
    }

    /**
     * <p>
     * Subclasses of this class may override, or envelope, this method to
     * add validation behavior for the BeanContext to examine child objects
     * immediately prior to their being added to the BeanContext. 
     * </p>
     *
     * @returns true iff the child may be added to this BeanContext, otherwise false.
     */

    protected boolean validatePendingAdd(Object targetChild) {
	return true;
    }

    /**
     * <p>
     * Subclasses of this class may override, or envelope, this method to
     * add validation behavior for the BeanContext to examine child objects
     * immediately prior to their being removed from the BeanContext. 
     * </p>
     *
     * @returns true iff the child may be removed from this BeanContext, otherwise false.
     */

    protected boolean validatePendingRemove(Object targetChild) {
	return true;
    }

    /**
     * subclasses may override this method to simply extend add() semantics
     * after the child has been added and before the event notification has
     * occurred. The method is called with the child synchronized.
     */

    protected void childJustAddedHook(Object child, BCSChild bcsc) {
    }

    /**
     * subclasses may override this method to simply extend remove() semantics
     * after the child has been removed and before the event notification has
     * occurred. The method is called with the child synchronized.
     */

    protected void childJustRemovedHook(Object child, BCSChild bcsc) {
    }

    /**
     * @return the Component (if any) associated with the specified Child
     */

    protected static final Visibility getChildVisibility(Object child) {
	try {
	    return (Visibility)child;
	} catch (ClassCastException cce) {
	    return null;
	}
    }
 
    /**
     * @return the Serializable (if any) associated with the specified Child
     */

    protected static final Serializable getChildSerializable(Object child) {
        try {
	    return (Serializable)child;
	} catch (ClassCastException cce) {
	    return null;
	}
    }

    /**
     * @return the PropertyChangeListener (if any) of the specified child
     */

    protected static final PropertyChangeListener getChildPropertyChangeListener(Object child) {
	try {
	    return (PropertyChangeListener)child;
	} catch (ClassCastException cce) {
	    return null;
	}
    }

    /**
     * @return the VetoableChangeListener (if any) of the specified child
     */

    protected static final VetoableChangeListener getChildVetoableChangeListener(Object child) {
	try {
	    return (VetoableChangeListener)child;
	} catch (ClassCastException cce) {
	    return null;
	}
    }

    /**
     * @return the BeanContextMembershipListener (if any) of the specified child
     */

    protected static final BeanContextMembershipListener getChildBeanContextMembershipListener(Object child) {
	try {
	    return (BeanContextMembershipListener)child;
	} catch (ClassCastException cce) {
	    return null;
	}
    }

    /**
     * @return the BeanContextChild (if any) of the specified child
     * @throw IllegalArgumentException if child implements both BeanContextChild and BeanContextProxy
     */

    protected static final BeanContextChild getChildBeanContextChild(Object child) {
        try {
	    BeanContextChild bcc = (BeanContextChild)child;

	    if (child instanceof BeanContextChild && child instanceof BeanContextProxy) 
		throw new IllegalArgumentException("child cannot implement both BeanContextChild and BeanContextProxy");
	    else
		return bcc;
	} catch (ClassCastException cce) {
	    try {
		return ((BeanContextProxy)child).getBeanContextProxy();
	    } catch (ClassCastException cce1) {
	        return null;
	    }
	}
    }

    /**
     * Fire a BeanContextshipEvent on the BeanContextMembershipListener interface
     */

    protected final void fireChildrenAdded(BeanContextMembershipEvent bcme) {
	Object[] copy;
							  
	synchronized(bcmListeners) { copy = bcmListeners.toArray(); }

	for (int i = 0; i < copy.length; i++)
	    ((BeanContextMembershipListener)copy[i]).childrenAdded(bcme);
    }

    /**
     * Fire a BeanContextshipEvent on the BeanContextMembershipListener interface
     */

    protected final void fireChildrenRemoved(BeanContextMembershipEvent bcme) {
	Object[] copy;
							  
	synchronized(bcmListeners) { copy = bcmListeners.toArray(); }

	for (int i = 0; i < copy.length; i++)
	    ((BeanContextMembershipListener)copy[i]).childrenRemoved(bcme);
    }

    /**
     * protected method called from constructor and readObject to initialize
     * transient state of BeanContextSupport instance.
     *
     * This class uses this method to instantiate inner class listeners used
     * to monitor PropertyChange and VetoableChange events on children.
     *
     * subclasses may envelope this method to add their own initialization
     * behavior
     */

    protected synchronized void initialize() {
	children     = new HashMap(serializable + 1);
	bcmListeners = new ArrayList(1);

	childPCL = new PropertyChangeListener() {

	    /*
	     * this adaptor is used by the BeanContextSupport class to forward
	     * property changes from a child to the BeanContext, avoiding 
	     * accidential serialization of the BeanContext by a badly 
	     * behaved Serializable child.
	     */

	    public void propertyChange(PropertyChangeEvent pce) {
	        BeanContextSupport.this.propertyChange(pce);
	    }
	};

	childVCL = new VetoableChangeListener() {

	    /*
	     * this adaptor is used by the BeanContextSupport class to forward
	     * vetoable changes from a child to the BeanContext, avoiding 
	     * accidential serialization of the BeanContext by a badly 
	     * behaved Serializable child.
	     */

	    public void vetoableChange(PropertyChangeEvent pce) throws PropertyVetoException {
	        BeanContextSupport.this.vetoableChange(pce);
             }
        };
    }

    /**
     * @returns a copy of the current nested children
     */

    protected final Object[] copyChildren() {
	synchronized(children) { return children.keySet().toArray(); }
    }

    /**
     * @return if two class objects, or their names are equal.
     */

    protected static final boolean classEquals(Class first, Class second) {
	return first.equals(second) || first.getName().equals(second.getName());
    }


    /*
     * fields
     */


    /**
     * all accesses to the <code> protected HashMap children </code> field
     * shall be synchronized on that object.
     */

    protected transient HashMap		children;

    private   	        int 		serializable  = 0; // children serializable

    /**
     * all accesses to the <code> protected ArrayList bcmListeners </code> field
     * shall be synchronized on that object.
     */

    protected transient ArrayList	bcmListeners;

    // 

    protected 		Locale		locale;

    protected 		boolean		okToUseGui;

    protected 		boolean		designTime;

    /*
     * transient 
     */

    private transient PropertyChangeListener childPCL;

    private transient VetoableChangeListener childVCL;

    private transient boolean		     serializing;
}
