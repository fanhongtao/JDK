/*
 * @(#)ColorChooserComponentFactory.java	1.16 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.colorchooser;

import javax.swing.*;



/**
 * A class designed to produce preconfigured "accessory" objects to
 * insert into color choosers.
 *
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @version 1.16 01/23/03
 * @author Steve Wilson
 */
public class ColorChooserComponentFactory {

    private ColorChooserComponentFactory() { } // can't instantiate


    public static AbstractColorChooserPanel[] getDefaultChooserPanels() {
        AbstractColorChooserPanel[] choosers = { new DefaultSwatchChooserPanel(),
						 new DefaultHSBChooserPanel(),
						 new DefaultRGBChooserPanel() };
        return choosers;
    }

    public static JComponent getPreviewPanel() {
        return new DefaultPreviewPanel();
    }

}
