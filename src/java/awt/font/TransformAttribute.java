/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * (C) Copyright Taligent, Inc. 1996 - 1997, All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998, All Rights Reserved
 *
 * The original version of this source code and documentation is
 * copyrighted and owned by Taligent, Inc., a wholly-owned subsidiary
 * of IBM. These materials are provided under terms of a License
 * Agreement between Taligent and Sun. This technology is protected
 * by multiple US and International patents.
 *
 * This notice and attribution to Taligent may not be removed.
 * Taligent is a registered trademark of Taligent, Inc.
 *
 */

package java.awt.font;

import java.awt.geom.AffineTransform;
import java.io.Serializable;

/**
 * The <code>TransformAttribute</code> class provides an immutable
 * wrapper for a transform so that it is safe to use as an attribute.
 */
public final class TransformAttribute implements Serializable {

    private AffineTransform transform;

    /**
   * Wraps the specified transform.  The transform is cloned and a
   * reference to the clone is kept.  The original transform is unchanged.
   * @param transform the specified {@link AffineTransform} to be wrapped
     */
    public TransformAttribute(AffineTransform transform) {
	if (transform == null) {
	    throw new IllegalArgumentException("transform may not be null");
	}

	this.transform = new AffineTransform(transform);
    }

    /**
     * Returns a copy of the wrapped transform.
     * @return a <code>AffineTransform</code> that is a copy of the wrapped
     * transform of this <code>TransformAttribute</code>.
     */
    public AffineTransform getTransform() {
	return new AffineTransform(transform);


    }
}


