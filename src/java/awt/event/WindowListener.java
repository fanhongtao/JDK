/*
 * @(#)WindowListener.java	1.6 96/12/17
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
 * 
 */

package java.awt.event;

import java.util.EventListener;

/**
 * The listener interface for receiving window events.
 *
 * @version 1.6 12/17/96
 * @author Carl Quinn
 */
public interface WindowListener extends EventListener {
    /**
     * Invoked when a window has been opened.
     */
    public void windowOpened(WindowEvent e);

    /**
     * Invoked when a window is in the process of being closed.
     * The close operation can be overridden at this point.
     */
    public void windowClosing(WindowEvent e);

    /**
     * Invoked when a window has been closed.
     */
    public void windowClosed(WindowEvent e);

    /**
     * Invoked when a window is iconified.
     */
    public void windowIconified(WindowEvent e);

    /**
     * Invoked when a window is de-iconified.
     */
    public void windowDeiconified(WindowEvent e);

    /**
     * Invoked when a window is activated.
     */
    public void windowActivated(WindowEvent e);

    /**
     * Invoked when a window is de-activated.
     */
    public void windowDeactivated(WindowEvent e);
}
