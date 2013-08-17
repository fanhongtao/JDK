/*
 * @(#)MotifLabelUI.java	1.9 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package com.sun.java.swing.plaf.motif;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.plaf.ComponentUI;

/**
 * A Motif L&F implementation of LabelUI.
 * This merely sets up new default values in MotifLookAndFeel.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.9 02/02/00
 * @author Amy Fowler
 */
public class MotifLabelUI extends BasicLabelUI
{
    static MotifLabelUI sharedInstance = new MotifLabelUI();

    public static ComponentUI createUI(JComponent c) {
        return sharedInstance;
    }
}
