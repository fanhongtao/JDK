/*
 * @(#)FinalReference.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.lang.ref;


/* Final references, used to implement finalization */

class FinalReference extends Reference {

    public FinalReference(Object referent, ReferenceQueue q) {
	super(referent, q);
    }

}
