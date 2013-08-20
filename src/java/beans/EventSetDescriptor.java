/*
 * @(#)EventSetDescriptor.java	1.64 04/05/05
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.beans;

import java.lang.ref.Reference;

import java.lang.reflect.Method;

/**
 * An EventSetDescriptor describes a group of events that a given Java
 * bean fires.
 * <P>
 * The given group of events are all delivered as method calls on a single
 * event listener interface, and an event listener object can be registered
 * via a call on a registration method supplied by the event source.
 */
public class EventSetDescriptor extends FeatureDescriptor {

    private MethodDescriptor[] listenerMethodDescriptors;

    private Reference listenerMethodsRef;
    private Reference listenerTypeRef;
    private Reference addMethodRef;
    private Reference removeMethodRef;
    private Reference getMethodRef;

    private String[] listenerMethodNames;
    private String addMethodName;
    private String removeMethodName;
    private String getMethodName;

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
    public EventSetDescriptor(Class<?> sourceClass, String eventSetName,
		Class<?> listenerType, String listenerMethodName) 
		throws IntrospectionException {
	this(sourceClass, eventSetName, listenerType, 
	     new String[] { listenerMethodName },
	     "add" + getListenerClassName(listenerType),
	     "remove" + getListenerClassName(listenerType),
	     "get" + getListenerClassName(listenerType) + "s");

	String eventName = capitalize(eventSetName) + "Event";
	Method[] listenerMethods = getListenerMethods();
	if (listenerMethods.length > 0) {
	    Class[] args = listenerMethods[0].getParameterTypes();
	    // Check for EventSet compliance. Special case for vetoableChange. See 4529996
	    if (!"vetoableChange".equals(eventSetName) && !args[0].getName().endsWith(eventName)) {
		throw new IntrospectionException("Method \"" + listenerMethodNames[0] +
						 "\" should have argument \"" + 
						 eventName + "\"");
	    }
	}
    }

    private static String getListenerClassName(Class cls) {
	String className = cls.getName(); 
	return className.substring(className.lastIndexOf('.') + 1); 
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
    public EventSetDescriptor(Class<?> sourceClass,
		String eventSetName, 
		Class<?> listenerType,
		String listenerMethodNames[],
		String addListenerMethodName,
		String removeListenerMethodName)
		throws IntrospectionException {
	this(sourceClass, eventSetName, listenerType,
	     listenerMethodNames, addListenerMethodName, 
	     removeListenerMethodName, null);
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
    public EventSetDescriptor(Class<?> sourceClass,
		String eventSetName, 
		Class<?> listenerType,
		String listenerMethodNames[],
		String addListenerMethodName,
		String removeListenerMethodName,
		String getListenerMethodName)
		throws IntrospectionException {
	if (sourceClass == null || eventSetName == null || listenerType == null) {
	    throw new NullPointerException();
	}
	setName(eventSetName);
	setClass0(sourceClass);
	setListenerType(listenerType);
	
	Method[] listenerMethods = new Method[listenerMethodNames.length];
	for (int i = 0; i < listenerMethodNames.length; i++) {
	    // Check for null names
	    if (listenerMethodNames[i] == null) {
		throw new NullPointerException();
	    }
	    listenerMethods[i] = getMethod(listenerType, listenerMethodNames[i], 1);
	}
	setListenerMethods(listenerMethods);

	setAddListenerMethod(getMethod(sourceClass, addListenerMethodName, 1));
	setRemoveListenerMethod(getMethod(sourceClass, removeListenerMethodName, 1));

	// Be more forgiving of not finding the getListener method.
	Method method = Introspector.findMethod(sourceClass, 
						getListenerMethodName, 0);
	if (method != null) {
	    setGetListenerMethod(method);
	}
    }

    private static Method getMethod(Class cls, String name, int args) 
	throws IntrospectionException {
	if (name == null) {
	    return null;
	}
	Method method = Introspector.findMethod(cls, name, args);
	if (method == null) {
	    throw new IntrospectionException("Method not found: " + name + 
					     " on class " + cls.getName());
	}
	return method;
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
		Class<?> listenerType,
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
		Class<?> listenerType,
		Method listenerMethods[],
		Method addListenerMethod,
		Method removeListenerMethod,
		Method getListenerMethod) 
		throws IntrospectionException {
	setName(eventSetName);
	setListenerMethods(listenerMethods);
	setAddListenerMethod(addListenerMethod);
	setRemoveListenerMethod( removeListenerMethod);
	setGetListenerMethod(getListenerMethod);
	setListenerType(listenerType);
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
		Class<?> listenerType,
		MethodDescriptor listenerMethodDescriptors[],
		Method addListenerMethod,
		Method removeListenerMethod) 
		throws IntrospectionException {
	setName(eventSetName);
	this.listenerMethodDescriptors = listenerMethodDescriptors;
	setAddListenerMethod(addListenerMethod);
	setRemoveListenerMethod(removeListenerMethod);
	setListenerType(listenerType);
    }

    /** 
     * Gets the <TT>Class</TT> object for the target interface.
     *
     * @return The Class object for the target interface that will
     * get invoked when the event is fired.
     */
    public Class<?> getListenerType() {
	return (Class)getObject(listenerTypeRef);
    }

    private void setListenerType(Class cls) {
	listenerTypeRef = createReference(cls);
    }

    /** 
     * Gets the methods of the target listener interface.
     *
     * @return An array of <TT>Method</TT> objects for the target methods
     * within the target listener interface that will get called when
     * events are fired.
     */
    public synchronized Method[] getListenerMethods() {
	Method[] methods = getListenerMethods0();
	if (methods == null) {
	    if (listenerMethodDescriptors != null) {
		methods = new Method[listenerMethodDescriptors.length];
		for (int i = 0; i < methods.length; i++) {
		    methods[i] = listenerMethodDescriptors[i].getMethod();
		}
	    } else if (listenerMethodNames != null) {
		methods = new Method[listenerMethodNames.length];
		
		for (int i = 0; i < methods.length; i++) {
		    methods[i] = Introspector.findMethod(getListenerType(),
							 listenerMethodNames[i], 1);
		}
	    }
	    setListenerMethods(methods);
	}
	return methods;
    }

    private void setListenerMethods(Method[] methods) {
	if (methods == null) {
	    return;
	}
	if (listenerMethodNames == null) {
	    listenerMethodNames = new String[methods.length];
	    for (int i = 0; i < methods.length; i++) {
		listenerMethodNames[i] = methods[i].getName();
	    }
	}
	listenerMethodsRef = createReference(methods, true);
    }

    private Method[] getListenerMethods0() {
	return (Method[])getObject(listenerMethodsRef);
    }

    /** 
     * Gets the <code>MethodDescriptor</code>s of the target listener interface.
     *
     * @return An array of <code>MethodDescriptor</code> objects for the target methods
     * within the target listener interface that will get called when
     * events are fired.
     */
    public synchronized MethodDescriptor[] getListenerMethodDescriptors() {
	Method[] listenerMethods = getListenerMethods();
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
    public synchronized Method getAddListenerMethod() {
	Method method = getAddListenerMethod0();
	if (method == null) {
	    Class cls = getClass0();
	    if (cls == null) {
		return null;
	    }
	    method = Introspector.findMethod(cls, addMethodName, 1);
	    setAddListenerMethod(method);
	}
	return method;
    }

    private Method getAddListenerMethod0() {
	return (Method)getObject(addMethodRef);
    }

    private synchronized void setAddListenerMethod(Method method) {
	if (method == null) {
	    return;
	}
	if (getClass0() == null) {
	    setClass0(method.getDeclaringClass());
	}
	addMethodName = method.getName();
	addMethodRef = createReference(method, true);
    }

    /** 
     * Gets the method used to remove event listeners.
     *
     * @return The method used to remove a listener at the event source.
     */
    public synchronized Method getRemoveListenerMethod() {
	Method method = getRemoveListenerMethod0();
	if (method == null) {
	    Class cls = getClass0();
	    if (cls == null) {
		return null;
	    }
	    method = Introspector.findMethod(cls, removeMethodName, 1);
	    setRemoveListenerMethod(method);
	}
	return method;
    }

    private Method getRemoveListenerMethod0() {
	return (Method)getObject(removeMethodRef);
    }

    private synchronized void setRemoveListenerMethod(Method method) {
	if (method == null) {
	    return;
	}
	if (getClass0() == null) {
	    setClass0(method.getDeclaringClass());
	}
	removeMethodName = method.getName();
	removeMethodRef = createReference(method, true);
    }

    /**
     * Gets the method used to access the registered event listeners.
     * 
     * @return The method used to access the array of listeners at the event
     *         source or null if it doesn't exist.
     * @since 1.4
     */
    public synchronized Method getGetListenerMethod() {
	Method method = getGetListenerMethod0();
	if (method == null) {
	    Class cls = getClass0();
	    if (cls == null) {
		return null;
	    }
	    method = Introspector.findMethod(cls, getMethodName, 0);
	    setGetListenerMethod(method);
	}
	return method;
    }

    private Method getGetListenerMethod0() {
	return (Method)getObject(getMethodRef);
    }

    private synchronized void setGetListenerMethod(Method method) {
	if (method == null) {
	    return;
	}
	if (getClass0() == null) {
	    setClass0(method.getDeclaringClass());
	}
	getMethodName = method.getName();
	getMethodRef = createReference(method, true);
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
	listenerMethodNames = x.listenerMethodNames;
	if (y.listenerMethodNames != null) {
	    listenerMethodNames = y.listenerMethodNames;
	}
	listenerTypeRef = x.listenerTypeRef;
	if (y.listenerTypeRef != null) {
	    listenerTypeRef = y.listenerTypeRef;
	}

	addMethodRef = x.addMethodRef;
	if (y.addMethodRef != null) {
	    addMethodRef = y.addMethodRef;
	}
	addMethodName = x.addMethodName;
	if (y.addMethodName != null) {
	    addMethodName = y.addMethodName;
	}

	removeMethodRef = x.removeMethodRef;
	if (y.removeMethodRef != null) {
	    removeMethodRef = y.removeMethodRef;
	}
	removeMethodName = x.removeMethodName;
	if (y.removeMethodName != null) {
	    removeMethodName = y.removeMethodName;
	}

	getMethodRef = x.getMethodRef;
	if (y.getMethodRef != null) {
	    getMethodRef = y.getMethodRef;
	}
	getMethodName = x.getMethodName;
	if (y.getMethodName != null) {
	    getMethodName = y.getMethodName;
	}

	unicast = y.unicast;
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
	if (old.listenerMethodNames != null) {
	    int len = old.listenerMethodNames.length;
	    listenerMethodNames = new String[len];
	    for (int i = 0; i < len; i++) {
		listenerMethodNames[i] = old.listenerMethodNames[i];
	    }
	}
	listenerTypeRef = old.listenerTypeRef;

	addMethodRef = old.addMethodRef;
    addMethodName = old.addMethodName;

	removeMethodRef = old.removeMethodRef;
    removeMethodName = old.removeMethodName;

	getMethodRef = old.getMethodRef;
    getMethodName = old.getMethodName;

	unicast = old.unicast;
	inDefaultEventSet = old.inDefaultEventSet;
    }
}
