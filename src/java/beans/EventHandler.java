/*
 * @(#)EventHandler.java	1.16 05/08/09
 *
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.beans; 

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;

import java.util.EventObject;
import sun.reflect.misc.MethodUtil;

/**
 * The <code>EventHandler</code> class provides 
 * support for dynamically generating event listeners whose methods
 * execute a simple statement involving an incoming event object
 * and a target object. 
 * <p>
 * The <code>EventHandler</code> class is intended to be used by interactive tools, such as
 * application builders, that allow developers to make connections between
 * beans. Typically connections are made from a user interface bean 
 * (the event <em>source</em>)
 * to an application logic bean (the <em>target</em>). The most effective 
 * connections of this kind isolate the application logic from the user 
 * interface.  For example, the <code>EventHandler</code> for a 
 * connection from a <code>JCheckBox</code> to a method 
 * that accepts a boolean value can deal with extracting the state 
 * of the check box and passing it directly to the method so that 
 * the method is isolated from the user interface layer. 
 * <p>
 * Inner classes are another, more general way to handle events from 
 * user interfaces.  The <code>EventHandler</code> class 
 * handles only a subset of what is possible using inner 
 * classes. However, <code>EventHandler</code> works better
 * with the long-term persistence scheme than inner classes.
 * Also, using <code>EventHandler</code> in large applications in 
 * which the same interface is implemented many times can
 * reduce the disk and memory footprint of the application. 
 * <p>
 * The reason that listeners created with <code>EventHandler</code>
 * have such a small 
 * footprint is that the <code>Proxy</code> class, on which 
 * the <code>EventHandler</code> relies, shares implementations 
 * of identical
 * interfaces. For example, if you use 
 * the <code>EventHandler</code> <code>create</code> methods to make
 * all the <code>ActionListener</code>s in an application,
 * all the action listeners will be instances of a single class
 * (one created by the <code>Proxy</code> class).
 * In general, listeners based on 
 * the <code>Proxy</code> class require one listener class 
 * to be created per <em>listener type</em> (interface),
 * whereas the inner class 
 * approach requires one class to be created per <em>listener</em>
 * (object that implements the interface). 
 *
 * <p>
 * You don't generally deal directly with <code>EventHandler</code>
 * instances.
 * Instead, you use one of the <code>EventHandler</code>
 * <code>create</code> methods to create
 * an object that implements a given listener interface.
 * This listener object uses an <code>EventHandler</code> object
 * behind the scenes to encapsulate information about the
 * event, the object to be sent a message when the event occurs,
 * the message (method) to be sent, and any argument
 * to the method.
 * The following section gives examples of how to create listener
 * objects using the <code>create</code> methods.
 *
 * <h2>Examples of Using EventHandler</h2>
 *
 * The simplest use of <code>EventHandler</code> is to install  
 * a listener that calls a method on the target object with no arguments.  
 * In the following example we create an <code>ActionListener</code> 
 * that invokes the <code>toFront</code> method on an instance 
 * of <code>javax.swing.JFrame</code>.
 *
 * <blockquote>
 *<pre>
 *myButton.addActionListener(
 *    (ActionListener)EventHandler.create(ActionListener.class, frame, "toFront"));
 *</pre>
 * </blockquote>
 *
 * When <code>myButton</code> is pressed, the statement 
 * <code>frame.toFront()</code> will be executed.  One could get 
 * the same effect, with some additional compile-time type safety, 
 * by defining a new implementation of the <code>ActionListener</code> 
 * interface and adding an instance of it to the button: 
 *
 * <blockquote>
 *<pre>
//Equivalent code using an inner class instead of EventHandler.
 *myButton.addActionListener(new ActionListener() {
 *    public void actionPerformed(ActionEvent e) {
 *        frame.toFront();
 *    }
 *});
 *</pre> 
 * </blockquote>
 *
 * The next simplest use of <code>EventHandler</code> is 
 * to extract a property value from the first argument  
 * of the method in the listener interface (typically an event object) 
 * and use it to set the value of a property in the target object.  
 * In the following example we create an <code>ActionListener</code> that
 * sets the <code>nextFocusableComponent</code> property of the target 
 * object to the value of the "source" property of the event.
 *
 * <blockquote>
 *<pre>
 *EventHandler.create(ActionListener.class, target, "nextFocusableComponent", "source")
 *</pre>
 * </blockquote>
 *
 * This would correspond to the following inner class implementation: 
 *
 * <blockquote>
 *<pre>
//Equivalent code using an inner class instead of EventHandler.
 *new ActionListener() {
 *    public void actionPerformed(ActionEvent e) {
 *        button.setNextFocusableComponent((Component)e.getSource()); 
 *    }
 *}
 *</pre>
 * </blockquote>
 *
 * Probably the most common use of <code>EventHandler</code> 
 * is to extract a property value from the 
 * <em>source</em> of the event object and set this value as 
 * the value of a property of the target object. 
 * In the following example we create an <code>ActionListener</code> that
 * sets the "label" property of the target 
 * object to the value of the "text" property of the 
 * source (the value of the "source" property) of the event.
 *
 * <blockquote>
 *<pre>
 *EventHandler.create(ActionListener.class, button, "label", "source.text")
 *</pre>
 * </blockquote>
 *
 * This would correspond to the following inner class implementation: 
 *
 * <blockquote>
 *<pre>
//Equivalent code using an inner class instead of EventHandler.
 *new ActionListener {
 *    public void actionPerformed(ActionEvent e) {
 *        button.setLabel(((JTextField)e.getSource()).getText()); 
 *    }
 *}
 *</pre>
 * </blockquote>
 *
 * The event property may be be "qualified" with an arbitrary number 
 * of property prefixes delimited with the "." character. The "qualifying" 
 * names that appear before the "." characters are taken as the names of 
 * properties that should be applied, left-most first, to 
 * the event object.  
 * <p>  
 * For example, the following action listener
 *
 * <blockquote>
 *<pre>
 *EventHandler.create(ActionListener.class, target, "a", "b.c.d")
 *</pre>
 * </blockquote>
 *
 * might be written as the following inner class  
 * (assuming all the properties had canonical getter methods and 
 * returned the appropriate types): 
 *
 * <blockquote>
 *<pre>
//Equivalent code using an inner class instead of EventHandler.
 *new ActionListener {
 *    public void actionPerformed(ActionEvent e) {
 *        target.setA(e.getB().getC().isD()); 
 *    }
 *}
 *</pre>
 * </blockquote>
 * 
 * @see java.lang.reflect.Proxy
 * @see java.util.EventObject
 * 
 * @since 1.4 
 * 
 * @author Mark Davidson
 * @author Philip Milne
 * @author Hans Muller
 *
 * @version 1.2 10/24/00
 */
public class EventHandler implements InvocationHandler {
    private static Object[] empty = new Object[]{};
    
    private Object target;
    private Method targetMethod;
    private String action;
    private String eventPropertyName;
    private String listenerMethodName; 
    private AccessControlContext acc;
    
    /**
     * Creates a new <code>EventHandler</code> object;
     * you generally use one of the <code>create</code> methods
     * instead of invoking this constructor directly.
     * 
     * @param target the object that will perform the action
     * @param action the (possibly qualified) name of a writable property or method on the target
     * @param eventPropertyName the (possibly qualified) name of a readable property of the incoming event
     * @param listenerMethodName the name of the method in the listener interface that should trigger the action
     * 
     * @see EventHandler
     * @see #create(Class, Object, String, String, String)
     * @see #getTarget
     * @see #getAction
     * @see #getEventPropertyName
     * @see #getListenerMethodName
     */
    public EventHandler(Object target, String action, String eventPropertyName, String listenerMethodName) {
	this.acc = AccessController.getContext();
        this.target = target;
        this.action = action;
        this.eventPropertyName = eventPropertyName;
        this.listenerMethodName = listenerMethodName;
    } 
    
    /**
     * Returns the object to which this event handler will send a message.
     * 
     * @return the target of this event handler
     * @see #EventHandler(Object, String, String, String)
     */
    public Object getTarget()  {
        return target;
    }
    
    /**
     * Returns the name of the target's writable property
     * that this event handler will set,
     * or the name of the method that this event handler
     * will invoke on the target.
     * 
     * @return the action of this event handler
     * @see #EventHandler(Object, String, String, String)
     */
    public String getAction()  {
        return action;
    }
    
    /**
     * Returns the property of the event that should be 
     * used in the action applied to the target. 
     * 
     * @return the property of the event
     * 
     * @see #EventHandler(Object, String, String, String)
     */
    public String getEventPropertyName()  {
        return eventPropertyName;
    }
    
    /**
     * Returns the name of the method that will trigger the action.  
     * A return value of <code>null</code> signifies that all methods in the
     * listener interface trigger the action.  
     * 
     * @return the name of the method that will trigger the action
     *
     * @see #EventHandler(Object, String, String, String)
     */
    public String getListenerMethodName()  {
        return listenerMethodName;
    } 
    
    private Object applyGetters(Object target, String getters) { 
        if (getters == null || getters.equals("")) { 
            return target; 
        }
        int firstDot = getters.indexOf('.'); 
        if (firstDot == -1) { 
            firstDot = getters.length(); 
        }
        String first = getters.substring(0, firstDot); 
        String rest = getters.substring(Math.min(firstDot + 1, getters.length())); 
        
        try { 
            Method getter = ReflectionUtils.getMethod(target.getClass(), 
				      "get" + NameGenerator.capitalize(first),
				      new Class[]{});
            if (getter == null) { 
                getter = ReflectionUtils.getMethod(target.getClass(), 
				   "is" + NameGenerator.capitalize(first), 
				   new Class[]{});
            } 
            if (getter == null) { 
                getter = ReflectionUtils.getMethod(target.getClass(), first, new Class[]{});
            } 
            if (getter == null) { 
		throw new RuntimeException("No method called: " + first + 
					   " defined on " + target); 
            } 
            Object newTarget = MethodUtil.invoke(getter, target, new Object[]{}); 
            return applyGetters(newTarget, rest); 
        } 
        catch (Throwable e) { 
            throw new RuntimeException("Failed to call method: " + first + 
				       " on " + target, e); 
        }
    }
    
    /**
     * Extract the appropriate property value from the event and 
     * pass it to the action associated with 
     * this <code>EventHandler</code>. 
     *
     * @param proxy the proxy object
     * @param method the method in the listener interface
     * @return the result of applying the action to the target
     * 
     * @see EventHandler
     */
    public Object invoke(final Object proxy, final Method method, final Object[] arguments) {
	return AccessController.doPrivileged(new PrivilegedAction() {
	    public Object run() {
	        return invokeInternal(proxy, method, arguments);
	    }
	}, acc);
    }

    private Object invokeInternal(Object proxy, Method method, Object[] arguments) {
        String methodName = method.getName();
        if (method.getDeclaringClass() == Object.class)  {
            // Handle the Object public methods.
            if (methodName.equals("hashCode"))  {
                return new Integer(System.identityHashCode(proxy));   
            } else if (methodName.equals("equals")) {
                return (proxy == arguments[0] ? Boolean.TRUE : Boolean.FALSE);
            } else if (methodName.equals("toString")) {
                return proxy.getClass().getName() + '@' + Integer.toHexString(proxy.hashCode());
            }
        }

        if (listenerMethodName == null || listenerMethodName.equals(methodName)) {
            Class[] argTypes = null; 
            Object[] newArgs = null; 

            if (eventPropertyName == null) {     // Nullary method. 
                newArgs = new Object[]{}; 
                argTypes = new Class[]{}; 
            }
            else { 
                Object input = applyGetters(arguments[0], getEventPropertyName()); 
                newArgs = new Object[]{input}; 
                argTypes = new Class[]{input.getClass()}; 
            }
            try {
                if (targetMethod == null) { 
                    targetMethod = ReflectionUtils.getMethod(target.getClass(), 
							     action, argTypes);
                }
                if (targetMethod == null) { 
                    targetMethod = ReflectionUtils.getMethod(target.getClass(), 
			     "set" + NameGenerator.capitalize(action), argTypes);
                }
                if (targetMethod == null) { 
		    throw new RuntimeException("No method called: " + 
					       action + " on class " + 
					       target.getClass() + " with argument "
					       + argTypes[0]); 
                }
                return MethodUtil.invoke(targetMethod, target, newArgs);
            } 
            catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
            catch (InvocationTargetException ex) {
                throw new RuntimeException(ex.getTargetException());
	    }
        }
        return null;
    }

    /**
     * Creates an implementation of <code>listenerInterface</code> in which
     * <em>all</em> of the methods in the listener interface apply 
     * the handler's <code>action</code> to the <code>target</code>. This 
     * method is implemented by calling the other, more general, 
     * implementation of the <code>create</code> method with both 
     * the <code>eventPropertyName</code> and the <code>listenerMethodName</code> 
     * taking the value <code>null</code>. 
     * <p>
     * To create an <code>ActionListener</code> that shows a 
     * <code>JDialog</code> with <code>dialog.show()</code>, 
     * one can write:
     *
     *<blockquote>
     *<pre>
     *EventHandler.create(ActionListener.class, dialog, "show")
     *</pre>
     *</blockquote>
     *
     * <p>
     * @param listenerInterface the listener interface to create a proxy for
     * @param target the object that will perform the action
     * @param action the name of a writable property or method on the target
     * 
     * @return an object that implements <code>listenerInterface</code>
     * 
     * @see #create(Class, Object, String, String)
     */
    public static <T> T create(Class<T> listenerInterface,
			       Object target, String action)
    {
        return create(listenerInterface, target, action, null, null); 
    }

    /**
     * Creates an implementation of <code>listenerInterface</code> in which
     * <em>all</em> of the methods pass the value of the event 
     * expression, <code>eventPropertyName</code>, to the final method in the
     * statement, <code>action</code>, which is applied to the <code>target</code>.
     * This method is implemented by calling the
     * more general, implementation of the <code>create</code> method with 
     * the <code>listenerMethodName</code> taking the value <code>null</code>. 
     * <p>
     * To create an <code>ActionListener</code> that sets the
     * the text of a <code>JLabel</code> to the text value of 
     * the <code>JTextField</code> source of the incoming event,
     * you can use the following code:
     *
     *<blockquote>
     *<pre>
     *EventHandler.create(ActionListener.class, label, "text", "source.text");
     *</pre>
     *</blockquote>
     * 
     * This is equivalent to the following code:
     *<blockquote>
     *<pre>
//Equivalent code using an inner class instead of EventHandler.
     *label.setText((JTextField(event.getSource())).getText()) 
     *</pre>
     *</blockquote>
     *
     * @param listenerInterface the listener interface to create a proxy for
     * @param target the object that will perform the action
     * @param action the name of a writable property or method on the target
     * @param eventPropertyName the (possibly qualified) name of a readable property of the incoming event
     * 
     * @return an object that implements <code>listenerInterface</code>
     * 
     * @see #create(Class, Object, String, String, String)
     */
    public static <T> T create(Class<T> listenerInterface,
			       Object target, String action,
			       String eventPropertyName)
    {
        return create(listenerInterface, target, action, eventPropertyName, null); 
    }

    /**
     * Creates an implementation of <code>listenerInterface</code> in which
     * the method named <code>listenerMethodName</code> 
     * passes the value of the event expression, <code>eventPropertyName</code>, 
     * to the final method in the statement, <code>action</code>, which 
     * is applied to the <code>target</code>. All of the other listener 
     * methods do nothing. 
     * <p>
     * If the <code>eventPropertyName</code> is <code>null</code> the 
     * implementation calls a method with the name specified 
     * in <code>action</code> that takes an <code>EventObject</code> 
     * or a no-argument method with the same name if a method 
     * accepting an <code>EventObject</code> is not defined. 
     * <p>
     * If the <code>listenerMethodName</code> is <code>null</code> 
     * <em>all</em> methods in the interface trigger the <code>action</code> to be 
     * executed on the <code>target</code>. 
     * <p>
     * For example, to create a <code>MouseListener</code> that sets the target
     * object's <code>origin</code> property to the incoming <code>MouseEvent</code>'s
     * location (that's the value of <code>mouseEvent.getPoint()</code>) each 
     * time a mouse button is pressed, one would write:
     *<blockquote>
     *<pre>
     *EventHandler.create(MouseListener.class, "mousePressed", target, "origin", "point");
     *</pre>
     *</blockquote>
     *
     * This is comparable to writing a <code>MouseListener</code> in which all
     * of the methods except <code>mousePressed</code> are no-ops:
     *
     *<blockquote>
     *<pre>
//Equivalent code using an inner class instead of EventHandler.
     *new MouseAdapter() {
     *    public void mousePressed(MouseEvent e) {
     *        target.setOrigin(e.getPoint());
     *    }
     *}
     * </pre>
     *</blockquote>
     * 
     * @param listenerInterface the listener interface to create a proxy for
     * @param target the object that will perform the action
     * @param action the name of a writable property or method on the target
     * @param eventPropertyName the (possibly qualified) name of a readable property of the incoming event
     * @param listenerMethodName the name of the method in the listener interface that should trigger the action
     * 
     * @return an object that implements <code>listenerInterface</code>
     * 
     * @see EventHandler
     */
    public static <T> T create(Class<T> listenerInterface,
			       Object target, String action,
			       String eventPropertyName,
			       String listenerMethodName)
    {
        return (T)Proxy.newProxyInstance(target.getClass().getClassLoader(),
					 new Class[] {listenerInterface},
					 new EventHandler(target, action,
							  eventPropertyName,
							  listenerMethodName));
    }
}





