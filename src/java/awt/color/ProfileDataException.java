/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
