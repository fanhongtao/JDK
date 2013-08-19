package com.sun.corba.se.ActivationIDL;


/**
* com/sun/corba/se/ActivationIDL/ActivatorOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from ../../../../../../src/share/classes/com/sun/corba/se/ActivationIDL/activation.idl
* Friday, June 20, 2003 2:21:23 AM PDT
*/

public interface ActivatorOperations 
{

  // A new ORB started server registers itself with the Activator
  void active (int serverId, com.sun.corba.se.ActivationIDL.Server serverObj) throws com.sun.corba.se.ActivationIDL.ServerNotRegistered;

  // Install a particular kind of endpoint
  void registerEndpoints (int serverId, String orbId, com.sun.corba.se.ActivationIDL.EndPointInfo[] endPointInfo) throws com.sun.corba.se.ActivationIDL.ServerNotRegistered, com.sun.corba.se.ActivationIDL.NoSuchEndPoint, com.sun.corba.se.ActivationIDL.ORBAlreadyRegistered;

  // list active servers
  int[] getActiveServers ();

  // If the server is not running, start it up.
  void activate (int serverId) throws com.sun.corba.se.ActivationIDL.ServerAlreadyActive, com.sun.corba.se.ActivationIDL.ServerNotRegistered, com.sun.corba.se.ActivationIDL.ServerHeldDown;

  // If the server is running, shut it down
  void shutdown (int serverId) throws com.sun.corba.se.ActivationIDL.ServerNotActive, com.sun.corba.se.ActivationIDL.ServerNotRegistered;

  // currently running, this method will activate it.
  void install (int serverId) throws com.sun.corba.se.ActivationIDL.ServerNotRegistered, com.sun.corba.se.ActivationIDL.ServerHeldDown, com.sun.corba.se.ActivationIDL.ServerAlreadyInstalled;

  // list all registered ORBs for a server
  String[] getORBNames (int serverId) throws com.sun.corba.se.ActivationIDL.ServerNotRegistered;

  // After this hook completes, the server may still be running.
  void uninstall (int serverId) throws com.sun.corba.se.ActivationIDL.ServerNotRegistered, com.sun.corba.se.ActivationIDL.ServerHeldDown, com.sun.corba.se.ActivationIDL.ServerAlreadyUninstalled;
} // interface ActivatorOperations
