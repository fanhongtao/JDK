/*
 * @(#)UnknownGroupException.java	1.6 98/07/08
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.rmi.activation;

/**
 * An <code>UnknownGroupException</code> is thrown by methods of classes and
 * interfaces in the <code>java.rmi.activation</code> package when the
 * <code>ActivationGroupID</code> parameter to the method is determined to be
 * invalid, i.e., not known by the <code>ActivationSystem</code>.  An
 * <code>UnknownGroupException</code> is also thrown if the
 * <code>ActivationGroupID</code> in an <code>ActivationDesc</code> refers to
 * a group that is not registered with the <code>ActivationSystem</code>
 * 
 * @version 1.6, 07/08/98
 * @author  Ann Wollrath
 * @since   JDK1.2
 * @see     java.rmi.activation.Activatable
 * @see     java.rmi.activation.ActivationGroup
 * @see     java.rmi.activation.ActivationGroupID
 * @see     java.rmi.activation.ActivationMonitor
 * @see     java.rmi.activation.ActivationSystem
 */
public class UnknownGroupException extends ActivationException {

    /** indicate compatibility with JDK 1.2 version of class */
    private static final long serialVersionUID = 7056094974750002460L;

    /**
     * Constructs an <code>UnknownGroupException</code> with the specified
     * detail message.
     *
     * @param s the detail message
     * @since JDK1.2
     */
    public UnknownGroupException(String s) {
	super(s);
    }
}
