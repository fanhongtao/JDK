/*
 * @(#)MotifMenuBarUI.java	1.33 98/08/28
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

package com.sun.java.swing.plaf.motif;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.*;
import java.io.Serializable;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicMenuBarUI;
//REMIND
import javax.swing.plaf.basic.*;

/**
 * A Windows L&F implementation of MenuBarUI.  This implementation 
 * is a "combined" view/controller.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * 1.33 08/28/98
 * @author Georges Saab
 * @author Rich Schiavi
 */

public class MotifMenuBarUI extends BasicMenuBarUI
{

    public static ComponentUI createUI(JComponent x) {
	return new MotifMenuBarUI();
    }

} // end class

