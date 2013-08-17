/*
 * @(#)DimensionUIResource.java	1.6 98/08/28
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
 * @version 1.6 08/28/98
 * @author Amy Fowler
 * 
 */
public class DimensionUIResource extends Dimension implements UIResource
{
    public DimensionUIResource(int width, int height) {
        super(width, height);
    }
}
