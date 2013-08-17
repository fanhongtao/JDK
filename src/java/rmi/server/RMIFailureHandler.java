/*
 * @(#)RMIFailureHandler.java	1.3 98/01/09
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
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
 * @version	1.3, 01/09/98
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

