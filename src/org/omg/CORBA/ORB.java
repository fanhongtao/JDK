/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.omg.CORBA;

import org.omg.CORBA.portable.*;
import org.omg.CORBA.ORBPackage.InvalidName;

import java.util.Properties;
import java.applet.Applet;
import java.io.File;
import java.io.FileInputStream;

import java.security.AccessController; 
import java.security.PrivilegedAction;

/**
 * A class providing APIs for the CORBA Object Request Broker
 * features.  The <code>ORB</code> class also provides
 * "pluggable ORB implementation" APIs that allow another vendor's ORB
 * implementation to be used.
 * <P>
 * An ORB makes it possible for CORBA objects to communicate
 * with each other by connecting objects making requests (clients) with
 * objects servicing requests (servers).
 * <P>
 *
 * The <code>ORB</code> class, which
 * encapsulates generic CORBA functionality, does the following:
 * (Note that items 5 and 6, which include most of the methods in
 * the class <code>ORB</code>, are typically used with the <code>Dynamic Invocation
 * Interface</code> (DII) and the <code>Dynamic Skeleton Interface</code>
 * (DSI).
 * These interfaces may be used by a developer directly, but
 * most commonly they are used by the ORB internally and are
 * not seen by the general programmer.)
 * <OL>
 * <li> initializes the ORB implementation by supplying values for
 *      predefined properties and environmental parameters
 * <li> obtains initial object references to services such as
 * the NameService using the method <code>resolve_initial_references</code>
 * <li> converts object references to strings and back
 * <li> connects the ORB to a servant (an instance of a CORBA object
 * implementation) and disconnects the ORB from a servant
 * <li> creates objects such as
 *   <ul>
 *   <li><code>TypeCode</code>
 *   <li><code>Any</code>
 *   <li><code>NamedValue</code>
 *   <li><code>Context</code>
 *   <li><code>Environment</code>
 *   <li>lists (such as <code>NVList</code>) containing these objects
 *   </ul>
 * <li> sends multiple messages in the DII
 * </OL>
 *
 * <P>
 * The <code>ORB</code> class can be used to obtain references to objects
 * implemented anywhere on the network.
 * <P>
 * An application or applet gains access to the CORBA environment
 * by initializing itself into an <code>ORB</code> using one of
 * three <code>init</code> methods.  Two of the three methods use the properties
 * (associations of a name with a value) shown in the
 * table below.<BR>
 * <TABLE BORDER>
 * <TR><TH>Property Name</TH>   <TH>Property Value</TH></TR>
 * <CAPTION>Standard Java CORBA Properties:</CAPTION>
 *     <TR><TD>org.omg.CORBA.ORBClass</TD>
 *     <TD>class name of an ORB implementation</TD></TR>
 *     <TR><TD>org.omg.CORBA.ORBSingletonClass</TD>
 *     <TD>class name of the ORB returned by <code>init()</code></TD></TR>
 * </TABLE>
 * <P>
 * These properties allow a different vendor's <code>ORB</code>
 * implementation to be "plugged in."
 * <P>
 * When an ORB instance is being created, the class name of the ORB
 * implementation is located using
 * the following standard search order:<P>
 *
 * <OL>
 *     <LI>check in Applet parameter or application string array, if any
 *
 *     <LI>check in properties parameter, if any
 *
 *     <LI>check in the System properties 
 *
 *     <LI>check in the orb.properties file located in the java.home/lib 
 *         directory
 *
 *     <LI>fall back on a hardcoded default behavior (use the Java&nbsp;IDL
 *         implementation)
 * </OL>
 * <P>
 * Note that Java&nbsp;IDL provides a default implementation for the
 * fully-functional ORB and for the Singleton ORB.  When the method
 * <code>init</code> is given no parameters, the default Singleton
 * ORB is returned.  When the method <code>init</code> is given parameters
 * but no ORB class is specified, the Java&nbsp;IDL ORB implementation
 * is returned.
 * <P>
 * The following code fragment creates an <code>ORB</code> object
 * initialized with the default ORB Singleton.
 * This ORB has a
 * restricted implementation to prevent malicious applets from doing
 * anything beyond creating typecodes.
 * It is called a singleton
 * because there is only one instance for an entire virtual machine.
 * <PRE>
 *    ORB orb = ORB.init();
 * </PRE>
 * <P>
 * The following code fragment creates an <code>ORB</code> object
 * for an application.  The parameter <code>args</code>
 * represents the arguments supplied to the application's <code>main</code>
 * method.  Since the property specifies the ORB class to be
 * "SomeORBImplementation", the new ORB will be initialized with
 * that ORB implementation.  If p had been null,
 * and the arguments had not specified an ORB class,
 * the new ORB would have been
 * initialized with the default Java&nbsp;IDL implementation.
 * <PRE>
 *    Properties p = new Properties();
 *    p.put("org.omg.CORBA.ORBClass", "SomeORBImplementation");
 *    ORB orb = ORB.init(args, p);
 * </PRE>
 * <P>
 * The following code fragment creates an <code>ORB</code> object
 * for the applet supplied as the first parameter.  If the given
 * applet does not specify an ORB class, the new ORB will be
 * initialized with the default Java&nbsp;IDL implementation.
 * <PRE>
 *    ORB orb = ORB.init(myApplet, null);
 * </PRE>
 * <P>
 * An application or applet can be initialized in one or more ORBs.
 * ORB initialization is a bootstrap call into the CORBA world.
 * @version 1.70, 09/09/97
 * @since   JDK1.2
 */
abstract public class ORB {

    //
    // This is the ORB implementation used when nothing else is specified.
    // Whoever provides this class customizes this string to
    // point at their ORB implementation.
    //
    private static final String ORBClassKey = "org.omg.CORBA.ORBClass";
    private static final String ORBSingletonClassKey = "org.omg.CORBA.ORBSingletonClass";

    //
    // The last resort fallback ORB implementation classes in case
    // no ORB implementation class is dynamically configured through
    // properties or applet parameters. Change these values to
    // vendor-specific class names.
    //
    private static final String defaultORB = "com.sun.corba.se.internal.iiop.ORB";
    private static final String defaultORBSingleton = "com.sun.corba.se.internal.corba.ORBSingleton";

    //
    // The global instance of the singleton ORB implementation which
    // acts as a factory for typecodes for generated Helper classes.
    // TypeCodes should be immutable since they may be shared across
    // different security contexts (applets). There should be no way to
    // use a TypeCode as a storage depot for illicitly passing
    // information or Java objects between different security contexts.
    //
    static private ORB singleton;

    // Get System property
    private static String getSystemProperty(final String name) {

	// This will not throw a SecurityException because this
	// class was loaded from rt.jar using the bootstrap classloader.
        String propValue = (String) AccessController.doPrivileged(
	    new PrivilegedAction() {
		public java.lang.Object run() {
	            return System.getProperty(name);
	        }
            }
	);

	return propValue;
    }

    // Get property from <java-home>/lib/orb.properties file
    private static String getPropertyFromFile(final String name) {
	// This will not throw a SecurityException because this
	// class was loaded from rt.jar using the bootstrap classloader.

        String propValue = (String) AccessController.doPrivileged(
	    new PrivilegedAction() {
		public java.lang.Object run() {
	            Properties props = new Properties();
	            try {
	                // Check if orb.properties exists
	                String javaHome = System.getProperty("java.home");
	                File propFile = new File(javaHome + File.separator
				     + "lib" + File.separator
				     + "orb.properties");
	                if ( !propFile.exists() )
		            return null;

	                // Load properties from orb.properties
	                FileInputStream fis = new FileInputStream(propFile);
			try {
			    props.load(fis);
			} finally {
			    fis.close();
			}
	            } catch ( Exception ex ) {
	                return null;
	            }

	            return props.getProperty( name ) ;
                }
	    }
	);

	return propValue;
    }

    /**
     * Returns the <code>ORB</code> singleton object. This method always returns the
     * same ORB instance, which is an instance of the class described by the
     * <code>org.omg.CORBA.ORBSingletonClass</code> system property.
     * <P>
     * This no-argument version of the method <code>init</code> is used primarily
     * as a factory for <code>TypeCode</code> objects, which are used by
     * <code>Helper</code> classes to implement the method <code>type</code>.
     * It is also used to create <code>Any</code> objects that are used to
     * describe <code>union</code> labels (as part of creating a <code>
     * TypeCode</code> object for a <code>union</code>).
     * <P>
     * This method is not intended to be used by applets, and in the event
     * that it is called in an applet environment, the ORB it returns
     * is restricted so that it can be used only as a factory for
     * <code>TypeCode</code> objects.  Any <code>TypeCode</code> objects
     * it produces can be safely shared among untrusted applets.
     * <P>
     * If an ORB is created using this method from an applet,
     * a system exception will be thrown if
     * methods other than those for
     * creating <code>TypeCode</code> objects are invoked.
     *
     * @return the singleton ORB
     */
    public static ORB init() {
        if (singleton == null) {
            String className = getSystemProperty(ORBSingletonClassKey);
            if (className == null)
                className = getPropertyFromFile(ORBSingletonClassKey);
            if (className == null)
                className = defaultORBSingleton;

	    ClassLoader cl = Thread.currentThread().getContextClassLoader();

	    if (cl == null)
	        cl = ClassLoader.getSystemClassLoader();
            singleton = create_impl(className, cl);
        }
	return singleton;
    }

    private static ORB create_impl(String className, ClassLoader cl) {
        try {
            return (ORB) Class.forName(className).newInstance();
	} catch (ClassNotFoundException ex) {
	    // Eat the exception and try again below...
        } catch (Exception ex) {
	    throw new INITIALIZE(
				 "can't instantiate default ORB implementation " + className);
        }

        try {
            return (ORB) Class.forName(className, true, cl).newInstance();
        } catch (Exception ex) {
	    throw new INITIALIZE(
		"can't instantiate default ORB implementation " + className);
        }
    }

    /**
     * Creates a new <code>ORB</code> instance for a standalone
     * application.  This method may be called from applications
     * only and returns a new fully functional <code>ORB</code> object
     * each time it is called.
     * @param args command-line arguments for the application's <code>main</code>
     *             method; may be <code>null</code>
     * @param props application-specific properties; may be <code>null</code>
     * @return the newly-created ORB instance
     */
    public static ORB init(String[] args, Properties props) {
	//
	// Note that there is no standard command-line argument for
	// specifying the default ORB implementation. For an
	// application you can choose an implementation either by
	// setting the CLASSPATH to pick a different org.omg.CORBA
	// and it's baked-in ORB implementation default or by
	// setting an entry in the properties object or in the
	// system properties.
	//
        String className = null;
	ORB orb;

	if (props != null)
	    className = props.getProperty(ORBClassKey);
	if (className == null)
	    className = getSystemProperty(ORBClassKey);
	if (className == null)
            className = getPropertyFromFile(ORBClassKey);
        if (className == null)
            className = defaultORB;

	ClassLoader cl = Thread.currentThread().getContextClassLoader();

	if (cl == null)
	    cl = ClassLoader.getSystemClassLoader();

	orb = create_impl(className, cl);
	orb.set_parameters(args, props);
	return orb;
    }


    /**
     * Creates a new <code>ORB</code> instance for an applet.  This
     * method may be called from applets only and returns a new
     * fully-functional <code>ORB</code> object each time it is called.
     * @param app the applet; may be <code>null</code>
     * @param props applet-specific properties; may be <code>null</code>
     * @return the newly-created ORB instance
     */
    public static ORB init(Applet app, Properties props) {
        String className;
	ORB orb;
	ClassLoader cl = null;

        className = app.getParameter(ORBClassKey);
	if (className == null && props != null)
	    className = props.getProperty(ORBClassKey);
	if (className == null)
	    className = getSystemProperty(ORBClassKey);
	if (className == null)
            className = getPropertyFromFile(ORBClassKey);
        if (className == null)
            className = defaultORB;

	if (app != null)
	    cl = app.getClass().getClassLoader();
	else
	    cl = Thread.currentThread().getContextClassLoader();

	if (cl == null)
	    cl = ClassLoader.getSystemClassLoader();

        orb = create_impl(className, cl);
        orb.set_parameters(app, props);
	return orb;
    }

    /**
     * Allows the ORB implementation to be initialized with the given
     * parameters and properties. This method, used in applications only,
     * is implemented by subclass ORB implementations and called
     * by the appropriate <code>init</code> method to pass in its parameters.
     *
     * @param args command-line arguments for the application's <code>main</code>
     *             method; may be <code>null</code>
     * @param props application-specific properties; may be <code>null</code>
     */
    abstract protected void set_parameters(String[] args, Properties props);

    /**
     * Allows the ORB implementation to be initialized with the given
     * applet and parameters. This method, used in applets only,
     * is implemented by subclass ORB implementations and called
     * by the appropriate <code>init</code> method to pass in its parameters.
     *
     * @param app the applet; may be <code>null</code>
     * @param props applet-specific properties; may be <code>null</code>
     */
    abstract protected void set_parameters(Applet app, Properties props);

    /**
     * Connects the given servant object (a Java object that is
     * an instance of the server implementation class)
     * to the ORB. The servant class must
     * extend the <code>ImplBase</code> class corresponding to the interface that is
     * supported by the server. The servant must thus be a CORBA object
     * reference, and inherit from <code>org.omg.CORBA.Object</code>.
     * Servants created by the user can start receiving remote invocations
     * after the method <code>connect</code> has been called. A servant may also be
     * automatically and implicitly connected to the ORB if it is passed as
     * an IDL parameter in an IDL method invocation on a non-local object,
     * that is, if the servant object has to be marshalled and sent outside of the
     * process address space.
     * <P>
     * Calling the method <code>connect</code> has no effect
     * when the servant object is already connected to the ORB.
     * <P>
     * Deprecated by the OMG in favor of the Portable Object Adapter APIs.
     *
     * @param obj the servant object reference
     * @see <a href="package-summary.html#unimpl"><code>CORBA</code> package
     *      comments for unimplemented features</a>
     */
    public void connect(org.omg.CORBA.Object obj) {
	throw new NO_IMPLEMENT();
    }

    /**
     * Destroys the ORB instance and frees all the resources under an ORB instance.
     * The method is not implemented, The API is provided to conform with the OMG
     * spec.
     */
    public void destroy( ) {
	throw new NO_IMPLEMENT();
    }

    /**
     * Disconnects the given servant object from the ORB. After this method returns,
     * the ORB will reject incoming remote requests for the disconnected
     * servant and will send the exception
     * <code>org.omg.CORBA.OBJECT_NOT_EXIST</code> back to the
     * remote client. Thus the object appears to be destroyed from the
     * point of view of remote clients. Note, however, that local requests issued
     * using the servant  directly do not
     * pass through the ORB; hence, they will continue to be processed by the
     * servant.
     * <P>
     * Calling the method <code>disconnect</code> has no effect
     * if the servant is not connected to the ORB.
     * <P>
     * Deprecated by the OMG in favor of the Portable Object Adapter APIs.
     *
     * @param obj The servant object to be disconnected from the ORB
     * @see <a href="package-summary.html#unimpl"><code>CORBA</code> package
     *      comments for unimplemented features</a>
     */
    public void disconnect(org.omg.CORBA.Object obj) {
	throw new NO_IMPLEMENT();
    }

    //
    // ORB method implementations.
    //
    // We are trying to accomplish 2 things at once in this class.
    // It can act as a default ORB implementation front-end,
    // creating an actual ORB implementation object which is a
    // subclass of this ORB class and then delegating the method
    // implementations.
    //
    // To accomplish the delegation model, the 'delegate' private instance
    // variable is set if an instance of this class is created directly.
    //

    /**
     * Returns a list of the initially available CORBA object references,
     * such as "NameService" and "InterfaceRepository".
     *
     * @return an array of <code>String</code> objects that represent
     *         the object references for CORBA services
     *         that are initially available with this ORB
     */
    abstract public String[] list_initial_services();

    /**
     * Resolves a specific object reference from the set of available
     * initial service names.
     *
     * @param object_name the name of the initial service as a string
     * @return  the object reference associated with the given name
     * @exception InvalidName if the given name is not associated with a
     *                         known service
     */
    abstract public org.omg.CORBA.Object resolve_initial_references(String object_name)
	throws InvalidName;

    /**
     * Converts the given CORBA object reference to a string.
     * Note that the format of this string is predefined by IIOP, allowing
     * strings generated by a different ORB to be converted back into an object
     * reference.
     * <P>
     * The resulting <code>String</code> object may be stored or communicated
     * in any way that a <code>String</code> object can be manipulated.
     *
     * @param obj the object reference to stringify
     * @return the string representing the object reference
     */
    abstract public String object_to_string(org.omg.CORBA.Object obj);

    /**
     * Converts a string produced by the method <code>object_to_string</code>
     * back to a CORBA object reference.
     *
     * @param str the string to be converted back to an object reference.  It must
     * be the result of converting an object reference to a string using the
     * method <code>object_to_string</code>.
     * @return the object reference
     */
    abstract public org.omg.CORBA.Object string_to_object(String str);

    /**
     * Allocates an <code>NVList</code> with (probably) enough
     * space for the specified number of <code>NamedValue</code> objects.
     * Note that the specified size is only a hint to help with
     * storage allocation and does not imply the maximum size of the list.
     *
     * @param count  suggested number of <code>NamedValue</code> objects for
     *               which to allocate space
     * @return the newly-created <code>NVList</code>
     *
     * @see NVList
     */
    abstract public NVList create_list(int count);

    /**
     * Creates an <code>NVList</code> initialized with argument
     * descriptions for the operation described in the given
     * <code>OperationDef</code> object.  This <code>OperationDef</code> object
     * is obtained from an Interface Repository. The arguments in the
     * returned <code>NVList</code> object are in the same order as in the
     * original IDL operation definition, which makes it possible for the list
     * to be used in dynamic invocation requests.
     *
     * @param oper	the <code>OperationDef</code> object to use to create the list
     * @return		a newly-created <code>NVList</code> object containing
     * descriptions of the arguments to the method described in the given
     * <code>OperationDef</code> object
     *
     * @see NVList
     */
    public NVList create_operation_list(org.omg.CORBA.Object oper)
    {
	// If we came here, it means that the actual ORB implementation
	// did not have a create_operation_list(...CORBA.Object oper) method,
	// so lets check if it has a create_operation_list(OperationDef oper)
	// method.
	try {
	    // First try to load the OperationDef class
	    String opDefClassName = "org.omg.CORBA.OperationDef";
	    Class opDefClass = null;

	    ClassLoader cl = Thread.currentThread().getContextClassLoader();
	    if ( cl == null )
		cl = ClassLoader.getSystemClassLoader();
	    // if this throws a ClassNotFoundException, it will be caught below.
	    opDefClass = Class.forName(opDefClassName, true, cl);
	    
	    // OK, we loaded OperationDef. Now try to get the
	    // create_operation_list(OperationDef oper) method.
	    Class[] argc = { opDefClass };
            java.lang.reflect.Method meth = 
                this.getClass().getMethod("create_operation_list", argc);

	    // OK, the method exists, so invoke it and be happy.
	    Object[] argx = { oper };
            return (org.omg.CORBA.NVList)meth.invoke(this, argx);
        } 
        catch( java.lang.reflect.InvocationTargetException exs ) {
            Throwable t = exs.getTargetException();
            if (t instanceof Error) {
                throw (Error) t;
            }
            else if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            else {
                throw new org.omg.CORBA.NO_IMPLEMENT();
            }
        }
	catch( RuntimeException ex ) {
	    throw ex;
	} 
	catch( Exception exr ) {
            throw new org.omg.CORBA.NO_IMPLEMENT();
        }
    }


    /**
     * Creates a <code>NamedValue</code> object
     * using the given name, value, and argument mode flags.
     * <P>
     * A <code>NamedValue</code> object serves as (1) a parameter or return
     * value or (2) a context property.
     * It may be used by itself or
     * as an element in an <code>NVList</code> object.
     *
     * @param s  the name of the <code>NamedValue</code> object
     * @param any  the <code>Any</code> value to be inserted into the
     *             <code>NamedValue</code> object
     * @param flags  the argument mode flags for the <code>NamedValue</code>: one of
     * <code>ARG_IN.value</code>, <code>ARG_OUT.value</code>,
     * or <code>ARG_INOUT.value</code>.
     *
     * @return	the newly-created <code>NamedValue</code> object
     * @see NamedValue
     */
    abstract public NamedValue create_named_value(String s, Any any, int flags);

    /**
     * Creates an empty <code>ExceptionList</code> object.
     *
     * @return	the newly-created <code>ExceptionList</code> object
     */
    abstract public ExceptionList create_exception_list();

    /**
     * Creates an empty <code>ContextList</code> object.
     *
     * @return	the newly-created <code>ContextList</code> object
     * @see ContextList
     * @see Context
     */
    abstract public ContextList create_context_list();

    /**
     * Gets the default <code>Context</code> object.
     *
     * @return the default <code>Context</code> object
     * @see Context
     */
    abstract public Context get_default_context();

    /**
     * Creates an <code>Environment</code> object.
     *
     * @return	the newly-created <code>Environment</code> object
     * @see Environment
     */
    abstract public Environment create_environment();

    /**
     * Creates a new <code>org.omg.CORBA.portable.OutputStream</code> into which
     * IDL method parameters can be marshalled during method invocation.
     * @return		the newly-created
     *              <code>org.omg.CORBA.portable.OutputStream</code> object
     */
    abstract public org.omg.CORBA.portable.OutputStream create_output_stream();

    /**
     * Sends multiple dynamic (DII) requests asynchronously without expecting
     * any responses. Note that oneway invocations are not guaranteed to
     * reach the server.
     *
     * @param req		an array of request objects
     */
    abstract public void send_multiple_requests_oneway(Request[] req);

    /**
     * Sends multiple dynamic (DII) requests asynchronously.
     *
     * @param req		an array of <code>Request</code> objects
     */
    abstract public void send_multiple_requests_deferred(Request[] req);

    /**
     * Finds out if any of the deferred (asynchronous) invocations have
     * a response yet.
     * @return <code>true</code> if there is a response available;
     *         <code> false</code> otherwise
     */
    abstract public boolean poll_next_response();

    /**
     * Gets the next <code>Request</code> instance for which a response
     * has been received.
     *
     * @return		the next <code>Request</code> object ready with a response
     * @exception WrongTransaction if the method <code>get_next_response</code>
     * is called from a transaction scope different
     * from the one from which the original request was sent. See the
     * OMG Transaction Service specification for details.
     */
    abstract public Request get_next_response() throws WrongTransaction;

    /**
     * Retrieves the <code>TypeCode</code> object that represents
     * the given primitive IDL type.
     *
     * @param tcKind	the <code>TCKind</code> instance corresponding to the
     *                  desired primitive type
     * @return		the requested <code>TypeCode</code> object
     */
    abstract public TypeCode get_primitive_tc(TCKind tcKind);

    /**
     * Creates a <code>TypeCode</code> object representing an IDL <code>struct</code>.
     * The <code>TypeCode</code> object is initialized with the given id,
     * name, and members.
     *
     * @param id	the repository id for the <code>struct</code>
     * @param name	the name of the <code>struct</code>
     * @param members	an array describing the members of the <code>struct</code>
     * @return		a newly-created <code>TypeCode</code> object describing
     *              an IDL <code>struct</code>
     */
    abstract public TypeCode create_struct_tc(String id, String name,
					      StructMember[] members);

    /**
     * Creates a <code>TypeCode</code> object representing an IDL <code>union</code>.
     * The <code>TypeCode</code> object is initialized with the given id,
     * name, discriminator type, and members.
     *
     * @param id	the repository id of the <code>union</code>
     * @param name	the name of the <code>union</code>
     * @param discriminator_type	the type of the <code>union</code> discriminator
     * @param members	an array describing the members of the <code>union</code>
     * @return		a newly-created <code>TypeCode</code> object describing
     *              an IDL <code>union</code>
     */
    abstract public TypeCode create_union_tc(String id, String name,
					     TypeCode discriminator_type,
					     UnionMember[] members);

    /**
     * Creates a <code>TypeCode</code> object representing an IDL <code>enum</code>.
     * The <code>TypeCode</code> object is initialized with the given id,
     * name, and members.
     *
     * @param id	the repository id for the <code>enum</code>
     * @param name	the name for the <code>enum</code>
     * @param members	an array describing the members of the <code>enum</code>
     * @return		a newly-created <code>TypeCode</code> object describing
     *              an IDL <code>enum</code>
     */
    abstract public TypeCode create_enum_tc(String id, String name, String[] members);

    /**
     * Creates a <code>TypeCode</code> object representing an IDL <code>alias</code>
     * (<code>typedef</code>).
     * The <code>TypeCode</code> object is initialized with the given id,
     * name, and original type.
     *
     * @param id	the repository id for the alias
     * @param name	the name for the alias
     * @param original_type
     * 			the <code>TypeCode</code> object describing the original type
     *          for which this is an alias
     * @return		a newly-created <code>TypeCode</code> object describing
     *              an IDL <code>alias</code>
     */
    abstract public TypeCode create_alias_tc(String id, String name,
					     TypeCode original_type);

    /**
     * Creates a <code>TypeCode</code> object representing an IDL <code>exception</code>.
     * The <code>TypeCode</code> object is initialized with the given id,
     * name, and members.
     *
     * @param id	the repository id for the <code>exception</code>
     * @param name	the name for the <code>exception</code>
     * @param members	an array describing the members of the <code>exception</code>
     * @return		a newly-created <code>TypeCode</code> object describing
     *              an IDL <code>exception</code>
     */
    abstract public TypeCode create_exception_tc(String id, String name,
						 StructMember[] members);

    /**
     * Creates a <code>TypeCode</code> object representing an IDL <code>interface</code>.
     * The <code>TypeCode</code> object is initialized with the given id
     * and name.
     *
     * @param id	the repository id for the interface
     * @param name	the name for the interface
     * @return		a newly-created <code>TypeCode</code> object describing
     *              an IDL <code>interface</code>
     */

    abstract public TypeCode create_interface_tc(String id, String name);

    /**
     * Creates a <code>TypeCode</code> object representing a bounded IDL 
     * <code>string</code>.
     * The <code>TypeCode</code> object is initialized with the given bound,
     * which represents the maximum length of the string. Zero indicates
     * that the string described by this type code is unbounded.
     *
     * @param bound	the bound for the <code>string</code>; cannot be negative
     * @return		a newly-created <code>TypeCode</code> object describing
     *              a bounded IDL <code>string</code>
     * @exception BAD_PARAM if bound is a negative value
     */

    abstract public TypeCode create_string_tc(int bound);

    /**
     * Creates a <code>TypeCode</code> object representing a bounded IDL
     * <code>wstring</code> (wide string).
     * The <code>TypeCode</code> object is initialized with the given bound,
     * which represents the maximum length of the wide string. Zero indicates
     * that the string described by this type code is unbounded.
     *
     * @param bound	the bound for the <code>wstring</code>; cannot be negative
     * @return		a newly-created <code>TypeCode</code> object describing
     *              a bounded IDL <code>wstring</code>
     * @exception BAD_PARAM if bound is a negative value
     */
    abstract public TypeCode create_wstring_tc(int bound);

    /**
     * Creates a <code>TypeCode</code> object representing an IDL <code>sequence</code>.
     * The <code>TypeCode</code> object is initialized with the given bound and
     * element type.
     *
     * @param bound	the bound for the <code>sequence</code>
     * @param element_type
     *			the <code>TypeCode</code> object describing the elements
     *          contained in the <code>sequence</code>
     * @return		a newly-created <code>TypeCode</code> object describing
     *              an IDL <code>sequence</code>
     */
    abstract public TypeCode create_sequence_tc(int bound, TypeCode element_type);

    /**
     * Creates a <code>TypeCode</code> object representing a
     * a recursive IDL <code>sequence</code>.
     * <P>
     * For the IDL <code>struct</code> Foo in following code fragment,
     * the offset parameter for creating its sequence would be 1:
     * <PRE>
     *    Struct Foo {
     *        long value;
     *        Sequence &lt;Foo&gt; Chain;
     *    };
     * </PRE>
     *
     * @param bound	the bound for the sequence
     * @param offset	the index to the enclosing <code>TypeCode</code> object
     *                  that describes the elements of this sequence
     * @return		a newly-created <code>TypeCode</code> object describing
     *                   a recursive sequence
     * @deprecated
     */
    abstract public TypeCode create_recursive_sequence_tc(int bound, int offset);

    /**
     * Creates a <code>TypeCode</code> object representing an IDL <code>array</code>.
     * The <code>TypeCode</code> object is initialized with the given length and
     * element type.
     *
     * @param length	the length of the <code>array</code>
     * @param element_type  a <code>TypeCode</code> object describing the type
     *                      of element contained in the <code>array</code>
     * @return		a newly-created <code>TypeCode</code> object describing
     *              an IDL <code>array</code>
     */
    abstract public TypeCode create_array_tc(int length, TypeCode element_type);

    /**
     * Create a <code>TypeCode</code> object for an IDL native type.
     *
     * @param id        the logical id for the native type.
     * @param name      the name of the native type.
     * @return          the requested TypeCode.
     * @see <a href="package-summary.html#unimpl"><code>CORBA</code> package
     *      comments for unimplemented features</a>
     */
    public org.omg.CORBA.TypeCode create_native_tc(String id,
                                                   String name)
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /**
     * Create a <code>TypeCode</code> object for an IDL abstract interface.
     *
     * @param id        the logical id for the abstract interface type.
     * @param name      the name of the abstract interface type.
     * @return          the requested TypeCode.
     * @see <a href="package-summary.html#unimpl"><code>CORBA</code> package
     *      comments for unimplemented features</a>
     */
    public org.omg.CORBA.TypeCode create_abstract_interface_tc(
							       String id,
							       String name)
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    /**
     * Create a <code>TypeCode</code> object for an IDL fixed type.
     *
     * @param digits    specifies the total number of decimal digits in the number
     *                  and must be from 1 to 31 inclusive.
     * @param scale     specifies the position of the decimal point.
     * @return          the requested TypeCode.
     * @see <a href="package-summary.html#unimpl"><code>CORBA</code> package
     *      comments for unimplemented features</a>
     */
    public org.omg.CORBA.TypeCode create_fixed_tc(short digits, short scale)
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    // orbos 98-01-18: Objects By Value -- begin
    

    /**
     * Create a <code>TypeCode</code> object for an IDL value type.
     * The concrete_base parameter is the TypeCode for the immediate
     * concrete valuetype base of the valuetype for which the TypeCode
     * is being created.
     * It may be null if the valuetype does not have a concrete base.
     *
     * @param id                 the logical id for the value type.
     * @param name               the name of the value type.
     * @param type_modifier      one of the value type modifier constants:
	 *                      VM_NONE, VM_CUSTOM, VM_ABSTRACT or VM_TRUNCATABLE
     * @param concrete_base      a <code>TypeCode</code> object
     *                           describing the concrete valuetype base
	 * @param members            an array containing the members of the value type
     * @return                   the requested TypeCode
     * @see <a href="package-summary.html#unimpl"><code>CORBA</code> package
     *      comments for unimplemented features</a>
     */
    public org.omg.CORBA.TypeCode create_value_tc(String id,
						  String name,
						  short type_modifier,
						  TypeCode concrete_base,
						  ValueMember[] members)
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /**
     * Create a recursive <code>TypeCode</code> object which
     * serves as a placeholder for a concrete TypeCode during the process of creating
     * TypeCodes which contain recursion. The id parameter specifies the repository id of
     * the type for which the recursive TypeCode is serving as a placeholder. Once the
     * recursive TypeCode has been properly embedded in the enclosing TypeCode which
     * corresponds to the specified repository id, it will function as a normal TypeCode.
     * Invoking operations on the recursive TypeCode before it has been embedded in the
     * enclosing TypeCode will result in undefined behavior.
     * <P>
     * For example, the following
     * IDL type declarations contain recursion:
     * <PRE>
     *    Struct Foo {
     *        long value;
     *        Sequence &lt;Foo&gt; Chain;
     *    };
     *    Struct Bar {
     *        public Bar member;
     *    };
     * </PRE>
     * <P>
     * To create a TypeCode for struct Bar, you would invoke the TypeCode creation
     * operations as shown below:
     * <PRE>
     * String barID = "IDL:Bar:1.0";
     * TypeCode recursiveTC = orb.create_recursive_tc(barID);
     * StructMember[] members = { new StructMember("member", recursiveTC, null) };
     * TypeCode structBarTC = orb.create_struct_tc(barID, "Bar", members);
     * </PRE>
     * @param id                 the logical id of the referenced type
     * @return                   the requested TypeCode
     * @see <a href="package-summary.html#unimpl"><code>CORBA</code> package
     *      comments for unimplemented features</a>
     */
    public org.omg.CORBA.TypeCode create_recursive_tc(String id) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }   

    /**
     * Creates a <code>TypeCode</code> object for an IDL value box.
     *
     * @param id                 the logical id for the value type
     * @param name               the name of the value type
	 * @param boxed_type         the TypeCode for the type
     * @return                   the requested TypeCode
     * @see <a href="package-summary.html#unimpl"><code>CORBA</code> package
     *      comments for unimplemented features</a>
     */
    public org.omg.CORBA.TypeCode create_value_box_tc(String id,
						      String name,
						      TypeCode boxed_type)
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    // orbos 98-01-18: Objects By Value -- end
    
    /**
     * Creates an IDL <code>Any</code> object initialized to
     * contain a <code>Typecode</code> object whose <code>kind</code> field
     * is set to <code>TCKind.tc_null</code>.
     *
     * @return		a newly-created <code>Any</code> object
     */
    abstract public Any create_any();




    /**
     * Retrieves a <code>Current</code> object.
     * The <code>Current</code> interface is used to manage thread-specific
     * information for use by services such as transactions and security.
     *
     * @see <a href="package-summary.html#unimpl"><code>CORBA</code> package
     *      comments for unimplemented features</a>
     *
     * @return		a newly-created <code>Current</code> object
     * @deprecated      use resolve_initial_references.
     * @see <a href="package-summary.html#unimpl"><code>CORBA</code> package
     *      comments for unimplemented features</a>
     */
    public org.omg.CORBA.Current get_current()
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /**
     * This operation returns when the ORB has shutdown. If called by
     * the main thread, it enables the ORB to perform work using the
     * main thread. Otherwise it simply waits until the ORB has shutdown.
     *
     * @see <a href="package-summary.html#unimpl"><code>CORBA</code> package
     *      comments for unimplemented features</a>
     */
    public void run()
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /**
     * Instructs the ORB to shut down, which causes all
     * object adapters to shut down. If the <code>wait_for_completion</code>
     * parameter
     * is true, this operation blocks until all ORB processing (including
     * processing of currently executing requests, object deactivation,
     * and other object adapter operations) has completed.
     * The <code>ORB.run</code> method will return after
     * <code>shutdown</code> has been called.
     *
	 * @param wait_for_completion <code>true</code> to indicate that the
	 *                            ORB should complete processing before
	 *                            shutting down; <code>false</code> to indicate
	 *                            that the ORB should shut down immediately
     * @see <a href="package-summary.html#unimpl"><code>CORBA</code> package
     *      comments for unimplemented features</a>
     */
    public void shutdown(boolean wait_for_completion)
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /**
     * Returns <code>true</code> if the ORB needs the main thread to
     * perform some work, and <code>false</code> if the ORB does not
     * need the main thread.
	 *
	 * @return <code>true</code> if there is work pending, meaning that the ORB
	 *         needs the main thread to perform some work; <code>false</code>
	 *         if there is no work pending and thus the ORB does not need the
	 *         main thread
     *
     * @see <a href="package-summary.html#unimpl"><code>CORBA</code> package
     *      comments for unimplemented features</a>
     */
    public boolean work_pending()
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /**
     * Performs an implementation-dependent unit of work if called
     * by the main thread. Otherwise it does nothing.
     * The methods <code>work_pending</code> and <code>perform_work</code>
     * can be used in
     * conjunction to implement a simple polling loop that multiplexes
     * the main thread among the ORB and other activities.
     *
     * @see <a href="package-summary.html#unimpl"><code>CORBA</code> package
     *      comments for unimplemented features</a>
     */
    public void perform_work()
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /** 
	 * See package comments regarding unimplemented features.
     * @see <a href="package-summary.html#unimpl"><code>CORBA</code> package
     *      comments for unimplemented features</a>
     */
    public boolean get_service_information(short service_type,
					   ServiceInformationHolder service_info)
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    // orbos 98-01-18: Objects By Value -- begin

    /** 
	 * See package comments regarding unimplemented features.
     * @see <a href="package-summary.html#unimpl"><code>CORBA</code> package
     *      comments for unimplemented features</a>
     */
    public org.omg.CORBA.DynAny create_dyn_any(org.omg.CORBA.Any value)
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /** 
	 * See package comments regarding unimplemented features.
     * @see <a href="package-summary.html#unimpl"><code>CORBA</code> package
     *      comments for unimplemented features</a>
     */
    public org.omg.CORBA.DynAny create_basic_dyn_any(org.omg.CORBA.TypeCode type) throws org.omg.CORBA.ORBPackage.InconsistentTypeCode
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /** 
	 * See package comments regarding unimplemented features.
     * @see <a href="package-summary.html#unimpl"><code>CORBA</code> package
     *      comments for unimplemented features</a>
     */
    public org.omg.CORBA.DynStruct create_dyn_struct(org.omg.CORBA.TypeCode type) throws org.omg.CORBA.ORBPackage.InconsistentTypeCode
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /** 
	 * See package comments regarding unimplemented features.
     * @see <a href="package-summary.html#unimpl"><code>CORBA</code> package
     *      comments for unimplemented features</a>
     */
    public org.omg.CORBA.DynSequence create_dyn_sequence(org.omg.CORBA.TypeCode type) throws org.omg.CORBA.ORBPackage.InconsistentTypeCode
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    /** 
	 * See package comments regarding unimplemented features.
     * @see <a href="package-summary.html#unimpl"><code>CORBA</code> package
     *      comments for unimplemented features</a>
     */
    public org.omg.CORBA.DynArray create_dyn_array(org.omg.CORBA.TypeCode type) throws org.omg.CORBA.ORBPackage.InconsistentTypeCode
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /** 
	 * See package comments regarding unimplemented features.
     * @see <a href="package-summary.html#unimpl"><code>CORBA</code> package
     *      comments for unimplemented features</a>
     */
    public org.omg.CORBA.DynUnion create_dyn_union(org.omg.CORBA.TypeCode type) throws org.omg.CORBA.ORBPackage.InconsistentTypeCode
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /** 
	 * See package comments regarding unimplemented features.
     * @see <a href="package-summary.html#unimpl"><code>CORBA</code> package
     *      comments for unimplemented features</a>
     */
    public org.omg.CORBA.DynEnum create_dyn_enum(org.omg.CORBA.TypeCode type) throws org.omg.CORBA.ORBPackage.InconsistentTypeCode
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /** 
	 * See package comments regarding unimplemented features.
     * @see <a href="package-summary.html#unimpl"><code>CORBA</code> package
     *      comments for unimplemented features</a>
     */
    public org.omg.CORBA.Policy create_policy(int type, org.omg.CORBA.Any val)
        throws org.omg.CORBA.PolicyError
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

}

