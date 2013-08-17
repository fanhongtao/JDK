/*
 * @(#)RMIFailureHandler.java	1.2 96/11/18
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
 * An RMIFailureHandler can be registered via the
 * RMISocketFactory.setFailureHandler call. The "failure" method of
 * the handler is invoked when the RMI runtime is unable to create a
 * Socket or ServerSocket. The "failure" method returns a boolean
 * indicating whether the runtime should attempt to retry.
 */
public interface RMIFailureHandler {

    /**
     * The "failure" callback is invoked when the RMI runtime is
     * unable to create a Socket or ServerSocket via the
     * RMISocketFactory. The RMIFailureHandler is registered via a
     * call to RMISocketFacotry.setFailureHandler().  The default
     * failure handler returns false.
     *
     * @param ex the exception that occurred during socket creation
     * @return if true, the RMI runtime attempts to retry
     */
    public boolean failure(Exception ex);
    
}

