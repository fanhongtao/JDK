/*
 * @(#)CompositeContext.java	1.20 98/10/19
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

package java.awt;

import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * The <code>CompositeContext</code> interface defines the encapsulated
 * and optimized environment for a compositing operation.
 * <code>CompositeContext</code> objects maintain state for
 * compositing operations.  In a multi-threaded environment, several
 * contexts can exist simultaneously for a single {@link Composite} 
 * object.
 * @see Composite
 * @version 10 Feb 1997
 */

public interface CompositeContext {
    /**
     * Releases resources allocated for a context.
     */
    public void dispose();

    /**
     * Composes the two source {@link Raster} objects and 
     * places the result in the destination 
     * {@link WritableRaster}.  Note that the destination 
     * can be the same object as either the first or second 
     * source. Note that <code>dstIn</code> and 
     * <code>dstOut</code> must be compatible with the 
     * <code>dstColorModel</code> passed to the 
     * {@link Composite#createContext(java.awt.image.ColorModel, java.awt.image.ColorModel, java.awt.RenderingHints) createContext} 
     * method of the <code>Composite</code> interface.
     * @param src the first source for the compositing operation
     * @param dstIn the second source for the compositing operation
     * @param dstOut the <code>WritableRaster</code> into which the 
     * result of the operation is stored
     * @see Composite
     */
    public void compose(Raster src,
                        Raster dstIn,
			WritableRaster dstOut);


}

