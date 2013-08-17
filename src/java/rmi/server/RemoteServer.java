/*
 * @(#)RemoteServer.java	1.11 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.rmi.server;

import java.rmi.*;

/**
 * The RemoteServer class is the common superclass to all server
 * implementations and provides the framework to support a wide range
 * of remote reference semantics.  Specifically, the functions needed
 * to create and export remote objects (i.e. to make them remotely
 * available) are provided abstractly by RemoteServer and concretely
 * by its subclass(es). <p>
 *
 * The subclass selected identifies the semantics of the remote
 * reference, for example whether the server is a single object or is
 * a replicated object requiring communications with multiple
 * locations. At present only UnicastRemoteObject is supported.
 */
public abstract class RemoteServer extends RemoteObject
{
    private static final long serialVersionUID = -4100238210092549637L;

    private static String logname = "RMI";
    private static LogStream log;

    protected RemoteServer() {
	super();
    }
    
    protected RemoteServer(RemoteRef ref) {
	super(ref);
    }

    /**
     * Return the hostname of the current client.  When called from a
     * thread actively handling a remote method invocation the
     * hostname of the client is returned.
     * @exception ServerNotActiveException If called outside of servicing
     * a remote method invocation.
     */
    public static String getClientHost() throws ServerNotActiveException {
	try {
	    Class refClass = Class.forName(RemoteRef.packagePrefix +
					   ".UnicastServerRef");
	    ServerRef ref = (ServerRef)refClass.newInstance();
	    return ref.getClientHost();
	} catch (ServerNotActiveException e) {
	    throw e;
	} catch (Exception e) {
	    throw new ServerNotActiveException("Client host unobtainable");
	}
    }

    /**
     * Log RMI calls to the output stream <I>out</I>. If <I>out</I> is
     * null, call logging is turned off.
     */
    public static void setLog(java.io.OutputStream out) 
    {
	if (out == null) {
	    log = null;
	} else {
	    LogStream tempLog = LogStream.log(logname);
	    tempLog.setOutputStream(out);
	    log = tempLog;
	}
    }
    /**
     * Returns stream for the RMI call log.
     */
    public static java.io.PrintStream getLog() 
    {
	return log;
    }

    static 
    {
	// initialize log
	try {
	    log = (Boolean.getBoolean("java.rmi.server.logCalls") ?
		   LogStream.log(logname) : null);
	} catch (Exception e) {
	}
    }
}
