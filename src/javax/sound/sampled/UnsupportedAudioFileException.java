/*
 * @(#)UnsupportedAudioFileException.java	1.9 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.sound.sampled;		  

/**
 * An <code>UnsupportedAudioFileException</code> is an exception indicating that an
 * operation failed because a file did not contain valid data of a recognized file
 * type and format.
 *
 * @author Kara Kytle
 * @version 1.9 03/12/19
 * @since 1.3
 */
/*
 * An <code>UnsupportedAudioFileException</code> is an exception indicating that an
 * operation failed because a file did not contain valid data of a recognized file
 * type and format.
 *
 * @version 1.9 03/12/19
 * @author Kara Kytle
 */

public class UnsupportedAudioFileException extends Exception {

    /**
     * Constructs a <code>UnsupportedAudioFileException</code> that has 
     * <code>null</code> as its error detail message.
     */
    public UnsupportedAudioFileException() {

	super();
    }


    /**
     * Constructs a <code>UnsupportedAudioFileException</code> that has 
     * the specified detail message.
     *
     * @param message a string containing the error detail message
     */
    public UnsupportedAudioFileException(String message) {

	super(message);
    }
}

