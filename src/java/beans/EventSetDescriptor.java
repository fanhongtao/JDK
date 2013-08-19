/*
 * @(#)EventSetDescriptor.java	1.55 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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

    private Class listenerType;
    private Method[] listenerMethods;
    private MethodDescriptor[] listenerMethodDescriptors;
    private Method addMethod;
    private Method removeMethod;
    private Method getListenerMethod;
    private boolean unicast;
    private boolean inDefaultEventSet = true;

    /**
     * Creates an <TT>EventSetDescriptor</TT> assuming that you are
     * following the most simple standard design pattern where a named
     * event &quot;fred&quot; is (1) delivered as a call on the single method of
     * interface FredListener, (2) has a single argument of type FredEvent,
     * and (3) where the FredListener may be registered with a call on an
     * addFredListener method of the source component and removed with a
     * call on a removeFredListener method.
     *
     * @param sourceClass  The class firing the event.
     * @param eventSetName  The programmatic name of the event.  E.g. &quot;fred&quot;.
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
	
	char chars[] = eventSetName.toCharArray();
	chars[0] = Character.toUpperCase(eventSetName.charAt(0));
	String eventName = new String(chars);
	eventName += "Event";

	listenerMethods = new Method[1];
	listenerMethods[0] = Introspector.findMethod(listenerType,
						listenerMethodName, 1);

	Class[] args = listenerMethods[0].getParameterTypes();
	// Check for EventSet compliance. Special case for vetoableChange. See 4529996
	if (!"vetoableChange".equals(eventSetName) && !args[0].getName().endsWith(eventName)) {
	    throw new IntrospectionException("Method \"" + listenerMethodName +
					     "\" should have argument \"" + 
					     eventName + "\"");
	}

	String listenerClassName = listenerType.getName();
	String tail = listenerClassName.substring(listenerClassName.lastIndexOf('.') + 1);

	String addMethodName = "add" + tail;
	addMethod = Introspector.findMethod(sourceClass, addMethodName, 1);

	String removeMethodName = "remove" + tail;
	removeMethod = Introspector.findMethod(sourceClass, removeMethodName, 1);

	// Look for get<Foo>Listeners method. This was ammended to the JavaBeans
	// spec for 1.4 and it's a bonus if it exists but it's not enforced.
	String getMethodName = "get" + tail + "s";
	try {
	    getListenerMethod = Introspector.findMethod(sourceClass, getMethodName, 0);
	} catch (IntrospectionException ex) {
	    // Just catch and fall through if it doesn't exist.
	}
    }

    /**
     * Creates an <TT>EventSetDescriptor</TT> from scratch using
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
	this(sourceClass, eventSetName, listenerType,
	     listenerMethodNames, addListenerMethodName, 
	     removeListenerMethodName, "");
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
     * @param getListenerMethodName The method on the event source that
     *          can be used to access the array of event listener objects.
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     * @since 1.4
     */
    public EventSetDescriptor(Class sourceClass,
		String eventSetName, 
		Class listenerType,
		String listenerMethodNames[],
		String addListenerMethodName,
		String removeListenerMethodName,
		String getListenerMethodName)
		throws IntrospectionException {
	setName(eventSetName);
	listenerMethods = new Method[listenerMethodNames.length];
	for (int i = 0; i < listenerMethods.length; i++) {
	    String listenerName = listenerMethodNames[i];
	    if (listenerName == null) {
		throw new NullPointerException();
	    }
	    listenerMethods[i] = Introspector.findMethod(listenerType,
							listenerName, 1);
	}

	this.addMethod = Introspector.findMethod(sourceClass,
					addListenerMethodName, 1);
	this.removeMethod = Introspector.findMethod(sourceClass,
					removeListenerMethodName, 1);
	if (getListenerMethodName != null && !getListenerMethodName.equals("")) {
	    this.getListenerMethod = Introspector.findMethod(sourceClass,
					      getListenerMethodName, 1);
	}

	this.listenerType = listenerType;
    }

    /**
     * Creates an <TT>EventSetDescriptor</TT> from scratch using
     * <TT>java.lang.reflect.Method</TT> and <TT>java.lang.Class</TT> objects.
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
	this(eventSetName, listenerType, listenerMethods,
	     addListenerMethod, removeListenerMethod, null);
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
     * @param getListenerMethod The method on the event source
     *          that can be used to access the array of event listener objects.
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     * @since 1.4
     */
    public EventSetDescriptor(String eventSetName, 
		Class listenerType,
		Method listenerMethods[],
		Method addListenerMethod,
		Method removeListenerMethod,
		Method getListenerMethod) 
		throws IntrospectionException {
	setName(eventSetName);
	this.listenerMethods = listenerMethods;
	this.addMethod = addListenerMethod;
	this.removeMethod = removeListenerMethod;
	this.getListenerMethod = getListenerMethod;
	this.listenerType = listenerType;
    }

    /**
     * Creates an <TT>EventSetDescriptor</TT> from scratch using
     * <TT>java.lang.reflect.MethodDescriptor</TT> and <TT>java.lang.Class</TT>
     *  objects.
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
     * Gets the <TT>Class</TT> object for the target interface.
     *
     * @return The Class object for the target interface that will
     * get invoked when the event is fired.
     */
    public Class getListenerType() {
	return listenerType;
    }

    /** 
     * Gets the methods of the target listener interface.
     *
     * @return An array of <TT>Method</TT> objects for the target methods
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
     * Gets the <code>MethodDescriptor</code>s of the target listener interface.
     *
     * @return An array of <code>MethodDescriptor</code> objects for the target methods
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
     * Gets the method used to add event listeners.
     *
     * @return The method used to register a listener at the event source.
     */
    public Method getAddListenerMethod() {
	return addMethod;
    }

    /** 
     * Gets the method used to remove event listeners.
     *
     * @return The method used to remove a listener at the event source.
     */
    public Method getRemoveListenerMethod() {
	return removeMethod;
    }

    /**
     * Gets the method used to access the registered event listeners.
     * 
     * @return The method used to access the array of listeners at the event
     *         source or null if it doesn't exist.
     * @since 1.4
     */
    public Method getGetListenerMethod() {
	return getListenerMethod;
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
     * @return  <TT>true</TT> if the event set is unicast.  
     *          Defaults to <TT>false</TT>.
     */
    public boolean isUnicast() {
	return unicast;
    }

    /**
     * Marks an event set as being in the &quot;default&quot; set (or not).
     * By default this is <TT>true</TT>.
     *
     * @param inDefaultEventSet <code>true</code> if the event set is in
     *                          the &quot;default&quot; set, 
     *                          <code>false</code> if not 
     */
    public void setInDefaultEventSet(boolean inDefaultEventSet) {
	this.inDefaultEventSet = inDefaultEventSet;
    }
    
    /**
     * Reports if an event set is in the &quot;default&quot; set.
     *
     * @return  <TT>true</TT> if the event set is in 
     *          the &quot;default&quot; set.  Defaults to <TT>true</TT>.
     */
    public boolean isInDefaultEventSet() {
	return inDefaultEventSet;
    }

    /*
     * Package-private constructor
     * Merge two event set descriptors.  Where they conflict, give the
     * second argument (y) priority over the first argument (x).
     *
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

    /*
     * Package-private dup constructor
     * This must isolate the new object from any changes to the old object.
     */
    EventSetDescriptor(EventSetDescriptor old) {
	super(old);
	if (old.listenerMethodDescriptors != null) {
	    int len = old.listenerMethodDescriptors.length;
	    listenerMethodDescriptors = new MethodDescriptor[len];
	    for (int i = 0; i < len; i++) {
		listenerMethodDescriptors[i] = new MethodDescriptor(
					old.listenerMethodDescriptors[i]);
	    }
	}
	if (old.listenerMethods != null) {
	    int len = old.listenerMethods.length;
	    listenerMethods = new Method[len];
	    for (int i = 0; i < len; i++) {
		listenerMethods[i] = old.listenerMethods[i];
	    }
	}
	addMethod = old.addMethod;
	removeMethod = old.removeMethod;
	unicast = old.unicast;
	listenerType = old.listenerType;
	inDefaultEventSet = old.inDefaultEventSet;
    }
}
