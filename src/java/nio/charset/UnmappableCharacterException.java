/*
 * @(#)UnmappableCharacterException.java	1.3 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.nio.charset;


/**
 * Checked exception thrown when an input character (or byte) sequence
 * is valid but cannot be mapped to an output byte (or character)
 * sequence.  </p>
 *
 * @since 1.4
 */

public class UnmappableCharacterException
    extends CharacterCodingException
{

    private int inputLength;

    public UnmappableCharacterException(int inputLength) {
	this.inputLength = inputLength;
    }

    public int getInputLength() {
	return inputLength;
    }

    public String getMessage() {
	return "Input length = " + inputLength;
    }

}
