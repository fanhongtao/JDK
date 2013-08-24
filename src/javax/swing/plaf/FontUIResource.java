/*
 * @(#)FontUIResource.java	1.15 05/11/02
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf;

import java.awt.Font;
import javax.swing.plaf.UIResource;

import sun.font.FontManager;

/**
 * A subclass of java.awt.Font that implements UIResource. 
 * UI classes which set default font properties should use
 * this class.
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
 * @version 1.15 11/02/05
 * @author Hans Muller
 * 
 */
public class FontUIResource extends Font implements UIResource
{
    public FontUIResource(String name, int style, int size) {
	super(name, style, size);
    }
    
    public FontUIResource(Font font) {
	super(font.getName(), font.getStyle(), font.getSize());
        FontManager.setSameHandle(font, this);
    }
}
