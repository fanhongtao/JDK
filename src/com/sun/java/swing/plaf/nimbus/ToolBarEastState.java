/*
 * ToolBarEastState.java 07/12/12
 *
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.nimbus;

import java.awt.*;
import javax.swing.*;

/**
 */
class ToolBarEastState extends State {
    ToolBarEastState() {
        super("East");
    }

    @Override protected boolean isInState(JComponent c) {

        JToolBar toolbar = (JToolBar)c;
        return NimbusLookAndFeel.resolveToolbarConstraint(toolbar) == BorderLayout.EAST;
               
    }
}

