/*
 * @(#)ColorUIResource.java	1.12 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf;

import java.awt.Color;
import javax.swing.plaf.UIResource;


/*
 * A subclass of Color that implements UIResource.  UI
 * classes that create colors should use this class.
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
 * @see javax.swing.plaf.UIResource
 * @version 1.12 01/23/03
 * @author Hans Muller
 * 
 */
public class ColorUIResource extends Color implements UIResource
{
    public ColorUIResource(int r, int g, int b) {
	super(r, g, b);
    }

    public ColorUIResource(int rgb) {
	super(rgb);
    }

    public ColorUIResource(float r, float g, float b) {
	super(r, g, b);
    }

    public ColorUIResource(Color c) {
	super(c.getRed(), c.getGreen(), c.getBlue());
    }
}
