/*
 * @(#)WindowsInternalFrameUI.java	1.10 98/08/28
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

package com.sun.java.swing.plaf.windows;

import javax.swing.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.ComponentUI;

/**
 * Windows rendition of the component.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 */
public class WindowsInternalFrameUI extends BasicInternalFrameUI
{
    public static ComponentUI createUI(JComponent b)    {
        return new WindowsInternalFrameUI((JInternalFrame)b);
    }

    public WindowsInternalFrameUI(JInternalFrame w){
	super(w);
    }

    protected DesktopManager createDesktopManager(){
      return new WindowsDesktopManager();
    }
    
}


