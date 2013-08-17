/*
 * @(#)FinalReference.java	1.4 98/05/01
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

package java.lang.ref;


/* Final references, used to implement finalization */

class FinalReference extends Reference {

    public FinalReference(Object referent, ReferenceQueue q) {
	super(referent, q);
    }

}
