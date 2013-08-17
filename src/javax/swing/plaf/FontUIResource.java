/*
 * @(#)FontUIResource.java	1.8 98/08/28
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
 * @version 1.8 08/28/98
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
