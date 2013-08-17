/*
 * @(#)ProfileDataException.java	1.11 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.awt.color;

/**
 * This exception is thrown when an error occurs in accessing or
 * processing an ICC_Profile object.
 */

public class ProfileDataException extends java.lang.RuntimeException {

    /**
     *  Constructs a ProfileDataException with the specified detail message.
     */
    public ProfileDataException(String s) {
	super (s);
    }
}
