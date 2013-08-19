/*
 * @(#)FinalReference.java	1.8 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang.ref;


/* Final references, used to implement finalization */

class FinalReference extends Reference {

    public FinalReference(Object referent, ReferenceQueue q) {
	super(referent, q);
    }

}
