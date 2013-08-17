/*
 * @(#)DimensionUIResource.java	1.8 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
 * @version 1.8 02/02/00
 * @author Amy Fowler
 * 
 */
public class DimensionUIResource extends Dimension implements UIResource
{
    public DimensionUIResource(int width, int height) {
        super(width, height);
    }
}
