/*
 * @(#)BootstrapServer.java	1.36 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.CosNaming;

import java.lang.Thread;

import java.util.Enumeration;
import java.util.Properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.omg.CORBA.*;
import com.sun.corba.se.internal.iiop.*;
import com.sun.corba.se.internal.core.*;
import com.sun.corba.se.internal.orbutil.CorbaResourceUtil;

/**
 * Class BootstrapServer is the main loop for the bootstrap server
 * implementation, listening for new connections and spawning threads
 * to handle requests on each new connection.
 * It uses BootstrapRequestHandler objects to actually
 * serve requests on the connections. The supported services is a
 * special properties object that ensures all accesses are synchronized;
 * if a file is supplied then the properties are loaded from and stored to
 * that file. The properties object is shared among all threads.
 * @see BootstrapServiceProperties
 * @see BootstrapRequestHandler
 */
public class BootstrapServer
{
    /**
     * Constructs a new BootstrapServer object and the
     * BootstrapServiceProperties object shared among all threads.
     * @param port the port on which the server will listen.
     * @parma file a file containing the supported properties
     * @param svcs a Properties defining the set of supported services.
     */
    public BootstrapServer(com.sun.corba.se.internal.iiop.ORB orb, int port,
			   File file, Properties svcs)
    {
	listenerPort = port;
	this.orb = orb;

	// Create a properties object with the file and properties
	supportedServices =  new BootstrapServiceProperties(file,svcs);

	// Create the server SC
	serverSC = new BootstrapRequestHandler(orb,supportedServices);

	// Register it with the orb for big and little indians.. eh, endians.
	orb.getSubcontractRegistry().registerBootstrapServer(serverSC);
    }

    /**
     * Start the BootstrapServer. It creates a listener thread on
     * the specified listenerport which will serve requests.
     */
    public void start() {
	// Create a listener thread on the port - this will process requests
	orb.getServerGIOP().getBootstrapEndpoint(listenerPort);
    }

    /**
     * Adds a service to the supported set of services. Delegates to
     * the shared BootstrapServiceProperties object.
     * @param key the key under which the service is known.
     * @param val the stringified object reference for the service.
     * @param save a boolean which is true if the key,value pair should be saved.
     * @return any previously bound value under the key.
     */
    public String addService(String key, String val, boolean save) {
	// Don't hesitate - delegate.
	return supportedServices.put(key,val,save);
    }

    /**
     * Accessor method to get the Initial Service based on the Key.
     * @param key the service name.
     *
     * @return stringified IOR previously registered or null if none registered
     */
    public String getService( String key ) {
        return supportedServices.get( key );
    }

    /**
     * Main startup routine in case the bootstrap server is run standalone.
     * It first determines the port on which to listen, checks that the
     * specified file is available, and then creates the BootstrapServer object
     * that will service the requests.
     * @param args the command-line arguments to the main program.
     */
    public static final void main(String[] args)
    {
	String propertiesFilename = null;

	// Get an orb object. Narrow to specific ORB so we can use Wait().
	Properties props = new Properties() ;
	props.put( "org.omg.CORBA.ORBClass", "com.sun.corba.se.internal.iiop.ORB" ) ;
	com.sun.corba.se.internal.iiop.ORB orb = (com.sun.corba.se.internal.iiop.ORB)
	    org.omg.CORBA.ORB.init(args,props);

	// Determine the initial bootstrap port to use
	int initialPort = 900; // by convention
	try {
	    // Try environment
	    String ips = System.getProperty("org.omg.CORBA.ORBInitialPort");
	    if (ips != null && ips.length() > 0)
		initialPort = java.lang.Integer.parseInt(ips);
	} catch (java.lang.NumberFormatException e) {
	    // do nothing
	}
	// Process arguments
	for (int i=0;i<args.length;i++) {
	    // Look for the filename
	    if (args[i].equals("-InitialServicesFile") && i < args.length -1) {
		propertiesFilename = args[i+1];
	    }
	    // Was the initial port specified? If so, override
	    if (args[i].equals("-ORBInitialPort") && i < args.length-1) {
		initialPort = java.lang.Integer.parseInt(args[i+1]);
	    }
	}

	if (propertiesFilename == null) {
	    System.out.println(CorbaResourceUtil.getText("bootstrap.usage", "BootstrapServer"));
	    return;
	}

	// Create a file
	File file = new File(propertiesFilename);

	// Verify that if it exists, it is readable
	if (file.exists() == true && file.canRead() == false) {
	    System.err.println(CorbaResourceUtil.getText("bootstrap.filenotreadable", file.getAbsolutePath()));
	    return;
	}

	// Success: start up
	System.out.println(CorbaResourceUtil.getText("bootstrap.success", Integer.toString(initialPort), file.getAbsolutePath()));

	// We are ready: start the server with the computed file and
	// with an empty set of properties: it will be read from the file.
	BootstrapServer server = new BootstrapServer(orb,
						     initialPort,
						     file,
						     new Properties());

	// Start it
	server.start();

	// Stick around
	java.lang.Object sync = new java.lang.Object();
	try {
	    synchronized (sync) { sync.wait();}
	} catch (Exception ex) {}

    }

    public final static boolean debug = false;
    private int listenerPort;
    private Thread listenerThread;
    private BootstrapServiceProperties supportedServices;
    private BootstrapRequestHandler serverSC;
    private com.sun.corba.se.internal.iiop.ORB orb;
}

/**
 * Class BootstrapServiceProperties implements a synchronized properties set
 * that automatically gets loaded from the supplied file if the modification
 * timestamp changes, or stored to the file when updated. Not providing a
 * file implies running in memory with no persistent storage.
 * It only implements get(), put() and keys(); all of
 * these methods are synchronized to ensure correct updates.
 */
final class BootstrapServiceProperties
{
    /**
     * Constructs a BootstrapServiceProperties object.
     * The file and properties object reference are stored; loading is
     * done on demand.
     * @param pFile a file object which is the file containing the Properties.
     */
    public BootstrapServiceProperties(File pFile, Properties props) {
	// Do not bother loading now; wait for first request
	propFile = pFile;
	savedProps = props;
	allProps = new Properties(savedProps);
    }

    /**
   * Returns the value associated with the key, if any.
   * First the check() method is called to make sure we have an up to
   * date copy, and then the getProperty() method is called on the
   * Properties object. This method is synchronized so access gets
   * serialized.
   * @param key a string which is the key to look up.
   * @return a string which is the value associated with the key, or null
   * if no such key exists.
   */
    public synchronized String get(String key) {
	// Check first, then call properties
	this.check();
	return allProps.getProperty(key);
    }

    /**
   * Stores a value associated with a key, and optionally writes the
   * key,value pair to a file.
   * This method is synchronized so access gets
   * serialized.
   * @param key a string which is the key to store the value under.
   * @param val a string which is the value to store.
   * @param save a boolean which is true if the key,value pair should be saved.
   * @return any previously bound value.
   */
    public synchronized String put(String key,String val, boolean save) {
	// If we're not supposed to save it, just put it in and be done
	if (!save)
	    return (String)allProps.put(key,val);
	// Check first, then call properties
	String s = (String)savedProps.put(key,val);
	if (propFile != null) {
	    try {
		// Store file
		FileOutputStream fileOS = new FileOutputStream(propFile);
		// Clear and reload
		savedProps.save(fileOS,null);
		fileOS.close();
		// Update timestamp
		fileModified = propFile.lastModified();
	    } catch (java.io.FileNotFoundException e) {
		System.err.println(CorbaResourceUtil.getText("bootstrap.filenotfound", propFile.getAbsolutePath()));
	    } catch (java.io.IOException e) {
		System.err.println(CorbaResourceUtil.getText("bootstrap.exception",propFile.getAbsolutePath(), e.toString()));
	    }
	}
	return s;
    }

    /**
   * Returns an array of strings containing the list of available keys.
   * First the check() method is called to make sure we have an up to
   * date copy, and then an enumeration is created to iterate through
   * the properties object, collecting all keys in a string array.
   * @return an array of strings containing the available keys in the
   * Properties object.
   */
    public synchronized String[] keys() {
	// Ensure up to date
	this.check();
	// Compute list of keys
	String[] services = null;
	int size = allProps.size() + savedProps.size();
	if (size > 0) {
	    services = new String[size];

	    // Obtain all the keys from the property object
	    Enumeration theKeys = allProps.propertyNames();
	    for (int index=0;theKeys.hasMoreElements();index++) {
		services[index] = (String)theKeys.nextElement();
	    }
	}
	return services;
    }

    /**
   * Checks the lastModified() timestamp of the file and optionally
   * re-reads the Properties object from the file if newer.
   */
protected void check() {
    // Is there a file to read?
    if (propFile == null)
	return;
    // Assume we already have the object lock
    long lastMod = propFile.lastModified();
    // Up to date?
    if (lastMod > fileModified) {
	try {
	    // No, load it again.
	    FileInputStream fileIS = new FileInputStream(propFile);
	    // Clear and reload
	    savedProps.clear();
	    savedProps.load(fileIS);
	    fileIS.close();
	    // Set new timestamp
	    fileModified = lastMod;
	} catch (java.io.FileNotFoundException e) {
	    System.err.println(CorbaResourceUtil.getText("bootstrap.filenotfound", propFile.getAbsolutePath()));
	} catch (java.io.IOException e) {
	    System.err.println(CorbaResourceUtil.getText("bootstrap.exception",propFile.getAbsolutePath(), e.toString()));
	}
    }
}

    private File propFile;
private long fileModified;
private Properties savedProps;
private Properties allProps;
}

/**
 * Class BootstrapRequestHandler handles the requests coming to the
 * BootstrapServer. It implements Server so that it can be registered
 * as a subcontract. It is passed a BootstrapServiceProperties object
 * which contains
 * the supported ids and their values for the bootstrap service. This
 * Properties object is only read from, never written to, and is shared
 * among all threads.
 * <p>
 * The BootstrapRequestHandler responds primarily to GIOP requests,
 * but LocateRequests are (reluctantly) handled for graceful interoperability.
 * The BootstrapRequestHandler handles one request at a time.
 */
final class BootstrapRequestHandler implements ServerSubcontract
{
    public static final int OBJECT_KEY_BAD_LEN = 10000;
    public static final int OPERATION_NOT_GET_OR_LIST = 10001;
    public static final int JAVA_RUNTIME_EXC_CAUGHT = 10002;
    public static final int JAVA_EXC_CAUGHT = 10003;

    private com.sun.corba.se.internal.iiop.ORB orb;
    private BootstrapServiceProperties props;
    private static final boolean debug = false;

    public BootstrapRequestHandler(com.sun.corba.se.internal.iiop.ORB orb,
				   BootstrapServiceProperties props) {
	this.orb = orb;
	this.props = props;
    }

    public void destroyObjref(java.lang.Object objref)
    {
    }

    /**
     * Dispatch is called by the ORB and will serve get(key) and list()
     * invocations on the initial object key.
     */
    public com.sun.corba.se.internal.core.ServerResponse dispatch(
					       com.sun.corba.se.internal.core.ServerRequest request)
    {
	com.sun.corba.se.internal.core.ServerResponse response = null;

	try {
	    MarshalInputStream is = (MarshalInputStream) request;
	    String method = request.getOperationName();
	    response = request.createResponse(null);
	    MarshalOutputStream os = (MarshalOutputStream) response;

            if (method.equals("get")) {

                // Get the name of the requested service
                String service_key = is.read_string();

                // Look it up
                String service_value = props.get(service_key);
                org.omg.CORBA.Object service_obj = null;
                if (service_value != null) {
                    try {
                      	service_obj = orb.string_to_object(service_value);
                    } catch (org.omg.CORBA.SystemException e) {}
                }

                if (debug)
		    System.out.println("BootstrapRequestHandler: get(" +
				       service_key + ") -> " + service_value);

                // Write reply value
                os.write_Object(service_obj);

            } else if (method.equals("list")) {

                // Get all known keys
                String keys[] = props.keys();
                int keys_len = 0;
            	if (keys == null) keys = new String[0];
                else keys_len = keys.length;

                if (debug)
		    System.out.println("BootstrapRequestHandler: list() -> " +
				       keys_len + " entries.");

                // Write length of sequence
                os.write_long(keys_len);

                // Write each string in the sequence
                for (int i=0;i<keys_len;i++) {
              	    os.write_string(keys[i]);
            	}
	    } else {

                // Bad operation
		if (debug) System.out.println(method);
                throw new BAD_OPERATION(OPERATION_NOT_GET_OR_LIST,
					CompletionStatus.COMPLETED_NO);
            }

	} catch (org.omg.CORBA.SystemException ex) {
            // Marshal the exception thrown
            if (debug) ex.printStackTrace();
	    response = request.createSystemExceptionResponse(ex, null);
	} catch (java.lang.RuntimeException ex) {
            // Unknown exception
            if (debug) ex.printStackTrace();
	    response = request.createSystemExceptionResponse(
							     new UNKNOWN(ex.toString(),
									 JAVA_RUNTIME_EXC_CAUGHT,
									 CompletionStatus.COMPLETED_NO),
							     null);
	} catch (java.lang.Exception ex) {
            // Unknown exception
            if (debug) ex.printStackTrace();
            response = request.createSystemExceptionResponse(
							     new UNKNOWN(ex.toString(),
									 JAVA_EXC_CAUGHT,
									 CompletionStatus.COMPLETED_NO),
							     null);
	}

	return response;
    }

    /**
     * Locates the object mentioned in the locate requests, and returns
     * object here iff the object is the initial object key. A SystemException
     * thrown if the object key is not the initial object key.
     */
    public com.sun.corba.se.internal.core.IOR locate(
            com.sun.corba.se.internal.ior.ObjectKey objectKey) {
	return null;
    }

    /**
     * Not implemented; throws org.omg.CORBA.NO_IMPLEMENT().
     */
public java.lang.Object createObjref( IOR ior )
{
    throw new NO_IMPLEMENT();
}

/**
 * Not implemented; throws org.omg.CORBA.NO_IMPLEMENT().
 */
public java.lang.Object createObjref(byte[] key,
				     java.lang.Object servant)
{
    throw new NO_IMPLEMENT();
}

/**
 * Not implemented; throws org.omg.CORBA.NO_IMPLEMENT().
 */
public byte[] getKey(org.omg.CORBA.Object objref)
{
    throw new NO_IMPLEMENT();
}

/**
 * Not implemented; throws org.omg.CORBA.NO_IMPLEMENT().
 */
public int getImplId(org.omg.CORBA.Object objref)
{
    throw new NO_IMPLEMENT();
}

/**
 * isServantSupported should return null
 */
public boolean isServantSupported()
{
    return false;
}

/**
 * Not implemented; throws org.omg.CORBA.NO_IMPLEMENT().
 */
public java.lang.Object getServant(IOR ior) {
    throw new NO_IMPLEMENT();
}

/**
 * Not implemented; throws org.omg.CORBA.NO_IMPLEMENT().
 */
public void setOrb(com.sun.corba.se.internal.core.ORB orb) {
    throw new NO_IMPLEMENT();
}

/**
 * Not implemented; throws org.omg.CORBA.NO_IMPLEMENT().
 */
public void setId(int scid) {
    throw new NO_IMPLEMENT();
}

/**
 * Not implemented; throws org.omg.CORBA.NO_IMPLEMENT().
 */
public int getId() {
    throw new NO_IMPLEMENT();
}

/**
 * Not implemented; throws org.omg.CORBA.NO_IMPLEMENT().
 */
public java.lang.Class getClientSubcontractClass() {
    throw new NO_IMPLEMENT();
}
}
