/*
 * @(#)StandardMBean.java	1.23 05/05/27
 * 
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management;

import com.sun.jmx.mbeanserver.StandardMBeanMetaDataImpl;
import com.sun.jmx.trace.Trace;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * <p>An MBean whose management interface is determined by reflection
 * on a Java interface.</p>
 *
 * <p>This class brings more flexibility to the notion of Management
 * Interface in the use of Standard MBeans.  Straightforward use of
 * the patterns for Standard MBeans described in the JMX Specification
 * means that there is a fixed relationship between the implementation
 * class of an MBean and its management interface (i.e., if the
 * implementation class is Thing, the management interface must be
 * ThingMBean).  This class makes it possible to keep the convenience
 * of specifying the management interface with a Java interface,
 * without requiring that there be any naming relationship between the
 * implementation and interface classes.</p>
 *
 * <p>By making a DynamicMBean out of an MBean, this class makes
 * it possible to select any interface implemented by the MBean as its
 * management interface, provided that it complies with JMX patterns
 * (i.e., attributes defined by getter/setter etc...).</p>
 *
 * <p> This class also provides hooks that make it possible to supply
 * custom descriptions and names for the {@link MBeanInfo} returned by
 * the DynamicMBean interface.</p>
 *
 * <p>Using this class, an MBean can be created with any
 * implementation class name <i>Impl</i> and with a management
 * interface defined (as for current Standard MBeans) by any interface
 * <i>Intf</i>, in one of two general ways:</p>
 *
 * <ul>
 *
 * <li>Using the public constructor 
 *     {@link #StandardMBean(java.lang.Object, java.lang.Class)
 *     StandardMBean(impl,interface)}:
 *     <pre>
 *     MBeanServer mbs;
 *     ...
 *     Impl impl = new Impl(...);
 *     StandardMBean mbean = new StandardMBean(impl, Intf.class);
 *     mbs.registerMBean(mbean, objectName);
 *     </pre></li>
 *
 * <li>Subclassing StandardMBean:
 *     <pre>
 *     public class Impl extends StandardMBean implements Intf {
 *        public Impl() {
 *          super(Intf.class);
 *       }
 *       // implement methods of Intf
 *     }
 *
 *     [...]
 *
 *     MBeanServer mbs;
 *     ....
 *     Impl impl = new Impl();
 *     mbs.registerMBean(impl, objectName);
 *     </pre></li>
 *
 * </ul>
 *
 * <p>In either case, the class <i>Impl</i> must implement the 
 * interface <i>Intf</i>.</p>
 *
 * <p>Standard MBeans based on the naming relationship between
 * implementation and interface classes are of course still
 * available.</p>
 *
 * @since 1.5
 * @since.unbundled JMX 1.2
 */
public class StandardMBean implements DynamicMBean {
    /** The name of this class to be used for tracing */
    private final static String dbgTag = "StandardMBean";

    /**
     * The management interface.
     **/
    private Class     mbeanInterface;

    /**
     * The implementation.
     **/
    private Object    implementation;

    /**
     * The MetaData object used for invoking reflection.
     **/
    private final StandardMBeanMetaDataImpl meta;

    /**
     * The cached MBeanInfo.
     **/
    private MBeanInfo cachedMBeanInfo;

    /**
     * Make a DynamicMBean out of <var>implementation</var>, using the 
     * specified <var>mbeanInterface</var> class.
     * @param implementation The implementation of this MBean.
     *        If <code>null</code>, and null implementation is allowed,
     *        then the implementation is assumed to be <var>this</var>.
     * @param mbeanInterface The Management Interface exported by this
     *        MBean's implementation. If <code>null</code>, then this 
     *        object will use standard JMX design pattern to determine
     *        the management interface associated with the given 
     *        implementation.
     * @param nullImplementationAllowed <code>true</code> if a null 
     *        implementation is allowed. If null implementation is allowed,
     *        and a null implementation is passed, then the implementation 
     *        is assumed to be <var>this</var>.
     * @exception IllegalArgumentException if the given
     *    <var>implementation</var> is null, and null is not allowed.
     * @exception NotCompliantMBeanException if the <var>mbeanInterface</var>
     *    does not follow JMX design patterns for Management Interfaces, or
     *    if the given <var>implementation</var> does not implement the 
     *    specified interface.
     **/
    private StandardMBean(Object implementation, Class mbeanInterface,
			  boolean nullImplementationAllowed) 
	throws NotCompliantMBeanException {
	if (implementation == null) {
	    if (nullImplementationAllowed) implementation = this;
	    else throw new IllegalArgumentException("implementation is null");
	}
	this.meta = new StandardMBeanMetaDataImpl(this);
	setImplementation(implementation,mbeanInterface);
    }

    /**
     * <p>Make a DynamicMBean out of the object
     * <var>implementation</var>, using the specified
     * <var>mbeanInterface</var> class.</p>
     *
     * @param implementation The implementation of this MBean.
     * @param mbeanInterface The Management Interface exported by this
     *        MBean's implementation. If <code>null</code>, then this 
     *        object will use standard JMX design pattern to determine
     *        the management interface associated with the given 
     *        implementation.
     *
     * @exception IllegalArgumentException if the given
     *    <var>implementation</var> is null.
     * @exception NotCompliantMBeanException if the <var>mbeanInterface</var>
     *    does not follow JMX design patterns for Management Interfaces, or
     *    if the given <var>implementation</var> does not implement the 
     *    specified interface.
     **/
    public StandardMBean(Object implementation,Class mbeanInterface) 
	    throws NotCompliantMBeanException {
	this(implementation,mbeanInterface,false);
    }

    /**
     * <p>Make a DynamicMBean out of <var>this</var>, using the specified
     * <var>mbeanInterface</var> class.</p>
     *
     * <p>Call {@link #StandardMBean(java.lang.Object, java.lang.Class)
     *       this(this,mbeanInterface)}.
     * This constructor is reserved to subclasses.</p>
     *
     * @param mbeanInterface The Management Interface exported by this
     *        MBean.
     *
     * @exception NotCompliantMBeanException if the <var>mbeanInterface</var>
     *    does not follow JMX design patterns for Management Interfaces, or
     *    if <var>this</var> does not implement the specified interface.
     **/
    protected StandardMBean(Class mbeanInterface) 
	    throws NotCompliantMBeanException {
	this(null,mbeanInterface,true);
    }

    /**
     * <p>Replace the implementation object wrapped in this
     * object.</p>
     *
     * @param implementation The new implementation of this MBean.
     * The <code>implementation</code> object must implement the MBean
     * interface that was supplied when this
     * <code>StandardMBean</code> was constructed.
     *
     * @exception IllegalArgumentException if the given
     *    <var>implementation</var> is null.
     *
     * @exception NotCompliantMBeanException if the given
     * <var>implementation</var> does not implement the MBean
     * interface that was supplied at construction.
     *
     * @see #getImplementation
     **/
    public synchronized void setImplementation(Object implementation)
	    throws NotCompliantMBeanException {
	setImplementation(implementation, getMBeanInterface());
    }

    /**
     * Replace the implementation and management interface wrapped in
     * this object.
     * @param implementation The new implementation of this MBean.
     * @param mbeanInterface The Management Interface exported by this
     *        MBean's implementation. If <code>null</code>, then this 
     *        object will use standard JMX design patterns to determine
     *        the management interface associated with the given 
     *        implementation.
     * @exception IllegalArgumentException if the given
     *    <var>implementation</var> is null.
     * @exception NotCompliantMBeanException if the <var>mbeanInterface</var>
     *    does not follow JMX design patterns for Management Interfaces, or
     *    if the given <var>implementation</var> does not implement the 
     *    specified interface.
     **/
    private synchronized void setImplementation(Object implementation,
						Class mbeanInterface) 
	    throws NotCompliantMBeanException {
	if (implementation == null) 
	    throw new IllegalArgumentException("implementation is null");

	// test compliance
	this.meta.testCompliance(implementation.getClass(),mbeanInterface);

	// flush the cache...
	cacheMBeanInfo(null);
	this.implementation = implementation;	
	this.mbeanInterface = mbeanInterface;
	if (this.mbeanInterface == null)
	    this.mbeanInterface =
		meta.getStandardMBeanInterface(implementation.getClass());
    }

    /**
     * Get the implementation of this MBean.
     * @return The implementation of this MBean.
     *
     * @see #setImplementation
     **/
    public synchronized Object getImplementation() {
	return implementation;
    }

    /**
     * Get the Management Interface of this MBean.
     * @return The management interface of this MBean.
     **/
    public final synchronized Class getMBeanInterface() {
	return mbeanInterface;
    }

    /**
     * Get the class of the implementation of this MBean.
     * @return The class of the implementation of this MBean.
     **/
    public synchronized Class getImplementationClass() {
	if (implementation == null) return null;
	return implementation.getClass();
    }

    // ------------------------------------------------------------------
    // From the DynamicMBean interface.
    // ------------------------------------------------------------------
    public Object getAttribute(String attribute) 
	throws AttributeNotFoundException,
	       MBeanException, ReflectionException {
	return meta.getAttribute(getImplementation(),attribute);
    }
    
    // ------------------------------------------------------------------
    // From the DynamicMBean interface.
    // ------------------------------------------------------------------
    public void setAttribute(Attribute attribute) 
	throws AttributeNotFoundException,
	       InvalidAttributeValueException, MBeanException, 
	       ReflectionException {
	meta.setAttribute(getImplementation(),attribute);
    }
        
    // ------------------------------------------------------------------
    // From the DynamicMBean interface.
    // ------------------------------------------------------------------
    public AttributeList getAttributes(String[] attributes) {
	try {
	    return meta.getAttributes(getImplementation(),attributes);	
	} catch (ReflectionException x) {
	    final RuntimeException r = 
		new UndeclaredThrowableException(x,x.getMessage());
	    throw new RuntimeOperationsException(r,x.getMessage());
	}
    }

    // ------------------------------------------------------------------
    // From the DynamicMBean interface.
    // ------------------------------------------------------------------
    public AttributeList setAttributes(AttributeList attributes) {
	try {
	    return meta.setAttributes(getImplementation(),attributes);	
	} catch (ReflectionException x) {
	    final RuntimeException r = 
		new UndeclaredThrowableException(x,x.getMessage());
	    throw new RuntimeOperationsException(r,x.getMessage());
	}
    }
    
    // ------------------------------------------------------------------
    // From the DynamicMBean interface.
    // ------------------------------------------------------------------
    public Object invoke(String actionName, Object params[], 
			 String signature[])
	throws MBeanException, ReflectionException {
	return meta.invoke(getImplementation(),actionName,params,signature);
    }

    /**
     * Get the {@link MBeanInfo} for this MBean.
     * <p>
     * This method implements 
     * {@link javax.management.DynamicMBean#getMBeanInfo() 
     *   DynamicMBean.getMBeanInfo()}.
     * <p>
     * This method first calls {@link #getCachedMBeanInfo()} in order to
     * retrieve the cached MBeanInfo for this MBean, if any. If the
     * MBeanInfo returned by {@link #getCachedMBeanInfo()} is not null,
     * then it is returned.<br>
     * Otherwise, this method builds a default MBeanInfo for this MBean, 
     * using the Management Interface specified for this MBean.
     * <p>
     * While building the MBeanInfo, this method calls the customization 
     * hooks that make it possible for subclasses to supply their custom
     * descriptions, parameter names, etc...<br>
     * Finally, it calls {@link #cacheMBeanInfo(javax.management.MBeanInfo)
     * cacheMBeanInfo()} in order to cache the new MBeanInfo. 
     * @return The cached MBeanInfo for that MBean, if not null, or a 
     *         newly built MBeanInfo if none was cached.
     **/
    public MBeanInfo getMBeanInfo() {
	try {
	    final MBeanInfo cached = getCachedMBeanInfo();
	    if (cached != null) return (MBeanInfo)cached;
	} catch (RuntimeException x) {
	    debug("getMBeanInfo","failed to get cached MBeanInfo: "+x);
	    debugX("getMBeanInfo",x);
	}

	if (isTraceOn()) {
	    trace("getMBeanInfo", "Building MBeanInfo for "+
		  getImplementationClass().getName());
	}

	final MBeanInfo bi;
	final Object    impl;
	try {
	    synchronized (this) {
		impl = getImplementation();
		bi   = buildStandardMBeanInfo();
	    }
	} catch (NotCompliantMBeanException x) {
	    final RuntimeException r = 
		new UndeclaredThrowableException(x,x.getMessage());
	    throw new RuntimeOperationsException(r,x.getMessage());
	}

	final String                  cname = getClassName(bi);
	final String                  text  = getDescription(bi);
	final MBeanConstructorInfo[]  ctors = getConstructors(bi,impl);
	final MBeanAttributeInfo[]    attrs = getAttributes(bi);
	final MBeanOperationInfo[]    ops   = getOperations(bi);
	final MBeanNotificationInfo[] ntfs  = getNotifications(bi);
	final MBeanInfo nmbi = 
	    new MBeanInfo(cname,text,attrs,ctors,ops,ntfs);

	try { cacheMBeanInfo(nmbi); } catch (RuntimeException x) {
	    debug("cacheMBeanInfo","failed to cache MBeanInfo: "+x);
	    debugX("cacheMBeanInfo",x);
	}

	return nmbi; 
    }

    /**
     * Customization hook:
     * Get the className that will be used in the MBeanInfo returned by
     * this MBean.
     * <br>
     * Subclasses may redefine this method in order to supply their
     * custom class name.  The default implementation returns
     * {@link MBeanInfo#getClassName() info.getClassName()}.
     * @param info The default MBeanInfo derived by reflection.
     * @return the class name for the new MBeanInfo.
     **/
    protected String getClassName(MBeanInfo info) {
	if (info == null) return getImplementationClass().getName();
	return info.getClassName();
    }

    /**
     * Customization hook:
     * Get the description that will be used in the MBeanInfo returned by
     * this MBean.
     * <br>
     * Subclasses may redefine this method in order to supply their
     * custom MBean description.  The default implementation returns
     * {@link MBeanInfo#getDescription() info.getDescription()}.
     * @param info The default MBeanInfo derived by reflection.
     * @return the description for the new MBeanInfo.
     **/
    protected String getDescription(MBeanInfo info) {
	if (info == null) return null;
	return info.getDescription();
    }

    /**
     * <p>Customization hook:
     * Get the description that will be used in the MBeanFeatureInfo 
     * returned by this MBean.</p>
     *
     * <p>Subclasses may redefine this method in order to supply
     * their custom description.  The default implementation returns
     * {@link MBeanFeatureInfo#getDescription()
     * info.getDescription()}.</p>
     *
     * <p>This method is called by 
     *      {@link #getDescription(MBeanAttributeInfo)},
     *      {@link #getDescription(MBeanOperationInfo)},
     *      {@link #getDescription(MBeanConstructorInfo)}.</p>
     *
     * @param info The default MBeanFeatureInfo derived by reflection.
     * @return the description for the given MBeanFeatureInfo.
     **/
    protected String getDescription(MBeanFeatureInfo info) {
	if (info == null) return null;
	return info.getDescription();
    }

    /**
     * Customization hook:
     * Get the description that will be used in the MBeanAttributeInfo 
     * returned by this MBean.
     *
     * <p>Subclasses may redefine this method in order to supply their
     * custom description.  The default implementation returns {@link
     * #getDescription(MBeanFeatureInfo)
     * getDescription((MBeanFeatureInfo) info)}.
     * @param info The default MBeanAttributeInfo derived by reflection.
     * @return the description for the given MBeanAttributeInfo.
     **/
    protected String getDescription(MBeanAttributeInfo info) {
	return getDescription((MBeanFeatureInfo)info);
    }

    /**
     * Customization hook:
     * Get the description that will be used in the MBeanConstructorInfo 
     * returned by this MBean.
     * <br>
     * Subclasses may redefine this method in order to supply their
     * custom description.
     * The default implementation returns {@link
     * #getDescription(MBeanFeatureInfo)
     * getDescription((MBeanFeatureInfo) info)}.
     * @param info The default MBeanConstructorInfo derived by reflection.
     * @return the description for the given MBeanConstructorInfo.
     **/
    protected String getDescription(MBeanConstructorInfo info) {
	return getDescription((MBeanFeatureInfo)info);
    }

    /**
     * Customization hook:
     * Get the description that will be used for the  <var>sequence</var>
     * MBeanParameterInfo of the MBeanConstructorInfo returned by this MBean.
     * <br>
     * Subclasses may redefine this method in order to supply their
     * custom description.  The default implementation returns
     * {@link MBeanParameterInfo#getDescription() param.getDescription()}.
     * 
     * @param ctor  The default MBeanConstructorInfo derived by reflection.
     * @param param The default MBeanParameterInfo derived by reflection.
     * @param sequence The sequence number of the parameter considered 
     *        ("0" for the first parameter, "1" for the second parameter,
     *        etc...).
     * @return the description for the given MBeanParameterInfo.
     **/
    protected String getDescription(MBeanConstructorInfo ctor,
				    MBeanParameterInfo   param,
				    int sequence) {
	if (param == null) return null;
	return param.getDescription();
    }

    /**
     * Customization hook:
     * Get the name that will be used for the <var>sequence</var>
     * MBeanParameterInfo of the MBeanConstructorInfo returned by this MBean.
     * <br>
     * Subclasses may redefine this method in order to supply their
     * custom parameter name.  The default implementation returns
     * {@link MBeanParameterInfo#getName() param.getName()}.
     *  
     * @param ctor  The default MBeanConstructorInfo derived by reflection.
     * @param param The default MBeanParameterInfo derived by reflection.
     * @param sequence The sequence number of the parameter considered 
     *        ("0" for the first parameter, "1" for the second parameter,
     *        etc...).
     * @return the name for the given MBeanParameterInfo.
     **/
    protected String getParameterName(MBeanConstructorInfo ctor,
				      MBeanParameterInfo param,
				      int sequence) {
	if (param == null) return null;
	return param.getName();
    }

    /**
     * Customization hook:
     * Get the description that will be used in the MBeanOperationInfo 
     * returned by this MBean.
     * <br>
     * Subclasses may redefine this method in order to supply their
     * custom description.  The default implementation returns
     * {@link #getDescription(MBeanFeatureInfo)
     * getDescription((MBeanFeatureInfo) info)}.
     * @param info The default MBeanOperationInfo derived by reflection.
     * @return the description for the given MBeanOperationInfo.
     **/
    protected String getDescription(MBeanOperationInfo info) {
	return getDescription((MBeanFeatureInfo)info);
    }

    /**
     * Customization hook:
     * Get the <var>impact</var> flag of the operation that will be used in 
     * the MBeanOperationInfo returned by this MBean.
     * <br>
     * Subclasses may redefine this method in order to supply their
     * custom impact flag.  The default implementation returns
     * {@link MBeanOperationInfo#getImpact() info.getImpact()}.
     * @param info The default MBeanOperationInfo derived by reflection.
     * @return the impact flag for the given MBeanOperationInfo.
     **/
    protected int getImpact(MBeanOperationInfo info) {
	if (info == null) return MBeanOperationInfo.UNKNOWN;
	return info.getImpact();
    }

    /**
     * Customization hook:
     * Get the name that will be used for the <var>sequence</var>
     * MBeanParameterInfo of the MBeanOperationInfo returned by this MBean.
     * <br>
     * Subclasses may redefine this method in order to supply their
     * custom parameter name.  The default implementation returns
     * {@link MBeanParameterInfo#getName() param.getName()}.
     *  
     * @param op    The default MBeanOperationInfo derived by reflection.
     * @param param The default MBeanParameterInfo derived by reflection.
     * @param sequence The sequence number of the parameter considered 
     *        ("0" for the first parameter, "1" for the second parameter,
     *        etc...).
     * @return the name to use for the given MBeanParameterInfo.
     **/
    protected String getParameterName(MBeanOperationInfo op,
				      MBeanParameterInfo param,
				      int sequence) {
	if (param == null) return null;
	return param.getName();
    }

    /**
     * Customization hook:
     * Get the description that will be used for the  <var>sequence</var>
     * MBeanParameterInfo of the MBeanOperationInfo returned by this MBean.
     * <br>
     * Subclasses may redefine this method in order to supply their
     * custom description.  The default implementation returns
     * {@link MBeanParameterInfo#getDescription() param.getDescription()}.
     *  
     * @param op    The default MBeanOperationInfo derived by reflection.
     * @param param The default MBeanParameterInfo derived by reflection.
     * @param sequence The sequence number of the parameter considered 
     *        ("0" for the first parameter, "1" for the second parameter,
     *        etc...).
     * @return the description for the given MBeanParameterInfo.
     **/
    protected String getDescription(MBeanOperationInfo op,
				    MBeanParameterInfo param,
				    int sequence) {
	if (param == null) return null;
	return param.getDescription();
    }

    /**
     * Customization hook:
     * Get the MBeanConstructorInfo[] that will be used in the MBeanInfo
     * returned by this MBean.
     * <br>
     * By default, this method returns <code>null</code> if the wrapped
     * implementation is not <var>this</var>. Indeed, if the wrapped
     * implementation is not this object itself, it will not be possible
     * to recreate a wrapped implementation by calling the implementation 
     * constructors through <code>MBeanServer.createMBean(...)</code>.<br>
     * Otherwise, if the wrapped implementation is <var>this</var>, 
     * <var>ctors</var> is returned.
     * <br>
     * Subclasses may redefine this method in order to modify this 
     * behavior, if needed.
     * @param ctors The default MBeanConstructorInfo[] derived by reflection.
     * @param impl  The wrapped implementation. If <code>null</code> is
     *        passed, the wrapped implementation is ignored and 
     *        <var>ctors</var> is returned.
     * @return the MBeanConstructorInfo[] for the new MBeanInfo.
     **/
    protected MBeanConstructorInfo[] 
	getConstructors(MBeanConstructorInfo[] ctors, Object impl) {
	    if (ctors == null) return null;
	    if (impl != null && impl != this) return null;
	    return ctors;
    }

    /**
     * Customization hook:
     * Get the MBeanNotificationInfo[] that will be used in the MBeanInfo
     * returned by this MBean.
     * <br>
     * Subclasses may redefine this method in order to supply their
     * custom notifications.
     * @param info The default MBeanInfo derived by reflection.
     * @return the MBeanNotificationInfo[] for the new MBeanInfo.
     **/
    // Private because not needed - the StandardMBeanMetaDataImpl already
    // calls getNotificationInfo() on the implementation....
    private MBeanNotificationInfo[] 
	getNotifications(MBeanInfo info) {
	if (info == null) return null;
        return info.getNotifications();
    }
    
    /**
     * Customization hook:
     * Return the MBeanInfo cached for this object.
     *
     * <p>Subclasses may redefine this method in order to implement their
     * own caching policy.  The default implementation stores one
     * {@link MBeanInfo} object per instance.
     *
     * @return The cached MBeanInfo, or null if no MBeanInfo is cached.
     *
     * @see #cacheMBeanInfo(MBeanInfo)
     **/
    protected synchronized MBeanInfo getCachedMBeanInfo() {
	return cachedMBeanInfo;
    }

    /**
     * Customization hook:
     * cache the MBeanInfo built for this object.
     *
     * <p>Subclasses may redefine this method in order to implement
     * their own caching policy.  The default implementation stores
     * <code>info</code> in this instance.  A subclass can define
     * other policies, such as not saving <code>info</code> (so it is
     * reconstructed every time {@link #getMBeanInfo()} is called) or
     * sharing a unique {@link MBeanInfo} object when several
     * <code>StandardMBean</code> instances have equal {@link
     * MBeanInfo} values.
     *
     * @param info the new <code>MBeanInfo</code> to cache.  Any
     * previously cached value is discarded.  This parameter may be
     * null, in which case there is no new cached value.
     **/
    protected synchronized void cacheMBeanInfo(MBeanInfo info) {
	cachedMBeanInfo = info;
    }

    // ------------------------------------------------------------------
    // Build the defaullt standard MBeanInfo. 
    // ------------------------------------------------------------------    
    private synchronized MBeanInfo buildStandardMBeanInfo() 
	throws NotCompliantMBeanException {
	return meta.buildMBeanInfo(getImplementationClass(),
				   getMBeanInterface());
    }

    // ------------------------------------------------------------------
    // Build the custom MBeanConstructorInfo[]
    // ------------------------------------------------------------------    
    private MBeanConstructorInfo[] 
	getConstructors(MBeanInfo info,Object impl) {
	final MBeanConstructorInfo[] ctors = 
	    getConstructors(info.getConstructors(),impl);
	final MBeanConstructorInfo[] nctors;
	if (ctors != null) {
	    final int ctorlen = ctors.length;
	    nctors = new MBeanConstructorInfo[ctorlen];
	    for (int i=0; i<ctorlen; i++) {
		final MBeanConstructorInfo c = ctors[i];
		final MBeanParameterInfo[] params = c.getSignature();
		final MBeanParameterInfo[] nps;
		if (params != null) {
		    final int plen = params.length;
		    nps = new MBeanParameterInfo[plen];
		    for (int ii=0;ii<plen;ii++) {
			MBeanParameterInfo p = params[ii];
			final String name = getParameterName(c,p,ii);
			final String text = getDescription(c,p,ii);
			nps[ii] = new MBeanParameterInfo(name,
							 p.getType(),
							 text);
		    }
		} else {
		    nps = null;
		}
		nctors[i] = new MBeanConstructorInfo(c.getName(),
						     getDescription(c),
						     nps);
	    }
	} else {
	    nctors = null;
	}
	return nctors;
    }

    // ------------------------------------------------------------------
    // Build the custom MBeanOperationInfo[]
    // ------------------------------------------------------------------    
    private MBeanOperationInfo[] getOperations(MBeanInfo info) {
	final MBeanOperationInfo[] ops = info.getOperations();
	final MBeanOperationInfo[] nops;
	if (ops != null) {
	    final int oplen = ops.length;
	    nops = new MBeanOperationInfo[oplen];
	    for (int i=0; i<oplen; i++) {
		final MBeanOperationInfo o = ops[i];
		final MBeanParameterInfo[] params = o.getSignature();
		final MBeanParameterInfo[] nps;
		if (params != null) {
		    final int plen = params.length;
		    nps = new MBeanParameterInfo[plen];
		    for (int ii=0;ii<plen;ii++) {
			MBeanParameterInfo p = params[ii];
			final String name = getParameterName(o,p,ii);
			final String text = getDescription(o,p,ii);
			nps[ii] = new MBeanParameterInfo(name,
							 p.getType(),
							 text);
		    }
		} else {
		    nps = null;
		}
		nops[i] = new MBeanOperationInfo(o.getName(),
						 getDescription(o),
						 nps,
						 o.getReturnType(),
						 getImpact(o));
	    }
	} else {
	    nops = null;
	}
	return nops;
    }

    // ------------------------------------------------------------------
    // Build the custom MBeanAttributeInfo[]
    // ------------------------------------------------------------------    
    private MBeanAttributeInfo[] getAttributes(MBeanInfo info) {
	final MBeanAttributeInfo[] atts = info.getAttributes();
	final MBeanAttributeInfo[] natts;
	if (atts != null) {
	    final int attlen = atts.length;
	    natts = new MBeanAttributeInfo[attlen];
	    for (int i=0; i<attlen; i++) {
		final MBeanAttributeInfo a = atts[i];
		natts[i] = new MBeanAttributeInfo(a.getName(),
						  a.getType(),
						  getDescription(a),
						  a.isReadable(),
						  a.isWritable(),
						  a.isIs());
	    }
	} else {
	    natts = null;
	}
	return natts;
    }

    // private stuff

    private static boolean isTraceOn() {
        return Trace.isSelected(Trace.LEVEL_TRACE, Trace.INFO_MISC);
    }

    private static void trace(String clz, String func, String info) {
        Trace.send(Trace.LEVEL_TRACE, Trace.INFO_MISC, clz, func, info);
    }
    
    private static void trace(String func, String info) {
        trace(dbgTag, func, info);
    }
    
    private static boolean isDebugOn() {
        return Trace.isSelected(Trace.LEVEL_DEBUG, Trace.INFO_MISC);
    }
    
    private static void debug(String clz, String func, String info) {
        Trace.send(Trace.LEVEL_DEBUG, Trace.INFO_MISC, clz, func, info);
    }
    
    private static void debug(String func, String info) {
        debug(dbgTag, func, info);
    }
    
    private static void debugX(String func,Throwable e) {
	if (isDebugOn()) {
	    final StringWriter s = new StringWriter();
	    e.printStackTrace(new PrintWriter(s));
	    final String stack = s.toString();
	    
	    debug(dbgTag,func,"Exception caught in "+ func+"(): "+e);
	    debug(dbgTag,func,stack);
	
	    // java.lang.System.err.println("**** Exception caught in "+
	    // 			     func+"(): "+e);
	    // java.lang.System.err.println(stack);
	}
    }
    
}
