/*
 * @(#)SynthUI.java	1.8 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import javax.swing.*;

/**
 * SynthUI is used to fetch the SynthContext for a particular Component.
 *
 * @version 1.8, 01/23/03
 * @author Scott Violet
 */
interface SynthUI extends SynthConstants {
    /**
     * Returns the Context for the specified component.
     *
     * @param c Component requesting SynthContext.
     * @return SynthContext describing component.
     */
    public SynthContext getContext(JComponent c);
}
