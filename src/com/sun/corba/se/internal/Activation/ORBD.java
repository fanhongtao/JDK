/*
 * @(#)ORBD.java	1.49 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/* 
 * @(#)ORBD.java	1.27 00/03/02
 *
 * Copyright 1993-1997 Sun Microsystems, Inc. 901 San Antonio Road,
 * Palo Alto, California, 94303, U.S.A.  All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 *
 * CopyrightVersion 1.2
 *
 */

package com.sun.corba.se.internal.Activation;

import java.io.File;
import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.CompletionStatus;
import org.omg.CosNaming.NamingContext;
import org.omg.PortableServer.POA;

import org.omg.CORBA.portable.ObjectImpl;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.core.ClientSubcontract;
import com.sun.corba.se.internal.core.ServerSubcontract;
import com.sun.corba.se.internal.ior.ObjectKeyTemplate;
import com.sun.corba.se.internal.core.INSObjectKeyMap;
import com.sun.corba.se.internal.core.INSObjectKeyEntry;
import com.sun.corba.se.internal.CosNaming.BootstrapServer;
import com.sun.corba.se.internal.CosNaming.TransientNameService;
import com.sun.corba.se.internal.PCosNaming.NameService;
import com.sun.corba.se.internal.POA.POAORB;
import com.sun.corba.se.internal.orbutil.ORBConstants;
import com.sun.corba.se.internal.orbutil.CorbaResourceUtil;
import com.sun.corba.se.ActivationIDL.Repository;
import com.sun.corba.se.ActivationIDL.RepositoryPackage.ServerDef;
import com.sun.corba.se.ActivationIDL.Locator;
import com.sun.corba.se.ActivationIDL.LocatorHelper;
import com.sun.corba.se.ActivationIDL.Activator;
import com.sun.corba.se.ActivationIDL.ActivatorHelper;
import com.sun.corba.se.ActivationIDL.ServerAlreadyRegistered;


/**
 * 
 * @version 	1.10, 97/12/06
 * @author	Rohit Garg
 * @since	JDK1.2
 */
public class ORBD
{
    private int initSvcPort;
    protected InitialNamingImpl initializeBootNaming(POAORB orb)
    {
	// construct boostrap server args
	Properties props = new Properties(); // null properties
	String fileSep = System.getProperty("file.separator");
    	File nameFile = new File( dbDir, 
	    fileSep + ORBConstants.INITIAL_ORB_DB );

    	// create a bootstrap server
        BootstrapServer bootServer;
	initSvcPort = orb.getORBInitialPort();
    	bootServer = new BootstrapServer(orb, initSvcPort, nameFile, props);
    	bootServer.start();

	// add the Initial Naming object
	InitialNamingImpl ins = new InitialNamingImpl(orb, bootServer);

	return ins;
    }

    protected POAORB createORB(String[] args)
    {
	Properties props = System.getProperties();

	props.put( ORBConstants.PERSISTENT_SERVER_PORT_PROPERTY, 
	    props.getProperty( ORBConstants.ORBD_PORT_PROPERTY,
		Integer.toString( 
		    ORBConstants.DEFAULT_ACTIVATION_PORT ) ) ) ;

	// See Bug 4396928 for more information about why we are initializing
	// the ORBClass to PIORB.
	props.put("org.omg.CORBA.ORBClass", 
	    "com.sun.corba.se.internal.Interceptors.PIORB");

	return (POAORB) ORB.init(args, props);
    }

    private void run(String[] args) 
    {
	try {
	    // parse the args and try setting the values for these
	    // properties
	    processArgs(args);

	    POAORB orb = createORB(args);

	    if (orb.orbdDebugFlag) 
		System.out.println( "ORBD begins initialization." ) ;

	    boolean firstRun = createSystemDirs( ORBConstants.DEFAULT_DB_DIR );

	    startActivationObjects(orb);

	    if (firstRun) // orbd is being run the first time
		installOrbServers(getRepository(), getActivator());

	    if (orb.orbdDebugFlag) {
		System.out.println( "ORBD is ready." ) ;
	        System.out.println("ORBD serverid: " +
	                System.getProperty(ORBConstants.SERVER_ID_PROPERTY));
	        System.out.println("activation dbdir: " +
	                System.getProperty(ORBConstants.DB_DIR_PROPERTY));
	        System.out.println("activation port: " +
	                System.getProperty(ORBConstants.ORBD_PORT_PROPERTY));

                String pollingTime = System.getProperty(
                    ORBConstants.SERVER_POLLING_TIME);
                if( pollingTime == null ) {
                    pollingTime = Integer.toString( 
                        ORBConstants.DEFAULT_SERVER_POLLING_TIME );
                }
                System.out.println("activation Server Polling Time: " +
                        pollingTime + " milli-seconds ");

                String startupDelay = System.getProperty(
                    ORBConstants.SERVER_STARTUP_DELAY);
                if( startupDelay == null ) {
                    startupDelay = Integer.toString( 
                        ORBConstants.DEFAULT_SERVER_STARTUP_DELAY );
                }
	        System.out.println("activation Server Startup Delay: " +
                        startupDelay + " milli-seconds " );
	    }

	    // The following two lines start the Persistent NameService
            NameServiceStartThread theThread =
                new NameServiceStartThread( orb, dbDir, ins );
            theThread.start( );

	    orb.run();
	} catch( org.omg.CORBA.COMM_FAILURE cex ) {
            System.out.println( CorbaResourceUtil.getText("orbd.commfailure"));
        } catch( org.omg.CORBA.INTERNAL iex ) {
            System.out.println( CorbaResourceUtil.getText(
                "orbd.internalexception"));
        } catch (Exception ex) {
	    System.out.println(CorbaResourceUtil.getText(
                "orbd.usage", "orbd"));
	    System.out.println( ex );
	    ex.printStackTrace();
	}
    }

    private void processArgs(String[] args)
    {
	Properties props = System.getProperties();
	for (int i=0; i < args.length; i++) {
	    if (args[i].equals("-port")) {
	        if ((i+1) < args.length) {
	            props.put(ORBConstants.ORBD_PORT_PROPERTY, args[++i]);
	        } else {
	            System.out.println(CorbaResourceUtil.getText(
			"orbd.usage", "orbd"));
	        }
	    } else if (args[i].equals("-defaultdb")) {
	        if ((i+1) < args.length) {
	            props.put(ORBConstants.DB_DIR_PROPERTY, args[++i]);
	        } else {
	            System.out.println(CorbaResourceUtil.getText(
			"orbd.usage", "orbd"));
	        }
	    } else if (args[i].equals("-serverid")) {
	        if ((i+1) < args.length) {
	            props.put(ORBConstants.SERVER_ID_PROPERTY, args[++i]);
	        } else {
	            System.out.println(CorbaResourceUtil.getText(
			"orbd.usage", "orbd"));
	        }
	    } else if (args[i].equals("-serverPollingTime")) {
	        if ((i+1) < args.length) {
	            props.put(ORBConstants.SERVER_POLLING_TIME, args[++i]);
	        } else {
	            System.out.println(CorbaResourceUtil.getText(
			"orbd.usage", "orbd"));
	        }
	    } else if (args[i].equals("-serverStartupDelay")) {
	        if ((i+1) < args.length) {
	            props.put(ORBConstants.SERVER_STARTUP_DELAY, args[++i]);
	        } else {
	            System.out.println(CorbaResourceUtil.getText(
			"orbd.usage", "orbd"));
	        }
            }
	}
    }

    /**
     * Ensure that the Db directory exists. If not, create the Db
     * and the log directory and return true. Otherwise return false.
     */
    protected boolean createSystemDirs(String defaultDbDir)
    {
	boolean dirCreated = false;
	Properties props = System.getProperties();
	String fileSep = props.getProperty("file.separator");

	// determine the ORB db directory
	dbDir = new File (props.getProperty( ORBConstants.DB_DIR_PROPERTY,
	    props.getProperty("user.dir") + fileSep + defaultDbDir));

	// create the db and the logs directories
        dbDirName = dbDir.getAbsolutePath();
	props.put(ORBConstants.DB_DIR_PROPERTY, dbDirName);
	if (!dbDir.exists()) {
	    dbDir.mkdir();
	    dirCreated = true;
	}

	File logDir = new File (dbDir, ORBConstants.SERVER_LOG_DIR ) ;
	if (!logDir.exists()) logDir.mkdir();

	return dirCreated;
    }

    protected File dbDir;
    protected File getDbDir()
    {
	return dbDir;
    }

    private String dbDirName;
    protected String getDbDirName()
    {
	return dbDirName;
    }

    protected InitialNamingImpl ins;

    protected void startActivationObjects(POAORB orb) throws Exception
    {
	// create Initial Name Service object
	ins = initializeBootNaming(orb);

	// create Repository object
	repository = new RepositoryImpl(orb, dbDir, orb.orbdDebugFlag );
	ins.bind( ORBConstants.SERVER_REPOSITORY_NAME, repository, false);

	// create Locator and Activator objects
	com.sun.corba.se.internal.core.ServerGIOP sgiop = orb.getServerGIOP();
	sgiop.initEndpoints();
	ServerManagerImpl serverMgr =
	    new ServerManagerImpl( orb, 
				   sgiop,
				   repository, 
				   getDbDirName(), 
				   orb.orbdDebugFlag );

	locator = LocatorHelper.narrow(serverMgr);
	ins.bind( ORBConstants.SERVER_LOCATOR_NAME, locator, false);

	activator = ActivatorHelper.narrow(serverMgr);
	ins.bind( ORBConstants.SERVER_ACTIVATOR_NAME, activator, false);

        // start Name Service
        TransientNameService nameService = 
            new TransientNameService(orb, 
                ORBConstants.TRANSIENT_NAME_SERVICE_NAME);
        org.omg.CORBA.Object rootContext = nameService.initialNamingContext();
        ins.bind(ORBConstants.TRANSIENT_NAME_SERVICE_NAME,rootContext, false);
    }

    protected Locator locator;
    protected Locator getLocator()
    {
	return locator;
    }

    protected Activator activator;
    protected Activator getActivator()
    {
	return activator;
    }

    protected RepositoryImpl repository;
    protected RepositoryImpl getRepository()
    {
	return repository;
    }

    /** 
     * Go through the list of ORB Servers and initialize and start
     * them up.
     */
    protected void installOrbServers(RepositoryImpl repository, 
				     Activator activator)
    {
	int serverId;
	String[] server;
	ServerDef serverDef;

	for (int i=0; i < orbServers.length; i++) {
	    try {
		server = orbServers[i];
		serverDef = new ServerDef(server[1], server[2], 
					  server[3], server[4], server[5] );

		serverId = Integer.valueOf(orbServers[i][0]).intValue();

		repository.registerServer(serverDef, serverId);

		activator.activate(serverId);

	    } catch (Exception ex) {}
	}
    }

    public static void main(String[] args) {
	ORBD orbd = new ORBD();
	orbd.run(args);
    }

    /**
     * List of servers to be auto registered and started by the ORBd.
     * 
     * Each server entry is of the form {id, name, path, args, vmargs}.
     */
    private static String[][] orbServers = {
	{""}
    };
}
