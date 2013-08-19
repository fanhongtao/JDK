/*
 * @(#)ORB.java	1.264 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Licensed Materials - Property of IBM
 * RMI-IIOP v1.0
 * Copyright IBM Corp. 1998 1999  All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

package com.sun.corba.se.internal.corba;

// Import JDK stuff
import java.net.*;
import java.util.*;
import java.applet.Applet;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field ;
import java.lang.reflect.Modifier ;
import java.lang.ThreadLocal;
import java.io.File ;
import java.io.IOException ;
import java.io.FileInputStream ;
import java.io.FileNotFoundException ;

// Import our stuff
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.Environment;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.NVList;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Object;
import org.omg.CORBA.Request;
import org.omg.CORBA.OBJ_ADAPTER;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.Any;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.UnionMember;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.BAD_INV_ORDER;

import org.omg.CORBA.portable.*;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ValueFactory;

import org.omg.PortableServer.Servant;

import com.sun.corba.se.connection.ORBSocketFactory;

import com.sun.corba.se.internal.core.CodeSetComponentInfo;
import com.sun.corba.se.internal.core.ClientSubcontract;
import com.sun.corba.se.internal.core.ClientResponse;
import com.sun.corba.se.internal.core.ClientRequest;
import com.sun.corba.se.internal.iiop.Connection;
import com.sun.corba.se.internal.ior.IIOPProfile;
import com.sun.corba.se.internal.core.ServerSubcontract;
import com.sun.corba.se.internal.core.ServiceContexts;
import com.sun.corba.se.internal.core.SubcontractRegistry;
import com.sun.corba.se.internal.core.ClientGIOP;
import com.sun.corba.se.internal.core.ServerGIOP;
import com.sun.corba.se.internal.core.ServerRequest;
import com.sun.corba.se.internal.core.MarshalInputStream;
import com.sun.corba.se.internal.core.MarshalOutputStream;
import com.sun.corba.se.internal.core.InternalRuntimeForwardRequest;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.core.Future;
import com.sun.corba.se.internal.core.Closure;
import com.sun.corba.se.internal.core.Constant;
import com.sun.corba.se.internal.core.GIOPVersion;
import com.sun.corba.se.internal.core.ORBVersion;
import com.sun.corba.se.internal.core.ORBVersionFactory;
import com.sun.corba.se.internal.core.INSObjectKeyMap;
import com.sun.corba.se.internal.core.INSObjectKeyEntry;
import com.sun.corba.se.internal.core.StandardIIOPProfileTemplate;

import com.sun.corba.se.internal.util.Utility;

import com.sun.corba.se.internal.orbutil.ORBUtility;
import com.sun.corba.se.internal.orbutil.ORBClassLoader;
import com.sun.corba.se.internal.orbutil.ORBConstants;
import com.sun.corba.se.internal.orbutil.ORBUtility;
import com.sun.corba.se.internal.orbutil.MinorCodes;
import com.sun.corba.se.internal.orbutil.SubcontractList;

import com.sun.corba.se.internal.iiop.DefaultSocketFactory;
import com.sun.corba.se.internal.iiop.messages.ReplyMessage;
import com.sun.corba.se.internal.iiop.messages.KeyAddr;
import com.sun.corba.se.internal.iiop.messages.ProfileAddr;
import com.sun.corba.se.internal.iiop.messages.ReferenceAddr;

import com.sun.corba.se.internal.ior.IIOPProfileTemplate;
import com.sun.corba.se.internal.ior.IIOPAddress ;
import com.sun.corba.se.internal.ior.IIOPAddressImpl ;
import com.sun.corba.se.internal.ior.ObjectKey ;
import com.sun.corba.se.internal.ior.ObjectKeyFactory ;
import com.sun.corba.se.internal.ior.ObjectId;

import com.sun.corba.se.internal.DynamicAny.DynAnyFactoryImpl;

import com.sun.corba.se.internal.ior.ObjectKeyTemplate ;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;

import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;
import com.sun.org.omg.SendingContext.CodeBase;
import com.sun.org.omg.SendingContext.CodeBaseHelper;

/** 
 * The JavaIDL ORB implementation.
 */
public abstract class ORB extends com.sun.corba.se.internal.core.ORB 
    implements TypeCodeFactory
{
    //d11638; pure java orb, caching the servant IOR per ORB
    private static IOR ior = null;

    protected void dprint( String msg ) 
    {
	ORBUtility.dprint( this, msg ) ;
    }

    // Flag set at compile time to debug flag processing: this can't 
    // be one of the xxxDebugFlags because it is used to debug the mechanism
    // that sets the xxxDebugFlags!
    public static boolean ORBInitDebug = false;

    // Currently defined debug flags.  Any additions must be called xxxDebugFlag.
    // All debug flags must be public boolean types.
    // These are set by passing the flag -ORBDebug x,y,z in the ORB init args.
    // Note that x,y,z must not contain spaces.
    public boolean transportDebugFlag = false ;
    public boolean subcontractDebugFlag = false ;
    public boolean poaDebugFlag = false ;
    public boolean orbdDebugFlag = false ;
    public boolean namingDebugFlag = false ;
    public boolean serviceContextDebugFlag = false ;
    public boolean transientObjectManagerDebugFlag = false ;
    public boolean giopVersionDebugFlag = false;
    //    public boolean iiopConnectionDebugFlag = false;
    public boolean shutdownDebugFlag = false;
    public boolean giopDebugFlag = false;

    private static final String[] JavaIDLPropertyNames = {
        ORBConstants.INITIAL_HOST_PROPERTY,
        ORBConstants.INITIAL_PORT_PROPERTY,
        ORBConstants.INITIAL_SERVICES_PROPERTY,
        ORBConstants.DEFAULT_INIT_REF_PROPERTY,
        ORBConstants.ORB_INIT_REF_PROPERTY,
        ORBConstants.SERVER_PORT_PROPERTY,
        ORBConstants.SERVER_HOST_PROPERTY,
        ORBConstants.ORB_ID_PROPERTY,
        ORBConstants.DEBUG_PROPERTY,
        ORBConstants.HIGH_WATER_MARK_PROPERTY,
        ORBConstants.LOW_WATER_MARK_PROPERTY,
        ORBConstants.NUMBER_TO_RECLAIM_PROPERTY,
        ORBConstants.SOCKET_FACTORY_CLASS_PROPERTY,
        ORBConstants.LISTEN_SOCKET_PROPERTY,
        //Temporary way of installing a version of GIOP on an ORB.
        //Can be beautified later.
        ORBConstants.GIOP_VERSION,
        ORBConstants.GIOP_FRAGMENT_SIZE,
        ORBConstants.GIOP_BUFFER_SIZE,
        ORBConstants.GIOP_11_BUFFMGR,
        ORBConstants.GIOP_12_BUFFMGR,
        ORBConstants.GIOP_TARGET_ADDRESSING,        
        ORBConstants.ALWAYS_SEND_CODESET_CTX_PROPERTY,
        ORBConstants.USE_BOMS,
        ORBConstants.USE_BOMS_IN_ENCAPS,
        ORBConstants.CHAR_CODESETS,
        ORBConstants.WCHAR_CODESETS,
	ORBConstants.ALLOW_LOCAL_OPTIMIZATION
    };

    private static final String[] JavaIDLPropertyNamePrefixes = {
        // Intentionally blank;
    };
    
    private static final String[] JavaIDLURLPropertyNames = {
    	ORBConstants.INITIAL_SERVICES_PROPERTY
    };

    //
    // The following fields form our special little collection of global state. 
    // We keep it bottled up here in the ORB class and attach a reference to
    // ourselves to every object reference that we create and handle.
    //

    // Vector holding deferred Requests
    private Vector    _dynamicRequests;
    protected SynchVariable     _svResponseReceived;

    // Applet/command-line parameters
    protected String ORBInitialHost = "";
    protected int ORBInitialPort;
    protected String ORBServerHost = "";
    protected int ORBServerPort = 0;
    protected String appletHost = "";
    protected URL appletCodeBase = null;
    protected ORBSocketFactory socketFactory;
    protected String orbId = "";
    public boolean allowLocalOptimization = false ;

    private Collection userSpecifiedListenPorts = new Vector();

    //Connection management parameters
    protected int highWaterMark = 240;
    protected int lowWaterMark = 100;
    protected int numberToReclaim = 5;

    // GIOP Related Constants
    // Default is 1.0.  We will change it to 1.2 later.
    protected GIOPVersion giopVersion = GIOPVersion.DEFAULT_VERSION;
    protected int giopFragmentSize = ORBConstants.GIOP_DEFAULT_FRAGMENT_SIZE;
    protected int giopBufferSize = ORBConstants.GIOP_DEFAULT_BUFFER_SIZE;
    protected int giop11BuffMgr =  ORBConstants.DEFAULT_GIOP_11_BUFFMGR;
    protected int giop12BuffMgr =  ORBConstants.DEFAULT_GIOP_12_BUFFMGR;
    protected short giopTargetAddressPreference = ORBConstants.ADDR_DISP_HANDLE_ALL;
    protected short giopAddressDisposition = KeyAddr.value;
    
    // Synchronization variable for shutdown
    private java.lang.Object runObj = new java.lang.Object();
    private java.lang.Object shutdownObj = new java.lang.Object();
    private java.lang.Object waitForCompletionObj = new java.lang.Object();
    private java.lang.Object invocationObj = new java.lang.Object();
    private int numInvocations = 0;
    protected static final byte STATUS_OPERATING = 1;
    protected static final byte STATUS_SHUTTING_DOWN = 2;
    protected static final byte STATUS_SHUTDOWN = 3;
    protected static final byte STATUS_DESTROYED = 4;
    byte status = STATUS_OPERATING;
    // thread local variable to store a boolean to detect deadlock in ORB.shutdown(true).
    protected ThreadLocal isProcessingInvocation = new ThreadLocal () {
        protected java.lang.Object initialValue() {
            return Boolean.FALSE;
        }
    };

    // Code set related
    protected boolean useByteOrderMarkers = ORBConstants.DEFAULT_USE_BYTE_ORDER_MARKERS;
    protected boolean useByteOrderMarkersInEncaps
        = ORBConstants.DEFAULT_USE_BYTE_ORDER_MARKERS_IN_ENCAPS;

    // Preferred native and conversion code sets for use with connections.  Can
    // override or change with appropriate properties.
    protected CodeSetComponentInfo codesets
        = CodeSetComponentInfo.JAVASOFT_DEFAULT_CODESETS;

    protected boolean alwaysSendCodeSetCtx
        = ORBConstants.DEFAULT_ALWAYS_SEND_CODESET_CTX;
    
    InitialNamingClient initialNamingClient;
    DynAnyFactoryImpl dynAnyFactory = null;

    // This map is needed for resolving recursive type code placeholders
    // based on the unique repository id.
    private Map typeCodeMap = null;
    // This map is caching TypeCodes created for a certain class (key)
    // and is used in Util.writeAny()
    private Map typeCodeForClassMap = null;
    private String savedId = null;
    private TypeCodeImpl savedCode = null;

    // Cache to hold ValueFactories (Helper classes) keyed on repository ids
    protected Hashtable valueFactoryCache = new Hashtable();

    // thread local variable to store the current ORB version
    private ThreadLocal orbVersionThreadLocal ;

    // Table used for all locally registered services.
    // This is used for register/resolve/list initial methods.
    // This table maps Strings to Closures.  The evaluate method
    // of the Closure must return a CORBA.Object.
    private HashMap initialReferenceTable ;

    /** 
     * Create a new ORB. Should only be invoked from the
     * CORBA ORB superclass. Should be followed by the appropriate
     * set_parameters() call.
     */
    public ORB() 
    {
        _dynamicRequests = new Vector();
        _svResponseReceived = new SynchVariable();

	initialNamingClient = new InitialNamingClient(this);
	initialReferenceTable = new HashMap() ;

	// default ORB version is the version of ORB with correct Rep-id
	// changes
        orbVersionThreadLocal  = new ThreadLocal () {
           protected java.lang.Object initialValue() {
               // set default to version of the ORB with correct Rep-ids
               return ORBVersionFactory.getORBVersion() ; 
           }
        };

	// Register the Dynamic Any factory
	Closure closure = new Closure() {
	    public java.lang.Object evaluate() {
		return new DynAnyFactoryImpl( ORB.this ) ;
	    }
	} ;
	Future future = new Future( closure ) ;
	registerInitialReference( ORBConstants.DYN_ANY_FACTORY_NAME, future ) ;
    }

    public InitialNamingClient getInitialNamingClient( ) {
        return initialNamingClient;
    }

    public ORBVersion getORBVersion()
    {
	// return the thread local data
	return (ORBVersion)(orbVersionThreadLocal.get()) ;
    }

    public void setORBVersion(ORBVersion verObj)
    {
	// set the thread local data
	orbVersionThreadLocal.set(verObj);
    }

    /**
     * Should the client send the code set service context on every
     * request?
     */
    public boolean alwaysSendCodeSetServiceContext() {
        return alwaysSendCodeSetCtx;
    }

    /**
     * Use byte order markers when applicable during character conversion?
     */
    public boolean useByteOrderMarkers() {
        return useByteOrderMarkers;
    }

    /**
     * Use byte order markers even in encapsulations?
     */
    public boolean useByteOrderMarkersInEncapsulations() {
        return useByteOrderMarkersInEncaps;
    }

    /**
     * Get the prefered code sets for connections.
     */
    public CodeSetComponentInfo getCodeSetComponentInfo() {
        return codesets;
    }

    /****************************************************************************
     * The following methods deal with parsing parameters and doing appropriate
     * initialization.
     ****************************************************************************/

    /**
     * Initialize any necessary ORB state; get attributes if possible.
     * Called from org.omg.CORBA.ORB.init().
     * @param app the applet
     * @param props the applet properties
     */
    protected void set_parameters(Applet app, Properties props) 
    {
        // Note: In some applet-development frameworks
        // the applet object may not be directly available and so it's
        // possible for the Applet instance to be null.

	// Get the full list of property names that we are interested in
        String[] propertyNames = getPropertyNames();
	String[] propertyNamePrefixes = getPropertyNamePrefixes();

        if (app != null) {
            appletCodeBase = app.getCodeBase();

	    if (appletCodeBase != null)
		appletHost = appletCodeBase.getHost( );
        }

        // Build up the full list of configuration properties
	// from the applet-params and props-argument. 

        // Make Properties Vector to handle multiple -ORBInitDef s
        Properties propList = new Properties();

        // Until we decide it's ok for getSystemProperty() to
        // use AccessController.beginPrivileged(), I've commented
        // out the use of System properties for applets since
        // this will result in confusing SecurityExceptions in
        // most situations.
        //
	/*
           findORBPropertiesFromSystem(propList,
	                               propertyNames, propertyNamePrefixes);
	*/

        findPropertiesFromProperties(propList, props, 
				     propertyNames, propertyNamePrefixes);
        findPropertiesFromApplet(propList, app,
				 propertyNames, propertyNamePrefixes);

        checkAppletPropertyDefaults(propList);

        // Use the full props list to set ORB state.
        parseProperties(propList);
    }

    /**
     * Initialize any necessary ORB state; get attributes if possible.
     * Called from org.omg.CORBA.ORB.init().
     * @param params An array of parameters in the form of alternating <br>
     * "-param-name" and "param-value" strings.
     * @param props the application properties
     */
    protected void set_parameters (String[] params, Properties props)
    {
	// Get the full list of property names that we are interested in
        String[] propertyNames = getPropertyNames();
	String[] propertyNamePrefixes = getPropertyNamePrefixes();

        // Build up the full list of configuration properties
	// from the command-line-params, props-argument, System properties.

        Properties propList = new Properties();
	Vector orbInitRefList = new Vector();

        findORBPropertiesFromSystem(propList,
				    propertyNames, propertyNamePrefixes);
        findPropertiesFromProperties(propList, props,
				     propertyNames, propertyNamePrefixes);
	findPropertiesFromFile(propList, propertyNames, propertyNamePrefixes);
        findPropertiesFromArgs(propList, orbInitRefList,
			       params, propertyNames, propertyNamePrefixes);

        checkApplicationPropertyDefaults(propList);

        // Use the full props list to set ORB state.
        parseProperties(propList);

        boolean result = initialNamingClient.setORBInitRefList( 
	    orbInitRefList );
	// If we are unsuccessful in setting the ORBInitDef list then it means 
	// there are some malformed URLs presesnt
	if( result == false )
	{
	    // Make sure to add the right minor codes here, for now it is 
	    // reusing one of the old Minorcode.
            throw new org.omg.CORBA.BAD_PARAM(MinorCodes.BAD_STRINGIFIED_IOR, 
					      CompletionStatus.COMPLETED_NO);
	}
    }

    /** Return a list of property names that this ORB is interested in.
     *  This may be overridden by subclasses, but subclasses must call
     *  super.getPropertyNames() to get all names.
     */
    protected String[] getPropertyNames()
    {
	String[] names = new String[JavaIDLPropertyNames.length];
	for ( int i=0; i<JavaIDLPropertyNames.length; i++ ) 
	    names[i] = JavaIDLPropertyNames[i]; 

	if (ORBInitDebug)
	    dprint( "getPropertyNames returns " + 
		ORBUtility.objectToString( names ) ) ;

	return names;
    }

    /** Return a list of property name prefixes that this ORB is interested in.
     *  This may be overridden by subclasses, but subclasses must call
     *  super.getPropertyNames() to get all names.
     */
    protected String[] getPropertyNamePrefixes()
    {
	String[] names = new String[JavaIDLPropertyNamePrefixes.length];
	for ( int i=0; i<JavaIDLPropertyNamePrefixes.length; i++ ) {
	    names[i] = JavaIDLPropertyNamePrefixes[i]; 
	}

	if (ORBInitDebug) {
	    dprint( "getPropertyNamePrefixes returns " + ORBUtility.objectToString( names ) ) ;
	}

	return names;
    }

    /*
     * A callback is used since sometimes we cannot actually get our
     * hands on the properties object (e.g., System.properties).
     */
    private void findPropertiesWithPrefix(String[] propertyNamePrefixes,
					  Enumeration namesToSearch,
					  GetPropertyCallback getProperty,
					  String source,
					  Properties resultProperties)
    {
        while (namesToSearch.hasMoreElements()) {
	    String pn = (String) namesToSearch.nextElement();	    
	    for (int j = 0; j < propertyNamePrefixes.length; j++) {
		if (pn.startsWith(propertyNamePrefixes[j])) {
		    String value = getProperty.get(pn);
		    // Note: do a put even if value is null since just
		    // the presence of the property may be significant.
		    resultProperties.put(pn, value);
		    if (ORBInitDebug) {
		        dprint( "Found prefixed property " + pn + "=" +
                                value + " in " + source);
		    }
		}
	    }
	}
    }

    protected boolean singleParam(String param)
    {
	// Return true if this param does not have a data field after it
	// (e.g. for on/off flags).

        return false;
    }

    protected String findMatchingPropertyName( String[] propertyNames,
					       String suffix ) 
    {
	for (int ctr=0; ctr<propertyNames.length; ctr++) {
	    if (propertyNames[ctr].endsWith( suffix )) {
		return propertyNames[ctr] ;
	    }
	}

	return null ;
    }

    //
    // Map command-line arguments to ORB properties.
    //
    protected void findPropertiesFromArgs(Properties props, 
					  Vector orbInitRefList,
                                          String[] params,
					  String[] propertyNames,
					  String[] propertyNamePrefixes) 
    {
        // REVISIT: Parameter propertyNamePrefixes is ignored at this time.
        //          No OMG specs have -ORB<prefix> at this time.

	if (ORBInitDebug) 
	    dprint( "findPropertiesFromArgs called with params=" +
		    ORBUtility.objectToString( params ) + " propertyNames = " +
		    ORBUtility.objectToString( propertyNames ) ) ;

        if (params == null) 
            return;

	// All command-line args are of the form "-ORBkey value".
	// The key is mapped to org.omg.CORBA.key.

	String name ;
	String value ;

        for ( int i=0; i<params.length; i++ ) {
	    value = null ;
	    name = null ;

            if ( params[i] != null && params[i].startsWith("-ORB") ) {
		String argName = params[i].substring( 1 ) ;
		name = findMatchingPropertyName( propertyNames, argName ) ;

		if (name != null)
		    if (singleParam(params[i])) {
			value = params[i] ;
		    } else if ( i+1 < params.length && params[i+1] != null ) {
			value = params[++i];
		    } 
            }

	    if (value != null) {
		if (ORBInitDebug) 
		    dprint( "Found property " + name + "=" + value + 
			    " in args" ) ;
	 	if( name.equals( "org.omg.CORBA.ORBInitRef" ) ) 
		{
			orbInitRefList.add( value );
		}
		else
		{
			props.put( name, value ) ;
		}
	    }
        }  
    }

    //
    // Map applet parameters to ORB properties.
    //
    protected void findPropertiesFromApplet(Properties props, 
					    Applet app, 
					    String[] propertyNames,
					    String[] propertyNamePrefixes) 
    {
        // REVISIT: propertyNamePrefixes is ignored at this time.
        //          There is no Java API to get a applet parameter
        //          by specifying its prefix.

        if (app == null)
            return;

        for (int i=0; i < propertyNames.length; i++) {

            String value = app.getParameter(propertyNames[i]);
            if (value == null)
	        continue;

            props.put(propertyNames[i], value);
        }

        //
        // Special Case:
        //
        // Convert any applet parameter relative URLs to an
        // absolute URL based on the Document Root. This is so HTML URLs can be
        // kept relative which is sometimes useful for managing the
        // Document Root layout.
        //
        for (int i=0; i < JavaIDLURLPropertyNames.length; i++) {
            String value;

            value = props.getProperty(JavaIDLURLPropertyNames[i]);
            if (value == null)
                continue;

            try {
	        URL url;
                url = new URL(app.getDocumentBase(), value);
	        props.put(JavaIDLURLPropertyNames[i], url.toExternalForm());
            } catch (java.net.MalformedURLException ex) {
	        //
	        // This will be caught again later if this property is used.
	        // Don't worry about reporting exceptions now.
	        //
            }
        }
    }

    private static Class thisClass = ORB.class;

    private static String getSystemProperty(final String name) 
    {
        return (String)AccessController.doPrivileged(new GetPropertyAction(name));
    }

    private static Enumeration getSystemPropertyNames()
    {          
        // This will not throw a SecurityException because this           
        // class was loaded from rt.jar using the bootstrap classloader.  
        return (Enumeration)
	    AccessController.doPrivileged(        
	        new PrivilegedAction() {                                      
	              public java.lang.Object run() {
			  return System.getProperties().propertyNames();
		      }
	        }                                                             
		                         );
    }

    private void getPropertiesFromFile( Properties props, String fileName )
    {
        try {
	    File file = new File( fileName ) ;
	    if (!file.exists())
		return ;

            FileInputStream in = new FileInputStream( file ) ;
	    
	    try {
		props.load( in ) ;
	    } finally {
		in.close() ;
	    }
        } catch (Exception exc) {
            if (ORBInitDebug)
                dprint( "ORB properties file " + fileName + " not found: " + 
		    exc) ;
        }
    }

    Properties getFileProperties()
    {
        Properties defaults = new Properties() ;

	String javaHome = getSystemProperty( "java.home" ) ;
	String fileName = javaHome + File.separator + "lib" + File.separator +
	    "orb.properties" ;

	getPropertiesFromFile( defaults, fileName ) ;

	Properties results = new Properties( defaults ) ;

        String userHome = getSystemProperty( "user.home" ) ;
        fileName = userHome + File.separator + "orb.properties" ;

	getPropertiesFromFile( results, fileName ) ;
	return results ;
    }

    protected void findPropertiesFromFile(Properties props, 
					  String[] propertyNames,
					  String[] propertyNamePrefixes) 
    {
	final Properties fileProps = getFileProperties() ;
	if (fileProps==null)
	    return ;

        for (int i=0; i < propertyNames.length; i++) {
            String value;

            value = (String)fileProps.getProperty(propertyNames[i]);
            if (value == null)
	        continue;

            props.put(propertyNames[i], value);
	    if (ORBInitDebug) 
		dprint( "Found property " + propertyNames[i] + "=" +
			value + " in file properties" ) ;
	}

	findPropertiesWithPrefix(
	    propertyNamePrefixes, fileProps.propertyNames(),
	    new GetPropertyCallback() {
	        public String get(String name) {
	            return fileProps.getProperty(name);
	            }
	        },
	    "file properties", props
	);
    }

    //
    // Map System properties to ORB properties.
    // Security bug fix 4278205:
    // Allow only reading of system properties with ORB prefixes.
    // Previously a malicious subclass was able to read ANY system property.
    //
    private void findORBPropertiesFromSystem(Properties props, 
					     String[] propertyNames,
					     String[] propertyNamePrefixes) 
    {
        for (int i=0; i < propertyNames.length; i++) {
            if (propertyNames[i].startsWith( ORBConstants.OMG_PREFIX ) ||
                propertyNames[i].startsWith( ORBConstants.SUN_PREFIX ) ||
                propertyNames[i].startsWith( ORBConstants.SUN_LC_PREFIX ) ||
                propertyNames[i].startsWith( ORBConstants.SUN_LC_VERSION_PREFIX ))
                                             
            {
                String value = getSystemProperty(propertyNames[i]);
                if (value != null) {
                    props.put(propertyNames[i], value);
                    if (ORBInitDebug)
                        dprint( "Found property " + propertyNames[i] + "=" +
                                value + " in system properties" ) ;
                }
            }
        }

	// Any bad apples spoil the whole bunch.
	for (int i = 0; i < propertyNamePrefixes.length; i++) {
  	    if (! propertyNamePrefixes[i].startsWith( 
		ORBConstants.PI_ORB_INITIALIZER_CLASS_PREFIX ) )
            {
	        throw
		  new INTERNAL("findORBPropertiesFromSystem: illegal prefix: "
			       + propertyNamePrefixes[i]);
	    }
	}

	Enumeration systemPropertyNames = getSystemPropertyNames();
	findPropertiesWithPrefix(
	    propertyNamePrefixes, 
	    systemPropertyNames,
	    new GetPropertyCallback() {
		public String get(String name) {
		    return getSystemProperty(name);
		}
	    },
	    "system properties", 
	    props);
    }

    //
    // Map/copy the properties argument from set_properties() into our
    // ORB properties object.
    //
    protected void findPropertiesFromProperties(Properties props, 
						Properties arg, 
						String[] propertyNames,
						String[] propertyNamePrefixes)
    {
	if (ORBInitDebug)
	    dprint( "FindPropertiesFromProperties called with args = " + 
		    ORBUtility.objectToString( arg ) + "propertytNames = " + 
		    ORBUtility.objectToString( propertyNames ) ) ;

        if (arg == null)
            return;

        for (int i=0; i < propertyNames.length; i++) {
            String value;

            value = arg.getProperty(propertyNames[i]);
            if (value == null)
	        continue;

            props.put(propertyNames[i], value);
	    if (ORBInitDebug) 
		dprint( "Found property " + propertyNames[i] + "=" +
			value + " in properties argument" ) ;
        }

	final Properties finalArg = arg;
	findPropertiesWithPrefix(
	    propertyNamePrefixes, 
	    arg.propertyNames(),
	    new GetPropertyCallback() {
		public String get(String name) {
		    return finalArg.getProperty(name);
		}
	    },
	    "properties argument", 
	    props);

    }

    // Set appropriate defaults for an applet ORB.
    private void checkAppletPropertyDefaults(Properties props) 
    {
        String host =
	    props.getProperty( ORBConstants.INITIAL_HOST_PROPERTY ) ; 

        if ((host == null) || (host.equals(""))) {
	    props.put( ORBConstants.INITIAL_HOST_PROPERTY, appletHost);
        }
	
	String serverHost =
	    props.getProperty( ORBConstants.SERVER_HOST_PROPERTY ) ;

	if ((serverHost == null) || (serverHost.equals(""))) {
	    props.put( ORBConstants.SERVER_HOST_PROPERTY, 
		      getLocalHostName());
	}
    }

    // Set appropriate defaults for an application ORB.
    private void checkApplicationPropertyDefaults(Properties props) 
    {
        String host = props.getProperty( ORBConstants.INITIAL_HOST_PROPERTY ) ;

        if ((host == null) || (host.equals(""))) {
	    props.put( ORBConstants.INITIAL_HOST_PROPERTY, 
		      getLocalHostName());
        }

	String serverHost =
	    props.getProperty( ORBConstants.SERVER_HOST_PROPERTY ) ;

	if ((serverHost == null) || (serverHost.equals(""))) {
	    props.put( ORBConstants.SERVER_HOST_PROPERTY,
		      getLocalHostName());
	}
    }

    /* keeping a copy of the getLocalHostName so that it can only be called 
     * internally and the unauthorized clients cannot have access to the
     * localHost information, originally, the above code was calling getLocalHostName
     * from Connection.java.  If the hostname is cached in Connection.java, then
     * it is a security hole, since any unauthorized client has access to
     * the host information.  With this change it is used internally so the
     * security problem is resolved.  Also in Connection.java, the getLocalHost()
     * implementation has changed to always call the 
     * InetAddress.getLocalHost().getHostAddress()
     */

    /*
     * getLocalHostName is private in this class. 
     * Eventually, it should be protected, similar methods 
     * in the subclasses removed and security check made here.
     *
     */
    private static String localHostString = null;

    private String getLocalHostName() {
        if (localHostString != null) {
            return localHostString;
        } else {
            try {
                synchronized (com.sun.corba.se.internal.corba.ORB.class){
                    if ( localHostString == null )
                        localHostString = InetAddress.getLocalHost().getHostAddress();
                    return localHostString;
                }

            } catch (Exception ex) {
                throw new INTERNAL( MinorCodes.GET_LOCAL_HOST_FAILED,
                                CompletionStatus.COMPLETED_NO );
            }
	}
    }

    protected void setDebugFlags( String args ) 
    {
	StringTokenizer st = new StringTokenizer( args, "," ) ;
	while (st.hasMoreTokens()) {
	    String token = st.nextToken() ;

	    // If there is a public boolean data member in this class
	    // named token + "DebugFlag", set it to true.
	    try {
		Field fld = this.getClass().getField( token + "DebugFlag" ) ; 
		int mod = fld.getModifiers() ;
		if (Modifier.isPublic( mod ) && !Modifier.isStatic( mod ))
		    if (fld.getType() == boolean.class)
			fld.setBoolean( this, true ) ;
	    } catch (Exception exc) {
		// ignore it
	    }
	}
    }

    /** Use the properties object to configure ORB state.
     *  This may be overridden by subclasses, but they must call
     *  super.parseProperties() to allow this class to set its state.
     */ 
    protected void parseProperties(Properties props)
    {
	// get server debug flags
        String debugFlags = props.getProperty( ORBConstants.DEBUG_PROPERTY ) ;
        if (debugFlags != null) {
	    if (ORBInitDebug)
		dprint( "Setting debug flags to " + debugFlags ) ;
            setDebugFlags(debugFlags) ;
        }
    
        String param = props.getProperty( ORBConstants.INITIAL_HOST_PROPERTY ) ;
        if (param != null) {
	    if (ORBInitDebug)
		dprint( "setting initial host to " + param ) ;
            ORBInitialHost = param;
        }

        param = props.getProperty( ORBConstants.INITIAL_PORT_PROPERTY ) ;
        if (param != null) {
            try {
		ORBInitialPort = Integer.parseInt(param);
		if (ORBInitDebug)
		    dprint( "setting initial services port to " + ORBInitialPort ) ;
	        initialNamingClient.setInitialServicesPort(ORBInitialPort);
            } catch (java.lang.NumberFormatException e) {}
        }

	param = props.getProperty( ORBConstants.SERVER_HOST_PROPERTY ) ;
	if (param != null) {
	    if (ORBInitDebug)
		dprint( "setting ORB server host to " + param ) ;
	    ORBServerHost = param;
	}

	param = props.getProperty( ORBConstants.SERVER_PORT_PROPERTY ) ;
	if (param != null) {
	    try {
		ORBServerPort = Integer.parseInt(param);
		if (ORBInitDebug)
		    dprint( "setting ORB server port to " + ORBServerPort ) ;
	    } catch (java.lang.NumberFormatException e) { }
	}

	param = props.getProperty( ORBConstants.ORB_ID_PROPERTY ) ;
	if (param != null) {
	    if (ORBInitDebug)
		dprint( "setting ORB Id to " + param ) ;
	    orbId = param;
	}

        param = props.getProperty( ORBConstants.INITIAL_SERVICES_PROPERTY ) ;
        if (param != null) {
	    try {
		if (ORBInitDebug)
		    dprint( "setting initial services URL to " + param ) ;
	        initialNamingClient.setServicesURL(new URL(param));
	    } catch (java.io.IOException ex) {
	        // Fallthrough
            }
	}

        param = props.getProperty( ORBConstants.ORB_INIT_REF_PROPERTY );
        if (param != null) {
	    try {
		if (ORBInitDebug)
		    dprint( "setting ORBInitRef to " + param ) ;
	       	initialNamingClient.addORBInitRef(param);
	    } catch (Exception ex) {
	        // Fallthrough
            }
	}

        param = props.getProperty( ORBConstants.DEFAULT_INIT_REF_PROPERTY ) ;
        if (param != null) {
	    try {
		if (ORBInitDebug)
		    dprint( "setting ORBDefaultInitRef to " + param ) ;
	       	initialNamingClient.setORBDefaultInitRef(param);
	    } catch (Exception ex) {
	        // Fallthrough
            }
	}

        //...//The following 3 parameters are there for connection management.
        param = props.getProperty( ORBConstants.HIGH_WATER_MARK_PROPERTY ) ;
        if (param != null) {
            try {
		highWaterMark = Integer.parseInt(param);
		if (ORBInitDebug)
		    dprint( "setting high water mark for connections " + highWaterMark ) ;
            } catch (java.lang.NumberFormatException e) {}
        }

        param = props.getProperty( ORBConstants.LOW_WATER_MARK_PROPERTY ) ;
        if (param != null) {
            try {
		lowWaterMark = Integer.parseInt(param);
		if (ORBInitDebug)
		    dprint( "setting low water mark for connections " + lowWaterMark ) ;
            } catch (java.lang.NumberFormatException e) {}
        }

        param = props.getProperty( ORBConstants.NUMBER_TO_RECLAIM_PROPERTY ) ;
        if (param != null) {
            try {
		numberToReclaim = Integer.parseInt(param);
		if (ORBInitDebug)
		    dprint( "setting number of connections to reclaim during cleanup " + 
			numberToReclaim ) ;
            } catch (java.lang.NumberFormatException e) {}
        }

        //GIOP Related Constants.
        param = props.getProperty( ORBConstants.GIOP_VERSION ) ;
        if (param != null) {
            try {

                giopVersion = GIOPVersion.parseVersion(param);
                if (ORBInitDebug)
                    dprint( "setting default GIOP version to " + giopVersion);

            } catch (java.lang.NumberFormatException e) {
                if (ORBInitDebug)
                    dprint( "Error: " + e);
            }
        }

        param = props.getProperty( ORBConstants.GIOP_FRAGMENT_SIZE ) ;
        if (param != null) {
            try {
                giopFragmentSize = Integer.parseInt(param);

		if(giopFragmentSize < ORBConstants.GIOP_FRAGMENT_MINIMUM_SIZE){
		    throw new INITIALIZE(ORBConstants.GIOP_FRAGMENT_SIZE
					 + " Illegal value: " + giopFragmentSize
					 + " (must be at least "
					 + ORBConstants.GIOP_FRAGMENT_MINIMUM_SIZE
					 + ")");
		}

                if (giopFragmentSize % ORBConstants.GIOP_FRAGMENT_DIVISOR != 0)
                    throw new INITIALIZE(ORBConstants.GIOP_FRAGMENT_SIZE
                                         + " Illegal value: " + giopFragmentSize
                                         + " (not divisible by "
                                         + ORBConstants.GIOP_FRAGMENT_DIVISOR
                                         + ")");

		if (ORBInitDebug)
		    dprint( "setting GIOP fragment size to " + giopFragmentSize ) ;
            } catch (java.lang.NumberFormatException e) {}
        }

        param = props.getProperty( ORBConstants.GIOP_BUFFER_SIZE ) ;
        if (param != null) {
            try {
                giopBufferSize = Integer.parseInt(param);
		if (ORBInitDebug)
		    dprint( "setting GIOP buffer size to " + giopFragmentSize ) ;
            } catch (java.lang.NumberFormatException e) {}
        }

        param = props.getProperty( ORBConstants.GIOP_11_BUFFMGR ) ;
        if (param != null) {
            try {

                giop11BuffMgr = Integer.parseInt(param);
                if (ORBInitDebug)
                    dprint( "setting default GIOP11 BuffMgr to " + giop11BuffMgr);

            } catch (java.lang.NumberFormatException e) {
                if (ORBInitDebug)
                    dprint( "Error: " + e);
            }
        }

        param = props.getProperty( ORBConstants.GIOP_12_BUFFMGR ) ;
        if (param != null) {
            try {

                giop12BuffMgr = Integer.parseInt(param);
                if (ORBInitDebug)
                    dprint( "setting default GIOP12 BuffMgr to " + giop12BuffMgr);

            } catch (java.lang.NumberFormatException e) {
                if (ORBInitDebug)
                    dprint( "Error: " + e);
            }
        }

        param = props.getProperty(ORBConstants.GIOP_TARGET_ADDRESSING);
        if (param != null) {
            try {
                short targetAddressing = Short.parseShort(param);
                switch (targetAddressing) {
                case ORBConstants.ADDR_DISP_OBJKEY :
                    giopAddressDisposition = KeyAddr.value;
                    giopTargetAddressPreference = targetAddressing;
                    break;
                case ORBConstants.ADDR_DISP_PROFILE :
                    giopAddressDisposition = ProfileAddr.value;     
                    giopTargetAddressPreference = targetAddressing;
                    break;
                case ORBConstants.ADDR_DISP_IOR :
                    giopAddressDisposition = ReferenceAddr.value;
                    giopTargetAddressPreference = targetAddressing;
                    break;
                case ORBConstants.ADDR_DISP_HANDLE_ALL :
                    giopAddressDisposition = KeyAddr.value;
                    giopTargetAddressPreference = targetAddressing;
                    break;
                default:
                    throw new INITIALIZE(ORBConstants.GIOP_TARGET_ADDRESSING +
                        " Illegal value : " + param);
                }
                if (ORBInitDebug) {
                    dprint("setting GIOP TargetAddressing to " +
                        giopTargetAddressPreference);
                }
            } catch (java.lang.NumberFormatException e) {
                if (ORBInitDebug)
                    dprint( "Error: " + e);
                throw new INITIALIZE(ORBConstants.GIOP_TARGET_ADDRESSING +
                    " Illegal value : " + param);                    
            }          
        }

        // Code set related
        param = props.getProperty(ORBConstants.ALWAYS_SEND_CODESET_CTX_PROPERTY);
        if (param != null) {
            alwaysSendCodeSetCtx = Boolean.valueOf(param).booleanValue();
            if (ORBInitDebug)
                dprint("Setting alwaysSendCodeSetCtx to " + alwaysSendCodeSetCtx);
        }
        param = props.getProperty(ORBConstants.USE_BOMS);
        if (param != null) {
            useByteOrderMarkers = Boolean.valueOf(param).booleanValue();
            if (ORBInitDebug)
                dprint("Setting useByteOrderMarkers to " + useByteOrderMarkers);
        }

        param = props.getProperty(ORBConstants.USE_BOMS_IN_ENCAPS);
        if (param != null) {
            useByteOrderMarkersInEncaps = Boolean.valueOf(param).booleanValue();
            if (ORBInitDebug)
                dprint("Setting useByteOrderMarkersInEncaps to " + useByteOrderMarkersInEncaps);
        }

        CodeSetComponentInfo.CodeSetComponent charData
            = CodeSetComponentInfo.JAVASOFT_DEFAULT_CODESETS.getCharComponent();

        param = props.getProperty(ORBConstants.CHAR_CODESETS);
        if (param != null) {
            charData = CodeSetComponentInfo.createFromString(param);

            if (ORBInitDebug)
                dprint("charData: " + charData);
        }

        CodeSetComponentInfo.CodeSetComponent wcharData
            = CodeSetComponentInfo.JAVASOFT_DEFAULT_CODESETS.getWCharComponent();

        param = props.getProperty(ORBConstants.WCHAR_CODESETS);
        if (param != null) {
            wcharData = CodeSetComponentInfo.createFromString(param);

            if (ORBInitDebug)
                dprint("wcharData: " + wcharData);
        }

        codesets = new CodeSetComponentInfo(charData, wcharData);

	param = props.getProperty( ORBConstants.ALLOW_LOCAL_OPTIMIZATION ) ;
	if (param != null) {
	    allowLocalOptimization = true ;
	}

        //...//
        param = props.getProperty( ORBConstants.SOCKET_FACTORY_CLASS_PROPERTY ) ;
        if (param != null) {
            try {
                Class socketFactoryClass = ORBClassLoader.loadClass(param);
                // For security reasons avoid creating an instance if
		// this socket factory class is not one that would fail
		// the class cast anyway.
                if (ORBSocketFactory.class.isAssignableFrom(socketFactoryClass)) {
		    socketFactory = (ORBSocketFactory)socketFactoryClass.newInstance();
		    if (ORBInitDebug) {
			dprint("setting socketFactory to: " + socketFactory);
		    }
                } else {
                    // throw some exception just to get into the outer catch clause
                    throw new ClassCastException();
                }
            } catch (Exception ex) {
                // ClassNotFoundException, IllegalAccessException, InstantiationException,
                // SecurityException or ClassCastException
                throw new INITIALIZE(
		    "can't instantiate custom socket factory: " + param);
            }
        } else {
            socketFactory = new DefaultSocketFactory();
        }

 	//
 	// ORBListenSocket
 	//
 	param = props.getProperty(ORBConstants.LISTEN_SOCKET_PROPERTY);
 	if (param != null) {
 	    StringTokenizer pairs = 
		new StringTokenizer(param, ",");
 	    while (pairs.hasMoreTokens()) {
 		String current = pairs.nextToken();
 		StringTokenizer pair = new StringTokenizer(current, ":");
 		String type = null;
 		int port = -1;
 		if  (pair.hasMoreTokens()) {
 		    type = pair.nextToken();
 		    if (pair.hasMoreTokens()) {
 			try {
 			    port = Integer.parseInt(pair.nextToken());
 			} catch (NumberFormatException e) {
 			    ;
 			}
 		    }
 		}
 		if (type == null || port == -1) {
 		    throw new INITIALIZE("Improper ORBListenSocket format: "
 					 + param);
 		}
 		userSpecifiedListenPorts.add(new UserSpecifiedListenPort(type, port));
 	    }
	    if (ORBInitDebug) {
		dprint("setting listen sockets: " + param);
	    }
	}

    }

/****************************************************************************
 * The following methods are getters and setters for ORB variables.
 ****************************************************************************/

    /** 
     * Get the name of the host running the initial services nameserver.
     * @return The name of the ORBInitialHost.
     */
    public String getORBInitialHost()
    {
        return ORBInitialHost;
    }

    /** 
     * Get the port of the initial services nameserver.
     */
    public int getORBInitialPort()
    {
        return ORBInitialPort;
    }

    public String getORBServerHost() {
        return ORBServerHost;
    }

    public int getORBServerPort() {
        return ORBServerPort;
    }

    /** 
     * Get the name of the host from which this applet was downloaded.
     * @return The name of the AppletHost.
     */
    public String getAppletHost()
    {
        return appletHost;
    }

    /** 
     * Get the codebase from which this applet was downloaded.
     */
    public URL getAppletCodeBase()
    {
        return appletCodeBase;
    }

    public class UserSpecifiedListenPort
    {
	private String type;
	private int    port;
	UserSpecifiedListenPort (String type, int port)
	{
	    this.type = type;
	    this.port = port;
	}
	public String getType  () { return type; }
	public int    getPort  () { return port; }
	public String toString () { return type + ":" + port; }
    }

    /**
     * Get the user-defined listen port types.
     */
    public Collection getUserSpecifiedListenPorts ()
    {
	return userSpecifiedListenPorts;
    }

    /**
     * Get the socket factory for this ORB.
     */
    public ORBSocketFactory getSocketFactory ()
    {
        return socketFactory;
    }

    /**
     * Get high water mark number beyond which 
     * ORB will not create any more new connections.
     */
    public int getHighWaterMark(){
        return highWaterMark;
    }

    /**
     * Get low water mark number above which 
     * ORB will start connection cleanup.
     */
    public int getLowWaterMark(){
        return lowWaterMark;
    }

    /**
     * Get number to reclaim which the
     * ORB uses to determine how many connections to 
     * try to clean up.
     */
    public int getNumberToReclaim(){
        return numberToReclaim;
    }

    /**
     * GIOP Related Constants.
     */
    public GIOPVersion getGIOPVersion() {
        return giopVersion;
    }
    public int getGIOPFragmentSize() { 
        return giopFragmentSize;
    }
    public int getGIOPBufferSize() {
        return giopBufferSize;
    }
    public int getGIOPBuffMgrStrategy(GIOPVersion gv) {
        if(gv!=null){
            if (gv.equals(GIOPVersion.V1_0)) return 0; //Always grow for 1.0
            if (gv.equals(GIOPVersion.V1_1)) return giop11BuffMgr;
            if (gv.equals(GIOPVersion.V1_2)) return giop12BuffMgr;
        }
        //If a "faulty" GIOPVersion is passed, it's going to return 0;
        return 0; 
    }

    /**
     * @return the GIOP Target Addressing preference of the ORB.
     * This ORB by default supports all addressing dispositions unless specified
     * otherwise via a java system property ORBConstants.GIOP_TARGET_ADDRESSING
     */
    public short getGIOPTargetAddressPreference() {
        return giopTargetAddressPreference;
    }
        
    /**
     * The default addressing disposition is KeyAddr.value
     */
    public short getGIOPAddressDisposition() {
        return giopAddressDisposition;    
    }
    
    public org.omg.CORBA.portable.OutputStream create_output_stream()
    {
        checkShutdownState();
        return new EncapsOutputStream(this);
    }

    /**
     * Get a Current pseudo-object.
     * The Current interface is used to manage thread-specific
     * information for use by the transactions, security and other
     * services. This method is deprecated, 
     * and replaced by ORB.resolve_initial_references("NameOfCurrentObject");
     *
     * @return          a Current pseudo-object.
     * @deprecated
     */
    public org.omg.CORBA.Current get_current()
    {
        checkShutdownState();
        /* _REVISIT_
           The implementation of get_current is not clear. How would
           ORB know whether the caller wants a Current for transactions
           or security ?? Or is it assumed that there is just one
           implementation for both ? If Current is thread-specific,
           then it should not be instantiated; so where does the
           ORB get a Current ? */
        throw new NO_IMPLEMENT();
    }

    /*
     **************************************************************************
     *  The following methods are hooks for Portable Interceptors.
     *  They have empty method bodies so that we may ship with or without
     *  PI support.  The actual implementations can be found in 
     *  Interceptors.PIORB.  Note that not all of these are used in this
     *  package, but this ORB class serves as a common place for javadoc
     *  comments and to enumerate all hooks.
     *************************************************************************/

    /*
     *****************
     * Client PI hooks
     *****************/
    
    /**
     * Called for pseudo-ops to temporarily disable portable interceptor
     * hooks for calls on this thread.  Keeps track of the number of
     * times this is called and increments the disabledCount.
     */
    protected void disableInterceptorsThisThread() {
        // Intentionally left empty.  See above note.
    }
    
    /**
     * Called for pseudo-ops to re-enable portable interceptor
     * hooks for calls on this thread.  Decrements the disabledCount.
     * If disabledCount is 0, interceptors are re-enabled.
     */
    protected void enableInterceptorsThisThread() {
        // Intentionally left empty.  See above note.
    }
    
    /**
     * Called when the send_request or send_poll portable interception point 
     * is to be invoked for all appropriate client-side request interceptors.
     *
     * @exception RemarhsalException - Thrown when this request needs to
     *     be retried.
     */
    protected void invokeClientPIStartingPoint() 
        throws RemarshalException 
    {
        // Intentionally left empty.  See above note.
    }
    
    /**
     * Called when the appropriate client ending interception point is
     * to be invoked for all apporpriate client-side request interceptors.
     *
     * @param replyStatus One of the constants in iiop.messages.ReplyMessage
     *     indicating which reply status to set.
     * @param exception The exception before ending interception points have
     *     been invoked, or null if no exception at the moment.
     * @return The exception to be thrown, after having gone through
     *     all ending points, or null if there is no exception to be
     *     thrown.  Note that this exception can be either the same or
     *     different from the exception set using setClientPIException.
     *     There are four possible return types: null (no exception), 
     *     SystemException, UserException, or RemarshalException.
     */
    protected Exception invokeClientPIEndingPoint(
        int replyStatus, Exception exception )
    {
        // Defualt implementation is just a simple pass-through of the
        // given exception.
        return exception;
    }
    
    /**
     * Invoked when a request is about to be created.  Must be called before
     * any of the setClientPI* methods so that a new info object can be
     * prepared for information collection.
     *
     * @param diiRequest True if this is to be a DII request, or false if it
     *     is a "normal" request.  In the DII case, initiateClientPIRequest
     *     is called twice and we need to ignore the second one.
     */
    protected void initiateClientPIRequest( boolean diiRequest ) {
        // Intentionally left empty.  See above note.
    }
    
    /**
     * Invoked when a request is about to be cleaned up.  Must be called
     * after ending points are called so that the info object on the stack
     * can be deinitialized and popped from the stack at the appropriate
     * time.
     */
    protected void cleanupClientPIRequest() {
        // Intentionally left empty.  See above note.
    }
    
    /**
     * Notifies PI of the information for client-side interceptors.  
     * PI will use this information as a source of information for the 
     * ClientRequestInfo object.
     */
    protected void setClientPIInfo( Connection connection,
				    ClientDelegate delegate, 
                                    IOR effectiveTarget,
                                    IIOPProfile profile, 
                                    int requestId,
                                    String opName,
                                    boolean isOneWay,
                                    ServiceContexts svc ) 
    {
        // Intentionally left empty.  See above note.
    }
    
    /**
     * Notifies PI of additional information for client-side interceptors.
     * PI will use this information as a source of information for the
     * ClientRequestInfo object.
     */
    protected void setClientPIInfo( ClientResponse response ) {
        // Intentionally left empty.  See above note.
    }

    /**
     * Notifies PI of additional information for client-side interceptors.
     * PI will use this information as a source of information for the
     * ClientRequestInfo object.
     */
    protected void setClientPIInfo( RequestImpl requestImpl ) {
        // Intentionally left empty.  See above note.
    }

    /**
     * Overridden from corba.ORB.
     * See description in corba.ORB.
     */
    protected void sendCancelRequestIfFinalFragmentNotSent() {
        // Intentionally left empty.  See above note.
    }
    
    /*
     *****************
     * Server PI hooks
     *****************/
    
    /**
     * Called when the appropriate server starting interception point is
     * to be invoked for all appropriate server-side request interceptors.
     *
     * @throws InternalRuntimeForwardRequest Thrown if an interceptor raises
     *     ForwardRequest.  This is an unchecked exception so that we need 
     *     not modify the entire execution path to declare throwing 
     *     ForwardRequest.
     */
    protected void invokeServerPIStartingPoint() 
        throws InternalRuntimeForwardRequest
    {
        // Intentionally left empty.  See above note.
    }
    
    /**
     * Called when the appropriate server intermediate interception point is
     * to be invoked for all appropriate server-side request interceptors.
     *
     * @throws InternalRuntimeForwardRequest Thrown if an interceptor raises
     *     ForwardRequest.  This is an unchecked exception so that we need 
     *     not modify the entire execution path to declare throwing 
     *     ForwardRequest.
     */
    protected void invokeServerPIIntermediatePoint() 
        throws InternalRuntimeForwardRequest
    {
        // Intentionally left empty.  See above note.
    }
    
    /**
     * Called when the appropriate server ending interception point is
     * to be invoked for all appropriate server-side request interceptors.
     *
     * @param replyMessage The iiop.messages.ReplyMessage containing the
     *     reply status.  The ior in this object may be modified by the PIORB.
     * @throws InternalRuntimeForwardRequest Thrown if an interceptor raises
     *     ForwardRequest.  This is an unchecked exception so that we need 
     *     not modify the entire execution path to declare throwing 
     *     ForwardRequest.
     */
    protected void invokeServerPIEndingPoint( ReplyMessage replyMessage )
        throws InternalRuntimeForwardRequest
    {
        // Intentionally left empty.  See above note.
    }
    
    /**
     * Notifies PI to start a new server request and set initial
     * information for server-side interceptors.
     * PI will use this information as a source of information for the
     * ServerRequestInfo object.  poaimpl is declared as an Object so that
     * we need not introduce a dependency on the POA package.
     */
    protected void initializeServerPIInfo( ServerRequest request, 
	java.lang.Object poaimpl, byte[] objectId, byte[] adapterId ) 
    {
        // Intentionally left empty.  See above note.
    }
    
    /**
     * Notifies PI of additional information reqired for ServerRequestInfo.
     *
     * @param servant The servant.  This is java.lang.Object because in the
     *     POA case, this will be a org.omg.PortableServer.Servant whereas
     *     in the ServerDelegate case this will be an ObjectImpl.
     * @param targetMostDerivedInterface.  The most derived interface.  This
     *     is passed in instead of calculated when needed because it requires
     *     extra information in the POA case that we didn't want to bother
     *     creating extra methods for to pass in.
     */
    protected void setServerPIInfo( java.lang.Object servant, 
				    String targetMostDerivedInterface ) 
    {
        // Intentionally left empty.  See above note.
    }
    
    /**
     * Notifies PI of additional information required for ServerRequestInfo.
     */
    protected void setServerPIInfo( Exception exception ) {
        // Intentionally left empty.  See above note.
    }

    /**
     * Notifies PI of additional information for server-side interceptors.
     * PI will use this information as a source of information for the
     * ServerRequestInfo object.  These are the arguments for a DSI request.
     */
    protected void setServerPIInfo( NVList arguments ) {
        // Intentionally left empty.  See above note.
    }

    /**
     * Notifies PI of additional information for server-side interceptors.
     * PI will use this information as a source of information for the
     * ServerRequestInfo object.  This is the exception of a DSI request.
     */
    protected void setServerPIExceptionInfo( Any exception ) {
        // Intentionally left empty.  See above note.
    }

    /**
     * Notifies PI of additional information for server-side interceptors.
     * PI will use this information as a source of information for the
     * ServerRequestInfo object.  This is the result of a DSI request.
     */
    protected void setServerPIInfo( Any result ) {
        // Intentionally left empty.  See above note.
    }

    /**
     * Invoked when a request is about to be cleaned up.  Must be called
     * after ending points are called so that the info object on the stack
     * can be deinitialized and popped from the stack at the appropriate
     * time.
     */
    protected void cleanupServerPIRequest() {
        // Intentionally left empty.  See above note.
    }

    /*************************************************************************
     * The following methods deal with creation of various types of lists.
     *************************************************************************/

    /**
     * Create an NVList
     *
     * @param count	size of list to create
     * @result		NVList created
     *
     * @see NVList
     */
    public NVList create_list(int count)
    {
        checkShutdownState();
        return new NVListImpl(this, count);
    }

    /**
     * Create an NVList corresponding to an OperationDef
     *
     * @param oper	operation def to use to create list
     * @result		NVList created
     *
     * @see NVList
     */
    public NVList create_operation_list(org.omg.CORBA.Object oper)
    {
        checkShutdownState();
        throw new NO_IMPLEMENT();
    }

    /**
     * Create a NamedValue
     *
     * @result		NamedValue created
     */
    public NamedValue create_named_value(String s, Any any, int flags)
    {
        checkShutdownState();
	return new NamedValueImpl(this, s, any, flags);
    }

    /**
     * Create an ExceptionList
     *
     * @result		ExceptionList created
     */
    public org.omg.CORBA.ExceptionList create_exception_list()
    {
        checkShutdownState();
	return new ExceptionListImpl();
    }

    /**
     * Create a ContextList
     *
     * @result		ContextList created
     */
    public org.omg.CORBA.ContextList create_context_list()
    {
        checkShutdownState();
        return new ContextListImpl(this);
    }

    /**
     * Get the default Context object
     *
     * @result		the default Context object
     */
    public org.omg.CORBA.Context get_default_context()
    {
        checkShutdownState();
        throw new NO_IMPLEMENT();
    }

    /**
     * Create an Environment
     *
     * @result		Environment created
     */
    public org.omg.CORBA.Environment create_environment()
    {
        checkShutdownState();
        return new EnvironmentImpl();
    }

/****************************************************************************
 * The following methods deal with multiple/deferred DII invocations.
 ****************************************************************************/

    public void send_multiple_requests_oneway(Request[] req)
    {
        checkShutdownState();

        // Invoke the send_oneway on each new Request
        for (int i = 0; i < req.length; i++) {
            req[i].send_oneway();
        }
    }
 
    /**
     * Send multiple dynamic requests asynchronously.
     *
     * @param req         an array of request objects.
     */
    public void send_multiple_requests_deferred(Request[] req)
    {
        checkShutdownState();

        // add the new Requests to pending dynamic Requests
        for (int i = 0; i < req.length; i++) {
            _dynamicRequests.addElement(req[i]);
        }
 
        // Invoke the send_deferred on each new Request
        for (int i = 0; i < req.length; i++) {
	    AsynchInvoke invokeObject = new AsynchInvoke(
		this, (com.sun.corba.se.internal.corba.RequestImpl)req[i], true);
	    new Thread(invokeObject).start();
        }
    }

    /**
     * Find out if any of the deferred invocations have a response yet. 
     */
    public boolean poll_next_response()
    {
        checkShutdownState();

        Request currRequest;
 
        // poll on each pending request
        Enumeration ve = _dynamicRequests.elements();
        while (ve.hasMoreElements() == true) {
            currRequest = (Request)ve.nextElement();
            if (currRequest.poll_response() == true) {
                return true;
            }
        }
        return false;
    }
 
    /**
     * Get the next request that has gotten a response.
     *
     * @result            the next request ready with a response.
     */
    public org.omg.CORBA.Request get_next_response()
        throws org.omg.CORBA.WrongTransaction
    {
        checkShutdownState();

	while (true) {
	    // check if there already is a response
	    synchronized ( _dynamicRequests ) {
		Enumeration elems = _dynamicRequests.elements();
		while ( elems.hasMoreElements() ) {
		    Request currRequest = (Request)elems.nextElement();
		    if ( currRequest.poll_response() ) {
			// get the response for this successfully polled Request
			currRequest.get_response();
			_dynamicRequests.removeElement(currRequest);
			return currRequest;
		    }
		}
	    }

	    // wait for a response
	    synchronized(this._svResponseReceived) {
	        this._svResponseReceived.reset();
		while (this._svResponseReceived.value() == false) {
    		    try {
		        this._svResponseReceived.wait();
    		    } catch(java.lang.InterruptedException ex) {}
		}
		// reinitialize the response flag
		this._svResponseReceived.reset();
	    }
	}
    }

    /** 
     * Notify response to ORB for get_next_response
     */
    synchronized void notifyResponse() {
        this._svResponseReceived.set();
        this._svResponseReceived.notify();
    }

/****************************************************************************
 * The following methods deal with stringifying/destringifying
 * and connecting/disconnecting object references.
 ****************************************************************************/

    /**
     * Convert an object ref to a string.
     * @param obj The object to stringify.
     * @return A stringified object reference.
     */
    public String object_to_string(org.omg.CORBA.Object obj) 
    {
        checkShutdownState();

        // Handle the null objref case
        if (obj == null) 
	    return IOR.NULL.stringify(this);

	if (! (obj instanceof ObjectImpl)) {
	    throw new MARSHAL("Argument is not an ObjectImpl.",
			      MinorCodes.NOT_AN_OBJECT_IMPL,
			      CompletionStatus.COMPLETED_NO);
	}

	// Try and get the objref's delegate if it exists
        ClientSubcontract rep=null;
        ObjectImpl oi = (ObjectImpl)obj;
        try {
	    // throws BAD_OPERATION if no delegate
            rep = (ClientSubcontract)oi._get_delegate();  
        } catch ( Exception ex ) {}

        if ( rep == null ) {
            // No delegate: servant was not connected to ORB
	    // This connect only occurs in non-POA cases.
	    connect(obj);
	    rep = (ClientSubcontract)oi._get_delegate();
        }

        return rep.marshal().stringify();
    }

    /**
     * Convert a stringified object reference to the object it represents.
     * @param str The stringified object reference.
     * @return The unstringified object reference.
     */
    public org.omg.CORBA.Object string_to_object(String str) 
    {
        checkShutdownState();

        if ( str == null )
            throw new org.omg.CORBA.BAD_PARAM(MinorCodes.NULL_PARAM, 
					      CompletionStatus.COMPLETED_NO);

	// If the String starts with corbaloc: resolve it using the
	// URL resolution technique
	if( str.startsWith( "corbaloc:" ) ) {
	    String sresult = null;
	    CorbaLoc theCorbaLocObject = null;

	    theCorbaLocObject = initialNamingClient.checkcorbalocGrammer( str ); 
	    if( theCorbaLocObject == null ) 
		// This is thrown because the corbaloc: URL is not correct
		throw new org.omg.CORBA.BAD_PARAM(MinorCodes.BAD_STRINGIFIED_IOR, 
		    CompletionStatus.COMPLETED_NO);
            // If the choosen protocol option is not rir: then just create the
            // IOR with the given corbaloc: info
            if( theCorbaLocObject.getRIRFlag() == false ) {
                // HostInfo will never be null if CORBALOC object is not null
                // and RIRFlag is false. Hence no need to do an extra check for
                // hostInfo == null.
                java.util.Vector hostInfoList = theCorbaLocObject.getHostInfo();
                if( ( hostInfoList != null ) && (hostInfoList.size() > 0 ) ) {
                    com.sun.corba.se.internal.ior.IOR ior = 
                        new com.sun.corba.se.internal.ior.IOR( );
                    ObjectKey okey = ObjectKeyFactory.get().create( this,
                        theCorbaLocObject.getKeyString().getBytes() );
                    ObjectId id = okey.getId();
 
                    for( Enumeration e = hostInfoList.elements( );
                         e.hasMoreElements(); ) 
                    {
                        HostInfo hostInfo = (HostInfo) e.nextElement();
                        IIOPAddress addr = new IIOPAddressImpl( 
                            hostInfo.getHostName(), hostInfo.getPortNumber());
                        
                        IIOPProfileTemplate iiopt = 
                            new StandardIIOPProfileTemplate( addr, 
                            hostInfo.getMajorNumber(), 
                            hostInfo.getMinorNumber(),
                            okey.getTemplate(), null, this );
                        com.sun.corba.se.internal.ior.IIOPProfile profile = 
                            new com.sun.corba.se.internal.ior.IIOPProfile( id,
                                iiopt );     
                        ior.add( profile );
                    }
                    ior.makeImmutable();
                    String iorstr = ior.stringify( this );
	            return IOR.getIORFromString( this, iorstr ) ;
                }
            }
	    sresult = initialNamingClient.resolveCorbaloc( theCorbaLocObject );
	    if( sresult == null ) 
		throw new INV_OBJREF( MinorCodes.BAD_CORBALOC_STRING,
		    CompletionStatus.COMPLETED_NO ) ;

	    return IOR.getIORFromString( this, sresult ) ;
	} else if( str.startsWith( "corbaname:" ) ) {
	    // If it String starts with corbaname: resolve it using the
	    // URL resolution technique
	    CorbaName theCorbaNameObject = null;

	    theCorbaNameObject = initialNamingClient.checkcorbanameGrammer( str ); 

	    if( theCorbaNameObject == null ) 
		// This is thrown because the corbaname: URL is not correct
		throw new org.omg.CORBA.BAD_PARAM(MinorCodes.BAD_STRINGIFIED_IOR, 
	            CompletionStatus.COMPLETED_NO);

	    return initialNamingClient.resolveCorbaname( theCorbaNameObject );
	} else if (str.startsWith( IOR.STRINGIFY_PREFIX ) ) {
	    // else it starts with IOR, So we need to do the regular IOR:
	    // resolution.
	    return IOR.getIORFromString( this, str ) ;
	} else {
	    // Not a legal string: raise an exception
            throw new org.omg.CORBA.DATA_CONVERSION( 
		MinorCodes.BAD_STRINGIFIED_IOR, CompletionStatus.COMPLETED_NO );
	}
    }

    /** This is the implementation of the public API used to connect
     *  a servant-skeleton to the ORB. 
     */
    public void connect(org.omg.CORBA.Object servant)
    {
        checkShutdownState();

        int subContractIndex = SubcontractList.defaultSubcontract;
        
        try {
            ServerSubcontract s
		= getSubcontractRegistry().getServerSubcontract(subContractIndex);

            s.createObjref((byte[]) null, servant);
        }
        catch ( Exception ex ) {
	    if (subcontractDebugFlag) {
		dprint( "Error in createObjref: " + ex ) ;
		ex.printStackTrace() ;
	    }

            throw new OBJ_ADAPTER(MinorCodes.ORB_CONNECT_ERROR, 
				  CompletionStatus.COMPLETED_NO);
	}
    }

    public void disconnect(org.omg.CORBA.Object obj)
    {
        checkShutdownState();

        try {
            // Get the delegate, then ior, then server-subcontract, 
	    // then tell it to delete the servant from its "servant manager".
            ObjectImpl oi = (ObjectImpl)obj;
            ClientSubcontract rep = (ClientSubcontract)oi._get_delegate();
            IOR ior = rep.marshal();
            ObjectKeyTemplate temp = ior.getProfile().getTemplate().getObjectKeyTemplate();
	    int scid = temp.getSubcontractId() ;
            ServerSubcontract sc = getSubcontractRegistry().getServerSubcontract(scid);
	    sc.destroyObjref(obj);
        }
        catch ( Exception ex ) {
            throw new OBJ_ADAPTER(MinorCodes.ORB_CONNECT_ERROR, 
		CompletionStatus.COMPLETED_MAYBE);
        }
    }

    //d11638: pure java orb support, moved this method from FVDCodeBaseImpl
    // Note that we connect this if we have not already done so.
    public IOR getServantIOR(){

        if (ior != null) // i.e. We are already connected to it
            return ior;

	// backward compatability 4365188
        CodeBase cb;

        ValueHandler vh = ORBUtility.createValueHandler(this);

        cb = (CodeBase)vh.getRunTimeCodeBase();

        connect(cb);

        ClientSubcontract rep=null;
        ObjectImpl oi = (ObjectImpl)cb;
        rep = (ClientSubcontract)oi._get_delegate();
        ior = rep.marshal();
        return ior;

    }

/****************************************************************************
 * The following methods deal with creation of TypeCodes and Any, etc.
 ****************************************************************************/

    /**
     * Get the TypeCode for a primitive type.
     *
     * @param tcKind	the integer kind for the primitive type
     * @return		the requested TypeCode
     */
    public TypeCode get_primitive_tc(TCKind tcKind)
    {
        checkShutdownState();
        // _REVISIT_ if this returns a null, throw an exception perhaps?
        return TypeCodeImpl.get_primitive_tc(tcKind);
    }

    /**
     * Create a TypeCode for a structure.
     *
     * @param id		the logical id for the typecode.
     * @param name	the name for the typecode.
     * @param members	an array describing the members of the TypeCode.
     * @return		the requested TypeCode.
     */
    public TypeCode create_struct_tc(String id,
				     String name,
				     StructMember[] members)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_struct, id, name, members);
    }
    
    /**
     * Create a TypeCode for a union.
     *
     * @param id		the logical id for the typecode.
     * @param name	the name for the typecode.
     * @param discriminator_type
     *			the type of the union discriminator.
     * @param members	an array describing the members of the TypeCode.
     * @return		the requested TypeCode.
     */
    public TypeCode create_union_tc(String id,
				    String name,
				    TypeCode discriminator_type,
				    UnionMember[] members)
    {
        checkShutdownState();
        return new TypeCodeImpl(this,
				TCKind._tk_union, 
				id, 
				name, 
				discriminator_type, 
				members);
    }
    
    /**
     * Create a TypeCode for an enum.
     *
     * @param id		the logical id for the typecode.
     * @param name	the name for the typecode.
     * @param members	an array describing the members of the TypeCode.
     * @return		the requested TypeCode.
     */
    public TypeCode create_enum_tc(String id,
				   String name,
				   String[] members)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_enum, id, name, members);
    }
    
    /**
     * Create a TypeCode for an alias.
     *
     * @param id		the logical id for the typecode.
     * @param name	the name for the typecode.
     * @param original_type
     * 			the type this is an alias for.
     * @return		the requested TypeCode.
     */
    public TypeCode create_alias_tc(String id,
				    String name,
				    TypeCode original_type)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_alias, id, name, original_type);
    }
    
    /**
     * Create a TypeCode for an exception.
     *
     * @param id		the logical id for the typecode.
     * @param name	the name for the typecode.
     * @param members	an array describing the members of the TypeCode.
     * @return		the requested TypeCode.
     */
    public TypeCode create_exception_tc(String id,
					String name,
					StructMember[] members)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_except, id, name, members);
    }
    
    /**
     * Create a TypeCode for an interface.
     *
     * @param id		the logical id for the typecode.
     * @param name	the name for the typecode.
     * @return		the requested TypeCode.
     */
    public TypeCode create_interface_tc(String id,
					String name)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_objref, id, name);
    }
    
    /**
     * Create a TypeCode for a string.
     *
     * @param bound	the bound for the string.
     * @return		the requested TypeCode.
     */
    public TypeCode create_string_tc(int bound)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_string, bound);
    }
    
    /**
     * Create a TypeCode for a wide string.
     *
     * @param bound	the bound for the string.
     * @return		the requested TypeCode.
     */
    public TypeCode create_wstring_tc(int bound)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_wstring, bound);
    }
    
    /**
     * Create a TypeCode for a sequence.
     *
     * @param bound	the bound for the sequence.
     * @param element_type
     *			the type of elements of the sequence.
     * @return		the requested TypeCode.
     */
    public TypeCode create_sequence_tc(int bound,
				       TypeCode element_type)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_sequence, bound, element_type);
    }
    
    
    /**
     * Create a recursive TypeCode in a sequence.
     *
     * @param bound	the bound for the sequence.
     * @param offset	the index to the enclosing TypeCode that is
     *			being referenced.  
     * @return		the requested TypeCode.
     */
    public TypeCode create_recursive_sequence_tc(int bound,
						 int offset)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_sequence, bound, offset);
    }
    
    
    /**
     * Create a TypeCode for an array.
     *
     * @param length	the length of the array.
     * @param element_type
     *			the type of elements of the array.
     * @return		the requested TypeCode.
     */
    public TypeCode create_array_tc(int length,
				    TypeCode element_type)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_array, length, element_type);
    }


    public org.omg.CORBA.TypeCode create_native_tc(String id,
                                                   String name)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_native, id, name);
    }

    public org.omg.CORBA.TypeCode create_abstract_interface_tc(
							       String id,
							       String name)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_abstract_interface, id, name);
    }

    public org.omg.CORBA.TypeCode create_fixed_tc(short digits, short scale)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_fixed, digits, scale);
    }

    public org.omg.CORBA.TypeCode create_value_tc(String id,
                                                  String name,
                                                  short type_modifier,
                                                  TypeCode concrete_base,
                                                  ValueMember[] members)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_value, id, name,
                                type_modifier, concrete_base, members);
    }

    public org.omg.CORBA.TypeCode create_recursive_tc(String id) {
        checkShutdownState();
        return new TypeCodeImpl(this, id);
    }

    public org.omg.CORBA.TypeCode create_value_box_tc(String id,
						      String name,
						      TypeCode boxed_type)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_value_box, id, name, boxed_type);
    }

    /**
     * Create a new Any
     *
     * @return		the new Any created.
     */
    public Any create_any()
    {
        checkShutdownState();
        return new AnyImpl(this);
    }

    // TypeCodeFactory interface methods.
    // Keeping track of type codes by repository id.

    public void setTypeCode(String id, TypeCodeImpl tci) {
        if (typeCodeMap == null)
            typeCodeMap = Collections.synchronizedMap(new WeakHashMap(64));
        // Store only meaningfull ids and store only one TypeCode per id.
        // There can be many TypeCodes created programmatically by the user with the same id.
        if (id != null && id.length() != 0 && ! typeCodeMap.containsKey(id))
            typeCodeMap.put(id, tci);
    }

    public TypeCodeImpl getTypeCode(String id) {
        if (typeCodeMap == null)
            return null;
        return (TypeCodeImpl)typeCodeMap.get(id);
    }

    // Keeping a cache of TypeCodes associated with the class
    // they got created from in Util.writeAny().

    void setTypeCodeForClass(Class c, TypeCodeImpl tci) {
        if (typeCodeForClassMap == null)
            typeCodeForClassMap = Collections.synchronizedMap(new WeakHashMap(64));
        // Store only one TypeCode per class.
        if ( ! typeCodeForClassMap.containsKey(c))
            typeCodeForClassMap.put(c, tci);
    }

    TypeCodeImpl getTypeCodeForClass(Class c) {
        if (typeCodeForClassMap == null)
            return null;
        return (TypeCodeImpl)typeCodeForClassMap.get(c);
    }

/****************************************************************************
 * The following methods deal with listing and resolving the initial
 * (bootstrap) object references such as "NameService".
 ****************************************************************************/

    /**
     * Get a list of the initially available CORBA services. 
     * This does not work unless an ORBInitialHost is specified during initialization
     * (or unless there is an ORB running on the AppletHost) since the localhostname 
     * is inaccessible to applets. If a service properties URL was specified,
     * then it is used, otherwise the bootstrapping protocol is used.
     * @return A list of the initial services available.
     */
    public String[] list_initial_services() 
    {
        checkShutdownState();

        String[] res1 = initialNamingClient.list_initial_services();
	String[] res2 = listInitialReferences() ;
	return ORBUtility.concatenateStringArrays( res1, res2 ) ;
    }

    /**
     * Resolve the stringified reference of one of the initially
     * available CORBA services.
     * @param identifier The stringified object reference of the
     * desired service.
     * @return An object reference for the desired service.
     * @exception InvalidName The supplied identifier is not associated
     * with a known service.
     * @exception SystemException One of a fixed set of Corba system exceptions.
     */
    public org.omg.CORBA.Object resolve_initial_references(
	String identifier) throws InvalidName
    {
        checkShutdownState();

        org.omg.CORBA.Object result = resolveInitialReference( identifier ) ;
	if (result == null)
	    result = initialNamingClient.resolve_initial_references( 
		identifier ) ;
	return result ;
    }

    /**
     * If this operation is called with an id, <code>"Y"</code>, and an 
     * object, <code>YY</code>, then a subsequent call to        
     * <code>ORB.resolve_initial_references( "Y" )</code> will             
     * return object <code>YY</code>.
     *        
     * @param id The ID by which the initial reference will be known.
     * @param obj The initial reference itself.
     * @throws InvalidName if this operation is called with an empty string id
     *     or this operation is called with an id that is already registered,
     *     including the default names defined by OMG.
     * @throws BAD_PARAM if the obj parameter is null.
     */
    public void register_initial_reference( 
	String id, org.omg.CORBA.Object obj ) throws InvalidName
    {
	if ((id == null) || (id.length() == 0))
	    throw new InvalidName() ;

	java.lang.Object obj2 = initialReferenceTable.get( id ) ;
	if (obj2 != null)
	    throw new InvalidName(id + " already registered") ;

	registerInitialReference( id, new Constant( obj )) ;
  
        // This extra step of registering the ServerSubcontract is to make the
        // this object (Service) available to INS clients.
        // Note: LocalObjects will not be available through INS URL's.
        if( obj instanceof ObjectImpl ) {
            ObjectImpl oi = (ObjectImpl)obj;
            ClientSubcontract rep = (ClientSubcontract)oi._get_delegate();
            IOR ior = rep.marshal();
            ObjectKeyTemplate temp = 
            ior.getProfile().getTemplate().getObjectKeyTemplate();
            int scid = temp.getSubcontractId() ;
            ServerSubcontract sc = 
                getSubcontractRegistry().getServerSubcontract(scid);
            INSObjectKeyEntry entry = new INSObjectKeyEntry( ior, sc );
            INSObjectKeyMap.getInstance().setEntry( id, entry );
        }
    }

/***************************************************
 * Methods used to support local initial references
 ***************************************************/

    protected void registerInitialReference( String id, Closure closure ) 
    {
	initialReferenceTable.put( id, closure ) ;
    }

    protected org.omg.CORBA.Object resolveInitialReference( String id )
    {
	java.lang.Object obj = initialReferenceTable.get( id ) ;
	if (obj == null)
	    return null ;

	Closure closure = (Closure)obj ;
	java.lang.Object result = closure.evaluate() ;
	return (org.omg.CORBA.Object)result ;
    }

    protected String[] listInitialReferences() 
    {
	java.lang.Object[] arr = initialReferenceTable.keySet().toArray() ;
	String[] result = new String[arr.length] ;
	for (int ctr=0; ctr< result.length; ctr++)
	    result[ctr] = (String)(arr[ctr]) ;
	return result ;	
    }

/****************************************************************************
 * The following methods (introduced in POA / CORBA2.1) deal with
 * shutdown / single threading.
 ****************************************************************************/

    public void run() {
        checkShutdownState();
        synchronized (runObj) {
            try {
                runObj.wait();
            } catch ( InterruptedException ex ) {}
        }
    }

    public void shutdown(boolean wait_for_completion) {
        // Avoid more than one thread performing shutdown at a time.
        synchronized (shutdownObj) {
            checkShutdownState();
            // This is to avoid deadlock
            if (wait_for_completion && isProcessingInvocation.get() == Boolean.TRUE) {
                throw new BAD_INV_ORDER(
                    "Request to shutdown ORB with waiting for completion while servicing a request",
                    MinorCodes.SHUTDOWN_WAIT_FOR_COMPLETION_DEADLOCK,
                    CompletionStatus.COMPLETED_NO);
            }
            status = STATUS_SHUTTING_DOWN;
            shutdownServants(wait_for_completion);
            if (wait_for_completion) {
                synchronized ( waitForCompletionObj ) {
                    while (numInvocations > 0) {
                        try {
                            waitForCompletionObj.wait();
                        } catch (InterruptedException ex) {}
                    }
                }
            }
            synchronized ( runObj ) {
                runObj.notifyAll();
            }
            status = STATUS_SHUTDOWN;
        }
    }

    // specific shutdown work for each subclass goes here
    protected abstract void shutdownServants(boolean wait_for_completion);
    protected abstract void destroyConnections();

    protected void checkShutdownState() {
        if (status == STATUS_DESTROYED) {
            throw new OBJECT_NOT_EXIST("ORB has been destroyed");
        }
        if (status == STATUS_SHUTDOWN) {
            throw new BAD_INV_ORDER("ORB has been shut down",
                MinorCodes.BAD_OPERATION_AFTER_SHUTDOWN,
                CompletionStatus.COMPLETED_NO);
        }
    }

    protected void startingDispatch() {
        synchronized (invocationObj) {
            isProcessingInvocation.set(Boolean.TRUE);
            numInvocations++;
        }
    }

    protected void finishedDispatch() {
        synchronized (invocationObj) {
            numInvocations--;
            isProcessingInvocation.set(Boolean.FALSE);
            if (numInvocations == 0) {
                synchronized (waitForCompletionObj) {
                    waitForCompletionObj.notifyAll();
                }
            } else if (numInvocations < 0) {
                throw new INTERNAL("Invocation tracking information out of sync.", 0, CompletionStatus.COMPLETED_YES );
            }
        }
    }

    /**
     *	formal/99-10-07 p 159: "If destroy is called on an ORB that has
     *	not been shut down, it will start the shutdown process and block until
     *	the ORB has shut down before it destroys the ORB."
     */
    public void destroy() {
        if (status == STATUS_OPERATING) {
            shutdown(true);
        }
        destroyConnections();
        status = STATUS_DESTROYED;
    }

    public boolean work_pending()
    {
        checkShutdownState();
	throw new NO_IMPLEMENT();
    }

    public void perform_work()
    {
        checkShutdownState();
	throw new NO_IMPLEMENT();
    }

    /**
     * Registers a value factory for a particular repository ID.
     *
     * @param repositoryID the repository ID.
     * @param factory the factory.
     * @return the previously registered factory for the given repository ID, or null
     * if no such factory was previously registered.
     * @exception org.omg.CORBA.BAD_PARAM if the registration fails.
     **/
    public ValueFactory register_value_factory(String repositoryID, ValueFactory factory) {
        checkShutdownState();

        if ((repositoryID == null) || (factory == null))
            throw new BAD_PARAM(MinorCodes.UNABLE_REGISTER_VALUE_FACTORY,
                                CompletionStatus.COMPLETED_NO);
        return (ValueFactory)valueFactoryCache.put(repositoryID, factory);
    }

    /**
     * Unregisters a value factory for a particular repository ID.
     *
     * @param repositoryID the repository ID.
     **/
    public void unregister_value_factory(String repositoryID) {
        checkShutdownState();

        if (valueFactoryCache.remove(repositoryID) == null)
            throw new BAD_PARAM(MinorCodes.NULL_PARAM, CompletionStatus.COMPLETED_NO);
    }
    
    /**
     * Finds and returns a value factory for the given repository ID.
     * The value factory returned was previously registered by a call to
     * {@link #register_value_factory} or is the default factory.
     *
     * @param repositoryID the repository ID.
     * @return the value factory.
     * @exception org.omg.CORBA.BAD_PARAM if unable to locate a factory.
     **/
    public ValueFactory lookup_value_factory(String repositoryID) {
        checkShutdownState();

        ValueFactory factory = (ValueFactory)valueFactoryCache.get(repositoryID);
	if (factory != null) {
	    return factory;
	} else {
	    try {
		return Utility.getFactory(null, null, null, repositoryID);
	    } catch(org.omg.CORBA.MARSHAL ex) {
		throw new org.omg.CORBA.BAD_PARAM(ex.getMessage(),
                                    MinorCodes. UNABLE_FIND_VALUE_FACTORY,
                                    CompletionStatus.COMPLETED_NO);
	    }
	}
    }
} // Class ORB

////////////////////////////////////////////////////////////////////////
/// Helper class for a Synchronization Variable

class SynchVariable {
 
    // Synchronization Variable
    public boolean _flag;
 
    // Constructor
    SynchVariable() {
        _flag = false;
    }
 
    // set Flag to true
    public synchronized void set() {
        _flag = true;
    }

        // get value
    public synchronized boolean value() {
        return _flag;
    }

    // reset Flag to true
    public synchronized void reset() {
        _flag = false;
    }
}


// Used to collect properties from various sources.
abstract class GetPropertyCallback
{
    abstract public String get(String name);
}
