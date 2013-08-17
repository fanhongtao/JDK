/*
 * @(#)Remote.java	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi;

/** 
 * The <code>Remote</code> interface serves to identify interfaces whose
 * methods may be invoked from a non-local virtual machine.  Any object that
 * is a remote object must directly or indirectly implement this interface.
 * Only those methods specified in a "remote interface", an interface that
 * extends <code>java.rmi.Remote</code>, are available remotely.
 *
 * <p>Implementation classes can implement any number of remote
 * interfaces and can extend other remote implementation classes.  RMI
 * provides a convenience class,
 * <code>java.rmi.server.UnicastRemoteObject</code>, that remote
 * object implementations can extend to facilitate remote object
 * creation.
 *
 * <p>For complete details on RMI, see the <a
 href=http://java.sun.com/products/jdk/1.1/docs/guide/rmi/spec/rmiTOC.doc.html>RMI Specification</a> which describes the RMI API and system</a>.  
 */

public interface Remote {}
