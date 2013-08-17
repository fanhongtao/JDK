/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf;

import java.awt.Dimension;
import javax.swing.plaf.UIResource;


/*
 * A subclass of Dimension that implements UIResource.  UI
 * classes that use Dimension values for default properties
 * should use this class.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 * 
 * @see javax.swing.plaf.UIResource
 * @version 1.9 02/06/02
 * @author Amy Fowler
 * 
 */
public class DimensionUIResource extends Dimension implements UIResource
{
    public DimensionUIResource(int width, int height) {
        super(width, height);
    }
}
