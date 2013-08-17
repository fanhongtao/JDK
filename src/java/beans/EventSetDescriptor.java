/*
 * @(#)EventSetDescriptor.java	1.40 98/07/01
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.beans;

import java.lang.reflect.*;

/**
 * An EventSetDescriptor describes a group of events that a given Java
 * bean fires.
 * <P>
 * The given group of events are all delivered as method calls on a single
 * event listener interface, and an event listener object can be registered
 * via a call on a registration method supplied by the event source.
 */


public class EventSetDescriptor extends FeatureDescriptor {

    /**
     * This constructor creates an EventSetDescriptor assuming that you are
     * following the most simple standard design pattern where a named
     * event "fred" is (1) delivered as a call on the single method of
     * interface FredListener, (2) has a single argument of type FredEvent,
     * and (3) where the FredListener may be registered with a call on an
     * addFredListener method of the source component and removed with a
     * call on a removeFredListener method.
     *
     * @param sourceClass  The class firing the event.
     * @param eventSetName  The programmatic name of the event.  E.g. "fred".
     *		Note that this should normally start with a lower-case character.
     * @param listenerType  The target interface that events
     *		will get delivered to.
     * @param listenerMethodName  The method that will get called when the event gets
     *		delivered to its target listener interface.
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     */
    public EventSetDescriptor(Class sourceClass, String eventSetName,
		Class listenerType, String listenerMethodName) 
		throws IntrospectionException {

   	setName(eventSetName);

	// Get a Class object for the listener class.
    	this.listenerType = listenerType;
	
	listenerMethods = new Method[1];
	listenerMethods[0] = Introspector.findMethod(listenerType,
						listenerMethodName, 1);

	String listenerClassName = listenerType.getName();
	String tail = listenerClassName.substring(listenerClassName.lastIndexOf('.') + 1);

	String addMethodName = "add" + tail;
	addMethod = Introspector.findMethod(sourceClass, addMethodName, 1);

	String removeMethodName = "remove" + tail;
	removeMethod = Introspector.findMethod(sourceClass, removeMethodName, 1);
					

    }

    /**
     * This constructor creates an EventSetDescriptor from scratch using
     * string names.
     *
     * @param sourceClass  The class firing the event.
     * @param eventSetName The programmatic name of the event set.
     *		Note that this should normally start with a lower-case character.
     * @param listenerType  The Class of the target interface that events
     *		will get delivered to.
     * @param listenerMethodNames The names of the methods that will get called 
     *		when the event gets delivered to its target listener interface.
     * @param addListenerMethodName  The name of the method on the event source
     *		that can be used to register an event listener object.
     * @param removeListenerMethodName  The name of the method on the event source
     *		that can be used to de-register an event listener object.
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     */
    public EventSetDescriptor(Class sourceClass,
		String eventSetName, 
		Class listenerType,
		String listenerMethodNames[],
		String addListenerMethodName,
		String removeListenerMethodName)
		throws IntrospectionException {
	setName(eventSetName);
	listenerMethods = new Method[listenerMethodNames.length];
	for (int i = 0; i < listenerMethods.length; i++) {
	    listenerMethods[i] = Introspector.findMethod(listenerType,
					listenerMethodNames[i], 1);
	}

	this.addMethod = Introspector.findMethod(sourceClass,
					addListenerMethodName, 1);
	this.removeMethod = Introspector.findMethod(sourceClass,
					removeListenerMethodName, 1);

	this.listenerType = listenerType;
    }

    /**
     * This constructor creates an EventSetDescriptor from scratch using
     * java.lang.reflect.Method and java.lang.Class objects.
     *
     * @param eventSetName The programmatic name of the event set.
     * @param listenerType The Class for the listener interface.
     * @param listenerMethods  An array of Method objects describing each
     *		of the event handling methods in the target listener.
     * @param addListenerMethod  The method on the event source
     *		that can be used to register an event listener object.
     * @param removeListenerMethod  The method on the event source
     *		that can be used to de-register an event listener object.
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     */
    public EventSetDescriptor(String eventSetName, 
		Class listenerType,
		Method listenerMethods[],
		Method addListenerMethod,
		Method removeListenerMethod) 
		throws IntrospectionException {
	setName(eventSetName);
	this.listenerMethods = listenerMethods;
	this.addMethod = addListenerMethod;
	this.removeMethod = removeListenerMethod;
	this.listenerType = listenerType;
    }

    /**
     * This constructor creates an EventSetDescriptor from scratch using
     * java.lang.reflect.MethodDescriptor and java.lang.Class objects.
     *
     * @param eventSetName The programmatic name of the event set.
     * @param listenerType The Class for the listener interface.
     * @param listenerMethodDescriptors  An array of MethodDescriptor objects
     *		 describing each of the event handling methods in the
     *           target listener.
     * @param addListenerMethod  The method on the event source
     *		that can be used to register an event listener object.
     * @param removeListenerMethod  The method on the event source
     *		that can be used to de-register an event listener object.
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     */
    public EventSetDescriptor(String eventSetName, 
		Class listenerType,
		MethodDescriptor listenerMethodDescriptors[],
		Method addListenerMethod,
		Method removeListenerMethod) 
		throws IntrospectionException {
	setName(eventSetName);
	this.listenerMethodDescriptors = listenerMethodDescriptors;
	this.addMethod = addListenerMethod;
	this.removeMethod = removeListenerMethod;
	this.listenerType = listenerType;
    }

    /** 
     * @return The Class object for the target interface that will
     * get invoked when the event is fired.
     */
    public Class getListenerType() {
	return listenerType;
    }

    /** 
     * @return An array of Method objects for the target methods
     * within the target listener interface that will get called when
     * events are fired.
     */
    public Method[] getListenerMethods() {
	if (listenerMethods == null && listenerMethodDescriptors != null) {
	    // Create Method array from MethodDescriptor array.
	    listenerMethods = new Method[listenerMethodDescriptors.length];
	    for (int i = 0; i < listenerMethods.length; i++) {
		listenerMethods[i] = listenerMethodDescriptors[i].getMethod();
	    }
	}
	return listenerMethods;
    }


    /** 
     * @return An array of MethodDescriptor objects for the target methods
     * within the target listener interface that will get called when
     * events are fired.
     */
    public MethodDescriptor[] getListenerMethodDescriptors() {
	if (listenerMethodDescriptors == null && listenerMethods != null) {
	    // Create MethodDescriptor array from Method array.
	    listenerMethodDescriptors = 
				new MethodDescriptor[listenerMethods.length];
	    for (int i = 0; i < listenerMethods.length; i++) {
		listenerMethodDescriptors[i] = 
				new MethodDescriptor(listenerMethods[i]);
	    }
	}
	return listenerMethodDescriptors;
    }

    /** 
     * @return The method used to register a listener at the event source.
     */
    public Method getAddListenerMethod() {
	return addMethod;
    }

    /** 
     * @return The method used to register a listener at the event source.
     */
    public Method getRemoveListenerMethod() {
	return removeMethod;
    }

    /**
     * Mark an event set as unicast (or not).
     *
     * @param unicast  True if the event set is unicast.
     */

    public void setUnicast(boolean unicast) {
	this.unicast = unicast;
    }
    
    /**
     * Normally event sources are multicast.  However there are some 
     * exceptions that are strictly unicast.
     *
     * @return  True if the event set is unicast.  Defaults to "false".
     */

    public boolean isUnicast() {
	return unicast;
    }

    /**
     * Mark an event set as being in the "default" set (or not).
     * By default this is true.
     *
     * @param unicast  True if the event set is unicast.
     */

    public void setInDefaultEventSet(boolean inDefaultEventSet) {
	this.inDefaultEventSet = inDefaultEventSet;
    }
    
    /**
     * Report if an event set is in the "default set".
     *
     * @return  True if the event set is in the "default set".  Defaults to "true".
     */

    public boolean isInDefaultEventSet() {
	return inDefaultEventSet;
    }

    /*
     * Package-private constructor
     * Merge two event set descriptors.  Where they conflict, give the
     * second argument (y) priority over the first argument (x).
     * @param x  The first (lower priority) EventSetDescriptor
     * @param y  The second (higher priority) EventSetDescriptor
     */

    EventSetDescriptor(EventSetDescriptor x, EventSetDescriptor y) {
	super(x,y);
	listenerMethodDescriptors = x.listenerMethodDescriptors;
	if (y.listenerMethodDescriptors != null) {
	    listenerMethodDescriptors = y.listenerMethodDescriptors;
	}
	if (listenerMethodDescriptors == null) {
	    listenerMethods = y.listenerMethods;
	}
	addMethod = y.addMethod;
	removeMethod = y.removeMethod;
	unicast = y.unicast;
	listenerType = y.listenerType;
	if (!x.inDefaultEventSet || !y.inDefaultEventSet) {
	    inDefaultEventSet = false;
	}
    }

    private Class listenerType;
    private Method[] listenerMethods;
    private MethodDescriptor[] listenerMethodDescriptors;
    private Method addMethod;
    private Method removeMethod;
    private boolean unicast;
    private boolean inDefaultEventSet = true;
}
