/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf;

import java.awt.Font;
import javax.swing.plaf.UIResource;


/**
 * A subclass of java.awt.Font that implements UIResource. 
 * UI classes which set default font properties should use
 * this class.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 * 
 * @see javax.swing.plaf.UIResource
 * @version 1.11 02/06/02
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
    }
}
