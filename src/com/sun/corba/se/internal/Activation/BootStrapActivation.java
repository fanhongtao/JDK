/*
 * @(#)BootStrapActivation.java	1.11 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.Activation;

import java.io.File;
import java.util.Properties;

import org.omg.CosNaming.NamingContext;
import org.omg.CORBA.INTERNAL ;

import com.sun.corba.se.internal.POA.POAORB;
import com.sun.corba.se.ActivationIDL.Repository;
import com.sun.corba.se.ActivationIDL.RepositoryPackage.ServerDef;
import com.sun.corba.se.ActivationIDL.Locator;
import com.sun.corba.se.ActivationIDL.LocatorHelper;
import com.sun.corba.se.ActivationIDL.Activator;
import com.sun.corba.se.ActivationIDL.ActivatorHelper;
import com.sun.corba.se.internal.CosNaming.BootstrapServer;
import com.sun.corba.se.internal.orbutil.ORBConstants;

public class BootStrapActivation
{
    private POAORB orb;

    private String dbDirName;

    private File dbDir;

    private InitialNamingImpl ins;

    public BootStrapActivation( POAORB theorb ) 
    {
	orb = theorb;
    }

    private void createSystemDirs(String defaultDbDir ) {
	Properties props = System.getProperties();
	String fileSep = props.getProperty("file.separator");

	dbDir = new File(props.getProperty(
	    ORBConstants.DB_DIR_PROPERTY,
	    props.getProperty("user.dir") + fileSep + 
	    ORBConstants.DEFAULT_DB_DIR ) );

	dbDirName = dbDir.getAbsolutePath( );
	props.put(ORBConstants.DB_DIR_PROPERTY, dbDirName );
	if( !dbDir.exists() ) {
		dbDir.mkdir();
	}

	File logDir = new File( dbDir, "logs");
	if( !logDir.exists() ) {
	    logDir.mkdir();
	}
    }

    private void initializeBootNaming( ) 
    {
	try {
	    createSystemDirs( ORBConstants.DEFAULT_DB_DIR );
	    Properties props = new Properties(); // null properties
	    String fileSep = System.getProperty("file.separator");
	    File nameFile = new File(dbDir, fileSep + 
		ORBConstants.INITIAL_ORB_DB);

	    // create a bootstrap server
	    BootstrapServer bootServer;
	    int initSvcPort = orb.getORBInitialPort();
	    bootServer = new BootstrapServer(orb, initSvcPort,
				     nameFile, props);
	    bootServer.start();

	    // add the Initial Naming object
	    ins = new InitialNamingImpl((org.omg.CORBA.ORB)orb, 
					    bootServer);
	} catch( Exception e ) {
	    // No Exception is displayed, Because the NameService release
	    // Expects the exception in certain format.
	    // _REVISIT_ and print out the right message
	}
   }
   
    public void start( ) 
    {
	try {
	    initializeBootNaming( );
	    
	    // Initialize Server Repository
	    RepositoryImpl repository = 
	    (RepositoryImpl) orb.getInitialService( 
		ORBConstants.SERVER_REPOSITORY_NAME );
	    if( repository == null ) {
		repository = new RepositoryImpl( 
		    orb, dbDir, orb.orbdDebugFlag );
	    }
	    ins.bind( ORBConstants.SERVER_REPOSITORY_NAME, repository, 
		false );

	    // Initialize Locator and Activator objects
	    ServerManagerImpl serverMgr = 
		(ServerManagerImpl)orb.getInitialService( 
		    ORBConstants.SERVER_LOCATOR_NAME );

	    if( serverMgr == null ) {
		com.sun.corba.se.internal.core.ServerGIOP sgiop =
		    orb.getServerGIOP();
		sgiop.initEndpoints();
		serverMgr = new ServerManagerImpl( orb,
		    sgiop, repository, dbDirName, orb.orbdDebugFlag );
	    }
    
	    Locator locator = LocatorHelper.narrow( serverMgr );
	    ins.bind( ORBConstants.SERVER_LOCATOR_NAME, locator, false );

	    Activator activator = ActivatorHelper.narrow( serverMgr );
	    ins.bind( ORBConstants.SERVER_ACTIVATOR_NAME, activator, 
		false );

	    // Initialize Name Service
	    org.omg.CORBA.Object rootContext = 
		orb.getInitialService("NameService");

	    ins.bind( "NameService", rootContext, false );
	} catch( Exception e ) {
	    throw new INTERNAL() ;
	}
    }
}
