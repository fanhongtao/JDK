/*
 * @(#)BeanContextChildSupport.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.beans.beancontext;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;

import java.beans.PropertyVetoException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * <p>
 * This is a general support class to provide support for implementing the
 * BeanContextChild protocol.
 * 
 * This class may either be directly subclassed, or encapsulated and delegated
 * to in order to implement this interface for a given component.
 * </p>
 *
 * @author	Laurence P. G. Cable
 * @version	1.4
 * @since	JDK1.2
 * 
 * @seealso	java.beans.beancontext.BeanContext
 * @seealso	java.beans.beancontext.BeanContextServices
 * @seealso	java.beans.beancontext.BeanContextChild
 */

public class BeanContextChildSupport implements BeanContextChild, BeanContextServicesListener, Serializable {

    static final long serialVersionUID = 6328947014421475877L;

    /**
     * construct a BeanContextChildSupport where this class has been 
     * subclassed in order to implement the JavaBean component itself.
     */

    public BeanContextChildSupport() {
	super();

	beanContextChildPeer = this;

	pcSupport = new PropertyChangeSupport(beanContextChildPeer);
	vcSupport = new VetoableChangeSupport(beanContextChildPeer);
    }

    /**
     * construct a BeanContextChildSupport where the JavaBean component
     * itself implements BeanContextChild, and encapsulates this, delegating
     * that interface to this implementation
     */

    public BeanContextChildSupport(BeanContextChild bcc) {
	super();

	beanContextChildPeer = (bcc != null) ? bcc : this;

	pcSupport = new PropertyChangeSupport(beanContextChildPeer);
	vcSupport = new VetoableChangeSupport(beanContextChildPeer);
    }

    /**
     * setBeanContext
     */

    public synchronized void setBeanContext(BeanContext bc) throws PropertyVetoException {
	if (bc == beanContext) return;

	BeanContext oldValue = beanContext;
	BeanContext newValue = bc;

	if (!rejectedSetBCOnce) {
	    if (rejectedSetBCOnce = !validatePendingSetBeanContext(bc)) {
		throw new PropertyVetoException(
		    "setBeanContext() change rejected:",
		    new PropertyChangeEvent(beanContextChildPeer, "beanContext", oldValue, newValue)
		);
	    }

	    try {
		fireVetoableChange("beanContext",
				   oldValue,
				   newValue
		);
	    } catch (PropertyVetoException pve) {
		rejectedSetBCOnce = true;

		throw pve; // re-throw
	    }
	}

	if (beanContext != null) releaseBeanContextResources();

	beanContext       = newValue;
	rejectedSetBCOnce = false;

	firePropertyChange("beanContext", 
			   oldValue,
			   newValue
	);

	if (beanContext != null) initializeBeanContextResources();
    }

    /**
     * @returns the current BeanContext associated with the JavaBean
     */

    public synchronized BeanContext getBeanContext() { return beanContext; }

    /**
     * add a property change listener
     */

    public void addPropertyChangeListener(String name, PropertyChangeListener pcl) {
	pcSupport.addPropertyChangeListener(name, pcl);
    }

    /**
     * remove a property change listener
     */

    public void removePropertyChangeListener(String name, PropertyChangeListener pcl) {
	pcSupport.removePropertyChangeListener(name, pcl);
    }

    /**
     * add a vetoable change listener
     */

    public void addVetoableChangeListener(String name, VetoableChangeListener vcl) {
	vcSupport.addVetoableChangeListener(name, vcl);
    }

    /**
     * remove a vetoable change listener
     */

    public void removeVetoableChangeListener(String name, VetoableChangeListener vcl) {
	vcSupport.removeVetoableChangeListener(name, vcl);
    }

    /**
     * a service provided by the nesting BeanContext has been revoked.
     * 
     * subclasses may override this method in order to implement their own
     * behaviors 
     */

    public void serviceRevoked(BeanContextServiceRevokedEvent bcsre) { }

    /**
     * a new service is available from the nesting BeanContext.
     * 
     * subclasses may override this method in order to implement their own
     * behaviors 
     */

    public void serviceAvailable(BeanContextServiceAvailableEvent bcsae) { }

    /**
     * @return the BeanContextChild peer of this class
     */

    public BeanContextChild getBeanContextChildPeer() { return beanContextChildPeer; }

    /**
     * @return true if this class is a delegate of another
     */

    public boolean isDelegated() { return !this.equals(beanContextChildPeer); }

    /**
     * fires a propertyChange Event
     */

    public void firePropertyChange(String name, Object oldValue, Object newValue) {
	pcSupport.firePropertyChange(name, oldValue, newValue);
    }

    /**
     * fires a vetoableChange Event
     */

    public void fireVetoableChange(String name, Object oldValue, Object newValue) throws PropertyVetoException {
	vcSupport.fireVetoableChange(name, oldValue, newValue);
    }

    /**
     * called from setBeanContext to validate (or otherwise) the
     * pending change in the nesting BeanContext property value.
     *
     * returning false will cause setBeanContext to throw
     * PropertyVetoException.
     */

    public boolean validatePendingSetBeanContext(BeanContext newValue) {
	return true;
    }

    /**
     * This method may be overridden by subclasses to provide their own
     * release behaviors. When invoked any resources held by this instance
     * obtained from its current BeanContext property should be released
     * since the object is no longer nested within that BeanContext.
     */

    protected  void releaseBeanContextResources() {
	// do nothing
    }

    /**
     * This method may be overridden by subclasses to provide their own
     * initialization behaviors. When invoked any resources requried by the
     * BeanContextChild should be obtained from the current BeanContext.
     */

    protected void initializeBeanContextResources() {
	// do nothing
    }

    /**
     * Write the persistence state of the object.
     */

    private void writeObject(ObjectOutputStream oos) throws IOException {

	/*
	 * dont serialize if we are delegated and the delegator isnt also
	 * serializable.
	 */

	if (!equals(beanContextChildPeer) && !(beanContextChildPeer instanceof Serializable))
	    throw new IOException("BeanContextChildSupport beanContextChildPeer not Serializable");

	else 
            oos.defaultWriteObject();
	    
    }


    /**
     * Restore a persistent object, must wait for subsequent setBeanContext()
     * to fully restore any resources obtained from the new nesting 
     * BeanContext
     */

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
	ois.defaultReadObject();
    }

    /*
     * fields
     */

    public    BeanContextChild 	    beanContextChildPeer;

    protected PropertyChangeSupport pcSupport;
    protected VetoableChangeSupport vcSupport;

    protected transient BeanContext	      beanContext;
    protected transient boolean     	      rejectedSetBCOnce;

}
