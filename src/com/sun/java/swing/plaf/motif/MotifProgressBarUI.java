/*
 * @(#)MotifProgressBarUI.java	1.13 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.motif;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import java.io.Serializable;

import javax.swing.plaf.basic.BasicProgressBarUI;


/**
 * A Motif ProgressBarUI.  
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.13 12/19/03
 * @author Michael C. Albers
 */
public class MotifProgressBarUI extends BasicProgressBarUI 
{
    /**
     * Creates the ProgressBar's UI
     */
    public static ComponentUI createUI(JComponent x) {
	return new MotifProgressBarUI();
    }

}
