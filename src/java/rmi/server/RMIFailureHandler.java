/*
 * @(#)RMIFailureHandler.java	1.4 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.rmi.server;

/**
 * An <code>RMIFailureHandler</code> can be registered via the
 * <code>RMISocketFactory.setFailureHandler</code> call. The
 * <code>failure</code> method of the handler is invoked when the RMI
 * runtime is unable to create a <code>ServerSocket</code> to listen
 * for incoming calls. The <code>failure</code> method returns a boolean
 * indicating whether the runtime should attempt to re-create the
 * <code>ServerSocket</code>.
 *
 * @author 	Ann Wollrath
 * @version	1.4, 07/01/98
 * @since 	JDK1.1
 */
public interface RMIFailureHandler {

    /**
     * The <code>failure</code> callback is invoked when the RMI
     * runtime is unable to create a <code>ServerSocket</code> via the
     * <code>RMISocketFactory</code>. An <code>RMIFailureHandler</code>
     * is registered via a call to
     * <code>RMISocketFacotry.setFailureHandler</code>.  If no failure
     * handler is installed, the default behavior is to attempt to
     * re-create the ServerSocket.
     *
     * @param ex the exception that occurred during <code>ServerSocket</code>
     *           creation
     * @return if true, the RMI runtime attempts to retry
     * <code>ServerSocket</code> creation
     */
    public boolean failure(Exception ex);
    
}

