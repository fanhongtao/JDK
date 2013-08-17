/*
 * @(#)Remote.java	1.10 00/02/02
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.rmi;

/** 
 * The <code>Remote</code> interface serves to identify interfaces whose
 * methods may be invoked from a non-local virtual machine.  Any object that
 * is a remote object must directly or indirectly implement this interface.
 * Only those methods specified in a "remote interface", an interface that
 * extends <code>java.rmi.Remote</code> are available remotely.
 *
 * <p>Implementation classes can implement any number of remote interfaces and
 * can extend other remote implementation classes.  RMI provides some
 * convenience classes that remote object implementations can extend which
 * facilitate remote object creation.  These classes are
 * <code>java.rmi.server.UnicastRemoteObject</code> and
 * <code>java.rmi.activation.Activatable</code>.
 *
 * <p>For complete details on RMI, see the <a
 href=../../../guide/rmi/spec/rmiTOC.doc.html>RMI Specification</a> which describes the RMI API and system</a>.
 *
 * @version 1.10, 02/02/00
 * @since   JDK1.1
 * @author  Ann Wollrath
 * @see	    java.rmi.server.UnicastRemoteObject
 * @see     java.rmi.activation.Activatable
 */
public interface Remote {}
