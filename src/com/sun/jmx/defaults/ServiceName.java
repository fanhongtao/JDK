/*
 * @(#)ServiceName.java	1.118 06/06/22
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jmx.defaults;


import java.security.AccessController;
import java.security.PrivilegedAction;
import com.sun.jmx.mbeanserver.GetPropertyAction;


/**
 * Used for storing default values used by JMX services.
 *
 * @since 1.5
 */
public class ServiceName {

    // private constructor defined to "hide" the default public constructor
    private ServiceName() {
    }

    /**
     * The object name of the MBeanServer delegate object
     * <BR>
     * The value is <CODE>JMImplementation:type=MBeanServerDelegate</CODE>.
     */
    public static final String DELEGATE = 
	"JMImplementation:type=MBeanServerDelegate" ;

    /**
     * The default key properties for registering the class loader of the 
     * MLet service.
     * <BR>
     * The value is <CODE>type=MLet</CODE>.
     */
    public static final String MLET = "type=MLet";

    /**
     * The default domain.
     * <BR>
     * The value is <CODE>DefaultDomain</CODE>.
     */
    public static final String DOMAIN = "DefaultDomain";

    /**
     * The name of the JMX specification implemented by this product.    
     * <BR>
     * The value is <CODE>Java Management Extensions</CODE>.
     */
    public static final String JMX_SPEC_NAME = "Java Management Extensions";

    /**
     * The version of the JMX specification implemented by this product.
     * <BR>
     * The value is <CODE>1.4</CODE>.
     */
    public static final String JMX_SPEC_VERSION = "1.4";

    /**
     * The vendor of the JMX specification implemented by this product.     
     * <BR>
     * The value is <CODE>Sun Microsystems</CODE>.
     */
    public static final String JMX_SPEC_VENDOR = "Sun Microsystems";

    /**
     * The name of this product implementing the  JMX specification.
     * <BR>
     * The value is <CODE>JMX</CODE>.
     */
    public static final String JMX_IMPL_NAME = "JMX";

    /**
     * The name of the vendor of this product implementing the  
     * JMX specification.  
     * <BR>
     * The value is <CODE>Sun Microsystems</CODE>.
     */
    public static final String JMX_IMPL_VENDOR = "Sun Microsystems";

    /**
     * The version of this implementation of the JMX specification.  
     * <BR>
     * For example, with a JMX implementation as part of the 
     * Java Runtime Environment, this property has the same value as
     * the <tt>java.runtime.version</tt> system property.
     */
    public static final String JMX_IMPL_VERSION =
	AccessController.doPrivileged(new GetPropertyAction("java.runtime." +
							    "version"));

}
