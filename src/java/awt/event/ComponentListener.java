/*
 * @(#)ComponentListener.java	1.6 96/11/25 Carl Quinn
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
 * The listener interface for receiving component events.
 * Component events are provided for notification purposes ONLY;
 * The AWT will automatically handle component moves and resizes
 * internally so that GUI layout works properly regardless of
 * whether a program registers a ComponentListener or not.
 *
 * @version 1.6 11/25/96
 * @author Carl Quinn
 */
public interface ComponentListener extends EventListener {
    /**
     * Invoked when component has been resized.
     */
    public void componentResized(ComponentEvent e);

    /**
     * Invoked when component has been moved.
     */    
    public void componentMoved(ComponentEvent e);

    /**
     * Invoked when component has been shown.
     */
    public void componentShown(ComponentEvent e);

    /**
     * Invoked when component has been hidden.
     */
    public void componentHidden(ComponentEvent e);
}
