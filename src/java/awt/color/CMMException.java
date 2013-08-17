/*
 * @(#)CMMException.java	1.5 98/09/21
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

/*
 * @(#)JavaCMMException.java    @(#)JavaCMMException.java	1.2    11/04/97

    Created by gbp, October 25, 1997

 * 
 */
/**********************************************************************
 **********************************************************************
 **********************************************************************
 *** COPYRIGHT (c) Eastman Kodak Company, 1997                      ***
 *** As  an unpublished  work pursuant to Title 17 of the United    ***
 *** States Code.  All rights reserved.                             ***
 **********************************************************************
 **********************************************************************
 **********************************************************************/


package java.awt.color;


/**
 * This exception is thrown if the native CMM returns an error.
 */

public class CMMException extends java.lang.RuntimeException {

    /**
     *  Constructs a CMMException with the specified detail message.
     */
    public CMMException (String s) {
        super (s);
    }
}
