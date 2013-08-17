/*
 * @(#)WindowsProgressBarUI.java	1.15 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package com.sun.java.swing.plaf.windows;

import javax.swing.plaf.basic.*;
import javax.swing.plaf.*;
import javax.swing.*;
import java.awt.*;


/**
 * Windows rendition of the component.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.15 02/02/00
 * @author Michael C. Albers
 */
public class WindowsProgressBarUI extends BasicProgressBarUI
{

    public static ComponentUI createUI(JComponent x) {
	return new WindowsProgressBarUI();
    }
}

