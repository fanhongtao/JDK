/*
 * @(#)MotifSeparatorUI.java	1.21 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.motif;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicSeparatorUI;

/**
 * A Motif L&F implementation of SeparatorUI.  This implementation 
 * is a "combined" view/controller.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.21 12/19/03
 * @author Georges Saab
 * @author Jeff Shapiro
 */

public class MotifSeparatorUI extends BasicSeparatorUI
{
    public static ComponentUI createUI( JComponent c )
    {
        return new MotifSeparatorUI();
    }

}




